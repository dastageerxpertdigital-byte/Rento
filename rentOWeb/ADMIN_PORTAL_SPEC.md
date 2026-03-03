# RentO — Admin Web Portal
## Complete Engineering Specification

> **Version:** 1.0.0  
> **Branch:** `feature/admin-portal`  
> **Stack:** Next.js 14 (App Router) · TypeScript · Tailwind CSS · Firebase SDK v10 · Lucide React  
> **Design Source:** Provided React design reference (dark theme default, light mode toggle)  
> **Audience:** Web / Portal Agent  
>
> ⚠️ **Transactions section is EXCLUDED** — not applicable to this platform.  
> ⚠️ Design is pixel-matched from the reference component. All colour values, spacing, radii, and typography are extracted verbatim.

---

## Table of Contents

1. [Project Setup](#1-project-setup)
2. [Design System — CSS Tokens](#2-design-system--css-tokens)
3. [File Structure](#3-file-structure)
4. [Task List](#4-task-list)
5. [Task P01 — Bootstrap](#task-p01--bootstrap)
6. [Task P02 — Global Styles & Fonts](#task-p02--global-styles--fonts)
7. [Task P03 — Firebase Config & Auth Context](#task-p03--firebase-config--auth-context)
8. [Task P04 — Shared UI Components](#task-p04--shared-ui-components)
9. [Task P05 — Layout: Sidebar](#task-p05--layout-sidebar)
10. [Task P06 — Layout: Top Header](#task-p06--layout-top-header)
11. [Task P07 — Auth: Login & Register Pages](#task-p07--auth-login--register-pages)
12. [Task P08 — Dashboard Page (`/`)](#task-p08--dashboard-page-)
13. [Task P09 — Listings Page (`/listings`)](#task-p09--listings-page-listings)
14. [Task P10 — Requests Page (`/requests`)](#task-p10--requests-page-requests)
15. [Task P11 — Users Page (`/users`)](#task-p11--users-page-users)
16. [Task P12 — Content Page (`/content`)](#task-p12--content-page-content)
17. [Task P13 — Subscriptions Page (`/subscriptions`)](#task-p13--subscriptions-page-subscriptions)
18. [Task P14 — Packages Page (`/packages`)](#task-p14--packages-page-packages)
19. [Task P15 — Gemini Logs Page (`/gemini-logs`)](#task-p15--gemini-logs-page-gemini-logs)
20. [Task P16 — Settings / Config Page (`/settings`)](#task-p16--settings--config-page-settings)
21. [Task P17 — Firestore Hooks & Data Layer](#task-p17--firestore-hooks--data-layer)
22. [Task P18 — Build Gate](#task-p18--build-gate)
23. [Journey Coverage Checklist](#23-journey-coverage-checklist)

---

## 1. Project Setup

```
npx create-next-app@14 rento-admin \
  --typescript \
  --tailwind \
  --eslint \
  --app \
  --src-dir \
  --import-alias "@/*"

cd rento-admin
npm install firebase lucide-react recharts
npm install -D @types/node
```

**`tailwind.config.ts`** — extend with CSS variable references:
```typescript
import type { Config } from 'tailwindcss';

const config: Config = {
  darkMode: 'class',
  content: ['./src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        fraunces: ['Fraunces', 'serif'],
        sans:     ['Plus Jakarta Sans', 'sans-serif'],
      },
      borderRadius: {
        card: '24px',
        chip: '100px',
      },
    },
  },
};
export default config;
```

**`next.config.ts`:**
```typescript
const nextConfig = {
  images: {
    domains: ['firebasestorage.googleapis.com', 'i.pravatar.cc'],
  },
};
export default nextConfig;
```

---

## 2. Design System — CSS Tokens

### 2.1 CSS Variables

Defined in `src/app/globals.css`. These are the **exact values** from the design reference.

```css
@import url('https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,300;400;600;700&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap');

:root {
  /* ── Light Mode ─────────────────────────────────────────── */
  --bg0:               #F0FAF6;
  --bg1:               #FFFFFF;
  --bg2:               #FFFFFF;
  --bg3:               #E8F5EE;
  --bg4:               #D6EDE5;
  --pri:               #0C7A50;
  --pri2:              #14A06A;
  --pri-transparent:   rgba(12, 122, 80, 0.15);
  --sec:               #0D9E86;
  --acc:               #B87010;
  --red:               #C04040;
  --blue:              #2E6EA0;
  --t0:                #06201A;
  --t1:                #265044;
  --t2:                #52806E;
  --t3:                #A0C4B8;
  --bd:                #C4E2D8;
  --bd2:               #B0D4C8;
  --nav:               rgba(255, 255, 255, 0.95);
}

.dark {
  /* ── Dark Mode ──────────────────────────────────────────── */
  --bg0:               #04100C;
  --bg1:               #08150F;
  --bg2:               #0D1E17;
  --bg3:               #142A20;
  --bg4:               #1C3529;
  --pri:               #2ECC8A;
  --pri2:              #54DBA2;
  --pri-transparent:   rgba(46, 204, 138, 0.15);
  --sec:               #27B99A;
  --acc:               #F0C94A;
  --red:               #E06060;
  --blue:              #5A9FD4;
  --t0:                #E4F4EC;
  --t1:                #9DC8B4;
  --t2:                #5A8A76;
  --t3:                #2A4A3C;
  --bd:                #142A20;
  --bd2:               #1C3529;
  --nav:               rgba(8, 21, 15, 0.95);
}

/* ── Base ───────────────────────────────────────────────────── */
body {
  font-family: 'Plus Jakarta Sans', sans-serif;
  background-color: var(--bg0);
  color: var(--t0);
}

/* ── Utility Classes ────────────────────────────────────────── */
.font-fraunces   { font-family: 'Fraunces', serif; }
.card-gradient   { background: linear-gradient(135deg, var(--pri), var(--sec)); }
.glass-effect    { background: var(--nav); backdrop-filter: blur(12px); }

/* ── Scrollbar ──────────────────────────────────────────────── */
::-webkit-scrollbar       { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: var(--bg4); border-radius: 10px; }
::-webkit-scrollbar-thumb:hover { background: var(--t3); }
```

### 2.2 Typography Scale

| Use | Font | Weight | Size | Class |
|-----|------|--------|------|-------|
| Page title | Fraunces | 600 | 30px (text-3xl) | `font-fraunces text-3xl font-semibold` |
| Card title large | Fraunces | 700 | 30px (text-3xl) | `font-fraunces text-3xl font-bold` |
| Card title medium | Fraunces | 600 | 18px (text-lg) | `font-fraunces text-lg font-semibold` |
| Section label | Plus Jakarta Sans | 700 | 11px | `text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider` |
| Table header | Plus Jakarta Sans | 700 | 11px | `text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider` |
| Table cell primary | Plus Jakarta Sans | 700 | 13px | `text-[13px] font-bold text-[var(--t0)]` |
| Table cell secondary | Plus Jakarta Sans | 500 | 13px | `text-[13px] font-medium text-[var(--t2)]` |
| Nav label | Plus Jakarta Sans | 600/700 | 13px | `text-[13px] tracking-wide` |
| KPI value | Fraunces | 700 | 30px | `font-fraunces text-3xl font-bold` |
| Badge / chip | Plus Jakarta Sans | 700 | 11px | `text-[11px] font-bold` |
| Body caption | Plus Jakarta Sans | 500 | 12px | `text-[12px] font-medium text-[var(--t2)]` |

### 2.3 Spacing & Radius

| Token | Value | Usage |
|-------|-------|-------|
| Card corner | 24px | All main content cards: `rounded-[24px]` |
| Input corner | 12px | Form inputs: `rounded-xl` |
| Button pill | 100px | Action buttons: `rounded-full` |
| Badge/chip | 6px | Status badges: `rounded-md` |
| Nav item | 16px | Sidebar nav items: `rounded-2xl` |
| Card inner | 16px | Inner nested cards: `rounded-2xl` |
| Sidebar width | 256px | `w-64` |
| Header height | 80px | `h-20` |
| Card padding | 24px | `p-6` |
| Page padding | 32px | `p-8` |

### 2.4 Gradient

```css
/* Primary gradient — used on logo, avatar ring, buttons, left accent strips */
background: linear-gradient(135deg, var(--pri), var(--sec));
```

---

## 3. File Structure

```
src/
├── app/
│   ├── globals.css
│   ├── layout.tsx                    ← root layout (ThemeProvider, AuthGuard)
│   ├── login/page.tsx
│   ├── register-admin/page.tsx       ← localhost only
│   └── (admin)/                      ← route group with shared layout
│       ├── layout.tsx                ← Sidebar + Header shell
│       ├── page.tsx                  ← Dashboard
│       ├── listings/page.tsx
│       ├── requests/page.tsx
│       ├── users/page.tsx
│       │   └── [uid]/page.tsx        ← User Detail
│       ├── content/page.tsx          ← tabbed: Feedback | Sliders | Reports | Notif Log
│       ├── subscriptions/page.tsx
│       ├── packages/page.tsx
│       ├── gemini-logs/page.tsx
│       └── settings/page.tsx
├── components/
│   ├── ui/
│   │   ├── StatusBadge.tsx
│   │   ├── KPICard.tsx
│   │   ├── ToggleSwitch.tsx
│   │   ├── NavItem.tsx
│   │   ├── DataTable.tsx
│   │   ├── ConfirmDialog.tsx
│   │   ├── InputReasonDialog.tsx
│   │   ├── ImageModal.tsx
│   │   ├── PageHeader.tsx
│   │   └── Shimmer.tsx
│   ├── layout/
│   │   ├── Sidebar.tsx
│   │   └── TopHeader.tsx
│   └── charts/
│       ├── MRRLineChart.tsx
│       └── ListingStatusBar.tsx
├── lib/
│   ├── firebase.ts
│   ├── firestore.ts                  ← typed Firestore helpers
│   └── fcm.ts                        ← call sendFCMNotification Cloud Function
├── hooks/
│   ├── useAuth.ts
│   ├── useTheme.ts
│   └── useFirestore.ts
├── context/
│   ├── AuthContext.tsx
│   └── ThemeContext.tsx
└── types/
    └── index.ts                      ← all shared TypeScript types
```

---

## 4. Task List

| ID | Task | Status |
|----|------|--------|
| P01 | Project bootstrap — Next.js 14, deps, tailwind config | ☐ |
| P02 | Global styles, CSS variables, fonts | ☐ |
| P03 | Firebase config + AuthContext + useAuth hook | ☐ |
| P04 | Shared UI components (StatusBadge, KPICard, ToggleSwitch, ConfirmDialog, DataTable, etc.) | ☐ |
| P05 | Sidebar layout component | ☐ |
| P06 | Top Header layout component | ☐ |
| P07 | Login page + Register Admin page (localhost guard) | ☐ |
| P08 | Dashboard page — KPIs, MRR chart, Listings by Status bar, Expiring table, Approval rate gauge | ☐ |
| P09 | Listings page — table, filters, Approve/Reject/Block/Delete actions with dialogs | ☐ |
| P10 | Requests page — table, filters, same action set | ☐ |
| P11 | Users page — table + User Detail modal (block/unblock, delete account) | ☐ |
| P12 | Content page — 4 tabs: Feedback, Sliders, Reports (moderation), Notification Log | ☐ |
| P13 | Subscriptions page — Pending/Active/Expired tabs, payment proof modal, approve/reject, free upgrade | ☐ |
| P14 | Packages page — card grid, create/edit/delete form, active toggle | ☐ |
| P15 | Gemini Logs page — table, bulk clear | ☐ |
| P16 | Settings / Config page — all config fields, approval toggles, bank details | ☐ |
| P17 | Firestore hooks + data layer + Cloud Function callers | ☐ |
| P18 | Build gate — lint, type check, deploy | ☐ |

---

## Task P01 — Bootstrap

### Sub-tasks
- [ ] **P01-A** `npx create-next-app@14 rento-admin --typescript --tailwind --eslint --app --src-dir`
- [ ] **P01-B** Install deps: `npm install firebase lucide-react recharts`
- [ ] **P01-C** Configure `tailwind.config.ts` with `darkMode: 'class'`, fontFamily extensions.
- [ ] **P01-D** Configure `next.config.ts` with image domains.
- [ ] **P01-E** Add `.env.local`:
```
NEXT_PUBLIC_FIREBASE_API_KEY=...
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=...
NEXT_PUBLIC_FIREBASE_PROJECT_ID=...
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=...
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=...
NEXT_PUBLIC_FIREBASE_APP_ID=...
NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID=...
```

---

## Task P02 — Global Styles & Fonts

### Sub-tasks
- [ ] **P02-A** Paste full CSS variables block into `src/app/globals.css` — both `:root` (light) and `.dark` blocks.
- [ ] **P02-B** Add `@import` for Google Fonts at top of `globals.css`.
- [ ] **P02-C** Add `.font-fraunces`, `.card-gradient`, `.glass-effect`, scrollbar CSS.
- [ ] **P02-D** `src/app/layout.tsx` — root layout wraps `<ThemeProvider><AuthProvider>{children}</AuthProvider></ThemeProvider>`.
- [ ] **P02-E** `ThemeContext.tsx`:

```typescript
'use client';
import { createContext, useContext, useEffect, useState } from 'react';

type Theme = 'dark' | 'light';

const ThemeContext = createContext<{
  theme: Theme;
  toggle: () => void;
}>({ theme: 'dark', toggle: () => {} });

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setTheme] = useState<Theme>('dark');   // Default: dark

  useEffect(() => {
    const saved = localStorage.getItem('theme') as Theme | null;
    const initial = saved ?? 'dark';
    setTheme(initial);
    document.documentElement.classList.toggle('dark', initial === 'dark');
  }, []);

  const toggle = () => {
    const next = theme === 'dark' ? 'light' : 'dark';
    setTheme(next);
    localStorage.setItem('theme', next);
    document.documentElement.classList.toggle('dark', next === 'dark');
  };

  return (
    <ThemeContext.Provider value={{ theme, toggle }}>
      {children}
    </ThemeContext.Provider>
  );
}

export const useTheme = () => useContext(ThemeContext);
```

---

## Task P03 — Firebase Config & Auth Context

**File:** `src/lib/firebase.ts`
```typescript
import { initializeApp, getApps } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';
import { getFunctions } from 'firebase/functions';

const firebaseConfig = {
  apiKey:            process.env.NEXT_PUBLIC_FIREBASE_API_KEY!,
  authDomain:        process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN!,
  projectId:         process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID!,
  storageBucket:     process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET!,
  messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID!,
  appId:             process.env.NEXT_PUBLIC_FIREBASE_APP_ID!,
};

const app        = getApps().length ? getApps()[0] : initializeApp(firebaseConfig);
export const auth      = getAuth(app);
export const db        = getFirestore(app);
export const storage   = getStorage(app);
export const functions = getFunctions(app);
```

**File:** `src/context/AuthContext.tsx`
```typescript
'use client';
import { createContext, useContext, useEffect, useState } from 'react';
import { User, signInWithEmailAndPassword, signOut as fbSignOut, onAuthStateChanged } from 'firebase/auth';
import { doc, getDoc } from 'firebase/firestore';
import { auth, db } from '@/lib/firebase';
import { useRouter } from 'next/navigation';

interface AuthCtx {
  user: User | null;
  adminName: string;
  isLoading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
}

const AuthContext = createContext<AuthCtx | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user,      setUser]      = useState<User | null>(null);
  const [adminName, setAdminName] = useState('Admin');
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    return onAuthStateChanged(auth, async (u) => {
      if (u) {
        // Verify admin
        const snap = await getDoc(doc(db, 'users', u.uid));
        if (snap.data()?.userType !== 'admin') {
          await fbSignOut(auth);
          setUser(null);
        } else {
          setUser(u);
          setAdminName(snap.data()?.name ?? 'Admin');
        }
      } else {
        setUser(null);
      }
      setIsLoading(false);
    });
  }, []);

  const signIn = async (email: string, password: string) => {
    await signInWithEmailAndPassword(auth, email, password);
    // onAuthStateChanged handles admin check
  };

  const signOut = async () => {
    await fbSignOut(auth);
    router.push('/login');
  };

  return (
    <AuthContext.Provider value={{ user, adminName, isLoading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be inside AuthProvider');
  return ctx;
};
```

**Auth guard:** `src/app/(admin)/layout.tsx` — redirect to `/login` if `user === null && !isLoading`.

### Sub-tasks
- [ ] **P03-A** `src/lib/firebase.ts` — app init + exports.
- [ ] **P03-B** `src/context/AuthContext.tsx` — auth state + admin check.
- [ ] **P03-C** `src/app/(admin)/layout.tsx` — auth guard + redirect.
- [ ] **P03-D** Cloud Function caller in `src/lib/fcm.ts`:
```typescript
import { httpsCallable } from 'firebase/functions';
import { functions } from './firebase';

export const sendFCMNotification = httpsCallable(functions, 'sendFCMNotification');
export const broadcastToAll      = httpsCallable(functions, 'broadcastToAll');
export const deleteUserAccount   = httpsCallable(functions, 'deleteUserAccount');
export const onListingDeleted    = httpsCallable(functions, 'onListingDeleted');
export const onRequestDeleted    = httpsCallable(functions, 'onRequestDeleted');
export const syncSubscriptions   = httpsCallable(functions, 'syncSubscriptions');
```

---

## Task P04 — Shared UI Components

### `StatusBadge`

```typescript
// src/components/ui/StatusBadge.tsx
type BadgeStatus = string;

export function StatusBadge({ status }: { status: BadgeStatus }) {
  const s = status.toLowerCase();

  let cls = 'bg-[var(--bg4)] text-[var(--t1)]';  // default neutral
  if (['active', 'published', 'completed', 'approved', 'resolved', 'ready'].includes(s))
    cls = 'bg-[var(--pri-transparent)] text-[var(--pri)]';
  else if (['pending', 'draft', 'open', 'processing'].includes(s))
    cls = 'bg-[rgba(240,201,74,0.15)] text-[var(--acc)]';
  else if (['rejected', 'blocked', 'closed', 'failed'].includes(s))
    cls = 'bg-[rgba(224,96,96,0.15)] text-[var(--red)]';

  return (
    <span className={`text-[11px] font-bold px-2.5 py-1 rounded-md inline-flex items-center gap-1 ${cls}`}>
      {status}
    </span>
  );
}
```

### `KPICard`

```typescript
// src/components/ui/KPICard.tsx
import { TrendingUp, TrendingDown } from 'lucide-react';
import { ReactNode } from 'react';

interface KPICardProps {
  title: string;
  value: string;
  change: string;
  isUp: boolean;
  icon: ReactNode;
}

export function KPICard({ title, value, change, isUp, icon }: KPICardProps) {
  return (
    <div className="bg-[var(--bg2)] border border-[var(--bd)] rounded-[24px] p-5 flex flex-col hover:-translate-y-1 transition-transform duration-300">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-sm font-bold text-[var(--t0)]">{title}</h3>
        <div className="p-2 rounded-xl bg-[var(--bg3)]">{icon}</div>
      </div>
      <div className="flex items-end gap-3 mt-auto">
        <h2 className="font-fraunces text-3xl font-bold text-[var(--t0)]">{value}</h2>
        <span className={`flex items-center gap-1 text-[11px] font-bold mb-1.5 px-2 py-0.5 rounded-md ${
          isUp
            ? 'text-[var(--pri)] bg-[var(--pri-transparent)]'
            : 'text-[var(--red)] bg-[rgba(224,96,96,0.15)]'
        }`}>
          {isUp ? <TrendingUp size={10} /> : <TrendingDown size={10} />}
          {change}
        </span>
      </div>
      <p className="text-[11px] font-medium text-[var(--t2)] mt-2">vs. last period</p>
    </div>
  );
}
```

### `ToggleSwitch`

```typescript
// src/components/ui/ToggleSwitch.tsx
'use client';
import { useState } from 'react';

export function ToggleSwitch({
  checked: controlled,
  onChange,
  defaultChecked = false,
}: {
  checked?: boolean;
  onChange?: (v: boolean) => void;
  defaultChecked?: boolean;
}) {
  const [internal, setInternal] = useState(defaultChecked);
  const checked = controlled ?? internal;

  const toggle = () => {
    const next = !checked;
    setInternal(next);
    onChange?.(next);
  };

  return (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      onClick={toggle}
      className={`relative inline-flex w-12 h-6 items-center rounded-full transition-colors duration-200 focus:outline-none ${
        checked ? 'bg-[var(--pri)]' : 'bg-[var(--bg4)]'
      }`}
    >
      <span
        className={`inline-block w-5 h-5 bg-white rounded-full shadow-md transform transition-transform duration-200 ${
          checked ? 'translate-x-6' : 'translate-x-1'
        }`}
      />
    </button>
  );
}
```

### `NavItem`

```typescript
// src/components/ui/NavItem.tsx
import { ReactNode } from 'react';

interface NavItemProps {
  icon: ReactNode;
  label: string;
  badge?: string;
  active?: boolean;
  onClick?: () => void;
}

export function NavItem({ icon, label, badge, active, onClick }: NavItemProps) {
  return (
    <div
      onClick={onClick}
      className={`flex items-center justify-between px-3 py-3 rounded-2xl cursor-pointer
        transition-all duration-200 select-none
        ${active
          ? 'bg-[var(--pri-transparent)] text-[var(--pri)] font-bold'
          : 'text-[var(--t1)] hover:bg-[var(--bg3)] hover:text-[var(--t0)] font-medium'
        }`}
    >
      <div className="flex items-center gap-3">
        {icon}
        <span className="text-[13px] tracking-wide">{label}</span>
      </div>
      {badge && (
        <span className="text-[10px] font-bold bg-[var(--pri-transparent)] text-[var(--pri)] px-2 py-0.5 rounded-md">
          {badge}
        </span>
      )}
    </div>
  );
}
```

### `ConfirmDialog`

Full-page glass overlay with confirmation dialog:

```typescript
// src/components/ui/ConfirmDialog.tsx
'use client';
import { X } from 'lucide-react';
import { ReactNode } from 'react';

interface ConfirmDialogProps {
  isOpen: boolean;
  title: string;
  description: ReactNode;
  confirmLabel?: string;
  confirmDanger?: boolean;
  isLoading?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmDialog({
  isOpen, title, description, confirmLabel = 'Confirm',
  confirmDanger = false, isLoading = false, onConfirm, onCancel,
}: ConfirmDialogProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Overlay */}
      <div
        className="absolute inset-0 bg-black/60 backdrop-blur-sm cursor-pointer"
        onClick={onCancel}
      />
      {/* Card */}
      <div className="relative bg-[var(--bg1)] border border-[var(--bd)] p-6 rounded-[24px] max-w-md w-full shadow-2xl z-10">
        <div className="flex justify-between items-center mb-4">
          <h4 className="font-fraunces text-xl font-bold text-[var(--t0)]">{title}</h4>
          <button
            onClick={onCancel}
            className="p-2 bg-[var(--bg0)] rounded-full text-[var(--t2)] hover:text-[var(--red)] transition-colors"
          >
            <X size={18} />
          </button>
        </div>
        <div className="text-[14px] text-[var(--t1)] mb-6 leading-relaxed">{description}</div>
        <div className="flex gap-3">
          <button
            onClick={onCancel}
            className="flex-1 bg-[var(--bg0)] border border-[var(--bd)] py-3 rounded-full text-sm font-bold text-[var(--t1)] hover:bg-[var(--bg3)] transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={isLoading}
            className={`flex-1 py-3 rounded-full text-sm font-bold text-white transition-opacity hover:opacity-90 ${
              confirmDanger
                ? 'bg-[var(--red)]'
                : 'card-gradient shadow-lg shadow-[var(--pri-transparent)]'
            } ${isLoading ? 'opacity-60 cursor-not-allowed' : ''}`}
          >
            {isLoading ? 'Loading…' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
```

### `InputReasonDialog`

Same structure as `ConfirmDialog` but with a textarea for admin to type a reason/message:

```typescript
// Extends ConfirmDialog with an extra textarea field
// Props: same + { reasonLabel: string; onConfirmWithReason: (reason: string) => void }
// Internal state: reasonText
// Textarea: bg0 border bd rounded-xl p-3 text-sm text-t0 w-full mt-2 resize-none h-24
```

### `ImageModal`

Used for payment proof screenshots:

```
Fixed overlay (bg-black/60 backdrop-blur-sm) + click to close
Modal card: bg1 border bd p-4 rounded-[24px] max-w-md shadow-2xl
  Header: "Payment Screenshot" font-fraunces text-lg bold + X button
  Image area: w-full h-[60vh] rounded-xl overflow-hidden object-contain
  Footer: "Close" outline button full width rounded-full
```

### `PageHeader`

```typescript
// src/components/ui/PageHeader.tsx
import { Filter, Plus } from 'lucide-react';
import { ReactNode } from 'react';

interface PageHeaderProps {
  title: string;
  primaryAction?: { label: string; onClick: () => void };
  secondaryAction?: { label: string; onClick: () => void };
  extra?: ReactNode;
}

export function PageHeader({ title, primaryAction, secondaryAction, extra }: PageHeaderProps) {
  return (
    <div className="flex items-center justify-between mb-6">
      <h1 className="font-fraunces text-3xl font-semibold text-[var(--t0)] tracking-tight">
        {title}
      </h1>
      <div className="flex gap-3 items-center">
        {extra}
        {secondaryAction && (
          <button
            onClick={secondaryAction.onClick}
            className="flex items-center gap-2 bg-[var(--bg2)] border border-[var(--bd)] px-4 py-2 rounded-full text-sm font-bold text-[var(--t0)] hover:bg-[var(--bg3)] transition-colors"
          >
            <Filter size={16} /> {secondaryAction.label}
          </button>
        )}
        {primaryAction && (
          <button
            onClick={primaryAction.onClick}
            className="flex items-center gap-2 card-gradient px-5 py-2 rounded-full text-sm font-bold text-white shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity"
          >
            <Plus size={16} /> {primaryAction.label}
          </button>
        )}
      </div>
    </div>
  );
}
```

### `DataTable`

Generic sortable table wrapper (used by all pages):

```typescript
// src/components/ui/DataTable.tsx
// Props: columns: { key, label, render? }[], rows: T[], onRowClick?
// Structure:
//   Container: bg2 border bd rounded-[24px] p-6 overflow-x-auto
//   Table: w-full text-left border-collapse
//   thead > tr border-b bd: th text-[11px] font-bold t2 uppercase tracking-wider pb-4 pt-2 px-4
//   tbody: tr border-b bd hover:bg-bg3 transition-colors group
//           td py-4 px-4 text-[13px] font-medium t0
```

### Sub-tasks
- [ ] **P04-A** Implement all 8 shared UI components.
- [ ] **P04-B** Export from `src/components/ui/index.ts`.

---

## Task P05 — Layout: Sidebar

**File:** `src/components/layout/Sidebar.tsx`

### Exact Specification

```
aside (w-64, bg-[var(--bg1)], border-r border-[var(--bd)], flex flex-col, z-10):

  ── Logo row (h-20, items-center, px-6) ──────────────────────────────
  Row(gap-3):
    Box(w-8 h-8, rounded-lg, card-gradient, flex center, text-white font-bold text-lg):
      "R"
    Text("RentO", font-fraunces text-2xl font-bold t0)

  ── Nav scroll area (flex-1 overflow-y-auto py-6 px-4 flex flex-col gap-1) ──
  
  Section label: "MAIN MENU" (text-[11px] font-bold t2 uppercase tracking-wider mb-2 px-3)
  
  NavItem(LayoutDashboard, "Dashboard")
  NavItem(Building, "Listings", badge = pendingListingCount)
  NavItem(Search, "Requests")
  NavItem(Users, "Users")
  NavItem(MessageSquare, "Content")

  Section label: "FINANCES" (mt-6 mb-2)
  NavItem(Package, "Subscriptions")
  NavItem(Gift, "Packages")

  Section label: "SYSTEM" (mt-6 mb-2)
  NavItem(Cpu, "Gemini Logs")

  ── Bottom section (p-4, border-t bd, flex flex-col gap-1) ────────────
  NavItem(Settings, "Settings")
  NavItem(HelpCircle, "Help & Support")   ← placeholder page (static FAQ)

  ── Appearance card (mt-4 p-4 bg-[var(--bg2)] rounded-2xl border border-[var(--bd)]) ──
  Row(items-center justify-between mb-2):
    Text("Appearance", text-sm font-semibold t0)
    button(w-10 h-10, rounded-xl, bg-bg3, t1 hover:pri, onClick=toggleTheme):
      if dark: <Sun size=18 />
      else:    <Moon size=18 />
  Text("Switch between Light and Dark mode.", text-[11px] t2 leading-tight)
```

### Active State

Active nav item uses `usePathname()` from `next/navigation` to determine current route:
```typescript
const pathname = usePathname();
const isActive = (href: string) => pathname === href || pathname.startsWith(href + '/');
```

### Sub-tasks
- [ ] **P05-A** Implement `Sidebar.tsx`.
- [ ] **P05-B** Pending badge on "Listings" reads from Firestore `listings` count where `status == "pending_approval"`.
- [ ] **P05-C** Nav links use `next/link` — not `onClick` setActiveTab (proper routing).

---

## Task P06 — Layout: Top Header

**File:** `src/components/layout/TopHeader.tsx`

### Exact Specification

```
header (h-20, flex items-center justify-between, px-8, glass-effect, z-10):

  ── Left: Search (w-96) ──────────────────────────────────────────────
  Relative div(w-full):
    SearchIcon(absolute left-4 top-50% -translate-y-50%, t2, size=18)
    input(
      type="text", placeholder="Search anything...",
      bg-[var(--bg2)] border-[var(--bd)] t0 rounded-full py-2.5 pl-11 pr-4,
      text-sm focus:border-[var(--pri)] transition-colors placeholder-t3
    )
    // ⌘K kbd shortcut badge
    div(absolute right-3 top-50%):
      kbd(px-2 py-0.5 rounded-md bg-bg3 t2 text-xs font-medium border-bd): "⌘K"

  ── Right: Controls ──────────────────────────────────────────────────
  Row(gap-4):
    // Notification bell
    button(w-10 h-10, rounded-full, bg-bg2, border-bd, t1 hover:pri, relative):
      Bell(size=18)
      // Red dot
      span(absolute top-2 right-2.5, w-2 h-2, rounded-full, bg-[var(--red)], border-2 border-bg2)

    // Admin avatar
    div(w-10 h-10, rounded-full, card-gradient, p-0.5, ml-2):
      div(w-full h-full, rounded-full, bg-bg1, overflow-hidden):
        img(admin avatar from Firestore users/{uid}.photoUrl, object-cover)
```

> **Note on search:** Implement as client-side global search triggering Firestore queries across listings and users. Results shown in a dropdown popover below the input.

### Sub-tasks
- [ ] **P06-A** Implement `TopHeader.tsx`.
- [ ] **P06-B** Admin avatar falls back to gradient initial circle if no `photoUrl`.

---

## Task P07 — Auth: Login & Register Pages

### Login Page (`/login`)

```
Full screen: flex items-center justify-center bg-[var(--bg0)] min-h-screen

Card (bg-bg1, border bd, rounded-[24px], p-10, max-w-md w-full, shadow-2xl):
  // Logo
  Row(items-center gap-3, justify-center, mb-8):
    Box(w-10 h-10, rounded-xl, card-gradient, flex center, text-white text-xl font-bold):
      "R"
    Text("RentO Admin", font-fraunces text-3xl font-bold t0)

  Text("Welcome back", font-fraunces text-xl font-semibold t0, mb-1)
  Text("Sign in to your admin dashboard.", text-sm t2, mb-8)

  Form (flex flex-col gap-4):
    // Email field
    label(text-[11px] font-bold t2 uppercase tracking-wider mb-1): "Email Address"
    input(type=email, bg-bg0, border-bd, t0, rounded-xl, py-3 px-4, text-sm, focus:border-pri)

    // Password field
    label: "Password"
    input(type=password)

    // Error message (if any)
    if error:
      p(text-sm text-[var(--red)] bg-[rgba(224,96,96,0.1)] rounded-xl px-4 py-3):
        error text

    // Submit
    button(type=submit, card-gradient, w-full, py-3, rounded-full, text-sm font-bold text-white,
           shadow-lg shadow-[var(--pri-transparent)], hover:opacity-90, disabled=isLoading):
      if isLoading: "Signing in…" else: "Sign In"
```

### Register Admin Page (`/register-admin`)

Guard: In `page.tsx`, check `process.env.NODE_ENV !== 'development'` → redirect to `/login` if not in dev. This page is localhost-only.

Same card layout as login but with additional fields:
- Name (text input)
- Email
- Password
- Confirm Password
- On submit: `createUserWithEmailAndPassword` → write `users/{uid}` with `userType: "admin"`.

### Sub-tasks
- [ ] **P07-A** Login page with email/password Firebase auth.
- [ ] **P07-B** Post-login: if `userType !== "admin"` → sign out + show error "Access denied. Admin accounts only."
- [ ] **P07-C** Register page with `NODE_ENV` guard.
- [ ] **P07-D** Both pages apply `ThemeProvider` (dark default, no sidebar).

---

## Task P08 — Dashboard Page

**Route:** `/`

### Layout

```
grid grid-cols-12 gap-6
```

### Section 1 — KPI Cards (col-span-12, grid grid-cols-4 gap-6)

Four cards using `KPICard` component:

| Title | Value | Icon | Icon colour |
|-------|-------|------|-------------|
| Total Users | Firestore count | Users | `var(--blue)` |
| Total Listings | Firestore count | Building | `var(--pri)` |
| Pending Requests | Firestore pending count | Search | `var(--red)` |
| Active Subs | Firestore count `isPaid==true` | Package | `var(--acc)` |

Each card: `bg-[var(--bg2)] border border-[var(--bd)] rounded-[24px] p-5 flex flex-col hover:-translate-y-1 transition-transform duration-300`

### Section 2 — MRR Chart (col-span-8) + Listings by Status bar chart (col-span-4)

#### MRR Chart Card

```
bg-bg2 border-bd rounded-[24px] p-6 h-full flex flex-col relative overflow-hidden

Header row (justify-between mb-4 z-10):
  Left:
    Text("MONTHLY RECURRING REVENUE", text-sm font-bold t2 uppercase tracking-wider mb-1)
    Row(items-end gap-3):
      Text("PKR 446.7K", font-fraunces text-3xl font-bold t0)    ← from Firestore aggregation
      Badge(TrendingUp size=12, "+24.4%", pri text, pri-transparent bg, px-2 py-1 rounded-md text-xs font-bold)
  Right: MoreHorizontal button (t2)

SVG Line Chart (flex-1 mt-4 relative w-full h-48):
  viewBox="0 0 800 200"
  Defs: linearGradient id="lineGrad" (pri 0.4 → pri 0.0 top→bottom)
  Grid lines: y=150, y=100, y=50 — stroke bd strokeDasharray "4 4" strokeWidth 1
  Previous period line: dashed, stroke bd2, strokeWidth 2
  Current period: filled area (url(#lineGrad)) + solid line (stroke pri, strokeWidth 3, strokeLinecap round)
  Active dot: cx at peak, r=5, fill bg1, stroke pri strokeWidth 3
  Vertical dotted line from dot to x-axis: pri strokeDasharray "4 4"
  
  Tooltip card (absolute, bg-bg1, border-bd, shadow-xl, p-3, rounded-xl):
    Date label (text-[10px] font-bold t2 mb-1)
    Row: pri line swatch + "512,504" pri bold + "this month" t1 text-xs
    Row: t3 line swatch + "85,953" t1 bold + "last month" t1 text-xs

X-axis labels (absolute -bottom-6, flex justify-between, text-[11px] font-medium t2 px-2):
  "1 Jan", "8 Jan", "15 Jan", "22 Jan", "29 Jan"

Bottom divider row (mt-8 flex gap-6 pt-4 border-t bd):
  StatMini(Building icon pri-transparent bg/pri, "2,884", "Listings")
  Divider (w-px h-10 bg-bd)
  StatMini(Search icon blue-transparent/blue, "1,432", "Requests")
  Divider
  StatMini(Package icon acc-transparent/acc, "562", "Packages")
```

#### Listings by Status Bar Chart Card (col-span-4)

```
bg-bg2 border-bd rounded-[24px] p-6 h-full flex flex-col

Header: "Listings by Status" (font-fraunces text-lg font-semibold t0) + MoreHorizontal

Bar chart area (flex-1, flex items-end justify-between gap-4 mt-4 px-2):
  4 bars — all using StatusBar component:
    Published: height proportional to count, color=pri
    Pending:   color=acc
    Drafts:    color=t2
    Blocked:   color=red

Each StatusBar:
  Column(items-center gap-2 flex-1 group cursor-pointer relative):
    // Tooltip (absolute, hidden, group-hover:opacity-100)
    Tooltip(bg-t0, text-bg1, text-[10px] font-bold py-1 px-2 rounded-lg + downward triangle)
    // Bar
    Box(w-full bg-bg3 rounded-t-lg h-32 overflow-hidden):
      div(w-full rounded-t-lg, height=%, backgroundColor=color, hover:brightness-110)
    Text(label, text-[11px] font-bold t0 mt-1)
```

### Section 3 — Expiring Subscriptions (col-span-8) + Listing Approval Rate gauge (col-span-4)

#### Expiring Subscriptions Table

```
bg-bg2 border-bd rounded-[24px] p-6

Header: "Expiring Subscriptions" font-fraunces text-lg bold t0 + MoreHorizontal

Table (border-collapse):
  Columns: ID · Name · Package · Expiry · Action
  
  Row: border-b bd hover:bg-bg3 transition-colors cursor-pointer
    ID: text-[13px] t2 font-medium
    Name: Row(items-center gap-3):
      Avatar(w-8 h-8 rounded-full bg-bg4 t1 flex center font-bold text-xs): initial
      Text(name, font-bold t0 text-[14px])
    Package: text-[13px] font-medium t1 text-right
    Expiry badge:
      if ≤ 5 days: bg-[rgba(224,96,96,0.15)] text-red
      else:        bg-bg4 text-t1
      text-[12px] font-bold px-2.5 py-1 rounded-md
    Action: "Review" button
      text-[12px] font-bold text-pri bg-pri-transparent px-3 py-1.5 rounded-full
      hover:bg-pri hover:text-white transition-colors
```

#### Listing Approval Rate Gauge (col-span-4)

```
bg-bg2 border-bd rounded-[24px] p-6 flex-1 flex flex-col

Header: "Listing Approval Rate" font-fraunces text-lg bold + MoreHorizontal

Gauge (flex-1 flex flex-col items-center justify-center mt-4):
  Relative w-40 h-20 overflow-hidden:
    SVG viewBox="0 0 200 100":
      Background arc: path D="M 20 100 A 80 80 0 0 1 180 100"
        fill=none stroke=bg3 strokeWidth=16 strokeLinecap=round strokeDasharray="4 8"
      Value arc: D="M 20 100 A 80 80 0 0 1 140 30"  (68% → adjust endpoint dynamically)
        fill=none stroke=pri strokeWidth=16 strokeLinecap=round strokeDasharray="4 8"
  
  Absolute centre:
    Text(68%, font-fraunces text-3xl font-bold t0)
    Text("On track for 80% target", text-[10px] t2 font-medium mt-1)

"Show details" button (mt-4 py-2 w-full bg-bg0 border-bd text-[13px] font-bold t1 rounded-full hover:bg-bg3)
```

### Dashboard — Broadcast Push Controls

In the header row `PageHeader`, the primary action button is **"📢 Broadcast"** (instead of "Add New").

Clicking it opens a `ConfirmDialog` variant with two text inputs (Title + Body) and a "Send to All" confirm button → calls `broadcastToAll({ title, body })`.

### Dashboard — Sync Subscriptions Button

Below expiring table, a secondary action row:
```
Row(items-center justify-between mt-4 pt-4 border-t bd):
  Text("Last synced: [timestamp]", text-[12px] t2)
  button("Sync Subscriptions", ghost style, onClick=openSyncConfirm)
```

Clicking → `ConfirmDialog("Sync subscriptions?", "This will expire overdue users and send FCM notifications.")` → calls `syncSubscriptions()`.

### Sub-tasks
- [ ] **P08-A** KPI cards with real Firestore counts via `getCountFromServer`.
- [ ] **P08-B** MRR chart (SVG, data from Firestore `subscriptionRequests` approved in last 30 days).
- [ ] **P08-C** Listings by Status bar chart (Firestore aggregation per status).
- [ ] **P08-D** Expiring subscriptions table (users where `packageExpiryDate` within next 30 days).
- [ ] **P08-E** Approval rate gauge (approved / (approved + rejected) listings).
- [ ] **P08-F** Broadcast dialog + `broadcastToAll` call.
- [ ] **P08-G** Sync subscriptions confirm flow.

---

## Task P09 — Listings Page

**Route:** `/listings`

### Page Header

```
PageHeader(
  title = "Listings",
  secondaryAction = { label: "Filters", onClick: openFilter },
  primaryAction = null  ← no Add New (listings created by users)
)
```

### Filters Bar

```
Row(mb-4 gap-3 flex-wrap):
  StatusFilter dropdown (All / Published / Pending / Rejected / Blocked / Draft)
    → styled select or custom dropdown: bg-bg2 border-bd rounded-full px-4 py-2 text-sm t0
  Search input (title or owner email) — same style
```

### Table

```
bg-bg2 border-bd rounded-[24px] p-6
DataTable columns:
  Cover   — 48×36dp thumbnail (AsyncImage, rounded-lg, bg-bg3 fallback with property icon)
  Title   — font-bold t0 text-[13px]
  Type    — t2 font-medium
  City    — t2 font-medium
  Owner   — owner email t2
  Status  — StatusBadge
  Date    — createdAt formatted t2
  Actions — icon button row (see below)
```

### Row Actions

```
Actions cell (flex justify-end gap-2):
  // View
  button(p-2 bg-bg0 rounded-lg, pri hover:pri-transparent, Eye icon size=16)
    → opens ListingDetailPanel (right slide-over or modal with listing fields read-only)

  // Approve (only if status == "pending_approval")
  button(p-2 bg-pri-transparent rounded-lg, pri hover:bg-pri hover:text-white, CheckCircle size=16)
    → ConfirmDialog("Approve listing?")
    → Firestore: listings/{id}.status = "published"
    → sendFCMNotification({ notifType: "listing_approved", ... })

  // Reject
  button(p-2 bg-[rgba(240,201,74,0.15)] rounded-lg, acc, XCircle size=16)
    → InputReasonDialog("Reject Listing", reasonLabel="Rejection reason")
    → Firestore: status = "rejected", rejectionReason = reason
    → sendFCMNotification({ notifType: "listing_rejected", ... })

  // Block
  button(p-2 bg-[rgba(224,96,96,0.15)] rounded-lg, red hover:bg-red hover:text-white, Ban size=16)
    → ConfirmDialog("Block Listing?")
    → Firestore: status = "blocked"
    → sendFCMNotification({ notifType: "listing_blocked", ... })

  // Delete (mandatory message)
  button(p-2 bg-bg0 rounded-lg, red hover:bg-[rgba(224,96,96,0.15)], Trash2 size=16)
    → InputReasonDialog(
        title = "Delete Listing",
        reasonLabel = "Notify the owner with this message (required)",
        confirmLabel = "Delete Listing",
        confirmDanger = true,
      )
    → onListingDeleted({ listingId, ownerUid, adminMessage: reason })
```

> **Auto-approve toggle** lives on the Settings page (`config/listing.requireApproval`). When disabled, new listings skip pending status.

### Sub-tasks
- [ ] **P09-A** Listings page with real-time Firestore listener (`onSnapshot`).
- [ ] **P09-B** Client-side status filter + search.
- [ ] **P09-C** All 5 row actions with correct dialogs + Cloud Function calls.
- [ ] **P09-D** Listing Detail slide-over panel (read-only: title, description, photos grid, city, price, type, owner info).
- [ ] **P09-E** Pagination: load 20 at a time, "Load more" button.

---

## Task P10 — Requests Page

**Route:** `/requests`  
Mirrors the Listings page exactly. Differences:

### Table Columns

```
Requester Name | Email | Intent (Share/Full Rent) | Area | Budget | Status | Created | Actions
```

### Row Actions

Same as Listings but:
- "Approve" → `status = "active"`, FCM `new_request_pending` (to admin only) — actually admin sends to requester
- "Reject" → reason → `status = "rejected"`, FCM notification
- "Close" → `status = "closed"` (not deleted — can re-open)
- "Delete" → `onRequestDeleted({ requestId, ownerUid, adminMessage })` (mandatory message)

### Sub-tasks
- [ ] **P10-A** Requests page mirroring Listings architecture.
- [ ] **P10-B** "Close" action (no Cloud Function needed — direct Firestore update).
- [ ] **P10-C** Request Detail panel: budget, areas, radius, property type, move-in, bio.

---

## Task P11 — Users Page

**Route:** `/users`

### Table

```
DataTable columns:
  Avatar  — 36dp circle (card-gradient ring, user photo or initial)
  Name    — font-bold t0
  Email   — t2
  Type    — "Individual" | "Business" (badge)
  Status  — StatusBadge (Active / Blocked)
  Package — packageName or "Basic Free" t2
  Joined  — createdAt formatted
  Actions — View button → User Detail modal
```

### Search Bar

Top of page: live filter by name or email (client-side after loading).

### User Detail Modal / Page (`/users/[uid]`)

Full-width slide-over or dedicated page with:

```
Card (bg-bg1 border-bd rounded-[24px] p-8):
  Row(items-start gap-6):
    // Avatar (64dp, card-gradient ring p-0.5)
    // Info block
    Column:
      Text(user.name, font-fraunces text-2xl font-bold t0)
      Text(user.email, text-sm t2)
      Row(gap-2 mt-2):
        Badge(user.accountType)
        Badge(user.packageName ?? "Basic Free")
        if user.isBlocked: Badge("Blocked", red)

  Divider

  Grid(grid-cols-3 gap-4 mt-6):
    StatCard("Published Listings", count)
    StatCard("Active Requests", count)
    StatCard("Days Until Expiry", daysCalc)

  Divider

  // Block / Unblock
  Row(justify-between items-center p-4 bg-bg0 border-bd rounded-xl mt-6):
    Column:
      Text("Block User", 14sp bold t0)
      if user.blockReason: Text(user.blockReason, 12sp t2)
    if user.isBlocked:
      button("Unblock", card-gradient rounded-full px-6 py-2 text-sm font-bold text-white)
        → Firestore: isBlocked=false, blockReason=null
        (No FCM needed for unblock)
    else:
      button("Block User", bg-red rounded-full px-6 py-2 text-sm font-bold text-white)
        → InputReasonDialog("Block User", reasonLabel="Reason for blocking (shown to admin only)")
        → Firestore: isBlocked=true, blockReason=reason
        → sendFCMNotification({ notifType: "account_blocked", targetUid: uid, ... })

  // Delete Account (bottom, dangerous zone)
  Box(mt-8 p-4 bg-[rgba(224,96,96,0.08)] border border-[rgba(224,96,96,0.25)] rounded-xl):
    Row(justify-between items-center):
      Column:
        Text("Delete Account", 14sp bold red)
        Text("Permanently deletes all user data. Cannot be undone.", 12sp t2)
      button("Delete Account", bg-red text-white text-sm font-bold rounded-full px-6 py-2)
        → ConfirmDialog(
            title="Delete Account?",
            description="This will permanently delete all listings, requests, chats, and data for this user.",
            confirmLabel="Delete Account",
            confirmDanger=true,
          )
        → deleteUserAccount({ uid })
```

### Sub-tasks
- [ ] **P11-A** Users table with real-time Firestore listener.
- [ ] **P11-B** User Detail — profile card, stats (3 Firestore count queries).
- [ ] **P11-C** Block/Unblock with FCM + Firestore.
- [ ] **P11-D** Delete Account → Cloud Function call.
- [ ] **P11-E** Free Upgrade button per user: select package dropdown → update user's package fields.

---

## Task P12 — Content Page

**Route:** `/content`  
Single page with **4 horizontal tabs**: Feedback · Sliders · Reports · Notification Log

Tab strip style:
```
Row(gap-2 mb-6):
  Tab chip per tab:
    Selected: card-gradient text-white px-5 py-2 rounded-full text-sm font-bold
    Inactive: bg-bg2 border-bd t1 px-5 py-2 rounded-full text-sm font-bold hover:bg-bg3
```

### Tab 1: Feedback (Feature Requests)

```
DataTable columns: Title · User · Date · Status (Open/Closed) · Actions
Row Actions:
  View button → right panel with full title + description + user info
  "Mark Closed" button → Firestore: status = "closed"
  "Delete" button → ConfirmDialog → delete doc
```

### Tab 2: Sliders

```
DataTable columns:
  User · Linked Listing title · Image preview (48×36dp thumbnail) · Status · Submitted At · Actions

Row Actions:
  Preview image → ImageModal
  "Approve" → Firestore: status = "approved" → sendFCMNotification({ notifType: "slider_approved" })
  "Reject"  → Firestore: status = "rejected"
```

Note: Only `status = "approved"` sliders appear in the Android home carousel.

### Tab 3: Reports (User-Submitted Moderation Reports)

```
DataTable columns:
  Reporter · Reported (name/email) · Type (User/Listing/Request) · Reason · Date · Status · Actions

Row Actions:
  View — full context panel (reporter info, reported entity card, reason text)
  "Dismiss"     → Firestore: status = "resolved"
  "Block User"  → same flow as Users page block
  "Block Listing" → Firestore: listings/{id}.status = "blocked" + FCM
```

### Tab 4: Notification Log

```
Read-only audit table. No actions.
DataTable columns: Title · Body · Type (notifType) · Recipient (uid) · Timestamp

Data source: Firestore collection group query across notifications/{uid}/items/ 
  → orderBy createdAt DESC, limit 100
```

### Sub-tasks
- [ ] **P12-A** Tab switching with URL query param `?tab=feedback` (persists on refresh).
- [ ] **P12-B** All 4 tab tables with real Firestore data.
- [ ] **P12-C** Sliders: image preview in table + full ImageModal on click.
- [ ] **P12-D** Reports: block user/listing actions calling same functions as Users/Listings pages.

---

## Task P13 — Subscriptions Page

**Route:** `/subscriptions`

### Layout

3 status tabs: **Pending · Active · Expired**

Tab selected state: same chip style as Content page.

### Subscriptions Table

```
DataTable columns:
  Request ID · User · Package · Amount (PKR X,XXX) · Date · Proof · Status · Actions

Proof column:
  if proof URL exists:
    button(Eye size=14, "View", blue/10 bg, blue text, rounded-full, hover:bg-blue hover:text-white)
    → ImageModal(proofImageUrl, title="Payment Screenshot")
  else:
    italic text "N/A" t3

Status column: StatusBadge

Actions column:
  if status == "Pending":
    // Approve
    button(p-2 bg-pri-transparent rounded-lg pri hover:bg-pri hover:text-white):
      Check size=16
    → ConfirmDialog("Approve Subscription?")
    → Firestore batch:
        subscriptionRequests/{id}.status = "approved"
        subscriptionRequests/{id}.processedAt = serverTimestamp()
        subscriptionRequests/{id}.processedBy = admin.uid
        users/{uid}: {
          isPaid: true,
          currentPackageId: packageId,
          packageName: packageName,
          packageExpiryDate: now + package.durationDays,
          maxPublishedListings: package.maxPublishedListings,
          maxTotalListings: package.maxTotalListings,
          maxPublishedRequests: package.maxPublishedRequests,
          dailyMessageLimit: package.dailyMessageLimit,
          allowsSlider: package.allowsSlider,
        }
    → sendFCMNotification({ notifType: "subscription_approved", targetUid, ... })

    // Reject
    button(p-2 bg-[rgba(224,96,96,0.15)] rounded-lg red hover:bg-red hover:text-white):
      X size=16
    → InputReasonDialog("Reject Subscription", "Rejection reason")
    → Firestore: status = "rejected"
    → sendFCMNotification({ notifType: "subscription_rejected", targetUid, ... })

  else:
    // Delete record
    button(p-2 bg-bg0 rounded-lg t3 hover:text-red): Trash2 size=16
```

### Free Upgrade Button

Per row, a "Upgrade Free" button (visible for all users, not just pending):
```
button("⬆ Upgrade Free", t2 text-xs rounded-full px-3 py-1.5 bg-bg3 hover:bg-bg4)
→ Dropdown / select to choose package (fetched from packages/ collection)
→ Same Firestore batch as Approve but without status update on subscriptionRequest
→ sendFCMNotification({ notifType: "subscription_upgraded", ... })
```

### Sub-tasks
- [ ] **P13-A** 3-tab subscriptions page with real Firestore data.
- [ ] **P13-B** Payment proof ImageModal.
- [ ] **P13-C** Approve: Firestore batch (update subscriptionRequest + update user's package fields).
- [ ] **P13-D** Reject: reason dialog + FCM.
- [ ] **P13-E** Free Upgrade: package selector → Firestore update + FCM.

---

## Task P14 — Packages Page

**Route:** `/packages`

### Layout

```
PageHeader(title="Packages", primaryAction={ label: "Add Package", onClick: openCreateForm })
Grid(grid-cols-3 gap-6):
  PackageCard per package
```

### `PackageCard`

```
bg-bg2 border-bd rounded-[24px] p-6 flex flex-col relative:
  // Active/Inactive pill (absolute top-4 right-4)
  StatusBadge(package.isActive ? "Active" : "Inactive")

  Text(package.name, font-fraunces text-xl font-bold t0 mb-1)
  Text(package.description, text-sm t2 mb-4 line-clamp-2)
  
  // Price
  Row(items-end gap-1 mb-6):
    Text("PKR", text-sm t2)
    Text(formatPrice(package.price), font-fraunces text-3xl font-bold t0)
    Text("/ " + package.durationDays + " days", text-sm t2 mb-1)

  // Feature list
  ul(flex flex-col gap-2 mb-6 flex-1):
    li(flex items-center gap-2 text-[13px] t1):
      CheckCircle(size=14 pri) "Up to {maxPublishedListings} published listings"
    li: CheckCircle + "Up to {maxPublishedRequests} requests"
    li: CheckCircle + "{dailyMessageLimit} messages/day"
    if allowsSlider: li: CheckCircle + "Featured slider access"
    if isBusinessPackage: li: CheckCircle + "Business badge"

  // Actions
  Row(gap-2 mt-auto):
    ToggleSwitch(checked=package.isActive, onChange=toggleActive)
    div(flex-1)
    button(Edit icon, rounded-lg bg-bg0 p-2 pri hover:pri-transparent)
      → opens EditPackageForm (same form as Create, pre-filled)
    button(Trash2 icon, rounded-lg bg-bg0 p-2 red hover:red-transparent)
      → ConfirmDialog("Delete Package?")
      → Firestore: delete packages/{id}
```

### Create / Edit Package Form

Right slide-over panel or modal:

```
Form fields (grid-cols-2 gap-4):
  Name           (text input, required)
  Description    (textarea 3 rows)
  Price PKR      (number input)
  Duration (days) (number input)
  Max Published Listings (number)
  Max Total Listings     (number)
  Max Published Requests (number)  ← NEW field
  Daily Message Limit    (number)
  [row] Business Package toggle + label
  [row] Allows Slider toggle + label
  [row] Active toggle + label

Submit button: card-gradient rounded-full px-8 py-3 text-sm font-bold text-white
→ Firestore: add or update packages/{id}
```

### Sub-tasks
- [ ] **P14-A** Package card grid with real Firestore data.
- [ ] **P14-B** Create + Edit form (slide-over panel, form validation).
- [ ] **P14-C** Delete with confirmation.
- [ ] **P14-D** Active/inactive toggle (immediate Firestore update).

---

## Task P15 — Gemini Logs Page

**Route:** `/gemini-logs`

### Layout

```
PageHeader(
  title = "Gemini Key Logs",
  primaryAction = { label: "Clear All", onClick: openClearConfirm, dangerous=true }
)

DataTable columns:
  Timestamp (formatted) · Key Number (Key #1–#4) · Error Message

Empty state: "No errors logged. All Gemini keys are healthy."

Footer note: "Logs are written automatically when a Gemini API call fails.
              Admin is notified via push notification on each failure."
```

### Sub-tasks
- [ ] **P15-A** Gemini logs table reading from `geminiKeyLogs/` collection ordered by timestamp DESC.
- [ ] **P15-B** "Clear All" → ConfirmDialog → batch delete all docs.

---

## Task P16 — Settings / Config Page

**Route:** `/settings`

### Layout

```
grid grid-cols-12 gap-6

Left card (col-span-8, bg-bg2 border-bd rounded-[24px] p-8):
  Title: "Platform Configuration" (font-fraunces text-xl bold t0, border-b pb-4 mb-6)

  Grid(grid-cols-2 gap-6):
    Field: "Admin Support Email"     → config/admin.adminEmail
    Field: "WhatsApp Number (Payments)" → config/admin.whatsappNumber
    Field: "Expiry Warning Days"     → config/subscription.expiryWarningDays
    Field: "Free User Message Limit" → config/chat.freeUserDailyLimit
    Field: "Paid User Message Limit" → config/chat.paidUserDailyLimit
    Field: "Max Images Per Listing"  → config/listing.maxImages

  Section: "Free Tier Limits" (font-fraunces text-lg bold t0 mt-10 mb-4)
  Grid(grid-cols-2 gap-6):
    Field: "Free Max Published Listings" → users default maxPublishedListings (sets default for new free users)
    Field: "Free Max Total Listings"     → users default maxTotalListings
    Field: "Free Max Published Requests" → users default maxPublishedRequests
    Field: "Free Max Total Requests"     → users default maxTotalRequests
    Field: "Max Active Requests"         → config/request.maxActiveRequests

  Section: "Bank Account Details" (font-fraunces text-lg bold t0 mt-10 mb-4)
  Grid(grid-cols-2 gap-6):
    Field: "Account Title"   → config/bankAccount.accountTitle
    Field: "IBAN"            → config/bankAccount.iban
    Field: "Bank Name"       → config/bankAccount.bankName

  Section: "Approval Settings" (font-fraunces text-lg bold t0 mt-10 mb-4)
  
  ToggleRow("Require Listing Approval",
    "New properties must be reviewed by admin before going live.",
    checked = config/listing.requireApproval)
  
  ToggleRow("Require Request Approval",
    "Tenant looking requests must be reviewed before appearing to hosts.",
    checked = config/request.requireApproval)

  Section: "Listing Form" (font-fraunces text-lg bold t0 mt-10 mb-4)
  ToggleRow("Listing Form Style",
    "'Wizard' = multi-step; 'Scroll' = single-page scroll.",
    checked = config/listing.formStyle)

  Section: "App Version Gate" (font-fraunces text-lg bold t0 mt-10 mb-4)
  Grid(grid-cols-3 gap-6):
    Field: "Latest Build Number" → config/app.latestBuildNumber
    Field: "Min Build Number"    → config/app.minBuildNumber
    Field: "Force Update"        → ToggleSwitch (config/app.isForceUpdate)

  Section: "Admin Notifications" (font-fraunces text-lg bold t0 mt-10 mb-4)
  Grid(grid-cols-1 gap-6):
    Field: "Admin FCM Token" (read-only display) → config/admin.adminFcmToken
    Note: "This token is auto-registered when admin logs in on mobile."

  Save button row (border-t bd pt-6 mt-4 flex justify-end):
    button("Save Configurations", card-gradient rounded-full px-8 py-3 text-sm font-bold text-white)
    → Batch Firestore writes to all config/ docs

Right column (col-span-4, flex flex-col gap-6):
  // API Integrations card
  bg-bg2 border-bd rounded-[24px] p-6:
    Title: "API Integrations" font-fraunces text-lg bold t0 mb-4
    APICard("Google Maps API", "Active", key preview "AIzaSyB...aJ8K1p")
    APICard("Firebase Cloud Messaging", "Active", "Configured via google-services.json")
    APICard("Gemini AI", "Active", key preview from config)

  // Broadcast card (separate from dashboard)
  bg-bg2 border-bd rounded-[24px] p-6:
    Title: "Broadcast Notification" font-fraunces text-lg t0 bold mb-4
    input(Title, bg-bg0 border-bd rounded-xl py-3 px-4 text-sm w-full mb-3)
    textarea(Body, bg-bg0 border-bd rounded-xl py-3 px-4 text-sm w-full resize-none h-24 mb-3)
    // Target selector (v2.3 §32.28)
    Label: "TARGET AUDIENCE" section-label
    Select / chip row (bg-bg0 border-bd rounded-xl py-3 px-4 text-sm w-full mb-4):
      Options: "All Users" | "Free Users Only" | "Paid Users Only"
      Maps to FCM topics: "all_users" | "free_users" | "paid_users"
    button("Send Broadcast", card-gradient rounded-full w-full py-3 text-sm font-bold text-white)
      → ConfirmDialog → broadcastToAll({ title, body, target })
```

**ToggleRow component:**
```
Box(flex items-center justify-between p-4 bg-bg0 border-bd rounded-xl):
  Column:
    Text(label, 14px bold t0)
    Text(desc, 12px t2 mt-1)
  ToggleSwitch(checked, onChange)
```

**APICard component:**
```
Box(p-4 border-bd rounded-xl bg-bg0 mb-3):
  Row(justify-between items-center mb-2):
    Text(name, font-bold text-[13px] t0)
    StatusBadge("Active")
  Text(detail, text-[11px] t2 truncate)
```

### Sub-tasks
- [ ] **P16-A** Load all config fields on mount (multiple Firestore `getDoc` calls for config/* docs).
- [ ] **P16-B** "Save Configurations" → batch write all changed fields.
- [ ] **P16-C** Broadcast card with ConfirmDialog.
- [ ] **P16-D** API Integrations card (static display; Gemini key from `config/gemini`).

---

## Task P17 — Firestore Hooks & Data Layer

**File:** `src/lib/firestore.ts`

Typed helper wrappers for common Firestore ops:

```typescript
// Generic real-time collection listener
export function useCollection<T>(
  collectionPath: string,
  constraints: QueryConstraint[] = [],
): { data: T[]; isLoading: boolean; error: string | null }

// Generic paginated query
export function usePaginatedCollection<T>(
  collectionPath: string,
  constraints: QueryConstraint[],
  pageSize: number,
): { data: T[]; loadMore: () => void; hasMore: boolean; isLoading: boolean }

// Single document
export function useDocument<T>(
  path: string,
): { data: T | null; isLoading: boolean }
```

**TypeScript types** (`src/types/index.ts`):
```typescript
export interface AdminUser {
  uid: string; name: string; email: string; phone?: string; photoUrl?: string;
  isBlocked: boolean; blockReason?: string; userType: 'user' | 'admin';
  accountType: 'individual' | 'business'; defaultMode: 'looking' | 'hosting';
  isPaid: boolean; currentPackageId?: string; packageName?: string;
  packageExpiryDate?: Timestamp; maxPublishedListings: number;
  maxTotalListings: number; maxPublishedRequests: number;
  dailyMessageLimit: number; allowsSlider: boolean;
  fcmToken?: string; createdAt: Timestamp;
}

export interface AdminListing {
  id: string; uid: string; ownerEmail: string; title: string;
  propertyType: string; city: string; price: number;
  status: 'published' | 'draft' | 'pending_approval' | 'rejected' | 'blocked';
  photos: string[]; rejectionReason?: string; adminDeleteMessage?: string;
  createdAt: Timestamp; updatedAt: Timestamp;
}

export interface AdminRequest {
  id: string; uid: string; requesterName: string; requesterEmail: string;
  intent: string; propertyType: string; city: string;
  preferredAreas: string[]; budgetMax: number;
  status: 'active' | 'pending_approval' | 'closed' | 'rejected';
  rejectionReason?: string; createdAt: Timestamp;
}

export interface Package {
  id: string; name: string; description: string; price: number;
  durationDays: number; maxPublishedListings: number; maxTotalListings: number;
  maxPublishedRequests: number; dailyMessageLimit: number;
  isBusinessPackage: boolean; allowsSlider: boolean; isActive: boolean;
  createdAt: Timestamp;
}

export interface SubscriptionRequest {
  id: string; uid: string; userName: string; packageId: string;
  packageName: string; amount: number; proofUrl?: string;
  status: 'pending' | 'approved' | 'rejected';
  isExtension: boolean; createdAt: Timestamp; processedAt?: Timestamp;
}

export interface SliderRequest {
  id: string; uid: string; userName: string; listingId: string;
  listingTitle: string; imageUrl: string;
  status: 'pending_approval' | 'approved' | 'rejected';
  createdAt: Timestamp;
}

export interface Report {
  id: string; reporterUid: string; reportedUid?: string;
  listingId?: string; requestId?: string;
  type: 'user' | 'listing' | 'request';
  reason: string; status: 'open' | 'resolved'; createdAt: Timestamp;
}

export interface FeatureRequest {
  id: string; uid: string; userName: string; title: string;
  description: string; status: 'open' | 'closed'; createdAt: Timestamp;
}

export interface GeminiLog {
  id: string; keyNumber: number; errorMessage: string; timestamp: Timestamp;
}
```

### Sub-tasks
- [ ] **P17-A** Implement typed Firestore hooks.
- [ ] **P17-B** All TypeScript types in `src/types/index.ts`.
- [ ] **P17-C** No `any` types anywhere — strict TypeScript.

---

## Task P18 — Build Gate

- [ ] `npm run lint` → 0 ESLint errors.
- [ ] `npm run build` → 0 TypeScript errors, successful build.
- [ ] Deploy to Firebase Hosting: `firebase deploy --only hosting`.
- [ ] Test on localhost: dark + light theme toggle works on every page.
- [ ] Test auth guard: unauthenticated user → redirected to `/login`.
- [ ] Test admin check: non-admin Firebase user → signed out + error shown.
- [ ] Create `CODE_REVIEW_PORTAL.md`.

---

## 23. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Dark mode is default on first load | `ThemeContext` — localStorage check, fallback `'dark'` | ☐ |
| Theme toggle (sidebar bottom) switches light↔dark instantly | `document.documentElement.classList.toggle('dark')` | ☐ |
| Non-admin login → signed out + "Admin accounts only" error | `AuthContext` userType check | ☐ |
| Sidebar active route highlights correctly | `usePathname()` comparison | ☐ |
| Listings badge shows live pending count | Firestore `getCountFromServer` | ☐ |
| Dashboard KPIs load real counts | `getCountFromServer` per collection | ☐ |
| MRR chart renders with SVG gradient area | `lineGrad` linearGradient | ☐ |
| Expiring rows ≤5 days → red badge | `isUrgent` check on expiry days | ☐ |
| Approve listing → status published + FCM sent | Firestore update + `sendFCMNotification` | ☐ |
| Reject listing → reason dialog required → FCM with reason | `InputReasonDialog` enforces non-empty | ☐ |
| Delete listing → mandatory message → `onListingDeleted` CF | `InputReasonDialog` + CF call | ☐ |
| Block user → FCM `account_blocked` sent | `sendFCMNotification` | ☐ |
| Delete account → `deleteUserAccount` CF → full cascade | CF handles all cleanup | ☐ |
| Payment proof → ImageModal with scroll (60vh) | `ImageModal` component | ☐ |
| Approve subscription → user package fields updated in batch | Firestore batch write | ☐ |
| Free upgrade → package selector → user package updated + FCM | Dropdown + Firestore update | ☐ |
| Create package → all fields saved → card appears | Firestore add + real-time listener | ☐ |
| Active toggle on package updates immediately | Firestore update on toggle change | ☐ |
| Slider approved → FCM `slider_approved` | `sendFCMNotification` | ☐ |
| Config save → all config/* docs updated in batch | Firestore batch | ☐ |
| Broadcast → ConfirmDialog → `broadcastToAll` CF | CF sends to `all_users` topic | ☐ |
| Gemini logs clear all → batch delete | Firestore batch delete | ☐ |
| Notification Log read-only → no actions shown | UI renders no action column | ☐ |
| Register Admin page only accessible on localhost | `NODE_ENV !== 'development'` guard | ☐ |

---

## Appendix: Pages vs Routes Reference

| Sidebar Label | Route | Requirements §22 section |
|---------------|-------|--------------------------|
| Dashboard | `/` | §22.3 |
| Listings | `/listings` | §22.5 |
| Requests | `/requests` | §22.6 |
| Users | `/users` | §22.4 |
| Content (tabs: Feedback, Sliders, Reports, Notif Log) | `/content` | §22.9, §22.10, §22.11, §22.14 |
| Subscriptions | `/subscriptions` | §22.7 |
| Packages | `/packages` | §22.8 |
| Gemini Logs | `/gemini-logs` | §22.13 |
| Settings | `/settings` | §22.12 |
| ~~Transactions~~ | ~~REMOVED~~ | — |

---

*End of Admin Web Portal Specification v1.0.0*
