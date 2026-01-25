package app.jaewook.stockmanager.api.mapper;

import app.jaewook.stockmanager.api.dto.StockRequest;
import app.jaewook.stockmanager.api.dto.StockResponse;
import app.jaewook.stockmanager.service.dto.StockCommand;
import app.jaewook.stockmanager.service.dto.StockResult;
import org.springframework.stereotype.Component;

@Component
public class StockControllerMapper {

    /*
     * Controller DTO -> Service DTO Mapping
     */

    public StockCommand.Screen toScreenCommand(StockRequest.Screen request) {
        return StockCommand.Screen.builder()
                .exchangeType(request.exchangeType())
                .build();
    }

    /*
     * Service DTO -> Controller DTO Mapping
     */

    public StockResponse.Conditions fromConditionsResult(StockResult.Conditions result) {
        return StockResponse.Conditions.builder()
                .resultCode(result.resultCode())
                .resultMessage(result.resultMessage())
                .items(result.items().stream()
                        .map(this::toConditionItem)
                        .toList())
                .build();
    }

    private StockResponse.Conditions.ConditionItem toConditionItem(StockResult.Conditions.ConditionInfo info) {
        return StockResponse.Conditions.ConditionItem.builder()
                .conditionSeq(info.conditionSeq())
                .conditionName(info.conditionName())
                .build();
    }

    public StockResponse.Screen fromScreenResult(StockResult.Screen result) {
        return StockResponse.Screen.builder()
                .totalCount(result.totalCount())
                .items(result.items().stream()
                        .map(this::toStockDto)
                        .toList())
                .hasNext(result.hasNext())
                .build();
    }

    private StockResponse.Screen.StockDto toStockDto(StockResult.Screen.StockInfo dto) {
        return StockResponse.Screen.StockDto.builder()
                .exchangeType(dto.exchangeType())
                .code(dto.code())
                .name(dto.name())
                .price(dto.price())
                .totalQuantity(dto.totalQuantity())
                .roe(dto.roe())
                .per(dto.per())
                .pbr(dto.pbr())
                .debtRatio(dto.debtRatio())
                .operatingMargin(dto.operatingMargin())
                .dividend(dto.dividend())
                .build();
    }
}
