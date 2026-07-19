# Stock Manager — API 명세서 (API SPEC)

## 0. 문서 정보

| 항목 | 내용 |
|---|---|
| 목적 | 백엔드가 제공하는 REST API의 요청/응답 스펙을 엔드포인트 단위로 상세 기술 |
| 대상 독자 | 프론트엔드 개발자, API를 직접 호출/테스트하는 개발자 |
| 관련 문서 | [`FEATURES.md`](./FEATURES.md) — 각 기능의 배경, 처리 로직(캐싱/분할조회 등), 화면 동작은 그쪽을 참고. 본 문서는 "무엇을 요청하면 무엇이 오는가"에만 집중 |
| 작성 기준 | 소스 코드(`api/dto/*Request.java`, `api/dto/*Response.java`, `*Controller.java`) 및 `backend/README.md` 실사 |
| 최종 갱신 | 2026-07-08 |

---

## 1. 공통 사항

### 1.1 Base URL
- 로컬 개발: `http://localhost:8080`
- 프론트엔드 dev 서버(`localhost:5173`)는 `/api/*` 요청을 위 주소로 프록시한다.

### 1.2 공통 응답 포맷

모든 엔드포인트는 다음 포맷으로 래핑되어 응답한다.

```json
{
  "success": true,
  "data": { }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `success` | boolean | 요청 성공 여부 |
| `data` | object | 엔드포인트별 응답 payload |

**주의**: 현재 전역 예외 처리(`@ControllerAdvice`)가 구현되어 있지 않다. 예외 발생 시 위 포맷을 따르지 않는 Spring 기본 오류 응답(HTML 또는 기본 에러 JSON)이 반환될 수 있다. 실패 응답 포맷은 아직 표준화되어 있지 않다.

### 1.3 인증
없음. 모든 엔드포인트는 인증 없이 호출 가능하다.

### 1.4 엔드포인트 목록

| Method | Path | 설명 | 상태 |
|---|---|---|---|
| GET | [`/api/accounts`](#2-get-apiaccounts) | 계좌 목록 조회 | ✅ |
| GET | [`/api/assets/realized-pnl`](#3-get-apiassetsrealized-pnl) | 실현손익 조회 | ✅ |
| GET | [`/api/assets/cash-flow`](#4-get-apiassetscash-flow) | 현금흐름(거래내역) 조회 | ✅ |
| GET | [`/api/stocks/screen`](#5-get-apistocksscreen) | 종목 스크리닝 | ✅ |
| GET | [`/api/stocks/conditions`](#6-get-apistocksconditions) | 조건검색식 목록 조회 | 🟡 스텁 (항상 빈 결과) |

---

## 2. `GET /api/accounts`

계좌 목록을 조회한다.

### 요청
파라미터 없음.

### 응답

```json
{
  "success": true,
  "data": {
    "accounts": [
      { "id": 1, "accountNumber": "12345678-01", "alias": "위탁", "type": "KIWOOM" }
    ]
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `accounts[].id` | Long | 계좌 식별자 (다른 API의 `accountId` 파라미터로 사용) |
| `accounts[].accountNumber` | String | 계좌번호 |
| `accounts[].alias` | String | 계좌 별칭 (예: "위탁") |
| `accounts[].type` | Enum | 계좌 유형. 현재 `KIWOOM`만 존재 |

키움 앱키/시크릿키(`appkey`, `secretkey`)는 이 응답에 포함되지 않는다.

---

## 3. `GET /api/assets/realized-pnl`

기간별 실현손익 내역을 조회한다. (원천: 키움 `ka10073` 일자별종목별실현손익요청-기간)

### 요청 파라미터

| 파라미터 | 타입 | 필수 | Length | 설명 |
|---|---|---|---|---|
| `accountId` | Long | Y | - | 계좌 식별자 |
| `stockCode` | String | N | 6 | 특정 종목만 필터링 |
| `startDate` | LocalDate (`YYYY-MM-DD`) | Y | 8 | 조회 시작일 |
| `endDate` | LocalDate (`YYYY-MM-DD`) | Y | 8 | 조회 종료일 |

### 응답

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "date": "2026-06-01",
        "htsSellCommission": "...",
        "stockName": "삼성전자",
        "quantity": 10,
        "buyPrice": 70000,
        "executionPrice": 75000,
        "realizedPnl": 48000,
        "pnlRate": 6.5,
        "stockCode": "005930",
        "tradingCommission": 120,
        "tradingTax": 90
      }
    ]
  }
}
```

| JSON 필드 | 타입 | 원본(키움 한글명) | 설명 |
|---|---|---|---|
| `items[].date` | LocalDate | 일자 | 거래(매도 체결)일 |
| `items[].htsSellCommission` | String | 당일hts매도수수료 | |
| `items[].stockName` | String | 종목명 | |
| `items[].quantity` | Integer | 체결량 | |
| `items[].buyPrice` | BigDecimal | 매입단가 | |
| `items[].executionPrice` | BigDecimal | 체결가 | 매도 체결가 |
| `items[].realizedPnl` | BigDecimal | 당일매도손익 | |
| `items[].pnlRate` | BigDecimal | 손익율 | 단위 % |
| `items[].stockCode` | String | 종목코드 | |
| `items[].tradingCommission` | BigDecimal | 당일매매수수료 | |
| `items[].tradingTax` | BigDecimal | 당일매매세금 | |

### 참고
- 처리 로직(캐싱, 3개월 단위 자동 분할, 1년 이내 제약)은 [`FEATURES.md`](./FEATURES.md) 4.2 참고.

---

## 4. `GET /api/assets/cash-flow`

계좌의 거래 원장(입출금/매매/대출/환전 등)을 조회한다. (원천: 키움 `kt00015` 위탁종합거래내역요청)

### 요청 파라미터

| 파라미터 | 타입 | 필수 | Length | 설명 |
|---|---|---|---|---|
| `accountId` | Long | Y | - | 계좌 식별자 |
| `startDate` | LocalDate | Y | 8 | 조회 시작일 |
| `endDate` | LocalDate | Y | 8 | 조회 종료일 |
| `category` | String | Y | 1 | 구분: `0`전체, `1`입출금, `2`입출고, `3`매매, `4`매수, `5`매도, `6`입금, `7`출금, `A`예탁담보대출입금, `B`매도담보대출입금, `C`현금상환(융자,담보상환), `F`환전, `M`입출금+환전, `G`외화매수, `H`외화매도, `I`환전정산입금, `J`환전정산출금 |
| `stockCode` | String | N | 12 | 종목코드 |
| `currencyCode` | String | N | 3 | 통화코드 |
| `productType` | String | Y | 1 | 상품구분: `0`전체, `1`국내주식, `2`수익증권, `3`해외주식, `4`금융상품 |
| `overseasExchangeCode` | String | N | 10 | 해외거래소코드 |
| `domesticExchangeCode` | String | Y | 6 | 국내거래소구분: `%`전체, `KRX`한국거래소, `NXT`넥스트트레이드 |

### 응답

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "tradeDate": "20260601",
        "tradeNumber": "000000001",
        "summary": "주식매수",
        "creditTradeTypeName": "-",
        "settlementAmount": 750000,
        "stockCode": "005930",
        "stockName": "삼성전자"
      }
    ]
  }
}
```

`items[]`의 전체 필드 (JSON 필드명 / 타입 / 원본 키움 한글명):

| JSON 필드 | 타입 | 원본(키움 한글명) |
|---|---|---|
| `tradeDate` | String | 거래일자 |
| `tradeNumber` | String | 거래번호 |
| `summary` | String | 적요명 |
| `creditTradeTypeName` | String | 신용거래구분명 |
| `settlementAmount` | BigDecimal | 정산금액 |
| `loanRepayment` | BigDecimal | 대출금상환 |
| `tradingAmountForeign` | BigDecimal | 거래금액(외) |
| `settlementAmountForeign` | BigDecimal | 정산금액(외) |
| `depositBalance` | BigDecimal | 예수금잔고 |
| `currencyCode` | String | 통화코드 |
| `tradeTypeCode` | String | 거래종류구분 (`1`입출금, `2`펀드, `3`ELS, `4`채권, `5`해외채권, `6`외화RP, `7`외화발행어음) |
| `tradeTypeName` | String | 거래종류명 |
| `stockName` | String | 종목명 |
| `tradingAmount` | BigDecimal | 거래금액 |
| `tradingAndAgriculturalTax` | BigDecimal | 거래및농특세 |
| `repaymentDifference` | BigDecimal | 상환차금 |
| `transactionTaxForeign` | BigDecimal | 거래세(외) |
| `overdueSum` | BigDecimal | 연체합 |
| `foreignDepositBalance` | BigDecimal | 외화예수금잔고 |
| `mediaTypeName` | String | 매체구분명 |
| `inOutType` | String | 입출구분 |
| `inOutTypeName` | String | 입출구분명 |
| `originalTradeNumber` | String | 원거래번호 |
| `stockCode` | String | 종목코드 |
| `tradeQuantity` | String | 거래수량/좌수 |
| `commission` | BigDecimal | 수수료 |
| `interestOrBorrowingUse` | BigDecimal | 이자/대주이용 |
| `commissionForeign` | BigDecimal | 수수료(외) |
| `overdueSumForeign` | BigDecimal | 연체합(외) |
| `securitiesBalance` | String | 유가금잔 |
| `processTime` | String | 처리시간 |
| `isinCode` | String | ISIN코드 |
| `exchangeCode` | String | 거래소코드 |
| `exchangeName` | String | 거래소명 |
| `tradePriceOrExchangeRate` | String | 거래단가/환율 |
| `incomeTaxOrResidentTax` | BigDecimal | 소득/주민세 |
| `loanDate` | String | 대출일 |
| `receivable` | String | 미수(원/주) |
| `repaymentSum` | String | 변제합 |
| `executionDate` | String | 체결일 |
| `cashierNumber` | String | 출납번호 |
| `processor` | String | 처리자 |
| `processingBranch` | String | 처리점 |
| `tradeForm` | String | 매매형태 |
| `taxBasePrice` | BigDecimal | 과세기준가 |
| `taxCommissionSum` | BigDecimal | 세금수수료합 |
| `foreignPaidTaxForeign` | BigDecimal | 외국납부세액(외) |
| `receivableForeign` | BigDecimal | 미수(외) |
| `repaymentSumForeign` | String | 변제합(외) |
| `depositor` | String | 입금자 |
| `tradeHistoryType` | String | 거래내역구분 |

(총 45개 필드. 대부분 국내/외화 페어로 구성되어 해외 거래에도 대응한다.)

### 참고
- 처리 로직(캐싱, 12개월 단위 자동 분할)은 [`FEATURES.md`](./FEATURES.md) 4.3 참고.

---

## 5. `GET /api/stocks/screen`

ROE/PER/거래소 조건으로 상장 종목을 스크리닝한다. 사전에 배치로 적재된 DB 데이터를 대상으로 하며, 실시간 키움 API 호출은 발생하지 않는다.

### 요청 파라미터

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `exchangeType` | String | N | 거래소 구분 (코스피/코스닥 등) |
| `minRoe` | BigDecimal | N | ROE 최소값 |
| `maxRoe` | BigDecimal | N | ROE 최대값 |
| `minPer` | BigDecimal | N | PER 최소값 |
| `maxPer` | BigDecimal | N | PER 최대값 |
| `page` | int | N | 페이지 번호 (페이지당 50건) |

### 응답

```json
{
  "success": true,
  "data": {
    "totalCount": 1234,
    "hasNext": true,
    "items": [
      {
        "exchangeType": "KOSPI",
        "code": "005930",
        "name": "삼성전자",
        "price": 75000,
        "marketCap": 4500000000000,
        "totalQuantity": 5969782550,
        "roe": 12.3,
        "per": 15.2,
        "pbr": 1.8,
        "eps": 5000,
        "bps": 42000,
        "operatingProfit": 30000000000000,
        "salesAmount": 280000000000000
      }
    ]
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `totalCount` | long | 조건에 맞는 전체 종목 수 |
| `hasNext` | boolean | 다음 페이지 존재 여부 |
| `items[].exchangeType` | String | 거래소 구분 |
| `items[].code` | String | 종목코드 |
| `items[].name` | String | 종목명 |
| `items[].price` | BigDecimal | 현재가 |
| `items[].marketCap` | BigDecimal | 시가총액 (정렬 기준, 내림차순) |
| `items[].totalQuantity` | long | 상장주식수 등 수량 |
| `items[].roe` | BigDecimal | ROE |
| `items[].per` | BigDecimal | PER |
| `items[].pbr` | BigDecimal | PBR |
| `items[].eps` | BigDecimal | EPS |
| `items[].bps` | BigDecimal | BPS |
| `items[].operatingProfit` | BigDecimal | 영업이익 |
| `items[].salesAmount` | BigDecimal | 매출액 |

### 참고
- 데이터 원천(배치 갱신 로직)은 [`FEATURES.md`](./FEATURES.md) 4.6 참고.

---

## 6. `GET /api/stocks/conditions`

키움 HTS에 등록된 조건검색식 목록을 조회한다. (원천: 키움 조건검색 목록조회 API)

> **주의**: 현재 `StockService.getConditions()`는 실제 키움 API를 호출하지 않고 항상 아래와 같은 빈 결과를 반환하는 하드코딩 스텁이다. 응답 스펙은 정의되어 있으나 데이터는 채워지지 않는다.

### 요청
파라미터(request body) 없음.

### 응답

```json
{
  "success": true,
  "data": {
    "resultCode": 0,
    "resultMessage": null,
    "items": []
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `resultCode` | int | 정상 `0` |
| `resultMessage` | String | 정상인 경우 없음(null) |
| `items[].conditionSeq` | String | 조건검색식 일련번호 |
| `items[].conditionName` | String | 조건검색식 명 |

### 참고
- 미구현 상세 및 후속 작업은 [`FEATURES.md`](./FEATURES.md) 4.5, [`TASK.md`](./TASK.md) 5장 참고.

---

## 7. 참고: 소스 위치

| 영역 | 경로 |
|---|---|
| 계좌 API | `backend/src/main/java/app/jaewook/stockmanager/api/AccountController.java`, `api/dto/AccountResponse.java` |
| 자산(실현손익/현금흐름) API | `backend/.../api/AssetController.java`, `api/dto/AssetRequest.java`, `api/dto/AssetResponse.java` |
| 종목 API | `backend/.../api/StockController.java`, `api/dto/StockRequest.java`, `api/dto/StockResponse.java` |
