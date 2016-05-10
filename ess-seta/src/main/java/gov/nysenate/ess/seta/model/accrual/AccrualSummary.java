package gov.nysenate.ess.seta.model.accrual;

import java.math.BigDecimal;

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

    /** --- Copy Constructor --- */

    public AccrualSummary(AccrualSummary s) {
        super(s);
        if (s != null) {
            this.setEmpHoursAccrued(s.getEmpHoursAccrued());
            this.setEmpHoursBanked(s.getEmpHoursBanked());
            this.setPerHoursAccrued(s.getPerHoursAccrued());
            this.setVacHoursAccrued(s.getVacHoursAccrued());
            this.setVacHoursBanked(s.getVacHoursBanked());
            this.setEmpHoursUsed(s.getEmpHoursUsed());
        }
    }

    /** --- Basic Getters/Setters --- */

    public BigDecimal getVacHoursAccrued() {
        return vacHoursAccrued;
    }

    public void setVacHoursAccrued(BigDecimal vacHoursAccrued) {
        this.vacHoursAccrued = vacHoursAccrued;
    }

    public BigDecimal getVacHoursBanked() {
        return vacHoursBanked;
    }

    public void setVacHoursBanked(BigDecimal vacHoursBanked) {
        this.vacHoursBanked = vacHoursBanked;
    }

    public BigDecimal getPerHoursAccrued() {
        return perHoursAccrued;
    }

    public void setPerHoursAccrued(BigDecimal perHoursAccrued) {
        this.perHoursAccrued = perHoursAccrued;
    }

    public BigDecimal getEmpHoursAccrued() {
        return empHoursAccrued;
    }

    public void setEmpHoursAccrued(BigDecimal empHoursAccrued) {
        this.empHoursAccrued = empHoursAccrued;
    }

    public BigDecimal getEmpHoursBanked() {
        return empHoursBanked;
    }

    public void setEmpHoursBanked(BigDecimal empHoursBanked) {
        this.empHoursBanked = empHoursBanked;
    }
}