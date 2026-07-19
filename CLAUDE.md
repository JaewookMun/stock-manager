# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Stock Manager — a personal tool that aggregates and serves stock trading data (realized P&L, cash flow, stock screening) sourced entirely from the Kiwoom Securities (키움증권) OAuth REST API. It is a monorepo with two independently-versioned subprojects:

- `backend/` — Spring Boot 3.4.10 / Java 21 REST API. Fetches data from Kiwoom, persists it to MariaDB, and serves reshaped/cached data to the frontend.
- `frontend/` — React 19 + Vite SPA that consumes the backend's `/api/*` endpoints.

**Each subproject has its own `CLAUDE.md` with full build/run commands and architecture — read `backend/CLAUDE.md` or `frontend/CLAUDE.md` before working inside that directory.** This root file only covers what spans both.

## Documentation map (`docs/`)

`docs/` holds living product/technical docs with deliberately separated concerns. Check the right one before making non-trivial changes — don't duplicate content across them:

- `docs/PRD.md` — why the product exists, target users, scope, functional/non-functional requirements. No implementation detail, no status tracking.
- `docs/FEATURES.md` — how each PRD requirement (sections 4.1–4.6, numbered to match `PRD.md` §4) actually behaves today: processing logic, caching, UI behavior. No progress/status tracking — that belongs in `TASK.md`.
- `docs/API_SPEC.md` — field-level request/response spec for every REST endpoint.
- `docs/TRD.md` — architecture, tech stack, infra/deployment gaps, security posture, tech debt.
- `docs/TASK.md` — the single source of truth for remaining work and its priority (`P0`–`P3`), organized by feature area to mirror `FEATURES.md`.

When you change what a feature *does*, update `FEATURES.md`. When you change what work *remains*, update `TASK.md`. Keep the two separate.

## Commands (quick reference — see subproject CLAUDE.md for full detail)

Backend (`backend/`):
```bash
gradlew.bat compileJava        # generate QueryDSL Q-classes — required before first build / after entity changes
gradlew.bat bootRun
gradlew.bat test               # full integration tests — needs a live local MariaDB + real Kiwoom credentials in DB, no mocking
gradlew.bat build -x test      # always skip tests when just building
```

Frontend (`frontend/`):
```bash
npm run dev      # port 5173, proxies /api -> http://localhost:8080
npm run build
npm run lint      # no test framework is configured
```

## Cross-cutting architecture

- **Single external data source**: Kiwoom Securities OAuth REST API. The backend never proxies it directly to the frontend — it fetches, persists to MariaDB, and serves cached/reshaped data. This absorbs Kiwoom's rate limit (5 req/s, enforced via `KiwoomRateLimiter`) and per-request period limits (3 months for realized P&L, 12 months for cash flow) so the frontend/user never has to think about them.
- **Request flow**: frontend SWR hooks (`frontend/src/api/*.js`) → `/api/*` (Vite dev proxy → `localhost:8080`) → backend layered architecture (`api` → `service` → `domain`/`infra`) → Kiwoom REST API, with MariaDB as the persistence + cache layer.
- **No authentication anywhere** — both the API and the SPA assume a single-user, personal-use deployment (see `docs/PRD.md` §2.2). Don't add auth-gated behavior without checking that assumption still holds.
- **No global exception handling** on the backend — error responses are not yet standardized; don't assume a consistent error JSON shape.

## Git workflow

`main` is the integration branch. Long-lived `backend` and `frontend` branches on `origin` track work scoped to each subproject and are merged into `main` via PR (see git log for the pattern: `Merge pull request #N from JaewookMun/backend|frontend`). When making backend-only or frontend-only changes, follow this branch convention rather than committing feature work directly to `main`.

## Known gaps (don't assume these exist)

- No DB migration tooling — `ddl-auto: none` and no Flyway/Liquibase/DDL script in-repo. Schema must be reproduced manually against a MariaDB instance.
- No CI/CD, no Dockerfile, no environment profile separation (single `application.yml`, DB credentials currently committed in plaintext there).
- Several frontend routes exist but render placeholders despite the backend being complete: `/assets/cash-flow`, `/analysis/company-filtering`, and `/` (Home). Don't assume a route shows real data just because it's registered — check `docs/TASK.md` for current per-feature status before working on or around these.
- No account registration API/UI — new `Account` rows (with Kiwoom app key/secret) must be seeded manually via the commented-out `DataInit.java` `@PostConstruct`.