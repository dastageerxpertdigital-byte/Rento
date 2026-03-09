# RentO — Android App
## Module 09 — Notifications (FCM)
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 09
> **Branch:** `feature/module-09-notifications`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 08 ✅
> **Audience:** Android Agent
>
> ⚠️ **OneSignal is NOT used. All push is Firebase Cloud Messaging (FCM) only.**

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
5. [Task M09-T01 — Domain Models](#task-m09-t01--domain-models)
6. [Task M09-T02 — `NotificationRepository`](#task-m09-t02--notificationrepository)
7. [Task M09-T03 — `NescoFCMService`](#task-m09-t03--nescofcmservice)
8. [Task M09-T04 — In-App Notification Banner](#task-m09-t04--in-app-notification-banner)
9. [Task M09-T05 — Notification Channel Setup](#task-m09-t05--notification-channel-setup)
10. [Task M09-T06 — FCM Token Management in `AuthRepositoryImpl`](#task-m09-t06--fcm-token-management-in-authrepositoryimpl)
11. [Task M09-T07 — `NotificationViewModel`](#task-m09-t07--notificationviewmodel)
12. [Task M09-T08 — Koin Module](#task-m09-t08--koin-module)
13. [Task M09-T09 — Navigation Wiring + Deep Link Routing](#task-m09-t09--navigation-wiring--deep-link-routing)
14. [Task M09-T10 — `NotificationScreen`](#task-m09-t10--notificationscreen)
15. [Task M09-T11 — Unread Badge Logic](#task-m09-t11--unread-badge-logic)
16. [Task M09-T12 — String Resources](#task-m09-t12--string-resources)
17. [Task M09-T13 — Unit Tests](#task-m09-t13--unit-tests)
18. [Task M09-T14 — Build Gate](#task-m09-t14--build-gate)
19. [Journey Coverage Checklist](#19-journey-coverage-checklist)
20. [CODE_REVIEW_MODULE_09.md Template](#20-code_review_module_09md-template)

---

## 1. Module Overview

Module 09 delivers the complete **push notification infrastructure and in-app notification screen** for RentO.

**Key features:**
- `NescoFCMService` — `FirebaseMessagingService` subclass handling token refresh and incoming messages
- **Foreground**: in-app notification banner composable that slides down from the top for 4 seconds, then auto-dismisses
- **Background/Killed**: system notification tray notification; tapping deep-links into the correct screen based on `notifType`
- **Always**: every received notification is written to Firestore `notifications/{uid}/items/{notifId}` and `SharedPreferences hasUnreadNotifications = true`
- FCM token saved to `users/{uid}.fcmToken` on login and on `onNewToken()`
- Topic subscription to `"all_users"` at login for admin broadcasts
- **Notifications Screen** (`notifications` route): full list of past notifications with type-specific icons, tap-to-navigate, "No notifications yet" empty state, red-dot badge cleared on open
- 16 notification types with distinct icons and navigation targets
- `AndroidManifest.xml` declarations

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   └── AppNotification.kt               ← NEW
│   └── repository/
│       └── NotificationRepository.kt        ← NEW
├── data/
│   └── repository/
│       └── NotificationRepositoryImpl.kt    ← NEW
├── service/
│   └── NescoFCMService.kt                   ← NEW
├── presentation/
│   ├── notification/
│   │   ├── NotificationViewModel.kt
│   │   ├── NotificationScreen.kt
│   │   └── components/
│   │       ├── NotificationRow.kt
│   │       └── InAppNotificationBanner.kt
│   └── main/
│       └── MainActivity.kt                  ← UPDATED: deep link + banner wiring
└── di/
    └── NotificationModule.kt                ← NEW

app/src/main/AndroidManifest.xml             ← UPDATED
app/src/main/res/values/strings.xml          ← Module 09 strings appended
app/src/test/java/com/rento/app/
└── presentation/notification/
    └── NotificationViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M09-T01 | `AppNotification` domain model | `domain/model/` | ☐ |
| M09-T02 | `NotificationRepository` interface + `NotificationRepositoryImpl` | `domain/repository/`, `data/repository/` | ☐ |
| M09-T03 | `NescoFCMService` — token + message handling + Firestore write | `service/` | ☐ |
| M09-T04 | `InAppNotificationBanner` — slide-down composable, 4s auto-dismiss | `presentation/notification/components/` | ☐ |
| M09-T05 | Notification channel setup in `RentoApplication.onCreate()` | `RentoApplication.kt` | ☐ |
| M09-T06 | FCM token save + topic subscription in `AuthRepositoryImpl` | `data/repository/AuthRepositoryImpl.kt` | ☐ |
| M09-T07 | `NotificationViewModel` | `presentation/notification/` | ☐ |
| M09-T08 | Koin module — `NotificationModule.kt` | `di/` | ☐ |
| M09-T09 | Navigation wiring — `notifications` route + deep link dispatcher | `RentoNavGraph.kt`, `MainActivity.kt` | ☐ |
| M09-T10 | `NotificationScreen` + `NotificationRow` | `presentation/notification/` | ☐ |
| M09-T11 | Unread badge — SharedPreferences key + red dot on bottom nav bell | `RentoApplication`, bottom nav | ☐ |
| M09-T12 | String resources | `strings.xml` | ☐ |
| M09-T13 | Unit tests | `*Test.kt` | ☐ |
| M09-T14 | Build gate | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 `AppNotification` Domain Model

```kotlin
data class AppNotification(
    val id: String,
    val notifType: String,      // see full type table in section 4.4
    val title: String,
    val body: String,
    val data: Map<String, String>,   // extraData from FCM payload
    val createdAt: Long,
    val isRead: Boolean = false,
)
```

### 4.2 Notification Types Table

| `notifType` | Icon | Navigate To |
|---|---|---|
| `subscription_approved` | `RentoIcons.Star` (accent) | `packages` |
| `subscription_rejected` | `RentoIcons.AlertCircle` (t2) | `packages` |
| `subscription_upgraded` | `RentoIcons.Star` (primary) | `packages` |
| `subscription_expiring` | `RentoIcons.Clock` (accent) | `packages` |
| `listing_approved` | `RentoIcons.Check` (primary) | `listing/{listingId}` |
| `listing_rejected` | `RentoIcons.X` (red) | `listing/{listingId}` |
| `listing_blocked` | `RentoIcons.Shield` (red) | `listing/{listingId}` |
| `listing_deleted_by_admin` | `RentoIcons.Trash` (red) | `my_listings` |
| `request_deleted_by_admin` | `RentoIcons.Trash` (red) | `my_requests` |
| `new_message` | `RentoIcons.Chat` (primary) | `chat/{chatId}` |
| `account_blocked` | `RentoIcons.Shield` (red) | (blocked screen) |
| `slider_approved` | `RentoIcons.Star` (accent) | `my_listings` |
| `gemini_key_failed` | `RentoIcons.AlertCircle` (accent) | (admin only) |
| `new_listing_pending` | `RentoIcons.Home` (t1) | (admin only) |
| `new_request_pending` | `RentoIcons.Search` (t1) | (admin only) |
| `new_subscription_request` | `RentoIcons.Package` (t1) | (admin only) |
| `new_feedback` | `RentoIcons.Chat` (t1) | (admin only) |

### 4.3 Repository Interface

```kotlin
interface NotificationRepository {
    fun getNotifications(uid: String): Flow<List<AppNotification>>
    suspend fun markAllRead(uid: String): Result<Unit>
    suspend fun writeNotification(uid: String, notification: AppNotification): Result<Unit>
}
```

### 4.4 SharedPreferences Keys

```kotlin
object NotifPrefs {
    const val PREFS_NAME              = "rento_notif_prefs"
    const val KEY_HAS_UNREAD          = "hasUnreadNotifications"
}
```

### 4.5 Koin Module

```kotlin
val notificationModule = module {
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    factory { GetNotificationsUseCase(get(), get()) }
    viewModel { NotificationViewModel(get(), get()) }
}
```

---

## Task M09-T01 — Domain Models

### Sub-tasks
- [ ] **M09-T01-A** Create `domain/model/AppNotification.kt` as defined in section 4.1. Zero Android imports.

---

## Task M09-T02 — `NotificationRepository`

**Files:** `domain/repository/NotificationRepository.kt`, `data/repository/NotificationRepositoryImpl.kt`

### Sub-tasks
- [ ] **M09-T02-A** Create interface from section 4.3.

- [ ] **M09-T02-B** Implement `getNotifications` — Firestore snapshot Flow:

```kotlin
override fun getNotifications(uid: String): Flow<List<AppNotification>> = callbackFlow {
    val query = firestore.collection("notifications").document(uid)
        .collection("items")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(100)
    val listener = query.addSnapshotListener { snap, err ->
        if (err != null) { close(err); return@addSnapshotListener }
        val items = snap?.documents?.mapNotNull { doc ->
            runCatching {
                AppNotification(
                    id        = doc.id,
                    notifType = doc.getString("notifType") ?: "",
                    title     = doc.getString("title") ?: "",
                    body      = doc.getString("body") ?: "",
                    data      = (doc.get("data") as? Map<*, *>)
                                    ?.mapNotNull { (k, v) -> (k as? String)?.let { it to (v?.toString() ?: "") } }
                                    ?.toMap() ?: emptyMap(),
                    createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                    isRead    = doc.getBoolean("isRead") ?: false,
                )
            }.getOrNull()
        } ?: emptyList()
        trySend(items)
    }
    awaitClose { listener.remove() }
}
```

- [ ] **M09-T02-C** Implement `writeNotification` — used by `NescoFCMService`:

```kotlin
override suspend fun writeNotification(uid: String, notification: AppNotification): Result<Unit> =
    runCatching {
        val data = hashMapOf(
            "notifType" to notification.notifType,
            "title"     to notification.title,
            "body"      to notification.body,
            "data"      to notification.data,
            "isRead"    to false,
            "createdAt" to FieldValue.serverTimestamp(),
        )
        firestore.collection("notifications").document(uid)
            .collection("items").add(data).await()
    }
```

- [ ] **M09-T02-D** Implement `markAllRead` — batch update:

```kotlin
override suspend fun markAllRead(uid: String): Result<Unit> = runCatching {
    val items = firestore.collection("notifications").document(uid)
        .collection("items").whereEqualTo("isRead", false).get().await()
    val batch = firestore.batch()
    items.documents.forEach { doc -> batch.update(doc.reference, "isRead", true) }
    batch.commit().await()
}
```

---

## Task M09-T03 — `NescoFCMService`

**File:** `service/NescoFCMService.kt`

```kotlin
class NescoFCMService : FirebaseMessagingService() {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth      by lazy { FirebaseAuth.getInstance() }
    private val prefs     by lazy {
        getSharedPreferences(NotifPrefs.PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ── Token refresh ─────────────────────────────────────────────────
    override fun onNewToken(token: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .update("fcmToken", token)
    }

    // ── Message received ──────────────────────────────────────────────
    override fun onMessageReceived(message: RemoteMessage) {
        val uid = auth.currentUser?.uid ?: return

        val notifType  = message.data["notifType"] ?: ""
        val title      = message.notification?.title ?: message.data["title"] ?: "RentO"
        val body       = message.notification?.body  ?: message.data["body"]  ?: ""
        val extraData  = message.data.filterKeys { it != "notifType" }

        // 1. Always: write to Firestore notification log
        writeToFirestore(uid, notifType, title, body, extraData)

        // 2. Always: set unread flag in SharedPreferences
        prefs.edit().putBoolean(NotifPrefs.KEY_HAS_UNREAD, true).apply()

        // 3. Foreground: show in-app banner via broadcast
        //    Background/killed: system tray notification
        if (isAppInForeground()) {
            sendInAppBannerBroadcast(notifType, title, body, extraData)
        } else {
            showSystemNotification(notifType, title, body, extraData)
        }
    }

    // ── Firestore write ───────────────────────────────────────────────
    private fun writeToFirestore(
        uid: String,
        notifType: String,
        title: String,
        body: String,
        data: Map<String, String>,
    ) {
        val doc = hashMapOf(
            "notifType" to notifType,
            "title"     to title,
            "body"      to body,
            "data"      to data,
            "isRead"    to false,
            "createdAt" to FieldValue.serverTimestamp(),
        )
        firestore.collection("notifications").document(uid)
            .collection("items").add(doc)
    }

    // ── In-app banner (foreground) ───────────────────────────────────
    private fun sendInAppBannerBroadcast(
        notifType: String, title: String, body: String, data: Map<String, String>,
    ) {
        val intent = Intent("com.rento.INAPP_NOTIF").apply {
            putExtra("notifType", notifType)
            putExtra("title",     title)
            putExtra("body",      body)
            putExtra("data",      HashMap(data))
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // ── System tray notification (background) ─────────────────────────
    private fun showSystemNotification(
        notifType: String, title: String, body: String, data: Map<String, String>,
    ) {
        val pendingIntent = buildPendingIntent(notifType, data)
        val notification  = NotificationCompat.Builder(this, "nesco_default")
            .setSmallIcon(R.drawable.ic_notification)    // 24dp white monochrome icon
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setColor(0xFF2ECC8A.toInt())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun buildPendingIntent(
        notifType: String, data: Map<String, String>,
    ): PendingIntent {
        val deepLinkRoute = resolveDeepLinkRoute(notifType, data)
        val intent        = Intent(this, MainActivity::class.java).apply {
            putExtra("deepLinkRoute", deepLinkRoute)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    // ── Route resolution ──────────────────────────────────────────────
    private fun resolveDeepLinkRoute(notifType: String, data: Map<String, String>): String =
        when (notifType) {
            "listing_approved",
            "listing_rejected",
            "listing_blocked"          -> "listing/${data["listingId"] ?: ""}"
            "listing_deleted_by_admin" -> "my_listings"
            "request_deleted_by_admin" -> "my_requests"
            "new_message"              -> "chat/${data["chatId"] ?: ""}"
            "subscription_approved",
            "subscription_rejected",
            "subscription_upgraded",
            "subscription_expiring"    -> "packages"
            "slider_approved"          -> "my_listings"
            "account_blocked"          -> "account_blocked"
            else                       -> "notifications"
        }

    // ── Foreground detection ──────────────────────────────────────────
    private fun isAppInForeground(): Boolean {
        val am    = getSystemService(ActivityManager::class.java)
        val tasks = am.getRunningAppProcesses() ?: return false
        val pkg   = packageName
        return tasks.any {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && it.processName == pkg
        }
    }
}
```

### `AndroidManifest.xml` declaration

```xml
<service
    android:name=".service.NescoFCMService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>

<!-- Notification icon meta-data -->
<meta-data
    android:name="com.google.firebase.messaging.default_notification_channel_id"
    android:value="nesco_default" />
<meta-data
    android:name="com.google.firebase.messaging.default_notification_icon"
    android:resource="@drawable/ic_notification" />
<meta-data
    android:name="com.google.firebase.messaging.default_notification_color"
    android:resource="@color/primary_green" />
```

### Sub-tasks
- [ ] **M09-T03-A** Create `NescoFCMService` exactly as above.
- [ ] **M09-T03-B** Add service to `AndroidManifest.xml` with `MESSAGING_EVENT` intent-filter.
- [ ] **M09-T03-C** Create `res/drawable/ic_notification.xml` — 24dp white monochrome vector of the RentO logo or home icon (must be monochrome for Android notification icon requirements).
- [ ] **M09-T03-D** Add `firebase-messaging` dependency to `app/build.gradle.kts` if not already present.
- [ ] **M09-T03-E** Add `localbroadcastmanager` dependency: `androidx.localbroadcastmanager:localbroadcastmanager`.

---

## Task M09-T04 — In-App Notification Banner

**File:** `presentation/notification/components/InAppNotificationBanner.kt`

The banner is a global overlay rendered in `MainActivity` above all screens. It receives broadcasts from `NescoFCMService` and auto-dismisses after 4 seconds.

### Design

```
AnimatedVisibility(
  visible = isVisible,
  enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(300)),
  exit    = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(200)),
):
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .align(TopCenter)
      .zIndex(Float.MAX_VALUE),
  ):
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RentoColors.bg1, RoundedCornerShape(16.dp))
        .border(1.dp, RentoColors.border, RoundedCornerShape(16.dp))
        .then(API31 → Modifier.blur(2.dp) else Modifier)
        .padding(12.dp),
      verticalAlignment = CenterVertically,
      horizontalArrangement = spacedBy(12.dp),
    ):
      // Icon circle (36dp)
      Box(
        36.dp circle,
        background = RentoColors.primaryTint,
        contentAlignment = Center,
      ):
        Icon(notifIcon, 20.dp, RentoColors.primary)

      Column(Modifier.weight(1f)):
        Text(title, 14sp SemiBold, RentoColors.t0, maxLines=1, overflow=Ellipsis)
        Spacer(2.dp)
        Text(body, 13sp, RentoColors.t2, maxLines=2, overflow=Ellipsis)

      // Dismiss X
      Icon(
        RentoIcons.X, 16.dp, RentoColors.t3,
        modifier = Modifier.clickable { dismiss() },
      )
```

### Implementation

```kotlin
@Composable
fun InAppNotificationBanner(
    notification: InAppNotifData?,
    onDismiss: () -> Unit,
    onTap: (notifType: String, data: Map<String, String>) -> Unit,
) {
    val isVisible = notification != null

    LaunchedEffect(notification) {
        if (notification != null) {
            delay(4_000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible       = isVisible,
        enter         = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(300)),
        exit          = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(200)),
        modifier      = Modifier.fillMaxWidth().zIndex(1000f),
    ) {
        if (notification != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(MaterialTheme.rentoColors.bg1, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.rentoColors.border, RoundedCornerShape(16.dp))
                    .clickable { onTap(notification.notifType, notification.data); onDismiss() }
                    .padding(12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val icon = resolveNotifIcon(notification.notifType)
                Box(
                    Modifier.size(36.dp).background(MaterialTheme.rentoColors.primaryTint, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, null, tint = MaterialTheme.rentoColors.primary, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(notification.title, style = 14.sp.semiBold(), color = MaterialTheme.rentoColors.t0, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(2.dp)
                    Text(notification.body, style = 13.sp.normal(), color = MaterialTheme.rentoColors.t2, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Icon(
                    RentoIcons.X, null,
                    tint     = MaterialTheme.rentoColors.t3,
                    modifier = Modifier.size(16.dp).clickable { onDismiss() },
                )
            }
        }
    }
}

data class InAppNotifData(
    val notifType: String,
    val title: String,
    val body: String,
    val data: Map<String, String>,
)

fun resolveNotifIcon(notifType: String): ImageVector = when (notifType) {
    "new_message"                               -> RentoIcons.Chat
    "listing_approved"                          -> RentoIcons.Check
    "listing_rejected", "listing_blocked",
    "listing_deleted_by_admin",
    "request_deleted_by_admin", "account_blocked" -> RentoIcons.AlertCircle
    "subscription_approved", "subscription_upgraded",
    "slider_approved"                           -> RentoIcons.Star
    "subscription_expiring"                     -> RentoIcons.Clock
    else                                        -> RentoIcons.Bell
}
```

### `MainActivity` wiring

In `MainActivity.kt`, register a `LocalBroadcastManager` receiver and pass the banner data via a `MutableState`:

```kotlin
// In MainActivity (inside setContent or before):
var inAppNotif by remember { mutableStateOf<InAppNotifData?>(null) }

DisposableEffect(Unit) {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            inAppNotif = InAppNotifData(
                notifType = intent.getStringExtra("notifType") ?: "",
                title     = intent.getStringExtra("title") ?: "",
                body      = intent.getStringExtra("body") ?: "",
                data      = (intent.getSerializableExtra("data") as? HashMap<*, *>)
                                ?.mapNotNull { (k, v) -> (k as? String)?.let { it to (v?.toString() ?: "") } }
                                ?.toMap() ?: emptyMap(),
            )
        }
    }
    LocalBroadcastManager.getInstance(context)
        .registerReceiver(receiver, IntentFilter("com.rento.INAPP_NOTIF"))
    onDispose {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }
}

// Overlay at top of nav host:
Box(Modifier.fillMaxSize()) {
    RentoNavHost(...)   // main nav graph
    InAppNotificationBanner(
        notification = inAppNotif,
        onDismiss    = { inAppNotif = null },
        onTap        = { type, data -> navController.navigate(resolveDeepLinkRoute(type, data)); inAppNotif = null },
    )
}
```

### Sub-tasks
- [ ] **M09-T04-A** Implement `InAppNotificationBanner` composable.
- [ ] **M09-T04-B** Implement `resolveNotifIcon()` mapping.
- [ ] **M09-T04-C** Wire banner into `MainActivity` via `LocalBroadcastManager` receiver.
- [ ] **M09-T04-D** Auto-dismiss `LaunchedEffect` — 4000ms delay resets each time `notification` changes key.
- [ ] **M09-T04-E** Banner tapping navigates AND dismisses.

---

## Task M09-T05 — Notification Channel Setup

**File:** `RentoApplication.kt`

### Sub-tasks
- [ ] **M09-T05-A** Add channel creation in `RentoApplication.onCreate()`:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel(
        "nesco_default",
        "RentO Notifications",
        NotificationManager.IMPORTANCE_HIGH,
    ).apply {
        description = "General RentO push notifications"
        enableLights(true)
        lightColor = 0xFF2ECC8A.toInt()
        enableVibration(true)
    }
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
}
```

- [ ] **M09-T05-B** Create `res/values/colors.xml` entry `primary_green` = `#2ECC8A` if not present.

---

## Task M09-T06 — FCM Token Management in `AuthRepositoryImpl`

**File:** `data/repository/AuthRepositoryImpl.kt` — update

### Sub-tasks
- [ ] **M09-T06-A** After successful login (email/password or Google), add:

```kotlin
// After login success:
FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener
    firestore.collection("users").document(uid)
        .update("fcmToken", token)
}
// Subscribe to broadcast topic:
FirebaseMessaging.getInstance().subscribeToTopic("all_users")
```

- [ ] **M09-T06-B** On sign-out: unsubscribe from topic and clear token:

```kotlin
// On sign-out:
FirebaseMessaging.getInstance().unsubscribeFromTopic("all_users")
val uid = FirebaseAuth.getInstance().currentUser?.uid
if (uid != null) {
    firestore.collection("users").document(uid).update("fcmToken", FieldValue.delete())
}
FirebaseAuth.getInstance().signOut()
```

- [ ] **M09-T06-C** `onNewToken()` in `NescoFCMService` handles token rotation post-login automatically.

---

## Task M09-T07 — `NotificationViewModel`

**File:** `presentation/notification/NotificationViewModel.kt`

```kotlin
data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        val uid = auth.getCurrentUserId() ?: run {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            repository.getNotifications(uid)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { items ->
                    _uiState.update { it.copy(notifications = items, isLoading = false) }
                }
        }
        // Mark all read when screen opens
        viewModelScope.launch {
            repository.markAllRead(uid)
        }
    }
}
```

---

## Task M09-T08 — Koin Module

**File:** `di/NotificationModule.kt`

### Sub-tasks
- [ ] **M09-T08-A** Create `notificationModule` per section 4.5.
- [ ] **M09-T08-B** Add to `RentoApplication.startKoin`.

---

## Task M09-T09 — Navigation Wiring + Deep Link Routing

### Sub-tasks
- [ ] **M09-T09-A** Wire `notifications` route:

```kotlin
composable(route = "notifications") {
    NotificationScreen(
        onNavigate = { route -> navController.navigate(route) },
        onBack     = { navController.popBackStack() },
    )
}
```

- [ ] **M09-T09-B** In `MainActivity.onCreate()`, handle deep link from system tray tap:

```kotlin
// In onCreate, after navController is ready:
val deepLinkRoute = intent?.getStringExtra("deepLinkRoute")
if (deepLinkRoute != null) {
    navController.navigate(deepLinkRoute)
}
```

- [ ] **M09-T09-C** Add `resolveDeepLinkRoute()` as a shared helper (reused by `NescoFCMService` and `InAppNotificationBanner`):

```kotlin
// In a shared object or top-level function:
fun resolveDeepLinkRoute(notifType: String, data: Map<String, String>): String = when (notifType) {
    "listing_approved",
    "listing_rejected",
    "listing_blocked"          -> "listing/${data["listingId"] ?: ""}"
    "listing_deleted_by_admin" -> "my_listings"
    "request_deleted_by_admin" -> "my_requests"
    "new_message"              -> "chat/${data["chatId"] ?: ""}"
    "subscription_approved",
    "subscription_rejected",
    "subscription_upgraded",
    "subscription_expiring"    -> "packages"
    "slider_approved"          -> "my_listings"
    "account_blocked"          -> "account_blocked"
    else                       -> "notifications"
}
```

---

## Task M09-T10 — `NotificationScreen`

**File:** `presentation/notification/NotificationScreen.kt`

### Exact Specification (Section 16.7)

```kotlin
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = koinViewModel(),
    onNavigate: (route: String) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0),
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Spacer(Modifier.height(16.dp))

        // Header with back
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            RentoIconButton(RentoIcons.Back, 42.dp, onBack)
            Text(
                "Notifications",
                style = MaterialTheme.rentoTypography.displayS,   // Fraunces 28sp SemiBold
                color = MaterialTheme.rentoColors.t0,
            )
        }

        when {
            uiState.isLoading -> NotificationShimmer()
            uiState.notifications.isEmpty() -> EmptyState(
                icon     = RentoIcons.Bell,
                title    = "No notifications yet",
                subtitle = "You'll see listing approvals, messages, and more here.",
            )
            else -> LazyColumn {
                items(uiState.notifications, key = { it.id }) { notif ->
                    NotificationRow(
                        notification = notif,
                        onClick      = { onNavigate(resolveDeepLinkRoute(notif.notifType, notif.data)) },
                    )
                    HorizontalDivider(1.dp, MaterialTheme.rentoColors.border)
                }
            }
        }
    }
}
```

### `NotificationRow` — Exact Specification

```
Row(
  fillMaxWidth, CenterVertically,
  padding = 14.dp V / 16.dp H,
  background = if !isRead RentoColors.primaryTint.copy(alpha=0.3f) else Color.Transparent,
  clickable { onClick() },
):
  // Icon circle (40dp)
  Box(
    40.dp circle,
    background = resolveNotifIconBg(notif.notifType),
    contentAlignment = Center,
  ):
    Icon(resolveNotifIcon(notif.notifType), 20.dp, resolveNotifIconTint(notif.notifType))

  Spacer(12.dp)

  Column(Modifier.weight(1f)):
    Text(notif.title, 14sp SemiBold, RentoColors.t0)
    Spacer(2.dp)
    Text(notif.body, 13sp, RentoColors.t2, maxLines=2, overflow=Ellipsis)
    Spacer(4.dp)
    Text(formatTimestamp(notif.createdAt), 11sp, RentoColors.t3)
```

**Icon tint/bg resolution per type:**

```kotlin
fun resolveNotifIconBg(type: String): Color = when (type) {
    "listing_approved", "subscription_approved",
    "subscription_upgraded", "slider_approved"  -> RentoColors.primaryTint
    "listing_rejected", "listing_blocked",
    "listing_deleted_by_admin",
    "request_deleted_by_admin", "account_blocked" -> RentoColors.redTint
    "subscription_expiring",
    "gemini_key_failed"                          -> RentoColors.accentTint
    else                                         -> RentoColors.bg2
}

fun resolveNotifIconTint(type: String): Color = when (type) {
    "listing_approved", "subscription_approved",
    "subscription_upgraded", "slider_approved",
    "new_message"                                -> RentoColors.primary
    "listing_rejected", "listing_blocked",
    "listing_deleted_by_admin",
    "request_deleted_by_admin", "account_blocked" -> RentoColors.red
    "subscription_expiring",
    "gemini_key_failed"                          -> RentoColors.accent
    else                                         -> RentoColors.t1
}
```

### Sub-tasks
- [ ] **M09-T10-A** Implement `NotificationScreen` + `NotificationRow`.
- [ ] **M09-T10-B** Unread rows get a faint `primaryTint` background (alpha 0.3f).
- [ ] **M09-T10-C** `@Preview` — 3 notifications (unread + read), empty, loading, dark + light.

---

## Task M09-T11 — Unread Badge Logic

**Files:** `RentoApplication.kt`, bottom nav composable

The bottom nav bell icon shows a red dot when `hasUnreadNotifications = true` in SharedPreferences. It clears when the Notifications screen opens (cleared by ViewModel → `markAllRead`).

```kotlin
// Read in bottom nav composable (recomposed when prefs change):
val hasUnread by remember {
    mutableStateOf(
        context.getSharedPreferences(NotifPrefs.PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(NotifPrefs.KEY_HAS_UNREAD, false)
    )
}

// Display: if hasUnread, overlay a 8dp red circle on top-right of bell icon
```

> **Note:** For real-time updates to the red dot without a full recomposition, use a `Flow<Boolean>` from a `DataStore<Preferences>` rather than SharedPreferences. If the team prefers simplicity, SharedPreferences read on nav screen recompose is acceptable for V1.

### Sub-tasks
- [ ] **M09-T11-A** SharedPreferences `hasUnreadNotifications` set to `true` in `NescoFCMService.onMessageReceived()`.
- [ ] **M09-T11-B** Set to `false` when `NotificationViewModel.init` calls `markAllRead()`.
- [ ] **M09-T11-C** Bottom nav bell shows an 8dp red dot (`Color(0xFFE05555)`) on top-right corner when unread.

---

## Task M09-T12 — String Resources

```xml
<!-- ─── Notifications ────────────────────────────────────────────────────── -->
<string name="notif_screen_title">Notifications</string>
<string name="notif_empty_title">No notifications yet</string>
<string name="notif_empty_subtitle">You\'ll see listing approvals, messages, and more here.</string>

<!-- Notification type titles (used for in-app banner) -->
<string name="notif_subscription_approved">Subscription Approved 🎉</string>
<string name="notif_subscription_rejected">Subscription Update</string>
<string name="notif_subscription_upgraded">Congratulations! 🎊 You\'ve been upgraded!</string>
<string name="notif_subscription_expiring">Your plan expires soon</string>
<string name="notif_listing_approved">Listing Approved ✅</string>
<string name="notif_listing_rejected">Listing Update</string>
<string name="notif_listing_blocked">Listing Blocked</string>
<string name="notif_listing_deleted">Listing Removed</string>
<string name="notif_request_deleted">Request Removed</string>
<string name="notif_new_message">New message from %1$s</string>
<string name="notif_account_blocked">Account Notice</string>
<string name="notif_slider_approved">Your featured slide is live!</string>
```

---

## Task M09-T13 — Unit Tests

### `NotificationViewModelTest.kt`

```
init_collectsNotificationsFromRepository
init_callsMarkAllReadOnOpen
init_setsLoadingFalseOnEmission
init_setsErrorOnFlowError
init_unauthenticated_setsLoadingFalse
```

### Sub-tasks
- [ ] **M09-T13-A** Implement tests using MockK + Turbine + `StandardTestDispatcher`.
- [ ] **M09-T13-B** Mock `NotificationRepository` returning a flow of 3 items.
- [ ] **M09-T13-C** `./gradlew test` → all pass.

---

## Task M09-T14 — Build Gate

- [ ] **M09-T14-A** `./gradlew lint` → zero new warnings.
- [ ] **M09-T14-B** `./gradlew assembleDebug` → `BUILD SUCCESSFUL`.
- [ ] **M09-T14-C** `./gradlew test` → all pass.
- [ ] **M09-T14-D** Manually verify: send test FCM via Firebase console → foreground banner appears; background → system tray; tapping navigates correctly.
- [ ] **M09-T14-E** Update `ANDROID_PROGRESS.md`.

---

## 19. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| App open → token saved to Firestore | `AuthRepositoryImpl` post-login | ☐ |
| Token refresh → `NescoFCMService.onNewToken()` updates Firestore | `onNewToken()` | ☐ |
| Login → subscribed to `"all_users"` FCM topic | `subscribeToTopic()` | ☐ |
| Sign-out → unsubscribed + token cleared | `unsubscribeFromTopic()` + `delete()` | ☐ |
| FCM received in foreground → in-app banner slides down | `LocalBroadcastManager` broadcast | ☐ |
| Banner auto-dismisses after 4s | `LaunchedEffect(notification) { delay(4_000) }` | ☐ |
| Banner tap → navigates to correct screen | `resolveDeepLinkRoute()` | ☐ |
| Banner × tap → dismisses | `onDismiss()` | ☐ |
| FCM received in background → system tray notification | `showSystemNotification()` | ☐ |
| System tray tap → deep links into app | `MainActivity` `deepLinkRoute` intent extra | ☐ |
| Every received FCM → written to Firestore notification log | `writeToFirestore()` | ☐ |
| Every received FCM → SharedPreferences `hasUnreadNotifications = true` | `prefs.edit()` | ☐ |
| Bottom nav bell → shows red dot when unread | `hasUnread` prefs read | ☐ |
| Notification screen opens → `markAllRead()` called | `NotificationViewModel.init` | ☐ |
| Red dot clears after opening notification screen | `hasUnreadNotifications = false` | ☐ |
| Notification rows — unread has faint primaryTint bg | `!isRead` check | ☐ |
| Notification rows — correct icon per type | `resolveNotifIcon()` | ☐ |
| Notification rows — correct icon colour per type | `resolveNotifIconTint()` | ☐ |
| Notification row tap → navigates to correct screen | `resolveDeepLinkRoute()` | ☐ |
| Notification screen empty state — Bell icon | `notifications.isEmpty()` | ☐ |
| `listing_approved` → navigates to listing detail | `"listing/{listingId}"` | ☐ |
| `new_message` → navigates to chat detail | `"chat/{chatId}"` | ☐ |
| `subscription_approved` → navigates to packages | `"packages"` | ☐ |
| `account_blocked` → navigates to blocked screen | `"account_blocked"` | ☐ |

---

## 20. CODE_REVIEW_MODULE_09.md Template

```markdown
# Code Review — Module 09: Notifications (FCM)
**Date:** YYYY-MM-DD
**Spec version:** ANDROID_MODULE_09.md v1.0.0

## ✅ FCM Setup
- [ ] `NescoFCMService` declared in `AndroidManifest.xml` with `MESSAGING_EVENT`
- [ ] `nesco_default` notification channel created in `RentoApplication.onCreate()`
- [ ] `ic_notification` drawable is monochrome (required by Android)
- [ ] `firebase-messaging` dependency present in `build.gradle.kts`
- [ ] `localbroadcastmanager` dependency present

## ✅ Token Lifecycle
- [ ] Token saved after login
- [ ] `onNewToken()` saves token without requiring login restart
- [ ] Sign-out clears token and unsubscribes from topic
- [ ] `"all_users"` topic subscription confirmed

## ✅ Message Routing
- [ ] All 16 `notifType` values have a route mapping in `resolveDeepLinkRoute()`
- [ ] Foreground → in-app banner; Background → system tray
- [ ] In-app banner auto-dismisses after 4000ms
- [ ] System tray tap navigates correctly via `deepLinkRoute` intent extra

## ✅ Firestore Write
- [ ] Every received FCM (foreground AND background) writes to `notifications/{uid}/items/`
- [ ] `markAllRead()` uses batch write (not per-document updates)

## ✅ Design Reference
- [ ] Banner: 16dp corner, border, slides down from top (not from bottom)
- [ ] Banner icon circle: 36dp, primaryTint bg
- [ ] NotifRow icon circle: 40dp, type-specific bg + tint
- [ ] Unread row: faint primaryTint (alpha 0.3f) background
- [ ] Red dot on bell: 8dp, DarkRed fill

## ✅ Code Quality
- [ ] `./gradlew assembleDebug` → ✅
- [ ] `./gradlew test` → ✅
```

---

*End of Module 09 — Notifications (FCM) v1.0.0*
*Next: Module 10 — Saved Items.*
