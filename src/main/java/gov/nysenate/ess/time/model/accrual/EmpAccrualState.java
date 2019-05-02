package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.core.model.payroll.PayType;

import java.math.BigDecimal;

/**
 * An immutable representation of an employee's state in relation to computing accruals.
 */
public class EmpAccrualState
{
    protected int payPeriodCount;
    protected boolean employeeAccruing;
    protected PayType payType;
    protected BigDecimal minTotalHours;
    protected BigDecimal minHoursToEnd;

    public EmpAccrualState(int payPeriodCount, boolean employeeAccruing, PayType payType, BigDecimal minTotalHours,
                           BigDecimal minHoursToEnd) {
        this.payPeriodCount = payPeriodCount;
        this.employeeAccruing = employeeAccruing;
        this.payType = payType;
        this.minTotalHours = minTotalHours;
        this.minHoursToEnd = minHoursToEnd;
    }

    public EmpAccrualState(AccrualState accrualState) {
        if (accrualState != null) {
            this.payPeriodCount = accrualState.payPeriodCount;
            this.employeeAccruing = accrualState.empAccruing;
            this.payType = accrualState.payType;
            this.minTotalHours = accrualState.minTotalHours;
            this.minHoursToEnd = accrualState.minHoursToEnd;
        }
    }

    public int getPayPeriodCount() {
        return payPeriodCount;
    }

    public boolean isEmployeeAccruing() {
        return employeeAccruing;
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
