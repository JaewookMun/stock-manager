package app.jaewook.stockmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal marketCap;

    @Column(precision = 10, scale = 4)
    private BigDecimal per;

    @Column(precision = 10, scale = 4)
    private BigDecimal roe;

    @Column(precision = 10, scale = 4)
    private BigDecimal pbr;

    @Column(precision = 19, scale = 4)
    private BigDecimal eps;

    @Column(precision = 19, scale = 4)
    private BigDecimal bps;

    @Column(precision = 19, scale = 4)
    private BigDecimal operatingProfit;

    @Column(precision = 19, scale = 4)
    private BigDecimal salesAmount;

    private Long tradingVolume;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public StockDetail(Stock stock, BigDecimal currentPrice, BigDecimal marketCap,
                       BigDecimal per, BigDecimal roe, BigDecimal pbr,
                       BigDecimal eps, BigDecimal bps,
                       BigDecimal operatingProfit, BigDecimal salesAmount,
                       Long tradingVolume, LocalDateTime updatedAt) {
        this.stock = stock;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.per = per;
        this.roe = roe;
        this.pbr = pbr;
        this.eps = eps;
        this.bps = bps;
        this.operatingProfit = operatingProfit;
        this.salesAmount = salesAmount;
        this.tradingVolume = tradingVolume;
        this.updatedAt = updatedAt;
    }

}
