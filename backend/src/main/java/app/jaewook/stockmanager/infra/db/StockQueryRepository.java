package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.QStock;
import app.jaewook.stockmanager.domain.QStockDetail;
import app.jaewook.stockmanager.service.dto.StockCommand;
import app.jaewook.stockmanager.service.dto.StockResult;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockQueryRepository {

    public static final int PAGE_SIZE = 50;

    private final JPAQueryFactory queryFactory;

    private static final QStock stock = QStock.stock;
    private static final QStockDetail stockDetail = QStockDetail.stockDetail;

    public long countStocks(StockCommand.Screen command) {
        Long count = queryFactory
                .select(stockDetail.count())
                .from(stockDetail)
                .join(stockDetail.stock, stock)
                .where(
                        exchangeTypeEq(command.exchangeType()),
                        roeGoe(command.minRoe()),
                        roeLoe(command.maxRoe()),
                        perGoe(command.minPer()),
                        perLoe(command.maxPer())
                )
                .fetchOne();
        return count != null ? count : 0L;
    }

    public List<StockResult.Screen.StockInfo> screenStocks(StockCommand.Screen command) {
        return queryFactory
                .select(Projections.constructor(StockResult.Screen.StockInfo.class,
                        stock.marketType,
                        stock.code,
                        stock.name,
                        stockDetail.currentPrice,
                        stockDetail.marketCap,
                        stockDetail.tradingVolume.coalesce(0L),
                        stockDetail.roe,
                        stockDetail.per,
                        stockDetail.pbr,
                        stockDetail.eps,
                        stockDetail.bps,
                        stockDetail.operatingProfit,
                        stockDetail.salesAmount
                ))
                .from(stockDetail)
                .join(stockDetail.stock, stock)
                .where(
                        exchangeTypeEq(command.exchangeType()),
                        roeGoe(command.minRoe()),
                        roeLoe(command.maxRoe()),
                        perGoe(command.minPer()),
                        perLoe(command.maxPer())
                )
                .orderBy(stockDetail.marketCap.desc())
                .offset((long) command.page() * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .fetch();
    }

    private BooleanExpression exchangeTypeEq(String exchangeType) {
        return (exchangeType != null && !exchangeType.isBlank()) ? stock.marketType.eq(exchangeType) : null;
    }

    private BooleanExpression roeGoe(BigDecimal minRoe) {
        return minRoe != null ? stockDetail.roe.goe(minRoe) : null;
    }

    private BooleanExpression roeLoe(BigDecimal maxRoe) {
        return maxRoe != null ? stockDetail.roe.loe(maxRoe) : null;
    }

    private BooleanExpression perGoe(BigDecimal minPer) {
        return minPer != null ? stockDetail.per.goe(minPer) : null;
    }

    private BooleanExpression perLoe(BigDecimal maxPer) {
        return maxPer != null ? stockDetail.per.loe(maxPer) : null;
    }
}
