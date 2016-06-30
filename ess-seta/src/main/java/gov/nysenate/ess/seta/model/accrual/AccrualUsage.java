package gov.nysenate.ess.seta.model.accrual;

import gov.nysenate.ess.seta.service.attendance.validation.AccrualTRV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Helper class to store accrual usage numbers.
 */
public class AccrualUsage
{

    private static final Logger logger = LoggerFactory.getLogger(AccrualUsage.class);

    int empId;
    BigDecimal workHours = BigDecimal.ZERO;
    BigDecimal travelHoursUsed = BigDecimal.ZERO;
    BigDecimal vacHoursUsed = BigDecimal.ZERO;
    BigDecimal perHoursUsed = BigDecimal.ZERO;
    BigDecimal empHoursUsed = BigDecimal.ZERO;
    BigDecimal famHoursUsed = BigDecimal.ZERO;
    BigDecimal holHoursUsed = BigDecimal.ZERO;
    BigDecimal miscHoursUsed = BigDecimal.ZERO;

    public AccrualUsage() {}

    public AccrualUsage(int empId) {
        this.empId = empId;
    }

    public AccrualUsage(AccrualUsage lhs) {
        if (lhs != null) {
            this.empId = lhs.empId;
            this.workHours = lhs.workHours;
            this.travelHoursUsed = lhs.travelHoursUsed;
            this.vacHoursUsed = lhs.vacHoursUsed;
            this.perHoursUsed = lhs.perHoursUsed;
            logger.info("AccrualUsage(b): perHoursUsed:"+perHoursUsed);
            this.empHoursUsed = lhs.empHoursUsed;
            this.famHoursUsed = lhs.famHoursUsed;
            this.holHoursUsed = lhs.holHoursUsed;
            this.miscHoursUsed = lhs.miscHoursUsed;
        }
    }

    public AccrualUsage(int empId, Collection<AccrualUsage> usages) {
        this(usages.stream().reduce(new AccrualUsage(empId), AccrualUsage::addUsages));
    }

    /** --- Public Methods --- */

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
        logger.info("AccrualUsage(a): perHoursUsed:"+perHoursUsed);
        this.empHoursUsed = this.empHoursUsed.add(usage.empHoursUsed);
        this.famHoursUsed = this.famHoursUsed.add(usage.famHoursUsed);
        this.holHoursUsed = this.holHoursUsed.add(usage.holHoursUsed);
        this.miscHoursUsed = this.miscHoursUsed.add(usage.miscHoursUsed);
    }

    public static AccrualUsage addUsages(AccrualUsage lhs, AccrualUsage rhs) {
        lhs.addUsage(rhs);
        return lhs;
    }

    /** --- Functional Getters/Setters --- */

    /** The total hours is the sum of the hours used */
    public BigDecimal getTotalHoursUsed() {
        return workHours.add(travelHoursUsed).add(vacHoursUsed).add(perHoursUsed).add(empHoursUsed)
                        .add(famHoursUsed).add(holHoursUsed).add(miscHoursUsed);
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public BigDecimal getTravelHoursUsed() {
        return travelHoursUsed;
    }

    public void setTravelHoursUsed(BigDecimal travelHoursUsed) {
        this.travelHoursUsed = travelHoursUsed;
    }

    public BigDecimal getVacHoursUsed() {
        return vacHoursUsed;
    }

    public void setVacHoursUsed(BigDecimal vacHoursUsed) {
        this.vacHoursUsed = vacHoursUsed;
    }

    public BigDecimal getPerHoursUsed() {
        return perHoursUsed;
    }

    public void setPerHoursUsed(BigDecimal perHoursUsed) {
        this.perHoursUsed = perHoursUsed;
    }

    public BigDecimal getEmpHoursUsed() {
        return empHoursUsed;
    }

    public void setEmpHoursUsed(BigDecimal empHoursUsed) {
        this.empHoursUsed = empHoursUsed;
    }

    public BigDecimal getFamHoursUsed() {
        return famHoursUsed;
    }

    public void setFamHoursUsed(BigDecimal famHoursUsed) {
        this.famHoursUsed = famHoursUsed;
    }

    public BigDecimal getHolHoursUsed() {
        return holHoursUsed;
    }

    public void setHolHoursUsed(BigDecimal holHoursUsed) {
        this.holHoursUsed = holHoursUsed;
    }

    public BigDecimal getMiscHoursUsed() {
        return miscHoursUsed;
    }

    public void setMiscHoursUsed(BigDecimal miscHoursUsed) {
        this.miscHoursUsed = miscHoursUsed;
    }
}
