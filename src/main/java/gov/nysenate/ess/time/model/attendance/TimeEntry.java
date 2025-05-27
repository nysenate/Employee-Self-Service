package gov.nysenate.ess.time.model.attendance;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
    protected MiscLeaveType miscType2;
    protected boolean active;
    protected String empComment;
    protected PayType payType;
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
        this.employeeName = record.getLastUser();
        this.date = date;
        this.active = true;
        this.payType = payType;
        this.originalUserId = record.getOriginalUserId();
        this.updateUserId = this.originalUserId;
//        this.originalDate = LocalDateTime.now();
//        this.updateDate = this.originalDate;
    }

    public TimeEntry(TimeEntry other) {
        super(other);
        this.entryId = other.entryId;
        this.timeRecordId = other.timeRecordId;
        this.empId = other.empId;
        this.employeeName = other.employeeName;
        this.date = other.date;
        this.miscType = other.miscType;
        this.miscType2 = other.miscType2;
        this.active = other.active;
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
                miscType2 == null &&
                empComment == null;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;
        if (!super.equals(o)) return false;
        TimeEntry timeEntry = (TimeEntry) o;
        return empId == timeEntry.empId &&
                active == timeEntry.active &&
                Objects.equal(entryId, timeEntry.entryId) &&
                Objects.equal(timeRecordId, timeEntry.timeRecordId) &&
                Objects.equal(employeeName, timeEntry.employeeName) &&
                Objects.equal(date, timeEntry.date) &&
                miscType == timeEntry.miscType &&
                miscType2 == timeEntry.miscType2 &&
                Objects.equal(empComment, timeEntry.empComment) &&
                payType == timeEntry.payType &&
                Objects.equal(originalUserId, timeEntry.originalUserId) &&
                Objects.equal(updateUserId, timeEntry.updateUserId) &&
                Objects.equal(originalDate, timeEntry.originalDate) &&
                Objects.equal(updateDate, timeEntry.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), entryId, timeRecordId, empId,
                employeeName, date, miscType, miscType2, active, empComment, payType,
                originalUserId, updateUserId, originalDate, updateDate);
    }

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder(String.valueOf(date));
        if (isEmpty()) {
            sbuilder.append(" - empty");
        } else {
            ImmutableMap<String, Optional> hourMap = ImmutableMap.<String, Optional>builder()
                    .put("work", getWorkHours())
                    .put("hol", getHolidayHours())
                    .put("vac", getVacationHours())
                    .put("pers", getPersonalHours())
                    .put("sickEmp", getSickEmpHours())
                    .put("sickFam", getSickFamHours())
                    .put("misc", getMiscHours())
                    .put("misc2", getMisc2Hours())
                    .build();
            hourMap.forEach((label, valueOpt) -> {
                if (valueOpt.isPresent()) {
                    sbuilder.append(" ")
                            .append(label)
                            .append(":")
                            .append(valueOpt.get());
                }
            });
        }
        return sbuilder.toString();
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

    public MiscLeaveType getMiscType2() {
        return miscType2;
    }

    public void setMiscType2(MiscLeaveType miscType2) {
        this.miscType2 = miscType2;
    }

    public boolean isActive() {
        return active;
    }

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