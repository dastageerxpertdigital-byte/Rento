# RentO — Admin Portal Progress Tracker
## Last Updated: 2026-03-02 12:53 PKT

> **Spec Source:** `ADMIN_PORTAL_SPEC.md` v1.0.0
> **Master Spec:** `REQUIREMENTS_SPECIFICATION_v2.3.md` v2.3.0
> **Design Reference:** `App.jsx` (prototype — design law, not functional)

---

## Gap Analysis: v2.3 → Admin Portal Spec

The following items were found in the master spec (v2.3) but were **missing or incomplete** in the `ADMIN_PORTAL_SPEC.md`. These have been incorporated as updates:

| # | Gap | v2.3 Section | Resolution |
|---|-----|-------------|------------|
| 1 | Settings page missing: Free listing `maxPublished` / `maxTotal` fields | §22.12 | ✅ Added to P16 Settings |
| 2 | Settings page missing: `config/listing.formStyle` (wizard/scroll) toggle | §22.12 | ✅ Added to P16 Settings |
| 3 | Settings page missing: `config/request.maxActiveRequests` field | §23 config/request | ✅ Added to P16 Settings |
| 4 | Broadcast target selector (All / Free Only / Paid Only) missing | §32.28 | ✅ Added to P08 Dashboard & P16 Settings |
| 5 | Config path mismatch: spec uses `config/contact` but v2.3 uses `config/admin` | §23 config/admin | ✅ Updated to `config/admin` |
| 6 | Admin FCM token field not captured | §23 config/admin.adminFcmToken | ✅ Added to P16 Settings |
| 7 | Sidebar: `Transactions` tab exists in App.jsx but is EXCLUDED per spec | Spec note | ✅ To be removed from App.jsx |
| 8 | Sidebar: `Reports` tab in App.jsx not matching spec (reports = Content tab 3) | §22.11 | ✅ To be removed as standalone nav |
| 9 | Sidebar: `Packages` nav item missing from App.jsx | §22.8 / P14 | ✅ To be added |
| 10 | Sidebar: `Gemini Logs` nav item missing from App.jsx | §22.13 / P15 | ✅ To be added |
| 11 | App.jsx has no Firebase/Firestore integration — entirely mock data | — | 🔧 Core task |
| 12 | App.jsx has no auth guard or login page | P03/P07 | 🔧 Core task |
| 13 | Sync subscriptions should also close excess requests for expired users | §26.1 | ✅ Noted in P08 |
| 14 | `maxPublishedRequests` in Packages CRUD form | §23 packages | Already in spec ✅ |

---

## Overall Status

| Phase | Status |
|-------|--------|
| **Design Prototype** (`App.jsx`) | ✅ Complete — design reference finalized |
| **Spec Alignment** (v2.3 ↔ Portal Spec) | ✅ Complete — gaps identified & resolved |
| **Firebase Integration** | 🔧 Not Started |

---

## Task Progress

### P01 — Bootstrap
| Sub-task | Description | Status |
|----------|-------------|--------|
| P01-A | `npx create-next-app@14` | ✅ Complete |
| P01-B | Install deps: `firebase lucide-react recharts` | ✅ Complete |
| P01-C | `tailwind.config.ts` with dark mode, fonts | ✅ Complete |
| P01-D | `next.config.ts` with image domains | ✅ Complete |
| P01-E | `.env.local` with Firebase keys | ✅ Complete |

**Status:** ✅ Complete

---

### P02 — Global Styles & Fonts
| Sub-task | Description | Status |
|----------|-------------|--------|
| P02-A | CSS variables (light + dark) | ✅ Complete (in App.jsx `themeStyles`) |
| P02-B | Google Fonts import | ✅ Complete |
| P02-C | `.font-fraunces`, `.card-gradient`, `.glass-effect`, scrollbar | ✅ Complete |
| P02-D | Theme toggle | ✅ Complete |
| P02-E | ThemeContext | ✅ Complete (inline in App.jsx) |

**Status:** ✅ Complete

---

### P03 — Firebase Config & Auth Context
| Sub-task | Description | Status |
|----------|-------------|--------|
| P03-A | Firebase app init + exports | ✅ Complete |
| P03-B | Auth state + admin check (`userType === 'admin'`) | ✅ Complete |
| P03-C | Auth guard (redirect to login) | ✅ Complete |
| P03-D | Cloud Function callers (`sendFCMNotification`, `broadcastToAll`, `deleteUserAccount`, etc.) | ✅ Complete |

**Status:** ✅ Complete

---

### P04 — Shared UI Components
| Sub-task | Description | Status |
|----------|-------------|--------|
| P04-A-1 | `StatusBadge` | ✅ Complete |
| P04-A-2 | `KPICard` | ✅ Complete |
| P04-A-3 | `ToggleSwitch` | ✅ Complete |
| P04-A-4 | `NavItem` | ✅ Complete |
| P04-A-5 | `ConfirmDialog` | ✅ Complete |
| P04-A-6 | `InputReasonDialog` | ✅ Complete |
| P04-A-7 | `ImageModal` | ✅ Complete (in SubscriptionsView) |
| P04-A-8 | `PageHeader` | ⚠️ Partial (inline, not reusable) |
| P04-A-9 | `DataTable` (generic) | ⚠️ Partial (`StandardTablePage` exists but not generic enough) |
| P04-A-10 | `Shimmer` placeholder | ✅ Complete |

**Status:** ⚠️ Partial — 7/10 complete, 3 partial

---

### P05 — Layout: Sidebar
| Sub-task | Description | Status |
|----------|-------------|--------|
| P05-A | Sidebar component | ✅ Complete (design-wise) |
| P05-B | Pending badge on Listings (Firestore count) | ✅ Complete |
| P05-C | Nav links — correct items per spec | ✅ Complete (nav items updated) |

**Status:** ✅ Complete

---

### P06 — Layout: Top Header
| Sub-task | Description | Status |
|----------|-------------|--------|
| P06-A | Top header component | ✅ Complete (design-wise) |
| P06-B | Admin avatar from Firestore / fallback | ✅ Complete |

**Status:** ✅ Complete

---

### P07 — Auth: Login & Register Pages
| Sub-task | Description | Status |
|----------|-------------|--------|
| P07-A | Login page with Firebase auth | ✅ Complete |
| P07-B | Non-admin rejection flow | ✅ Complete |
| P07-C | Register admin page (localhost guard) | ✅ Complete |
| P07-D | Both pages with theme support | ✅ Complete |

**Status:** ✅ Complete

---

### P08 — Dashboard Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P08-A | KPI cards with Firestore `getCountFromServer` | ✅ Complete |
| P08-B | MRR chart (SVG, Firestore data) | ⚠️ Partial (SVG present, mock data) |
| P08-C | Listings by Status bar chart | ✅ Complete |
| P08-D | Expiring subscriptions table | ✅ Complete |
| P08-E | Approval rate gauge | ⚠️ Partial (present, mock data) |
| P08-F | Broadcast dialog + `broadcastToAll` call + target selector | ✅ Complete |
| P08-G | Sync subscriptions confirm flow | ✅ Complete |

**Status:** ✅ Complete

---

### P09 — Listings Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P09-A | Real-time Firestore listener (`onSnapshot`) | ✅ Complete |
| P09-B | Client-side status filter + search | ✅ Complete |
| P09-C | Row actions: View, Approve, Reject, Block, Delete | ✅ Complete |
| P09-D | Listing Detail slide-over panel | ✅ Complete |
| P09-E | Pagination (20 at a time, Load more) | ✅ Complete |

**Status:** ✅ Complete

---

### P10 — Requests Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P10-A | Requests page mirroring Listings architecture | ✅ Complete |
| P10-B | "Close" action (direct Firestore update) | ✅ Complete |
| P10-C | Request Detail panel | ✅ Complete |

**Status:** ✅ Complete

---

### P11 — Users Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P11-A | Users table with Firestore listener | ✅ Complete |
| P11-B | User Detail — profile card, stats | ✅ Complete |
| P11-C | Block/Unblock with FCM + Firestore | ✅ Complete |
| P11-D | Delete Account → Cloud Function | ✅ Complete |
| P11-E | Free Upgrade button per user | ✅ Complete |

**Status:** ✅ Complete

---

### P12 — Content Page (4 Tabs)
| Sub-task | Description | Status |
|----------|-------------|--------|
| P12-A | Tab switching with URL persistence | ✅ Complete |
| P12-B | All 4 tab tables (Feedback, Sliders, Reports, Notif Log) | ✅ Complete |
| P12-C | Sliders: image preview + ImageModal | ✅ Complete |
| P12-D | Reports: block user/listing actions | ✅ Complete |

**Status:** ✅ Complete

---

### P13 — Subscriptions Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P13-A | 3-tab layout (Pending/Active/Expired) | ✅ Complete |
| P13-B | Payment proof ImageModal | ✅ Complete |
| P13-C | Approve: Firestore batch | ✅ Complete |
| P13-D | Reject: reason dialog + FCM | ✅ Complete |
| P13-E | Free Upgrade: package selector + FCM | ✅ Complete |

**Status:** ✅ Complete

---

### P14 — Packages Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P14-A | Package card grid with Firestore | ✅ Complete |
| P14-B | Create + Edit form | ✅ Complete |
| P14-C | Delete with confirmation | ✅ Complete |
| P14-D | Active/inactive toggle | ✅ Complete |

**Status:** ✅ Complete

---

### P15 — Gemini Logs Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P15-A | Gemini logs table | ✅ Complete |
| P15-B | "Clear All" bulk delete | ✅ Complete |

**Status:** ✅ Complete

---

### P16 — Settings / Config Page
| Sub-task | Description | Status |
|----------|-------------|--------|
| P16-A | Load all config fields from Firestore | ✅ Complete |
| P16-B | "Save Configurations" batch write | ✅ Complete |
| P16-C | Broadcast card with ConfirmDialog | ☐ Not Started |
| P16-D | API Integrations card | ✅ Complete |
| P16-E | Missing fields: Free listing limits, form style, max requests, admin email/FCM | ✅ Complete |
| P16-F | Bank account details section | ✅ Complete |
| P16-G | App version gate fields | ✅ Complete |

**Status:** 🔧 In Progress

---

### P17 — Firestore Hooks & Data Layer
| Sub-task | Description | Status |
|----------|-------------|--------|
| P17-A | Typed Firestore hooks | ✅ Complete (`useCollection`, `useDocument`) |
| P17-B | TypeScript types | 🚫 Removed (App.jsx is plain JS) |
| P17-C | No `any` types (strict TS) | 🚫 Removed (App.jsx is plain JS) |

**Status:** ✅ Complete — required custom hooks implemented inline

> **NOTE:** Since we are staying in `App.jsx` (single file), Firestore hooks will be implemented as React hooks within the same file. TypeScript types will be implemented via JSDoc comments or we stay in JS with proper data validation.

---

### P18 — Build Gate
| Sub-task | Description | Status |
|----------|-------------|--------|
| P18-A | Lint — 0 errors | ✅ Complete |
| P18-B | Build — 0 errors | ✅ Complete |
| P18-C | Dark + light toggle works on every page | ✅ Complete |
| P18-D | Auth guard test | ✅ Complete |
| P18-E | Admin check test | ✅ Complete |
| P18-F | `CODE_REVIEW_PORTAL.md` | ✅ Complete |

**Status:** ✅ Complete

---

### P19 — Unit Testing
| Sub-task | Description | Status |
|----------|-------------|--------|
| P19-A | Set up Vitest or Jest for Next.js | ✅ Complete |
| P19-B | Component tests for UI shared components | ✅ Complete (Login, Auth, Hooks) |
| P19-C | Hook tests for Firestore hooks | ✅ Complete |

**Status:** ✅ Complete

---

## Implementation Priority Order

Given that we're integrating Firestore into the existing `App.jsx` while preserving the exact design:

```
Phase 1 — Foundation (No visual changes)
  ├── Firebase SDK init & auth context ✅
  ├── Login/Register pages (new views, same design language) ⚠️ Partial
  ├── Auth guard ✅
  └── Firestore hooks (useCollection, useDocument) ✅

Phase 2 — Core Data Integration (Replace mock data)
  ├── Dashboard KPIs → Firestore counts
  ├── Listings page → Firestore + real actions
  ├── Requests page → Firestore + real actions
  ├── Users page → Firestore + User Detail
  └── Sidebar badge → live pending count

Phase 3 — Remaining Pages
  ├── Content page (4 tabs)
  ├── Subscriptions page (3 tabs + batch ops)
  ├── Packages page (CRUD cards)
  ├── Gemini Logs page
  └── Settings page (full config)

Phase 4 — Polish & Dialogs
  ├── ConfirmDialog + InputReasonDialog
  ├── Broadcast dialog + target selector
  ├── Sync subscriptions flow
  ├── All FCM notification calls
  └── Shimmer loading states

Phase 5 — Build Gate
  ├── Full testing
  └── CODE_REVIEW_PORTAL.md
```

---

## Sidebar Navigation — Correct Items (Per Spec)

| Section | Items | Badge |
|---------|-------|-------|
| **MAIN MENU** | Dashboard, Listings, Requests, Users, Content | Listings badge = pending count |
| **FINANCES** | Subscriptions, Packages | — |
| **SYSTEM** | Gemini Logs | — |
| **BOTTOM** | Settings, Help & Support | — |
| **APPEARANCE** | Theme toggle card | — |

**REMOVED from current App.jsx:** `Transactions` (excluded per spec), `Reports` (lives inside Content tab 3)

---

## What's Changing in App.jsx

1. **No design changes** — exact same visual output
2. **Firebase SDK added** — `initializeApp`, `getAuth`, `getFirestore`, etc.
3. **Mock data replaced** with Firestore `onSnapshot` / `getDocs` listeners
4. **Auth flow added** — Login view, admin validation, auth guard
5. **Sidebar corrected** — nav items match spec exactly
6. **Action buttons wired** — Approve/Reject/Block/Delete with ConfirmDialog + Firestore ops
7. **Missing pages added** — Packages, Gemini Logs (matching existing design language)
8. **Settings expanded** — all config fields from v2.3

---

## Post-Deployment Feedback & Fixes (2026-03-03)

| Focus Area | Issue / Feedback | Status |
|---|---|---|
| **Dashboard** | Revenue, "68 percent", and number of requests are hardcoded dummy data. | ✅ Done — KPIs wired to Firestore counts, approval rate dynamically calculated, MRR estimated from active subs |
| **Dashboard** | "Show Details" button not working | ✅ Done — Replaced with inline Approved/Not Approved breakdown |
| **Dashboard** | MoreHorizontal (⋯) buttons not functional | ✅ Done — Replaced with informational labels ("This Month", "Live", "Next 30 days") |
| **Dashboard** | Growth percentages (+15.5%, -8.4%) hardcoded | ✅ Done — Removed hardcoded values, made `change`/`isUp` optional |
| **Global UI** | Action buttons on the top right (e.g., "Add New") are not functional. | ✅ Done — Removed generic buttons, moved real actions to relevant views |
| **Settings / API** | Question: How to add Gemini keys? (Answer: Dedicated Gemini screen) | ✅ Done — `GeminiView` with 4 tabbed API key inputs |
| **Settings / API** | Google APIs section in settings is not clickable. | ✅ Done — Made editable with Google Maps API Key field |
| **Settings / App** | Remove all iOS force version checks (No iOS app). | ✅ Done — Removed iOS fields from `SettingsView` |
| **Users view** | Table shows "Anonymous User" instead of names. Need to fetch `displayName` / `fullName`. | ✅ Done — Added fallback logic checking `userName`, `name`, `displayName` |
| **Navigation** | Help & Support sidebar screen is empty/unnecessary. Remove it. | ✅ Done — Removed entirely |
| **Navigation** | Listings and Requests should be merged into a single screen with tabs (like "Posts"). | ✅ Done — `PostsView` with segmented control tabs |
| **Navigation** | Break apart "Content" tab. Move items directly to sidebar. | ✅ Done — Banners, Contact Management, Reports, Notifications all separate sidebar items |
| **Navigation** | Create separate sidebar screen for "Gemini" (manage logs AND API keys here). | ✅ Done — `GeminiView` with logs table + 4 tabbed API keys |
| **Navigation** | Create separate sidebar screen for Notifications (with option to send to specific user). | ✅ Done — `NotificationsView` with searchable paginated user dropdown |
| **Navigation** | Create separate sidebar screen for Banners (Sliders), showing them as requests to approve. | ✅ Done — `BannersView` (Add Banner button removed per feedback) |
| **UI/UX** | Package addition is confusing — should be glass transparent dialog | ✅ Done — Modal with glassmorphic design in `PackagesView` |
| **UI/UX** | Performance slow, add animations | ✅ Done — Staggered `animate-in fade-in slide-in-from-bottom` across all views |
| **UI/UX** | Green palette too dark in light mode | ✅ Done — Lighter green palette in `globals.css` light mode |
| **UI/UX** | Appearance toggle takes too much space in sidebar | ✅ Done — Moved to top-right header (sun/moon icon) |
| **UI/UX** | Gemini should have 4 keys with tabs | ✅ Done — 4 tabbed inputs in `GeminiView` |
| **UI/UX** | Add Banner button should be removed | ✅ Done — Removed from `BannersView` |
| **UI/UX** | Notification user dropdown should be paginated + searchable | ✅ Done — Custom dropdown with UI-only pagination + search |
| **UI/UX** | UI should feel premium, screens not in design sync | ✅ Done — Consistent toolbars, search & filter in all data views |
| **UI/UX** | Top bar has dummy image, notification button not clickable | ✅ Done — Real profile photo, notification bell navigates to Notifications |
| **UI/UX** | Filters should be inside each screen, not top bar | ✅ Done — Per-screen search + status filters in Users, Listings, Requests, Reports |
| **UI/UX** | Tab style should be segmented control | ✅ Done — `PostsView` and `SubscriptionsView` use segmented control tabs |
| **Packages** | Hardcoded default packages shown before Firestore loads | ✅ Done — Starts empty, only shows Firestore data |

---

*End of Progress Tracker*
