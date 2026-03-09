# RentO — Android App
## Module 14 — Map View
### Complete Engineering Specification

> **Version:** 1.0.0  
> **Branch:** `feature/module-14-map-view`  
> **Depends on:** Modules 01–13 ✅  
> **Audience:** Android Agent  
>
> ⚠️ Uses Google Maps Compose SDK + Maps Utility Library for clustering. All map state uses `remember`. Markers fetched by bounding box only — never a full collection scan.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture](#4-architecture)
5. [Task M14-T01 — Dependencies & Manifest](#task-m14-t01--dependencies--manifest)
6. [Task M14-T02 — Domain Models](#task-m14-t02--domain-models)
7. [Task M14-T03 — `MapRepository` Interface + Impl](#task-m14-t03--maprepository-interface--impl)
8. [Task M14-T04 — Use Cases](#task-m14-t04--use-cases)
9. [Task M14-T05 — `MapViewModel`](#task-m14-t05--mapviewmodel)
10. [Task M14-T06 — Koin Module](#task-m14-t06--koin-module)
11. [Task M14-T07 — Navigation Wiring](#task-m14-t07--navigation-wiring)
12. [Task M14-T08 — `MapScreen` — Shell](#task-m14-t08--mapscreen--shell)
13. [Task M14-T09 — Mode Toggle (Listings / Requests)](#task-m14-t09--mode-toggle-listings--requests)
14. [Task M14-T10 — Custom Listing Markers + Cluster Markers](#task-m14-t10--custom-listing-markers--cluster-markers)
15. [Task M14-T11 — Custom Request Circle Markers](#task-m14-t11--custom-request-circle-markers)
16. [Task M14-T12 — Listing Bottom Sheet Preview](#task-m14-t12--listing-bottom-sheet-preview)
17. [Task M14-T13 — Shimmer / Fallback State](#task-m14-t13--shimmer--fallback-state)
18. [Task M14-T14 — String Resources](#task-m14-t14--string-resources)
19. [Task M14-T15 — Unit Tests](#task-m14-t15--unit-tests)
20. [Task M14-T16 — Build Gate](#task-m14-t16--build-gate)
21. [Journey Coverage Checklist](#21-journey-coverage-checklist)
22. [CODE_REVIEW_MODULE_14.md Template](#22-code_review_module_14md-template)

---

## 1. Module Overview

Module 14 delivers the **Map View** — a full-screen Google Maps Compose screen accessible from the bottom navigation bar.

**Key features:**
- **Full-screen Google Maps Compose** — no UI chrome behind the map; only a floating mode toggle at top and a floating FAB-area at bottom.
- **Mode toggle** — switches between "Listings" pins and "Requests" circle pins. Default: Listings.
- **Listings mode:** custom markers (property type icon + abbreviated price, DarkPri background). Clustering via Google Maps Utility Library. Tapping a marker/cluster → `ModalBottomSheet` card preview.
- **Requests mode:** circle radius pins per `TenantRequest`, centred on preferred area. DarkBlue colour. Tapping → `ModalBottomSheet` with request summary.
- **Bounding box fetch:** fetches markers only for the visible map region. Updates on camera idle.
- **Listing sheet:** 100dp gradient image area, title/city/price/type, "View Details" → `ListingDetailScreen`.
- **Request sheet:** requester name + budget + area + radius.
- **MapBackground grid fallback** used as placeholder before tiles load.

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── MapListing.kt           ← lightweight pin model (not full Listing)
│   │   └── MapRequest.kt
│   ├── repository/
│   │   └── MapRepository.kt
│   └── usecase/
│       └── map/
│           ├── GetMapListingsUseCase.kt
│           └── GetMapRequestsUseCase.kt
├── data/
│   └── repository/
│       └── MapRepositoryImpl.kt
└── presentation/
    └── map/
        ├── MapViewModel.kt
        ├── MapScreen.kt
        └── components/
            ├── MapModeToggle.kt
            ├── ListingMarkerContent.kt       ← renders Bitmap for marker
            ├── RequestCircleMarker.kt
            ├── ListingMapSheet.kt
            └── RequestMapSheet.kt
di/
└── MapModule.kt
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M14-T01 | Google Maps Compose dependency + Maps API key | ☐ |
| M14-T02 | `MapListing` + `MapRequest` domain models | ☐ |
| M14-T03 | `MapRepository` interface + impl (bounding box query) | ☐ |
| M14-T04 | `GetMapListingsUseCase` + `GetMapRequestsUseCase` | ☐ |
| M14-T05 | `MapViewModel` — camera idle → fetch, mode switch, sheet open/close | ☐ |
| M14-T06 | Koin module | ☐ |
| M14-T07 | Navigation — wire `map` route | ☐ |
| M14-T08 | `MapScreen` shell — Google Maps Compose full-screen | ☐ |
| M14-T09 | Mode toggle chip — Listings / Requests floating at top | ☐ |
| M14-T10 | Custom listing markers + cluster marker icon | ☐ |
| M14-T11 | Request circle pins | ☐ |
| M14-T12 | Listing bottom sheet preview | ☐ |
| M14-T13 | Fallback state — `MapBackground` before tiles | ☐ |
| M14-T14 | String resources | ☐ |
| M14-T15 | Unit tests | ☐ |
| M14-T16 | Build gate | ☐ |

---

## 4. Architecture

### 4.1 Domain Models

```kotlin
// MapListing.kt — lightweight, only what a pin needs
data class MapListing(
    val id: String,
    val title: String,
    val city: String,
    val propertyType: String,
    val price: Int,
    val priceLabel: String,           // formatted e.g. "30k"
    val photoUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val intent: String,               // "share" | "fullRent" | "hourly"
)

// MapRequest.kt
data class MapRequest(
    val id: String,
    val requesterName: String,
    val budgetMax: Int,
    val preferredAreas: List<String>,
    val latitude: Double,             // centroid of preferred area (approx)
    val longitude: Double,
    val radiusKm: Int,
    val propertyType: String,
)
```

> **Geolocation:** Listings must store `latitude` and `longitude` in Firestore as number fields. If not yet in schema, add `latitude: Double` and `longitude: Double` to the `listings` Firestore schema and to `ListingFormViewModel` Step 5 (Location) — store coordinates alongside province/city after reverse geocoding or pin drop. Tenant requests do NOT have exact coordinates — use a city-level centroid from a static lookup table.

### 4.2 `MapRepository` Interface

```kotlin
interface MapRepository {
    suspend fun getListingsInBounds(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double,
    ): Result<List<MapListing>>

    suspend fun getRequestsInBounds(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double,
    ): Result<List<MapRequest>>
}
```

### 4.3 `MapUiState`

```kotlin
enum class MapMode { LISTINGS, REQUESTS }

data class MapUiState(
    val mode: MapMode = MapMode.LISTINGS,
    val listings: List<MapListing> = emptyList(),
    val requests: List<MapRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Bottom sheet
    val selectedListing: MapListing? = null,
    val selectedRequest: MapRequest? = null,
    val showSheet: Boolean = false,
)
```

---

## Task M14-T01 — Dependencies & Manifest

### Sub-tasks
- [ ] **M14-T01-A** Add to `app/build.gradle.kts`:
```kotlin
implementation("com.google.maps.android:maps-compose:4.3.3")
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.maps.android:android-maps-utils:3.8.2")   // clustering
```
- [ ] **M14-T01-B** Add Maps API key to `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```
- [ ] **M14-T01-C** Add `MAPS_API_KEY` to `local.properties` and read via `manifestPlaceholders` in `build.gradle.kts`:
```kotlin
// in android { defaultConfig { } }
manifestPlaceholders["MAPS_API_KEY"] = properties["MAPS_API_KEY"] as? String ?: ""
```

---

## Task M14-T02 — Domain Models

### Sub-tasks
- [ ] **M14-T02-A** Create `domain/model/MapListing.kt` and `domain/model/MapRequest.kt` per section 4.1.
- [ ] **M14-T02-B** Verify: zero Android imports.

---

## Task M14-T03 — `MapRepository` Interface + Impl

**File:** `data/repository/MapRepositoryImpl.kt`

Firestore does not support native geospatial queries. Use a **bounding box** on `latitude`/`longitude` fields with two `whereGreaterThanOrEqualTo` + `whereLessThanOrEqualTo` chained queries (Firestore only allows range filters on one field at a time, so filter on `latitude` in Firestore and filter `longitude` client-side):

```kotlin
class MapRepositoryImpl(private val firestore: FirebaseFirestore) : MapRepository {

    override suspend fun getListingsInBounds(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double,
    ): Result<List<MapListing>> = runCatching {
        firestore.collection("listings")
            .whereEqualTo("status", "published")
            .whereGreaterThanOrEqualTo("latitude", minLat)
            .whereLessThanOrEqualTo("latitude", maxLat)
            .limit(200)
            .get().await()
            .documents
            .mapNotNull { doc ->
                val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                if (lng < minLng || lng > maxLng) return@mapNotNull null
                doc.toMapListing()
            }
    }

    override suspend fun getRequestsInBounds(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double,
    ): Result<List<MapRequest>> = runCatching {
        firestore.collection("tenantRequests")
            .whereEqualTo("status", "active")
            .whereGreaterThanOrEqualTo("latitude", minLat)
            .whereLessThanOrEqualTo("latitude", maxLat)
            .limit(200)
            .get().await()
            .documents
            .mapNotNull { doc ->
                val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                if (lng < minLng || lng > maxLng) return@mapNotNull null
                doc.toMapRequest()
            }
    }
}

private fun DocumentSnapshot.toMapListing(): MapListing? = runCatching {
    val price = getLong("price")?.toInt() ?: 0
    MapListing(
        id           = id,
        title        = getString("title") ?: "",
        city         = getString("city") ?: "",
        propertyType = getString("propertyType") ?: "",
        price        = price,
        priceLabel   = formatPriceAbbrev(price),
        photoUrl     = (get("photos") as? List<*>)?.firstOrNull() as? String,
        latitude     = getDouble("latitude") ?: 0.0,
        longitude    = getDouble("longitude") ?: 0.0,
        intent       = getString("intent") ?: "",
    )
}.getOrNull()

private fun DocumentSnapshot.toMapRequest(): MapRequest? = runCatching {
    MapRequest(
        id             = id,
        requesterName  = getString("requesterName") ?: "",
        budgetMax      = getLong("budgetMax")?.toInt() ?: 0,
        preferredAreas = (get("preferredAreas") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        latitude       = getDouble("latitude") ?: 0.0,
        longitude      = getDouble("longitude") ?: 0.0,
        radiusKm       = getLong("radiusKm")?.toInt() ?: 5,
        propertyType   = getString("propertyType") ?: "",
    )
}.getOrNull()

private fun formatPriceAbbrev(price: Int): String = when {
    price >= 100_000 -> "${price / 1_000}k"
    price >= 10_000  -> "${price / 1_000}k"
    else             -> price.toString()
}
```

> **Firestore composite index required:** Create index on `listings` for `(status ASC, latitude ASC)` and `(status ASC, latitude DESC)` in Firebase Console → Firestore → Indexes. Add index creation to `CODE_REVIEW` checklist.

### Sub-tasks
- [ ] **M14-T03-A** Create `domain/repository/MapRepository.kt`.
- [ ] **M14-T03-B** Implement `MapRepositoryImpl` with bounding box query + client-side longitude filter.
- [ ] **M14-T03-C** Document Firestore composite indexes needed in `CODE_REVIEW_MODULE_14.md`.

---

## Task M14-T04 — Use Cases

```kotlin
class GetMapListingsUseCase(private val repo: MapRepository) {
    suspend operator fun invoke(bounds: LatLngBounds): Result<List<MapListing>> =
        repo.getListingsInBounds(
            bounds.southwest.latitude, bounds.northeast.latitude,
            bounds.southwest.longitude, bounds.northeast.longitude,
        )
}

class GetMapRequestsUseCase(private val repo: MapRepository) {
    suspend operator fun invoke(bounds: LatLngBounds): Result<List<MapRequest>> =
        repo.getRequestsInBounds(
            bounds.southwest.latitude, bounds.northeast.latitude,
            bounds.southwest.longitude, bounds.northeast.longitude,
        )
}
```

> `LatLngBounds` from `com.google.android.gms.maps.model` — allowed in domain use cases only via a wrapper if desired, but for simplicity use it directly since it is a Maps SDK value type.

---

## Task M14-T05 — `MapViewModel`

**File:** `presentation/map/MapViewModel.kt`

```kotlin
class MapViewModel(
    private val getListings: GetMapListingsUseCase,
    private val getRequests: GetMapRequestsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // ── Mode ─────────────────────────────────────────────────────────
    fun setMode(mode: MapMode) {
        _uiState.update { it.copy(mode = mode, showSheet = false, selectedListing = null, selectedRequest = null) }
    }

    // ── Bounds fetch (called on camera idle) ─────────────────────────
    fun onCameraIdle(bounds: LatLngBounds) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (_uiState.value.mode) {
                MapMode.LISTINGS -> getListings(bounds).fold(
                    onSuccess = { pins -> _uiState.update { it.copy(listings = pins, isLoading = false) } },
                    onFailure = { e  -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
                )
                MapMode.REQUESTS -> getRequests(bounds).fold(
                    onSuccess = { pins -> _uiState.update { it.copy(requests = pins, isLoading = false) } },
                    onFailure = { e  -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
                )
            }
        }
    }

    // ── Sheet ─────────────────────────────────────────────────────────
    fun selectListing(listing: MapListing) =
        _uiState.update { it.copy(selectedListing = listing, selectedRequest = null, showSheet = true) }

    fun selectRequest(request: MapRequest) =
        _uiState.update { it.copy(selectedRequest = request, selectedListing = null, showSheet = true) }

    fun dismissSheet() =
        _uiState.update { it.copy(showSheet = false, selectedListing = null, selectedRequest = null) }
}
```

---

## Task M14-T06 — Koin Module

**File:** `di/MapModule.kt`

```kotlin
val mapModule = module {
    single<MapRepository>        { MapRepositoryImpl(get()) }
    factory { GetMapListingsUseCase(get()) }
    factory { GetMapRequestsUseCase(get()) }
    viewModel { MapViewModel(get(), get()) }
}
```

Add to `RentoApplication.startKoin { modules(…, mapModule) }`.

---

## Task M14-T07 — Navigation Wiring

```kotlin
composable("map") {
    MapScreen(
        onNavigateToListingDetail  = { listingId -> navController.navigate("listing/$listingId") },
        onNavigateToRequestDetail  = { requestId -> navController.navigate("request/$requestId") },
    )
}
```

---

## Task M14-T08 — `MapScreen` Shell

**File:** `presentation/map/MapScreen.kt`

```kotlin
@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(),
    onNavigateToListingDetail: (String) -> Unit,
    onNavigateToRequestDetail: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Default camera position: Karachi, Pakistan
    val defaultCameraPos = remember {
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(LatLng(24.8607, 67.0011), 12f)
        )
    }

    Box(Modifier.fillMaxSize()) {
        // ── Google Map ───────────────────────────────────────────────
        GoogleMap(
            modifier            = Modifier.fillMaxSize(),
            cameraPositionState = defaultCameraPos,
            uiSettings          = MapUiSettings(
                zoomControlsEnabled    = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled      = false,
            ),
            properties = MapProperties(
                mapType            = MapType.NORMAL,
                isMyLocationEnabled = false,   // request location permission separately
            ),
            onMapClick = { viewModel.dismissSheet() },
        ) {
            if (uiState.mode == MapMode.LISTINGS) {
                // Cluster + individual markers (see M14-T10)
                ListingMarkers(
                    listings = uiState.listings,
                    onSelect = { viewModel.selectListing(it) },
                )
            } else {
                // Request circle overlays (see M14-T11)
                uiState.requests.forEach { req ->
                    RequestCircleMarker(
                        request  = req,
                        onSelect = { viewModel.selectRequest(it) },
                    )
                }
            }
        }

        // Camera idle listener — fetch on move
        LaunchedEffect(defaultCameraPos.isMoving) {
            if (!defaultCameraPos.isMoving) {
                defaultCameraPos.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                    viewModel.onCameraIdle(bounds)
                }
            }
        }

        // ── Mode toggle — floating top-centre ───────────────────────
        MapModeToggle(
            selectedMode = uiState.mode,
            onSelect     = { viewModel.setMode(it) },
            modifier     = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp),
        )

        // ── Loading indicator ────────────────────────────────────────
        if (uiState.isLoading) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth().align(Alignment.TopStart).statusBarsPadding(),
                color      = MaterialTheme.rentoColors.primary,
                trackColor = Color.Transparent,
            )
        }

        // ── Bottom sheet ─────────────────────────────────────────────
        if (uiState.showSheet) {
            uiState.selectedListing?.let { listing ->
                ListingMapSheet(
                    listing               = listing,
                    onViewDetails         = { onNavigateToListingDetail(listing.id) },
                    onDismiss             = { viewModel.dismissSheet() },
                )
            }
            uiState.selectedRequest?.let { request ->
                RequestMapSheet(
                    request              = request,
                    onViewDetails        = { onNavigateToRequestDetail(request.id) },
                    onDismiss            = { viewModel.dismissSheet() },
                )
            }
        }
    }
}
```

---

## Task M14-T09 — Mode Toggle

**File:** `presentation/map/components/MapModeToggle.kt`

```
Row(
  background = RentoColors.navBg,
  border     = 1.dp RentoColors.border,
  shape      = RoundedCornerShape(100.dp),
  padding    = 4.dp,
  shadow     = 6.dp,
):
  ModeChip("Listings", selected = mode == LISTINGS, onClick = { onSelect(LISTINGS) })
  ModeChip("Requests", selected = mode == REQUESTS, onClick = { onSelect(REQUESTS) })

// ModeChip:
Box(
  background = if selected: Brush.linear(primary → secondary) else Transparent,
  shape = RoundedCornerShape(100.dp),
  padding = 8.dp V / 18.dp H,
  clickable,
):
  Text(label, 14sp SemiBold, if selected Color.White else RentoColors.t1)
```

---

## Task M14-T10 — Custom Listing Markers + Clusters

**File:** `presentation/map/components/ListingMarkerContent.kt`

**Strategy:** Use `MarkerComposable` from `maps-compose` to render a Composable as a map marker bitmap.

### Individual listing marker

```kotlin
@Composable
fun ListingMarkerContent(listing: MapListing, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier
                .background(
                    if (isSelected) MaterialTheme.rentoColors.accent
                    else Brush.linearGradient(listOf(rentoColors.primary, rentoColors.secondary)),
                    RoundedCornerShape(10.dp),
                )
                .border(1.5.dp, Color.White.copy(0.3f), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(resolvePropertyIcon(listing.propertyType), null,
                 tint = Color.White, modifier = Modifier.size(12.dp))
            Text(listing.priceLabel, 11sp Bold, Color.White)
        }
        // Drop-shadow pin tip
        Box(
            Modifier
                .size(width = 10.dp, height = 6.dp)
                .background(rentoColors.primary, TriangleShape),
        )
    }
}
```

Register marker in `GoogleMap` content:

```kotlin
// In ListingMarkers composable
uiState.listings.forEach { listing ->
    MarkerComposable(
        state   = MarkerState(position = LatLng(listing.latitude, listing.longitude)),
        onClick = { viewModel.selectListing(listing); true },
    ) {
        ListingMarkerContent(listing, isSelected = uiState.selectedListing?.id == listing.id)
    }
}
```

### Cluster marker

Using `Clustering` from `maps-compose-utils`:

```kotlin
Clustering(
    items       = uiState.listings.map { it.toClusterItem() },
    clusterContent = { cluster ->
        Box(
            Modifier
                .size(44.dp)
                .background(
                    Brush.linearGradient(listOf(rentoColors.primary, rentoColors.secondary)),
                    CircleShape,
                )
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("${cluster.size}", 13sp ExtraBold, Color.White)
        }
    },
    itemContent = { item ->
        ListingMarkerContent(item.mapListing, isSelected = false)
    },
    onClusterClick     = { cluster -> /* zoom in */ false },
    onClusterItemClick = { item -> viewModel.selectListing(item.mapListing); false },
)
```

> Implement `ClusterItem` wrapper:
```kotlin
data class ListingClusterItem(val mapListing: MapListing) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(mapListing.latitude, mapListing.longitude)
    override fun getTitle(): String = mapListing.title
    override fun getSnippet(): String? = null
    override fun getZIndex(): Float = 0f
}
fun MapListing.toClusterItem() = ListingClusterItem(this)
```

### Sub-tasks
- [ ] **M14-T10-A** Implement `ListingMarkerContent` composable.
- [ ] **M14-T10-B** Implement `ListingClusterItem` + `Clustering` composable.
- [ ] **M14-T10-C** `TriangleShape` helper: a `Shape` that draws a downward-pointing triangle for the pin tip.
- [ ] **M14-T10-D** Selected marker uses accent background (gold) to differentiate from other markers.

---

## Task M14-T11 — Request Circle Pins

**File:** `presentation/map/components/RequestCircleMarker.kt`

```kotlin
@Composable
fun RequestCircleMarker(request: MapRequest, onSelect: (MapRequest) -> Unit) {
    val position = LatLng(request.latitude, request.longitude)
    val radiusMeters = request.radiusKm * 1_000.0

    // Filled semi-transparent circle overlay
    Circle(
        center      = position,
        radius      = radiusMeters,
        fillColor   = Color(0x222ECC8A),    // DarkBlueM equivalent — use primary with low alpha
        strokeColor = Color(0xFF2ECC8A),
        strokeWidth = 2f,
        clickable   = true,
        onClick     = { onSelect(request) },
    )

    // Centre dot marker
    MarkerComposable(
        state   = MarkerState(position = position),
        onClick = { onSelect(request); true },
    ) {
        Box(
            Modifier
                .size(12.dp)
                .background(rentoColors.primary, CircleShape)
                .border(2.dp, Color.White, CircleShape),
        )
    }
}
```

> The spec says "DarkBlue" for request pins — in the design system `DarkBlue = #3B82F6`. Use `Color(0xFF3B82F6)` and `Color(0x223B82F6)` for request circles if that colour is defined in `RentoColors`. Otherwise substitute with `primary`.

---

## Task M14-T12 — Listing Bottom Sheet Preview

**File:** `presentation/map/components/ListingMapSheet.kt`

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingMapSheet(
    listing: MapListing,
    onViewDetails: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = sheetState,
        containerColor      = MaterialTheme.rentoColors.bg1,
        dragHandle          = { BottomSheetDefaults.DragHandle(color = MaterialTheme.rentoColors.border) },
    ) {
        Column(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {

            // ── Image area (100dp) ────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.linearGradient(listOf(rentoColors.primaryTint, rentoColors.bg2)),
                    ),
            ) {
                if (listing.photoUrl != null) {
                    AsyncImage(
                        model   = ImageRequest.Builder(LocalContext.current)
                            .data(listing.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = listing.title,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        resolvePropertyIcon(listing.propertyType),
                        null,
                        tint     = rentoColors.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(40.dp).align(Alignment.Center),
                    )
                }
            }

            // ── Content ───────────────────────────────────────────────
            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(listing.title, 15sp Bold, rentoColors.t0, maxLines = 1, overflow = Ellipsis)
                        Spacer(4.dp)
                        Text(listing.city, 12sp, rentoColors.t2)
                    }
                    Text("PKR ${listing.priceLabel}/mo", 14sp Bold, rentoColors.primary)
                }

                Spacer(12.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RentoChip(label = listing.propertyType, selected = false, onClick = {})
                    RentoChip(label = intentLabel(listing.intent), selected = false, onClick = {})
                }

                Spacer(16.dp)
                PrimaryButton("View Details", Modifier.fillMaxWidth(), onClick = onViewDetails)
            }
        }
    }
}
```

### `RequestMapSheet` — Exact Specification

```kotlin
@Composable
fun RequestMapSheet(request: MapRequest, onViewDetails: () -> Unit, onDismiss: () -> Unit) {
    // Same ModalBottomSheet shell as ListingMapSheet
    Column(Modifier.fillMaxWidth().padding(20.dp).padding(bottom = 24.dp)) {
        Text(request.requesterName, Fraunces 20sp SemiBold, rentoColors.t0)
        Spacer(6.dp)
        Text("Budget: PKR ${formatBudget(request.budgetMax)}/mo", 14sp, rentoColors.t1)
        Spacer(4.dp)
        Text("Looking in: ${request.preferredAreas.take(2).joinToString(", ")}", 13sp, rentoColors.t2)
        Spacer(4.dp)
        Text("Radius: ${request.radiusKm} km", 13sp, rentoColors.t2)
        Spacer(16.dp)
        PrimaryButton("View Request", Modifier.fillMaxWidth(), onClick = onViewDetails)
    }
}
```

---

## Task M14-T13 — Fallback State

Before map tiles load (or when SDK not initialised), show `MapBackground` composable filling the screen as a placeholder. This is the same `MapBackground` from Module 05/07.

```kotlin
// In MapScreen, conditionally under the GoogleMap:
// (GoogleMap itself handles tile loading — no separate fallback needed in Compose)
// The MapBackground is only relevant if GoogleMap fails to load.
// Implement: if mapLoadFailed state → show MapBackground full screen with error message overlay
```

---

## Task M14-T14 — String Resources

```xml
<string name="map_mode_listings">Listings</string>
<string name="map_mode_requests">Requests</string>
<string name="map_view_details">View Details</string>
<string name="map_view_request">View Request</string>
<string name="map_listing_sheet_price">PKR %1$s/mo</string>
<string name="map_request_budget">Budget: PKR %1$s/mo</string>
<string name="map_request_areas">Looking in: %1$s</string>
<string name="map_request_radius">Radius: %1$d km</string>
<string name="map_loading">Loading pins…</string>
```

---

## Task M14-T15 — Unit Tests

### `MapViewModelTest.kt`
```
onCameraIdle_listingsMode_fetchesListings
onCameraIdle_requestsMode_fetchesRequests
setMode_switchesToRequests_clearsSheet
selectListing_showsSheet
selectRequest_showsSheet
dismissSheet_hidesSheet
onCameraIdle_error_setsError
```

---

## Task M14-T16 — Build Gate

- [ ] `./gradlew lint` → zero new warnings.
- [ ] `./gradlew detekt` → zero violations.
- [ ] `./gradlew assembleDebug` → `BUILD SUCCESSFUL`.
- [ ] `./gradlew test` → all pass.
- [ ] Update `ANDROID_PROGRESS.md`. Create `CODE_REVIEW_MODULE_14.md`.

---

## 21. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Map tab → full-screen Google Map visible | `GoogleMap(Modifier.fillMaxSize())` | ☐ |
| Default camera: Karachi | `CameraPosition.fromLatLngZoom(LatLng(24.86, 67.00), 12f)` | ☐ |
| Mode toggle floating top-centre | `Alignment.TopCenter` overlay | ☐ |
| Listings mode is default | `MapMode.LISTINGS` initial state | ☐ |
| Camera stops moving → fetch triggered | `isMoving == false` LaunchedEffect | ☐ |
| Listing pins appear on map | `MarkerComposable` per listing | ☐ |
| Cluster badge shows count | `Clustering` content composable | ☐ |
| Tap listing marker → bottom sheet | `viewModel.selectListing()` | ☐ |
| Sheet shows image / gradient, title, city, price | `ListingMapSheet` | ☐ |
| "View Details" → Listing Detail screen | `onNavigateToListingDetail()` | ☐ |
| Tap map → sheet dismissed | `onMapClick = { viewModel.dismissSheet() }` | ☐ |
| Switch to Requests mode → listing pins removed | `mode == REQUESTS` branch | ☐ |
| Request circle overlays shown in Requests mode | `RequestCircleMarker` | ☐ |
| Tap request circle → request sheet | `viewModel.selectRequest()` | ☐ |
| "View Request" → Request Detail screen | `onNavigateToRequestDetail()` | ☐ |
| Loading progress bar while fetching | `isLoading` → `LinearProgressIndicator` | ☐ |

---

## 22. CODE_REVIEW_MODULE_14.md Template

```markdown
# Code Review — Module 14: Map View
**Branch:** feature/module-14-map-view

## ✅ Maps Setup
- [ ] `maps-compose` dependency present
- [ ] `android-maps-utils` for clustering
- [ ] Maps API key via manifest placeholder
- [ ] `MAPS_API_KEY` in local.properties (not committed to git)

## ✅ Firestore Composite Indexes Created
- [ ] `listings`: `(status ASC, latitude ASC)` composite index
- [ ] `tenantRequests`: `(status ASC, latitude ASC)` composite index
- [ ] Index creation confirmed in Firebase Console

## ✅ Architecture
- [ ] Bounding box query on camera idle — not on every recomposition
- [ ] Longitude filtered client-side (Firestore limitation)
- [ ] `limit(200)` applied — no unbounded queries
- [ ] `MapListing` is lightweight — not full `Listing` model

## ✅ Design Compliance
- [ ] Listing marker: gradient bg, property icon, price label, pin tip triangle
- [ ] Cluster: gradient circle with count badge
- [ ] Request circles: semi-transparent fill + solid stroke
- [ ] Mode toggle: pill shape, gradient fill for selected

## ✅ Code Quality
- [ ] `./gradlew assembleDebug` → ✅
- [ ] `./gradlew test` → ✅
```

*End of Module 14 — Map View v1.0.0*
*Next: Module 15 — My Dashboard (My Listings + My Requests).*
