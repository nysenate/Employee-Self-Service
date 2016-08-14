package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.payroll.Deduction;
import gov.nysenate.ess.time.model.payroll.Paycheck;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;

@XmlRootElement
public class PaycheckView implements ViewObject
{
    protected String payPeriod;
    protected LocalDate checkDate;
    protected BigDecimal grossIncome;
    protected BigDecimal netIncome;
    /** Map of deduction code to deduction for a paycheck. Makes for less work displaying in client. */
    protected TreeMap<String, DeductionView> deductions;
    protected BigDecimal directDepositAmount;
    protected BigDecimal checkAmount;

    public PaycheckView(Paycheck paycheck) {
        this.payPeriod = paycheck.getPayPeriod();
        this.checkDate = paycheck.getCheckDate();
        this.grossIncome = paycheck.getGrossIncome();
        this.netIncome = paycheck.getNetIncome();
        this.deductions = Optional.ofNullable(paycheck.getDeductions())
                .orElse(Collections.emptyList()).stream()
                .collect(toMap(
                        Deduction::getDescription,
                        DeductionView::new, (a, b) -> a, TreeMap::new));
        this.directDepositAmount = paycheck.getDirectDepositAmount();
        this.checkAmount = paycheck.getCheckAmount();
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "Paycheck";
    }

    @XmlElement
    public String getPayPeriod() {
        return payPeriod;
    }

    @XmlElement
    public LocalDate getCheckDate() {
        return checkDate;
    }

    @XmlElement
    public BigDecimal getGrossIncome() {
        return grossIncome;
    }

    @XmlElement
    public BigDecimal getNetIncome() {
        return netIncome;
    }

    @XmlElement
    public TreeMap<String, DeductionView> getDeductions() {
        return deductions;
    }

    @XmlElement
    public BigDecimal getDirectDepositAmount() {
        return directDepositAmount;
    }

    @XmlElement
    public BigDecimal getCheckAmount() {
        return checkAmount;
    }
}
