# RentO — Android App
## Module 13 — Settings, Packages & Subscriptions, Force Update, Connectivity, Feedback
### Complete Engineering Specification

> **Version:** 1.0.0 | **Branch:** `feature/module-13-settings-packages`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 09 ✅ · Module 12 ✅

---

## 1. Module Overview

Module 13 delivers five related standalone concerns grouped together because they are all comparatively lightweight:

1. **Settings Screen** (`settings`) — theme toggle, notification preferences link, privacy/terms WebViews, app version, delete account, sign out
2. **Packages & Subscriptions** (`packages`, `packages/current`) — package listing, subscription request (bank transfer flow), current plan detail with usage stats
3. **Force Update Dialog** — version gate on app open (config-driven)
4. **No Internet Screen** — full-screen overlay with auto-dismiss
5. **Feedback Screen** (`feedback`) — title + description form → Firestore + FCM to admin

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   └── usecase/
│       ├── settings/
│       │   └── DeleteAccountUseCase.kt
│       └── packages/
│           ├── GetPackagesUseCase.kt
│           ├── GetCurrentPlanUseCase.kt
│           └── SubmitSubscriptionRequestUseCase.kt
├── data/
│   ├── repository/
│   │   └── PackageRepositoryImpl.kt
│   └── local/
│       └── ThemePrefsDataStore.kt
├── presentation/
│   ├── settings/
│   │   ├── SettingsViewModel.kt
│   │   ├── SettingsScreen.kt
│   │   ├── notifications/
│   │   │   └── NotificationPrefsScreen.kt
│   │   └── components/
│   │       ├── DeleteAccountDialog.kt
│   │       └── DeleteAccountLoadingScreen.kt
│   ├── packages/
│   │   ├── PackagesViewModel.kt
│   │   ├── PackagesScreen.kt
│   │   ├── CurrentPlanScreen.kt
│   │   └── components/
│   │       ├── PackageCard.kt
│   │       ├── SubscriptionSuccessScreen.kt
│   │       └── UsageStatsCard.kt
│   ├── feedback/
│   │   ├── FeedbackViewModel.kt
│   │   └── FeedbackScreen.kt
│   └── system/
│       ├── NoInternetScreen.kt
│       └── ForceUpdateDialog.kt
├── service/
│   └── ConnectivityObserver.kt
└── di/
    └── SettingsModule.kt
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M13-T01 | `ThemePrefsDataStore` — Light/Dark/System preference | ☐ |
| M13-T02 | `SettingsViewModel` — theme toggle, delete account (4-phase), sign-out | ☐ |
| M13-T03 | `SettingsScreen` | ☐ |
| M13-T04 | `NotificationPrefsScreen` (`settings/notifications`) | ☐ |
| M13-T05 | `DeleteAccountDialog` + `DeleteAccountLoadingScreen` (§33.22) | ☐ |
| M13-T06 | `PackageRepositoryImpl` — `getPackages()`, `getCurrentPlan()`, `submitSubscriptionRequest()` | ☐ |
| M13-T07 | `PackagesScreen` (`packages`) — package cards, subscribe flow | ☐ |
| M13-T08 | `CurrentPlanScreen` (`packages/current`) — plan card + usage stats | ☐ |
| M13-T09 | `FeedbackScreen` (`feedback`) + `FeedbackViewModel` | ☐ |
| M13-T10 | `ConnectivityObserver` — NetworkCallback in Application | ☐ |
| M13-T11 | `NoInternetScreen` — full-screen overlay with shake animation | ☐ |
| M13-T12 | `ForceUpdateDialog` — hard + soft update variants | ☐ |
| M13-T13 | Nav wiring — all new routes | ☐ |
| M13-T14 | Koin module — `SettingsModule` | ☐ |
| M13-T15 | String resources | ☐ |
| M13-T16 | Build gate | ☐ |

---

## 4. Settings Screen — Exact Specification (§19)

```
Column(fillMaxSize, DarkBg0):
  Spacer(statusBar)
  Header (16.dp top, 20.dp H, 20.dp bottom):
    RentoIconButton(RentoIcons.Back, 42.dp, onBack)
    Text("Settings", Fraunces 24sp SemiBold)

  LazyColumn:
    SettingsSection("APPEARANCE"):
      SettingsRow("Theme"):
        // Segmented control: Light | Dark | System
        ThemeSegmentedControl(current=theme, onChange={ viewModel.setTheme(it) })

    SettingsSection("NOTIFICATIONS"):
      SettingsMenuRow("Notification Preferences", RentoIcons.Bell, onNavigateToNotifPrefs)

    SettingsSection("LEGAL"):
      SettingsMenuRow("Privacy Policy",      RentoIcons.Shield, { openWebView("privacy_policy_url") })
      SettingsMenuRow("Terms & Conditions",  RentoIcons.FileText, { openWebView("terms_url") })

    SettingsSection("APP"):
      SettingsInfoRow("App Version", BuildConfig.VERSION_NAME)

    SettingsSection("ACCOUNT"):
      SettingsMenuRow("Sign Out",       RentoIcons.LogOut, RentoColors.t1, { viewModel.openSignOutDialog() })
      SettingsMenuRow("Delete Account", RentoIcons.Trash,  RentoColors.red, { viewModel.openDeleteAccountDialog() })
```

### `ThemeSegmentedControl`

```kotlin
@Composable
fun ThemeSegmentedControl(
    current: String,    // "light" | "dark" | "system"
    onChange: (String) -> Unit,
) {
    Row(
        Modifier
            .background(MaterialTheme.rentoColors.bg2, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.rentoColors.border, RoundedCornerShape(12.dp))
            .padding(3.dp),
    ) {
        listOf("light" to "Light", "dark" to "Dark", "system" to "System").forEach { (value, label) ->
            val selected = current == value
            Box(
                Modifier
                    .weight(1f)
                    .background(
                        if (selected) MaterialTheme.rentoColors.primary else Color.Transparent,
                        RoundedCornerShape(10.dp),
                    )
                    .clickable { onChange(value) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, 13.sp.medium(), if (selected) Color.White else MaterialTheme.rentoColors.t2)
            }
        }
    }
}
```

### Theme application

```kotlin
// In RentoApplication or MainActivity:
val themeFlow: Flow<String> = themePrefsDataStore.themeFlow   // "light" | "dark" | "system"

// In setContent:
val theme by themeFlow.collectAsStateWithLifecycle(initialValue = "system")
val darkTheme = when (theme) {
    "light"  -> false
    "dark"   -> true
    else     -> isSystemInDarkTheme()
}
RentoTheme(darkTheme = darkTheme) { /* ... */ }
```

---

## 5. Delete Account — 4-Phase Flow (§33.22)

### Phase 1 — `DeleteAccountDialog`

```
GlassDialog (extra tall, scrollable body):
  Icon: RentoIcons.User in DarkRed circle (60dp container)
  Title: Fraunces 24sp "Delete Account?"
  Body (14sp DarkT1, 1.65 lineHeight):
    "This will permanently erase everything:
     • All your listings and draft listings
     • All your tenant requests
     • All your conversations and messages
     • Your saved listings and requests
     • Your subscription and packages
     • Your profile and account

    You will be signed out immediately.
    This cannot be reversed."

  Checkbox 1 (required, spacing 16dp top):
    "I understand my account and all data will be permanently deleted."

  AnimatedVisibility (visible after checkbox 1 checked):
    Checkbox 2 (spacing 12dp top):
      "I understand this action cannot be undone."

  Row(spacedBy=10.dp, Column):
    GhostButton("Cancel", fillMaxWidth, onDismiss)
    Button(
      "Delete Account",
      fillMaxWidth,
      containerColor = RentoColors.red,
      enabled = checkbox1 && checkbox2,
      onClick = { viewModel.confirmDeleteAccount() }
    )
```

### Phase 2 — `DeleteAccountLoadingScreen` (full-screen, back blocked)

```
Box(fillMaxSize, DarkBg0, BackHandler { /* blocked */ }):
  Column(CenterVertically, CenterHorizontally):
    RentoDeleteSpinner(64.dp)
    Spacer(24.dp)
    Text("Deleting your account…", Fraunces 22sp, RentoColors.t0, centred)
    Spacer(8.dp)
    // Three pulsing dots
    PulsingDots()
    Spacer(32.dp)
    // Progress steps (lit up as Cloud Function progresses)
    Column(spacedBy=12.dp):
      DeleteStep("Removing listings...",     isDone = steps.listings)
      DeleteStep("Removing requests...",     isDone = steps.requests)
      DeleteStep("Clearing conversations…",  isDone = steps.chats)
      DeleteStep("Removing saved items…",    isDone = steps.saved)
      DeleteStep("Deleting profile…",        isDone = steps.profile)
```

```kotlin
@Composable
private fun DeleteStep(label: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(
            if (isDone) RentoIcons.Check else RentoIcons.Clock,
            null,
            tint     = if (isDone) MaterialTheme.rentoColors.primary else MaterialTheme.rentoColors.t3,
            modifier = Modifier.size(16.dp),
        )
        Text(label, 13.sp, if (isDone) MaterialTheme.rentoColors.t0 else MaterialTheme.rentoColors.t2)
    }
}
```

### Phase 3a — Success → navigate to Welcome (`clearBackStack = true`)

### Phase 3b — Error Screen

```
Column(fillMaxSize, CenterVertically, CenterHorizontally, DarkBg0):
  Box(80.dp circle, DarkRedM, centred):
    Icon(RentoIcons.X, 36.dp, RentoColors.red)
  Spacer(24.dp)
  Text("Something went wrong", Fraunces 22sp, centred)
  Spacer(8.dp)
  Text("Your account could not be deleted. Your data is safe.", 14sp, RentoColors.t2, centred)
  Spacer(32.dp)
  PrimaryButton("Try Again", onClick = { viewModel.confirmDeleteAccount() })
  Spacer(12.dp)
  GhostButton("Cancel",     onClick = { viewModel.resetDeleteFlow() })
```

### `DeleteAccountUseCase`

```kotlin
class DeleteAccountUseCase(
    private val auth: AuthRepository,
    private val functions: FirebaseFunctions,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        // Call Cloud Function "deleteAccount" which cascades all Firestore deletes
        functions.getHttpsCallable("deleteAccount").call().await()
        auth.signOut()
    }
}
```

---

## 6. Notification Preferences Screen (§32.17)

**Route:** `settings/notifications`

```
Column(fillMaxSize, DarkBg0):
  Spacer(statusBar)
  Header: back + "Notification Preferences" title

  LazyColumn(weight=1f):
    NotifPrefRow("💬 New Messages",           DarkPri, KEY_MESSAGES,       onToggle)
    NotifPrefRow("✅ Listing Approved/Rejected", DarkPri, KEY_LISTINGS,     onToggle)
    NotifPrefRow("🔔 Subscription Updates",    DarkAcc, KEY_SUBSCRIPTIONS,  onToggle)
    NotifPrefRow("📣 Promotions",              DarkBlue, KEY_PROMOTIONS,    onToggle)
    NotifPrefRow("⚠️ Account Alerts",           DarkRed, KEY_ACCOUNT_ALERTS, onToggle)

  // Sticky save button
  Box(padding=16.dp, DarkBg0 fill, 1dp top border):
    PrimaryButton("Save Preferences", fillMaxWidth, onClick = { viewModel.savePrefs() })
```

```kotlin
@Composable
private fun NotifPrefRow(
    label: String, color: Color, prefKey: String,
    value: Boolean, onToggle: (String, Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = bodyM, color = MaterialTheme.rentoColors.t0, modifier = Modifier.weight(1f))
        ToggleSwitch(checked = value, onCheckedChange = { onToggle(prefKey, it) })
    }
    HorizontalDivider(0.5.dp, MaterialTheme.rentoColors.border)
}
```

Saves to: `SharedPreferences` + `Firestore users/{uid}.notifPreferences` map.
FCM topic subscribe/unsubscribe per applicable category.

---

## 7. Packages Screen — Exact Specification (§17)

**Route:** `packages`

### Package Card Design

```kotlin
@Composable
fun PackageCard(
    pkg: Package,
    isCurrentPlan: Boolean,
    onSubscribe: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.rentoColors.bg2, RoundedCornerShape(20.dp))
            .border(
                1.5.dp,
                if (isCurrentPlan) MaterialTheme.rentoColors.primary else MaterialTheme.rentoColors.border,
                RoundedCornerShape(20.dp),
            )
            .padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(pkg.name, Fraunces 20sp SemiBold, RentoColors.t0)
            if (isCurrentPlan) Badge("Current", BadgeStyle.PRIMARY)
        }
        Spacer(6.dp)
        Text(pkg.description, 13sp, RentoColors.t2)
        Spacer(16.dp)

        // Price
        Row(CenterVertically):
            Text("PKR ${formatPrice(pkg.price)}", 28sp Bold, RentoColors.primary)
            Spacer(4.dp)
            Text("/ ${pkg.durationDays} days", 12sp, RentoColors.t2)

        HorizontalDivider(1.dp, RentoColors.border, Modifier.padding(vertical = 14.dp))

        // Feature rows
        FeatureRow("${pkg.maxPublishedListings} publish slots")
        FeatureRow("${pkg.dailyMessageLimit} messages/day")
        FeatureRow("${pkg.maxPublishedRequests} active requests")
        if (pkg.allowsSlider) FeatureRow("Featured slider")

        Spacer(16.dp)

        if (!isCurrentPlan) {
            PrimaryButton("Subscribe →", Modifier.fillMaxWidth(), onSubscribe)
        } else {
            OutlinePrimaryButton("Manage Plan", Modifier.fillMaxWidth(), onNavigateToCurrentPlan)
        }
    }
}

@Composable
private fun FeatureRow(label: String) {
    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(RentoIcons.Check, null, tint = MaterialTheme.rentoColors.primary, Modifier.size(14.dp))
        Spacer(8.dp)
        Text(label, 13sp, RentoColors.t1)
    }
}
```

### Subscribe Flow

When "Subscribe →" tapped → open `SubscribeConfirmSheet`:

```
GlassBottomSheet:
  Title: Fraunces 22sp "Subscribe to ${pkg.name}"
  Price + duration row
  HorizontalDivider
  "Payment Method: Bank Transfer" row (info only)

  // Bank details
  SectionLabel("PAYMENT DETAILS")
  Box(bg2, border, 16dp corner, 14dp/16dp padding):
    Row: Text("Bank Name:") + Text(bankName, Bold, primary)
    Row: Text("Account:") + Text(accountNumber, Bold, primary, copyable)
    Row: Text("Account Title:") + Text(accountTitle, Bold)

  // WhatsApp CTA
  Spacer(16.dp)
  Text("After transferring, send your screenshot to:", 13sp, t2, centred)
  Spacer(6.dp)
  Row(CenterVertically, centred):
    Icon(RentoIcons.Chat, 16.dp, RentoColors.primary)
    Spacer(6.dp)
    Text(whatsappNumber, 15sp Bold, RentoColors.primary,
         Modifier.clickable { openWhatsApp(number) })

  Spacer(20.dp)
  PrimaryButton("I've Sent the Screenshot", fillMaxWidth) {
    // Create subscriptionRequests/{id} with status=pending
    viewModel.submitRequest(pkg.id)
  }
```

### After Submit — `SubscriptionSuccessScreen` (§32.13)

```
Full-screen DarkBg0:
  Icon: RentoIcons.Package (DarkAcc, 80dp)
  Fraunces 28sp "Request Submitted!"
  Body: "We'll review your payment and activate your plan. You'll be notified via the app."
  
  Box(bg2, border, 16dp corner, 14dp/16dp padding):
    "Bank Name: {bankName}" + "Account: {accountNumber}" (DarkPri, tappable copy)
    "WhatsApp: {number}" (DarkPri, tappable opens WhatsApp)

  PrimaryButton("Done", onClick = onNavigateHome)
```

---

## 8. Current Plan Screen (§32.18)

**Route:** `packages/current`

```
LazyColumn(padding=20.dp H):

  // ── Current Plan Card ─────────────────────────────────────────
  Box(
    fillMaxWidth,
    background = Brush.linearGradient([RentoColors.primary, RentoColors.secondary]),
    shape = RoundedCornerShape(20.dp),
    padding = 20.dp,
  ):
    Column:
      Row(SpaceBetween, CenterVertically):
        Text(plan.packageName ?: "Free Plan", Fraunces 20sp, Color.White)
        Badge("Active", small DarkPriM/White text)
      Spacer(6.dp)
      Row(CenterVertically):
        Text("PKR ${plan.price}", 26sp Bold, Color.White)
        Spacer(4.dp)
        Text(" / ${plan.durationDays} days", 13sp, Color.White.copy(0.7f))
      HorizontalDivider(1.dp, Color.White.copy(0.2f), vertical=14.dp)
      // Feature rows
      Text("✓ ${plan.maxPublishedListings} listing slots", 13sp, Color.White)
      Text("✓ ${plan.dailyMessageLimit} messages/day", 13sp, Color.White)
      if plan.allowsSlider: Text("✓ Featured Slider", 13sp, Color.White)
      Spacer(12.dp)
      // Expiry row
      if plan.expiresAt != null:
        val daysLeft = daysBetween(now, plan.expiresAt)
        Row(CenterVertically, spacedBy=8.dp):
          Text("Expires: ${formatDate(plan.expiresAt)}", 12sp, Color.White.copy(0.8f))
          if daysLeft <= 7:
            Badge("Expires in $daysLeft days", style=BadgeStyle.RED_SMALL)

  Spacer(20.dp)

  // ── Usage stats card ──────────────────────────────────────────
  Box(bg2, border, 16dp, 14dp padding):
    Column(spacedBy=12.dp):
      UsageRow("Published Listings", used=plan.publishedListings, max=plan.maxPublishedListings)
      UsageRow("Active Requests",    used=plan.activeRequests,    max=plan.maxPublishedRequests)
      UsageRow("Messages today",     used=plan.messagesToday,     max=plan.dailyMessageLimit)

  Spacer(20.dp)

  PrimaryButton("Extend / Renew Plan",   fillMaxWidth, onClick=onNavigateToPackages)
  Spacer(10.dp)
  OutlinePrimaryButton("Browse Other Plans", fillMaxWidth, onClick=onNavigateToPackages)
```

```kotlin
@Composable
private fun UsageRow(label: String, used: Int, max: Int) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, 13sp, RentoColors.t1)
            Text("$used / $max", 13sp Bold, RentoColors.primary)
        }
        Spacer(6.dp)
        LinearProgressIndicator(
            progress   = { (used.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
            modifier   = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color      = RentoColors.primary,
            trackColor = RentoColors.bg4,
        )
    }
}
```

---

## 9. Feedback Screen (§20)

**Route:** `feedback`

```kotlin
@Composable
fun FeedbackScreen(
    viewModel: FeedbackViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) { onBack() }
    }

    Column(Modifier.fillMaxSize().background(RentoColors.bg0)) {
        Spacer(windowInsetsTop)
        Row(header): { RentoIconButton(Back, onBack); Text("Help & Feedback", Fraunces 24sp) }

        LazyColumn(padding=20.dp):
            item { Text("Share your thoughts, report an issue, or suggest a feature.", 14sp, t2, bottom=24.dp) }

            item {
                SectionLabel("TITLE")
                Spacer(10.dp)
                UnderlineInputField(
                    label         = "What's this about?",
                    value         = uiState.title,
                    onValueChange = { if (it.length <= 100) viewModel.setTitle(it) },
                    trailingText  = "${uiState.title.length}/100",
                )
            }

            item {
                Spacer(24.dp)
                SectionLabel("DESCRIPTION")
                Spacer(10.dp)
                BasicTextField(
                    value         = uiState.description,
                    onValueChange = { if (it.length <= 1000) viewModel.setDescription(it) },
                    textStyle     = bodyM + RentoColors.t0,
                    minLines      = 5,
                    modifier      = Modifier.fillMaxWidth()
                        .background(RentoColors.bg2, RoundedCornerShape(18.dp))
                        .border(1.5.dp, RentoColors.border, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    decorationBox = { inner ->
                        if (uiState.description.isEmpty())
                            Text("Describe your feedback in detail…", bodyM, RentoColors.t3)
                        inner()
                    },
                )
                Spacer(6.dp)
                Text("${uiState.description.length} / 1000", 12sp, RentoColors.t3, textAlign=End, Modifier.fillMaxWidth())
            }

            item {
                Spacer(24.dp)
                PrimaryButton(
                    label     = if uiState.isSubmitting "Submitting…" else "Send Feedback",
                    modifier  = Modifier.fillMaxWidth(),
                    onClick   = { viewModel.submit() },
                    isLoading = uiState.isSubmitting,
                    enabled   = uiState.title.isNotBlank() && uiState.description.isNotBlank() && !uiState.isSubmitting,
                )
            }
    }

    // Error snackbar
    if uiState.error != null:
        RentoSnackbar(uiState.error, onDismiss = { viewModel.clearError() })
}
```

```kotlin
class FeedbackViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: AuthRepository,
) : ViewModel() {

    data class FeedbackUiState(
        val title: String = "",
        val description: String = "",
        val isSubmitting: Boolean = false,
        val submitSuccess: Boolean = false,
        val error: String? = null,
    )

    fun submit() {
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            runCatching {
                val uid = auth.getCurrentUserId()
                firestore.collection("featureRequests").add(mapOf(
                    "uid"         to uid,
                    "title"       to _uiState.value.title,
                    "description" to _uiState.value.description,
                    "status"      to "open",
                    "createdAt"   to FieldValue.serverTimestamp(),
                )).await()
                // FCM push to admin via Cloud Function
                // (admin subscribes to topic "admin_notifs" — or individual FCM token notification)
            }.fold(
                onSuccess = { _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(isSubmitting = false, error = e.message) } },
            )
        }
    }
}
```

> On success: screen navigates back + parent shows snackbar `"Thank you! Your feedback has been submitted."` (passed via `SavedStateHandle` or shared snackbar host).

---

## 10. Connectivity Observer (§21)

**File:** `service/ConnectivityObserver.kt`

```kotlin
class ConnectivityObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    val isConnected: StateFlow<Boolean> = MutableStateFlow(
        connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } ?: false
    ).also { flow ->
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network)  { (flow as MutableStateFlow).value = true  }
            override fun onLost(network: Network)       { (flow as MutableStateFlow).value = false }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        // Unregister in Application.onTerminate() or via lifecycle scope
    }
}
```

Registered as `single` in Koin: `single { ConnectivityObserver(androidContext()) }`.

In `MainActivity`, overlay `NoInternetScreen` when `!isConnected`:

```kotlin
val isConnected by connectivityObserver.isConnected.collectAsStateWithLifecycle()
Box(Modifier.fillMaxSize()) {
    RentoNavHost(...)
    InAppNotificationBanner(...)
    AnimatedVisibility(visible = !isConnected) {
        NoInternetScreen(onRetry = { /* check connectivity */ })
    }
}
```

---

## 11. `NoInternetScreen` — Exact Specification (§32.19)

```kotlin
@Composable
fun NoInternetScreen(onRetry: () -> Unit) {
    var isShaking by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        targetValue   = if (isShaking) 8f else 0f,
        animationSpec = repeatable(3, tween(80, easing = LinearEasing), RepeatMode.Reverse),
        finishedListener = { isShaking = false },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0)
            .zIndex(Float.MAX_VALUE),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment    = Alignment.CenterHorizontally,
            verticalArrangement    = Arrangement.Center,
        ) {
            // Floating wifi-X icon
            val floatAnim by rememberInfiniteTransition(label = "float")
                .animateFloat(
                    initialValue   = 0f,
                    targetValue    = -12f,
                    animationSpec  = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label          = "floatY",
                )
            Icon(
                RentoIcons.WifiOff,
                null,
                tint     = MaterialTheme.rentoColors.t2,
                modifier = Modifier.size(72.dp).graphicsLayer { translationY = floatAnim },
            )
            Spacer(24.dp)
            Text("No Internet", style = MaterialTheme.rentoTypography.displayS, color = MaterialTheme.rentoColors.t0)
            Spacer(8.dp)
            Text(
                "Check your connection and try again.",
                14.sp, MaterialTheme.rentoColors.t2,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 40.dp),
            )
            Spacer(32.dp)
            Box(Modifier.width(180.dp).offset(x = shakeOffset.dp)) {
                PrimaryButton("Try Again") {
                    val isNowConnected = /* check connectivity */ false
                    if (isNowConnected) onRetry()
                    else isShaking = true   // shake animation
                }
            }
        }
    }
}
```

> `RentoIcons.WifiOff` — verify in Module 01 icon set. Add if absent (Material Icons `WifiOff`).

---

## 12. `ForceUpdateDialog` — Exact Specification (§32.20)

Checked on `MainActivity.onCreate()` by fetching `config/app`:

```kotlin
val configSnap = firestore.collection("config").document("app").get().await()
val latestBuild = configSnap.getLong("latestBuildNumber")?.toInt() ?: 0
val isForceUpdate = configSnap.getBoolean("isForceUpdate") ?: false
val currentBuild  = BuildConfig.VERSION_CODE

if (currentBuild < latestBuild) {
    showForceUpdateDialog = true
    this.isForceUpdate    = isForceUpdate
}
```

**Hard update (isForceUpdate = true):**
```
GlassDialog (non-dismissable: onDismissRequest = {}, BackHandler { }):
  Icon: RentoIcons.Package (40.dp, DarkPri)
  Title: Fraunces 22sp "Update Required"
  Body: "A new version of RentO is available. Please update to continue."
  Single button:
    PrimaryButton("Update Now") {
      context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${packageName}"))
      )
    }
```

**Soft update (isForceUpdate = false):**
```
GlassDialog (dismissable):
  Icon, Title, Body (same as hard)
  GhostButton("Later", onDismiss)
  Spacer(10.dp)
  PrimaryButton("Update Now", openPlayStore)
```

---

## 13. String Resources

```xml
<!-- ─── Settings ─────────────────────────────────────────────────────────── -->
<string name="settings_title">Settings</string>
<string name="settings_section_appearance">APPEARANCE</string>
<string name="settings_theme_label">Theme</string>
<string name="settings_theme_light">Light</string>
<string name="settings_theme_dark">Dark</string>
<string name="settings_theme_system">System</string>
<string name="settings_section_notifications">NOTIFICATIONS</string>
<string name="settings_notif_prefs">Notification Preferences</string>
<string name="settings_section_legal">LEGAL</string>
<string name="settings_privacy">Privacy Policy</string>
<string name="settings_terms">Terms &amp; Conditions</string>
<string name="settings_section_app">APP</string>
<string name="settings_version">App Version</string>
<string name="settings_section_account">ACCOUNT</string>

<!-- ─── Delete Account ───────────────────────────────────────────────────── -->
<string name="del_account_title">Delete Account?</string>
<string name="del_account_body">This will permanently erase everything:\n• All your listings and draft listings\n• All your tenant requests\n• All your conversations and messages\n• Your saved listings and requests\n• Your subscription and packages\n• Your profile and account\n\nYou will be signed out immediately.\nThis cannot be reversed.</string>
<string name="del_account_check1">I understand my account and all data will be permanently deleted.</string>
<string name="del_account_check2">I understand this action cannot be undone.</string>
<string name="del_account_confirm">Delete Account</string>
<string name="del_account_loading_title">Deleting your account…</string>
<string name="del_account_step_listings">Removing listings…</string>
<string name="del_account_step_requests">Removing requests…</string>
<string name="del_account_step_chats">Clearing conversations…</string>
<string name="del_account_step_saved">Removing saved items…</string>
<string name="del_account_step_profile">Deleting profile…</string>
<string name="del_account_error_title">Something went wrong</string>
<string name="del_account_error_body">Your account could not be deleted. Your data is safe.</string>
<string name="del_account_try_again">Try Again</string>

<!-- ─── Packages ─────────────────────────────────────────────────────────── -->
<string name="packages_title">Packages</string>
<string name="packages_subscribe">Subscribe →</string>
<string name="packages_current_plan">Current Plan</string>
<string name="packages_manage_plan">Manage Plan</string>
<string name="packages_extend">Extend / Renew Plan</string>
<string name="packages_browse_other">Browse Other Plans</string>
<string name="packages_active_badge">Active</string>
<string name="packages_expires_in">Expires in %1$d days</string>
<string name="packages_submitted_title">Request Submitted!</string>
<string name="packages_submitted_body">We\'ll review your payment and activate your plan. You\'ll be notified via the app.</string>
<string name="packages_done">Done</string>
<string name="packages_ive_sent">I\'ve Sent the Screenshot</string>

<!-- ─── Feedback ──────────────────────────────────────────────────────────── -->
<string name="feedback_title">Help &amp; Feedback</string>
<string name="feedback_subtitle">Share your thoughts, report an issue, or suggest a feature.</string>
<string name="feedback_title_label">TITLE</string>
<string name="feedback_title_hint">What\'s this about?</string>
<string name="feedback_desc_label">DESCRIPTION</string>
<string name="feedback_desc_hint">Describe your feedback in detail…</string>
<string name="feedback_submit">Send Feedback</string>
<string name="feedback_submitting">Submitting…</string>
<string name="feedback_success">Thank you! Your feedback has been submitted.</string>

<!-- ─── No Internet ───────────────────────────────────────────────────────── -->
<string name="no_internet_title">No Internet</string>
<string name="no_internet_body">Check your connection and try again.</string>
<string name="no_internet_retry">Try Again</string>

<!-- ─── Force Update ─────────────────────────────────────────────────────── -->
<string name="force_update_title">Update Required</string>
<string name="force_update_body">A new version of RentO is available. Please update to continue.</string>
<string name="force_update_btn">Update Now</string>
<string name="soft_update_later">Later</string>
```

---

## 14. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Settings — theme toggle applies immediately | `ThemePrefsDataStore` + `isSystemInDarkTheme()` override | ☐ |
| Settings — Privacy Policy opens WebView | `Intent(VIEW, url)` or `WebViewScreen` | ☐ |
| Settings — "Delete Account" → 4-phase dialog flow | `DeleteAccountDialog` phases | ☐ |
| Delete account — both checkboxes required before confirm | `checkbox1 && checkbox2` | ☐ |
| Delete account — loading screen blocks back press | `BackHandler { }` | ☐ |
| Delete account — success → Welcome screen clear back stack | `popUpTo(0)` | ☐ |
| Delete account — error → try again / cancel | error phase screen | ☐ |
| Packages — all active packages listed | `GetPackagesUseCase` | ☐ |
| Packages — subscribe → bank details sheet | `SubscribeConfirmSheet` | ☐ |
| Packages — "I've Sent Screenshot" → creates subscriptionRequest doc | `SubmitSubscriptionRequestUseCase` | ☐ |
| Packages — success screen shows bank details + WhatsApp | `SubscriptionSuccessScreen` | ☐ |
| Current plan — gradient card with feature rows | `CurrentPlanScreen` | ☐ |
| Current plan — expiry warning badge when ≤7 days | `daysLeft <= 7` | ☐ |
| Current plan — usage stats with progress bars | `UsageRow` | ☐ |
| Notification prefs — all 5 toggles save to Firestore + prefs | `savePrefs()` | ☐ |
| Feedback — title + description both required | `enabled = title.isNotBlank() && desc.isNotBlank()` | ☐ |
| Feedback — success → navigates back + snackbar | `submitSuccess = true` | ☐ |
| No Internet — overlay appears on connectivity loss | `ConnectivityObserver.isConnected = false` | ☐ |
| No Internet — auto-dismisses when connected | `isConnected = true` → `AnimatedVisibility` | ☐ |
| No Internet — "Try Again" shakes if still offline | `isShaking = true` → shake offset anim | ☐ |
| No Internet — wifi-X icon floats with `infiniteTransition` | `floatY` animation | ☐ |
| Force update (hard) — non-dismissable, only "Update Now" | `onDismissRequest = {}` | ☐ |
| Force update (soft) — dismissable, "Later" option | `GlassDialog` with `onDismissRequest` | ☐ |

---

## 15. CODE_REVIEW_MODULE_13.md Template

```markdown
# Code Review — Module 13: Settings, Packages, Force Update, Connectivity, Feedback
**Spec version:** ANDROID_MODULE_13.md v1.0.0

## ✅ Settings
- [ ] ThemeSegmentedControl: 3 segments (Light/Dark/System), applied in real-time
- [ ] Delete account: TWO checkboxes, second appears only after first is checked
- [ ] Delete account loading: BackHandler fully blocked
- [ ] Delete account success: clearBackStack = true (popUpTo root)
- [ ] Sign-out: clears prefs EXCEPT theme

## ✅ Packages
- [ ] PackageCard: bg2 + border (primary if current, regular otherwise)
- [ ] Subscribe sheet: bank details shown before payment submit
- [ ] SubscriptionRequest doc: status = "pending"
- [ ] Success screen: bank details + WhatsApp as tappable DarkPri links
- [ ] Current plan card: gradient (primary → secondary), white text
- [ ] Expiry badge: only shown when ≤ 7 days remaining

## ✅ System
- [ ] ConnectivityObserver registered in Application (not Activity)
- [ ] NoInternetScreen zIndex = Float.MAX_VALUE (above everything)
- [ ] WifiOff icon floats with infiniteTransition (−12dp Y)
- [ ] "Try Again" shake: translateX ±8dp, 3 cycles, 80ms tween
- [ ] ForceUpdate config fetched in MainActivity.onCreate()
- [ ] Hard update: BackHandler blocked, no outside dismiss

## ✅ Build
- [ ] `./gradlew assembleDebug` → ✅
- [ ] `./gradlew test` → ✅
- [ ] `./gradlew detekt` → 0 violations
```

*End of Module 13 — Settings, Packages, Force Update, Connectivity, Feedback v1.0.0*
*Next: Module 14 — Map View.*
