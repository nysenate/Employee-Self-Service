package gov.nysenate.ess.web.model.attendance;

import com.google.common.collect.Range;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public class AttendanceRecord extends AttendanceHours {

    protected Integer employeeId;
    protected boolean active;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected Year year;
    protected LocalDateTime postDate;
    protected LocalDateTime createdDate;
    protected LocalDateTime updatedDate;
    protected String transactionNote;
    protected List<BigInteger> timesheetIds;
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

    public List<BigInteger> getTimesheetIds() {
        return timesheetIds;
    }

    public void setTimesheetIds(List<BigInteger> timesheetIds) {
        this.timesheetIds = timesheetIds;
    }

    public Integer getExpectedDays() {
        return expectedDays;
    }

    public void setExpectedDays(Integer expectedDays) {
        this.expectedDays = expectedDays;
    }
}
