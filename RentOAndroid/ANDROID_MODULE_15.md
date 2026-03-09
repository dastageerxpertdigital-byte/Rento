# RentO — Android App
## Module 15 — My Dashboard (My Listings & My Requests)
### Complete Engineering Specification

> **Version:** 1.0.0  
> **Branch:** `feature/module-15-my-dashboard`  
> **Depends on:** Modules 01–14 ✅  
> **Audience:** Android Agent  
>
> Both screens share the same shell architecture. My Listings has 5 tabs; My Requests has 4 tabs.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture](#4-architecture)
5. [Task M15-T01 — Domain Models + Use Cases](#task-m15-t01--domain-models--use-cases)
6. [Task M15-T02 — `DashboardRepository` Interface + Impl](#task-m15-t02--dashboardrepository-interface--impl)
7. [Task M15-T03 — `MyListingsViewModel`](#task-m15-t03--mylistingsviewmodel)
8. [Task M15-T04 — `MyRequestsViewModel`](#task-m15-t04--myrequestsviewmodel)
9. [Task M15-T05 — Koin Module](#task-m15-t05--koin-module)
10. [Task M15-T06 — Navigation Wiring](#task-m15-t06--navigation-wiring)
11. [Task M15-T07 — `DashboardShell` Shared Composable](#task-m15-t07--dashboardshell-shared-composable)
12. [Task M15-T08 — `MyListingsScreen`](#task-m15-t08--mylistingsscreen)
13. [Task M15-T09 — Publish Slot Banner](#task-m15-t09--publish-slot-banner)
14. [Task M15-T10 — Listing Tab Cards](#task-m15-t10--listing-tab-cards)
15. [Task M15-T11 — `MyRequestsScreen`](#task-m15-t11--myrequestsscreen)
16. [Task M15-T12 — Request Slot Banner](#task-m15-t12--request-slot-banner)
17. [Task M15-T13 — Request Tab Cards](#task-m15-t13--request-tab-cards)
18. [Task M15-T14 — Shimmer States](#task-m15-t14--shimmer-states)
19. [Task M15-T15 — String Resources](#task-m15-t15--string-resources)
20. [Task M15-T16 — Unit Tests](#task-m15-t16--unit-tests)
21. [Task M15-T17 — Build Gate](#task-m15-t17--build-gate)
22. [Journey Coverage Checklist](#22-journey-coverage-checklist)
23. [CODE_REVIEW_MODULE_15.md Template](#23-code_review_module_15md-template)

---

## 1. Module Overview

Module 15 delivers two profile-accessible dashboard screens:

### My Listings (`my_listings`)
For hosts to manage their property listings across 5 status tabs:
- **Published** — Live listings with Edit + Unpublish actions
- **Drafts** — Incomplete listings with Continue button + progress bar
- **Pending** — Awaiting admin approval card
- **Rejected** — Admin note + "Edit & Resubmit" button
- **Blocked** — Admin reason + "Contact Support" link

**Publish Slot Banner** shows X/Y slots used with progress bar + "Upgrade" link.

### My Requests (`my_requests`)
For seekers to manage their tenant requests across 4 tabs:
- **Active** — Live requests with Edit + Close actions
- **Pending** — Awaiting approval (if config `requireRequestApproval = true`)
- **Closed** — Closed requests (by user)
- **Rejected** — Admin rejection reason

**Request Slot Banner** shows X/Y request slots used.

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── MyListing.kt
│   │   └── MyRequest.kt
│   ├── repository/
│   │   └── DashboardRepository.kt
│   └── usecase/
│       └── dashboard/
│           ├── GetMyListingsUseCase.kt
│           ├── GetMyRequestsUseCase.kt
│           ├── UnpublishListingUseCase.kt
│           ├── DeleteListingUseCase.kt
│           ├── CloseRequestUseCase.kt
│           └── GetPublishSlotsUseCase.kt
├── data/
│   └── repository/
│       └── DashboardRepositoryImpl.kt
└── presentation/
    └── dashboard/
        ├── listings/
        │   ├── MyListingsViewModel.kt
        │   └── MyListingsScreen.kt
        ├── requests/
        │   ├── MyRequestsViewModel.kt
        │   └── MyRequestsScreen.kt
        └── components/
            ├── DashboardShell.kt
            ├── SlotBanner.kt
            ├── DashboardTabRow.kt
            ├── PublishedListingCard.kt
            ├── DraftListingCard.kt
            ├── PendingCard.kt
            ├── RejectedListingCard.kt
            ├── BlockedListingCard.kt
            ├── ActiveRequestCard.kt
            ├── RejectedRequestCard.kt
            ├── ClosedRequestCard.kt
            └── DashboardShimmer.kt
di/
└── DashboardModule.kt
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M15-T01 | Domain models + use cases | ☐ |
| M15-T02 | `DashboardRepository` interface + impl | ☐ |
| M15-T03 | `MyListingsViewModel` | ☐ |
| M15-T04 | `MyRequestsViewModel` | ☐ |
| M15-T05 | Koin module | ☐ |
| M15-T06 | Navigation wiring | ☐ |
| M15-T07 | `DashboardShell` shared composable + `DashboardTabRow` | ☐ |
| M15-T08 | `MyListingsScreen` — shell wiring, tab switching | ☐ |
| M15-T09 | `SlotBanner` — publish slots progress | ☐ |
| M15-T10 | All 5 listing tab cards (Published, Draft, Pending, Rejected, Blocked) | ☐ |
| M15-T11 | `MyRequestsScreen` | ☐ |
| M15-T12 | Request slot banner | ☐ |
| M15-T13 | All 4 request tab cards (Active, Pending, Closed, Rejected) | ☐ |
| M15-T14 | Shimmer states | ☐ |
| M15-T15 | String resources | ☐ |
| M15-T16 | Unit tests | ☐ |
| M15-T17 | Build gate | ☐ |

---

## 4. Architecture

### 4.1 Domain Models

```kotlin
// MyListing.kt — richer than MapListing — full detail for dashboard
data class MyListing(
    val id: String,
    val title: String,
    val city: String,
    val propertyType: String,
    val price: Int,
    val status: String,          // "published" | "draft" | "pending_approval" | "rejected" | "blocked"
    val photoUrl: String?,
    val intent: String,
    val rejectionReason: String?,
    val adminDeleteMessage: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val completionPercent: Int,  // for drafts — computed from non-null fields
)

// MyRequest.kt
data class MyRequest(
    val id: String,
    val propertyType: String,
    val budgetMax: Int,
    val city: String,
    val preferredAreas: List<String>,
    val radiusKm: Int,
    val status: String,          // "active" | "pending_approval" | "closed" | "rejected"
    val rejectionReason: String?,
    val createdAt: Long,
)
```

### 4.2 `DashboardRepository` Interface

```kotlin
interface DashboardRepository {
    fun getMyListings(uid: String): Flow<List<MyListing>>
    fun getMyRequests(uid: String): Flow<List<MyRequest>>
    suspend fun unpublishListing(listingId: String): Result<Unit>
    suspend fun deleteListing(listingId: String): Result<Unit>
    suspend fun closeRequest(requestId: String): Result<Unit>
    suspend fun getPublishSlots(uid: String): Result<Pair<Int, Int>>   // (used, max)
    suspend fun getRequestSlots(uid: String): Result<Pair<Int, Int>>   // (used, max)
}
```

### 4.3 UI States

```kotlin
// MyListingsUiState
data class MyListingsUiState(
    val selectedTab: ListingTab = ListingTab.PUBLISHED,
    val listings: List<MyListing> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val publishedCount: Int = 0,
    val maxPublish: Int = 3,        // from config — defaults to Free tier
    val showUnpublishConfirm: String? = null,   // listingId
    val actionLoading: String? = null,          // listingId of listing being acted on
)

enum class ListingTab { PUBLISHED, DRAFTS, PENDING, REJECTED, BLOCKED }

// MyRequestsUiState
data class MyRequestsUiState(
    val selectedTab: RequestTab = RequestTab.ACTIVE,
    val requests: List<MyRequest> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val activeCount: Int = 0,
    val maxRequests: Int = 2,
    val actionLoading: String? = null,
)

enum class RequestTab { ACTIVE, PENDING, CLOSED, REJECTED }
```

### 4.4 Koin

```kotlin
val dashboardModule = module {
    single<DashboardRepository>      { DashboardRepositoryImpl(get()) }
    factory { GetMyListingsUseCase(get()) }
    factory { GetMyRequestsUseCase(get()) }
    factory { UnpublishListingUseCase(get()) }
    factory { DeleteListingUseCase(get()) }
    factory { CloseRequestUseCase(get()) }
    factory { GetPublishSlotsUseCase(get()) }
    viewModel { MyListingsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { MyRequestsViewModel(get(), get(), get(), get()) }
}
```

---

## Task M15-T01 — Domain Models + Use Cases

### Sub-tasks
- [ ] **M15-T01-A** Create `MyListing.kt` + `MyRequest.kt` per section 4.1. Zero Android imports.
- [ ] **M15-T01-B** Create all 6 use cases:

```kotlin
class GetMyListingsUseCase(private val repo: DashboardRepository, private val auth: AuthRepository) {
    operator fun invoke(): Flow<List<MyListing>> {
        val uid = auth.getCurrentUserId() ?: return emptyFlow()
        return repo.getMyListings(uid)
    }
}

class GetMyRequestsUseCase(private val repo: DashboardRepository, private val auth: AuthRepository) {
    operator fun invoke(): Flow<List<MyRequest>> {
        val uid = auth.getCurrentUserId() ?: return emptyFlow()
        return repo.getMyRequests(uid)
    }
}

class UnpublishListingUseCase(private val repo: DashboardRepository) {
    suspend operator fun invoke(listingId: String): Result<Unit> =
        repo.unpublishListing(listingId)
}

class DeleteListingUseCase(private val repo: DashboardRepository) {
    suspend operator fun invoke(listingId: String): Result<Unit> =
        repo.deleteListing(listingId)
}

class CloseRequestUseCase(private val repo: DashboardRepository) {
    suspend operator fun invoke(requestId: String): Result<Unit> =
        repo.closeRequest(requestId)
}

class GetPublishSlotsUseCase(private val repo: DashboardRepository, private val auth: AuthRepository) {
    suspend operator fun invoke(): Result<Pair<Int, Int>> {
        val uid = auth.getCurrentUserId() ?: return Result.failure(Exception("Not authenticated."))
        return repo.getPublishSlots(uid)
    }
}
```

---

## Task M15-T02 — `DashboardRepositoryImpl`

**File:** `data/repository/DashboardRepositoryImpl.kt`

```kotlin
class DashboardRepositoryImpl(private val firestore: FirebaseFirestore) : DashboardRepository {

    override fun getMyListings(uid: String): Flow<List<MyListing>> = callbackFlow {
        val query = firestore.collection("listings")
            .whereEqualTo("uid", uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            val items = snap?.documents?.mapNotNull { it.toMyListing() } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    override fun getMyRequests(uid: String): Flow<List<MyRequest>> = callbackFlow {
        val query = firestore.collection("tenantRequests")
            .whereEqualTo("uid", uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            val items = snap?.documents?.mapNotNull { it.toMyRequest() } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun unpublishListing(listingId: String): Result<Unit> = runCatching {
        firestore.collection("listings").document(listingId)
            .update("status", "draft", "updatedAt", FieldValue.serverTimestamp()).await()
    }

    override suspend fun deleteListing(listingId: String): Result<Unit> = runCatching {
        // Note: Does NOT delete Firebase Storage photos — that is done by a Cloud Function
        // triggered on Firestore document delete. The Cloud Function is outside Android scope.
        firestore.collection("listings").document(listingId).delete().await()
    }

    override suspend fun closeRequest(requestId: String): Result<Unit> = runCatching {
        firestore.collection("tenantRequests").document(requestId)
            .update("status", "closed", "updatedAt", FieldValue.serverTimestamp()).await()
    }

    override suspend fun getPublishSlots(uid: String): Result<Pair<Int, Int>> = runCatching {
        val config = firestore.collection("config").document("listing").get().await()
        // Get user's subscription info to know their max
        val userDoc = firestore.collection("users").document(uid).get().await()
        val maxSlots = userDoc.getLong("maxPublishedListings")?.toInt()
            ?: config.getLong("freeMaxPublished")?.toInt()
            ?: 3
        val used = firestore.collection("listings")
            .whereEqualTo("uid", uid)
            .whereEqualTo("status", "published")
            .get().await().size()
        Pair(used, maxSlots)
    }

    override suspend fun getRequestSlots(uid: String): Result<Pair<Int, Int>> = runCatching {
        val config = firestore.collection("config").document("request").get().await()
        val userDoc = firestore.collection("users").document(uid).get().await()
        val maxSlots = userDoc.getLong("maxPublishedRequests")?.toInt()
            ?: config.getLong("freeMaxRequests")?.toInt()
            ?: 2
        val used = firestore.collection("tenantRequests")
            .whereEqualTo("uid", uid)
            .whereEqualTo("status", "active")
            .get().await().size()
        Pair(used, maxSlots)
    }
}
```

---

## Task M15-T03 — `MyListingsViewModel`

```kotlin
class MyListingsViewModel(
    private val getListings: GetMyListingsUseCase,
    private val unpublish: UnpublishListingUseCase,
    private val delete: DeleteListingUseCase,
    private val getSlots: GetPublishSlotsUseCase,
    private val auth: AuthRepository,
    private val config: ConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyListingsUiState())
    val uiState: StateFlow<MyListingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getListings()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { all ->
                    val published = all.count { it.status == "published" }
                    _uiState.update { it.copy(listings = all, isLoading = false, publishedCount = published) }
                }
        }
        viewModelScope.launch {
            getSlots().onSuccess { (used, max) ->
                _uiState.update { it.copy(publishedCount = used, maxPublish = max) }
            }
        }
    }

    fun selectTab(tab: ListingTab) = _uiState.update { it.copy(selectedTab = tab) }

    // Filtered listings for selected tab
    val filteredListings: StateFlow<List<MyListing>> = combine(
        uiState.map { it.listings },
        uiState.map { it.selectedTab },
    ) { all, tab ->
        all.filter { it.status == tab.statusKey }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun unpublish(listingId: String) {
        _uiState.update { it.copy(actionLoading = listingId) }
        viewModelScope.launch {
            unpublish.invoke(listingId).fold(
                onSuccess = { _uiState.update { it.copy(actionLoading = null) } },
                onFailure = { e -> _uiState.update { it.copy(actionLoading = null, error = e.message) } },
            )
        }
    }

    fun delete(listingId: String) {
        _uiState.update { it.copy(actionLoading = listingId) }
        viewModelScope.launch {
            delete.invoke(listingId).fold(
                onSuccess = { _uiState.update { it.copy(actionLoading = null) } },
                onFailure = { e -> _uiState.update { it.copy(actionLoading = null, error = e.message) } },
            )
        }
    }
}

val ListingTab.statusKey: String get() = when (this) {
    ListingTab.PUBLISHED -> "published"
    ListingTab.DRAFTS    -> "draft"
    ListingTab.PENDING   -> "pending_approval"
    ListingTab.REJECTED  -> "rejected"
    ListingTab.BLOCKED   -> "blocked"
}
```

---

## Task M15-T04 — `MyRequestsViewModel`

```kotlin
class MyRequestsViewModel(
    private val getRequests: GetMyRequestsUseCase,
    private val closeReq: CloseRequestUseCase,
    private val getSlots: GetPublishSlotsUseCase,   // reuse for request slots via a separate use case
    private val auth: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyRequestsUiState())
    val uiState: StateFlow<MyRequestsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getRequests()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { all ->
                    val active = all.count { it.status == "active" }
                    _uiState.update { it.copy(requests = all, isLoading = false, activeCount = active) }
                }
        }
    }

    fun selectTab(tab: RequestTab) = _uiState.update { it.copy(selectedTab = tab) }

    val filteredRequests: StateFlow<List<MyRequest>> = combine(
        uiState.map { it.requests },
        uiState.map { it.selectedTab },
    ) { all, tab ->
        all.filter { it.status == tab.statusKey }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun close(requestId: String) {
        _uiState.update { it.copy(actionLoading = requestId) }
        viewModelScope.launch {
            closeReq(requestId).fold(
                onSuccess = { _uiState.update { it.copy(actionLoading = null) } },
                onFailure = { e -> _uiState.update { it.copy(actionLoading = null, error = e.message) } },
            )
        }
    }
}

val RequestTab.statusKey: String get() = when (this) {
    RequestTab.ACTIVE   -> "active"
    RequestTab.PENDING  -> "pending_approval"
    RequestTab.CLOSED   -> "closed"
    RequestTab.REJECTED -> "rejected"
}
```

---

## Task M15-T06 — Navigation Wiring

```kotlin
composable("my_listings") {
    MyListingsScreen(
        onNavigateToListingForm     = { navController.navigate("listing/form") },
        onNavigateToListingEdit     = { id -> navController.navigate("listing/form?listingId=$id&mode=edit") },
        onNavigateToListingDetail   = { id -> navController.navigate("listing/$id") },
        onNavigateToPackages        = { navController.navigate("packages") },
    )
}

composable("my_requests") {
    MyRequestsScreen(
        onNavigateToRequestForm     = { navController.navigate("request/form") },
        onNavigateToRequestEdit     = { id -> navController.navigate("request/form?requestId=$id&mode=edit") },
        onNavigateToRequestDetail   = { id -> navController.navigate("request/$id") },
        onNavigateToPackages        = { navController.navigate("packages") },
    )
}
```

---

## Task M15-T07 — `DashboardShell` Shared Composable

**File:** `presentation/dashboard/components/DashboardShell.kt`

```kotlin
@Composable
fun DashboardShell(
    title: String,
    onNewClick: () -> Unit,
    newButtonLabel: String = "New +",
    slotBanner: @Composable () -> Unit,
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelect: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0),
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Spacer(Modifier.height(16.dp))

        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = MaterialTheme.rentoTypography.displayS, color = MaterialTheme.rentoColors.t0)
            // "New +" button
            PrimaryButton(
                text     = newButtonLabel,
                modifier = Modifier.wrapContentWidth(),
                onClick  = onNewClick,
                // Smaller: 10dp V / 18dp H padding, 13sp
            )
        }

        // Slot banner
        Box(Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)) {
            slotBanner()
        }

        // Tab row
        DashboardTabRow(
            tabs            = tabs,
            selectedIndex   = selectedTabIndex,
            onSelect        = onTabSelect,
        )

        // Tab content
        content()
    }
}
```

### `DashboardTabRow` — Exact Specification

Horizontal scrollable chip row:

```kotlin
@Composable
fun DashboardTabRow(tabs: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    LazyRow(
        contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier            = Modifier.padding(bottom = 16.dp),
    ) {
        itemsIndexed(tabs) { idx, label ->
            RentoChip(
                label    = label,
                selected = idx == selectedIndex,
                onClick  = { onSelect(idx) },
            )
        }
    }
}
```

---

## Task M15-T08 — `MyListingsScreen`

```kotlin
@Composable
fun MyListingsScreen(
    viewModel: MyListingsViewModel = koinViewModel(),
    onNavigateToListingForm: () -> Unit,
    onNavigateToListingEdit: (String) -> Unit,
    onNavigateToListingDetail: (String) -> Unit,
    onNavigateToPackages: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filtered by viewModel.filteredListings.collectAsStateWithLifecycle()

    val tabs = listOf("Published", "Drafts", "Pending", "Rejected", "Blocked")

    DashboardShell(
        title          = "My Listings",
        onNewClick     = onNavigateToListingForm,
        slotBanner     = {
            SlotBanner(
                label      = "Publish Slots",
                used       = uiState.publishedCount,
                max        = uiState.maxPublish,
                onUpgrade  = onNavigateToPackages,
            )
        },
        tabs           = tabs,
        selectedTabIndex = uiState.selectedTab.ordinal,
        onTabSelect    = { viewModel.selectTab(ListingTab.values()[it]) },
    ) {
        if (uiState.isLoading) {
            DashboardShimmer()
        } else {
            AnimatedContent(targetState = uiState.selectedTab, label = "ListingTab") { tab ->
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (filtered.isEmpty()) {
                        item {
                            EmptyTabState(tab = tab.name.lowercase())
                        }
                    } else {
                        items(filtered, key = { it.id }) { listing ->
                            when (tab) {
                                ListingTab.PUBLISHED -> PublishedListingCard(
                                    listing  = listing,
                                    isActing = uiState.actionLoading == listing.id,
                                    onEdit   = { onNavigateToListingEdit(listing.id) },
                                    onUnpublish = { viewModel.unpublish(listing.id) },
                                    onTap    = { onNavigateToListingDetail(listing.id) },
                                )
                                ListingTab.DRAFTS    -> DraftListingCard(
                                    listing  = listing,
                                    onContinue = { onNavigateToListingEdit(listing.id) },
                                )
                                ListingTab.PENDING   -> PendingCard(type = "listing")
                                ListingTab.REJECTED  -> RejectedListingCard(
                                    listing  = listing,
                                    onEditResubmit = { onNavigateToListingEdit(listing.id) },
                                )
                                ListingTab.BLOCKED   -> BlockedListingCard(listing = listing)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(120.dp)) }   // nav bar clearance
                }
            }
        }
    }
}
```

---

## Task M15-T09 — Publish Slot Banner

**File:** `presentation/dashboard/components/SlotBanner.kt`

```
Box(
  fillMaxWidth,
  background = RentoColors.primaryTint,
  border     = 1.dp RentoColors.primaryRing,
  shape      = RoundedCornerShape(18.dp),
  padding    = 14.dp / 16.dp,
):
  Column:
    Row(fillMaxWidth, SpaceBetween, CenterVertically):
      Text(label, 13sp, t1)                              // "Publish Slots"
      Text("$used / $max", 13sp Bold, primary)
    Spacer(6.dp)
    LinearProgressIndicator(
      progress   = { used.toFloat() / max.toFloat() },
      modifier   = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
      color      = RentoColors.primary,
      trackColor = RentoColors.bg4,
    )
    Spacer(6.dp)
    Row:
      Text("Free plan · ", 11sp, t2)
      Text("Upgrade →", 11sp Bold primary, clickable = onUpgrade)
```

---

## Task M15-T10 — Listing Tab Cards

### Published Listing Card

```
Row(
  fillMaxWidth, CenterVertically,
  background = RentoColors.bg2,
  border     = 1.5.dp RentoColors.border,
  shape      = RoundedCornerShape(20.dp),
  clickable  = onTap,
):
  // Left image area (88dp wide)
  Box(88.dp × fillMaxHeight, background=gradient, clip=RoundedCornerShape(topStart=20, bottomStart=20)):
    if photoUrl != null:
      AsyncImage(...)
    else:
      Text(propertyEmoji(listing.intent), 28sp, centred)   // 🏠 / 🛋️ / 🕐

  // Right content (14dp padding)
  Column(Modifier.weight(1f).padding(14.dp)):
    Text(listing.title, 13sp Bold, t0, overflow=Ellipsis, maxLines=1)
    Spacer(2.dp)
    Text("${listing.city} · ${listing.propertyType}", 12sp, t2)
    Spacer(4.dp)
    Row(CenterVertically, spacedBy=6.dp):
      // "● Live" badge
      Box(RentoColors.primaryTint, RoundedCornerShape(100.dp), padding=4.dp H / 2.dp V):
        Row(CenterVertically, spacedBy=4.dp):
          Box(6.dp circle, RentoColors.primary)   // green dot
          Text("Live", 10sp Bold, primary)
      Text("PKR ${formatPrice(listing.price)}/mo", 13sp Bold, primary)
    Spacer(8.dp)
    // Action row
    Row(spacedBy=8.dp):
      OutlinePrimaryButton("Edit", Modifier.weight(1f), onClick=onEdit)
      if isActing:
        Box(Modifier.weight(1f), Center):
          CircularProgressIndicator(20.dp, strokeWidth=2.dp)
      else:
        GhostButton("Unpublish", Modifier.weight(1f), onClick=onUnpublish)
```

### Draft Listing Card

```
Column(bg2, border, 20dp corner, 14dp padding):
  Row(SpaceBetween, CenterVertically):
    Text(listing.title, 13sp Bold, t0)
    // Completion % badge
    Box(DarkPriM, RoundedCornerShape(6.dp), 4.dp V / 8.dp H):
      Text("${listing.completionPercent}%", 11sp Bold, primary)
  Spacer(6.dp)
  Text("${listing.city} · ${listing.propertyType}", 12sp, t2)
  Spacer(8.dp)
  // Progress bar
  LinearProgressIndicator(
    progress   = { listing.completionPercent / 100f },
    color      = RentoColors.primary,
    trackColor = RentoColors.bg4,
    modifier   = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
  )
  Spacer(10.dp)
  PrimaryButton("Continue", Modifier.fillMaxWidth(), onClick=onContinue)
```

### Pending Card (same for listings and requests)

```
Box(
  fillMaxWidth,
  background = RentoColors.primaryTint,
  border     = 1.dp RentoColors.primaryRing,
  shape      = RoundedCornerShape(20.dp),
  padding    = 20.dp,
  contentAlignment = Center,
):
  Column(Center, spacedBy=6.dp):
    Text("⏳", 44sp)
    Text("Awaiting Approval", 14sp Bold, t0, centred)
    Text("Usually reviewed within a few hours.", 13sp, t2, centred)
```

### Rejected Listing Card

```
Column(
  background = Color(0x22E05555),      // DarkRedM
  border     = 1.dp Color(0x38E05555), // DarkRed/22%
  shape      = RoundedCornerShape(20.dp),
  padding    = 16.dp,
):
  Row(CenterVertically, spacedBy=8.dp):
    Text("❌", 28sp)
    Column(weight=1f):
      Text(listing.title, 13sp Bold, t0, maxLines=1, overflow=Ellipsis)
      Spacer(2.dp)
      // "Rejected" badge
      Badge("Rejected", Color(0xFFE05555), Color.White)
  if listing.rejectionReason != null:
    Spacer(10.dp)
    Box(
      fillMaxWidth,
      background = Color(0x22E05555),
      shape      = RoundedCornerShape(11.dp),
      padding    = 13.dp / 11.dp,
    ):
      Text(listing.rejectionReason, 13sp, t1)
  Spacer(12.dp)
  PrimaryButton("Edit & Resubmit", Modifier.fillMaxWidth(), onClick=onEditResubmit)
```

### Blocked Listing Card

Same as Rejected but:
- Uses admin message from `listing.adminDeleteMessage`
- Instead of "Edit & Resubmit": shows "Contact Support" text link (opens email intent)

### Sub-tasks
- [ ] **M15-T10-A** Implement all 5 listing card composables.
- [ ] **M15-T10-B** `propertyEmoji()` helper: `"share" → "🛋️"`, `"fullRent" → "🏠"`, `"hourly" → "🕐"`.
- [ ] **M15-T10-C** `@Preview` each card variant, dark + light.

---

## Task M15-T11 — `MyRequestsScreen`

Same shell pattern as `MyListingsScreen`. Uses `DashboardShell` with:
- Title: "My Requests"
- Tabs: ["Active", "Pending", "Closed", "Rejected"]
- "New +" → `onNavigateToRequestForm()`
- Slot banner: request slots

```kotlin
// Similar structure — when tab is ACTIVE:
ActiveRequestCard(
    request  = request,
    isActing = uiState.actionLoading == request.id,
    onEdit   = { onNavigateToRequestEdit(request.id) },
    onClose  = { viewModel.close(request.id) },
    onTap    = { onNavigateToRequestDetail(request.id) },
)
```

---

## Task M15-T12 — Request Slot Banner

Same `SlotBanner` composable as M15-T09 but with:
- `label = "Request Slots"`
- `used = uiState.activeCount`
- `max = uiState.maxRequests`

---

## Task M15-T13 — Request Tab Cards

### Active Request Card

```
Row(bg2, border, 20dp corner, clickable=onTap):
  // Left accent strip (8dp wide, primary gradient, full height)
  Box(8.dp wide, fillMaxHeight, Brush.linear(primary→secondary))
  Column(Modifier.weight(1f).padding(14.dp)):
    Row(SpaceBetween):
      Text("${request.propertyType} in ${request.city}", 13sp Bold, t0)
      // "Active" badge
      Badge("Active", primaryTint, primary)
    Spacer(4.dp)
    Text("Budget: PKR ${formatBudget(request.budgetMax)}/mo", 12sp, t1)
    Spacer(4.dp)
    Text("${request.preferredAreas.take(2).joinToString(", ")}", 12sp, t2, maxLines=1, overflow=Ellipsis)
    Spacer(10.dp)
    Row(spacedBy=8.dp):
      OutlinePrimaryButton("Edit", Modifier.weight(1f), onClick=onEdit)
      GhostButton("Close", Modifier.weight(1f), onClick=onClose)
```

### Closed Request Card — minimal, greyed out with "Closed" badge
### Rejected Request Card — same as RejectedListingCard but for requests

### Sub-tasks
- [ ] **M15-T13-A** Implement `ActiveRequestCard`, `ClosedRequestCard`, `RejectedRequestCard`.
- [ ] **M15-T13-B** Pending tab for requests uses same `PendingCard(type = "request")`.

---

## Task M15-T14 — Shimmer States

**File:** `presentation/dashboard/components/DashboardShimmer.kt`

```kotlin
@Composable
fun DashboardShimmer() {
    Column(
        Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(4) {
            Row(
                Modifier.fillMaxWidth().height(90.dp)
                    .clip(RoundedCornerShape(20.dp)).shimmer(),
            ) {}
        }
    }
}
```

---

## Task M15-T15 — String Resources

```xml
<!-- ─── My Listings ──────────────────────────────────────────────── -->
<string name="my_listings_title">My Listings</string>
<string name="my_listings_new">New +</string>
<string name="my_listings_tab_published">Published</string>
<string name="my_listings_tab_drafts">Drafts</string>
<string name="my_listings_tab_pending">Pending</string>
<string name="my_listings_tab_rejected">Rejected</string>
<string name="my_listings_tab_blocked">Blocked</string>
<string name="my_listings_slot_label">Publish Slots</string>
<string name="my_listings_upgrade">Upgrade →</string>
<string name="my_listings_free_plan">Free plan · </string>
<string name="my_listings_live">Live</string>
<string name="my_listings_edit">Edit</string>
<string name="my_listings_unpublish">Unpublish</string>
<string name="my_listings_continue">Continue</string>
<string name="my_listings_edit_resubmit">Edit &amp; Resubmit</string>
<string name="my_listings_contact_support">Contact Support</string>
<string name="my_listings_pending_title">Awaiting Approval</string>
<string name="my_listings_pending_subtitle">Usually reviewed within a few hours.</string>
<string name="my_listings_empty">No listings here yet.</string>

<!-- ─── My Requests ──────────────────────────────────────────────── -->
<string name="my_requests_title">My Requests</string>
<string name="my_requests_new">New +</string>
<string name="my_requests_tab_active">Active</string>
<string name="my_requests_tab_pending">Pending</string>
<string name="my_requests_tab_closed">Closed</string>
<string name="my_requests_tab_rejected">Rejected</string>
<string name="my_requests_slot_label">Request Slots</string>
<string name="my_requests_edit">Edit</string>
<string name="my_requests_close">Close</string>
<string name="my_requests_empty">No requests here yet.</string>
```

---

## Task M15-T16 — Unit Tests

### `MyListingsViewModelTest.kt`
```
init_collectsListings_groupsByStatus
selectTab_updatesSelectedTab
filteredListings_publishedTab_returnsPublished
filteredListings_draftsTab_returnsDrafts
unpublish_setsActionLoading_thenClears
unpublish_failure_setsError
```

### `MyRequestsViewModelTest.kt`
```
init_collectsRequests
selectTab_updatesTab
filteredRequests_activeTab_returnsActive
close_setsActionLoading_thenClears
close_failure_setsError
```

---

## Task M15-T17 — Build Gate

- [ ] `./gradlew lint` → zero new warnings.
- [ ] `./gradlew detekt` → zero violations.
- [ ] `./gradlew assembleDebug` → `BUILD SUCCESSFUL`.
- [ ] `./gradlew test` → all pass.
- [ ] Update `ANDROID_PROGRESS.md`. Create `CODE_REVIEW_MODULE_15.md`.

---

## 22. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| My Listings screen loads user's listings | `GetMyListingsUseCase` Flow | ☐ |
| Publish slot banner shows X/Y progress bar | `SlotBanner(used, max)` | ☐ |
| "New +" navigates to Listing Form | `onNavigateToListingForm()` | ☐ |
| Tab switch filters correctly | `filteredListings` by `statusKey` | ☐ |
| Published card — "● Live" badge | green dot + "Live" label | ☐ |
| Published card — "Edit" → Listing Edit Form | `onNavigateToListingEdit()` | ☐ |
| Published card — "Unpublish" → sets status=draft | `UnpublishListingUseCase` | ☐ |
| Draft card — completion % badge + progress bar | `completionPercent` | ☐ |
| Draft card — "Continue" → Listing Edit Form | `onNavigateToListingEdit()` | ☐ |
| Pending tab — hourglass card shown | `PendingCard` | ☐ |
| Rejected tab — red card + admin note | `RejectedListingCard` | ☐ |
| Rejected card — "Edit & Resubmit" | `onNavigateToListingEdit()` | ☐ |
| Blocked tab — admin message + "Contact Support" | `BlockedListingCard` | ☐ |
| Tap listing card → Listing Detail | `onNavigateToListingDetail()` | ☐ |
| My Requests screen loads user's requests | `GetMyRequestsUseCase` Flow | ☐ |
| Request slot banner shows X/Y | `SlotBanner(activeCount, maxRequests)` | ☐ |
| "New +" navigates to Request Form | `onNavigateToRequestForm()` | ☐ |
| Active tab — accent strip + Edit/Close buttons | `ActiveRequestCard` | ☐ |
| "Edit" → Request Edit Form | `onNavigateToRequestEdit()` | ☐ |
| "Close" → sets status=closed | `CloseRequestUseCase` | ☐ |
| Shimmer while loading | `DashboardShimmer()` | ☐ |
| Empty state per tab | `EmptyTabState(tab.name)` | ☐ |

---

## 23. CODE_REVIEW_MODULE_15.md Template

```markdown
# Code Review — Module 15: My Dashboard
**Branch:** feature/module-15-my-dashboard

## ✅ Architecture
- [ ] `getMyListings()` + `getMyRequests()` return `Flow` — real-time Firestore listeners
- [ ] `filteredListings` / `filteredRequests` are derived `StateFlow` via `combine`
- [ ] Tab filter is purely client-side — no extra Firestore queries per tab
- [ ] `unpublishListing` sets status to "draft" — does NOT delete
- [ ] `deleteListing` deletes Firestore doc — Storage cleanup delegated to Cloud Function

## ✅ Design Compliance
- [ ] Slot banner: `primaryTint` bg, `primaryRing` border, primary progress bar
- [ ] Published card: left image area 88dp, gradient, emoji watermark
- [ ] "● Live" badge: green dot 6dp + "Live" text in `primaryTint` pill
- [ ] Draft card: completion % badge + `LinearProgressIndicator`
- [ ] Pending card: `primaryTint` bg + ⏳ emoji + text
- [ ] Rejected card: `0x22E05555` bg, admin note in darker red box
- [ ] Active request card: left gradient accent strip 8dp

## ✅ Code Quality
- [ ] All strings in `strings.xml`
- [ ] `./gradlew test` → ✅
- [ ] `./gradlew assembleDebug` → ✅
```

*End of Module 15 — My Dashboard v1.0.0*
*All Android feature modules complete. Next: ANDROID_MASTER_PROGRESS.md update.*
