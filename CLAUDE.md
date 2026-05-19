# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Layout

Monorepo for the **LegalNow** platform — a legal-consultation product that pairs clients with lawyers over real-time chat, audio, and video.

- `frontend/` — Vue 3 + TypeScript + Vite SPA (the user-facing app).
- `backend/redis-api/` — Express + TypeScript service that fronts Redis for consultations, chat sessions, and statuses.
- `backend/janus/` — Janus WebRTC gateway config + recordings volume (run via Docker).
- `backend/whisper/` — `transcribe.py` mounted into a `faster-whisper-server` container that consumes Janus recordings.
- `docker-compose.yaml` at the repo root orchestrates the full stack on a single `legalnet` bridge network.

## Running the Stack

Full stack (Janus + Whisper + Redis + redis-api + webapp), from repo root:

```sh
docker compose up --build
```

Service ports:
- `5173` webapp (Vite dev server, run inside container via `npm run dev -- --host`)
- `3030` redis-api (Express)
- `6379` redis-server
- `8188` Janus WS, `8088`/`7088` Janus HTTP(S), `10000-10003/udp` RTP media

The webapp talks to backends via three env vars (set in `docker-compose.yaml` for the `webapp` service): `VITE_JANUS_WSS`, `VITE_JANUS_API_SECRET`, `VITE_REDIS_API`.

## Frontend (`frontend/`)

Package manager: pnpm (`pnpm-lock.yaml` + `pnpm-workspace.yaml` present), but `package.json` scripts use `npm`. Either works.

Common commands (run inside `frontend/`):

```sh
npm install
npm run dev              # vite dev server
npm run build            # type-check + vite build
npm run type-check       # vue-tsc --build
npm run lint             # eslint . --fix
npm run format           # prettier --write src/
npm run test:unit        # vitest
npm run test:e2e         # playwright (run `npm run build` first for CI)
npm run test:e2e -- --project=chromium
npm run test:e2e -- tests/example.spec.ts
npm run rooms:create     # tsx scripts/createRooms.ts — pre-creates Janus TextRoom/AudioBridge
```

Path alias: `@/` → `frontend/src/` (configured in `vite.config.ts` + `tsconfig`).

### Frontend architecture

- **Entry**: `src/main.ts` wires Pinia, Vue Router, Vuetify, vue-toastification, and globally registers a handful of `radix-vue` `NavigationMenu*` components.
- **Routing** (`src/router/index.ts`): two route trees — public landing/auth routes, and a `/dashboard` subtree gated by `DashboardLayout.vue`. A global `beforeEach` calls `useAuthStore().init()`, redirects unauthenticated users on non-`public` routes to `/login`, and routes authenticated users to `/dashboard/client` or `/dashboard/lawyer` based on `user.role`. Per-route `meta.roles: ['client' | 'lawyer']` enforces RBAC.
- **Auth** (`src/stores/auth.ts`): Pinia store with an in-file `mockUsers` array (no backend yet). Session persists in `localStorage` under key `tuabogado-auth`. `init()` rehydrates on app boot.
- **API client** (`src/services/apiClient.ts`): thin `fetch` wrapper around `VITE_REDIS_API`. Default endpoint is `/consultations`. Returns `{ data, error }` — callers branch on `error`. Always sends `ngrok-skip-browser-warning: true` (the stack is regularly tunneled).
- **WebRTC** lives in two parallel implementations — `src/services/useJanus.ts` (composable-style) and `src/composables/initJanusLib.ts` (module-level singleton handling TextRoom + VideoRoom + AudioBridge plugins simultaneously). The Janus JS library is loaded globally and accessed via `(window as any).Janus`.
- **State** (`src/stores/`): `auth`, `chatSessionStore`, `janusSessionStore`, `memoryStore`. Chat session state is the bridge between the Janus DataChannel messages and the UI.
- **Types** in `src/types/{chat,payment,user}.ts` — `Role = 'client' | 'lawyer'` is the central enum that drives routing and UI.

### Frontend gotchas

- `App.vue.bkp`, `Home.vue.bkp`, `DashboardUsers.vue.bkp`, `ChatMessages_Original.vue` and similar `*_Original.vue` / `*.bkp` files are stale legacy copies — do not edit them; they are not imported.
- Janus WSS URLs are hardcoded in `scripts/createRooms.ts` (an ngrok URL). Update before running.
- The repo state on `mffm-remove-unnecesary-code` shows a large historical move of frontend files from repo root into `frontend/`. Treat the deleted root-level `src/`, `package.json`, etc. as moved — the active code is under `frontend/`.

## Backend redis-api (`backend/redis-api/`)

Express + TypeScript service. All routes mounted under `/api` (built by the consumer — webapp uses `${VITE_REDIS_API}` which already includes `/api`).

Commands (run inside `backend/redis-api/`):

```sh
npm install
npm run dev      # nodemon, hot reload
npm run build    # tsc
npm run start    # node dist/index.js
```

### Key design

- Redis keys are namespaced via prefix helpers in `index.ts`: `consultations:`, `status:`, `chatSession:`, `chatMessages:`.
- Consultations are stored as Redis **hashes** keyed by user email; each `field` is a consultation id; value is JSON.
- Endpoints follow CRUD on `/consultations` plus parallel routes for chat sessions and statuses. POST refuses duplicates (409); PUT requires existence (404). Errors are JSON with `{ success, message }`.

## Workflow notes

- This is a personal project; backend is intentionally thin (mock users in the frontend, Redis as the only durable store). Don't introduce a real auth backend or database without discussion.
- Whisper transcription runs on a polling loop against the Janus recordings volume — recordings flow Janus → shared volume → whisper container → transcripts.
- Don't reintroduce the deleted root-level Vue files; the canonical frontend tree is `frontend/src/`.
