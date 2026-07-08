package app.jaewook.stockmanager.api.mapper;

import app.jaewook.stockmanager.api.dto.AssetRequest;
import app.jaewook.stockmanager.api.dto.AssetResponse;
import app.jaewook.stockmanager.service.dto.AssetCommand;
import app.jaewook.stockmanager.service.dto.AssetResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetControllerMapper {

    /*
     * Controller DTO -> Service DTO Mapping
     */

    public AssetCommand.RealizedPnl toRealizedPnlCommand(AssetRequest.RealizedPnl request) {
        return AssetCommand.RealizedPnl.builder()
                .accountId(request.accountId())
                .stockCode(request.stockCode())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
    }

    public AssetCommand.CashFlow toCashFlowCommand(AssetRequest.CashFlow request) {
        return AssetCommand.CashFlow.builder()
                .accountId(request.accountId())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .category(request.category())
                .stockCode(request.stockCode())
                .currencyCode(request.currencyCode())
                .productType(request.productType())
                .overseasExchangeCode(request.overseasExchangeCode())
                .domesticExchangeCode(request.domesticExchangeCode())
                .build();
    }

    /*
     * Service DTO -> Controller DTO Mapping
     */

    public AssetResponse.RealizedPnl toRealizedPnlResponse(AssetResult.RealizedPnl result) {
        List<AssetResponse.RealizedPnl.RealizedPnlItem> items = result.items().stream()
                .map(this::toRealizedPnlItem)
                .toList();

        return AssetResponse.RealizedPnl.builder()
                .items(items)
                .build();
    }

    private AssetResponse.RealizedPnl.RealizedPnlItem toRealizedPnlItem(AssetResult.RealizedPnl.RealizedPnlItemDto dto) {
        return AssetResponse.RealizedPnl.RealizedPnlItem.builder()
                .date(dto.date())
                .htsSellCommission(dto.htsSellCommission())
                .stockName(dto.stockName())
                .quantity(dto.quantity())
                .buyPrice(dto.buyPrice())
                .executionPrice(dto.executionPrice())
                .realizedPnl(dto.realizedPnl())
                .pnlRate(dto.pnlRate())
                .stockCode(dto.stockCode())
                .tradingCommission(dto.tradingCommission())
                .tradingTax(dto.tradingTax())
                .build();
    }

    public AssetResponse.CashFlow toCashFlowResponse(AssetResult.CashFlow result) {
        List<AssetResponse.CashFlow.CashFlowItem> items = result.items().stream()
                .map(this::toCashFlowItem)
                .toList();

        return AssetResponse.CashFlow.builder()
                .items(items)
                .build();
    }

    private AssetResponse.CashFlow.CashFlowItem toCashFlowItem(AssetResult.CashFlow.CashFlowItemDto dto) {
        return AssetResponse.CashFlow.CashFlowItem.builder()
                .tradeDate(dto.tradeDate())
                .tradeNumber(dto.tradeNumber())
                .summary(dto.summary())
                .creditTradeTypeName(dto.creditTradeTypeName())
                .settlementAmount(dto.settlementAmount())
                .loanRepayment(dto.loanRepayment())
                .tradingAmountForeign(dto.tradingAmountForeign())
                .settlementAmountForeign(dto.settlementAmountForeign())
                .depositBalance(dto.depositBalance())
                .currencyCode(dto.currencyCode())
                .tradeTypeCode(dto.tradeTypeCode())
                .tradeTypeName(dto.tradeTypeName())
                .stockName(dto.stockName())
                .tradingAmount(dto.tradingAmount())
                .tradingAndAgriculturalTax(dto.tradingAndAgriculturalTax())
                .repaymentDifference(dto.repaymentDifference())
                .transactionTaxForeign(dto.transactionTaxForeign())
                .overdueSum(dto.overdueSum())
                .foreignDepositBalance(dto.foreignDepositBalance())
                .mediaTypeName(dto.mediaTypeName())
                .inOutType(dto.inOutType())
                .inOutTypeName(dto.inOutTypeName())
                .originalTradeNumber(dto.originalTradeNumber())
                .stockCode(dto.stockCode())
                .tradeQuantity(dto.tradeQuantity())
                .commission(dto.commission())
                .interestOrBorrowingUse(dto.interestOrBorrowingUse())
                .commissionForeign(dto.commissionForeign())
                .overdueSumForeign(dto.overdueSumForeign())
                .securitiesBalance(dto.securitiesBalance())
                .processTime(dto.processTime())
                .isinCode(dto.isinCode())
                .exchangeCode(dto.exchangeCode())
                .exchangeName(dto.exchangeName())
                .tradePriceOrExchangeRate(dto.tradePriceOrExchangeRate())
                .incomeTaxOrResidentTax(dto.incomeTaxOrResidentTax())
                .loanDate(dto.loanDate())
                .receivable(dto.receivable())
                .repaymentSum(dto.repaymentSum())
                .executionDate(dto.executionDate())
                .cashierNumber(dto.cashierNumber())
                .processor(dto.processor())
                .processingBranch(dto.processingBranch())
                .tradeForm(dto.tradeForm())
                .taxBasePrice(dto.taxBasePrice())
                .taxCommissionSum(dto.taxCommissionSum())
                .foreignPaidTaxForeign(dto.foreignPaidTaxForeign())
                .receivableForeign(dto.receivableForeign())
                .repaymentSumForeign(dto.repaymentSumForeign())
                .depositor(dto.depositor())
                .tradeHistoryType(dto.tradeHistoryType())
                .build();
    }
}