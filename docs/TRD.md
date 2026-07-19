# Stock Manager — 기술 요구사항 문서 (TRD)

## 0. 문서 정보

| 항목 | 내용 |
|---|---|
| 목적 | 시스템 아키텍처, 기술 스택, 인프라/배포, 비기능 기술 요구사항을 정의 |
| 대상 독자 | 백엔드/프론트엔드 개발자, 신규 합류자, 운영 담당자 |
| 관련 문서 | [`PRD.md`](./PRD.md)(제품 배경/목적) · [`FEATURES.md`](./FEATURES.md)(기능별 동작 방식) · [`API_SPEC.md`](./API_SPEC.md)(API 요청/응답 스펙) · [`TASK.md`](./TASK.md)(작업 목록/진행 상태) — 본 문서는 "어떻게 구현되어 있고, 어떤 기술적 제약/원칙을 따르는가"에 집중 |
| 작성 기준 | `backend/build.gradle`, `backend/src/main/resources/application.yml`, `frontend/package.json`, `frontend/vite.config.js`, `backend/CLAUDE.md`, `frontend/CLAUDE.md`, 소스 코드 실사 |
| 최종 갱신 | 2026-07-08 |

---

## 1. 시스템 아키텍처 개요

```
┌─────────────────────┐        /api/*        ┌──────────────────────┐        REST (OAuth)      ┌──────────────────┐
│  Frontend (SPA)      │  ───────────────────▶ │  Backend (REST API)   │  ───────────────────────▶ │  키움증권 OpenAPI  │
│  React 19 + Vite      │  ◀─────────────────── │  Spring Boot 3.4.10   │  ◀─────────────────────── │  (외부 데이터 소스) │
│  (localhost:5173)     │      JSON             │  (localhost:8080)     │       JSON                └──────────────────┘
└─────────────────────┘                        └──────┬────────────────┘
                                                        │ JDBC (MariaDB Connector)
                                                        ▼
                                                 ┌──────────────┐
                                                 │  MariaDB       │
                                                 │  stock_manager │
                                                 └──────────────┘
```

- 프론트엔드는 백엔드가 재가공한 데이터만 소비하며, 키움 API를 직접 호출하지 않는다.
- 백엔드는 키움 API 응답을 단순 프록시하지 않고 **DB에 적재 후 재사용**하는 구조(캐싱)를 기본 원칙으로 한다 — 키움 API의 레이트 리밋(초당 5건) 및 조회 기간 제약을 흡수하기 위함.
- 인증 계층 없음(단일 사용자 개인 도구 전제). 배포 토폴로지(리버스 프록시, 컨테이너 등)는 현재 정의되어 있지 않다 — 로컬 개발 환경 기준 구성만 존재.

---

## 2. 기술 스택

### 2.1 Backend

| 항목 | 버전/선택 | 비고 |
|---|---|---|
| 언어 | Java 21 (toolchain 고정) | |
| 프레임워크 | Spring Boot 3.4.10 | `spring-boot-starter-web`, `spring-boot-starter-data-jpa` |
| 빌드 도구 | Gradle (wrapper, `gradlew.bat`) | `io.spring.dependency-management` 1.1.7 |
| ORM/쿼리 | Spring Data JPA + QueryDSL (`io.github.openfeign.querydsl` **fork** `7.1`, jakarta APT) | 공식 QueryDSL이 아닌 OpenFeign 포크 사용 — Jakarta EE 9+ 네임스페이스 호환 때문으로 추정. Q클래스는 `build/generated/querydsl`에 생성되며 `gradlew compileJava` 실행이 선행되어야 함(신규 clone 후 필수 스텝) |
| DB 드라이버 | `org.mariadb.jdbc:mariadb-java-client` (runtime) | |
| HTTP 클라이언트 | Spring `RestClient` | `RestTemplate`/`WebClient`가 아닌 `RestClient` 채택(Spring 6.1+ 최신 동기 클라이언트) — 키움 API(동기 REST) 호출에 사용 |
| 보일러플레이트 제거 | Lombok | `@Builder`, `@NoArgsConstructor` 등 |
| 스케줄링 | Spring `@Scheduled` | 별도 스케줄러 라이브러리(Quartz 등) 미사용 |
| 인증/보안 | 없음 | `spring-boot-starter-security` 의존성 미포함 |

### 2.2 Frontend

| 항목 | 버전/선택 | 비고 |
|---|---|---|
| 프레임워크 | React 19.1.1 | |
| 빌드 도구 | Vite 7.1.7 (`@vitejs/plugin-react`) | React Compiler 미적용 |
| 라우팅 | react-router 7.9.3 (`createBrowserRouter`) | |
| 데이터 페칭/상태관리 | SWR 2.3.6 | 서버 상태뿐 아니라 사이드바 open/close 같은 로컬 UI 상태도 SWR의 `mutate`로 관리하는 독특한 패턴 채택(전용 상태관리 라이브러리 도입 대신 기존 SWR 재사용) |
| UI/스타일 | Bootstrap 5.3.8 + react-bootstrap 2.10.10 + SCSS (`sass` 1.77.6, `api: modern-compiler`) | Datta Able Free React Admin Template 기반 |
| 아이콘 | Phosphor(`ph ph-*`), Tabler | 폰트 아이콘 |
| 기타 | `simplebar-react`(커스텀 스크롤바) + `react-device-detect`(데스크톱/모바일 분기) | |
| Lint | ESLint 9.36.0 + `eslint-plugin-react-hooks`, `eslint-plugin-react-refresh` | |
| 타입 시스템 | 없음 (JS, `.jsx`) | `@types/react`는 개발 편의용 devDependency만 존재, 실제 TS 마이그레이션은 안 됨 |
| 테스트 | 없음 | 테스트 러너/프레임워크 미구성 |

### 2.3 외부 연동

| 항목 | 내용 |
|---|---|
| 데이터 소스 | 키움증권(Kiwoom Securities) OpenAPI REST, OAuth 인증 |
| 사용 API | OAuth 토큰 발급, `ka10073`(일자별종목별실현손익-기간), `kt00015`(위탁종합거래내역), `ka10099`(종목정보 리스트), `ka10001`(주식기본정보), 조건검색 목록조회(미연동) |
| 통신 방식 | 동기 REST (`RestClient`), 페이지네이션은 응답 헤더(`cont-yn`, `next-key`) 기반 "연속조회" |
| 레이트 리밋 | 초당 5건 (키움 정책) |

---

## 3. 백엔드 아키텍처

### 3.1 레이어드 아키텍처 및 패키지 구조

```
app.jaewook.stockmanager
├── api/                     # REST API 계층
│   ├── dto/                 # *Request / *Response (API 경계 DTO)
│   ├── mapper/               # *ControllerMapper (API DTO ↔ Service DTO)
│   ├── AccountController.java
│   ├── AssetController.java
│   └── StockController.java
├── service/                 # 비즈니스 로직 계층
│   ├── dto/                  # *Command / *Result (Service 경계 DTO)
│   └── mapper/                # *DbMapper (Entity ↔ Service DTO)
├── domain/                  # JPA 엔티티
├── infra/
│   ├── db/                   # Spring Data JPA Repository + StockQueryRepository(QueryDSL)
│   └── kiwoom/                # 키움 API 연동 클라이언트
│       ├── dto/                # Kiwoom*Request / Kiwoom*Response
│       │   └── stock/           # ka10099/ka10001 전용 DTO
│       ├── KiwoomApiClient.java
│       ├── KiwoomApiMapper.java   # Kiwoom DTO ↔ Service DTO (infra 계층에 위치, service/mapper가 아님)
│       ├── KiwoomTokenManager.java
│       └── KiwoomRateLimiter.java
├── schedule/                 # @Scheduled 배치
└── config/                   # Spring 설정 (QueryDslConfig 등)
```

### 3.2 계층 간 데이터 흐름

```
HTTP Request → *Request DTO → *ControllerMapper → *Command DTO → *Service → infra → Kiwoom API
                                                                                  ↓
HTTP Response ← *Response DTO ← *ControllerMapper ← *Result DTO ← KiwoomApiMapper ← Kiwoom Response
```

- 계층 간에는 항상 전용 DTO를 거치며, 엔티티가 API 계층까지 직접 노출되지 않는다.
- 모든 DTO는 Java `record` + `@Builder`(Lombok)이며, 연관된 DTO는 부모 클래스 안에 nested record로 그룹화한다(예: `AssetRequest.RealizedPnl`, `AssetRequest.CashFlow`).
- 네이밍 컨벤션: API 계층 `*Request`/`*Response`, Service 계층 `*Command`/`*Result`, Infra 계층 `Kiwoom*Request`/`Kiwoom*Response`.

### 3.3 QueryDSL 사용 규칙

- `StockQueryRepository`(`infra/db/`)가 `QueryDslConfig`에 구성된 `JPAQueryFactory`로 동적 조건 쿼리를 수행한다.
- 결과 매핑은 `Projections.constructor` 사용 — **record의 생성자 파라미터 순서가 쿼리의 `select(...)` 컬럼 순서와 정확히 일치해야 함** (순서가 어긋나도 컴파일 에러 없이 값이 뒤섞이는 런타임 버그가 될 수 있어 변경 시 각별한 주의 필요).
- Q클래스(`QStock`, `QStockDetail`)는 `gradlew compileJava` 실행 시 `build/generated/querydsl`에 생성된다. 엔티티 변경 후에는 반드시 재빌드해야 IDE/컴파일 오류가 해소된다.

### 3.4 키움 API 연동 계층 상세

- **응답 래핑**: `KiwoomApiClient`의 각 메서드는 `Kiwoom*Result` record를 반환하며, 여기에 역직렬화된 응답 바디와 `KiwoomResponseHeader`(페이지네이션 헤더 `cont-yn`→`hasNext: boolean`, `next-key`→`nextKey: String`으로 변환)를 함께 담는다. 이를 통해 `ResponseEntity`가 Service 계층까지 새어나가지 않도록 캡슐화한다.
- **토큰 관리**: `KiwoomTokenManager`가 계좌별 OAuth 토큰을 `ConcurrentHashMap<accountNumber, TokenInfo>`로 캐싱하며, 만료 1분 전 자동 갱신한다. 앱키/시크릿키는 `Account` 엔티티(DB)에 계좌별로 저장되며 `application.yml`에는 없다.
- **레이트 리밋**: `KiwoomRateLimiter`가 단일 스레드 `ScheduledExecutorService`로 200ms 간격(초당 5건)을 강제한다.
  - `fetchAllPages`: 연속조회(페이지네이션) 순차 루프.
  - `scheduleAll`: N개의 독립 호출을 `i * 200ms` 오프셋으로 팬아웃 실행, 개별 실패는 스킵하고 전체를 중단시키지 않음(대량 종목 상세 조회에 사용).
- **기간 제약 및 자동 분할**: 실현손익 3개월/요청, 현금흐름 12개월/요청, 실현손익은 최근 1년 이내 조회만 가능. `AssetService`가 요청 구간을 이 제약에 맞춰 자동 분할·순차 호출한다.
- **조회 이력 캐싱**: `RealizedPnlFetchHistory`/`CashFlowFetchHistory`가 계좌·날짜 단위로 조회 완료 여부를 기록해, 이미 가져온 날짜는 키움 API를 재호출하지 않고 DB 데이터를 재사용한다.

### 3.5 엔티티 컨벤션

- 모든 엔티티: `@NoArgsConstructor(access = PROTECTED)`(JPA 요구사항 충족 + 직접 인스턴스화 방지) + 명시적 all-args 생성자에 `@Builder`. Setter 없음(불변).
- 예외: `StockUpdateLog`만 `success()`/`fail()` 도메인 메서드로 상태를 변경하는 mutable 엔티티.
- 연관관계: 트랜잭션 엔티티(`RealizedPnl`, `CashFlow`, `*FetchHistory`)는 `accountId`(Long FK)가 아닌 `Account`를 `@ManyToOne(fetch = LAZY)`로 직접 참조한다.

---

## 4. 프론트엔드 아키텍처

### 4.1 진입/라우팅 흐름

```
main.jsx → App.jsx → routes/DefaultRouter.jsx → layouts/Layout.jsx (<Outlet/>으로 페이지 래핑)
```

- `DefaultRouter.jsx`는 `path: "/"` + `Component: Layout`인 루트 엔트리를 여러 개 두고, 각 엔트리 하위에 페이지를 nest하는 패턴을 사용한다. 신규 라우트 추가 시 이 패턴을 따른다.
- 라우트 규칙: `/[category]/[page]` (예: `/assets/realized-pnl`, `/analysis/company-filtering`).

### 4.2 상태 관리

- 서버 상태: SWR 훅을 `src/api/*.js`에 정의(예: `useGetAccounts`, `useGetRealizedPnl`). fetcher는 단순 `fetch(url).then(res => res.json())`.
- 로컬 UI 상태: 별도 상태관리 라이브러리(Redux/Zustand 등) 없이 SWR을 네트워크 호출 없는 로컬 상태 컨테이너로 재사용(`src/api/menu.js`, 사이드바 open/close). 신규 UI 전역 상태 필요 시 이 패턴을 따르거나, 복잡도가 커지면 전용 상태관리 도입을 검토해야 한다.
- API 프록시: Vite dev 서버가 `/api/*` 요청을 `http://localhost:8080`으로 프록시(`vite.config.js`). 프로덕션 빌드 시에는 별도 리버스 프록시/동일 오리진 구성이 필요하다(현재 미정의).

### 4.3 네비게이션/메뉴

- `src/menu-items/`의 각 파일이 `{ id, title, type: "group", children: [...] }` 형태로 메뉴 그룹을 export, `index.jsx`에서 취합.
- 현재 활성 메뉴 파일은 `navigation.jsx`, `analysis.jsx`, `assets.jsx` 3개뿐이며, 나머지(`charts-maps.jsx`, `forms.jsx`, `pages.jsx`, `tables.jsx`, `ui-components.jsx`, `other.jsx`)는 Datta Able 템플릿에서 남은 미사용 placeholder이므로 참고하지 않는다.

### 4.4 스타일링

- SCSS 진입 체인: `src/index.scss` → `src/assets/scss/style.scss` → 파셜들.
- 테마 변수: `src/assets/scss/settings/_bootstrap-variables.scss`, `_color-variables.scss`, `_theme-variables.scss`.
- `pc-sidebar`, `pc-item`, `pc-container` 등 클래스명은 Datta Able 템플릿 유래이며 임의로 재정의하지 않는다.

### 4.5 컴포넌트 컨벤션

- 모든 컴포넌트는 PropTypes로 prop 검증(TypeScript 대신).
- 데이터 테이블/폼은 `RealizedPnl.jsx`의 `COLUMNS` 배열 패턴(선언적 컬럼 정의: `label`, `key`, `align`, `render`, 선택적 `getValue`/`colorize`)을 참고해 일관성 있게 구현.

---

## 5. 데이터베이스

### 5.1 스키마 관리 방식

- MariaDB, DB명 `stock_manager`.
- **`ddl-auto: none`** — Hibernate가 스키마를 자동 생성/변경하지 않는다. 스키마는 외부(수동 DDL 또는 별도 마이그레이션)로 관리되며, **현재 저장소에 마이그레이션 도구(Flyway/Liquibase)나 DDL 스크립트가 존재하지 않는다.** 엔티티 변경 시 개발자가 직접 스키마를 맞춰야 하며, 이는 팀 확장 시 리스크 요인이다.
- `open-in-view: false` — 트랜잭션 밖에서 지연 로딩 접근 시 예외 발생. Service 계층에서 필요한 연관 데이터를 트랜잭션 내에 명시적으로 로딩해야 한다.
- `show-sql: true`, 애플리케이션 패키지 로그 레벨 `DEBUG`로 설정되어 있어 로컬 개발 시 SQL이 콘솔에 출력된다(운영 환경에서는 조정 필요).

### 5.2 주요 테이블(엔티티) 개요

| 엔티티 | 역할 | 관계 |
|---|---|---|
| `Account` | 계좌 마스터, 키움 앱키/시크릿키 보관 | 1:N → 아래 4개 |
| `RealizedPnl` | 실현손익 원장(종목·일자별 1행) | N:1 → `Account` |
| `RealizedPnlFetchHistory` | 실현손익 조회 이력 캐시(계좌·날짜) | N:1 → `Account` |
| `CashFlow` | 거래 원장(약 45필드) | N:1 → `Account` |
| `CashFlowFetchHistory` | 현금흐름 조회 이력 캐시(계좌·날짜) | N:1 → `Account` |
| `Stock` | 상장 종목 마스터(코드 unique) | 1:N → `StockDetail` |
| `StockDetail` | 재무 지표 스냅샷(append-only 시계열) | N:1 → `Stock` |
| `StockUpdateLog` | 배치 실행 감사 로그 | 독립 |

`Stock`/`StockDetail`은 `Account`/거래 엔티티와 관계가 없다(시장 전체 공용 데이터, 계좌 독립적).

---

## 6. 개발 환경 / 빌드 / 실행

### 6.1 사전 요구사항

- Java 21 (toolchain으로 고정되어 있어 다른 버전 사용 시 빌드 실패 가능)
- Node.js (Vite 7 / React 19 요구 버전 — package.json에 engines 명시 없음, LTS 권장)
- 로컬 MariaDB 인스턴스 (`localhost:3306/stock_manager`)
- 유효한 키움증권 OpenAPI 앱키/시크릿키 (DB `Account` 테이블에 사전 등록 필요)

### 6.2 백엔드 명령어

```bash
# QueryDSL Q클래스 생성 (최초 빌드 전 또는 엔티티 변경 후 필수)
gradlew.bat compileJava

# 빌드 (Windows, 테스트 항상 스킵)
gradlew.bat build -x test

# 실행
gradlew.bat bootRun

# 전체 테스트 실행 (실 DB + 실 키움 API 필요)
gradlew.bat test

# 단일 테스트 클래스/메서드
gradlew.bat test --tests "app.jaewook.stockmanager.service.AssetServiceTest"
gradlew.bat test --tests "app.jaewook.stockmanager.service.AssetServiceTest.methodName"
```

### 6.3 프론트엔드 명령어

```bash
npm run dev       # 개발 서버, 포트 5173, /api → localhost:8080 프록시
npm run build     # 프로덕션 빌드
npm run preview   # 빌드 결과 프리뷰
npm run lint      # ESLint
```

### 6.4 최초 계좌 시딩

- `DataInit.java`에 주석 처리된 `@PostConstruct` 메서드가 있다. 최초 실행 시 이를 임시로 주석 해제하여 초기 `Account` 레코드(앱키/시크릿키 포함)를 삽입해야 한다. 계좌 등록 API/화면은 아직 없다.

---

## 7. 배포/인프라 현황 (Gap 명시)

현재 저장소에는 **배포 관련 산출물이 존재하지 않는다.** 다음 항목들은 향후 필요 시 별도 정의가 필요하다.

| 항목 | 현재 상태 |
|---|---|
| 컨테이너화 | Dockerfile 없음 |
| CI/CD | `.github/workflows` 등 파이프라인 없음 — 빌드/테스트/배포 모두 수동 |
| 환경별 설정 분리 | `application.yml` 단일 파일만 존재 (`application-dev.yml`/`application-prod.yml` 등 프로파일 분리 없음) |
| 프로덕션 리버스 프록시/동일 오리진 구성 | 미정의 (Vite dev proxy는 개발 전용) |
| 모니터링/APM | 없음 (Actuator 등 헬스체크 엔드포인트도 미확인) |
| 시크릿 관리 | DB 계정 정보가 `application.yml`에 평문으로 하드코딩되어 저장소에 커밋되어 있음. 키움 앱키/시크릿키도 DB에 평문 저장. **운영 배포 전 반드시 환경변수/시크릿 매니저로 전환 필요** |

---

## 8. 보안 기술 요구사항 (현황 및 개선 필요사항)

| 영역 | 현재 상태 | 개선 필요사항 |
|---|---|---|
| 인증/인가 | 없음 (Spring Security 미적용) | 다중 사용자/외부 배포 시 로그인 체계 도입 필요. 개인용 로컬 실행에 한정한다면 우선순위 낮음 |
| 자격증명 저장 | DB 접속 정보가 `application.yml`에 평문 커밋, 키움 앱키/시크릿키가 `Account` 테이블에 평문 저장 | 환경변수 또는 `.env`(gitignore 처리) + 운영 환경에서는 시크릿 매니저/KMS로 암호화 저장 전환. `application.yml`의 자격증명은 즉시 git 이력에서 제거를 검토해야 함(이미 커밋된 값은 유출로 간주) |
| 전역 예외 처리 | `@ControllerAdvice` 없음 — 예외 발생 시 Spring 기본 오류 응답 노출 가능(스택트레이스 등 정보 노출 위험) | `@ControllerAdvice` + 표준 에러 응답 포맷 도입 |
| 입력 검증 | Bean Validation(`@Valid`, `@NotNull` 등) 사용 흔적 없음, 컨트롤러 파라미터 바인딩에 의존 | 필수 파라미터 누락/형식 오류에 대한 명시적 검증 및 4xx 응답 정의 필요 |
| CORS | 별도 설정 확인 안 됨(dev 프록시로 우회 중이라 이슈가 드러나지 않음) | 프로덕션에서 프론트/백엔드가 다른 오리진일 경우 CORS 정책 정의 필요 |

---

## 9. 에러 처리 / 로깅

- **에러 처리**: 전역 예외 처리기가 없어 계층별로 예외를 어떻게 다루는지 일관되지 않다. 신규 기능 추가 시 최소한 `@ControllerAdvice` 도입 전까지는 서비스 계층에서 의미 있는 예외를 던지고, 컨트롤러 단에서 처리 방침을 통일하는 것을 권장.
- **로깅**: `app.jaewook.stockmanager` 패키지 전체가 `DEBUG` 레벨로 설정되어 있으며, `AssetController`/`AccountController` 등 일부 컨트롤러에 `@Slf4j` 기반 요청 로깅(`log.info("GET /api/... - request: {}", request)`)이 존재. 구조화 로깅(JSON) 또는 로그 수집 파이프라인은 없음.

---

## 10. 테스트 전략

- **백엔드**: `@SpringBootTest` 기반 통합 테스트만 존재하며 **Mock을 사용하지 않는다.** 테스트는 실제 로컬 MariaDB와 실제 키움 API(별칭 `"위탁"` 등으로 DB에서 실제 `Account`를 조회)를 대상으로 동작하므로, CI 환경에서 그대로 실행하려면 테스트용 DB와 키움 자격증명 프로비저닝이 선행되어야 한다. 현재 순수 단위 테스트(Mockito 등)는 없다.
- **프론트엔드**: 테스트 프레임워크 미구성. UI 회귀는 수동 확인에 의존.
- **개선 방향(제안)**: 순수 로직(날짜 분할, 레이트리밋 스케줄링 등)에 대한 단위 테스트 도입, 키움 API를 모킹하는 계약 테스트 계층 분리 검토.

---

## 11. 성능/확장성 고려사항

- **외부 API 병목**: 키움 API 초당 5건 제한이 시스템 전체 처리량의 상한을 결정한다. 특히 종목 배치 갱신(전 종목 상세 조회, 2000+ 건)은 순차 처리 특성상 완료까지 상당 시간이 소요되며, 병렬화는 레이트리밋을 위반하지 않는 범위(`KiwoomRateLimiter`)로 제한된다.
- **DB 성장 패턴**: `StockDetail`이 append-only 시계열이라 배치 실행마다 전 종목 수만큼 행이 누적된다. 장기적으로 테이블 크기가 선형 증가하므로 인덱스 전략(예: `stock_id` + `updatedAt` 복합 인덱스) 및 보관 주기 정책(오래된 스냅샷 아카이빙/삭제) 검토가 필요하나 현재 미정의.
- **캐싱 전략**: 애플리케이션 레벨 캐시(Redis 등)는 없으며, "DB 자체를 캐시로 사용"하는 fetch-history 패턴이 유일한 캐싱 계층이다.
- **커넥션 풀/스레드**: 기본 Spring Boot/HikariCP 기본값을 그대로 사용 중으로 추정(별도 튜닝 설정 미확인).

---

## 12. 기술 부채 / 향후 개선 과제 요약

우선순위는 [`PRD.md`](./PRD.md) 6장(로드맵)과 별개로, 기술적 관점에서만 정리한다.

1. **DB 마이그레이션 도구 부재** (`ddl-auto: none`인데 DDL 스크립트가 저장소에 없음) — Flyway/Liquibase 도입으로 스키마 변경 이력을 코드로 관리해야 한다.
2. **시크릿 하드코딩** — DB 자격증명이 `application.yml`에 평문 커밋되어 있다. 즉시 환경변수 전환 및 git 이력 정리를 검토해야 한다.
3. **전역 예외 처리 부재** — `@ControllerAdvice` 도입.
4. **CI/CD 파이프라인 부재** — 최소한 빌드+테스트 자동화부터 시작.
5. **테스트 피라미드 역전** — 통합 테스트만 존재하고 단위 테스트가 없어 피드백 루프가 느리고 CI 구성이 어렵다.
6. **환경 프로파일 분리 부재** — dev/prod `application-*.yml` 분리 필요.
7. **입력 검증 부재** — Bean Validation 도입 검토.
