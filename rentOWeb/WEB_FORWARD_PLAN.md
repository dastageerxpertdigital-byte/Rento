# Web Agent — Forward Plan (rentOWeb)

## Scope

- Scope is `rentOWeb` only.
- Do not change the Android app unless explicitly assigned.

## Rules

- No secrets: never commit `.env.local` (or any `.env.*` with values).
- Only commit variable names via `.env.example` and `README.md`.
- Evidence: after changes, capture that `npm ci`, `npm run lint`, `npm run test`, and `npm run build` were run successfully.
- Commits: themed commits only (e.g. `docs: ...`, `fix: ...`, `chore(deps): ...`). Never commit API keys.

## Operating Rhythm (Default Workload)

| ID | Task | When |
|---|------|------|
| W1 | Full pipeline: `npm ci && npm run lint && npm run test && npm run build` | Every release; after dependency bumps; at least monthly if idle |
| W2 | Run `npm audit`; patch or document accepted risk | Quarterly or when a CVE affects you |
| W3 | If hosting/Firebase changes: ensure all keys in `.env.example` and `README.md` stay accurate | Each deploy/env change |

## Product / Spec Follow-ups (Only If Product Asks)

| ID | Task | Done when |
|---|------|----------|
| W4 | P04 debt (if still required): reusable `PageHeader` and/or generic `DataTable` per `PROGRESS.md` | Stakeholder sign-off + W1 green |
| W5 | Docs sync: if broadcast/settings UX changes, update `ADMIN_PORTAL_SPEC.md` and `PROGRESS.md` together | No conflicting instructions |
| W6 | Optional E2E: Playwright (or similar) on staging for login + one path per major section | CI job or documented manual run |

## Firebase / SSR Guardrails

- Keep `src/lib/firebase.ts` safe for missing env and non-browser contexts.
- Any new `NEXT_PUBLIC_*` env var → add to `.env.example` and `README.md`.

## Commands to Run Before Calling Work “Done”

```bash
npm ci
npm run lint
npm run test
npm run build
```
