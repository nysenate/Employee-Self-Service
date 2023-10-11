package gov.nysenate.ess.time.model.payroll;

import com.google.common.base.Objects;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * A record indicating that personnel has granted an employee permission to use a type of misc leave
 */
// TODO: record in Java 17
public class MiscLeaveGrant {

    private final int empId;
    private final MiscLeaveType miscLeaveType;
    private final LocalDate beginDate;
    private final LocalDate endDate;
    private final BigDecimal hours;

    public MiscLeaveGrant(int empId, MiscLeaveType miscLeaveType,
                          LocalDate beginDate, LocalDate endDate, BigDecimal hours) {
        this.empId = empId;
        this.miscLeaveType = miscLeaveType;
        this.beginDate = beginDate;
        // Default to a limit of a rolling year for this type.
        if (miscLeaveType == MiscLeaveType.DONATED_MISC_LEAVE && endDate == null) {
            endDate = beginDate.plusYears(1).minusDays(1);
        }
        this.endDate = endDate;
        this.hours = hours;
    }

    /** --- Functional Getters --- */

    public Range<LocalDate> getDateRange() {
        return Range.closed(
                Optional.ofNullable(beginDate).orElse(DateUtils.LONG_AGO),
                Optional.ofNullable(endDate).orElse(DateUtils.THE_FUTURE)
        );
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MiscLeaveGrant)) return false;
        MiscLeaveGrant that = (MiscLeaveGrant) o;
        return empId() == that.empId() &&
                miscLeaveType() == that.miscLeaveType() &&
                Objects.equal(beginDate, that.beginDate) &&
                Objects.equal(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(empId, miscLeaveType, beginDate, endDate, hours);
    }

    /** --- Getters --- */

    public int empId() {
        return empId;
    }

    public MiscLeaveType miscLeaveType() {
        return miscLeaveType;
    }

    public LocalDate beginDate() {
        return beginDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public BigDecimal hours() {
        return hours;
    }
}
