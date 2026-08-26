# AGENTS.md

This file provides guidance to coding agents when working with code in this repository.

## Project Overview

**ESS (Employee Self Service)** is a Java/Spring web application for NY State Senate employees. It's a single-artifact, multi-package system with three major feature areas: Time/Attendance tracking, Supply requisitions, and Travel requests, plus a shared core module.

- **Version**: 2.2
- **Java**: JDK 21 (`<release>21</release>`, compiled with `-parameters`)
- **Build Tool**: Maven 3
- **Deployment**: Tomcat 11 WAR (Jakarta EE — Servlet 6.1; all code uses the `jakarta.*` namespace, not `javax.*`)
- **Databases**: PostgreSQL (local) + Oracle (remote SFMS)
- **Frontend**: Node.js 20.12.0 (pinned via Volta). Two coexisting stacks — React/webpack and legacy AngularJS/Grunt — see [Frontend](#frontend).

## Build & Test Commands

### Full Build with Tests
```bash
mvn verify
```
Builds backend + frontend, runs unit tests (via Surefire) and integration tests (via Failsafe).

The frontend is wired into the Maven build: `exec-maven-plugin` runs `npm ci` in `src/main/webapp` at `generate-sources`, and that package's `postinstall` script runs `npm run build && bower install && grunt compile` — so one `mvn` invocation builds both the React bundle and the legacy Angular/LESS assets.

### Quick Build (Skip Tests)
```bash
mvn clean package -DskipTests
```

### Run Specific Test
Tests are split by JUnit 4 category (`@UnitTest` and `@IntegrationTest`):
```bash
# Run only unit tests
mvn test

# Run only integration tests
mvn verify -Dgroups=gov.nysenate.ess.core.annotation.IntegrationTest

# Run a single test class
mvn test -Dtest=YourTestClass
```

### Frontend Only
```bash
cd src/main/webapp
npm ci                # also triggers the full frontend build via postinstall
npm run build         # React bundle only (webpack, production mode)
npm run dev           # webpack dev server on :3000, proxying /api to :8080
npm test              # vitest
npm run lint          # eslint over WEB-INF/app
grunt compile         # legacy AngularJS/LESS assets only
```

### Database Migrations
```bash
mvn flyway:migrate
```
Uses Flyway for the PostgreSQL schema, across the `ess`, `supply`, and `travel` schemas. Migration files live in `src/main/resources/sql/migrations/`; config in `src/main/resources/flyway.conf` (create from `flyway.conf.example`).

Migrations after the initial four use a timestamp version convention: `V<YYYYMMDD>.<HHmm>__description.sql`. `flyway.outOfOrder` is enabled, so a migration dated earlier than one already applied will still run.

## Project Structure

### Top-Level Packages under `gov.nysenate.ess`

- **`core`**: Shared infrastructure — authentication (Apache Shiro + LDAP), database access, caching, models, controllers, and utility layers. Houses the REST API base class and response formatting.
- **`time`**: Time and attendance tracking (timesheet management, payroll, accruals).
- **`supply`**: Supply requisitions with requisition workflow, item catalog, destinations, authorization/permissions, notifications, and WebSocket support.
- **`travel`**: Travel request management, approvals, reimbursement, provider integration.
- **`web`**: Web-layer configuration, page controllers, filters, security configuration, XSRF validation.

### Architecture Pattern: Layered MVC

Each module splits into the usual `model/`, `service/`, `dao/`, and `controller/` packages, plus two worth calling out:

- **`client/`**: External service integration (LDAP, Google Maps, SFMS Oracle)
- **`view/`**: `*View` classes are the JSON-serialized shape of a domain object; REST responses wrap views, not domain models directly.

Within `controller/`, REST controllers extend `BaseRestApiCtrl` and page controllers return JSP views from `src/main/webapp/WEB-INF/view/`.

### Database Layer

- **Dual DataSources**: `localDataSource` (PostgreSQL) and `remoteDataSource` (Oracle)
- **Transaction Managers**: `localTxManager` and `remoteTxManager`, selected via `@Transactional`
- **Spring JDBC**: No ORM. Uses `JdbcTemplate` and `NamedParameterJdbcTemplate` directly
- **Connection Pooling**: c3p0, min 3 / max 10 connections per source (`DbConnectionPoolConfig`)
- **Schema Maps**: Named schemas (`masterSchema`, `tsSchema`, `essSchema`, `supplySchema`, `travelSchema`, `baseSfmsSchema`) built in `DatabaseConfig` and injected as a bean property

### REST API & Response Format

- **Base Path**: `/api/v1` (`BaseRestApiCtrl.REST_PATH`); admin endpoints under `BaseRestApiCtrl.ADMIN_REST_PATH` (`/api/v1/admin`)
- **Response Wrapper**: All endpoints return `BaseResponse` subclasses:
  - `ViewObjectResponse<T>` for single objects
  - `ListViewResponse<T>` for paginated lists
  - `ErrorResponse` / `ViewObjectErrorResponse` for failures
- **Authentication**: Shiro-based (LDAP in prod, configurable in dev)
- **Authorization**: Permission checks via `@RequiresPermissions` or explicit permission objects
- **Pagination**: `LimitOffset` and `PaginatedList` utilities; `BaseRestApiCtrl` (in `core.controller.api`) rejects a `limit` query param above 1000

### Configuration

**Config Files** — `app.properties` and `flyway.conf` are gitignored; create them from the `.example` files in `src/main/resources/`:
- `app.properties`: Runtime level, data directories, LDAP, DB credentials, mail, auth, frontend framework toggles
- `flyway.conf`: Database migration settings

`shiro.ini` is checked in, not generated. Its `[urls]` section maps Ant-style paths to filter beans declared in `web/config/SecurityConfig` (`essAuthc`, `essApiAuthc`, `essRedmineAuthc`) and `web/security/filter/` (`verifyAuthz`, `deptAuthz`). Anything not matched by an earlier rule falls through to `/** = essAuthc, verifyAuthz, sessionTimeoutFilter, deptAuthz`.

**Spring Profiles**: `dev`, `test`, `prod` (`spring.profiles.active` context-param in `web.xml`; checked-in value is `dev`)

**Key Config Classes** (`core/config/`, plus `web/config/`):
- `PropertyConfig`: Loads environment-specific properties
- `WebApplicationConfig`: MVC setup, Jackson converters, `StandardServletMultipartResolver` (no size limits configured in the app — the container's limits apply)
- `DatabaseConfig`: Dual datasources, schema map bean
- `DbConnectionPoolConfig`: c3p0 pools
- `SecurityConfig`: Apache Shiro with custom LDAP + API authentication filters
- `AsyncConfig`: `ThreadPoolTaskScheduler` (pool size 8) and the `essAsync` executor (core pool size 4)
- `LdapConfig`, `JacksonConfig`, `EventBusConfig`, `FreemarkerConfig`, `WebSocketConfig`, `EverfiClientConfig`, `BeanPostProcessorConfig`

### Frontend

Two frontends coexist, and **which one is served is a per-app runtime property**, not a build-time decision:

```properties
frontend.myinfo.framework = angularjs   # or: react
frontend.time.framework   = angularjs
frontend.supply.framework = angularjs
frontend.travel.framework = angularjs
```

Each page controller (`TimePageCtrl`, `SupplyPageCtrl`, `TravelPageCtrl`, `MyInfoPageCtrl`) reads its property through `FrontendFramework.fromProperty`. On `REACT` it returns `forward:/assets/dist/index.html` (the SPA); otherwise it renders the module's JSP. An absent or unrecognized value logs a warning and **defaults to AngularJS**. When changing frontend behavior, check which stack the app is actually configured to serve.

**React SPA** (`src/main/webapp/WEB-INF/app/`, entry `index.js`):
- React 18, react-router-dom 6, TanStack Query 5, react-hook-form + zod, Tailwind CSS 4 (via PostCSS), Headless UI, `@stomp/stompjs`
- Built by webpack to `assets/dist/`; `app` is an alias for `WEB-INF/app`
- Tested with vitest + Testing Library (`WEB-INF/app/test`)
- `npm run dev` serves on :3000 and proxies `/api`, `/assets`, `/logout`, and POST `/login` to `localhost:8080`

**Legacy AngularJS** (`src/main/webapp/assets/js/src/`, with the JSPs under `WEB-INF/`):
- Built by Grunt (LESS → `assets/css/dist`, uglify → `assets/js/dest`), Bower for vendor deps
- JSPs in `WEB-INF/view/` use custom tag files in `WEB-INF/tags/` and Shiro JSP tags

### Testing Framework

- **Unit Tests**: JUnit **4** (`@Test`, `@Category`) with Mockito, AssertJ, and Hamcrest. There is no JUnit 5 on the classpath — do not write `org.junit.jupiter` tests.
- **Integration Tests**: Spring Test with `@ContextConfiguration`
- **Test Categories** (`core.annotation`, applied via `@Category(UnitTest.class)` on the class or method):
  - `@UnitTest`: reproducible unit tests — run by Surefire
  - `@IntegrationTest`: DB-dependent tests — run by Failsafe
  - `@TestDependsOnDatabase`: additional marker, usually combined with `@IntegrationTest`
  - `@SillyTest`, `@WorkInProgress`: scratch/non-reproducible tests, not intended to pass. Neither Surefire nor Failsafe runs them, so a test in these categories never executes in CI.
- **Test Config**: `TestConfig` (Spring profile `test`) loads `app.properties`, `test.default.properties`, `test.app.properties`, `test.data.properties`, and `shiro.ini`. None is declared `ignoreResourceNotFound`, so a missing file fails the context — and `test.app.properties` is gitignored with no `.example`, so it has to be written by hand. Logging via `test.log4j2.xml`.

### Dependencies

Versions live in the `<properties>` block of `pom.xml`. The ones that change how you write code:

- **Lombok** — configured as an annotation processor, but used sparsely (only `@Builder`, in 4 classes). Match the surrounding code rather than introducing Lombok into plain classes.
- **EhCache** — reached through the `javax.cache` (JSR-107) API, not EhCache's own.
- **Guava** — collections, plus the EventBus used for async messaging.

## Common Development Patterns

### Adding a New REST Endpoint

1. Create a controller extending `BaseRestApiCtrl`
2. Annotate with `@RestController` and `@RequestMapping(BaseRestApiCtrl.REST_PATH + "/module/path")`
3. Use `@RequestMapping` on methods; responses auto-serialized to JSON via Jackson
4. Return `ViewObjectResponse<>` or `ListViewResponse<>` for success, `ErrorResponse` for errors
5. Use `LimitOffset` from query params for pagination
6. Check permissions via `@RequiresPermissions` or explicit Shiro `SecurityUtils.getSubject().checkPermission()`

### Accessing Dual Databases

- Inject `@Qualifier("localJdbcTemplate")` or `@Qualifier("remoteJdbcTemplate")`
- Methods typically have `@Transactional(transactionManager = "localTxManager")` or `"remoteTxManager"`
- Use schema placeholders (e.g., `${masterSchema}`) in SQL; resolved via `SqlQueryUtils.substituteSchema(schemaMap, sql)`

### Adding Caching

**Spring's `@Cacheable`/`@CacheEvict` is not used anywhere in this codebase.** Caching goes through a hand-rolled EhCache layer in `core.service.cache`:

- Add a constant to the `CacheType` enum (`core.model.cache.CacheType`)
- Extend the abstract `CachingService`, implementing `cacheType()`, `clearCache(boolean warmCache)`, `evictContent(String key)`, and `getCron()`
- `CachingService` registers a `@PostConstruct` cron trigger that periodically clears and re-warms the cache, using the cron expression from `getCron()`
- Obtain the backing `Cache` through `EssCacheManager.createCache(...)`; `EssCacheManager` also exposes `removeEntry`, `clearCaches`, and `getStatsView` for admin tooling
- See `EmployeeEhCache` for a worked example

### Async Tasks

Inject `@Qualifier("essAsync")` executor or use `@Async` on service methods (core pool size 4). Scheduled work uses the `ThreadPoolTaskScheduler` bean (pool size 8).

### WebSocket/Real-Time Updates

`supply.socket` handles real-time requisition updates via Spring WebSocket + STOMP. See `RequisitionStompService` and `WebSocketConfig`. The React client connects with `@stomp/stompjs` over SockJS.

## Important Notes

- **Configuration Setup**: `for f in src/{main,test}/resources/*.example; do cp -- "$f" "${f%.example}"; done` — see README.md for the full setup, including `grunt.properties.json` and `test.log4j2.xml`
- **Email in Dev**: Set `mail.test.address` in `app.properties` to avoid real email sends
- **Dev Login**: Set `auth.enabled = false` and `auth.master.pass` to log in as any user
- **Session Handling**: `web.xml` sets `tracking-mode` COOKIE (no `jsessionid` in URLs) and `session-timeout` to `-1`; timeouts are enforced in code by `sessionTimeoutFilter`, not the container
- **XSRF Protection**: Custom token validation in `XsrfValidator`; tokens stored in session
- **Department Authorization**: Optional whitelist filtering via `restrict.department.enabled` and `restrict.department.whitelist`, enforced by the `deptAuthz` filter
- **API IP Whitelist**: Certain endpoints allow unauthenticated access for whitelisted IPs (`auth.api.ip.whitelist`)
- **Migrations run during `mvn verify`**: the Flyway plugin binds `migrate` to `pre-integration-test`, so a build against an unconfigured `flyway.conf` fails there

## Deployment Considerations

- WAR file: `target/ess##2.2.war` (finalName pattern: `${project.artifactId}##${project.version}`)
- Tomcat Context Root: `/` (application runs at server root)
- Database: Requires pre-existing PostgreSQL and Oracle connections configured
- Data Directory: External file storage at path in `data.dir` (default `/data/ess/`) — travel attachments, logs, PEC docs
- Logging: Log4j2 config in `src/main/resources/log4j2.xml` (from `.example`); output under `data.log_subdir`
