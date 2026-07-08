package app.jaewook.stockmanager.api;

import app.jaewook.stockmanager.api.dto.ApiResponse;
import app.jaewook.stockmanager.api.dto.StockRequest;
import app.jaewook.stockmanager.api.dto.StockResponse;
import app.jaewook.stockmanager.api.mapper.StockControllerMapper;
import app.jaewook.stockmanager.service.StockService;
import app.jaewook.stockmanager.service.dto.StockCommand;
import app.jaewook.stockmanager.service.dto.StockResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final StockControllerMapper mapper;

    /**
     * GET /api/stocks/conditions
     * 검색조건 목록 조회 API
     */
    @GetMapping("/conditions")
    public ApiResponse<StockResponse.Conditions> conditions() {
        StockResult.Conditions result = stockService.getConditions();
        return ApiResponse.success(mapper.fromConditionsResult(result));
    }

    /**
     * GET /api/stocks/screen
     * 상장기업 조건 검색 API
     */
    @GetMapping("/screen")
    public ApiResponse<StockResponse.Screen> screen(StockRequest.Screen request) {
        StockCommand.Screen command = mapper.toScreenCommand(request);
        StockResult.Screen screenStocks = stockService.screenStocks(command);

        return ApiResponse.success(mapper.fromScreenResult(screenStocks));
    }
}
