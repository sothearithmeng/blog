# Spring Boot 4 starter

A layered Spring Boot 4 / Java 21 REST API starter. It comes with a full working example
resource (`Task`) wired through every layer so you can see the intended pattern end to end,
then delete or copy it for your own resources.

Included out of the box:

- Layered package structure (controller → service → repository → entity) with a
  `package-info.java` explaining each package's job
- A working example resource (`/api/v1/tasks`) exercising every layer, including validation,
  pagination, and error handling
- Uniform API response/error envelopes, global exception handling
- Flyway-managed database schema (production), Hibernate auto-DDL (dev)
- Environment profiles (`dev` / `prod`) with sensible, safe-by-default configuration
- Structured logging: colored console in dev, JSON (Elastic Common Schema) in prod, both with
  90-day-retention rolling file output
- OpenAPI/Swagger UI, Spring Boot Actuator, CORS configuration, small utility classes

## Requirements

- Java 21+
- PostgreSQL for local `dev` and deployed `prod` runs (tests use an in-memory H2 database)

## Quick start

### Run it (defaults to the `dev` profile)

No profile needs to be set explicitly — the app falls back to `dev` when nothing else says
otherwise (see [Environments / profiles](#environments--profiles)). `.env.example` lists every
env var the app reads, with defaults and which ones `prod` requires — copy it to `.env` and
adjust, or export the values directly as shown below.

```powershell
# Needed only when your local PostgreSQL settings differ from the defaults.
$env:DB_URL = 'jdbc:postgresql://localhost:5432/postgres'
$env:DB_USERNAME = 'postgres'
$env:DB_PASSWORD = 'postgres'
.\gradlew.bat bootRun
```

That's it — Hibernate creates/updates the schema for you in `dev` (see
[Database & migrations](#database--migrations)). The database is supplied by PostgreSQL (or a
student's Compose file); the active Spring profile chooses who manages its schema. Swagger UI is at
`http://localhost:8080/swagger-ui.html`.

### Run the tests

```powershell
.\gradlew.bat test
```

Tests use an in-memory H2 database (`src/test/resources/application.yml`) — no PostgreSQL or
env vars required.

### Run it as `prod`

`prod` is deliberately strict: it **fails fast at startup** rather than silently falling back to
guessable defaults, so every required value below must be set explicitly.

```powershell
$env:SPRING_PROFILES_ACTIVE = 'prod'
$env:DB_URL = 'jdbc:postgresql://your-host:5432/your-db'
$env:DB_USERNAME = 'your-user'
$env:DB_PASSWORD = 'your-password'
$env:CORS_ALLOWED_ORIGINS = 'https://your-frontend.example.com'
.\gradlew.bat bootRun
```

In `prod`, Flyway (not Hibernate) creates/updates the schema — see below.

## Project structure

```text
com.kshrd.blog
├── BlogApplication.java     application entry point
├── common/                  types shared across layers (not one feature's alone)
│   └── entity/               BaseEntity — id + createdAt/updatedAt, extend it in every entity
├── config/                   app-wide setup: CORS, Jackson, OpenAPI (see below)
├── controller/                REST controllers — HTTP in, HTTP out, no business logic
├── dto/
│   ├── request/               validated input shapes for endpoints
│   └── response/              output shapes; controllers never return entities directly
├── entity/                    JPA entities (extend common.entity.BaseEntity)
├── exception/                 custom exceptions + the global handler that formats all errors
├── repository/                Spring Data JPA repositories
├── service/                   business-logic interfaces
│   └── impl/                  their @Service implementations
└── util/                      small static helper classes (not Spring beans)
```

Every package has a `package-info.java` with a one-paragraph explanation — read that first
when you're unsure where something belongs.

### Request flow

```text
Client → Controller → Service (interface) → Service.impl → Repository → Database
             │              │
             │              └─ throws BusinessException subtypes on failure
             └─ wraps the result in ApiResponse / unwraps errors via GlobalExceptionHandler
```

Controllers depend on the **service interface**, never the `impl` class — that's the one rule
this layout enforces structurally (the `impl` package is a separate package from `service`).

## The example flow: `Task`

`Task` (`/api/v1/tasks`) is a complete, working vertical slice — copy these six files as the
template for a new resource:

| Layer        | File                                                                                      | Purpose                                                                                                                          |
|--------------|-------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| Entity       | `entity/Task.java`                                                                        | Extends `BaseEntity`; own columns are `title`, `description`, `completed`, `reporterEmail`                                       |
| Repository   | `repository/TaskRepository.java`                                                          | Plain `JpaRepository<Task, UUID>` plus one derived query (`existsByTitleIgnoreCase`)                                             |
| Request DTOs | `dto/request/CreateTaskRequest.java`, `CreateTasksRequest.java`, `UpdateTaskRequest.java` | Bean Validation annotations with explicit `message`s; `CreateTasksRequest` wraps a list with cascading `@Valid` for batch create |
| Response DTO | `dto/response/TaskResponse.java`                                                          | `TaskResponse.from(Task)` maps the entity to its API shape (masks the email, truncates the description)                          |
| Service      | `service/TaskService.java` + `service/impl/TaskServiceImpl.java`                          | Business rules: duplicate-title conflict check, batch chunking, not-found handling                                               |
| Controller   | `controller/TaskController.java`                                                          | Maps HTTP to service calls; every response goes through `ResponseUtil`                                                           |

### Endpoints

| Method   | Path                                           | Body                 | Notes                                                       |
|----------|------------------------------------------------|----------------------|-------------------------------------------------------------|
| `POST`   | `/api/v1/tasks`                                | `CreateTaskRequest`  | 409 if a task with the same title (case-insensitive) exists |
| `POST`   | `/api/v1/tasks/batch`                          | `CreateTasksRequest` | Creates many at once, chunked internally in batches of 50   |
| `GET`    | `/api/v1/tasks/{id}`                           | —                    | 404 if not found                                            |
| `GET`    | `/api/v1/tasks?page=&size=&sortBy=&direction=` | —                    | Paginated list; `size` capped at 100                        |
| `PATCH`  | `/api/v1/tasks/{id}`                           | `UpdateTaskRequest`  | Partial update — only non-null fields are applied           |
| `DELETE` | `/api/v1/tasks/{id}`                           | —                    | Returns `204 No Content`                                    |

## API responses and errors

Every successful response is wrapped in `ApiResponse<T>` (`util/ApiResponse.java`):

```json
{ "success": true, "data": { "...": "..." }, "message": "Task created", "timestamp": "2026-08-19T10:00:00Z" }
```

Build these with `ResponseUtil` (`util/ResponseUtil.java`) instead of constructing
`ResponseEntity`/`ApiResponse` by hand:

- `ResponseUtil.ok(data)` / `ResponseUtil.ok(data, message)` → `200`
- `ResponseUtil.created(data)` / `ResponseUtil.created(data, message)` → `201`
- `ResponseUtil.noContent()` → `204`

Every error response is `ErrorResponse` (`dto/response/ErrorResponse.java`), produced by
`GlobalExceptionHandler` (`exception/GlobalExceptionHandler.java`) so controllers never need
`try`/`catch`:

```json
{ "code": "RESOURCE_NOT_FOUND", "message": "Task not found with id: ...", "status": 404, "timestamp": "...", "path": "/api/v1/tasks/...", "fieldErrors": null }
```

| Throw this                              | When                                           | HTTP status                       |
|-----------------------------------------|------------------------------------------------|-----------------------------------|
| `ResourceNotFoundException("Task", id)` | Requested entity doesn't exist                 | 404                               |
| `ConflictException("...")`              | Conflicts with existing state (e.g. duplicate) | 409                               |
| `BusinessException(ErrorCode, "...")`   | Any other domain rule violation                | per `ErrorCode`                   |
| (nothing — throw it yourself)           | `@Valid` request body fails validation         | 400, with `fieldErrors` populated |

`ErrorCode` (`exception/ErrorCode.java`) is the single place mapping a machine-readable code to
an HTTP status — add a new constant there before inventing a new exception type.

## Environments / profiles

The app has one shared configuration file plus two environment profiles, each documented inline
with *why*, not just *what*:

|                      | `application.yml` (base)                         | `application-dev.yml`                                              | `application-prod.yml`                                     |
|----------------------|--------------------------------------------------|--------------------------------------------------------------------|------------------------------------------------------------|
| Activation           | always loaded                                    | default when nothing else is set                                   | `SPRING_PROFILES_ACTIVE=prod`                              |
| Schema               | —                                                | Hibernate `ddl-auto: update`, Flyway **off**                       | Flyway migrates, Hibernate `ddl-auto: validate` only       |
| CORS                 | no origins (disabled) unless a profile sets some | `localhost:3000` / `:5173`                                         | `CORS_ALLOWED_ORIGINS` — **required**, no wildcard         |
| DB credentials       | local defaults (`postgres`/`postgres`)           | inherited from base                                                | **no defaults** — fails fast if unset                      |
| Logging              | —                                                | colored console + `logs/blog-dev-<date>.log`, `DEBUG` for app code | JSON (ECS) console + `logs/blog-<date>.log`, `WARN`/`INFO` |
| Swagger/OpenAPI docs | —                                                | enabled                                                            | disabled                                                   |
| Actuator             | `health` only                                    | all endpoints, full health detail                                  | `health`,`info` only, no detail                            |

Why dev doesn't use Flyway: local iteration is faster when Hibernate just updates the schema
from your entities as you edit them. This makes the first lessons simpler. Before merging, still
add a real migration (see below) so prod gets the change too.

## Database & migrations

- `common/entity/BaseEntity.java` — a `@MappedSuperclass` (not its own table) giving every
  entity that extends it a UUID `id` plus `createdAt`/`updatedAt`, auto-populated via
  `@PrePersist`/`@PreUpdate`. Extend this in every new entity.
- `db/migration/V1__init_schema.sql` — the only migration so far; creates the `tasks` table.
  Flyway is the source of truth for the schema in `prod`.
- **Adding a schema change**: create `src/main/resources/db/migration/V2__description.sql`
  (never edit an already-applied migration file — Flyway checksums them). `dev` doesn't need
  the migration to boot (it uses `ddl-auto: update`), but add it anyway so `prod` stays correct.
- **Flyway is off in `dev`** (`spring.flyway.enabled: false`) — local schema management is
  Hibernate's `ddl-auto: update` instead. A Compose file only starts PostgreSQL; it does not
  enable Flyway or change this schema-management choice. If you turn Flyway back on in `dev` and
  your local Postgres already
  has tables from before Flyway existed, it will refuse to run against that "unversioned"
  schema — set `spring.flyway.baseline-on-migrate: true` (with `baseline-version` matching your
  latest migration) to make it adopt the existing schema instead of failing.

## Logging

`logback-spring.xml` configures format/output (verbosity is controlled separately by
`logging.level.*` in each `application-*.yml`):

- **dev**: colored, human-readable console pattern, also written to
  `logs/blog-dev-<yyyy-MM-dd>.log`
- **prod**: one JSON object per line ([Elastic Common Schema](https://www.elastic.co/guide/en/ecs/current/index.html)),
  to both console and `logs/blog-<yyyy-MM-dd>.log` — built into Spring Boot 4, no extra
  dependency
- Both: a new file per day; anything older than **90 days** is deleted automatically
  (`maxHistory`), with a `1GB` total-size cap as a second safety net
- Override the file location/base name with the `LOG_FILE` env var (or
  `logging.file.name` per profile) — useful for containers, e.g. `/var/log/blog/app`

## Configuration classes (`config/`)

- **`CorsConfig` + `CorsProperties`** — binds `app.cors.*`. No default origins: an empty list
  means CORS is disabled entirely (secure by default) rather than silently allowing everything.
- **`JacksonConfig`** — tunes the app's `JsonMapper` (Spring Boot 4 defaults to Jackson 3,
  package `tools.jackson.*`, not the older Jackson 2 `com.fasterxml.jackson`). Dates serialize
  as ISO-8601 text, not epoch timestamps.
- **`OpenApiConfig`** — springdoc `OpenAPI` bean powering Swagger UI / `/v3/api-docs`.

## Utility classes (`util/`)

All static, not Spring-managed — use them anywhere, including outside `@Service` classes.

| Class             | For                                                                                                                                                          |
|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ApiResponse<T>`  | The success envelope shape — normally built via `ResponseUtil`, not directly                                                                                 |
| `ResponseUtil`    | Wraps controller results into `ResponseEntity<ApiResponse<T>>`                                                                                               |
| `PageUtils`       | Turns raw `page`/`size`/`sortBy`/`direction` query params into a bounded `Pageable` (size capped at 100; unsorted if `sortBy` is blank)                      |
| `StringUtils`     | Null-safe blank checks, `truncate`, `capitalize`, `mask` (for logging PII like emails)                                                                       |
| `CollectionUtils` | Null-safe empty checks, `emptyIfNull`, `partition` (chunking for batch operations)                                                                           |
| `DateTimeUtils`   | UTC-oriented `Instant`/`LocalDateTime` conversion and ISO formatting                                                                                         |
| `ValidationUtils` | Lightweight email/UUID/range checks — prefer Bean Validation annotations on DTOs for actual API input                                                        |
| `JsonUtils`       | Standalone Jackson 3 serialize/deserialize for logging/caching use, *not* controller responses (Spring's injected `ObjectMapper`/`JsonMapper` handles those) |

## Add a new feature

Using `Task` as the template, for a new resource called `Book`:

1. `entity/Book.java` — `@Entity`, `extends BaseEntity`, add your own columns.
2. `repository/BookRepository.java` — `extends JpaRepository<Book, UUID>` plus any derived
   queries you need.
3. `db/migration/V2__create_books_table.sql` — the real schema change, for `prod`.
4. `dto/request/CreateBookRequest.java` (+ `UpdateBookRequest`, batch wrapper if needed) — Bean
   Validation annotations with explicit `message`s.
5. `dto/response/BookResponse.java` — a `from(Book)` static factory mapping entity → API shape.
6. `service/BookService.java` (interface) + `service/impl/BookServiceImpl.java`
   (`@Service` + `@Transactional`) — business rules, throwing `ResourceNotFoundException` /
   `ConflictException` as needed.
7. `controller/BookController.java` — `@RestController`, depends on `BookService` (the
   interface), returns `ResponseEntity<ApiResponse<...>>` via `ResponseUtil`.

## Useful URLs (when running)

- Swagger UI: `http://localhost:8080/swagger-ui.html` (dev only)
- OpenAPI spec: `http://localhost:8080/v3/api-docs` (dev only)
- Actuator health: `http://localhost:8080/actuator/health`
- Actuator info (build metadata): `http://localhost:8080/actuator/info`
