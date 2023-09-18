package gov.nysenate.ess.time.model.accrual;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Holds basic accrual information (hours accrued/used/banked). This class is intended to
 * be sub classed with additional context such as the date bounds (e.g. pay period or year).
 */
public class AccrualSummary extends AccrualUsage
{
    /** The number of vacation hours accrued within a certain time frame. */
    protected BigDecimal vacHoursAccrued = BigDecimal.ZERO;

    /** The number of vacation hours that have rolled over from prior years. */
    protected BigDecimal vacHoursBanked = BigDecimal.ZERO;

    /** The number of personal hours accrued within a certain time frame. */
    protected BigDecimal perHoursAccrued = BigDecimal.ZERO;

    /** The number of sick employee hours accrued within a certain time frame. */
    protected BigDecimal empHoursAccrued = BigDecimal.ZERO;

    /** The number of sick employee hours that have rolled over from prior years. */
    protected BigDecimal empHoursBanked = BigDecimal.ZERO;

    /** --- Constructors --- */

    public AccrualSummary() {}

    /** --- Functional Getters/Setters --- */

    public BigDecimal getTotalVacHoursAccrued() {
        return vacHoursAccrued.add(vacHoursBanked);
    }

    public BigDecimal getTotalEmpHoursAccrued() {
        return empHoursAccrued.add(empHoursBanked);
    }

    public void setVacHoursAccrued(BigDecimal vacHoursAccrued) {
        this.vacHoursAccrued = Optional.ofNullable(vacHoursAccrued).orElse(BigDecimal.ZERO);
    }

    public void setVacHoursBanked(BigDecimal vacHoursBanked) {
        this.vacHoursBanked = Optional.ofNullable(vacHoursBanked).orElse(BigDecimal.ZERO);
    }

    public void setPerHoursAccrued(BigDecimal perHoursAccrued) {
        this.perHoursAccrued = Optional.ofNullable(perHoursAccrued).orElse(BigDecimal.ZERO);
    }

    public void setEmpHoursAccrued(BigDecimal empHoursAccrued) {
        this.empHoursAccrued = Optional.ofNullable(empHoursAccrued).orElse(BigDecimal.ZERO);
    }

    public void setEmpHoursBanked(BigDecimal empHoursBanked) {
        this.empHoursBanked = Optional.ofNullable(empHoursBanked).orElse(BigDecimal.ZERO);
    }

    /** --- Copy Constructor --- */

    public AccrualSummary(AccrualSummary s) {
        super(s);
        if (s != null) {
            this.setEmpHoursAccrued(s.getEmpHoursAccrued());
            this.setEmpHoursBanked(s.getEmpHoursBanked());
            this.setPerHoursAccrued(s.getPerHoursAccrued());
            this.setVacHoursAccrued(s.getVacHoursAccrued());
            this.setVacHoursBanked(s.getVacHoursBanked());
        }
    }

    /** --- Basic Getters/Setters --- */

    public BigDecimal getVacHoursAccrued() {
        return vacHoursAccrued;
    }

    public BigDecimal getVacHoursBanked() {
        return vacHoursBanked;
    }

    public BigDecimal getPerHoursAccrued() {
        return perHoursAccrued;
    }

    public BigDecimal getEmpHoursAccrued() {
        return empHoursAccrued;
    }

    public BigDecimal getEmpHoursBanked() {
        return empHoursBanked;
    }
}