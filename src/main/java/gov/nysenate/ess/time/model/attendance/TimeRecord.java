package gov.nysenate.ess.time.model.attendance;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A Time Record is the biweekly collection of daily time entries. The time record
 * is typically created in accordance with the attendance pay periods.
 */
public class TimeRecord implements Comparable<TimeRecord>
{
    protected BigInteger timeRecordId;
    protected Integer employeeId;
    protected Integer supervisorId;
    protected String lastUser;
    protected String respHeadCode;
    protected boolean active;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected PayPeriod payPeriod;
    protected String remarks;
    protected String exceptionDetails;
    protected LocalDate processedDate;
    protected TimeRecordStatus recordStatus;
    protected String originalUserId;
    protected String updateUserId;
    protected LocalDateTime createdDate;
    protected LocalDateTime updateDate;
    protected Integer approvalEmpId;
    protected TreeMap<LocalDate, TimeEntry> timeEntryMap = new TreeMap<>();

    /** --- Constructors --- */

    public TimeRecord() {}

    public TimeRecord(Employee employee, Range<LocalDate> dateRange, PayPeriod payPeriod) {
        setEmpInfo(employee);
        this.active = true;
        this.beginDate = DateUtils.startOfDateRange(dateRange);
        this.endDate = DateUtils.endOfDateRange(dateRange);
        this.payPeriod = payPeriod;
        this.recordStatus = TimeRecordStatus.NOT_SUBMITTED;
        this.originalUserId = this.lastUser;
        this.updateUserId = this.lastUser;
        this.createdDate = LocalDateTime.now();
        this.updateDate = createdDate;
    }

    public TimeRecord(TimeRecord other) {
        this.timeRecordId = other.timeRecordId;
        this.employeeId = other.employeeId;
        this.supervisorId = other.supervisorId;
        this.lastUser = other.lastUser;
        this.respHeadCode = other.respHeadCode;
        this.active = other.active;
        this.beginDate = other.beginDate;
        this.endDate = other.endDate;
        this.payPeriod = other.payPeriod;
        this.remarks = other.remarks;
        this.exceptionDetails = other.exceptionDetails;
        this.processedDate = other.processedDate;
        this.recordStatus = other.recordStatus;
        this.originalUserId = other.originalUserId;
        this.updateUserId = other.updateUserId;
        this.createdDate = other.createdDate;
        this.updateDate = other.updateDate;
        other.getTimeEntries().stream()
                .map(TimeEntry::new)
                .forEach(this::addTimeEntry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeRecord)) return false;
        TimeRecord that = (TimeRecord) o;
        return active == that.active &&
                Objects.equal(timeRecordId, that.timeRecordId) &&
                Objects.equal(employeeId, that.employeeId) &&
                Objects.equal(supervisorId, that.supervisorId) &&
                Objects.equal(lastUser, that.lastUser) &&
                Objects.equal(respHeadCode, that.respHeadCode) &&
                Objects.equal(beginDate, that.beginDate) &&
                Objects.equal(endDate, that.endDate) &&
                Objects.equal(payPeriod, that.payPeriod) &&
                Objects.equal(remarks, that.remarks) &&
                Objects.equal(exceptionDetails, that.exceptionDetails) &&
                Objects.equal(processedDate, that.processedDate) &&
                recordStatus == that.recordStatus &&
                Objects.equal(originalUserId, that.originalUserId) &&
                Objects.equal(updateUserId, that.updateUserId) &&
                Objects.equal(createdDate, that.createdDate) &&
                Objects.equal(updateDate, that.updateDate) &&
                Objects.equal(timeEntryMap, that.timeEntryMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timeRecordId, employeeId, supervisorId, lastUser, respHeadCode, active, beginDate,
                endDate, payPeriod, remarks, exceptionDetails, processedDate, recordStatus, originalUserId,
                updateUserId, createdDate, updateDate, timeEntryMap);
    }

    @Override
    public int compareTo(TimeRecord o) {
        return ComparisonChain.start()
                .compare(this.beginDate, o.beginDate)
                .compare(this.endDate, o.endDate)
                .compare(this.employeeId, o.employeeId)
                .result();
    }

    /** --- Functions --- */

    public boolean encloses(PayPeriod period) {
        return getDateRange().encloses(period.getDateRange());
    }

    /**
     * Return true iff the employee info in this time record matches the given employee info
     */
    public boolean checkEmployeeInfo(Employee empInfo) {
        return Objects.equal(this.employeeId, empInfo.getEmployeeId()) &&
                Objects.equal(this.supervisorId, empInfo.getSupervisorId()) &&
                Objects.equal(this.respHeadCode,
                        empInfo.getRespCenter() != null && empInfo.getRespCenter().getHead() != null
                                ? empInfo.getRespCenter().getHead().getCode() : null);
    }


    /** --- Functional Getters / Setters --- */

    public Range<LocalDate> getDateRange() {
        return Range.closedOpen(beginDate, endDate.plusDays(1));
    }

    public void setDateRange(Range<LocalDate> dateRange) {
        this.beginDate = DateUtils.startOfDateRange(dateRange);
        this.endDate = DateUtils.endOfDateRange(dateRange);
    }

    public ImmutableList<TimeEntry> getTimeEntries() {
        return ImmutableList.copyOf(timeEntryMap.values());
    }

    public void addTimeEntry(TimeEntry entry) {
        this.timeEntryMap.put(entry.getDate(), entry);
    }

    public void addTimeEntries(Collection<TimeEntry> timeEntries) {
        timeEntries.forEach(this::addTimeEntry);
    }

    public TimeEntry removeEntry(LocalDate date) {
        return timeEntryMap.remove(date);
    }

    public boolean containsEntry(LocalDate date) {
        return timeEntryMap.containsKey(date);
    }

    public TimeEntry getEntry(LocalDate date) {
        return timeEntryMap.get(date);
    }

    public void setEmpInfo(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.supervisorId = employee.getSupervisorId();
        this.lastUser = employee.getUid() != null ? employee.getUid().toUpperCase() : null;
        this.respHeadCode = employee.getRespCenter().getHead().getCode();
    }

    /**
     * Constructs and returns a PeriodAccUsage by summing the values from the time entries.
     * @return PeriodAccUsage
     */
    public PeriodAccUsage getPeriodAccUsage() {
        PeriodAccUsage usage = new PeriodAccUsage();
        usage.setEmpId(employeeId);
        usage.setPayPeriod(payPeriod);
        usage.setYear(payPeriod.getEndDate().getYear());
        usage.setWorkHours(getSumOfTimeEntries(TimeEntry::getWorkHours));
        usage.setEmpHoursUsed(getSumOfTimeEntries(TimeEntry::getSickEmpHours));
        usage.setFamHoursUsed(getSumOfTimeEntries(TimeEntry::getSickFamHours));
        usage.setHolHoursUsed(getSumOfTimeEntries(TimeEntry::getHolidayHours));
        usage.setMiscHoursUsed(getSumOfTimeEntries(TimeEntry::getMiscHours));
        usage.setPerHoursUsed(getSumOfTimeEntries(TimeEntry::getPersonalHours));
        usage.setTravelHoursUsed(getSumOfTimeEntries(TimeEntry::getTravelHours));
        usage.setVacHoursUsed(getSumOfTimeEntries(TimeEntry::getVacationHours));
        return usage;
    }

    public BigDecimal getSumOfTimeEntries(Function<? super TimeEntry, Optional<BigDecimal>> mapper) {
        return timeEntryMap.values().stream()
                .map(entry -> mapper.apply(entry).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Sets time record id for all entries as well as for the time record
     */
    public void setTimeRecordId(BigInteger timeRecordId) {
        this.timeRecordId = timeRecordId;
        timeEntryMap.forEach(((date, entry) -> entry.setTimeRecordId(timeRecordId)));
    }

    /** Return true if this record contains no entered data */
    public boolean isEmpty() {
        return recordStatus == TimeRecordStatus.NOT_SUBMITTED &&
                remarks == null &&
                exceptionDetails == null &&
                timeEntryMap.values().stream().allMatch(TimeEntry::isEmpty);
    }

    /**
     * Extract the time record scope from the time record status
     */
    public TimeRecordScope getScope() {
        return Optional.ofNullable(recordStatus)
                .map(TimeRecordStatus::getScope)
                .orElse(null);
    }

    /**
     * Get the greatest update between the record and all of its entries
     * @return LocalDateTime
     */
    public LocalDateTime getOverallUpdateDate() {
        return timeEntryMap.values().stream()
                .map(TimeEntry::getUpdateDate)
                .filter(i -> i != null)
                .filter(this.updateDate::isBefore)
                .max(LocalDateTime::compareTo)
                .orElse(this.updateDate);
    }

    /**
     * Set all update user fields for this time record and its entries to the given user id
     * @param updateUser String - user id
     */
    public void setOverallUpdateUser(String updateUser) {
        this.setLastUser(updateUser);
        this.setUpdateUserId(updateUser);
        this.getTimeEntries().forEach(e -> {
            e.setUpdateUserId(updateUser);
            e.setEmployeeName(updateUser);
        });
    }

    /**
     * Gets a set of all pay types represented in this time record
     *
     * @return {@link Set<PayType>}
     */
    public Set<PayType> getPayTypes() {
        return timeEntryMap.values().stream()
                .map(TimeEntry::getPayType)
                .collect(Collectors.toSet());
    }

    /**
     * Get the pay period number.
     *
     * @return String
     */
    public String getPayPeriodNum() {
        return Optional.ofNullable(payPeriod)
                .map(PayPeriod::getPayPeriodNum)
                .orElse(null);
    }

    /** --- Basic Getters/Setters --- */

    public BigInteger getTimeRecordId() {
        return timeRecordId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Integer supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getLastUser() {
        return lastUser;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
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

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(String exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }

    public TimeRecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(TimeRecordStatus recordStatus) {
        this.recordStatus = recordStatus;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getRespHeadCode() {
        return respHeadCode;
    }

    public void setRespHeadCode(String respHeadCode) {
        this.respHeadCode = respHeadCode;
    }

    public void setApprovalEmpId(Integer approvalEmpId) { this.approvalEmpId = approvalEmpId; }

    public Integer getApprovalEmpId() { return approvalEmpId; };
}