package gov.nysenate.ess.seta.model.accrual;

import java.math.BigDecimal;
import java.util.Date;

public class Hours
{
    public final int SFMSPOSTED = 101,
                     SFMSNOTPOSTED = 102,
                     TIMESHEETAPPROVED = 103,
                     TIMESHEETSUBMITTED = 104,
                     TIMESHEETENTERED = 105;

    protected BigDecimal Hours;
    protected Date beginDate;
    protected Date endDate;
    protected int hourStatus;
    protected int empId;

    /** --- Basic Getters/Setters --- */

    public void setHours(BigDecimal Hours) {
        this.Hours = Hours;
    }

    public BigDecimal getHours() {
        return Hours;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setHourStatus(int hourStatus) {
        this.hourStatus = hourStatus;
    }

    public int getHourStatus() {
        return hourStatus;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getEmpId() {
        return empId;
    }

}
