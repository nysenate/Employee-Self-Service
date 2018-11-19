package gov.nysenate.ess.time.model.attendance;

import com.google.common.collect.Range;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

public class AttendanceRecord extends AttendanceHours {

    protected Integer employeeId;
    protected boolean active;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected Year year;
    protected String payPeriodNum;
    protected LocalDateTime postDate;
    protected LocalDateTime createdDate;
    protected LocalDateTime updatedDate;
    protected String transactionNote;
    protected LinkedHashSet<BigInteger> timesheetIds;
    protected Integer expectedDays;

    /** --- Functional Getters / Setters --- */

    public boolean isPaperTimesheet() {
        return timesheetIds.isEmpty();
    }

    public Optional<LocalDateTime> getPostDate() {
        return Optional.ofNullable(postDate);
    }

    public boolean isPosted() {
        return getPostDate().isPresent();
    }

    public Range<LocalDate> getDateRange() {
        return Range.closedOpen(beginDate, endDate.plusDays(1));
    }

    public void setTimesheetIds(Collection<BigInteger> timesheetIds) {
        this.timesheetIds = new LinkedHashSet<>(timesheetIds);
    }

    /**
     * Get a subset of given time records that cover this attendance record, iff there is a subset that fully covers it.
     *
     * If none of the given time records are listed on this attendance record,
     * or the given time records do not contain all time records listed on this attendance record,
     * return an empty list.
     */
    public List<TimeRecord> getTimeRecordCoverage(Collection<TimeRecord> timeRecords) {
        Set<BigInteger> presentTimesheetIds = new HashSet<>();
        List<TimeRecord> validTimeRecords = new ArrayList<>();
        for (TimeRecord twreck : timeRecords) {
            if (timesheetIds.contains(twreck.getTimeRecordId())) {
                validTimeRecords.add(twreck);
                presentTimesheetIds.add(twreck.getTimeRecordId());
            }
        }
        if (!presentTimesheetIds.equals(timesheetIds)) {
            validTimeRecords = Collections.emptyList();
        }
        return validTimeRecords;
    }

    /** --- Getters / Setters --- */

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        this.transactionNote = transactionNote;
    }

    public LinkedHashSet<BigInteger> getTimesheetIds() {
        return timesheetIds;
    }

    public Integer getExpectedDays() {
        return expectedDays;
    }

    public void setExpectedDays(Integer expectedDays) {
        this.expectedDays = expectedDays;
    }

    public String getPayPeriodNum() {
        return payPeriodNum;
    }

    public void setPayPeriodNum(String payPeriodNum) {
        this.payPeriodNum = payPeriodNum;
    }
}
