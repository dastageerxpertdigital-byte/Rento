# RentO — Android App
## Module 05 — Tenant Request Detail
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 05
> **Branch:** `feature/module-05-request-detail`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 03 ✅ · Module 04 ✅
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every colour, padding, animation, and interaction is derived verbatim from the prototype and `REQUIREMENTS_SPECIFICATION_v2_3.md`. Do not simplify. Do not improvise. If anything is ambiguous — stop and ask.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
5. [Task M05-T01 — Shared Components Refactor](#task-m05-t01--shared-components-refactor)
6. [Task M05-T02 — `MapBackground` + `RadiusCircle` Components](#task-m05-t02--mapbackground--radiuscircle-components)
7. [Task M05-T03 — Use Cases](#task-m05-t03--use-cases)
8. [Task M05-T04 — `RequestDetailViewModel`](#task-m05-t04--requestdetailviewmodel)
9. [Task M05-T05 — Koin Module Update](#task-m05-t05--koin-module-update)
10. [Task M05-T06 — Navigation Wiring](#task-m05-t06--navigation-wiring)
11. [Task M05-T07 — Screen: `RequestDetailScreen` Shell](#task-m05-t07--screen-requestdetailscreen-shell)
12. [Task M05-T08 — Header Row](#task-m05-t08--header-row)
13. [Task M05-T09 — Profile Block](#task-m05-t09--profile-block)
14. [Task M05-T10 — Budget Banner](#task-m05-t10--budget-banner)
15. [Task M05-T11 — Preferred Area Section](#task-m05-t11--preferred-area-section)
16. [Task M05-T12 — Requirements Section](#task-m05-t12--requirements-section)
17. [Task M05-T13 — About the Tenant](#task-m05-t13--about-the-tenant)
18. [Task M05-T14 — Sticky Bottom Bar](#task-m05-t14--sticky-bottom-bar)
19. [Task M05-T15 — Shimmer Loading State](#task-m05-t15--shimmer-loading-state)
20. [Task M05-T16 — String Resources](#task-m05-t16--string-resources)
21. [Task M05-T17 — Unit Tests](#task-m05-t17--unit-tests)
22. [Task M05-T18 — Build Gate](#task-m05-t18--build-gate)
23. [Journey Coverage Checklist](#23-journey-coverage-checklist)
24. [CODE_REVIEW_MODULE_05.md Template](#24-code_review_module_05md-template)

---

## 1. Module Overview

Module 05 delivers the **Tenant Request Detail screen** — the destination after tapping a `TenantRequestCard` in the home feed or saved items. It is the mirror of Module 04's Listing Detail but for seeker requests rather than host listings.

**Key features:**
- Standard header bar with Back, Bookmark (save/unsave), Share buttons
- Centred profile block with large avatar, requester name, move-in label
- Budget banner with gradient fill + intent badge
- `MapBackground` + `RadiusCircle` static preview showing the requester's preferred area and radius
- Requirements chip row (property type, bedrooms, bathrooms, furnishing)
- Expandable bio section with italic quote styling
- Sticky bottom bar: "💬 Message {firstName}" (guest) or "Edit Request" (requester)
- Report Request via shared `ReportSheet` (moved from Module 04 to `shared/components/`)
- Shimmer skeleton loading state

**Repositories used (all from prior modules):** `TenantRequestRepository` (M03), `SavedRepository` (M04), `AuthRepository` (M02), `ReportRepository` (M04)

**New shared components:** `MapBackground`, `RadiusCircle` (used again in Module 06 — Request Form Step 4)

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   └── usecase/
│       └── request/
│           ├── GetRequestDetailUseCase.kt
│           ├── SaveRequestUseCase.kt
│           ├── UnsaveRequestUseCase.kt
│           └── IsRequestSavedUseCase.kt
├── presentation/
│   ├── shared/
│   │   └── components/
│   │       ├── ReportSheet.kt             ← MOVED from listing/detail/components/
│   │       ├── MapBackground.kt           ← NEW
│   │       └── RadiusCircle.kt            ← NEW
│   └── request/
│       └── detail/
│           ├── RequestDetailViewModel.kt
│           ├── RequestDetailScreen.kt
│           └── components/
│               ├── RequestDetailHeader.kt
│               ├── RequesterProfileBlock.kt
│               ├── BudgetBanner.kt
│               ├── PreferredAreaSection.kt
│               ├── RequirementsSection.kt
│               ├── AboutTenantSection.kt
│               ├── RequestDetailBottomBar.kt
│               └── RequestDetailShimmer.kt
└── di/
    └── ListingDetailModule.kt             ← updated to add request detail bindings

app/src/main/res/values/strings.xml        ← Module 05 strings appended
app/src/test/java/com/rento/app/
├── domain/usecase/request/
│   └── GetRequestDetailUseCaseTest.kt
└── presentation/request/detail/
    └── RequestDetailViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M05-T01 | Move `ReportSheet` to `presentation/shared/components/`; update import in `ListingDetailScreen` | `shared/components/ReportSheet.kt` | ☐ |
| M05-T02 | `MapBackground` + `RadiusCircle` shared components | `shared/components/MapBackground.kt`, `RadiusCircle.kt` | ☐ |
| M05-T03 | Use cases — `GetRequestDetailUseCase`, `SaveRequestUseCase`, `UnsaveRequestUseCase`, `IsRequestSavedUseCase` | `domain/usecase/request/` | ☐ |
| M05-T04 | `RequestDetailViewModel` | `presentation/request/detail/` | ☐ |
| M05-T05 | Koin module update — add request detail bindings to `listingDetailModule` | `di/ListingDetailModule.kt` | ☐ |
| M05-T06 | Nav graph — wire `request/{requestId}` route | `RentoNavGraph.kt` | ☐ |
| M05-T07 | `RequestDetailScreen` shell — `Scaffold`, `LazyColumn`, overlay wiring | `RequestDetailScreen.kt` | ☐ |
| M05-T08 | `RequestDetailHeader` — back, bookmark, share buttons | `RequestDetailHeader.kt` | ☐ |
| M05-T09 | `RequesterProfileBlock` — large avatar, name, move-in label | `RequesterProfileBlock.kt` | ☐ |
| M05-T10 | `BudgetBanner` — gradient fill, SectionLabel, price, intent badge | `BudgetBanner.kt` | ☐ |
| M05-T11 | `PreferredAreaSection` — `MapBackground` + `RadiusCircle` + info pills | `PreferredAreaSection.kt` | ☐ |
| M05-T12 | `RequirementsSection` — wrapping chip row | `RequirementsSection.kt` | ☐ |
| M05-T13 | `AboutTenantSection` — italic bio with quote marks | `AboutTenantSection.kt` | ☐ |
| M05-T14 | `RequestDetailBottomBar` — guest / requester / unauth variants | `RequestDetailBottomBar.kt` | ☐ |
| M05-T15 | `RequestDetailShimmer` | `RequestDetailShimmer.kt` | ☐ |
| M05-T16 | String resources | `strings.xml` | ☐ |
| M05-T17 | Unit tests | `*Test.kt` | ☐ |
| M05-T18 | Build gate | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 UI State

```kotlin
data class RequestDetailUiState(
    val request: TenantRequest? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false,
    val saveLoading: Boolean = false,
    val currentUserId: String? = null,
    val showReportSheet: Boolean = false,
    val reportCategory: String? = null,
    val reportDetails: String = "",
    val reportLoading: Boolean = false,
    val reportSuccess: Boolean = false,
    val reportError: String? = null,
)
```

### 4.2 ViewModel Contract

```kotlin
class RequestDetailViewModel(
    private val getRequest: GetRequestDetailUseCase,
    private val saveRequest: SaveRequestUseCase,
    private val unsaveRequest: UnsaveRequestUseCase,
    private val isRequestSaved: IsRequestSavedUseCase,
    private val submitReport: SubmitReportUseCase,       // reused from M04
    private val authRepository: AuthRepository,
) : ViewModel()
```

Functions:
```
fun load(requestId: String)
fun toggleSave()
fun openReportSheet()
fun closeReportSheet()
fun setReportCategory(category: String)
fun setReportDetails(details: String)
fun submitReport()
```

### 4.3 Koin Additions to `listingDetailModule`

```kotlin
// Added to existing listingDetailModule in di/ListingDetailModule.kt:
factory { GetRequestDetailUseCase(get()) }
factory { SaveRequestUseCase(get(), get()) }
factory { UnsaveRequestUseCase(get(), get()) }
factory { IsRequestSavedUseCase(get(), get()) }
viewModel { RequestDetailViewModel(get(), get(), get(), get(), get(), get()) }
```

> `listingDetailModule` is extended rather than creating a new module — both detail screens share the same Koin scope.

### 4.4 Navigation Route

```kotlin
// In RentoNavGraph.kt — replace placeholder:
composable(
    route     = HomeRoutes.REQUEST_DETAIL,   // "request/{requestId}"
    arguments = listOf(navArgument("requestId") { type = NavType.StringType }),
) { backStackEntry ->
    val requestId = backStackEntry.arguments?.getString("requestId") ?: return@composable
    RequestDetailScreen(
        requestId         = requestId,
        onBack            = { navController.popBackStack() },
        onNavigateToChat  = { chatId -> navController.navigate("chat/$chatId") },
        onNavigateToLogin = { navController.navigate(AuthRoutes.LOGIN) },
        onNavigateToEdit  = { navController.navigate("request/form?requestId=$requestId&mode=edit") },
    )
}
```

---

## Task M05-T01 — Shared Components Refactor

### Sub-tasks
- [ ] **M05-T01-A** Create `presentation/shared/components/` package.
- [ ] **M05-T01-B** Move `ReportSheet.kt` from `presentation/listing/detail/components/ReportSheet.kt` to `presentation/shared/components/ReportSheet.kt`. Update the package declaration inside the file.
- [ ] **M05-T01-C** Update the import in `ListingDetailScreen.kt` to use the new path.
- [ ] **M05-T01-D** `./gradlew assembleDebug` → `BUILD SUCCESSFUL` confirming no broken imports.

---

## Task M05-T02 — `MapBackground` + `RadiusCircle` Components

**Files:** `presentation/shared/components/MapBackground.kt`, `presentation/shared/components/RadiusCircle.kt`

These are Module 01 design system components that were not yet implemented. They are specified in Section 3.4.17–3.4.18 and first used in Module 05. They will be reused in Modules 06 (Request Form Step 4) and 07 (Listing Form Step 5).

### `MapBackground` — Section 3.4.17

```kotlin
@Composable
fun MapBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.rentoColors.bg2)
            .drawBehind {
                val gridSpacingPx = 28.dp.toPx()
                val lineColor     = rentoColors.border.copy(alpha = 1f)
                val strokeWidth   = 1.dp.toPx()

                // Horizontal lines
                var y = 0f
                while (y <= size.height) {
                    drawLine(
                        color       = lineColor,
                        start       = Offset(0f, y),
                        end         = Offset(size.width, y),
                        strokeWidth = strokeWidth,
                    )
                    y += gridSpacingPx
                }

                // Vertical lines
                var x = 0f
                while (x <= size.width) {
                    drawLine(
                        color       = lineColor,
                        start       = Offset(x, 0f),
                        end         = Offset(x, size.height),
                        strokeWidth = strokeWidth,
                    )
                    x += gridSpacingPx
                }
            },
        content = content,
    )
}
```

> `rentoColors` inside `drawBehind` is accessed via `val rentoColors = MaterialTheme.rentoColors` captured before the lambda.

### `RadiusCircle` — Section 3.4.18

```kotlin
@Composable
fun RadiusCircle(
    radiusKm: Int,
    modifier: Modifier = Modifier,
    animate: Boolean = true,         // false for static display (detail screen)
) {
    // diameter = 80dp + (radiusKm × 12dp), min 80dp, max ~260dp for 15km
    val targetDiameter = (80 + radiusKm * 12).dp
    val diameter by if (animate) {
        animateDpAsState(
            targetValue    = targetDiameter,
            animationSpec  = tween(150),
            label          = "radiusCircleDiameter",
        )
    } else {
        remember { mutableStateOf(targetDiameter) }
    }

    Box(
        modifier        = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Filled circle (DarkPriM — 10% green)
        Box(
            modifier = Modifier
                .size(diameter)
                .background(
                    color = MaterialTheme.rentoColors.primaryTint,
                    shape = CircleShape,
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.rentoColors.primary,
                    shape = CircleShape,
                ),
        )

        // Centre dot — 12dp DarkPri circle with 4dp DarkBg0 ring
        Box(
            modifier = Modifier
                .size(20.dp)   // 12dp dot + 4dp ring each side
                .background(MaterialTheme.rentoColors.bg0, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.rentoColors.primary, CircleShape),
            )
        }
    }
}
```

### Sub-tasks
- [ ] **M05-T02-A** Implement `MapBackground` with Canvas grid lines.
- [ ] **M05-T02-B** Implement `RadiusCircle` with `animateDpAsState` when `animate=true`, static when `false`.
- [ ] **M05-T02-C** `@Preview` `MapBackground` alone; `MapBackground` + `RadiusCircle(radiusKm=5)`, dark + light.

---

## Task M05-T03 — Use Cases

**Files:** `domain/usecase/request/`

### Sub-tasks
- [ ] **M05-T03-A** `GetRequestDetailUseCase`:

```kotlin
class GetRequestDetailUseCase(private val repository: TenantRequestRepository) {
    suspend operator fun invoke(requestId: String): Result<TenantRequest> =
        repository.getRequestById(requestId)
}
```

- [ ] **M05-T03-B** `SaveRequestUseCase`:

```kotlin
class SaveRequestUseCase(
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(requestId: String): Result<Unit> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        return savedRepository.saveRequest(uid, requestId)
    }
}
```

- [ ] **M05-T03-C** `UnsaveRequestUseCase`:

```kotlin
class UnsaveRequestUseCase(
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(requestId: String): Result<Unit> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        return savedRepository.unsaveRequest(uid, requestId)
    }
}
```

- [ ] **M05-T03-D** `IsRequestSavedUseCase`:

```kotlin
class IsRequestSavedUseCase(
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(requestId: String): Result<Boolean> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.success(false)
        return savedRepository.isRequestSaved(uid, requestId)
    }
}
```

---

## Task M05-T04 — `RequestDetailViewModel`

**File:** `presentation/request/detail/RequestDetailViewModel.kt`

```kotlin
class RequestDetailViewModel(
    private val getRequest: GetRequestDetailUseCase,
    private val saveRequest: SaveRequestUseCase,
    private val unsaveRequest: UnsaveRequestUseCase,
    private val isRequestSaved: IsRequestSavedUseCase,
    private val submitReport: SubmitReportUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState.asStateFlow()

    private var requestId: String = ""

    fun load(id: String) {
        requestId = id
        _uiState.update {
            it.copy(isLoading = true, error = null,
                    currentUserId = authRepository.getCurrentUserId())
        }
        viewModelScope.launch {
            val requestResult = getRequest(id)
            val savedResult   = isRequestSaved(id)
            requestResult.fold(
                onSuccess = { request ->
                    _uiState.update { state ->
                        state.copy(
                            request   = request,
                            isLoading = false,
                            isSaved   = savedResult.getOrDefault(false),
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    // ── Save toggle (optimistic) ─────────────────────────────────────
    fun toggleSave() {
        val wasSaved = _uiState.value.isSaved
        _uiState.update { it.copy(isSaved = !wasSaved, saveLoading = true) }
        viewModelScope.launch {
            val result = if (wasSaved) unsaveRequest(requestId)
                         else          saveRequest(requestId)
            result.onFailure {
                _uiState.update { it.copy(isSaved = wasSaved) }   // revert
            }
            _uiState.update { it.copy(saveLoading = false) }
        }
    }

    // ── Report sheet ─────────────────────────────────────────────────
    fun openReportSheet() {
        _uiState.update {
            it.copy(showReportSheet = true, reportSuccess = false,
                    reportCategory = null, reportDetails = "", reportError = null)
        }
    }
    fun closeReportSheet()            { _uiState.update { it.copy(showReportSheet = false) } }
    fun setReportCategory(c: String)  { _uiState.update { it.copy(reportCategory = c, reportError = null) } }
    fun setReportDetails(d: String)   { _uiState.update { it.copy(reportDetails = d) } }

    fun submitReport() {
        val category = _uiState.value.reportCategory ?: return
        _uiState.update { it.copy(reportLoading = true, reportError = null) }
        viewModelScope.launch {
            submitReport(
                targetId   = requestId,
                targetType = ReportTargetType.REQUEST,
                category   = category,
                details    = _uiState.value.reportDetails.takeIf { it.isNotBlank() },
            ).fold(
                onSuccess = { _uiState.update { it.copy(reportLoading = false, reportSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(reportLoading = false, reportError = e.message) } },
            )
        }
    }
}
```

---

## Task M05-T05 — Koin Module Update

**File:** `di/ListingDetailModule.kt`

### Sub-tasks
- [ ] **M05-T05-A** Add use case factories and `RequestDetailViewModel` binding to the existing `listingDetailModule`:

```kotlin
// Append to val listingDetailModule = module { ... }:
factory { GetRequestDetailUseCase(get()) }
factory { SaveRequestUseCase(get(), get()) }
factory { UnsaveRequestUseCase(get(), get()) }
factory { IsRequestSavedUseCase(get(), get()) }
viewModel { RequestDetailViewModel(get(), get(), get(), get(), get(), get()) }
```

- [ ] **M05-T05-B** No new Koin module file needed — `listingDetailModule` covers both detail screens.

---

## Task M05-T06 — Navigation Wiring

### Sub-tasks
- [ ] **M05-T06-A** Replace placeholder `request/{requestId}` composable in `RentoNavGraph.kt` per section 4.4.
- [ ] **M05-T06-B** `request/form?requestId=…&mode=edit` remains a placeholder (Module 09 edit flow).

---

## Task M05-T07 — Screen: `RequestDetailScreen` Shell

**File:** `presentation/request/detail/RequestDetailScreen.kt`

```kotlin
@Composable
fun RequestDetailScreen(
    requestId: String,
    viewModel: RequestDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToChat: (chatId: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEdit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(requestId) { viewModel.load(requestId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0),
    ) {
        when {
            uiState.isLoading -> RequestDetailShimmer()

            uiState.error != null && uiState.request == null ->
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    EmptyState(
                        icon     = RentoIcons.AlertCircle,
                        title    = stringResource(R.string.error_generic_title),
                        subtitle = uiState.error!!,
                        cta      = stringResource(R.string.error_retry),
                        onCta    = { viewModel.load(requestId) },
                    )
                }

            uiState.request != null -> {
                val request  = uiState.request!!
                val isAuth   = uiState.currentUserId != null
                val isOwner  = isAuth && uiState.currentUserId == request.uid
                val firstName = request.requesterName.split(" ").firstOrNull() ?: request.requesterName

                Scaffold(
                    containerColor = MaterialTheme.rentoColors.bg0,
                    bottomBar = {
                        RequestDetailBottomBar(
                            isOwner         = isOwner,
                            isAuthenticated = isAuth,
                            firstName       = firstName,
                            onMessageTap    = {
                                if (!isAuth) onNavigateToLogin()
                                else {
                                    val chatId = buildRequestChatId(
                                        request.uid, uiState.currentUserId!!, request.id
                                    )
                                    onNavigateToChat(chatId)
                                }
                            },
                            onEditTap = onNavigateToEdit,
                        )
                    },
                ) { paddingValues ->
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    ) {
                        item(key = "header") {
                            RequestDetailHeader(
                                isSaved    = uiState.isSaved,
                                onBack     = onBack,
                                onToggleSave = { viewModel.toggleSave() },
                                onShare    = { shareRequest(context, request) },
                            )
                        }

                        item(key = "profile") {
                            RequesterProfileBlock(
                                name    = request.requesterName,
                                moveIn  = request.moveInDate,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 24.dp),
                            )
                        }

                        item(key = "budget") {
                            BudgetBanner(
                                budget = request.budgetMax,
                                intent = request.intent,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 24.dp),
                            )
                        }

                        item(key = "preferred_area") {
                            PreferredAreaSection(
                                preferredAreas = request.preferredAreas,
                                radiusKm       = request.radiusKm,
                                modifier       = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 24.dp),
                            )
                        }

                        item(key = "requirements") {
                            RequirementsSection(
                                propertyType      = request.propertyType,
                                minBedrooms       = request.minBedrooms,
                                minBathrooms      = request.minBathrooms,
                                furnishingRequired = request.furnishingRequired,
                                modifier          = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 24.dp),
                            )
                        }

                        if (request.bio.isNotBlank()) {
                            item(key = "about") {
                                AboutTenantSection(
                                    bio      = request.bio,
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .padding(bottom = 24.dp),
                                )
                            }
                        }

                        if (isAuth) {
                            item(key = "report_link") {
                                Text(
                                    text      = stringResource(R.string.detail_report_request),
                                    style     = MaterialTheme.rentoTypography.bodyM,
                                    color     = MaterialTheme.rentoColors.t2,
                                    textAlign = TextAlign.Center,
                                    modifier  = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openReportSheet() }
                                        .padding(vertical = 16.dp, horizontal = 20.dp),
                                )
                            }
                        }

                        item(key = "bottom_spacer") { Spacer(Modifier.height(20.dp)) }
                    }
                }
            }
        }
    }

    // ── Overlays ────────────────────────────────────────────────────
    if (uiState.showReportSheet) {
        ReportSheet(
            targetLabel      = "Request",
            selectedCategory = uiState.reportCategory,
            details          = uiState.reportDetails,
            isLoading        = uiState.reportLoading,
            isSuccess        = uiState.reportSuccess,
            error            = uiState.reportError,
            onSelectCategory = { viewModel.setReportCategory(it) },
            onDetailsChange  = { viewModel.setReportDetails(it) },
            onSubmit         = { viewModel.submitReport() },
            onDismiss        = { viewModel.closeReportSheet() },
        )
    }
}

// ── Helpers ──────────────────────────────────────────────────────────
private fun buildRequestChatId(
    requesterUid: String,
    viewerUid: String,
    requestId: String,
): String {
    val sorted = listOf(requesterUid, viewerUid).sorted()
    return "chat_${sorted[0]}_${sorted[1]}_req_$requestId"
}

private fun shareRequest(context: Context, request: TenantRequest) {
    val text = "Looking for ${request.propertyType} in ${request.preferredAreas.joinToString(", ")}" +
               " — Budget PKR ${request.budgetMax}/mo\nFind on RentO"
    context.startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            null,
        )
    )
}
```

### Sub-tasks
- [ ] **M05-T07-A** Implement `RequestDetailScreen` shell exactly as above.
- [ ] **M05-T07-B** `@Preview` — loaded state, loading state, dark + light.

---

## Task M05-T08 — Header Row

**File:** `presentation/request/detail/components/RequestDetailHeader.kt`

### Exact Specification

```
StatusBar()

Row(
  fillMaxWidth,
  SpaceBetween,
  CenterVertically,
  modifier = Modifier
    .background(RentoColors.bg0)
    .padding(horizontal = 20.dp, vertical = 16.dp),
):

  // Back button
  RentoIconButton(
    icon   = RentoIcons.Back,
    size   = 42.dp,
    onClick = onBack,
  )

  // Right side: Bookmark + Share (9dp gap)
  Row(horizontalArrangement = spacedBy(9.dp)):
    // Bookmark button — fills DarkPri when saved
    RentoIconButton(
      icon     = if isSaved RentoIcons.BookmarkFilled else RentoIcons.Bookmark,
      tint     = if isSaved RentoColors.primary else RentoColors.t0,
      size     = 42.dp,
      onClick  = onToggleSave,
    )
    // Share button
    RentoIconButton(
      icon   = RentoIcons.Share,
      size   = 42.dp,
      onClick = onShare,
    )
```

> `RentoIconButton` is the standard 42dp icon button from Module 01 Design System (DarkBg2 fill, 1.5dp DarkBd2 border, 15dp corner). Verify it exists in Module 01 — if absent, implement it here and document as a Module 01 backfill.

> Unlike the Listing Detail glassmorphic buttons, these are standard `RentoIconButton` components on a plain bg0 background — NOT glassmorphic.

> `RentoIcons.BookmarkFilled` — verify in Module 01. Add if absent.

### Sub-tasks
- [ ] **M05-T08-A** Implement `RequestDetailHeader`.
- [ ] **M05-T08-B** `animateColorAsState` on bookmark icon tint (saved ↔ unsaved).
- [ ] **M05-T08-C** `@Preview` — saved + unsaved states, dark + light.

---

## Task M05-T09 — Profile Block

**File:** `presentation/request/detail/components/RequesterProfileBlock.kt`

### Exact Specification

```
Column(
  fillMaxWidth,
  CenterHorizontally,
  modifier = modifier,
):

  // Avatar — 84dp circle
  Box(
    84.dp circle,
    background = RentoColors.primaryTint,
    border = 2.dp RentoColors.primary,
  ):
    Icon(RentoIcons.User, 40.dp, RentoColors.primary, centred)

  Spacer(14.dp)

  // Name
  Text(
    name,
    style = Fraunces 24sp SemiBold,
    color = RentoColors.t0,
    textAlign = Center,
  )

  Spacer(4.dp)

  // Move-in label
  Text(
    text  = "Looking for space by ${formatMoveIn(moveIn)}",
    style = 13sp,
    color = RentoColors.t2,
    textAlign = Center,
  )
```

```kotlin
private fun formatMoveIn(moveIn: String): String = when (moveIn) {
    "immediate"    -> "Immediately"
    "within_month" -> "End of Month"
    "flexible"     -> "Flexible Date"
    else           -> moveIn   // ISO date string — pass through as-is
}
```

### Sub-tasks
- [ ] **M05-T09-A** Implement `RequesterProfileBlock`.
- [ ] **M05-T09-B** `@Preview` dark + light, long name, each moveIn variant.

---

## Task M05-T10 — Budget Banner

**File:** `presentation/request/detail/components/BudgetBanner.kt`

### Exact Specification

```
Row(
  fillMaxWidth,
  SpaceBetween,
  CenterVertically,
  modifier = modifier
    .background(
      brush = Brush.horizontalGradient(
        colors = listOf(RentoColors.primaryTint, RentoColors.bg2)
      ),
      shape = RoundedCornerShape(20.dp),
    )
    .border(1.5.dp, RentoColors.primaryRing, RoundedCornerShape(20.dp))
    .padding(20.dp),
):

  // Left: label + price
  Column:
    SectionLabel("BUDGET UP TO")
    Spacer(6.dp)
    Row(CenterVertically, baselineAlign):
      Text(
        "PKR ${formatBudget(budget)}",
        28sp Bold,
        RentoColors.primary,
      )
      Spacer(4.dp)
      Text(
        " PKR/mo",
        12sp,
        RentoColors.t2,
      )

  // Right: intent badge
  Badge(
    label = intentLabel(intent),
    style = BadgeStyle.INTENT,
  )
```

```kotlin
private fun formatBudget(budget: Int): String = when {
    budget >= 100_000 -> "${budget / 1_000}k"
    budget >= 10_000  -> "${budget / 1_000}k"
    else              -> NumberFormat.getNumberInstance(Locale.US).format(budget)
}

private fun intentLabel(intent: String): String = when (intent) {
    "share"    -> "Shared Space"
    "fullRent" -> "Full Property"
    else       -> intent.replaceFirstChar { it.uppercase() }
}
```

### Sub-tasks
- [ ] **M05-T10-A** Implement `BudgetBanner`. Reuse `SectionLabel` and `Badge` from Module 01.
- [ ] **M05-T10-B** Gradient direction is **horizontal** (left = primaryTint → right = bg2).
- [ ] **M05-T10-C** `@Preview` dark + light, both intent values.

---

## Task M05-T11 — Preferred Area Section

**File:** `presentation/request/detail/components/PreferredAreaSection.kt`

### Exact Specification

```
Column(fillMaxWidth, modifier):
  Text("Preferred Area", Fraunces 20sp SemiBold, RentoColors.t0, bottom=12.dp)

  // MapBackground container (180dp height, clipped)
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(180.dp)
      .clip(RoundedCornerShape(18.dp))
      .border(1.5.dp, RentoColors.border, RoundedCornerShape(18.dp)),
  ):
    MapBackground(Modifier.fillMaxSize()):

      // RadiusCircle (centred, static — not interactive)
      RadiusCircle(
        radiusKm = radiusKm,
        animate  = false,
        modifier = Modifier.align(Alignment.Center),
      )

      // Bottom-left info pill
      Row(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(start = 10.dp, bottom = 10.dp)
          .then(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
              Modifier.blur(10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            else Modifier
          )
          .background(RentoColors.navBg.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
          .border(1.dp, RentoColors.border, RoundedCornerShape(10.dp))
          .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(6.dp),
      ):
        Icon(RentoIcons.Pin, 14.dp, RentoColors.primary)
        Text(
          text  = preferredAreas.joinToString(", "),
          style = 12sp Bold,
          color = RentoColors.t0,
          maxLines = 1,
          overflow = Ellipsis,
        )

      // Top-right radius pill
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(end = 10.dp, top = 10.dp)
          .background(RentoColors.primaryTint, RoundedCornerShape(100.dp))
          .border(1.dp, RentoColors.primaryRing, RoundedCornerShape(100.dp))
          .padding(vertical = 5.dp, horizontal = 10.dp),
      ):
        Text("${radiusKm} km radius", 10sp Bold, RentoColors.primary)
```

### Sub-tasks
- [ ] **M05-T11-A** Implement `PreferredAreaSection` using `MapBackground` and `RadiusCircle` from M05-T02.
- [ ] **M05-T11-B** `blur(10.dp)` on bottom-left pill guarded with API ≥ 31 check.
- [ ] **M05-T11-C** `preferredAreas.joinToString(", ")` — handles multiple areas gracefully.
- [ ] **M05-T11-D** `@Preview` — `radiusKm=5`, `radiusKm=15`, 3 area names, dark + light.

---

## Task M05-T12 — Requirements Section

**File:** `presentation/request/detail/components/RequirementsSection.kt`

### Exact Specification

```
Column(fillMaxWidth, modifier):
  Text("Requirements", Fraunces 20sp SemiBold, RentoColors.t0, bottom=12.dp)

  FlowRow(horizontalGap=8.dp, verticalGap=8.dp):
    // Property type chip
    RentoChip(label = propertyType, selected = false, onClick = {})

    // Min bedrooms chip
    val bedroomsLabel = if (minBedrooms == 0) "Any Beds" else "${minBedrooms}+ Beds"
    RentoChip(label = bedroomsLabel, selected = false, onClick = {})

    // Min bathrooms chip
    val bathroomsLabel = "${minBathrooms}+ Baths"
    RentoChip(label = bathroomsLabel, selected = false, onClick = {})

    // Furnishing chip
    val furnishLabel = when (furnishingRequired) {
        "furnished" -> "Furnished"
        "semi"      -> "Semi-Furnished"
        "any"       -> "Any Furnish"
        else        -> furnishingRequired.replaceFirstChar { it.uppercase() }
    }
    RentoChip(label = furnishLabel, selected = false, onClick = {})
```

> These chips are **display-only** — `selected = false` always, `onClick = {}` (no-op). They use the standard unselected chip style. Do NOT make them interactive on this screen.

### Sub-tasks
- [ ] **M05-T12-A** Implement `RequirementsSection`. Reuse `RentoChip` from Module 01.
- [ ] **M05-T12-B** Chips are non-interactive — no click handling needed.
- [ ] **M05-T12-C** `@Preview` — various combinations, dark + light.

---

## Task M05-T13 — About the Tenant

**File:** `presentation/request/detail/components/AboutTenantSection.kt`

### Exact Specification

```
Column(fillMaxWidth, modifier):
  Text("About the Tenant", Fraunces 20sp SemiBold, RentoColors.t0, bottom=10.dp)

  // Bio with italic quote styling
  // Wraps the text in opening and closing curly quote marks
  Text(
    text      = "\u201C${bio}\u201D",   // " " Unicode curly quotes
    style     = bodyM.copy(
      fontStyle  = FontStyle.Italic,
      lineHeight = 24.sp,              // 14sp × 1.72
    ),
    color     = RentoColors.t1,
  )
```

> `\u201C` = `"` (left double quotation mark), `\u201D` = `"` (right double quotation mark). These are the "curly quotes" specified in the prototype, not straight `"` ASCII quotes.

### Sub-tasks
- [ ] **M05-T13-A** Implement `AboutTenantSection`.
- [ ] **M05-T13-B** `@Preview` — short bio, long bio, dark + light.

---

## Task M05-T14 — Sticky Bottom Bar

**File:** `presentation/request/detail/components/RequestDetailBottomBar.kt`

### Exact Specification

```
Container (same as ListingBottomBar from M04):
  HorizontalDivider(1.dp, RentoColors.border)
  Row(
    fillMaxWidth,
    background = RentoColors.navBg,
    padding = 14.dp top / (28.dp + navBarInsets) bottom / 24.dp H,
    CenterVertically,
  )
```

**Variant A — Guest (authenticated or unauthenticated, not requester):**
```
PrimaryButton(
  label    = "💬 Message ${firstName}",
  modifier = Modifier.fillMaxWidth(),
  onClick  = onMessageTap,
)
```

**Variant B — Requester (viewer IS the request owner):**
```
OutlinePrimaryButton(
  label    = "Edit Request",
  modifier = Modifier.fillMaxWidth(),
  onClick  = onEditTap,
)
```

> `onMessageTap` handles auth check internally in the screen — if unauthenticated, it redirects to Login.

### Sub-tasks
- [ ] **M05-T14-A** Implement `RequestDetailBottomBar` with both variants.
- [ ] **M05-T14-B** `WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()` added to bottom padding.
- [ ] **M05-T14-C** `@Preview` — guest variant (firstName="Ahmed"), requester variant, dark + light.

---

## Task M05-T15 — Shimmer Loading State

**File:** `presentation/request/detail/components/RequestDetailShimmer.kt`

### Exact Specification

```kotlin
@Composable
fun RequestDetailShimmer() {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0)
            .verticalScroll(rememberScrollState()),
    ) {
        // Header bar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).shimmer())
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).shimmer())
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).shimmer())
            }
        }

        // Profile block
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.size(84.dp).clip(CircleShape).shimmer())
            Spacer(14.dp)
            Box(Modifier.width(160.dp).height(24.dp).shimmer())
            Spacer(6.dp)
            Box(Modifier.width(200.dp).height(14.dp).shimmer())
        }
        Spacer(24.dp)

        // Budget banner
        Box(
            Modifier.fillMaxWidth().height(88.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp)).shimmer()
        )
        Spacer(24.dp)

        // Preferred area map
        Box(
            Modifier.fillMaxWidth().height(180.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(18.dp)).shimmer()
        )
        Spacer(24.dp)

        // Requirements chips
        Column(Modifier.padding(horizontal = 20.dp)) {
            Box(Modifier.width(120.dp).height(20.dp).shimmer())
            Spacer(10.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { Box(Modifier.width(80.dp).height(32.dp).clip(RoundedCornerShape(100.dp)).shimmer()) }
            }
        }
        Spacer(24.dp)

        // About bio
        Column(Modifier.padding(horizontal = 20.dp)) {
            Box(Modifier.width(140.dp).height(20.dp).shimmer())
            Spacer(10.dp)
            repeat(3) { Box(Modifier.fillMaxWidth().height(14.dp).shimmer()); Spacer(4.dp) }
        }
        Spacer(32.dp)
    }
}
```

### Sub-tasks
- [ ] **M05-T15-A** Implement `RequestDetailShimmer`.
- [ ] **M05-T15-B** `@Preview` dark mode.

---

## Task M05-T16 — String Resources

Append to `res/values/strings.xml`:

```xml
<!-- ─── Request Detail ──────────────────────────────────────────────────── -->
<string name="request_detail_report">Report this request</string>
<string name="request_detail_message_btn">💬 Message %1$s</string>
<string name="request_detail_edit_btn">Edit Request</string>
<string name="request_detail_share_cd">Share request</string>
<string name="request_detail_bookmark_cd">Save request</string>
<string name="request_detail_unbookmark_cd">Remove from saved</string>
<string name="request_detail_back_cd">Go back</string>

<string name="request_profile_looking_by">Looking for space by %1$s</string>
<string name="request_movein_immediate">Immediately</string>
<string name="request_movein_month">End of Month</string>
<string name="request_movein_flexible">Flexible Date</string>

<string name="request_budget_label">BUDGET UP TO</string>
<string name="request_budget_suffix">PKR/mo</string>

<string name="request_area_title">Preferred Area</string>
<string name="request_radius_pill">%1$d km radius</string>

<string name="request_requirements_title">Requirements</string>
<string name="request_beds_any">Any Beds</string>
<string name="request_beds_min">%1$d+ Beds</string>
<string name="request_baths_min">%1$d+ Baths</string>
<string name="request_furnish_any">Any Furnish</string>
<string name="request_furnish_furnished">Furnished</string>
<string name="request_furnish_semi">Semi-Furnished</string>

<string name="request_about_title">About the Tenant</string>
```

---

## Task M05-T17 — Unit Tests

### `RequestDetailViewModelTest.kt`

```
load_success_populatesRequestAndSaveState
load_failure_setsError
toggleSave_whenUnsaved_savesOptimisticallyAndCallsUseCase
toggleSave_whenSaved_unsavesOptimisticallyAndCallsUseCase
toggleSave_onFailure_revertsOptimisticUpdate
openReportSheet_resetsReportState
setReportCategory_updatesCategory
submitReport_withNoCategory_doesNothing
submitReport_success_setsReportSuccess
submitReport_failure_setsReportError
isOwner_trueWhenCurrentUserIsRequester
isOwner_falseWhenDifferentUser
isOwner_falseWhenUnauthenticated
```

### `GetRequestDetailUseCaseTest.kt`

```
invoke_delegatesToRepository
invoke_propagatesError
```

### Sub-tasks
- [ ] **M05-T17-A** Implement all tests using MockK + Turbine + `StandardTestDispatcher`.
- [ ] **M05-T17-B** Mock `TenantRequestRepository`, `SavedRepository`, `ReportRepository`, `AuthRepository`.
- [ ] **M05-T17-C** `./gradlew test` → all pass. Paste output.
- [ ] **M05-T17-D** `./gradlew koverReport` → ≥ 80% on `presentation.request.detail` + `domain.usecase.request`. Paste summary.

---

## Task M05-T18 — Build Gate

- [ ] **M05-T18-A** `./gradlew lint` → zero new warnings.
- [ ] **M05-T18-B** `./gradlew detekt` → zero violations.
- [ ] **M05-T18-C** `./gradlew assembleDebug` → `BUILD SUCCESSFUL`. Paste output.
- [ ] **M05-T18-D** `./gradlew test` → all pass. Paste output.
- [ ] **M05-T18-E** `./gradlew koverReport` → ≥ 80%. Paste summary.
- [ ] **M05-T18-F** Update `ANDROID_PROGRESS.md`.
- [ ] **M05-T18-G** Create `CODE_REVIEW_MODULE_05.md`.

---

## 23. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Load request via nav arg `requestId` | `LaunchedEffect(requestId) { viewModel.load(it) }` | ☐ |
| Shimmer shown while loading | `isLoading = true` → `RequestDetailShimmer()` | ☐ |
| Error screen with retry | `error != null && request == null` | ☐ |
| Back button navigates back | `onBack()` | ☐ |
| Bookmark button — saves request optimistically | `toggleSave()` + immediate flip | ☐ |
| Bookmark icon fills primary when saved | `isSaved` drives icon + tint | ☐ |
| Bookmark save failure — reverts | `onFailure { isSaved = wasSaved }` | ☐ |
| Share button — opens Android ShareSheet | `shareRequest(context, request)` | ☐ |
| Profile block — name displayed correctly | `request.requesterName` | ☐ |
| Profile block — move-in label formatted | `formatMoveIn(request.moveInDate)` | ☐ |
| Budget banner — gradient fill visible | `Brush.horizontalGradient(primaryTint → bg2)` | ☐ |
| Budget banner — price formatted (PKR Xk) | `formatBudget(budget)` | ☐ |
| Budget banner — intent badge correct | `intentLabel(intent)` | ☐ |
| Preferred area — `MapBackground` grid visible | `MapBackground` canvas lines | ☐ |
| Preferred area — `RadiusCircle` correct size | `diameter = 80dp + radiusKm × 12dp` | ☐ |
| Preferred area — area names in bottom-left pill | `preferredAreas.joinToString(", ")` | ☐ |
| Preferred area — radius shown in top-right pill | `"${radiusKm} km radius"` | ☐ |
| Requirements chips — all 4 shown (type, beds, baths, furnish) | `RequirementsSection` | ☐ |
| Requirements chips — non-interactive | `onClick = {}` no-op | ☐ |
| About tenant — italic curly-quote styling | `FontStyle.Italic` + `\u201C…\u201D` | ☐ |
| About tenant — hidden when bio is blank | `if (request.bio.isNotBlank())` guard | ☐ |
| Report link — hidden for unauthenticated | `if (isAuth)` guard | ☐ |
| Report link — opens ReportSheet | `viewModel.openReportSheet()` | ☐ |
| Report sheet — "Report Request" title | `targetLabel = "Request"` | ☐ |
| Report sheet — submit disabled until category selected | `enabled = selectedCategory != null` | ☐ |
| Report sheet — success auto-dismisses after 2 s | `LaunchedEffect` in `ReportSheet` | ☐ |
| Bottom bar — guest: "💬 Message {firstName}" | `!isOwner` variant | ☐ |
| Bottom bar — message tap when auth: navigate to chat | `onNavigateToChat(buildRequestChatId(...))` | ☐ |
| Bottom bar — message tap when unauth: navigate to Login | `!isAuth → onNavigateToLogin()` | ☐ |
| Bottom bar — requester: "Edit Request" | `isOwner` variant | ☐ |

---

## 24. CODE_REVIEW_MODULE_05.md Template

```markdown
# Code Review — Module 05: Tenant Request Detail
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-05-request-detail
**Spec version:** ANDROID_MODULE_05.md v1.0.0

---

## ✅ Shared Components Refactor
- [ ] `ReportSheet.kt` moved to `presentation/shared/components/` — no duplication
- [ ] Import updated in `ListingDetailScreen.kt`
- [ ] `MapBackground.kt` in `shared/components/` — correct grid spec (28dp spacing, border colour, 1dp stroke)
- [ ] `RadiusCircle.kt` in `shared/components/` — diameter formula `80dp + (radiusKm × 12dp)` verified
- [ ] `RadiusCircle` `animate=false` on detail screen (static); `animate=true` on request form (interactive)

---

## ✅ Architecture Compliance
- [ ] All use cases delegate to `TenantRequestRepository` / `SavedRepository` — no direct Firestore calls
- [ ] `RequestDetailViewModel` uses use cases — not repositories directly
- [ ] `SubmitReportUseCase` reused from M04 — not duplicated
- [ ] No new Koin module file — bindings added to `listingDetailModule`

---

## ✅ Module 01 Backfills
| Item | File | Notes |
|------|------|-------|
| `RentoIcons.BookmarkFilled` | `RentoIcons.kt` | Filled bookmark for saved state |
| `RentoIcons.Bookmark` | `RentoIcons.kt` | Outline bookmark |

---

## ✅ Design Reference Verification

| Component | Spec Ref | BG ✓ | Radii ✓ | Font/Size/Weight ✓ | Icons ✓ | Animations ✓ | States ✓ |
|-----------|---------|------|---------|-------------------|---------|--------------|---------|
| Header row (back, bookmark, share) | §12.1 | | | | | | |
| Profile block (84dp avatar, name, label) | §12.1 | | | | | | |
| Budget banner (gradient, SectionLabel, badge) | §12.1 | | | | | | |
| MapBackground (grid lines 28dp) | §3.4.17 | | | | | | |
| RadiusCircle (formula, fill, stroke, dot) | §3.4.18 | | | | | | |
| Preferred area pills (bottom-left, top-right) | §12.1 | | | | | | |
| Requirements chip row | §12.1 | | | | | | |
| About Tenant (italic, curly quotes) | §12.1 | | | | | | |
| Sticky bottom bar — guest | §12.1 | | | | | | |
| Sticky bottom bar — requester | §12.1 | | | | | | |
| ReportSheet (shared) | §32.8 | | | | | | |

---

## ✅ Design System Compliance
- [ ] Zero hardcoded colours — all via `MaterialTheme.rentoColors.*`
- [ ] `SectionLabel`, `Badge`, `RentoChip` from Module 01 reused
- [ ] `RentoIconButton` (42dp standard) used for header buttons — NOT glassmorphic
- [ ] `PrimaryButton`, `OutlinePrimaryButton` from Module 01 in bottom bar
- [ ] `shimmer()` from Module 01 in shimmer component
- [ ] `Modifier.blur()` guarded with API 31+ check in `PreferredAreaSection`
- [ ] Curly quotes used: `\u201C` and `\u201D` — NOT straight `"`

---

## ✅ Journey Coverage
- [ ] All 30 journeys in Section 23 verified

---

## ✅ Code Quality
- [ ] All strings in `strings.xml`
- [ ] `./gradlew test` → ✅ PASSING
- [ ] `./gradlew assembleDebug` → ✅ PASSING
- [ ] `./gradlew detekt` → 0 violations
- [ ] `./gradlew koverReport` → ≥ 80%

---

## ⚠️ Lint Findings (Non-Blocking)
| ID | File | Line | Finding | Severity |
|----|------|------|---------|----------|

---

## 📝 Notes
```

---

*End of Module 05 — Tenant Request Detail v1.0.0*
*Depends on: Modules 01–04. Next module: Module 06 — Property Listing Form (11-step wizard).*
