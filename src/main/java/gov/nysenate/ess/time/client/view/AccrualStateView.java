package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.accrual.EmpAccrualState;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "AccrualState")
public class AccrualStateView implements ViewObject
{
    protected String payType;
    protected boolean employeeActive;
    protected int payPeriodCount;
    protected BigDecimal minTotalHours;
    protected BigDecimal minHoursToEnd;

    public AccrualStateView(EmpAccrualState accrualState) {
        if (accrualState != null) {
            this.payType = (accrualState.getPayType() != null) ? accrualState.getPayType().name() : null;
            this.employeeActive = accrualState.isEmployeeActive();
            this.payPeriodCount = accrualState.getPayPeriodCount();
            this.minTotalHours = accrualState.getMinTotalHours();
            this.minHoursToEnd = accrualState.getMinHoursToEnd();
        }
    }

    @XmlElement
    public String getPayType() {
        return payType;
    }

    @XmlElement
    public boolean isEmployeeActive() {
        return employeeActive;
    }

    @XmlElement
    public int getPayPeriodCount() {
        return payPeriodCount;
    }

    @XmlElement
    public BigDecimal getMinTotalHours() {
        return minTotalHours;
    }

    @XmlElement
    public BigDecimal getMinHoursToEnd() {
        return minHoursToEnd;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "accrual-state";
    }
}
