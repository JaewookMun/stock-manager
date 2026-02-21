package app.jaewook.stockmanager.service;

import app.jaewook.stockmanager.infra.db.StockQueryRepository;
import app.jaewook.stockmanager.service.dto.StockCommand;
import app.jaewook.stockmanager.service.dto.StockResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockQueryRepository stockQueryRepository;

    public StockResult.Conditions getConditions() {
        return StockResult.Conditions.builder()
                .resultCode(0)
                .resultMessage(null)
                .items(List.of())
                .build();
    }

    public StockResult.Screen screenStocks(StockCommand.Screen command) {
        log.info("screenStocks - command: {}", command);

        long totalCount = stockQueryRepository.countStocks(command);
        List<StockResult.Screen.StockInfo> items = stockQueryRepository.screenStocks(command);
        boolean hasNext = (long) (command.page() + 1) * StockQueryRepository.PAGE_SIZE < totalCount;

        return StockResult.Screen.builder()
                .totalCount(totalCount)
                .items(items)
                .hasNext(hasNext)
                .build();
    }
}
