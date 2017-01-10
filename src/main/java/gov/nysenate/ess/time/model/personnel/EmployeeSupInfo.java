package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Associates a supervisor to an employee during a specific time period.
 */
public class EmployeeSupInfo
{
    protected int empId;
    protected int supId;
    protected String empLastName;
    // The requested supervisor date range when this instance was created
    // TODO rename these fields
    protected LocalDate startDate;
    protected LocalDate endDate;
    // The date range when this person was under the specified supervisor
    protected LocalDate supStartDate;
    protected LocalDate supEndDate;

    /** --- Constructors --- */

    public EmployeeSupInfo() {}

    public EmployeeSupInfo(int empId, int supId, LocalDate startDate, LocalDate endDate) {
        this.empId = empId;
        this.supId = supId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** --- Functional Getters/Setters --- */

    public Range<LocalDate> getEffectiveDateRange() {
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

    /** --- Overrides --- */

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
}
