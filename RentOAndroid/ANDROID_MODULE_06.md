# RentO — Android App
## Module 06 — Property Listing Form (11-Step Wizard)
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 06
> **Branch:** `feature/module-06-listing-form`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 03 ✅ · Module 04 ✅ · Module 05 ✅
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every measurement, colour, interaction, and animation is derived verbatim from the prototype and `REQUIREMENTS_SPECIFICATION_v2_3.md`. No improvisation. No simplification. If anything is ambiguous — stop and ask.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Domain & Data Layer](#4-domain--data-layer)
5. [Architecture — ViewModel & State](#5-architecture--viewmodel--state)
6. [Task M06-T01 — Domain Models & Repository Interfaces](#task-m06-t01--domain-models--repository-interfaces)
7. [Task M06-T02 — `ListingRepositoryImpl` Extensions](#task-m06-t02--listingrepositoryimpl-extensions)
8. [Task M06-T03 — `StorageRepositoryImpl`](#task-m06-t03--storagerepositoryimpl)
9. [Task M06-T04 — `GeminiRepositoryImpl`](#task-m06-t04--geminirepositoryimpl)
10. [Task M06-T05 — Use Cases](#task-m06-t05--use-cases)
11. [Task M06-T06 — `ListingFormViewModel`](#task-m06-t06--listingformviewmodel)
12. [Task M06-T07 — Koin Module](#task-m06-t07--koin-module)
13. [Task M06-T08 — Navigation Wiring](#task-m06-t08--navigation-wiring)
14. [Task M06-T09 — Shared Form Components](#task-m06-t09--shared-form-components)
15. [Task M06-T10 — `ListingFormScreen` Shell](#task-m06-t10--listingformscreen-shell)
16. [Task M06-T11 — Step 1: What Are You Offering?](#task-m06-t11--step-1-what-are-you-offering)
17. [Task M06-T12 — Step 2: Type of Property?](#task-m06-t12--step-2-type-of-property)
18. [Task M06-T13 — Step 3: Availability & Duration](#task-m06-t13--step-3-availability--duration)
19. [Task M06-T14 — Step 4: Pricing](#task-m06-t14--step-4-pricing)
20. [Task M06-T15 — Step 5: Location](#task-m06-t15--step-5-location)
21. [Task M06-T16 — Step 6: Property Details](#task-m06-t16--step-6-property-details)
22. [Task M06-T17 — Step 7: Amenities](#task-m06-t17--step-7-amenities)
23. [Task M06-T18 — Step 8: What's Nearby?](#task-m06-t18--step-8-whats-nearby)
24. [Task M06-T19 — Step 9: Add Photos](#task-m06-t19--step-9-add-photos)
25. [Task M06-T20 — Step 10: AI Summary](#task-m06-t20--step-10-ai-summary)
26. [Task M06-T21 — Step 11: Review & Publish](#task-m06-t21--step-11-review--publish)
27. [Task M06-T22 — Publish Limit Sheet](#task-m06-t22--publish-limit-sheet)
28. [Task M06-T23 — Success Screen](#task-m06-t23--success-screen)
29. [Task M06-T24 — Edit Mode Support](#task-m06-t24--edit-mode-support)
30. [Task M06-T25 — `ProgressStepBar` Component](#task-m06-t25--progressstepbar-component)
31. [Task M06-T26 — String Resources](#task-m06-t26--string-resources)
32. [Task M06-T27 — Unit Tests](#task-m06-t27--unit-tests)
33. [Task M06-T28 — Build Gate](#task-m06-t28--build-gate)
34. [Journey Coverage Checklist](#34-journey-coverage-checklist)
35. [CODE_REVIEW_MODULE_06.md Template](#35-code_review_module_06md-template)

---

## 1. Module Overview

Module 06 delivers the **11-step Property Listing Form** — the full wizard for hosts to create a rental listing. It is the most complex form in the app.

**Key features:**
- 11-step wizard with `ProgressStepBar`, slide left/right step transition animations
- Draft auto-saved to `DataStore` on every step advance; restored on screen re-entry
- Edit mode: pre-populate all steps from Firestore for updating existing listings
- Step 9: photo upload to Firebase Storage with client-side image compression (max 1.5MB)
- Step 10: AI-generated description via Gemini API with 4-key rotation + failure logging
- Step 11: publish slot limit check, `requireApproval` config flag
- Success screen (MeshBackground) after publish/draft save
- Publish Limit bottom sheet (GlassBottomSheet) when slot limit reached
- `ProgressStepBar` shared component (also used in Module 07)

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── ListingFormData.kt               ← NEW
│   │   ├── ListingStatus.kt                 ← already in M04 — verify exists
│   │   └── TimeSlot.kt                      ← NEW
│   ├── repository/
│   │   ├── StorageRepository.kt             ← NEW
│   │   └── GeminiRepository.kt              ← NEW
│   └── usecase/
│       └── listingform/
│           ├── CreateListingUseCase.kt
│           ├── UpdateListingUseCase.kt
│           ├── SaveDraftUseCase.kt
│           ├── LoadDraftUseCase.kt
│           ├── GetPublishedCountUseCase.kt
│           ├── GetListingConfigUseCase.kt
│           ├── UploadListingImagesUseCase.kt
│           └── GenerateDescriptionUseCase.kt
├── data/
│   ├── repository/
│   │   ├── StorageRepositoryImpl.kt         ← NEW
│   │   └── GeminiRepositoryImpl.kt          ← NEW
│   └── local/
│       └── ListingDraftDataStore.kt         ← NEW
├── presentation/
│   ├── shared/
│   │   └── components/
│   │       └── ProgressStepBar.kt           ← NEW (shared with Module 07)
│   └── listing/
│       └── form/
│           ├── ListingFormViewModel.kt
│           ├── ListingFormScreen.kt
│           ├── ListingFormSuccessScreen.kt
│           └── steps/
│               ├── Step1WhatOffering.kt
│               ├── Step2PropertyType.kt
│               ├── Step3Availability.kt
│               ├── Step4Pricing.kt
│               ├── Step5Location.kt
│               ├── Step6Details.kt
│               ├── Step7Amenities.kt
│               ├── Step8Nearby.kt
│               ├── Step9Photos.kt
│               ├── Step10Summary.kt
│               └── Step11Review.kt
│       └── components/
│           ├── IntentCard.kt
│           ├── PhotoSlot.kt
│           ├── PublishLimitSheet.kt
│           └── NearbyPlaceRow.kt
└── di/
    └── ListingFormModule.kt
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M06-T01 | Domain models (`ListingFormData`, `TimeSlot`) + repository interfaces (`StorageRepository`, `GeminiRepository`) | ☐ |
| M06-T02 | `ListingRepositoryImpl` extensions — `createListing`, `updateListing`, `getDraftCount`, `getPublishedCount` | ☐ |
| M06-T03 | `StorageRepositoryImpl` — upload/delete listing images with compression | ☐ |
| M06-T04 | `GeminiRepositoryImpl` — 4-key rotation, failure logging | ☐ |
| M06-T05 | All use cases | ☐ |
| M06-T06 | `ListingFormViewModel` + `ListingFormUiState` + `ListingDraftDataStore` | ☐ |
| M06-T07 | Koin module — `ListingFormModule.kt` | ☐ |
| M06-T08 | Navigation wiring — `listing/form` + `listing/form?listingId=&mode=edit` | ☐ |
| M06-T09 | Shared form components — `ProgressStepBar`, `IntentCard`, `PhotoSlot`, `NearbyPlaceRow` | ☐ |
| M06-T10 | `ListingFormScreen` shell — header, animated step content, footer | ☐ |
| M06-T11 | Step 1 — What Are You Offering? (3 intent cards) | ☐ |
| M06-T12 | Step 2 — Type of Property? (chip grid) | ☐ |
| M06-T13 | Step 3 — Availability & Duration (chips, date picker, time slots) | ☐ |
| M06-T14 | Step 4 — Pricing (price input, negotiate toggle, duration chips) | ☐ |
| M06-T15 | Step 5 — Location (map pin drop, dropdowns, current location) | ☐ |
| M06-T16 | Step 6 — Property Details (beds, baths, floor, furnished, details) | ☐ |
| M06-T17 | Step 7 — Amenities (3-column grid, multi-select) | ☐ |
| M06-T18 | Step 8 — What's Nearby? (toggle rows with editable distance) | ☐ |
| M06-T19 | Step 9 — Add Photos (2×2 grid, upload, compress) | ☐ |
| M06-T20 | Step 10 — AI Summary (Gemini call, shimmer, editable field, regenerate) | ☐ |
| M06-T21 | Step 11 — Review & Publish (preview card, slot indicator, publish) | ☐ |
| M06-T22 | Publish Limit Sheet (GlassBottomSheet) | ☐ |
| M06-T23 | Success Screen (MeshBackground, bounce animation) | ☐ |
| M06-T24 | Edit mode — pre-populate, status transitions, save changes | ☐ |
| M06-T25 | `ProgressStepBar` component | ☐ |
| M06-T26 | String resources | ☐ |
| M06-T27 | Unit tests | ☐ |
| M06-T28 | Build gate | ☐ |

---

## 4. Domain & Data Layer

### 4.1 `ListingFormData`

```kotlin
data class ListingFormData(
    // Step 1
    val intent: String = "",                  // "share" | "fullRent" | "hourly"
    // Step 2
    val propertyType: String = "",
    // Step 3
    val suitableFor: String = "",
    val duration: String = "monthly",         // "daily" | "weekly" | "monthly" | "hourly"
    val availableImmediately: Boolean = true,
    val availableFrom: Long? = null,          // epoch millis if custom date
    val timeSlots: List<TimeSlot> = emptyList(),
    val availableDays: List<String> = emptyList(),
    // Step 4
    val price: Int = 0,
    val isNegotiable: Boolean = false,
    // Step 5
    val province: String = "",
    val city: String = "",
    val area: String = "",
    val fullAddress: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    // Step 6
    val bedrooms: Int = 1,
    val bathrooms: Int = 1,
    val furnished: String = "furnished",      // "furnished" | "semi" | "unfurnished"
    val floor: Int? = null,
    val additionalDetails: String = "",
    // Step 7
    val amenities: List<String> = emptyList(),
    // Step 8
    val nearbyPlaces: List<NearbyPlace> = emptyList(),
    // Step 9
    val imageUris: List<Uri> = emptyList(),   // local URIs before upload
    val existingImageUrls: List<String> = emptyList(),  // edit mode: already uploaded
    val deletedImageUrls: List<String> = emptyList(),   // edit mode: to delete
    // Step 10
    val description: String = "",
    // Meta
    val listingId: String? = null,            // set after create/in edit mode
)

data class TimeSlot(
    val start: String,   // "09:00"
    val end: String,     // "17:00"
)
```

### 4.2 Repository Interfaces

```kotlin
// StorageRepository.kt
interface StorageRepository {
    suspend fun uploadListingImages(
        uid: String,
        listingId: String,
        imageUris: List<Uri>,
    ): Result<List<String>>   // returns download URLs

    suspend fun deleteImages(urls: List<String>): Result<Unit>
}

// GeminiRepository.kt
interface GeminiRepository {
    suspend fun generateDescription(data: ListingFormData): Result<String>
}
```

### 4.3 `ListingRepository` — New Methods Required

Add to `ListingRepository` interface and `ListingRepositoryImpl`:

```kotlin
suspend fun createListing(uid: String, ownerName: String, data: ListingFormData, imageUrls: List<String>, status: String): Result<String>  // returns listingId
suspend fun updateListing(listingId: String, data: ListingFormData, imageUrls: List<String>, status: String): Result<Unit>
suspend fun getPublishedCount(uid: String): Result<Int>
suspend fun getUserListings(uid: String): Result<List<Listing>>
```

### 4.4 `ListingDraftDataStore`

```kotlin
// data/local/ListingDraftDataStore.kt
class ListingDraftDataStore(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("listing_draft")
    private val dataStore = context.dataStore
    private val KEY = stringPreferencesKey("listing_form_draft")

    suspend fun saveDraft(data: ListingFormData) {
        dataStore.edit { it[KEY] = Json.encodeToString(data) }
    }

    suspend fun loadDraft(): ListingFormData? = runCatching {
        dataStore.data.first()[KEY]?.let { Json.decodeFromString<ListingFormData>(it) }
    }.getOrNull()

    suspend fun clearDraft() {
        dataStore.edit { it.remove(KEY) }
    }
}
```

> `ListingFormData` must be annotated with `@Serializable` (kotlinx.serialization). `Uri` fields (`imageUris`) must be excluded from serialization — use `@Transient` or store as String paths.

---

## 5. Architecture — ViewModel & State

### `ListingFormUiState`

```kotlin
data class ListingFormUiState(
    val step: Int = 1,
    val totalSteps: Int = 11,
    val formData: ListingFormData = ListingFormData(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Step-specific UI states
    val isGeneratingSummary: Boolean = false,
    val summaryError: String? = null,
    val isUploadingPhotos: Boolean = false,
    val uploadProgress: Float = 0f,

    // Publish flow
    val publishedCount: Int = 0,
    val maxPublishedListings: Int = 2,
    val requireApproval: Boolean = false,
    val showPublishLimitSheet: Boolean = false,

    // Submission result
    val submitSuccess: Boolean = false,
    val submitAsDraft: Boolean = false,
    val submittedListingId: String? = null,

    // Edit mode
    val isEditMode: Boolean = false,
    val isLoadingExisting: Boolean = false,
)
```

### `ListingFormViewModel` — Function Signatures

```kotlin
class ListingFormViewModel(
    private val createListing: CreateListingUseCase,
    private val updateListing: UpdateListingUseCase,
    private val saveDraft: SaveDraftUseCase,
    private val loadDraft: LoadDraftUseCase,
    private val getPublishedCount: GetPublishedCountUseCase,
    private val getListingConfig: GetListingConfigUseCase,
    private val uploadImages: UploadListingImagesUseCase,
    private val generateDescription: GenerateDescriptionUseCase,
    private val authRepository: AuthRepository,
    private val listingRepository: ListingRepository,
) : ViewModel()

// Functions:
fun initForm(listingId: String? = null, isEditMode: Boolean = false)
fun advanceStep()                     // validates current step; if valid → step++
fun goBack()                          // step-- (or pop screen on step 1)
fun updateFormData(update: ListingFormData.() -> ListingFormData)
fun setIntent(intent: String)
fun setPropertyType(type: String)
fun setSuitableFor(value: String)
fun setDuration(duration: String)
fun setAvailableImmediately(immediate: Boolean)
fun setAvailableFrom(epochMillis: Long)
fun addTimeSlot(slot: TimeSlot)
fun removeTimeSlot(index: Int)
fun toggleAvailableDay(day: String)
fun setPrice(price: Int)
fun setNegotiable(negotiable: Boolean)
fun setLocation(province: String, city: String, area: String, lat: Double, lng: Double)
fun setBedrooms(bedrooms: Int)
fun setBathrooms(bathrooms: Int)
fun setFurnished(furnished: String)
fun setFloor(floor: Int?)
fun setAdditionalDetails(details: String)
fun toggleAmenity(amenity: String)
fun toggleNearbyPlace(place: NearbyPlace)
fun updateNearbyDistance(category: String, meters: Int)
fun addImageUri(uri: Uri)
fun removeImageUri(uri: Uri)
fun removeExistingImage(url: String)
fun setDescription(description: String)
fun generateAISummary()
fun saveAsDraft()
fun publishListing()
fun dismissPublishLimitSheet()
fun saveChanges()                     // edit mode only
```

### Step Validation Rules

```kotlin
fun validateStep(step: Int, data: ListingFormData): String? = when (step) {
    1  -> if (data.intent.isBlank()) "Please select what you're offering" else null
    2  -> if (data.propertyType.isBlank()) "Please select a property type" else null
    3  -> if (data.suitableFor.isBlank()) "Please select who this is suitable for" else null
    4  -> if (data.price <= 0) "Please enter a valid price" else null
    5  -> if (data.province.isBlank() || data.city.isBlank() || data.area.isBlank())
             "Please complete all location fields" else null
    6  -> null   // bedrooms/baths/furnished have defaults
    7  -> null   // amenities optional
    8  -> null   // nearby optional
    9  -> if (data.imageUris.isEmpty() && data.existingImageUrls.isEmpty())
             "Please add at least one photo" else null
    10 -> null   // description optional (Gemini may fail gracefully)
    11 -> null   // review step — publish/draft handled separately
    else -> null
}
```

---

## Task M06-T01 — Domain Models & Repository Interfaces

### Sub-tasks
- [ ] **M06-T01-A** Create `domain/model/ListingFormData.kt` and `domain/model/TimeSlot.kt`.
- [ ] **M06-T01-B** Annotate `ListingFormData` with `@Serializable`. Mark `imageUris: List<Uri>` as `@Transient` (not serialized — reconstructed from file picker each session). `existingImageUrls` IS serialized.
- [ ] **M06-T01-C** Create `domain/repository/StorageRepository.kt` and `domain/repository/GeminiRepository.kt`.
- [ ] **M06-T01-D** Add new methods to `ListingRepository` interface: `createListing`, `updateListing`, `getPublishedCount`, `getUserListings`.
- [ ] **M06-T01-E** Verify: zero Android imports in all domain files (`Uri` is `android.net.Uri` — place Uri-containing model in `data` layer or use `String` for paths in domain and convert in use cases/repos). **Decision:** `ListingFormData` lives in `domain` but `imageUris` field type is `@Transient` and `String` paths are used for domain purposes. The actual `android.net.Uri` handling stays in the use case / repository layer.

Revised `ListingFormData` for domain purity:
```kotlin
// In domain — uri-free version:
data class ListingFormData(
    // ...all fields above...
    val localImagePaths: List<String> = emptyList(),     // file paths as strings
    val existingImageUrls: List<String> = emptyList(),
    val deletedImageUrls: List<String> = emptyList(),
    // ...
)
// ViewModel holds actual Uri list separately in _selectedUris: MutableList<Uri>
```

---

## Task M06-T02 — `ListingRepositoryImpl` Extensions

Add to existing `ListingRepositoryImpl.kt`:

### Sub-tasks
- [ ] **M06-T02-A** `createListing`:

```kotlin
override suspend fun createListing(
    uid: String,
    ownerName: String,
    data: ListingFormData,
    imageUrls: List<String>,
    status: String,
): Result<String> = runCatching {
    val doc = firestore.collection("listings").document()
    val map = data.toFirestoreMap(uid, ownerName, imageUrls, status, doc.id)
    doc.set(map).await()
    doc.id
}
```

- [ ] **M06-T02-B** `updateListing`:

```kotlin
override suspend fun updateListing(
    listingId: String,
    data: ListingFormData,
    imageUrls: List<String>,
    status: String,
): Result<Unit> = runCatching {
    val map = data.toFirestoreUpdateMap(imageUrls, status)
    firestore.collection("listings").document(listingId).update(map).await()
}
```

- [ ] **M06-T02-C** `getPublishedCount`:

```kotlin
override suspend fun getPublishedCount(uid: String): Result<Int> = runCatching {
    firestore.collection("listings")
        .whereEqualTo("uid", uid)
        .whereEqualTo("status", "published")
        .get().await().size()
}
```

- [ ] **M06-T02-D** `toFirestoreMap` — extension function that maps `ListingFormData` to `HashMap<String, Any?>`:

```kotlin
private fun ListingFormData.toFirestoreMap(
    uid: String, ownerName: String, imageUrls: List<String>,
    status: String, id: String,
): HashMap<String, Any?> = hashMapOf(
    "uid"                  to uid,
    "ownerName"            to ownerName,
    "intent"               to intent,
    "propertyType"         to propertyType,
    "suitableFor"          to suitableFor,
    "duration"             to duration,
    "timeSlots"            to timeSlots.map { mapOf("start" to it.start, "end" to it.end) },
    "availableDays"        to availableDays,
    "availableImmediately" to availableImmediately,
    "availableFrom"        to availableFrom?.let { Timestamp(it / 1000, 0) },
    "price"                to price,
    "isNegotiable"         to isNegotiable,
    "province"             to province,
    "city"                 to city,
    "area"                 to area,
    "fullAddress"          to fullAddress,
    "lat"                  to lat,
    "lng"                  to lng,
    "bedrooms"             to bedrooms,
    "bathrooms"            to bathrooms,
    "furnished"            to furnished,
    "floor"                to floor,
    "additionalDetails"    to additionalDetails,
    "amenities"            to amenities,
    "nearbyPlaces"         to nearbyPlaces.map { mapOf("category" to it.category, "distanceMeters" to it.distanceMeters) },
    "imageUrls"            to imageUrls,
    "description"          to description,
    "status"               to status,
    "hasActiveSlider"      to false,
    "createdAt"            to FieldValue.serverTimestamp(),
    "updatedAt"            to FieldValue.serverTimestamp(),
)
```

---

## Task M06-T03 — `StorageRepositoryImpl`

**File:** `data/repository/StorageRepositoryImpl.kt`

### Sub-tasks
- [ ] **M06-T03-A** Implement image upload with client-side compression:

```kotlin
class StorageRepositoryImpl(
    private val storage: FirebaseStorage,
    private val context: Context,
) : StorageRepository {

    override suspend fun uploadListingImages(
        uid: String,
        listingId: String,
        imageUris: List<Uri>,
    ): Result<List<String>> = runCatching {
        imageUris.map { uri ->
            val compressed = compressImage(uri)
            val ref = storage.reference
                .child("listings/$uid/$listingId/${UUID.randomUUID()}.jpg")
            ref.putBytes(compressed).await()
            ref.downloadUrl.await().toString()
        }
    }

    override suspend fun deleteImages(urls: List<String>): Result<Unit> = runCatching {
        urls.forEach { url ->
            runCatching { storage.getReferenceFromUrl(url).delete().await() }
            // Swallow individual delete failures — file may already be gone
        }
    }

    // Client-side compression: target max 1.5MB, 80% quality JPEG
    private suspend fun compressImage(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Scale down if dimensions exceed 1920×1080
        val maxWidth = 1920
        val maxHeight = 1080
        val scaled = if (originalBitmap.width > maxWidth || originalBitmap.height > maxHeight) {
            val ratio = minOf(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height,
            )
            Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * ratio).toInt(),
                (originalBitmap.height * ratio).toInt(),
                true,
            )
        } else originalBitmap

        val output = ByteArrayOutputStream()
        var quality = 85
        do {
            output.reset()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
            quality -= 10
        } while (output.size() > 1_500_000 && quality > 30)

        if (scaled != originalBitmap) scaled.recycle()
        originalBitmap.recycle()
        output.toByteArray()
    }
}
```

---

## Task M06-T04 — `GeminiRepositoryImpl`

**File:** `data/repository/GeminiRepositoryImpl.kt`

Implements the 4-key rotation + failure logging per Section 24.2:

```kotlin
class GeminiRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig,
    private val firestore: FirebaseFirestore,
) : GeminiRepository {

    private fun getKeys(): List<String> = listOf(
        remoteConfig.getString("gemini_key_1"),
        remoteConfig.getString("gemini_key_2"),
        remoteConfig.getString("gemini_key_3"),
        remoteConfig.getString("gemini_key_4"),
    ).filter { it.isNotBlank() }

    override suspend fun generateDescription(data: ListingFormData): Result<String> {
        val keys = getKeys()
        if (keys.isEmpty()) return Result.failure(Exception("no_keys_configured"))

        keys.forEachIndexed { index, key ->
            runCatching { callGeminiApi(key, data) }
                .onSuccess { return Result.success(it) }
                .onFailure { e -> logKeyFailure(index + 1, e.message ?: "Unknown") }
        }
        return Result.failure(Exception("all_keys_exhausted"))
    }

    private suspend fun callGeminiApi(key: String, data: ListingFormData): String {
        val prompt = buildPrompt(data)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$key"
        val body = """
            {
              "contents": [{"parts": [{"text": "$prompt"}]}],
              "generationConfig": {"maxOutputTokens": 250, "temperature": 0.7}
            }
        """.trimIndent()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")

        val json = JSONObject(response.body!!.string())
        return json
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
            .trim()
    }

    private fun buildPrompt(data: ListingFormData): String =
        "Write a compelling 3-sentence property listing description for a ${data.intent} property " +
        "in ${data.area}, ${data.city}. It is a ${data.propertyType} with ${data.bedrooms} bedrooms, " +
        "${data.bathrooms} bathrooms, ${data.furnished} furnishing. Amenities: ${data.amenities.take(5).joinToString(", ")}. " +
        "Price: PKR ${data.price}/month. Keep it factual, warm, and professional. " +
        "Do not use quotes. Output only the description text."

    private suspend fun logKeyFailure(keyNumber: Int, error: String) {
        runCatching {
            firestore.collection("geminiKeyLogs").add(
                mapOf(
                    "keyNumber"    to keyNumber,
                    "errorMessage" to error,
                    "timestamp"    to FieldValue.serverTimestamp(),
                )
            ).await()
        }
    }
}
```

---

## Task M06-T05 — Use Cases

All in `domain/usecase/listingform/`:

### Sub-tasks
- [ ] **M06-T05-A** `CreateListingUseCase`:

```kotlin
class CreateListingUseCase(
    private val listingRepository: ListingRepository,
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        data: ListingFormData,
        imageUris: List<Uri>,
        status: String,
    ): Result<String> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Not authenticated."))
        val ownerName = authRepository.getCurrentUserName() ?: "Host"
        // Upload images first
        val listingId = java.util.UUID.randomUUID().toString()
        val uploadResult = storageRepository.uploadListingImages(uid, listingId, imageUris)
        val imageUrls = uploadResult.getOrElse { return Result.failure(it) }
        return listingRepository.createListing(uid, ownerName, data, imageUrls, status)
    }
}
```

- [ ] **M06-T05-B** `UpdateListingUseCase`:

```kotlin
class UpdateListingUseCase(
    private val listingRepository: ListingRepository,
    private val storageRepository: StorageRepository,
) {
    suspend operator fun invoke(
        listingId: String,
        data: ListingFormData,
        newImageUris: List<Uri>,
        deletedUrls: List<String>,
        status: String,
    ): Result<Unit> {
        // Delete removed images from Storage
        if (deletedUrls.isNotEmpty()) storageRepository.deleteImages(deletedUrls)
        // Upload new images
        val uid = data.existingImageUrls.firstOrNull()?.let {
            it.substringAfter("listings/").substringBefore("/")
        } ?: listingId
        val newUrls = if (newImageUris.isNotEmpty()) {
            storageRepository.uploadListingImages(uid, listingId, newImageUris)
                .getOrElse { return Result.failure(it) }
        } else emptyList()
        val allImageUrls = data.existingImageUrls + newUrls
        return listingRepository.updateListing(listingId, data, allImageUrls, status)
    }
}
```

- [ ] **M06-T05-C** `SaveDraftUseCase`:

```kotlin
class SaveDraftUseCase(private val draftDataStore: ListingDraftDataStore) {
    suspend operator fun invoke(data: ListingFormData) = draftDataStore.saveDraft(data)
}
```

- [ ] **M06-T05-D** `LoadDraftUseCase`:

```kotlin
class LoadDraftUseCase(private val draftDataStore: ListingDraftDataStore) {
    suspend operator fun invoke(): ListingFormData? = draftDataStore.loadDraft()
}
```

- [ ] **M06-T05-E** `GetPublishedCountUseCase`:

```kotlin
class GetPublishedCountUseCase(
    private val listingRepository: ListingRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Int> {
        val uid = authRepository.getCurrentUserId()
            ?: return Result.success(0)
        return listingRepository.getPublishedCount(uid)
    }
}
```

- [ ] **M06-T05-F** `GetListingConfigUseCase`:

```kotlin
class GetListingConfigUseCase(private val firestore: FirebaseFirestore) {
    data class ListingConfig(val requireApproval: Boolean, val maxPublishedListings: Int)

    suspend operator fun invoke(): Result<ListingConfig> = runCatching {
        val doc = firestore.collection("config").document("listing").get().await()
        ListingConfig(
            requireApproval     = doc.getBoolean("requireApproval") ?: false,
            maxPublishedListings = (doc.getLong("maxPublishedListings") ?: 2).toInt(),
        )
    }
}
```

- [ ] **M06-T05-G** `UploadListingImagesUseCase` — thin wrapper around `StorageRepository`.
- [ ] **M06-T05-H** `GenerateDescriptionUseCase` — thin wrapper around `GeminiRepository`.

---

## Task M06-T06 — `ListingFormViewModel`

**File:** `presentation/listing/form/ListingFormViewModel.kt`

Full implementation:

```kotlin
class ListingFormViewModel(
    private val createListing: CreateListingUseCase,
    private val updateListing: UpdateListingUseCase,
    private val saveDraft: SaveDraftUseCase,
    private val loadDraft: LoadDraftUseCase,
    private val getPublishedCount: GetPublishedCountUseCase,
    private val getListingConfig: GetListingConfigUseCase,
    private val generateDescription: GenerateDescriptionUseCase,
    private val authRepository: AuthRepository,
    private val listingRepository: ListingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingFormUiState())
    val uiState: StateFlow<ListingFormUiState> = _uiState.asStateFlow()

    // Holds actual Uri objects — not in domain state
    private val _selectedUris = mutableListOf<Uri>()
    val selectedUris: List<Uri> get() = _selectedUris.toList()

    fun initForm(listingId: String? = null, isEditMode: Boolean = false) {
        viewModelScope.launch {
            val config = getListingConfig().getOrNull()
            val count  = getPublishedCount().getOrDefault(0)
            _uiState.update {
                it.copy(
                    isEditMode           = isEditMode,
                    publishedCount       = count,
                    requireApproval      = config?.requireApproval ?: false,
                    maxPublishedListings = config?.maxPublishedListings ?: 2,
                )
            }
            if (isEditMode && listingId != null) {
                loadExistingListing(listingId)
            } else {
                // Load saved draft (new form only)
                loadDraft()?.let { draft ->
                    _uiState.update { it.copy(formData = draft) }
                }
            }
        }
    }

    private suspend fun loadExistingListing(listingId: String) {
        _uiState.update { it.copy(isLoadingExisting = true) }
        listingRepository.getListingById(listingId).fold(
            onSuccess = { listing ->
                val formData = listing.toFormData()
                _uiState.update { it.copy(formData = formData, isLoadingExisting = false) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoadingExisting = false, error = e.message) }
            },
        )
    }

    fun advanceStep() {
        val state = _uiState.value
        val error = validateStep(state.step, state.formData)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        _uiState.update { it.copy(error = null) }
        viewModelScope.launch {
            saveDraft(state.formData)  // auto-save draft on every step advance
        }
        if (state.step < state.totalSteps) {
            _uiState.update { it.copy(step = state.step + 1) }
        }
    }

    fun goBack() {
        val step = _uiState.value.step
        if (step > 1) {
            _uiState.update { it.copy(step = step - 1, error = null) }
        }
        // If step == 1, screen handles back navigation
    }

    fun updateFormData(update: ListingFormData.() -> ListingFormData) {
        _uiState.update { it.copy(formData = it.formData.update(), error = null) }
    }

    // Individual setters delegate to updateFormData
    fun setIntent(intent: String)           = updateFormData { copy(intent = intent) }
    fun setPropertyType(type: String)       = updateFormData { copy(propertyType = type) }
    fun setSuitableFor(value: String)       = updateFormData { copy(suitableFor = value) }
    fun setDuration(d: String)              = updateFormData { copy(duration = d) }
    fun setAvailableImmediately(v: Boolean) = updateFormData { copy(availableImmediately = v) }
    fun setAvailableFrom(ms: Long)          = updateFormData { copy(availableFrom = ms, availableImmediately = false) }
    fun setPrice(p: Int)                    = updateFormData { copy(price = p) }
    fun setNegotiable(n: Boolean)           = updateFormData { copy(isNegotiable = n) }
    fun setBedrooms(b: Int)                 = updateFormData { copy(bedrooms = b) }
    fun setBathrooms(b: Int)                = updateFormData { copy(bathrooms = b) }
    fun setFurnished(f: String)             = updateFormData { copy(furnished = f) }
    fun setFloor(f: Int?)                   = updateFormData { copy(floor = f) }
    fun setAdditionalDetails(d: String)     = updateFormData { copy(additionalDetails = d) }
    fun setDescription(d: String)           = updateFormData { copy(description = d) }

    fun setLocation(province: String, city: String, area: String, lat: Double, lng: Double) =
        updateFormData { copy(province = province, city = city, area = area, lat = lat, lng = lng) }

    fun toggleAmenity(amenity: String) = updateFormData {
        val updated = if (amenity in amenities) amenities - amenity else amenities + amenity
        copy(amenities = updated)
    }

    fun toggleNearbyPlace(place: NearbyPlace) = updateFormData {
        val exists = nearbyPlaces.any { it.category == place.category }
        val updated = if (exists) nearbyPlaces.filter { it.category != place.category }
                      else nearbyPlaces + place
        copy(nearbyPlaces = updated)
    }

    fun updateNearbyDistance(category: String, meters: Int) = updateFormData {
        copy(nearbyPlaces = nearbyPlaces.map {
            if (it.category == category) it.copy(distanceMeters = meters) else it
        })
    }

    fun addImageUri(uri: Uri) {
        if (_selectedUris.size < 4) {
            _selectedUris.add(uri)
            _uiState.update { it.copy(error = null) }
        }
    }

    fun removeImageUri(uri: Uri) { _selectedUris.remove(uri) }

    fun removeExistingImage(url: String) = updateFormData {
        copy(
            existingImageUrls = existingImageUrls - url,
            deletedImageUrls  = deletedImageUrls + url,
        )
    }

    fun generateAISummary() {
        _uiState.update { it.copy(isGeneratingSummary = true, summaryError = null) }
        viewModelScope.launch {
            generateDescription(_uiState.value.formData).fold(
                onSuccess = { text ->
                    _uiState.update { it.copy(
                        isGeneratingSummary = false,
                        formData            = it.formData.copy(description = text),
                    )}
                },
                onFailure = { e ->
                    _uiState.update { it.copy(
                        isGeneratingSummary = false,
                        summaryError        = if (e.message == "all_keys_exhausted") null   // graceful
                                             else e.message,
                    )}
                },
            )
        }
    }

    fun saveAsDraft() {
        viewModelScope.launch {
            saveDraft(_uiState.value.formData)
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val ownerName = authRepository.getCurrentUserName() ?: "Host"
            createListing(_uiState.value.formData, emptyList(), "draft").fold(
                onSuccess = { id ->
                    _uiState.update { it.copy(submitSuccess = true, submitAsDraft = true, submittedListingId = id) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message) }
                },
            )
        }
    }

    fun publishListing() {
        val state = _uiState.value
        // Check publish slot limit
        if (state.publishedCount >= state.maxPublishedListings) {
            _uiState.update { it.copy(showPublishLimitSheet = true) }
            return
        }
        val status = if (state.requireApproval) "pending_approval" else "published"
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            createListing(state.formData, _selectedUris.toList(), status).fold(
                onSuccess = { id ->
                    loadDraft()?.let { saveDraft(ListingFormData()) }  // clear draft
                    _uiState.update { it.copy(
                        isLoading           = false,
                        submitSuccess       = true,
                        submitAsDraft       = false,
                        submittedListingId  = id,
                    )}
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    fun dismissPublishLimitSheet() {
        _uiState.update { it.copy(showPublishLimitSheet = false) }
    }

    fun saveChanges() {
        val state = _uiState.value
        val listingId = state.formData.listingId ?: return
        val targetStatus = when (state.formData.listingStatus) {
            "published" -> if (state.requireApproval) "pending_approval" else "published"
            "rejected"  -> "pending_approval"
            else        -> state.formData.listingStatus
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            updateListing(
                listingId   = listingId,
                data        = state.formData,
                newImageUris = _selectedUris.toList(),
                deletedUrls = state.formData.deletedImageUrls,
                status      = targetStatus,
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, submitSuccess = true, submitAsDraft = false) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }
}
```

---

## Task M06-T07 — Koin Module

**File:** `di/ListingFormModule.kt`

```kotlin
val listingFormModule = module {
    single { ListingDraftDataStore(androidContext()) }
    single<StorageRepository> { StorageRepositoryImpl(get(), androidContext()) }
    single<GeminiRepository>  { GeminiRepositoryImpl(get(), get()) }

    factory { CreateListingUseCase(get(), get(), get()) }
    factory { UpdateListingUseCase(get(), get()) }
    factory { SaveDraftUseCase(get()) }
    factory { LoadDraftUseCase(get()) }
    factory { GetPublishedCountUseCase(get(), get()) }
    factory { GetListingConfigUseCase(get()) }
    factory { UploadListingImagesUseCase(get()) }
    factory { GenerateDescriptionUseCase(get()) }

    viewModel { ListingFormViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
```

Register `listingFormModule` in `RentoApplication`.

---

## Task M06-T08 — Navigation Wiring

```kotlin
// New form
composable(route = HomeRoutes.LISTING_FORM) {
    ListingFormScreen(
        onBack          = { navController.popBackStack() },
        onNavigateHome  = { navController.navigate(HomeRoutes.HOME) { popUpTo(HomeRoutes.HOME) } },
        onNavigateMyListings = { navController.navigate("my_listings") },
        onNavigatePackages   = { navController.navigate("packages") },
    )
}

// Edit mode
composable(
    route     = "listing/form?listingId={listingId}&mode=edit",
    arguments = listOf(
        navArgument("listingId") { type = NavType.StringType },
        navArgument("mode") { defaultValue = "new" },
    ),
) { backStackEntry ->
    val listingId = backStackEntry.arguments?.getString("listingId")
    val isEditMode = backStackEntry.arguments?.getString("mode") == "edit"
    ListingFormScreen(
        listingId    = listingId,
        isEditMode   = isEditMode,
        onBack       = { navController.popBackStack() },
        onNavigateHome = { navController.navigate(HomeRoutes.HOME) { popUpTo(HomeRoutes.HOME) } },
        onNavigateMyListings = { navController.navigate("my_listings") },
        onNavigatePackages   = { navController.navigate("packages") },
    )
}
```

---

## Task M06-T09 — Shared Form Components

### `ProgressStepBar` — Section 3.4.10

**File:** `presentation/shared/components/ProgressStepBar.kt`

```kotlin
@Composable
fun ProgressStepBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            repeat(totalSteps) { index ->
                val stepNum = index + 1
                val state = when {
                    stepNum == currentStep -> StepState.ACTIVE
                    stepNum < currentStep  -> StepState.COMPLETED
                    else                   -> StepState.FUTURE
                }
                val width by animateDpAsState(
                    targetValue   = if (state == StepState.ACTIVE) 22.dp else 5.dp,
                    animationSpec = tween(300),
                    label         = "stepBarWidth$index",
                )
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(5.dp)
                        .background(
                            color = when (state) {
                                StepState.ACTIVE    -> MaterialTheme.rentoColors.primary
                                StepState.COMPLETED -> MaterialTheme.rentoColors.primary2.copy(alpha = 0.55f)
                                StepState.FUTURE    -> MaterialTheme.rentoColors.bg4
                            },
                            shape = RoundedCornerShape(100.dp),
                        ),
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(
            text  = "Step $currentStep of $totalSteps",
            style = MaterialTheme.rentoTypography.labelS,   // 11sp SemiBold
            color = MaterialTheme.rentoColors.t2,
        )
    }
}

private enum class StepState { ACTIVE, COMPLETED, FUTURE }
```

### `IntentCard` — reusable large selection card

**File:** `presentation/listing/form/components/IntentCard.kt`

```kotlin
@Composable
fun IntentCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isSelected) MaterialTheme.rentoColors.primaryTint
                else            MaterialTheme.rentoColors.bg2,
                RoundedCornerShape(18.dp),
            )
            .border(
                width = 1.5.dp,
                color = if (isSelected) MaterialTheme.rentoColors.primary
                        else            MaterialTheme.rentoColors.border2,
                shape = RoundedCornerShape(18.dp),
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(
                    if (isSelected) MaterialTheme.rentoColors.primaryTint
                    else            MaterialTheme.rentoColors.bg3,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier    = Modifier.size(28.dp),
                tint        = if (isSelected) MaterialTheme.rentoColors.primary
                              else            MaterialTheme.rentoColors.t2,
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.rentoTypography.bodyL, color = MaterialTheme.rentoColors.t0)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, style = MaterialTheme.rentoTypography.bodyS, color = MaterialTheme.rentoColors.t2)
        }
        if (isSelected) {
            Spacer(Modifier.width(12.dp))
            Icon(RentoIcons.Check, null,
                tint     = MaterialTheme.rentoColors.primary,
                modifier = Modifier.size(22.dp))
        }
    }
}
```

### Sub-tasks
- [ ] **M06-T09-A** Implement `ProgressStepBar` in `shared/components/`.
- [ ] **M06-T09-B** Implement `IntentCard`.
- [ ] **M06-T09-C** Implement `PhotoSlot` (see M06-T19).
- [ ] **M06-T09-D** Implement `NearbyPlaceRow` (see M06-T18).

---

## Task M06-T10 — `ListingFormScreen` Shell

**File:** `presentation/listing/form/ListingFormScreen.kt`

### Shell Layout (Section 9.1)

```kotlin
@Composable
fun ListingFormScreen(
    listingId: String? = null,
    isEditMode: Boolean = false,
    viewModel: ListingFormViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateMyListings: () -> Unit,
    onNavigatePackages: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedUris = viewModel.selectedUris

    LaunchedEffect(Unit) { viewModel.initForm(listingId, isEditMode) }

    // Navigate to success when submitSuccess = true
    if (uiState.submitSuccess) {
        ListingFormSuccessScreen(
            isDraft       = uiState.submitAsDraft,
            requireApproval = uiState.requireApproval && !uiState.submitAsDraft,
            onViewListings  = onNavigateMyListings,
            onBackHome      = onNavigateHome,
        )
        return
    }

    if (uiState.isLoadingExisting) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.rentoColors.bg0),
            Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.rentoColors.primary)
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0)
            .imePadding(),
    ) {
        // ── HEADER ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RentoIconButton(RentoIcons.Back, size = 42.dp, onClick = {
                if (uiState.step > 1) viewModel.goBack() else onBack()
            })
            ProgressStepBar(
                currentStep = uiState.step,
                totalSteps  = uiState.totalSteps,
                modifier    = Modifier.weight(1f),
            )
            Text(
                text     = if (isEditMode) "Save Draft" else "Save Draft",
                style    = MaterialTheme.rentoTypography.labelM.copy(fontWeight = FontWeight.Bold),
                color    = MaterialTheme.rentoColors.primary,
                modifier = Modifier.clickable { viewModel.saveAsDraft() },
            )
        }

        // ── CONTENT AREA ─────────────────────────────────────────────
        val scrollState = rememberScrollState()

        // Step transition animation
        AnimatedContent(
            targetState  = uiState.step,
            modifier     = Modifier.weight(1f),
            transitionSpec = {
                val enterDir  = if (targetState > initialState) 1 else -1
                (slideInHorizontally { w -> enterDir * w } + fadeIn(tween(200))) togetherWith
                (slideOutHorizontally { w -> -enterDir * w } + fadeOut(tween(150)))
            },
            label = "stepContent",
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 26.dp),
            ) {
                // Step title (su animation — slide up from below)
                val stepMeta = getStepMeta(step, isEditMode)
                Text(
                    text     = stepMeta.title,
                    style    = MaterialTheme.rentoTypography.displayS,   // Fraunces 28sp SemiBold
                    color    = MaterialTheme.rentoColors.t0,
                    modifier = Modifier.padding(bottom = 7.dp),
                )
                Text(
                    text     = stepMeta.subtitle,
                    style    = MaterialTheme.rentoTypography.bodyM,
                    color    = MaterialTheme.rentoColors.t2,
                    modifier = Modifier.padding(bottom = 28.dp),
                )

                // Error banner
                AnimatedVisibility(visible = uiState.error != null) {
                    uiState.error?.let { err ->
                        ErrorBanner(message = err, modifier = Modifier.padding(bottom = 16.dp))
                    }
                }

                // Step content
                when (step) {
                    1  -> Step1WhatOffering(uiState.formData, viewModel)
                    2  -> Step2PropertyType(uiState.formData, viewModel)
                    3  -> Step3Availability(uiState.formData, viewModel)
                    4  -> Step4Pricing(uiState.formData, viewModel)
                    5  -> Step5Location(uiState.formData, viewModel)
                    6  -> Step6Details(uiState.formData, viewModel)
                    7  -> Step7Amenities(uiState.formData, viewModel)
                    8  -> Step8Nearby(uiState.formData, viewModel)
                    9  -> Step9Photos(uiState.formData, selectedUris, viewModel)
                    10 -> Step10Summary(uiState, viewModel)
                    11 -> Step11Review(uiState, viewModel)
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // ── FOOTER ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.rentoColors.bg0)
                .border(BorderStroke(1.dp, MaterialTheme.rentoColors.border), RoundedCornerShape(0.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 18.dp)
                .navigationBarsPadding(),
        ) {
            val isLastStep = uiState.step == uiState.totalSteps
            PrimaryButton(
                label     = when {
                    isLastStep && isEditMode -> "💾 Save Changes"
                    isLastStep               -> "🚀 Publish Listing"
                    else                     -> "Continue →"
                },
                isLoading = uiState.isLoading,
                modifier  = Modifier.fillMaxWidth(),
                onClick   = {
                    if (isLastStep) {
                        if (isEditMode) viewModel.saveChanges() else viewModel.publishListing()
                    } else {
                        viewModel.advanceStep()
                    }
                },
            )
            if (!isLastStep) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text      = "Save as Draft",
                    style     = MaterialTheme.rentoTypography.bodyS,
                    color     = MaterialTheme.rentoColors.t3,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.saveAsDraft() },
                )
            }
        }
    }

    // ── Overlays ─────────────────────────────────────────────────────
    if (uiState.showPublishLimitSheet) {
        PublishLimitSheet(
            used         = uiState.publishedCount,
            max          = uiState.maxPublishedListings,
            onUpgrade    = { viewModel.dismissPublishLimitSheet(); onNavigatePackages() },
            onManage     = { viewModel.dismissPublishLimitSheet(); onNavigateMyListings() },
            onSaveDraft  = { viewModel.dismissPublishLimitSheet(); viewModel.saveAsDraft() },
            onDismiss    = { viewModel.dismissPublishLimitSheet() },
        )
    }
}

data class StepMeta(val title: String, val subtitle: String)

private fun getStepMeta(step: Int, isEditMode: Boolean): StepMeta = when (step) {
    1  -> StepMeta("What are you offering?", "Tell us what type of space you're listing")
    2  -> StepMeta("Type of property?", "Select the category that best fits your space")
    3  -> StepMeta("How can guests stay?", "Set availability and duration options")
    4  -> StepMeta("Set your price", "Enter your monthly rent and options")
    5  -> StepMeta("Where is your property?", "Set the location for your listing")
    6  -> StepMeta("What's included?", "Add details about the space")
    7  -> StepMeta("What do you offer?", "Select all amenities available")
    8  -> StepMeta("What's nearby?", "Add nearby landmarks and distances")
    9  -> StepMeta("Show them around", "Add up to 4 photos of your space")
    10 -> StepMeta("Your listing summary", "AI will generate a description for you")
    11 -> if (isEditMode) StepMeta("Review Changes", "Check your updates before saving")
         else             StepMeta("Ready to publish?", "Review and publish your listing")
    else -> StepMeta("", "")
}
```

### Sub-tasks
- [ ] **M06-T10-A** Implement `ListingFormScreen` shell.
- [ ] **M06-T10-B** `AnimatedContent` step transition: `slideInHorizontally + fadeIn` (advance = left, back = right).
- [ ] **M06-T10-C** `imePadding()` applied to root Column — keyboard doesn't overlap footer.
- [ ] **M06-T10-D** `navigationBarsPadding()` applied to footer.

---

## Task M06-T11 — Step 1: What Are You Offering?

3 `IntentCard` components in a Column with 12dp gap.

```
Intent options:
  "Share Space"   | Users icon    | "Room, bed or paying guest"
  "Full Property" | House icon    | "Entire apartment or house"
  "By the Hour"   | Clock icon    | "Coworking or guest room"
```

```kotlin
@Composable
fun Step1WhatOffering(data: ListingFormData, vm: ListingFormViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            Triple("share",    RentoIcons.Users,    "Share Space"   to "Room, bed or paying guest"),
            Triple("fullRent", RentoIcons.Home,     "Full Property" to "Entire apartment or house"),
            Triple("hourly",   RentoIcons.Clock,    "By the Hour"   to "Coworking or guest room"),
        ).forEach { (value, icon, labels) ->
            IntentCard(
                icon       = icon,
                title      = labels.first,
                subtitle   = labels.second,
                isSelected = data.intent == value,
                onClick    = { vm.setIntent(value) },
            )
        }
    }
}
```

---

## Task M06-T12 — Step 2: Type of Property?

Single-select chip grid (wrapping `FlowRow`):

```
Apartment · House · Duplex · Portion · Room · Studio · Hostel Bed ·
Penthouse · Farm House · Office Space · Guest Room · Coworking Space
```

Each `RentoChip` with leading icon. Show only types relevant to intent (all types visible always — no filtering needed per spec).

```kotlin
val propertyTypes = listOf(
    "Apartment" to RentoIcons.Building,
    "House"     to RentoIcons.Home,
    "Duplex"    to RentoIcons.Layers,
    "Portion"   to RentoIcons.Grid,
    "Room"      to RentoIcons.Bed,
    "Studio"    to RentoIcons.Star,
    "Hostel Bed" to RentoIcons.Bed,
    "Penthouse" to RentoIcons.ArrowUpDown,
    "Farm House" to RentoIcons.Pin,
    "Office Space" to RentoIcons.Monitor,
    "Guest Room"  to RentoIcons.Users,
    "Coworking Space" to RentoIcons.Users,
)
```

---

## Task M06-T13 — Step 3: Availability & Duration

### Suitable For (single-select chips)
```
Male Only · Female Only · Male & Female · Family · All Welcome
```

### Duration (single-select chips, context-aware)
```
Daily · Weekly · Monthly
+ "Hourly" chip ONLY if data.intent == "hourly"
```

### Available From
```
"Immediately" chip (selected by default) + "Custom Date" chip
If Custom Date selected → show DatePickerDialog on click
```

### Time Slots (hourly intent only)
```
if data.intent == "hourly":
  Button "+ Add Time Slot" (GhostButton with Plus icon)
  → opens time picker dialogs for start + end
  Each slot shown as chip with delete X
  Available Days chips: Mon · Tue · Wed · Thu · Fri · Sat · Sun (multi-select)
```

### Sub-tasks
- [ ] **M06-T13-A** Implement `Step3Availability`.
- [ ] **M06-T13-B** Use `DatePickerDialog` from `android.app.DatePickerDialog` wrapped in a Composable.
- [ ] **M06-T13-C** Use `TimePickerDialog` wrapped similarly for time slots.
- [ ] **M06-T13-D** Time slot items show `"${slot.start} – ${slot.end}"` with delete icon.

---

## Task M06-T14 — Step 4: Pricing

### Price Input Box
```
Box(
  fillMaxWidth,
  background=RentoColors.bg2, border=1.5.dp RentoColors.border,
  shape=RoundedCornerShape(22.dp), padding=22.dp,
):
  SectionLabel("MONTHLY RENT")   // label updates based on selected duration
  Spacer(12.dp)
  Row(CenterVertically, spacedBy=8.dp):
    Text("PKR", 28sp Bold, RentoColors.primary)
    BasicTextField(
      value        = if price == 0 "" else price.toString(),
      onValueChange = { vm.setPrice(it.toIntOrNull() ?: 0) },
      textStyle     = 34sp Bold DarkT0,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      placeholder   = { Text("0", 34sp, RentoColors.t3) },
      singleLine    = true,
    )
  HorizontalDivider(1.dp, RentoColors.border2, modifier=Modifier.padding(vertical=14.dp))
  // Negotiate toggle row
  Row(fillMaxWidth, SpaceBetween, CenterVertically):
    Text("Allow negotiation", bodyM, RentoColors.t1)
    ToggleSwitch(checked=data.isNegotiable, onCheckedChange={ vm.setNegotiable(it) })
```

### Duration Chips (below price box, 12dp top)
```
Daily · Weekly · Monthly  (single select)
Active selection updates SectionLabel at top of box
```

---

## Task M06-T15 — Step 5: Location

### Map Pin Drop Area
```
Box(
  fillMaxWidth, height=155.dp,
  clip(RoundedCornerShape(18.dp)), border=1.5.dp DarkBd,
  clickable → open full MapScreen or use system location
):
  MapBackground(fillMaxSize)
  Icon(RentoIcons.Pin, 32.dp, DarkPri, centred + pulse animation)
  if lat != 0.0:
    // Pin has been dropped — show coordinates pill
    Box(bottom-centre, DarkNavBg, 8dp/12dp padding, 100dp corner):
      Text("${lat.format2dp}, ${lng.format2dp}", 11sp, DarkPri)
  else:
    Text("Tap to drop pin", 12sp, DarkT2, below-centre)
```

> Pin pulse animation: `rememberInfiniteTransition` scale 0.85↔1.15, 900ms, RepeatMode.Reverse.

> In V1: tapping map opens `CityPickerSheet` for province + city selection. Full map pin-drop (GPS coordinates) is V2. Log "TODO: replace with map pin drop in V2".

### Province + City + Area Fields
```
SectionLabel("PROVINCE")
UnderlineInputField with dropdown (CityPickerSheet on tap)
SectionLabel("CITY")
UnderlineInputField (populated from province selection)
SectionLabel("AREA / NEIGHBOURHOOD")
UnderlineInputField(free text)
```

### "Use current location" link
```
Text("📍 Use current location", 13sp DarkPri, clickable):
  → request ACCESS_FINE_LOCATION permission
  → if granted: FusedLocationProvider.lastLocation
  → TODO V2: reverse geocode to fill province/city/area
  → V1: fill with "Current Location" placeholder
```

### Sub-tasks
- [ ] **M06-T15-A** Implement `Step5Location`.
- [ ] **M06-T15-B** Tapping the map container opens `CityPickerSheet` (from Module 03) for province/city.
- [ ] **M06-T15-C** Location permission handling via `ActivityResultContracts.RequestPermission`.
- [ ] **M06-T15-D** Area is free-text `UnderlineInputField`.

---

## Task M06-T16 — Step 6: Property Details

### Bedrooms (single-select chips with icons)
```
Studio (0) · 1 · 2 · 3 · 4+
Each RentoChip with Bed icon
```

### Bathrooms
```
1 · 2 · 3+
Each RentoChip with Bath icon
```

### Furnished (single-select chips)
```
Furnished (Check icon) · Semi (Check icon) · Unfurnished (X icon)
```

### Floor Number
```
SectionLabel("FLOOR NUMBER (OPTIONAL)")
UnderlineInputField(numeric, placeholder="e.g. 2")
```

### Additional Details
```
SectionLabel("ADDITIONAL DETAILS (OPTIONAL)")
Box(fillMaxWidth, minHeight=100.dp, DarkBg2, 1.5dp DarkBd, 14dp corner, 14dp/16dp padding):
  BasicTextField(
    value = data.additionalDetails,
    onValueChange = { vm.setAdditionalDetails(it) },
    placeholder = { Text("Anything else potential guests should know...", DarkT3) },
    minLines = 3,
  )
```

---

## Task M06-T17 — Step 7: Amenities

3-column grid, multi-select. All 26 amenities always shown.

```
Tile (selected):
  DarkPriM fill, 1dp DarkPri border
  Icon 24dp DarkPri, label 11sp DarkPri, 18dp corner, 14dp V / 8dp H padding, centred

Tile (unselected):
  DarkBg2 fill, 1dp DarkBd border
  Icon 24dp DarkT2, label 11sp DarkT2

All 26 amenities from amenityIconMap (same as Module 04 M04-T14):
  WiFi · AC · Generator · Water 24h · Gas · Parking · Lift · Balcony · CCTV · Geyser ·
  Laundry · Pet Friendly · Heating · Solar · TV · Fridge · Microwave · Washing Machine ·
  Security Guard · Rooftop Access · Prayer Room · Common Kitchen · Shared Lounge ·
  Garden / Lawn · Swimming Pool · Smoking Allowed
```

```kotlin
// 3-column chunked grid (NOT LazyVerticalGrid — inside ScrollColumn)
Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    allAmenities.chunked(3).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach { amenity ->
                AmenitySelectTile(
                    amenity    = amenity,
                    icon       = amenityIconMap[amenity] ?: RentoIcons.Star,
                    isSelected = amenity in data.amenities,
                    onClick    = { vm.toggleAmenity(amenity) },
                    modifier   = Modifier.weight(1f),
                )
            }
            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}
```

---

## Task M06-T18 — Step 8: What's Nearby?

12 nearby place categories, each as a toggle row:

```
Categories:
  Mosque · Hospital · Mart · School · Bank/ATM · Bus Stop ·
  Restaurant · Petrol Pump · Gym · Park · Pharmacy · Office Area
```

### `NearbyPlaceRow` composable

```
Row(
  fillMaxWidth, CenterVertically,
  background=DarkBg2, border=1.5dp DarkBd, shape=16dp, padding=13dp V / 15dp H,
):
  Icon(category icon, 22dp, DarkT1)
  Spacer(12dp)
  Text(category, 14sp, DarkT1, Modifier.weight(1f))

  // When toggled ON:
  if enabled:
    // Distance pill (DarkPriM bg, 100dp corner, tappable)
    Box(
      DarkPriM bg, DarkPri2 border, 100dp corner, 6dp V / 12dp H padding,
      clickable → show slider/number input
    ):
      Text("${meters}m", 12sp Bold, DarkPri)

  Spacer(8dp)
  ToggleSwitch(
    checked = enabled,
    onCheckedChange = { on ->
      if (on) vm.toggleNearbyPlace(NearbyPlace(category, 500))  // default 500m
      else    vm.toggleNearbyPlace(NearbyPlace(category, 0))    // removes
    },
  )

// When enabled AND distance pill tapped: show inline slider below row
if enabled && distanceEditing:
  Slider(
    value    = (meters / 100).toFloat(),   // 1–50 steps of 100m
    onValueChange = { vm.updateNearbyDistance(category, (it * 100).toInt()) },
    valueRange = 1f..50f,
    steps    = 48,
    // Custom thumb: 20dp DarkPri circle
    // Custom track: 4dp, DarkBg4 inactive / DarkPri active
  )
  Text("${meters}m away", 12sp DarkT2, centred)
```

### Sub-tasks
- [ ] **M06-T18-A** Implement `Step8Nearby` + `NearbyPlaceRow`.
- [ ] **M06-T18-B** `distanceEditing: Boolean` state per row — track locally in `Step8Nearby` with `remember { mutableStateMapOf<String, Boolean>() }`.

---

## Task M06-T19 — Step 9: Add Photos

### 2×2 Photo Grid

```
Column(verticalArrangement=spacedBy(11.dp)):
  Row(horizontalArrangement=spacedBy(11.dp)):
    PhotoSlot(index=0, uri=..., isCover=true)
    PhotoSlot(index=1, uri=...)
  Row(horizontalArrangement=spacedBy(11.dp)):
    PhotoSlot(index=2, uri=...)
    PhotoSlot(index=3, uri=...)
```

### `PhotoSlot` composable

```
Box(
  fillMaxWidth, height=120.dp,
  shape=18dp, Modifier.weight(1f),
):
  if uri != null || existingUrl != null:
    AsyncImage(uri or existingUrl, fillMaxSize, ContentScale.Crop, clip=18dp)
    // Delete overlay
    Box(
      Modifier.align(TopEnd).padding(6.dp)
        .size(28.dp)
        .background(Color.Black.copy(0.55f), CircleShape)
        .clickable { onDelete() },
      contentAlignment=Center,
    ):
      Icon(RentoIcons.X, 14.dp, Color.White)
    if isCover:
      Box(
        Modifier.align(TopStart).padding(6.dp)
          .background(DarkPriM, RoundedCornerShape(6.dp))
          .padding(3.dp V / 6.dp H),
      ):
        Text("COVER", 9sp ExtraBold, DarkPri, letterSpacing=0.08em)
  else:
    // Empty slot
    Box(
      fillMaxSize,
      background=if isCover DarkPriM else DarkBg2,
      border=2dp dashed DarkBd2, shape=18dp,
      clickable { onAdd() },
      contentAlignment=Center,
    ):
      Column(CenterHorizontally):
        Icon(RentoIcons.Camera, 28dp, if isCover DarkPri else DarkT3)
        Spacer(6dp)
        Text("Add photo", 12sp, if isCover DarkPri else DarkT2)
        if isCover: Text("COVER", 9sp ExtraBold, DarkPri, top=2dp, letterSpacing=0.08em)
```

### Info Banner

```
Row(
  fillMaxWidth,
  background=DarkPriM, border=1dp DarkPriR, shape=14dp,
  padding=13dp V / 15dp H, 12dp top margin,
):
  Text("🌿 Max 4 photos · Max 1.5MB · Auto-compressed before upload",
       13sp, DarkPri)
```

### Image Picker

```kotlin
val imagePickerLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri -> uri?.let { viewModel.addImageUri(it) } }

// on slot tap:
imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
```

### Sub-tasks
- [ ] **M06-T19-A** Implement `Step9Photos` + `PhotoSlot`.
- [ ] **M06-T19-B** First slot always renders `isCover=true`.
- [ ] **M06-T19-C** Existing URLs shown for edit mode (with delete button) alongside new URIs.
- [ ] **M06-T19-D** Max 4 total (existing + new combined).
- [ ] **M06-T19-E** Use `ActivityResultContracts.PickVisualMedia` (Photo Picker API — Android 13+) with fallback `GetContent()` for older versions.
- [ ] **M06-T19-F** Dashed border via `PathEffect.dashPathEffect(floatArrayOf(10f, 8f))` in `drawBehind`.

---

## Task M06-T20 — Step 10: AI Summary

### Layout

```
Column(fillMaxWidth):

  // AI Summary Card
  Box(
    fillMaxWidth, DarkBg2, 1.5dp DarkBd, 18dp corner, padding=16dp,
  ):
    // Header row
    Row(CenterVertically, spacedBy=8dp):
      Icon(RentoIcons.Star, 18dp, DarkPri)
      Text("AI-Generated Summary", 12sp Bold DarkPri, Modifier.weight(1f))
      if !isGenerating && description.isNotBlank():
        Badge("Ready", BadgeStyle.INTENT)

    Spacer(8dp)

    // Content
    if isGeneratingSummary:
      // Shimmer skeleton (3 lines)
      repeat(3) {
        Box(Modifier.fillMaxWidth().height(14.dp).padding(vertical=3.dp).shimmer())
      }
    else:
      // Editable text field
      BasicTextField(
        value         = data.description,
        onValueChange = { vm.setDescription(it) },
        textStyle     = 14sp + DarkT0 + lineHeight=24sp,
        modifier      = Modifier.fillMaxWidth().defaultMinSize(minHeight=140.dp),
        placeholder   = { Text(if summaryError != null
                              "AI summary unavailable. Enter a description manually."
                              else "Generating your summary...", 14sp DarkT3) },
      )

  // Error note for Gemini failure (inline, not snackbar)
  if summaryError != null:
    Spacer(8dp)
    Text("AI is temporarily unavailable. You can write your description manually.",
         13sp DarkT2)

  Spacer(16dp)

  // Regenerate button
  OutlinePrimaryButton(
    label    = "✨ Regenerate",
    icon     = RentoIcons.Star,
    modifier = Modifier.fillMaxWidth(),
    onClick  = { vm.generateAISummary() },
    enabled  = !isGeneratingSummary,
  )
```

### Sub-tasks
- [ ] **M06-T20-A** Implement `Step10Summary`.
- [ ] **M06-T20-B** `LaunchedEffect(Unit)` triggers `vm.generateAISummary()` on first entry to step 10 (only if description is currently blank).
- [ ] **M06-T20-C** Gemini failure → show inline message; description field stays editable; step is not blocked.
- [ ] **M06-T20-D** `OutlinePrimaryButton` from Module 01.

---

## Task M06-T21 — Step 11: Review & Publish

### Layout

```
Column(fillMaxWidth):

  // Summary preview card
  Box(
    fillMaxWidth, DarkBg2, 1.5dp DarkBd2, 20dp corner,
    overflow=Hidden,
  ):
    // Image area (100dp height)
    if images available:
      AsyncImage(first image, 100dp, ContentScale.Crop, fillMaxWidth)
    else:
      Box(100dp, fillMaxWidth, gradientPrimary bg):
        Text("🏠", 40sp, centred, alpha=0.4f)

    // Info below image
    Column(padding=14dp):
      Text("${data.propertyType} in ${data.area}", 15sp Bold, DarkT0)
      Text("${data.city} · ${intentLabel(data.intent)}", 12sp, DarkT2)
      Spacer(6dp)
      Text("PKR ${formatPrice(data.price)}/mo", 16sp Bold, DarkPri)

  Spacer(16dp)

  // Publish slot indicator
  Row(
    fillMaxWidth, CenterVertically, spacedBy=10dp,
    background=DarkPriM, border=1dp DarkPriR, shape=14dp, padding=13dp/15dp,
  ):
    Icon(RentoIcons.Check, 18dp, DarkPri)
    Text(
      "${uiState.publishedCount + 1} of ${uiState.maxPublishedListings} publish slots will be used",
      13sp, DarkPri,
    )

  // Warning if at limit
  if uiState.publishedCount >= uiState.maxPublishedListings:
    Spacer(8dp)
    Row(
      DarkAccM bg, DarkAcc border, 14dp corner, 12dp/15dp padding, spacedBy=8dp,
    ):
      Icon(RentoIcons.AlertCircle, 18dp, DarkAcc)
      Text("Publish limit reached. Save as Draft or upgrade your plan.", 13sp, DarkAcc)
```

### Sub-tasks
- [ ] **M06-T21-A** Implement `Step11Review`.
- [ ] **M06-T21-B** Publish button in footer becomes disabled when `publishedCount >= maxPublishedListings` (warning shown instead).
- [ ] **M06-T21-C** In edit mode, step 11 header is "Review Changes" and button label is "💾 Save Changes".

---

## Task M06-T22 — Publish Limit Sheet

**File:** `presentation/listing/form/components/PublishLimitSheet.kt`

Section 32.10 spec:

```
GlassBottomSheet(onDismiss):

  Column(CenterHorizontally, 24dp padding):
    Box(68dp circle, DarkAccM):
      Icon(RentoIcons.Building, 40dp, DarkAcc)

    Spacer(16dp)
    Text("Publish Limit Reached", Fraunces 22sp SemiBold, centred)
    Spacer(8dp)
    Text(
      "You've used $used of $max publish slots. Unpublish a listing or upgrade.",
      14sp, DarkT2, centred
    )
    Spacer(24dp)
    PrimaryButton("🚀 Upgrade Plan", fillMaxWidth, onClick=onUpgrade)
    Spacer(8dp)
    GhostButton("Manage Listings", fillMaxWidth, onClick=onManage)
    Spacer(8dp)
    Text(
      "Save as Draft instead",
      13sp DarkT2, centred, clickable { onSaveDraft() }
    )
    Spacer(24dp + navBarPadding)
```

---

## Task M06-T23 — Success Screen

**File:** `presentation/listing/form/ListingFormSuccessScreen.kt`

Section 32.13 spec:

```kotlin
@Composable
fun ListingFormSuccessScreen(
    isDraft: Boolean,
    requireApproval: Boolean,
    onViewListings: () -> Unit,
    onBackHome: () -> Unit,
)
```

```
Box(fillMaxSize):
  MeshBackground(fillMaxSize)  // from Module 01

  Column(CenterHorizontally, CenterVertically, padding=32dp):
    // Animated check/draft icon
    BounceEffect:
      Box(80dp circle, DarkPriM bg, 3dp DarkPri border):
        Icon(
          if isDraft RentoIcons.FileText else RentoIcons.Check,
          36dp, DarkPri,
        )

    Spacer(24dp)

    Text(
      if isDraft "Saved as Draft!" else "Listing Submitted!",
      Fraunces 28sp SemiBold, centred,
    )
    Spacer(12dp)
    Text(
      when {
        isDraft       -> "Your listing is saved. You can publish it any time."
        requireApproval -> "Your listing is under review. We'll notify you when approved."
        else            -> "Your listing is live! People can now find your space."
      },
      14sp, DarkT2, centred, lineHeight=22sp,
    )

    Spacer(32dp)
    PrimaryButton(
      if isDraft "View My Drafts" else "View My Listings",
      fillMaxWidth, onClick=onViewListings
    )
    Spacer(10dp)
    GhostButton("Back to Home", fillMaxWidth, onClick=onBackHome)

  // Auto-navigate after 4 seconds (new publish only — not draft)
  LaunchedEffect(Unit):
    if !isDraft: delay(4_000); onViewListings()
```

---

## Task M06-T24 — Edit Mode Support

### Sub-tasks
- [ ] **M06-T24-A** `Listing.toFormData()` — extension function to map a `Listing` domain object to `ListingFormData` for pre-population:

```kotlin
fun Listing.toFormData(): ListingFormData = ListingFormData(
    intent              = intent,
    propertyType        = propertyType,
    suitableFor         = suitableFor,
    duration            = duration,
    availableImmediately = availableImmediately,
    availableFrom       = availableFrom,
    timeSlots           = timeSlots,
    availableDays       = availableDays,
    price               = price,
    isNegotiable        = isNegotiable,
    province            = province,
    city                = city,
    area                = area,
    fullAddress         = fullAddress,
    lat                 = lat,
    lng                 = lng,
    bedrooms            = bedrooms,
    bathrooms           = bathrooms,
    furnished           = furnished,
    floor               = floor,
    additionalDetails   = additionalDetails ?: "",
    amenities           = amenities,
    nearbyPlaces        = nearbyPlaces,
    existingImageUrls   = imageUrls,
    description         = description ?: "",
    listingId           = id,
    listingStatus       = status.name.lowercase(),
)
```

- [ ] **M06-T24-B** `ListingFormData` must include `listingStatus: String = ""` to track for status transition logic.
- [ ] **M06-T24-C** In edit mode, "Save Draft" link in header does nothing (replaced by footer "Save Changes" button).
- [ ] **M06-T24-D** Status transitions on save:

```kotlin
val targetStatus = when {
    formData.listingStatus == "published" && requireApproval -> "pending_approval"
    formData.listingStatus == "published"                    -> "published"
    formData.listingStatus == "rejected"                     -> "pending_approval"
    formData.listingStatus == "draft"                        -> "draft"
    else                                                     -> formData.listingStatus
}
```

---

## Task M06-T26 — String Resources

```xml
<!-- ─── Listing Form ──────────────────────────────────────────────────────── -->
<string name="form_save_draft">Save Draft</string>
<string name="form_continue">Continue →</string>
<string name="form_publish">🚀 Publish Listing</string>
<string name="form_save_changes">💾 Save Changes</string>
<string name="form_save_as_draft">Save as Draft</string>
<string name="form_step_label">Step %1$d of %2$d</string>

<!-- Step titles -->
<string name="step1_title">What are you offering?</string>
<string name="step1_subtitle">Tell us what type of space you\'re listing</string>
<string name="step2_title">Type of property?</string>
<string name="step2_subtitle">Select the category that best fits your space</string>
<string name="step3_title">How can guests stay?</string>
<string name="step3_subtitle">Set availability and duration options</string>
<string name="step4_title">Set your price</string>
<string name="step4_subtitle">Enter your monthly rent and options</string>
<string name="step5_title">Where is your property?</string>
<string name="step5_subtitle">Set the location for your listing</string>
<string name="step6_title">What\'s included?</string>
<string name="step6_subtitle">Add details about the space</string>
<string name="step7_title">What do you offer?</string>
<string name="step7_subtitle">Select all amenities available</string>
<string name="step8_title">What\'s nearby?</string>
<string name="step8_subtitle">Add nearby landmarks and distances</string>
<string name="step9_title">Show them around</string>
<string name="step9_subtitle">Add up to 4 photos of your space</string>
<string name="step10_title">Your listing summary</string>
<string name="step10_subtitle">AI will generate a description for you</string>
<string name="step11_title">Ready to publish?</string>
<string name="step11_subtitle">Review and publish your listing</string>
<string name="step11_edit_title">Review Changes</string>
<string name="step11_edit_subtitle">Check your updates before saving</string>

<!-- Intent cards -->
<string name="intent_share_title">Share Space</string>
<string name="intent_share_sub">Room, bed or paying guest</string>
<string name="intent_full_title">Full Property</string>
<string name="intent_full_sub">Entire apartment or house</string>
<string name="intent_hourly_title">By the Hour</string>
<string name="intent_hourly_sub">Coworking or guest room</string>

<!-- Step 4 -->
<string name="pricing_section_label_monthly">MONTHLY RENT</string>
<string name="pricing_section_label_daily">DAILY RENT</string>
<string name="pricing_section_label_weekly">WEEKLY RENT</string>
<string name="pricing_section_label_hourly">HOURLY RATE</string>
<string name="pricing_negotiate_label">Allow negotiation</string>

<!-- Step 9 -->
<string name="photos_info_banner">🌿 Max 4 photos · Max 1.5MB · Auto-compressed before upload</string>
<string name="photos_add_label">Add photo</string>
<string name="photos_cover_label">COVER</string>

<!-- Step 10 -->
<string name="summary_ai_label">AI-Generated Summary</string>
<string name="summary_ready_badge">Ready</string>
<string name="summary_regenerate">✨ Regenerate</string>
<string name="summary_ai_unavailable">AI is temporarily unavailable. You can write your description manually.</string>

<!-- Publish limit sheet -->
<string name="publish_limit_title">Publish Limit Reached</string>
<string name="publish_limit_body">You\'ve used %1$d of %2$d publish slots. Unpublish a listing or upgrade.</string>
<string name="publish_limit_upgrade">🚀 Upgrade Plan</string>
<string name="publish_limit_manage">Manage Listings</string>
<string name="publish_limit_save_draft">Save as Draft instead</string>

<!-- Success screen -->
<string name="success_listing_submitted">Listing Submitted!</string>
<string name="success_listing_draft">Saved as Draft!</string>
<string name="success_under_review">Your listing is under review. We\'ll notify you when approved.</string>
<string name="success_listing_live">Your listing is live! People can now find your space.</string>
<string name="success_draft_body">Your listing is saved. You can publish it any time.</string>
<string name="success_view_listings">View My Listings</string>
<string name="success_view_drafts">View My Drafts</string>
<string name="success_back_home">Back to Home</string>
```

---

## Task M06-T27 — Unit Tests

### `ListingFormViewModelTest.kt`

```
initForm_loadsConfig_andDraft
initForm_editMode_loadsExistingListing
advanceStep_validStep_incrementsStep
advanceStep_invalidStep1_setsError
advanceStep_invalidStep4_priceZero_setsError
advanceStep_autoSavesDraftOnAdvance
goBack_fromStep3_decrementsToStep2
goBack_fromStep1_doesNotDecrement
setIntent_updatesFormData
toggleAmenity_addsAndRemoves
toggleNearbyPlace_addsWithDefaultDistance_andRemoves
updateNearbyDistance_updatesCorrectPlace
addImageUri_maxFour_ignoresFifth
removeExistingImage_movesToDeletedUrls
publishListing_atLimit_showsLimitSheet
publishListing_success_setsSubmitSuccess
publishListing_requireApproval_setsPendingStatus
publishListing_noApproval_setsPublishedStatus
generateAISummary_success_setsDescription
generateAISummary_failure_setsErrorGracefully
saveAsDraft_createsDraftListing
saveChanges_editMode_callsUpdateListing
saveChanges_publishedWithApproval_setsPendingStatus
```

### `CreateListingUseCaseTest.kt`

```
invoke_unauthenticated_returnsFailure
invoke_uploadsImages_thenCreatesListing
invoke_uploadFailure_propagatesError
```

### `GeminiRepositoryImplTest.kt`

```
generateDescription_firstKeySuccess_returnsText
generateDescription_firstKeyFails_triesSecond
generateDescription_allKeysFail_returnsExhaustedError
generateDescription_noKeysConfigured_returnsFailure
```

---

## Task M06-T28 — Build Gate

- [ ] `./gradlew lint` → zero new warnings
- [ ] `./gradlew detekt` → zero violations
- [ ] `./gradlew assembleDebug` → `BUILD SUCCESSFUL`
- [ ] `./gradlew test` → all pass
- [ ] `./gradlew koverReport` → ≥ 80%
- [ ] Update `ANDROID_PROGRESS.md`
- [ ] Create `CODE_REVIEW_MODULE_06.md`

---

## 34. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Enter form via AddOverlay "I'm Hosting" | FAB → `AddOverlaySheet` → `ListingFormScreen` | ☐ |
| Step progress bar animates on advance | `ProgressStepBar` `animateDpAsState` | ☐ |
| Step slides left on advance, right on back | `AnimatedContent` directional transition | ☐ |
| Step 1 — select intent, checkmark appears | `IntentCard` selected state | ☐ |
| Step 2 — select property type chip | `RentoChip` single-select | ☐ |
| Step 3 — suitable for, duration, available from | chip selections | ☐ |
| Step 3 — hourly: add/remove time slots | `addTimeSlot`, `removeTimeSlot` | ☐ |
| Step 3 — custom date picker opens | `DatePickerDialog` | ☐ |
| Step 4 — price input, negotiate toggle | `BasicTextField` + `ToggleSwitch` | ☐ |
| Step 4 — duration chip updates section label | label reactive to `data.duration` | ☐ |
| Step 5 — map area tap opens city picker | `CityPickerSheet` | ☐ |
| Step 5 — use current location | permission + location | ☐ |
| Step 6 — bedrooms/baths/furnished chips | single-select per group | ☐ |
| Step 7 — amenity tile toggles selected state | multi-select, DarkPriM fill | ☐ |
| Step 8 — toggle row enables distance pill | `ToggleSwitch` → distance pill visible | ☐ |
| Step 8 — distance pill tap shows slider | `distanceEditing` state | ☐ |
| Step 9 — photo slot adds image via picker | `PickVisualMedia` | ☐ |
| Step 9 — delete button removes photo | overlay X button | ☐ |
| Step 9 — first slot always shows COVER label | `isCover = index == 0` | ☐ |
| Step 10 — AI summary auto-generated on entry | `LaunchedEffect(Unit)` if blank | ☐ |
| Step 10 — shimmer shown during generation | `isGeneratingSummary` | ☐ |
| Step 10 — Gemini failure shows inline message | `summaryError != null` | ☐ |
| Step 10 — regenerate button re-calls Gemini | `vm.generateAISummary()` | ☐ |
| Step 10 — description field is editable | `BasicTextField` | ☐ |
| Step 11 — preview card shows first image | first image URL | ☐ |
| Step 11 — publish slot indicator correct | `publishedCount + 1 of max` | ☐ |
| Step 11 — at limit: warning shown | `publishedCount >= max` | ☐ |
| Publish tap at limit → limit sheet shown | `showPublishLimitSheet = true` | ☐ |
| Limit sheet — Upgrade Plan navigates to packages | `onUpgrade` | ☐ |
| Limit sheet — Save as Draft saves draft | `onSaveDraft` | ☐ |
| Publish success → success screen | `submitSuccess = true` | ☐ |
| Success screen — requireApproval=true: review text | `requireApproval` flag | ☐ |
| Success screen — auto-navigate after 4s | `LaunchedEffect + delay(4_000)` | ☐ |
| Save Draft → success screen (draft) | `submitAsDraft = true` | ☐ |
| "Save Draft" header link saves and navigates | `vm.saveAsDraft()` | ☐ |
| Draft auto-saved on every step advance | `saveDraft(formData)` in `advanceStep()` | ☐ |
| Draft restored on re-entry | `loadDraft()` in `initForm` | ☐ |
| Edit mode — all steps pre-populated | `loadExistingListing` + `toFormData()` | ☐ |
| Edit mode — step 11 shows "Review Changes" | `isEditMode` flag in `getStepMeta` | ☐ |
| Edit mode — save: published+approval → pending | status transition logic | ☐ |
| Edit mode — deleted images removed from Storage | `deletedImageUrls` tracked | ☐ |

---

## 35. CODE_REVIEW_MODULE_06.md Template

```markdown
# Code Review — Module 06: Property Listing Form
**Date:** YYYY-MM-DD
**Spec version:** ANDROID_MODULE_06.md v1.0.0

## ✅ Architecture
- [ ] `ListingFormData` in domain — no Android imports; `imageUris` handled via `@Transient` or separate ViewModel list
- [ ] `StorageRepository`, `GeminiRepository` in domain/repository
- [ ] All use cases call repositories — no direct Firestore calls in ViewModel
- [ ] `ListingDraftDataStore` uses DataStore Preferences — not SharedPreferences
- [ ] Gemini key rotation implemented: 4 keys, logKeyFailure per failure, all_keys_exhausted error

## ✅ Design Reference
| Component | Spec ✓ |
|-----------|--------|
| ProgressStepBar (22dp active, 5dp inactive, 300ms animate) | |
| IntentCard (58dp icon circle, checkmark, DarkPriM selected) | |
| Step 4 price box (22dp corner, 22dp padding, 34sp price text) | |
| Step 8 distance pill (DarkPriM, editable on tap) | |
| Step 9 photo slots (120dp, dashed border, COVER label) | |
| Step 10 shimmer + editable field + inline Gemini error | |
| Publish Limit GlassBottomSheet (Section 32.10) | |
| Success screen (MeshBackground, bounce, 4s auto-navigate) | |

## ✅ Code Quality
- [ ] `./gradlew test` → PASSING
- [ ] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [ ] Image compression loop verified (max 1.5MB, quality ladder 85→30)
- [ ] Edit mode status transitions all 4 paths covered
- [ ] `imePadding()` on root → keyboard doesn't clip footer
- [ ] `navigationBarsPadding()` on footer
```

---

*End of Module 06 — Property Listing Form v1.0.0*
*Depends on: Modules 01–05. Next module: Module 07 — Tenant Request Form.*
