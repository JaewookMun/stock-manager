package app.jaewook.stockmanager.service.mapper;

import app.jaewook.stockmanager.domain.CashFlow;
import app.jaewook.stockmanager.service.dto.AssetResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CashFlowDbMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /*
     * Service DTO -> Entity Mapping
     */

    public CashFlow toEntity(AssetResult.CashFlow.CashFlowItemDto dto) {
        return CashFlow.builder()
                .tradeDate(parseDate(dto.tradeDate()))
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

    /*
     * Entity -> Service DTO Mapping
     */

    public AssetResult.CashFlow toServiceResult(List<CashFlow> entities) {
        List<AssetResult.CashFlow.CashFlowItemDto> items = entities.stream()
                .map(this::toItemDto)
                .toList();
        return new AssetResult.CashFlow(items);
    }

    private AssetResult.CashFlow.CashFlowItemDto toItemDto(CashFlow entity) {
        return new AssetResult.CashFlow.CashFlowItemDto(
                formatDate(entity.getTradeDate()),
                entity.getTradeNumber(),
                entity.getSummary(),
                entity.getCreditTradeTypeName(),
                entity.getSettlementAmount(),
                entity.getLoanRepayment(),
                entity.getTradingAmountForeign(),
                entity.getSettlementAmountForeign(),
                entity.getDepositBalance(),
                entity.getCurrencyCode(),
                entity.getTradeTypeCode(),
                entity.getTradeTypeName(),
                entity.getStockName(),
                entity.getTradingAmount(),
                entity.getTradingAndAgriculturalTax(),
                entity.getRepaymentDifference(),
                entity.getTransactionTaxForeign(),
                entity.getOverdueSum(),
                entity.getForeignDepositBalance(),
                entity.getMediaTypeName(),
                entity.getInOutType(),
                entity.getInOutTypeName(),
                entity.getOriginalTradeNumber(),
                entity.getStockCode(),
                entity.getTradeQuantity(),
                entity.getCommission(),
                entity.getInterestOrBorrowingUse(),
                entity.getCommissionForeign(),
                entity.getOverdueSumForeign(),
                entity.getSecuritiesBalance(),
                entity.getProcessTime(),
                entity.getIsinCode(),
                entity.getExchangeCode(),
                entity.getExchangeName(),
                entity.getTradePriceOrExchangeRate(),
                entity.getIncomeTaxOrResidentTax(),
                entity.getLoanDate(),
                entity.getReceivable(),
                entity.getRepaymentSum(),
                entity.getExecutionDate(),
                entity.getCashierNumber(),
                entity.getProcessor(),
                entity.getProcessingBranch(),
                entity.getTradeForm(),
                entity.getTaxBasePrice(),
                entity.getTaxCommissionSum(),
                entity.getForeignPaidTaxForeign(),
                entity.getReceivableForeign(),
                entity.getRepaymentSumForeign(),
                entity.getDepositor(),
                entity.getTradeHistoryType()
        );
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }
}
