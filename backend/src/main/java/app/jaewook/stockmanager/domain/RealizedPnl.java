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
public class RealizedPnl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * 일자
     */
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String stockCode;

    @Column(nullable = false)
    private String stockName;

    @Column(nullable = false)
    private int quantity;

    /**
     * 매입가
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal buyPrice;

    /**
     * 매도체결가
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal executionPrice;

    /**
     * 당일매도손익
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal realizedPnl;

    /**
     * 손익율
     */
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal pnlRate;

    /**
     * 당일 hts매도 수수료
     */
    @Column(nullable = false)
    private String htsSellCommission;

    /**
     * 당일 매매 수수료
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal tradingCommission;

    /**
     * 당일 매매 세금
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal tradingTax;

    @Builder
    public RealizedPnl(Account account, LocalDate date, String stockCode, String stockName, int quantity,
                       BigDecimal buyPrice, BigDecimal executionPrice, BigDecimal realizedPnl,
                       BigDecimal pnlRate, String htsSellCommission, BigDecimal tradingCommission,
                       BigDecimal tradingTax) {
        this.account = account;
        this.date = date;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.executionPrice = executionPrice;
        this.realizedPnl = realizedPnl;
        this.pnlRate = pnlRate;
        this.htsSellCommission = htsSellCommission;
        this.tradingCommission = tradingCommission;
        this.tradingTax = tradingTax;
    }
}
