# CLAUDE.md

Guía Claude Code para este repo.

---

## Estilo de comunicación

Sin artículos. Sin relleno. Sin formalismos. Sin repetir preguntas.
Código habla solo. Explica solo si piden.
Sin firmas. Sin "avísame si...".
Errores: una línea. Corrección: solo diferencias.
Commits: convencionales, asunto ≤50 chars.
Revisión PR: ubicación + problema + solución. Una línea por incidencia.

---

## Estructura del repositorio

Monorepo plataforma **LegalNow** — consultoría legal: clientes ↔ abogados via chat, audio, video tiempo real.

```
apps/webapp/               → Vue 3 + TypeScript + Vite (SPA principal)
apps/mobile/               → (futuro) React Native / Flutter
backend/rest-api/          → Java 21 + Spring Boot 3 (API principal, reemplaza redis-api)
backend/janus/             → Config de Janus WebRTC Gateway + volumen de grabaciones
backend/whisper/           → transcribe.py montado en contenedor faster-whisper-server
docker-compose.yaml        → Orquesta el stack completo en red bridge `legalnet`
```

> **Nota:** `backend/redis-api/` (Express + TypeScript) y `backend/redis_data/` eliminados en Phase 2 cleanup. Lógica consultas + chat sessions migró a Spring Boot + PostgreSQL. Dir `redis_data/` permanece en `.gitignore` por si reintroduce Redis futuro (ver decisiones diferidas).

---

## Levantar el stack

Desde raíz:

```sh
docker compose up --build
```

| Servicio   | Puerto(s)                                    |
| ---------- | -------------------------------------------- |
| webapp     | `5173` (Vite dev server, corre con `--host`) |
| rest-api   | `8080`                                       |
| Janus WS   | `8188`                                       |
| Janus HTTP | `8088` / `7088`                              |
| RTP media  | `10000–10003/udp`                            |

Env vars servicio `webapp` (en `docker-compose.yaml`):

- `VITE_JANUS_WSS`
- `VITE_JANUS_API_SECRET`
- `VITE_API_BASE_URL` ← reemplaza `VITE_REDIS_API`

---

## Frontend (`apps/webapp/`)

**Package manager:** pnpm (`pnpm-lock.yaml` + `pnpm-workspace.yaml`), aunque scripts `package.json` usan `npm`. Ambos funcionan.

```sh
npm install
npm run dev              # servidor de desarrollo Vite
npm run build            # type-check + build Vite
npm run type-check       # vue-tsc --build
npm run lint             # eslint . --fix
npm run format           # prettier --write src/
npm run test:unit        # vitest
npm run test:e2e         # playwright (requiere `npm run build` primero en CI)
npm run rooms:create     # pre-crea salas Janus (TextRoom/AudioBridge)
```

**Alias ruta:** `@/` → `apps/webapp/src/` (en `vite.config.ts` + `tsconfig`).

### Arquitectura frontend

- **Entry:** `src/main.ts` — registra Pinia, Vue Router, Vuetify, vue-toastification + componentes globales `radix-vue`.
- **Routing** (`src/router/index.ts`): rutas públicas + subárbol `/dashboard` protegido por `DashboardLayout.vue`. Guard global `beforeEach` llama `useAuthStore().init()`, redirige no autenticados a `/login`, enruta según `user.role` a `/dashboard/client` o `/dashboard/lawyer`. RBAC por ruta via `meta.roles: ['client' | 'lawyer']`.
- **Auth** (`src/stores/auth.ts`): store Pinia con `mockUsers` en archivo (sin backend real aún). Sesión persiste `localStorage` clave `tuabogado-auth`. `init()` rehidrata al arrancar.
- **API client** (`src/services/apiClient.ts`): wrapper delgado `fetch` sobre `VITE_API_BASE_URL`. Retorna `{ data, error }`. Siempre envía `ngrok-skip-browser-warning: true` (stack tuneliza frecuente con ngrok). **Ya no apunta a redis-api — consume Spring Boot API en `8080`.**
- **WebRTC:** dos impl paralelas — `src/services/useJanus.ts` (composable) + `src/composables/initJanusLib.ts` (singleton modular con TextRoom + VideoRoom + AudioBridge simultáneos). Lib Janus carga global via `(window as any).Janus`.
- **Stores** (`src/stores/`): `auth`, `chatSessionStore`, `janusSessionStore`, `memoryStore`. Estado chat = puente entre mensajes DataChannel Janus y UI.
- **Tipos** en `src/types/{chat,payment,user}.ts`. `Role = 'client' | 'lawyer'` = enum central controla routing + UI.

### Gotchas frontend

- Archivos `*.bkp` y `*_Original.vue` (ej: `App.vue.bkp`, `ChatMessages_Original.vue`) = copias legadas obsoletas — **no editar, no importados**.
- URLs Janus WSS en `scripts/createRooms.ts` tienen URL ngrok hardcodeada — actualizar antes de correr.
- Árbol activo frontend = `apps/webapp/src/`. Archivos eliminados raíz (rama `mffm-remove-unnecesary-code`) = código movido, no borrado.

---

## Backend Spring API (`backend/rest-api/`)

Servicio Java 21 + Spring Boot 3. API REST bajo `/api`. Reemplaza completamente al anterior `redis-api` (Express + TypeScript).

```sh
./mvnw spring-boot:run          # desarrollo local
./mvnw clean package            # build → target/rest-api.jar
./mvnw test                     # tests unitarios + integración
```

### Diseño clave

- Arquitectura monolito modular — paquetes por dominio: `auth`, `consultation`, `lawyer`, `matching`.
- PostgreSQL = fuente de verdad. Esquema gestionado por Flyway (`src/main/resources/db/migration/`).
- DTOs como Java `record`. Controladores delgados, lógica en servicios `@Transactional`.
- Errores manejados centralmente por `GlobalExceptionHandler` (`@RestControllerAdvice`).

---

## Estado actual del proyecto (MVP)

| Módulo                            | Estado                                |
| --------------------------------- | ------------------------------------- |
| Autenticación y roles             | 🟡 Mock en frontend, sin backend real |
| Búsqueda de abogados              | 🔴 No iniciado                        |
| Matching con IA                   | 🔴 No iniciado                        |
| Chat en tiempo real               | 🟢 Funcional (Janus DataChannel)      |
| Videollamada                      | 🟢 Funcional (WebRTC + Janus)         |
| Grabaciones                       | 🟢 Funcional (volumen compartido)     |
| Transcripciones (Whisper)         | 🟡 Pipeline configurado, en pruebas   |
| Pagos                             | 🔴 No iniciado                        |
| Migración redis-api → Spring Boot | ✅ Phase 2 cleanup completado         |

**Alcance MVP:** Autenticación + Búsqueda/Matching abogados + Chat/Video.

---

## Decisiones diferidas

Decisiones tomadas conscientemente, **no revertir sin discusión**:

- **Redis eliminado** — reintroducir solo cuando haya necesidad real de multi-instancia o scale-out WebSocket (ej: sesiones compartidas entre pods, pub/sub entre instancias). Por ahora PostgreSQL + Spring Session suficiente.
- **Auth mock en frontend** — no conectar a backend real sin discutir primero.
- **Sin librerías UI adicionales** — Vuetify ya genera bloat; evaluar antes de agregar nuevo.

---

## Convenciones de código

### General

- **Commits:** conventional — `feat:`, `fix:`, `chore:`, `refactor:`.
- **Env vars:** toda URL, IP, secreto va en env vars — nunca hardcodeado.

### Java 21 + Spring Boot 3

- **Java 21 features:** usar `records` para DTOs y objetos valor, `sealed classes` para modelar estados/resultados, pattern matching (`instanceof`, `switch`), y `SequencedCollection` donde aplique. Sin clases `final` innecesarias.
- **Arquitectura:** monolito modular — paquetes por dominio (`auth`, `consultation`, `lawyer`, `matching`, `payment`), no por capa. Cada módulo expone su API interna y oculta implementación.
  ```
  src/main/java/com/legalnow/
    auth/
      AuthController.java
      AuthService.java
      domain/          ← entidades y repositorios del módulo
      dto/             ← records de entrada/salida
    consultation/
    lawyer/
    ...
  ```
- **Controladores:** `@RestController` + `@RequestMapping`. Delgados — solo validación entrada y delegación a servicio. Sin lógica negocio.
- **Servicios:** `@Service` + `@Transactional` a nivel método, no clase. Toda lógica negocio vive aquí.
- **DTOs:** siempre `record` — sin getters/setters manuales. Un record de request, uno de response por caso uso.
  ```java
  public record CreateConsultationRequest(
      @NotBlank String descripcion,
      @NotBlank String especialidad
  ) {}
  ```
- **Manejo errores:** un `@RestControllerAdvice` global (`GlobalExceptionHandler`). Nunca capturar excepciones en controladores individuales. Usar excepciones dominio propias (`ConsultationNotFoundException`, `LawyerNotAvailableException`, etc.).
- **Respuestas:** estructura uniforme `ApiResponse<T>` con `success`, `data`, `message`. Sin exponer stack traces al cliente.
- **Validación:** `@Valid` en controladores + Bean Validation (`@NotBlank`, `@Email`, `@Size`). Validaciones negocio en servicio.
- **Spring Security:** `SecurityFilterChain` por bean — sin extender `WebSecurityConfigurerAdapter` (deprecado). JWT stateless; sin sesiones HTTP.
- **Inyección dependencias:** siempre por constructor — sin `@Autowired` en campos.

### JPA / Hibernate

- **Entidades:** `@Entity` con `@Table(name = "...")` explícito. IDs con `UUID` generado por app (`UUID.randomUUID()`), no por DB — facilita tests y evita dependencia secuenciador.
- **Relaciones:** cargar `LAZY` por defecto — `EAGER` solo con justificación documentada.
- **Sin `@Data` Lombok en entidades** — causa problemas con proxies Hibernate. Usar `@Getter` + `@Setter` selectivos o records para proyecciones.
- **Proyecciones:** preferir interfaces o records Spring Data para queries solo lectura en vez de cargar entidades completas.
- **Repositorios:** extender `JpaRepository<T, UUID>`. Queries complejas con `@Query` JPQL — SQL nativo solo último recurso.

### Flyway

- **Toda modificación al esquema va en migración Flyway** — nunca con `spring.jpa.hibernate.ddl-auto=update` en ningún entorno.
- **Nomenclatura:** `V{versión}__{descripción}.sql` — ej: `V1__create_users_table.sql`, `V2__add_lawyer_profile.sql`.
- **Migraciones atómicas:** una responsabilidad por archivo. No mezclar DDL con DML masivo.
- **Nunca editar migración ya aplicada** — crear nueva que corrija.
- **Ruta:** `src/main/resources/db/migration/`.

### Redis

- **Prefijos de clave obligatorios** para evitar colisiones entre módulos:
  ```
  session:{userId}          ← sesiones JWT activas
  cache:lawyer:{id}         ← perfil de abogado cacheado
  cache:search:{hash}       ← resultados de búsqueda
  ratelimit:{userId}:{acción}
  ```
- **TTL explícito en toda clave** — ninguna clave persiste indefinida sin justificación.
- **`RedisTemplate<String, String>`** con serialización JSON — sin `JdkSerializationRedisSerializer` (genera dependencia de versión).
- Redis = caché y estado temporal, **no fuente de verdad** — PostgreSQL = sistema de registro.

### Frontend Vue 3

- **Composition API + `<script setup>`** únicamente — sin Options API.
- **Stores:** Pinia — sin Vuex ni estado global ad-hoc.
- **Estilos:** clases utilitarias TailwindCSS. CSS scoped solo si estrictamente necesario.
- **TypeScript:** sin `any` sin comentario explicando por qué.

---

## NO hacer ❌

- **No conectar auth real ni DB** sin discutir primero.
- **No agregar librerías UI nuevas** — Vuetify ya genera bloat en bundle; no añadir más.
- **No editar archivos `*.bkp` ni `*_Original.vue`** — legado, no se usan.
- **No reintroducir archivos Vue eliminados de raíz** — árbol canónico = `apps/webapp/src/`.
- **No reintroducir `redis-api` ni `redis-server`** — eliminados Phase 2. Ver decisiones diferidas para cuándo Redis vuelve a tener sentido.
- **No hardcodear URLs ngrok** en código fuente — usar env vars.

---

## Pipeline transcripción

```
Janus → volumen compartido (grabaciones) → contenedor Whisper → transcripciones
```

Whisper corre loop polling contra volumen grabaciones Janus. Transcripciones quedan disponibles post-consulta.

---

## Preferencias

- Utiliza siempre la salida comprimida al delegar tareas a subagentes.
- Por defecto, proporcione respuestas técnicas y concisas.

---

# Configuraciones de Agentes

Delegar a subagente según tarea:

```
task: [descripción]
scope: [investigator | builder | reviewer]
compressed output: yes
```

- [builder, reviewer]: `claude-sonnet-4-6` (mejor calidad)
- [investigator]: `claude-haiku-4-5` (velocidad + eficiencia tokens)
