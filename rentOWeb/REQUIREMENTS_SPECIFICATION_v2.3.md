# 🏠 NesCo — Full Requirements Specification
> **Version:** 2.3.0  
> **Date:** 2026-02-22  
> **Status:** Active — Single Source of Truth  
> **Audience:** Android Agent · Web Admin Agent · QA Agent · Code Review Agent  
> **Supersedes:** v2.0.0, v2.1 Addendum, v2.2 Draft  
> **This is the only file agents need. All prior versions are obsolete.**

---

## ⚠️ AGENT STANDING ORDERS (Read Before EVERY Task)

1. **Never modify a design component — only extend it.** Open/Closed Principle enforced.
2. **Never change the color palette** once finalised. Values are frozen in Section 3 of this document.
3. **Every ViewModel must have unit tests.**
4. **Every Repository method must have unit tests** (mock Firestore with fakes).
5. **No UseCase that is a pass-through.** UseCases only exist if they contain logic beyond a single repo call.
6. **ViewModels call Repository interfaces, not implementations.**
7. **Domain layer has zero Android imports.**
8. **Before starting any feature, switch to a new Git branch.**
9. **After every module, run `./gradlew assembleDebug` and `./gradlew test`. Both must pass.**
10. **After every module, update `PROGRESS.md`** with tasks and their completion status.
11. **After every module, produce `CODE_REVIEW_<module>.md`** with lint findings (non-blocking, nice-to-have).
12. **All user-facing strings live in `res/values/strings.xml`. Zero hardcoded strings.**
13. **All Composables must use `remember`, `derivedStateOf`, stable data classes (`@Stable`/`@Immutable`).**
14. **LazyColumn/LazyRow always use `key {}` parameter.**
15. **If Firestore layer changes to a backend, only `*RepositoryImpl` files change — nothing else.**
16. **Push notifications use Firebase Cloud Messaging (FCM) only. OneSignal is NOT used in this project.**
17. **The dual marketplace (Listings + Tenant Requests) is a core feature. Never reduce it to listings-only.**
18. **The prototype JSX file (`design_references.md`) is the visual law. Every pixel, gradient, radius, font, and animation is derived from it. Do not improvise or substitute.**
19. **Before marking any screen complete, open `design_references.md` and verify the implementation against it.** Check: background colour tokens, border radii, font family/size/weight, icon names, padding values, gradient directions, animation types, and all interactive states. Log results in `CODE_REVIEW_<module>.md` under "Design Reference Verification".
20. **Every journey in Section 33 must be fully implemented before a module is marked complete.** They are not optional polish — they are core user flows.
21. **Every destructive action must use the Glassmorphic Dialog or Sheet pattern defined in Section 34.** No plain Material dialogs for destructive actions.
22. **Deletion is never instant.** Every delete flow shows three phases: Confirm → Loading (locked UI) → Result. All three required.
23. **"Delete" and "Block" action buttons are always DarkRed fill with white text.** Never primary green. Destructive = red. No exceptions.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Design System — Pixel-Perfect Spec](#3-design-system--pixel-perfect-spec)
4. [Architecture](#4-architecture)
5. [Package Structure](#5-package-structure)
6. [Module: Authentication](#6-module-authentication)
7. [Module: User Profile & Onboarding](#7-module-user-profile--onboarding)
8. [Module: Home & Discovery (Dual Marketplace)](#8-module-home--discovery-dual-marketplace)
9. [Module: Property Listing Form (Hosting)](#9-module-property-listing-form-hosting)
10. [Module: Tenant Request Form (Looking)](#10-module-tenant-request-form-looking)
11. [Module: Listing Detail](#11-module-listing-detail)
12. [Module: Tenant Request Detail](#12-module-tenant-request-detail)
13. [Module: Map View](#13-module-map-view)
14. [Module: Saved Items](#14-module-saved-items)
15. [Module: Chat](#15-module-chat)
16. [Module: Notifications (FCM)](#16-module-notifications-fcm)
17. [Module: Packages & Subscriptions](#17-module-packages--subscriptions)
18. [Module: My Listings & My Requests (Dashboard)](#18-module-my-listings--my-requests-dashboard)
19. [Module: Settings & Account](#19-module-settings--account)
20. [Module: Feedback & Feature Requests](#20-module-feedback--feature-requests)
21. [Module: Force Update & Connectivity](#21-module-force-update--connectivity)
22. [Module: Admin Web Portal](#22-module-admin-web-portal)
23. [Firestore Data Models](#23-firestore-data-models)
24. [Remote Config & Gemini Integration](#24-remote-config--gemini-integration)
25. [Firebase Cloud Messaging (FCM)](#25-firebase-cloud-messaging-fcm)
26. [Cloud Functions](#26-cloud-functions)
27. [Performance & Optimisation Rules](#27-performance--optimisation-rules)
28. [Security Rules](#28-security-rules)
29. [Testing Strategy](#29-testing-strategy)
30. [PROGRESS.md Spec](#30-progressmd-spec)
31. [Design Reference Verification System](#31-design-reference-verification-system)
32. [All Journeys & Edge-Case Flows](#32-all-journeys--edge-case-flows)
33. [Delete Dialogs & Glassmorphic Design System](#33-delete-dialogs--glassmorphic-design-system)
34. [CODE_REVIEW.md Spec](#34-code_reviewmd-spec)

---

## 1. Project Overview

**App Name:** NesCo (Placeholder — client to finalise)  
**Platform:** Native Android (Jetpack Compose) + React Web Admin Portal  
**Purpose:** A two-sided accommodation marketplace — hosts list properties; seekers post what they need. Both sides are fully equal citizens of the platform.  
**Launch Market:** Pakistan (Karachi-first, nation-wide scope)  
**Currency:** PKR  
**Language:** English only (V1); all strings in `strings.xml` for future localisation  
**Dark Mode:** Required from day one. Default = system preference.  
**Light Mode:** Fully implemented — identical UX in both themes.

---

## 2. Tech Stack

### Android App
| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (single Activity) |
| Navigation | Jetpack Navigation Compose |
| DI | Koin |
| Async | Coroutines + `.await()` on all Firestore calls |
| Database | Cloud Firestore |
| Auth | Firebase Authentication (email/password + Google Sign-In) |
| Storage | Firebase Storage |
| Push Notifications | **Firebase Cloud Messaging (FCM)** — no OneSignal |
| Remote Config | Firebase Remote Config |
| Image Loading | Coil (with crossfade) |
| Image Compression | Client-side (Compressor / Luban) — target <500KB, reject if original >1.5MB |
| Maps | Google Maps SDK (Compose) |
| Analytics | Firebase Analytics |
| Crash Reporting | Firebase Crashlytics |
| Connectivity | NetworkCallback (show no-internet screen — no offline support) |
| Fonts | Fraunces (display/serif) + Plus Jakarta Sans (body) — bundled as assets |
| Min SDK | API 30 (Android 11) |

### Web Admin Portal
| Layer | Technology |
|---|---|
| Framework | React (Vite) |
| Auth | Firebase Authentication (same Firebase project) |
| Database | Firestore SDK for Web |
| Push | FCM Admin SDK via Cloud Functions |
| Charts | Recharts or Chart.js |
| Design | Same design tokens as Android app — green on dark, same typography |
| Hosting | Internal use only (localhost) V1 |

---

## 3. Design System — Pixel-Perfect Spec

> ⚠️ **AGENT LAW:** Every value in this section is copied verbatim from the approved prototype. Do not substitute, approximate, or "improve" these values. Deviating from this section is a hard blocker.

### 3.1 Typography

| Role | Typeface | Weights Used | Notes |
|---|---|---|---|
| Display / Headings | **Fraunces** (serif) | 300, 400, 600, 700 | Screen titles, card titles, price displays. Italics available. Bundle in `assets/fonts/`. |
| Body / UI | **Plus Jakarta Sans** (sans-serif) | 300, 400, 500, 600, 700, 800 | All buttons, labels, chips, body text, navigation. Bundle in `assets/fonts/`. |

**Android font setup:** Declare both typefaces in `res/font/` as downloadable fonts OR bundle directly. Create a `FontFamily` in `ui/theme/Type.kt`:
```kotlin
val FrauncesFamily = FontFamily(
    Font(R.font.fraunces_light, FontWeight.Light),
    Font(R.font.fraunces_regular, FontWeight.Normal),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_bold, FontWeight.Bold),
)
val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_light, FontWeight.Light),
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)
```

**Type Scale:**
| Name | Family | Size | Weight | Usage |
|---|---|---|---|---|
| `displayXL` | Fraunces | 50sp | SemiBold | Welcome screen hero |
| `displayL` | Fraunces | 32sp | SemiBold | Auth screen titles |
| `displayM` | Fraunces | 28sp | SemiBold | Form step titles, section headings |
| `displayS` | Fraunces | 22–26sp | SemiBold | Card titles, section headers |
| `bodyL` | Plus Jakarta Sans | 15–16sp | Bold/SemiBold | Button text, primary body |
| `bodyM` | Plus Jakarta Sans | 14sp | Regular/Medium | Body text, descriptions |
| `bodyS` | Plus Jakarta Sans | 13sp | Regular/Medium | Secondary labels, subtitles |
| `label` | Plus Jakarta Sans | 11–12sp | Bold | Chips, badges, ALLCAPS section labels |
| `micro` | Plus Jakarta Sans | 10–11sp | Regular/Medium | Timestamps, captions, nav labels |

### 3.2 Colour Palette

Define all colours in `ui/theme/Color.kt`. Never hardcode colours in Composables — always reference theme tokens.

#### Dark Theme Tokens
```kotlin
// Backgrounds (darkest → lightest)
val DarkBg0 = Color(0xFF04100C)   // App background / screen base
val DarkBg1 = Color(0xFF08150F)   // Card surface primary
val DarkBg2 = Color(0xFF0D1E17)   // Card surface secondary / input bg
val DarkBg3 = Color(0xFF142A20)   // Subtle fill / chip bg
val DarkBg4 = Color(0xFF1C3529)   // Progress track / toggle track

// Primary (green)
val DarkPri  = Color(0xFF2ECC8A)   // Primary action, selected states, icons
val DarkPri2 = Color(0xFF54DBA2)   // Lighter primary, gradient mid
val DarkPri3 = Color(0xFFA8F0D8)   // Lightest primary, gradient end
val DarkSec  = Color(0xFF27B99A)   // Secondary green (gradient complement)

// Semantic
val DarkAcc  = Color(0xFFF0C94A)   // Accent / star / warning
val DarkRed  = Color(0xFFE06060)   // Error / rejected / destructive
val DarkBlue = Color(0xFF5A9FD4)   // Info / badge

// Text
val DarkT0 = Color(0xFFE4F4EC)   // Primary text
val DarkT1 = Color(0xFF9DC8B4)   // Secondary text
val DarkT2 = Color(0xFF5A8A76)   // Tertiary text / labels
val DarkT3 = Color(0xFF2A4A3C)   // Placeholder / disabled text

// Borders
val DarkBd  = Color(0xFF142A20)   // Standard border
val DarkBd2 = Color(0xFF1C3529)   // Subtle border

// Overlays & Nav
val DarkNav = Color(0xF208150F)   // Bottom nav (95% opacity bg1)
val DarkOv  = Color(0xB804100C)   // Modal overlay (72% opacity)
val DarkIpl = Color(0xFF243D30)   // Input underline idle

// Tinted overlays (use as ARGB)
val DarkPriM  = Color(0x1A2ECC8A) // Primary tint 10% (priM)
val DarkPriR  = Color(0x4D2ECC8A) // Primary ring 30% (priR)
val DarkAccM  = Color(0x21F0C94A) // Accent tint 13%
val DarkRedM  = Color(0x1CE06060) // Error tint 11%
val DarkBlueM = Color(0x1C5A9FD4) // Blue tint 11%
```

#### Light Theme Tokens
```kotlin
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

val LightNav = Color(0xF5FFFFFF)
val LightOv  = Color(0x800620IA)
val LightIpl = Color(0xFFB8D8CC)

val LightPriM  = Color(0x140C7A50)
val LightPriR  = Color(0x3D0C7A50)
val LightAccM  = Color(0x17B87010)
val LightRedM  = Color(0x12C04040)
val LightBlueM = Color(0x122E6EA0)
```

#### Gradients (define as Brush constants)
```kotlin
// Primary action button gradient
val GradientPrimary = Brush.linearGradient(
    colorStops = arrayOf(0f to DarkPri, 1f to DarkSec),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

// Hero/banner gradient (property cards)
// Each property card has its own gradient — these are the standard ones:
val GradientCard1 = Brush.linearGradient(
    colors = listOf(Color(0xFF03200E), Color(0xFF083C1C), Color(0xFF052814)),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
val GradientCard2 = Brush.linearGradient(
    colors = listOf(Color(0xFF031E18), Color(0xFF073828), Color(0xFF042818)),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
val GradientCard3 = Brush.linearGradient(
    colors = listOf(Color(0xFF041C0E), Color(0xFF093618), Color(0xFF062412)),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
val GradientCard4 = Brush.linearGradient(
    colors = listOf(Color(0xFF031A14), Color(0xFF073020), Color(0xFF041C12)),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

// Gradient text effect (green shimmer on display headings)
// Use as shader on TextPaint or custom DrawScope — apply to "Your Next Home" text etc.
val GradientTextBrush = Brush.linearGradient(
    colorStops = arrayOf(0f to DarkPri, 0.55f to DarkPri2, 1f to DarkPri3),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, 0f)
)

// Mesh background (home screen)
// Implement as Canvas with two radial gradients behind bg0 fill:
// Ellipse at 15%/18%: DarkPri at 5.5% opacity, transparent at 55%
// Ellipse at 82%/80%: DarkSec at 3.5% opacity, transparent at 55%
```

### 3.3 Spacing & Sizing

| Token | Value | Usage |
|---|---|---|
| `screenPadH` | 20dp | Horizontal screen padding |
| `screenPadTop` | 16dp | Screen top padding below status bar |
| `cardRadius` | 24dp | Standard card corner radius |
| `cardRadiusSmall` | 18–20dp | Compact card, bottom sheet containers |
| `btnRadius` | 100dp | All buttons (fully rounded / pill) |
| `chipRadius` | 100dp | All chips (fully rounded) |
| `inputWrapperRadius` | 16dp | Input field container |
| `badgeRadius` | 7dp | Status badges |
| `avatarRadius` | 50% | All circular avatars |
| `iconBtnRadius` | 15dp | Square icon buttons (back button etc.) |
| `amenityTileRadius` | 16dp | Amenity grid tiles |
| `navHeight` | 86dp | Bottom navigation bar height |
| `navPaddingBottom` | 18dp | Nav content bottom offset |
| `navPaddingH` | 8dp | Nav horizontal padding |
| `fabSize` | 54dp | Floating action button (add button in nav) |
| `cardImageHeightFull` | 190dp | Full property card image area |
| `cardImageHeightCompact` | 108dp | Grid/compact card image area |
| `detailImageHeight` | 315dp | Property detail hero image |
| `sliderHeight` | 162dp | Home screen banner slider |
| `sectionPad` | 24dp | Between major sections |
| `cardGap` | 16dp | Vertical gap between list cards |
| `gridGap` | 12dp | Gap in 2-column grid |
| `chipGap` | 8–10dp | Gap between chips in wrapping row |

### 3.4 Reusable Components

Every component below must be implemented as a standalone `@Composable` function in `presentation/shared/components/`.

---

#### 3.4.1 `StatusBar` (Fake iOS-Style)
Displayed at top of every screen within the app container.
```
Layout: Row — full width, padding 13dp top, 24dp horizontal
Left: "9:41" — 15sp, bold, t0 colour
Right: Signal bars SVG + Battery SVG (drawn as Canvas or ImageVector)
  Signal bars: 4 bars at 0%, 32%, 55%, 78%, 100% opacity; each bar progressively taller
  Battery: outline rect (22×11dp, 3.5dp corner), fill rect (17dp wide), right nub (2×4dp)
```
Use `Spacer` to push right cluster to end. This bar is always `t0` coloured (adapts to theme).

---

#### 3.4.2 `PrimaryButton` (`.btnp`)
```
Shape: RoundedCornerShape(100dp) — full pill
Background: Brush.linearGradient(DarkPri → DarkSec, 135°)
Text: white, 15sp, Bold, Plus Jakarta Sans
Padding: 16dp vertical, full width by default
Shadow: 0dp offset, 18dp blur, DarkPriR colour (30% green)
Pressed: scale(0.97) + alpha(0.9) — use `interactionSource` + `animateFloatAsState`
Animation: 200ms ease
```

---

#### 3.4.3 `GhostButton` (`.btng`)
```
Shape: RoundedCornerShape(100dp)
Background: transparent
Border: 1.5dp, DarkBd2
Text: DarkT1, 13–15sp, Medium
Pressed: background tint DarkBg3
```

---

#### 3.4.4 `OutlinePrimaryButton` (`.btnop`)
```
Shape: RoundedCornerShape(100dp)
Background: transparent
Border: 1.5dp, DarkPri
Text: DarkPri, 13sp, Medium
Pressed: background tint DarkPriM
```

---

#### 3.4.5 `Chip` (`.chip`)
```
Shape: RoundedCornerShape(100dp)
Background idle: DarkBg3
Border idle: 1.5dp DarkBd2
Text idle: DarkT1, 12.5sp, Medium
Padding: 7dp vertical, 14dp horizontal
Icon (optional): 14dp, same colour as text, 6dp gap

SELECTED (.chip.on):
Background: DarkPriM (10% green tint)
Border: 1.5dp DarkPri
Text: DarkPri, Bold
Icon: DarkPri

Transition: animateColorAsState, 220ms
White-space: no wrap (scrollable row)
```

---

#### 3.4.6 `TabPill` (`.tpill` / `.topt`)
Used for the "Looking / Hosting" toggle on home screen.
```
Container: Row, DarkBg3 fill, 100dp corner, 4dp padding, 1.5dp DarkBd border

Tab item (inactive):
  Padding: 8dp vertical, 20dp horizontal
  Text: 13sp, Medium, DarkT2
  Background: transparent
  Corner: 100dp

Tab item (active .topt.on):
  Background: DarkPri
  Text: white, Bold
  Shadow: 0dp/14dp/DarkPriR
  Transition: 260ms
```

---

#### 3.4.7 `Badge` (`.bdg` variants)
All badges: inline, 3dp vertical / 9dp horizontal padding, 7dp corner.
```
.bp (primary):   DarkPriM fill, DarkPri text, 1dp DarkPriR border, 11sp Bold
.br (red):       DarkRedM fill, DarkRed text, 11sp Bold
.ba (accent):    DarkAccM fill, DarkAcc text, 11sp Bold
.bb (blue):      DarkBlueM fill, DarkBlue text, 11sp Bold
.bm (neutral):   DarkBg3 fill, DarkT2 text, 1dp DarkBd2 border, 11sp Bold
```

---

#### 3.4.8 `PropertyCard` (Full + Compact variants)

**Full card:**
```
Container: DarkBg2 fill, 1.5dp DarkBd border, 24dp corner, clip to bounds
Pressed: scale(0.985), 240ms

Image area (190dp height):
  Background: card gradient (assigned per listing)
  Emoji watermark: centred, 72sp, 10% opacity
  Gradient overlay (iov): linear top-transparent → bottom-92%-black
  Top-left badge: property type, glassmorphism (rgba(4,16,12,0.55), 10dp blur, 1dp green border, 11sp)
  Top-right: Heart/bookmark icon button — 34dp circle, same glassmorphism
  Bottom-left: Pin icon (14dp, pri2 colour) + "Area, City" — 12sp, rgba(E4F4EC, 0.82)

Content area (padding 14dp/16dp/16dp):
  Title: 15sp, SemiBold, 1.3 line height
  Beds row: Pin icon chips — "X bed" + "X bath" — 14dp icons, 12.5sp DarkT2
  Price: 19sp Bold DarkPri + " PKR/mo" 11sp DarkT2
  Bottom badges row: Intent badge (.bp) + Furnish badge (.bm), gap 7dp, top margin 10dp
```

**Compact card (2-column grid):**
```
Container: same card style
Image: 108dp height, same gradient + emoji overlay
Content: 10dp/13dp/14dp padding
  Title: 12sp, SemiBold, 1.35 line height
  Price: 14sp Bold DarkPri + " PKR/mo" 10sp DarkT2
```

---

#### 3.4.9 `TenantRequestCard` (`.RCard`)
```
Container: DarkBg2 fill, 1.5dp DarkBd border, 24dp corner
Padding: 18dp/20dp
Pressed: scale(0.985)

Top row:
  Avatar: 44dp circle, DarkPriM fill, 1.5dp DarkPriR border, User icon 20dp DarkPri
  Name: 15sp Bold, bottom "Needs by {date}" 12sp DarkT2
  Right: Intent badge (.bb)

Middle box (DarkBg3, 12dp/14dp padding, 14dp corner):
  Label: "LOOKING FOR" 11sp DarkT2 Bold uppercase letter-spacing 0.06em
  Type: 14sp SemiBold DarkT0
  Beds/baths: 12sp DarkT1 "X Bed • X Bath"

Bottom row:
  Left: Pin 14dp DarkPri + areas + "(+Xkm)" — 12sp DarkT1
  Right: "Up to" 11sp DarkT2 + price 18sp Bold DarkPri + " PKR/mo" 11sp DarkT2
```

---

#### 3.4.10 `ProgressStepBar`
Used in multi-step forms.
```
Row of dots (step indicators):
  Active step: 22dp wide, 5dp tall, 100dp corner, DarkPri fill
  Completed step: 5dp wide, 5dp tall, 100dp corner, DarkPri2 at 55% opacity
  Future step: 5dp wide, 5dp tall, 100dp corner, DarkBg4 fill
  Gap between dots: 5dp
  
Below dots: "Step N of T" — 11sp SemiBold DarkT2

Animate width changes: animateDpAsState 300ms
```

---

#### 3.4.11 `SectionLabel` (`.ilbl`)
```
11sp, Bold, DarkT2, UPPERCASE, letter-spacing 0.06em
Bottom margin: 8dp
```

---

#### 3.4.12 `UnderlineInputField` (`.ifield`)
```
Width: fill max
Background: transparent
Border: none (no box), only 2dp bottom line
  Idle: DarkIpl colour
  Focused: DarkPri colour (animateColorAsState)
Text: 15sp, DarkT0
Placeholder: DarkT3
Padding: 10dp vertical, 0 horizontal
Font: Plus Jakarta Sans
Transition: 220ms border colour
```

---

#### 3.4.13 `BoxedInputField`
Used in auth screens and search bar:
```
Container Row: DarkBg2 fill, 1.5dp DarkBd border, 16dp corner
Padding: 12dp vertical, 16dp horizontal
Leading icon: 22dp, DarkT2 colour, 12dp gap
Input (underline-less): same as UnderlineInputField but border=none
Optional trailing icon: eye, etc.
```

---

#### 3.4.14 `BottomNavBar` (`.bnav`)
```
Position: absolute bottom 0, full width, height 86dp
Background: DarkNav (95% opacity DarkBg1) with BackdropBlur (24dp)
Border top: 1dp DarkBd
Content padding bottom: 18dp

5 items: Home · Map · [Add FAB] · Chat · Profile

Nav item (inactive):
  Column: icon (22dp) + label (10sp Medium DarkT2)
  Padding: 8dp V / 14dp H, 18dp corner
  Icon colour: DarkT2

Nav item (active .ni.on):
  Background: DarkPriM
  Icon colour: DarkPri
  Label colour: DarkPri
  Transition: 220ms

Center FAB (Add button):
  54dp circle
  Background: gradient DarkPri → DarkSec (135°)
  Icon: Plus, white, 22dp
  Animation: gGlow pulse (3s infinite)
    0%,100%: shadow 0/16dp DarkPriR
    50%: shadow 0/32dp DarkPriR + 0/60dp rgba(46,204,138,0.12)
  On tap: opens AddOverlay bottom sheet
```

---

#### 3.4.15 `AddOverlay` (Bottom Sheet — Intent Chooser)

Triggered by tapping the centre FAB. This is the gateway to both sides of the marketplace.

```
Wrapper: full-screen overlay
  Background: DarkOv (72% opacity) + BackdropBlur(6dp)
  Tap outside: dismiss

Sheet container (slides up, spring animation):
  Background: DarkBg1
  Corner: 28dp top only
  Padding: 24dp H / 48dp bottom
  Border top: 1.5dp DarkBd2
  Drag handle: 40dp × 5dp, DarkBg4, 100dp corner, auto-centred, bottom margin 24dp

Sheet title: "What's your goal?" — Fraunces 26sp SemiBold
Subtitle: "Choose how you want to use the platform today." — 14sp DarkT2, centred, bottom margin 28dp

Two action cards (column, 14dp gap):
  Each card: DarkBg2 fill, 1.5dp DarkBd2 border, 24dp corner
  Padding: 20dp
  Layout: Row (icon area + text + chevron)
  Pressed: scale 0.985

  Card 1 — "I'm Looking":
    Icon container: 56dp circle, rgba(90,159,212,0.15) fill, DarkBlue colour, Search icon 26dp
    Title: 16sp Bold
    Subtitle: "Post a request and let hosts find you" — 13sp DarkT2
    Chevron: 22dp DarkT3
    → Navigates to: Tenant Request Form

  Card 2 — "I'm Hosting":
    Icon container: 56dp circle, DarkPriM fill, DarkPri colour, Home icon 26dp
    Title: 16sp Bold
    Subtitle: "List a property to find the perfect tenant" — 13sp DarkT2
    Chevron: 22dp DarkT3
    → Navigates to: Property Listing Form
```

---

#### 3.4.16 `HomeBannerSlider`
```
Container: 0dp H margin, 24dp bottom margin
  Corner: 22dp
  Height: 162dp
  Clip to shape

Content area (background = one of banner gradients, cycling every 3.2 seconds):
  Emoji watermark: top-right, 70sp, 12% opacity
  
  Featured pill (top-left corner at 14dp):
    Background: rgba(46,204,138,0.17)
    Border: 1dp rgba(46,204,138,0.33), 100dp corner
    Text: "✦ FEATURED" — 10sp, DarkPri2, ExtraBold, letter-spacing 0.08em
    Padding: 3dp V / 11dp H
    Bottom margin: 9dp

  Title: Fraunces 22sp SemiBold DarkT0 (E4F4EC in dark), bottom 4dp
  Subtitle: 13sp rgba(228,244,236,0.58), bottom 12dp

  Bottom row:
    Left: CTA text + "→" arrow — 12sp DarkPri2 Bold
    Right: Page indicator dots
      Active: 20dp wide, 6dp tall, 100dp corner, DarkPri
      Inactive: 6dp wide, 6dp tall, 100dp corner, rgba(228,244,236,0.28)
      Transition: 320ms width + colour

Animation: LaunchedEffect auto-advances every 3200ms using HorizontalPager
Manual swipe: enabled
Tap: navigates to linked listing
```

---

#### 3.4.17 `MapBackground` (`.mapbg`)
```
Background: DarkBg2 fill
Grid lines overlay (drawn as Canvas):
  Horizontal lines: every 28dp, DarkBd colour, 1dp stroke
  Vertical lines: every 28dp, DarkBd colour, 1dp stroke
Used for: radius selector in request form, area map preview in detail
```

---

#### 3.4.18 `RadiusCircle` (Request Form map)
```
Centred in MapBackground container
Circle: diameter = 80dp + (radiusKm × 12dp)
Fill: DarkPriM (10% green)
Stroke: 2dp DarkPri
Centre dot: 12dp circle, DarkPri fill, 4dp DarkBg0 ring around it (outlined)
Animate diameter: animateDpAsState 150ms
```

---

#### 3.4.19 `ShimmerLoader`
```
For list items and cards: use InfiniteTransition
Background gradient: DarkBg3 at 25%, DarkBg2 at 50% (shimmer peak), DarkBg3 at 75%
Horizontal sweep animation: 1500ms linear infinite
Apply to Box/Column placeholders matching card dimensions
```

---

#### 3.4.20 `ToggleSwitch`
```
Container: 46dp × 26dp, 100dp corner
Active (on): DarkPri fill, shadow 0/10dp DarkPriR
Inactive (off): DarkBg4 fill
Thumb: 20dp circle, white, offset 3dp from edge (right when on, left when off)
Animate: animateDpAsState for thumb position, 200ms spring
```

---

### 3.5 Screen Transition Animations
```
Default push (forward): fadeIn + slideInHorizontally(from right, 180ms EaseOut)
Back (pop): fadeOut + slideOutHorizontally(to right, 180ms EaseOut)
Bottom sheet: slideInVertically(from bottom) + spring(dampingRatio=0.8, stiffness=Medium)
Overlay (modal): fadeIn(180ms) + scaleIn(from 0.95, 180ms EaseOut)
Form step advance: slideInHorizontally(from right) + fadeIn, 200ms
Form step back: slideInHorizontally(from left) + fadeIn, 200ms
```

---

### 3.6 Animation Tokens
```kotlin
// Keyframe animations (implement with InfiniteTransition or Animatable)

// floatY: floating logo on welcome screen
// 0%,100%: translateY(0); 50%: translateY(-9dp)
// Duration: 4000ms, EaseInOut, infinite

// pulse: notification dot, map pin
// 0%,100%: alpha 1.0; 50%: alpha 0.42
// Duration: 2000ms, linear, infinite

// gGlow: FAB add button
// 0%,100%: shadow spread 16dp DarkPriR
// 50%: shadow spread 32dp DarkPriR + 60dp rgba(46,204,138,0.12)
// Duration: 3000ms, EaseInOut, infinite

// bounce: small icon celebration
// 0%: scale(0.82) rotate(-15°); 60%: scale(1.12) rotate(4°); 100%: scale(1.0)
// Duration: 400ms, Spring

// shimmer: loading skeleton
// 0%: backgroundPosition -500dp; 100%: +500dp
// Duration: 1500ms, EaseInOut, infinite
```

---

### 3.7 Icons

All icons are custom-drawn SVG paths implemented as `ImageVector` in `ui/icons/NescoIcons.kt`. Do not use Material Icons for these — the designs are custom-stroked (strokeWidth = 1.8dp default, 2.5dp for confirmation checkmarks).

| Name | Usage | Path Description |
|---|---|---|
| `Home` / `House` | Nav, hosting card | Standard house outline |
| `Building` | Apartment type | Multi-story building with windows + door |
| `Clock` | Hourly listing | Circle + hands at 2:00 |
| `Door` | Room/studio type | Door with handle + line |
| `Wifi` | Amenity | 3-arc wifi signal |
| `Snow` | AC | Snowflake (6-point star arrows) |
| `Zap` | Generator | Lightning bolt polygon |
| `Drop` | Water | Teardrop shape |
| `Flame` | Gas/geyser | Flame outline |
| `Parking` | Parking | Rounded rect + P letter |
| `Lift` | Elevator | Rounded rect + up arrow with base line |
| `Leaf` | Balcony/eco | Organic leaf + stem |
| `Cctv` | Security camera | Camera body + lens |
| `Wash` | Laundry | Front-load washer outline |
| `Paw` | Pet friendly | 4 toes + main pad |
| `Hospital` | Nearby hospital | Rounded rect + plus cross |
| `Cart` | Nearby mart | Shopping cart with 2 wheels |
| `School` | Nearby school | Open book outline |
| `Bank` | Bank/ATM | Pillars + triangle roof |
| `Star` | Rating, AI | 5-point star polygon |
| `Check` / `Ok` | Selection, confirm | Checkmark path |
| `X` | Deselect, close | Two crossing lines |
| `Users` | Multiple people | Two overlapping heads |
| `Map` | Nav, map | Folded map polygon |
| `Chat` | Nav, messaging | Speech bubble |
| `Heart` / `Hrt` | Save/favourite | Heart outline or filled |
| `User` | Nav, profile | Head + shoulders outline |
| `Bell` | Notifications | Bell shape + clapper |
| `Search` / `Srch` | Search bar | Circle + handle |
| `Back` | Navigation | Left arrow with shaft |
| `Plus` | Add FAB | Cross (+ sign) |
| `Filter` / `Filt` | Filter button | 3 horizontal lines tapered |
| `Pin` | Location | Teardrop + inner circle |
| `Share` / `Shr` | Share action | 3 circles + 2 lines |
| `Bed` | Bedrooms | Bed outline |
| `Bath` | Bathrooms | Bathtub with feet |
| `Send` | Chat send | Paper plane |
| `Camera` / `Cam` | Photo upload | Camera body + lens circle |
| `Chevron` | List arrows | Single right-facing > |
| `Grid` | Grid view | 4 squares 2×2 |
| `List` / `Lst` | List view | Lines + dots |
| `Eye` | Show password | Eye + iris |
| `Bookmark` | Save request | Ribbon bookmark |
| `FileText` | Documents | Page with lines |
| `Shield` | Security | Shield outline |
| `Lightbulb` | AI/tips | Bulb with base + rays |
| `Package` / `Pkg` | Subscription | 3D box outline |
| `Sun` | Light mode | Circle + 8 rays |
| `Moon` | Dark mode, mosque | Crescent |
| `Mail` | Email | Envelope + flap |
| `Lock` | Password | Padlock closed |
| `LogOut` | Sign out | Door with arrow |

All icons: `strokeLinecap = Round`, `strokeLinejoin = Round`, default size 22dp.

---

## 4. Architecture

### Clean Architecture — 4 Layers
```
app/
├── data/           ← Firestore implementations, DTOs, mappers
├── domain/         ← Entities, Repository interfaces, UseCases (logic-only)
├── presentation/   ← ViewModels, UI State, Composables
└── di/             ← Koin modules
```

### Layer Rules
- `domain/` has **zero Android imports**. No Context. No Firebase. No Coroutines (only `suspend` functions allowed).
- `data/` implements `domain/` interfaces. All Firestore calls use `.await()`.
- `presentation/` imports only `domain/` entities and calls repository interfaces via ViewModel.
- Changing from Firestore to REST API = **only `*RepositoryImpl.kt` files change**.

### ViewModel Pattern
```kotlin
class HomeViewModel(
    private val listingRepo: ListingRepository,
    private val requestRepo: TenantRequestRepository,
    private val sliderRepo: SliderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadListings(city: String, filters: ListingFilters) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            listingRepo.getListings(city, filters).onSuccess { listings ->
                _uiState.update { it.copy(listings = listings, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
```

---

## 5. Package Structure

```
com.nesco.app/
├── data/
│   ├── remote/
│   │   ├── dto/
│   │   ├── mapper/
│   │   └── repository/
│   └── local/
│       └── prefs/
├── domain/
│   ├── entity/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── auth/
│   ├── onboarding/
│   ├── home/
│   ├── listing/
│   │   ├── form/
│   │   └── detail/
│   ├── request/           ← NEW: Tenant request form + detail
│   │   ├── form/
│   │   └── detail/
│   ├── map/
│   ├── chat/
│   ├── notifications/
│   ├── packages/
│   ├── mylistings/
│   ├── myrequests/        ← NEW: My posted requests
│   ├── settings/
│   ├── feedback/
│   └── shared/
│       ├── components/    ← All reusable Composables (Section 3.4)
│       ├── theme/         ← Color.kt, Type.kt, Theme.kt, Shape.kt
│       └── icons/         ← NescoIcons.kt (all custom icons)
├── di/
│   └── modules/
└── util/
    ├── extension/
    ├── connectivity/
    ├── compression/
    └── fcm/               ← FCM token management
```

---

## 6. Module: Authentication

### 6.1 Screens

#### Screen: Splash / Entry
- **Route:** `splash`
- Displays the NesCo logo with `floatY` animation (Section 3.6).
- App background: `DarkBg0` / `LightBg0`.
- Checks in order:
  1. Internet connectivity. If offline → `NoInternetScreen`.
  2. App version vs Firestore `config/app`. Force update if required.
  3. Firebase Auth state. If authenticated → check `isEmailVerified` → navigate to Home. If not → navigate to Welcome.
- No navigation UI elements on this screen.

---

#### Screen: Welcome
- **Route:** `welcome`
- Background: `mesh` gradient (Section 3.2 GradientMesh).
- Centre logo block:
  - 92dp rounded square (30dp corner), gradient DarkPri→DarkSec, Home icon 46dp white, `floatY` animation.
  - Below: Fraunces 50sp SemiBold "Your Space\n**Awaits You**" (second line uses `GradientTextBrush`).
  - Subtext: 15sp DarkT2 "Rooms, homes & shared spaces that truly feel like home — across Pakistan." centred, max 272dp width.
- Two action cards (column, 12dp gap, `su` slide-up animation with 0.1s stagger):
  - **"I'm Looking"**: Search icon, subtitle "Find accommodation that fits your life".
  - **"I'm Hosting"**: Home icon, subtitle "List your space and find the right tenant".
  - Both cards → navigate to `auth/register`.
  - Card design: DarkBg2 fill, 1.5dp DarkBd2 border, 24dp corner. Icon: 54dp rounded square (18dp corner), DarkPriM fill, 1.5dp DarkPriR border.
- Bottom: "Have an account? Sign In" link → `auth/login`.
- Footer: Terms & Privacy links, 11sp DarkT3.

---

#### Screen: Login
- **Route:** `auth/login`
- Back button → `welcome`.
- Title: Fraunces 32sp "Welcome Back" with `su` animation.
- Subtitle: 14sp DarkT2 "Sign in to continue to your account."
- Fields (BoxedInputField style):
  - Email — leading Mail icon.
  - Password — leading Lock icon, trailing Eye toggle.
  - "Forgot Password?" link right-aligned, 12sp DarkPri Bold.
- `PrimaryButton` "Sign In" → validates → `auth/login` ViewModel → navigate to Home.
- "Don't have an account? Sign Up" link → `auth/register`.
- After login: check `isEmailVerified`. If false → `auth/verify_email`. Check `isBlocked` → `auth/blocked`.

---

#### Screen: Register
- **Route:** `auth/register`
- Back button → `welcome`.
- Title: Fraunces 32sp "Create Account".
- Fields: Full Name, Email, Password (with Eye toggle), Confirm Password.
- `PrimaryButton` "Sign Up" → validate → create Firebase user → create Firestore `users/{uid}` doc → navigate to Onboarding.
- "Already have an account? Sign In" link → `auth/login`.

---

#### Screen: Email Verification Pending
- **Route:** `auth/verify_email`
- Illustration + instructions.
- "Resend Email" button — throttled 60 seconds.
- "I've Verified" button → reload Firebase user → if verified: update Firestore `emailVerified: true` → navigate to Onboarding.
- Auto-poll every 5 seconds, max 10 times (`repeatOnLifecycle`).

---

#### Screen: Blocked User
- **Route:** `auth/blocked`
- Message: "Your account has been blocked. For queries contact [admin_email from Firestore config]."
- No navigation out except Sign Out.

---

### 6.2 ViewModels

**`AuthViewModel`**
- `loginWithEmail(email, password)`
- `loginWithGoogle(idToken)`
- `register(name, email, password, confirmPassword)`
- `resendVerification()`
- `checkEmailVerified()`
- `sendPasswordReset(email)`
- `signOut()`
- UI State: `AuthUiState` (sealed: Idle, Loading, Success, Error(message))

### 6.3 Repository Interface
```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun resendVerification(): Result<Unit>
    suspend fun isEmailVerified(): Result<Boolean>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut()
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
}
```

---

## 7. Module: User Profile & Onboarding

### 7.1 Onboarding Flow

Only shown once. Gated by `users/{uid}.onboardingComplete = false`.

#### Step 1: Intent Selection
- "How will you use NesCo?" — two large animated selection cards (same design as AddOverlay cards, Section 3.4.15).
- "I'm Looking" → `defaultMode: "looking"`. "I'm Hosting" → `defaultMode: "hosting"`.
- Saves to SharedPreferences and Firestore. **Both modes always available from main app.**

#### Step 2: Personal Info
- Profile photo (optional) — camera or gallery, compressed <500KB.
- Full Name (pre-filled, editable, maxLength 60).
- Phone number (numeric, maxLength 15).
- Date of Birth (date picker, optional).
- Province dropdown + City dropdown (filtered by province, hardcoded in `strings.xml`).
- "Use current location" → request permission → reverse geocode → auto-fill.
- Saves `deviceLat`, `deviceLng` to Firestore.

#### Step 3: User Type
- Individual vs Business — two selection cards.
- Saves `accountType`.

#### Step 4: How Did You Hear About Us?
- Multi-select chip row: Social Media · Friend / Word of Mouth · Google Search · TV / Radio · Other.
- "Finish" → `onboardingComplete: true` → navigate to Home.

### 7.2 Profile Screen
- **Route:** `profile`
- Profile photo + name + member since + account type badge.
- Mode toggle pill (Looking / Hosting) — changes home screen feed.
- My Listings shortcut (always visible, regardless of mode).
- My Requests shortcut (always visible).
- My Subscriptions.
- Saved Items.
- Notifications.
- Help / Feedback.
- Privacy Policy · Terms (WebView).
- Settings.
- Sign Out.
- Delete Account.

### 7.3 ViewModels & Repositories
Same as v1.0.0 with addition of `TenantRequestRepository` references in UserRepository where needed.

---

## 8. Module: Home & Discovery (Dual Marketplace)

### 8.1 Overview

The home screen is the core of the dual marketplace. It switches between two distinct views based on the user's current mode:

- **Looking mode:** Shows property listings (hosts' listings).
- **Hosting mode:** Shows tenant requests (seekers' requests).

### 8.2 Screen: Home Feed
**Route:** `home`

#### Full Layout (top to bottom):

**Status Bar** (`StatusBar` composable — always at top)

**Header Section (padding 16dp top, 20dp H):**
- Row: Location chip (left) + Notification bell (right)
  - Location: Pin icon (15dp DarkPri) + "Karachi, Sindh" (13sp DarkT1 Medium) + Chevron. Tapping opens city picker.
  - Notification bell: 46dp rounded square (16dp corner), DarkBg2 fill, 1.5dp DarkBd2 border. Red pulsing dot (`.ndot`: 9dp circle, DarkRed, 2.5dp DarkBg1 ring).
- Below: Display heading Fraunces 30sp SemiBold.
  - Looking mode: "Find Your **Next Home**" (second part uses `GradientTextBrush`)
  - Hosting mode: "Find Your **Tenant**"

**Mode Toggle + View Switcher Row (padding 16dp top, 20dp H):**
- Left: `TabPill` — "Looking" / "Hosting" tabs.
- Right: View mode switcher — small pill (DarkBg2 fill, 1.5dp DarkBd border, 14dp corner, 3dp padding).
  - List icon button (18dp).
  - Grid icon button (18dp).
  - Active: DarkBg4 fill, DarkT0. Inactive: transparent, DarkT2. 11dp corner, 7dp V / 11dp H padding.

**Search Bar (padding 18dp top, 20dp H):**
- Full-width tappable row (NOT a real text input — opens bottom sheet on tap).
- DarkBg2 fill, 1.5dp DarkBd2 border, 18dp corner, 14dp V / 16dp H padding.
- Left: Search icon (18dp DarkT2).
- Centre: Placeholder text (14sp DarkT3):
  - Looking mode: "Area, property type, budget…"
  - Hosting mode: "Search tenant requests..."
- Right: Filter pill — DarkPriM fill, 1dp DarkPriR border, 11dp corner, 6dp V / 11dp H padding. Filter icon (14dp) + "Filter" text (12sp DarkPri Bold).

**Quick Filter Chips Row (padding 14dp top, 20dp H, horizontal scroll):**
- Looking mode: All · Share · Full Rent · PG · Hourly · Female Only · ≤ 25k
- Hosting mode: All · Immediate · This Month · Student · Family · Professional
- Chip style: standard `.chip`, `.chip.on` when selected.

**Banner Slider (only in Looking mode, padding 24dp top):**
- `HomeBannerSlider` component (Section 3.4.16).
- Margin: 0dp H (slider spans inset of 20dp screen padding).

**Section Header (padding 20dp H, 20dp top):**
- Left: "Nearby Spaces" (Looking) or "Recent Requests" (Hosting) — Fraunces 22sp SemiBold.
- Right: "See all" — 13sp DarkPri Bold, cursor pointer.

**Feed (LazyColumn or 2-col LazyVerticalGrid, padding 20dp H, gap per variant):**
- **List view + Looking mode:** `PropertyCard` (full variant), 16dp gap.
- **Grid view + Looking mode:** `PropertyCard` (compact variant), 2 columns, 12dp gap.
- **List view + Hosting mode:** `TenantRequestCard`, 16dp gap.
- **Grid view + Hosting mode:** `TenantRequestCard` (compact), 2 columns, 12dp gap.
- Skeleton shimmer on load.
- `startAfter` cursor pagination — trigger on scroll-to-end.
- Empty state: illustrated screen with text.

**Structured Search Sheet (Bottom Sheet — full spec):**
Opens when search bar is tapped.
- Drag handle: 36dp × 4dp, DarkBg4, top margin 20dp.
- Title: Fraunces 24sp SemiBold "Smart Search", bottom 20dp.
- **For Looking mode sections:**
  - Property Type: Apartment · House · Room · Studio · Hostel Bed · Portion · PG · Guest Room · Coworking
  - Suitable For: Male Only · Female Only · Family · All
  - Furnished: Furnished · Semi-Furnished · Unfurnished
  - Duration: Daily · Weekly · Monthly
  - Price Range: Slider — PKR 0 to 500,000 (display current range as "PKR X – PKR Y")
  - Bedrooms: 1 · 2 · 3 · 4+
- **For Hosting mode sections:**
  - Looking For: Shared Space · Full Property · Room · Studio · Apartment · House
  - Suitable Tenant: Male · Female · Family · Any
  - Budget: Up to PKR X (slider)
  - Move-In: Immediate · Within 1 Month · Flexible
- "Search" button: `PrimaryButton`, full width, top margin 16dp.
- Chip selection: multi-select (array stored in filter state). Each chip uses `.chip.on` when selected.
- Each section: `SectionLabel` + wrapping chip row, bottom margin 16dp.

**Bottom Navigation:** `BottomNavBar` (always visible, 86dp).

---

### 8.3 Firestore Pagination

- Default page size: 10 items.
- Cursor: `startAfter(lastDocument)`.
- Listings compound indexes: `(city, status, createdAt DESC)`, `(city, status, propertyType, createdAt DESC)`.
- Requests compound indexes: `(city, status, createdAt DESC)`, `(city, intent, createdAt DESC)`.

### 8.4 ViewModels

**`HomeViewModel`**
- `loadListings(city, filters, reset: Boolean)` → `ListingRepository.getListings()`
- `loadRequests(city, filters, reset: Boolean)` → `TenantRequestRepository.getRequests()`
- `loadNextPage()` → paginates
- `applyFilters(filters)` → resets cursor, reloads
- `applySort(sort)` → re-queries
- `loadSliders()` → `SliderRepository.getActiveSliders()`
- `setMode(mode: MarketplaceMode)` → stores, switches feed
- `changeCity(province, city)`
- UI State: `HomeUiState(listings, requests, sliders, mode, loading, error, hasMore, filters, city)`

### 8.5 Unit Tests
- Mode switching: listings loaded in Looking, requests loaded in Hosting
- Pagination: first page, next page, end of list
- Filters: each type applied correctly for both modes
- Empty state triggers
- Slider: only approved shown

---

## 9. Module: Property Listing Form (Hosting)

This is the **11-step wizard** for hosts to list a property.

**Entry point:** AddOverlay → "I'm Hosting"  
**Route:** `listing/form`

### 9.1 Shell Layout

```
Column layout, fills screen, no bottom nav
StatusBar at top
Header row (padding 12dp top, 20dp H):
  Back button (42dp, DarkBg2, 1.5dp DarkBd, 15dp corner, Back icon)
  ProgressStepBar (flex 1, see Section 3.4.10)
  "Save Draft" link (12sp DarkPri Bold)
Content area (flex 1, padding 26dp top, 20dp H, scrollable, hide scrollbar)
  Step title: Fraunces 28sp SemiBold, 1.2 line height, bottom 7dp, su animation
  Step subtitle: 14sp DarkT2, bottom 28dp, su animation (0.05s delay)
  Step content (varies per step)
Footer (padding 16dp top, 20dp H, 34dp bottom, DarkBg0 fill, 1dp DarkBd top border, no overlap):
  PrimaryButton "Continue →" (or "🚀 Publish Listing" on last step)
  Below: "Save as Draft" — 12sp DarkT3, centred, tappable (only on non-last steps)
Step transition: slide left (advance), slide right (back)
```

### 9.2 Steps (11 Total)

#### Step 1: What Are You Offering?
- Title: "What are you offering?"
- 3 large intent cards (column, 12dp gap):
  - **Share Space**: Users icon, "Room, bed or paying guest"
  - **Full Property**: House icon, "Entire apartment or house"
  - **By the Hour**: Clock icon, "Coworking or guest room"
- Card design: DarkBg2 fill, 1.5dp DarkBd2 border/DarkPri selected, DarkPriM bg selected. Icon container: 58dp circle. 28dp icon. Checkmark Ok icon right side when selected.

#### Step 2: Type of Property?
- Title: "Type of property?"
- Single-select chip grid (wrapping):
  - Apartment · House · Duplex · Portion · Room · Studio · Hostel Bed · Penthouse · Farm House · Office Space · Guest Room · Coworking Space
- Chip style: standard chip with leading icon.

#### Step 3: Availability & Duration
- Title: "How can guests stay?"
- Suitable For chips: Male Only · Female Only · Male & Female · Family · All Welcome
- Duration chips: Daily · Weekly · Monthly (+ Hourly if intent = hourly)
- Available From: "Immediately" chip + "Custom Date" (date picker chip). If custom: show DatePicker dialog.
- If hourly: "+ Add Time Slot" button → start/end time pickers. Multiple slots. Available Days chips: Mon Tue Wed Thu Fri Sat Sun.

#### Step 4: Pricing
- Title: "Set your price"
- Price input box (DarkBg2, 1.5dp DarkBd, 22dp corner, 22dp padding):
  - Label: "MONTHLY RENT" (or appropriate duration) — SectionLabel style
  - Input: "PKR" label (28sp Bold DarkPri) + numeric input (34sp Bold DarkT0, transparent bg)
  - Divider: 1dp DarkBd2, margin 14dp V
  - Negotiate toggle row: "Allow negotiation" label + ToggleSwitch
- Duration chips below: Daily · Weekly · Monthly

#### Step 5: Location
- Title: "Where is your property?"
- Map pin drop area: 155dp height, `MapBackground`, centred Pin icon with pulse animation. "Tap to drop pin" hint.
- Province dropdown · City dropdown (filtered) · Area / Neighbourhood (free text)
- Each uses `SectionLabel` + `UnderlineInputField`
- "Use current location" link — requests permission, reverse geocodes.

#### Step 6: Property Details
- Title: "What's included?"
- Bedrooms: Studio · 1 · 2 · 3 · 4+ (chips with icons)
- Bathrooms: 1 · 2 · 3+ (chips)
- Furnished: Furnished (Check icon) · Semi (Check icon) · Unfurnished (X icon)
- Floor number: numeric input
- Additional details: free text area

#### Step 7: Amenities
- Title: "What do you offer?"
- 3-column grid (10dp gap):
  - WiFi · AC · Generator · Water 24h · Gas · Parking · Lift · Balcony · CCTV · Geyser · Laundry · Pet Friendly · Heating · Solar · TV · Fridge · Microwave · Washing Machine · Security Guard · Parking · Rooftop Access · Prayer Room · Common Kitchen · Shared Lounge · Garden / Lawn · Swimming Pool · Smoking Allowed
  - Each tile: 18dp corner, 14dp V / 8dp H padding, centred icon (24dp) + label (11sp below)
  - Selected: DarkPriM fill, DarkPri border, DarkPri icon + text
  - Unselected: DarkBg2 fill, DarkBd border, DarkT2 colour

#### Step 8: What's Nearby?
- Title: "What's nearby?"
- Column of rows (11dp gap):
  - Mosque · Hospital · Mart · School · Bank/ATM · Bus Stop · Restaurant · Petrol Pump · Gym · Park · Pharmacy · Office Area
  - Each row: 13dp/15dp padding, DarkBg2, 1.5dp DarkBd, 16dp corner
    - Icon (22dp DarkT1) + label (14sp DarkT1 flex) + distance pill (12sp DarkPri, DarkPriM bg, 100dp corner) + ToggleSwitch
  - Tapping toggle enables/disables row; when on, distance pill becomes editable (or slider appears below row)

#### Step 9: Add Photos
- Title: "Show them around"
- 2×2 grid (11dp gap):
  - Each slot: 120dp height, 18dp corner, 2dp dashed DarkBd2 border, centred Camera icon + "Add photo" text
  - First slot: DarkPriM fill, "COVER" label (DarkPri, 9sp ExtraBold) top-left
  - Filled slots: show compressed image preview
  - Delete button overlay on filled slots
- Info banner: "🌿 Max 4 photos · Max 1.5MB · Auto-compressed before upload" — DarkPriM fill, 1dp DarkPriR border, 14dp corner, 13dp/15dp padding.

#### Step 10: Listing Summary (AI-Generated)
- Title: "Your listing summary"
- AI summary card (DarkBg2, 1.5dp DarkBd, 18dp corner, 16dp padding):
  - Header row: Star icon (18dp DarkPri) + "AI-Generated Summary" (12sp DarkPri Bold) + "Ready" badge (right, DarkPriM/DarkPri2)
  - Editable `TextField` — 140dp min height, transparent bg, 14sp, 1.72 line height
  - If Gemini unavailable: friendly inline message (not snackbar). Empty field. Manual entry optional.
- "Regenerate" button: `OutlinePrimaryButton` with Star icon.
- Skeleton shimmer on generation.

#### Step 11: Review & Publish
- Title: "Ready to publish?"
- Summary preview card (DarkBg2, DarkBd2 border, 20dp corner):
  - 100dp gradient image area
  - Title + city/type info + price
- Publish slot indicator: DarkPriM/DarkPriR banner, Ok icon + "X of Y publish slots will be used"
- If at limit: warning instead of "Publish Now". "Save as Draft" still available.
- On Publish: `listing.status = "pending_approval"` (if `config/listing.requireApproval = true`) else `"published"`.

### 9.3 ViewModels & Repositories
(Same interfaces as v1.0.0 Section 7 — `ListingFormViewModel`, `ListingRepository`, `StorageRepository`, `GeminiRepository`)

---

## 10. Module: Tenant Request Form (Looking)

This is the **6-step wizard** for seekers to post what they're looking for.

**Entry point:** AddOverlay → "I'm Looking"  
**Route:** `request/form`

### 10.1 Shell Layout

Same shell as Property Listing Form (Section 9.1). Progress: 6 steps. Button: "Continue →" / "🚀 Post Request" on last.

### 10.2 Steps (6 Total)

#### Step 1: What Do You Need?
- Title: "What do you need?"
- 2 intent cards:
  - **Shared Space**: Users icon, "Looking to share a room or apartment"
  - **Full Property**: House icon, "Looking for an entire place"
- Same card design as listing form Step 1.

#### Step 2: Property Type
- Title: "Property type"
- Single-select chip grid:
  - Apartment · House · Room · Studio · Hostel Bed · Coworking
- Chip with icon.

#### Step 3: Your Budget
- Title: "Your Budget"
- Single price input box:
  - "MAX MONTHLY RENT" label
  - PKR prefix + numeric input (34sp Bold)
- No duration selector needed (monthly implied for seekers).

#### Step 4: Where & Radius
- Title: "Where & Radius"
- Area search input (BoxedInputField): Search icon + "e.g. DHA Phase 6" placeholder
- "SEARCH RADIUS: X km" SectionLabel (X updates live as slider moves)
- Map area (200dp height, `MapBackground`, 18dp corner, DarkBd border):
  - `RadiusCircle` component centred (Section 3.4.18)
  - Animates size as slider moves
- Range slider:
  - Min: 1km, Max: 15km, Step: 1km
  - Thumb: 20dp circle, DarkPri fill, 10dp DarkPriR glow shadow
  - Track: 4dp height, DarkBg4 fill; progress portion DarkPri fill
  - Value stored in step state

#### Step 5: Preferences
- Title: "Preferences"
- Minimum Bedrooms chips: Studio · 1+ · 2+ · 3+
- Minimum Bathrooms chips: 1+ · 2+ · 3+
- Furnishing Required chips: Furnished (Check icon) · Semi (Check icon) · Any (Star icon)
- Same chip + label layout as listing form.

#### Step 6: About You
- Title: "About You"
- Single multi-line TextArea (DarkBg2, 1.5dp DarkBd, 18dp corner, 16dp padding):
  - 140dp min height
  - Placeholder: "Tell hosts a bit about yourself, your routine, and exactly what you are looking for..."
  - maxLength: 500
  - 14sp, 1.72 line height, transparent bg

### 10.3 Data Saved On Submit

On "Post Request": creates Firestore `tenantRequests/{requestId}` document (see Section 23).

### 10.4 ViewModels

**`TenantRequestFormViewModel`**
- Holds `TenantRequestFormState` (all step data).
- `advanceStep()` · `goBack()` · `saveStep(data)` · `submitRequest()` · `saveDraft()`
- Draft persisted to DataStore on every step advance.
- `submitRequest()` → `TenantRequestRepository.createRequest(data)`
- UI State: `RequestFormUiState(step, formData, loading, error)`

### 10.5 Repository Interface
```kotlin
interface TenantRequestRepository {
    suspend fun createRequest(request: TenantRequest): Result<String>
    suspend fun updateRequest(requestId: String, request: TenantRequest): Result<Unit>
    suspend fun deleteRequest(requestId: String): Result<Unit>
    suspend fun getRequests(city: String, filters: RequestFilters, cursor: DocumentSnapshot?): Result<List<TenantRequest>>
    suspend fun getRequestById(requestId: String): Result<TenantRequest>
    suspend fun getUserRequests(uid: String): Result<List<TenantRequest>>
    suspend fun getActiveRequestsCount(uid: String): Result<Int>
}
```

### 10.6 Unit Tests
- Each step validation (empty budget, no area)
- Submit creates correct Firestore document
- Draft persistence and restoration
- Step navigation: advance, back, jump to specific step on edit

---

## 11. Module: Listing Detail

**Route:** `listing/{listingId}`

### 11.1 Full Layout (Top to Bottom)

**Image Gallery Header (315dp height):**
- Full-width `HorizontalPager` with listing images (Coil lazy load).
- Fallback: card gradient + emoji watermark if no images.
- `iov` gradient overlay (transparent top → 92% dark bottom).
- "N / 4" page indicator: glassmorphism pill (bottom-right, 12dp corner, 5dp V / 13dp H padding, 12sp Bold).
- Manual page indicator dots (bottom-centre): same style as slider dots.
- Top-left: Back button (42dp, glassmorphism: rgba(4,16,12,0.5), 12dp blur, 1dp green border, 15dp corner).
- Top-right: Heart button + Share button (same glassmorphism style, 42dp each, 9dp gap).

**Content Area (padding 20dp H):**

*Price & Title row (20dp top):*
- Left: Fraunces 26sp SemiBold title (flex 1, 1.2 line height, 12dp right padding).
- Right: Price (23sp Bold DarkPri) + "PKR / month" (11sp DarkT2).

*Badges row (12dp bottom):*
- Intent badge (.bp) · Furnish badge (DarkPriM/DarkPri2) · Suitability badge (.bm) · Negotiable badge (.ba) if applicable.

*Location row (20dp bottom):*
- DarkBg2 fill, 1.5dp DarkBd border, 16dp corner, 12dp V / 14dp H padding.
- Pin icon (DarkPri) + "Area, City, Province" (14sp DarkT1, flex 1) + Map icon button (34dp square, DarkPriM, 11dp corner, 18dp map icon DarkPri).

*Key facts 4-column grid (10dp gap, 24dp bottom):*
- Tiles: DarkBg2, 1.5dp DarkBd, 16dp corner, 12dp V / 6dp H padding, centred.
- Each: icon (20dp DarkT1) + value (14sp Bold) + label (10sp DarkT2 top 2dp).
- Beds · Baths · Floor · Area (sqft).

*About This Space (24dp bottom):*
- Fraunces 20sp SemiBold title, 10dp bottom.
- Expandable text: first 3 lines shown, "Read more ↓" tap expands, "Show less ↑" collapses.
- 14sp DarkT1, 1.72 line height.

*Amenities (24dp bottom):*
- Fraunces 20sp SemiBold title, 12dp bottom.
- 4-column grid (10dp gap).
- Same tile style as key facts: icon + label.
- Available: DarkT1. Unavailable: DarkT3.

*Nearby Places (24dp bottom):*
- Fraunces 20sp SemiBold title, 12dp bottom.
- Column list (8dp gap): each row DarkBg2, 1.5dp DarkBd, 14dp corner, 11dp/14dp padding.
  - Icon (20dp DarkT1) + name (14sp DarkT1, flex) + distance badge (.bp).

*Lister Info Card (24dp bottom):*
- DarkBg2 fill, 1.5dp DarkBd border, 18dp corner, 16dp padding.
- Avatar: 54dp circle, gradient DarkPri→DarkSec, User icon 28dp white.
- Name (15sp Bold) + "Member since [date]" (12sp DarkT2) + star rating (DarkAcc, 12sp).
- "Host" badge (.bb) right side.

*Report link (14sp, DarkT2, centred, tappable) — authenticated only.*

**Sticky Bottom Bar:**
- DarkNav background, 1dp DarkBd top border, 14dp top / 28dp bottom padding, 24dp H padding.
- If not lister: Ghost share button (52dp × 52dp, 16dp corner) + `PrimaryButton` "💬 Message Host" (flex 1).
- If lister: "Edit Listing" + "Unpublish / Publish" buttons side by side.
- If unauthenticated: "💬 Message Host" → redirects to Login.

### 11.2 ViewModels & Repositories
Same as v1.0.0 Section 10.

---

## 12. Module: Tenant Request Detail

**Route:** `request/{requestId}`

### 12.1 Full Layout (Top to Bottom)

**Status Bar + Header (padding 16dp top, 20dp H):**
- Row: Back button (42dp) + Bookmark button (42dp) + Share button (42dp). Bookmark fills DarkPri when saved.

**Profile Block (centred, 24dp bottom):**
- Avatar: 84dp circle, DarkPriM fill, 2dp DarkPri border, User icon 40dp DarkPri.
- Name: Fraunces 24sp SemiBold.
- "Looking for space by {moveIn}" — 13sp DarkT2, top 4dp.

**Budget Banner (24dp bottom):**
- DarkPriM → DarkBg2 gradient fill, 1.5dp DarkPriR border, 20dp corner, 20dp padding.
- Left: "BUDGET UP TO" SectionLabel + price (28sp Bold DarkPri + " PKR/mo" 12sp DarkT2).
- Right: Intent badge (.bp).

**Preferred Area Section (24dp bottom):**
- Fraunces 20sp SemiBold "Preferred Area", 12dp bottom.
- MapBackground container (180dp height, 18dp corner, 1.5dp DarkBd border, clip):
  - `RadiusCircle` (static, non-interactive, sized from `radius` field).
  - Bottom-left info pill: DarkNav bg, 10dp blur, 1dp DarkBd border, 8dp/12dp padding, Pin icon (14dp DarkPri) + area names.
  - Top-right pill: 10sp Bold DarkPri, "{radius} km radius".

**Requirements Section (24dp bottom):**
- Fraunces 20sp SemiBold, 12dp bottom.
- Wrapping chip row (DarkBg2 fill chips): property type, beds, baths, furnish preference, other requirements.

**About the Tenant (24dp bottom):**
- Fraunces 20sp SemiBold, 10dp bottom.
- ""{bio text}"" — 14sp DarkT1, 1.72 line height, italic quotes.

**Sticky Bottom Bar:**
- `PrimaryButton` "💬 Message {firstName}" full width.
- If unauthenticated → redirect to Login.
- If viewer IS the requester → replace with "Edit Request" button.

### 12.2 ViewModels

**`RequestDetailViewModel`**
- `loadRequest(requestId)` → `TenantRequestRepository.getRequestById()`
- `saveRequest(requestId)` → `SavedRepository.saveRequest()`
- `unsaveRequest(requestId)` → `SavedRepository.unsaveRequest()`
- `isSaved(requestId)` → `SavedRepository.isRequestSaved()`
- `isCurrentUserRequester()` — compare auth uid with request.uid

---

## 13. Module: Map View

**Route:** `map`

### 13.1 Screen: Map Listings

- Google Maps Compose full-screen.
- Custom cluster markers (count badge, DarkPri background).
- Individual markers: custom pin with property type icon + abbreviated price.
- On marker tap → `ModalBottomSheet` slides up:
  - 100dp image area (gradient bg if no photo).
  - Title · City · Price · Type badges.
  - "View Details" `PrimaryButton` → listing detail.
  - Sheet dismisses on map tap.
- Fetch listings by bounding box (lat/lng range query, Firestore).
- Geofence circle overlay if active.
- `MapBackground` grid style used for the fallback background before tiles load.
- Mode selector at top: toggle between Listings view and Requests view (shows pins for requests too).

### 13.2 Request Map Pins
When in Requests mode, show circle radius pins for each tenant request (centred on their preferred area). Colour: DarkBlue.

---

## 14. Module: Saved Items

**Route:** `saved`

### 14.1 Screen: Saved Items

- **Two tabs:** "Properties" + "Requests"
- Properties tab: list of saved `PropertyCard` (full variant).
- Requests tab: list of saved `TenantRequestCard`.
- Swipe-to-remove or unsave button on each card.
- Empty state per tab: illustrated empty state with tab-appropriate message.
- Tapping a card → respective detail screen.

### 14.2 Data Model

```
users/{uid}/savedListings/{listingId}    savedAt: timestamp
users/{uid}/savedRequests/{requestId}   savedAt: timestamp
```

### 14.3 Repository Interface Extension
```kotlin
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
```

---

## 15. Module: Chat

### 15.1 Encryption
AES-256-GCM client-side encryption. Key derivation: `PBKDF2(uid_a + uid_b + listingId, salt)`. Salt in Firestore `chats/{chatId}.salt`. Encrypted blobs stored as base64.

### 15.2 Daily Limits
- Free: `config/chat.freeUserDailyLimit` (default 20).
- Paid: `config/chat.paidUserDailyLimit` (default 100).
- Tracked: `users/{uid}/dailyMessageCount/{YYYY-MM-DD}.count`.
- On limit: non-dismissable upgrade bottom sheet.

### 15.3 Screen: Chat List
**Route:** `chats`

```
StatusBar
Padding: 16dp top, 20dp H
Title: Fraunces 28sp "Messages", 20dp bottom

Conversation list (LazyColumn, key = chatId):
  Each item: Row, 14dp V padding, 1dp DarkBd bottom border
    Left: Avatar (52dp circle, gradient, User icon 24dp white)
         + Unread count badge (if > 0): 19dp circle DarkRed, ExtraBold 10sp white, 2.5dp DarkBg0 ring
    Centre:
      Name (15sp Bold) + timestamp (11sp DarkT2) — row
      Listing title (11sp DarkPri Bold) — subtitle
      Last message preview (13sp DarkT2, overflow ellipsis, single line)
    Right: nothing (badge handled on avatar)
  
Empty state: "No conversations yet."
```

### 15.4 Screen: Chat Detail
**Route:** `chat/{chatId}`

```
Column fills screen

Top app bar (DarkNav, 1dp DarkBd bottom, blur):
  StatusBar
  Padding 12dp top, 20dp H, 14dp bottom
  Row: Back button + Avatar (42dp circle gradient) + Name/listing col + Eye icon (col right)
    Name: 15sp Bold
    Listing title: 11sp DarkPri

Messages area (LazyColumn, flex 1, scrollable, reversed bottom-up):
  Messages reversed: newest at bottom
  Key = messageId
  Date separator: inline centred pill (DarkBg2, 1dp DarkBd, 11sp DarkT2)
  
  Sender bubble (.bbl.s):
    Background: gradient DarkPri→DarkSec (135°)
    Text: white, 13.5sp, 1.55 line height
    Max width: 72%
    Align: flex-end
    Corner: 20dp, bottom-right 5dp
    Font-weight: Medium
    Timestamp: 10sp DarkT3, right-aligned, top 4dp
  
  Receiver bubble (.bbl.r):
    Background: DarkBg3
    Border: 1dp DarkBd2
    Text: DarkT0, 13.5sp
    Max width: 72%
    Align: flex-start
    Corner: 20dp, bottom-left 5dp
    Timestamp: 10sp DarkT3, left-aligned, top 4dp
  
  Long-press: context menu (Edit / Delete for sender)
  Deleted: "Message deleted" placeholder

Input bar (sticky bottom, DarkNav, 1dp DarkBd top, blur):
  Padding 12dp top, 16dp H, 30dp bottom
  Row: TextInput + Send button
    Input: DarkBg2 fill, 1.5dp DarkBd2, 18dp corner, 12dp V / 15dp H padding, flex 1
    Send button: 50dp × 50dp, 18dp corner, DarkPri gradient, Send icon white
```

### 15.5 ViewModels & Repository
Same as v1.0.0 Section 12. Chat linked to `listingId` OR `requestId` (both supported in `chats/{chatId}.referenceId` and `chats/{chatId}.referenceType: "listing" | "request"`).

---

## 16. Module: Notifications (FCM)

> ⚠️ OneSignal is NOT used. All push notifications use **Firebase Cloud Messaging (FCM)**.

### 16.1 Android FCM Setup

1. Add `google-services.json` to app module (from Firebase project).
2. Add `com.google.firebase:firebase-messaging:x.x.x` to `build.gradle`.
3. Create `NescoFCMService : FirebaseMessagingService`.
4. On `onNewToken(token)`: save token to Firestore `users/{uid}.fcmToken`.
5. On `onMessageReceived(message)`:
   - If app in foreground: show in-app notification banner (custom Composable overlay, slides down from top, auto-dismisses in 4 seconds).
   - If app in background/killed: system notification. Tapping opens app and navigates to relevant screen based on `data.notifType`.
   - Always: write to Firestore `notifications/{uid}/items/{notifId}`. Set `SharedPreferences hasUnreadNotifications = true`.

### 16.2 Token Management

```kotlin
// In AuthRepositoryImpl, after successful login:
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        firestore.collection("users").document(uid)
            .update("fcmToken", token).await()
    }
}

// NescoFCMService:
override fun onNewToken(token: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    firestore.collection("users").document(uid)
        .update("fcmToken", token)
}
```

### 16.3 Cloud Function: `sendFCMNotification`

**Trigger:** HTTP callable from admin portal or other Cloud Functions.

```typescript
// functions/src/notifications.ts
import * as admin from 'firebase-admin';

export const sendFCMNotification = functions.https.onCall(async (data, context) => {
    const { fcmToken, title, body, notifType, extraData } = data;
    
    const message: admin.messaging.Message = {
        token: fcmToken,
        notification: { title, body },
        data: { notifType, ...extraData },
        android: {
            priority: 'high',
            notification: {
                channelId: 'nesco_default',
                color: '#2ECC8A',
            }
        }
    };
    
    await admin.messaging().send(message);
    
    // Also write to Firestore notification log
    await admin.firestore()
        .collection('notifications')
        .doc(data.targetUid)
        .collection('items')
        .add({
            notifType, title, body,
            data: extraData,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        });
});
```

### 16.4 Notification Channels (Android)

Declare in `AndroidManifest.xml` and create in `Application.onCreate()`:
```kotlin
val channel = NotificationChannel(
    "nesco_default",
    "NesCo Notifications",
    NotificationManager.IMPORTANCE_HIGH
).apply {
    description = "General NesCo notifications"
    enableLights(true)
    lightColor = 0xFF2ECC8A.toInt()
}
```

### 16.5 Notification Types

| Type | Title | Navigate To |
|---|---|---|
| `subscription_approved` | "Subscription Approved 🎉" | Packages screen |
| `subscription_rejected` | "Subscription Update" | Packages screen |
| `subscription_upgraded` | "Congratulations! 🎊 You've been upgraded!" | Packages screen |
| `subscription_expiring` | "Your plan expires soon" | Packages screen |
| `listing_approved` | "Listing Approved ✅" | Listing Detail |
| `listing_rejected` | "Listing Update" | Listing Detail |
| `listing_blocked` | "Listing Blocked" | Listing Detail |
| `listing_deleted_by_admin` | "Listing Removed" | My Listings |
| `request_deleted_by_admin` | "Request Removed" | My Requests |
| `new_message` | "New message from [name]" | Chat Detail |
| `account_blocked` | "Account Notice" | Blocked screen |
| `slider_approved` | "Your featured slide is live!" | My Listings |
| `gemini_key_failed` | "⚠️ Gemini Key Alert" | Admin: Gemini Logs |
| `new_listing_pending` | "New listing pending review" | Admin: Listings |
| `new_request_pending` | "New request posted" | Admin: Requests |
| `new_subscription_request` | "New subscription request" | Admin: Subscriptions |
| `new_feedback` | "New feedback received" | Admin: Feature Requests |

### 16.6 Admin Broadcast

- Admin portal sends broadcast to all users via FCM using **topic messaging**:
  - All users subscribed to topic `"all_users"` at login time:
    ```kotlin
    FirebaseMessaging.getInstance().subscribeToTopic("all_users")
    ```
  - Cloud Function `broadcastToAll(title, body)` calls:
    ```typescript
    admin.messaging().sendToTopic("all_users", { notification: { title, body } })
    ```

### 16.7 In-App Notification Screen

**Route:** `notifications`

```
StatusBar
Padding: 16dp top, 20dp H
Title: Fraunces 28sp "Notifications"

LazyColumn (key = notifId):
  Each item: Row, padding 14dp V, 16dp H, 1dp DarkBd bottom
    Icon: 40dp circle, type-based icon (see table above)
    Text block: title (14sp SemiBold) + body (13sp DarkT2, 2 lines max) + timestamp (11sp DarkT3)
    Tap: navigate per notifType

Empty state: Bell icon, "No notifications yet."
Red dot on bell cleared when screen opened (SharedPreferences hasUnreadNotifications = false)
```

---

## 17. Module: Packages & Subscriptions

(Unchanged from v1.0.0 Section 14 — no functional changes. Replace all OneSignal calls with FCM `sendFCMNotification` Cloud Function calls.)

**Key payment flow:** Bank transfer → WhatsApp screenshot → Admin approves → FCM notification sent.

---

## 18. Module: My Listings & My Requests (Dashboard)

### 18.1 Screen: My Listings
**Route:** `my_listings`

```
StatusBar
Header row (16dp top, 20dp H):
  "My Listings" — Fraunces 28sp SemiBold
  "New +" button — PrimaryButton, "New" + Plus icon, 10dp V / 18dp H padding, 13sp

Publish slot banner (DarkPriM fill, DarkPriR border, 18dp corner, 14dp/16dp padding):
  Row: "Publish Slots" (13sp DarkT1) + "X / Y" (13sp DarkPri Bold)
  ProgressBar: 4dp height, DarkBg4 track, DarkPri→DarkPri2 fill, animated width
  Below: "Free plan · Upgrade →" (11sp DarkT2, DarkPri link)

Tab row (horizontal scroll chips, 20dp bottom):
  Published · Drafts · Pending · Rejected · Blocked
  Active: .chip.on style

Tab content:
```

**Published tab:**
- Horizontal card per listing (DarkBg2, 1.5dp DarkBd, 20dp corner, flex row):
  - Left image area (88dp wide, listing gradient, emoji watermark at 45% opacity).
  - Right content (14dp padding):
    - Title (13sp Bold, overflow ellipsis).
    - City · type (12sp DarkT2).
    - "● Live" badge (.bp) + price (13sp DarkPri Bold).
    - Action row (top 11dp): "Edit" GhostButton + "Unpublish" GhostButton.

**Drafts tab:**
- Horizontal card + "Continue" PrimaryButton right.
- Progress bar showing completion %.

**Pending tab:**
- Centred card: DarkPriM fill, 20dp corner. Hourglass emoji (44sp) + "Awaiting Approval" (Bold) + "Usually reviewed within a few hours." (13sp DarkT2).

**Rejected tab:**
- DarkRedM fill, 1dp DarkRed/22% border, 20dp corner.
- X emoji (28sp) + listing title + Rejected badge (.br).
- Admin note (13sp DarkT1, DarkRedM bg, 11dp/13dp padding, 11dp corner).
- "Edit & Resubmit" PrimaryButton.

**Blocked tab:**
- Same as Rejected but admin reason shown with "Contact Support" link.

---

### 18.2 Screen: My Requests
**Route:** `my_requests`

Same shell as My Listings.

**Tabs:** Active · Pending · Closed · Rejected

**Active tab:**
- `TenantRequestCard` with edit + close buttons.
- "X / Y request slots" banner (same style as listing slots banner).

**Pending tab:**
- Awaiting admin review (if `requireRequestApproval` is enabled in config).

**Closed/Rejected tabs:** appropriate status cards.

**"New +" button** → navigates to Tenant Request Form.

---

### 18.3 Combined Mode Logic

In Profile, the "My Listings" and "My Requests" are always both accessible as separate shortcuts, regardless of current mode. The Home screen mode only affects the discovery feed.

---

## 19. Module: Settings & Account

**Route:** `settings`

- Theme toggle: Light / Dark / System Default (saved to SharedPreferences + applied immediately via `isSystemInDarkTheme()` and manual override).
- Notification preferences (per type toggle, stored locally).
- Privacy Policy · Terms & Conditions (WebView).
- App version display.
- Delete Account (confirmation dialog, cascades via Cloud Function).
- Sign Out.

---

## 20. Module: Feedback & Feature Requests

**Route:** `feedback`

- Title field (maxLength 100, required) + Description (maxLength 1000, required).
- Submit → Firestore `featureRequests/{requestId}` → FCM push to admin `new_feedback`.
- Success snackbar: "Thank you! Your feedback has been submitted."

---

## 21. Module: Force Update & Connectivity

### Force Update
- On app open: fetch `config/app`. Compare `BuildConfig.VERSION_CODE`.
- `isForceUpdate = true` → non-dismissable dialog, "Update Now" → Play Store.
- `isForceUpdate = false` → dismissable dialog with "Later" option.

### No Internet
- `NetworkCallback` in `Application` class.
- Full-screen `NoInternetScreen` composable overlay on top of current screen when offline.
- Auto-dismisses and reloads when connectivity restores.
- No offline cache.

---

## 22. Module: Admin Web Portal

> The admin portal uses **the same visual design language as the Android app** — same colour palette (dark theme default, light mode toggle available), same typography (Fraunces + Plus Jakarta Sans via Google Fonts), same border radii, same chip/card/badge styles, and same gradient buttons. It is a "jampackaged" (comprehensive, feature-dense) dashboard.

### 22.1 Auth
- Firebase Authentication (email/password).
- Login page at `/login`. Register at `/register-admin` (localhost only).
- Post-login: check `users/{uid}.userType = "admin"`. If not admin → sign out + error toast.
- Header: app logo + admin name + sign out button.

### 22.2 Layout
```
Left sidebar (240dp, dark/light bg1, border-right bd):
  Logo + app name (top, 20dp padding)
  Navigation links (icon + label, same .ni style from bottom nav)
  Theme toggle at bottom (Sun/Moon icon)

Main content area (flex, bg0, padding 24dp)
  Page header: page title (Fraunces 28sp) + action button(s) right
  Content below
```

### 22.3 Dashboard Page (`/`)

**KPI Cards Row (4 columns, responsive):**
Each card: bg2 fill, 1.5dp bd border, 18dp corner, 20dp padding.
- Total Users (Users icon, blue accent)
- Total Listings (Building icon, primary green)
- Total Requests (Search icon, blue)
- Active Subscriptions (Package icon, accent yellow)
- Pending Requests (Clock icon, red/warning)
- MRR — PKR (Bank icon, primary green)
- Expiring Soon (Bell icon, accent orange)

**Charts Row (2 columns):**
- Users over time — line chart (Recharts LineChart, last 30 days, DarkPri stroke)
- Listings by status — bar chart (DarkPri published, DarkAcc pending, DarkRed blocked, DarkT2 draft)
- Subscribed vs Free — pie chart (DarkPri vs DarkBg3)
- MRR trend — line chart (monthly)

**Expiring Soon Table:**
- Columns: Name · Email · Package · Expiry Date · Days Left
- Row highlight: DarkRedM fill if ≤ 3 days remaining

**Controls:**
- "Sync Subscriptions" button (GhostButton with confirmation dialog). Shows last sync timestamp.
- "📢 Broadcast Push Notification": title + body inputs + "Send to All" PrimaryButton.

---

### 22.4 Users Page (`/users`)

**Table:** paginated, sortable.
- Columns: Avatar · Name · Email · Account Type · Status · Package · Joined Date · Actions

**Search:** email or name — live filter.

**Row Actions:** "View" → User Detail modal/page.

**User Detail:**
- Profile info card (same profile card style as Android).
- Listing count + Request count + subscription info.
- **Block / Unblock toggle** — when blocking, optional reason text input. Reason stored in `users/{uid}.blockReason`. Sends FCM `account_blocked` notification if blocking.
- **Delete Account** button (with confirmation) → calls Cloud Function `deleteUserAccount`.

---

### 22.5 Listings Page (`/listings`)

**Table:** paginated.
- Columns: Cover thumbnail · Title · Type · City · Owner Email · Status · Created Date · Actions

**Filters:** Status dropdown (All / Published / Pending / Rejected / Blocked / Draft).

**Row Actions:**
- "View": opens listing detail panel (right drawer or modal).
- "Approve": sets `status = "published"`. Sends FCM `listing_approved` to owner.
- "Reject": shows reason input dialog. Sets `status = "rejected"`. Sends FCM `listing_rejected` with reason.
- **"Delete"**: confirmation dialog with mandatory notification message field.
  - Admin enters a message explaining why (e.g. "This listing violates our content policy").
  - On confirm: deletes listing document + Firebase Storage files → sends FCM `listing_deleted_by_admin` with the admin's message as notification body.
- "Block": sets `status = "blocked"`. Sends FCM `listing_blocked`.

**Global setting:** Approval toggle — when OFF, new listings auto-publish without review.

---

### 22.6 Requests Page (`/requests`)

**NEW — Mirrors the Listings page but for Tenant Requests.**

**Table:** paginated.
- Columns: Requester Name · Email · Intent (Share/Full Rent) · Area · Budget · Status · Created Date · Actions

**Filters:** Status dropdown (All / Active / Pending / Rejected / Closed).

**Row Actions:**
- "View": opens request detail panel.
- "Approve": sets `status = "active"`. Sends FCM notification to requester.
- "Reject": reason input → sets `status = "rejected"` → FCM notification.
- **"Delete"**: confirmation dialog with mandatory notification message field (same UX as Listings delete).
  - Admin writes reason message.
  - On confirm: deletes request document → sends FCM `request_deleted_by_admin` with admin's message.
- "Close": marks request as closed (not deleted — requester can re-open).

---

### 22.7 Subscriptions Page (`/subscriptions`)

**Tabs:** Pending · Active · Expired

**Pending tab:**
- Each row: User name · Email · Package · Requested At · Approve / Reject buttons.
- Approve: confirm dialog → update request `status = "approved"` → update `users/{uid}` package fields → FCM `subscription_approved`.
- Reject: reason input → FCM `subscription_rejected`.
- **Free Upgrade button** (per user): select package dropdown → sets package without payment → FCM `subscription_upgraded`.

---

### 22.8 Packages Page (`/packages`)

- List of packages in card grid (same card style, bg2 fill).
- Create Package form: Name · Description · Price (PKR) · Duration (days) · Max Published · Max Total · Daily Message Limit · Business Package toggle · Allows Slider toggle.
- Edit / Delete with confirmation.
- Active/Inactive toggle per package.

---

### 22.9 Feature Requests Page (`/feedback`)

- Table: Title · User · Date · Status (Open / Closed).
- View Detail panel: full title + description + user info.
- "Mark Closed" button. "Delete" with confirmation.

---

### 22.10 Sliders Page (`/sliders`)

- Table: User · Linked Listing · Image preview · Status · Submitted At.
- Preview image thumbnail.
- "Approve" → `status = "approved"` → FCM `slider_approved`. "Reject" button.
- Only approved sliders appear in app home carousel.

---

### 22.11 Reports Page (`/reports`)

- Table: Reporter · Reported (user or listing) · Type · Reason · Date · Status.
- View Detail: full context panel.
- Actions: Dismiss · Block User · Block Listing (each with confirmation dialog + FCM notification).

---

### 22.12 App Config Page (`/config`)

Editable fields (save to Firestore `config/` collection):
- Free listing max published / max total
- Max images per listing
- Free user daily message limit
- Paid user daily message limit
- Expiry warning days
- Admin email
- WhatsApp number (payment proof)
- Bank account title / IBAN / bank name
- Listing form style (wizard / scroll)
- Listing approval toggle
- Request approval toggle (**NEW**)
- App version: latestBuildNumber · minBuildNumber · isForceUpdate

---

### 22.13 Gemini Key Logs Page (`/gemini-logs`)

- Table: Timestamp · Key Number (1–4) · Error Message.
- Cleared by admin (bulk delete action).
- FCM push sent to admin on each key failure (from Cloud Function).

---

### 22.14 Notifications Log Page (`/notification-log`)

- All notifications ever sent. Columns: Title · Body · Type · Recipient · Timestamp.
- Read-only audit log.

---

## 23. Firestore Data Models

```
users/{uid}
  uid: string
  name: string
  email: string
  phone: string | null
  photoUrl: string | null
  emailVerified: boolean
  isBlocked: boolean
  blockReason: string | null
  userType: "user" | "admin"
  accountType: "individual" | "business"
  defaultMode: "looking" | "hosting"
  onboardingComplete: boolean
  province: string
  city: string
  deviceLat: double | null
  deviceLng: double | null
  referralSource: string[]
  fcmToken: string | null                  ← NEW (replaces oneSignalPlayerId)
  currentPackageId: string | null
  packageName: string | null
  packageExpiryDate: timestamp | null
  maxPublishedListings: int (default 2)
  maxTotalListings: int (default 4)
  dailyMessageLimit: int (default 20)
  allowsSlider: boolean
  maxPublishedRequests: int (default 2)    ← NEW
  maxTotalRequests: int (default 4)        ← NEW
  createdAt: timestamp

listings/{listingId}
  uid: string
  ownerName: string
  intent: "share" | "fullRent" | "hourly"
  propertyType: string
  propertySubType: string | null
  suitableFor: string
  duration: "daily" | "weekly" | "monthly" | "hourly"
  timeSlots: [{start: string, end: string}] | null
  availableDays: string[] | null
  availableFrom: timestamp | null
  availableImmediately: boolean
  price: int
  isNegotiable: boolean
  province: string
  city: string
  area: string
  fullAddress: string
  lat: double
  lng: double
  bedrooms: int
  bathrooms: int
  furnished: "furnished" | "semi" | "unfurnished"
  floor: int | null
  totalFloors: int | null
  additionalDetails: string | null
  amenities: string[]
  nearbyPlaces: [{category: string, distanceMeters: int}]
  imageUrls: string[]
  description: string | null
  status: "draft" | "published" | "pending_approval" | "rejected" | "unpublished" | "blocked"
  rejectionReason: string | null
  adminDeleteMessage: string | null        ← NEW
  hasActiveSlider: boolean
  createdAt: timestamp
  updatedAt: timestamp

tenantRequests/{requestId}                 ← NEW COLLECTION
  uid: string
  requesterName: string
  intent: "share" | "fullRent"
  propertyType: string
  budgetMax: int
  province: string
  city: string
  preferredAreas: string[]
  radiusKm: int
  minBedrooms: int
  minBathrooms: int
  furnishingRequired: "furnished" | "semi" | "any"
  moveInDate: "immediate" | "within_month" | "flexible" | timestamp
  bio: string
  status: "active" | "pending_approval" | "rejected" | "closed"
  rejectionReason: string | null
  adminDeleteMessage: string | null
  createdAt: timestamp
  updatedAt: timestamp

chats/{chatId}
  listingId: string | null
  requestId: string | null               ← NEW
  referenceType: "listing" | "request"   ← NEW
  participants: string[]
  salt: string
  createdAt: timestamp
  lastMessage: string (encrypted)
  lastMessageAt: timestamp

chats/{chatId}/messages/{msgId}
  senderUid: string
  content: string (encrypted)
  editedContent: string | null
  isEdited: boolean
  isDeleted: boolean
  createdAt: timestamp
  editedAt: timestamp | null

subscriptionRequests/{requestId}
  uid: string
  packageId: string
  packageName: string
  status: "pending" | "approved" | "rejected"
  isExtension: boolean
  createdAt: timestamp
  processedAt: timestamp | null
  processedBy: string | null

packages/{packageId}
  name: string
  description: string
  price: int
  durationDays: int
  maxPublishedListings: int
  maxTotalListings: int
  maxPublishedRequests: int              ← NEW
  dailyMessageLimit: int
  isBusinessPackage: boolean
  allowsSlider: boolean
  isActive: boolean
  createdAt: timestamp

sliders/{sliderId}
  uid: string
  listingId: string
  imageUrl: string
  status: "pending_approval" | "approved" | "rejected"
  createdAt: timestamp
  approvedAt: timestamp | null

reports/{reportId}
  reporterUid: string
  reportedUid: string | null
  listingId: string | null
  requestId: string | null               ← NEW
  type: "user" | "listing" | "request"   ← UPDATED
  reason: string
  status: "open" | "resolved"
  createdAt: timestamp

featureRequests/{requestId}
  uid: string
  title: string
  description: string
  status: "open" | "closed"
  createdAt: timestamp

notifications/{uid}/items/{notifId}
  notifType: string
  title: string
  body: string
  data: map
  createdAt: timestamp

users/{uid}/savedListings/{listingId}
  savedAt: timestamp

users/{uid}/savedRequests/{requestId}    ← NEW
  savedAt: timestamp

users/{uid}/dailyMessageCount/{YYYY-MM-DD}
  count: int

geminiKeyLogs/{logId}
  keyNumber: int (1-4)
  errorMessage: string
  timestamp: timestamp

config/app
  latestBuildNumber: int
  minBuildNumber: int
  isForceUpdate: boolean

config/listing
  formStyle: "wizard" | "scroll"
  maxImages: int
  requireApproval: boolean

config/request                           ← NEW
  requireApproval: boolean
  maxActiveRequests: int

config/chat
  freeUserDailyLimit: int
  paidUserDailyLimit: int

config/subscription
  expiryWarningDays: int

config/bankAccount
  accountTitle: string
  iban: string
  bankName: string

config/admin
  adminEmail: string
  whatsappNumber: string
  adminFcmToken: string                  ← NEW (for admin-targeted notifications)

config/sync
  lastSyncTimestamp: timestamp
```

---

## 24. Remote Config & Gemini Integration

### 24.1 Keys
- Stored in Firebase Remote Config: `gemini_key_1` through `gemini_key_4`.
- Fetched on app launch with 1-hour minimum interval.

### 24.2 Key Rotation Logic

```kotlin
class GeminiRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig,
    private val firestore: FirebaseFirestore,
    private val fcmService: FCMNotificationService
) : GeminiRepository {
    private fun getKeys() = listOf(
        remoteConfig.getString("gemini_key_1"),
        remoteConfig.getString("gemini_key_2"),
        remoteConfig.getString("gemini_key_3"),
        remoteConfig.getString("gemini_key_4")
    ).filter { it.isNotBlank() }

    override suspend fun generateDescription(data: ListingFormData): Result<String> {
        getKeys().forEachIndexed { index, key ->
            try {
                val result = callGeminiApi(key, data)
                return Result.success(result)
            } catch (e: Exception) {
                logKeyFailure(index + 1, e.message ?: "Unknown")
            }
        }
        return Result.failure(Exception("all_keys_exhausted"))
    }

    private suspend fun logKeyFailure(keyNumber: Int, error: String) {
        firestore.collection("geminiKeyLogs").add(mapOf(
            "keyNumber" to keyNumber,
            "errorMessage" to error,
            "timestamp" to FieldValue.serverTimestamp()
        )).await()
        // FCM push to admin via Cloud Function
        fcmService.notifyAdmin("gemini_key_failed", "⚠️ Gemini Key $keyNumber Failed", error)
    }
}
```

---

## 25. Firebase Cloud Messaging (FCM)

### 25.1 Setup Checklist
- [ ] Add `google-services.json` (contains FCM config).
- [ ] `firebase-messaging` dependency in `build.gradle`.
- [ ] `NescoFCMService` declared in `AndroidManifest.xml` with `MESSAGING_EVENT` intent filter.
- [ ] Notification channel `nesco_default` created in `Application.onCreate()`.
- [ ] Device FCM token saved to `users/{uid}.fcmToken` on login and token refresh.
- [ ] Admin FCM token saved to `config/admin.adminFcmToken` separately for targeted admin alerts.
- [ ] All users subscribed to FCM topic `"all_users"` at login.
- [ ] Cloud Function `sendFCMNotification` deployed (see Section 16.3).

### 25.2 Notification Payload Contract

All notifications follow this data payload structure so navigation works correctly on tap:
```json
{
  "notifType": "listing_approved",
  "listingId": "abc123",
  "requestId": null,
  "chatId": null,
  "targetUid": "user_uid"
}
```

The `NescoFCMService.onMessageReceived` reads `notifType` and routes navigation accordingly using the table in Section 16.5.

---

## 26. Cloud Functions

### 26.1 `syncSubscriptions`
- Trigger: Firebase Cloud Scheduler every 5 days + manual admin trigger.
- Logic: expire users past `packageExpiryDate`, reset to free tier, unpublish excess listings, send FCM expiry notifications.
- Also: for expired users with active requests beyond `maxPublishedRequests` → close oldest excess requests.

### 26.2 `deleteUserAccount`
- Trigger: HTTP callable from Android app.
- Cascades: delete listings + storage + requests + chats + saved items + notifications + user doc + Firebase Auth.

### 26.3 `sendFCMNotification`
- Trigger: HTTP callable from admin portal and other functions.
- Sends FCM via `admin.messaging().send(message)`.
- Writes to `notifications/{uid}/items/`.

### 26.4 `broadcastToAll`
- Trigger: HTTP callable from admin portal.
- Uses `admin.messaging().sendToTopic("all_users", { notification: { title, body } })`.

### 26.5 `onListingDeleted` (Admin Action Hook)
- Trigger: Firestore `onDelete` on `listings/{listingId}` OR explicit callable.
- Deletes Firebase Storage files at `listings/{uid}/{listingId}/`.
- Sends FCM `listing_deleted_by_admin` to listing owner with admin message.

### 26.6 `onRequestDeleted` (Admin Action Hook) ← NEW
- Trigger: HTTP callable from admin portal delete action.
- Deletes `tenantRequests/{requestId}`.
- Sends FCM `request_deleted_by_admin` to requester with admin message.

---

## 27. Performance & Optimisation Rules

### 27.1 Jetpack Compose
- All data classes in Compose: `@Stable` or `@Immutable`.
- `remember { }` for expensive computations.
- `derivedStateOf { }` for computed values.
- `LazyColumn` / `LazyVerticalGrid` / `LazyRow`: always `key { item.id }`.
- Event handler lambdas: `remember { {} }`.
- `rememberUpdatedState` for callbacks in effects.
- No state reads inside LazyColumn item blocks beyond what's needed.
- Paging: `androidx.paging` or manual cursor pagination — never load all data at once.

### 27.2 Images
- Coil `AsyncImage` with `crossfade(true)`.
- List cards: 300×200dp max.
- Detail gallery: `Size.ORIGINAL`.
- Disk cache + LRU memory cache enabled.

### 27.3 Maps
- Marker clustering (Google Maps Utility Library).
- Fetch by bounding box only.
- `remember` all map state.
- `DisposableEffect` for map lifecycle.
- Cluster manager updates only on data change.

### 27.4 Memory Leaks
- All `Flow` collections: `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- No anonymous inner classes holding Context.
- ViewModel never holds Activity/View reference.
- `DisposableEffect` for subscriptions.
- `rememberCoroutineScope()` for Composable-scoped coroutines.

### 27.5 Animations
- Screen transitions: `fadeIn + slideInVertically` or `fadeIn + slideInHorizontally`, 180ms EaseOut.
- Skeleton shimmer: `InfiniteTransition`.
- Bottom sheet: `ModalBottomSheet` with `AnimatedVisibility`.
- Chip selection: `animateColorAsState`, 100ms.
- FAB: `AnimatedVisibility` with scale + fade.
- Banner slider: `HorizontalPager` + `LaunchedEffect` 3200ms auto-scroll.
- Form step: slide left (advance), slide right (back), 200ms.

### 27.6 Network
- All Firestore on `Dispatchers.IO` or `viewModelScope`.
- `try/catch` on all repo calls → domain-level error.
- No API calls in Composables.
- Connectivity check before network operations.

---

## 28. Security Rules

```
// users/{uid}: read/write by matching uid. Admin read-all.
// listings/: public read for status="published". Write by owner uid.
// tenantRequests/: public read for status="active". Write by owner uid.
// chats/: read/write by participants array members. Admin read-all.
// reports/: create by any authenticated user. Read by admin only.
// packages/: read by any authenticated user. Write by admin only.
// subscriptionRequests/: create by auth user. Read by owner or admin. Update by admin only.
// notifications/{uid}/: read/write by matching uid. Admin write-all.
// config/: read by all authenticated users. Write by admin only.
// geminiKeyLogs/: write by Cloud Functions only. Read by admin only.
// tenantRequests/: write by owner. Read publicly if status="active".
```

App-level:
- Email verification required before listing, requesting, or chatting.
- Blocked user: signed out immediately.
- Admin portal: separate auth check (`userType = "admin"`). Localhost only (V1).

---

## 29. Testing Strategy

### 29.1 Unit Tests (Android)
- Framework: JUnit 4 + MockK + Kotlin Coroutines Test.
- Coverage: 100% ViewModels + UseCases. 80%+ Repository implementations.
- Every ViewModel function: success, failure, loading state.
- Test naming: `*ViewModelTest.kt`, `*RepositoryTest.kt`, `*UseCaseTest.kt`.

### 29.2 Integration Tests
- Firestore emulator for repository tests.
- Firebase Auth emulator for auth tests.

### 29.3 Pre-Feature Checklist
```
[ ] git checkout -b feature/module-name
[ ] Read REQUIREMENTS_SPECIFICATION_v2.md for this module (including Section 3 design tokens)
[ ] Implement feature using design tokens only — no hardcoded colours/sizes
[ ] Write unit tests for all new ViewModels and UseCases
[ ] Run: ./gradlew assembleDebug     → MUST PASS
[ ] Run: ./gradlew test              → MUST PASS
[ ] Update PROGRESS.md
[ ] Create CODE_REVIEW_<module>.md
[ ] Commit
```

---

## 30. PROGRESS.md Spec

```markdown
# Project Progress
## Last Updated: YYYY-MM-DD HH:mm

## Module: Authentication
**Branch:** feature/auth
**Status:** ✅ Complete
**Build:** ✅ Passing
**Tests:** ✅ All passing (24/24)
### Tasks
- [x] Splash screen
- [x] Welcome screen (both action cards, floatY animation, gradient text)
- [x] Login screen
- [x] Register screen
- [x] Email verification
- [x] Blocked screen
- [x] FCM token save on login
- [x] AuthViewModel + tests
- [x] AuthRepositoryImpl + tests
```

---

## 31. CODE_REVIEW.md Spec

```markdown
# Code Review — Module: [Name]
**Date:** YYYY-MM-DD
**Branch:** feature/module-name

## ✅ Architecture Compliance
- [ ] Domain has zero Android imports
- [ ] ViewModels do not hold Activity references
- [ ] Repository interface in domain, impl in data
- [ ] No pass-through UseCases

## ✅ Design System Compliance
- [ ] No hardcoded colours — all from theme tokens (Section 3.2)
- [ ] No hardcoded sizes — all from spacing constants (Section 3.3)
- [ ] Fonts: Fraunces for display, Plus Jakarta Sans for body
- [ ] All icons use NescoIcons.kt
- [ ] No design components modified — only extended
- [ ] Animations match Section 3.5 and 3.6 specs
- [ ] FCM used — no OneSignal references

## ⚠️ Lint Findings (Non-Blocking)
| ID | File | Line | Finding | Severity |
|----|------|------|---------|----------|

## ✅ Performance Checks
- [ ] No network calls in Composables
- [ ] remember{} for expensive computations
- [ ] LazyColumn uses key {}
- [ ] No memory leak patterns
- [ ] DisposableEffect for subscriptions

## 📝 Notes
**DO NOT change design specs based on lint findings.**
```

---

*End of Requirements Specification v2.0.0*  
*This document is the single source of truth. All agents must treat Section 3 (Design System) as immutable law. Any scope change requires updating this document first and bumping the version number.*

---

## 31. Design Reference Verification System

### 31.1 How It Works

`design_references.md` contains the approved React/JSX prototype code provided by the client. Every Composable screen built must be verified against the corresponding component in that file before the module is marked complete.

### 31.2 Verification Process (Per Screen)

```
For each screen being built:

1. LOCATE the equivalent component in design_references.md.
   Search by function name (e.g. "function Home(", "function Detail(").

2. EXTRACT from the reference component:
   - Background colours (map to v2.3 Section 3.2 token names)
   - Border radii (borderRadius values → dp)
   - Font sizes (fontSize → sp) and weights
   - Padding and gap values
   - Gradient definitions (compare to Section 3.2 gradient constants)
   - Icon names (compare to Section 3.7 icon table)
   - Animation class names (fi, su, gGlow, pulse, floatY, sheet, bounce, shimmer)
   - Interactive states (pressed, selected, disabled)

3. COMPARE to Compose implementation. Every value must have a 1:1 counterpart.

4. RECORD in CODE_REVIEW under "Design Reference Verification" table.
```

### 31.3 CSS → Compose Translation Reference

| CSS / JSX value | Compose equivalent |
|---|---|
| `borderRadius: 24` | `RoundedCornerShape(24.dp)` |
| `borderRadius: "50%"` | `CircleShape` |
| `borderRadius: 100` | `RoundedCornerShape(100.dp)` |
| `fontSize: 15` | `15.sp` |
| `fontWeight: 700` | `FontWeight.Bold` |
| `fontWeight: 500` | `FontWeight.Medium` |
| `fontWeight: 800` | `FontWeight.ExtraBold` |
| `gap: 12` | `Arrangement.spacedBy(12.dp)` |
| `padding: "14px 20px"` | `PaddingValues(horizontal=20.dp, vertical=14.dp)` |
| `background: var(--bg2)` | bg2 token from `LocalNescoColors.current` |
| `color: var(--pri)` | primary token |
| `border: "1.5px solid var(--bd)"` | `BorderStroke(1.5.dp, NescoColors.bd)` |
| `backdropFilter: blur(24px)` | `graphicsLayer { renderEffect = BlurEffect(24f) }` API 31+; fallback: higher opacity |
| `animation: "fi"` | `fadeIn(tween(360))` |
| `animation: "su"` | `fadeIn + slideIn(from bottom 26dp)`, spring 420ms |
| `animation: "gGlow 3s"` | `InfiniteTransition` shadow animation (Section 3.6) |
| `animation: "pulse 2s"` | `InfiniteTransition` alpha 1.0→0.42→1.0 |
| `animation: "floatY 4s"` | `InfiniteTransition` translateY 0→-9dp→0 |
| `animation: "sheet"` | `slideInVertically(from bottom 36dp) + fadeIn` 350ms spring |
| `overflow: hidden` | `clip = true` on Modifier |
| `flex: 1` | `Modifier.weight(1f)` |
| `position: absolute, inset: 0` | `Modifier.fillMaxSize()` in `Box` |
| `white-space: nowrap` | `softWrap = false` on Text |
| `text-overflow: ellipsis` | `overflow = TextOverflow.Ellipsis, maxLines = 1` |
| `textTransform: uppercase` | `text.uppercase()` |
| `letter-spacing: 0.06em` | `letterSpacing = 0.06.em` |

### 31.4 Component Name Mapping

| design_references.md | Compose Composable | Route |
|---|---|---|
| `Welcome` | `WelcomeScreen` | `welcome` |
| `SignIn` | `LoginScreen` | `auth/login` |
| `SignUp` | `RegisterScreen` | `auth/register` |
| `Home` | `HomeScreen` | `home` |
| `Detail` | `ListingDetailScreen` | `listing/{id}` |
| `ReqDetail` | `RequestDetailScreen` | `request/{id}` |
| `Form` | `ListingFormScreen` | `listing/form` |
| `RequestForm` | `TenantRequestFormScreen` | `request/form` |
| `ChatScr` | `ChatScreen` | `chats`, `chat/{id}` |
| `MyListings` | `MyListingsScreen` | `my_listings` |
| `BNav` | `BottomNavBar` | shared |
| `SBar` | `StatusBar` | shared |
| `PCard` | `PropertyCard` | shared |
| `RCard` | `TenantRequestCard` | shared |
| `BSlider` | `HomeBannerSlider` | shared |
| `AddOverlay` | `AddOverlaySheet` | shared |

---

## 32. All Journeys & Edge-Case Flows

Every journey listed here is a **required** user flow. A module is not complete until all applicable journeys for that module are implemented.

---

### 32.1 Edit Profile

**Entry points:** Profile screen → "Edit Profile" · Tap profile photo.  
**Route:** `profile/edit`

```
StatusBar
Header row (16dp top, 20dp H):
  Back button (42dp, DarkBg2, 1.5dp DarkBd, 15dp corner)
  Title: "Edit Profile" — Fraunces 24sp SemiBold, centred
  "Save" text button (DarkPri, 15sp Bold) — disabled until changes detected

Scrollable content (20dp H, 24dp top):

  Profile Photo (centred, 32dp bottom):
    Current photo or default avatar (84dp circle, DarkPriM fill, User icon)
    Tap → sheet: "Take Photo" / "Choose from Gallery" / "Remove Photo"
    Semi-transparent scrim + Camera icon overlay on photo circle
    Preview updates immediately; upload happens on Save

  Section: PERSONAL INFO
    Full Name — UnderlineInputField, maxLength 60, pre-filled
    Phone — UnderlineInputField, numeric, maxLength 15
    Date of Birth — tap opens DatePickerDialog, displays formatted date

  Section: LOCATION
    Province — tappable field → Province picker sheet
    City — tappable field → City chips sheet (filtered by province)
    "Use current location" link — 13sp DarkPri

  Section: ACCOUNT TYPE
    Two non-tappable chips: Individual / Business (current one shows .chip.on)
    Note below: "Account type cannot be changed." — 11sp DarkT3

  PrimaryButton "Save Changes" (full width, 32dp top):
    Shows spinner while uploading photo + saving
    On success: pop back to Profile + snackbar "Profile updated"

Unsaved changes → back pressed → GlassDialog:
  "Discard changes?" — "Discard" (DarkRed) / "Keep Editing" (Primary)
```

**ViewModel:** `EditProfileViewModel` — `loadProfile`, `updateProfile`, `detectChanges`, `validateForm`

---

### 32.2 Edit Listing (Pre-Populated)

**Entry points:** My Listings → "Edit" · Listing Detail → "Edit Listing".  
**Route:** `listing/form?listingId={id}&mode=edit`

- Same 11-step `ListingFormScreen` reused with `isEditMode = true`.
- `loadExistingListing(listingId)` called first; shimmer while loading.
- All steps pre-populated from Firestore listing document.
- Step 11 header changes to "Review Changes"; button becomes "💾 Save Changes".
- No new publish slot consumed.
- Status transitions on save:
  - `published` + `requireApproval=true` → `pending_approval`
  - `published` + `requireApproval=false` → stays `published`
  - `draft` → stays `draft`
  - `rejected` → `pending_approval`
- Image handling: existing images shown with delete; new images fill remaining slots; deleted URLs tracked, Storage delete happens on Save.
- Navigate to My Listings on success + snackbar "Listing updated".

**ViewModel additions:** `loadExistingListing(id)`, `isEditMode`, `deletedImageUrls`, `saveChanges()`

---

### 32.3 Edit Tenant Request (Pre-Populated)

**Entry points:** My Requests → "Edit" · Request Detail → "Edit Request".  
**Route:** `request/form?requestId={id}&mode=edit`

- Same 6-step `TenantRequestFormScreen` with `isEditMode = true`.
- All steps pre-populated; Step 4 radius slider restores to `radiusKm`.
- Last step button: "💾 Update Request".
- Status: `active` + `requireApproval=true` → `pending_approval`; else stays `active`.
- Navigate to My Requests + snackbar "Request updated".

**ViewModel additions:** `loadExistingRequest(id)`, `isEditMode`, `saveChanges()`

---

### 32.4 Forgot Password

**Entry point:** Login → "Forgot Password?" link.  
**Route:** `auth/forgot_password`

```
Back button → auth/login
Illustration: Mail icon (36dp DarkPri) in 80dp DarkPriM circle, floatY animation
Title: Fraunces 28sp "Forgot Password?" (su)
Subtitle: "Enter your email and we'll send a reset link." 14sp DarkT2

Email — BoxedInputField (Mail icon)

PrimaryButton "Send Reset Link":
  Loading state while calling Firebase sendPasswordResetEmail()

SUCCESS state (replaces form):
  80dp check circle (DarkPriM fill, Ok icon 36dp DarkPri), bounce animation
  Title: Fraunces 26sp "Check your inbox"
  Body: "We've sent a link to {email}. Check spam too." — 14sp DarkT2 centred
  "Resend Email" OutlinePrimaryButton — throttled 60s (grey + countdown during throttle)
  "Back to Sign In" link (13sp DarkPri Bold, centred)

ERROR state (inline banner):
  DarkRedM fill, DarkRed border, 12dp corner, 12dp/14dp padding
  Error message text 11sp DarkRed
```

**ViewModel:** `ForgotPasswordViewModel` — `sendReset(email)`, `resend()` — `ForgotPasswordUiState(idle|loading|success(email)|error(msg)|throttled(secondsLeft))`

---

### 32.5 Change Password

**Entry point:** Settings → "Change Password" (email accounts only; Google accounts see read-only "Linked with Google" row instead).  
**Route:** `settings/change_password`

```
Info banner (DarkBg2, DarkBd2, 14dp corner): Lock icon + "Re-enter your current password for security."

Current Password — BoxedInputField (Eye toggle)
New Password — BoxedInputField (Eye toggle)
Confirm New Password — BoxedInputField

Password requirements row (visible once New Password focused):
  Chips: "8+ chars" · "1 uppercase" · "1 number"
  Met: .chip.on; Unmet: .chip (grey)

PrimaryButton "Update Password" — disabled until all validations pass
Success: pop back + snackbar "Password updated"
Error: inline red banner below relevant field
```

---

### 32.6 Delete Account Confirmation

Full three-phase flow. See Section 33 — Delete Dialog 33.12 for complete spec.

---

### 32.7 Google Sign-In

**Entry points:** Login screen · Register screen.

```
Divider row: line — "or" (12sp DarkT2) — line

Google button:
  DarkBg2 fill, 1.5dp DarkBd border, 100dp corner, 15dp V / 20dp H
  Row: Google "G" SVG (22dp, full colour) + "Continue with Google" (15sp Medium DarkT0)
  Full width

Flow:
  → Google credential picker (One Tap)
  → AuthViewModel.loginWithGoogle(idToken)
  → New user: create Firestore doc → Onboarding
  → Returning: check isBlocked, onboardingComplete → route accordingly
  → FCM token saved

Errors:
  Play Services unavailable: snackbar
  Cancelled: silent dismiss
  Network: snackbar "Network error. Please try again."
```

---

### 32.8 Report Listing / Request / User

**Entry points:** Listing Detail (bottom text link) · Request Detail (bottom text link) · Chat Detail (three-dot menu → "Report User").

```
GlassBottomSheet (Section 33 glass style):
  Drag handle
  Title: "Report Listing" / "Report Request" / "Report User" — Fraunces 22sp
  Subtitle: "What's the issue?" — 14sp DarkT2

  Category chips (wrapping, single-select):
    Spam · Misleading Info · Fake Listing · Inappropriate Content
    Harassment · Pricing Issues · Duplicate · Other

  "More details (optional)" SectionLabel
  TextArea (DarkBg2, 14dp corner, 12dp/14dp padding, maxLength 500, 80dp min height)

  PrimaryButton "Submit Report" — disabled until category selected
  
  On success: sheet replaces with:
    60dp check circle (DarkPriM, Ok icon)
    "Report submitted" Fraunces 20sp
    "We'll review it within 24 hours." 13sp DarkT2
    Auto-dismiss 2 seconds
```

---

### 32.9 Chat Upgrade Prompt (Limit Reached)

Triggered when `dailyMessageCount >= dailyMessageLimit`. Non-dismissable by tapping outside.

```
GlassBottomSheet (non-dismissable, no drag handle):
  Icon: Package (44dp DarkAcc) in 72dp DarkAccM circle
  Title: Fraunces 24sp "Daily limit reached"
  Subtitle: "You've used all {limit} messages today." 14sp DarkT2 centred
  
  Limit bar (DarkBg3, 14dp corner, 12dp/14dp padding):
    "Free plan" + "{used}/{limit}" row
    ProgressBar — DarkRed progress at 100%

  PrimaryButton "🚀 Upgrade Now" (full width)
  "Maybe Later" text link (13sp DarkT2, centred, 12dp top) — this IS tappable to dismiss
```

---

### 32.10 Publish Limit Warning

Triggered when `publishedCount >= maxPublishedListings` and user taps Publish.

```
GlassBottomSheet:
  Icon: Building (40dp DarkAcc) in 68dp DarkAccM circle
  Title: Fraunces 22sp "Publish Limit Reached"
  Body: "You've used {used} of {max} publish slots. Unpublish a listing or upgrade."
  
  PrimaryButton "🚀 Upgrade Plan" → Packages
  GhostButton "Manage Listings" → My Listings Published tab
  "Save as Draft instead" text link (13sp DarkT2, centred)
```

---

### 32.11 Request Limit Warning

Triggered when `activeRequestsCount >= maxPublishedRequests` and user taps Post Request.

```
GlassBottomSheet (same pattern as 32.10):
  Icon: Search (40dp DarkBlue) in 68dp DarkBlueM circle
  Title: "Request Limit Reached"
  Body: "You've posted {used} of {max} active requests. Close one or upgrade."
  
  PrimaryButton "🚀 Upgrade Plan"
  GhostButton "Manage Requests"
  "Save as Draft instead" link
```

---

### 32.12 Feature Slider Submission

**Entry point:** My Listings → "Manage Feature Slider" (visible only if `allowsSlider = true`).  
**Route:** `my_listings/slider`

```
Info card (DarkPriM, DarkPriR, 16dp corner): "Your plan includes 1 featured slider slot"

Step 1 — Choose Listing:
  LazyColumn of published listings (horizontal compact card, radio selection right side)

Step 2 — Upload Banner Image:
  200dp upload zone (DarkBg2, dashed DarkBd2, 18dp corner)
  "📐 16:9 ratio · Max 1.5MB · Auto-compressed" info note

PrimaryButton "Submit for Approval" (disabled until listing + image both selected)
  → compress client-side → upload Storage → create sliders/{id} doc

Status view if slider already exists:
  Status badge (pending: DarkAcc, approved: DarkPri, rejected: DarkRed)
  "Remove Slider" GhostButton if approved
  Rejection reason in DarkRedM card + "Resubmit" button if rejected
```

---

### 32.13 Success Screens

#### After Publishing Listing
```
Full screen mesh bg:
  80dp check circle (DarkPri border 3dp, DarkPriM fill, Ok icon 36dp DarkPri) bounce animation
  Fraunces 28sp "Listing Submitted!" (su)
  requireApproval=true: "Your listing is under review. We'll notify you when approved."
  requireApproval=false: "Your listing is live! People can now find your space."
  PrimaryButton "View My Listings" / GhostButton "Back to Home"
  Auto-navigate after 4 seconds
```

#### After Posting Request
```
Same shell, Search icon instead of check.
Fraunces 28sp "Request Posted!"
PrimaryButton "View My Requests" / GhostButton "Browse Listings"
```

#### After Submitting Subscription
```
Package icon (DarkAcc). Fraunces 28sp "Request Submitted!"
Body shows bank details + WhatsApp number as tappable DarkPri link.
PrimaryButton "Done" → Home
```

---

### 32.14 Full-Screen Image Gallery

**Entry point:** Listing Detail — tap any image in HorizontalPager.  
**Route:** `listing/{id}/gallery?index={n}`

```
Full-screen DarkBg0 background.
HorizontalPager — full-screen, no padding, pinch-to-zoom (max 3×, snap back below 1×).
Top overlay: Close button (42dp DarkNav bg, X icon) + "{n}/{total}" pill (DarkNav bg)
  Position absolute, 48dp top (status bar), 16dp H
Bottom: page indicator dots (centred, 28dp bottom)
Transition: shared element from detail screen image; fallback fadeIn/Out 180ms
```

---

### 32.15 City / Province Picker Sheet

Used in: Home location chip · Onboarding Step 2 · Edit Profile · Listing Form Step 5 · Request Form Step 4.

```
GlassBottomSheet:
  Title: "Select Location" Fraunces 20sp
  Subtitle: "Choose your province and city" 13sp DarkT2

  Province chips (wrapping):
    Punjab · Sindh · KPK · Balochistan · AJK · Gilgit-Baltistan · ICT

  City chips (slide in after province selected, filtered by province):
    All cities for selected province from strings.xml

  PrimaryButton "Confirm" — disabled until both selected
  "Use Current Location" link (centred, 13sp DarkPri Bold)
    → permission request → reverse geocode → auto-select chips
```

---

### 32.16 Geofence / Radius Picker

**Entry point:** Home screen filter → "Search Near Me" option.

```
Tall bottom sheet (85% screen height):
  Title: Fraunces 22sp "Search Near Me"
  Google Maps area (fills remaining height):
    Draggable pin + DarkPri radius circle overlay (2dp stroke, DarkPriM 20% fill)
    "Use My Location" floating button (top-right, DarkNav bg, Pin icon)
  
  Bottom controls (DarkBg1, 24dp top corners, 24dp padding):
    "SEARCH RADIUS" SectionLabel + "{X} km" (24sp Bold DarkPri, right-aligned)
    Range slider (1–50km, step 1km, same thumb/track style as Section 3)
    Row: "Clear Geofence" GhostButton (flex 1) + "Apply" PrimaryButton (flex 1)
    34dp bottom padding

State persistence: SharedPreferences geofenceEnabled/Lat/Lng/RadiusKm
Home screen indicator: "📍 X km radius" pill shown below search bar when active
```

---

### 32.17 Notification Preferences

**Route:** `settings/notifications`

```
Toggle list per category:
  💬 New Messages (DarkPri)
  ✅ Listing Approved/Rejected (DarkPri)
  🔔 Subscription Updates (DarkAcc)
  📣 Promotions & Announcements (DarkBlue)
  ⚠️ Account Alerts (DarkRed)

Each row: icon + label (flex 1) + ToggleSwitch

"Save Preferences" PrimaryButton sticky bottom
Saves to: SharedPreferences + Firestore users/{uid}.notifPreferences map
FCM topic subscribe/unsubscribe per applicable category
```

---

### 32.18 Subscription Detail & Extend

**Route:** `packages/current`

```
Current Plan Card (gradient DarkPri→DarkSec, 20dp corner, 20dp padding, white text):
  Package name (Fraunces 20sp white) + "Active" badge
  Price (26sp Bold) + " PKR / {duration}" (13sp 70% opacity)
  Divider (1dp 20% opacity)
  Feature rows: ✓ {maxPublished} slots · ✓ {dailyLimit} messages/day · ✓ slider if applicable
  Expiry row: "Expires: {date}" — red pill "Expires in X days" if ≤ expiryWarningDays

Usage stats card (DarkBg2, DarkBd, 16dp, 14dp padding):
  "Published Listings" row + ProgressBar
  "Active Requests" row + ProgressBar
  "Messages today" row + ProgressBar (resets daily)

PrimaryButton "Extend / Renew Plan"
OutlinePrimaryButton "Browse Other Plans"

Expiry warning banner on Home + My Listings:
  DarkAccM fill, DarkAcc border, Bell icon + "Plan expires in X days. Renew →"
```

---

### 32.19 No Internet Screen

Triggered by `NetworkCallback.onLost()` registered in `Application.onCreate()`.

```
Full-screen opaque overlay (DarkBg0, Z=highest):
  Wifi-X icon (72dp DarkT2), floatY animation
  Fraunces 26sp "No Internet"
  "Check your connection and try again." 14sp DarkT2 centred

  PrimaryButton "Try Again" (180dp wide):
    On tap: check connectivity
    If restored: dismiss overlay + reload last ViewModel
    If still offline: shake animation on button (translateX ±8dp, 3 cycles, 400ms)

Auto-dismiss when NetworkCallback.onAvailable() fires.
Does NOT clear navigation stack.
```

---

### 32.20 Force Update Dialog

```
Hard update (isForceUpdate=true):
  Non-dismissable (back press intercepted, tap-outside blocked)
  Package icon (40dp DarkPri)
  Title + body from strings.xml
  Single PrimaryButton "Update Now" → Play Store deep link

Soft update:
  Dismissable (tap outside = "Later")
  GhostButton "Later" + PrimaryButton "Update Now"
```

---

### 32.21 Share Listing / Request + Deep Links

```
Android native ShareSheet (Intent.ACTION_SEND, text/plain)

Listing share text: "{title} — {type} in {area}, {city}. PKR {price}/mo. NesCo: nesco://listing/{id}"
Request share text: "{name} is looking for {type} in {area}. Budget PKR {budget}/mo. NesCo: nesco://request/{id}"

Deep link handling in AndroidManifest:
  scheme="nesco" host="listing" → ListingDetailScreen
  scheme="nesco" host="request" → RequestDetailScreen
  Fallback: Play Store link
```

---

### 32.22 Onboarding Skip / Return

```
Steps 2, 3, 4: "Skip for now →" text link (12sp DarkT3, right-aligned). Step 1 not skippable.

Return: if onboardingComplete=false on login → resume from first incomplete step, pre-fill saved data.

Profile completeness banner (on Profile screen):
  Shown until: phone + photo + city all set
  DarkPriM fill, DarkPriR border, 14dp corner: "Complete your profile for better visibility. Complete →"
```

---

### 32.23 Date Picker (Listing Form Step 3)

```
Material3 DatePickerDialog styled with NesCo colours:
  Header: DarkPri background
  Selected day: DarkPri circle, white text
  Today indicator: DarkPri outline
  Min date: today. Max: 2 years from today.
  "OK": DarkPri text. "Cancel": DarkT2 text.

On confirm: "Custom Date" chip updates label to formatted date e.g. "15 Oct 2025"
"Immediately" and date chip are mutually exclusive.
```

---

### 32.24 Time Slot Picker (Hourly Listings — Form Step 3)

```
"+ Add Time Slot" OutlinePrimaryButton (Plus icon)

Time Slot GlassBottomSheet:
  "From" + "To" time dials side by side (12-hour + AM/PM toggle)
  Validation: To must be after From; no overlap with existing slots
  Inline error if invalid: "End time must be after start time." (DarkRed, 12sp)
  "Add Slot" PrimaryButton

Added slots: .chip.on style chips with X delete icon, horizontal scroll row
Available Days: Mon–Sun multi-select chips below slots
```

---

### 32.25 Admin — Listing Delete with Notification

```
Confirmation dialog (Section 33.16):
  Listing title shown in quotes
  Mandatory "Notification Message to Owner" textarea (required, maxLength 300)
  Notification preview box (title: "Your Listing Was Removed", body: typed message)
  Delete button disabled until message filled

On confirm:
  Cloud Function onListingDeleted(listingId, ownerUid, adminMessage)
  → deletes Firestore doc + Storage files
  → FCM listing_deleted_by_admin to owner with adminMessage as body
  Admin UI: row removed + success toast
```

---

### 32.26 Admin — Request Delete with Notification

Same pattern as 32.25 but for tenant requests. Cloud Function `onRequestDeleted`. FCM type `request_deleted_by_admin`.

---

### 32.27 Admin — Free Upgrade Flow

```
"Free Upgrade" dialog:
  User name + email info card
  Package dropdown (all active packages)
  Duration input (pre-filled with package.durationDays, editable)
  Optional note textarea (maxLength 200, shown in FCM body)
  "Grant Upgrade" PrimaryButton (green)

On confirm:
  Update users/{uid} package fields
  Create subscriptionRequests/{id} status=approved
  FCM subscription_upgraded to user
  Admin toast: "Upgrade granted. User notified."
```

---

### 32.28 Admin — Broadcast Push Notification

```
Dashboard card section:
  Title + Body inputs
  Target selector: All Users / Free Only / Paid Only (FCM topics)
  Android notification preview card
  "Send Broadcast" PrimaryButton → confirmation dialog → Cloud Function broadcastToAll()

FCM topics managed in Android app:
  Login: subscribe "all_users"
  Package activated: subscribe "paid_users", unsubscribe "free_users"
  Package expired: reverse
  Logout: unsubscribe all
```

---

## 33. Delete Dialogs & Glassmorphic Design System

### 33.1 Philosophy

The glass dialog creates a sense of floating above content through three combined techniques:
1. **Backdrop scrim** — screen behind is darkened AND blurred
2. **Translucent container** — dialog box itself is semi-transparent (not opaque)
3. **Edge highlight** — thin gradient border simulates light catching a glass surface

Every destructive action follows three mandatory phases: **Confirm → Loading → Result**.

---

### 33.2 Glassmorphic Scrim

```kotlin
// Full-screen scrim behind every glass dialog/sheet
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(Color(0x99000000))  // 60% black
        .then(
            if (Build.VERSION.SDK_INT >= 31)
                Modifier.blur(16.dp)
            else
                Modifier  // API 30 fallback: bump opacity to 0xBB (73%)
        )
)
// Animate: fadeIn(tween(220)) appear, fadeOut(tween(180)) dismiss
// Destructive dialogs: tap-outside does NOTHING
// Non-destructive dialogs (e.g. Discard Changes): tap-outside dismisses
```

**Colour tokens:**
```
Dark mode scrim:  rgba(0, 0, 0, 0.60) + 16dp blur
Light mode scrim: rgba(6, 32, 26, 0.55) + 16dp blur
API 30 fallback:  opacity 0.73, no blur
```

---

### 33.3 Glass Dialog Container

```kotlin
Surface(
    modifier = Modifier
        .fillMaxWidth(0.88f)
        .wrapContentHeight()
        .graphicsLayer { alpha = 0.97f },  // 3% transparency — key for glass feel
    shape = RoundedCornerShape(28.dp),
    color = GlassDialogBg,
    border = BorderStroke(1.dp, GlassDialogBorderBrush)
)

// DARK MODE
val GlassDialogBg = Color(0xE6081610)
// rgba(8, 22, 16, 0.90) — deep dark green-tinted, 90% opaque

// LIGHT MODE
val GlassDialogBgLight = Color(0xF0FFFFFF)
// rgba(255, 255, 255, 0.94)

// Border: gradient simulating light on glass edge
val GlassDialogBorderBrush = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to Color(0x2AFFFFFF),   // 16% white — top-left highlight
        0.4f to Color(0x122ECC8A),   // 7% primary green — mid
        1.0f to Color(0x08FFFFFF),   // 3% white — bottom-right fade
    ),
    start = Offset(0f, 0f),
    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

// LIGHT MODE border
val GlassDialogBorderBrushLight = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to Color(0x400C7A50),   // 25% primary green
        0.5f to Color(0x18000000),   // 9% black mid
        1.0f to Color(0x0A0C7A50),   // 4% green fade
    ),
    start = Offset(0f, 0f),
    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
```

**Dialog entry/exit animation:**
```kotlin
// Entry: spring from slightly below + fade in
AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(200)) + slideInVertically(
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        initialOffsetY = { it / 8 }   // slides up from 1/8 of its own height
    ),
    exit = fadeOut(tween(160)) + slideOutVertically(
        animationSpec = tween(160),
        targetOffsetY = { it / 10 }
    )
)
```

---

### 33.4 Glass Bottom Sheet Container

For destructive actions that need more space (e.g. admin delete with message field).

```kotlin
// Same glass colour + border as dialog
// Shape: only top corners rounded — 28dp
Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

// Entry animation: slides up from bottom with spring
enter = slideInVertically(
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
    initialOffsetY = { it }  // from full height below
) + fadeIn(tween(280))

exit = slideOutVertically(
    animationSpec = tween(220),
    targetOffsetY = { it }
) + fadeOut(tween(180))

// Drag handle (non-dismissable dialogs: HIDE the handle — its absence signals non-dismissable)
// Dismissable sheets: show 40dp × 4dp DarkBg4 handle centred, 12dp top margin
```

---

### 33.5 Destructive Button Style

```kotlin
// All Delete / Block / Remove / Reject action buttons
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = DarkRed,   // Color(0xFFE06060) dark / Color(0xFFC04040) light
        contentColor   = Color.White
    ),
    shape = RoundedCornerShape(100.dp),
    elevation = ButtonDefaults.buttonElevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp
    )
)
// Same pill shape, same padding as PrimaryButton — only colour differs
// Pressed: scale(0.97) alpha(0.9) — same as PrimaryButton
```

---

### 33.6 Three-Phase Delete Flow

Every single delete/destructive action follows this pattern. No exceptions.

```
Phase 1 — CONFIRM:
  Glass dialog or glass bottom sheet shown
  User reads consequences, fills any required fields (e.g. admin message)
  "Cancel" GhostButton + destructive action button (DarkRed)
  Tap destructive button → Phase 2

Phase 2 — LOADING:
  Dialog content replaced in-place (no dismiss + re-show):
    Previous content fades out (200ms)
    Loading content fades in (200ms):
      Spinner (see Section 33.7 for loading animation spec)
      Loading message (e.g. "Deleting listing…")
  Dialog is NON-DISMISSABLE during loading (tap-outside blocked, back press blocked)
  All buttons hidden / replaced by spinner

Phase 3a — SUCCESS:
  Loading content crossfades to success content (crossfade 300ms):
    Check circle (DarkPriM fill, Ok icon 32dp DarkPri) + bounce animation
    Success message
  Auto-dismiss after 1.8 seconds
  Then: navigate/update UI as appropriate

Phase 3b — ERROR:
  Loading content crossfades to error content:
    X circle (DarkRedM fill, X icon 32dp DarkRed)
    Error message (what went wrong + "Try again?" suggestion)
  "Try Again" OutlinePrimaryButton + "Cancel" GhostButton shown
  Tapping "Try Again" returns to Phase 1 (form pre-filled with previous values)
  Tapping "Cancel" dismisses entirely (no action taken)
```

---

### 33.7 Loading Animation During Deletion

The spinner shown during Phase 2 is a custom NesCo branded loader — not the default Material `CircularProgressIndicator`.

```kotlin
@Composable
fun NescoDeleteSpinner(size: Dp = 52.dp) {
    // Three concentric arcs rotating at different speeds
    // Outer arc: 360° sweep, DarkPri, 3dp stroke, rotation speed 1200ms
    // Mid arc:   240° sweep, DarkPri2 70% opacity, 2.5dp stroke, rotation speed 800ms (reverse)
    // Inner arc: 120° sweep, DarkPri3 50% opacity, 2dp stroke, rotation speed 600ms
    
    val infiniteTransition = rememberInfiniteTransition()
    
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing))
    )
    val midRotation by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing))
    )
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing))
    )
    
    Canvas(modifier = Modifier.size(size)) {
        // Outer arc
        rotate(outerRotation) {
            drawArc(color = pri, startAngle = 0f, sweepAngle = 300f,
                useCenter = false, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        }
        // Mid arc
        rotate(midRotation) {
            drawArc(color = pri2.copy(alpha = 0.70f), startAngle = 60f, sweepAngle = 220f,
                useCenter = false, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
        }
        // Inner arc
        rotate(innerRotation) {
            drawArc(color = pri3.copy(alpha = 0.50f), startAngle = 120f, sweepAngle = 120f,
                useCenter = false, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}
```

**Loading message typography:** 14sp, DarkT1, centred, 16dp below spinner.

**Pulsing dot animation** (for longer operations like account deletion):
```kotlin
// Three dots pulsing in sequence (ellipsis style)
// Each dot: 8dp circle, DarkPri
// Dot 1: pulse delay 0ms. Dot 2: 200ms. Dot 3: 400ms.
// Scale 0.6→1.0→0.6, duration 1200ms each, infinite
// Shown below "Deleting your account…" text for delete-account flow only
```

---

### 33.8 Dialog Icon System

Each delete dialog has a themed icon in a coloured circle. The icon immediately communicates what is being destroyed.

```
Icon container: 56dp circle
Icon size: 26dp
Stroke width: 1.8dp (standard NescoIcon weight)

Icon + colour per action type:
  Delete listing:    Building icon, DarkRed fill circle (DarkRedM), DarkRed icon
  Delete request:    Search icon,   DarkRed fill circle, DarkRed icon
  Delete draft:      FileText icon, DarkAcc fill circle (DarkAccM), DarkAcc icon
  Unpublish:         Eye icon (crossed), DarkAcc fill circle, DarkAcc icon
  Delete message:    Chat icon,     DarkRed fill circle, DarkRed icon
  Delete/leave chat: Chat icon,     DarkRed fill circle, DarkRed icon
  Remove photo:      Cam icon,      DarkAcc fill circle, DarkAcc icon
  Remove slider:     Star icon,     DarkAcc fill circle, DarkAcc icon
  Unsave item:       Hrt/Bookmark,  DarkAcc fill circle, DarkAcc icon
  Delete account:    User icon,     DarkRed fill circle, DarkRed icon
  Sign out:          LogOut icon,   DarkBg4 fill circle, DarkT2 icon
  Discard form:      X icon,        DarkAcc fill circle, DarkAcc icon
  Admin — delete:    Trash icon,    DarkRed fill circle, DarkRed icon
  Admin — block:     Shield icon,   DarkRed fill circle, DarkRed icon
  Admin — reject:    X icon,        DarkRed fill circle, DarkRed icon
```

---

### 33.9 Standard Glass Dialog Layout Template

```
[GlassScrim]
  [GlassDialogContainer — 88% width, 28dp corner, glass bg + gradient border]
    
    Content padding: 28dp all sides
    
    Icon block (centred, bottom 18dp):
      56dp circle (colour per 33.8) + 26dp icon
      Entry: bounce animation (scale 0.82→1.12→1.0, 400ms spring)
    
    Title (centred, Fraunces 22sp SemiBold, bottom 10dp):
      e.g. "Delete Listing?"
    
    Body text (centred, 14sp DarkT1, 1.65 line height, bottom 24dp):
      Consequences list — each item on own line, leading bullet "•"
      e.g. "• Your listing will be permanently removed
            • Saved by X people — they will lose it
            • This cannot be undone."
    
    [Optional: extra content area — e.g. admin message textarea]
    
    Button row (column, 10dp gap):
      Cancel: GhostButton, full width
      Destructive action: DarkRed PrimaryButton variant, full width
      Button order: Cancel ALWAYS above destructive (friction before action)
```

---

### 33.10 Standard Glass Bottom Sheet Template

Used when more vertical space is needed (admin fields, multi-step context).

```
[GlassScrim]
  [GlassBottomSheetContainer — 28dp top corners only, glass bg + gradient border]
    
    Drag handle (only for dismissable sheets): 40dp × 4dp DarkBg4, centred, 12dp top margin
    
    Content padding: 24dp H, 20dp top (after handle), 48dp bottom
    
    Title: Fraunces 24sp SemiBold, bottom 6dp
    Subtitle: 14sp DarkT2, bottom 20dp
    
    [Content specific to each dialog]
    
    Button row (column or row, 12dp gap):
      Always: Cancel (Ghost) + Action (DarkRed)
```

---

### 33.11 Delete Own Listing (User)

**Trigger:** My Listings → Published tab → row action menu → "Delete".  
**Type:** Glass Dialog.

```
Icon: Building (DarkRed circle)
Title: "Delete Listing?"
Body:
  "• "{listing title}" will be permanently removed
   • Anyone who saved it will lose it from their saved items
   • Active chats about this listing will be closed
   • This cannot be undone."

Acknowledge checkbox (required before Delete button enables):
  Custom checkbox (22dp, DarkPriM border idle, DarkPri fill when checked, checkmark white):
    Label: "I understand this listing will be permanently deleted." — 13sp DarkT1

Cancel / "Delete Listing" (DarkRed)

Loading: "Deleting listing…" + NescoDeleteSpinner
Success: "Listing deleted" + navigate to My Listings
Error: "Could not delete. Please try again."
```

---

### 33.12 Delete Draft Listing (User)

**Type:** Glass Dialog (smaller — drafts have no public impact).

```
Icon: FileText (DarkAcc circle)
Title: "Delete Draft?"
Body: ""{draft title}" will be permanently removed. You'll lose all your progress."

No acknowledge checkbox needed (drafts are not public, lower stakes).

Cancel / "Delete Draft" (DarkRed)

Loading: "Removing draft…" + spinner
Success: snackbar "Draft deleted" (no navigation)
```

---

### 33.13 Unpublish Listing (User)

Not a delete — but shown as a Glass Dialog because it affects other users.

```
Icon: Eye (crossed out, DarkAcc circle)
Title: "Unpublish Listing?"
Body:
  "• "{listing title}" will be hidden from search
   • It will appear in your Drafts
   • You can republish any time
   • Active chats will remain open"

Cancel / "Unpublish" (DarkAcc fill, not DarkRed — this is reversible)

Loading: "Unpublishing…" + spinner
Success: snackbar "Listing unpublished. It's now in your Drafts." (no navigation)
```

---

### 33.14 Delete Tenant Request (User)

**Trigger:** My Requests → row action menu → "Delete Request".  
**Type:** Glass Dialog.

```
Icon: Search (DarkRed circle)
Title: "Delete Request?"
Body:
  "• Your looking request will be permanently removed
   • Hosts who messaged you about this request won't be affected
   • This cannot be undone."

Acknowledge checkbox:
  "I understand this request will be permanently deleted."

Cancel / "Delete Request" (DarkRed)

Loading: "Deleting request…" + spinner
Success: snackbar "Request deleted" + remove from My Requests list
```

---

### 33.15 Close Tenant Request (User)

Reversible — shown in DarkAcc, not DarkRed.

```
Icon: X (DarkAcc circle)
Title: "Close Request?"
Body:
  "• Your request will be marked as closed and hidden from hosts
   • You can reopen it at any time from My Requests
   • Current chats remain open"

Cancel / "Close Request" (DarkAcc fill)

Loading: "Closing request…" + spinner
Success: snackbar "Request closed. Reopen it from My Requests anytime."
```

---

### 33.16 Delete Chat Message (User)

**Trigger:** Long-press message → "Delete".  
**Type:** Glass Dialog (compact).

```
Icon: Chat (DarkRed circle)
Title: "Delete Message?"
Body: "This message will be replaced with "Message deleted" for both you and the recipient."

No acknowledge checkbox (low stakes, immediately visible context).

Cancel / "Delete" (DarkRed)

Loading: spinner inline in dialog (no text needed — quick operation)
Success: dialog auto-closes, bubble shows "Message deleted" placeholder text (DarkT3, italic)
Error: inline error in dialog — "Could not delete. Try again."
```

---

### 33.17 Delete / Leave Conversation (User)

**Trigger:** Chat Detail → three-dot menu → "Delete Conversation".  
**Type:** Glass Dialog.

```
Icon: Chat (DarkRed circle)
Title: "Delete Conversation?"
Body:
  "• All messages will be permanently erased for you
   • The other person will keep their copy
   • This cannot be undone."

Cancel / "Delete Conversation" (DarkRed)

Loading: "Deleting conversation…" + spinner
Success: navigate to Chat List, row removed
```

---

### 33.18 Remove Photo from Listing Form

**Trigger:** Listing Form Step 9 → tap X on a filled photo slot.  
**Type:** Glass Dialog (compact — inline in form flow).

```
Icon: Cam (DarkAcc circle — reversible within form session)
Title: "Remove Photo?"
Body: "This photo will be removed from your listing."
(Only shows "cannot be undone" if listing is already published and photo exists in Storage)

Cancel / "Remove" (DarkAcc fill — not DarkRed, user is still in a form)

No loading state needed — instant local removal from form state.
Success: slot clears immediately.
```

---

### 33.19 Remove Feature Slider (User)

**Trigger:** My Listings → Manage Feature Slider → "Remove Slider".  
**Type:** Glass Dialog.

```
Icon: Star (DarkAcc circle)
Title: "Remove Featured Slider?"
Body:
  "• Your banner will be removed from the home screen carousel
   • Your linked listing will remain published
   • You can submit a new slider anytime"

Cancel / "Remove Slider" (DarkAcc fill)

Loading: "Removing slider…" + spinner
Success: snackbar "Slider removed" + status card updates
```

---

### 33.20 Unsave / Remove Saved Listing

**Trigger:** Saved Items → swipe-to-remove or unsave icon.  
**Type:** No dialog — but show undo snackbar instead.

```
Swipe left on card → reveal DarkRed "Remove" action tile (behind card)
On confirm (full swipe or tap tile):
  Card slides out with scale(0.9) + fadeOut 200ms
  Item removed from list immediately (optimistic)
  Snackbar "Removed from saved" + "Undo" action
  If "Undo" tapped within 4 seconds: re-add, card slides back in
  After 4 seconds: Firestore delete confirmed
```

---

### 33.21 Unsave / Remove Saved Request

Same pattern as 33.20 for the Requests tab in Saved Items.

---

### 33.22 Delete Account (User)

Full three-phase flow with the highest friction of any delete in the app.

**Trigger:** Settings → "Delete Account" OR Profile → "Delete Account".  
**Type:** Glass Dialog (Phase 1) → Loading overlay (Phase 2) → Welcome screen (Phase 3).

```
PHASE 1 — Glass Dialog:

Icon: User (DarkRed circle, 60dp container — larger than standard)
Title: Fraunces 24sp "Delete Account?"
Body:
  "This will permanently erase everything:
   • All your listings and draft listings
   • All your tenant requests
   • All your conversations and messages
   • Your saved listings and requests
   • Your subscription and packages
   • Your profile and account

  You will be signed out immediately.
  This cannot be reversed."

14sp DarkT1, 1.65 line height

Acknowledge checkbox (required):
  "I understand my account and all data will be permanently deleted."

Second acknowledge (appears after first is checked):
  "I understand this action cannot be undone."
  (Two checkboxes create meaningful friction for irreversible action)

"Delete Account" DarkRed button only enabled after BOTH boxes checked.
Cancel GhostButton (above Delete, as always)

PHASE 2 — Full-screen loading (NOT inside dialog — replaces screen entirely):
  DarkBg0 full screen
  NescoDeleteSpinner (64dp, larger than standard)
  "Deleting your account…" Fraunces 22sp DarkT0, centred
  Three pulsing dots below text (see Section 33.7)
  Progress steps shown below (each lights up as Cloud Function completes):
    ✓ Removing listings...
    ✓ Removing requests...
    ✓ Clearing conversations...
    ✓ Removing saved items...
    ✓ Deleting profile...
  Each step: 13sp DarkT2, check turns DarkPri when complete
  (Steps are approximate — driven by Cloud Function callback)
  Back press blocked entirely during loading.

PHASE 3a — SUCCESS:
  Navigate to Welcome screen with clearBackStack = true
  All SharedPreferences cleared
  FCM unsubscribe all topics
  No success screen needed — Welcome IS the success state

PHASE 3b — ERROR:
  Full-screen error (stays on same screen, back press now allowed):
    X circle (80dp, DarkRedM, X icon DarkRed 36dp)
    Fraunces 22sp "Something went wrong"
    "Your account could not be deleted. Your data is safe." 14sp DarkT2
    PrimaryButton "Try Again" + GhostButton "Cancel"
```

---

### 33.23 Sign Out Confirmation

**Trigger:** Settings → "Sign Out" · Profile → "Sign Out".  
**Type:** Glass Dialog (minimal — sign out is reversible).

```
Icon: LogOut (DarkBg4 circle — neutral colour, not DarkRed)
Title: "Sign Out?"
Body: "You'll need to sign in again to access your account."
  (No bullet list needed — single clear consequence)

Cancel / "Sign Out" — DarkT0 fill (not DarkRed — this is reversible)

Loading: "Signing out…" + spinner (brief — 300–800ms)
Success: navigate to Welcome, clearBackStack = true, SharedPreferences cleared (except theme preference)
```

---

### 33.24 Discard Form / Unsaved Changes

**Trigger:** Back press or "Cancel" in any multi-step form with changes detected.  
**Type:** Glass Dialog.

```
Icon: X (DarkAcc circle)
Title: "Discard Changes?"
Body (varies by context):
  Listing form:  "Your listing progress will be lost. Save as a draft instead?"
  Request form:  "Your request progress will be lost."
  Edit profile:  "Your profile changes won't be saved."
  Edit listing:  "Your edits won't be saved to this listing."

Three buttons for forms (listing/request — drafts possible):
  "Save as Draft" PrimaryButton (full width) — saves draft, navigates away
  "Discard" DarkRed GhostButton — discard, navigate away
  "Keep Editing" GhostButton — dismiss dialog, stay in form

Two buttons for simple forms (edit profile, settings):
  "Discard" DarkRed text (not button — lower visual weight)
  "Keep Editing" PrimaryButton
```

---

### 33.25 Remove Time Slot (Listing Form)

**Trigger:** Tap X on a time slot chip in Form Step 3 (hourly listings).  
**Type:** No dialog — instant removal with undo.

```
Tap X on chip → chip disappears with scale(0.8)+fadeOut 180ms
SnackBar "Slot removed" + "Undo" action (4-second window)
If Undo: chip reappears with scale+fadeIn
After 4 seconds: permanently removed from form state
```

---

### 33.26 Admin — Delete Listing

**Trigger:** Admin Portal → Listings page → row action → "Delete".  
**Type:** Glass Bottom Sheet (needs textarea).

```
GlassBottomSheet:
  Title: Fraunces 24sp "Delete Listing?"
  Listing context card (DarkBg2, 12dp corner):
    Cover thumbnail (60dp, 10dp corner) + title + owner email + status badge — row
  
  "Notification message to owner" — required textarea
    Label: SectionLabel "WHY IS THIS BEING REMOVED?"
    Placeholder: "This message will be sent to the owner as a push notification."
    DarkBg2, 1.5dp DarkBd, 12dp corner, 14dp padding, 88dp min height
    maxLength: 300
    Required — Delete button stays disabled until filled (minimum 10 characters)
  
  Notification preview box (DarkBg3, 12dp corner, 12dp/14dp padding, top 12dp):
    Label: "OWNER WILL RECEIVE:" SectionLabel
    Preview row:
      DarkBg0 mock notification bubble:
        "Your Listing Was Removed" — 13sp Bold DarkT0
        [admin's typed message] — 12sp DarkT1 (live updates as typed)
  
  Character counter: "{n}/300" — 11sp DarkT3, right-aligned below textarea
  
  Cancel GhostButton / "Delete Listing" DarkRed PrimaryButton
  Delete button disabled until message ≥ 10 characters.

Loading: "Deleting listing and notifying owner…" + NescoDeleteSpinner
Success: toast "Listing deleted. Owner has been notified." + row removed from table
Error: toast "Failed to delete. Please try again."
```

---

### 33.27 Admin — Delete Tenant Request

Identical pattern to 33.26.

```
GlassBottomSheet:
  Title: "Delete Request?"
  Context card: requester name + area + budget
  
  Required textarea: "WHY IS THIS BEING REMOVED?"
  Same preview box as above:
    Title: "Your Request Was Removed"
    Body: [typed message]
  
  Cancel / "Delete Request" DarkRed
  Loading: "Deleting request and notifying requester…"
  Success: "Request deleted. Requester has been notified."
```

---

### 33.28 Admin — Delete User Account

**Type:** Glass Bottom Sheet (most serious admin action).

```
GlassBottomSheet:
  Title: Fraunces 24sp "Delete User Account?"
  User context card (avatar + name + email + account type + listing count)
  
  Consequences (DarkRedM, 12dp corner, 14dp padding):
    DarkRed X icon (16dp) + "This will permanently delete:" (13sp DarkRed Bold)
    List: All listings · All requests · All conversations · Subscription data · Profile
    "This cannot be undone." (13sp DarkRed)
  
  Required textarea: "REASON FOR DELETION" (internal admin note, NOT sent to user)
    Placeholder: "Internal reason (not shown to user)"
    Min 10 characters. maxLength 500.
  
  Notification toggle (Row with ToggleSwitch):
    "Send notification to user before deletion"
    Default: ON
    If ON: shows notification message textarea:
      Pre-filled: "Your NesCo account has been removed for violating our terms of service."
      Editable. maxLength 300.
  
  Acknowledge checkbox:
    "I confirm this user account and all associated data will be permanently deleted."
  
  "Delete Account" DarkRed (disabled until checkbox + reason filled)
  Cancel GhostButton

Loading: multi-step (same pattern as user-side delete, Section 33.22):
  "Removing listings…"
  "Removing requests…"
  "Clearing conversations…"
  "Deleting profile…"
  If notification toggle ON: "Sending notification…" appears as first step

Success: "Account deleted." + row removed from Users table
Error: "Failed. Please try again."
```

---

### 33.29 Admin — Block Listing

**Type:** Glass Dialog.

```
Icon: Shield (DarkRed circle)
Title: "Block Listing?"
Listing title shown in quotes below title.

Body:
  "• The listing will be hidden from all users
   • The owner will not be able to republish it without admin review
   • A notification will be sent to the owner"

"Block reason (optional)" — UnderlineInputField / textarea
  Pre-filled: "This listing was blocked for violating our content policy."
  Editable. maxLength 200.

Cancel / "Block Listing" DarkRed

Loading: "Blocking listing…" + spinner
Success: toast "Listing blocked. Owner notified." + status badge in table updates
```

---

### 33.30 Admin — Block User

**Type:** Glass Bottom Sheet.

```
Title: "Block User?"
User context card (name + email)

Body:
  "• The user will be signed out immediately
   • They will see a "Your account has been blocked" screen
   • They will not be able to post listings or requests
   • All their listings will be hidden"

"Block reason (sent to user)" — textarea, required, maxLength 200
  Default: "Your account has been suspended for violating our terms of service."
  This is shown on the blocked screen in the app.

Acknowledge checkbox: "I confirm this user will be blocked and notified."

Cancel / "Block User" DarkRed (requires checkbox + reason)

Loading: "Blocking user…" + spinner
Success: toast "User blocked. They have been notified." + status badge updates in table
```

---

### 33.31 Admin — Reject Subscription Request

**Type:** Glass Dialog (compact).

```
Icon: X (DarkRed circle)
Title: "Reject Subscription?"
User + package info card (compact: name + package name)

"Rejection reason (optional)" — UnderlineInputField
  Placeholder: "Leave a reason (optional — sent to user)"
  maxLength 200

Cancel / "Reject" DarkRed

Loading: spinner
Success: toast "Subscription rejected. User notified." + status updates
```

---

### 33.32 Admin — Delete Package

**Type:** Glass Dialog.

```
Icon: Package (DarkRed circle)
Title: "Delete Package?"
Package name in quotes.

Body:
  "• This package will be permanently removed
   • Users currently on this plan will not be affected until expiry
   • New users will not be able to purchase it"

Acknowledge checkbox: "I understand existing subscribers will keep their current plan until expiry."

Cancel / "Delete Package" DarkRed (requires checkbox)

Loading: "Removing package…" + spinner
Success: toast "Package deleted." + row removed
```

---

### 33.33 Admin — Delete Feature Request (Feedback)

**Type:** Glass Dialog (minimal).

```
Icon: FileText (DarkAcc circle — not DarkRed, low stakes)
Title: "Delete Feedback?"
Feedback title shown in quotes.

Body: "This feedback item will be permanently removed."

No acknowledge checkbox.

Cancel / "Delete" DarkAcc fill (lower severity)

Loading: "Removing…" + spinner (no loading message text needed)
Success: toast "Feedback deleted." + row removed
```

---

### 33.34 Admin — Dismiss Report

**Type:** Glass Dialog.

```
Icon: Shield (DarkBg4 circle — neutral, dismissing = no action)
Title: "Dismiss Report?"
Reporter + type shown.

Body: "No action will be taken. The content will remain visible. This report will be marked as resolved."

Cancel / "Dismiss Report" DarkT2 fill (neutral — not destructive)

No loading state needed (quick Firestore update).
Success: toast "Report dismissed." + status updates to "resolved".
```

---

### 33.35 Admin — Reject Feature Slider

**Type:** Glass Dialog.

```
Icon: Star (DarkRed circle)
Title: "Reject Slider?"
User name + linked listing shown.

Body: "The user's featured slider submission will be rejected and they will be notified."

"Rejection reason (optional)" — UnderlineInputField
  Placeholder: e.g. "Image does not meet 16:9 ratio requirement"
  maxLength 200

Cancel / "Reject Slider" DarkRed

Loading: "Rejecting submission…" + spinner
Success: toast "Slider rejected. User notified." + status updates
```

---

## 34. CODE_REVIEW.md Spec

After every module the agent creates `/CODE_REVIEW_<MODULE>.md` using this exact template:

```markdown
# Code Review — Module: [Module Name]
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-name
**Spec version:** v2.3.0

---

## ✅ Architecture Compliance
- [ ] Domain layer has zero Android imports
- [ ] ViewModels do not hold Activity references
- [ ] Repository interface in domain, implementation in data
- [ ] No pass-through UseCases
- [ ] Koin modules declared for all new classes

---

## ✅ Design Reference Verification (design_references.md)

For each screen in this module:

| Screen | Reference Component | BG Colour ✓ | Radii ✓ | Font/Size/Weight ✓ | Icons ✓ | Animations ✓ | States ✓ |
|---|---|---|---|---|---|---|---|
| ExampleScreen | function Home() | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |

For any ✗ — describe discrepancy:
> DISCREPANCY: [Screen] — [property] — EXPECTED: [reference value] — ACTUAL: [implemented value]

Visual discrepancies are BLOCKERS. Do not mark module complete with any open ✗ entries unless client approves deviation.

---

## ✅ Design System Compliance
- [ ] No hardcoded colour values — all from Section 3.2 tokens
- [ ] No hardcoded size values — all from Section 3.3 constants
- [ ] Fonts: Fraunces for display/headings, Plus Jakarta Sans for body/UI
- [ ] All icons from NescoIcons.kt (Section 3.7) — no Material Icons substitutes
- [ ] No design components modified — only extended
- [ ] Screen transitions match Section 3.5
- [ ] Keyframe animations match Section 3.6
- [ ] FCM used — zero OneSignal references anywhere in codebase

---

## ✅ Glassmorphic Dialogs Compliance (Section 33)
- [ ] All destructive actions use GlassDialog or GlassBottomSheet
- [ ] No plain Material AlertDialog for destructive actions
- [ ] GlassDialogBg + GlassDialogBorderBrush applied correctly (dark + light mode)
- [ ] Scrim uses backdrop blur (API 31+) with correct fallback for API 30
- [ ] All delete/block buttons use DarkRed fill with white text
- [ ] Three-phase flow implemented: Confirm → Loading → Result
- [ ] NescoDeleteSpinner used during loading phase (not CircularProgressIndicator)
- [ ] Loading phase is non-dismissable (back press + tap-outside both blocked)
- [ ] Success phase auto-dismisses after 1.8 seconds
- [ ] Error phase shows "Try Again" + "Cancel" options
- [ ] Acknowledge checkboxes present where required (see Section 33 per-dialog spec)
- [ ] Dialog button order: Cancel ABOVE destructive action

---

## ✅ Journey Coverage (Section 32)
For this module, confirm all applicable journeys implemented:
- [ ] Edit journey (pre-populated form) if module has a create form
- [ ] Success screen if module has a primary action
- [ ] Limit warning sheet if module has quotas
- [ ] Empty state on all list screens
- [ ] Shimmer loading state on all data-fetching screens
- [ ] Unauthenticated CTA redirect where auth-gated
- [ ] All relevant delete dialogs from Section 33 implemented

---

## ✅ Performance Checks
- [ ] No network calls in Composables
- [ ] remember{} for expensive computations
- [ ] derivedStateOf{} for computed values
- [ ] LazyColumn/LazyVerticalGrid uses key {}
- [ ] No memory leak patterns detected
- [ ] DisposableEffect for subscriptions
- [ ] repeatOnLifecycle(STARTED) for Flow collections

---

## ✅ Code Quality
- [ ] All strings in res/values/strings.xml
- [ ] No hardcoded values anywhere
- [ ] Unit tests written: success + failure + loading for every ViewModel function
- [ ] ./gradlew test → ✅ PASSING
- [ ] ./gradlew assembleDebug → ✅ PASSING

---

## ⚠️ Lint Findings (Non-Blocking — Nice to Have)
| ID | File | Line | Finding | Severity |
|----|------|------|---------|----------|

---

## 📝 Notes
**DO NOT change design specifications based on lint findings.**
**DO NOT resolve design discrepancies without client approval.**
**Visual deviations from design_references.md are always blockers.**
```

---

*End of Requirements Specification v2.3.0*  
*This is the single source of truth. Sections 3 (Design System), 33 (Delete Dialogs + Glass Design), and 34 (CODE_REVIEW template) are immutable. Any scope change requires bumping the version number and updating this document first.*
