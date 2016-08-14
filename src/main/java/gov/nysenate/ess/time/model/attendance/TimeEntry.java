package gov.nysenate.ess.time.model.attendance;

import com.google.common.base.Objects;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import gov.nysenate.ess.core.model.payroll.PayType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A TimeEntry contains all the hours worked and charged for a specific date.
 * TimeEntries are associated together via a common TimeRecord Id.
 */
public class TimeEntry extends AttendanceHours
{
    protected BigInteger entryId;
    protected BigInteger timeRecordId;
    protected int empId;
    protected String employeeName;
    protected LocalDate date;
    protected MiscLeaveType miscType;
    protected boolean active;
    protected String empComment;
    protected PayType payType;
    protected boolean accruing;
    protected String originalUserId;
    protected String updateUserId;
    protected LocalDateTime originalDate;
    protected LocalDateTime updateDate;

    /** --- Constructors --- */

    public TimeEntry() {}

    public TimeEntry(BigInteger timeRecordId, int empId) {
        this.timeRecordId = timeRecordId;
        this.empId = empId;
    }

    public TimeEntry(TimeRecord record, PayType payType, LocalDate date) {
        this.timeRecordId = record.getTimeRecordId();
        this.empId = record.getEmployeeId();
        this.employeeName = record.getLastUpdater();
        this.date = date;
        this.active = true;
        this.accruing = true;
        this.payType = payType;
        this.originalUserId = record.getOriginalUserId();
        this.updateUserId = this.originalUserId;
        this.originalDate = LocalDateTime.now();
        this.updateDate = this.originalDate;
    }

    public TimeEntry(TimeEntry other) {
        super(other);
        this.entryId = other.entryId;
        this.timeRecordId = other.timeRecordId;
        this.empId = other.empId;
        this.employeeName = other.employeeName;
        this.date = other.date;
        this.miscType = other.miscType;
        this.active = other.active;
        this.accruing = other.accruing;
        this.empComment = other.empComment;
        this.payType = other.payType;
        this.originalUserId = other.originalUserId;
        this.updateUserId = other.updateUserId;
        this.originalDate = other.originalDate;
        this.updateDate = other.updateDate;
    }


    /** --- Functional Getters/Setters --- */

    /**
     * @return true if no fields are set for this time record
     */
    public boolean isEmpty() {
        return super.isEmpty() &&
                miscType == null &&
                empComment == null;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;
        if (!super.equals(o)) return false;
        TimeEntry entry = (TimeEntry) o;
        return Objects.equal(empId, entry.empId) &&
                Objects.equal(active, entry.active) &&
                Objects.equal(entryId, entry.entryId) &&
                Objects.equal(timeRecordId, entry.timeRecordId) &&
                Objects.equal(employeeName, entry.employeeName) &&
                Objects.equal(date, entry.date) &&
                Objects.equal(miscType, entry.miscType) &&
                Objects.equal(empComment, entry.empComment) &&
                Objects.equal(payType, entry.payType) &&
                Objects.equal(originalUserId, entry.originalUserId) &&
                Objects.equal(updateUserId, entry.updateUserId) &&
                Objects.equal(originalDate, entry.originalDate) &&
                Objects.equal(updateDate, entry.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), entryId, timeRecordId, empId, employeeName, date, miscType, active,
                empComment, payType, originalUserId, updateUserId, originalDate, updateDate);
    }

    /** --- Basic Getters/Setters --- */

    public BigInteger getEntryId() {
        return entryId;
    }

    public void setEntryId(BigInteger entryId) {
        this.entryId = entryId;
    }

    public BigInteger getTimeRecordId() {
        return timeRecordId;
    }

    public void setTimeRecordId(BigInteger timeRecordId) {
        this.timeRecordId = timeRecordId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MiscLeaveType getMiscType() {
        return miscType;
    }

    public void setMiscType(MiscLeaveType miscType) {
        this.miscType = miscType;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAccruing() {
        return accruing;
    }

    public void setAccruing(boolean accruing) { this.accruing = accruing; };

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmpComment() {
        return empComment;
    }

    public void setEmpComment(String empComment) {
        this.empComment = empComment;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public String getOriginalUserId() {
        return originalUserId;
    }

    public void setOriginalUserId(String originalUserId) {
        this.originalUserId = originalUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(LocalDateTime originalDate) {
        this.originalDate = originalDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}