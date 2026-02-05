package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomCashFlowRequest;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomCashFlowResponse;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomRealizedPnlRequest;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomRealizedPnlResponse;
import app.jaewook.stockmanager.service.dto.AssetCommand;
import app.jaewook.stockmanager.service.dto.AssetResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 키움 API DTO <-> Service DTO 매퍼
 */
@Component
public class KiwoomApiMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // CashFlow 고정값
    private static final String CASH_FLOW_CATEGORY = "1";           // 구분: 입출금
    private static final String CASH_FLOW_PRODUCT_TYPE = "0";       // 상품구분: 전체
    private static final String CASH_FLOW_DOMESTIC_EXCHANGE = "%";  // 국내거래소구분: 전체

    public KiwoomRealizedPnlRequest toKiwoomRequest(AssetCommand.RealizedPnl command) {
        return KiwoomRealizedPnlRequest.builder()
                .startDate(command.startDate().format(DATE_FORMATTER))
                .endDate(command.endDate().format(DATE_FORMATTER))
//                .stockCode(command.stockCode())
                .build();
    }

    public AssetResult.RealizedPnl toServiceResult(KiwoomRealizedPnlResponse response) {
        if (response == null || response.output() == null) {
            return new AssetResult.RealizedPnl(Collections.emptyList());
        }

        List<AssetResult.RealizedPnl.RealizedPnlItemDto> items = response.output().stream()
                .map(this::toRealizedPnlItem)
                .toList();

        return new AssetResult.RealizedPnl(items);
    }

    public KiwoomCashFlowRequest toKiwoomRequest(AssetCommand.CashFlow command) {
        return KiwoomCashFlowRequest.builder()
                .startDate(command.startDate().format(DATE_FORMATTER))
                .endDate(command.endDate().format(DATE_FORMATTER))
                .category(CASH_FLOW_CATEGORY)
                .stockCode(command.stockCode())
                .currencyCode(command.currencyCode())
                .productType(CASH_FLOW_PRODUCT_TYPE)
                .overseasExchangeCode(command.overseasExchangeCode())
                .domesticExchangeCode(CASH_FLOW_DOMESTIC_EXCHANGE)
                .build();
    }

    public AssetResult.CashFlow toServiceResult(KiwoomCashFlowResponse response) {
        if (response == null || response.output() == null) {
            return new AssetResult.CashFlow(Collections.emptyList());
        }

        List<AssetResult.CashFlow.CashFlowItemDto> items = response.output().stream()
                .map(this::toCashFlowItem)
                .toList();

        return new AssetResult.CashFlow(items);
    }

    private AssetResult.CashFlow.CashFlowItemDto toCashFlowItem(KiwoomCashFlowResponse.CashFlowItem item) {
        return new AssetResult.CashFlow.CashFlowItemDto(
                item.tradeDate(),
                item.tradeNumber(),
                item.summary(),
                item.creditTradeTypeName(),
                parseBigDecimal(item.settlementAmount()),
                parseBigDecimal(item.loanRepayment()),
                parseBigDecimal(item.tradingAmountForeign()),
                parseBigDecimal(item.settlementAmountForeign()),
                parseBigDecimal(item.depositBalance()),
                item.currencyCode(),
                item.tradeTypeCode(),
                item.tradeTypeName(),
                item.stockName(),
                parseBigDecimal(item.tradingAmount()),
                parseBigDecimal(item.tradingAndAgriculturalTax()),
                parseBigDecimal(item.repaymentDifference()),
                parseBigDecimal(item.transactionTaxForeign()),
                parseBigDecimal(item.overdueSum()),
                parseBigDecimal(item.foreignDepositBalance()),
                item.mediaTypeName(),
                item.inOutType(),
                item.inOutTypeName(),
                item.originalTradeNumber(),
                item.stockCode(),
                item.tradeQuantity(),
                parseBigDecimal(item.commission()),
                parseBigDecimal(item.interestOrBorrowingUse()),
                parseBigDecimal(item.commissionForeign()),
                parseBigDecimal(item.overdueSumForeign()),
                item.securitiesBalance(),
                item.processTime(),
                item.isinCode(),
                item.exchangeCode(),
                item.exchangeName(),
                item.tradePriceOrExchangeRate(),
                parseBigDecimal(item.incomeTaxOrResidentTax()),
                item.loanDate(),
                item.receivable(),
                item.repaymentSum(),
                item.executionDate(),
                item.cashierNumber(),
                item.processor(),
                item.processingBranch(),
                item.tradeForm(),
                parseBigDecimal(item.taxBasePrice()),
                parseBigDecimal(item.taxCommissionSum()),
                parseBigDecimal(item.foreignPaidTaxForeign()),
                parseBigDecimal(item.receivableForeign()),
                item.repaymentSumForeign(),
                item.depositor(),
                item.tradeHistoryType()
        );
    }

    private AssetResult.RealizedPnl.RealizedPnlItemDto toRealizedPnlItem(
            KiwoomRealizedPnlResponse.RealizedPnlItem item) {
        return new AssetResult.RealizedPnl.RealizedPnlItemDto(
                parseDate(item.date()),
                item.htsSellCommission(),
                item.stockName(),
                parseInteger(item.quantity()),
                parseBigDecimal(item.buyPrice()),
                parseBigDecimal(item.executionPrice()),
                parseBigDecimal(item.realizedPnl()),
                parseBigDecimal(item.pnlRate()),
                item.stockCode(),
                parseBigDecimal(item.tradingCommission()),
                parseBigDecimal(item.tradingTax())
        );
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim());
    }

    private int parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }
}
