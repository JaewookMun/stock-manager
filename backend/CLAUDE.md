# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Spring Boot REST API for stock management - provides realized P&L and cash flow data via Kiwoom Securities REST API integration.

## Build and Run Commands

```bash
# Generate QueryDSL Q-classes (required before first build or after entity changes)
gradlew.bat compileJava

# Build (Windows - use gradlew.bat or .\gradlew)
gradlew.bat build

# Run the application
gradlew.bat bootRun

# Run all tests
gradlew.bat test

# Run a single test class
gradlew.bat test --tests "app.jaewook.stockmanager.service.AssetServiceTest"

# Run a single test method
gradlew.bat test --tests "app.jaewook.stockmanager.service.AssetServiceTest.methodName"

# Clean build
gradlew.bat clean build
```

## Technology Stack

- Java 21, Spring Boot 3.4.10, Gradle
- Spring Data JPA + QueryDSL (openfeign fork `7.1`) + MariaDB (localhost:3306/stock_manager)
- Spring `RestClient` for HTTP calls (not RestTemplate/WebClient)
- Lombok

## Architecture

Layered architecture with strict DTO separation between layers.

### Package Structure

```
app.jaewook.stockmanager
├── api/                           # REST API layer
│   ├── dto/                       # Request/Response DTOs (API boundary)
│   ├── mapper/                    # Controller-level mappers (API DTO <-> Service DTO)
│   └── *Controller.java           # REST controllers
├── service/                       # Business logic layer
│   ├── dto/                       # Command/Result DTOs (Service boundary)
│   └── mapper/                    # Service-level mappers (Entity <-> Service DTO)
├── domain/                        # Domain entities (JPA entities)
├── infra/                         # External system integrations
│   ├── db/                        # Spring Data JPA repositories + StockQueryRepository (QueryDSL)
│   └── kiwoom/                    # Kiwoom Securities API client
│       ├── dto/                   # Kiwoom API Request/Response DTOs
│       ├── KiwoomApiClient.java   # REST API calls to Kiwoom
│       └── KiwoomApiMapper.java   # Service DTO <-> Kiwoom DTO mapping
├── schedule/                      # Scheduled tasks
└── config/                        # Spring configuration
```

### DTO Conventions

All DTOs are Java records with `@Builder` (Lombok). Related DTOs are grouped as nested records inside a parent class (e.g., `AssetCommand.RealizedPnl`, `AssetCommand.CashFlow`).

- **API Layer**: `*Request` / `*Response`
- **Service Layer**: `*Command` / `*Result`
- **Infrastructure Layer**: `Kiwoom*Request` / `Kiwoom*Response`

### Data Flow Pattern

```
HTTP Request → Request DTO → ControllerMapper → Command DTO → Service → Infrastructure → Kiwoom API
                                                                      ↓
HTTP Response ← Response DTO ← ControllerMapper ← Result DTO ← KiwoomApiMapper ← Kiwoom Response
```

### Mapper Responsibilities

- `*ControllerMapper` (in `api/mapper/`): API DTO ↔ Service DTO
- `*DbMapper` (in `service/mapper/`): Entity ↔ Service DTO
- `KiwoomApiMapper` (in `infra/kiwoom/`): Kiwoom DTO ↔ Service DTO (not in `service/mapper/` — it belongs to the infra layer)

### QueryDSL

`StockQueryRepository` (in `infra/db/`) uses `JPAQueryFactory` (configured in `QueryDslConfig`) for dynamic queries. Q-classes (`QStock`, `QStockDetail`) are generated into `build/generated/querydsl` via `gradlew.bat compileJava`.

Result mapping uses `Projections.constructor` — the record's constructor parameter order must exactly match the `select(...)` column order in the query. `GET /api/stocks/screen` filters by `exchangeType`, `minRoe`/`maxRoe`, `minPer`/`maxPer` and returns results ordered by market cap descending.

### Kiwoom API Result Wrapper

`KiwoomApiClient` methods return a `Kiwoom*Result` record that bundles both the deserialized response body and the pagination headers:

```java
// Single request returns (response, header) tuple — no ResponseEntity leaks into the service layer
public KiwoomRealizedPnlResult getRealizedPnlByPeriod(...) {
    return new KiwoomRealizedPnlResult(response, responseHeader);
}
```

`KiwoomResponseHeader` wraps `cont-yn` and `next-key` response headers as `hasNext: boolean` and `nextKey: String`.

### API Response Wrapper

All API responses are wrapped in `ApiResponse<T>`:
- `success`: boolean status
- `data`: the response payload

There is currently no `@ControllerAdvice` for global error handling.

### Token Management

`KiwoomTokenManager` handles OAuth token lifecycle:
- Caches tokens per account in a `ConcurrentHashMap<accountNumber, TokenInfo>`
- Tokens are refreshed 1 minute before actual expiration
- App key/secret are stored per account in the `Account` table (DB), not in `application.yml`

### External API Integration

Kiwoom Securities REST API integration for:
- OAuth token issuance (`issueAccessToken`)
- Realized P&L by period (`ka10073`)
- Cash flow transactions (`kt00015`)
- Stock info list (`ka10099`) - full market listing with pagination
- Stock basic info (`ka10001`) - individual stock financial details

### Pagination & Caching Pattern

**Continuous Query (연속 조회)**: Kiwoom API uses header-based pagination:
- `cont-yn: Y` + `next-key` in response headers indicate more data
- Service layer owns the pagination loop; `KiwoomApiClient` makes individual single requests
- Period limits: 3 months for realized P&L, 12 months for cash flow — the service splits large date ranges into conforming chunks automatically

**Fetch History Caching**: Data is cached by date to avoid redundant API calls:
- `RealizedPnlFetchHistory` / `CashFlowFetchHistory` track which dates have been fetched per account
- On each request, the service checks for missing dates and only calls the Kiwoom API for those

### Entity Conventions

All entities use:
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` — satisfies JPA, prevents direct instantiation
- `@Builder` on an explicit all-args constructor — no setters; entities are immutable after construction
- Exception: `StockUpdateLog` has `success()` / `fail()` mutating domain methods

### Scheduled Stock Update

`StockUpdateScheduler` runs at `0 30 15 * * MON-FRI` (weekday 15:30 KST):
1. Fetches all KOSPI + KOSDAQ stocks via `ka10099` (with pagination)
2. Calls `ka10001` for each stock's financial details, rate-limited via `Thread.sleep(1000)` (5 req/s)
3. Upserts `Stock` rows (insert-only for new codes) and replaces all `StockDetail` rows
4. Records the run in `StockUpdateLog` (RUNNING → SUCCESS/FAILED)

### Testing

All tests are full integration tests (`@SpringBootTest`) against a live MariaDB instance and real Kiwoom API — no mocking framework is used. Tests look up real `Account` records from the DB by alias (e.g., `"위탁"`). A running MariaDB instance and valid Kiwoom API credentials in the DB are required to run tests.

### Database

- Schema: `ddl-auto: none` — schema is managed externally, not auto-generated by Hibernate
- `open-in-view: false` — no lazy loading outside transactions
- `Account` stores Kiwoom API credentials (`appkey`, `secretkey`) per account; transaction entities (`RealizedPnl`, `CashFlow`) reference accounts by `accountNumber` String, not via JPA foreign key

### First-Time Setup

`DataInit.java` contains a commented-out `@PostConstruct` method that seeds initial `Account` rows. Uncomment it temporarily to insert seed data on first run.
