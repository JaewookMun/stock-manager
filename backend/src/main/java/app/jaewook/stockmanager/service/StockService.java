package app.jaewook.stockmanager.service;

import app.jaewook.stockmanager.service.dto.StockCommand;
import app.jaewook.stockmanager.service.dto.StockResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    public StockResult.Conditions getConditions() {
        return StockResult.Conditions.builder()
                .resultCode(0)
                .resultMessage(null)
                .items(List.of())
                .build();
    }

    public StockResult.Screen screenStocks(StockCommand.Screen command) {
        return StockResult.Screen.builder()
                .totalCount(0)
                .items(Arrays.asList(
                        StockResult.Screen.StockInfo.builder()
                                // ...
                                .build()
                ))
                .hasNext(false)
                .build();
    }
}
