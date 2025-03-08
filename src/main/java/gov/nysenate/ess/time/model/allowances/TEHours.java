package gov.nysenate.ess.time.model.allowances;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by heitner on 7/23/2014.
 */
public class TEHours {
    public final int PAID = 101, SFMSPROCESSING = 102, WAITINGTOBEPROCESSED = 103, ENTEREDBYEMPLOYEE = 104;

    protected BigDecimal TEHours;
    protected Date beginDate;
    protected Date endDate;
    protected int hourStatus;
    protected int empId;

    public BigDecimal getTEHours() {
        return TEHours;
    }

    /**
     * --- Basic Getters/Setters ---
     */

    public void setTEHours(BigDecimal TEHours) {
        this.TEHours = TEHours;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getHourStatus() {
        return hourStatus;
    }

    public void setHourStatus(int hourStatus) {
        this.hourStatus = hourStatus;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }


}
