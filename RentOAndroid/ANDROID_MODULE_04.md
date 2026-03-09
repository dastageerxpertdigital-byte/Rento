# RentO — Android App
## Module 04 — Listing Detail
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 04
> **Branch:** `feature/module-04-listing-detail`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 03 ✅
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every section, icon, colour, padding, and interaction is derived verbatim from the prototype and `REQUIREMENTS_SPECIFICATION_v2_3.md`. No improvisation. No simplification. If anything is ambiguous — stop and ask.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
5. [Task M04-T01 — Domain Models & Repository Interfaces](#task-m04-t01--domain-models--repository-interfaces)
6. [Task M04-T02 — `SavedRepositoryImpl`](#task-m04-t02--savedrepositoryimpl)
7. [Task M04-T03 — `ReportRepositoryImpl`](#task-m04-t03--reportrepositoryimpl)
8. [Task M04-T04 — `ListingDetailViewModel` + Use Cases](#task-m04-t04--listingdetailviewmodel--use-cases)
9. [Task M04-T05 — Koin Module](#task-m04-t05--koin-module)
10. [Task M04-T06 — Navigation Wiring](#task-m04-t06--navigation-wiring)
11. [Task M04-T07 — Screen: `ListingDetailScreen` Shell](#task-m04-t07--screen-listingdetailscreen-shell)
12. [Task M04-T08 — Image Gallery Header](#task-m04-t08--image-gallery-header)
13. [Task M04-T09 — Price & Title Row](#task-m04-t09--price--title-row)
14. [Task M04-T10 — Badges Row](#task-m04-t10--badges-row)
15. [Task M04-T11 — Location Row](#task-m04-t11--location-row)
16. [Task M04-T12 — Key Facts Grid](#task-m04-t12--key-facts-grid)
17. [Task M04-T13 — About This Space (Expandable)](#task-m04-t13--about-this-space-expandable)
18. [Task M04-T14 — Amenities Grid](#task-m04-t14--amenities-grid)
19. [Task M04-T15 — Nearby Places](#task-m04-t15--nearby-places)
20. [Task M04-T16 — Lister Info Card](#task-m04-t16--lister-info-card)
21. [Task M04-T17 — Report Link](#task-m04-t17--report-link)
22. [Task M04-T18 — Sticky Bottom Bar](#task-m04-t18--sticky-bottom-bar)
23. [Task M04-T19 — Report Bottom Sheet](#task-m04-t19--report-bottom-sheet)
24. [Task M04-T20 — Unpublish Dialog](#task-m04-t20--unpublish-dialog)
25. [Task M04-T21 — Shimmer Loading State](#task-m04-t21--shimmer-loading-state)
26. [Task M04-T22 — String Resources](#task-m04-t22--string-resources)
27. [Task M04-T23 — Unit Tests](#task-m04-t23--unit-tests)
28. [Task M04-T24 — Build Gate](#task-m04-t24--build-gate)
29. [Journey Coverage Checklist](#29-journey-coverage-checklist)
30. [CODE_REVIEW_MODULE_04.md Template](#30-code_review_module_04md-template)

---

## 1. Module Overview

Module 04 delivers the full **Listing Detail screen** — the primary destination after tapping a `PropertyCard` in the home feed. It is a rich, information-dense screen with a cinematic image gallery header, scrollable content body, sticky CTA bottom bar, and several interactive overlays.

**Key features delivered:**
- Glassmorphic `HorizontalPager` image gallery (315dp) with `iov` gradient overlay, dot indicators, count pill, back/heart/share glassmorphic buttons
- Full listing content: title, price, badges, location row, 4-column key facts, expandable description, amenities grid (all amenities with available/unavailable state), nearby places, lister info card
- Save / unsave toggle — optimistic update, Firestore `users/{uid}/savedListings` subcollection
- Android `ShareSheet` for sharing listing text
- **Owner view:** Edit Listing + Unpublish/Publish toggle
- **Guest view:** Share button + Message Host CTA (→ Chat stub)
- **Unauthenticated view:** Message Host → redirects to Login
- Report Listing via `GlassBottomSheet` (reusable — also used in Module 05)
- Unpublish via 3-phase `GlassDialog` (CONFIRM → LOADING → SUCCESS/ERROR)
- Shimmer skeleton full-screen loading state

**Repositories introduced:** `SavedRepository`, `ReportRepository`

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── NearbyPlace.kt                   ← NEW
│   │   ├── Report.kt                        ← NEW
│   │   └── ReportTargetType.kt              ← NEW
│   ├── repository/
│   │   ├── SavedRepository.kt               ← NEW
│   │   └── ReportRepository.kt              ← NEW
│   └── usecase/
│       └── listing/
│           ├── GetListingDetailUseCase.kt
│           ├── SaveListingUseCase.kt
│           ├── UnsaveListingUseCase.kt
│           ├── IsListingSavedUseCase.kt
│           ├── UnpublishListingUseCase.kt
│           ├── PublishListingUseCase.kt
│           └── SubmitReportUseCase.kt
├── data/
│   └── repository/
│       ├── SavedRepositoryImpl.kt           ← NEW
│       └── ReportRepositoryImpl.kt          ← NEW
├── presentation/
│   └── listing/
│       └── detail/
│           ├── ListingDetailViewModel.kt
│           ├── ListingDetailScreen.kt
│           └── components/
│               ├── ListingImageGallery.kt
│               ├── ListingPriceTitleRow.kt
│               ├── ListingBadgesRow.kt
│               ├── ListingLocationRow.kt
│               ├── ListingKeyFacts.kt
│               ├── ListingAboutSection.kt
│               ├── ListingAmenitiesGrid.kt
│               ├── ListingNearbyPlaces.kt
│               ├── ListerInfoCard.kt
│               ├── ListingBottomBar.kt
│               ├── ReportSheet.kt
│               ├── UnpublishDialog.kt
│               └── ListingDetailShimmer.kt
└── di/
    └── ListingDetailModule.kt

app/src/main/res/values/strings.xml           ← Module 04 strings appended
app/src/test/java/com/rento/app/
├── domain/usecase/listing/
│   ├── GetListingDetailUseCaseTest.kt
│   ├── SaveListingUseCaseTest.kt
│   └── UnpublishListingUseCaseTest.kt
└── presentation/listing/detail/
    └── ListingDetailViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M04-T01 | Domain models (`NearbyPlace`, `Report`, `ReportTargetType`) + repository interfaces (`SavedRepository`, `ReportRepository`) + update `Listing.nearbyPlaces` type | `domain/` | ☐ |
| M04-T02 | `SavedRepositoryImpl` — save/unsave/isSaved for listings and requests | `data/repository/` | ☐ |
| M04-T03 | `ReportRepositoryImpl` — write report to Firestore `reports` collection | `data/repository/` | ☐ |
| M04-T04 | All use cases + `ListingDetailViewModel` | `domain/usecase/listing/`, `presentation/listing/detail/` | ☐ |
| M04-T05 | Koin DI — `ListingDetailModule.kt` | `di/` | ☐ |
| M04-T06 | Nav graph — wire `listing/{listingId}` route | `RentoNavGraph.kt` | ☐ |
| M04-T07 | `ListingDetailScreen` shell — `Scaffold`, `LazyColumn`, overlay wiring | `ListingDetailScreen.kt` | ☐ |
| M04-T08 | `ListingImageGallery` — pager, iov overlay, glassmorphic buttons, dots | `ListingImageGallery.kt` | ☐ |
| M04-T09 | `ListingPriceTitleRow` | `ListingPriceTitleRow.kt` | ☐ |
| M04-T10 | `ListingBadgesRow` — intent, furnish, suitability, negotiable badges | `ListingBadgesRow.kt` | ☐ |
| M04-T11 | `ListingLocationRow` — area/city/province + map button | `ListingLocationRow.kt` | ☐ |
| M04-T12 | `ListingKeyFacts` — 4-column grid (beds, baths, floor, type) | `ListingKeyFacts.kt` | ☐ |
| M04-T13 | `ListingAboutSection` — expandable text with overflow detection | `ListingAboutSection.kt` | ☐ |
| M04-T14 | `ListingAmenitiesGrid` — all amenities, available/unavailable states | `ListingAmenitiesGrid.kt` | ☐ |
| M04-T15 | `ListingNearbyPlaces` — category rows with distance badge | `ListingNearbyPlaces.kt` | ☐ |
| M04-T16 | `ListerInfoCard` — avatar, name, member since, star rating, Host badge | `ListerInfoCard.kt` | ☐ |
| M04-T17 | Report link (auth-gated, inline in screen) | `ListingDetailScreen.kt` | ☐ |
| M04-T18 | `ListingBottomBar` — 3 variants (guest, owner, unauth) | `ListingBottomBar.kt` | ☐ |
| M04-T19 | `ReportSheet` — `GlassBottomSheet`, category chips, details textarea, success state | `ReportSheet.kt` | ☐ |
| M04-T20 | `UnpublishDialog` — 3-phase `GlassDialog` (CONFIRM → LOADING → SUCCESS/ERROR) | `UnpublishDialog.kt` | ☐ |
| M04-T21 | `ListingDetailShimmer` | `ListingDetailShimmer.kt` | ☐ |
| M04-T22 | String resources | `strings.xml` | ☐ |
| M04-T23 | Unit tests — ViewModel + use cases | `*Test.kt` | ☐ |
| M04-T24 | Build gate | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 Domain Models

```kotlin
// NearbyPlace.kt
data class NearbyPlace(
    val category: String,       // "Mosque" | "Hospital" | "Mart" | "School" | "Bank/ATM" |
                                //  "Bus Stop" | "Restaurant" | "Petrol Pump" | "Gym" |
                                //  "Park" | "Pharmacy" | "Office Area"
    val distanceMeters: Int,
)

// ReportTargetType.kt
enum class ReportTargetType { LISTING, REQUEST, USER }

// Report.kt
data class Report(
    val targetId: String,
    val targetType: ReportTargetType,
    val reporterId: String,
    val category: String,
    val details: String?,
    val createdAt: Long,
)
```

**Update `Listing.kt`** — change the `nearbyPlaces` field type:
```kotlin
val nearbyPlaces: List<NearbyPlace>,   // was List<Any> — update to NearbyPlace
```

**Update `ListingMapper.kt`** — map the nested objects:
```kotlin
nearbyPlaces = (get("nearbyPlaces") as? List<*>)?.mapNotNull { item ->
    (item as? Map<*, *>)?.let { map ->
        NearbyPlace(
            category       = map["category"] as? String ?: return@mapNotNull null,
            distanceMeters = (map["distanceMeters"] as? Long)?.toInt() ?: 0,
        )
    }
} ?: emptyList(),
```

### 4.2 Repository Interfaces

```kotlin
// SavedRepository.kt
interface SavedRepository {
    suspend fun saveListing(uid: String, listingId: String): Result<Unit>
    suspend fun unsaveListing(uid: String, listingId: String): Result<Unit>
    suspend fun getSavedListings(uid: String): Result<List<String>>
    suspend fun isSaved(uid: String, listingId: String): Result<Boolean>
    suspend fun saveRequest(uid: String, requestId: String): Result<Unit>
    suspend fun unsaveRequest(uid: String, requestId: String): Result<Unit>
    suspend fun getSavedRequests(uid: String): Result<List<String>>
    suspend fun isRequestSaved(uid: String, requestId: String): Result<Boolean>
}

// ReportRepository.kt
interface ReportRepository {
    suspend fun submitReport(report: Report): Result<Unit>
}
```

### 4.3 UI State & ViewModel Contract

```kotlin
data class ListingDetailUiState(
    val listing: Listing? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false,
    val saveLoading: Boolean = false,
    val currentUserId: String? = null,
    val showReportSheet: Boolean = false,
    val showUnpublishDialog: Boolean = false,
    val reportCategory: String? = null,
    val reportDetails: String = "",
    val reportLoading: Boolean = false,
    val reportSuccess: Boolean = false,
    val reportError: String? = null,
    val unpublishPhase: DialogPhase = DialogPhase.IDLE,
    val unpublishError: String? = null,
)

enum class DialogPhase { IDLE, CONFIRM, LOADING, SUCCESS, ERROR }
```

### 4.4 `ListingRepository` — New Method Required

Add to `ListingRepository` interface and `ListingRepositoryImpl`:

```kotlin
// Interface addition:
suspend fun updateListingStatus(listingId: String, status: ListingStatus): Result<Unit>

// Implementation:
override suspend fun updateListingStatus(listingId: String, status: ListingStatus): Result<Unit> =
    runCatching {
        firestore.collection("listings").document(listingId)
            .update("status", status.name.lowercase()).await()
    }
```

### 4.5 Koin Module

```kotlin
val listingDetailModule = module {
    single<SavedRepository>  { SavedRepositoryImpl(get()) }
    single<ReportRepository> { ReportRepositoryImpl(get()) }
    factory { GetListingDetailUseCase(get()) }
    factory { SaveListingUseCase(get(), get()) }
    factory { UnsaveListingUseCase(get(), get()) }
    factory { IsListingSavedUseCase(get(), get()) }
    factory { UnpublishListingUseCase(get()) }
    factory { PublishListingUseCase(get()) }
    factory { SubmitReportUseCase(get(), get()) }
    viewModel {
        ListingDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }
}
```

---

## Task M04-T01 — Domain Models & Repository Interfaces

### Sub-tasks
- [ ] **M04-T01-A** Create `domain/model/NearbyPlace.kt`, `domain/model/Report.kt`, `domain/model/ReportTargetType.kt`.
- [ ] **M04-T01-B** Update `domain/model/Listing.kt` — change `nearbyPlaces: List<NearbyPlace>`. Recompile — fix any type errors in `ListingMapper.kt`.
- [ ] **M04-T01-C** Update `data/repository/ListingMapper.kt` with `nearbyPlaces` nested-object mapper.
- [ ] **M04-T01-D** Add `updateListingStatus()` to `ListingRepository` interface and `ListingRepositoryImpl`.
- [ ] **M04-T01-E** Create `domain/repository/SavedRepository.kt` and `domain/repository/ReportRepository.kt`.
- [ ] **M04-T01-F** Verify: `grep -r "import android\.\|import com.google.firebase" app/src/main/java/com/rento/app/domain/` → empty output.

---

## Task M04-T02 — `SavedRepositoryImpl`

**File:** `data/repository/SavedRepositoryImpl.kt`

### Sub-tasks
- [ ] **M04-T02-A** Implement all 8 interface methods:

```kotlin
class SavedRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : SavedRepository {

    override suspend fun saveListing(uid: String, listingId: String): Result<Unit> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedListings").document(listingId)
                .set(mapOf("savedAt" to FieldValue.serverTimestamp())).await()
        }

    override suspend fun unsaveListing(uid: String, listingId: String): Result<Unit> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedListings").document(listingId)
                .delete().await()
        }

    override suspend fun getSavedListings(uid: String): Result<List<String>> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedListings").get().await()
                .documents.map { it.id }
        }

    override suspend fun isSaved(uid: String, listingId: String): Result<Boolean> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedListings").document(listingId)
                .get().await().exists()
        }

    override suspend fun saveRequest(uid: String, requestId: String): Result<Unit> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedRequests").document(requestId)
                .set(mapOf("savedAt" to FieldValue.serverTimestamp())).await()
        }

    override suspend fun unsaveRequest(uid: String, requestId: String): Result<Unit> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedRequests").document(requestId)
                .delete().await()
        }

    override suspend fun getSavedRequests(uid: String): Result<List<String>> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedRequests").get().await()
                .documents.map { it.id }
        }

    override suspend fun isRequestSaved(uid: String, requestId: String): Result<Boolean> =
        runCatching {
            firestore.collection("users").document(uid)
                .collection("savedRequests").document(requestId)
                .get().await().exists()
        }
}
```

---

## Task M04-T03 — `ReportRepositoryImpl`

**File:** `data/repository/ReportRepositoryImpl.kt`

### Sub-tasks
- [ ] **M04-T03-A** Implement `submitReport`:

```kotlin
class ReportRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : ReportRepository {

    override suspend fun submitReport(report: Report): Result<Unit> = runCatching {
        val data = hashMapOf(
            "targetId"   to report.targetId,
            "targetType" to report.targetType.name.lowercase(),
            "reporterId" to report.reporterId,
            "category"   to report.category,
            "details"    to (report.details ?: ""),
            "status"     to "pending",
            "createdAt"  to FieldValue.serverTimestamp(),
        )
        firestore.collection("reports").add(data).await()
    }
}
```

---

## Task M04-T04 — `ListingDetailViewModel` + Use Cases

**Files:** `domain/usecase/listing/*.kt`, `presentation/listing/detail/ListingDetailViewModel.kt`

### Sub-tasks

- [ ] **M04-T04-A** `GetListingDetailUseCase` — wraps `ListingRepository.getListingById()`:

```kotlin
class GetListingDetailUseCase(private val listingRepository: ListingRepository) {
    suspend operator fun invoke(listingId: String): Result<Listing> =
        listingRepository.getListingById(listingId)
}
```

- [ ] **M04-T04-B** `SaveListingUseCase` and `UnsaveListingUseCase`:

```kotlin
class SaveListingUseCase(
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(listingId: String): Result<Unit> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        return savedRepository.saveListing(uid, listingId)
    }
}

class UnsaveListingUseCase(
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(listingId: String): Result<Unit> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        return savedRepository.unsaveListing(uid, listingId)
    }
}
```

- [ ] **M04-T04-C** `IsListingSavedUseCase`:

```kotlin
class IsListingSavedUseCase(
    private val savedRepository: SavedRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(listingId: String): Result<Boolean> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.success(false)   // unauthenticated → treat as not saved
        return savedRepository.isSaved(uid, listingId)
    }
}
```

- [ ] **M04-T04-D** `UnpublishListingUseCase` and `PublishListingUseCase`:

```kotlin
class UnpublishListingUseCase(private val listingRepository: ListingRepository) {
    suspend operator fun invoke(listingId: String): Result<Unit> =
        listingRepository.updateListingStatus(listingId, ListingStatus.UNPUBLISHED)
}

class PublishListingUseCase(private val listingRepository: ListingRepository) {
    suspend operator fun invoke(listingId: String): Result<Unit> =
        listingRepository.updateListingStatus(listingId, ListingStatus.PUBLISHED)
}
```

- [ ] **M04-T04-E** `SubmitReportUseCase`:

```kotlin
class SubmitReportUseCase(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        targetId: String,
        targetType: ReportTargetType,
        category: String,
        details: String?,
    ): Result<Unit> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        if (category.isBlank())
            return Result.failure(Exception("Please select a report category."))
        val report = Report(
            targetId   = targetId,
            targetType = targetType,
            reporterId = uid,
            category   = category,
            details    = details?.takeIf { it.isNotBlank() },
            createdAt  = System.currentTimeMillis(),
        )
        return reportRepository.submitReport(report)
    }
}
```

- [ ] **M04-T04-F** Implement `ListingDetailViewModel`:

```kotlin
class ListingDetailViewModel(
    private val getListing: GetListingDetailUseCase,
    private val saveListing: SaveListingUseCase,
    private val unsaveListing: UnsaveListingUseCase,
    private val isListingSaved: IsListingSavedUseCase,
    private val unpublishListing: UnpublishListingUseCase,
    private val publishListing: PublishListingUseCase,
    private val submitReport: SubmitReportUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingDetailUiState())
    val uiState: StateFlow<ListingDetailUiState> = _uiState.asStateFlow()

    private var listingId: String = ""

    fun load(id: String) {
        listingId = id
        _uiState.update {
            it.copy(isLoading = true, error = null,
                    currentUserId = authRepository.getCurrentUserId())
        }
        viewModelScope.launch {
            val listingResult = getListing(id)
            val savedResult   = isListingSaved(id)
            listingResult.fold(
                onSuccess = { listing ->
                    _uiState.update { state ->
                        state.copy(
                            listing   = listing,
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

    // ── Save toggle (optimistic) ──────────────────────────────────────
    fun toggleSave() {
        val wasSaved = _uiState.value.isSaved
        _uiState.update { it.copy(isSaved = !wasSaved, saveLoading = true) }
        viewModelScope.launch {
            val result = if (wasSaved) unsaveListing(listingId)
                         else          saveListing(listingId)
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
                targetId   = listingId,
                targetType = ReportTargetType.LISTING,
                category   = category,
                details    = _uiState.value.reportDetails.takeIf { it.isNotBlank() },
            ).fold(
                onSuccess = { _uiState.update { it.copy(reportLoading = false, reportSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(reportLoading = false, reportError = e.message) } },
            )
        }
    }

    // ── Unpublish dialog ─────────────────────────────────────────────
    fun openUnpublishDialog() {
        _uiState.update {
            it.copy(showUnpublishDialog = true,
                    unpublishPhase = DialogPhase.CONFIRM, unpublishError = null)
        }
    }
    fun closeUnpublishDialog() {
        _uiState.update { it.copy(showUnpublishDialog = false, unpublishPhase = DialogPhase.IDLE) }
    }

    fun confirmUnpublish() {
        _uiState.update { it.copy(unpublishPhase = DialogPhase.LOADING) }
        viewModelScope.launch {
            unpublishListing(listingId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            unpublishPhase = DialogPhase.SUCCESS,
                            listing        = state.listing?.copy(status = ListingStatus.UNPUBLISHED),
                        )
                    }
                    delay(1_800)
                    _uiState.update { it.copy(showUnpublishDialog = false, unpublishPhase = DialogPhase.IDLE) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(unpublishPhase = DialogPhase.ERROR, unpublishError = e.message) }
                },
            )
        }
    }

    fun retryUnpublish() {
        _uiState.update { it.copy(unpublishPhase = DialogPhase.CONFIRM, unpublishError = null) }
    }

    // ── Publish (for currently-unpublished listings) ─────────────────
    fun togglePublish() {
        viewModelScope.launch {
            publishListing(listingId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(listing = state.listing?.copy(status = ListingStatus.PUBLISHED))
                    }
                },
                onFailure = { /* snackbar handled in screen */ },
            )
        }
    }
}
```

---

## Task M04-T05 — Koin Module

**File:** `di/ListingDetailModule.kt`

### Sub-tasks
- [ ] **M04-T05-A** Create `listingDetailModule` per section 4.5.
- [ ] **M04-T05-B** Add `listingDetailModule` to `RentoApplication.startKoin { modules(...) }`.
- [ ] **M04-T05-C** Verify: `./gradlew assembleDebug` — no Koin injection failures.

---

## Task M04-T06 — Navigation Wiring

**File:** `presentation/navigation/RentoNavGraph.kt`

### Sub-tasks
- [ ] **M04-T06-A** Replace placeholder composable for `HomeRoutes.LISTING_DETAIL` with:

```kotlin
composable(
    route     = HomeRoutes.LISTING_DETAIL,   // "listing/{listingId}"
    arguments = listOf(navArgument("listingId") { type = NavType.StringType }),
) { backStackEntry ->
    val listingId = backStackEntry.arguments?.getString("listingId") ?: return@composable
    ListingDetailScreen(
        listingId         = listingId,
        onBack            = { navController.popBackStack() },
        onNavigateToChat  = { chatId -> navController.navigate("chat/$chatId") },
        onNavigateToLogin = { navController.navigate(AuthRoutes.LOGIN) },
        onNavigateToEdit  = { navController.navigate("${HomeRoutes.LISTING_FORM}?listingId=$listingId&mode=edit") },
        onNavigateToMap   = { lat, lng, label ->
            navController.navigate("${HomeRoutes.MAP}?lat=$lat&lng=$lng&label=${Uri.encode(label)}")
        },
    )
}
```

- [ ] **M04-T06-B** `chat/{chatId}`, `listing/form?listingId=…`, and `map?lat=…` remain placeholder routes — no implementation change needed in this module.

---

## Task M04-T07 — Screen: `ListingDetailScreen` Shell

**File:** `presentation/listing/detail/ListingDetailScreen.kt`

### Full Implementation

```kotlin
@Composable
fun ListingDetailScreen(
    listingId: String,
    viewModel: ListingDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToChat: (chatId: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToMap: (lat: Double, lng: Double, label: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(listingId) { viewModel.load(listingId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0)
    ) {
        when {
            uiState.isLoading -> ListingDetailShimmer()

            uiState.error != null && uiState.listing == null ->
                ListingErrorScreen(
                    message = uiState.error!!,
                    onRetry = { viewModel.load(listingId) },
                )

            uiState.listing != null -> {
                val listing = uiState.listing!!
                val isOwner = uiState.currentUserId != null && uiState.currentUserId == listing.uid
                val isAuth  = uiState.currentUserId != null

                Scaffold(
                    containerColor = MaterialTheme.rentoColors.bg0,
                    bottomBar = {
                        ListingBottomBar(
                            isOwner         = isOwner,
                            isAuthenticated = isAuth,
                            isPublished     = listing.status == ListingStatus.PUBLISHED,
                            onMessageHost   = {
                                if (!isAuth) onNavigateToLogin()
                                else {
                                    val chatId = buildChatId(listing.uid, uiState.currentUserId!!, listing.id)
                                    onNavigateToChat(chatId)
                                }
                            },
                            onShare         = { shareListing(context, listing) },
                            onEdit          = onNavigateToEdit,
                            onUnpublish     = { viewModel.openUnpublishDialog() },
                            onPublish       = { viewModel.togglePublish() },
                        )
                    },
                ) { paddingValues ->
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                    ) {
                        item(key = "gallery") {
                            ListingImageGallery(
                                imageUrls    = listing.imageUrls,
                                isSaved      = uiState.isSaved,
                                onBack       = onBack,
                                onToggleSave = { viewModel.toggleSave() },
                                onShare      = { shareListing(context, listing) },
                            )
                        }

                        item(key = "price_title") {
                            ListingPriceTitleRow(
                                title    = "${listing.propertyType} in ${listing.area}",
                                price    = listing.price,
                                duration = listing.duration,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            )
                        }

                        item(key = "badges") {
                            ListingBadgesRow(
                                intent      = listing.intent,
                                furnished   = listing.furnished,
                                suitableFor = listing.suitableFor,
                                negotiable  = listing.isNegotiable,
                                modifier    = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 12.dp),
                            )
                        }

                        item(key = "location") {
                            ListingLocationRow(
                                area     = listing.area,
                                city     = listing.city,
                                province = listing.province,
                                onMapTap = { onNavigateToMap(listing.lat, listing.lng, listing.area) },
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 20.dp),
                            )
                        }

                        item(key = "key_facts") {
                            ListingKeyFacts(
                                bedrooms     = listing.bedrooms,
                                bathrooms    = listing.bathrooms,
                                floor        = listing.floor,
                                propertyType = listing.propertyType,
                                modifier     = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 24.dp),
                            )
                        }

                        if (!listing.description.isNullOrBlank()) {
                            item(key = "about") {
                                ListingAboutSection(
                                    description = listing.description,
                                    modifier    = Modifier
                                        .padding(horizontal = 20.dp)
                                        .padding(bottom = 24.dp),
                                )
                            }
                        }

                        if (listing.amenities.isNotEmpty()) {
                            item(key = "amenities") {
                                ListingAmenitiesGrid(
                                    availableAmenities = listing.amenities,
                                    modifier           = Modifier
                                        .padding(horizontal = 20.dp)
                                        .padding(bottom = 24.dp),
                                )
                            }
                        }

                        if (listing.nearbyPlaces.isNotEmpty()) {
                            item(key = "nearby") {
                                ListingNearbyPlaces(
                                    places   = listing.nearbyPlaces,
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .padding(bottom = 24.dp),
                                )
                            }
                        }

                        item(key = "lister") {
                            ListerInfoCard(
                                ownerName   = listing.ownerName,
                                createdAt   = listing.createdAt,
                                modifier    = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 24.dp),
                            )
                        }

                        if (isAuth) {
                            item(key = "report_link") {
                                Text(
                                    text      = stringResource(R.string.detail_report_listing),
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
            targetLabel      = "Listing",
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

    if (uiState.showUnpublishDialog) {
        UnpublishDialog(
            listingTitle = uiState.listing?.let { "${it.propertyType} in ${it.area}" } ?: "",
            phase        = uiState.unpublishPhase,
            errorMessage = uiState.unpublishError,
            onConfirm    = { viewModel.confirmUnpublish() },
            onRetry      = { viewModel.retryUnpublish() },
            onDismiss    = { viewModel.closeUnpublishDialog() },
        )
    }
}

// ── Helpers ──────────────────────────────────────────────────────────
private fun buildChatId(hostUid: String, viewerUid: String, listingId: String): String {
    // Deterministic chat ID — same regardless of who initiates
    val sorted = listOf(hostUid, viewerUid).sorted()
    return "chat_${sorted[0]}_${sorted[1]}_$listingId"
}

private fun shareListing(context: Context, listing: Listing) {
    val text = "${listing.propertyType} in ${listing.area}, ${listing.city} — " +
               "PKR ${listing.price}/mo\nFind it on RentO"
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

### `ListingErrorScreen` — private composable

```kotlin
@Composable
private fun ListingErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0),
        contentAlignment  = Alignment.Center,
    ) {
        EmptyState(
            icon     = RentoIcons.AlertCircle,
            title    = stringResource(R.string.error_generic_title),
            subtitle = message,
            cta      = stringResource(R.string.error_retry),
            onCta    = onRetry,
        )
    }
}
```

### Sub-tasks
- [ ] **M04-T07-A** Implement `ListingDetailScreen` shell.
- [ ] **M04-T07-B** `buildChatId()` produces a sorted, deterministic chat key — verify both `hostUid_viewerUid` and `viewerUid_hostUid` resolve to the same ID.
- [ ] **M04-T07-C** `@Preview` with mock listing, dark + light — loading state, loaded state, error state.

---

## Task M04-T08 — Image Gallery Header

**File:** `presentation/listing/detail/components/ListingImageGallery.kt`

### Glassmorphic Button Helper

```kotlin
// Defined as a top-level function in the file — used by all 3 overlay buttons
private fun Modifier.glassmorphicButton(): Modifier = this
    .clip(RoundedCornerShape(15.dp))
    .background(Color(0xFF040C08).copy(alpha = 0.50f), RoundedCornerShape(15.dp))
    .then(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            Modifier.blur(12.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        else
            Modifier   // API 30 fallback: no blur, alpha compensated to 0.75f above
    )
    .border(1.dp, MaterialTheme.rentoColors.primaryRing, RoundedCornerShape(15.dp))
```

> ⚠️ `Modifier.blur()` requires API 31+. App `minSdk = 30`. Always guard with `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S`. On API 30, use `alpha = 0.75f` and no blur — visually similar.

### Exact Specification

```kotlin
@Composable
fun ListingImageGallery(
    imageUrls: List<String>,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    onShare: () -> Unit,
)
```

```
Box(fillMaxWidth, height=315.dp, contentAlignment=BottomCenter):

  ─── PAGER / FALLBACK ──────────────────────────────────────────────
  if imageUrls.isNotEmpty():
    HorizontalPager(state=pagerState, modifier=fillMaxSize):
      AsyncImage(
        model   = ImageRequest.Builder(context).data(url).crossfade(300).build(),
        contentScale = ContentScale.Crop,
        modifier = fillMaxSize,
      )

  else:
    Box(fillMaxSize):
      Box(fillMaxSize, background=gradientPrimary brush)   // from RentoTheme
      Text("🏠", fontSize=80.sp, Modifier.align(Center).alpha(0.4f))

  ─── IOV GRADIENT OVERLAY ──────────────────────────────────────────
  Box(
    modifier = fillMaxSize + background(
      Brush.verticalGradient(
        colors = [Color.Transparent, RentoColors.bg0.copy(alpha=0.92f)],
      )
    )
  )

  ─── BACK BUTTON (top-left) ────────────────────────────────────────
  Box(
    modifier = Modifier
      .align(TopStart).padding(start=16.dp, top=16.dp)
      .size(42.dp)
      .glassmorphicButton()
      .clickable(indication=null) { onBack() },
    contentAlignment = Center,
  ):
    Icon(RentoIcons.Back, null, tint=Color.White, size=20.dp)

  ─── HEART + SHARE BUTTONS (top-right) ────────────────────────────
  Row(
    modifier = Modifier.align(TopEnd).padding(end=16.dp, top=16.dp),
    horizontalArrangement = spacedBy(9.dp),
  ):
    // Heart
    Box(
      42.dp, glassmorphicButton(), clickable { onToggleSave() }, contentAlignment=Center,
    ):
      Icon(
        if isSaved RentoIcons.HeartFilled else RentoIcons.Heart,
        tint = if isSaved RentoColors.primary else Color.White,
        size = 20.dp,
      )

    // Share
    Box(42.dp, glassmorphicButton(), clickable { onShare() }, contentAlignment=Center):
      Icon(RentoIcons.Share, tint=Color.White, size=20.dp)

  ─── PAGE COUNT PILL (bottom-right) ───────────────────────────────
  if imageUrls.size > 1:
    Box(
      modifier = Modifier
        .align(BottomEnd).padding(end=16.dp, bottom=16.dp)
        .background(Color(0xFF040C08).copy(alpha=0.5f), RoundedCornerShape(12.dp))
        .padding(vertical=5.dp, horizontal=13.dp),
    ):
      Text("${pagerState.currentPage + 1} / ${imageUrls.size}", 12sp Bold, Color.White)

  ─── DOT INDICATORS (bottom-centre) ───────────────────────────────
  if imageUrls.size > 1:
    Row(
      modifier = Modifier.align(BottomCenter).padding(bottom=16.dp),
      horizontalArrangement = spacedBy(6.dp),
      verticalAlignment = CenterVertically,
    ):
      repeat(imageUrls.size) { idx ->
        val active = pagerState.currentPage == idx
        Box(
          modifier = Modifier
            .animateContentSize()
            .width(if active 20.dp else 6.dp)
            .height(6.dp)
            .background(
              if active RentoColors.primary else Color.White.copy(alpha=0.5f),
              RoundedCornerShape(3.dp),
            )
        )
      }
```

### Sub-tasks
- [ ] **M04-T08-A** Implement `ListingImageGallery`.
- [ ] **M04-T08-B** Use `androidx.compose.foundation.pager.HorizontalPager` (Compose ≥ 1.4 built-in). No accompanist dependency.
- [ ] **M04-T08-C** `pagerState = rememberPagerState(pageCount = { imageUrls.size.coerceAtLeast(1) })`.
- [ ] **M04-T08-D** `animateContentSize()` on dot width — smooth expand/collapse on page change.
- [ ] **M04-T08-E** `RentoIcons.HeartFilled` must exist in Module 01. If absent, add it and document in code review.
- [ ] **M04-T08-F** `@Preview` — 3 images, isSaved=true + isSaved=false; 0 images (fallback), dark + light.

---

## Task M04-T09 — Price & Title Row

**File:** `presentation/listing/detail/components/ListingPriceTitleRow.kt`

### Exact Specification

```
Row(fillMaxWidth, SpaceBetween, verticalAlignment=Top):

  LEFT (weight=1f, paddingEnd=12.dp):
    Text(title, Fraunces 26sp SemiBold, RentoColors.t0, lineHeight=26*1.2=31.2sp, maxLines=3)

  RIGHT (Column, horizontalAlignment=End):
    Text("PKR ${formatPricePKR(price)}", 23sp Bold, RentoColors.primary)
    Text("/ ${durationLabel(duration)}", 11sp, RentoColors.t2, textAlign=End)
```

```kotlin
private fun formatPricePKR(price: Int): String = when {
    price >= 100_000 -> "${price / 1_000}k"
    price >= 10_000  -> "${price / 1_000}k"
    else             -> NumberFormat.getNumberInstance(Locale.US).format(price)
}

private fun durationLabel(duration: String): String = when (duration) {
    "daily"   -> "day"
    "weekly"  -> "week"
    "monthly" -> "month"
    "hourly"  -> "hour"
    else      -> "month"
}
```

### Sub-tasks
- [ ] **M04-T09-A** Implement `ListingPriceTitleRow`.
- [ ] **M04-T09-B** `@Preview` — long title + high price, short title + low price, dark + light.

---

## Task M04-T10 — Badges Row

**File:** `presentation/listing/detail/components/ListingBadgesRow.kt`

Reuses `Badge` component from Module 01. Four possible badges in a `LazyRow`.

### Badge Variant Mapping

| Field | Badge Style | Example Label |
|-------|-------------|---------------|
| `intent` | `BadgeStyle.INTENT` (`.bp` — primaryTint/primary2) | "Shared Space" / "Full Property" / "By the Hour" |
| `furnished` | `BadgeStyle.PRIMARY` (primaryTint/primaryRing) | "Furnished" / "Semi-Furnished" / "Unfurnished" |
| `suitableFor` | `BadgeStyle.ACCENT` (`.bm` — accentTint/accentRing) | "Female Only" / "Family" / "All" |
| `isNegotiable` (only if true) | `BadgeStyle.SECONDARY` (`.ba` — yellow tint) | "Negotiable" |

```kotlin
private fun intentLabel(intent: String) = when (intent) {
    "share"    -> "Shared Space"
    "fullRent" -> "Full Property"
    "hourly"   -> "By the Hour"
    else       -> intent.replaceFirstChar { it.uppercase() }
}

private fun furnishedLabel(furnished: String) = when (furnished) {
    "furnished"   -> "Furnished"
    "semi"        -> "Semi-Furnished"
    "unfurnished" -> "Unfurnished"
    else          -> furnished.replaceFirstChar { it.uppercase() }
}
```

### Sub-tasks
- [ ] **M04-T10-A** Implement `ListingBadgesRow` with `LazyRow(horizontalArrangement = spacedBy(8.dp))`.
- [ ] **M04-T10-B** Negotiable badge only rendered when `negotiable = true`.
- [ ] **M04-T10-C** `@Preview` — all 4 badges, 3 badges (not negotiable), dark + light.

---

## Task M04-T11 — Location Row

**File:** `presentation/listing/detail/components/ListingLocationRow.kt`

### Exact Specification

```
Row(
  fillMaxWidth, CenterVertically,
  background=RentoColors.bg2, border=1.5.dp RentoColors.border,
  shape=RoundedCornerShape(16.dp), padding=12.dp V / 14.dp H,
):
  Icon(RentoIcons.Pin, 18.dp, RentoColors.primary)
  Spacer(10.dp)
  Text("${area}, ${city}, ${province}", bodyM 14sp, RentoColors.t1,
    Modifier.weight(1f), maxLines=2, overflow=Ellipsis)
  Spacer(10.dp)
  Box(
    34.dp × 34.dp,
    background=RentoColors.primaryTint, shape=RoundedCornerShape(11.dp),
    clickable { onMapTap() },
    contentAlignment=Center,
  ):
    Icon(RentoIcons.Map, 18.dp, RentoColors.primary)
```

### Sub-tasks
- [ ] **M04-T11-A** Implement `ListingLocationRow`.
- [ ] **M04-T11-B** `@Preview` dark + light, long address.

---

## Task M04-T12 — Key Facts Grid

**File:** `presentation/listing/detail/components/ListingKeyFacts.kt`

### Exact Specification

4-column `Row` with equal-weight tiles. Each tile:

```
Box(
  Modifier.weight(1f),
  background=RentoColors.bg2, border=1.5.dp RentoColors.border,
  shape=RoundedCornerShape(16.dp), padding=12.dp V / 6.dp H,
  contentAlignment=Center,
):
  Column(CenterHorizontally):
    Icon(icon, 20.dp, RentoColors.t1)
    Spacer(4.dp)
    Text(value, 14sp Bold, RentoColors.t0)
    Spacer(2.dp)
    Text(label, 10sp, RentoColors.t2)
```

**Tile definitions:**

| # | Icon | Value | Label |
|---|------|-------|-------|
| 1 | `RentoIcons.Bed` | `if (bedrooms==0) "Studio" else "$bedrooms"` | "Beds" |
| 2 | `RentoIcons.Bath` | `"$bathrooms"` | "Baths" |
| 3 | `RentoIcons.Layers` | `if (floor==null) "N/A" else floorLabel(floor)` | "Floor" |
| 4 | `RentoIcons.Home` | `propertyType.take(5)` (short label) | "Type" |

```kotlin
private fun floorLabel(floor: Int): String = when (floor) {
    0    -> "Ground"
    else -> "$floor${ordinalSuffix(floor)}"
}

private fun ordinalSuffix(n: Int): String = when {
    n % 100 in 11..13 -> "th"
    n % 10 == 1       -> "st"
    n % 10 == 2       -> "nd"
    n % 10 == 3       -> "rd"
    else              -> "th"
}
```

### Sub-tasks
- [ ] **M04-T12-A** Implement `ListingKeyFacts`.
- [ ] **M04-T12-B** Tile row uses `Row(horizontalArrangement = Arrangement.spacedBy(10.dp))`.
- [ ] **M04-T12-C** `@Preview` — floor=null, floor=0, floor=3, bedrooms=0, dark + light.

---

## Task M04-T13 — About This Space (Expandable)

**File:** `presentation/listing/detail/components/ListingAboutSection.kt`

### Exact Specification

```kotlin
@Composable
fun ListingAboutSection(description: String, modifier: Modifier = Modifier) {
    var expanded       by remember { mutableStateOf(false) }
    var textOverflows  by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(
            "About This Space",
            style    = Fraunces 20sp SemiBold,
            color    = RentoColors.t0,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        Text(
            text        = description,
            style       = bodyM,          // 14sp
            color       = RentoColors.t1,
            lineHeight  = 24.sp,          // 14sp × 1.72 ≈ 24sp
            maxLines    = if (expanded) Int.MAX_VALUE else 3,
            overflow    = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            onTextLayout = { result -> if (!expanded) textOverflows = result.hasVisualOverflow },
            modifier    = Modifier.animateContentSize(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
        )

        if (textOverflows || expanded) {
            Spacer(Modifier.height(8.dp))
            Text(
                text     = if (expanded) "Show less ↑" else "Read more ↓",
                style    = bodyS,         // 13sp Bold
                color    = RentoColors.primary,
                modifier = Modifier.clickable { expanded = !expanded },
            )
        }
    }
}
```

### Sub-tasks
- [ ] **M04-T13-A** Implement `ListingAboutSection`.
- [ ] **M04-T13-B** `onTextLayout` callback detects overflow only when `!expanded` — prevents false-positive on expanded state.
- [ ] **M04-T13-C** `animateContentSize` with `spring(DampingRatioMediumBouncy)` for smooth expansion.
- [ ] **M04-T13-D** `@Preview` — short text (no toggle shown), long text collapsed, long text expanded, dark + light.

---

## Task M04-T14 — Amenities Grid

**File:** `presentation/listing/detail/components/ListingAmenitiesGrid.kt`

### Canonical Amenity → Icon Map

```kotlin
private val amenityIconMap: Map<String, ImageVector> = mapOf(
    "WiFi"            to RentoIcons.Wifi,
    "AC"              to RentoIcons.Wind,
    "Generator"       to RentoIcons.Zap,
    "Water 24h"       to RentoIcons.Droplet,
    "Gas"             to RentoIcons.Flame,
    "Parking"         to RentoIcons.Car,
    "Lift"            to RentoIcons.ArrowUpDown,
    "Balcony"         to RentoIcons.Grid,
    "CCTV"            to RentoIcons.Video,
    "Geyser"          to RentoIcons.Thermometer,
    "Laundry"         to RentoIcons.Package,
    "Pet Friendly"    to RentoIcons.Heart,
    "Heating"         to RentoIcons.Flame,
    "Solar"           to RentoIcons.Sun,
    "TV"              to RentoIcons.Monitor,
    "Fridge"          to RentoIcons.Package,
    "Microwave"       to RentoIcons.Package,
    "Washing Machine" to RentoIcons.Package,
    "Security Guard"  to RentoIcons.Shield,
    "Rooftop Access"  to RentoIcons.ArrowUpDown,
    "Prayer Room"     to RentoIcons.Star,
    "Common Kitchen"  to RentoIcons.Home,
    "Shared Lounge"   to RentoIcons.Users,
    "Garden / Lawn"   to RentoIcons.Pin,
    "Swimming Pool"   to RentoIcons.Droplet,
    "Smoking Allowed" to RentoIcons.AlertCircle,
)
```

### Exact Specification

Shows **all** canonical amenities — available ones highlighted, unavailable ones dimmed. 4-column row grid inside `Column` (no `LazyVerticalGrid` — embedded in `LazyColumn`).

```kotlin
@Composable
fun ListingAmenitiesGrid(availableAmenities: List<String>, modifier: Modifier = Modifier) {
    val available = availableAmenities.toSet()
    val all       = amenityIconMap.keys.toList()

    Column(modifier) {
        Text("Amenities", style = Fraunces 20sp SemiBold, color = RentoColors.t0,
             modifier = Modifier.padding(bottom = 12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            all.chunked(4).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { amenity ->
                        AmenityTile(
                            amenity     = amenity,
                            icon        = amenityIconMap[amenity] ?: RentoIcons.Star,
                            isAvailable = amenity in available,
                            modifier    = Modifier.weight(1f),
                        )
                    }
                    // Fill remainder to maintain 4-col alignment
                    repeat(4 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
```

### `AmenityTile` — private composable

```
Box(
  weight=1f (from parent),
  background=RentoColors.bg2, border=1.5.dp RentoColors.border,
  shape=RoundedCornerShape(16.dp), padding=12.dp V / 6.dp H,
  contentAlignment=Center,
):
  Column(CenterHorizontally):
    Icon(icon, 20.dp, tint = if isAvailable RentoColors.t1 else RentoColors.t3)
    Spacer(4.dp)
    Text(amenity, 10sp, color = if isAvailable RentoColors.t1 else RentoColors.t3,
         textAlign=Center, maxLines=2, overflow=Ellipsis)
```

### Sub-tasks
- [ ] **M04-T14-A** Implement `ListingAmenitiesGrid` + `AmenityTile`.
- [ ] **M04-T14-B** `amenityIconMap` is a top-level val (not inside a composable) for stable reference.
- [ ] **M04-T14-C** `@Preview` — 10 amenities available out of 26, dark + light.

---

## Task M04-T15 — Nearby Places

**File:** `presentation/listing/detail/components/ListingNearbyPlaces.kt`

### Category → Icon Map

```kotlin
private val nearbyIconMap: Map<String, ImageVector> = mapOf(
    "Mosque"      to RentoIcons.Star,
    "Hospital"    to RentoIcons.AlertCircle,
    "Mart"        to RentoIcons.Package,
    "School"      to RentoIcons.Home,
    "Bank/ATM"    to RentoIcons.Package,
    "Bus Stop"    to RentoIcons.Car,
    "Restaurant"  to RentoIcons.Flame,
    "Petrol Pump" to RentoIcons.Zap,
    "Gym"         to RentoIcons.Users,
    "Park"        to RentoIcons.Pin,
    "Pharmacy"    to RentoIcons.AlertCircle,
    "Office Area" to RentoIcons.Building,
)
```

### Exact Specification

```
Column(modifier):
  Text("What's Nearby", Fraunces 20sp SemiBold, bottom=12.dp)

  Column(verticalArrangement=spacedBy(8.dp)):
    places.forEach { place ->
      Row(
        fillMaxWidth, CenterVertically,
        background=RentoColors.bg2, border=1.5.dp RentoColors.border,
        shape=RoundedCornerShape(14.dp), padding=11.dp V / 14.dp H,
      ):
        Icon(nearbyIconMap[place.category] ?: RentoIcons.Pin, 20.dp, RentoColors.t1)
        Spacer(12.dp)
        Text(place.category, bodyM 14sp, RentoColors.t1, Modifier.weight(1f))
        Spacer(8.dp)
        Badge(label=formatDistance(place.distanceMeters), style=BadgeStyle.INTENT)
    }
```

```kotlin
private fun formatDistance(meters: Int): String = when {
    meters >= 1_000 -> "${meters / 1_000} km"
    else            -> "$meters m"
}
```

### Sub-tasks
- [ ] **M04-T15-A** Implement `ListingNearbyPlaces`. Reuse `Badge` from Module 01.
- [ ] **M04-T15-B** `@Preview` — 4 nearby places, dark + light.

---

## Task M04-T16 — Lister Info Card

**File:** `presentation/listing/detail/components/ListerInfoCard.kt`

### Exact Specification

```
Row(
  fillMaxWidth, CenterVertically,
  background=RentoColors.bg2, border=1.5.dp RentoColors.border,
  shape=RoundedCornerShape(18.dp), padding=16.dp,
):

  // Avatar (54dp circle, gradient fill)
  Box(
    54.dp circle,
    background = Brush.linearGradient([RentoColors.primary, RentoColors.secondary]),
  ):
    Icon(RentoIcons.User, 28.dp, Color.White, centred)

  Spacer(14.dp)

  Column(Modifier.weight(1f)):

    Row(CenterVertically, spacedBy(8.dp)):
      Text(ownerName, 15sp Bold, RentoColors.t0)
      Badge("Host", BadgeStyle.BLUE)   // .bb = blue badge from Module 01

    Spacer(4.dp)

    Text(
      "Member since ${joinYear}",
      12sp, RentoColors.t2,
    )

    Spacer(4.dp)

    // Static star rating: 4.5 (placeholder — real ratings in a future module)
    Row(CenterVertically, spacedBy(4.dp)):
      repeat(5) { idx ->
        Icon(
          if idx < 4 RentoIcons.StarFilled else RentoIcons.StarHalf,
          14.dp, RentoColors.accent,
        )
      }
      Text("4.5", 12sp, RentoColors.t2, modifier=Modifier.padding(start=2.dp))
```

`joinYear` calculation:
```kotlin
val joinYear = remember(createdAt) {
    val cal = Calendar.getInstance().apply { timeInMillis = createdAt }
    cal.get(Calendar.YEAR).toString()
}
```

### Sub-tasks
- [ ] **M04-T16-A** Implement `ListerInfoCard`.
- [ ] **M04-T16-B** `RentoIcons.StarFilled` and `RentoIcons.StarHalf` — verify both exist in Module 01 `RentoIcons.kt`. If absent, add them there (document in `CODE_REVIEW_MODULE_04.md` under "Module 01 backfills").
- [ ] **M04-T16-C** `@Preview` dark + light.

---

## Task M04-T17 — Report Link

Implemented directly in `ListingDetailScreen` (see M04-T07). Verified sub-tasks:

- [ ] **M04-T17-A** Report link ONLY shown when `isAuth = true` — unauthenticated users cannot report.
- [ ] **M04-T17-B** `R.string.detail_report_listing = "Report this listing"` confirmed in strings.xml (M04-T22).

---

## Task M04-T18 — Sticky Bottom Bar

**File:** `presentation/listing/detail/components/ListingBottomBar.kt`

### Container (all variants)

```
Column(fillMaxWidth):
  HorizontalDivider(1.dp, RentoColors.border)
  Row/content(
    fillMaxWidth,
    background = RentoColors.navBg,
    padding = top=14.dp / bottom=(28.dp + navBarInsets) / horizontal=24.dp,
  )
```

```kotlin
@Composable
fun ListingBottomBar(
    isOwner: Boolean,
    isAuthenticated: Boolean,
    isPublished: Boolean,
    onMessageHost: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onUnpublish: () -> Unit,
    onPublish: () -> Unit,
)
```

### Variant A — Guest (authenticated, not owner)

```
Row(fillMaxWidth, CenterVertically, spacedBy(12.dp)):
  // Ghost share button
  Box(
    52.dp × 52.dp,
    background=RentoColors.bg2, border=1.5.dp RentoColors.border2,
    shape=RoundedCornerShape(16.dp), clickable { onShare() },
    contentAlignment=Center,
  ):
    Icon(RentoIcons.Share, 22.dp, RentoColors.t0)

  PrimaryButton(
    label    = "💬 Message Host",
    modifier = Modifier.weight(1f),
    onClick  = onMessageHost,
  )
```

### Variant B — Owner

```
Row(fillMaxWidth, CenterVertically, spacedBy(12.dp)):
  GhostButton("Edit Listing", Modifier.weight(1f), onClick=onEdit)

  if isPublished:
    GhostButton("Unpublish", Modifier.weight(1f), onClick=onUnpublish)
  else:
    PrimaryButton("Publish", Modifier.weight(1f), onClick=onPublish)
```

### Variant C — Unauthenticated

Identical to Variant A. `onMessageHost` redirects to Login in `ListingDetailScreen` — the button itself is visually identical to authenticated guest view.

### Sub-tasks
- [ ] **M04-T18-A** Implement `ListingBottomBar` switching between variants based on `isOwner`.
- [ ] **M04-T18-B** `WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()` added to bottom padding.
- [ ] **M04-T18-C** `@Preview` all 3 variants (guest, owner-published, owner-unpublished), dark + light.

---

## Task M04-T19 — Report Bottom Sheet

**File:** `presentation/listing/detail/components/ReportSheet.kt`

> **Note:** This component is designed for reuse. It will be moved to `presentation/shared/components/ReportSheet.kt` in Module 05. For now it lives in `listing/detail/components/`. When Module 05 is built, refactor the path and update the import in `ListingDetailScreen.kt`.

### Exact Specification (Section 32.8)

```kotlin
@Composable
fun ReportSheet(
    targetLabel: String,           // "Listing" | "Request" | "User"
    selectedCategory: String?,
    details: String,
    isLoading: Boolean,
    isSuccess: Boolean,
    error: String?,
    onSelectCategory: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
)
```

Uses `GlassBottomSheet` from Module 01 as the container:

```
GlassBottomSheet(onDismiss = onDismiss):

  AnimatedContent(targetState = isSuccess, transitionSpec = fadeIn+fadeOut 260ms):

    ── isSuccess = true ──────────────────────────────────────────────
    Column(CenterHorizontally, padding=32.dp):
      Box(60.dp circle, RentoColors.primaryTint bg, 1.5.dp RentoColors.primaryRing border):
        Icon(RentoIcons.Check, 28.dp, RentoColors.primary)
      Spacer(16.dp)
      Text("Report submitted", Fraunces 20sp SemiBold, centred)
      Spacer(8.dp)
      Text("We'll review it within 24 hours.", 13sp, RentoColors.t2, centred)

    LaunchedEffect(isSuccess):
      if (isSuccess) { delay(2_000); onDismiss() }

    ── isSuccess = false (form) ───────────────────────────────────────
    Column(padding horizontal=20.dp, verticalScroll=rememberScrollState()):
      Spacer(8.dp)
      Text("Report $targetLabel", Fraunces 22sp SemiBold, RentoColors.t0)
      Spacer(6.dp)
      Text("What's the issue?", bodyM, RentoColors.t2)
      Spacer(20.dp)

      FlowRow(horizontalGap=8.dp, verticalGap=8.dp):
        report categories (single-select):
          "Spam" · "Misleading Info" · "Fake Listing" · "Inappropriate Content"
          "Harassment" · "Pricing Issues" · "Duplicate" · "Other"
        Each: RentoChip(label, selected=(label==selectedCategory), onClick={onSelectCategory(label)})

      Spacer(20.dp)
      SectionLabel("MORE DETAILS (OPTIONAL)")
      Spacer(8.dp)

      // Multiline TextArea
      Box(
        fillMaxWidth, minHeight=80.dp,
        background=RentoColors.bg2, border=1.5.dp RentoColors.border2,
        shape=RoundedCornerShape(14.dp), padding=12.dp/14.dp,
      ):
        BasicTextField(
          value         = details,
          onValueChange = { if (it.length <= 500) onDetailsChange(it) },
          textStyle     = bodyM + RentoColors.t0,
          minLines      = 3,
          decorator     = { inner ->
            if (details.isEmpty()) Text("Additional context…", bodyM, RentoColors.t3)
            inner()
          },
        )

      AnimatedVisibility(visible = error != null):
        ErrorBanner(message = error ?: "", modifier = Modifier.padding(top=12.dp))

      Spacer(16.dp)

      PrimaryButton(
        label    = "Submit Report",
        isLoading = isLoading,
        enabled  = selectedCategory != null && !isLoading,
        onClick  = onSubmit,
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(32.dp + navBarBottomPadding)
```

### Sub-tasks
- [ ] **M04-T19-A** Implement `ReportSheet` using `GlassBottomSheet` from Module 01.
- [ ] **M04-T19-B** `AnimatedContent` on `isSuccess` state — crossfade transition.
- [ ] **M04-T19-C** Auto-dismiss: `LaunchedEffect(isSuccess) { if (isSuccess) { delay(2_000); onDismiss() } }`.
- [ ] **M04-T19-D** Max length 500 characters enforced in `onValueChange`.
- [ ] **M04-T19-E** `@Preview` — form with category selected; form with no category; success state, dark + light.

---

## Task M04-T20 — Unpublish Dialog

**File:** `presentation/listing/detail/components/UnpublishDialog.kt`

### Glass Dialog Design Tokens (from Section 33.3)

```kotlin
// Dark mode
val GlassDialogBg    = Color(0xE6081610)           // rgba(8,22,16,0.90)
val GlassDialogBorderBrush = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to Color(0x2AFFFFFF),   // 16% white
        0.4f to Color(0x122ECC8A),   // 7% primary green
        1.0f to Color(0x08FFFFFF),   // 3% white
    ),
    start = Offset.Zero,
    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

// Light mode
val GlassDialogBgLight          = Color(0xF0FFFFFF)
val GlassDialogBorderBrushLight = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to Color(0x400C7A50),
        0.5f to Color(0x18000000),
        1.0f to Color(0x0A0C7A50),
    ),
    start = Offset.Zero,
    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
```

### Dialog Entry / Exit Animation (Section 33.3)

```kotlin
AnimatedVisibility(
    visible = showDialog,
    enter   = fadeIn(tween(200)) + slideInVertically(
        animationSpec   = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        initialOffsetY  = { it / 8 },
    ),
    exit    = fadeOut(tween(160)) + slideOutVertically(
        animationSpec  = tween(160),
        targetOffsetY  = { it / 10 },
    ),
)
```

### Scrim (Section 33.2)

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(Color(0x99000000))  // 60% black
        .then(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                Modifier.blur(16.dp)
            else Modifier
        )
        .clickable(indication=null) { /* non-dismissable during LOADING */ }
)
```

### Exact Specification (from Section 33.13)

```kotlin
@Composable
fun UnpublishDialog(
    listingTitle: String,
    phase: DialogPhase,
    errorMessage: String?,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
)
```

```
// Scrim + Dialog wrapped in Box(fillMaxSize):
GlassScrim(dismissable = phase != DialogPhase.LOADING, onDismiss = onDismiss)
GlassDialogContainer (fillMaxWidth(0.88f), wrapContentHeight):

  BackHandler(enabled = phase == DialogPhase.LOADING) { /* block back during load */ }

  AnimatedContent(targetState = phase):

    ── CONFIRM ────────────────────────────────────────────────────────
    Column(CenterHorizontally, padding=24.dp, spacedBy=0.dp):
      // Icon
      Box(56.dp circle, RentoColors.accentTint):
        Icon(RentoIcons.EyeOff, 28.dp, RentoColors.accent)
      Spacer(16.dp)
      Text("Unpublish Listing?", Fraunces 22sp SemiBold, centred)
      Spacer(12.dp)
      // Body bullets
      Column(spacedBy=4.dp):
        Text("• \"$listingTitle\" will be hidden from search", 14sp, RentoColors.t1)
        Text("• It will appear in your Drafts", 14sp, RentoColors.t1)
        Text("• You can republish any time", 14sp, RentoColors.t1)
        Text("• Active chats will remain open", 14sp, RentoColors.t1)
      Spacer(24.dp)
      // Buttons (column, 10dp gap)
      GhostButton("Cancel", fillMaxWidth, onClick=onDismiss)
      Spacer(10.dp)
      // DarkAcc fill (not DarkRed — reversible action)
      Button(
        onClick = onConfirm,
        modifier = fillMaxWidth,
        colors = ButtonDefaults.buttonColors(containerColor = RentoColors.accent),
        shape  = RoundedCornerShape(100.dp),
      ):
        Text("Unpublish", 15sp Bold, Color.White)

    ── LOADING ────────────────────────────────────────────────────────
    Column(CenterHorizontally, padding=32.dp):
      RentoDeleteSpinner(size=32.dp)
      Spacer(16.dp)
      Text("Unpublishing…", 14sp, RentoColors.t2)

    ── SUCCESS ────────────────────────────────────────────────────────
    Column(CenterHorizontally, padding=28.dp):
      BounceEffect:
        Box(60.dp circle, RentoColors.primaryTint):
          Icon(RentoIcons.Check, 28.dp, RentoColors.primary)
      Spacer(16.dp)
      Text("Listing unpublished", Fraunces 22sp SemiBold, centred)
      Spacer(8.dp)
      Text("It's now in your Drafts.", 13sp, RentoColors.t2, centred)
      // Auto-dismiss handled by ViewModel (delay 1800ms) — no local delay here

    ── ERROR ──────────────────────────────────────────────────────────
    Column(CenterHorizontally, padding=24.dp):
      Box(56.dp circle, RentoColors.redTint):
        Icon(RentoIcons.AlertCircle, 28.dp, RentoColors.red)
      Spacer(16.dp)
      Text("Couldn't unpublish", Fraunces 22sp SemiBold, centred)
      Spacer(8.dp)
      Text(errorMessage ?: "Please try again.", 13sp, RentoColors.t2, centred)
      Spacer(24.dp)
      GhostButton("Cancel", fillMaxWidth, onClick=onDismiss)
      Spacer(10.dp)
      PrimaryButton("Try Again", fillMaxWidth, onClick=onRetry)
```

### Sub-tasks
- [ ] **M04-T20-A** Implement `UnpublishDialog`. The `GlassDialog` host (scrim + container) may be implemented as an inline composable here if the Module 01 `GlassDialog` component does not already exist with the exact token values above. If `GlassDialog` exists in Module 01 — use it. If it only has a stub — implement full tokens here and document as "Module 01 backfill" in code review.
- [ ] **M04-T20-B** `BackHandler(enabled = phase == LOADING)` blocks back press only during loading.
- [ ] **M04-T20-C** SUCCESS auto-dismiss: handled entirely in `ViewModel.confirmUnpublish()` with `delay(1_800)`. The composable shows SUCCESS state and awaits the ViewModel's next state update.
- [ ] **M04-T20-D** `BounceEffect` in SUCCESS phase — uses `rememberBounceEffect()` from Module 01 Animations.
- [ ] **M04-T20-E** `@Preview` — CONFIRM, LOADING, SUCCESS, ERROR phases, dark + light.

---

## Task M04-T21 — Shimmer Loading State

**File:** `presentation/listing/detail/components/ListingDetailShimmer.kt`

### Exact Specification

Full-screen skeleton that mirrors the real screen layout while loading:

```kotlin
@Composable
fun ListingDetailShimmer() {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0)
            .verticalScroll(rememberScrollState()),
    ) {
        // Image area
        Box(Modifier.fillMaxWidth().height(315.dp).shimmer())

        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(20.dp)

            // Title + price
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Box(Modifier.fillMaxWidth(0.6f).height(26.dp).shimmer())
                Box(Modifier.width(80.dp).height(26.dp).shimmer())
            }
            Spacer(12.dp)

            // Badges
            Row(horizontalArrangement = spacedBy(8.dp)) {
                repeat(3) {
                    Box(Modifier.width(80.dp).height(26.dp)
                        .clip(RoundedCornerShape(100.dp)).shimmer())
                }
            }
            Spacer(20.dp)

            // Location row
            Box(Modifier.fillMaxWidth().height(52.dp)
                .clip(RoundedCornerShape(16.dp)).shimmer())
            Spacer(20.dp)

            // Key facts
            Row(horizontalArrangement = spacedBy(10.dp)) {
                repeat(4) {
                    Box(Modifier.weight(1f).height(72.dp)
                        .clip(RoundedCornerShape(16.dp)).shimmer())
                }
            }
            Spacer(24.dp)

            // About title
            Box(Modifier.fillMaxWidth(0.5f).height(20.dp).shimmer())
            Spacer(8.dp)
            // About lines
            repeat(3) {
                Box(Modifier.fillMaxWidth().height(14.dp).shimmer())
                Spacer(4.dp)
            }
            Spacer(24.dp)

            // Lister card
            Box(Modifier.fillMaxWidth().height(80.dp)
                .clip(RoundedCornerShape(18.dp)).shimmer())
            Spacer(32.dp)
        }
    }
}
```

`.shimmer()` = `Modifier.shimmer()` from Module 01 via `rememberShimmerEffect()`.

### Sub-tasks
- [ ] **M04-T21-A** Implement `ListingDetailShimmer` as above.
- [ ] **M04-T21-B** `@Preview` dark mode only.

---

## Task M04-T22 — String Resources

Append to `res/values/strings.xml`:

```xml
<!-- ─── Listing Detail ──────────────────────────────────────────────────── -->
<string name="detail_report_listing">Report this listing</string>
<string name="detail_message_host">💬 Message Host</string>
<string name="detail_edit_listing">Edit Listing</string>
<string name="detail_unpublish">Unpublish</string>
<string name="detail_publish">Publish</string>
<string name="detail_share_cd">Share listing</string>
<string name="detail_save_cd">Save listing</string>
<string name="detail_unsave_cd">Remove from saved</string>
<string name="detail_back_cd">Go back</string>
<string name="detail_map_cd">View on map</string>

<string name="detail_about_title">About This Space</string>
<string name="detail_read_more">Read more ↓</string>
<string name="detail_show_less">Show less ↑</string>

<string name="detail_amenities_title">Amenities</string>
<string name="detail_nearby_title">What\'s Nearby</string>
<string name="detail_member_since">Member since %1$s</string>
<string name="detail_host_badge">Host</string>
<string name="detail_star_rating">4.5</string>

<string name="detail_key_beds">Beds</string>
<string name="detail_key_baths">Baths</string>
<string name="detail_key_floor">Floor</string>
<string name="detail_key_type">Type</string>
<string name="detail_floor_ground">Ground</string>
<string name="detail_beds_studio">Studio</string>

<!-- ─── Unpublish Dialog ────────────────────────────────────────────────── -->
<string name="unpublish_title">Unpublish Listing?</string>
<string name="unpublish_bullet_1">• \"%1$s\" will be hidden from search</string>
<string name="unpublish_bullet_2">• It will appear in your Drafts</string>
<string name="unpublish_bullet_3">• You can republish any time</string>
<string name="unpublish_bullet_4">• Active chats will remain open</string>
<string name="unpublish_confirm_btn">Unpublish</string>
<string name="unpublish_loading">Unpublishing…</string>
<string name="unpublish_success_title">Listing unpublished</string>
<string name="unpublish_success_body">It\'s now in your Drafts.</string>
<string name="unpublish_error_title">Couldn\'t unpublish</string>
<string name="unpublish_error_retry">Try Again</string>

<!-- ─── Report Sheet ────────────────────────────────────────────────────── -->
<string name="report_title_listing">Report Listing</string>
<string name="report_title_request">Report Request</string>
<string name="report_title_user">Report User</string>
<string name="report_subtitle">What\'s the issue?</string>
<string name="report_details_label">MORE DETAILS (OPTIONAL)</string>
<string name="report_details_placeholder">Additional context…</string>
<string name="report_submit_btn">Submit Report</string>
<string name="report_success_title">Report submitted</string>
<string name="report_success_body">We\'ll review it within 24 hours.</string>
<string name="report_cat_spam">Spam</string>
<string name="report_cat_misleading">Misleading Info</string>
<string name="report_cat_fake">Fake Listing</string>
<string name="report_cat_inappropriate">Inappropriate Content</string>
<string name="report_cat_harassment">Harassment</string>
<string name="report_cat_pricing">Pricing Issues</string>
<string name="report_cat_duplicate">Duplicate</string>
<string name="report_cat_other">Other</string>

<!-- ─── Generic Error Screen ────────────────────────────────────────────── -->
<string name="error_generic_title">Something went wrong</string>
<string name="error_retry">Try Again</string>
```

---

## Task M04-T23 — Unit Tests

### `ListingDetailViewModelTest.kt`

```
load_success_populatesListingAndSaveState
load_failure_setsErrorAndClearsLoading
toggleSave_whenUnsaved_savesOptimisticallyAndCallsUseCase
toggleSave_whenSaved_unsavesOptimisticallyAndCallsUseCase
toggleSave_onSaveFailure_revertsOptimisticUpdate
openReportSheet_resetsAllReportState
setReportCategory_updatesStateAndClearsError
submitReport_withNoCategory_doesNothing
submitReport_success_setsReportSuccess
submitReport_failure_setsReportError
openUnpublishDialog_setsConfirmPhaseAndClearsError
confirmUnpublish_success_setsSuccessAndUpdatesListingStatus
confirmUnpublish_success_autoDismissesAfter1800ms
confirmUnpublish_failure_setsErrorPhaseWithMessage
retryUnpublish_resetsToConfirmPhase
closeUnpublishDialog_setsIdlePhase
togglePublish_callsPublishUseCaseAndUpdatesStatus
currentUserId_setOnLoad
```

### `GetListingDetailUseCaseTest.kt`

```
invoke_delegatesToRepository
invoke_propagatesRepositorySuccess
invoke_propagatesRepositoryError
```

### `SaveListingUseCaseTest.kt`

```
invoke_whenAuthenticated_callsSaveListingWithCorrectUid
invoke_whenUnauthenticated_returnsFailureWithoutCallingRepository
```

### `UnpublishListingUseCaseTest.kt`

```
invoke_callsUpdateStatusWithUnpublished
invoke_propagatesRepositoryError
```

### Sub-tasks
- [ ] **M04-T23-A** Implement all tests using MockK + Turbine + `StandardTestDispatcher`.
- [ ] **M04-T23-B** Mock `AuthRepository`, `ListingRepository`, `SavedRepository`, `ReportRepository`.
- [ ] **M04-T23-C** For `confirmUnpublish_success_autoDismissesAfter1800ms`: use `advanceTimeBy(1_900)` after triggering confirm — verify `showUnpublishDialog = false`.
- [ ] **M04-T23-D** `./gradlew test` → all pass. Paste output.
- [ ] **M04-T23-E** `./gradlew koverReport` → ≥ 80% on `com.rento.app.presentation.listing.detail` + `com.rento.app.domain.usecase.listing`. Paste summary.

---

## Task M04-T24 — Build Gate

- [ ] **M04-T24-A** `./gradlew lint` → zero new warnings.
- [ ] **M04-T24-B** `./gradlew detekt` → zero violations.
- [ ] **M04-T24-C** `./gradlew assembleDebug` → `BUILD SUCCESSFUL`. Paste output.
- [ ] **M04-T24-D** `./gradlew test` → all pass. Paste output.
- [ ] **M04-T24-E** `./gradlew koverReport` → ≥ 80%. Paste summary.
- [ ] **M04-T24-F** Update `ANDROID_PROGRESS.md`.
- [ ] **M04-T24-G** Create `CODE_REVIEW_MODULE_04.md` using template in Section 30.

---

## 29. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Load listing via nav arg `listingId` | `LaunchedEffect(listingId) { viewModel.load(it) }` | ☐ |
| Shimmer shown while loading | `uiState.isLoading && listing==null` → `ListingDetailShimmer()` | ☐ |
| Error screen with retry button | `uiState.error!=null && listing==null` → `ListingErrorScreen` | ☐ |
| Image gallery — swipe between photos | `HorizontalPager` + `pagerState` | ☐ |
| Image gallery — page count pill updates | `pagerState.currentPage + 1 / total` | ☐ |
| Image gallery — dot indicators animate width | `animateContentSize()` on active dot | ☐ |
| Image gallery — fallback for no images | gradient + 🏠 emoji watermark | ☐ |
| Back button — glassmorphic, navigates back | `onBack()` | ☐ |
| Heart button — toggles save optimistically | `viewModel.toggleSave()` + optimistic state | ☐ |
| Heart icon fills primary colour when saved | `isSaved` drives icon + tint | ☐ |
| Save failure — reverts optimistic update | `onFailure { isSaved = wasSaved }` | ☐ |
| Share button — opens Android ShareSheet | `shareListing(context, listing)` | ☐ |
| Price formatted correctly (PKR Xk / month) | `formatPricePKR()` + `durationLabel()` | ☐ |
| All 4 badges rendered (intent, furnish, suit, nego) | `ListingBadgesRow` | ☐ |
| Negotiable badge hidden when `isNegotiable=false` | conditional render | ☐ |
| Location row — tap map button navigates to Map | `onNavigateToMap(lat, lng, label)` | ☐ |
| Key facts — Studio shown for 0 bedrooms | `if bedrooms==0 "Studio"` | ☐ |
| Key facts — "Ground" shown for floor=0 | `floorLabel(0)` | ☐ |
| Key facts — "N/A" for null floor | `if floor==null "N/A"` | ☐ |
| About section — collapsed to 3 lines by default | `maxLines=3` when not expanded | ☐ |
| About section — "Read more" toggle expands | `expanded=true` → `Int.MAX_VALUE` lines | ☐ |
| About section — toggle only shown when text overflows | `hasVisualOverflow` detection | ☐ |
| Amenities — all 26 shown, available highlighted | `amenityIconMap.keys` loop | ☐ |
| Amenities — unavailable ones dimmed (t3) | `isAvailable` drives tint + text colour | ☐ |
| Nearby places — distance badge formatted | `formatDistance()` | ☐ |
| Lister info — member since year | `Calendar.get(YEAR)` from `createdAt` | ☐ |
| Lister info — static 4.5 star rating | 4 StarFilled + 1 StarHalf icons | ☐ |
| Report link — hidden for unauthenticated | `if (isAuth)` guard | ☐ |
| Report link tap — opens report sheet | `viewModel.openReportSheet()` | ☐ |
| Report sheet — submit disabled until category selected | `enabled = selectedCategory != null` | ☐ |
| Report sheet — success state auto-dismisses after 2s | `LaunchedEffect(isSuccess) { delay(2_000); onDismiss() }` | ☐ |
| Bottom bar — guest view: share + message host | `!isOwner && isAuth` | ☐ |
| Bottom bar — message host taps → chat | `onNavigateToChat(buildChatId(...))` | ☐ |
| Bottom bar — unauthenticated: message host → login | `!isAuth → onNavigateToLogin()` | ☐ |
| Bottom bar — owner, published: edit + unpublish | `isOwner && isPublished` | ☐ |
| Bottom bar — owner, unpublished: edit + publish | `isOwner && !isPublished` | ☐ |
| Unpublish dialog — CONFIRM phase shown on tap | `viewModel.openUnpublishDialog()` → `DialogPhase.CONFIRM` | ☐ |
| Unpublish dialog — LOADING phase on confirm | `confirmUnpublish()` → `DialogPhase.LOADING` | ☐ |
| Unpublish dialog — back press blocked during LOADING | `BackHandler(enabled = phase==LOADING)` | ☐ |
| Unpublish dialog — SUCCESS phase, listing status updates | `listing.copy(status = UNPUBLISHED)` | ☐ |
| Unpublish dialog — auto-dismisses 1.8s after SUCCESS | `delay(1_800)` in ViewModel | ☐ |
| Unpublish dialog — ERROR phase with retry | `DialogPhase.ERROR` → retry button | ☐ |
| Publish tap — updates listing status to PUBLISHED | `viewModel.togglePublish()` | ☐ |

---

## 30. CODE_REVIEW_MODULE_04.md Template

```markdown
# Code Review — Module 04: Listing Detail
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-04-listing-detail
**Spec version:** ANDROID_MODULE_04.md v1.0.0

---

## ✅ Architecture Compliance
- [ ] Domain models have zero Android imports
- [ ] `NearbyPlace` used in `Listing.nearbyPlaces` — not raw `Map<*, *>` or `Any`
- [ ] `SavedRepository` + `ReportRepository` in domain; impls in data
- [ ] All use cases contain logic — no pass-throughs except `GetListingDetailUseCase` (wrapper for nav convenience — acceptable)
- [ ] `ListingDetailViewModel` uses use cases — not repositories directly
- [ ] Koin `listingDetailModule` added to `RentoApplication`
- [ ] `ListingRepository.updateListingStatus()` added correctly to interface + impl

---

## ✅ Module 01 Backfills
> List any icons or components that were missing from Module 01 and added in this module.

| Item | File Modified | Notes |
|------|--------------|-------|
| `RentoIcons.HeartFilled` | `RentoIcons.kt` | Filled heart for saved state |
| `RentoIcons.StarFilled` | `RentoIcons.kt` | Full star for rating |
| `RentoIcons.StarHalf` | `RentoIcons.kt` | Half star for rating |
| `RentoIcons.EyeOff` | `RentoIcons.kt` | Unpublish icon |
| `RentoColors.navBg` | `RentoTheme.kt` | Bottom bar background |
| `RentoColors.accentTint` | `RentoTheme.kt` | Accent-tinted bg for icons |
| `RentoColors.redTint` | `RentoTheme.kt` | Red-tinted bg for error icons |

---

## ✅ Design Reference Verification

| Section | Prototype Ref | Spec Match ✓ | Deviations |
|---------|---------------|-------------|------------|
| Image gallery header (315dp) | Section 11.1 | | |
| iov gradient overlay | Section 11.1 | | |
| Glassmorphic back button | Section 11.1 | | |
| Glassmorphic heart + share | Section 11.1 | | |
| Page count pill (glassmorphism) | Section 11.1 | | |
| Dot indicators (animated width) | Section 11.1 | | |
| Price & title row | Section 11.1 | | |
| Badges row (4 badges) | Section 11.1 | | |
| Location row + map button | Section 11.1 | | |
| Key facts 4-column grid | Section 11.1 | | |
| About section (expandable) | Section 11.1 | | |
| Amenities 4-column (all amenities) | Section 11.1 | | |
| Nearby places list | Section 11.1 | | |
| Lister info card | Section 11.1 | | |
| Sticky bottom bar — guest | Section 11.1 | | |
| Sticky bottom bar — owner | Section 11.1 | | |
| Report sheet | Section 32.8 | | |
| Unpublish dialog — all 4 phases | Section 33.13 | | |

---

## ✅ Design System Compliance
- [ ] Zero hardcoded colours — all from `LocalRentoColors.current.*`
- [ ] Fraunces for section titles; Plus Jakarta Sans for body/UI
- [ ] `Badge` from Module 01 used for intent/furnish/suitability/negotiable/distance — not recreated
- [ ] `GlassBottomSheet` from Module 01 used for `ReportSheet` container
- [ ] `GlassDialog` / tokens from Module 01 used for `UnpublishDialog`
- [ ] `shimmer()` modifier from Module 01 used in `ListingDetailShimmer`
- [ ] `PrimaryButton`, `GhostButton` from Module 01 — not recreated
- [ ] `EmptyState` from Module 01 used in `ListingErrorScreen`
- [ ] `BounceEffect` from Module 01 used on SUCCESS check icon
- [ ] `RentoDeleteSpinner` from Module 01 used in LOADING phase
- [ ] `API 30 blur guard` present on ALL uses of `Modifier.blur()`

---

## ✅ Journey Coverage
- [ ] All 44 journeys in Section 29 verified manually on emulator/device

---

## ✅ Glassmorphic Accuracy
- [ ] Back/Heart/Share button background: `rgba(4,12,8, 0.50)` + 12dp blur + 1dp primaryRing border
- [ ] Scrim: `rgba(0,0,0,0.60)` + 16dp blur (API 31+) or `0.73` opacity (API 30)
- [ ] Dialog container: `rgba(8,22,16,0.90)` + gradient border brush
- [ ] Dialog entry: spring slide-up (initialOffset = height/8) + fadeIn
- [ ] Dialog exit: tween 160ms slide-down (targetOffset = height/10) + fadeOut
- [ ] Blur API guard present in `glassmorphicButton()` and scrim

---

## ✅ Performance Checks
- [ ] `collectAsStateWithLifecycle()` used — not `collectAsState()`
- [ ] `LazyColumn` with `key = { ... }` on all items
- [ ] `animateContentSize()` applied to expandable text
- [ ] `remember { }` wrapping stable computations (joinYear, chatId)
- [ ] No Firestore calls inside Composable bodies
- [ ] `koinViewModel()` used — not `viewModel()`

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
**All visual deviations from spec are blockers — do not ship with known design discrepancies.**
**API 30 blur fallback is mandatory — test on API 30 emulator before marking build gate passed.**
```

---

*End of Module 04 — Listing Detail v1.0.0*
*Depends on: Modules 01–03. Next module: Module 05 — Tenant Request Detail.*
