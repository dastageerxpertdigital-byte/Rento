# RentO — Android App
## Module 03 — Home & Discovery (Dual Marketplace)
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 03
> **Branch:** `feature/module-03-home`
> **Depends on:** Module 01 ✅, Module 02 ✅ (`feature/module-02-auth` merged to `main`)
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every pixel of this screen is derived from the prototype (`design_references.jsx`) and `REQUIREMENTS_SPECIFICATION_v2_3.md`. The dual marketplace (Looking/Hosting) is a **core feature** — never reduce to listings-only. Do not improvise any layout, colour, padding, animation, or interaction. If anything is ambiguous — stop and ask.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
   - [4.1 Domain Models](#41-domain-models)
   - [4.2 Repository Interfaces](#42-repository-interfaces)
   - [4.3 Firestore Schema & Pagination](#43-firestore-schema--pagination)
   - [4.4 `HomeViewModel` — Full Spec](#44-homeviewmodel--full-spec)
   - [4.5 Koin Module](#45-koin-module)
   - [4.6 Navigation Extensions](#46-navigation-extensions)
5. [Task M03-T01 — Domain Models](#task-m03-t01--domain-models)
6. [Task M03-T02 — Repository Interfaces](#task-m03-t02--repository-interfaces)
7. [Task M03-T03 — `ListingRepositoryImpl`](#task-m03-t03--listingrepositoryimpl)
8. [Task M03-T04 — `TenantRequestRepositoryImpl`](#task-m03-t04--tenantrequestrepositoryimpl)
9. [Task M03-T05 — `SliderRepositoryImpl`](#task-m03-t05--sliderrepositoryimpl)
10. [Task M03-T06 — `HomeViewModel`](#task-m03-t06--homeviewmodel)
11. [Task M03-T07 — Koin Module](#task-m03-t07--koin-module)
12. [Task M03-T08 — Nav Graph Extension](#task-m03-t08--nav-graph-extension)
13. [Task M03-T09 — Screen: `HomeScreen` Shell](#task-m03-t09--screen-homescreen-shell)
14. [Task M03-T10 — Header Section](#task-m03-t10--header-section)
15. [Task M03-T11 — Mode Toggle + View Switcher](#task-m03-t11--mode-toggle--view-switcher)
16. [Task M03-T12 — Search Bar](#task-m03-t12--search-bar)
17. [Task M03-T13 — Quick Filter Chips](#task-m03-t13--quick-filter-chips)
18. [Task M03-T14 — Banner Slider (Looking Mode)](#task-m03-t14--banner-slider-looking-mode)
19. [Task M03-T15 — Section Header + Feed](#task-m03-t15--section-header--feed)
20. [Task M03-T16 — Shimmer Loading States](#task-m03-t16--shimmer-loading-states)
21. [Task M03-T17 — Empty State](#task-m03-t17--empty-state)
22. [Task M03-T18 — Search Bottom Sheet](#task-m03-t18--search-bottom-sheet)
23. [Task M03-T19 — City Picker Bottom Sheet](#task-m03-t19--city-picker-bottom-sheet)
24. [Task M03-T20 — Add Overlay Sheet](#task-m03-t20--add-overlay-sheet)
25. [Task M03-T21 — Notification Badge Dot Logic](#task-m03-t21--notification-badge-dot-logic)
26. [Task M03-T22 — Pagination Logic](#task-m03-t22--pagination-logic)
27. [Task M03-T23 — String Resources](#task-m03-t23--string-resources)
28. [Task M03-T24 — Unit Tests](#task-m03-t24--unit-tests)
29. [Task M03-T25 — Build Gate](#task-m03-t25--build-gate)
30. [Journey Coverage Checklist](#30-journey-coverage-checklist)
31. [CODE_REVIEW_MODULE_03.md Template](#31-code_review_module_03md-template)

---

## 1. Module Overview

Module 03 implements the **heart of RentO** — the dual-mode home and discovery screen. It is the first screen users land on after authentication, and the most-used screen in the entire app.

**Two equal modes:**
- **Looking mode:** Property listings feed — users browse available spaces.
- **Hosting mode:** Tenant requests feed — hosts find people looking for space.

Both modes share the exact same screen shell and are visually parity-level — neither mode is an afterthought.

**Key features delivered in this module:**
- Full `HomeScreen` with every section from the prototype
- Dual-mode `TabPill` mode switcher
- List / Grid view switcher
- Tappable search bar → Smart Search bottom sheet (all filters)
- Quick filter chips (mode-specific)
- `HomeBannerSlider` (Looking mode only) — featured listings
- Firestore pagination with `startAfter` cursor (page size 10)
- Skeleton shimmer loading for all feed variants
- `EmptyState` for zero-result conditions
- City picker sheet
- `AddOverlaySheet` (FAB → choose Hosting or Looking form)
- `BottomNavBar` integration
- Notification unread dot (SharedPreferences-driven)

**Screens produced:** 1 screen (`HomeScreen`) + 3 bottom sheets (Search, CityPicker, AddOverlay)

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── Listing.kt
│   │   ├── TenantRequest.kt
│   │   ├── FeaturedSlider.kt
│   │   ├── ListingFilters.kt
│   │   ├── RequestFilters.kt
│   │   └── MarketplaceMode.kt
│   ├── repository/
│   │   ├── ListingRepository.kt
│   │   ├── TenantRequestRepository.kt
│   │   └── SliderRepository.kt
│   └── usecase/
│       └── home/
│           ├── GetListingsUseCase.kt
│           ├── GetRequestsUseCase.kt
│           └── GetActiveSlidersUseCase.kt
├── data/
│   ├── model/
│   │   ├── ListingDto.kt
│   │   ├── TenantRequestDto.kt
│   │   └── FeaturedSliderDto.kt
│   └── repository/
│       ├── ListingRepositoryImpl.kt
│       ├── TenantRequestRepositoryImpl.kt
│       └── SliderRepositoryImpl.kt
├── presentation/
│   ├── home/
│   │   ├── HomeViewModel.kt
│   │   ├── HomeScreen.kt
│   │   └── components/
│   │       ├── HomeHeader.kt
│   │       ├── ModeToggleRow.kt
│   │       ├── HomeSearchBar.kt
│   │       ├── QuickFilterChips.kt
│   │       ├── SectionHeader.kt
│   │       ├── FeedList.kt
│   │       ├── FeedGrid.kt
│   │       ├── ShimmerFeedList.kt
│   │       ├── ShimmerFeedGrid.kt
│   │       ├── SearchSheet.kt
│   │       ├── CityPickerSheet.kt
│   │       └── AddOverlaySheet.kt
│   └── navigation/
│       └── RentoNavGraph.kt    ← updated to wire home routes
└── di/
    └── HomeModule.kt

app/src/main/res/values/strings.xml   ← Module 03 strings appended

app/src/test/java/com/rento/app/
├── domain/usecase/home/
│   ├── GetListingsUseCaseTest.kt
│   └── GetRequestsUseCaseTest.kt
└── presentation/home/
    └── HomeViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M03-T01 | Domain models — Listing, TenantRequest, FeaturedSlider, filters, MarketplaceMode | `domain/model/` | ☐ |
| M03-T02 | Repository interfaces | `domain/repository/` | ☐ |
| M03-T03 | `ListingRepositoryImpl` — Firestore queries + pagination | `data/repository/` | ☐ |
| M03-T04 | `TenantRequestRepositoryImpl` — Firestore queries + pagination | `data/repository/` | ☐ |
| M03-T05 | `SliderRepositoryImpl` — active sliders query | `data/repository/` | ☐ |
| M03-T06 | `HomeViewModel` + use cases | `presentation/home/`, `domain/usecase/` | ☐ |
| M03-T07 | Koin DI module — `HomeModule.kt` | `di/HomeModule.kt` | ☐ |
| M03-T08 | Nav graph — wire home route, add listing/request detail placeholders | `RentoNavGraph.kt` | ☐ |
| M03-T09 | `HomeScreen` shell — `Scaffold`, `BottomNavBar`, `LazyColumn` root | `HomeScreen.kt` | ☐ |
| M03-T10 | Header section — location chip + notification bell | `HomeHeader.kt` | ☐ |
| M03-T11 | Mode toggle + view switcher row | `ModeToggleRow.kt` | ☐ |
| M03-T12 | Search bar (tappable, opens sheet) | `HomeSearchBar.kt` | ☐ |
| M03-T13 | Quick filter chips (scrollable, mode-aware) | `QuickFilterChips.kt` | ☐ |
| M03-T14 | Banner slider — `HomeBannerSlider` integration | `HomeScreen.kt` | ☐ |
| M03-T15 | Section header + feed (list + grid, both modes) | `FeedList.kt`, `FeedGrid.kt`, `SectionHeader.kt` | ☐ |
| M03-T16 | Shimmer loading states — list + grid variants | `ShimmerFeedList.kt`, `ShimmerFeedGrid.kt` | ☐ |
| M03-T17 | Empty state integration | `HomeScreen.kt` | ☐ |
| M03-T18 | Search bottom sheet — all filter sections, both modes | `SearchSheet.kt` | ☐ |
| M03-T19 | City picker bottom sheet | `CityPickerSheet.kt` | ☐ |
| M03-T20 | Add overlay sheet — FAB + two intent cards | `AddOverlaySheet.kt` | ☐ |
| M03-T21 | Notification badge dot — unread indicator logic | `HomeHeader.kt` | ☐ |
| M03-T22 | Pagination — scroll-to-end trigger, `loadNextPage()` | `HomeScreen.kt`, `HomeViewModel.kt` | ☐ |
| M03-T23 | String resources | `strings.xml` | ☐ |
| M03-T24 | Unit tests — `HomeViewModel` + use cases | `HomeViewModelTest.kt` | ☐ |
| M03-T25 | Build gate | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 Domain Models

All in `domain/model/`. Zero Android imports.

```kotlin
// MarketplaceMode.kt
enum class MarketplaceMode { LOOKING, HOSTING }

// ViewMode.kt
enum class ViewMode { LIST, GRID }

// ListingStatus.kt
enum class ListingStatus {
    DRAFT, PUBLISHED, PENDING_APPROVAL, REJECTED, UNPUBLISHED, BLOCKED
}

// RequestStatus.kt
enum class RequestStatus { ACTIVE, PENDING_APPROVAL, CLOSED, BLOCKED }

// Listing.kt
data class Listing(
    val id: String,
    val uid: String,
    val ownerName: String,
    val intent: String,          // "share" | "fullRent" | "hourly"
    val propertyType: String,
    val propertySubType: String?,
    val suitableFor: String,
    val duration: String,        // "daily" | "weekly" | "monthly" | "hourly"
    val availableImmediately: Boolean,
    val price: Int,              // PKR
    val isNegotiable: Boolean,
    val province: String,
    val city: String,
    val area: String,
    val fullAddress: String,
    val lat: Double,
    val lng: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val furnished: String,       // "furnished" | "semi" | "unfurnished"
    val floor: Int?,
    val amenities: List<String>,
    val imageUrls: List<String>,
    val description: String?,
    val status: ListingStatus,
    val hasActiveSlider: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

// TenantRequest.kt
data class TenantRequest(
    val id: String,
    val uid: String,
    val requesterName: String,
    val requesterPhotoUrl: String?,
    val intent: String,           // "share" | "fullRent"
    val propertyType: String,
    val budgetMax: Int,
    val province: String,
    val city: String,
    val preferredAreas: List<String>,
    val radiusKm: Int,
    val minBedrooms: Int,
    val minBathrooms: Int,
    val furnishingRequired: String,  // "furnished" | "semi" | "any"
    val moveInDate: String,          // "immediate" | "within_month" | "flexible" | ISO date
    val bio: String,
    val status: RequestStatus,
    val createdAt: Long,
)

// FeaturedSlider.kt
data class FeaturedSlider(
    val id: String,
    val listingId: String?,
    val imageUrl: String,
    val title: String,
    val subtitle: String?,
    val ctaLabel: String?,
    val position: Int,
)

// ListingFilters.kt
data class ListingFilters(
    val propertyTypes: List<String> = emptyList(),
    val suitableFor: List<String> = emptyList(),
    val furnished: List<String> = emptyList(),
    val durations: List<String> = emptyList(),
    val priceMin: Int = 0,
    val priceMax: Int = 500_000,
    val bedroomsMin: Int? = null,
    val quickFilter: String? = null,   // "share" | "fullRent" | "hourly" | "female_only" | "le_25k" etc.
)

// RequestFilters.kt
data class RequestFilters(
    val intents: List<String> = emptyList(),
    val suitableTenant: List<String> = emptyList(),
    val budgetMax: Int? = null,
    val moveIn: List<String> = emptyList(),
    val quickFilter: String? = null,   // "immediate" | "this_month" | "student" | "family" | "professional"
)
```

### 4.2 Repository Interfaces

```kotlin
// ListingRepository.kt
interface ListingRepository {
    suspend fun getListings(
        city: String,
        filters: ListingFilters,
        cursor: DocumentSnapshot?,
        pageSize: Int = 10,
    ): Result<Pair<List<Listing>, DocumentSnapshot?>>  // (items, nextCursor)

    suspend fun getListingById(listingId: String): Result<Listing>
    suspend fun getFeaturedListings(city: String): Result<List<Listing>>
}

// TenantRequestRepository.kt
interface TenantRequestRepository {
    suspend fun getRequests(
        city: String,
        filters: RequestFilters,
        cursor: DocumentSnapshot?,
        pageSize: Int = 10,
    ): Result<Pair<List<TenantRequest>, DocumentSnapshot?>>

    suspend fun getRequestById(requestId: String): Result<TenantRequest>
    suspend fun createRequest(request: TenantRequest): Result<String>
    suspend fun updateRequest(requestId: String, request: TenantRequest): Result<Unit>
    suspend fun deleteRequest(requestId: String): Result<Unit>
    suspend fun getUserRequests(uid: String): Result<List<TenantRequest>>
    suspend fun getActiveRequestsCount(uid: String): Result<Int>
}

// SliderRepository.kt
interface SliderRepository {
    suspend fun getActiveSliders(): Result<List<FeaturedSlider>>
}
```

> ⚠️ `DocumentSnapshot` is a Firestore type from `com.google.firebase.firestore.DocumentSnapshot`. The **repository interfaces** accept it as a cursor parameter. This is a pragmatic decision — abstracting the cursor further would add complexity without architectural benefit in this project. The domain `model` classes themselves remain Android-free; only the repository interfaces accept this Firestore type.

### 4.3 Firestore Schema & Pagination

**Compound indexes required (must be created in Firebase Console before launch):**

```
listings:
  (city ASC, status ASC, createdAt DESC)
  (city ASC, status ASC, propertyType ASC, createdAt DESC)
  (city ASC, status ASC, intent ASC, createdAt DESC)
  (city ASC, status ASC, furnished ASC, createdAt DESC)

tenantRequests:
  (city ASC, status ASC, createdAt DESC)
  (city ASC, status ASC, intent ASC, createdAt DESC)
  (city ASC, status ASC, moveInDate ASC, createdAt DESC)
```

Document indexes declared in `firestore.indexes.json` (generated — not hand-written).

**Pagination pattern:**

```kotlin
// First page:
firestore.collection("listings")
    .whereEqualTo("city", city)
    .whereEqualTo("status", "published")
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .limit(pageSize.toLong())
    .get().await()

// Subsequent pages:
firestore.collection("listings")
    ...
    .startAfter(lastDocument)
    .get().await()
```

Result tuple: `(List<Listing>, DocumentSnapshot?)` where the `DocumentSnapshot?` is the last document of the returned page (used as `startAfter` cursor on next call). If returned list size < pageSize, `hasMore = false`.

### 4.4 `HomeViewModel` — Full Spec

```kotlin
data class HomeUiState(
    val mode: MarketplaceMode = MarketplaceMode.LOOKING,
    val viewMode: ViewMode = ViewMode.LIST,

    // Listings feed (Looking mode)
    val listings: List<Listing> = emptyList(),
    val listingsLoading: Boolean = false,
    val listingsError: String? = null,
    val listingsHasMore: Boolean = true,
    val listingsCursor: DocumentSnapshot? = null,

    // Requests feed (Hosting mode)
    val requests: List<TenantRequest> = emptyList(),
    val requestsLoading: Boolean = false,
    val requestsError: String? = null,
    val requestsHasMore: Boolean = true,
    val requestsCursor: DocumentSnapshot? = null,

    // Sliders (Looking mode)
    val sliders: List<FeaturedSlider> = emptyList(),
    val slidersLoading: Boolean = false,

    // Location
    val selectedCity: String = "Karachi",
    val selectedProvince: String = "Sindh",

    // Filters
    val listingFilters: ListingFilters = ListingFilters(),
    val requestFilters: RequestFilters = RequestFilters(),

    // Active quick filter chips
    val activeListingQuickFilter: String? = null,
    val activeRequestQuickFilter: String? = null,

    // Notification dot
    val hasUnreadNotifications: Boolean = false,
)
```

**`HomeViewModel` functions:**

```kotlin
class HomeViewModel(
    private val getListings: GetListingsUseCase,
    private val getRequests: GetRequestsUseCase,
    private val getActiveSliders: GetActiveSlidersUseCase,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    fun setMode(mode: MarketplaceMode)
    fun setViewMode(viewMode: ViewMode)
    fun changeCity(province: String, city: String)

    fun loadFeed(reset: Boolean = false)           // loads listings or requests based on mode
    fun loadNextPage()                             // cursor-based next page
    fun refreshFeed()                              // pull-to-refresh: reset=true

    fun applyListingFilters(filters: ListingFilters)
    fun applyRequestFilters(filters: RequestFilters)
    fun setListingQuickFilter(filter: String?)
    fun setRequestQuickFilter(filter: String?)

    fun loadSliders()
    fun checkUnreadNotifications()                 // reads SharedPreferences
}
```

### 4.5 Koin Module

```kotlin
val homeModule = module {
    single<ListingRepository>       { ListingRepositoryImpl(get()) }          // Firestore
    single<TenantRequestRepository> { TenantRequestRepositoryImpl(get()) }
    single<SliderRepository>        { SliderRepositoryImpl(get()) }
    factory { GetListingsUseCase(get()) }
    factory { GetRequestsUseCase(get()) }
    factory { GetActiveSlidersUseCase(get()) }
    viewModel {
        HomeViewModel(
            get(), get(), get(),
            get()  // SharedPreferences — named("rento_prefs") binding
        )
    }
}
```

Add `single<SharedPreferences>(named("rento_prefs")) { context.getSharedPreferences("rento_prefs", Context.MODE_PRIVATE) }` to `firebaseModule` or a new `appModule`.

### 4.6 Navigation Extensions

New routes added to `RentoNavGraph.kt`:

```kotlin
object HomeRoutes {
    const val HOME           = "home"
    const val LISTING_DETAIL = "listing/{listingId}"
    const val REQUEST_DETAIL = "request/{requestId}"
    const val LISTING_FORM   = "listing/form"
    const val REQUEST_FORM   = "request/form"
    const val MAP            = "map"
    const val SAVED          = "saved"
    const val CHATS          = "chats"
    const val NOTIFICATIONS  = "notifications"
    const val PROFILE        = "profile"
}

fun NavHostController.toListingDetail(id: String) =
    navigate(HomeRoutes.LISTING_DETAIL.replace("{listingId}", id))
fun NavHostController.toRequestDetail(id: String) =
    navigate(HomeRoutes.REQUEST_DETAIL.replace("{requestId}", id))
```

---

## Task M03-T01 — Domain Models

### Sub-tasks
- [ ] **M03-T01-A** Create all domain models listed in section 4.1: `Listing.kt`, `TenantRequest.kt`, `FeaturedSlider.kt`, `ListingFilters.kt`, `RequestFilters.kt`, `MarketplaceMode.kt`, `ViewMode.kt`, `ListingStatus.kt`, `RequestStatus.kt`.
- [ ] **M03-T01-B** Create `ListingMapper.kt` and `TenantRequestMapper.kt` in `data/repository/` for DTO → domain conversions.
- [ ] **M03-T01-C** Create DTOs: `ListingDto.kt`, `TenantRequestDto.kt`, `FeaturedSliderDto.kt` in `data/model/` — matching exact Firestore schema from Section 23 of spec.
- [ ] **M03-T01-D** Verify: `grep -r "import android\.\|import com.google.firebase" app/src/main/java/com/rento/app/domain/` — empty output.

---

## Task M03-T02 — Repository Interfaces

### Sub-tasks
- [ ] **M03-T02-A** Create `ListingRepository.kt`, `TenantRequestRepository.kt`, `SliderRepository.kt` in `domain/repository/` exactly matching section 4.2.
- [ ] **M03-T02-B** Note: `DocumentSnapshot` import from `com.google.firebase.firestore.DocumentSnapshot` is permitted **only in repository interface files**, not in domain model files.

---

## Task M03-T03 — `ListingRepositoryImpl`

**File:** `data/repository/ListingRepositoryImpl.kt`

### Sub-tasks
- [ ] **M03-T03-A** Implement `getListings`:

```kotlin
override suspend fun getListings(
    city: String,
    filters: ListingFilters,
    cursor: DocumentSnapshot?,
    pageSize: Int,
): Result<Pair<List<Listing>, DocumentSnapshot?>> = runCatching {
    var query: Query = firestore.collection("listings")
        .whereEqualTo("city", city)
        .whereEqualTo("status", "published")

    // Apply filters
    if (filters.propertyTypes.isNotEmpty())
        query = query.whereIn("propertyType", filters.propertyTypes)
    if (filters.suitableFor.isNotEmpty())
        query = query.whereIn("suitableFor", filters.suitableFor)
    if (filters.durations.isNotEmpty())
        query = query.whereIn("duration", filters.durations)
    if (filters.furnished.isNotEmpty())
        query = query.whereIn("furnished", filters.furnished)
    if (filters.bedroomsMin != null)
        query = query.whereGreaterThanOrEqualTo("bedrooms", filters.bedroomsMin)

    // Quick filter shortcuts
    when (filters.quickFilter) {
        "share"       -> query = query.whereEqualTo("intent", "share")
        "fullRent"    -> query = query.whereEqualTo("intent", "fullRent")
        "hourly"      -> query = query.whereEqualTo("intent", "hourly")
        "female_only" -> query = query.whereEqualTo("suitableFor", "Female Only")
        "le_25k"      -> query = query.whereLessThanOrEqualTo("price", 25_000)
    }

    query = query.orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(pageSize.toLong())

    if (cursor != null) query = query.startAfter(cursor)

    val snapshot = query.get().await()
    val listings = snapshot.documents.map { it.toListing() }
    val nextCursor = if (snapshot.documents.size < pageSize) null
                     else snapshot.documents.lastOrNull()
    Pair(listings, nextCursor)
}
```

- [ ] **M03-T03-B** Implement `getListingById`:

```kotlin
override suspend fun getListingById(listingId: String): Result<Listing> = runCatching {
    firestore.collection("listings").document(listingId).get().await().toListing()
}
```

- [ ] **M03-T03-C** Implement `getFeaturedListings`:

```kotlin
override suspend fun getFeaturedListings(city: String): Result<List<Listing>> = runCatching {
    firestore.collection("listings")
        .whereEqualTo("city", city)
        .whereEqualTo("status", "published")
        .whereEqualTo("hasActiveSlider", true)
        .limit(5)
        .get().await()
        .documents.map { it.toListing() }
}
```

- [ ] **M03-T03-D** Create `DocumentSnapshot.toListing()` extension in `data/repository/ListingMapper.kt` mapping all fields with safe defaults:

```kotlin
fun DocumentSnapshot.toListing(): Listing = Listing(
    id            = id,
    uid           = getString("uid") ?: "",
    ownerName     = getString("ownerName") ?: "",
    intent        = getString("intent") ?: "fullRent",
    propertyType  = getString("propertyType") ?: "",
    propertySubType = getString("propertySubType"),
    suitableFor   = getString("suitableFor") ?: "All",
    duration      = getString("duration") ?: "monthly",
    availableImmediately = getBoolean("availableImmediately") ?: true,
    price         = getLong("price")?.toInt() ?: 0,
    isNegotiable  = getBoolean("isNegotiable") ?: false,
    province      = getString("province") ?: "",
    city          = getString("city") ?: "",
    area          = getString("area") ?: "",
    fullAddress   = getString("fullAddress") ?: "",
    lat           = getDouble("lat") ?: 0.0,
    lng           = getDouble("lng") ?: 0.0,
    bedrooms      = getLong("bedrooms")?.toInt() ?: 0,
    bathrooms     = getLong("bathrooms")?.toInt() ?: 0,
    furnished     = getString("furnished") ?: "unfurnished",
    floor         = getLong("floor")?.toInt(),
    amenities     = (get("amenities") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
    imageUrls     = (get("imageUrls") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
    description   = getString("description"),
    status        = ListingStatus.valueOf(
                        (getString("status") ?: "published").uppercase()
                        .replace(" ", "_")
                    ),
    hasActiveSlider = getBoolean("hasActiveSlider") ?: false,
    createdAt     = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
    updatedAt     = getTimestamp("updatedAt")?.toDate()?.time ?: 0L,
)
```

---

## Task M03-T04 — `TenantRequestRepositoryImpl`

**File:** `data/repository/TenantRequestRepositoryImpl.kt`

### Sub-tasks
- [ ] **M03-T04-A** Implement `getRequests` with same cursor-pagination pattern as `getListings`:

```kotlin
override suspend fun getRequests(
    city: String,
    filters: RequestFilters,
    cursor: DocumentSnapshot?,
    pageSize: Int,
): Result<Pair<List<TenantRequest>, DocumentSnapshot?>> = runCatching {
    var query: Query = firestore.collection("tenantRequests")
        .whereEqualTo("city", city)
        .whereEqualTo("status", "active")

    if (filters.intents.isNotEmpty())
        query = query.whereIn("intent", filters.intents)
    if (filters.budgetMax != null)
        query = query.whereLessThanOrEqualTo("budgetMax", filters.budgetMax)

    when (filters.quickFilter) {
        "immediate"    -> query = query.whereEqualTo("moveInDate", "immediate")
        "this_month"   -> query = query.whereEqualTo("moveInDate", "within_month")
        "student"      -> query = query.whereEqualTo("tenantType", "student")
        "family"       -> query = query.whereEqualTo("suitableTenant", "Family")
        "professional" -> query = query.whereEqualTo("tenantType", "professional")
    }

    query = query.orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(pageSize.toLong())

    if (cursor != null) query = query.startAfter(cursor)

    val snapshot = query.get().await()
    val requests = snapshot.documents.map { it.toTenantRequest() }
    val nextCursor = if (snapshot.documents.size < pageSize) null
                     else snapshot.documents.lastOrNull()
    Pair(requests, nextCursor)
}
```

- [ ] **M03-T04-B** Implement `getRequestById`, `createRequest`, `updateRequest`, `deleteRequest`, `getUserRequests`, `getActiveRequestsCount` — all per spec section 10.5.
- [ ] **M03-T04-C** Create `DocumentSnapshot.toTenantRequest()` mapper in `data/repository/TenantRequestMapper.kt`.

---

## Task M03-T05 — `SliderRepositoryImpl`

**File:** `data/repository/SliderRepositoryImpl.kt`

### Sub-tasks
- [ ] **M03-T05-A** Implement `getActiveSliders`:

```kotlin
override suspend fun getActiveSliders(): Result<List<FeaturedSlider>> = runCatching {
    firestore.collection("featuredSliders")
        .whereEqualTo("status", "approved")
        .orderBy("position", Query.Direction.ASCENDING)
        .get().await()
        .documents.map { doc ->
            FeaturedSlider(
                id        = doc.id,
                listingId = doc.getString("listingId"),
                imageUrl  = doc.getString("imageUrl") ?: "",
                title     = doc.getString("title") ?: "",
                subtitle  = doc.getString("subtitle"),
                ctaLabel  = doc.getString("ctaLabel"),
                position  = doc.getLong("position")?.toInt() ?: 0,
            )
        }
}
```

---

## Task M03-T06 — `HomeViewModel`

**File:** `presentation/home/HomeViewModel.kt`

### Sub-tasks
- [ ] **M03-T06-A** Create `GetListingsUseCase`:

```kotlin
class GetListingsUseCase(private val listingRepository: ListingRepository) {
    suspend operator fun invoke(
        city: String,
        filters: ListingFilters,
        cursor: DocumentSnapshot?,
    ): Result<Pair<List<Listing>, DocumentSnapshot?>> =
        listingRepository.getListings(city, filters, cursor)
}
```

- [ ] **M03-T06-B** Create `GetRequestsUseCase`:

```kotlin
class GetRequestsUseCase(private val requestRepository: TenantRequestRepository) {
    suspend operator fun invoke(
        city: String,
        filters: RequestFilters,
        cursor: DocumentSnapshot?,
    ): Result<Pair<List<TenantRequest>, DocumentSnapshot?>> =
        requestRepository.getRequests(city, filters, cursor)
}
```

- [ ] **M03-T06-C** Create `GetActiveSlidersUseCase`:

```kotlin
class GetActiveSlidersUseCase(private val sliderRepository: SliderRepository) {
    suspend operator fun invoke(): Result<List<FeaturedSlider>> =
        sliderRepository.getActiveSliders()
}
```

> Note: All three are true use cases — they are integration points in the architecture even though they don't contain complex logic. They isolate the presentation layer from repository types.

- [ ] **M03-T06-D** Implement full `HomeViewModel`:

```kotlin
class HomeViewModel(
    private val getListings: GetListingsUseCase,
    private val getRequests: GetRequestsUseCase,
    private val getActiveSliders: GetActiveSlidersUseCase,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var listingsFetchJob: Job? = null
    private var requestsFetchJob: Job? = null

    init {
        loadFeed(reset = true)
        loadSliders()
        checkUnreadNotifications()
    }

    fun setMode(mode: MarketplaceMode) {
        if (_uiState.value.mode == mode) return
        _uiState.update { it.copy(mode = mode) }
        loadFeed(reset = true)
    }

    fun setViewMode(viewMode: ViewMode) {
        _uiState.update { it.copy(viewMode = viewMode) }
    }

    fun changeCity(province: String, city: String) {
        _uiState.update { it.copy(selectedProvince = province, selectedCity = city) }
        loadFeed(reset = true)
        loadSliders()
    }

    fun loadFeed(reset: Boolean = false) {
        when (_uiState.value.mode) {
            MarketplaceMode.LOOKING  -> loadListings(reset)
            MarketplaceMode.HOSTING  -> loadRequests(reset)
        }
    }

    fun loadNextPage() {
        when (_uiState.value.mode) {
            MarketplaceMode.LOOKING  -> {
                if (!_uiState.value.listingsHasMore || _uiState.value.listingsLoading) return
                loadListings(reset = false)
            }
            MarketplaceMode.HOSTING  -> {
                if (!_uiState.value.requestsHasMore || _uiState.value.requestsLoading) return
                loadRequests(reset = false)
            }
        }
    }

    fun refreshFeed() = loadFeed(reset = true)

    private fun loadListings(reset: Boolean) {
        listingsFetchJob?.cancel()
        if (reset) _uiState.update { it.copy(listings = emptyList(), listingsCursor = null, listingsHasMore = true) }
        _uiState.update { it.copy(listingsLoading = true, listingsError = null) }
        listingsFetchJob = viewModelScope.launch {
            val cursor = if (reset) null else _uiState.value.listingsCursor
            val filters = _uiState.value.listingFilters
                .copy(quickFilter = _uiState.value.activeListingQuickFilter)
            getListings(_uiState.value.selectedCity, filters, cursor).fold(
                onSuccess = { (newItems, nextCursor) ->
                    _uiState.update { state ->
                        val merged = if (reset) newItems else state.listings + newItems
                        state.copy(
                            listings        = merged,
                            listingsCursor  = nextCursor,
                            listingsHasMore = nextCursor != null,
                            listingsLoading = false,
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(listingsLoading = false, listingsError = e.message) }
                },
            )
        }
    }

    private fun loadRequests(reset: Boolean) {
        requestsFetchJob?.cancel()
        if (reset) _uiState.update { it.copy(requests = emptyList(), requestsCursor = null, requestsHasMore = true) }
        _uiState.update { it.copy(requestsLoading = true, requestsError = null) }
        requestsFetchJob = viewModelScope.launch {
            val cursor = if (reset) null else _uiState.value.requestsCursor
            val filters = _uiState.value.requestFilters
                .copy(quickFilter = _uiState.value.activeRequestQuickFilter)
            getRequests(_uiState.value.selectedCity, filters, cursor).fold(
                onSuccess = { (newItems, nextCursor) ->
                    _uiState.update { state ->
                        val merged = if (reset) newItems else state.requests + newItems
                        state.copy(
                            requests        = merged,
                            requestsCursor  = nextCursor,
                            requestsHasMore = nextCursor != null,
                            requestsLoading = false,
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(requestsLoading = false, requestsError = e.message) }
                },
            )
        }
    }

    fun applyListingFilters(filters: ListingFilters) {
        _uiState.update { it.copy(listingFilters = filters) }
        loadListings(reset = true)
    }

    fun applyRequestFilters(filters: RequestFilters) {
        _uiState.update { it.copy(requestFilters = filters) }
        loadRequests(reset = true)
    }

    fun setListingQuickFilter(filter: String?) {
        _uiState.update { it.copy(activeListingQuickFilter = filter) }
        loadListings(reset = true)
    }

    fun setRequestQuickFilter(filter: String?) {
        _uiState.update { it.copy(activeRequestQuickFilter = filter) }
        loadRequests(reset = true)
    }

    fun loadSliders() {
        viewModelScope.launch {
            _uiState.update { it.copy(slidersLoading = true) }
            getActiveSliders().fold(
                onSuccess = { sliders -> _uiState.update { it.copy(sliders = sliders, slidersLoading = false) } },
                onFailure = { _uiState.update { it.copy(slidersLoading = false) } },
            )
        }
    }

    fun checkUnreadNotifications() {
        val has = sharedPreferences.getBoolean("hasUnreadNotifications", false)
        _uiState.update { it.copy(hasUnreadNotifications = has) }
    }

    fun clearUnreadDot() {
        sharedPreferences.edit().putBoolean("hasUnreadNotifications", false).apply()
        _uiState.update { it.copy(hasUnreadNotifications = false) }
    }
}
```

---

## Task M03-T07 — Koin Module

### Sub-tasks
- [ ] **M03-T07-A** Create `di/HomeModule.kt` with all bindings from section 4.5.
- [ ] **M03-T07-B** Add `homeModule` to `RentoApplication.startKoin` modules list.
- [ ] **M03-T07-C** Add `SharedPreferences` named binding to `appModule`.

---

## Task M03-T08 — Nav Graph Extension

### Sub-tasks
- [ ] **M03-T08-A** Replace `HomeScreen` placeholder in `RentoNavGraph.kt` with the real `HomeScreen` composable call.
- [ ] **M03-T08-B** Add `composable(HomeRoutes.LISTING_DETAIL)` and `composable(HomeRoutes.REQUEST_DETAIL)` as `EmptyState` placeholder screens (full implementation in Modules 04 and 05).
- [ ] **M03-T08-C** Add `composable(HomeRoutes.LISTING_FORM)`, `composable(HomeRoutes.REQUEST_FORM)`, `composable(HomeRoutes.MAP)`, `composable(HomeRoutes.SAVED)`, `composable(HomeRoutes.CHATS)`, `composable(HomeRoutes.NOTIFICATIONS)`, `composable(HomeRoutes.PROFILE)` as placeholder composables.
- [ ] **M03-T08-D** Wire navigation callbacks in `HomeScreen` composable:

```kotlin
composable(HomeRoutes.HOME) {
    HomeScreen(
        onListingTap       = { id -> navController.toListingDetail(id) },
        onRequestTap       = { id -> navController.toRequestDetail(id) },
        onAddListingTap    = { navController.navigate(HomeRoutes.LISTING_FORM) },
        onAddRequestTap    = { navController.navigate(HomeRoutes.REQUEST_FORM) },
        onMapTap           = { navController.navigate(HomeRoutes.MAP) },
        onSavedTap         = { navController.navigate(HomeRoutes.SAVED) },
        onChatsTap         = { navController.navigate(HomeRoutes.CHATS) },
        onNotificationsTap = { navController.navigate(HomeRoutes.NOTIFICATIONS) },
        onProfileTap       = { navController.navigate(HomeRoutes.PROFILE) },
    )
}
```

---

## Task M03-T09 — Screen: `HomeScreen` Shell

**File:** `presentation/home/HomeScreen.kt`

The `HomeScreen` uses a `Scaffold` with the `BottomNavBar` in the `bottomBar` slot. The main content is a `LazyColumn` that contains all sections as items.

### Exact Screen Architecture

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onListingTap: (String) -> Unit,
    onRequestTap: (String) -> Unit,
    onAddListingTap: () -> Unit,
    onAddRequestTap: () -> Unit,
    onMapTap: () -> Unit,
    onSavedTap: () -> Unit,
    onChatsTap: () -> Unit,
    onNotificationsTap: () -> Unit,
    onProfileTap: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearchSheet    by remember { mutableStateOf(false) }
    var showCitySheet      by remember { mutableStateOf(false) }
    var showAddOverlay     by remember { mutableStateOf(false) }
    val listState          = rememberLazyListState()
    val gridState          = rememberLazyGridState()

    // Pull-to-refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = when (uiState.mode) {
            MarketplaceMode.LOOKING -> uiState.listingsLoading && uiState.listings.isEmpty()
            MarketplaceMode.HOSTING -> uiState.requestsLoading && uiState.requests.isEmpty()
        },
        onRefresh  = { viewModel.refreshFeed() },
    )

    Scaffold(
        bottomBar = {
            BottomNavBar(
                activeRoute    = HomeRoutes.HOME,
                onHomeTap      = { /* already here */ },
                onMapTap       = onMapTap,
                onFabTap       = { showAddOverlay = true },
                onSavedTap     = onSavedTap,
                onProfileTap   = onProfileTap,
            )
        },
        containerColor = MaterialTheme.rentoColors.bg0,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState),
        ) {
            // ── Main scrollable content ──────────────────────────────────
            LazyColumn(
                state           = listState,
                modifier        = Modifier.fillMaxSize(),
                contentPadding  = PaddingValues(bottom = 16.dp),
            ) {
                item(key = "status_bar") { StatusBar() }

                item(key = "header") {
                    HomeHeader(
                        city                 = uiState.selectedCity,
                        province             = uiState.selectedProvince,
                        mode                 = uiState.mode,
                        hasUnread            = uiState.hasUnreadNotifications,
                        onCityTap            = { showCitySheet = true },
                        onNotificationsTap   = {
                            viewModel.clearUnreadDot()
                            onNotificationsTap()
                        },
                    )
                }

                item(key = "mode_toggle") {
                    ModeToggleRow(
                        mode         = uiState.mode,
                        viewMode     = uiState.viewMode,
                        onModeChange = { viewModel.setMode(it) },
                        onViewChange = { viewModel.setViewMode(it) },
                    )
                }

                item(key = "search_bar") {
                    HomeSearchBar(
                        mode       = uiState.mode,
                        onTap      = { showSearchSheet = true },
                    )
                }

                item(key = "quick_filters") {
                    QuickFilterChips(
                        mode          = uiState.mode,
                        activeFilter  = when (uiState.mode) {
                            MarketplaceMode.LOOKING  -> uiState.activeListingQuickFilter
                            MarketplaceMode.HOSTING  -> uiState.activeRequestQuickFilter
                        },
                        onFilterSelect = when (uiState.mode) {
                            MarketplaceMode.LOOKING  -> { f -> viewModel.setListingQuickFilter(f) }
                            MarketplaceMode.HOSTING  -> { f -> viewModel.setRequestQuickFilter(f) }
                        },
                    )
                }

                // Banner slider — Looking mode only
                if (uiState.mode == MarketplaceMode.LOOKING) {
                    item(key = "banner_slider") {
                        HomeBannerSlider(
                            sliders           = uiState.sliders,
                            isLoading         = uiState.slidersLoading,
                            onSlideTap        = { linkedListingId ->
                                if (linkedListingId != null) onListingTap(linkedListingId)
                            },
                        )
                    }
                }

                item(key = "section_header") {
                    SectionHeader(
                        mode     = uiState.mode,
                        onSeeAll = { /* See all — same as removing city filter; Module 11 */ },
                    )
                }

                // ─ Feed content ─────────────────────────────────────────
                val isLoading = when (uiState.mode) {
                    MarketplaceMode.LOOKING  -> uiState.listingsLoading && uiState.listings.isEmpty()
                    MarketplaceMode.HOSTING  -> uiState.requestsLoading && uiState.requests.isEmpty()
                }
                val isEmpty = when (uiState.mode) {
                    MarketplaceMode.LOOKING  -> !isLoading && uiState.listings.isEmpty()
                    MarketplaceMode.HOSTING  -> !isLoading && uiState.requests.isEmpty()
                }

                when {
                    isLoading -> {
                        // Shimmer skeleton items
                        if (uiState.viewMode == ViewMode.LIST) {
                            items(5, key = { "shimmer_$it" }) {
                                ShimmerPropertyCardFull(modifier = Modifier.padding(horizontal = 20.dp))
                                Spacer(16.dp)
                            }
                        } else {
                            item(key = "shimmer_grid") {
                                ShimmerFeedGrid(modifier = Modifier.padding(horizontal = 20.dp))
                            }
                        }
                    }
                    isEmpty -> {
                        item(key = "empty_state") {
                            HomeEmptyState(mode = uiState.mode)
                        }
                    }
                    else -> {
                        // ── LOOKING + LIST ──
                        if (uiState.mode == MarketplaceMode.LOOKING && uiState.viewMode == ViewMode.LIST) {
                            items(uiState.listings, key = { it.id }) { listing ->
                                PropertyCard(
                                    listing   = listing,
                                    variant   = PropertyCardVariant.FULL,
                                    onTap     = { onListingTap(listing.id) },
                                    modifier  = Modifier.padding(horizontal = 20.dp),
                                )
                                Spacer(16.dp)
                            }
                        }
                        // ── LOOKING + GRID ── (2-column grid embedded in LazyColumn via sub-grid)
                        if (uiState.mode == MarketplaceMode.LOOKING && uiState.viewMode == ViewMode.GRID) {
                            item(key = "listings_grid") {
                                FeedGrid(
                                    listings  = uiState.listings,
                                    onTap     = { onListingTap(it) },
                                    modifier  = Modifier.padding(horizontal = 20.dp),
                                )
                            }
                        }
                        // ── HOSTING + LIST ──
                        if (uiState.mode == MarketplaceMode.HOSTING && uiState.viewMode == ViewMode.LIST) {
                            items(uiState.requests, key = { it.id }) { request ->
                                TenantRequestCard(
                                    request  = request,
                                    variant  = TenantRequestCardVariant.FULL,
                                    onTap    = { onRequestTap(request.id) },
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                )
                                Spacer(16.dp)
                            }
                        }
                        // ── HOSTING + GRID ──
                        if (uiState.mode == MarketplaceMode.HOSTING && uiState.viewMode == ViewMode.GRID) {
                            item(key = "requests_grid") {
                                RequestFeedGrid(
                                    requests = uiState.requests,
                                    onTap    = { onRequestTap(it) },
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                )
                            }
                        }

                        // ── Pagination loader ──
                        val isLoadingMore = when (uiState.mode) {
                            MarketplaceMode.LOOKING -> uiState.listingsLoading && uiState.listings.isNotEmpty()
                            MarketplaceMode.HOSTING -> uiState.requestsLoading && uiState.requests.isNotEmpty()
                        }
                        if (isLoadingMore) {
                            item(key = "load_more_spinner") {
                                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    RentoDeleteSpinner(size = 28.dp)
                                }
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = pullRefreshState.refreshing,
                state      = pullRefreshState,
                backgroundColor = MaterialTheme.rentoColors.bg2,
                contentColor    = MaterialTheme.rentoColors.primary,
                modifier        = Modifier.align(Alignment.TopCenter),
            )
        }
    }

    // ── Scroll-to-end pagination trigger ────────────────────────────────
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = layoutInfo.totalItemsCount
            lastVisible >= total - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    // ── Bottom sheets ────────────────────────────────────────────────────
    if (showSearchSheet) {
        SearchSheet(
            mode            = uiState.mode,
            listingFilters  = uiState.listingFilters,
            requestFilters  = uiState.requestFilters,
            onApplyListingFilters  = { filters ->
                viewModel.applyListingFilters(filters)
                showSearchSheet = false
            },
            onApplyRequestFilters  = { filters ->
                viewModel.applyRequestFilters(filters)
                showSearchSheet = false
            },
            onDismiss       = { showSearchSheet = false },
        )
    }

    if (showCitySheet) {
        CityPickerSheet(
            currentProvince = uiState.selectedProvince,
            currentCity     = uiState.selectedCity,
            onSelect        = { province, city ->
                viewModel.changeCity(province, city)
                showCitySheet = false
            },
            onDismiss       = { showCitySheet = false },
        )
    }

    if (showAddOverlay) {
        AddOverlaySheet(
            onLookingTap = { showAddOverlay = false; onAddRequestTap() },
            onHostingTap = { showAddOverlay = false; onAddListingTap() },
            onDismiss    = { showAddOverlay = false },
        )
    }
}
```

### Sub-tasks
- [ ] **M03-T09-A** Implement `HomeScreen` exactly as above.
- [ ] **M03-T09-B** Note: `FeedGrid` and `RequestFeedGrid` within `LazyColumn` use fixed-height `Box` wrappers with intrinsic size to avoid `LazyVerticalGrid`-inside-`LazyColumn` layout conflicts. The grid items are rendered as a single non-lazy `FlowRow`-style layout for performance correctness:

```kotlin
@Composable
fun FeedGrid(
    listings: List<Listing>,
    onTap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use 2-column fixed grid via chunked rows (not LazyVerticalGrid inside LazyColumn)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listings.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { listing ->
                    PropertyCard(
                        listing = listing,
                        variant = PropertyCardVariant.COMPACT,
                        onTap   = { onTap(listing.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Fill empty slot if row has only 1 item
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
```

---

## Task M03-T10 — Header Section

**File:** `presentation/home/components/HomeHeader.kt`

### Exact Specification
```
Column(padding horizontal=20dp, top=16dp):
  Row(fillMaxWidth, SpaceBetween, CenterVertically):

    LEFT — Location chip:
      Row(CenterVertically, 8dp gap):
        RentoIcons.Pin, 15dp, RentoColors.primary
        Text: "{city}, {province}" — 13sp, Medium, RentoColors.t1
        RentoIcons.Chevron, 14dp, RentoColors.t2
      Entire row is clickable → onCityTap
      Press: no visual change needed (subtle alpha 0.7)

    RIGHT — Notification bell:
      Box(46dp × 46dp, clickable → onNotificationsTap):
        Background: RentoColors.bg2
        Border: 1.5dp RentoColors.border2
        Shape: RoundedCornerShape(16dp)
        Icon: RentoIcons.Bell, 22dp, RentoColors.t0, centred

        IF hasUnread:
          Notification dot (.ndot):
            Positioned: Modifier.align(Alignment.TopEnd).offset(x=(-8).dp, y=8.dp)
            9dp × 9dp circle, RentoColors.red fill
            2.5dp ring: drawn via Modifier.drawBehind or Box with border
              Ring colour: RentoColors.bg1 (dark bg)
            Pulsing animation:
              infiniteTransition.animateFloat(
                initialValue = 0.85f, targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                  tween(700, easing = FastOutSlowInEasing),
                  RepeatMode.Reverse
                )
              )
              Apply as Modifier.scale(pulseScale)

  Spacer(16dp)

  Display heading:
    Looking mode:
      Row (inline):
        "Find Your " — Fraunces 30sp SemiBold RentoColors.t0
        GradientText("Next Home", style = displayM override size 30sp)
    Hosting mode:
      Row (inline):
        "Find Your " — Fraunces 30sp SemiBold RentoColors.t0
        GradientText("Tenant", style = same)
```

### Sub-tasks
- [ ] **M03-T10-A** Implement `HomeHeader` composable.
- [ ] **M03-T10-B** Pulsing dot: `rememberInfiniteTransition()` with `animateFloat` between 0.85–1.0 scale.
- [ ] **M03-T10-C** Gradient heading uses `GradientText` from Module 01.
- [ ] **M03-T10-D** `@Preview` with unread dot + without, both modes, dark + light.

---

## Task M03-T11 — Mode Toggle + View Switcher

**File:** `presentation/home/components/ModeToggleRow.kt`

### Exact Specification
```
Row(fillMaxWidth, SpaceBetween, CenterVertically, padding horizontal=20dp, top=16dp):

  LEFT — TabPill:
    TabPill(
      tabs    = ["Looking", "Hosting"],
      selected = if (mode == LOOKING) 0 else 1,
      onSelect = { idx -> onModeChange(if (idx == 0) LOOKING else HOSTING) }
    )
    (TabPill is from Module 01 design system)

  RIGHT — View mode switcher pill:
    Row:
      Background: RentoColors.bg2
      Border: 1.5dp RentoColors.border
      Shape: RoundedCornerShape(14dp)
      Padding: 3dp all sides

      Two icon buttons side by side:
        Each: Modifier.clip(RoundedCornerShape(11dp)), padding 7dp V / 11dp H
          ACTIVE state: background RentoColors.bg4, RentoColors.t0
          INACTIVE state: transparent, RentoColors.t2
          animateColorAsState(220ms) on both background and icon tint

        Button 1 (LIST):
          Icon: RentoIcons.List, 18dp
          onClick = { onViewChange(ViewMode.LIST) }
          contentDescription = "List view"

        Button 2 (GRID):
          Icon: RentoIcons.Grid, 18dp
          onClick = { onViewChange(ViewMode.GRID) }
          contentDescription = "Grid view"
```

### Sub-tasks
- [ ] **M03-T11-A** Implement `ModeToggleRow`.
- [ ] **M03-T11-B** `animateColorAsState` on each view mode button bg + icon tint.
- [ ] **M03-T11-C** `@Preview` all four state combinations (LOOKING+LIST, LOOKING+GRID, HOSTING+LIST, HOSTING+GRID), dark + light.

---

## Task M03-T12 — Search Bar

**File:** `presentation/home/components/HomeSearchBar.kt`

### Exact Specification
```
Row(
  modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 20.dp, vertical = 0.dp)
    .padding(top = 18.dp)
    .background(RentoColors.bg2, RoundedCornerShape(18.dp))
    .border(1.5.dp, RentoColors.border2, RoundedCornerShape(18.dp))
    .padding(vertical = 14.dp, horizontal = 16.dp)
    .clickable(indication = null, interactionSource = ...) { onTap() },
  verticalAlignment = CenterVertically,
):

  RentoIcons.Search, 18dp, RentoColors.t2

  Spacer(10dp)

  Text(
    text = if (mode == LOOKING) "Area, property type, budget…"
           else "Search tenant requests...",
    style = bodyM,  // 14sp
    color = RentoColors.t3,
    modifier = Modifier.weight(1f),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
  )

  Spacer(8dp)

  // Filter pill
  Box(
    modifier = Modifier
      .background(RentoColors.primaryTint, RoundedCornerShape(11.dp))
      .border(1.dp, RentoColors.primaryRing, RoundedCornerShape(11.dp))
      .padding(vertical = 6.dp, horizontal = 11.dp),
  ) {
    Row(CenterVertically, 5dp gap):
      RentoIcons.Filter, 14dp, RentoColors.primary
      Text("Filter", style = label, color = RentoColors.primary, fontWeight = Bold)
  }
```

### Sub-tasks
- [ ] **M03-T12-A** Implement `HomeSearchBar`. It is purely a visual element — it does not contain a real `TextField`. `onTap` fires when anywhere in the bar is tapped.
- [ ] **M03-T12-B** `@Preview` both modes, dark + light.

---

## Task M03-T13 — Quick Filter Chips

**File:** `presentation/home/components/QuickFilterChips.kt`

### Exact Specification
```
LazyRow(
  modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
  contentPadding = PaddingValues(horizontal = 20.dp),
  horizontalArrangement = Arrangement.spacedBy(8.dp),
) {
  items(chips) { chip ->
    RentoChip(
      label    = chip.label,
      selected = chip.filterId == activeFilter,
      onClick  = {
        // Toggle: tapping active chip deselects (null); tapping new chip selects
        onFilterSelect(if (chip.filterId == activeFilter) null else chip.filterId)
      },
    )
  }
}
```

### Looking Mode Chips
| Label | filterId |
|-------|---------|
| All | `null` |
| Share | `"share"` |
| Full Rent | `"fullRent"` |
| PG | `"pg"` |
| Hourly | `"hourly"` |
| Female Only | `"female_only"` |
| ≤ 25k | `"le_25k"` |

### Hosting Mode Chips
| Label | filterId |
|-------|---------|
| All | `null` |
| Immediate | `"immediate"` |
| This Month | `"this_month"` |
| Student | `"student"` |
| Family | `"family"` |
| Professional | `"professional"` |

### Sub-tasks
- [ ] **M03-T13-A** Define chip data class:

```kotlin
private data class QuickFilterChip(val label: String, val filterId: String?)
```

- [ ] **M03-T13-B** Define chip lists as `remember`-stable values, not recreated on every recomposition.
- [ ] **M03-T13-C** Implement `QuickFilterChips` composable.
- [ ] **M03-T13-D** `@Preview` both modes, all states, dark + light.

---

## Task M03-T14 — Banner Slider (Looking Mode)

**File:** `HomeScreen.kt` (integrates `HomeBannerSlider` from Module 01)

### Exact Specification
```
Looking mode only. Rendered BELOW quick filter chips.
Padding: 24dp top, 0dp horizontal (full-bleed within 20dp screen margin — slider bleeds to edges)

HomeBannerSlider(
  sliders   = uiState.sliders,
  isLoading = uiState.slidersLoading,
  onSlideTap = { linkedListingId ->
    if (linkedListingId != null) onListingTap(linkedListingId)
    // null = no linked listing — no navigation
  },
)
```

`HomeBannerSlider` was already specified and implemented in Module 01. This task is the **integration** only — wire the live `sliders` state from `HomeViewModel` to the component.

### Sub-tasks
- [ ] **M03-T14-A** Wire `HomeBannerSlider` in `HomeScreen` with live data from `uiState.sliders` and `uiState.slidersLoading`.
- [ ] **M03-T14-B** When `sliders.isEmpty()` and `!slidersLoading`: do NOT render the slider section at all — the `item` should be skipped entirely (use `AnimatedVisibility` or a conditional `item` block).
- [ ] **M03-T14-C** When `slidersLoading && sliders.isEmpty()`: render a shimmer placeholder same dimensions as the slider (160dp height, full width, `shimmerModifier`).

---

## Task M03-T15 — Section Header + Feed

**File:** `presentation/home/components/SectionHeader.kt`, `FeedList.kt`, `FeedGrid.kt`

### `SectionHeader` Exact Specification
```
Row(
  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
  horizontalArrangement = Arrangement.SpaceBetween,
  verticalAlignment     = CenterVertically,
) {
  Text(
    text  = if (mode == LOOKING) "Nearby Spaces" else "Recent Requests",
    style = displayS,   // Fraunces 22sp SemiBold
    color = RentoColors.t0,
  )
  Text(
    text     = "See all",
    style    = bodyS,   // 13sp Bold
    color    = RentoColors.primary,
    modifier = Modifier.clickable { onSeeAll() },
  )
}
```

### `FeedList` — Not a separate composable

Feed items are rendered directly as `items(...)` in the `LazyColumn` in `HomeScreen` (see M03-T09). This is the correct pattern — wrapping in a nested `LazyColumn` inside a `LazyColumn` would break layout.

### `FeedGrid`

Implemented as a non-lazy chunked Column (see M03-T09-B). Two variants:
- `FeedGrid` for `PropertyCard` compact
- `RequestFeedGrid` for `TenantRequestCard` compact

```kotlin
@Composable
fun RequestFeedGrid(
    requests: List<TenantRequest>,
    onTap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        requests.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { req ->
                    TenantRequestCard(
                        request  = req,
                        variant  = TenantRequestCardVariant.COMPACT,
                        onTap    = { onTap(req.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
```

### Sub-tasks
- [ ] **M03-T15-A** Implement `SectionHeader`.
- [ ] **M03-T15-B** Implement `FeedGrid` and `RequestFeedGrid`.
- [ ] **M03-T15-C** `@Preview` both modes + both view variants, dark + light.

---

## Task M03-T16 — Shimmer Loading States

**File:** `presentation/home/components/ShimmerFeedList.kt`, `ShimmerFeedGrid.kt`

### `ShimmerPropertyCardFull`
Skeleton that matches `PropertyCard` full variant dimensions:

```
Box(fillMaxWidth, 270dp height, 24dp corner, shimmerModifier)
```

> `shimmerModifier` = `Modifier.shimmer()` from Module 01 `Animations.kt` (`rememberShimmerEffect()`)

### `ShimmerPropertyCardCompact`
Skeleton for grid variant:

```
Box(fillMaxWidth, 200dp height, 24dp corner, shimmerModifier)
```

### `ShimmerTenantRequestCardFull`
Skeleton for TenantRequestCard full:

```
Box(fillMaxWidth, 140dp height, 24dp corner, shimmerModifier)
```

### `ShimmerTenantRequestCardCompact`
```
Box(fillMaxWidth, 130dp height, 20dp corner, shimmerModifier)
```

### `ShimmerFeedGrid`
```
Column(20dp gap):
  Row(12dp gap):
    ShimmerPropertyCardCompact(Modifier.weight(1f))
    ShimmerPropertyCardCompact(Modifier.weight(1f))
  Row(12dp gap):
    ShimmerPropertyCardCompact(Modifier.weight(1f))
    ShimmerPropertyCardCompact(Modifier.weight(1f))
```

### Sub-tasks
- [ ] **M03-T16-A** Implement all shimmer skeleton composables.
- [ ] **M03-T16-B** `shimmerModifier` is the `shimmer()` animation from Module 01 — NOT a custom implementation. Import and reuse it.
- [ ] **M03-T16-C** `@Preview` all variants dark.

---

## Task M03-T17 — Empty State

**File:** `HomeScreen.kt` (inline composable)

### `HomeEmptyState` Exact Specification
```
Column(
  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 60.dp),
  horizontalAlignment = CenterHorizontally,
):
  EmptyState(
    icon     = if (mode == LOOKING) RentoIcons.Home else RentoIcons.Users,
    title    = if (mode == LOOKING) "No spaces found"
               else "No requests found",
    subtitle = if (mode == LOOKING)
                   "No listings match your search in {city}. Try adjusting your filters."
               else
                   "No tenant requests in {city} yet. Be the first to post!",
    cta      = if (mode == LOOKING) null
               else "Post a Request",
    onCta    = { onAddRequestTap() },
  )
```

> `EmptyState` composable is from Module 01. Reuse it — do NOT create a new one.

### Sub-tasks
- [ ] **M03-T17-A** Implement `HomeEmptyState` using Module 01 `EmptyState` component.
- [ ] **M03-T17-B** `@Preview` both modes dark + light.

---

## Task M03-T18 — Search Bottom Sheet

**File:** `presentation/home/components/SearchSheet.kt`

### Shell
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    mode: MarketplaceMode,
    listingFilters: ListingFilters,
    requestFilters: RequestFilters,
    onApplyListingFilters: (ListingFilters) -> Unit,
    onApplyRequestFilters: (RequestFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = MaterialTheme.rentoColors.bg1,
        shape             = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle        = {
            Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(36.dp).height(4.dp)
                    .background(MaterialTheme.rentoColors.bg4, RoundedCornerShape(2.dp)))
            }
        },
    ) {
        // Drag handle padding: 20dp top, header title, then mode-specific sections
    }
}
```

### Header
```
Text("Smart Search") — Fraunces 24sp SemiBold, RentoColors.t0, padding horizontal=20dp, top=20dp, bottom=20dp
```

### Looking Mode Sections (all inside `Column(verticalScroll)`)

```
─── PROPERTY TYPE ────────────────────────────────────────────────────────────
SectionLabel("PROPERTY TYPE", padding horizontal=20dp)
Spacer(10dp)
FlowRow(horizontalGap=8dp, verticalGap=8dp, padding horizontal=20dp):
  RentoChip each: "Apartment" "House" "Duplex" "Portion" "Room" "Studio"
                  "Hostel Bed" "Penthouse" "Farm House" "Office Space"
                  "Guest Room" "Coworking Space"
  selected = chip in localFilters.propertyTypes
  onClick = { toggle chip in local state }

Spacer(16dp)

─── SUITABLE FOR ─────────────────────────────────────────────────────────────
SectionLabel("SUITABLE FOR", padding horizontal=20dp)
Spacer(10dp)
FlowRow(padding horizontal=20dp, gaps same):
  "Male Only" "Female Only" "Family" "All"
  selected = chip in localFilters.suitableFor

Spacer(16dp)

─── FURNISHED ────────────────────────────────────────────────────────────────
SectionLabel("FURNISHED", padding horizontal=20dp)
Spacer(10dp)
FlowRow(padding horizontal=20dp):
  "Furnished" "Semi-Furnished" "Unfurnished"

Spacer(16dp)

─── DURATION ─────────────────────────────────────────────────────────────────
SectionLabel("DURATION", padding horizontal=20dp)
Spacer(10dp)
FlowRow(padding horizontal=20dp):
  "Daily" "Weekly" "Monthly"

Spacer(16dp)

─── PRICE RANGE ──────────────────────────────────────────────────────────────
SectionLabel("PRICE RANGE", padding horizontal=20dp)
Spacer(6dp)
Text("PKR ${formatPrice(priceMin)} – PKR ${formatPrice(priceMax)}", 13sp, RentoColors.primary, Bold,
  padding horizontal=20dp)
Spacer(10dp)
RangeSlider(
  value       = priceMin..priceMax,
  onValueChange = { range -> localFilters = localFilters.copy(priceMin=range.start.toInt(), priceMax=range.end.toInt()) },
  valueRange  = 0f..500_000f,
  steps       = 0,
  modifier    = Modifier.padding(horizontal=20dp),
  colors      = SliderDefaults.colors(
    thumbColor      = RentoColors.primary,
    activeTrackColor = RentoColors.primary,
    inactiveTrackColor = RentoColors.bg4,
  ),
)

Spacer(16dp)

─── BEDROOMS ─────────────────────────────────────────────────────────────────
SectionLabel("MIN BEDROOMS", padding horizontal=20dp)
Spacer(10dp)
FlowRow(padding horizontal=20dp):
  "Any" "1" "2" "3" "4+"
  selected based on bedroomsMin value

Spacer(24dp)
```

### Hosting Mode Sections

```
─── LOOKING FOR ──────────────────────────────────────────────────────────────
"Shared Space" "Full Property" "Room" "Studio" "Apartment" "House"

─── SUITABLE TENANT ──────────────────────────────────────────────────────────
"Male" "Female" "Family" "Any"

─── BUDGET UP TO ─────────────────────────────────────────────────────────────
SectionLabel + "PKR {value}" display + Slider (0..500000, steps=0)

─── MOVE-IN ──────────────────────────────────────────────────────────────────
"Immediate" "Within 1 Month" "Flexible"

Spacer(24dp)
```

### Bottom Buttons
```
Column(padding horizontal=20dp, bottom=32dp + WindowInsets.navigationBars.asPaddingValues()):
  PrimaryButton("Search"):
    onClick = {
      if (mode == LOOKING) onApplyListingFilters(localFilters.listingFilters)
      else onApplyRequestFilters(localFilters.requestFilters)
    }
  Spacer(10dp)
  GhostButton("Reset Filters"):
    onClick = { reset localFilters to defaults }
```

### Local State
```kotlin
// Inside SearchSheet:
var localListingFilters by remember(listingFilters) { mutableStateOf(listingFilters) }
var localRequestFilters by remember(requestFilters) { mutableStateOf(requestFilters) }
```

### Price formatter helper
```kotlin
private fun formatPrice(price: Int): String =
    when {
        price >= 100_000 -> "${price / 1000}k"
        price >= 10_000  -> "${price / 1000}k"
        else             -> price.toString()
    }
```

### Sub-tasks
- [ ] **M03-T18-A** Implement `SearchSheet` with all sections for both modes.
- [ ] **M03-T18-B** `RangeSlider` from `androidx.compose.material3`. Verify it's included in the Material3 version set in Module 01.
- [ ] **M03-T18-C** Chip toggle: tapping a selected chip deselects it (removes from list); tapping unselected adds it.
- [ ] **M03-T18-D** "Reset Filters" resets `localListingFilters` or `localRequestFilters` to `ListingFilters()` or `RequestFilters()` defaults.
- [ ] **M03-T18-E** Sheet is scrollable — all sections must be accessible even on small screens.
- [ ] **M03-T18-F** `@Preview` Looking mode, Hosting mode, dark + light.

---

## Task M03-T19 — City Picker Bottom Sheet

**File:** `presentation/home/components/CityPickerSheet.kt`

### Exact Specification
```kotlin
ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor   = MaterialTheme.rentoColors.bg1,
    shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    dragHandle       = { RentoDragHandle() },
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Choose City",
            style    = displayS,  // Fraunces 22sp SemiBold
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
        )

        // Province headers with expandable city lists
        // Or: flat scrollable list showing Province / City rows grouped by province

        LazyColumn(
            modifier        = Modifier.fillMaxWidth().height(400.dp),
            contentPadding  = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        ) {
            // All provinces and their cities
            provinceCityMap.forEach { (province, cities) ->
                stickyHeader(key = "header_$province") {
                    // Province header row
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.rentoColors.bg1)
                            .padding(vertical = 10.dp),
                    ) {
                        Text(province, style = labelStyle, color = MaterialTheme.rentoColors.t2)
                    }
                }
                items(cities, key = { "${province}_${it}" }) { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(province, city) }
                            .background(
                                if (city == currentCity) MaterialTheme.rentoColors.primaryTint
                                else Color.Transparent
                            )
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = CenterVertically,
                    ) {
                        Text(city, style = bodyM, color = MaterialTheme.rentoColors.t0)
                        if (city == currentCity) {
                            RentoIcons.Check with 18dp size, RentoColors.primary tint
                        }
                    }
                }
                item(key = "divider_$province") {
                    HorizontalDivider(color = MaterialTheme.rentoColors.border2, thickness = 0.5.dp)
                    Spacer(8.dp)
                }
            }
        }
        Spacer(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
    }
}
```

Province/city data: same `provinceCityMap` from Module 02 Onboarding. Extract to a shared `data/local/PakistanCities.kt` object:

```kotlin
object PakistanCities {
    val map: Map<String, List<String>> = mapOf(
        "Sindh" to listOf("Karachi", "Hyderabad", "Sukkur", "Larkana", "Mirpur Khas", "Nawabshah"),
        "Punjab" to listOf("Lahore", "Faisalabad", "Rawalpindi", "Gujranwala", "Sialkot", "Multan", "Bahawalpur", "Sargodha", "Sheikhupura"),
        "Khyber Pakhtunkhwa" to listOf("Peshawar", "Abbottabad", "Mardan", "Mingora", "Kohat"),
        "Balochistan" to listOf("Quetta", "Turbat", "Khuzdar", "Hub"),
        "Islamabad Capital Territory" to listOf("Islamabad"),
        "Azad Kashmir" to listOf("Muzaffarabad", "Mirpur", "Rawalakot"),
        "Gilgit-Baltistan" to listOf("Gilgit", "Skardu", "Chilas"),
    )
}
```

> This also replaces the duplicate string array approach from Module 02. Refactor `OnboardingScreen` Step 2 to use `PakistanCities.map` instead of string arrays. Update `data/local/PakistanCities.kt` and remove the `getCitiesForProvince()` reflection-based function.

### Sub-tasks
- [ ] **M03-T19-A** Create `data/local/PakistanCities.kt` with `PakistanCities` object.
- [ ] **M03-T19-B** Refactor Module 02 `OnboardingScreen` Step 2 to use `PakistanCities.map` (no breaking API changes — just the data source).
- [ ] **M03-T19-C** Implement `CityPickerSheet` with sticky province headers + city rows.
- [ ] **M03-T19-D** Currently selected city highlighted with `RentoColors.primaryTint` background.
- [ ] **M03-T19-E** `@Preview` dark + light, Karachi selected.

---

## Task M03-T20 — Add Overlay Sheet

**File:** `presentation/home/components/AddOverlaySheet.kt`

This sheet opens when the user taps the central FAB in `BottomNavBar`. It presents the two marketplace entry points.

### Exact Specification
```kotlin
ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor   = MaterialTheme.rentoColors.bg1,
    shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    dragHandle       = { RentoDragHandle() },
) {
    Column(
        modifier                = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalAlignment     = CenterHorizontally,
    ) {
        Spacer(8.dp)
        Text("What are you doing today?", style = displayS, color = RentoColors.t0)
        Spacer(6.dp)
        Text("Choose to find accommodation or list your space.", style = bodyS, color = RentoColors.t2)
        Spacer(28.dp)

        // TWO INTENT CARDS — same design as WelcomeScreen intent cards
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

          // LOOKING CARD
          AddOverlayCard(
            icon      = RentoIcons.Search,
            title     = "I'm Looking",
            subtitle  = "Post what you need — budget, area, preferences.",
            onClick   = onLookingTap,
          )

          // HOSTING CARD
          AddOverlayCard(
            icon      = RentoIcons.Home,
            title     = "I'm Hosting",
            subtitle  = "List your space and start getting enquiries.",
            onClick   = onHostingTap,
          )
        }

        Spacer(32.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
    }
}
```

### `AddOverlayCard` — private composable within `AddOverlaySheet.kt`
```
Row(
  modifier = Modifier
    .fillMaxWidth()
    .background(RentoColors.bg2, RoundedCornerShape(24.dp))
    .border(1.5.dp, RentoColors.border2, RoundedCornerShape(24.dp))
    .clip(RoundedCornerShape(24.dp))
    .clickable { onClick() }
    .padding(20.dp),
  verticalAlignment = CenterVertically,
  horizontalArrangement = Arrangement.spacedBy(18.dp),
):
  Box(54dp × 54dp, RentoColors.primaryTint bg, 18dp corner, 1.5dp RentoColors.primaryRing border):
    Icon(icon, 26dp, RentoColors.primary)
  Column(weight=1f):
    Text(title, 16sp Bold, RentoColors.t0)
    Spacer(4dp)
    Text(subtitle, 13sp, RentoColors.t2)
  RentoIcons.Chevron, 20dp, RentoColors.t3

Press: scale(0.985) spring animation
```

### Sub-tasks
- [ ] **M03-T20-A** Implement `AddOverlaySheet` and `AddOverlayCard`.
- [ ] **M03-T20-B** Press scale via `interactionSource.collectIsPressedAsState()`.
- [ ] **M03-T20-C** `@Preview` dark + light.

---

## Task M03-T21 — Notification Badge Dot Logic

**File:** `presentation/home/components/HomeHeader.kt`

The red pulsing dot on the notification bell is controlled by `SharedPreferences["hasUnreadNotifications"]`.

### State Flow
```
Module 13 (FCM): when onMessageReceived() fires → SharedPreferences["hasUnreadNotifications"] = true
HomeViewModel.checkUnreadNotifications(): reads pref → updates uiState.hasUnreadNotifications
HomeScreen: LaunchedEffect(Unit) → viewModel.checkUnreadNotifications() on resume
User taps bell: viewModel.clearUnreadDot() → SharedPreferences = false + uiState updates
```

### Sub-tasks
- [ ] **M03-T21-A** Verify that `checkUnreadNotifications()` is called in `HomeViewModel.init {}`.
- [ ] **M03-T21-B** Add `LaunchedEffect(Unit) { viewModel.checkUnreadNotifications() }` in `HomeScreen` to re-check on resume.
- [ ] **M03-T21-C** Pulsing dot: `rememberInfiniteTransition` + scale 0.85↔1.0, 700ms tween, `RepeatMode.Reverse`.
- [ ] **M03-T21-D** Dot ring: 2.5dp ring drawn via:
```kotlin
Modifier.drawBehind {
    drawCircle(
        color  = rentoColors.bg1,
        radius = size.minDimension / 2 + 2.5.dp.toPx(),
    )
    drawCircle(
        color  = rentoColors.red,
        radius = size.minDimension / 2,
    )
}
```
- [ ] **M03-T21-E** Dot is only rendered when `hasUnreadNotifications = true` — `AnimatedVisibility(visible = hasUnread)` with `scaleIn()` entry and `scaleOut()` exit.

---

## Task M03-T22 — Pagination Logic

**File:** `HomeScreen.kt` (LaunchedEffect + derivedStateOf already shown in M03-T09)

### Full Pagination Implementation

```kotlin
// In HomeScreen composable:
val shouldLoadMore by remember {
    derivedStateOf {
        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo
        val lastVisible = visibleItems.lastOrNull()?.index ?: return@derivedStateOf false
        val totalCount = layoutInfo.totalItemsCount
        // Trigger when within 3 items of the end
        lastVisible >= totalCount - 3 && totalCount > 0
    }
}

LaunchedEffect(shouldLoadMore) {
    if (shouldLoadMore) viewModel.loadNextPage()
}
```

For grid mode, the grid is a single item in the `LazyColumn` — the grid itself handles end detection via total item count check against `uiState.listings.size`.

A separate `LaunchedEffect` handles grid pagination:
```kotlin
val gridShouldLoadMore by remember {
    derivedStateOf {
        val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
        last >= gridState.layoutInfo.totalItemsCount - 2
    }
}
LaunchedEffect(gridShouldLoadMore) {
    if (gridShouldLoadMore) viewModel.loadNextPage()
}
```

### Sub-tasks
- [ ] **M03-T22-A** Implement `shouldLoadMore` using `derivedStateOf` as shown.
- [ ] **M03-T22-B** Loading indicator at bottom of list while `isLoadingMore = true`: renders `RentoDeleteSpinner(size=28.dp)` inside a centred `Box` item.
- [ ] **M03-T22-C** `hasMore = false` → do NOT show loading indicator, do NOT call `loadNextPage()`.
- [ ] **M03-T22-D** Verify: scrolling to end triggers load, new items append to existing list, no duplicates (verify by `key = { it.id }` in `items()`).

---

## Task M03-T23 — String Resources

Appended to `res/values/strings.xml`:

```xml
<!-- ─── Home Screen ──────────────────────────────────────────────────────── -->
<string name="home_header_looking_line1">Find Your </string>
<string name="home_header_looking_line2_gradient">Next Home</string>
<string name="home_header_hosting_line1">Find Your </string>
<string name="home_header_hosting_line2_gradient">Tenant</string>
<string name="home_section_looking">Nearby Spaces</string>
<string name="home_section_hosting">Recent Requests</string>
<string name="home_see_all">See all</string>

<string name="home_search_placeholder_looking">Area, property type, budget…</string>
<string name="home_search_placeholder_hosting">Search tenant requests…</string>
<string name="home_search_filter">Filter</string>

<!-- Quick filter chips — Looking -->
<string name="qf_all">All</string>
<string name="qf_share">Share</string>
<string name="qf_full_rent">Full Rent</string>
<string name="qf_pg">PG</string>
<string name="qf_hourly">Hourly</string>
<string name="qf_female_only">Female Only</string>
<string name="qf_le_25k">≤ 25k</string>

<!-- Quick filter chips — Hosting -->
<string name="qf_immediate">Immediate</string>
<string name="qf_this_month">This Month</string>
<string name="qf_student">Student</string>
<string name="qf_family">Family</string>
<string name="qf_professional">Professional</string>

<!-- View mode switcher -->
<string name="vm_list_cd">List view</string>
<string name="vm_grid_cd">Grid view</string>

<!-- Empty states -->
<string name="home_empty_looking_title">No spaces found</string>
<string name="home_empty_looking_subtitle">No listings match your search in %1$s. Try adjusting your filters.</string>
<string name="home_empty_hosting_title">No requests found</string>
<string name="home_empty_hosting_subtitle">No tenant requests in %1$s yet. Be the first to post!</string>
<string name="home_empty_hosting_cta">Post a Request</string>

<!-- City picker -->
<string name="city_picker_title">Choose City</string>

<!-- Add overlay -->
<string name="add_overlay_title">What are you doing today?</string>
<string name="add_overlay_subtitle">Choose to find accommodation or list your space.</string>
<string name="add_overlay_looking_title">I\'m Looking</string>
<string name="add_overlay_looking_subtitle">Post what you need — budget, area, preferences.</string>
<string name="add_overlay_hosting_title">I\'m Hosting</string>
<string name="add_overlay_hosting_subtitle">List your space and start getting enquiries.</string>

<!-- Search sheet -->
<string name="search_sheet_title">Smart Search</string>
<string name="search_button">Search</string>
<string name="search_reset">Reset Filters</string>
<string name="search_section_property_type">PROPERTY TYPE</string>
<string name="search_section_suitable_for">SUITABLE FOR</string>
<string name="search_section_furnished">FURNISHED</string>
<string name="search_section_duration">DURATION</string>
<string name="search_section_price_range">PRICE RANGE</string>
<string name="search_section_bedrooms">MIN BEDROOMS</string>
<string name="search_section_looking_for">LOOKING FOR</string>
<string name="search_section_tenant">SUITABLE TENANT</string>
<string name="search_section_budget">BUDGET UP TO</string>
<string name="search_section_movein">MOVE-IN</string>
<string name="search_any">Any</string>

<!-- Property type chips -->
<string name="pt_apartment">Apartment</string>
<string name="pt_house">House</string>
<string name="pt_duplex">Duplex</string>
<string name="pt_portion">Portion</string>
<string name="pt_room">Room</string>
<string name="pt_studio">Studio</string>
<string name="pt_hostel_bed">Hostel Bed</string>
<string name="pt_penthouse">Penthouse</string>
<string name="pt_farmhouse">Farm House</string>
<string name="pt_office">Office Space</string>
<string name="pt_guest_room">Guest Room</string>
<string name="pt_coworking">Coworking Space</string>

<!-- Suitable for -->
<string name="sf_male_only">Male Only</string>
<string name="sf_female_only">Female Only</string>
<string name="sf_family">Family</string>
<string name="sf_all">All</string>

<!-- Duration chips -->
<string name="dur_daily">Daily</string>
<string name="dur_weekly">Weekly</string>
<string name="dur_monthly">Monthly</string>

<!-- Furnished chips -->
<string name="fur_furnished">Furnished</string>
<string name="fur_semi">Semi-Furnished</string>
<string name="fur_unfurnished">Unfurnished</string>

<!-- Move-in chips -->
<string name="movein_immediate">Immediate</string>
<string name="movein_month">Within 1 Month</string>
<string name="movein_flexible">Flexible</string>

<!-- Errors -->
<string name="home_error_generic">Something went wrong. Pull to refresh.</string>
```

---

## Task M03-T24 — Unit Tests

### `HomeViewModelTest.kt`
```
init_loadsListingsForLookingMode
init_loadsSliders
init_checksUnreadNotifications
setMode_toLooking_triggersListingsLoad
setMode_toHosting_triggersRequestsLoad
setMode_sameModeAgain_doesNotReload
setViewMode_updatesViewMode_doesNotReload
changeCity_resetsAndReloads
loadFeed_reset_clearsExistingItems
loadFeed_looking_populatesListings
loadFeed_hosting_populatesRequests
loadFeed_networkError_setsListingsError
loadNextPage_appendsItems
loadNextPage_whenNoMore_doesNothing
loadNextPage_whenAlreadyLoading_doesNothing
applyListingFilters_resetsAndReloads
setListingQuickFilter_resetsAndReloads
setRequestQuickFilter_resetsAndReloads
clearUnreadDot_clearsPref
refreshFeed_resetsAndReloads
```

### `GetListingsUseCaseTest.kt`
```
invoke_callsRepositoryWithCorrectParams
invoke_propagatesRepositoryError
invoke_returnsCorrectPair
```

### `GetRequestsUseCaseTest.kt`
```
invoke_callsRepositoryWithCorrectParams
invoke_propagatesError
```

### Sub-tasks
- [ ] **M03-T24-A** Create all test files with all tests listed above.
- [ ] **M03-T24-B** Mock `ListingRepository`, `TenantRequestRepository`, `SliderRepository`, `SharedPreferences` with MockK.
- [ ] **M03-T24-C** Use `StandardTestDispatcher` + `Turbine` for Flow assertions.
- [ ] **M03-T24-D** `./gradlew test` → all pass. Paste output.
- [ ] **M03-T24-E** `./gradlew koverReport` → ≥ 80% on `com.rento.app.presentation.home` + `com.rento.app.domain.usecase.home`. Paste summary.

---

## Task M03-T25 — Build Gate

- [ ] **M03-T25-A** `./gradlew lint` → zero new warnings.
- [ ] **M03-T25-B** `./gradlew detekt` → zero violations.
- [ ] **M03-T25-C** `./gradlew assembleDebug` → `BUILD SUCCESSFUL`. Paste output.
- [ ] **M03-T25-D** `./gradlew test` → all tests pass. Paste output.
- [ ] **M03-T25-E** `./gradlew koverReport` → ≥ 80%. Paste summary.
- [ ] **M03-T25-F** Update `ANDROID_PROGRESS.md`.
- [ ] **M03-T25-G** Create `CODE_REVIEW_MODULE_03.md`.

---

## 30. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Looking mode feed load on launch | `HomeViewModel.init` → `loadListings(reset=true)` | ☐ |
| Mode switch LOOKING → HOSTING | `viewModel.setMode(HOSTING)` → requests load, listings pause | ☐ |
| Mode switch HOSTING → LOOKING | `viewModel.setMode(LOOKING)` → listings load, requests pause | ☐ |
| Pull-to-refresh (both modes) | `PullRefreshState` → `refreshFeed()` | ☐ |
| Scroll-to-end pagination | `derivedStateOf` + `loadNextPage()` | ☐ |
| List ↔ Grid view toggle (both modes) | `setViewMode()` → layout switch, no data reload | ☐ |
| Quick filter chip — select | `setListingQuickFilter` / `setRequestQuickFilter` + reload | ☐ |
| Quick filter chip — deselect (tap again) | null filter → clears quick filter | ☐ |
| Search bar tap → Smart Search sheet | `showSearchSheet = true` | ☐ |
| Apply listing filters from search sheet | `onApplyListingFilters` → reload | ☐ |
| Apply request filters from search sheet | `onApplyRequestFilters` → reload | ☐ |
| Reset filters in search sheet | defaults restored + reload | ☐ |
| City picker tap → change city | `showCitySheet = true` → `changeCity()` → reload | ☐ |
| Location chip shows correct city/province | `uiState.selectedCity`, `selectedProvince` | ☐ |
| Banner slider → tap slide with listing | `onListingTap(linkedListingId)` | ☐ |
| Banner slider → tap slide without listing | no navigation | ☐ |
| Shimmer shown while loading | `listingsLoading && listings.isEmpty()` | ☐ |
| Empty state — no listings | `listings.isEmpty && !loading` | ☐ |
| Empty state — Hosting CTA "Post a Request" | `onAddRequestTap()` | ☐ |
| FAB tap → Add Overlay sheet | `showAddOverlay = true` | ☐ |
| Add Overlay → I'm Hosting | `onHostingTap` → `onAddListingTap()` → `ListingFormScreen` | ☐ |
| Add Overlay → I'm Looking | `onLookingTap` → `onAddRequestTap()` → `RequestFormScreen` | ☐ |
| Notification bell tap → clears dot + navigates | `clearUnreadDot()` + `onNotificationsTap()` | ☐ |
| Unread dot visible when pref = true | `hasUnreadNotifications` pulsing dot | ☐ |
| PropertyCard tap → Listing Detail | `onListingTap(id)` → nav | ☐ |
| TenantRequestCard tap → Request Detail | `onRequestTap(id)` → nav | ☐ |

---

## 31. CODE_REVIEW_MODULE_03.md Template

```markdown
# Code Review — Module 03: Home & Discovery
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-03-home
**Spec version:** ANDROID_MODULE_03.md v1.0.0

---

## ✅ Architecture Compliance
- [ ] Domain models have zero Android imports
- [ ] `DocumentSnapshot` cursor type only in repository interfaces — not in domain models
- [ ] `HomeViewModel` uses use cases — not repository implementations directly
- [ ] No Firestore calls inside Composable bodies
- [ ] Koin `homeModule` registered in `RentoApplication`
- [ ] `PakistanCities` object extracted to `data/local/`

---

## ✅ Design Reference Verification

| Screen | Prototype Ref | BG ✓ | Radii ✓ | Font/Size/Weight ✓ | Icons ✓ | Animations ✓ | States ✓ |
|--------|---------------|------|---------|-------------------|---------|--------------|---------|
| HomeScreen (Looking+List) | function Home() | | | | | | |
| HomeScreen (Looking+Grid) | function Home() | | | | | | |
| HomeScreen (Hosting+List) | function Home() | | | | | | |
| HomeScreen (Hosting+Grid) | function Home() | | | | | | |
| SearchSheet (Looking) | SearchSheet() | | | | | | |
| SearchSheet (Hosting) | SearchSheet() | | | | | | |
| AddOverlaySheet | AddOverlay() | | | | | | |
| CityPickerSheet | — | | | | | | |

---

## ✅ Design System Compliance
- [ ] `MeshBackground` NOT used on HomeScreen (bg0 used for home — consistent with prototype)
- [ ] `GradientText` used for "Next Home" / "Tenant" gradient words in header
- [ ] `TabPill` from Module 01 used for mode switcher
- [ ] `HomeBannerSlider` from Module 01 connected to live data
- [ ] `PropertyCard` from Module 01 — not re-implemented
- [ ] `TenantRequestCard` from Module 01 — not re-implemented
- [ ] `EmptyState` from Module 01 — not re-implemented
- [ ] `RentoDeleteSpinner` used as pagination loader — not CircularProgressIndicator
- [ ] Shimmer via `rememberShimmerEffect()` from Module 01 Animations
- [ ] `BottomNavBar` from Module 01 integrated correctly
- [ ] Pulsing notification dot implemented with `rememberInfiniteTransition`
- [ ] `AnimatedVisibility` for notification dot enter/exit
- [ ] `animateColorAsState` on view mode switcher buttons
- [ ] Pull-to-refresh with RentO colours (bg2 + primary)

---

## ✅ Dual Marketplace Compliance
- [ ] Looking mode shows ONLY property listings — never tenant requests
- [ ] Hosting mode shows ONLY tenant requests — never property listings
- [ ] Mode switch immediately clears previous feed and loads new one
- [ ] Both modes have equal visual treatment and empty state quality
- [ ] Quick filter chips change completely per mode — not shared chips
- [ ] Search sheet shows different sections per mode — not shared sections
- [ ] Section header text changes per mode ("Nearby Spaces" vs "Recent Requests")

---

## ✅ Pagination Compliance
- [ ] First page loads 10 items
- [ ] Subsequent pages append (not replace) items
- [ ] `startAfter` cursor correctly stored and passed
- [ ] `hasMore = false` when returned count < pageSize
- [ ] Loading indicator shown at bottom during page load
- [ ] No loading indicator when `hasMore = false`
- [ ] Filter/mode change resets cursor and clears existing items before reload

---

## ✅ Performance Checks
- [ ] `LazyColumn` with `key {}` on all list items
- [ ] Grid uses chunked `Column` — NOT `LazyVerticalGrid` inside `LazyColumn`
- [ ] `derivedStateOf` for scroll-to-end detection
- [ ] `collectAsStateWithLifecycle()` — not `collectAsState()`
- [ ] `koinViewModel()` not `viewModel()`
- [ ] `remember(listingFilters)` for local filter state in SearchSheet

---

## ✅ Journey Coverage
- [ ] All 27 journeys in Section 30 verified

---

## ✅ Code Quality
- [ ] All strings in `strings.xml`
- [ ] Unit tests: all `HomeViewModel` functions covered
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

*End of Module 03 — Home & Discovery v1.0.0*
*Depends on: Module 01 + 02 complete. Next module: Module 04 — Listing Detail.*
