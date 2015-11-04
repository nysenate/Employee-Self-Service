package gov.nysenate.ess.seta.model.accrual;

import gov.nysenate.ess.seta.model.accrual.AccrualState;
import gov.nysenate.ess.web.model.payroll.PayType;

import java.math.BigDecimal;

/**
 * An immutable representation of an employee's state in relation to computing accruals.
 */
public class EmpAccrualState
{
    protected int payPeriodCount;
    protected boolean employeeActive;
    protected PayType payType;
    protected BigDecimal minTotalHours;
    protected BigDecimal minHoursToEnd;

    public EmpAccrualState(int payPeriodCount, boolean employeeActive, PayType payType, BigDecimal minTotalHours,
                           BigDecimal minHoursToEnd) {
        this.payPeriodCount = payPeriodCount;
        this.employeeActive = employeeActive;
        this.payType = payType;
        this.minTotalHours = minTotalHours;
        this.minHoursToEnd = minHoursToEnd;
    }

    public EmpAccrualState(AccrualState accrualState) {
        if (accrualState != null) {
            this.payPeriodCount = accrualState.payPeriodCount;
            this.employeeActive = accrualState.employeeActive;
            this.payType = accrualState.payType;
            this.minTotalHours = accrualState.minTotalHours;
            this.minHoursToEnd = accrualState.minHoursToEnd;
        }
    }

    public int getPayPeriodCount() {
        return payPeriodCount;
    }

    public boolean isEmployeeActive() {
        return employeeActive;
    }

    public PayType getPayType() {
        return payType;
    }

    public BigDecimal getMinTotalHours() {
        return minTotalHours;
    }

    public BigDecimal getMinHoursToEnd() {
        return minHoursToEnd;
    }
}
