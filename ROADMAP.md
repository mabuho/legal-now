# LegalNow — Roadmap & Kanban

Migration plan from the current Node/Redis backend + Vuetify frontend to the target stack (Java 21 / Spring Boot 3 / Postgres / Flyway / Vue 3 + Tailwind / Stripe Connect + Mercado Pago / motor IA matching).

See `CLAUDE.md` for architecture reference. See `.claude/memory/` for context that persists across sessions.

---

## Legend

- ✅ Done
- 🟡 In Progress
- ⬜ Todo
- 🔵 Blocked / Deferred

---

## 📋 Backlog

### Phase 7 — AI matching / triage engine
- ⬜ Define matching algorithm (specialization + availability + rating + price)
- ⬜ Replace `legalTriage.ts` bot stub with real triage service
- ⬜ Wire matching to `consultations` creation flow
- ⬜ Track lawyer ratings (new table)

### Phase 6 — Payments (Stripe Connect + Mercado Pago) _(requires Phase 5 lawyer verification)_
- ⬜ Stripe Connect onboarding for lawyers (payouts)
- ⬜ Stripe Checkout for client payments
- ⬜ Webhooks handling (idempotent, signature-verified)
- ⬜ Mercado Pago integration (phase 6b)
- ⬜ Refund flow

### Phase 5 — Lawyer onboarding & verification _(unblocks Phase 6 + Phase 7)_
- ⬜ Document upload endpoints + storage (S3 or similar)
- ⬜ New tables: `lawyer_documents`, `verification_attempts` (Flyway V4+)
- ⬜ Gov API integration: SAT (cédula profesional), SEP/RNP (título)
- ⬜ Verification state machine → set `lawyer_profiles.verified_at`

### Phase 4 — Frontend: Vuetify → Tailwind
- ⬜ Install Tailwind in `frontend/`
- ⬜ Migrate landing pages first (lower risk)
- ⬜ Migrate dashboard layouts
- ⬜ Migrate chat / video views
- ⬜ Remove Vuetify dependency
- ⬜ Remove radix-vue dependency if redundant

---

## 🟡 In Progress

_(nothing active)_

---

## ✅ Done

### Phase 2 — Migrate Redis-API → Java _(completed 2026-05-19)_
- ✅ Build `com.legalnow.api.consultation` module (entity, repo, service, controller, DTOs, state machine)
- ✅ Build `com.legalnow.api.chat` module (ChatSession + ChatMessage entities, repos, services, controller, DTOs)
- ✅ Authorization helper: principal must be client/lawyer of consultation (or admin)
- ✅ Endpoints (JWT principal replaces `:email` in path):
  - `POST /api/v1/consultations`, `GET /api/v1/consultations[?status=&role=]`, `GET/PATCH/DELETE /{id}`, `POST /{id}/status`
  - `POST /api/v1/chat-sessions`, `GET /api/v1/chat-sessions`, `GET /{id}`, `POST /{id}/end`
  - `POST /api/v1/chat-sessions/{id}/messages`, `GET /api/v1/chat-sessions/{id}/messages`
- ✅ Smoke tests passed (auth, state machine, chat, authorization, idempotency)
- ✅ Cosmetic fix: `@Generated(INSERT/UPDATE)` on timestamps (POST responses now include them)
- ✅ Sub-phase 2a: Frontend `apiClient.ts` + `auth.ts` rewritten (Bearer, refresh-on-401, JWT real)
- ✅ Sub-phase 2b backend: enriched `ConsultationResponse` (`client`/`lawyer` embedded) + `GET /api/v1/users/{id}` public endpoint
- ✅ Sub-phase 2b frontend foundation: `types/chat.ts` + `chatSessionStore.ts` + `consultationStore.ts` (renamed)
- ✅ Sub-phase 2b chat components: `ChatList`, `ChatMessageHeader`, `ChatMessages`, `ConsultaCard`
- ✅ Sub-phase 2b dashboard views: `ConsultPayment`, `DashboardLawyers`, `PendingConsultations`, `ChatPanel`
- ✅ Dashboard wire-up batch (2026-05-19):
  - `DashboardUsers.vue` rewritten — real client consultations + tabs (En curso / Completadas / Rechazadas) + cancel
  - `AIAssistantChat.vue` "Nueva consulta" → emits `new-consult` → parent redirects to landing AI flow
  - `PendingConsultations.vue` 3-tab (Pendientes / Activas / Histórico) + state machine actions (accept/reject/schedule/start/complete/cancel)
  - `ChatPanel.vue` polish — consultations prop, query-string preselect, 5s message polling (Phase 6 WebSocket TODO)
- ✅ Verified in browser (2026-05-19): full flow client login → see consultations → lawyer accepts → both chat
- ✅ Decommission `backend/redis-api/` container in `docker-compose.yaml`
- ✅ Redis removed entirely — no `@Cacheable`, no Spring Redis dep, no redis service in compose
- ✅ `initJanusLib.ts` + `initTextRoomPerChat.ts` (→ `janusService.ts`) `saveChatMessage` fixed — Phase 3 PR #3

### Phase 3 — Janus room admin → Java _(merged PR #3, 2026-05-19)_
- ✅ Build Janus Admin API client in `com.legalnow.api.janus` (`JanusProperties`, `JanusClient`, `JanusService`)
- ✅ Wire `JanusService` into `ConsultationService` — room created on consultation
- ✅ Persist `janus_room_id` in `consultations` table on creation
- ✅ Frontend reads `roomId` from API, keeps `Janus.js` client
- ✅ Remove dead room-creation code from frontend; rename `initTextRoomPerChat.ts` → `janusService.ts`
- ✅ Issue short-lived Janus room pins from Java — generated on `IN_PROGRESS` transition, stored in `consultations.janus_pin`, exposed in `ConsultationResponse`, frontend passes in join payload

### Phase 1 — Java REST API base
- ✅ Spring Boot 3.3.5 Maven scaffold at `backend/api/`
- ✅ Postgres 16 + Flyway integration (V1 init schema, V2 specializations seed)
- ✅ Docker compose: `postgres` + `api` services
- ✅ Security baseline: BCrypt, CORS, stateless session, OpenAPI/Swagger UI
- ✅ Auth module: register, login, refresh (rotation), logout, `/me`
- ✅ JWT HS256 access + SHA-256-hashed refresh tokens persisted in DB
- ✅ Role-based registration (`client` / `lawyer` / `admin` from frontend payload)
- ✅ Global exception handler (401/409/400/500 JSON)
- ✅ `.env.example` template at repo root

---

## 🔵 Deferred (intentionally out of MVP)

- 🔵 `lawyer_profiles.hourly_rate` — re-introduce when per-minute/per-hour billing lands
- 🔵 Multi-currency lawyer pricing (phase 1 = MXN only)
- 🔵 Lawyer payouts via Stripe Connect splits (phase 5+)
- 🔵 Mercado Pago integration (Stripe first)
- 🔵 Recording transcription pipeline polish (Whisper already runs, but not wired into `chat_sessions.transcript_path`)

---

## Notes

- Update this file as work moves between columns. Newest done item on top of its phase.
- Status of any item being worked on: move to **In Progress** with a date stamp; back to **Done** on merge.
- Sub-tasks can be added inline under a phase as discovered — don't wait for a separate doc.
- Memory references: `MEMORY.md` index at `~/.claude/projects/-Users-fercho-code-personal-legal-now/memory/`.
