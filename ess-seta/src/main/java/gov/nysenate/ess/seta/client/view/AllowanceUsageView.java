package gov.nysenate.ess.seta.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.seta.model.allowances.AllowanceUsage;
import gov.nysenate.ess.core.client.view.base.ListView;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@XmlRootElement
public class AllowanceUsageView implements ViewObject
{

    protected int empId;
    protected int year;
    protected BigDecimal yearlyAllowance;
    protected BigDecimal baseMoneyUsed;
    protected BigDecimal recordMoneyUsed;
    protected BigDecimal moneyUsed;

    protected ListView<SalaryRecView> salaryRecs;

    public AllowanceUsageView(AllowanceUsage allowanceUsage) {
        this.empId = allowanceUsage.getEmpId();
        this.year = allowanceUsage.getYear();
        this.yearlyAllowance = allowanceUsage.getYearlyAllowance();
        this.baseMoneyUsed = allowanceUsage.getBaseMoneyUsed();
        this.recordMoneyUsed = allowanceUsage.getRecordMoneyUsed();
        this.moneyUsed = allowanceUsage.getMoneyUsed();
        this.salaryRecs = ListView.of(allowanceUsage.getSalaryRecs().stream()
                .map(SalaryRecView::new)
                .collect(Collectors.toList()));
    }

    @Override
    public String getViewType() {
        return "allowance";
    }

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @XmlElement
    public int getYear() {
        return year;
    }

    @XmlElement
    public BigDecimal getYearlyAllowance() {
        return yearlyAllowance;
    }

    @XmlElement
    public BigDecimal getBaseMoneyUsed() {
        return baseMoneyUsed;
    }

    @XmlElement
    public BigDecimal getRecordMoneyUsed() {
        return recordMoneyUsed;
    }

    @XmlElement
    public BigDecimal getMoneyUsed() {
        return moneyUsed;
    }

    @XmlElement
    public ListView<SalaryRecView> getSalaryRecs() {
        return salaryRecs;
    }
}
