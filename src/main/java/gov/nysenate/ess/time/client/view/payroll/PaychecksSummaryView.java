package gov.nysenate.ess.time.client.view.payroll;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.payroll.PaychecksSummary;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaychecksSummaryView implements ViewObject {

    private List<PaycheckView> paychecks;
    private List<DeductionView> deductions;
    private Map<Integer, BigDecimal> deductionTotals;
    private BigDecimal grossIncomeTotal;
    private BigDecimal netIncomeTotal;
    private BigDecimal directDepositTotal;
    private BigDecimal checkAmountTotal;

    public PaychecksSummaryView(PaychecksSummary summary) {
        this.paychecks = summary.paychecks().stream()
                .map(PaycheckView::new)
                .collect(Collectors.toList());
        this.deductions = summary.deductions().stream()
                .map(DeductionView::new)
                .collect(Collectors.toList());
        this.deductionTotals = summary.deductionTotals();
        this.grossIncomeTotal = summary.grossIncomeTotal();
        this.netIncomeTotal = summary.netIncomeTotal();
        this.directDepositTotal = summary.directDepositTotal();
        this.checkAmountTotal = summary.checkAmountTotal();
    }

    public List<PaycheckView> getPaychecks() {
        return paychecks;
    }

    public List<DeductionView> getDeductions() {
        return deductions;
    }

    public Map<Integer, BigDecimal> getDeductionTotals() {
        return deductionTotals;
    }

    public BigDecimal getGrossIncomeTotal() {
        return grossIncomeTotal;
    }

    public BigDecimal getNetIncomeTotal() {
        return netIncomeTotal;
    }

    public BigDecimal getDirectDepositTotal() {
        return directDepositTotal;
    }

    public BigDecimal getCheckAmountTotal() {
        return checkAmountTotal;
    }

    @Override
    public String getViewType() {
        return "Paychecks-summary";
    }
}
