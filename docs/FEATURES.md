# Stock Manager — 기능 명세서 (FEATURES)

## 0. 문서 정보

| 항목 | 내용 |
|---|---|
| 목적 | 현재 코드베이스에 구현된 기능이 **어떻게 동작하는가**(배경, 처리 로직, 화면 동작)를 사실 기준으로 문서화 |
| 대상 독자 | 백엔드/프론트엔드 개발자, 신규 합류자 |
| 관련 문서 | [`PRD.md`](./PRD.md) — 제품 배경/목적/로드맵 ("왜, 무엇을 만들려 하는가"). [`API_SPEC.md`](./API_SPEC.md) — 엔드포인트별 요청/응답 필드 상세 스펙. [`TASK.md`](./TASK.md) — **기능별 남은 작업 범위와 진행 상태**. [`kiwoom_REST_API_doc.xlsx`](open_api/kiwoom_REST_API_doc.xlsx) — 각 절에서 언급하는 키움 API ID의 공식 원본 스펙(API ID별 시트). 본 문서는 진행 상태·작업 목록·우선순위를 다루지 않는다 — 그것은 전적으로 `TASK.md`의 역할이다. |
| 번호 체계 | 4.1~4.6은 [`PRD.md`](./PRD.md) 4장(기능 요구사항)의 항목 번호와 1:1로 대응한다. |
| 작성 기준 | 소스 코드(controller/dto/entity), `backend/README.md`, `backend/CLAUDE.md`, `frontend/CLAUDE.md` 실사 |
| 최종 갱신 | 2026-07-08 |

---

## 4.1 계좌 관리

### 개요
등록된 키움증권 계좌 목록을 조회한다. 실현손익/현금흐름 조회 시 계좌를 선택하는 용도로 프론트엔드에서 사용된다.

### API
`GET /api/accounts` — 상세 요청/응답 스펙은 [`API_SPEC.md`](./API_SPEC.md#2-get-apiaccounts) 참고. 응답에는 키움 앱키/시크릿키(`appkey`, `secretkey`)가 포함되지 않는다(내부 인증용, `Account` 엔티티에만 존재).

### 계좌 추가 방법
현재 계좌 등록 API/화면은 없다. 신규 계좌는 `backend/.../DataInit.java`의 주석 처리된 `@PostConstruct` 코드를 직접 수정해 시딩하는 방식으로만 추가할 수 있다.

### 프론트엔드
- `src/api/accounts.js`의 `useGetAccounts()` 훅이 SWR로 캐싱하여 조회.
- 실현손익 화면(`RealizedPnl.jsx`)에서 계좌 셀렉트 박스로 사용, 로드 완료 시 첫 번째 계좌를 기본 선택.

구현 진행 상태 및 남은 작업은 [`TASK.md`](./TASK.md) 1장 참고.

---

## 4.2 실현손익 조회 (자산관리)

### 개요
증권사 앱은 실현손익 조회 기간이 제한적이므로, 키움 API(`ka10073` 일자별종목별실현손익요청-기간, [원본 스펙](open_api/kiwoom_REST_API_doc.xlsx) 시트 `ka10073`)를 통해 데이터를 DB에 누적하고 임의 기간을 재조합해 조회할 수 있게 한다.

### API
`GET /api/assets/realized-pnl` — 상세 요청/응답 스펙은 [`API_SPEC.md`](./API_SPEC.md#3-get-apiassetsrealized-pnl) 참고. `stockCode` 파라미터로 특정 종목만 필터링할 수 있다.

### 처리 로직 (`AssetService`)
1. 요청 구간(`startDate`~`endDate`) 중 이미 조회된 날짜는 `RealizedPnlFetchHistory`를 통해 스킵하고 DB(`RealizedPnl`)에서 바로 읽는다.
2. 아직 조회되지 않은 날짜만 골라 키움 API를 호출한다. 이때 **1회 요청당 최대 3개월** 제약이 있어, 미조회 구간을 3개월 단위로 자동 분할해 순차 호출한다.
3. **키움 API 특성상 최근 1년 이내 데이터만 조회 가능** — 조회 구간이 1년을 초과하는 과거 구간을 포함하면 해당 부분은 결과에서 제외/경고 처리된다.
4. 신규로 가져온 데이터는 `RealizedPnl` 엔티티로 저장하고, 해당 날짜를 `RealizedPnlFetchHistory`에 기록한다.
5. 최종적으로 DB에 있는 전체 구간 데이터를 합쳐 응답한다. 3개월/1년 분할은 백엔드 내부 동작이며 프론트엔드/사용자는 인지할 필요가 없다.

### 프론트엔드 — `/assets/realized-pnl` (`RealizedPnl.jsx`)

- **계좌 선택**: `useGetAccounts()`로 로드, 기본값은 목록의 첫 번째 계좌.
- **기간 선택**: 프리셋 버튼 `1개월 / 3개월 / 6개월 / 1년 / 2년` + `직접선택` 토글(체크 시 시작/종료일 직접 입력 가능). 프리셋 선택 시 종료일은 오늘, 시작일은 `오늘 - N개월`로 자동 계산.
- **조회 버튼**: `accountId`, `startDate`, `endDate`가 모두 유효할 때만 활성화. 클릭 시 SWR 키가 갱신되어 `GET /api/assets/realized-pnl` 호출. `stockCode` 입력 UI는 없다(API는 지원).
- **결과 테이블 컬럼**: 일시, 종목명, 실현손익(색상), 수익률(색상, `+`/`-` 부호 포함 `%` 표시), 수량, 매입가, 매도체결가, 수수료, 세금, **매입금액**(=매입가×수량, 클라이언트 계산), **매도금액**(=매도체결가×수량, 클라이언트 계산).
- **색상 규칙**: 값 > 0 → `text-danger`(빨강/이익), 값 < 0 → `text-primary`(파랑/손실) — 국내 시세 표시 관행.
- **합계**: 카드 헤더에 조회된 전체 실현손익 합계를 색상과 함께 표시.
- **상태 처리**: 조회 전(안내 문구) / 로딩 중 / 에러 / 빈 결과 각각 다른 메시지를 테이블 본문에 표시.

구현 진행 상태 및 남은 작업은 [`TASK.md`](./TASK.md) 2장 참고.

---

## 4.3 입출금/거래내역(현금흐름) 조회

### 개요
키움 `kt00015` 위탁종합거래내역요청 API([원본 스펙](open_api/kiwoom_REST_API_doc.xlsx) 시트 `kt00015`)를 통해 계좌의 전체 거래 원장(입출금, 매매, 대출, 환전 등)을 조회한다.

### API
`GET /api/assets/cash-flow` — 상세 요청/응답 스펙(45개 필드 전체)은 [`API_SPEC.md`](./API_SPEC.md#4-get-apiassetscash-flow) 참고.

### 처리 로직 (`AssetService`)
- 실현손익과 동일한 캐싱 패턴: `CashFlowFetchHistory`로 계좌·날짜 단위 조회 이력을 추적, 미조회 날짜만 키움에 요청.
- **1회 요청당 최대 12개월** 제약 — 요청 구간이 이를 초과하면 자동 분할 조회.

### 프론트엔드 — `/assets/cash-flow`
`CashFlow.jsx`는 `<div>cashflow</div>` 만 렌더링하는 placeholder이며, `/api/assets/cash-flow`를 호출하는 SWR 훅은 아직 없다. 사이드바 메뉴("입출금 내역")는 등록되어 있어 라우트 진입은 가능하다.

구현 진행 상태 및 남은 작업은 [`TASK.md`](./TASK.md) 3장 참고.

---

## 4.4 기업 조회 / 종목 스크리닝

### 개요
네이버 증권 등은 시가총액 기준 정렬만 제공하고 ROE/PER 기준 정렬·필터를 지원하지 않는다. 키움 API에도 이를 위한 단일 API가 없어, 자체적으로 수집한 종목 마스터+재무 데이터(4.6 참고)를 대상으로 조건 검색을 제공한다.

### API
`GET /api/stocks/screen` — 상세 요청/응답 스펙은 [`API_SPEC.md`](./API_SPEC.md#5-get-apistocksscreen) 참고.

### 처리 로직
- `StockQueryRepository`(QueryDSL, `infra/db/`)가 `StockDetail` ⋈ `Stock`을 조인하여 동적 조건(`exchangeType`, `roe`/`per` 범위)으로 필터링.
- 결과는 **시가총액 내림차순**, **페이지당 50건**으로 페이지네이션.
- 조회 시점에 키움 API를 직접 호출하지 않고, 사전에 배치로 적재된 DB 데이터를 사용한다 (실시간성 없음, 4.6 배치 갱신 참고).

### 프론트엔드 — `/analysis/company-filtering`
`CompanyFiltering.jsx`는 `<div>CompanyFiltering</div>`만 렌더링하는 placeholder이며, ROE/PER 필터 UI·결과 테이블·페이지네이션·API 훅 모두 없다.

구현 진행 상태 및 남은 작업은 [`TASK.md`](./TASK.md) 4장 참고.

---

## 4.5 조건검색 (조건조회)

### 개요
키움 HTS에서 사용자가 등록한 조건검색식 목록을 조회하여, 향후 이를 기반으로 종목을 검색하는 기능의 기초가 될 예정.

### API
`GET /api/stocks/conditions` — 상세 응답 스펙은 [`API_SPEC.md`](./API_SPEC.md#6-get-apistocksconditions) 참고.

### 현재 동작
`StockController.conditions()` → `StockService.getConditions()`는 실제 키움 조건검색 API를 호출하지 않고, 항상 `resultCode=0, items=[]`인 빈 결과를 반환하는 하드코딩 스텁이다. 프론트엔드 화면은 없다. 연동 대상 키움 API 원본 스펙([엑셀](open_api/kiwoom_REST_API_doc.xlsx)): `ka10171` 조건검색 목록조회(시트 `ka10171`), `ka10172` 조건검색 요청 일반(시트 `ka10172`), `ka10173` 조건검색 요청 실시간(시트 `ka10173`), `ka10174` 조건검색 실시간 해제(시트 `ka10174`).

구현 진행 상태 및 남은 작업은 [`TASK.md`](./TASK.md) 5장 참고.

---

## 4.6 종목 마스터/재무정보 자동 갱신

### 개요
4.4 기업 조회 / 종목 스크리닝에서 사용할 원천 데이터를 매 영업일 저녁 자동으로 수집·적재하는 스케줄 작업. 사용자가 직접 호출하는 API는 아니다.

### 트리거
`StockUpdateScheduler` — cron `0 30 15 * * MON-FRI` (평일 15:30 KST, 국내 정규장 마감 이후).

### 처리 순서
1. 키움 `ka10099`(종목정보 리스트, [원본 스펙](open_api/kiwoom_REST_API_doc.xlsx) 시트 `ka10099`) API로 **코스피 + 코스닥 전 종목** 목록을 연속조회(페이지네이션, `cont-yn`/`next-key`)로 모두 수집.
2. 수집된 각 종목에 대해 키움 `ka10001`(주식기본정보요청, [원본 스펙](open_api/kiwoom_REST_API_doc.xlsx) 시트 `ka10001`) API로 재무 상세(현재가, 시가총액, PER, ROE, PBR, EPS, BPS, 영업이익, 매출액, 거래량)를 조회. 종목 수가 많으므로(2000개 이상) **초당 5건 제한**을 준수하기 위해 `KiwoomRateLimiter.scheduleAll()`을 통해 200ms 간격으로 개별 호출을 분산 실행하며, 개별 종목 실패는 스킵하고 계속 진행한다.
3. `Stock`(종목 마스터): 이미 존재하는 종목코드는 갱신하지 않고, 신규 종목만 insert.
4. `StockDetail`(재무 스냅샷): 매 실행마다 **전부 신규 insert** — 기존 행을 삭제하거나 갱신하지 않으므로, 시간 경과에 따른 재무지표 변화가 시계열로 누적된다. 스크리닝 쿼리가 이 중 어떤 스냅샷을 사용하는지는 `StockQueryRepository` 쿼리 정의를 확인해야 한다.
5. 실행 결과는 `StockUpdateLog`에 기록: `status`(`RUNNING`→`SUCCESS`/`FAILED`), `kospiCount`, `kosdaqCount`, `totalSavedCount`, `errorMessage`(최대 1000자), `startedAt`, `completedAt`. 이 로그를 조회하는 API/화면은 없으며, DB 직접 조회로만 확인 가능하다.

### 공통 컴포넌트: `KiwoomRateLimiter`
- 키움 API 초당 5건 제한을 준수하기 위한 전용 컴포넌트 (`infra/kiwoom/`), 단일 스레드 `ScheduledExecutorService`로 200ms 간격 실행을 보장.
- `fetchAllPages`: 연속조회(페이지네이션) 순차 루프 처리.
- `scheduleAll`: N개의 독립 호출을 `i * 200ms` 오프셋으로 팬아웃 실행 (종목별 상세정보 수집에 사용), 개별 실패는 전체를 중단시키지 않고 스킵.

구현 진행 상태 및 남은 작업은 [`TASK.md`](./TASK.md) 6장 참고.

---

## 5. 공통 기술 요소

4.1~4.6 전반에 걸쳐 적용되는 공통 구현 원칙이다.

### 5.1 API 응답 포맷
모든 엔드포인트는 `ApiResponse<T> { success: boolean, data: T }` 형태로 응답한다. 예외 발생 시의 응답 형식은 표준화되어 있지 않다(전역 예외 처리기 없음). 공통 응답 포맷 상세는 [`API_SPEC.md`](./API_SPEC.md#12-공통-응답-포맷) 참고.

### 5.2 키움 OAuth 토큰 관리
- `KiwoomTokenManager`가 계좌별 OAuth 토큰을 `ConcurrentHashMap`에 캐싱.
- 만료 1분 전에 자동 갱신.
- 앱키/시크릿키는 `application.yml`이 아닌 `Account` 테이블(DB)에 계좌별로 저장된다.

### 5.3 레이트 리밋 준수
- 모든 키움 API 호출은 초당 5건 제한을 지켜야 하며, `KiwoomRateLimiter`를 통해서만 호출되어야 한다 (4.6 참고).

### 5.4 조회 이력 캐싱 (Fetch History)
- 실현손익/현금흐름 조회는 계좌·날짜 단위로 `RealizedPnlFetchHistory`/`CashFlowFetchHistory`에 조회 완료 여부를 기록하여, 동일 기간 재조회 시 키움 API를 다시 호출하지 않고 DB 데이터를 반환한다.
- 사용자가 조회 기간을 확장하면(예: 지난달 조회 후 이번엔 3개월 조회), 이미 캐싱된 지난달 데이터는 재사용하고 나머지 신규 구간만 키움에 요청한다.

### 5.5 인증/보안
- 현재 로그인/인증/인가 기능이 전혀 없다. 모든 API는 인증 없이 호출 가능하다 — 제품 차원의 전제(단일 사용자 개인 도구)는 [`PRD.md`](./PRD.md) 2.2 참고.
- 키움 앱키/시크릿키는 DB에 평문으로 저장된다.

---

## 6. 참고: 소스 위치

| 영역 | 경로 |
|---|---|
| 계좌 API | `backend/src/main/java/app/jaewook/stockmanager/api/AccountController.java` |
| 자산(실현손익/현금흐름) API | `backend/.../api/AssetController.java`, `api/dto/AssetRequest.java`, `api/dto/AssetResponse.java` |
| 종목 API | `backend/.../api/StockController.java`, `api/dto/StockRequest.java`, `api/dto/StockResponse.java` |
| 도메인 엔티티 | `backend/src/main/java/app/jaewook/stockmanager/domain/` |
| 키움 연동 | `backend/src/main/java/app/jaewook/stockmanager/infra/kiwoom/` |
| 스케줄러 | `backend/src/main/java/app/jaewook/stockmanager/schedule/` |
| 프론트 페이지 | `frontend/src/pages/` |
| 프론트 API 훅 | `frontend/src/api/` |

세부 API 요청/응답 필드 스펙은 [`API_SPEC.md`](./API_SPEC.md), 기능별 구현 진행 상태와 남은 작업은 [`TASK.md`](./TASK.md) 참고.
