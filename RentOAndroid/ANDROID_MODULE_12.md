# RentO — Android App
## Module 12 — Profile & Edit Profile
### Complete Engineering Specification

> **Version:** 1.0.0 | **Branch:** `feature/module-12-profile`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 09 ✅

---

## 1. Module Overview

Module 12 delivers:
- **Profile Screen** (`profile`) — the user's hub: avatar, name, plan badge, mode toggle, and navigation shortcuts
- **Edit Profile Screen** (`profile/edit`) — full name, phone, date of birth, province/city, photo upload; discard-changes dialog

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   └── usecase/
│       └── profile/
│           ├── GetProfileUseCase.kt
│           ├── UpdateProfileUseCase.kt
│           └── UploadProfilePhotoUseCase.kt
├── presentation/
│   └── profile/
│       ├── ProfileViewModel.kt
│       ├── ProfileScreen.kt
│       ├── edit/
│       │   ├── EditProfileViewModel.kt
│       │   ├── EditProfileScreen.kt
│       │   └── components/
│       │       ├── ProfilePhotoPicker.kt
│       │       └── DiscardProfileChangesDialog.kt
│       └── components/
│           ├── ProfileHeader.kt
│           ├── ModePill.kt
│           └── ProfileMenuRow.kt
└── di/
    └── ProfileModule.kt
```

---

## 3. Task List

| ID | Task | Status |
|----|------|--------|
| M12-T01 | Use cases — `GetProfileUseCase`, `UpdateProfileUseCase`, `UploadProfilePhotoUseCase` | ☐ |
| M12-T02 | `ProfileViewModel` — load profile, sign-out dialog, mode toggle | ☐ |
| M12-T03 | `EditProfileViewModel` — load, detect changes, validate, update, photo upload | ☐ |
| M12-T04 | Koin module | ☐ |
| M12-T05 | Nav wiring — `profile` + `profile/edit` | ☐ |
| M12-T06 | `ProfileScreen` — header + mode pill + menu rows + sign-out | ☐ |
| M12-T07 | `ModePill` — Looking / Hosting toggle | ☐ |
| M12-T08 | `EditProfileScreen` — all fields + photo picker + save | ☐ |
| M12-T09 | `ProfilePhotoPicker` — camera / gallery / remove bottom sheet | ☐ |
| M12-T10 | `DiscardProfileChangesDialog` — GlassDialog (2-button variant) | ☐ |
| M12-T11 | Sign-out dialog + sign-out flow (clears prefs, navigates to Welcome) | ☐ |
| M12-T12 | String resources | ☐ |
| M12-T13 | Unit tests + build gate | ☐ |

---

## 4. Architecture

### 4.1 `ProfileViewModel`

```kotlin
data class ProfileUiState(
    val user: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showSignOutDialog: Boolean = false,
    val isSigningOut: Boolean = false,
    val currentMode: String = "looking",   // "looking" | "hosting"
)

class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val auth: AuthRepository,
    private val prefs: SharedPreferences,
) : ViewModel() {

    init { loadProfile() }

    fun loadProfile() { /* fetchProfile → update uiState */ }

    fun setMode(mode: String) {
        prefs.edit().putString("homeMode", mode).apply()
        _uiState.update { it.copy(currentMode = mode) }
    }

    fun openSignOutDialog()   = _uiState.update { it.copy(showSignOutDialog = true) }
    fun dismissSignOutDialog() = _uiState.update { it.copy(showSignOutDialog = false) }

    fun signOut() {
        _uiState.update { it.copy(isSigningOut = true) }
        viewModelScope.launch {
            auth.signOut()
            // Clear prefs except theme
            val theme = prefs.getString("theme", "system")
            prefs.edit().clear().putString("theme", theme).apply()
            _uiState.update { it.copy(isSigningOut = false) }
        }
    }
}
```

### 4.2 `EditProfileViewModel`

```kotlin
data class EditProfileUiState(
    val original: UserProfile? = null,
    val editedName: String = "",
    val editedPhone: String = "",
    val editedDob: String = "",          // "YYYY-MM-DD" or ""
    val editedProvince: String = "",
    val editedCity: String = "",
    val photoUri: Uri? = null,           // local preview URI (not yet uploaded)
    val isDirty: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false,
    val showPhotoPicker: Boolean = false,
)

class EditProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val uploadPhoto: UploadProfilePhotoUseCase,
    private val auth: AuthRepository,
) : ViewModel() {

    init { loadProfile() }

    fun setName(name: String)         { _uiState.update { it.copy(editedName = name, isDirty = true) } }
    fun setPhone(phone: String)       { _uiState.update { it.copy(editedPhone = phone, isDirty = true) } }
    fun setDob(dob: String)           { _uiState.update { it.copy(editedDob = dob, isDirty = true) } }
    fun setProvince(p: String)        { _uiState.update { it.copy(editedProvince = p, editedCity = "", isDirty = true) } }
    fun setCity(c: String)            { _uiState.update { it.copy(editedCity = c, isDirty = true) } }
    fun setPhotoUri(uri: Uri?)        { _uiState.update { it.copy(photoUri = uri, isDirty = true) } }

    fun openPhotoPicker()             = _uiState.update { it.copy(showPhotoPicker = true) }
    fun closePhotoPicker()            = _uiState.update { it.copy(showPhotoPicker = false) }
    fun openDiscardDialog()           = _uiState.update { it.copy(showDiscardDialog = true) }
    fun dismissDiscardDialog()        = _uiState.update { it.copy(showDiscardDialog = false) }

    fun save() {
        val state = _uiState.value
        if (!validateForm(state)) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            var photoUrl: String? = null
            if (state.photoUri != null) {
                photoUrl = uploadPhoto(state.photoUri).getOrNull()
            }
            val updated = UserProfile(
                uid           = auth.getCurrentUserId() ?: return@launch,
                displayName   = state.editedName,
                phone         = state.editedPhone,
                dob           = state.editedDob,
                province      = state.editedProvince,
                city          = state.editedCity,
                photoUrl      = photoUrl ?: state.original?.photoUrl,
                // other fields preserved from original
            )
            updateProfile(updated).fold(
                onSuccess = { _uiState.update { it.copy(isSaving = false, saveSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(isSaving = false, saveError = e.message) } },
            )
        }
    }

    private fun validateForm(state: EditProfileUiState): Boolean {
        if (state.editedName.isBlank()) {
            _uiState.update { it.copy(saveError = "Full name is required.") }
            return false
        }
        return true
    }
}
```

### 4.3 Use Cases

```kotlin
class GetProfileUseCase(private val repo: UserRepository, private val auth: AuthRepository) {
    suspend operator fun invoke(): Result<UserProfile> {
        val uid = auth.getCurrentUserId() ?: return Result.failure(Exception("Not auth."))
        return repo.getProfile(uid)
    }
}

class UpdateProfileUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(profile: UserProfile): Result<Unit> = repo.updateProfile(profile)
}

class UploadProfilePhotoUseCase(private val storage: StorageRepository) {
    suspend operator fun invoke(uri: Uri): Result<String> = storage.uploadProfilePhoto(uri)
}
```

---

## 5. Profile Screen — Exact Specification (§7.2)

```
Column(fillMaxSize, DarkBg0):
  Spacer(statusBar)

  // ── Profile Header ──────────────────────────────────────────────
  Column(fillMaxWidth, CenterHorizontally, padding=20.dp top, 20.dp H, 24.dp bottom):
    // Avatar (84dp circle)
    Box(
      84.dp circle,
      background = if user.photoUrl != null → AsyncImage else DarkPriM fill,
    ):
      if no photo: Icon(RentoIcons.User, 40.dp, RentoColors.primary)

    Spacer(14.dp)

    // Name
    Text(user.displayName, Fraunces 22sp SemiBold, RentoColors.t0)

    Spacer(4.dp)

    // Member since + account type badge
    Row(CenterVertically, spacedBy=8.dp):
      Text("Member since ${memberSinceYear}", 13sp, RentoColors.t2)
      Badge(user.accountType, BadgeStyle.NEUTRAL)

    Spacer(16.dp)

    // Edit Profile button
    OutlinePrimaryButton("Edit Profile", onClick = onNavigateToEdit)

  // ── Mode Toggle ─────────────────────────────────────────────────
  ModePill(
    currentMode = uiState.currentMode,
    onSelectLooking  = { viewModel.setMode("looking") },
    onSelectHosting  = { viewModel.setMode("hosting") },
    modifier = Modifier.padding(horizontal=20.dp).padding(bottom=20.dp),
  )

  HorizontalDivider(1.dp, RentoColors.border)

  // ── Menu Rows ────────────────────────────────────────────────────
  LazyColumn:
    ProfileMenuSection("MY CONTENT"):
      ProfileMenuRow("My Listings",      RentoIcons.Home,      RentoColors.primary, onNavigateToMyListings)
      ProfileMenuRow("My Requests",      RentoIcons.Search,    RentoColors.primary, onNavigateToMyRequests)
      ProfileMenuRow("My Subscription",  RentoIcons.Package,   RentoColors.accent,  onNavigateToPackages)

    ProfileMenuSection("ACCOUNT"):
      ProfileMenuRow("Saved Items",      RentoIcons.Bookmark,  RentoColors.primary,  onNavigateToSaved)
      ProfileMenuRow("Notifications",    RentoIcons.Bell,      RentoColors.primary,  onNavigateToNotifications)
      ProfileMenuRow("Help & Feedback",  RentoIcons.Chat,      RentoColors.t1,       onNavigateToFeedback)
      ProfileMenuRow("Settings",         RentoIcons.Settings,  RentoColors.t1,       onNavigateToSettings)

    ProfileMenuSection("DANGER ZONE"):
      ProfileMenuRow("Sign Out",         RentoIcons.LogOut,    RentoColors.t1, { viewModel.openSignOutDialog() })
      ProfileMenuRow("Delete Account",   RentoIcons.Trash,     RentoColors.red, onNavigateToDeleteAccount)
```

### `ProfileMenuRow` design

```kotlin
@Composable
fun ProfileMenuRow(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(38.dp).background(iconTint.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(14.dp)
        Text(label, style = MaterialTheme.rentoTypography.bodyM, color = MaterialTheme.rentoColors.t0, modifier = Modifier.weight(1f))
        Icon(RentoIcons.ChevronRight, null, tint = MaterialTheme.rentoColors.t3, modifier = Modifier.size(16.dp))
    }
    HorizontalDivider(0.5.dp, MaterialTheme.rentoColors.border, Modifier.padding(start = 72.dp))
}
```

---

## 6. `ModePill` — Looking / Hosting Toggle

```kotlin
@Composable
fun ModePill(
    currentMode: String,
    onSelectLooking: () -> Unit,
    onSelectHosting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.rentoColors.bg2, RoundedCornerShape(100.dp))
            .border(1.dp, MaterialTheme.rentoColors.border, RoundedCornerShape(100.dp))
            .padding(4.dp),
    ) {
        // Looking pill
        ModeTab(
            label    = "👀 Looking",
            selected = currentMode == "looking",
            onClick  = onSelectLooking,
            modifier = Modifier.weight(1f),
        )
        // Hosting pill
        ModeTab(
            label    = "🏠 Hosting",
            selected = currentMode == "hosting",
            onClick  = onSelectHosting,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ModeTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(
                if (selected) MaterialTheme.rentoColors.primary else Color.Transparent,
                RoundedCornerShape(100.dp),
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.rentoTypography.labelM.copy(fontWeight = FontWeight.Bold),
            color = if (selected) Color.White else MaterialTheme.rentoColors.t2,
        )
    }
}
```

---

## 7. Edit Profile Screen — Exact Specification (§32.1)

```
Column(fillMaxSize, DarkBg0):
  Spacer(statusBar)

  // Header row
  Row(fillMaxWidth, SpaceBetween, CenterVertically, padding=16.dp top, 20.dp H):
    RentoIconButton(RentoIcons.Back, 42.dp, onClick=handleBack)
    Text("Edit Profile", Fraunces 24sp SemiBold, centred, Modifier.weight(1f))
    // "Save" text button — greyed out until isDirty
    TextButton(
      onClick  = { viewModel.save() },
      enabled  = uiState.isDirty && !uiState.isSaving,
    ):
      Text("Save", 15sp Bold, if enabled RentoColors.primary else RentoColors.t3)

  // Scrollable content
  LazyColumn(padding = 24.dp top, 20.dp H):

    item("photo") {
      // ── Profile Photo ─────────────────────────────────────────────
      Box(fillMaxWidth, CenterHorizontally, padding=bottom 32.dp):
        Box(
          84.dp circle,
          clickable = { viewModel.openPhotoPicker() },
        ):
          // Photo preview or default avatar
          if uiState.photoUri != null:
            AsyncImage(uiState.photoUri, 84.dp circle, ContentScale.Crop)
          elif uiState.original?.photoUrl != null:
            AsyncImage(url, 84.dp, ContentScale.Crop)
          else:
            Box(84.dp, DarkPriM fill, circle):
              Icon(RentoIcons.User, 40.dp, RentoColors.primary)

          // Camera overlay (semi-transparent scrim + camera icon)
          Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.35f), CircleShape),
            contentAlignment = Center,
          ):
            Icon(RentoIcons.Camera, 22.dp, Color.White)
    }

    item("personal") {
      SectionLabel("PERSONAL INFO")
      Spacer(16.dp)
      UnderlineInputField("Full Name",   uiState.editedName,  { viewModel.setName(it)  }, maxLength=60)
      Spacer(16.dp)
      UnderlineInputField("Phone",       uiState.editedPhone, { viewModel.setPhone(it) }, keyboardType=Phone, maxLength=15)
      Spacer(16.dp)
      // Date of Birth — tappable field opens DatePickerDialog
      Box(
        fillMaxWidth,
        modifier = Modifier.clickable { showDatePicker = true },
      ):
        UnderlineInputField(
          label       = "Date of Birth",
          value       = formatDob(uiState.editedDob),
          onValueChange = {},
          readOnly    = true,
          trailingIcon = { Icon(RentoIcons.Calendar, 18.dp, RentoColors.t2) },
        )
    }

    item("location") {
      Spacer(24.dp)
      SectionLabel("LOCATION")
      Spacer(16.dp)
      // Province tappable field → Province picker sheet
      Box(fillMaxWidth, clickable = { showProvincePicker = true }):
        UnderlineInputField("Province", uiState.editedProvince, {}, readOnly=true,
          trailingIcon = { Icon(RentoIcons.ChevronDown, 16.dp, RentoColors.t2) })
      Spacer(16.dp)
      Box(fillMaxWidth, clickable = { if province set → showCityPicker = true }):
        UnderlineInputField("City",     uiState.editedCity,     {}, readOnly=true,
          trailingIcon = { Icon(RentoIcons.ChevronDown, 16.dp, RentoColors.t2) })
      Spacer(8.dp)
      Text("Use current location", 13sp Bold, RentoColors.primary,
           Modifier.clickable { requestLocationPermission() })
    }

    item("account_type") {
      Spacer(24.dp)
      SectionLabel("ACCOUNT TYPE")
      Spacer(12.dp)
      Row(spacedBy=8.dp):
        // Non-tappable chips
        AccountTypeChip("Individual", selected = user.accountType == "individual")
        AccountTypeChip("Business",   selected = user.accountType == "business")
      Spacer(6.dp)
      Text("Account type cannot be changed.", 11sp, RentoColors.t3)
    }

    item("save_btn") {
      Spacer(32.dp)
      PrimaryButton(
        label      = if uiState.isSaving "Saving…" else "Save Changes",
        modifier   = Modifier.fillMaxWidth(),
        onClick    = { viewModel.save() },
        isLoading  = uiState.isSaving,
        enabled    = uiState.isDirty,
      )
      Spacer(40.dp)
    }
```

> **Back handling:** if `isDirty` → show `DiscardProfileChangesDialog`; else navigate back.

---

## 8. `ProfilePhotoPicker` Bottom Sheet

```
GlassBottomSheet(onDismiss = { viewModel.closePhotoPicker() }):
  Column(padding=20.dp, spacedBy=4.dp):
    Text("Change Profile Photo", Fraunces 20sp, bottom=16.dp)

    // Option rows
    PhotoPickerOption("Take Photo",           RentoIcons.Camera,  onClick = { launchCamera() })
    PhotoPickerOption("Choose from Gallery",  RentoIcons.Image,   onClick = { launchGallery() })
    if user.photoUrl != null:
      PhotoPickerOption("Remove Photo",       RentoIcons.Trash,   tint=RentoColors.red, onClick = { viewModel.setPhotoUri(null) })
```

```kotlin
@Composable
private fun PhotoPickerOption(label: String, icon: ImageVector, tint: Color = RentoColors.t0, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        Text(label, style = MaterialTheme.rentoTypography.bodyM, color = tint)
    }
}
```

Photo compression: compress selected bitmap to <500KB before passing to `UploadProfilePhotoUseCase`.

---

## 9. `DiscardProfileChangesDialog` (§33.24 — 2-button variant)

```
GlassDialog:
  Icon: RentoIcons.X in DarkAcc circle (56dp)
  Title: "Discard Changes?"
  Body: "Your profile changes won't be saved."

  // 2-button layout (column):
  PrimaryButton("Keep Editing", fillMaxWidth, onClick=onDismiss)
  Spacer(10.dp)
  TextButton(
    modifier = Modifier.fillMaxWidth(),
    onClick  = onConfirm,
  ):
    Text("Discard", 15sp Bold, RentoColors.red)
```

---

## 10. Sign-Out Dialog (§33.23)

```
GlassDialog:
  Icon: RentoIcons.LogOut in DarkBg4 circle (neutral — reversible action)
  Title: "Sign Out?"
  Body: "You'll need to sign in again to access your account."
  Buttons:
    GhostButton("Cancel",   fillMaxWidth, onDismiss)
    Spacer(10.dp)
    Button("Sign Out", fillMaxWidth, containerColor=RentoColors.t0, onClick=viewModel.signOut)
    Loading: "Signing out…" + spinner (300–800ms, then navigate to Welcome)
```

---

## 11. String Resources

```xml
<!-- ─── Profile ──────────────────────────────────────────────────────────── -->
<string name="profile_member_since">Member since %1$s</string>
<string name="profile_edit_btn">Edit Profile</string>
<string name="profile_mode_looking">👀 Looking</string>
<string name="profile_mode_hosting">🏠 Hosting</string>
<string name="profile_section_content">MY CONTENT</string>
<string name="profile_section_account">ACCOUNT</string>
<string name="profile_section_danger">DANGER ZONE</string>
<string name="profile_my_listings">My Listings</string>
<string name="profile_my_requests">My Requests</string>
<string name="profile_subscription">My Subscription</string>
<string name="profile_saved">Saved Items</string>
<string name="profile_notifications">Notifications</string>
<string name="profile_feedback">Help &amp; Feedback</string>
<string name="profile_settings">Settings</string>
<string name="profile_sign_out">Sign Out</string>
<string name="profile_delete_account">Delete Account</string>

<!-- ─── Edit Profile ─────────────────────────────────────────────────────── -->
<string name="edit_profile_title">Edit Profile</string>
<string name="edit_profile_save">Save</string>
<string name="edit_profile_save_changes">Save Changes</string>
<string name="edit_profile_saving">Saving…</string>
<string name="edit_profile_section_personal">PERSONAL INFO</string>
<string name="edit_profile_section_location">LOCATION</string>
<string name="edit_profile_section_account_type">ACCOUNT TYPE</string>
<string name="edit_profile_name_label">Full Name</string>
<string name="edit_profile_phone_label">Phone</string>
<string name="edit_profile_dob_label">Date of Birth</string>
<string name="edit_profile_province_label">Province</string>
<string name="edit_profile_city_label">City</string>
<string name="edit_profile_use_location">Use current location</string>
<string name="edit_profile_account_type_note">Account type cannot be changed.</string>
<string name="edit_profile_success_snackbar">Profile updated</string>
<string name="edit_profile_photo_sheet_title">Change Profile Photo</string>
<string name="edit_profile_photo_take">Take Photo</string>
<string name="edit_profile_photo_gallery">Choose from Gallery</string>
<string name="edit_profile_photo_remove">Remove Photo</string>
<string name="edit_profile_discard_title">Discard Changes?</string>
<string name="edit_profile_discard_body">Your profile changes won\'t be saved.</string>
<string name="edit_profile_keep_editing">Keep Editing</string>
<string name="edit_profile_discard_confirm">Discard</string>

<!-- ─── Sign Out Dialog ──────────────────────────────────────────────────── -->
<string name="signout_title">Sign Out?</string>
<string name="signout_body">You\'ll need to sign in again to access your account.</string>
<string name="signout_confirm">Sign Out</string>
<string name="signout_loading">Signing out…</string>
```

---

## 12. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Profile loads — avatar, name, member since, plan badge | `GetProfileUseCase` | ☐ |
| Mode pill — "Looking" selected highlights left pill | `ModePill(currentMode)` | ☐ |
| Mode pill — switch modes → home feed changes (via prefs) | `prefs.putString("homeMode")` | ☐ |
| "Edit Profile" → navigates to edit screen | `onNavigateToEdit()` | ☐ |
| All menu rows navigate to correct screens | each `ProfileMenuRow.onClick` | ☐ |
| "Sign Out" → dialog shown | `openSignOutDialog()` | ☐ |
| Sign-out dialog confirmed → signs out + clears prefs + navigates to Welcome | `signOut()` | ☐ |
| Edit profile — pre-fills all fields from existing profile | `loadProfile()` in init | ☐ |
| Edit profile — "Save" disabled until changes detected | `isDirty = false` initially | ☐ |
| Edit profile — tap photo → picker sheet | `openPhotoPicker()` | ☐ |
| Edit profile — camera/gallery → photo preview updates immediately | `setPhotoUri(uri)` | ☐ |
| Edit profile — upload happens on Save (not immediately) | `UploadProfilePhotoUseCase` in `save()` | ☐ |
| Edit profile — back with changes → discard dialog | `isDirty && BackHandler` | ☐ |
| Discard dialog — "Keep Editing" → stays | `dismissDiscardDialog()` | ☐ |
| Discard dialog — "Discard" → navigates back | `onBack()` | ☐ |
| Save success → navigate back + snackbar "Profile updated" | `saveSuccess = true` | ☐ |
| Account type chips — non-tappable, show current type | read-only chips | ☐ |
| Province → city cascades (city clears on province change) | `setProvince()` clears city | ☐ |
| Date picker opens on DOB field tap | `DatePickerDialog` | ☐ |

---

## 13. CODE_REVIEW_MODULE_12.md Template

```markdown
# Code Review — Module 12: Profile & Edit Profile
**Spec version:** ANDROID_MODULE_12.md v1.0.0

## ✅ Profile Screen
- [ ] Avatar: 84dp circle, AsyncImage or DarkPriM + User icon fallback
- [ ] ModePill: fullWidth, bg2 container, DarkPri selected pill
- [ ] Menu rows: 38dp icon circle with tinted bg, ChevronRight, 0.5dp divider at start=72dp
- [ ] Sign-out dialog: LogOut icon in DarkBg4 circle (neutral, not DarkRed)

## ✅ Edit Profile
- [ ] "Save" text button disabled until `isDirty`
- [ ] Camera overlay: 35% opacity black scrim + Camera icon
- [ ] Photo upload deferred to Save button (not on pick)
- [ ] Photo compressed to <500KB before upload
- [ ] Account type chips: non-tappable
- [ ] Back + isDirty → DiscardDialog (2-button: PrimaryButton + red TextButton)

## ✅ Build
- [ ] `./gradlew assembleDebug` → ✅
- [ ] `./gradlew test` → ✅
```

*End of Module 12 — Profile & Edit Profile v1.0.0*
*Next: Module 13 — Settings, Packages, Force Update & Connectivity.*
