package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
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

    protected BigDecimal recordMoneyUsed;
    protected BigDecimal baseMoneyUsed;
    protected BigDecimal moneyUsed;

    protected BigDecimal baseHoursUsed;
    protected BigDecimal recordHoursUsed;
    protected BigDecimal hoursUsed;

    protected ListView<SalaryRecView> salaryRecs;

    public AllowanceUsageView(AllowanceUsage allowanceUsage) {
        this.empId = allowanceUsage.getEmpId();
        this.year = allowanceUsage.getYear();
        this.yearlyAllowance = allowanceUsage.getYearlyAllowance();
        this.baseMoneyUsed = allowanceUsage.getBaseMoneyUsed();
        this.recordMoneyUsed = allowanceUsage.getRecordMoneyUsed();
        this.moneyUsed = allowanceUsage.getMoneyUsed();
        this.baseHoursUsed = allowanceUsage.getBaseHoursUsed();
        this.recordHoursUsed = allowanceUsage.getRecordHoursUsed();
        this.hoursUsed = allowanceUsage.getHoursUsed();
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

    @XmlElement
    public BigDecimal getBaseHoursUsed() {
        return baseHoursUsed;
    }

    @XmlElement
    public BigDecimal getRecordHoursUsed() {
        return recordHoursUsed;
    }

    @XmlElement
    public BigDecimal getHoursUsed() {
        return hoursUsed;
    }
}
