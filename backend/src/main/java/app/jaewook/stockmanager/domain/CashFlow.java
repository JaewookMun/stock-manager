package app.jaewook.stockmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * 거래일자
     */
    @Column(nullable = false)
    private LocalDate tradeDate;

    /**
     * 거래번호
     */
    @Column(nullable = false)
    private String tradeNumber;

    /**
     * 적요명
     */
    private String summary;

    /**
     * 신용거래구분명
     */
    private String creditTradeTypeName;

    /**
     * 정산금액
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal settlementAmount;

    /**
     * 대출금상환
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal loanRepayment;

    /**
     * 거래금액(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal tradingAmountForeign;

    /**
     * 정산금액(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal settlementAmountForeign;

    /**
     * 예수금잔고
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal depositBalance;

    /**
     * 통화코드
     */
    private String currencyCode;

    /**
     * 거래종류구분
     */
    private String tradeTypeCode;

    /**
     * 거래종류명
     */
    private String tradeTypeName;

    /**
     * 종목명
     */
    private String stockName;

    /**
     * 거래금액
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal tradingAmount;

    /**
     * 거래및농특세
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal tradingAndAgriculturalTax;

    /**
     * 상환차금
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal repaymentDifference;

    /**
     * 거래세(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal transactionTaxForeign;

    /**
     * 연체합
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal overdueSum;

    /**
     * 외화예수금잔고
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal foreignDepositBalance;

    /**
     * 매체구분명
     */
    private String mediaTypeName;

    /**
     * 입출구분
     */
    private String inOutType;

    /**
     * 입출구분명
     */
    private String inOutTypeName;

    /**
     * 원거래번호
     */
    private String originalTradeNumber;

    /**
     * 종목코드
     */
    private String stockCode;

    /**
     * 거래수량/좌수
     */
    private String tradeQuantity;

    /**
     * 수수료
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal commission;

    /**
     * 이자/대주이용
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal interestOrBorrowingUse;

    /**
     * 수수료(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal commissionForeign;

    /**
     * 연체합(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal overdueSumForeign;

    /**
     * 유가금잔
     */
    private String securitiesBalance;

    /**
     * 처리시간
     */
    private String processTime;

    /**
     * ISIN코드
     */
    private String isinCode;

    /**
     * 거래소코드
     */
    private String exchangeCode;

    /**
     * 거래소명
     */
    private String exchangeName;

    /**
     * 거래단가/환율
     */
    private String tradePriceOrExchangeRate;

    /**
     * 소득/주민세
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal incomeTaxOrResidentTax;

    /**
     * 대출일
     */
    private String loanDate;

    /**
     * 미수(원/주)
     */
    private String receivable;

    /**
     * 변제합
     */
    private String repaymentSum;

    /**
     * 체결일
     */
    private String executionDate;

    /**
     * 출납번호
     */
    private String cashierNumber;

    /**
     * 처리자
     */
    private String processor;

    /**
     * 처리점
     */
    private String processingBranch;

    /**
     * 매매형태
     */
    private String tradeForm;

    /**
     * 과세기준가
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal taxBasePrice;

    /**
     * 세금수수료합
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal taxCommissionSum;

    /**
     * 외국납부세액(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal foreignPaidTaxForeign;

    /**
     * 미수(외)
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal receivableForeign;

    /**
     * 변제합(외)
     */
    private String repaymentSumForeign;

    /**
     * 입금자
     */
    private String depositor;

    /**
     * 거래내역구분
     */
    private String tradeHistoryType;

    @Builder
    public CashFlow(Account account, LocalDate tradeDate, String tradeNumber, String summary, String creditTradeTypeName,
                    BigDecimal settlementAmount, BigDecimal loanRepayment, BigDecimal tradingAmountForeign,
                    BigDecimal settlementAmountForeign, BigDecimal depositBalance, String currencyCode,
                    String tradeTypeCode, String tradeTypeName, String stockName, BigDecimal tradingAmount,
                    BigDecimal tradingAndAgriculturalTax, BigDecimal repaymentDifference, BigDecimal transactionTaxForeign,
                    BigDecimal overdueSum, BigDecimal foreignDepositBalance, String mediaTypeName, String inOutType,
                    String inOutTypeName, String originalTradeNumber, String stockCode, String tradeQuantity,
                    BigDecimal commission, BigDecimal interestOrBorrowingUse, BigDecimal commissionForeign,
                    BigDecimal overdueSumForeign, String securitiesBalance, String processTime, String isinCode,
                    String exchangeCode, String exchangeName, String tradePriceOrExchangeRate,
                    BigDecimal incomeTaxOrResidentTax, String loanDate, String receivable, String repaymentSum,
                    String executionDate, String cashierNumber, String processor, String processingBranch,
                    String tradeForm, BigDecimal taxBasePrice, BigDecimal taxCommissionSum,
                    BigDecimal foreignPaidTaxForeign, BigDecimal receivableForeign, String repaymentSumForeign,
                    String depositor, String tradeHistoryType) {
        this.account = account;
        this.tradeDate = tradeDate;
        this.tradeNumber = tradeNumber;
        this.summary = summary;
        this.creditTradeTypeName = creditTradeTypeName;
        this.settlementAmount = settlementAmount;
        this.loanRepayment = loanRepayment;
        this.tradingAmountForeign = tradingAmountForeign;
        this.settlementAmountForeign = settlementAmountForeign;
        this.depositBalance = depositBalance;
        this.currencyCode = currencyCode;
        this.tradeTypeCode = tradeTypeCode;
        this.tradeTypeName = tradeTypeName;
        this.stockName = stockName;
        this.tradingAmount = tradingAmount;
        this.tradingAndAgriculturalTax = tradingAndAgriculturalTax;
        this.repaymentDifference = repaymentDifference;
        this.transactionTaxForeign = transactionTaxForeign;
        this.overdueSum = overdueSum;
        this.foreignDepositBalance = foreignDepositBalance;
        this.mediaTypeName = mediaTypeName;
        this.inOutType = inOutType;
        this.inOutTypeName = inOutTypeName;
        this.originalTradeNumber = originalTradeNumber;
        this.stockCode = stockCode;
        this.tradeQuantity = tradeQuantity;
        this.commission = commission;
        this.interestOrBorrowingUse = interestOrBorrowingUse;
        this.commissionForeign = commissionForeign;
        this.overdueSumForeign = overdueSumForeign;
        this.securitiesBalance = securitiesBalance;
        this.processTime = processTime;
        this.isinCode = isinCode;
        this.exchangeCode = exchangeCode;
        this.exchangeName = exchangeName;
        this.tradePriceOrExchangeRate = tradePriceOrExchangeRate;
        this.incomeTaxOrResidentTax = incomeTaxOrResidentTax;
        this.loanDate = loanDate;
        this.receivable = receivable;
        this.repaymentSum = repaymentSum;
        this.executionDate = executionDate;
        this.cashierNumber = cashierNumber;
        this.processor = processor;
        this.processingBranch = processingBranch;
        this.tradeForm = tradeForm;
        this.taxBasePrice = taxBasePrice;
        this.taxCommissionSum = taxCommissionSum;
        this.foreignPaidTaxForeign = foreignPaidTaxForeign;
        this.receivableForeign = receivableForeign;
        this.repaymentSumForeign = repaymentSumForeign;
        this.depositor = depositor;
        this.tradeHistoryType = tradeHistoryType;
    }
}
