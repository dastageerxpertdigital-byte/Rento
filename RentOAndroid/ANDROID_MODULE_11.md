# RentO — Android App
## Module 11 — My Listings & My Requests (Dashboard)
### Complete Engineering Specification

> **Version:** 1.0.0 | **Branch:** `feature/module-11-dashboard`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 04 ✅ · Module 06 ✅ · Module 07 ✅

---

## 1. Module Overview

Module 11 delivers two owner-dashboard screens:
- **My Listings** (`my_listings`) — host's listings across 5 status tabs: Published · Drafts · Pending · Rejected · Blocked
- **My Requests** (`my_requests`) — seeker's requests across 4 status tabs: Active · Pending · Closed · Rejected

Both screens share the same structural shell (header + slot-usage banner + horizontal tab chips + tab content). Each tab shows status-specific card variants with action buttons.

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   └── usecase/
│       ├── listing/
│       │   ├── GetMyListingsUseCase.kt
│       │   └── DeleteDraftListingUseCase.kt
│       └── request/
│           ├── GetMyRequestsUseCase.kt
│           └── CloseRequestUseCase.kt
├── presentation/
│   ├── listing/
│   │   └── dashboard/
│   │       ├── MyListingsViewModel.kt
│   │       ├── MyListingsScreen.kt
│   │       └── components/
│   │           ├── PublishedListingCard.kt
│   │           ├── DraftListingCard.kt
│   │           ├── PendingListingCard.kt
│   │           ├── RejectedListingCard.kt
│   │           ├── BlockedListingCard.kt
│   │           ├── PublishSlotBanner.kt
│   │           └── DashboardTabRow.kt
│   └── request/
│       └── dashboard/
│           ├── MyRequestsViewModel.kt
│           ├── MyRequestsScreen.kt
│           └── components/
│               ├── ActiveRequestCard.kt
│               ├── RequestSlotBanner.kt
│               └── StatusRequestCard.kt
└── di/
    └── DashboardModule.kt
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M11-T01 | Use cases — `GetMyListingsUseCase`, `GetMyRequestsUseCase`, `CloseRequestUseCase` | ☐ |
| M11-T02 | `MyListingsViewModel` — load, tabs, unpublish/publish via M04 use cases | ☐ |
| M11-T03 | `MyRequestsViewModel` — load, tabs, close request | ☐ |
| M11-T04 | Koin module — `DashboardModule` | ☐ |
| M11-T05 | Nav wiring — `my_listings` + `my_requests` | ☐ |
| M11-T06 | `PublishSlotBanner` shared component | ☐ |
| M11-T07 | `DashboardTabRow` shared component (horizontal scrollable chips) | ☐ |
| M11-T08 | `MyListingsScreen` shell + all 5 tab content composables | ☐ |
| M11-T09 | `MyRequestsScreen` shell + all 4 tab content composables | ☐ |
| M11-T10 | String resources | ☐ |
| M11-T11 | Unit tests + build gate | ☐ |

---

## 4. Architecture

### 4.1 UI States

```kotlin
data class MyListingsUiState(
    val published: List<Listing> = emptyList(),
    val drafts: List<Listing> = emptyList(),
    val pending: List<Listing> = emptyList(),
    val rejected: List<Listing> = emptyList(),
    val blocked: List<Listing> = emptyList(),
    val selectedTab: Int = 0,        // 0=Published, 1=Drafts, 2=Pending, 3=Rejected, 4=Blocked
    val isLoading: Boolean = true,
    val error: String? = null,
    val publishedCount: Int = 0,
    val maxPublishedListings: Int = 2,
)

data class MyRequestsUiState(
    val active: List<TenantRequest> = emptyList(),
    val pending: List<TenantRequest> = emptyList(),
    val closed: List<TenantRequest> = emptyList(),
    val rejected: List<TenantRequest> = emptyList(),
    val selectedTab: Int = 0,       // 0=Active, 1=Pending, 2=Closed, 3=Rejected
    val isLoading: Boolean = true,
    val error: String? = null,
    val activeCount: Int = 0,
    val maxPublishedRequests: Int = 2,
)
```

### 4.2 Use Cases

```kotlin
class GetMyListingsUseCase(
    private val repo: ListingRepository,
    private val auth: AuthRepository,
) {
    suspend operator fun invoke(): Result<List<Listing>> {
        val uid = auth.getCurrentUserId() ?: return Result.success(emptyList())
        return repo.getUserListings(uid)   // returns ALL statuses
    }
}

class GetMyRequestsUseCase(
    private val repo: TenantRequestRepository,
    private val auth: AuthRepository,
) {
    suspend operator fun invoke(): Result<List<TenantRequest>> {
        val uid = auth.getCurrentUserId() ?: return Result.success(emptyList())
        return repo.getUserRequests(uid)   // returns ALL statuses
    }
}

class CloseRequestUseCase(private val repo: TenantRequestRepository) {
    suspend operator fun invoke(requestId: String): Result<Unit> =
        repo.updateRequestStatus(requestId, "closed")
}
```

> Add `TenantRequestRepository.updateRequestStatus(requestId, status)` and `ListingRepository.getUserListings(uid)` methods.

### 4.3 ViewModels

**`MyListingsViewModel`:**
```kotlin
init {
    viewModelScope.launch {
        getMyListings().fold(
            onSuccess = { all ->
                _uiState.update { state ->
                    state.copy(
                        published      = all.filter { it.status == "published" },
                        drafts         = all.filter { it.status == "draft" },
                        pending        = all.filter { it.status == "pending_approval" },
                        rejected       = all.filter { it.status == "rejected" },
                        blocked        = all.filter { it.status == "blocked" },
                        publishedCount = all.count { it.status == "published" },
                        isLoading      = false,
                    )
                }
                loadUserPlan()
            },
            onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
        )
    }
}
fun setTab(tab: Int) = _uiState.update { it.copy(selectedTab = tab) }
// Reuse unpublish/publish from M04 use cases
```

**`MyRequestsViewModel`** — same pattern splitting on request `status`.

---

## 5. `PublishSlotBanner` — Exact Specification (§18.1)

```
Box(
  fillMaxWidth,
  background = RentoColors.primaryTint,
  border     = 1.dp RentoColors.primaryRing,
  shape      = RoundedCornerShape(18.dp),
  padding    = 14.dp V / 16.dp H,
):
  Column:
    Row(fillMaxWidth, SpaceBetween):
      Text("Publish Slots", 13sp, RentoColors.t1)
      Text("$used / $max", 13sp Bold, RentoColors.primary)

    Spacer(8.dp)

    LinearProgressIndicator(
      progress   = { (used.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
      modifier   = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
      color      = RentoColors.primary,      // DarkPri→DarkPri2 gradient approximated
      trackColor = RentoColors.bg4,
    )

    Spacer(8.dp)

    Row:
      Text("Free plan · ", 11sp, RentoColors.t2)
      Text(
        "Upgrade →",
        11sp Bold, RentoColors.primary,
        modifier = Modifier.clickable { onUpgrade() },
      )
```

---

## 6. `DashboardTabRow` — Horizontal Scrollable Chips

```kotlin
@Composable
fun DashboardTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
) {
    LazyRow(
        contentPadding   = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier         = Modifier.padding(bottom = 20.dp),
    ) {
        itemsIndexed(tabs) { idx, label ->
            val selected = idx == selectedTab
            Box(
                modifier = Modifier
                    .background(
                        if (selected) MaterialTheme.rentoColors.primary else MaterialTheme.rentoColors.bg2,
                        RoundedCornerShape(100.dp),
                    )
                    .border(
                        1.dp,
                        if (selected) MaterialTheme.rentoColors.primary else MaterialTheme.rentoColors.border,
                        RoundedCornerShape(100.dp),
                    )
                    .clickable { onSelectTab(idx) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.rentoTypography.labelM,
                    color = if (selected) Color.White else MaterialTheme.rentoColors.t1,
                )
            }
        }
    }
}
```

---

## 7. My Listings — Tab Cards

### Published Tab — `PublishedListingCard`

```
Row(
  fillMaxWidth,
  background = RentoColors.bg2,
  border     = 1.5.dp RentoColors.border,
  shape      = RoundedCornerShape(20.dp),
):
  // Image area (88dp wide)
  Box(
    width=88.dp, fillMaxHeight,
    background = Brush.verticalGradient([RentoColors.primary, RentoColors.secondary]),
    clip = RoundedCornerShape(topStart=20.dp, bottomStart=20.dp),
  ):
    // Emoji watermark at 45% opacity — from listing.propertyType
    Text(propertyEmoji, 32sp, Color.White.copy(alpha=0.45f), centred)

  // Content (14dp padding)
  Column(Modifier.weight(1f).padding(14.dp)):
    Text(listing.title, 13sp Bold, RentoColors.t0, maxLines=1, overflow=Ellipsis)
    Spacer(4.dp)
    Text("${listing.city} · ${listing.propertyType}", 12sp, RentoColors.t2)
    Spacer(6.dp)
    Row(CenterVertically, spacedBy(8.dp)):
      Badge(label="● Live", style=BadgeStyle.PRIMARY)
      Text("PKR ${formatPrice(listing.price)}/mo", 13sp Bold, RentoColors.primary)
    Spacer(11.dp)
    Row(spacedBy(8.dp)):
      GhostButton("Edit",      onClick = { onEdit(listing.id) },      Modifier.weight(1f))
      GhostButton("Unpublish", onClick = { onUnpublish(listing.id) }, Modifier.weight(1f))
```

### Drafts Tab — `DraftListingCard`

Same horizontal card style but:
- No "Live" badge; shows completion % text (`"${pct}% complete"`)
- `LinearProgressIndicator` bar (4dp, DarkPri fill)
- Single `PrimaryButton("Continue")` replacing action row

### Pending Tab — `PendingListingCard`

```
Box(
  fillMaxWidth, centred column,
  background = RentoColors.primaryTint,
  shape = RoundedCornerShape(20.dp),
  padding = 20.dp,
):
  Text("⏳", 44sp)
  Spacer(8.dp)
  Text("Awaiting Approval", 16sp Bold, RentoColors.t0, centred)
  Spacer(4.dp)
  Text("Usually reviewed within a few hours.", 13sp, RentoColors.t2, centred)
  Spacer(12.dp)
  Text(listing.title, 13sp Bold, RentoColors.primary, centred, maxLines=1)
```

### Rejected Tab — `RejectedListingCard`

```
Column(
  fillMaxWidth,
  background = RentoColors.redTint,
  border     = 1.dp RentoColors.red.copy(alpha=0.22f),
  shape      = RoundedCornerShape(20.dp),
  padding    = 16.dp,
):
  Row(CenterVertically):
    Text("❌", 28sp)
    Spacer(10.dp)
    Text(listing.title, 13sp Bold, RentoColors.t0, Modifier.weight(1f), maxLines=1)
    Badge("Rejected", BadgeStyle.REJECTED)

  Spacer(10.dp)

  // Admin note card
  Box(
    fillMaxWidth,
    background = RentoColors.redTint,
    shape = RoundedCornerShape(11.dp),
    padding = 11.dp V / 13.dp H,
  ):
    Text(listing.rejectionReason ?: "No reason provided.", 13sp, RentoColors.t1)

  Spacer(12.dp)

  PrimaryButton("Edit & Resubmit", fillMaxWidth, onClick = { onEdit(listing.id) })
```

### Blocked Tab — `BlockedListingCard`

Same as `RejectedListingCard` but:
- Title area says "🚫" emoji
- Badge is "Blocked" (`BadgeStyle.BLOCKED`)
- Action is `GhostButton("Contact Support")` → opens `mailto:support@rento.app`

---

## 8. My Requests — Tab Cards

### Active Tab — `ActiveRequestCard`

Uses shared `TenantRequestCard` with additional action row:
```
// Below the card content:
Row(spacedBy=8.dp):
  GhostButton("Edit",  Modifier.weight(1f), onClick = { onEditRequest(req.id) })
  GhostButton("Close", Modifier.weight(1f),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = RentoColors.red),
    onClick = { onCloseRequest(req.id) },
  )
```

### Pending Tab — same `PendingListingCard` shell adapted with request title.

### Closed / Rejected Tabs — `StatusRequestCard`:
```
// Closed: greyed out TenantRequestCard + "Closed" badge + "Repost" PrimaryButton
// Rejected: red tint card + admin reason + "Edit & Resubmit" PrimaryButton
```

---

## 9. String Resources

```xml
<!-- ─── My Listings ──────────────────────────────────────────────────────── -->
<string name="my_listings_title">My Listings</string>
<string name="my_listings_new_btn">New +</string>
<string name="my_listings_slots_label">Publish Slots</string>
<string name="my_listings_free_plan">Free plan · </string>
<string name="my_listings_upgrade">Upgrade →</string>
<string name="my_listings_tab_published">Published</string>
<string name="my_listings_tab_drafts">Drafts</string>
<string name="my_listings_tab_pending">Pending</string>
<string name="my_listings_tab_rejected">Rejected</string>
<string name="my_listings_tab_blocked">Blocked</string>
<string name="my_listings_pending_title">Awaiting Approval</string>
<string name="my_listings_pending_body">Usually reviewed within a few hours.</string>
<string name="my_listings_rejected_reason_fallback">No reason provided.</string>
<string name="my_listings_edit_resubmit">Edit &amp; Resubmit</string>
<string name="my_listings_contact_support">Contact Support</string>
<string name="my_listings_btn_edit">Edit</string>
<string name="my_listings_btn_unpublish">Unpublish</string>
<string name="my_listings_btn_continue">Continue</string>

<!-- ─── My Requests ──────────────────────────────────────────────────────── -->
<string name="my_requests_title">My Requests</string>
<string name="my_requests_new_btn">New +</string>
<string name="my_requests_slots_label">Request Slots</string>
<string name="my_requests_tab_active">Active</string>
<string name="my_requests_tab_pending">Pending</string>
<string name="my_requests_tab_closed">Closed</string>
<string name="my_requests_tab_rejected">Rejected</string>
<string name="my_requests_btn_edit">Edit</string>
<string name="my_requests_btn_close">Close</string>
<string name="my_requests_btn_repost">Repost</string>
```

---

## 10. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| My Listings — all 5 tabs load correctly | status filter in ViewModel | ☐ |
| Publish slot banner shows correct used/max | `publishedCount` / `maxPublishedListings` | ☐ |
| Published tab — "Edit" navigates to listing form edit | `onEdit(id)` | ☐ |
| Published tab — "Unpublish" triggers M04 unpublish flow | `updateListingStatus("unpublished")` | ☐ |
| Drafts tab — completion % progress bar shown | `completionPercent` computed | ☐ |
| Drafts tab — "Continue" navigates to listing form | `onContinueDraft(id)` | ☐ |
| Pending tab — hourglass + "Awaiting Approval" | `PendingListingCard` | ☐ |
| Rejected tab — admin rejection reason shown | `listing.rejectionReason` | ☐ |
| Rejected tab — "Edit & Resubmit" navigates to form | `onEdit(id)` | ☐ |
| Blocked tab — "Contact Support" opens mail | `mailto:` intent | ☐ |
| "New +" → navigates to Listing Form | `navController.navigate(listing/form)` | ☐ |
| My Requests — Active tab — "Edit" + "Close" buttons | `ActiveRequestCard` | ☐ |
| Close request → status updated to "closed" | `CloseRequestUseCase` | ☐ |
| "New +" → navigates to Tenant Request Form | `navController.navigate(request/form)` | ☐ |
| Request slot banner shown | `RequestSlotBanner` | ☐ |

---

## 11. CODE_REVIEW_MODULE_11.md Template

```markdown
# Code Review — Module 11: My Listings & My Requests
**Spec version:** ANDROID_MODULE_11.md v1.0.0

## ✅ Architecture
- [ ] Listings filtered client-side by status from single `getUserListings()` call
- [ ] `DashboardTabRow` is horizontal scrollable chips (not Material TabRow)
- [ ] `PublishSlotBanner` uses `primaryTint` bg + `primaryRing` border
- [ ] Progress bar: 4dp height, DarkPri fill, DarkBg4 track

## ✅ Card Specs
- [ ] Published card image area: 88dp wide, gradient, emoji watermark at 45% opacity
- [ ] Pending card: primaryTint fill, hourglass emoji 44sp
- [ ] Rejected card: redTint fill, border alpha 0.22
- [ ] Rejected admin note: 11dp corner, redTint bg, 11dp/13dp padding

## ✅ Build
- [ ] `./gradlew assembleDebug` → ✅
- [ ] `./gradlew test` → ✅
```

*End of Module 11 — My Listings & My Requests v1.0.0*
*Next: Module 12 — Profile & Edit Profile.*
