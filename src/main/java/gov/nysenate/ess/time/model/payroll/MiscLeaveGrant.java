package gov.nysenate.ess.time.model.payroll;

import com.google.common.base.Objects;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A record indicating that personnel has granted an employee permission to use a type of misc leave
 */
public class MiscLeaveGrant {

    private int empId;
    private MiscLeaveType miscLeaveType;
    private LocalDate beginDate;
    private LocalDate endDate;

    public MiscLeaveGrant(int empId, MiscLeaveType miscLeaveType, LocalDate beginDate, LocalDate endDate) {
        this.empId = empId;
        this.miscLeaveType = miscLeaveType;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    /** --- Functional Getters --- */

    public Range<LocalDate> getDateRange() {
        return Range.closedOpen(
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
        return getEmpId() == that.getEmpId() &&
                getMiscLeaveType() == that.getMiscLeaveType() &&
                Objects.equal(getBeginDate(), that.getBeginDate()) &&
                Objects.equal(getEndDate(), that.getEndDate());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEmpId(), getMiscLeaveType(), getBeginDate(), getEndDate());
    }

    /** --- Getters --- */

    public int getEmpId() {
        return empId;
    }

    public MiscLeaveType getMiscLeaveType() {
        return miscLeaveType;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
