package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.util.DateUtils;
import org.apache.commons.lang3.ObjectUtils;

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

    protected PayType payType;
    protected boolean senator;

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

    public EmployeeSupInfo(EmployeeSupInfo other) {
        this.empId = other.empId;
        this.supId = other.supId;
        this.empLastName = other.empLastName;
        this.empFirstName = other.empFirstName;
        this.payType = other.payType;
        this.senator = other.senator;
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

    /**
     * Determines if two employee sup infos are connecting.
     * This means that they have intersecting effective date ranges,
     * and are equivalent for all non-date fields.
     * If they are overlapping, then they can be effectively {@link #merge(EmployeeSupInfo) merged}.
     * @param other {@link EmployeeSupInfo}
     * @return boolean - true if the other {@link EmployeeSupInfo} connects with this one
     */
    public boolean isConnected(EmployeeSupInfo other) {
        // Test that the effective ranges are connected
        if (this.getEffectiveDateRange().isConnected(other.getEffectiveDateRange())) {
            // Test that the non-date fields are equivalent using a modified copy of other
            EmployeeSupInfo testEsi = new EmployeeSupInfo(other);
            testEsi.startDate = this.startDate;
            testEsi.endDate = this.endDate;
            testEsi.supStartDate = this.supStartDate;
            testEsi.supEndDate = this.supEndDate;
            return this.equals(testEsi);
        }
        return false;
    }

    /**
     * Creates an {@link EmployeeSupInfo} that spans all dates covered by this {@link EmployeeSupInfo}
     * or the one provided as an argument.
     *
     * @param other {@link EmployeeSupInfo}
     * @return {@link EmployeeSupInfo}
     * @throws IllegalStateException if the provided {@link EmployeeSupInfo} does not connect with this one.
     */
    public EmployeeSupInfo merge(EmployeeSupInfo other) {
        if (!isConnected(other)) {
            throw new IllegalArgumentException("Attempt to merge non-connecting emp sup infos");
        }
        EmployeeSupInfo result = new EmployeeSupInfo(this);
        result.startDate = ObjectUtils.min(this.startDate, other.startDate);
        result.endDate = ObjectUtils.max(this.endDate, other.endDate);
        result.supStartDate = ObjectUtils.min(this.supStartDate, other.supStartDate);

        /**
         *   Take the later Supervisor End Date between the two records.
         *      * For Supervisor End Dates, nulls should be considered later
         *      * If Supervisor End Date is null, use the null value (not the future date replacing the null for comparison)
         */

        //result.supEndDate = ObjectUtils.max( Optional.of(this.supEndDate).orElse(DateUtils.THE_FUTURE) , Optional.of(other.supEndDate).orElse(DateUtils.THE_FUTURE) );

        LocalDate supEndDateCur = ObjectUtils.defaultIfNull(this.supEndDate, DateUtils.THE_FUTURE);
        LocalDate supEndDateOther = ObjectUtils.defaultIfNull(other.supEndDate, DateUtils.THE_FUTURE);

        if (supEndDateCur.isBefore(supEndDateOther)) {
            result.supEndDate = other.supEndDate;
        }
        else {
            result.supEndDate = this.supEndDate;
        }

        return result;
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeSupInfo)) return false;
        EmployeeSupInfo that = (EmployeeSupInfo) o;
        return Objects.equals(empId, that.empId) &&
                Objects.equals(supId, that.supId) &&
                Objects.equals(empFirstName, that.empFirstName) &&
                Objects.equals(empLastName, that.empLastName) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(payType, that.payType) &&
                Objects.equals(senator, that.senator) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(supStartDate, that.supStartDate) &&
                Objects.equals(supEndDate, that.supEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, supId, empFirstName, empLastName, payType, senator,
                startDate, endDate, supStartDate, supEndDate);
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

    public boolean isSenator() {
        return senator;
    }

    public void setSenator(boolean senator) {
        this.senator = senator;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }
}
