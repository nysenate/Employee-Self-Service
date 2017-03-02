package gov.nysenate.ess.time.model.accrual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Helper class to store accrual usage numbers.
 */
public class AccrualUsage
{

    private static final Logger logger = LoggerFactory.getLogger(AccrualUsage.class);

    private int empId;
    private BigDecimal workHours = BigDecimal.ZERO;
    private BigDecimal travelHoursUsed = BigDecimal.ZERO;
    private BigDecimal vacHoursUsed = BigDecimal.ZERO;
    private BigDecimal perHoursUsed = BigDecimal.ZERO;
    private BigDecimal empHoursUsed = BigDecimal.ZERO;
    private BigDecimal famHoursUsed = BigDecimal.ZERO;
    private BigDecimal holHoursUsed = BigDecimal.ZERO;
    private BigDecimal miscHoursUsed = BigDecimal.ZERO;

    public AccrualUsage() {}

    public AccrualUsage(AccrualUsage lhs) {
        if (lhs != null) {
            this.empId = lhs.empId;
            this.workHours = lhs.workHours;
            this.travelHoursUsed = lhs.travelHoursUsed;
            this.vacHoursUsed = lhs.vacHoursUsed;
            this.perHoursUsed = lhs.perHoursUsed;
            this.empHoursUsed = lhs.empHoursUsed;
            this.famHoursUsed = lhs.famHoursUsed;
            this.holHoursUsed = lhs.holHoursUsed;
            this.miscHoursUsed = lhs.miscHoursUsed;
        }
    }

    /* --- Public Methods --- */

    /**
     * Adds the hours of one accrual usage to this usage. The usages must be for the same employee.
     * @param usage AccrualUsage
     */
    public void addUsage(AccrualUsage usage) {
        if (this.empId != usage.empId) {
            throw new IllegalArgumentException("You cannot addUsage accrual usages from two different employees");
        }
        this.workHours = this.workHours.add(usage.workHours);
        this.travelHoursUsed = this.travelHoursUsed.add(usage.travelHoursUsed);
        this.vacHoursUsed = this.vacHoursUsed.add(usage.vacHoursUsed);
        this.perHoursUsed = this.perHoursUsed.add(usage.perHoursUsed);
        this.empHoursUsed = this.empHoursUsed.add(usage.empHoursUsed);
        this.famHoursUsed = this.famHoursUsed.add(usage.famHoursUsed);
        this.holHoursUsed = this.holHoursUsed.add(usage.holHoursUsed);
        this.miscHoursUsed = this.miscHoursUsed.add(usage.miscHoursUsed);
    }

    /**
     * Sets all usage fields to 0
     */
    public void resetAccrualUsage() {
        this.workHours = BigDecimal.ZERO;
        this.travelHoursUsed = BigDecimal.ZERO;
        this.vacHoursUsed = BigDecimal.ZERO;
        this.perHoursUsed = BigDecimal.ZERO;
        this.empHoursUsed = BigDecimal.ZERO;
        this.famHoursUsed = BigDecimal.ZERO;
        this.holHoursUsed = BigDecimal.ZERO;
        this.miscHoursUsed = BigDecimal.ZERO;
    }

    /* --- Functional Getters/Setters --- */

    /** The total hours is the sum of the hours used */
    public BigDecimal getTotalHoursUsed() {
        return workHours
                .add(travelHoursUsed)
                .add(vacHoursUsed)
                .add(perHoursUsed)
                .add(empHoursUsed)
                .add(famHoursUsed)
                .add(holHoursUsed)
                .add(miscHoursUsed);
    }

    /**
     * Get total sick hours used (employee sick hours + family sick hours)
     */
    public BigDecimal getTotalSickHoursUsed() {
        return this.getEmpHoursUsed()
                .add(this.getFamHoursUsed());
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = Optional.ofNullable(workHours).orElse(BigDecimal.ZERO);
    }

    public void setTravelHoursUsed(BigDecimal travelHoursUsed) {
        this.travelHoursUsed = Optional.ofNullable(travelHoursUsed).orElse(BigDecimal.ZERO);
    }

    public void setVacHoursUsed(BigDecimal vacHoursUsed) {
        this.vacHoursUsed = Optional.ofNullable(vacHoursUsed).orElse(BigDecimal.ZERO);
    }

    public void setPerHoursUsed(BigDecimal perHoursUsed) {
        this.perHoursUsed = Optional.ofNullable(perHoursUsed).orElse(BigDecimal.ZERO);
    }

    public void setEmpHoursUsed(BigDecimal empHoursUsed) {
        this.empHoursUsed = Optional.ofNullable(empHoursUsed).orElse(BigDecimal.ZERO);
    }

    public void setFamHoursUsed(BigDecimal famHoursUsed) {
        this.famHoursUsed = Optional.ofNullable(famHoursUsed).orElse(BigDecimal.ZERO);
    }

    public void setHolHoursUsed(BigDecimal holHoursUsed) {
        this.holHoursUsed = Optional.ofNullable(holHoursUsed).orElse(BigDecimal.ZERO);
    }

    public void setMiscHoursUsed(BigDecimal miscHoursUsed) {
        this.miscHoursUsed = Optional.ofNullable(miscHoursUsed).orElse(BigDecimal.ZERO);
    }

    /* --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public BigDecimal getTravelHoursUsed() {
        return travelHoursUsed;
    }

    public BigDecimal getVacHoursUsed() {
        return vacHoursUsed;
    }

    public BigDecimal getPerHoursUsed() {
        return perHoursUsed;
    }

    public BigDecimal getEmpHoursUsed() {
        return empHoursUsed;
    }

    public BigDecimal getFamHoursUsed() {
        return famHoursUsed;
    }

    public BigDecimal getHolHoursUsed() {
        return holHoursUsed;
    }

    public BigDecimal getMiscHoursUsed() {
        return miscHoursUsed;
    }
}
