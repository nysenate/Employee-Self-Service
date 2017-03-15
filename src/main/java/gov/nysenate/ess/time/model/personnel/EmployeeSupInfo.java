package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Associates a supervisor to an employee during a specific time period.
 */
public class EmployeeSupInfo
{
    protected int empId;
    protected int supId;
    protected String empLastName;
    protected String empFirstName;

    // The requested supervisor date range when this instance was created
    // Forms a closed-open date range [supStartDate, supEndDate)
    // TODO rename these fields
    protected LocalDate startDate = DateUtils.LONG_AGO;
    protected LocalDate endDate = DateUtils.THE_FUTURE;
    // The date range when this person was under the specified supervisor
    // Forms a closed-open date range [supStartDate, supEndDate)
    protected LocalDate supStartDate;
    protected LocalDate supEndDate;

    /** --- Constructors --- */

    public EmployeeSupInfo() {}

    public EmployeeSupInfo(int empId, int supId) {
        this.empId = empId;
        this.supId = supId;
    }

    private EmployeeSupInfo(EmployeeSupInfo other) {
        this.empId = other.empId;
        this.supId = other.supId;
        this.empLastName = other.empLastName;
        this.empFirstName = other.empFirstName;
        this.startDate = other.startDate;
        this.endDate = other.endDate;
        this.supStartDate = other.supStartDate;
        this.supEndDate = other.supEndDate;
    }

    /* --- Methods --- */

    public EmployeeSupInfo restrictDates(Range<LocalDate> dateRange) {
        EmployeeSupInfo result = new EmployeeSupInfo(this);

        LocalDate newStartDate = DateUtils.startOfDateRange(dateRange);
        LocalDate newEndDate = DateUtils.endOfDateRange(dateRange).plusDays(1);

        if (newStartDate.isAfter(getStartDate())) {
            result.startDate = newStartDate;
        }
        if (newEndDate.isBefore(getEndDate())) {
            result.endDate = newEndDate;
        }

        return result;
    }

    /* --- Functional Getters/Setters --- */

    /**
     * Get the date range from the intersection of the supervisor date range
     * and the restriction date range.
     * The restriction date range will generally enclose the supervisor date range,
     * unless this {@link EmployeeSupInfo} is for an override
     * @return {@link Range<LocalDate>}
     */
    public Range<LocalDate> getEffectiveDateRange() {
        Range<LocalDate> supDateRange = getSupDateRange();
        Range<LocalDate> restrictDateRange = Range.closedOpen(getStartDate(), getEndDate());
        if (supDateRange.isConnected(restrictDateRange)) {
            return supDateRange.intersection(restrictDateRange);
        }
        // I wish that Range.empty() existed
        return Range.closedOpen(DateUtils.LONG_AGO, DateUtils.LONG_AGO);
    }

    public LocalDate getEndDate() {
        return Optional.ofNullable(endDate).orElse(DateUtils.THE_FUTURE);
    }

    public LocalDate getStartDate() {
        return Optional.ofNullable(startDate).orElse(DateUtils.LONG_AGO);
    }

    private Range<LocalDate> getSupDateRange() {
        if (supStartDate == null && supEndDate == null) {
            return Range.all();
        }
        if (supStartDate == null) {
            return Range.lessThan(supEndDate);
        }
        if (supEndDate == null) {
            return Range.atLeast(supStartDate);
        }
        return Range.closedOpen(supStartDate, supEndDate);
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeSupInfo)) return false;
        EmployeeSupInfo that = (EmployeeSupInfo) o;
        return Objects.equals(empId, that.empId) &&
                Objects.equals(supId, that.supId) &&
                Objects.equals(empLastName, that.empLastName) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(supStartDate, that.supStartDate) &&
                Objects.equals(supEndDate, that.supEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, supId, empLastName, startDate, endDate, supStartDate, supEndDate);
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getSupId() {
        return supId;
    }

    public void setSupId(int supId) {
        this.supId = supId;
    }

    public String getEmpLastName() {
        return empLastName;
    }

    public void setEmpLastName(String empLastName) {
        this.empLastName = empLastName;
    }

    public LocalDate getSupStartDate() {
        return supStartDate;
    }

    public void setSupStartDate(LocalDate supStartDate) {
        this.supStartDate = supStartDate;
    }

    public LocalDate getSupEndDate() {
        return supEndDate;
    }

    public void setSupEndDate(LocalDate supEndDate) {
        this.supEndDate = supEndDate;
    }

    public String getEmpFirstName() {
        return empFirstName;
    }

    public void setEmpFirstName(String empFirstName) {
        this.empFirstName = empFirstName;
    }
}
