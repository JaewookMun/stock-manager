package app.jaewook.stockmanager.service;

import app.jaewook.stockmanager.service.dto.AssetCommand;
import app.jaewook.stockmanager.service.dto.AssetResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetService {
    public AssetResult.RealizedPnl getRealizedPnl(AssetCommand.RealizedPnl command) {
        return null;
    }

    public AssetResult.CashFlow getCashFlow(AssetCommand.CashFlow command) {
        return null;
    }
}
