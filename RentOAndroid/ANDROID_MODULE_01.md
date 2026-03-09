# RentO — Android App
## Module 01 — Design System & Theme
### Complete Engineering Specification

> **Version:** 1.1.0 (Corrected & Completed)
> **Status:** Active — Single Source of Truth for Module 01
> **Branch:** `feature/module-01-design-system`
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every value in this module is derived verbatim from the approved prototype (`design_references.jsx`). Do not substitute, approximate, or "improve" any value. Zero improvisation. If anything is unclear — stop and ask, do not guess.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [Quality Control Rules](#2-quality-control-rules)
3. [Task List](#3-task-list)
4. [Task M01-T01 — Project Bootstrap & Fonts](#task-m01-t01--project-bootstrap--fonts)
5. [Task M01-T02 — Color Tokens (`Color.kt`)](#task-m01-t02--color-tokens-colorkt)
6. [Task M01-T03 — Typography (`Type.kt`)](#task-m01-t03--typography-typekt)
7. [Task M01-T04 — Spacing & Shape Tokens (`Dimens.kt` + `Shape.kt`)](#task-m01-t04--spacing--shape-tokens-dimenskkt--shapekt)
8. [Task M01-T05 — Gradient Constants (`Gradients.kt`)](#task-m01-t05--gradient-constants-gradientskt)
9. [Task M01-T06 — Theme Wiring (`Theme.kt`)](#task-m01-t06--theme-wiring-themekt)
10. [Task M01-T07 — Custom Icon Set (`RentoIcons.kt`)](#task-m01-t07--custom-icon-set-rentoiconskt)
11. [Task M01-T08 — Animation Tokens (`Animations.kt`)](#task-m01-t08--animation-tokens-animationskt)
12. [Task M01-T09 — Component: `RentoStatusBar`](#task-m01-t09--component-rentostatusbar)
13. [Task M01-T10 — Component: `PrimaryButton`](#task-m01-t10--component-primarybutton)
14. [Task M01-T11 — Component: `GhostButton`](#task-m01-t11--component-ghostbutton)
15. [Task M01-T12 — Component: `OutlinePrimaryButton`](#task-m01-t12--component-outlineprimarybutton)
16. [Task M01-T13 — Component: `DestructiveButton`](#task-m01-t13--component-destructivebutton)
17. [Task M01-T14 — Component: `RentoChip`](#task-m01-t14--component-rentochip)
18. [Task M01-T15 — Component: `TabPill`](#task-m01-t15--component-tabpill)
19. [Task M01-T16 — Component: `Badge`](#task-m01-t16--component-badge)
20. [Task M01-T17 — Component: `SectionLabel`](#task-m01-t17--component-sectionlabel)
21. [Task M01-T18 — Component: `UnderlineInputField`](#task-m01-t18--component-underlineinputfield)
22. [Task M01-T19 — Component: `BoxedInputField`](#task-m01-t19--component-boxedinputfield)
23. [Task M01-T20 — Component: `ToggleSwitch`](#task-m01-t20--component-toggleswitch)
24. [Task M01-T21 — Component: `ProgressStepBar`](#task-m01-t21--component-progressstepbar)
25. [Task M01-T22 — Component: `ProgressBar` (linear)](#task-m01-t22--component-progressbar-linear)
26. [Task M01-T23 — Component: `PropertyCard` (Full + Compact)](#task-m01-t23--component-propertycard-full--compact)
27. [Task M01-T24 — Component: `TenantRequestCard`](#task-m01-t24--component-tenantrequestcard)
28. [Task M01-T25 — Component: `BottomNavBar`](#task-m01-t25--component-bottomnavbar)
29. [Task M01-T26 — Component: `AddOverlaySheet`](#task-m01-t26--component-addoverlaysheet)
30. [Task M01-T27 — Component: `HomeBannerSlider`](#task-m01-t27--component-homebannerslider)
31. [Task M01-T28 — Component: `MapBackground`](#task-m01-t28--component-mapbackground)
32. [Task M01-T29 — Component: `RadiusCircle`](#task-m01-t29--component-radiuscircle)
33. [Task M01-T30 — Component: `ShimmerLoader`](#task-m01-t30--component-shimmerloader)
34. [Task M01-T31 — Component: `GlassScrim` + `GlassDialog`](#task-m01-t31--component-glassscrim--glassdialog)
35. [Task M01-T32 — Component: `GlassBottomSheet`](#task-m01-t32--component-glassbottomsheet)
36. [Task M01-T33 — Component: `RentoDeleteSpinner`](#task-m01-t33--component-rentodeletespinner)
37. [Task M01-T34 — Component: `InAppNotificationBanner`](#task-m01-t34--component-inappnotificationbanner)
38. [Task M01-T35 — Component: `EmptyState`](#task-m01-t35--component-emptystate)
39. [Task M01-T36 — Component: `ErrorBanner`](#task-m01-t36--component-errorbanner)
40. [Task M01-T37 — Component: `MeshBackground`](#task-m01-t37--component-meshbackground)
41. [Task M01-T38 — Component: `GradientText`](#task-m01-t38--component-gradienttext)
42. [Task M01-T39 — Unit Tests](#task-m01-t39--unit-tests)
43. [Task M01-T40 — Lint + Detekt + Build Gate](#task-m01-t40--lint--detekt--build-gate)
44. [CSS → Compose Translation Reference](#44-css--compose-translation-reference)
45. [Component Name Mapping (Prototype → Compose)](#45-component-name-mapping-prototype--compose)
46. [Progress File Spec](#46-progress-file-spec)

---

## 1. Module Overview

**Purpose:** Establish every shared UI primitive that every subsequent module will consume. Nothing in this module renders a real screen — it is the foundational library. Every other module depends on it.

**Files produced by this module:**

```
app/src/main/
├── res/
│   ├── font/
│   │   ├── fraunces_light.ttf
│   │   ├── fraunces_regular.ttf
│   │   ├── fraunces_semibold.ttf
│   │   ├── fraunces_bold.ttf
│   │   ├── plus_jakarta_sans_light.ttf
│   │   ├── plus_jakarta_sans_regular.ttf
│   │   ├── plus_jakarta_sans_medium.ttf
│   │   ├── plus_jakarta_sans_semibold.ttf
│   │   ├── plus_jakarta_sans_bold.ttf
│   │   └── plus_jakarta_sans_extrabold.ttf
│   └── values/
│       └── strings.xml          ← All component strings added here
└── java/com/rento/app/
    └── presentation/
        └── shared/
            ├── theme/
            │   ├── Color.kt
            │   ├── Type.kt
            │   ├── Shape.kt
            │   ├── Dimens.kt
            │   ├── Gradients.kt
            │   └── Theme.kt
            ├── icons/
            │   └── RentoIcons.kt
            ├── animations/
            │   └── Animations.kt
            └── components/
                ├── RentoStatusBar.kt
                ├── Buttons.kt               ← PrimaryButton, GhostButton, OutlinePrimaryButton, DestructiveButton
                ├── RentoChip.kt
                ├── TabPill.kt
                ├── Badge.kt
                ├── SectionLabel.kt
                ├── InputFields.kt           ← UnderlineInputField, BoxedInputField
                ├── ToggleSwitch.kt
                ├── ProgressStepBar.kt
                ├── ProgressBar.kt
                ├── PropertyCard.kt
                ├── TenantRequestCard.kt
                ├── BottomNavBar.kt
                ├── AddOverlaySheet.kt
                ├── HomeBannerSlider.kt
                ├── MapBackground.kt
                ├── RadiusCircle.kt
                ├── ShimmerLoader.kt
                ├── GlassDialog.kt           ← GlassScrim, GlassDialog, GlassBottomSheet
                ├── RentoDeleteSpinner.kt
                ├── InAppNotificationBanner.kt
                ├── EmptyState.kt
                ├── ErrorBanner.kt
                ├── MeshBackground.kt
                └── GradientText.kt

app/src/test/java/com/rento/app/
└── presentation/shared/components/
    ├── ButtonsTest.kt
    ├── RentoChipTest.kt
    ├── TabPillTest.kt
    ├── BadgeTest.kt
    ├── InputFieldsTest.kt
    ├── ToggleSwitchTest.kt
    ├── ProgressStepBarTest.kt
    ├── ProgressBarTest.kt
    ├── RadiusCircleTest.kt
    └── GlassDialogTest.kt
```

---

## 2. Quality Control Rules

These rules are **mandatory and non-negotiable**. Violating any rule means the task is **not complete**.

### 2.1 No Improvisation
The agent must not change any design value — colour, size, animation duration, radius, font weight, padding — unless the spec explicitly says "optional" or "editable". Follow the pixel-perfect spec exactly.

### 2.2 No Hardcoded Values
- **Zero hardcoded colour values** anywhere. All colours reference `LocalRentoColors.current.*` via the composition local defined in `Theme.kt`.
- **Zero hardcoded font families or sizes.** All typography references `LocalRentoTypography.current.*`.
- **Zero hardcoded dp/sp values** for design-system constants. All spacing and sizing references `LocalRentoDimens.current.*` from `Dimens.kt`.
- **Zero hardcoded strings.** All user-visible text uses `stringResource(R.string.*)`.

### 2.3 No TODO Rule
Every function, composable, and property defined in this module must be **fully implemented**. No `TODO()`, `FIXME`, empty function bodies, or placeholder comments.

### 2.4 Component Reuse Rule
Once a component is created in this module, it **must be reused** in every subsequent module. Never create a second version of any component. If a variant is needed, add a parameter to the existing composable — never create a parallel component.

### 2.5 Theming Rule
All components must render correctly in **both dark and light theme**. Every colour token has a dark and light variant in `Color.kt`. The `RentoTheme` wrapper in `Theme.kt` provides the correct set based on `isSystemInDarkTheme()` or manual override.

### 2.6 Evidence Rule
The agent must not mark any task ✅ without evidence:
- For a component task: the Compose preview must compile without error, the component must render in both dark and light theme, and all related unit tests must pass.
- For the module: `./gradlew assembleDebug` passes + `./gradlew test` passes + Detekt produces zero errors + lint produces zero errors. The agent must paste the relevant output when marking the module complete.

### 2.7 Detekt Rule
After every task, run `./gradlew detekt`. Fix all code-smell findings before proceeding. Do not suppress warnings with `@Suppress` without a documented reason in the same line comment.

### 2.8 Test Coverage
Minimum **80% line coverage** on the `components/` package. Run `./gradlew koverReport` and paste the summary when closing the module.

### 2.9 Performance Rule
- All `@Composable` functions that accept data must accept `@Stable` or `@Immutable` annotated data classes — never raw mutable types.
- `LazyColumn` / `LazyRow` / `LazyVerticalGrid` must always use `key {}` parameter.
- `remember {}` must wrap any expensive computation inside composables.
- `derivedStateOf {}` must wrap any value derived from state.
- No Firestore calls, network calls, or side effects inside composable bodies — only inside `LaunchedEffect` or `SideEffect`.

---

## 3. Task List

| ID | Task | File | Status |
|----|------|------|--------|
| M01-T01 | Project bootstrap, font assets, `build.gradle` deps | `build.gradle`, `res/font/` | ☐ |
| M01-T02 | Colour tokens — `Color.kt` | `Color.kt` | ☐ |
| M01-T03 | Typography — `Type.kt` | `Type.kt` | ☐ |
| M01-T04 | Spacing + Shape tokens — `Dimens.kt`, `Shape.kt` | `Dimens.kt`, `Shape.kt` | ☐ |
| M01-T05 | Gradient constants — `Gradients.kt` | `Gradients.kt` | ☐ |
| M01-T06 | Theme wiring — `Theme.kt` | `Theme.kt` | ☐ |
| M01-T07 | Custom icon set — `RentoIcons.kt` (all 46 icons) | `RentoIcons.kt` | ☐ |
| M01-T08 | Animation token helpers — `Animations.kt` | `Animations.kt` | ☐ |
| M01-T09 | Component: `RentoStatusBar` | `RentoStatusBar.kt` | ☐ |
| M01-T10 | Component: `PrimaryButton` | `Buttons.kt` | ☐ |
| M01-T11 | Component: `GhostButton` | `Buttons.kt` | ☐ |
| M01-T12 | Component: `OutlinePrimaryButton` | `Buttons.kt` | ☐ |
| M01-T13 | Component: `DestructiveButton` | `Buttons.kt` | ☐ |
| M01-T14 | Component: `RentoChip` | `RentoChip.kt` | ☐ |
| M01-T15 | Component: `TabPill` | `TabPill.kt` | ☐ |
| M01-T16 | Component: `Badge` (5 variants) | `Badge.kt` | ☐ |
| M01-T17 | Component: `SectionLabel` | `SectionLabel.kt` | ☐ |
| M01-T18 | Component: `UnderlineInputField` | `InputFields.kt` | ☐ |
| M01-T19 | Component: `BoxedInputField` | `InputFields.kt` | ☐ |
| M01-T20 | Component: `ToggleSwitch` | `ToggleSwitch.kt` | ☐ |
| M01-T21 | Component: `ProgressStepBar` | `ProgressStepBar.kt` | ☐ |
| M01-T22 | Component: `ProgressBar` (linear) | `ProgressBar.kt` | ☐ |
| M01-T23 | Component: `PropertyCard` (Full + Compact) | `PropertyCard.kt` | ☐ |
| M01-T24 | Component: `TenantRequestCard` | `TenantRequestCard.kt` | ☐ |
| M01-T25 | Component: `BottomNavBar` | `BottomNavBar.kt` | ☐ |
| M01-T26 | Component: `AddOverlaySheet` | `AddOverlaySheet.kt` | ☐ |
| M01-T27 | Component: `HomeBannerSlider` | `HomeBannerSlider.kt` | ☐ |
| M01-T28 | Component: `MapBackground` | `MapBackground.kt` | ☐ |
| M01-T29 | Component: `RadiusCircle` | `RadiusCircle.kt` | ☐ |
| M01-T30 | Component: `ShimmerLoader` | `ShimmerLoader.kt` | ☐ |
| M01-T31 | Component: `GlassScrim` + `GlassDialog` | `GlassDialog.kt` | ☐ |
| M01-T32 | Component: `GlassBottomSheet` | `GlassDialog.kt` | ☐ |
| M01-T33 | Component: `RentoDeleteSpinner` | `RentoDeleteSpinner.kt` | ☐ |
| M01-T34 | Component: `InAppNotificationBanner` | `InAppNotificationBanner.kt` | ☐ |
| M01-T35 | Component: `EmptyState` | `EmptyState.kt` | ☐ |
| M01-T36 | Component: `ErrorBanner` | `ErrorBanner.kt` | ☐ |
| M01-T37 | Component: `MeshBackground` | `MeshBackground.kt` | ☐ |
| M01-T38 | Component: `GradientText` | `GradientText.kt` | ☐ |
| M01-T39 | Unit tests for all components | `*Test.kt` | ☐ |
| M01-T40 | Lint + Detekt + Build gate | — | ☐ |

---

## Task M01-T01 — Project Bootstrap & Fonts

### Sub-tasks
- [ ] **M01-T01-A** Add all required Gradle dependencies to `app/build.gradle.kts`:
```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.10.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.foundation:foundation")
implementation("androidx.compose.animation:animation")
// Koin
implementation("io.insert-koin:koin-android:3.5.6")
implementation("io.insert-koin:koin-androidx-compose:3.5.6")
// Coil
implementation("io.coil-kt:coil-compose:2.7.0")
// Navigation
implementation("androidx.navigation:navigation-compose:2.8.3")
// Firebase BOM
implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")
implementation("com.google.firebase:firebase-remote-config-ktx")
// Google Maps
implementation("com.google.maps.android:maps-compose:4.4.1")
implementation("com.google.android.gms:play-services-maps:19.0.0")
implementation("com.google.android.gms:play-services-auth:21.2.0")
// Image compression
implementation("id.zelory:compressor:3.0.1")
// DataStore
implementation("androidx.datastore:datastore-preferences:1.1.1")
// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
testImplementation("app.cash.turbine:turbine:1.1.0")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
// Kover (coverage)
implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.8.3")
// Detekt
detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
```

- [ ] **M01-T01-B** Configure `minSdk = 30`, `targetSdk = 35`, `compileSdk = 35` in `build.gradle.kts`. Set `buildFeatures { compose = true }` and `composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }`.

- [ ] **M01-T01-C** Configure **Detekt** in root `build.gradle.kts`:
```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}
detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/detekt.yml"))
    autoCorrect = true
}
```
Create `detekt.yml` in root with:
```yaml
complexity:
  LongMethod:
    threshold: 60
style:
  MagicNumber:
    ignoreNumbers: ['0', '1', '-1', '0.0', '1.0', '0.5', '0.97', '0.9', '0.42', '0.12', '0.1']
naming:
  FunctionNaming:
    functionPattern: '[a-z][a-zA-Z0-9]*'
```

- [ ] **M01-T01-D** Configure **Kover** for coverage in root `build.gradle.kts`:
```kotlin
plugins {
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}
koverReport {
    filters {
        includes {
            packages("com.rento.app.presentation.shared")
        }
    }
    verify {
        rule {
            minBound(80)
        }
    }
}
```

- [ ] **M01-T01-E** Download and place all **10 font files** in `res/font/`:
  - `fraunces_light.ttf`, `fraunces_regular.ttf`, `fraunces_semibold.ttf`, `fraunces_bold.ttf`
  - `plus_jakarta_sans_light.ttf`, `plus_jakarta_sans_regular.ttf`, `plus_jakarta_sans_medium.ttf`, `plus_jakarta_sans_semibold.ttf`, `plus_jakarta_sans_bold.ttf`, `plus_jakarta_sans_extrabold.ttf`
  - Source: Google Fonts (both are OFL licensed). Download static weight files, not variable fonts — variable font filenames do not match the required names.
  - Font file names must be **exactly** as listed — they are referenced by name in `Type.kt`. Android resource names must be lowercase and use underscores only.

- [ ] **M01-T01-F** Create `RentoApplication : Application()` class. Register it in `AndroidManifest.xml` with `android:name=".RentoApplication"`. Initialize Koin in `onCreate()`:
```kotlin
class RentoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RentoApplication)
            modules(/* DI modules added per feature module */)
        }
    }
}
```

- [ ] **M01-T01-G** Add permissions block to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

- [ ] **M01-T01-H** Add `google-services.json` to `app/` directory. Apply plugin in `app/build.gradle.kts`:
```kotlin
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
```
Verify `./gradlew assembleDebug` passes before proceeding.

- [ ] **M01-T01-I** Create `MainActivity : ComponentActivity()` with `enableEdgeToEdge()` and `setContent { RentoTheme { /* NavGraph placeholder */ } }`. This must compile — `RentoNavGraph` is a stub that will be replaced in Module 02.

- [ ] **M01-T01-J** Confirm build compiles clean: `./gradlew assembleDebug` → PASS. Zero warnings. Paste output.

---

## Task M01-T02 — Color Tokens (`Color.kt`)

**File:** `presentation/shared/theme/Color.kt`

All colour definitions **exactly** as below. Zero deviation.

### Sub-tasks
- [ ] **M01-T02-A** Define all **Dark Theme** raw colour values:

```kotlin
package com.rento.app.presentation.shared.theme

import androidx.compose.ui.graphics.Color

// ─── Dark Theme Raw Values ────────────────────────────────────────────────────

// Backgrounds (darkest → lightest)
val DarkBg0 = Color(0xFF04100C)   // App background / screen base
val DarkBg1 = Color(0xFF08150F)   // Card surface primary
val DarkBg2 = Color(0xFF0D1E17)   // Card surface secondary / input bg
val DarkBg3 = Color(0xFF142A20)   // Subtle fill / chip bg
val DarkBg4 = Color(0xFF1C3529)   // Progress track / toggle track

// Primary (green)
val DarkPri  = Color(0xFF2ECC8A)  // Primary action, selected states, icons
val DarkPri2 = Color(0xFF54DBA2)  // Lighter primary, gradient mid-stop
val DarkPri3 = Color(0xFFA8F0D8)  // Lightest primary, gradient end-stop
val DarkSec  = Color(0xFF27B99A)  // Secondary green (gradient complement)

// Semantic
val DarkAcc  = Color(0xFFF0C94A)  // Accent / star / warning
val DarkRed  = Color(0xFFE06060)  // Error / rejected / destructive actions
val DarkBlue = Color(0xFF5A9FD4)  // Info / badge

// Text hierarchy
val DarkT0 = Color(0xFFE4F4EC)   // Primary text
val DarkT1 = Color(0xFF9DC8B4)   // Secondary text
val DarkT2 = Color(0xFF5A8A76)   // Tertiary text / section labels / icons
val DarkT3 = Color(0xFF2A4A3C)   // Placeholder / disabled text

// Borders
val DarkBd  = Color(0xFF142A20)  // Standard border
val DarkBd2 = Color(0xFF1C3529)  // Subtle border

// Overlays & navigation bar
val DarkNav = Color(0xF208150F)  // Bottom nav background (~95% opacity over DarkBg1)
val DarkOv  = Color(0xB804100C)  // Modal / sheet overlay (~72% opacity)
val DarkNavFallback = Color(0xCC08150F)  // API 30 nav fallback (80% — no blur available)

// Input
val DarkIpl = Color(0xFF243D30)  // Input underline — idle state

// Semi-transparent tints
val DarkPriM  = Color(0x1A2ECC8A)  // Primary tint 10%
val DarkPriR  = Color(0x4D2ECC8A)  // Primary ring 30% — shadows/glows
val DarkAccM  = Color(0x21F0C94A)  // Accent tint 13%
val DarkRedM  = Color(0x1CE06060)  // Error tint 11%
val DarkBlueM = Color(0x1C5A9FD4)  // Blue tint 11%

// Card image overlay — dark (transparent top → dark bottom, matches .iov CSS class)
val DarkImageOverlayStart = Color(0x0004100C)  // transparent DarkBg0
val DarkImageOverlayMid   = Color(0x2E04100C)  // 18% opacity
val DarkImageOverlayEnd   = Color(0xEB04100C)  // 92% opacity

// Glass dialog
val GlassDialogBgDark   = Color(0xE6081610)   // 90% opaque deep dark green-tinted
val GlassScrimDark      = Color(0x99000000)   // 60% black backdrop
val GlassDialogBorderHighlight = Color(0x2AFFFFFF)   // 16% white top-left highlight
val GlassDialogBorderMid       = Color(0x122ECC8A)   // 7% green mid
val GlassDialogBorderFade      = Color(0x08FFFFFF)   // 3% white fade
```

- [ ] **M01-T02-B** Define all **Light Theme** raw colour values:

```kotlin
// ─── Light Theme Raw Values ───────────────────────────────────────────────────

val LightBg0 = Color(0xFFF0FAF6)
val LightBg1 = Color(0xFFFFFFFF)
val LightBg2 = Color(0xFFFFFFFF)
val LightBg3 = Color(0xFFE8F5EE)
val LightBg4 = Color(0xFFD6EDE5)

val LightPri  = Color(0xFF0C7A50)
val LightPri2 = Color(0xFF14A06A)
val LightPri3 = Color(0xFF1EC888)
val LightSec  = Color(0xFF0D9E86)

val LightAcc  = Color(0xFFB87010)
val LightRed  = Color(0xFFC04040)
val LightBlue = Color(0xFF2E6EA0)

val LightT0 = Color(0xFF06201A)
val LightT1 = Color(0xFF265044)
val LightT2 = Color(0xFF52806E)
val LightT3 = Color(0xFFA0C4B8)

val LightBd  = Color(0xFFC4E2D8)
val LightBd2 = Color(0xFFB0D4C8)

val LightNav         = Color(0xF5FFFFFF)   // ~96% white
val LightNavFallback = Color(0xF0FFFFFF)   // API 30 fallback (94% — no blur)
val LightOv          = Color(0x8006201A)   // ~50% dark-green overlay
val LightIpl         = Color(0xFFB8D8CC)

val LightPriM  = Color(0x140C7A50)  // Primary tint 8%
val LightPriR  = Color(0x3D0C7A50)  // Primary ring 24%
val LightAccM  = Color(0x17B87010)
val LightRedM  = Color(0x12C04040)
val LightBlueM = Color(0x122E6EA0)

// Card image overlay — light (same direction, dark overlay still needed to show white text on card)
val LightImageOverlayStart = Color(0x0006201A)
val LightImageOverlayMid   = Color(0x2606201A)
val LightImageOverlayEnd   = Color(0xE106201A)  // 88% dark-green — prototype value

// Glass dialog
val GlassDialogBgLight = Color(0xF0FFFFFF)      // 94% white
val GlassScrimLight    = Color(0x8C06201A)       // 55% dark-green scrim
val GlassDialogBorderHighlightLight = Color(0x400C7A50)
val GlassDialogBorderMidLight       = Color(0x18000000)
val GlassDialogBorderFadeLight      = Color(0x0A0C7A50)
```

- [ ] **M01-T02-C** Define the `RentoColors` data class and both instances:

```kotlin
data class RentoColors(
    val bg0: Color,
    val bg1: Color,
    val bg2: Color,
    val bg3: Color,
    val bg4: Color,
    val primary: Color,
    val primary2: Color,
    val primary3: Color,
    val secondary: Color,
    val accent: Color,
    val red: Color,
    val blue: Color,
    val t0: Color,
    val t1: Color,
    val t2: Color,
    val t3: Color,
    val border: Color,
    val border2: Color,
    val navBg: Color,
    val navBgFallback: Color,         // API 30 fallback — no blur
    val overlay: Color,
    val inputUnderlineIdle: Color,
    val primaryTint: Color,
    val primaryRing: Color,
    val accentTint: Color,
    val redTint: Color,
    val blueTint: Color,
    val imageOverlayStart: Color,
    val imageOverlayMid: Color,
    val imageOverlayEnd: Color,
    val glassDialogBg: Color,
    val glassScrim: Color,
    val glassDialogBorderHighlight: Color,
    val glassDialogBorderMid: Color,
    val glassDialogBorderFade: Color,
    val isDark: Boolean,
)

val RentoDarkColors = RentoColors(
    bg0 = DarkBg0, bg1 = DarkBg1, bg2 = DarkBg2, bg3 = DarkBg3, bg4 = DarkBg4,
    primary = DarkPri, primary2 = DarkPri2, primary3 = DarkPri3, secondary = DarkSec,
    accent = DarkAcc, red = DarkRed, blue = DarkBlue,
    t0 = DarkT0, t1 = DarkT1, t2 = DarkT2, t3 = DarkT3,
    border = DarkBd, border2 = DarkBd2,
    navBg = DarkNav, navBgFallback = DarkNavFallback,
    overlay = DarkOv,
    inputUnderlineIdle = DarkIpl,
    primaryTint = DarkPriM, primaryRing = DarkPriR,
    accentTint = DarkAccM, redTint = DarkRedM, blueTint = DarkBlueM,
    imageOverlayStart = DarkImageOverlayStart,
    imageOverlayMid   = DarkImageOverlayMid,
    imageOverlayEnd   = DarkImageOverlayEnd,
    glassDialogBg = GlassDialogBgDark, glassScrim = GlassScrimDark,
    glassDialogBorderHighlight = GlassDialogBorderHighlight,
    glassDialogBorderMid       = GlassDialogBorderMid,
    glassDialogBorderFade      = GlassDialogBorderFade,
    isDark = true,
)

val RentoLightColors = RentoColors(
    bg0 = LightBg0, bg1 = LightBg1, bg2 = LightBg2, bg3 = LightBg3, bg4 = LightBg4,
    primary = LightPri, primary2 = LightPri2, primary3 = LightPri3, secondary = LightSec,
    accent = LightAcc, red = LightRed, blue = LightBlue,
    t0 = LightT0, t1 = LightT1, t2 = LightT2, t3 = LightT3,
    border = LightBd, border2 = LightBd2,
    navBg = LightNav, navBgFallback = LightNavFallback,
    overlay = LightOv,
    inputUnderlineIdle = LightIpl,
    primaryTint = LightPriM, primaryRing = LightPriR,
    accentTint = LightAccM, redTint = LightRedM, blueTint = LightBlueM,
    imageOverlayStart = LightImageOverlayStart,
    imageOverlayMid   = LightImageOverlayMid,
    imageOverlayEnd   = LightImageOverlayEnd,
    glassDialogBg = GlassDialogBgLight, glassScrim = GlassScrimLight,
    glassDialogBorderHighlight = GlassDialogBorderHighlightLight,
    glassDialogBorderMid       = GlassDialogBorderMidLight,
    glassDialogBorderFade      = GlassDialogBorderFadeLight,
    isDark = false,
)
```

- [ ] **M01-T02-D** Define `LocalRentoColors` composition local:

```kotlin
val LocalRentoColors = staticCompositionLocalOf<RentoColors> { RentoDarkColors }
```

- [ ] **M01-T02-E** Verify: every colour used across the entire spec is represented as a named token. No screen or component may use a raw `Color(0x...)` inline value.

---

## Task M01-T03 — Typography (`Type.kt`)

**File:** `presentation/shared/theme/Type.kt`

### Sub-tasks
- [ ] **M01-T03-A** Declare both font families using the bundled font files:

```kotlin
val FrauncesFamily = FontFamily(
    Font(R.font.fraunces_light,    FontWeight.Light),
    Font(R.font.fraunces_regular,  FontWeight.Normal),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_bold,     FontWeight.Bold),
)

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_light,     FontWeight.Light),
    Font(R.font.plus_jakarta_sans_regular,   FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium,    FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold,  FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,      FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)
```

- [ ] **M01-T03-B** Define the full **Type Scale** as named `TextStyle` values. All sizes are exact:

```kotlin
// ── Display (Fraunces) ──────────────────────────────────────────────────────
val RentoDisplayXL = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 50.sp, lineHeight = 56.sp,
)
val RentoDisplayL = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp, lineHeight = 38.sp,
)
val RentoDisplayM = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp, lineHeight = 34.sp,
)
val RentoDisplayS = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp, lineHeight = 28.sp,
)
val RentoDisplaySLarge = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 26.sp, lineHeight = 32.sp,
)

// ── Body / UI (Plus Jakarta Sans) ──────────────────────────────────────────
val RentoBodyL = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Bold,
    fontSize = 15.sp, lineHeight = 22.sp,
)
val RentoBodyLSemiBold = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp, lineHeight = 24.sp,
)
val RentoBodyM = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Normal,
    fontSize = 14.sp, lineHeight = 22.sp,
)
val RentoBodyMMedium = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Medium,
    fontSize = 14.sp, lineHeight = 22.sp,
)
val RentoBodyS = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Normal,
    fontSize = 13.sp, lineHeight = 20.sp,
)
val RentoBodySMedium = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Medium,
    fontSize = 13.sp, lineHeight = 20.sp,
)
val RentoLabel = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Bold,
    fontSize = 11.sp, letterSpacing = 0.06.em,
)
val RentoLabelLarge = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Bold,
    fontSize = 12.sp, letterSpacing = 0.06.em,
)
val RentoMicro = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Normal,
    fontSize = 10.sp, lineHeight = 14.sp,
)
val RentoMicroMedium = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Medium,
    fontSize = 11.sp, lineHeight = 15.sp,
)
```

- [ ] **M01-T03-C** Define `RentoTypography` data class and `LocalRentoTypography`:

```kotlin
data class RentoTypography(
    val displayXL: TextStyle      = RentoDisplayXL,
    val displayL: TextStyle       = RentoDisplayL,
    val displayM: TextStyle       = RentoDisplayM,
    val displayS: TextStyle       = RentoDisplayS,
    val displaySLarge: TextStyle  = RentoDisplaySLarge,
    val bodyL: TextStyle          = RentoBodyL,
    val bodyLSemiBold: TextStyle  = RentoBodyLSemiBold,
    val bodyM: TextStyle          = RentoBodyM,
    val bodyMMedium: TextStyle    = RentoBodyMMedium,
    val bodyS: TextStyle          = RentoBodyS,
    val bodySMedium: TextStyle    = RentoBodySMedium,
    val label: TextStyle          = RentoLabel,
    val labelLarge: TextStyle     = RentoLabelLarge,
    val micro: TextStyle          = RentoMicro,
    val microMedium: TextStyle    = RentoMicroMedium,
)

val LocalRentoTypography = staticCompositionLocalOf<RentoTypography> { RentoTypography() }
```

- [ ] **M01-T03-D** Map to Material3 `Typography` for Material component compatibility:

```kotlin
fun RentoTypography.toMaterial3Typography() = Typography(
    displayLarge   = displayXL,
    displayMedium  = displayL,
    displaySmall   = displayM,
    headlineLarge  = displaySLarge,
    headlineMedium = displayS,
    bodyLarge      = bodyL,
    bodyMedium     = bodyM,
    bodySmall      = bodyS,
    labelLarge     = labelLarge,
    labelMedium    = label,
    labelSmall     = micro,
)
```

---

## Task M01-T04 — Spacing & Shape Tokens (`Dimens.kt` + `Shape.kt`)

### Sub-tasks
- [ ] **M01-T04-A** Create `Dimens.kt`:

```kotlin
package com.rento.app.presentation.shared.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RentoDimens(
    // Screen layout
    val screenPadH: Dp              = 20.dp,
    val screenPadTop: Dp            = 16.dp,
    val sectionPad: Dp              = 24.dp,
    val sectionGap: Dp              = 24.dp,

    // Card
    val cardRadius: Dp              = 24.dp,
    val cardRadiusSmall: Dp         = 18.dp,
    val cardRadiusMedium: Dp        = 20.dp,
    val cardGap: Dp                 = 16.dp,
    val gridGap: Dp                 = 12.dp,
    val cardImageHeightFull: Dp     = 190.dp,
    val cardImageHeightCompact: Dp  = 108.dp,

    // Buttons
    val btnRadius: Dp               = 100.dp,
    val btnPadV: Dp                 = 16.dp,
    val btnPadH: Dp                 = 24.dp,

    // Chips
    val chipRadius: Dp              = 100.dp,
    val chipPadV: Dp                = 7.dp,
    val chipPadH: Dp                = 14.dp,
    val chipGap: Dp                 = 8.dp,
    val chipGapLarge: Dp            = 10.dp,

    // Input fields
    val inputWrapperRadius: Dp      = 16.dp,
    val inputPadV: Dp               = 12.dp,
    val inputPadH: Dp               = 16.dp,
    val inputBorderWidth: Dp        = 1.5.dp,
    val inputUnderlineWidth: Dp     = 2.dp,

    // Badges
    val badgeRadius: Dp             = 7.dp,
    val badgePadV: Dp               = 3.dp,
    val badgePadH: Dp               = 9.dp,

    // Navigation bar
    val navHeight: Dp               = 86.dp,
    val navPaddingBottom: Dp        = 18.dp,
    val navPaddingH: Dp             = 8.dp,
    val navItemRadius: Dp           = 18.dp,
    val navItemPadV: Dp             = 8.dp,
    val navItemPadH: Dp             = 14.dp,
    val navIconSize: Dp             = 22.dp,
    val navLabelGap: Dp             = 4.dp,

    // FAB (centre add button)
    val fabSize: Dp                 = 54.dp,

    // Detail screen
    val detailImageHeight: Dp       = 315.dp,
    val sliderHeight: Dp            = 162.dp,

    // Icon button (back, share, save etc.)
    val iconBtnSize: Dp             = 42.dp,
    val iconBtnRadius: Dp           = 15.dp,

    // Avatar
    val avatarSizeS: Dp             = 44.dp,
    val avatarSizeM: Dp             = 52.dp,
    val avatarSizeL: Dp             = 74.dp,
    val avatarSizeXL: Dp            = 84.dp,

    // Amenity / stat tile
    val amenityTileRadius: Dp       = 16.dp,
    val amenityTilePadV: Dp         = 14.dp,
    val amenityTilePadH: Dp         = 8.dp,

    // Toggle switch
    val toggleWidth: Dp             = 46.dp,
    val toggleHeight: Dp            = 26.dp,
    val toggleThumbSize: Dp         = 20.dp,
    val toggleThumbOffset: Dp       = 3.dp,  // inset from each edge in off/on states

    // Bottom sheet drag handle
    val dragHandleWidth: Dp         = 40.dp,
    val dragHandleHeight: Dp        = 5.dp,
    val dragHandleTopPad: Dp        = 12.dp,
    val dragHandleBottomPad: Dp     = 24.dp,

    // Progress bar
    val progressBarHeight: Dp       = 4.dp,
    val progressStepDotActiveWidth: Dp   = 22.dp,
    val progressStepDotHeight: Dp        = 5.dp,
    val progressStepDotInactiveWidth: Dp = 5.dp,
    val progressStepDotGap: Dp           = 5.dp,
    val progressStepLabelGap: Dp         = 7.dp,

    // Glass dialog
    val glassDialogRadius: Dp            = 28.dp,
    val glassDialogWidthFraction: Float  = 0.88f,
    val glassDialogPadH: Dp              = 24.dp,
    val glassDialogPadV: Dp              = 28.dp,
    val glassDialogIconCircleSize: Dp    = 56.dp,
    val glassDialogIconSize: Dp          = 26.dp,
    val glassDialogIconBottomGap: Dp     = 16.dp,

    // Map background grid
    val mapGridSpacing: Dp               = 28.dp,

    // Border widths
    val borderStandard: Dp               = 1.5.dp,
    val borderThin: Dp                   = 1.dp,

    // Add overlay sheet
    val addOverlaySheetCornerTop: Dp     = 28.dp,
    val addOverlaySheetPadH: Dp          = 20.dp,
    val addOverlaySheetPadBottom: Dp     = 48.dp,
    val addOverlayCardIconCircleSize: Dp = 56.dp,

    // Banner slider
    val bannerSliderMarginH: Dp          = 20.dp,
    val bannerSliderRadius: Dp           = 22.dp,
    val bannerSliderHeight: Dp           = 162.dp,
    val bannerDotActiveWidth: Dp         = 20.dp,
    val bannerDotInactiveWidth: Dp       = 6.dp,
    val bannerDotHeight: Dp              = 6.dp,

    // Notification dot (unread indicator on bell)
    val notifDotSize: Dp                 = 9.dp,
    val notifDotBorder: Dp               = 2.5.dp,

    // In-app notification banner
    val inAppBannerRadius: Dp            = 16.dp,
    val inAppBannerPadH: Dp              = 16.dp,
    val inAppBannerPadV: Dp              = 14.dp,

    // Empty state
    val emptyStateIconSize: Dp           = 80.dp,
    val emptyStateIconRadius: Dp         = 28.dp,

    // Error banner
    val errorBannerRadius: Dp            = 14.dp,
    val errorBannerPadH: Dp              = 16.dp,
    val errorBannerPadV: Dp              = 12.dp,
)

val LocalRentoDimens = staticCompositionLocalOf<RentoDimens> { RentoDimens() }
```

- [ ] **M01-T04-B** Create `Shape.kt`:

```kotlin
package com.rento.app.presentation.shared.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object RentoShapes {
    val card             = RoundedCornerShape(24.dp)
    val cardSmall        = RoundedCornerShape(18.dp)
    val cardMedium       = RoundedCornerShape(20.dp)
    val pill             = RoundedCornerShape(100.dp)
    val inputWrapper     = RoundedCornerShape(16.dp)
    val badge            = RoundedCornerShape(7.dp)
    val iconButton       = RoundedCornerShape(15.dp)
    val amenityTile      = RoundedCornerShape(16.dp)
    val navItem          = RoundedCornerShape(18.dp)
    val glassDialog      = RoundedCornerShape(28.dp)
    val glassSheetTop    = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val addSheetTop      = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val bannerSlider     = RoundedCornerShape(22.dp)
    val mapContainer     = RoundedCornerShape(18.dp)
    val notifBanner      = RoundedCornerShape(16.dp)
    val errorBanner      = RoundedCornerShape(14.dp)
    val avatarCircle     = RoundedCornerShape(100.dp)
    val toggleSwitch     = RoundedCornerShape(100.dp)
}
```

---

## Task M01-T05 — Gradient Constants (`Gradients.kt`)

**File:** `presentation/shared/theme/Gradients.kt`

### Sub-tasks
- [ ] **M01-T05-A** Define all gradient `Brush` constants:

```kotlin
package com.rento.app.presentation.shared.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary action button gradient: top-left → bottom-right (135° equivalent)
fun gradientPrimary(colors: RentoColors): Brush = Brush.linearGradient(
    colors = listOf(colors.primary, colors.secondary),
    start  = Offset(0f, 0f),
    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

// Gradient text brush (hero headings — "Your Next Home" etc.)
// Matches prototype: background: linear-gradient(130deg, pri 0%, pri2 55%, pri3 100%)
fun gradientText(colors: RentoColors): Brush = Brush.linearGradient(
    colorStops = arrayOf(
        0.00f to colors.primary,
        0.55f to colors.primary2,
        1.00f to colors.primary3,
    ),
    start = Offset(0f, 0f),
    end   = Offset(Float.POSITIVE_INFINITY, 0f),
)

// Glass dialog border brush — simulates light catching glass edge
// Uses per-theme colour tokens from Color.kt — no inline hex allowed here
fun glassDialogBorder(colors: RentoColors): Brush = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to colors.glassDialogBorderHighlight,
        0.4f to colors.glassDialogBorderMid,
        1.0f to colors.glassDialogBorderFade,
    ),
    start = Offset(0f, 0f),
    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

// Property card background gradients — 4 variants cycled by listing index
// Exact values from prototype PD array
val GradientCard1: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF03200E), Color(0xFF083C1C), Color(0xFF052814)),
    start  = Offset(0f, 0f),
    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
val GradientCard2: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF031E18), Color(0xFF073828), Color(0xFF042818)),
    start  = Offset(0f, 0f),
    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
val GradientCard3: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF041C0E), Color(0xFF093618), Color(0xFF062412)),
    start  = Offset(0f, 0f),
    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
val GradientCard4: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF031A14), Color(0xFF073020), Color(0xFF041C12)),
    start  = Offset(0f, 0f),
    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

val CardGradients: List<Brush> = listOf(GradientCard1, GradientCard2, GradientCard3, GradientCard4)

fun cardGradientForIndex(index: Int): Brush = CardGradients[index % CardGradients.size]

// Image overlay gradient — vertical, transparent-top to dark-bottom
// Used on all card images and detail screen header image.
// Uses per-theme tokens so the overlay works correctly in light mode too.
fun gradientImageOverlay(colors: RentoColors): Brush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to colors.imageOverlayStart,
        0.55f to colors.imageOverlayMid,
        1.00f to colors.imageOverlayEnd,
    ),
)

// Banner slider featured pill gradient (subtle green tint)
fun gradientFeaturedPill(colors: RentoColors): Brush = Brush.linearGradient(
    colors = listOf(colors.primaryTint, colors.primaryTint),
)
```

> ⚠️ Note: `GradientCard1–4` use raw `Color(0xFF...)` values because they are **card identity colours** derived directly from prototype data, not theme tokens. These are the only permitted inline colour values in this file. They do NOT change between light and dark mode — card gradients are always deep green.

---

## Task M01-T06 — Theme Wiring (`Theme.kt`)

**File:** `presentation/shared/theme/Theme.kt`

### Sub-tasks
- [ ] **M01-T06-A** Implement `RentoTheme` composable:

```kotlin
@Composable
fun RentoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) RentoDarkColors else RentoLightColors
    val typography = RentoTypography()
    val dimens = RentoDimens()

    CompositionLocalProvider(
        LocalRentoColors provides colors,
        LocalRentoTypography provides typography,
        LocalRentoDimens provides dimens,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography  = typography.toMaterial3Typography(),
            content     = content,
        )
    }
}
```

- [ ] **M01-T06-B** Implement `RentoColors.toMaterialColorScheme()`:

```kotlin
fun RentoColors.toMaterialColorScheme() = if (isDark) {
    darkColorScheme(
        primary          = primary,
        onPrimary        = Color.White,
        primaryContainer = primaryTint,
        background       = bg0,
        surface          = bg1,
        surfaceVariant   = bg2,
        onBackground     = t0,
        onSurface        = t0,
        onSurfaceVariant = t1,
        error            = red,
        onError          = Color.White,
        outline          = border,
        outlineVariant   = border2,
    )
} else {
    lightColorScheme(
        primary          = primary,
        onPrimary        = Color.White,
        primaryContainer = primaryTint,
        background       = bg0,
        surface          = bg1,
        surfaceVariant   = bg2,
        onBackground     = t0,
        onSurface        = t0,
        onSurfaceVariant = t1,
        error            = red,
        onError          = Color.White,
        outline          = border,
        outlineVariant   = border2,
    )
}
```

- [ ] **M01-T06-C** Add ergonomic extension properties on `MaterialTheme`:

```kotlin
val MaterialTheme.rentoColors: RentoColors
    @Composable @ReadOnlyComposable
    get() = LocalRentoColors.current

val MaterialTheme.rentoTypography: RentoTypography
    @Composable @ReadOnlyComposable
    get() = LocalRentoTypography.current

val MaterialTheme.rentoDimens: RentoDimens
    @Composable @ReadOnlyComposable
    get() = LocalRentoDimens.current
```

- [ ] **M01-T06-D** Apply `RentoTheme` in `MainActivity`. Also set `WindowCompat.setDecorFitsSystemWindows(window, false)` for proper edge-to-edge:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RentoTheme {
                RentoNavGraph()  // placeholder — replaced in Module 02
            }
        }
    }
}
```

---

## Task M01-T07 — Custom Icon Set (`RentoIcons.kt`)

**File:** `presentation/shared/icons/RentoIcons.kt`

> ⚠️ All 46 icons must be implemented as custom `ImageVector` objects. Do **not** import or substitute any Material Icons library icon. Each icon replicates the exact SVG path data from the prototype's `const I = { ... }` block. `strokeLinecap = StrokeCap.Round`, `strokeLinejoin = StrokeJoin.Round`, default `strokeWidth = 1.8f`. Check/Ok icons use `strokeWidth = 2.5f`.

### Sub-tasks
- [ ] **M01-T07-A** Create the `buildIcon` private helper:

```kotlin
private fun buildIcon(
    name: String,
    strokeWidth: Float = 1.8f,
    block: ImageVector.Builder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name           = "RentoIcons.$name",
    defaultWidth   = 24.dp,
    defaultHeight  = 24.dp,
    viewportWidth  = 24f,
    viewportHeight = 24f,
).apply(block).build()
```

- [ ] **M01-T07-B** Implement `object RentoIcons` with all icons. Each icon's path data is transcribed verbatim from the prototype. Full list with SVG paths:

```kotlin
object RentoIcons {

    // Home / House (same icon, two names)
    // path: M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z
    val Home: ImageVector by lazy {
        buildIcon("Home") {
            addPath(pathData = "M3,9l9,-7 9,7v11a2,2 0 0,1 -2,2H5a2,2 0 0,1 -2,-2z",
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round)
        }
    }
    val House: ImageVector get() = Home

    // Building: rect + 6 circles (windows)
    // rect x=4 y=2 w=16 h=20 rx=2; path M9 22v-4h6v4; circles at (9,7),(15,7),(9,11),(15,11),(9,15),(15,15)
    val Building: ImageVector by lazy {
        buildIcon("Building") {
            addPath(pathData = "M4,2h16a2,2 0 0,1 2,2v16a2,2 0 0,1 -2,2H4a2,2 0 0,1 -2,-2V4a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M9,22v-4h6v4",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            // windows: 6 small circles drawn as paths
            listOf(Pair(9f,7f),Pair(15f,7f),Pair(9f,11f),Pair(15f,11f),Pair(9f,15f),Pair(15f,15f)).forEach { (cx,cy) ->
                addPath(pathData = "M${cx},${cy}m-1,0a1,1 0 1,0 2,0a1,1 0 1,0 -2,0",
                    fill = SolidColor(Color.Black))
            }
        }
    }

    // Clock: circle + polyline hands
    // circle cx=12 cy=12 r=10; polyline 12 6 12 12 16 14
    val Clock: ImageVector by lazy {
        buildIcon("Clock") {
            addPath(pathData = "M12,2A10,10 0 1,0 12,22A10,10 0 0,0 12,2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,6L12,12L16,14",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Door: path + 2 lines
    // path: M18 20V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16; line x1=2 y1=20 x2=22 y2=20; line x1=14 y1=12 x2=14.01 y2=12
    val Door: ImageVector by lazy {
        buildIcon("Door") {
            addPath(pathData = "M18,20V4a2,2 0 0,0 -2,-2H8a2,2 0 0,0 -2,2v16",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M2,20L22,20",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M14,12L14.01,12",
                stroke = SolidColor(Color.Black), strokeLineWidth = 3f,
                strokeLineCap = StrokeCap.Round)
        }
    }

    // Wifi: 3 arcs + dot
    // path: M5 12.55a11 11 0 0 1 14.08 0; path: M1.42 9a16 16 0 0 1 21.16 0; path: M8.53 16.11a6 6 0 0 1 6.95 0; line x1=12 y1=20 x2=12.01 y2=20
    val Wifi: ImageVector by lazy {
        buildIcon("Wifi") {
            addPath(pathData = "M5,12.55a11,11 0 0,1 14.08,0", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M1.42,9a16,16 0 0,1 21.16,0", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M8.53,16.11a6,6 0 0,1 6.95,0", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M12,20L12.01,20", stroke = SolidColor(Color.Black), strokeLineWidth = 3f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Snow (snowflake): 3 lines + 6 arrows
    // line x1=2 y1=12 x2=22 y2=12; line x1=12 y1=2 x2=12 y2=22; 4 polylines
    val Snow: ImageVector by lazy {
        buildIcon("Snow") {
            addPath(pathData = "M2,12L22,12", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M12,2L12,22", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M20,16L12,12L20,8", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M4,8L12,12L4,16", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M16,4L12,12L8,4", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M8,20L12,12L16,20", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Zap: polygon M13 2 3 14 12 14 11 22 21 10 12 10 13 2
    val Zap: ImageVector by lazy {
        buildIcon("Zap") {
            addPath(pathData = "M13,2L3,14L12,14L11,22L21,10L12,10Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Drop (water droplet): path M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z
    val Drop: ImageVector by lazy {
        buildIcon("Drop") {
            addPath(pathData = "M12,2.69l5.66,5.66a8,8 0 1,1 -11.31,0Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Flame: path M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z
    val Flame: ImageVector by lazy {
        buildIcon("Flame") {
            addPath(pathData = "M8.5,14.5A2.5,2.5 0 0,0 11,12c0,-1.38 -0.5,-2 -1,-3C8.928,6.857 9.776,4.946 12,3c0.5,2.5 2,4.9 4,6.5c2,1.6 3,3.5 3,5.5a7,7 0 1,1 -14,0c0,-1.153 0.433,-2.294 1,-3a2.5,2.5 0 0,0 2.5,2.5z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Parking: rect x=3 y=3 w=18 h=18 rx=2; path M9 17V7h4a3 3 0 0 1 0 6H9
    val Parking: ImageVector by lazy {
        buildIcon("Parking") {
            addPath(pathData = "M3,3h18a2,2 0 0,1 2,2v14a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2V5a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M9,17V7h4a3,3 0 0,1 0,6H9",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Lift: rect x=4 y=4 w=16 h=16 rx=2; polyline 16 10 12 6 8 10; line x1=12 y1=18 x2=12 y2=6
    val Lift: ImageVector by lazy {
        buildIcon("Lift") {
            addPath(pathData = "M4,4h16a2,2 0 0,1 2,2v12a2,2 0 0,1 -2,2H4a2,2 0 0,1 -2,-2V6a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M16,10L12,6L8,10",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,18L12,6",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Leaf: path M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z; line x1=2 y1=22 x2=11 y2=20
    val Leaf: ImageVector by lazy {
        buildIcon("Leaf") {
            addPath(pathData = "M11,20A7,7 0 0,1 9.8,6.1C15.5,5 17,4.48 19,2c1,2 2,4.18 2,8c0,5.5 -4.78,10 -10,10Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M2,22L11,20",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Cctv (video camera): polygon 23 7 16 12 23 17 23 7; rect x=1 y=5 w=15 h=14 rx=2
    val Cctv: ImageVector by lazy {
        buildIcon("Cctv") {
            addPath(pathData = "M23,7L16,12L23,17Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M1,5h15a2,2 0 0,1 2,2v10a2,2 0 0,1 -2,2H1a2,2 0 0,1 -2,-2V7a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Wash (washer): rect x=4 y=2 w=16 h=20 rx=2; circle cx=12 cy=14 r=4; line x1=4 y1=6 x2=20 y2=6
    val Wash: ImageVector by lazy {
        buildIcon("Wash") {
            addPath(pathData = "M4,2h16a2,2 0 0,1 2,2v16a2,2 0 0,1 -2,2H4a2,2 0 0,1 -2,-2V4a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,10A4,4 0 1,0 12,18A4,4 0 0,0 12,10z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            addPath(pathData = "M4,6L20,6",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Paw: centre body + 4 toe pads
    val Paw: ImageVector by lazy {
        buildIcon("Paw") {
            addPath(pathData = "M12,12c-2.76,0 -5,2.24 -5,5s2.24,5 5,5s5,-2.24 5,-5s-2.24,-5 -5,-5z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            listOf(Pair(6f,10f), Pair(18f,10f)).forEach { (cx,cy) ->
                addPath(pathData = "M${cx},${cy-3}A3,3 0 1,0 ${cx},${cy+3}A3,3 0 0,0 ${cx},${cy-3}",
                    stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            }
            listOf(Pair(10.5f,5f), Pair(13.5f,5f)).forEach { (cx,cy) ->
                addPath(pathData = "M${cx},${cy-3}A3,3 0 1,0 ${cx},${cy+3}A3,3 0 0,0 ${cx},${cy-3}",
                    stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            }
        }
    }

    // Hospital: rect x=3 y=3 w=18 h=18 rx=2; + cross
    val Hospital: ImageVector by lazy {
        buildIcon("Hospital") {
            addPath(pathData = "M3,3h18a2,2 0 0,1 2,2v14a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2V5a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,8L12,16", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M8,12L16,12", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Cart: circle(9,21,1); circle(20,21,1); path M1 1h4l2.68 13.39...
    val Cart: ImageVector by lazy {
        buildIcon("Cart") {
            addPath(pathData = "M9,20A1,1 0 1,0 9,22A1,1 0 0,0 9,20z", fill = SolidColor(Color.Black))
            addPath(pathData = "M20,20A1,1 0 1,0 20,22A1,1 0 0,0 20,20z", fill = SolidColor(Color.Black))
            addPath(pathData = "M1,1h4l2.68,13.39a2,2 0 0,0 2,1.61h9.72a2,2 0 0,0 2,-1.61L23,6H6",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // School (open book): 2 paths
    val School: ImageVector by lazy {
        buildIcon("School") {
            addPath(pathData = "M4,19.5A2.5,2.5 0 0,1 6.5,17H20",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M6.5,2H20v20H6.5A2.5,2.5 0 0,1 4,19.5v-15A2.5,2.5 0 0,1 6.5,2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Bank: 5 columns + baseline + triangle roof
    val Bank: ImageVector by lazy {
        buildIcon("Bank") {
            addPath(pathData = "M3,22L21,22", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            listOf(6f,10f,14f,18f).forEach { x ->
                addPath(pathData = "M${x},18L${x},11", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            }
            addPath(pathData = "M12,2L20,7L4,7Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Star: polygon M12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2
    val Star: ImageVector by lazy {
        buildIcon("Star") {
            addPath(pathData = "M12,2L15.09,8.26L22,9.27L17,14.14L18.18,21.02L12,17.77L5.82,21.02L7,14.14L2,9.27L8.91,8.26Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Check / Ok: M20 6L9 17l-5-5  (strokeWidth 2.5)
    val Check: ImageVector by lazy {
        buildIcon("Check", strokeWidth = 2.5f) {
            addPath(pathData = "M20,6L9,17L4,12",
                stroke = SolidColor(Color.Black), strokeLineWidth = 2.5f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }
    val Ok: ImageVector get() = Check

    // Close / X: two diagonal lines
    val Close: ImageVector by lazy {
        buildIcon("Close") {
            addPath(pathData = "M18,6L6,18", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M6,6L18,18", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }
    val X: ImageVector get() = Close

    // Users: 2 head silhouettes
    val Users: ImageVector by lazy {
        buildIcon("Users") {
            addPath(pathData = "M17,21v-2a4,4 0 0,0 -4,-4H5a4,4 0 0,0 -4,4v2",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M9,3A4,4 0 1,0 9,11A4,4 0 0,0 9,3z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            addPath(pathData = "M23,21v-2a4,4 0 0,0 -3,-3.87",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M16,3.13a4,4 0 0,1 0,7.75",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Map: folded map with fold lines
    val MapIcon: ImageVector by lazy {
        buildIcon("Map") {
            addPath(pathData = "M1,6L1,22L8,18L16,22L23,18L23,2L16,6L8,2Z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M8,2L8,18", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M16,6L16,22", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Chat bubble: d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
    val Chat: ImageVector by lazy {
        buildIcon("Chat") {
            addPath(pathData = "M21,15a2,2 0 0,1 -2,2H7l-4,4V5a2,2 0 0,1 2,-2h14a2,2 0 0,1 2,2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Heart outline
    val Heart: ImageVector by lazy {
        buildIcon("Heart") {
            addPath(pathData = "M20.84,4.61a5.5,5.5 0 0,0 -7.78,0L12,5.67l-1.06,-1.06a5.5,5.5 0 0,0 -7.78,7.78l1.06,1.06L12,21.23l7.78,-7.78l1.06,-1.06a5.5,5.5 0 0,0 0,-7.78z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }
    // Heart filled (for saved state)
    val HeartFilled: ImageVector by lazy {
        buildIcon("HeartFilled") {
            addPath(pathData = "M20.84,4.61a5.5,5.5 0 0,0 -7.78,0L12,5.67l-1.06,-1.06a5.5,5.5 0 0,0 -7.78,7.78l1.06,1.06L12,21.23l7.78,-7.78l1.06,-1.06a5.5,5.5 0 0,0 0,-7.78z",
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // User: head circle + shoulders path
    val User: ImageVector by lazy {
        buildIcon("User") {
            addPath(pathData = "M20,21v-2a4,4 0 0,0 -4,-4H8a4,4 0 0,0 -4,4v2",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,3A4,4 0 1,0 12,11A4,4 0 0,0 12,3z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
        }
    }

    // Bell: path M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9; path M13.73 21a2 2 0 0 1-3.46 0
    val Bell: ImageVector by lazy {
        buildIcon("Bell") {
            addPath(pathData = "M18,8A6,6 0 0,0 6,8c0,7 -3,9 -3,9h18s-3,-2 -3,-9",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M13.73,21a2,2 0 0,1 -3.46,0",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Search: circle + handle
    val Search: ImageVector by lazy {
        buildIcon("Search") {
            addPath(pathData = "M11,3A8,8 0 1,0 11,19A8,8 0 0,0 11,3z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            addPath(pathData = "M21,21L16.65,16.65",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Back (left arrow): M19 12H5M12 19l-7-7 7-7
    val Back: ImageVector by lazy {
        buildIcon("Back") {
            addPath(pathData = "M19,12H5", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M12,19L5,12L12,5", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Plus: M12 5v14M5 12h14
    val Plus: ImageVector by lazy {
        buildIcon("Plus") {
            addPath(pathData = "M12,5L12,19", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M5,12L19,12", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Filter: 3 horizontal tapered lines
    val Filter: ImageVector by lazy {
        buildIcon("Filter") {
            addPath(pathData = "M4,6L20,6", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M8,12L16,12", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M11,18L13,18", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Pin: teardrop + inner circle
    val Pin: ImageVector by lazy {
        buildIcon("Pin") {
            addPath(pathData = "M21,10c0,7 -9,13 -9,13s-9,-6 -9,-13a9,9 0 0,1 18,0z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,7A3,3 0 1,0 12,13A3,3 0 0,0 12,7z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
        }
    }

    // Share: 3 circles + 2 lines
    val Share: ImageVector by lazy {
        buildIcon("Share") {
            addPath(pathData = "M18,2A3,3 0 1,0 18,8A3,3 0 0,0 18,2z", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            addPath(pathData = "M6,9A3,3 0 1,0 6,15A3,3 0 0,0 6,9z", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            addPath(pathData = "M18,16A3,3 0 1,0 18,22A3,3 0 0,0 18,16z", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            addPath(pathData = "M8.59,13.51L15.42,17.49", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M15.41,6.51L8.59,10.49", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Bed: headboard + mattress + legs
    val Bed: ImageVector by lazy {
        buildIcon("Bed") {
            addPath(pathData = "M2,4L2,20", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M2,8h20v12H2", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M2,8c0,-2.2 1.8,-4 4,-4h12c2.2,0 4,1.8 4,4",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Bath: tub + faucet feet
    val Bath: ImageVector by lazy {
        buildIcon("Bath") {
            addPath(pathData = "M4,12h16a1,1 0 0,1 1,1v3a4,4 0 0,1 -4,4H7a4,4 0 0,1 -4,-4v-3a1,1 0 0,1 1,-1z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M4,12V6a2,2 0 0,1 2,-2h0a2,2 0 0,1 2,2v0.5",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Send: paper plane
    val Send: ImageVector by lazy {
        buildIcon("Send") {
            addPath(pathData = "M22,2L11,13", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M22,2L15,22L11,13L2,9Z", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Camera: body + lens
    val Camera: ImageVector by lazy {
        buildIcon("Camera") {
            addPath(pathData = "M23,19a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2V8a2,2 0 0,1 2,-2h4l2,-3h6l2,3h4a2,2 0 0,1 2,2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,9A4,4 0 1,0 12,17A4,4 0 0,0 12,9z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
        }
    }

    // Chevron: right arrow >
    val Chevron: ImageVector by lazy {
        buildIcon("Chevron") {
            addPath(pathData = "M9,18L15,12L9,6",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Grid: 4 equal squares
    val Grid: ImageVector by lazy {
        buildIcon("Grid") {
            listOf(Pair(3f,3f), Pair(14f,3f), Pair(14f,14f), Pair(3f,14f)).forEach { (x,y) ->
                addPath(pathData = "M${x},${y}h7v7h-7Z",
                    stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            }
        }
    }

    // List: 3 lines + 3 dots
    val ListIcon: ImageVector by lazy {
        buildIcon("List") {
            listOf(6f, 12f, 18f).forEach { y ->
                addPath(pathData = "M8,${y}L21,${y}", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
                addPath(pathData = "M3,${y}L3.01,${y}", stroke = SolidColor(Color.Black), strokeLineWidth = 3f, strokeLineCap = StrokeCap.Round)
            }
        }
    }

    // Eye: outline + iris
    val Eye: ImageVector by lazy {
        buildIcon("Eye") {
            addPath(pathData = "M1,12s4,-8 11,-8s11,8 11,8s-4,8 -11,8s-11,-8 -11,-8z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M12,9A3,3 0 1,0 12,15A3,3 0 0,0 12,9z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
        }
    }

    // Bookmark outline
    val Bookmark: ImageVector by lazy {
        buildIcon("Bookmark") {
            addPath(pathData = "M19,21L12,16L5,21V5a2,2 0 0,1 2,-2h10a2,2 0 0,1 2,2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }
    // Bookmark filled
    val BookmarkFilled: ImageVector by lazy {
        buildIcon("BookmarkFilled") {
            addPath(pathData = "M19,21L12,16L5,21V5a2,2 0 0,1 2,-2h10a2,2 0 0,1 2,2z",
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // FileText: page with fold + 3 lines
    val FileText: ImageVector by lazy {
        buildIcon("FileText") {
            addPath(pathData = "M14,2H6a2,2 0 0,0 -2,2v16a2,2 0 0,0 2,2h12a2,2 0 0,0 2,-2V8z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M14,2L14,8L20,8", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M16,13L8,13", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M16,17L8,17", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M10,9L9,9L8,9", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Shield: M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z
    val Shield: ImageVector by lazy {
        buildIcon("Shield") {
            addPath(pathData = "M12,22s8,-4 8,-10V5l-8,-3l-8,3v7c0,6 8,10 8,10z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Lightbulb: bulb + filament hint
    val Lightbulb: ImageVector by lazy {
        buildIcon("Lightbulb") {
            addPath(pathData = "M9,18h6", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M10,22h4", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M15.09,14c0.18,-0.98 0.65,-1.74 1.41,-2.5A6,6 0 1,0 6,8c0,1 0.25,2 0.75,2.8c0.76,0.76 1.23,1.52 1.41,2.5",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Package (3D box)
    val PackageIcon: ImageVector by lazy {
        buildIcon("Package") {
            addPath(pathData = "M16.5,9.4L7.5,4.21", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M21,16V8a2,2 0 0,0 -1,-1.73l-7,-4a2,2 0 0,0 -2,0l-7,4A2,2 0 0,0 3,8v8a2,2 0 0,0 1,1.73l7,4a2,2 0 0,0 2,0l7,-4A2,2 0 0,0 21,16z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M3.27,6.96L12,12.01L20.73,6.96", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            addPath(pathData = "M12,22.08L12,12", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Sun: circle + 8 rays
    val Sun: ImageVector by lazy {
        buildIcon("Sun") {
            addPath(pathData = "M12,7A5,5 0 1,0 12,17A5,5 0 0,0 12,7z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f)
            listOf("M12,1L12,3", "M12,21L12,23", "M4.22,4.22L5.64,5.64",
                "M18.36,18.36L19.78,19.78", "M1,12L3,12", "M21,12L23,12",
                "M4.22,19.78L5.64,18.36", "M18.36,5.64L19.78,4.22").forEach { d ->
                addPath(pathData = d, stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
            }
        }
    }

    // Moon: crescent — M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z
    val Moon: ImageVector by lazy {
        buildIcon("Moon") {
            addPath(pathData = "M21,12.79A9,9 0 1,1 11.21,3A7,7 0 0,0 21,12.79z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        }
    }

    // Mail: envelope + flap
    val Mail: ImageVector by lazy {
        buildIcon("Mail") {
            addPath(pathData = "M4,4h16c1.1,0 2,0.9 2,2v12c0,1.1 -0.9,2 -2,2H4c-1.1,0 -2,-0.9 -2,-2V6c0,-1.1 0.9,-2 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M22,6L12,13L2,6",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // Lock: shackle + body rect
    val Lock: ImageVector by lazy {
        buildIcon("Lock") {
            addPath(pathData = "M3,11h18a2,2 0 0,1 2,2v7a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2v-7a2,2 0 0,1 2,-2z",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M7,11V7a5,5 0 0,1 10,0v4",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }

    // LogOut: door frame + arrow
    val LogOut: ImageVector by lazy {
        buildIcon("LogOut") {
            addPath(pathData = "M9,21H5a2,2 0 0,1 -2,-2V5a2,2 0 0,1 2,-2h4",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M16,17L21,12L16,7",
                stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
            addPath(pathData = "M21,12L9,12", stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round)
        }
    }
}
```

- [ ] **M01-T07-C** Write a `@Preview` composable `RentoIconsPreview` displaying all 46 icons in a `LazyVerticalGrid(columns = Fixed(6))` at 24dp each to visually verify rendering. Confirm no icon is blank.

- [ ] **M01-T07-D** Verify: run `./gradlew assembleDebug` — zero icon-related errors.

---

## Task M01-T08 — Animation Tokens (`Animations.kt`)

**File:** `presentation/shared/animations/Animations.kt`

> ⚠️ Animations must feel natural and buttery smooth. Spring physics are preferred over tween for interactive elements. Durations are specified per animation — do not change them.

### Sub-tasks
- [ ] **M01-T08-A** Define `rememberFloatYOffset` — infinite float for welcome screen logo:

```kotlin
@Composable
fun rememberFloatYOffset(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "floatY")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = -9f,  // -9dp — multiply by density for px
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatYValue",
    )
}
// Usage: Modifier.offset { IntOffset(0, (rememberFloatYOffset().value * density).roundToInt()) }
```

- [ ] **M01-T08-B** Define `rememberPulseAlpha` — notification dot pulse:

```kotlin
@Composable
fun rememberPulseAlpha(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.42f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )
}
```

- [ ] **M01-T08-C** Define `rememberGlowElevation` — FAB animated glow:

```kotlin
@Composable
fun rememberGlowElevation(): State<Dp> {
    val infiniteTransition = rememberInfiniteTransition(label = "gGlow")
    return infiniteTransition.animateValue(
        initialValue  = 8.dp,
        targetValue   = 20.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowElevation",
    )
}
```

- [ ] **M01-T08-D** Define `BounceEffect` — icon celebration after positive action:

```kotlin
@Composable
fun BounceEffect(
    trigger: Boolean,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue  = if (trigger) 1.0f else 0.82f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium,
        ),
        label = "bounceScale",
    )
    val rotation by animateFloatAsState(
        targetValue  = if (trigger) 0f else -15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bounceRotation",
    )
    content(
        Modifier.graphicsLayer {
            scaleX    = scale
            scaleY    = scale
            rotationZ = rotation
        }
    )
}
```

- [ ] **M01-T08-E** Define `rememberShimmerBrush` — skeleton loader:

```kotlin
@Composable
fun rememberShimmerBrush(colors: RentoColors): Brush {
    val shimmerColors = listOf(colors.bg3, colors.bg2, colors.bg3)
    val transition    = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start  = Offset(translateAnim - 500f, 0f),
        end    = Offset(translateAnim, 0f),
    )
}
```

- [ ] **M01-T08-F** Define `NavTransitions` — screen-level enter/exit transitions for Navigation Compose:

```kotlin
object NavTransitions {
    // Standard push (navigate forward)
    val pushEnter: EnterTransition = fadeIn(tween(200)) + slideInHorizontally(
        animationSpec  = tween(220, easing = FastOutSlowInEasing),
        initialOffsetX = { it },
    )
    val pushExit: ExitTransition = fadeOut(tween(200)) + slideOutHorizontally(
        animationSpec  = tween(220, easing = FastOutSlowInEasing),
        targetOffsetX  = { -it / 3 },
    )
    // Back pop (navigate back)
    val popEnter: EnterTransition = fadeIn(tween(200)) + slideInHorizontally(
        animationSpec  = tween(220, easing = FastOutSlowInEasing),
        initialOffsetX = { -it / 3 },
    )
    val popExit: ExitTransition = fadeOut(tween(200)) + slideOutHorizontally(
        animationSpec  = tween(220, easing = FastOutSlowInEasing),
        targetOffsetX  = { it },
    )
    // Form step — forward
    val formStepEnter: EnterTransition = fadeIn(tween(200)) + slideInHorizontally(
        animationSpec  = tween(220, easing = FastOutSlowInEasing),
        initialOffsetX = { it },
    )
    // Form step — back
    val formStepBack: EnterTransition = fadeIn(tween(200)) + slideInHorizontally(
        animationSpec  = tween(220, easing = FastOutSlowInEasing),
        initialOffsetX = { -it },
    )
    // Sheet / overlay appear
    val overlayEnter: EnterTransition = fadeIn(tween(200)) + slideInVertically(
        animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        initialOffsetY = { it / 3 },
    )
    val overlayExit: ExitTransition = fadeOut(tween(180)) + slideOutVertically(
        animationSpec  = tween(180),
        targetOffsetY  = { it / 3 },
    )
    // Dialog appear (scale up from centre)
    val dialogEnter: EnterTransition = fadeIn(tween(200)) + scaleIn(
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        initialScale  = 0.92f,
    )
    val dialogExit: ExitTransition = fadeOut(tween(160)) + scaleOut(
        animationSpec = tween(160),
        targetScale   = 0.92f,
    )
}
```

- [ ] **M01-T08-G** Define `fadeInSlideUp` — used for staggered list item entrance (`.su` CSS class equivalent):

```kotlin
@Composable
fun fadeInSlideUpModifier(
    visible: Boolean,
    delayMillis: Int = 0,
): Modifier {
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 360, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label         = "fadeAlpha",
    )
    val offsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 26.dp,
        animationSpec = tween(durationMillis = 420, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label         = "slideOffset",
    )
    return Modifier
        .graphicsLayer { this.alpha = alpha }
        .offset(y = offsetY)
}
```

---

## Task M01-T09 — Component: `RentoStatusBar`

**File:** `presentation/shared/components/RentoStatusBar.kt`

Fake iOS-style status bar at the top of every screen.

### Exact Specification
```
Layout: Row, fillMaxWidth, padding(top=13.dp, start=24.dp, end=24.dp)
Arrangement: SpaceBetween
Alignment: CenterVertically

LEFT — Time text:
  "9:41"
  Style: 15sp, Bold, PlusJakartaSans
  Color: RentoColors.t0

RIGHT — Row(gap=6.dp, CenterVertically):
  1. Signal bars (Canvas):
     4 bars, each 3dp wide
     Heights (bottom-aligned): bar1=3dp, bar2=5dp, bar3=7dp, bar4=10dp (max)
     X positions: 0dp, 4.5dp, 9dp, 13.5dp
     Opacity sequence: 32%, 55%, 78%, 100% (left→right, dimmest→brightest)
     Fill color: RentoColors.t0 with respective alpha
     Corner radius: 1dp on each bar
     Canvas size: 17dp × 12dp

  2. Battery (Canvas):
     Canvas size: 25dp × 12dp
     Outer rect: x=0.5dp, y=0.5dp, width=22dp, height=11dp, corner=3.5dp
       Stroke: 1dp, RentoColors.t0 at 28% alpha
     Fill rect: x=2dp, y=2dp, width=17dp, height=8dp, corner=2dp
       Fill: RentoColors.t0 (100% alpha)
     Right nub: x=23dp, y=4dp, width=2dp, height=4dp (right stub beyond outer rect)
       Fill: RentoColors.t0 at 38% alpha
```

### Sub-tasks
- [ ] **M01-T09-A** Implement `RentoStatusBar` as a `Row` composable. Draw signal bars and battery via `Canvas`.
- [ ] **M01-T09-B** Add `@Preview` annotations for both dark and light themes using `@PreviewParameter`.
- [ ] **M01-T09-C** Strings in `strings.xml`:
  - `status_bar_time` = "9:41" (placeholder — real time shown in actual app via `remember { Calendar.getInstance() }`)
  - `status_bar_signal_cd` = "Signal strength indicator"
  - `status_bar_battery_cd` = "Battery status indicator"

---

## Task M01-T10 — Component: `PrimaryButton`

**File:** `presentation/shared/components/Buttons.kt`

### Exact Specification
```
Shape:       RoundedCornerShape(100dp) — full pill
Background:  gradientPrimary(colors) brush — NOT a solid Color
Text:        Color.White, 15sp, Bold, PlusJakartaSans
Width:       fillMaxWidth by default (overridable via Modifier)
Shadow:      shadowElevation 4dp, tinted with DarkPriR approximation
Pressed:     scale(0.97) + alpha(0.9), 200ms tween
Disabled:    40% opacity (graphicsLayer alpha = 0.4), gradient replaced by DarkBg3→DarkBg4
Loading:     CircularProgressIndicator(18dp, Color.White strokeWidth=2dp) replaces text
             Button stays at same size during loading. Back-press blocked.
Leading icon: optional — renders before text with 8dp gap
```

### Signature
```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
)
```

### Sub-tasks
- [ ] **M01-T10-A** Implement via a `Box` wrapping a `Button` with `containerColor = Color.Transparent`. Apply `Modifier.background(brush = gradientPrimary(colors), shape = RentoShapes.pill)` to the box.
- [ ] **M01-T10-B** Collect `interactionSource.collectIsPressedAsState()`. Animate scale and alpha with `animateFloatAsState(tween(200))`.
- [ ] **M01-T10-C** When `isLoading = true`: show `CircularProgressIndicator`, set `enabled = false`, block clicks.
- [ ] **M01-T10-D** When `enabled = false`: `graphicsLayer { alpha = 0.4f }`.
- [ ] **M01-T10-E** Leading icon: if non-null, render `Icon(leadingIcon, ...)` at 18dp before text with `Spacer(8.dp)`.
- [ ] **M01-T10-F** Strings: `common_loading_cd` = "Loading" (content description on spinner).
- [ ] **M01-T10-G** Unit tests (`ButtonsTest.kt`):
  - `primaryButton_rendersText` — text is shown
  - `primaryButton_showsSpinnerWhenLoading` — loading=true shows `CircularProgressIndicator`
  - `primaryButton_doesNotFireWhenLoading` — click callback not called during loading
  - `primaryButton_doesNotFireWhenDisabled` — click callback not called when `enabled=false`
  - `primaryButton_rendersLeadingIcon` — icon visible when leadingIcon provided
- [ ] **M01-T10-H** `@Preview` for: default, loading, disabled, with leading icon, light theme.

---

## Task M01-T11 — Component: `GhostButton`

**File:** `presentation/shared/components/Buttons.kt`

### Exact Specification
```
Shape:      RoundedCornerShape(100dp)
Background: Color.Transparent
Border:     1.5dp, RentoColors.border2
Text:       RentoColors.t1, 13–15sp (fontSize param, default 15sp), Medium weight
Width:      fillMaxWidth by default
Pressed:    background tint RentoColors.bg3, 200ms tween
Disabled:   40% opacity
```

### Signature
```kotlin
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 15.sp,
    leadingIcon: ImageVector? = null,
)
```

### Sub-tasks
- [ ] **M01-T11-A** Implement `GhostButton` using `OutlinedButton` or custom `Box` with border.
- [ ] **M01-T11-B** `@Preview` dark + light.
- [ ] **M01-T11-C** Unit tests: renders text, click fires, disabled blocks click.

---

## Task M01-T12 — Component: `OutlinePrimaryButton`

**File:** `presentation/shared/components/Buttons.kt`

### Exact Specification
```
Shape:      RoundedCornerShape(100dp)
Background: Color.Transparent
Border:     1.5dp, RentoColors.primary
Text:       RentoColors.primary, 13sp, Medium
Padding:    10dp vertical, 20dp horizontal (default)
Pressed:    background fills to RentoColors.primaryTint, 200ms
```

### Signature
```kotlin
@Composable
fun OutlinePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
)
```

### Sub-tasks
- [ ] **M01-T12-A** Implement `OutlinePrimaryButton`.
- [ ] **M01-T12-B** `@Preview` dark + light.
- [ ] **M01-T12-C** Unit tests: renders text, click fires, disabled blocks click.

---

## Task M01-T13 — Component: `DestructiveButton`

**File:** `presentation/shared/components/Buttons.kt`

> ⚠️ This button is **always** `RentoColors.red` fill + `Color.White` text. Used exclusively for Delete / Block / Reject / Suspend actions inside glass dialogs. Never change the colour. Never use for non-destructive actions.

### Exact Specification
```
Shape:      RoundedCornerShape(100dp)
Background: RentoColors.red (flat, no gradient)
Text:       Color.White, 15sp, Bold
Elevation:  0dp (shadow = none)
Pressed:    scale(0.97) + alpha(0.9) — identical to PrimaryButton press
Loading:    CircularProgressIndicator(18dp, Color.White) replaces text — identical to PrimaryButton
```

### Signature
```kotlin
@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
)
```

### Sub-tasks
- [ ] **M01-T13-A** Implement `DestructiveButton`.
- [ ] **M01-T13-B** `@Preview` showing default + loading states.
- [ ] **M01-T13-C** Unit tests: renders text, spinner during loading, click blocked when loading, click blocked when disabled.

---

## Task M01-T14 — Component: `RentoChip`

**File:** `presentation/shared/components/RentoChip.kt`

### Exact Specification
```
Shape:   RoundedCornerShape(100dp)
Padding: 7dp vertical, 14dp horizontal
Font:    Plus Jakarta Sans, 12.5sp, Medium (unselected) / Bold (selected)
softWrap: false — chips never wrap to two lines

UNSELECTED:
  Background: RentoColors.bg3
  Border:     1.5dp, RentoColors.border2
  Text:       RentoColors.t1
  Icon (opt): 14dp, RentoColors.t2, gap 6dp before text

SELECTED:
  Background: RentoColors.primaryTint
  Border:     1.5dp, RentoColors.primary
  Text:       RentoColors.primary, Bold
  Icon (opt): 14dp, RentoColors.primary

Transition: animateColorAsState(220ms tween) for bg, border colour, text colour
Press: scale(0.97) via interactionSource
```

### Signature
```kotlin
@Composable
fun RentoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
)
```

### Sub-tasks
- [ ] **M01-T14-A** Implement `RentoChip` with animated colour transitions and press scale.
- [ ] **M01-T14-B** `@Preview`: unselected, selected, with icon unselected, with icon selected.
- [ ] **M01-T14-C** Unit tests:
  - `rentoChip_showsCorrectColourWhenSelected` — background, border, text colours match spec
  - `rentoChip_showsCorrectColourWhenUnselected`
  - `rentoChip_onClickFires`
  - `rentoChip_doesNotFireWhenDisabled`

---

## Task M01-T15 — Component: `TabPill`

**File:** `presentation/shared/components/TabPill.kt`

Used for "Looking / Hosting" toggle on Home screen.

### Exact Specification
```
Container Row:
  Background: RentoColors.bg3
  Shape: RoundedCornerShape(100dp)
  Padding: 4dp all sides
  Border: 1.5dp, RentoColors.border

Inactive tab:
  Padding: 8dp V, 20dp H
  Corner: 100dp
  Text: 13sp, Medium, RentoColors.t2
  Background: Transparent

Active tab:
  Background: RentoColors.primary
  Text: 13sp, Bold, Color.White
  Corner: 100dp
  Shadow: elevation 2dp with DarkPriR tint

Transition: animateColorAsState for text and background, 260ms tween
Only one tab active at a time. Minimum 2 tabs, maximum 4 tabs.
```

### Signature
```kotlin
@Composable
fun TabPill(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T15-A** Implement `TabPill`.
- [ ] **M01-T15-B** `@Preview` with ["Looking", "Hosting"] example.
- [ ] **M01-T15-C** Unit tests: correct tab selected by index, `onTabSelected` fires with correct index.

---

## Task M01-T16 — Component: `Badge`

**File:** `presentation/shared/components/Badge.kt`

All variants share: `3dp V / 9dp H padding`, `7dp corner`, `11sp Bold` text.

### Exact Specification
```
BadgeVariant.PRIMARY:
  Background: RentoColors.primaryTint
  Text:       RentoColors.primary
  Border:     1dp, RentoColors.primaryRing

BadgeVariant.RED:
  Background: RentoColors.redTint
  Text:       RentoColors.red
  Border:     none

BadgeVariant.ACCENT:
  Background: RentoColors.accentTint
  Text:       RentoColors.accent
  Border:     none

BadgeVariant.BLUE:
  Background: RentoColors.blueTint
  Text:       RentoColors.blue
  Border:     none

BadgeVariant.NEUTRAL:
  Background: RentoColors.bg3
  Text:       RentoColors.t2
  Border:     1dp, RentoColors.border2
```

### Signature
```kotlin
enum class BadgeVariant { PRIMARY, RED, ACCENT, BLUE, NEUTRAL }

@Composable
fun RentoBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.PRIMARY,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T16-A** Implement `RentoBadge` with all 5 variants.
- [ ] **M01-T16-B** `@Preview` showing all 5 side-by-side in dark and light.
- [ ] **M01-T16-C** Unit tests: each variant resolves correct colours from the composition local.

---

## Task M01-T17 — Component: `SectionLabel`

**File:** `presentation/shared/components/SectionLabel.kt`

### Exact Specification
```
Text: 11sp, Bold, RentoColors.t2, letterSpacing 0.06em
UPPERCASE applied internally (caller passes original case)
No built-in margin — caller controls via Modifier or Spacer
```

### Signature
```kotlin
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T17-A** Implement `SectionLabel` with `.uppercase(Locale.ROOT)` applied inside.
- [ ] **M01-T17-B** `@Preview` dark + light.

---

## Task M01-T18 — Component: `UnderlineInputField`

**File:** `presentation/shared/components/InputFields.kt`

### Exact Specification
```
Layout: fillMaxWidth, transparent background
Bottom line only — drawn via Modifier.drawBehind (NOT OutlinedTextField)
  Idle:    2dp stroke, RentoColors.inputUnderlineIdle
  Focused: 2dp stroke, RentoColors.primary
  Error:   2dp stroke, RentoColors.red
  Transition: animateColorAsState, 220ms

Text: 15sp, Plus Jakarta Sans, RentoColors.t0
Placeholder: RentoColors.t3 (same font, same size)
Cursor colour: RentoColors.primary
Padding on text: 10dp vertical
Label: optional — shows as SectionLabel above field (caller can also omit and use external SectionLabel)
Leading icon: optional — 20dp, RentoColors.t2, 12dp gap
Trailing icon: optional composable slot
Error message: if isError=true and errorMessage!=null — 12sp, RentoColors.red, shown below field with 4dp top padding

Keyboard: IME action configurable (default = ImeAction.Next)
KeyboardType: configurable
MaxLines: configurable (default = 1)
MaxLength: enforced via onValueChange filter (no VisualTransformation needed)
```

### Signature
```kotlin
@Composable
fun UnderlineInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
)
```

### Sub-tasks
- [ ] **M01-T18-A** Implement using `BasicTextField` — NOT `OutlinedTextField` or `TextField`.
- [ ] **M01-T18-B** Draw bottom line via `Modifier.drawBehind { drawLine(...) }`.
- [ ] **M01-T18-C** Animate underline colour via `interactionSource.collectIsFocusedAsState()`.
- [ ] **M01-T18-D** Enforce `maxLength` in `onValueChange` lambda: `if (it.length <= maxLength) onValueChange(it)`.
- [ ] **M01-T18-E** `@Preview`: default, focused (via `interactionSource`), error state, disabled.
- [ ] **M01-T18-F** Unit tests:
  - `underlineInput_rejectsInputBeyondMaxLength`
  - `underlineInput_showsErrorMessage`
  - `underlineInput_onValueChangeFiresCorrectly`
  - `underlineInput_doesNotAcceptInputWhenDisabled`

---

## Task M01-T19 — Component: `BoxedInputField`

**File:** `presentation/shared/components/InputFields.kt`

### Exact Specification
```
Container Row:
  Background: RentoColors.bg2
  Border:     1.5dp, RentoColors.border
  Corner:     16dp
  Padding:    12dp V, 16dp H
  CenterVertically alignment

Focus state: border animates to RentoColors.primary (animateColorAsState, 220ms)
Error state: border set to RentoColors.red

Leading icon: optional, 22dp, RentoColors.t2, 12dp gap right
Inner input: BasicTextField, 15sp, Plus Jakarta Sans, transparent background, no bottom line
Trailing:    optional composable slot (e.g. Eye toggle, Clear button)
Password:    isPassword=true auto-wires Eye icon toggle for `visualTransformation`
```

### Signature
```kotlin
@Composable
fun BoxedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    isError: Boolean = false,
)
```

### Sub-tasks
- [ ] **M01-T19-A** Implement `BoxedInputField`.
- [ ] **M01-T19-B** When `isPassword=true`: manage `passwordVisible: Boolean` state internally; auto-add Eye/EyeOff trailing icon using `RentoIcons.Eye`; apply `PasswordVisualTransformation()` when hidden.
- [ ] **M01-T19-C** Border focus animation: use `interactionSource.collectIsFocusedAsState()` + `animateColorAsState`.
- [ ] **M01-T19-D** `@Preview`: email field, password obscured, password visible.
- [ ] **M01-T19-E** Unit tests: password visibility toggle, maxLength enforced, click fires.

---

## Task M01-T20 — Component: `ToggleSwitch`

**File:** `presentation/shared/components/ToggleSwitch.kt`

### Exact Specification
```
Container: Box, 46dp × 26dp, RoundedCornerShape(100dp)
  ON:  RentoColors.primary fill
  OFF: RentoColors.bg4 fill
  Transition: animateColorAsState, 200ms tween
  Shadow (ON only): 0dp offset, 10dp blur, RentoColors.primaryRing

Thumb: 20dp × 20dp circle, Color.White fill
  ON  offset: (46 - 20 - 3) = 23dp from left edge
  OFF offset: 3dp from left edge
  Transition: animateDpAsState(spring(DampingRatioMediumBouncy)) — thumb bounces when toggled

Clickable: entire container via `Modifier.clickable`
Disabled: 50% opacity on entire component
```

### Signature
```kotlin
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
```

### Sub-tasks
- [ ] **M01-T20-A** Implement custom `ToggleSwitch` without using Material `Switch`.
- [ ] **M01-T20-B** Thumb spring animation.
- [ ] **M01-T20-C** `@Preview` showing ON/OFF in dark + light.
- [ ] **M01-T20-D** Unit tests: click toggles, disabled blocks click.

---

## Task M01-T21 — Component: `ProgressStepBar`

**File:** `presentation/shared/components/ProgressStepBar.kt`

Used at the top of listing form (11 steps) and request form (6 steps).

### Exact Specification
```
Row of dots:
  Each dot: rounded rectangle, height always 5dp, corner 100dp
  ACTIVE (current step):  22dp wide, RentoColors.primary fill
  COMPLETED (past step):  5dp wide,  RentoColors.primary2 at 55% alpha (Color.copy(alpha=0.55f))
  FUTURE (upcoming step): 5dp wide,  RentoColors.bg4 fill
  Gap between dots: 5dp

Width transition: animateDpAsState(
    targetValue = if (active) 22.dp else 5.dp,
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)

Label below dots (7dp top gap):
  "Step {currentStep + 1} of {totalSteps}"
  Font: 11sp, SemiBold, RentoColors.t2
```

### Signature
```kotlin
@Composable
fun ProgressStepBar(
    currentStep: Int,   // 0-indexed
    totalSteps: Int,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T21-A** Implement `ProgressStepBar` with animated dot widths. Use `key(index)` for each dot.
- [ ] **M01-T21-B** `@Preview`: step 0 of 11, step 5 of 11, step 10 of 11.
- [ ] **M01-T21-C** Strings: `form_step_label` = "Step %1$d of %2$d"
- [ ] **M01-T21-D** Unit tests:
  - `progressStepBar_activeStepHasCorrectWidth`
  - `progressStepBar_completedStepsHaveCorrectAlpha`
  - `progressStepBar_labelTextCorrect`

---

## Task M01-T22 — Component: `ProgressBar` (linear)

**File:** `presentation/shared/components/ProgressBar.kt`

### Exact Specification
```
Container: Box, fillMaxWidth, 4dp height, RoundedCornerShape(100dp)
  Background: RentoColors.bg4

Fill: inner Box
  Width: animateFloatAsState(
      targetValue = progress,
      animationSpec = spring(
          dampingRatio = Spring.DampingRatioNoBouncy,
          stiffness    = Spring.StiffnessLow,
      )
  ) × container width via BoxWithConstraints
  Background: gradientPrimary brush
  Corner: 100dp
```

### Signature
```kotlin
@Composable
fun RentoProgressBar(
    progress: Float,   // 0f to 1f, clamped internally
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T22-A** Implement `RentoProgressBar` with `BoxWithConstraints` to get actual width for fill calculation.
- [ ] **M01-T22-B** Clamp `progress` to `0f..1f` via `progress.coerceIn(0f, 1f)`.
- [ ] **M01-T22-C** `@Preview` at 0%, 50%, 100%.
- [ ] **M01-T22-D** Unit tests: clamps at 0, clamps at 1, 50% gives half width.

---

## Task M01-T23 — Component: `PropertyCard` (Full + Compact)

**File:** `presentation/shared/components/PropertyCard.kt`

### Data Model
```kotlin
@Immutable
data class PropertyCardData(
    val id: String,
    val title: String,
    val area: String,
    val city: String,
    val price: Int,
    val beds: Int,
    val baths: Int,
    val propertyType: String,
    val intent: String,
    val furnished: String,
    val imageUrls: List<String>,
    val cardGradientIndex: Int,
    val emoji: String,
    val isSaved: Boolean,
)
```

### Full Card Exact Specification
```
Container:
  Background: RentoColors.bg2
  Border:     1.5dp, RentoColors.border
  Shape:      RoundedCornerShape(24dp)
  Clip:       true
  Pressed:    scale(0.985), spring(DampingRatioMediumBouncy)
  onClick:    lambda

IMAGE AREA — 190dp height:
  Background: cardGradientForIndex(cardGradientIndex) brush
  Emoji watermark: Box centred, 72sp, alpha=0.10f, no interaction
  Overlay: gradientImageOverlay(colors) brush, fillMaxSize

  TOP-LEFT — Type badge (absolute top=14dp, start=14dp):
    Background: Color(0x8C04100C) dark / Color(0x8CFFFFFF) light
    Border:     1dp Color(0x332ECC8A) dark / 1dp Color(0x330C7A50) light
    Corner:     100dp
    Padding:    3dp V, 11dp H
    Text:       11sp, SemiBold, Color.White dark / Color(0xFF06201A) light

  TOP-RIGHT — Heart button (absolute top=14dp, end=14dp):
    Size: 34dp circle
    Background: Color(0x8C04100C) dark / Color(0x8CFFFFFF) light
    Icon: RentoIcons.Heart (outline) or RentoIcons.HeartFilled if isSaved
    Icon colour: Color.White if not saved / RentoColors.primary if saved
    onClick: onSaveToggle lambda
    Save animation: BounceEffect triggered on save

  BOTTOM-LEFT — Location row (absolute bottom=14dp, start=14dp):
    Row(6dp gap, CenterVertically):
      Pin icon: 14dp, RentoColors.primary2, alpha=0.9f
      "{area}, {city}": 12sp, Color(0xD1E4F4EC)

CONTENT AREA — padding(top=14dp, horizontal=16dp, bottom=16dp):
  Title:
    15sp, SemiBold, RentoColors.t0, lineHeight=1.3, maxLines=2, overflow=Ellipsis
    Bottom margin: 10dp

  Metrics row (SpaceBetween, CenterVertically, bottom margin=10dp):
    Left Row(14dp gap):
      Bed:  RentoIcons.Bed(14dp, RentoColors.t2) + "{beds} bed" (12.5sp, RentoColors.t2)
      Bath: RentoIcons.Bath(14dp, RentoColors.t2) + "{baths} bath" (12.5sp, RentoColors.t2)
    Right:
      "{price}" — 19sp, Bold, RentoColors.primary (formatted with commas: NumberFormat)
      " PKR/mo" — 11sp, RentoColors.t2

  Badges row (7dp gap, wrap):
    RentoBadge(intent, BadgeVariant.PRIMARY)
    RentoBadge(furnished, BadgeVariant.NEUTRAL)
```

### Compact Card Exact Specification
```
Container: identical to Full card
IMAGE AREA: 108dp height — same content, emoji at 50sp (not 72sp)
CONTENT AREA — padding(top=10dp, horizontal=13dp, bottom=14dp):
  Title:  12sp, SemiBold, lineHeight=1.35, maxLines=2
  Price:  "{price/1000}k" — 14sp, Bold, RentoColors.primary
          " PKR/mo" — 10sp, RentoColors.t2
  (No badges row, no metrics row in compact — price only)
```

### Signature
```kotlin
@Composable
fun PropertyCard(
    data: PropertyCardData,
    onClick: () -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
)
```

### Sub-tasks
- [ ] **M01-T23-A** Implement `PropertyCard` with `compact` param controlling variant.
- [ ] **M01-T23-B** Image: use Coil `AsyncImage`. On error or empty `imageUrls`: show gradient + emoji only.
- [ ] **M01-T23-C** Use `BounceEffect` from `Animations.kt` on the heart icon when `isSaved` changes.
- [ ] **M01-T23-D** Format price with `NumberFormat.getNumberInstance(Locale("en", "PK"))`.
- [ ] **M01-T23-E** Absolute-positioned elements (type badge, heart, location) use `Box` with `Alignment` modifiers via `Modifier.align(Alignment.TopStart)` etc. inside the image `Box`.
- [ ] **M01-T23-F** `@Preview`: full card dark, full card light, compact card dark, compact card light.
- [ ] **M01-T23-G** Content description: `rento_property_card_cd` = "%1$s in %2$s, %3$s PKR per month" (title, area, price).

---

## Task M01-T24 — Component: `TenantRequestCard`

**File:** `presentation/shared/components/TenantRequestCard.kt`

### Data Model
```kotlin
@Immutable
data class TenantRequestCardData(
    val id: String,
    val requesterName: String,
    val intent: String,
    val propertyType: String,
    val minBeds: Int,
    val minBaths: Int,
    val preferredAreas: List<String>,
    val radiusKm: Int,
    val budgetMax: Int,
    val moveInDate: String,
)
```

### Exact Specification
```
Container:
  Background: RentoColors.bg2
  Border:     1.5dp, RentoColors.border
  Shape:      RoundedCornerShape(24dp)
  Padding:    18dp all
  Pressed:    scale(0.985), spring
  onClick:    lambda

TOP ROW (SpaceBetween, bottom=14dp):
  Left Row(12dp gap, CenterVertically):
    Avatar:
      44dp circle
      Background: RentoColors.primaryTint
      Border: 1.5dp, RentoColors.primaryRing
      RentoIcons.User, 20dp, RentoColors.primary
    Column:
      Name: 15sp, Bold, RentoColors.t0
      "Needs by {moveInDate}": 12sp, RentoColors.t2, top=2dp
  Right: RentoBadge(intent, BadgeVariant.BLUE)

MIDDLE BOX (DarkBg3 / LightBg3 fill, 12dp V / 14dp H padding, 14dp corner, bottom=14dp):
  SectionLabel("Looking For", bottom=4dp)
  "{propertyType}": 14sp, SemiBold, RentoColors.t0, top=4dp
  "{minBeds} Bed • {minBaths} Bath": 12sp, RentoColors.t1, top=4dp

BOTTOM ROW (SpaceBetween, CenterVertically):
  Left Row(6dp gap, CenterVertically):
    RentoIcons.Pin, 14dp, RentoColors.primary
    "{areas.take(2).joinToString()} (+{radius}km)": 12sp, RentoColors.t1
  Right Column(end alignment):
    "Up to": 11sp, RentoColors.t2
    Budget: "{budgetMax/1000}k": 18sp, Bold, RentoColors.primary
            " PKR/mo": 11sp, RentoColors.t2
```

### Signature
```kotlin
@Composable
fun TenantRequestCard(
    data: TenantRequestCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T24-A** Implement `TenantRequestCard`.
- [ ] **M01-T24-B** `@Preview` dark + light.
- [ ] **M01-T24-C** Content description: `rento_request_card_cd` = "Request by %1$s, budget up to %2$s PKR".

---

## Task M01-T25 — Component: `BottomNavBar`

**File:** `presentation/shared/components/BottomNavBar.kt`

### Exact Specification
```
Container: Box fillMaxWidth
  Height: 86dp
  Background:
    API ≥ 31: RentoColors.navBg + Modifier.blur(24.dp) on background layer only
    API 30:   RentoColors.navBgFallback (pre-increased opacity, no blur)
  Border top: 1dp, RentoColors.border
  Z-index: Float.MAX_VALUE (always on top)

Inner Row: fillMaxWidth, SpaceAround, align to bottom
  Padding: horizontal=8dp, bottom=18dp

NAV ITEM (4 items: Home, Map, Chat, Profile):
  Column(4dp gap, CenterHorizontally), padding(8dp V, 14dp H), corner=18dp

  INACTIVE:
    Background: transparent
    Icon colour: RentoColors.t2
    Label: 10sp, Medium, RentoColors.t2

  ACTIVE:
    Background: RentoColors.primaryTint
    Icon colour: RentoColors.primary
    Label: 10sp, Medium, RentoColors.primary

  Transition: animateColorAsState(220ms)
  Icon size: 22dp

CENTRE FAB (position 3 of 5, between Map and Chat):
  Size: 54dp circle
  Background: gradientPrimary brush
  Icon: RentoIcons.Plus, 22dp, Color.White
  Animated elevation: rememberGlowElevation() — creates glow pulse
  onClick: onAddTap lambda
```

### Data
```kotlin
data class NavItem(
    val id: String,
    val labelRes: Int,      // string resource ID
    val icon: ImageVector,
)
```

### Signature
```kotlin
@Composable
fun BottomNavBar(
    items: List<NavItem>,      // exactly 4 items: Home, Map, Chat, Profile
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    onAddTap: () -> Unit,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T25-A** Implement `BottomNavBar`. FAB is injected at index 2 (between items[1] and items[2]).
- [ ] **M01-T25-B** API-level check for backdrop blur:
  ```kotlin
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      Modifier.blur(24.dp)
  } else {
      Modifier  // navBgFallback already has higher opacity
  }
  ```
- [ ] **M01-T25-C** `@Preview` dark + light.
- [ ] **M01-T25-D** Strings:
  - `nav_home` = "Home"
  - `nav_map` = "Map"
  - `nav_chat` = "Chat"
  - `nav_profile` = "Profile"
  - `nav_add_cd` = "Add listing or request"

---

## Task M01-T26 — Component: `AddOverlaySheet`

**File:** `presentation/shared/components/AddOverlaySheet.kt`

### Exact Specification
```
Full-screen overlay:
  Background: RentoColors.overlay (72% opacity)
  Blur: 6dp backdrop blur (API ≥ 31 only — fallback: opacity sufficient at 72%)
  Tap outside sheet: calls onDismiss
  Entry animation: NavTransitions.overlayEnter
  Exit animation:  NavTransitions.overlayExit

Sheet container (anchored to bottom):
  Background: RentoColors.bg1
  Shape: RoundedCornerShape(topStart=28dp, topEnd=28dp)
  Border top: 1.5dp, RentoColors.border2
  Padding: horizontal=20dp, bottom=48dp

Drag handle:
  Size: 40dp × 5dp, RentoColors.bg4, corner=100dp
  Top padding: 12dp, bottom padding: 24dp
  Centred horizontally

Title: "What's your goal?"
  Fraunces 26sp SemiBold, RentoColors.t0, centred, bottom=6dp

Subtitle: "Choose how you want to use the platform today."
  14sp, RentoColors.t2, centred, bottom=28dp

Two intent cards (Column, 14dp gap):
  Each card:
    Background: RentoColors.bg2
    Border:     1.5dp, RentoColors.border2
    Shape:      RoundedCornerShape(24dp)
    Padding:    20dp
    Row layout: CenterVertically, 18dp gap
    Pressed:    scale(0.985), spring

  LOOKING card:
    Icon circle: 56dp, Color(0x26598FD4) fill (~15% blue), icon=RentoIcons.Search(26dp), colour=RentoColors.blue
    Title: "I'm Looking" — 16sp, Bold, RentoColors.t0
    Subtitle: "Post a request and let hosts find you" — 13sp, RentoColors.t2
    Trailing chevron: RentoIcons.Chevron, 22dp, RentoColors.t3
    onClick: onLookingTap()

  HOSTING card:
    Icon circle: 56dp, RentoColors.primaryTint fill, icon=RentoIcons.Home(26dp), colour=RentoColors.primary
    Title: "I'm Hosting" — 16sp, Bold, RentoColors.t0
    Subtitle: "List a property to find the perfect tenant" — 13sp, RentoColors.t2
    Trailing chevron: RentoIcons.Chevron, 22dp, RentoColors.t3
    onClick: onHostingTap()
```

### Signature
```kotlin
@Composable
fun AddOverlaySheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onLookingTap: () -> Unit,
    onHostingTap: () -> Unit,
)
```

### Sub-tasks
- [ ] **M01-T26-A** Implement with `AnimatedVisibility`. Use `Box` for overlay + sheet stacking.
- [ ] **M01-T26-B** Tap detection: overlay Box intercepts all taps; sheet Box `stopPropagation` equivalent via `Modifier.clickable(indication=null) { /* consume */ }`.
- [ ] **M01-T26-C** `@Preview` with `visible=true`.
- [ ] **M01-T26-D** Strings:
  - `add_overlay_title` = "What's your goal?"
  - `add_overlay_subtitle` = "Choose how you want to use the platform today."
  - `add_overlay_looking_title` = "I'm Looking"
  - `add_overlay_looking_subtitle` = "Post a request and let hosts find you"
  - `add_overlay_hosting_title` = "I'm Hosting"
  - `add_overlay_hosting_subtitle` = "List a property to find the perfect tenant"

---

## Task M01-T27 — Component: `HomeBannerSlider`

**File:** `presentation/shared/components/HomeBannerSlider.kt`

### Data Model
```kotlin
@Immutable
data class BannerSlide(
    val id: String,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val gradientIndex: Int,
    val emoji: String,
    val linkedListingId: String?,  // null = no linked listing; navigate to search filter instead
)
```

### Exact Specification
```
Container: Modifier.padding(horizontal=20dp), corner=22dp, height=162dp, clip=true

HorizontalPager (Accompanist or Compose Foundation):
  Each page:
    Background: cardGradientForIndex(gradientIndex) brush
    Emoji watermark: absolute top-right area, 70sp, alpha=0.12f, pointer-ignore

    FEATURED pill (absolute top=14dp, start=14dp):
      Background: Color(0x2B2ECC8A)
      Border:     1dp Color(0x542ECC8A), corner=100dp
      Padding:    3dp V, 11dp H
      "✦ FEATURED": 10sp, RentoColors.primary2, ExtraBold, letterSpacing=0.08em

    Content column (absolute bottom=14dp, start=20dp, end=20dp):
      Title:    Fraunces 22sp SemiBold, Color(0xFFE4F4EC), bottom=4dp
      Subtitle: 13sp, Color(0x94E4F4EC), bottom=12dp

      Bottom Row (SpaceBetween):
        Left: "{ctaText} →" — 12sp, RentoColors.primary2, Bold
        Right page dots Row(5dp gap):
          ACTIVE:   20dp × 6dp, corner=100dp, RentoColors.primary
          INACTIVE: 6dp × 6dp,  corner=100dp, Color(0x47E4F4EC)
          Width: animateDpAsState(320ms tween) — smooth width morph
          Each dot tappable: animateScrollToPage(index)

Auto-advance:
  LaunchedEffect(pagerState) loop:
    delay(3200)
    if (!pagerState.isScrollInProgress) pagerState.animateScrollToPage((current+1) % count)

Tap on slide: onSlideTap(slide.linkedListingId)
  Note: null linkedListingId means no specific listing — caller handles (e.g. open search)
```

### Signature
```kotlin
@Composable
fun HomeBannerSlider(
    slides: List<BannerSlide>,
    onSlideTap: (listingId: String?) -> Unit,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T27-A** Implement with `HorizontalPager` from `androidx.compose.foundation.pager`.
- [ ] **M01-T27-B** Auto-advance with `LaunchedEffect(pagerState)`. Pause check: `pagerState.isScrollInProgress`.
- [ ] **M01-T27-C** `@Preview` with 3 sample slides. Verify dot transitions render.
- [ ] **M01-T27-D** Strings: `banner_featured_label` = "✦ FEATURED"

---

## Task M01-T28 — Component: `MapBackground`

**File:** `presentation/shared/components/MapBackground.kt`

### Exact Specification
```
Box fillMaxSize (or provided size modifier):
  Background: RentoColors.bg2

Canvas overlay (fillMaxSize):
  Grid lines:
    Horizontal: every 28dp, from top to bottom
    Vertical:   every 28dp, from left to right
    Stroke:     1dp, RentoColors.border, alpha=1f

Content slot: BoxScope composable overlay on top of grid
```

### Signature
```kotlin
@Composable
fun MapBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
)
```

### Sub-tasks
- [ ] **M01-T28-A** Implement `MapBackground` as `Box` + `Canvas` grid.
- [ ] **M01-T28-B** Grid spacing uses `LocalRentoDimens.current.mapGridSpacing` (28dp).
- [ ] **M01-T28-C** `@Preview` at 300dp × 200dp.

---

## Task M01-T29 — Component: `RadiusCircle`

**File:** `presentation/shared/components/RadiusCircle.kt`

Used inside `MapBackground` for tenant request radius visualiser.

### Exact Specification
```
Outer circle (Canvas or drawn Box):
  Diameter: (80 + radiusKm * 12).dp
  Fill:     RentoColors.primaryTint (soft green)
  Stroke:   2dp, RentoColors.primary
  Animated: animateDpAsState(150ms spring) when radiusKm changes

Centre dot (drawn on top):
  Size: 12dp circle
  Fill: RentoColors.primary
  Outer halo ring: 4dp RentoColors.bg0 (blends with background to create ring effect)

Position: centred in parent
```

### Signature
```kotlin
@Composable
fun RadiusCircle(
    radiusKm: Int,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T29-A** Implement `RadiusCircle` with animated diameter.
- [ ] **M01-T29-B** `@Preview` at radiusKm=1, 5, 15 side-by-side inside `MapBackground`.
- [ ] **M01-T29-C** Unit tests:
  - `radiusCircle_diameterForRadius1` — (80 + 1*12).dp = 92.dp
  - `radiusCircle_diameterForRadius5` — (80 + 5*12).dp = 140.dp
  - `radiusCircle_diameterForRadius15` — (80 + 15*12).dp = 260.dp

---

## Task M01-T30 — Component: `ShimmerLoader`

**File:** `presentation/shared/components/ShimmerLoader.kt`

### Exact Specification
```
Base composable: ShimmerBox(modifier)
  Applies rememberShimmerBrush(colors) as Modifier.background

Named skeleton slots (all use ShimmerBox internally):

ShimmerPropertyCard():
  Matches full PropertyCard layout:
    Image placeholder: 190dp height, corner=(24dp top, 0 bottom), shimmer
    Content area: 14dp top padding, 3 ShimmerBox rows (title, metrics, badges)
    Title bar: 60% width, 14dp height, 8dp corner
    Metric bar: 100% width, 11dp height, 6dp corner
    Badge row: 2 × 40dp wide, 22dp height, 100dp corner

ShimmerPropertyCardCompact():
  Matches compact PropertyCard layout:
    Image placeholder: 108dp height
    Title bar: 80% width, 11dp height
    Price bar: 30% width, 13dp height

ShimmerRequestCard():
  Matches TenantRequestCard layout:
    Avatar: 44dp circle shimmer
    Name bar: 40% width, 14dp height
    Middle box: full width, 68dp height, 14dp corner
    Bottom row: two bars

ShimmerListRow():
  Generic row: 52dp height, full width, 24dp corner
```

### Sub-tasks
- [ ] **M01-T30-A** Implement `ShimmerBox(modifier: Modifier)` base composable.
- [ ] **M01-T30-B** Implement all 4 named shimmer composables above.
- [ ] **M01-T30-C** `@Preview` showing 3 `ShimmerPropertyCard` stacked.

---

## Task M01-T31 — Component: `GlassScrim` + `GlassDialog`

**File:** `presentation/shared/components/GlassDialog.kt`

These are the foundational glassmorphism primitives used for **every** destructive action and important confirmation in the app. No plain Material `AlertDialog` is ever used for destructive actions.

### Exact Specification — `GlassScrim`
```
Full-screen Box:
  Background: RentoColors.glassScrim
  API ≥ 31: Modifier.blur(16.dp) applied ONLY to background layer, not content
  API 30:   opacity already baked into glassScrim token (no code change needed)

Dismiss behaviour (param-controlled):
  dismissible=true:  tap on scrim calls onDismiss
  dismissible=false: tap on scrim does NOTHING — all touch events consumed silently

Entry: fadeIn(tween(220))
Exit:  fadeOut(tween(180))
```

### Exact Specification — `GlassDialog`
```
Wrapper: Box fillMaxSize (centres dialog)
  Uses GlassScrim as background layer

Dialog container:
  Width: fillMaxWidth(0.88f)
  Height: wrapContentHeight
  Background: RentoColors.glassDialogBg
    graphicsLayer { alpha = 0.97f } — subtle glass transparency
  Shape: RoundedCornerShape(28dp)
  Border: 1dp, glassDialogBorder(colors) brush

Entry animation:
  fadeIn(tween(200)) +
  slideInVertically(
    spring(dampingRatio=DampingRatioMediumBouncy, stiffness=StiffnessMedium),
    initialOffsetY = { it / 8 }
  )

Exit animation:
  fadeOut(tween(160)) +
  slideOutVertically(tween(160), targetOffsetY = { it / 10 })

Internal layout (Column, 28dp H padding, 28dp top/bottom padding):
  PHASE 1 — CONFIRM:
    Icon row (centred, bottom=16dp):
      Circle: 56dp, specified fill colour (per-dialog — see dialog-specific specs)
      Icon:   26dp, specified icon vector
    Title: Fraunces 22sp SemiBold, RentoColors.t0, centred, bottom=8dp
    Context info (optional): 13sp Bold RentoColors.t1, centred, italic, bottom=8dp
    Body text: 14sp, RentoColors.t1, lineHeight=1.65, centred, bottom=20dp
    Acknowledge checkbox (optional, required for high-stakes actions):
      Row(8dp gap, CenterVertically, bottom=20dp):
        Checkbox: custom drawn (not Material Checkbox)
          Unchecked: 18dp × 18dp, RentoColors.bg4 fill, 1.5dp RentoColors.border2 stroke, 5dp corner
          Checked:   RentoColors.primary fill, white Check icon 12dp
        Label: 13sp, RentoColors.t1
    Buttons (Column, 10dp gap):
      ⚠️ ORDER RULE: Cancel button ALWAYS on top, destructive/confirm button BELOW
      Cancel: GhostButton (full width) — ALWAYS the first button
      Destructive/Confirm: DestructiveButton or PrimaryButton (full width) — ALWAYS second

  PHASE 2 — LOADING (non-dismissable):
    No icon row
    RentoDeleteSpinner (32dp) — centred, bottom=16dp
    Loading message (optional): 14sp, RentoColors.t1, centred
    ⚠️ Back press blocked during this phase
    ⚠️ Tap outside blocked during this phase — GlassScrim dismissible=false

  PHASE 3 — RESULT:
    SUCCESS:
      Icon: Check in green circle (DarkPri fill circle, white Check icon)
      Title: success message string
      Auto-dismiss after 1800ms
    ERROR:
      Icon: X in red circle (RentoColors.red fill circle)
      Title: error message string
      Buttons: "Try Again" (PrimaryButton) + "Cancel" (GhostButton)
      ⚠️ AGAIN: Cancel on top, Try Again below (same order rule)
```

### Signature
```kotlin
enum class GlassDialogPhase { CONFIRM, LOADING, SUCCESS, ERROR }

@Composable
fun GlassDialog(
    visible: Boolean,
    phase: GlassDialogPhase,
    iconVector: ImageVector,
    iconCircleColor: Color,
    title: String,
    body: String,
    contextInfo: String? = null,
    acknowledgeLabel: String? = null,           // non-null = show checkbox
    onAcknowledgeChange: ((Boolean) -> Unit)? = null,
    acknowledgeChecked: Boolean = false,
    confirmText: String,
    cancelText: String,
    loadingText: String? = null,
    successTitle: String,
    errorTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit = {},
    onDismissSuccess: () -> Unit = {},          // called after 1800ms auto-dismiss on SUCCESS
)
```

### Sub-tasks
- [ ] **M01-T31-A** Implement `GlassScrim` composable with dismiss behaviour and API-level blur.
- [ ] **M01-T31-B** Implement `GlassDialog` composable with all 3 phases (CONFIRM, LOADING, RESULT).
- [ ] **M01-T31-C** LOADING phase: set `dismissible=false` on scrim, block `BackHandler`.
- [ ] **M01-T31-D** SUCCESS phase: `LaunchedEffect(phase) { if (phase==SUCCESS) { delay(1800); onDismissSuccess() } }`.
- [ ] **M01-T31-E** Implement custom checkbox for acknowledge requirement (NOT Material `Checkbox`).
- [ ] **M01-T31-F** Button order enforcement: Cancel above destructive — this is a hard architectural rule. A code comment `// ORDER: Cancel first — rule enforced per spec` must appear at the button column declaration.
- [ ] **M01-T31-G** `@Preview` for each phase.
- [ ] **M01-T31-H** Strings added to `strings.xml`:
  - `dialog_cancel` = "Cancel"
  - `dialog_try_again` = "Try Again"
  - `dialog_loading_default` = "Please wait…"
  - `dialog_error_default_title` = "Something went wrong"
- [ ] **M01-T31-I** Unit tests (`GlassDialogTest.kt`):
  - `glassDialog_showsConfirmPhaseCorrectly`
  - `glassDialog_showsLoadingPhaseAndBlocksDismiss`
  - `glassDialog_autoDismissesAfter1800msOnSuccess`
  - `glassDialog_showsRetryAndCancelOnError`
  - `glassDialog_cancelButtonAlwaysAboveDestructive`

---

## Task M01-T32 — Component: `GlassBottomSheet`

**File:** `presentation/shared/components/GlassDialog.kt` (same file as GlassDialog)

Used for sheet-style destructive actions (e.g. delete from My Listings long-press context).

### Exact Specification
```
Full-screen overlay:
  Background: RentoColors.glassScrim
  dismissible param (same as GlassScrim)
  Entry: NavTransitions.overlayEnter
  Exit:  NavTransitions.overlayExit

Sheet container (bottom-anchored):
  Background: RentoColors.glassDialogBg
  Shape:      RoundedCornerShape(topStart=28dp, topEnd=28dp)
  Border top: 1dp, glassDialogBorder(colors) brush
  Padding:    horizontal=24dp, top=20dp, bottom=48dp

Drag handle:
  40dp × 5dp, RentoColors.bg4, corner=100dp
  Centred, top padding applied internally

Content: same 3-phase layout as GlassDialog (CONFIRM → LOADING → RESULT)
  Icon row, title, body, acknowledge checkbox, buttons — identical layout
  Only layout difference: no fixed-width constraint (fills width, normal sheet proportions)
  Button order rule: IDENTICAL — Cancel top, destructive bottom

LOADING phase: non-dismissable, BackHandler blocked (identical to GlassDialog)
SUCCESS phase: auto-dismiss after 1800ms (identical to GlassDialog)
```

### Signature
```kotlin
@Composable
fun GlassBottomSheet(
    visible: Boolean,
    phase: GlassDialogPhase,
    iconVector: ImageVector,
    iconCircleColor: Color,
    title: String,
    body: String,
    contextInfo: String? = null,
    acknowledgeLabel: String? = null,
    onAcknowledgeChange: ((Boolean) -> Unit)? = null,
    acknowledgeChecked: Boolean = false,
    confirmText: String,
    cancelText: String,
    loadingText: String? = null,
    successTitle: String,
    errorTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit = {},
    onDismissSuccess: () -> Unit = {},
)
```

### Sub-tasks
- [ ] **M01-T32-A** Implement `GlassBottomSheet`. Extract shared phase-rendering logic into a private composable `GlassDialogContent` reused by both `GlassDialog` and `GlassBottomSheet`.
- [ ] **M01-T32-B** Same phase behaviour as `GlassDialog`: loading blocks dismiss, success auto-dismisses.
- [ ] **M01-T32-C** `@Preview` with `visible=true`, all phases.

---

## Task M01-T33 — Component: `RentoDeleteSpinner`

**File:** `presentation/shared/components/RentoDeleteSpinner.kt`

Custom spinner shown exclusively during the LOADING phase of glass dialogs. Replaces `CircularProgressIndicator` in this context.

### Exact Specification
```
Canvas drawn spinner:
  Size: parameterised (default 32dp)
  Outer circle: stroke only, 2dp, RentoColors.border2, full 360°
  Sweeping arc: stroke 2.5dp, gradientPrimary brush applied as ShaderBrush
    Arc length: 270° (three-quarter circle)
    Animated: infinitely rotating via rememberInfiniteTransition
    Rotation speed: one full rotation per 900ms (LinearEasing — clean mechanical feel)
  No built-in padding
```

### Signature
```kotlin
@Composable
fun RentoDeleteSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
)
```

### Sub-tasks
- [ ] **M01-T33-A** Implement `RentoDeleteSpinner` via `Canvas`. Use `drawArc` for the sweeping arc. Apply `Brush.sweepGradient` from primary to secondary colours for the arc stroke.
- [ ] **M01-T33-B** Rotation animation: `rememberInfiniteTransition` → `animateFloat(0f → 360f, 900ms, LinearEasing, RepeatMode.Restart)`. Apply via `graphicsLayer { rotationZ = angle }` on the Canvas.
- [ ] **M01-T33-C** `@Preview` at 32dp.
- [ ] **M01-T33-D** Content description string: `spinner_cd` = "Loading, please wait"

---

## Task M01-T34 — Component: `InAppNotificationBanner`

**File:** `presentation/shared/components/InAppNotificationBanner.kt`

Shown as an overlay banner from the top of the screen when the app receives an FCM push notification while in the foreground. Dismissed automatically after 4 seconds or by swipe-up.

### Exact Specification
```
Position: absolute top of screen, horizontal padding=12dp, top padding = statusBarHeight + 8dp
  Drawn above all content (Composable in a special overlay scaffold slot)

Container:
  Background: RentoColors.bg1
  Border:     1dp, RentoColors.border2
  Shape:      RoundedCornerShape(16dp)
  Padding:    14dp V, 16dp H
  Shadow:     8dp elevation

Layout: Row(CenterVertically, 12dp gap):
  Icon box: 40dp × 40dp, RentoColors.primaryTint fill, corner=14dp
    Icon: RentoIcons.Bell, 20dp, RentoColors.primary
  Content Column:
    Title: 14sp, Bold, RentoColors.t0, maxLines=1, overflow=Ellipsis
    Body:  12sp, RentoColors.t2, maxLines=2, overflow=Ellipsis, top=2dp
  Close button (optional): RentoIcons.Close, 18dp, RentoColors.t2

Entry animation:
  slideInVertically(spring(DampingRatioMediumBouncy, StiffnessMedium), initialOffsetY = { -it })
  + fadeIn(tween(220))

Exit animation:
  slideOutVertically(tween(220), targetOffsetY = { -it })
  + fadeOut(tween(180))

Auto-dismiss: LaunchedEffect(notifId) { delay(4000); onDismiss() }
Swipe-up gesture: Modifier.pointerInput — detect upward swipe → onDismiss()

Tap on banner: onTap() lambda (navigate to notification detail or relevant screen)
```

### Signature
```kotlin
@Immutable
data class InAppNotification(
    val id: String,
    val title: String,
    val body: String,
)

@Composable
fun InAppNotificationBanner(
    notification: InAppNotification?,   // null = not shown
    onDismiss: () -> Unit,
    onTap: () -> Unit,
)
```

### Sub-tasks
- [ ] **M01-T34-A** Implement `InAppNotificationBanner`. Visibility controlled by `notification != null`.
- [ ] **M01-T34-B** Auto-dismiss `LaunchedEffect` keyed on `notification?.id`.
- [ ] **M01-T34-C** Swipe-up dismiss via `pointerInput`.
- [ ] **M01-T34-D** `@Preview` with a sample notification.
- [ ] **M01-T34-E** Strings:
  - `notif_banner_close_cd` = "Dismiss notification"
  - `notif_banner_tap_cd` = "Open notification"

---

## Task M01-T35 — Component: `EmptyState`

**File:** `presentation/shared/components/EmptyState.kt`

Shown on every list screen when there is no data.

### Exact Specification
```
Column(CenterHorizontally, vertically centred in parent):
  Icon container: 80dp × 80dp, RentoColors.bg3 fill, RoundedCornerShape(28dp), bottom=20dp
    Icon: specified per screen, 36dp, RentoColors.t2
  Title: Fraunces 22sp SemiBold, RentoColors.t0, centred, bottom=8dp
  Subtitle: 14sp, RentoColors.t2, centred, lineHeight=1.6, maxWidth=260dp, bottom=24dp
  CTA button (optional): PrimaryButton — only shown if ctaText non-null
```

### Signature
```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    ctaText: String? = null,
    onCtaClick: () -> Unit = {},
)
```

### Sub-tasks
- [ ] **M01-T35-A** Implement `EmptyState`.
- [ ] **M01-T35-B** `@Preview`: with CTA, without CTA, dark + light.
- [ ] **M01-T35-C** Verify: no hardcoded strings inside component — title/subtitle always passed from outside and come from `stringResource` at call site.

---

## Task M01-T36 — Component: `ErrorBanner`

**File:** `presentation/shared/components/ErrorBanner.kt`

Non-blocking inline error indicator. Used for network errors, form validation summaries.

### Exact Specification
```
Container Row(CenterVertically, 10dp gap):
  Background: RentoColors.redTint
  Border:     1dp, RentoColors.red at 30% alpha
  Shape:      RoundedCornerShape(14dp)
  Padding:    12dp V, 16dp H

Icon: RentoIcons.Close, 18dp, RentoColors.red (or custom icon param)
Text: 13sp, Medium, RentoColors.red, flex=1, maxLines=3, overflow=Ellipsis
Retry button (optional): "Retry" — 12sp Bold RentoColors.red underlined text
```

### Signature
```kotlin
@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = RentoIcons.Close,
    onRetry: (() -> Unit)? = null,
    retryLabel: String = stringResource(R.string.error_retry),
)
```

### Sub-tasks
- [ ] **M01-T36-A** Implement `ErrorBanner`.
- [ ] **M01-T36-B** `@Preview` with and without retry.
- [ ] **M01-T36-C** Strings: `error_retry` = "Retry" · `error_generic` = "Something went wrong. Please try again."

---

## Task M01-T37 — Component: `MeshBackground`

**File:** `presentation/shared/components/MeshBackground.kt`

The subtle radial gradient background used on the Welcome screen and Home screen. Matches the `.mesh` CSS class exactly.

### Exact Specification (Dark Mode)
```
3 layers drawn in order:
  Layer 1: Radial gradient centred at (15%, 18%) of container
    Center colour:  Color(0x0E2ECC8A)  (~5.5% alpha green)
    Edge colour:    Color.Transparent
    Radius:         55% of container width (approximate via Brush.radialGradient)

  Layer 2: Radial gradient centred at (82%, 80%) of container
    Center colour:  Color(0x0927B99A)  (~3.5% alpha teal)
    Edge colour:    Color.Transparent
    Radius:         55% of container width

  Layer 3: Solid fill RentoColors.bg0 (drawn UNDER layers 1 and 2)
    (Compose drawing order: bg0 first, then gradient overlays on top)
```

### Exact Specification (Light Mode)
```
Same structure, different colours:
  Layer 1 center: Color(0x0A0C7A50)  (~4% alpha green)
  Layer 2 center: Color(0x060D9E86)  (~2.5% alpha teal)
  Base: RentoColors.bg0 (LightBg0 = #F0FAF6)
```

### Signature
```kotlin
@Composable
fun MeshBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
)
```

### Sub-tasks
- [ ] **M01-T37-A** Implement `MeshBackground` using `Canvas` with `drawRect` (bg0) and two `drawCircle` calls with `Brush.radialGradient` for the soft glows.
- [ ] **M01-T37-B** Gradient offsets are proportional to container size — use `size.width` and `size.height` inside Canvas.
- [ ] **M01-T37-C** `@Preview` at fillMaxSize dark + light to verify the subtle glow is visible.

---

## Task M01-T38 — Component: `GradientText`

**File:** `presentation/shared/components/GradientText.kt`

### Exact Specification
```
Text with a gradient brush applied as foreground.
Matches the .gtxt CSS class: linear-gradient(130deg, pri 0%, pri2 55%, pri3 100%)
Applied via: text.copy(brush = gradientText(colors))
```

### Signature
```kotlin
@Composable
fun GradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
)
```

### Sub-tasks
- [ ] **M01-T38-A** Implement `GradientText`. Apply brush via `style.copy(brush = gradientText(LocalRentoColors.current))`.
- [ ] **M01-T38-B** `@Preview` showing "Awaits You" in `RentoDisplayXL` style.
- [ ] **M01-T38-C** Note: `GradientText` is a full composable, not just an extension function, because it participates in the Compose tree and accesses composition locals.

---

## Task M01-T39 — Unit Tests

**Files:** `src/test/java/com/rento/app/presentation/shared/components/*Test.kt`

All tests use `ComposeRule` from `androidx.compose.ui.test.junit4`. No Robolectric.

### Required Test Files and Coverage

| File | Tests |
|------|-------|
| `ButtonsTest.kt` | All 4 button types: render text, loading spinner, disabled state, click not fired when disabled/loading |
| `RentoChipTest.kt` | Selected/unselected colours, click fires, disabled blocks click |
| `TabPillTest.kt` | Correct tab selected, callback fires with index |
| `BadgeTest.kt` | All 5 variants produce correct colour tokens |
| `InputFieldsTest.kt` | MaxLength enforcement, error message shown, password toggle, value change fires |
| `ToggleSwitchTest.kt` | Click toggles state, disabled blocks |
| `ProgressStepBarTest.kt` | Active dot correct width, completed dots correct, label text correct |
| `ProgressBarTest.kt` | Progress clamped 0–1, proportional width |
| `RadiusCircleTest.kt` | Diameter formula: `(80 + km * 12).dp` for km=1,5,15 |
| `GlassDialogTest.kt` | All 4 phases render correctly, loading blocks dismiss, success auto-dismisses after 1800ms |

### Sub-tasks
- [ ] **M01-T39-A** Create all test files with tests listed above.
- [ ] **M01-T39-B** Every test class annotated with `@RunWith(AndroidJUnit4::class)` and uses `@get:Rule val composeTestRule = createComposeRule()`.
- [ ] **M01-T39-C** Run `./gradlew test` — all tests must pass. Paste output.
- [ ] **M01-T39-D** Run `./gradlew koverReport` — coverage ≥ 80% on `presentation.shared.components`. Paste summary.

---

## Task M01-T40 — Lint + Detekt + Build Gate

### Sub-tasks
- [ ] **M01-T40-A** Run `./gradlew lint` — zero new warnings. Fix all warnings. If a warning cannot be fixed (e.g. third-party library warning), suppress with `@Suppress` and a comment explaining why.
- [ ] **M01-T40-B** Run `./gradlew detekt` — zero code smell violations. Fix all violations.
- [ ] **M01-T40-C** Run `./gradlew assembleDebug` — must succeed. Paste output: `BUILD SUCCESSFUL in Xs`.
- [ ] **M01-T40-D** Run `./gradlew test` — all tests pass. Paste: `X tests completed, 0 failed`.
- [ ] **M01-T40-E** Run `./gradlew koverReport` — paste coverage summary. Coverage must be ≥ 80%.
- [ ] **M01-T40-F** Update `ANDROID_PROGRESS.md` with all task statuses and pasted evidence.

---

## 44. CSS → Compose Translation Reference

This table maps every CSS class from the prototype to its Compose equivalent. When implementing any screen, if a CSS class is used on a prototype element, find the Compose equivalent here.

| CSS Class | Prototype Usage | Compose Equivalent |
|-----------|-----------------|-------------------|
| `.mesh` | Welcome + Home screen background | `MeshBackground { ... }` |
| `.gtxt` | "Your Next Home" gradient hero text | `GradientText(text, style)` |
| `.btnp` | Primary action buttons | `PrimaryButton(...)` |
| `.btng` | Ghost/outline neutral buttons | `GhostButton(...)` |
| `.btnop` | Outline primary colour button | `OutlinePrimaryButton(...)` |
| `.card` | All listing/request cards | `PropertyCard(...)` / `TenantRequestCard(...)` / custom card with `RentoShapes.card` |
| `.card:active` scale(0.985) | Press feedback on cards | `interactionSource.collectIsPressedAsState()` + `animateFloatAsState` scale |
| `.chip` | Filter chips, type selector chips | `RentoChip(...)` |
| `.chip.on` | Selected chip state | `RentoChip(selected=true, ...)` |
| `.tpill` | Looking/Hosting toggle | `TabPill(...)` |
| `.topt.on` | Active tab in TabPill | Handled internally by `TabPill` |
| `.bdg.bp` | Intent badge (Full Rent, Share) | `RentoBadge(variant=PRIMARY)` |
| `.bdg.br` | Rejected / error badge | `RentoBadge(variant=RED)` |
| `.bdg.ba` | Accent/negotiable badge | `RentoBadge(variant=ACCENT)` |
| `.bdg.bb` | Info/looking badge | `RentoBadge(variant=BLUE)` |
| `.bdg.bm` | Neutral/furnished badge | `RentoBadge(variant=NEUTRAL)` |
| `.bnav` | Bottom navigation bar | `BottomNavBar(...)` |
| `.ni` | Nav item column | Handled internally by `BottomNavBar` |
| `.ni.on` | Active nav item | Handled internally by `BottomNavBar` |
| `.nl` | Nav label text | Handled internally by `BottomNavBar` |
| `.srow` | Horizontal scrolling chip row | `LazyRow(key = { it }) { ... }` |
| `.pb` | Progress bar track | `RentoProgressBar` container |
| `.pf` | Progress bar fill | `RentoProgressBar` fill |
| `.sd` | Step dot | Handled internally by `ProgressStepBar` |
| `.sd.a` | Active step dot | `currentStep` in `ProgressStepBar` |
| `.sd.d` | Completed step dot | Past step in `ProgressStepBar` |
| `.disp` | Fraunces font class | `style = LocalRentoTypography.current.displayX` |
| `.ifield` | Underline text input | `UnderlineInputField(...)` |
| `.ilbl` | Input field label | `SectionLabel(...)` |
| `.iov` | Image overlay gradient | `gradientImageOverlay(colors)` brush on `Box` |
| `.bbl.s` | Sent chat bubble | Chat screen sent bubble (Module 15 scope) |
| `.bbl.r` | Received chat bubble | Chat screen received bubble (Module 15 scope) |
| `.ndot` | Notification red dot | Custom `Box` with `rememberPulseAlpha()` |
| `.mapbg` | Map grid background | `MapBackground { ... }` |
| `.shim` | Shimmer loading placeholder | `ShimmerPropertyCard()` / `ShimmerRequestCard()` etc. |
| `.fi` | Fade-in on screen mount | `LaunchedEffect(Unit) { visible = true }` + `AnimatedVisibility` or `animateFloatAsState` |
| `.su` | Slide-up fade-in on mount (staggered) | `fadeInSlideUpModifier(visible, delayMillis = i * 70)` |
| `@keyframes sheet` | Bottom sheet slide-up entry | `NavTransitions.overlayEnter` |
| `@keyframes shimmer` | Shimmer sweep | `rememberShimmerBrush(colors)` |
| `@keyframes pulse` | Pulsing opacity | `rememberPulseAlpha()` |
| `@keyframes floatY` | Floating vertical bob | `rememberFloatYOffset()` |
| `@keyframes gGlow` | FAB green glow pulse | `rememberGlowElevation()` |
| `@keyframes bounce` | Icon bounce/celebrate | `BounceEffect(trigger) { ... }` |
| `backdrop-filter: blur(24px)` | Nav bar glass blur | `Modifier.blur(24.dp)` (API ≥ 31 only) |
| `backdrop-filter: blur(6px)` | Overlay sheet scrim blur | `Modifier.blur(6.dp)` on scrim layer (API ≥ 31) |
| `backdrop-filter: blur(16px)` | Glass dialog scrim blur | `Modifier.blur(16.dp)` on scrim layer (API ≥ 31) |
| `backdrop-filter: blur(12px)` | Detail screen top buttons | `Modifier.blur(12.dp)` (API ≥ 31) |
| `position: absolute` elements | Overlapping UI (badges on card) | `Box { ... Modifier.align(Alignment.TopStart) ... }` |
| `overflow: hidden` on card | Image not bleeding outside card | `Modifier.clip(RentoShapes.card)` |
| `white-space: nowrap` on chip | Chips don't wrap | `softWrap = false` on `Text` in `RentoChip` |

---

## 45. Component Name Mapping (Prototype → Compose)

| Prototype Variable / Component | Compose Component | Notes |
|-------------------------------|-------------------|-------|
| `function Welcome({nav})` | `WelcomeScreen` (Module 02) | Uses `MeshBackground`, `GradientText`, `PrimaryButton` |
| `function SignIn({nav})` | `SignInScreen` (Module 02) | Uses `BoxedInputField`, `PrimaryButton`, `GhostButton` |
| `function SignUp({nav})` | `SignUpScreen` (Module 02) | Uses `BoxedInputField`, `PrimaryButton` |
| `function Home({nav})` | `HomeScreen` (Module 03) | Uses `TabPill`, `HomeBannerSlider`, `PropertyCard`, `TenantRequestCard` |
| `function Detail({item, nav})` | `ListingDetailScreen` (Module 04) | Uses all card primitives, `MeshBackground` header |
| `function ReqDetail({item, nav})` | `RequestDetailScreen` (Module 05) | Uses `RadiusCircle`, `MapBackground`, `RentoChip` |
| `function Form({nav})` | `ListingFormScreen` (Module 06) | 11 steps, uses `ProgressStepBar`, `UnderlineInputField`, `ToggleSwitch` |
| `function RequestForm({nav})` | `RequestFormScreen` (Module 07) | 6 steps, uses `ProgressStepBar`, `RadiusCircle` slider |
| `function ChatScr({nav})` | `ChatScreen` (Module 08) | Chat bubbles are screen-specific, not shared components |
| `function MyListings({nav})` | `MyListingsScreen` (Module 09) | Uses `RentoProgressBar`, `RentoChip` tabs, `GlassBottomSheet` for delete |
| `function ProfileScr({nav})` | `ProfileScreen` (Module 10) | Uses `ToggleSwitch` (theme toggle), `PackageIcon` |
| `function Packages({nav})` | `PackagesScreen` (Module 11) | Uses `RentoProgressBar`, `PrimaryButton` |
| `function MapScr({nav})` | `MapScreen` (Module 12) | Uses `MapBackground`, price pin custom composables |
| `function Notifs({nav})` | `NotificationsScreen` (Module 13) | Uses `rememberPulseAlpha` on unread dot |
| `function BNav({active, nav, onAdd})` | `BottomNavBar` | ✅ Implemented in Module 01 |
| `function BSlider({nav})` | `HomeBannerSlider` | ✅ Implemented in Module 01 |
| `function PCard({p, nav, compact})` | `PropertyCard(compact=false/true)` | ✅ Implemented in Module 01 |
| `function RCard({r, nav})` | `TenantRequestCard` | ✅ Implemented in Module 01 |
| `function AddOverlay({close, nav})` | `AddOverlaySheet` | ✅ Implemented in Module 01 |
| `function SBar()` | `RentoStatusBar` | ✅ Implemented in Module 01 |
| `const css .shim` | `ShimmerLoader` family | ✅ Implemented in Module 01 |
| `const css .mapbg` | `MapBackground` | ✅ Implemented in Module 01 |

---

## 46. Progress File Spec

The agent must maintain `ANDROID_PROGRESS.md` in the project root. Updated **after every task** — not just after every module.

```markdown
# RentO Android — Progress Tracker

---

## Module 01 — Design System & Theme
**Status:** 🔄 In Progress | ✅ Complete | 🚫 Blocked
**Branch:** feature/module-01-design-system

### Tasks
- [x] M01-T01 — Project Bootstrap — evidence: `BUILD SUCCESSFUL in 12s`
- [x] M01-T02 — Color.kt — evidence: compiles, previews render in dark/light
- [ ] M01-T03 — Type.kt
- [ ] M01-T04 — Dimens.kt + Shape.kt
- [ ] M01-T05 — Gradients.kt
- [ ] M01-T06 — Theme.kt
- [ ] M01-T07 — RentoIcons.kt (46 icons)
- [ ] M01-T08 — Animations.kt
- [ ] M01-T09 — RentoStatusBar
- [ ] M01-T10 — PrimaryButton
- [ ] M01-T11 — GhostButton
- [ ] M01-T12 — OutlinePrimaryButton
- [ ] M01-T13 — DestructiveButton
- [ ] M01-T14 — RentoChip
- [ ] M01-T15 — TabPill
- [ ] M01-T16 — Badge (5 variants)
- [ ] M01-T17 — SectionLabel
- [ ] M01-T18 — UnderlineInputField
- [ ] M01-T19 — BoxedInputField
- [ ] M01-T20 — ToggleSwitch
- [ ] M01-T21 — ProgressStepBar
- [ ] M01-T22 — ProgressBar
- [ ] M01-T23 — PropertyCard (Full + Compact)
- [ ] M01-T24 — TenantRequestCard
- [ ] M01-T25 — BottomNavBar
- [ ] M01-T26 — AddOverlaySheet
- [ ] M01-T27 — HomeBannerSlider
- [ ] M01-T28 — MapBackground
- [ ] M01-T29 — RadiusCircle
- [ ] M01-T30 — ShimmerLoader
- [ ] M01-T31 — GlassScrim + GlassDialog
- [ ] M01-T32 — GlassBottomSheet
- [ ] M01-T33 — RentoDeleteSpinner
- [ ] M01-T34 — InAppNotificationBanner
- [ ] M01-T35 — EmptyState
- [ ] M01-T36 — ErrorBanner
- [ ] M01-T37 — MeshBackground
- [ ] M01-T38 — GradientText
- [ ] M01-T39 — Unit Tests
- [ ] M01-T40 — Lint + Detekt + Build Gate

### Build Gate (filled when module complete)
- `./gradlew assembleDebug`: PASS / FAIL
- `./gradlew test`: X passed, 0 failed
- `./gradlew lint`: 0 warnings
- `./gradlew detekt`: 0 violations
- `./gradlew koverReport`: XX% coverage (≥80% required)

### Blocking Issues
(List any blockers here with reason and date raised)
```

---

*End of Module 01 — Design System & Theme v1.1.0*
*This document is the single source of truth for Module 01. Do not modify design values without bumping the version number.*
