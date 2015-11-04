package gov.nysenate.ess.seta.model.accrual;

import gov.nysenate.ess.web.model.period.PayPeriod;
import gov.nysenate.ess.web.model.payroll.PayType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

/**
 * This class is intended for use within the accrual dao layer. It contains the necessary information
 * needed to compute accruals for a given pay period and provides methods to transfer data to/from other
 * accrual related classes.
 */
public class AccrualState extends AccrualSummary
{
    private static MathContext FOUR_DIGITS_MAX = new MathContext(4);
    private static BigDecimal MAX_YTD_HOURS = new BigDecimal("1820");

    protected LocalDate endDate;
    protected int payPeriodCount;
    protected boolean employeeActive;
    protected PayType payType;
    protected BigDecimal minTotalHours;
    protected BigDecimal minHoursToEnd;
    protected BigDecimal sickRate;
    protected BigDecimal vacRate;
    protected BigDecimal ytdHoursExpected;

    public AccrualState(AnnualAccSummary annualAccSummary) {
        super(annualAccSummary);
        this.endDate = annualAccSummary.getEndDate();
        this.payPeriodCount = annualAccSummary.getPayPeriodsBanked();
    }

    /** --- Methods --- */

    public PeriodAccSummary toPeriodAccrualSummary(PayPeriod refPeriod, PayPeriod currPeriod) {
        PeriodAccSummary periodAccSummary = new PeriodAccSummary(this);
        periodAccSummary.setYear(currPeriod.getEndDate().getYear());
        periodAccSummary.setComputed(true);
        periodAccSummary.setEmpAccrualState(new EmpAccrualState(this));
        periodAccSummary.setRefPayPeriod(refPeriod);
        periodAccSummary.setPayPeriod(currPeriod);
        periodAccSummary.setExpectedTotalHours(this.getYtdHoursExpected());
        periodAccSummary.setExpectedBiweekHours(getHoursExpectedInPeriod(currPeriod.getNumWeekDaysInPeriod()));
        periodAccSummary.setPrevTotalHoursYtd(this.getTotalHoursUsed());
        periodAccSummary.setSickRate(this.getSickRate());
        periodAccSummary.setVacRate(this.getVacRate());
        return periodAccSummary;
    }

    /**
     * Computes the vacation and sick accrual rates based on the current minimum hours to work per year.
     */
    public void computeRates() {
        if (minTotalHours == null) throw new IllegalStateException("Min total hours needs to be set first!");
        this.sickRate = AccrualRate.SICK.getRate(payPeriodCount, getProratePercentage());
        this.vacRate = AccrualRate.VACATION.getRate(payPeriodCount, getProratePercentage());
    }

    public BigDecimal getProratePercentage() {
        if (this.minTotalHours != null) {
            return this.minTotalHours.divide(MAX_YTD_HOURS, FOUR_DIGITS_MAX);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Helper method which increment the pay periods worked by 1.
     */
    public void incrementPayPeriodCount() {
        this.payPeriodCount++;
    }

    /**
     * Increments the accrued vacation and sick time based on the currently set vac/sick accrual rates.
     */
    public void incrementAccrualsEarned() {
        this.setVacHoursAccrued(this.getVacHoursAccrued().add(this.vacRate));
        this.setEmpHoursAccrued(this.getEmpHoursAccrued().add(this.sickRate));
    }

    /**
     * Increments the year to date expected hours by computing how many hours are required for the number of week
     * days in the pay period and prorating it based on the minimum hours required per year.
     *
     * @param period PayPeriod
     */
    public void incrementYtdHoursExpected(PayPeriod period) {
        BigDecimal hoursExpectedInPeriod = getHoursExpectedInPeriod(period.getNumWeekDaysInPeriod());
        if (ytdHoursExpected == null) {
            throw new IllegalStateException("YtdHoursExpected needs to be initialized before incrementing it.");
        }
        this.setYtdHoursExpected(this.getYtdHoursExpected().add(hoursExpectedInPeriod));
    }

    private BigDecimal getHoursExpectedInPeriod(int numWeekDaysInPeriod) {
        BigDecimal bdNumWeekDays = new BigDecimal(numWeekDaysInPeriod);
        return getProratePercentage().multiply(new BigDecimal(7)).multiply(bdNumWeekDays);
    }

    /**
     * At the start of a new year the following operations must take place on the recorded accruals:
     * - Year to date accrual usages and hours worked are set back to 0.
     * - Personal hours are reset to their initial state (35 hours prorated based on min hours required).
     * - Excess vacation and sick hours are banked, ensuring they are capped to their maximum values.
     * - Year to date Vacation and sick accruals are reset.
     * - Year to date expected hours are reset.
     */
    public void applyYearRollover() {
        resetCurrentYearUsage();
        this.setVacHoursBanked(this.getVacHoursBanked().add(this.getVacHoursAccrued()).min(AccrualRate.VACATION.getMaxHoursBanked()));
        this.setVacHoursAccrued(BigDecimal.ZERO);
        this.setEmpHoursBanked(this.getEmpHoursBanked().add(this.getEmpHoursAccrued()).min(AccrualRate.SICK.getMaxHoursBanked()));
        this.setEmpHoursAccrued(BigDecimal.ZERO);
        this.setYtdHoursExpected(BigDecimal.ZERO);
    }

    /**
     * Reset accrual usage for the current year.
     */
    public void resetCurrentYearUsage() {
        this.setVacHoursUsed(BigDecimal.ZERO);
        this.setPerHoursUsed(BigDecimal.ZERO);
        this.setEmpHoursUsed(BigDecimal.ZERO);
        this.setFamHoursUsed(BigDecimal.ZERO);
        this.setHolHoursUsed(BigDecimal.ZERO);
        this.setMiscHoursUsed(BigDecimal.ZERO);
        this.setWorkHours(BigDecimal.ZERO);
        this.setTravelHoursUsed(BigDecimal.ZERO);
    }

    /** --- Basic Getters/Setters --- */

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isEmployeeActive() {
        return employeeActive;
    }

    public void setEmployeeActive(boolean isActive) {
        this.employeeActive = isActive;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public BigDecimal getMinTotalHours() {
        return minTotalHours;
    }

    public void setMinTotalHours(BigDecimal minTotalHours) {
        this.minTotalHours = minTotalHours;
    }

    public BigDecimal getMinHoursToEnd() {
        return minHoursToEnd;
    }

    public void setMinHoursToEnd(BigDecimal minHoursToEnd) {
        this.minHoursToEnd = minHoursToEnd;
    }

    public BigDecimal getSickRate() {
        return sickRate;
    }

    public void setSickRate(BigDecimal sickRate) {
        this.sickRate = sickRate;
    }

    public BigDecimal getVacRate() {
        return vacRate;
    }

    public void setVacRate(BigDecimal vacRate) {
        this.vacRate = vacRate;
    }

    public int getPayPeriodCount() {
        return payPeriodCount;
    }

    public void setPayPeriodCount(int payPeriodCount) {
        this.payPeriodCount = payPeriodCount;
    }

    public BigDecimal getYtdHoursExpected() {
        return ytdHoursExpected;
    }

    public void setYtdHoursExpected(BigDecimal ytdHoursExpected) {
        this.ytdHoursExpected = ytdHoursExpected;
    }
}