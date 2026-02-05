package app.jaewook.stockmanager.api;

import app.jaewook.stockmanager.api.dto.AssetRequest;
import app.jaewook.stockmanager.api.dto.AssetResponse;
import app.jaewook.stockmanager.api.mapper.AssetControllerMapper;
import app.jaewook.stockmanager.service.AssetService;
import app.jaewook.stockmanager.service.dto.AssetResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final AssetControllerMapper mapper;

    /**
     * GET /api/assets/realized-pnl
     * 실현손익 기록 조회 - 기간
     */
    @GetMapping("/realized-pnl")
    public ApiResponse<AssetResponse.RealizedPnl> realizedPnl(AssetRequest.RealizedPnl request) {
        log.info("GET /api/assets/realized-pnl - request: {}", request);
        AssetResult.RealizedPnl result = assetService.getRealizedPnl(mapper.toRealizedPnlCommand(request));
        AssetResponse.RealizedPnl response = mapper.toRealizedPnlResponse(result);

        return ApiResponse.success(response);
    }

    /**
     * GET /api/assets/cash-flow
     * 이체내역 조회 (현금흐름)
     */
    @GetMapping("/cash-flow")
    public ApiResponse<AssetResponse.CashFlow> cashFlow(AssetRequest.CashFlow request) {
        log.info("GET /api/assets/cash-flow - request: {}", request);
        AssetResult.CashFlow result = assetService.getCashFlow(mapper.toCashFlowCommand(request));
        AssetResponse.CashFlow response = mapper.toCashFlowResponse(result);

        return ApiResponse.success(response);
    }
}
