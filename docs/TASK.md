# Stock Manager — 작업 목록 (TASK)

## 0. 문서 정보

| 항목 | 내용 |
|---|---|
| 목적 | 현재 미구현/부분구현 기능과 기술 부채를 실행 가능한 작업 단위로 정리하고 진행 상태를 추적 |
| 구성 방식 | **기능(도메인) 단위**로 그룹화. 각 작업에는 우선순위 태그(`P0`~`P3`)를 병기해 그룹 내 착수 순서를 판단할 수 있게 함 |
| 관련 문서 | [`PRD.md`](./PRD.md) 6장(로드맵), [`FEATURES.md`](./FEATURES.md)(기능별 동작 방식), [`TRD.md`](./TRD.md) 12장(기술 부채) — 진행 상태·작업 범위·우선순위는 본 문서가 유일한 기준(source of truth)이다 |
| 갱신 방법 | 작업 착수 시 상태를 `🔲 대기` → `🔄 진행중`으로, 완료 시 `✅ 완료`로 변경. 체크박스(`[ ]`/`[x]`)도 함께 갱신 |
| 최종 갱신 | 2026-07-08 |

### 우선순위 태그 기준

| 태그 | 기준 |
|---|---|
| `P0` | 실사용을 가로막는 결함/공백 — 없으면 기본 운영이 어려움 |
| `P1` | 백엔드는 완비되어 있으나 프론트가 없어 기능이 반쪽인 항목 — 저비용 고효율 |
| `P2` | 신규 기능(백엔드+프론트 모두 필요) 또는 구조적 개선 |
| `P3` | 장기 과제 / 정책 결정이 선행되어야 하는 항목 |

### 기능별 진행 상태 요약

PRD 4장 요구사항 기준 현재 진행 상태 — 동작 방식 상세는 [`FEATURES.md`](./FEATURES.md) 4.1~4.6 참고.

| PRD | 기능 | 백엔드 | 프론트 | 진행 상태 | 관련 |
|---|---|---|---|---|---|
| 4.1 | 계좌 관리 | 목록 조회만 | 전용 화면 없음(셀렉트박스로만 사용) | 🟡 부분 구현 | 1장 |
| 4.2 | 실현손익 조회 | 완료 | 완료 | ✅ 완료 (stockCode 필터 UI 누락) | 2장 |
| 4.3 | 현금흐름 조회 | 완료 | 없음(placeholder) | 🟡 백엔드만 완료 | 3장 |
| 4.4 | 종목 스크리닝 | 완료 | 없음(placeholder) | 🟡 백엔드만 완료 | 4장 |
| 4.5 | 조건검색 | 스텁(항상 빈 결과) | 없음 | ⬜ 미구현 | 5장 |
| 4.6 | 종목 마스터/재무 배치 갱신 | 완료 | 해당 없음 | ✅ 완료 (실행 이력 조회 API 없음) | 6장 |
| (PRD 범위 외) | Home 대시보드 | 없음 | 없음(placeholder) | ⬜ 미구현 | 7장 |

---

## 1. 계좌 관리

관련: [`FEATURES.md`](./FEATURES.md) 4.1 · [`PRD.md`](./PRD.md) 4.1

- [ ] `P0` **계좌 등록 API·화면 추가**
  - 현황: 신규 계좌 추가 수단이 `DataInit.java`의 주석 처리된 `@PostConstruct` 코드 수정뿐. 실사용성을 저해하는 가장 근본적인 공백.
  - 범위: `POST /api/accounts` (accountNumber, alias, type, appkey, secretkey) 추가, 계좌 관리 화면(등록/수정/비활성화) 구현.
  - 수용 기준: UI에서 신규 계좌를 등록하면 코드 수정 없이 즉시 실현손익/현금흐름 조회에 사용 가능.

- [ ] `P3` **타 증권사 연동 여부 결정 및 구현**
  - 현황: `AccountType` enum이 확장 가능하게 설계되어 있으나 `KIWOOM` 외 구현 없음.
  - 선행 질문: 키움 외 증권사 연동이 로드맵에 포함되는가? ([`PRD.md`](./PRD.md) 7장)
  - 결정 후: 신규 증권사 `infra/<broker>/` 계층 추가, `Account`별 브로커 타입에 따른 클라이언트 분기 설계.

---

## 2. 실현손익 조회

관련: [`FEATURES.md`](./FEATURES.md) 4.2 — 현재 상태 ✅(완료)이나 개선 항목 존재

- [ ] `P2` **`stockCode` 필터 UI 추가**
  - 현황: 백엔드 API는 `stockCode` 파라미터를 지원하나 프론트 화면(`RealizedPnl.jsx`)에 입력 UI 없음.
  - 범위: 폼에 종목코드/종목명 입력 필드 추가.

---

## 3. 현금흐름(입출금 내역) 조회

관련: [`FEATURES.md`](./FEATURES.md) 4.3 — 현재 상태 🟡(백엔드만 완료)

- [ ] `P1` **현금흐름 조회 화면 구현**
  - 현황: `GET /api/assets/cash-flow` 백엔드 완비, `frontend/src/pages/assets/CashFlow.jsx`는 `<div>cashflow</div>` placeholder.
  - 범위:
    - `src/api/cashFlow.js` SWR 훅 작성 (`useGetCashFlow`, `RealizedPnl.jsx`의 `useGetRealizedPnl` 패턴 참고)
    - 계좌/기간 선택 폼 (실현손익 화면과 유사한 UX), 추가로 `category`(구분), `productType`(상품구분), `domesticExchangeCode` 등 필터 UI
    - 45개 필드 중 사용자에게 의미 있는 핵심 컬럼 선정 및 테이블 구현 (전체 필드 노출은 비현실적 — 상세보기/펼치기 UX 검토)
  - 수용 기준: 계좌·기간·구분을 선택해 거래 내역을 조회하고 테이블로 확인 가능.

---

## 4. 종목 스크리닝 (기업 조회)

관련: [`FEATURES.md`](./FEATURES.md) 4.4 — 현재 상태 🟡(백엔드만 완료)

- [ ] `P1` **종목 스크리닝 화면 구현**
  - 현황: `GET /api/stocks/screen` 백엔드 완비(거래소/ROE/PER 필터 + 시가총액순 페이지네이션), `CompanyFiltering.jsx`는 placeholder.
  - 범위:
    - `src/api/stocks.js` SWR 훅 작성
    - 거래소 구분, ROE 범위, PER 범위 필터 입력 폼
    - 결과 테이블(시가총액순) + 페이지네이션 UI (`hasNext`, `totalCount` 활용)
  - 수용 기준: 조건(예: KOSPI, ROE 10~20%)으로 종목을 검색하고 시가총액순 목록을 페이지 단위로 확인 가능.

---

## 5. 조건검색 (조건조회)

관련: [`FEATURES.md`](./FEATURES.md) 4.5 — 현재 상태 ⬜(백엔드도 스텁)

- [ ] `P2` **키움 조건검색 API 연동 구현**
  - 현황: `GET /api/stocks/conditions`가 항상 빈 결과를 반환하는 하드코딩 스텁 (`StockService.getConditions()`).
  - 범위: 키움 "조건검색 목록조회"(`ka10171`) API 실제 연동(세션/소켓 방식 여부 확인 필요).
  - 의존성: 키움 조건검색 API가 REST가 아닌 별도 프로토콜(WebSocket 등)일 가능성 — 착수 전 [`kiwoom_REST_API_doc.xlsx`](open_api/kiwoom_REST_API_doc.xlsx)의 `ka10171`(조건검색 목록조회), `ka10172`(조건검색 요청 일반), `ka10173`(조건검색 요청 실시간), `ka10174`(실시간 해제) 시트 원문을 먼저 확인.

- [ ] `P2` **조건검색 결과 기반 종목 조회 화면**
  - 현황: 위 API 연동 완료 후 진행 가능한 후속 작업.
  - 범위: 조건검색식 목록 선택 UI, 선택한 조건으로 종목 조회 및 결과 표시.

---

## 6. 종목 마스터/재무정보 자동 갱신 배치

관련: [`FEATURES.md`](./FEATURES.md) 4.6 — 현재 상태 ✅(완료)이나 개선 항목 존재

- [ ] `P3` **`StockDetail` 시계열 데이터 활용 기능 검토**
  - 현황: 재무지표 스냅샷이 append-only로 누적되고 있으나 이를 조회/시각화하는 기능이 없음.
  - 검토 사항: 추세 차트, 특정 시점 스냅샷 비교 등 활용 방향 결정.

- [ ] `P3` **`StockDetail` 데이터 보관 정책 수립**
  - 현황: 배치 실행마다 전 종목 상세가 신규 insert되어 테이블이 선형 증가.
  - 범위: 인덱스 전략, 오래된 스냅샷 아카이빙/삭제 주기 결정.

- [ ] `P2` **배치 실행 상태 조회 API/화면**
  - 현황: `StockUpdateLog`에 실행 이력이 쌓이지만 이를 조회하는 API/화면이 없어 DB 직접 조회에 의존.
  - 범위: `GET /api/stocks/update-logs`(가칭) 및 관리자용 배치 이력 화면.

---

## 7. Home 대시보드

현재 상태: ⬜(미구현). Home 대시보드는 PRD 4장 요구사항 범위 밖의 항목이라 [`FEATURES.md`](./FEATURES.md)에는 별도 절이 없다.

- [ ] `P2` **Home 대시보드 설계 및 구현**
  - 현황: `Home.jsx`는 `<div>Home</div>` placeholder.
  - 선행 작업: 어떤 지표를 보여줄지 결정 필요(예: 최근 실현손익 요약, 계좌별 잔고 등) — [`PRD.md`](./PRD.md) 7장 오픈 퀘스천 참고.
  - 범위: 지표 확정 후 관련 백엔드 집계 API + 프론트 대시보드 카드/차트 구현.

---

## 8. 공통/인프라 (횡단 관심사)

관련: [`TRD.md`](./TRD.md) 7~12장 — 특정 기능이 아닌 시스템 전반에 걸친 항목

### 8.1 보안/시크릿

- [ ] `P0` **시크릿 하드코딩 제거**
  - 현황: `backend/src/main/resources/application.yml`에 DB 접속 정보가 평문으로 커밋되어 있음.
  - 범위: 환경변수(`SPRING_DATASOURCE_*`) 또는 `.env`(gitignore) 전환, 이미 노출된 자격증명은 교체(rotate), git 이력 정리 여부 검토.
  - 수용 기준: 저장소에 평문 자격증명이 존재하지 않고, DB 비밀번호가 교체됨.

- [ ] `P3` **인증/인가 체계 도입 여부 결정 및 구현**
  - 선행 질문: 다중 사용자 지원이 로드맵에 포함되는가, 개인용 도구로 유지되는가? ([`PRD.md`](./PRD.md) 7장)
  - 결정 후: Spring Security 도입, 로그인 화면, 세션/토큰 관리 설계.

### 8.2 API 안정성

- [ ] `P0` **전역 예외 처리(`@ControllerAdvice`) 도입**
  - 현황: 예외 발생 시 Spring 기본 오류 응답이 그대로 노출됨. `ApiResponse` 포맷이 성공 시에만 일관적.
  - 범위: 공통 에러 응답 포맷 정의(`ApiResponse.error(code, message)` 등), 도메인 예외 클래스 설계, 4xx/5xx 매핑.
  - 수용 기준: 필수 파라미터 누락, 존재하지 않는 accountId 등 오류 상황에서 일관된 JSON 에러 응답 반환.

- [ ] `P2` **입력 검증(Bean Validation) 도입**
  - 현황: `@Valid`/`@NotNull` 등 검증 어노테이션 사용 흔적 없음.
  - 범위: `*Request` DTO에 필수값/형식 검증 추가, 위 전역 예외 처리와 함께 검증 실패 응답 표준화.

### 8.3 데이터베이스

- [ ] `P0` **DB 마이그레이션 도구 도입 (Flyway/Liquibase)**
  - 현황: `ddl-auto: none`인데 스키마 정의 스크립트가 저장소에 없어, 신규 환경 구축 시 스키마를 재현할 방법이 없음.
  - 범위: 현재 엔티티 기준 초기 마이그레이션 스크립트 작성, 이후 스키마 변경을 버전 관리.
  - 수용 기준: 빈 DB에 마이그레이션만 실행하면 애플리케이션이 정상 기동.

### 8.4 테스트/CI

- [ ] `P2` **테스트 피라미드 보완 (단위 테스트 추가)**
  - 현황: `@SpringBootTest` 통합 테스트만 존재(Mock 없음, 실 DB/실 API 필요).
  - 범위: 날짜 분할 로직(`AssetService`), 레이트리밋 스케줄링(`KiwoomRateLimiter`) 등 순수 로직 단위 테스트 우선 추가. 키움 API 모킹 계층 분리 검토.

- [ ] `P2` **CI 파이프라인 구축 (빌드+테스트 자동화)**
  - 현황: `.github/workflows` 등 CI 설정 전무.
  - 범위: 최소 `gradlew build -x test` + `npm run build`/`npm run lint` 자동 실행부터 시작. 백엔드 테스트는 실 DB+실 키움 자격증명이 필요해 CI 편입이 어려우므로 위 단위 테스트 보완과 함께 진행 검토.

### 8.5 환경설정/배포

- [ ] `P2` **환경 프로파일 분리 (dev/prod)**
  - 현황: `application.yml` 단일 파일, 프로파일 분리 없음.
  - 범위: `application-dev.yml`/`application-prod.yml` 분리, `show-sql`/로그 레벨 등 환경별 차등 적용.

- [ ] `P3` **배포 인프라 구성 (컨테이너화/CI-CD 배포/모니터링)**
  - 현황: Dockerfile, 배포 파이프라인, 모니터링/APM 전무.
  - 선행 질문: 배포 대상 환경(개인 서버/클라우드)이 정해져야 구체화 가능. ([`PRD.md`](./PRD.md) 7장)

---

## 9. 완료된 주요 작업 (참고용 이력)

git 로그 기준으로 최근 완료된 것으로 확인되는 항목 — 신규 작업과 혼동하지 않도록 기록.

- [x] 계좌 조회 API 추가 (`GET /api/accounts`) — 관련: 1장
- [x] Asset 관련 엔티티/DTO/쿼리를 `accountNumber` → `accountId` 참조로 리팩터링 — 관련: 2, 3장
- [x] 키움 레이트리밋 기능 모듈화 (`KiwoomRateLimiter`) — 관련: 8장
- [x] QueryDSL 기반 종목 스크리닝 리포지토리 및 백엔드 API — 관련: 4장
- [x] 종목 데이터 갱신용 엔티티/리포지토리/스케줄러 — 관련: 6장
- [x] 실현손익 조회 화면 (`RealizedPnl.jsx`) 전체 구현 — 관련: 2장

---

## 10. 작업 진행 시 체크리스트 (공통 가이드)

신규 기능 작업 시 다음을 함께 확인한다.

- [ ] 백엔드: `*Request`/`*Response`/`*Command`/`*Result` 네이밍 컨벤션 준수 ([`TRD.md`](./TRD.md) 3.2)
- [ ] 백엔드: 엔티티는 `@NoArgsConstructor(PROTECTED)` + `@Builder`, setter 없음 ([`TRD.md`](./TRD.md) 3.5)
- [ ] 백엔드: 키움 API 호출은 반드시 `KiwoomRateLimiter` 경유 ([`TRD.md`](./TRD.md) 3.4)
- [ ] 프론트: SWR 훅은 `src/api/`에 위치, 기존 훅 패턴(`useGetAccounts` 등) 준수
- [ ] 프론트: 신규 라우트는 `DefaultRouter.jsx` 기존 패턴 + `src/menu-items/` 등록
- [ ] 문서: 기능 완료 시 본 문서의 체크박스·진행 상태 요약 표를 갱신하고, 동작 방식이 바뀌었다면 [`FEATURES.md`](./FEATURES.md) 해당 절도 함께 갱신
