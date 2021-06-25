package gov.nysenate.ess.time.client.view.payroll;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.client.view.payroll.DeductionView;
import gov.nysenate.ess.time.model.payroll.Deduction;
import gov.nysenate.ess.time.model.payroll.Paycheck;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@XmlRootElement
public class PaycheckView implements ViewObject
{
    protected String payPeriod;
    protected LocalDate checkDate;
    protected BigDecimal grossIncome;
    protected BigDecimal netIncome;
    protected List<DeductionView> deductions;
    protected BigDecimal directDepositAmount;
    protected BigDecimal checkAmount;

    public PaycheckView(Paycheck paycheck) {
        this.payPeriod = paycheck.getPayPeriod();
        this.checkDate = paycheck.getCheckDate();
        this.grossIncome = paycheck.getGrossIncome();
        this.netIncome = paycheck.getNetIncome();
        this.deductions = paycheck.getDeductions().stream()
                .map(DeductionView::new)
                .collect(Collectors.toList());
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
    public List<DeductionView> getDeductions() {
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
