# RentO — Android App
## Module 10 — Saved Items
### Complete Engineering Specification

> **Version:** 1.0.0 | **Branch:** `feature/module-10-saved-items`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 03 ✅ · Module 04 ✅ · Module 05 ✅

---

## 1. Module Overview

Module 10 delivers the **Saved Items screen** — two tabs showing the user's bookmarked listings and bookmarked tenant requests. Items can be removed via swipe-to-dismiss with undo snackbar. Tapping navigates to the respective detail screen.

**Route:** `saved`

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   └── usecase/
│       └── saved/
│           ├── GetSavedListingsUseCase.kt
│           ├── GetSavedRequestsUseCase.kt
│           ├── RemoveSavedListingUseCase.kt
│           └── RemoveSavedRequestUseCase.kt
├── presentation/
│   └── saved/
│       ├── SavedViewModel.kt
│       ├── SavedScreen.kt
│       └── components/
│           ├── SavedListingItem.kt
│           └── SavedRequestItem.kt
└── di/
    └── SavedModule.kt

app/src/main/res/values/strings.xml  ← appended
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M10-T01 | Use cases — `GetSavedListingsUseCase`, `GetSavedRequestsUseCase`, `RemoveSavedListingUseCase`, `RemoveSavedRequestUseCase` | ☐ |
| M10-T02 | `SavedViewModel` — load both tabs, optimistic remove with undo timer | ☐ |
| M10-T03 | Koin module — `savedModule` | ☐ |
| M10-T04 | Nav wiring — `saved` route | ☐ |
| M10-T05 | `SavedScreen` — tab row + two `LazyColumn` tabs | ☐ |
| M10-T06 | `SavedListingItem` — `PropertyCard` with swipe-to-remove | ☐ |
| M10-T07 | `SavedRequestItem` — `TenantRequestCard` with swipe-to-remove | ☐ |
| M10-T08 | Undo snackbar — 4s window, re-adds optimistically | ☐ |
| M10-T09 | String resources | ☐ |
| M10-T10 | Unit tests + build gate | ☐ |

---

## 4. Architecture

### 4.1 UI State

```kotlin
data class SavedUiState(
    val savedListings: List<Listing> = emptyList(),
    val savedRequests: List<TenantRequest> = emptyList(),
    val isLoadingListings: Boolean = true,
    val isLoadingRequests: Boolean = true,
    val error: String? = null,

    // Undo state
    val removedListingId: String? = null,
    val removedRequestId: String? = null,
    val snackbarMessage: String? = null,
)
```

### 4.2 Use Cases

```kotlin
class GetSavedListingsUseCase(
    private val savedRepo: SavedRepository,
    private val listingRepo: ListingRepository,
    private val auth: AuthRepository,
) {
    suspend operator fun invoke(): Result<List<Listing>> {
        val uid = auth.getCurrentUserId() ?: return Result.success(emptyList())
        val ids = savedRepo.getSavedListings(uid).getOrDefault(emptyList())
        // Fetch each listing (batch or sequential)
        return listingRepo.getListingsByIds(ids)
    }
}

class RemoveSavedListingUseCase(
    private val savedRepo: SavedRepository,
    private val auth: AuthRepository,
) {
    suspend operator fun invoke(listingId: String): Result<Unit> {
        val uid = auth.getCurrentUserId() ?: return Result.failure(Exception("Not auth."))
        return savedRepo.unsaveListing(uid, listingId)
    }
}
// GetSavedRequestsUseCase + RemoveSavedRequestUseCase follow same pattern
```

> **`ListingRepository.getListingsByIds(ids: List<String>)`** — add this method: performs `whereIn("__name__", ids)` Firestore query (max 30 per batch — chunk if needed).

### 4.3 `SavedViewModel`

```kotlin
class SavedViewModel(
    private val getSavedListings: GetSavedListingsUseCase,
    private val getSavedRequests: GetSavedRequestsUseCase,
    private val removeListingUseCase: RemoveSavedListingUseCase,
    private val removeRequestUseCase: RemoveSavedRequestUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    // Pending undo timers (cancelled if user taps Undo)
    private var listingUndoJob: Job? = null
    private var requestUndoJob: Job? = null

    init { loadAll() }

    fun loadAll() {
        loadListings()
        loadRequests()
    }

    private fun loadListings() {
        viewModelScope.launch {
            getSavedListings().fold(
                onSuccess = { _uiState.update { it.copy(savedListings = listings, isLoadingListings = false) } },
                onFailure = { e -> _uiState.update { it.copy(isLoadingListings = false, error = e.message) } },
            )
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            getSavedRequests().fold(
                onSuccess = { _uiState.update { it.copy(savedRequests = it, isLoadingRequests = false) } },
                onFailure = { e -> _uiState.update { it.copy(isLoadingRequests = false, error = e.message) } },
            )
        }
    }

    // ── Swipe-to-remove with undo ─────────────────────────────────────
    fun removeListing(listingId: String) {
        val listing = _uiState.value.savedListings.find { it.id == listingId } ?: return
        // Optimistic remove
        _uiState.update { state ->
            state.copy(
                savedListings    = state.savedListings.filter { it.id != listingId },
                removedListingId = listingId,
                snackbarMessage  = "Removed from saved",
            )
        }
        listingUndoJob?.cancel()
        listingUndoJob = viewModelScope.launch {
            delay(4_000)
            // Commit removal to Firestore
            removeListingUseCase(listingId)
            _uiState.update { it.copy(removedListingId = null, snackbarMessage = null) }
        }
    }

    fun undoRemoveListing() {
        listingUndoJob?.cancel()
        val removedId = _uiState.value.removedListingId ?: return
        // Re-add: reload from Firestore (simplest approach)
        _uiState.update { it.copy(removedListingId = null, snackbarMessage = null) }
        loadListings()
    }

    fun removeRequest(requestId: String) {
        // Mirror of removeListing
        _uiState.update { state ->
            state.copy(
                savedRequests    = state.savedRequests.filter { it.id != requestId },
                removedRequestId = requestId,
                snackbarMessage  = "Removed from saved",
            )
        }
        requestUndoJob?.cancel()
        requestUndoJob = viewModelScope.launch {
            delay(4_000)
            removeRequestUseCase(requestId)
            _uiState.update { it.copy(removedRequestId = null, snackbarMessage = null) }
        }
    }

    fun undoRemoveRequest() {
        requestUndoJob?.cancel()
        _uiState.update { it.copy(removedRequestId = null, snackbarMessage = null) }
        loadRequests()
    }

    fun clearSnackbar() = _uiState.update { it.copy(snackbarMessage = null) }
}
```

---

## 5. `SavedScreen` — Exact Specification

```
Column(fillMaxSize, DarkBg0):
  Spacer(statusBar)
  Header (16dp top, 20dp H, 20dp bottom):
    Text("Saved Items", Fraunces 28sp SemiBold, DarkT0)

  TabRow — 2 tabs:
    Tab("Properties", selected = tab == 0)
    Tab("Requests",   selected = tab == 1)
    Active: DarkPri underline indicator

  when tab:
    0 → SavedListingsTab(listings, onRemove, onNavigate)
    1 → SavedRequestsTab(requests, onRemove, onNavigate)

  // Snackbar
  if snackbarMessage != null:
    RentoSnackbar(
      message   = snackbarMessage,
      actionLabel = "Undo",
      onAction  = { if(tab==0) viewModel.undoRemoveListing() else viewModel.undoRemoveRequest() },
      onDismiss = { viewModel.clearSnackbar() },
    )
```

**Tab styling:**
```kotlin
TabRow(
    selectedTabIndex = selectedTab,
    containerColor   = MaterialTheme.rentoColors.bg0,
    contentColor     = MaterialTheme.rentoColors.primary,
    indicator        = { tabPositions ->
        TabRowDefaults.Indicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color    = MaterialTheme.rentoColors.primary,
            height   = 2.dp,
        )
    },
)
```

---

## 6. Swipe-to-Remove

### Specification (Section 33.20)

```
SwipeToDismiss (direction = EndToStart only):
  background = DarkRed, "Remove" label + Trash icon (white), 20dp corner
  Card slides out with scale(0.9) + fadeOut 200ms on confirm
  Snackbar "Removed from saved" + "Undo" — 4 second window
```

Implementation using `SwipeToDismissBox` from Material3:

```kotlin
@Composable
fun <T> SwipeToRemoveContainer(
    item: T,
    onRemove: (T) -> Unit,
    content: @Composable (T) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove(item)
                true
            } else false
        },
    )

    SwipeToDismissBox(
        state            = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    Color(0xFFE05555)
                else MaterialTheme.rentoColors.bg0
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(20.dp))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(RentoIcons.Trash, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(4.dp)
                    Text("Remove", 12.sp.bold(), Color.White)
                }
            }
        },
        enableDismissFromStartToEnd = false,
        content = { content(item) },
    )
}
```

---

## 7. Empty States

**Properties tab empty:**
```
EmptyState(
  icon     = RentoIcons.Bookmark,
  title    = "No saved properties",
  subtitle = "Tap the bookmark icon on any listing to save it here.",
  cta      = "Browse Listings",
  onCta    = { onNavigate("home") },
)
```

**Requests tab empty:**
```
EmptyState(
  icon     = RentoIcons.Bookmark,
  title    = "No saved requests",
  subtitle = "Tap the bookmark icon on any seeker request to save it here.",
  cta      = "Browse Requests",
  onCta    = { onNavigate("home") },
)
```

---

## 8. String Resources

```xml
<!-- ─── Saved Items ──────────────────────────────────────────────────────── -->
<string name="saved_title">Saved Items</string>
<string name="saved_tab_properties">Properties</string>
<string name="saved_tab_requests">Requests</string>
<string name="saved_empty_listings_title">No saved properties</string>
<string name="saved_empty_listings_sub">Tap the bookmark icon on any listing to save it here.</string>
<string name="saved_empty_requests_title">No saved requests</string>
<string name="saved_empty_requests_sub">Tap the bookmark icon on any seeker request to save it here.</string>
<string name="saved_removed_snackbar">Removed from saved</string>
<string name="saved_undo">Undo</string>
```

---

## 9. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Screen loads — both tabs fetch data in parallel | `loadListings()` + `loadRequests()` in init | ☐ |
| Properties tab — shows saved listings as `PropertyCard` | `SavedListingsTab` | ☐ |
| Requests tab — shows saved requests as `TenantRequestCard` | `SavedRequestsTab` | ☐ |
| Empty state — Properties tab | `savedListings.isEmpty()` | ☐ |
| Empty state — Requests tab | `savedRequests.isEmpty()` | ☐ |
| Swipe left on listing card → red "Remove" action reveals | `SwipeToDismissBox` | ☐ |
| Full swipe → card slides out with scale + fade | `confirmValueChange` returns true | ☐ |
| Snackbar "Removed from saved" + "Undo" appears | `snackbarMessage != null` | ☐ |
| "Undo" tapped → card re-appears, Firestore delete cancelled | `undoRemoveListing()` cancels job | ☐ |
| 4s timeout → Firestore `unsaveListing()` called | `delay(4_000)` then `removeListingUseCase()` | ☐ |
| Same swipe flow for Requests tab | `removeRequest()` + `undoRemoveRequest()` | ☐ |
| Tap listing card → navigate to listing detail | `onNavigate("listing/{id}")` | ☐ |
| Tap request card → navigate to request detail | `onNavigate("request/{id}")` | ☐ |

---

## 10. CODE_REVIEW_MODULE_10.md Template

```markdown
# Code Review — Module 10: Saved Items
**Spec version:** ANDROID_MODULE_10.md v1.0.0

## ✅ Swipe-to-Remove
- [ ] SwipeToDismissBox — end-to-start only (left swipe)
- [ ] Background: DarkRed + white Trash icon + "Remove" label
- [ ] Card exit: scale(0.9) + fadeOut 200ms
- [ ] Snackbar: 4 second undo window
- [ ] Undo: cancels coroutine job, reloads from Firestore
- [ ] After 4s: Firestore delete committed

## ✅ Architecture
- [ ] `ListingRepository.getListingsByIds()` uses Firestore `whereIn` (chunked at 30)
- [ ] Optimistic remove from UI list immediately
- [ ] Parallel load of both tabs in ViewModel init
- [ ] `collectAsStateWithLifecycle()` used

## ✅ Build
- [ ] `./gradlew assembleDebug` → ✅
- [ ] `./gradlew test` → ✅
```

*End of Module 10 — Saved Items v1.0.0*
*Next: Module 11 — My Listings & My Requests Dashboard.*
