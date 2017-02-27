package gov.nysenate.ess.time.model.accrual;

import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

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

    BigDecimal biweekVacHoursUsed = BigDecimal.ZERO;
    BigDecimal biweekPerHoursUsed = BigDecimal.ZERO;
    BigDecimal biweekEmpHoursUsed = BigDecimal.ZERO;
    BigDecimal biweekFamHoursUsed = BigDecimal.ZERO;
    BigDecimal biweekMiscHoursUsed = BigDecimal.ZERO;
    BigDecimal biweekHolHoursUsed = BigDecimal.ZERO;
    BigDecimal biweekWorkHours = BigDecimal.ZERO;
    BigDecimal biweekTravelHoursUsed = BigDecimal.ZERO;

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
            this.empHoursUsed = lhs.empHoursUsed;
            this.famHoursUsed = lhs.famHoursUsed;
            this.holHoursUsed = lhs.holHoursUsed;
            this.miscHoursUsed = lhs.miscHoursUsed;
            this.biweekVacHoursUsed = lhs.biweekVacHoursUsed;
            this.biweekPerHoursUsed = lhs.biweekPerHoursUsed;
            this.biweekEmpHoursUsed = lhs.biweekEmpHoursUsed;
            this.biweekFamHoursUsed = lhs.biweekFamHoursUsed;
            this.biweekMiscHoursUsed = lhs.biweekMiscHoursUsed;
            this.biweekHolHoursUsed = lhs.biweekHolHoursUsed;
            this.biweekWorkHours = lhs.biweekWorkHours;
            this.biweekHolHoursUsed = lhs.biweekHolHoursUsed;
            this.biweekTravelHoursUsed = lhs.biweekTravelHoursUsed;
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
        this.empHoursUsed = this.empHoursUsed.add(usage.empHoursUsed);
        this.famHoursUsed = this.famHoursUsed.add(usage.famHoursUsed);
        this.holHoursUsed = this.holHoursUsed.add(usage.holHoursUsed);
        this.miscHoursUsed = this.miscHoursUsed.add(usage.miscHoursUsed);

        /**
         * Store Biweekly hours so it is available to display to the employee when
         * we show Accrual Usage Report
         */

        this.biweekWorkHours = usage.workHours;
        this.biweekTravelHoursUsed = usage.travelHoursUsed;
        this.biweekVacHoursUsed = usage.vacHoursUsed;
        this.biweekPerHoursUsed = usage.perHoursUsed;
        this.biweekEmpHoursUsed = usage.empHoursUsed;
        this.biweekFamHoursUsed = usage.famHoursUsed;
        this.biweekHolHoursUsed = usage.holHoursUsed;
        this.biweekMiscHoursUsed = usage.miscHoursUsed;
    }

    public static AccrualUsage addUsages(AccrualUsage lhs, AccrualUsage rhs) {
        lhs.addUsage(rhs);
        return lhs;
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

    /** --- Functional Getters/Setters --- */

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

    public BigDecimal getBiweekVacHoursUsed() {
    return biweekVacHoursUsed;
}

    public BigDecimal getBiweekPerHoursUsed() {
        return biweekPerHoursUsed;
    }

    public BigDecimal getBiweekEmpHoursUsed() {
        return biweekEmpHoursUsed;
    }

    public BigDecimal getBiweekFamHoursUsed() {
        return biweekFamHoursUsed;
    }

    public BigDecimal getBiweekHolHoursUsed() {
        return biweekHolHoursUsed;
    }

    public BigDecimal getBiweekMiscHoursUsed() {
        return biweekMiscHoursUsed;
    }

    public BigDecimal getBiweekWorkHours() {
        return biweekWorkHours;
    }

    public BigDecimal getBiweekTravelkHours() {
        return biweekTravelHoursUsed;
    }
}
