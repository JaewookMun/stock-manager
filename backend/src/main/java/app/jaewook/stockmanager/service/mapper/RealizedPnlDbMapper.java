package app.jaewook.stockmanager.service.mapper;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.domain.RealizedPnl;
import app.jaewook.stockmanager.service.dto.AssetResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RealizedPnlDbMapper {

    /*
     * Service DTO -> Entity Mapping
     */

    public RealizedPnl toEntity(AssetResult.RealizedPnl.RealizedPnlItemDto dto, Account account) {
        return RealizedPnl.builder()
                .account(account)
                .date(dto.date())
                .stockCode(dto.stockCode())
                .stockName(dto.stockName())
                .quantity(dto.quantity())
                .buyPrice(dto.buyPrice())
                .executionPrice(dto.executionPrice())
                .realizedPnl(dto.realizedPnl())
                .pnlRate(dto.pnlRate())
                .htsSellCommission(dto.htsSellCommission())
                .tradingCommission(dto.tradingCommission())
                .tradingTax(dto.tradingTax())
                .build();
    }

    /*
     * Entity -> Service DTO Mapping
     */

    public AssetResult.RealizedPnl toServiceResult(List<RealizedPnl> entities) {
        List<AssetResult.RealizedPnl.RealizedPnlItemDto> items = entities.stream()
                .map(this::toItemDto)
                .toList();
        return new AssetResult.RealizedPnl(items);
    }

    private AssetResult.RealizedPnl.RealizedPnlItemDto toItemDto(RealizedPnl item) {
        return new AssetResult.RealizedPnl.RealizedPnlItemDto(
                item.getDate(),
                item.getHtsSellCommission(),
                item.getStockName(),
                item.getQuantity(),
                item.getBuyPrice(),
                item.getExecutionPrice(),
                item.getRealizedPnl(),
                item.getPnlRate(),
                item.getStockCode(),
                item.getTradingCommission(),
                item.getTradingTax()
        );
    }
}
