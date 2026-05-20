# CLAUDE.md

Este archivo guía a Claude Code al trabajar con el código de este repositorio.

---

## Estilo de comunicación

Sin artículos. Sin relleno. Sin formalismos. Sin repetir preguntas.
El código habla por sí solo. Explica solo si te lo piden.
Sin firmas. Sin "avísame si...".
Errores: una línea. Corrección: mostrar solo las diferencias.
Commits: convencionales, asunto de ≤50 caracteres.
Revisión de PR: ubicación + problema + solución. Una línea por incidencia.

---

## Estructura del repositorio

Monorepo de la plataforma **LegalNow** — producto de consultoría legal que conecta clientes con abogados mediante chat, audio y video en tiempo real.

```
apps/webapp/               → Vue 3 + TypeScript + Vite (SPA principal)
apps/mobile/               → (futuro) React Native / Flutter
backend/rest-api/          → Java 21 + Spring Boot 3 (API principal, reemplaza redis-api)
backend/janus/             → Config de Janus WebRTC Gateway + volumen de grabaciones
backend/whisper/           → transcribe.py montado en contenedor faster-whisper-server
docker-compose.yaml        → Orquesta el stack completo en red bridge `legalnet`
```

> **Nota:** `backend/redis-api/` (Express + TypeScript) y `backend/redis_data/` fueron eliminados en la Phase 2 cleanup. La lógica de consultas y chat sessions migró a Spring Boot + PostgreSQL. El directorio `redis_data/` permanece en `.gitignore` por si se reintroduce Redis en el futuro (ver decisiones diferidas).

---

## Levantar el stack

Desde la raíz del repositorio:

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

Variables de entorno del servicio `webapp` (definidas en `docker-compose.yaml`):

- `VITE_JANUS_WSS`
- `VITE_JANUS_API_SECRET`
- `VITE_API_BASE_URL` ← reemplaza `VITE_REDIS_API`

---

## Frontend (`apps/webapp/`)

**Gestor de paquetes:** pnpm (hay `pnpm-lock.yaml` + `pnpm-workspace.yaml`), aunque los scripts de `package.json` usan `npm`. Ambos funcionan.

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

**Alias de ruta:** `@/` → `apps/webapp/src/` (configurado en `vite.config.ts` y `tsconfig`).

### Arquitectura del frontend

- **Entry:** `src/main.ts` — registra Pinia, Vue Router, Vuetify, vue-toastification y componentes globales de `radix-vue`.
- **Routing** (`src/router/index.ts`): rutas públicas + subárbol `/dashboard` protegido por `DashboardLayout.vue`. El guard global `beforeEach` llama a `useAuthStore().init()`, redirige usuarios no autenticados a `/login`, y enruta según `user.role` a `/dashboard/client` o `/dashboard/lawyer`. RBAC por ruta via `meta.roles: ['client' | 'lawyer']`.
- **Auth** (`src/stores/auth.ts`): store Pinia con `mockUsers` en el archivo (sin backend real aún). La sesión persiste en `localStorage` bajo la clave `tuabogado-auth`. `init()` la rehidrata al arrancar.
- **API client** (`src/services/apiClient.ts`): wrapper delgado de `fetch` sobre `VITE_API_BASE_URL`. Retorna `{ data, error }`. Siempre envía `ngrok-skip-browser-warning: true` (el stack se tuneliza frecuentemente con ngrok). **Ya no apunta a redis-api — ahora consume el Spring Boot API en `8080`.**
- **WebRTC:** dos implementaciones paralelas — `src/services/useJanus.ts` (composable) y `src/composables/initJanusLib.ts` (singleton modular con TextRoom + VideoRoom + AudioBridge simultáneos). La lib de Janus se carga globalmente vía `(window as any).Janus`.
- **Stores** (`src/stores/`): `auth`, `chatSessionStore`, `janusSessionStore`, `memoryStore`. El estado de chat es el puente entre los mensajes DataChannel de Janus y la UI.
- **Tipos** en `src/types/{chat,payment,user}.ts`. `Role = 'client' | 'lawyer'` es el enum central que controla routing y UI.

### Gotchas del frontend

- Los archivos `*.bkp` y `*_Original.vue` (ej: `App.vue.bkp`, `ChatMessages_Original.vue`) son copias legadas obsoletas — **no los edites, no están importados**.
- Las URLs de Janus WSS en `scripts/createRooms.ts` tienen una URL de ngrok hardcodeada — actualizar antes de correr.
- El árbol activo del frontend es `apps/webapp/src/`. Los archivos eliminados en la raíz (rama `mffm-remove-unnecesary-code`) son código movido, no borrado.

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
- PostgreSQL como fuente de verdad. Esquema gestionado por Flyway (`src/main/resources/db/migration/`).
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

**Alcance del MVP:** Autenticación + Búsqueda/Matching de abogados + Chat/Video.

---

## Decisiones diferidas

Estas decisiones fueron tomadas conscientemente y **no deben revertirse sin discusión**:

- **Redis eliminado** — se reintroducirá únicamente cuando haya necesidad real de multi-instancia o scale-out de WebSocket (ej: sesiones compartidas entre pods, pub/sub entre instancias). Por ahora PostgreSQL + Spring Session es suficiente.
- **Auth mock en frontend** — no conectar a backend real sin discutirlo primero.
- **Sin librerías de UI adicionales** — Vuetify ya genera bloat; evaluar antes de agregar cualquier cosa nueva.

---

## Convenciones de código

### General

- **Commits:** conventional commits — `feat:`, `fix:`, `chore:`, `refactor:`.
- **Env vars:** toda URL, IP o secreto va en variables de entorno — nunca hardcodeado.

### Java 21 + Spring Boot 3

- **Java 21 features:** usar `records` para DTOs y objetos de valor, `sealed classes` para modelar estados/resultados, pattern matching (`instanceof`, `switch`), y `SequencedCollection` donde aplique. Sin clases `final` innecesarias.
- **Arquitectura:** monolito modular — paquetes por dominio (`auth`, `consultation`, `lawyer`, `matching`, `payment`), no por capa. Cada módulo expone su API interna y oculta su implementación.
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
- **Controladores:** `@RestController` + `@RequestMapping`. Delgados — solo validación de entrada y delegación al servicio. Sin lógica de negocio.
- **Servicios:** `@Service` + `@Transactional` a nivel de método, no de clase. Toda la lógica de negocio vive aquí.
- **DTOs:** siempre `record` — sin getters/setters manuales. Un record de request, uno de response por caso de uso.
  ```java
  public record CreateConsultationRequest(
      @NotBlank String descripcion,
      @NotBlank String especialidad
  ) {}
  ```
- **Manejo de errores:** un `@RestControllerAdvice` global (`GlobalExceptionHandler`). Nunca capturar excepciones en controladores individualmente. Usar excepciones de dominio propias (`ConsultationNotFoundException`, `LawyerNotAvailableException`, etc.).
- **Respuestas:** estructura uniforme `ApiResponse<T>` con `success`, `data`, `message`. Sin exponer stack traces al cliente.
- **Validación:** `@Valid` en controladores + Bean Validation (`@NotBlank`, `@Email`, `@Size`). Validaciones de negocio en el servicio.
- **Spring Security:** `SecurityFilterChain` por bean — sin extender `WebSecurityConfigurerAdapter` (deprecado). JWT stateless; sin sesiones HTTP.
- **Inyección de dependencias:** siempre por constructor — sin `@Autowired` en campos.

### JPA / Hibernate

- **Entidades:** `@Entity` con `@Table(name = "...")` explícito. IDs con `UUID` generado por la app (`UUID.randomUUID()`), no por la base de datos — facilita tests y evita dependencia del secuenciador.
- **Relaciones:** cargar en `LAZY` por defecto — `EAGER` solo si hay justificación documentada.
- **Sin `@Data` de Lombok en entidades** — causa problemas con proxies de Hibernate. Usar `@Getter` + `@Setter` selectivos o records para proyecciones.
- **Proyecciones:** preferir interfaces o records de Spring Data para queries de solo lectura en vez de cargar entidades completas.
- **Repositorios:** extender `JpaRepository<T, UUID>`. Queries complejas con `@Query` JPQL — SQL nativo solo como último recurso.

### Flyway

- **Toda modificación al esquema va en una migración Flyway** — nunca con `spring.jpa.hibernate.ddl-auto=update` en ningún entorno.
- **Nomenclatura:** `V{versión}__{descripción}.sql` — ej: `V1__create_users_table.sql`, `V2__add_lawyer_profile.sql`.
- **Migraciones atómicas:** una responsabilidad por archivo. No mezclar DDL con DML masivo.
- **Nunca editar una migración ya aplicada** — crear una nueva que corrija.
- **Ruta:** `src/main/resources/db/migration/`.

### Redis

- **Prefijos de clave obligatorios** para evitar colisiones entre módulos:
  ```
  session:{userId}          ← sesiones JWT activas
  cache:lawyer:{id}         ← perfil de abogado cacheado
  cache:search:{hash}       ← resultados de búsqueda
  ratelimit:{userId}:{acción}
  ```
- **TTL explícito en toda clave** — ninguna clave se persiste indefinidamente sin justificación.
- **`RedisTemplate<String, String>`** con serialización JSON — sin `JdkSerializationRedisSerializer` (genera dependencia de versión).
- Redis es caché y estado temporal, **no fuente de verdad** — PostgreSQL es el sistema de registro.

### Frontend Vue 3

- **Composition API + `<script setup>`** únicamente — sin Options API.
- **Stores:** Pinia — sin Vuex ni estado global ad-hoc.
- **Estilos:** clases utilitarias de TailwindCSS. CSS scoped solo si es estrictamente necesario.
- **TypeScript:** sin `any` sin un comentario que explique por qué.

---

## NO hacer ❌

- **No conectar auth real ni base de datos** sin discutirlo primero.
- **No agregar nuevas librerías de UI** — Vuetify ya genera bloat en el bundle; no añadir más.
- **No editar archivos `*.bkp` ni `*_Original.vue`** — son legado, no se usan.
- **No reintroducir archivos Vue eliminados de la raíz** — el árbol canónico es `apps/webapp/src/`.
- **No reintroducir `redis-api` ni `redis-server`** — eliminados en Phase 2. Ver decisiones diferidas para cuándo Redis vuelve a tener sentido.
- **No hardcodear URLs de ngrok** en código fuente — usar variables de entorno.

---

## Pipeline de transcripción

```
Janus → volumen compartido (grabaciones) → contenedor Whisper → transcripciones
```

Whisper corre en un loop de polling contra el volumen de grabaciones de Janus. Las transcripciones quedan disponibles post-consulta.
