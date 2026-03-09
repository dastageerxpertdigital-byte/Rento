# RentO — Android Specification Master Index
> Generated: February 2026 | Status: All Android Modules Specified

---

## Android Feature Modules (15 Total)

| File | Module | Covers | Tasks | Status |
|------|--------|--------|-------|--------|
| ANDROID_MODULE_01.md | Design System & Shared Components | Color tokens, typography, RentoChip, PrimaryButton, GhostButton, OutlinePrimaryButton, RentoIconButton, SectionLabel, Badge, GlassDialog, GlassBottomSheet, ProgressStepBar, MapBackground, RadiusCircle, BottomNavBar, shimmer, animations | 22 tasks | ✅ |
| ANDROID_MODULE_02.md | Authentication | Login, Register, Forgot Password, Google Sign-In, onboarding flow, AuthViewModel, Firebase Auth, JWT refresh | 18 tasks | ✅ |
| ANDROID_MODULE_03.md | Home & Discovery | Home feed (dual marketplace), Listing/Request tabs, PropertyCard, TenantRequestCard, FilterSheet, SearchBar, AddOverlaySheet, FAB, HomeViewModel, pagination | 24 tasks | ✅ |
| ANDROID_MODULE_04.md | Listing Detail | Glassmorphic gallery, property info, amenities, nearby, host block, report, sticky bottom bar, ReportSheet | 18 tasks | ✅ |
| ANDROID_MODULE_04_S29_S30.md | Listing Detail (Sections 29-30) | CODE_REVIEW template sections 29-30 supplement | — | ✅ |
| ANDROID_MODULE_05.md | Tenant Request Detail | Profile block, budget banner, PreferredArea section, MapBackground/RadiusCircle (static), requirements chips, about tenant, sticky bottom bar variants, ReportSheet (moved to shared) | 17 tasks | ✅ |
| ANDROID_MODULE_06.md | Property Listing Form | 11-step wizard, FormShell, intent cards, property type chips, availability, pricing, location, property details, amenities, nearby, photo upload, Gemini AI summary, review & publish, draft persistence, ListingFormViewModel | 22 tasks | ✅ |
| ANDROID_MODULE_07.md | Tenant Request Form | 6-step wizard (reuses FormShell), intent cards, property type chips, budget input, Where & Radius (animated RadiusCircle slider), preferences, bio textarea, DataStore draft, TenantRequestFormViewModel | 17 tasks | ✅ |
| ANDROID_MODULE_08.md | Chat | AES-256-GCM encryption (ChatCrypto, PBKDF2), ChatList, ChatDetail, sender/receiver bubbles, date separators, long-press context menu, edit/delete message, delete conversation, ChatUpgradePrompt, daily limits, Report User | 22 tasks | ✅ |
| ANDROID_MODULE_09.md | Notifications (FCM) | NescoFCMService, notification channels, token management, topic subscription, 17 notifType deeplinks, InAppNotificationBanner (4s auto-dismiss), NotificationsScreen, unread dot (SharedPreferences), NotifDeepLinkHandler | 15 tasks | ✅ |
| ANDROID_MODULE_10.md | Saved Items | Two-tab screen (Properties + Requests), swipe-to-remove, SavedRepository, optimistic unsave, empty states | 12 tasks | ✅ |
| ANDROID_MODULE_11.md | My Listings & My Requests (Dashboard) | 5-tab MyListings (Published/Drafts/Pending/Rejected/Blocked), 4-tab MyRequests (Active/Pending/Closed/Rejected), slot banners, listing/request cards, unpublish/close/delete actions | 17 tasks | ✅ |
| ANDROID_MODULE_12.md | Profile & Edit Profile | Profile screen, edit profile, avatar upload, mode switcher (Hosting/Looking), my listings/requests shortcuts, account stats | 14 tasks | ✅ |
| ANDROID_MODULE_13.md | Settings, Packages, Force Update, Connectivity, Feedback | Settings screen, theme toggle, Packages & Subscriptions (bank transfer flow), Force Update dialog, No Internet overlay, Feedback form, DeleteAccount | 20 tasks | ✅ |
| ANDROID_MODULE_14.md | Map View | Google Maps Compose full-screen, custom listing markers, cluster markers, request circle pins, mode toggle, bounding box fetch, ListingMapSheet, RequestMapSheet | 16 tasks | ✅ |
| ANDROID_MODULE_15.md | My Dashboard — Extended | Comprehensive re-spec of My Listings + My Requests with DashboardShell, SlotBanner, all card variants (Published, Draft, Pending, Rejected, Blocked, Active, Closed), DashboardShimmer | 17 tasks | ✅ |

> **Note on M11 vs M15:** Module 11 and Module 15 both cover My Listings & My Requests. Module 15 is the more comprehensive and detailed version with the full `DashboardShell` architecture. The implementation agent should use **Module 15** as the authoritative spec and treat Module 11 as a summary reference.

---

## Specification Coverage Map

| Spec Section | Android Module |
|-------------|---------------|
| §3 Design System | Module 01 |
| §6 Authentication | Module 02 |
| §7 Profile & Onboarding | Module 12 |
| §8 Home & Discovery | Module 03 |
| §9 Property Listing Form | Module 06 |
| §10 Tenant Request Form | Module 07 |
| §11 Listing Detail | Module 04 |
| §12 Tenant Request Detail | Module 05 |
| §13 Map View | Module 14 |
| §14 Saved Items | Module 10 |
| §15 Chat | Module 08 |
| §16 Notifications (FCM) | Module 09 |
| §17 Packages & Subscriptions | Module 13 |
| §18 My Listings & My Requests | Module 11 + Module 15 |
| §19 Settings & Account | Module 13 |
| §20 Feedback | Module 13 |
| §21 Force Update & Connectivity | Module 13 |
| §24 Remote Config & Gemini | Module 06 (GeminiRepository) |
| §28 Security Rules | → See FIREBASE_SECURITY_RULES.md (pending) |
| §22 Admin Web Portal | → See ADMIN_PORTAL_SPEC.md (pending) |
| §26 Cloud Functions | → See CLOUD_FUNCTIONS_SPEC.md (pending) |

---

## Remaining Deliverables (Not Yet Generated)

| File | Covers | Priority |
|------|--------|----------|
| FIREBASE_SECURITY_RULES.md | Complete Firestore security rules (spec §28), all collection rules, admin vs user vs public access | High |
| CLOUD_FUNCTIONS_SPEC.md | All Cloud Functions: sendFCMNotification, broadcastToAll, cleanupDeletedUser, cleanupDeletedListing, cleanupDeletedRequest, notifyAdmin, deleteAccount cascade | High |
| ADMIN_PORTAL_SPEC.md | Admin Web Portal (React/Next.js): Auth, Listings moderation, Requests moderation, Users management, Subscriptions approval, Feature Requests, Gemini Key Logs, Broadcast push, Analytics | Medium |

---

## Implementation Order (Recommended for Agent)

```
Phase 1 — Foundation
  M01 → M02 → M03

Phase 2 — Content Screens
  M04 → M05 → M06 → M07

Phase 3 — Social & Messaging
  M08 → M09 → M10

Phase 4 — User Management
  M11/M15 → M12 → M13

Phase 5 — Discovery
  M14

Phase 6 — Backend
  Firebase Security Rules → Cloud Functions

Phase 7 — Admin
  Admin Web Portal
```

---

## Total Task Count

| Phase | Modules | Tasks |
|-------|---------|-------|
| Foundation | 01–03 | ~64 tasks |
| Content Screens | 04–07 | ~74 tasks |
| Social & Messaging | 08–10 | ~49 tasks |
| User Management | 11–13 | ~51 tasks |
| Discovery | 14 | 16 tasks |
| Extended Dashboard | 15 | 17 tasks |
| **Total Android** | **15 modules** | **~271 tasks** |

---

*Generated by Claude. Spec source: REQUIREMENTS_SPECIFICATION_v2_3.md*
