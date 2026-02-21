# 프로젝트 개요
효율적인 주식거래를 위한 정보를 제공

## Querydsl
- 빌드 시 ./gradlew compileJava 를 실행하면 build/generated/querydsl 아래에 Q 클래스가 생성

## 주요 기능
- 상장기업 조회
  - 조건조회 - ROE, PER
- 자산관리
  - 실현손익
  - 현금흐름

### 상장기업 조회

**조건조회**
- 원하는 조건조회 기능이 API로 제공되지 않음
- '종목정보 리스트' API와 '주식 기본정보 요청' API를 활용하여 종목별 정보를 받아 DB에 저장한 뒤 검색.

### 자산관리

**실현손익**
- kiwoom '일자별종목별실현손익요청_일자', '일자별종목별실현손익요청_기간' API 활용
- 내부 API SPEC
  - GET /api/assets/realized-pnl
    - 요청:
    
      |  한글명  |  type   | Required  |  Length  | Description |
      |:-----:|:-------:|:---------:|:--------:|:-----------:|
      | 종목코드  | String  |     N     |    6     |      -      |
      | 시작일자  | String  |     Y     |    8     |  YYYYMMDD   |
      | 종료일자  | String  |     Y     |    8     |  YYYYMMDD   |
  
    - 응답
    
      | 한글명           |  type  |  Required  |  Length  | Description |
      |:--------------|:------:|:----------:|:--------:|:-----------:|
      | 일자별종목별실현손익    |  List  |     N      |    -     |      -      |	
      | - 일자          | String |     N      |    20    |      -      |
      | - 당일hts매도수수료  | String |     N      |    20    |      -      |
      | - 종목명         | String |     N      |    40    |      -      |
      | - 체결량         | String |     N      |    20    |      -      |
      | - 매입단가        | String |     N      |    20    |      -      |
      | - 체결가         | String |     N      |    20    |      -      |
      | - 당일매도손익      | String |     N      |    20    |      -      |
      | - 손익율         | String |     N      |    20    |      -      |
      | - 종목코드        | String |     N      |    20    |      -      |
      | - 당일매매수수료     | String |     N      |    20    |      -      |
      | - 당일매매세금      | String |     N      |    20    |      -      |


**현금흐름**
- kiwoom '위탁종합거래내역요청' API 활용
- 내부 API SPEC
  - GET /api/assets/cash-flow
  - 요청
    
    |   한글명   |   type   | Required | Length | Description                                                                                                                   |
    |:-------:|:--------:|:--------:|:------:|:------------------------------------------------------------------------------------------------------------------------------|
    |  시작일자   |  String  |    Y     |   8    | -                                                                                                                             |
    |  종료일자   |  String  |    Y     |   8    | -                                                                                                                             |
    |   구분    |  String  |    Y     |   1    | 0:전체,1:입출금,2:입출고,3:매매,4:매수,5:매도,6:입금,7:출금,A:예탁담보대출입금,B:매도담보대출입금,C:현금상환(융자,담보상환),F:환전,M:입출금+환전,G:외화매수,H:외화매도,I:환전정산입금,J:환전정산출금 |
    |  종목코드   |  String  |    N     |   12   | -                                                                                                                             |
    |  통화코드   |  String  |    N     |   3    | -                                                                                                                             |
    |  상품구분   |  String  |    Y     |   1    | 0:전체, 1:국내주식, 2:수익증권, 3:해외주식, 4:금융상품                                                                                          |
    | 해외거래소코드 |  String  |    N     |   10   | -                                                                                                                             |
    | 국내거래소구분 |  String  |    Y     |   6    | %:(전체),KRX:한국거래소,NXT:넥스트트레이드                                                                                                  |

  - 응답

    |     한글명     |  type  | Required | Length | Description                                        |
    |:-----------:|:------:|:--------:|:------:|:---------------------------------------------------|
    | 위탁종합거래내역배열  |  List  |    N     |        | -                                                  |
    |    거래일자     | String |    N     |   8    | -                                                  |
    |    거래번호     | String |    N     |   9    | -                                                  |
    |     적요명     | String |    N     |   60   | -                                                  |
    |   신용거래구분명   | String |    N     |   20   | -                                                  |
    |    정산금액     | String |    N     |   15   | -                                                  |
    |    대출금상환    | String |    N     |   15   | -                                                  |
    |   거래금액(외)   | String |    N     |   15   | -                                                  |
    |   정산금액(외)   | String |    N     |   15   | -                                                  |
    |    예수금잔고    | String |    N     |   15   | -                                                  |
    |    통화코드     | String |    N     |   3    | -                                                  |
    |   거래종류구분    | String |    N     |   2    | 1:입출금, 2:펀드, 3:ELS, 4:채권, 5:해외채권, 6:외화RP, 7:외화발행어음 |
    |    거래종류명    | String |    N     |   20   | -                                                  |
    |     종목명     | String |    N     |   40   | -                                                  |
    |    거래금액     | String |    N     |   15   | -                                                  |
    |   거래및농특세    | String |    N     |   15   | -                                                  |
    |    상환차금     | String |    N     |   15   | -                                                  |
    |   거래세(외)    | String |    N     |   15   | -                                                  |
    |     연체합     | String |    N     |   15   | -                                                  |
    |   외화예수금잔고   | String |    N     |   15   | -                                                  |
    |    매체구분명    | String |    N     |   20   | -                                                  |
    |    입출구분     | String |    N     |   1    | -                                                  |
    |    입출구분명    | String |    N     |   10   | -                                                  |
    |    원거래번호    | String |    N     |   9    | -                                                  |
    |    종목코드     | String |    N     |   12   | -                                                  |
    |   거래수량/좌수   | String |    N     |   30   | -                                                  |
    |     수수료     | String |    N     |   15   | -                                                  |
    |   이자/대주이용   | String |    N     |   15   | -                                                  |
    |   수수료(외)    | String |    N     |   15   | -                                                  |
    |   연체합(외)    | String |    N     |   15   | -                                                  |
    |    유가금잔     | String |    N     |   30   | -                                                  |
    |    처리시간     | String |    N     |  111   | -                                                  |
    |   ISIN코드    | String |    N     |   12   | -                                                  |
    |    거래소코드    | String |    N     |   10   | -                                                  |
    |    거래소명     | String |    N     |   20   | -                                                  |
    |   거래단가/환율   | String |    N     |   20   | -                                                  |
    |   소득/주민세    | String |    N     |   15   | -                                                  |
    |     대출일     | String |    N     |   8    | -                                                  |
    |   미수(원/주)   | String |    N     |   30   | -                                                  |
    |     변제합     | String |    N     |   30   | -                                                  |
    |     체결일     | String |    N     |   8    | -                                                  |
    |    출납번호     | String |    N     |   20   | -                                                  |
    |     처리자     | String |    N     |   20   | -                                                  |
    |     처리점     | String |    N     |   20   | -                                                  |
    |    매매형태     | String |    N     |   40   | -                                                  |
    |    과세기준가    | String |    N     |   15   | -                                                  |
    |   세금수수료합    | String |    N     |   15   | -                                                  |
    |  외국납부세액(외)  | String |    N     |   15   | -                                                  |
    |    미수(외)    | String |    N     |   15   | -                                                  |
    |   변제합(외)    | String |    N     |   30   | -                                                  |
    |     입금자     | String |    N     |   20   | -                                                  |
    |   거래내역구분    | String |    N     |   2    | -                                                  |



### 상장기업 조회

**조건조회**
- kiwoom '조건검색 목록조회' API 활용 - 검색조건 목록 확인
- 내부 API SPEC
  - GET /api/stocks/conditions
  - 요청: request body 없음.
  - 응답
    
    |    한글명     |  type  | Required | Length |  Description   |
    |:----------:|:------:|:--------:|:------:|:--------------:|
    |    결과코드    |  int   |    N     |   -    |     정상 : 0     |
    |   결과메시지    | String |    N     |   -    | 정상인 경우는 메시지 없음 |
    |  조건검색식 목록  |  LIST  |    N     |   -    |       -        |
    | 조건검색식 일련번호 | String |    N     |   -    |       -        |
    |  조건검색식 명   | String |    N     |   -    |       -        |
    
    
