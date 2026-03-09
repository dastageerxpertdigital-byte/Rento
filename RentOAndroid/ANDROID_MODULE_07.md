# RentO — Android App
## Module 07 — Tenant Request Form (Looking)
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 07
> **Branch:** `feature/module-07-request-form`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 03 ✅ · Module 04 ✅ · Module 05 ✅ · Module 06 ✅
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every colour, padding, animation, and interaction is derived verbatim from the prototype and `REQUIREMENTS_SPECIFICATION_v2_3.md`. Do not improvise. Do not simplify. If anything is ambiguous — stop and ask.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
5. [Task M07-T01 — Domain Models & Repository Interface](#task-m07-t01--domain-models--repository-interface)
6. [Task M07-T02 — `TenantRequestRepositoryImpl` — createRequest & updateRequest](#task-m07-t02--tenantrequestrepositorylimpl--createrequest--updaterequest)
7. [Task M07-T03 — DataStore Draft Persistence](#task-m07-t03--datastore-draft-persistence)
8. [Task M07-T04 — `TenantRequestFormViewModel`](#task-m07-t04--tenantformviewmodel)
9. [Task M07-T05 — Koin Module](#task-m07-t05--koin-module)
10. [Task M07-T06 — Navigation Wiring](#task-m07-t06--navigation-wiring)
11. [Task M07-T07 — Form Shell: `TenantRequestFormScreen`](#task-m07-t07--form-shell-tenantformscreen)
12. [Task M07-T08 — Step 1: What Do You Need?](#task-m07-t08--step-1-what-do-you-need)
13. [Task M07-T09 — Step 2: Property Type](#task-m07-t09--step-2-property-type)
14. [Task M07-T10 — Step 3: Your Budget](#task-m07-t10--step-3-your-budget)
15. [Task M07-T11 — Step 4: Where & Radius](#task-m07-t11--step-4-where--radius)
16. [Task M07-T12 — Step 5: Preferences](#task-m07-t12--step-5-preferences)
17. [Task M07-T13 — Step 6: About You](#task-m07-t13--step-6-about-you)
18. [Task M07-T14 — Discard Draft Dialog](#task-m07-t14--discard-draft-dialog)
19. [Task M07-T15 — String Resources](#task-m07-t15--string-resources)
20. [Task M07-T16 — Unit Tests](#task-m07-t16--unit-tests)
21. [Task M07-T17 — Build Gate](#task-m07-t17--build-gate)
22. [Journey Coverage Checklist](#22-journey-coverage-checklist)
23. [CODE_REVIEW_MODULE_07.md Template](#23-code_review_module_07md-template)

---

## 1. Module Overview

Module 07 delivers the **Tenant Request Form** — a 6-step wizard for seekers to post what they are looking for. It mirrors the Module 06 Listing Form shell (same `ProgressStepBar`, header, footer pattern) but has entirely different step content tailored to the seeker's perspective.

**Entry point:** Home screen → FAB → AddOverlaySheet → "I'm Looking" card  
**Route:** `request/form` (create) · `request/form?requestId={id}&mode=edit` (edit)

**Steps:**
1. What Do You Need? — 2 intent cards (Shared Space / Full Property)
2. Property Type — single-select chip grid (6 options)
3. Your Budget — single price input (max monthly rent)
4. Where & Radius — area search + animated `RadiusCircle` slider (1–15km)
5. Preferences — min bedrooms, min bathrooms, furnishing chips
6. About You — multi-line bio textarea (500 char max)

**Key architecture decisions:**
- Same shell composable as Module 06 — **not duplicated**. `FormShell` is extracted as a shared composable in Module 06 and reused here.
- Draft persisted to **DataStore** after every step advance.
- `TenantRequestRepository` already has its interface from Module 03 — only `createRequest()` and `updateRequest()` are implemented in this module (other methods were implemented in M03).
- No Firebase Storage, no Gemini — this form is simpler than the Listing Form.
- Submit creates `tenantRequests/{requestId}` Firestore document.

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   └── TenantRequestFormData.kt         ← NEW
│   └── usecase/
│       └── request/
│           ├── CreateRequestUseCase.kt       ← NEW
│           ├── UpdateRequestUseCase.kt       ← NEW
│           └── SaveRequestDraftUseCase.kt    ← NEW
├── data/
│   ├── repository/
│   │   └── TenantRequestRepositoryImpl.kt   ← UPDATE: add createRequest + updateRequest
│   └── local/
│       └── RequestDraftDataStore.kt         ← NEW
├── presentation/
│   └── request/
│       └── form/
│           ├── TenantRequestFormViewModel.kt
│           ├── TenantRequestFormScreen.kt
│           └── steps/
│               ├── RequestStep1Intent.kt
│               ├── RequestStep2PropertyType.kt
│               ├── RequestStep3Budget.kt
│               ├── RequestStep4WhereRadius.kt
│               ├── RequestStep5Preferences.kt
│               └── RequestStep6AboutYou.kt
└── di/
    └── RequestFormModule.kt                  ← NEW

app/src/main/res/values/strings.xml           ← Module 07 strings appended
app/src/test/java/com/rento/app/
├── domain/usecase/request/
│   ├── CreateRequestUseCaseTest.kt
│   └── UpdateRequestUseCaseTest.kt
└── presentation/request/form/
    └── TenantRequestFormViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M07-T01 | `TenantRequestFormData` domain model + `CreateRequestUseCase` + `UpdateRequestUseCase` + `SaveRequestDraftUseCase` | `domain/` | ☐ |
| M07-T02 | `TenantRequestRepositoryImpl` — implement `createRequest()` and `updateRequest()` | `data/repository/` | ☐ |
| M07-T03 | `RequestDraftDataStore` — DataStore persistence of form state | `data/local/` | ☐ |
| M07-T04 | `TenantRequestFormViewModel` — full state machine, step navigation, submit, draft | `presentation/request/form/` | ☐ |
| M07-T05 | Koin module — `RequestFormModule.kt` | `di/` | ☐ |
| M07-T06 | Nav graph — wire `request/form` and `request/form?requestId=…&mode=edit` | `RentoNavGraph.kt` | ☐ |
| M07-T07 | `TenantRequestFormScreen` shell — reuses `FormShell` from Module 06 | `TenantRequestFormScreen.kt` | ☐ |
| M07-T08 | Step 1 — intent cards (Shared Space / Full Property) | `RequestStep1Intent.kt` | ☐ |
| M07-T09 | Step 2 — property type chip grid | `RequestStep2PropertyType.kt` | ☐ |
| M07-T10 | Step 3 — budget price input | `RequestStep3Budget.kt` | ☐ |
| M07-T11 | Step 4 — area search + `RadiusCircle` slider | `RequestStep4WhereRadius.kt` | ☐ |
| M07-T12 | Step 5 — preferences chips (bedrooms, bathrooms, furnishing) | `RequestStep5Preferences.kt` | ☐ |
| M07-T13 | Step 6 — bio textarea | `RequestStep6AboutYou.kt` | ☐ |
| M07-T14 | Discard draft dialog — GlassDialog when back pressed with unsaved changes | `TenantRequestFormScreen.kt` | ☐ |
| M07-T15 | String resources | `strings.xml` | ☐ |
| M07-T16 | Unit tests | `*Test.kt` | ☐ |
| M07-T17 | Build gate | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 `TenantRequestFormData` — Domain Model

```kotlin
// domain/model/TenantRequestFormData.kt
data class TenantRequestFormData(
    // Step 1
    val intent: String = "",               // "share" | "fullRent"

    // Step 2
    val propertyType: String = "",         // "Apartment" | "House" | "Room" | "Studio" | "Hostel Bed" | "Coworking"

    // Step 3
    val budgetMax: Int = 0,

    // Step 4
    val preferredAreas: List<String> = emptyList(),   // user-typed area names
    val province: String = "",
    val city: String = "",
    val radiusKm: Int = 5,

    // Step 5
    val minBedrooms: Int = 0,             // 0 = Studio / Any
    val minBathrooms: Int = 1,
    val furnishingRequired: String = "any",  // "furnished" | "semi" | "any"

    // Step 6
    val bio: String = "",

    // Meta
    val moveInDate: String = "immediate",  // "immediate" | "within_month" | "flexible"
)
```

> `moveInDate` is collected implicitly as "immediate" in V1. A future update adds a move-in date picker step. For now, always submit with `"immediate"`.

### 4.2 `RequestFormUiState`

```kotlin
data class RequestFormUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 6,
    val formData: TenantRequestFormData = TenantRequestFormData(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitSuccess: Boolean = false,
    val isDirty: Boolean = false,           // true if any field has been modified
    val showDiscardDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val editRequestId: String? = null,

    // Step-level validation errors
    val stepError: String? = null,
)
```

### 4.3 `TenantRequestRepository` — Missing Methods to Implement

From Module 03, the repository interface is already defined. In M07 we implement the two mutating methods:

```kotlin
// Already in interface (from M03) — implement now:
suspend fun createRequest(request: TenantRequest): Result<String>      // returns new requestId
suspend fun updateRequest(requestId: String, request: TenantRequest): Result<Unit>
```

### 4.4 Koin Module

```kotlin
// di/RequestFormModule.kt
val requestFormModule = module {
    single { RequestDraftDataStore(get()) }          // DataStore<Preferences>
    factory { CreateRequestUseCase(get(), get()) }    // TenantRequestRepository + AuthRepository
    factory { UpdateRequestUseCase(get()) }           // TenantRequestRepository
    factory { SaveRequestDraftUseCase(get()) }        // RequestDraftDataStore
    viewModel { (requestId: String?, isEdit: Boolean) ->
        TenantRequestFormViewModel(get(), get(), get(), get(), requestId, isEdit)
    }
}
```

`requestFormModule` added to `RentoApplication.startKoin`.

### 4.5 Navigation Routes

```kotlin
// In HomeRoutes or a new RequestRoutes object:
const val REQUEST_FORM        = "request/form"
const val REQUEST_FORM_EDIT   = "request/form?requestId={requestId}&mode=edit"

// In RentoNavGraph.kt:
composable(route = HomeRoutes.REQUEST_FORM) {
    TenantRequestFormScreen(
        requestId   = null,
        isEditMode  = false,
        onBack      = { navController.popBackStack() },
        onSuccess   = { navController.popBackStack() },
    )
}

composable(
    route     = HomeRoutes.REQUEST_FORM_EDIT,
    arguments = listOf(
        navArgument("requestId") { type = NavType.StringType },
        navArgument("mode")      { type = NavType.StringType; defaultValue = "create" },
    ),
) { backStackEntry ->
    val requestId = backStackEntry.arguments?.getString("requestId")
    TenantRequestFormScreen(
        requestId  = requestId,
        isEditMode = true,
        onBack     = { navController.popBackStack() },
        onSuccess  = { navController.popBackStack() },
    )
}
```

---

## Task M07-T01 — Domain Models & Use Cases

### Sub-tasks
- [ ] **M07-T01-A** Create `domain/model/TenantRequestFormData.kt` as defined in section 4.1. Zero Android imports.
- [ ] **M07-T01-B** `CreateRequestUseCase`:

```kotlin
class CreateRequestUseCase(
    private val repository: TenantRequestRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(formData: TenantRequestFormData): Result<String> {
        val uid  = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        val user = authRepository.getCurrentUser()
            ?: return Result.failure(Exception("User profile not found."))

        val request = TenantRequest(
            id                 = "",           // assigned by Firestore
            uid                = uid,
            requesterName      = user.displayName ?: "Anonymous",
            intent             = formData.intent,
            propertyType       = formData.propertyType,
            budgetMax          = formData.budgetMax,
            province           = formData.province,
            city               = formData.city,
            preferredAreas     = formData.preferredAreas,
            radiusKm           = formData.radiusKm,
            minBedrooms        = formData.minBedrooms,
            minBathrooms       = formData.minBathrooms,
            furnishingRequired = formData.furnishingRequired,
            moveInDate         = formData.moveInDate,
            bio                = formData.bio,
            status             = "active",
            createdAt          = System.currentTimeMillis(),
            updatedAt          = System.currentTimeMillis(),
        )
        return repository.createRequest(request)
    }
}
```

- [ ] **M07-T01-C** `UpdateRequestUseCase`:

```kotlin
class UpdateRequestUseCase(private val repository: TenantRequestRepository) {
    suspend operator fun invoke(requestId: String, formData: TenantRequestFormData): Result<Unit> {
        // We fetch the existing request to preserve immutable fields (uid, createdAt, status)
        // then patch with new form data — repository handles the merge
        return repository.updateRequest(requestId, formData)
    }
}
```

> `TenantRequestRepository.updateRequest` signature changes to accept `TenantRequestFormData` instead of `TenantRequest` — simpler for the form's purposes. Update the interface:
```kotlin
suspend fun updateRequest(requestId: String, data: TenantRequestFormData): Result<Unit>
```

- [ ] **M07-T01-D** `SaveRequestDraftUseCase`:

```kotlin
class SaveRequestDraftUseCase(private val dataStore: RequestDraftDataStore) {
    suspend operator fun invoke(formData: TenantRequestFormData) =
        dataStore.saveDraft(formData)
}
```

- [ ] **M07-T01-E** Verify: zero Android imports in all new domain files.

---

## Task M07-T02 — `TenantRequestRepositoryImpl` — createRequest & updateRequest

**File:** `data/repository/TenantRequestRepositoryImpl.kt` (update existing from M03)

### Sub-tasks
- [ ] **M07-T02-A** Implement `createRequest`:

```kotlin
override suspend fun createRequest(request: TenantRequest): Result<String> = runCatching {
    val data = hashMapOf(
        "uid"                to request.uid,
        "requesterName"      to request.requesterName,
        "intent"             to request.intent,
        "propertyType"       to request.propertyType,
        "budgetMax"          to request.budgetMax,
        "province"           to request.province,
        "city"               to request.city,
        "preferredAreas"     to request.preferredAreas,
        "radiusKm"           to request.radiusKm,
        "minBedrooms"        to request.minBedrooms,
        "minBathrooms"       to request.minBathrooms,
        "furnishingRequired" to request.furnishingRequired,
        "moveInDate"         to request.moveInDate,
        "bio"                to request.bio,
        "status"             to request.status,
        "rejectionReason"    to null,
        "adminDeleteMessage" to null,
        "createdAt"          to FieldValue.serverTimestamp(),
        "updatedAt"          to FieldValue.serverTimestamp(),
    )
    firestore.collection("tenantRequests").add(data).await().id
}
```

- [ ] **M07-T02-B** Implement `updateRequest(requestId, data: TenantRequestFormData)`:

```kotlin
override suspend fun updateRequest(requestId: String, data: TenantRequestFormData): Result<Unit> =
    runCatching {
        val updates = mapOf(
            "intent"             to data.intent,
            "propertyType"       to data.propertyType,
            "budgetMax"          to data.budgetMax,
            "province"           to data.province,
            "city"               to data.city,
            "preferredAreas"     to data.preferredAreas,
            "radiusKm"           to data.radiusKm,
            "minBedrooms"        to data.minBedrooms,
            "minBathrooms"       to data.minBathrooms,
            "furnishingRequired" to data.furnishingRequired,
            "bio"                to data.bio,
            "updatedAt"          to FieldValue.serverTimestamp(),
        )
        firestore.collection("tenantRequests").document(requestId).update(updates).await()
    }
```

---

## Task M07-T03 — DataStore Draft Persistence

**File:** `data/local/RequestDraftDataStore.kt`

Draft is saved incrementally after every step advance, and restored on ViewModel init.

```kotlin
class RequestDraftDataStore(private val context: Context) {

    private val Context.requestDraftStore: DataStore<Preferences> by preferencesDataStore(
        name = "request_draft"
    )

    companion object {
        val KEY_INTENT          = stringPreferencesKey("intent")
        val KEY_PROPERTY_TYPE   = stringPreferencesKey("property_type")
        val KEY_BUDGET          = intPreferencesKey("budget_max")
        val KEY_AREAS           = stringPreferencesKey("preferred_areas")  // JSON array
        val KEY_PROVINCE        = stringPreferencesKey("province")
        val KEY_CITY            = stringPreferencesKey("city")
        val KEY_RADIUS          = intPreferencesKey("radius_km")
        val KEY_MIN_BEDROOMS    = intPreferencesKey("min_bedrooms")
        val KEY_MIN_BATHROOMS   = intPreferencesKey("min_bathrooms")
        val KEY_FURNISHING      = stringPreferencesKey("furnishing_required")
        val KEY_BIO             = stringPreferencesKey("bio")
        val KEY_MOVE_IN         = stringPreferencesKey("move_in_date")
    }

    suspend fun saveDraft(data: TenantRequestFormData) {
        context.requestDraftStore.edit { prefs ->
            prefs[KEY_INTENT]        = data.intent
            prefs[KEY_PROPERTY_TYPE] = data.propertyType
            prefs[KEY_BUDGET]        = data.budgetMax
            prefs[KEY_AREAS]         = Json.encodeToString(data.preferredAreas)
            prefs[KEY_PROVINCE]      = data.province
            prefs[KEY_CITY]          = data.city
            prefs[KEY_RADIUS]        = data.radiusKm
            prefs[KEY_MIN_BEDROOMS]  = data.minBedrooms
            prefs[KEY_MIN_BATHROOMS] = data.minBathrooms
            prefs[KEY_FURNISHING]    = data.furnishingRequired
            prefs[KEY_BIO]           = data.bio
            prefs[KEY_MOVE_IN]       = data.moveInDate
        }
    }

    suspend fun loadDraft(): TenantRequestFormData? {
        val prefs = context.requestDraftStore.data.first()
        val intent = prefs[KEY_INTENT] ?: return null   // no draft if intent not set
        return TenantRequestFormData(
            intent             = intent,
            propertyType       = prefs[KEY_PROPERTY_TYPE] ?: "",
            budgetMax          = prefs[KEY_BUDGET] ?: 0,
            preferredAreas     = prefs[KEY_AREAS]?.let { Json.decodeFromString(it) } ?: emptyList(),
            province           = prefs[KEY_PROVINCE] ?: "",
            city               = prefs[KEY_CITY] ?: "",
            radiusKm           = prefs[KEY_RADIUS] ?: 5,
            minBedrooms        = prefs[KEY_MIN_BEDROOMS] ?: 0,
            minBathrooms       = prefs[KEY_MIN_BATHROOMS] ?: 1,
            furnishingRequired = prefs[KEY_FURNISHING] ?: "any",
            bio                = prefs[KEY_BIO] ?: "",
            moveInDate         = prefs[KEY_MOVE_IN] ?: "immediate",
        )
    }

    suspend fun clearDraft() {
        context.requestDraftStore.edit { it.clear() }
    }
}
```

> Requires `kotlinx-serialization-json` dependency (already present if Module 06 uses it). Use `Json.encodeToString` / `Json.decodeFromString` for the `preferredAreas` list.

### Sub-tasks
- [ ] **M07-T03-A** Implement `RequestDraftDataStore` as above.
- [ ] **M07-T03-B** Confirm `kotlinx.serialization` gradle plugin enabled and `kotlinx-serialization-json` dependency present in `app/build.gradle.kts`.
- [ ] **M07-T03-C** The DataStore file name is `"request_draft"` — separate from Module 06's `"listing_draft"`.

---

## Task M07-T04 — `TenantRequestFormViewModel`

**File:** `presentation/request/form/TenantRequestFormViewModel.kt`

```kotlin
class TenantRequestFormViewModel(
    private val createRequest: CreateRequestUseCase,
    private val updateRequest: UpdateRequestUseCase,
    private val saveDraft: SaveRequestDraftUseCase,
    private val repository: TenantRequestRepository,   // for loading existing request in edit mode
    private val requestId: String?,
    private val isEditMode: Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestFormUiState(isEditMode = isEditMode))
    val uiState: StateFlow<RequestFormUiState> = _uiState.asStateFlow()

    init {
        if (isEditMode && requestId != null) {
            loadExistingRequest(requestId)
        }
        // Note: draft restoration NOT done in edit mode — edit loads live data from Firestore
    }

    // ── Step navigation ──────────────────────────────────────────────
    fun advanceStep() {
        val state = _uiState.value
        if (!validateCurrentStep(state)) return
        if (state.currentStep == state.totalSteps) {
            submitRequest()
            return
        }
        val newStep = state.currentStep + 1
        _uiState.update { it.copy(currentStep = newStep, stepError = null) }
        persistDraft()
    }

    fun goBack() {
        val state = _uiState.value
        if (state.currentStep == 1) {
            if (state.isDirty) {
                _uiState.update { it.copy(showDiscardDialog = true) }
            }
            // If not dirty, screen calls onBack() via callback — ViewModel doesn't navigate
            return
        }
        _uiState.update { it.copy(currentStep = state.currentStep - 1, stepError = null) }
    }

    // ── Field updates ────────────────────────────────────────────────
    fun setIntent(intent: String) =
        _uiState.update { it.copy(formData = it.formData.copy(intent = intent), isDirty = true, stepError = null) }

    fun setPropertyType(type: String) =
        _uiState.update { it.copy(formData = it.formData.copy(propertyType = type), isDirty = true, stepError = null) }

    fun setBudget(budget: Int) =
        _uiState.update { it.copy(formData = it.formData.copy(budgetMax = budget), isDirty = true) }

    fun setPreferredAreas(areas: List<String>) =
        _uiState.update { it.copy(formData = it.formData.copy(preferredAreas = areas), isDirty = true) }

    fun setProvince(province: String) =
        _uiState.update { it.copy(formData = it.formData.copy(province = province, city = ""), isDirty = true) }

    fun setCity(city: String) =
        _uiState.update { it.copy(formData = it.formData.copy(city = city), isDirty = true) }

    fun setRadius(km: Int) =
        _uiState.update { it.copy(formData = it.formData.copy(radiusKm = km), isDirty = true) }

    fun setMinBedrooms(beds: Int) =
        _uiState.update { it.copy(formData = it.formData.copy(minBedrooms = beds), isDirty = true) }

    fun setMinBathrooms(baths: Int) =
        _uiState.update { it.copy(formData = it.formData.copy(minBathrooms = baths), isDirty = true) }

    fun setFurnishing(furnish: String) =
        _uiState.update { it.copy(formData = it.formData.copy(furnishingRequired = furnish), isDirty = true) }

    fun setBio(bio: String) {
        if (bio.length > 500) return
        _uiState.update { it.copy(formData = it.formData.copy(bio = bio), isDirty = true) }
    }

    // ── Discard dialog ───────────────────────────────────────────────
    fun dismissDiscardDialog() = _uiState.update { it.copy(showDiscardDialog = false) }

    fun confirmDiscard() {
        viewModelScope.launch {
            saveDraft.dataStore.clearDraft()   // or inject clearDraft use case
            _uiState.update { it.copy(showDiscardDialog = false, isDirty = false) }
        }
        // Screen calls onBack() after this
    }

    // ── Submit ───────────────────────────────────────────────────────
    private fun submitRequest() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = if (isEditMode && requestId != null) {
                updateRequest(requestId, _uiState.value.formData)
            } else {
                createRequest(_uiState.value.formData).map { /* discard returned id */ }
            }
            result.fold(
                onSuccess = {
                    saveDraft.dataStore.clearDraft()
                    _uiState.update { it.copy(isLoading = false, submitSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    // ── Draft persistence ────────────────────────────────────────────
    private fun persistDraft() {
        if (isEditMode) return   // don't overwrite draft with edit-mode data
        viewModelScope.launch { saveDraft(_uiState.value.formData) }
    }

    // ── Edit mode load ───────────────────────────────────────────────
    private fun loadExistingRequest(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getRequestById(id).fold(
                onSuccess = { request ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading    = false,
                            editRequestId = id,
                            formData     = TenantRequestFormData(
                                intent             = request.intent,
                                propertyType       = request.propertyType,
                                budgetMax          = request.budgetMax,
                                preferredAreas     = request.preferredAreas,
                                province           = request.province,
                                city               = request.city,
                                radiusKm           = request.radiusKm,
                                minBedrooms        = request.minBedrooms,
                                minBathrooms       = request.minBathrooms,
                                furnishingRequired = request.furnishingRequired,
                                bio                = request.bio,
                                moveInDate         = request.moveInDate,
                            ),
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    // ── Validation ───────────────────────────────────────────────────
    private fun validateCurrentStep(state: RequestFormUiState): Boolean {
        val data = state.formData
        val error = when (state.currentStep) {
            1 -> if (data.intent.isBlank()) "Please select what you need." else null
            2 -> if (data.propertyType.isBlank()) "Please select a property type." else null
            3 -> if (data.budgetMax <= 0) "Please enter your maximum budget." else null
            4 -> when {
                data.city.isBlank()             -> "Please select a city."
                data.preferredAreas.isEmpty()   -> "Please enter at least one preferred area."
                else                            -> null
            }
            5 -> null   // preferences are optional — all have defaults
            6 -> null   // bio is optional
            else -> null
        }
        return if (error != null) {
            _uiState.update { it.copy(stepError = error) }
            false
        } else true
    }
}
```

> **Note on `saveDraft.dataStore.clearDraft()`:** The `confirmDiscard()` function references `saveDraft.dataStore` which is a simplification. In the actual implementation, inject a separate `ClearRequestDraftUseCase` or expose `clearDraft()` as a ViewModel function that directly calls `RequestDraftDataStore.clearDraft()` — inject `RequestDraftDataStore` directly into the ViewModel for clearing.

### Sub-tasks
- [ ] **M07-T04-A** Implement `TenantRequestFormViewModel` with the corrected `confirmDiscard` (inject `RequestDraftDataStore` directly for clear).
- [ ] **M07-T04-B** `init` block: if `!isEditMode`, attempt to restore draft from `RequestDraftDataStore` — if a draft exists and `isDirty` was previously set, restore `formData` and navigate to the last completed step.
- [ ] **M07-T04-C** `collectAsStateWithLifecycle()` used in all composables consuming this ViewModel.

---

## Task M07-T05 — Koin Module

**File:** `di/RequestFormModule.kt`

### Sub-tasks
- [ ] **M07-T05-A** Create `requestFormModule` per section 4.4.
- [ ] **M07-T05-B** Add to `RentoApplication.startKoin { modules(…, requestFormModule) }`.
- [ ] **M07-T05-C** `RequestDraftDataStore` bound as `single` — single instance per app lifecycle.

---

## Task M07-T06 — Navigation Wiring

### Sub-tasks
- [ ] **M07-T06-A** Wire both routes per section 4.5 in `RentoNavGraph.kt`.
- [ ] **M07-T06-B** The AddOverlaySheet's "I'm Looking" card in Module 03 navigates to `HomeRoutes.REQUEST_FORM` — confirm this navigation is already wired from M03.
- [ ] **M07-T06-C** The Request Detail screen's "Edit Request" button navigates to `HomeRoutes.REQUEST_FORM_EDIT` with `requestId` — confirm Module 05 nav callback is correct.

---

## Task M07-T07 — Form Shell: `TenantRequestFormScreen`

**File:** `presentation/request/form/TenantRequestFormScreen.kt`

The shell is **identical** to Module 06's `ListingFormScreen` shell — it reuses the `FormShell` composable extracted in Module 06. Only the step content and button labels differ.

```kotlin
@Composable
fun TenantRequestFormScreen(
    requestId: String?,
    isEditMode: Boolean,
    viewModel: TenantRequestFormViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // On submit success → navigate away
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) onSuccess()
    }

    // Back press handling
    BackHandler {
        viewModel.goBack()
        if (uiState.currentStep == 1 && !uiState.isDirty) onBack()
    }

    FormShell(
        currentStep      = uiState.currentStep,
        totalSteps       = uiState.totalSteps,
        onBack           = {
            viewModel.goBack()
            if (uiState.currentStep == 1 && !uiState.isDirty) onBack()
        },
        onSaveDraft      = { viewModel.persistDraftPublic() },
        showSaveDraft    = !isEditMode,
        continueLabel    = if (uiState.currentStep == uiState.totalSteps) "🚀 Post Request" else "Continue →",
        onContinue       = { viewModel.advanceStep() },
        isContinueLoading = uiState.isLoading,
        stepError        = uiState.stepError,
    ) {
        // Step content — slides left on advance, right on back
        AnimatedContent(
            targetState   = uiState.currentStep,
            transitionSpec = {
                val isForward = targetState > initialState
                slideInHorizontally(
                    initialOffsetX = { if (isForward) it else -it }
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { if (isForward) -it else it }
                )
            },
            label = "RequestFormStep",
        ) { step ->
            when (step) {
                1 -> RequestStep1Intent(
                    selectedIntent = uiState.formData.intent,
                    onSelect       = { viewModel.setIntent(it) },
                )
                2 -> RequestStep2PropertyType(
                    selectedType = uiState.formData.propertyType,
                    onSelect     = { viewModel.setPropertyType(it) },
                )
                3 -> RequestStep3Budget(
                    budget   = uiState.formData.budgetMax,
                    onChange = { viewModel.setBudget(it) },
                )
                4 -> RequestStep4WhereRadius(
                    preferredAreas = uiState.formData.preferredAreas,
                    province       = uiState.formData.province,
                    city           = uiState.formData.city,
                    radiusKm       = uiState.formData.radiusKm,
                    onAreasChange  = { viewModel.setPreferredAreas(it) },
                    onProvinceChange = { viewModel.setProvince(it) },
                    onCityChange   = { viewModel.setCity(it) },
                    onRadiusChange = { viewModel.setRadius(it) },
                )
                5 -> RequestStep5Preferences(
                    minBedrooms        = uiState.formData.minBedrooms,
                    minBathrooms       = uiState.formData.minBathrooms,
                    furnishingRequired = uiState.formData.furnishingRequired,
                    onBedroomsChange   = { viewModel.setMinBedrooms(it) },
                    onBathroomsChange  = { viewModel.setMinBathrooms(it) },
                    onFurnishingChange = { viewModel.setFurnishing(it) },
                )
                6 -> RequestStep6AboutYou(
                    bio      = uiState.formData.bio,
                    onChange = { viewModel.setBio(it) },
                )
            }
        }
    }

    // Discard dialog
    if (uiState.showDiscardDialog) {
        DiscardRequestDraftDialog(
            onConfirm = { viewModel.confirmDiscard(); onBack() },
            onDismiss = { viewModel.dismissDiscardDialog() },
        )
    }
}
```

> **`FormShell`** is the shared composable extracted from Module 06. Confirm it exposes all parameters listed above. If `FormShell` is not yet extracted in Module 06, extract it now and document in code review.

> **`persistDraftPublic()`** — add a public wrapper in the ViewModel:
```kotlin
fun persistDraftPublic() { persistDraft() }
```

### Sub-tasks
- [ ] **M07-T07-A** Implement `TenantRequestFormScreen` shell.
- [ ] **M07-T07-B** Confirm `FormShell` from Module 06 exposes: `currentStep`, `totalSteps`, `onBack`, `onSaveDraft`, `showSaveDraft`, `continueLabel`, `onContinue`, `isContinueLoading`, `stepError`, and a `content: @Composable () -> Unit` slot.
- [ ] **M07-T07-C** `AnimatedContent` transition: slide left on advance, slide right on back — matches Module 06 exactly.
- [ ] **M07-T07-D** `BackHandler` — step > 1: call `goBack()`. Step 1 + isDirty: show discard dialog. Step 1 + !isDirty: call `onBack()`.

---

## Task M07-T08 — Step 1: What Do You Need?

**File:** `presentation/request/form/steps/RequestStep1Intent.kt`

### Exact Specification

Step title: **"What do you need?"**
Step subtitle: *(none specified — omit subtitle or use "Select what you're looking for")*

```
Column(fillMaxWidth, verticalArrangement=spacedBy(12.dp)):

  2 intent cards (same design as Listing Form Step 1 cards from Module 06):

  ── "Shared Space" card ──────────────────────────────────────────────
  IntentCard(
    icon     = RentoIcons.Users,     // Users icon
    title    = "Shared Space",
    subtitle = "Looking to share a room or apartment",
    selected = selectedIntent == "share",
    onClick  = { onSelect("share") },
  )

  ── "Full Property" card ─────────────────────────────────────────────
  IntentCard(
    icon     = RentoIcons.Home,      // House icon
    title    = "Full Property",
    subtitle = "Looking for an entire place",
    selected = selectedIntent == "fullRent",
    onClick  = { onSelect("fullRent") },
  )
```

**`IntentCard` design** (from Module 06 / WelcomeScreen):
```
Row(
  fillMaxWidth, CenterVertically,
  background = if selected RentoColors.primaryTint else RentoColors.bg2,
  border     = 1.5.dp (if selected RentoColors.primary else RentoColors.border2),
  shape      = RoundedCornerShape(18.dp),
  padding    = 18.dp,
):
  // Icon container
  Box(58.dp circle, background = if selected RentoColors.primary else RentoColors.bg3):
    Icon(icon, 28.dp, tint = if selected Color.White else RentoColors.t1)
  Spacer(16.dp)
  Column(Modifier.weight(1f)):
    Text(title, 16sp Bold, if selected RentoColors.primary else RentoColors.t0)
    Spacer(4.dp)
    Text(subtitle, 13sp, RentoColors.t2)
  // Checkmark (only when selected)
  if selected:
    Icon(RentoIcons.Check, 22.dp, RentoColors.primary)
```

> `IntentCard` is extracted from Module 06 as a shared composable in `presentation/shared/components/IntentCard.kt` — reuse it here. If not yet extracted, extract it now.

### Sub-tasks
- [ ] **M07-T08-A** Implement `RequestStep1Intent` using shared `IntentCard`.
- [ ] **M07-T08-B** `@Preview` — both states (share selected, fullRent selected, none selected), dark + light.

---

## Task M07-T09 — Step 2: Property Type

**File:** `presentation/request/form/steps/RequestStep2PropertyType.kt`

### Exact Specification

Step title: **"Property type"**
Step subtitle: *(none)*

6 property type options (fewer than Module 06's 12 — seeker-focused):

```
Apartment · House · Room · Studio · Hostel Bed · Coworking
```

Same chip-with-icon grid as Module 06 Step 2 — single-select, wrapping:

```
FlowRow(horizontalGap=10.dp, verticalGap=10.dp):
  propertyTypes.forEach { type ->
    PropertyTypeChip(
      label    = type.label,
      icon     = type.icon,
      selected = selectedType == type.key,
      onClick  = { onSelect(type.key) },
    )
  }
```

**Property type definitions:**

```kotlin
data class PropertyTypeOption(val key: String, val label: String, val icon: ImageVector)

val requestPropertyTypes = listOf(
    PropertyTypeOption("Apartment",  "Apartment",   RentoIcons.Building),
    PropertyTypeOption("House",      "House",        RentoIcons.Home),
    PropertyTypeOption("Room",       "Room",         RentoIcons.Grid),
    PropertyTypeOption("Studio",     "Studio",       RentoIcons.Layers),
    PropertyTypeOption("HostelBed",  "Hostel Bed",   RentoIcons.Users),
    PropertyTypeOption("Coworking",  "Coworking",    RentoIcons.Monitor),
)
```

**`PropertyTypeChip` design** (from Module 06 — reuse):
```
Row(
  CenterVertically, 8.dp gap,
  background = if selected RentoColors.primaryTint else RentoColors.bg2,
  border     = 1.5.dp (if selected RentoColors.primary else RentoColors.border),
  shape      = RoundedCornerShape(12.dp),
  padding    = 10.dp V / 14.dp H,
):
  Icon(icon, 18.dp, if selected RentoColors.primary else RentoColors.t1)
  Text(label, 14sp, if selected RentoColors.primary else RentoColors.t1)
```

### Sub-tasks
- [ ] **M07-T09-A** Implement `RequestStep2PropertyType`. Reuse `PropertyTypeChip` from `shared/components/` (if extracted in M06).
- [ ] **M07-T09-B** `@Preview` — 2 selected, none selected, dark + light.

---

## Task M07-T10 — Step 3: Your Budget

**File:** `presentation/request/form/steps/RequestStep3Budget.kt`

### Exact Specification

Step title: **"Your Budget"**
Step subtitle: *(none)*

```
Column(fillMaxWidth, CenterHorizontally):

  // Price input card
  Box(
    fillMaxWidth,
    background = RentoColors.bg2,
    border     = 1.5.dp RentoColors.border,
    shape      = RoundedCornerShape(22.dp),
    padding    = 22.dp,
  ):
    Column:
      SectionLabel("MAX MONTHLY RENT")
      Spacer(10.dp)
      Row(CenterVertically):
        Text("PKR", 28sp Bold, RentoColors.primary)
        Spacer(8.dp)
        // Numeric input — no keyboard prefix shown, raw number entry
        BasicTextField(
          value         = if (budget == 0) "" else budget.toString(),
          onValueChange = { raw ->
            val cleaned = raw.filter { it.isDigit() }
            val value   = cleaned.toLongOrNull()?.coerceAtMost(9_999_999L)?.toInt() ?: 0
            onChange(value)
          },
          textStyle     = TextStyle(
            fontSize   = 34.sp,
            fontWeight = FontWeight.Bold,
            color      = RentoColors.t0,
          ),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine      = true,
          modifier        = Modifier.fillMaxWidth(),
          decorationBox   = { inner ->
            if (budget == 0) Text("e.g. 30000", 34sp, RentoColors.t3)
            inner()
          },
        )
      Divider(1.dp, RentoColors.border2, Modifier.padding(vertical = 14.dp))
      Text(
        "Monthly budget for rent",
        12sp, RentoColors.t2,
      )

  Spacer(16.dp)

  // Optional: formatted preview below the input
  if (budget > 0):
    Text(
      "Up to PKR ${formatBudget(budget)} per month",
      14sp, RentoColors.t1, centred,
    )
```

```kotlin
private fun formatBudget(budget: Int): String = when {
    budget >= 100_000 -> "${budget / 1_000}k"
    budget >= 10_000  -> "${budget / 1_000}k"
    else              -> NumberFormat.getNumberInstance(Locale.US).format(budget)
}
```

### Sub-tasks
- [ ] **M07-T10-A** Implement `RequestStep3Budget`.
- [ ] **M07-T10-B** Max input value capped at `9_999_999` (≈10M PKR).
- [ ] **M07-T10-C** Empty field (budget=0) shows placeholder — not "0".
- [ ] **M07-T10-D** `@Preview` — empty, filled (25000), filled (150000), dark + light.

---

## Task M07-T11 — Step 4: Where & Radius

**File:** `presentation/request/form/steps/RequestStep4WhereRadius.kt`

### Exact Specification

Step title: **"Where & Radius"**
Step subtitle: *(none)*

```
Column(fillMaxWidth, verticalArrangement=spacedBy(20.dp)):

  ─── AREA SEARCH INPUT ─────────────────────────────────────────────
  // Boxed input for area name entry
  Box(
    fillMaxWidth,
    background = RentoColors.bg2,
    border     = 1.5.dp RentoColors.border2,
    shape      = RoundedCornerShape(16.dp),
    padding    = 14.dp,
  ):
    Row(CenterVertically, spacedBy(10.dp)):
      Icon(RentoIcons.Search, 18.dp, RentoColors.t2)
      BasicTextField(
        value         = currentAreaInput,   // local state for the text field
        onValueChange = { currentAreaInput = it },
        textStyle     = bodyM + RentoColors.t0,
        singleLine    = true,
        modifier      = Modifier.weight(1f),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
          onDone = {
            if (currentAreaInput.isNotBlank()) {
              onAreasChange(preferredAreas + currentAreaInput.trim())
              currentAreaInput = ""
            }
          },
        ),
        decorationBox = { inner ->
          if (currentAreaInput.isEmpty()) Text("e.g. DHA Phase 6", bodyM, RentoColors.t3)
          inner()
        },
      )

  ─── ADDED AREA CHIPS ──────────────────────────────────────────────
  // Show chips for added areas, with × to remove
  if preferredAreas.isNotEmpty():
    FlowRow(horizontalGap=8.dp, verticalGap=8.dp):
      preferredAreas.forEach { area ->
        Row(
          CenterVertically, spacedBy(6.dp),
          background = RentoColors.primaryTint,
          border     = 1.dp RentoColors.primaryRing,
          shape      = RoundedCornerShape(100.dp),
          padding    = 6.dp V / 12.dp H,
        ):
          Text(area, 13sp Bold, RentoColors.primary)
          Icon(
            RentoIcons.X, 14.dp, RentoColors.primary,
            modifier = Modifier.clickable { onAreasChange(preferredAreas - area) }
          )

  ─── PROVINCE + CITY PICKERS ───────────────────────────────────────
  // Province dropdown (re-use CityPickerSheet logic from M03 or inline a simple dropdown)
  SectionLabel("PROVINCE")
  Spacer(6.dp)
  ProvinceDropdown(
    selectedProvince = province,
    onSelect         = onProvinceChange,
  )

  SectionLabel("CITY")
  Spacer(6.dp)
  CityDropdown(
    selectedProvince = province,
    selectedCity     = city,
    onSelect         = onCityChange,
  )

  ─── RADIUS SECTION LABEL ─────────────────────────────────────────
  SectionLabel("SEARCH RADIUS: ${radiusKm} km")

  ─── MAP PREVIEW ──────────────────────────────────────────────────
  Box(
    fillMaxWidth, height=200.dp,
    clip = RoundedCornerShape(18.dp),
    border = 1.5.dp RentoColors.border,
  ):
    MapBackground(Modifier.fillMaxSize()):
      RadiusCircle(
        radiusKm = radiusKm,
        animate  = true,            // animates as slider moves
        modifier = Modifier.align(Alignment.Center),
      )

  ─── RADIUS SLIDER ────────────────────────────────────────────────
  Slider(
    value         = radiusKm.toFloat(),
    onValueChange = { onRadiusChange(it.roundToInt()) },
    valueRange    = 1f..15f,
    steps         = 13,              // 1km increments: positions 1,2,...,15 = 14 gaps, 13 internal steps
    modifier      = Modifier.fillMaxWidth(),
    colors        = SliderDefaults.colors(
      thumbColor        = RentoColors.primary,
      activeTrackColor  = RentoColors.primary,
      inactiveTrackColor = RentoColors.bg4,
    ),
    thumb = {
      // Custom thumb: 20dp circle, DarkPri fill, 10dp DarkPriR glow shadow
      Box(
        modifier = Modifier
          .size(20.dp)
          .shadow(elevation=10.dp, shape=CircleShape, spotColor=RentoColors.primaryRing)
          .background(RentoColors.primary, CircleShape),
      )
    },
  )
```

**Province / City Dropdowns:**

Use `ExposedDropdownMenuBox` from Material3 or a simple `Box` + `DropdownMenu`. The province list and city map come from `PakistanCities.map` from Module 03.

```kotlin
@Composable
private fun ProvinceDropdown(selectedProvince: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        // Tappable field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RentoColors.bg2, RoundedCornerShape(14.dp))
                .border(1.5.dp, RentoColors.border2, RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = if (selectedProvince.isBlank()) "Select province" else selectedProvince,
                style = bodyM,
                color = if (selectedProvince.isBlank()) RentoColors.t3 else RentoColors.t0,
            )
            Icon(if (expanded) RentoIcons.ChevronUp else RentoIcons.ChevronDown,
                 null, tint = RentoColors.t2, modifier = Modifier.size(18.dp))
        }
        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(RentoColors.bg1),
        ) {
            PakistanCities.map.keys.forEach { province ->
                DropdownMenuItem(
                    text    = { Text(province, bodyM, color = RentoColors.t0) },
                    onClick = { onSelect(province); expanded = false },
                )
            }
        }
    }
}

// CityDropdown — same pattern, filters by selectedProvince
@Composable
private fun CityDropdown(selectedProvince: String, selectedCity: String, onSelect: (String) -> Unit) {
    val cities = PakistanCities.map[selectedProvince] ?: emptyList()
    var expanded by remember { mutableStateOf(false) }
    val isEnabled = selectedProvince.isNotBlank()
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isEnabled) RentoColors.bg2 else RentoColors.bg3,
                    RoundedCornerShape(14.dp),
                )
                .border(1.5.dp, RentoColors.border2, RoundedCornerShape(14.dp))
                .then(if (isEnabled) Modifier.clickable { expanded = true } else Modifier)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = if (selectedCity.isBlank()) "Select city" else selectedCity,
                style = bodyM,
                color = if (selectedCity.isBlank()) RentoColors.t3 else RentoColors.t0,
            )
            Icon(if (expanded) RentoIcons.ChevronUp else RentoIcons.ChevronDown,
                 null, tint = if (isEnabled) RentoColors.t2 else RentoColors.t3,
                 modifier = Modifier.size(18.dp))
        }
        if (isEnabled) {
            DropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false },
                modifier         = Modifier.background(RentoColors.bg1),
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text    = { Text(city, bodyM, color = RentoColors.t0) },
                        onClick = { onSelect(city); expanded = false },
                    )
                }
            }
        }
    }
}
```

### Sub-tasks
- [ ] **M07-T11-A** Implement `RequestStep4WhereRadius`.
- [ ] **M07-T11-B** Area input — local `var currentAreaInput by remember { mutableStateOf("") }`. Pressing IME Done adds the area to the list and clears the field.
- [ ] **M07-T11-C** `RadiusCircle(animate = true)` — the circle animates (`animateDpAsState`) as the slider value changes in real time.
- [ ] **M07-T11-D** `Slider.steps = 13` — this gives integer km values 1–15 (14 intervals, 13 interior stops). Verify: `steps` in Compose `Slider` = number of discrete values minus 2 (exclusive of endpoints). So for 1–15 with 1km increment = 15 values = `steps = 13`. Confirm this against `Slider` API docs.
- [ ] **M07-T11-E** City dropdown is disabled (and shows lighter background) until a province is selected.
- [ ] **M07-T11-F** `PakistanCities.map` from Module 03 `data/local/PakistanCities.kt` — import it directly.
- [ ] **M07-T11-G** `@Preview` — empty state, 2 areas added, radius=8km, dark + light.

---

## Task M07-T12 — Step 5: Preferences

**File:** `presentation/request/form/steps/RequestStep5Preferences.kt`

### Exact Specification

Step title: **"Preferences"**
Step subtitle: *(none)*

```
Column(fillMaxWidth, verticalArrangement=spacedBy(24.dp)):

  ─── MINIMUM BEDROOMS ──────────────────────────────────────────────
  Column:
    SectionLabel("MINIMUM BEDROOMS")
    Spacer(10.dp)
    Row(horizontalArrangement=spacedBy(8.dp)):
      // Options: Studio (0), 1+ (1), 2+ (2), 3+ (3)
      BedroomChip(label="Studio", value=0, selected=minBedrooms==0, onClick={onBedroomsChange(0)})
      BedroomChip(label="1+",     value=1, selected=minBedrooms==1, onClick={onBedroomsChange(1)})
      BedroomChip(label="2+",     value=2, selected=minBedrooms==2, onClick={onBedroomsChange(2)})
      BedroomChip(label="3+",     value=3, selected=minBedrooms==3, onClick={onBedroomsChange(3)})

  ─── MINIMUM BATHROOMS ─────────────────────────────────────────────
  Column:
    SectionLabel("MINIMUM BATHROOMS")
    Spacer(10.dp)
    Row(horizontalArrangement=spacedBy(8.dp)):
      // Options: 1+ (1), 2+ (2), 3+ (3)
      BathroomChip(label="1+", value=1, selected=minBathrooms==1, onClick={onBathroomsChange(1)})
      BathroomChip(label="2+", value=2, selected=minBathrooms==2, onClick={onBathroomsChange(2)})
      BathroomChip(label="3+", value=3, selected=minBathrooms==3, onClick={onBathroomsChange(3)})

  ─── FURNISHING REQUIRED ───────────────────────────────────────────
  Column:
    SectionLabel("FURNISHING")
    Spacer(10.dp)
    Row(horizontalArrangement=spacedBy(8.dp)):
      FurnishChip(
        label    = "Furnished",
        icon     = RentoIcons.Check,
        value    = "furnished",
        selected = furnishingRequired == "furnished",
        onClick  = { onFurnishingChange("furnished") },
      )
      FurnishChip(
        label    = "Semi",
        icon     = RentoIcons.Check,
        value    = "semi",
        selected = furnishingRequired == "semi",
        onClick  = { onFurnishingChange("semi") },
      )
      FurnishChip(
        label    = "Any",
        icon     = RentoIcons.Star,
        value    = "any",
        selected = furnishingRequired == "any",
        onClick  = { onFurnishingChange("any") },
      )
```

**Preference chip design** (same style as all form chips):
```
Row(
  CenterVertically, spacedBy(6.dp),
  background = if selected RentoColors.primaryTint else RentoColors.bg2,
  border     = 1.5.dp (if selected RentoColors.primary else RentoColors.border),
  shape      = RoundedCornerShape(12.dp),
  padding    = 10.dp V / 14.dp H,
):
  Icon(icon?, 16.dp, if selected RentoColors.primary else RentoColors.t1)
  Text(label, 14sp, if selected RentoColors.primary else RentoColors.t1)
```

> `BedroomChip` and `BathroomChip` use `RentoIcons.Bed` / `RentoIcons.Bath` as leading icon. `FurnishChip` uses the icon passed in the parameter.

### Sub-tasks
- [ ] **M07-T12-A** Implement `RequestStep5Preferences` with all three preference groups.
- [ ] **M07-T12-B** Default selections: Studio (0 bedrooms), 1+ bathrooms, Any furnishing.
- [ ] **M07-T12-C** `@Preview` — various combinations, dark + light.

---

## Task M07-T13 — Step 6: About You

**File:** `presentation/request/form/steps/RequestStep6AboutYou.kt`

### Exact Specification

Step title: **"About You"**
Step subtitle: *(none)*

```
Column(fillMaxWidth):

  // Textarea container
  Box(
    fillMaxWidth, minHeight=140.dp,
    background = RentoColors.bg2,
    border     = 1.5.dp RentoColors.border,
    shape      = RoundedCornerShape(18.dp),
    padding    = 16.dp,
  ):
    BasicTextField(
      value         = bio,
      onValueChange = { if (it.length <= 500) onChange(it) },
      textStyle     = TextStyle(
        fontSize    = 14.sp,
        lineHeight  = 14.sp * 1.72f,
        color       = RentoColors.t0,
      ),
      minLines  = 5,
      modifier  = Modifier.fillMaxWidth().defaultMinSize(minHeight = 140.dp),
      decorationBox = { inner ->
        if (bio.isEmpty()) {
          Text(
            "Tell hosts a bit about yourself, your routine, and exactly what you are looking for...",
            style = TextStyle(fontSize=14.sp, color=RentoColors.t3, lineHeight=24.sp),
          )
        }
        inner()
      },
    )

  Spacer(8.dp)

  // Character counter
  Text(
    "${bio.length} / 500",
    style = 12sp,
    color = if (bio.length >= 480) RentoColors.accent else RentoColors.t3,
    modifier = Modifier.align(Alignment.End),
    textAlign = TextAlign.End,
  )

  Spacer(16.dp)

  // Helper hint
  Text(
    "💡 A detailed description helps hosts decide faster.",
    style = 13sp,
    color = RentoColors.t2,
    textAlign = TextAlign.Center,
    modifier = Modifier.fillMaxWidth(),
  )
```

### Sub-tasks
- [ ] **M07-T13-A** Implement `RequestStep6AboutYou`.
- [ ] **M07-T13-B** Character counter turns accent colour (warning) at 480+ characters.
- [ ] **M07-T13-C** `@Preview` — empty, partially filled (200 chars), near limit (490 chars), dark + light.

---

## Task M07-T14 — Discard Draft Dialog

Implemented inline in `TenantRequestFormScreen.kt`.

### Exact Specification

Triggered when user presses back on Step 1 with `isDirty = true`.

Uses `GlassDialog` from Module 01:

```
Icon: RentoIcons.AlertCircle in RentoColors.accentTint circle (48dp)
Title: "Discard Request?"
Body: "You'll lose all the information you've entered. This cannot be undone."
Buttons (column, 10dp gap):
  GhostButton("Keep Editing", fillMaxWidth, onClick=onDismiss)
  Button("Discard", fillMaxWidth,
    colors = ButtonDefaults.buttonColors(containerColor = RentoColors.accent),
    onClick = onConfirm,
  )
```

> Uses accent colour (gold/yellow), not red — this is data loss but not a destructive server-side action.

### Sub-tasks
- [ ] **M07-T14-A** Implement `DiscardRequestDraftDialog` as a private composable inside `TenantRequestFormScreen.kt`.
- [ ] **M07-T14-B** `BackHandler` blocked while dialog is showing.

---

## Task M07-T15 — String Resources

Append to `res/values/strings.xml`:

```xml
<!-- ─── Tenant Request Form ─────────────────────────────────────────────── -->
<string name="req_form_continue">Continue →</string>
<string name="req_form_post">🚀 Post Request</string>
<string name="req_form_save_draft">Save as Draft</string>
<string name="req_form_save_draft_header">Save Draft</string>

<!-- Step 1 -->
<string name="req_step1_title">What do you need?</string>
<string name="req_intent_shared_title">Shared Space</string>
<string name="req_intent_shared_sub">Looking to share a room or apartment</string>
<string name="req_intent_full_title">Full Property</string>
<string name="req_intent_full_sub">Looking for an entire place</string>

<!-- Step 2 -->
<string name="req_step2_title">Property type</string>

<!-- Step 3 -->
<string name="req_step3_title">Your Budget</string>
<string name="req_budget_label">MAX MONTHLY RENT</string>
<string name="req_budget_placeholder">e.g. 30000</string>
<string name="req_budget_helper">Monthly budget for rent</string>
<string name="req_budget_preview">Up to PKR %1$s per month</string>

<!-- Step 4 -->
<string name="req_step4_title">Where &amp; Radius</string>
<string name="req_area_placeholder">e.g. DHA Phase 6</string>
<string name="req_province_label">PROVINCE</string>
<string name="req_city_label">CITY</string>
<string name="req_province_hint">Select province</string>
<string name="req_city_hint">Select city</string>
<string name="req_radius_label">SEARCH RADIUS: %1$d km</string>

<!-- Step 5 -->
<string name="req_step5_title">Preferences</string>
<string name="req_pref_bedrooms">MINIMUM BEDROOMS</string>
<string name="req_pref_bathrooms">MINIMUM BATHROOMS</string>
<string name="req_pref_furnish">FURNISHING</string>
<string name="req_bed_studio">Studio</string>
<string name="req_furnish_furnished">Furnished</string>
<string name="req_furnish_semi">Semi</string>
<string name="req_furnish_any">Any</string>

<!-- Step 6 -->
<string name="req_step6_title">About You</string>
<string name="req_bio_placeholder">Tell hosts a bit about yourself, your routine, and exactly what you are looking for…</string>
<string name="req_bio_counter">%1$d / 500</string>
<string name="req_bio_hint">💡 A detailed description helps hosts decide faster.</string>

<!-- Discard dialog -->
<string name="req_discard_title">Discard Request?</string>
<string name="req_discard_body">You\'ll lose all the information you\'ve entered. This cannot be undone.</string>
<string name="req_discard_keep">Keep Editing</string>
<string name="req_discard_confirm">Discard</string>
```

---

## Task M07-T16 — Unit Tests

### `TenantRequestFormViewModelTest.kt`

```
initialState_isStep1_emptyFormData
setIntent_updatesIntentAndMarksDirty
setPropertyType_updatesPropertyType
setBudget_updatesBudget
setBio_respectsMaxLength500
setBio_doesNotExceedLimit
setRadius_updatesRadiusKm
setPreferredAreas_updatesAreas
setProvince_clearsCityWhenChanged
advanceStep_step1_invalidIntent_doesNotAdvance
advanceStep_step1_validIntent_advancesToStep2
advanceStep_step3_zeroBudget_doesNotAdvance
advanceStep_step4_noCity_doesNotAdvance
advanceStep_step4_noArea_doesNotAdvance
advanceStep_step5_alwaysAdvances
advanceStep_step6_isLastStep_callsSubmit
submitRequest_success_setsSubmitSuccess
submitRequest_failure_setsError
goBack_step1_dirty_showsDiscardDialog
goBack_step1_notDirty_doesNotShowDialog
goBack_step2_returnsToStep1
confirmDiscard_clearsDialogAndDirtyFlag
editMode_loadsExistingRequest
editMode_doesNotPersistDraft
```

### `CreateRequestUseCaseTest.kt`

```
invoke_authenticated_callsCreateRequestWithCorrectData
invoke_unauthenticated_returnsFailure
invoke_noUserProfile_returnsFailure
```

### `UpdateRequestUseCaseTest.kt`

```
invoke_callsRepositoryWithCorrectData
invoke_propagatesError
```

### Sub-tasks
- [ ] **M07-T16-A** Implement all tests using MockK + Turbine + `StandardTestDispatcher`.
- [ ] **M07-T16-B** `./gradlew test` → all pass. Paste output.
- [ ] **M07-T16-C** `./gradlew koverReport` → ≥ 80% on `presentation.request.form` + `domain.usecase.request`. Paste summary.

---

## Task M07-T17 — Build Gate

- [ ] **M07-T17-A** `./gradlew lint` → zero new warnings.
- [ ] **M07-T17-B** `./gradlew detekt` → zero violations.
- [ ] **M07-T17-C** `./gradlew assembleDebug` → `BUILD SUCCESSFUL`. Paste output.
- [ ] **M07-T17-D** `./gradlew test` → all pass. Paste output.
- [ ] **M07-T17-E** `./gradlew koverReport` → ≥ 80%. Paste summary.
- [ ] **M07-T17-F** Update `ANDROID_PROGRESS.md`.
- [ ] **M07-T17-G** Create `CODE_REVIEW_MODULE_07.md`.

---

## 22. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Entry from AddOverlay "I'm Looking" | `navController.navigate(HomeRoutes.REQUEST_FORM)` | ☐ |
| Shell renders ProgressStepBar (6 steps) | `FormShell(totalSteps=6)` | ☐ |
| Step title animates in (su / slide-up animation) | `FormShell` step title animation | ☐ |
| Step transition — slides left on advance | `AnimatedContent` with `slideInHorizontally` | ☐ |
| Step transition — slides right on back | `AnimatedContent` inverse | ☐ |
| Step 1 — select "Shared Space" | card selected + `intent = "share"` | ☐ |
| Step 1 — select "Full Property" | card selected + `intent = "fullRent"` | ☐ |
| Step 1 — Continue without selection — blocked with error | `validateCurrentStep` | ☐ |
| Step 2 — select a property type chip | chip toggles selected state | ☐ |
| Step 2 — Continue without selection — blocked | validation | ☐ |
| Step 3 — enter budget via numeric keyboard | `BasicTextField` numeric only | ☐ |
| Step 3 — formatted preview updates as typed | `formatBudget()` shown below input | ☐ |
| Step 3 — Continue with 0 budget — blocked | validation | ☐ |
| Step 4 — type area + press Done — chip appears | area added to list | ☐ |
| Step 4 — tap × on area chip — removes it | area removed from list | ☐ |
| Step 4 — select province — city dropdown enables | `isEnabled = province.isNotBlank()` | ☐ |
| Step 4 — city dropdown shows filtered cities | `PakistanCities.map[province]` | ☐ |
| Step 4 — move radius slider — RadiusCircle animates | `animateDpAsState` in `RadiusCircle` | ☐ |
| Step 4 — SectionLabel shows current km value | `"SEARCH RADIUS: ${radiusKm} km"` | ☐ |
| Step 4 — Continue with no city — blocked | validation | ☐ |
| Step 4 — Continue with no area — blocked | validation | ☐ |
| Step 5 — select bedroom option | single-select chips | ☐ |
| Step 5 — select bathroom option | single-select chips | ☐ |
| Step 5 — select furnishing | single-select chips | ☐ |
| Step 5 — Continue always passes (optional) | no validation on step 5 | ☐ |
| Step 6 — type bio | `BasicTextField` multiline | ☐ |
| Step 6 — char counter updates | `"${bio.length} / 500"` | ☐ |
| Step 6 — counter turns accent at 480+ chars | `if (bio.length >= 480) accent` | ☐ |
| Step 6 — cannot exceed 500 chars | `if (it.length <= 500)` guard | ☐ |
| Step 6 — "🚀 Post Request" button label | last step CTA label change | ☐ |
| Back on Step 1 + dirty — discard dialog shown | `isDirty = true` + `goBack()` | ☐ |
| Discard dialog — "Keep Editing" dismisses dialog | `onDismiss()` | ☐ |
| Discard dialog — "Discard" clears draft + navigates back | `confirmDiscard() + onBack()` | ☐ |
| Back on Step 1 + not dirty — navigates back | `onBack()` directly | ☐ |
| Back on Step 2+ — returns to previous step | `goBack()` | ☐ |
| "Save Draft" header link — persists draft | `persistDraftPublic()` | ☐ |
| Submit success — navigates away | `onSuccess()` | ☐ |
| Submit failure — error shown (snackbar or inline) | `uiState.error != null` | ☐ |
| Edit mode — form pre-filled with existing data | `loadExistingRequest()` | ☐ |
| Edit mode — "Save as Draft" hidden | `showSaveDraft = false` | ☐ |
| Edit mode — submit calls `updateRequest` not `createRequest` | `isEditMode` flag | ☐ |

---

## 23. CODE_REVIEW_MODULE_07.md Template

```markdown
# Code Review — Module 07: Tenant Request Form
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-07-request-form
**Spec version:** ANDROID_MODULE_07.md v1.0.0

---

## ✅ Architecture Compliance
- [ ] `TenantRequestFormData` domain model has zero Android imports
- [ ] `TenantRequestRepositoryImpl.createRequest` + `updateRequest` implemented
- [ ] `RequestDraftDataStore` named `"request_draft"` — separate from `"listing_draft"`
- [ ] `FormShell` reused from Module 06 — no duplication of shell UI
- [ ] Edit mode loads from Firestore — does NOT restore DataStore draft
- [ ] `koinViewModel()` used — not `viewModel()`
- [ ] `collectAsStateWithLifecycle()` used throughout

---

## ✅ Module 06 FormShell Reuse
- [ ] `FormShell` composable extracted in Module 06 at `presentation/shared/components/FormShell.kt`
- [ ] All required parameters confirmed: `currentStep`, `totalSteps`, `onBack`, `onSaveDraft`, `showSaveDraft`, `continueLabel`, `onContinue`, `isContinueLoading`, `stepError`, `content`
- [ ] Request form uses `totalSteps = 6`, listing form uses `totalSteps = 11`

---

## ✅ Module 05 Shared Components Reuse
- [ ] `IntentCard` from `shared/components/IntentCard.kt` used in Step 1
- [ ] `MapBackground` + `RadiusCircle` from `shared/components/` used in Step 4
- [ ] `RadiusCircle(animate = true)` used in Step 4 (form = animated)
- [ ] `RadiusCircle(animate = false)` confirmed in Module 05 detail screen (static)

---

## ✅ Step-by-Step Design Verification

| Step | Title | UI Elements | Validation | Animations |
|------|-------|-------------|------------|------------|
| 1 | "What do you need?" | 2 intent cards | intent not blank | card bg/border animates on select |
| 2 | "Property type" | 6 chip options | type not blank | chip bg animates |
| 3 | "Your Budget" | price input + preview | budget > 0 | — |
| 4 | "Where & Radius" | area chips + dropdowns + slider + MapBG + RadiusCircle | city + areas | RadiusCircle diameter animates |
| 5 | "Preferences" | 3 chip groups | none | — |
| 6 | "About You" | textarea + counter | none | counter colour at 480+ |

---

## ✅ DataStore Persistence
- [ ] Draft saved after every `advanceStep()` call
- [ ] Draft cleared after successful submit
- [ ] Draft cleared on "Discard"
- [ ] Draft NOT saved in edit mode
- [ ] Draft file name is `"request_draft"` — confirmed separate from listing draft

---

## ✅ Slider Spec
- [ ] `Slider(valueRange = 1f..15f, steps = 13)` — 15 values, 13 internal stops
- [ ] Custom thumb: 20dp circle, `RentoColors.primary` fill, `10dp` elevation with `primaryRing` spot colour
- [ ] `RadiusCircle` updates in real-time with slider (via `onRadiusChange`)

---

## ✅ Code Quality
- [ ] All strings in `strings.xml`
- [ ] `./gradlew test` → ✅ PASSING (24+ tests)
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

*End of Module 07 — Tenant Request Form v1.0.0*
*Depends on: Modules 01–06. Next module: Module 08 — Chat (Encrypted Messaging).*
