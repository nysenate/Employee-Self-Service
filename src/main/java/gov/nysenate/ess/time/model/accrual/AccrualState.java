package gov.nysenate.ess.time.model.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * This class is intended for use within the accrual dao layer. It contains the necessary information
 * needed to compute accruals for a given pay period and provides methods to transfer data to/from other
 * accrual related classes.
 */
public class AccrualState extends AccrualSummary
{
    private static MathContext FOUR_DIGITS_MAX = new MathContext(4);
    private static BigDecimal HOURS_PER_DAY = new BigDecimal(7);
    private static BigDecimal MAX_YTD_HOURS = new BigDecimal("1820");

    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected int payPeriodCount;
    /** True iff the employee accrues time for the latest pay period */
    protected boolean empAccruing;
    /** True iff the latest period was calculated using a submitted time record */
    protected boolean submittedRecords;
    protected PayType payType;
    protected BigDecimal minTotalHours;
    protected BigDecimal minHoursToEnd;
    protected BigDecimal sickRate;
    protected BigDecimal vacRate;
    protected BigDecimal ytdHoursExpected;

    public AccrualState(AnnualAccSummary annualAccSummary) {
        super(annualAccSummary);
        if (annualAccSummary != null) {
            this.beginDate = ObjectUtils.max(
                    LocalDate.ofYearDay(annualAccSummary.getYear(), 1),
                    annualAccSummary.getContServiceDate());
            this.endDate = annualAccSummary.getEndDate();
            this.payPeriodCount = annualAccSummary.getPayPeriodsBanked();
        }
    }

    /** --- Methods --- */

    public PeriodAccSummary toPeriodAccrualSummary(PayPeriod refPeriod, PayPeriod currPeriod) {
        PeriodAccSummary periodAccSummary = new PeriodAccSummary(this);
        periodAccSummary.setYear(currPeriod.getEndDate().getYear());
        periodAccSummary.setComputed(true);
        periodAccSummary.setSubmitted(this.isSubmittedRecords());
        periodAccSummary.setEmpAccrualState(new EmpAccrualState(this));
        periodAccSummary.setRefPayPeriod(refPeriod);
        periodAccSummary.setPayPeriod(currPeriod);
        periodAccSummary.setExpectedTotalHours(this.getYtdHoursExpected());
        periodAccSummary.setExpectedBiweekHours(this.getExpectedHoursForPeriod(currPeriod));
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
        this.setVacHoursAccrued(this.getVacHoursAccrued().add(this.getVacRate()));
        this.setEmpHoursAccrued(this.getEmpHoursAccrued().add(this.getSickRate()));
    }

    /**
     * Increments the year to date expected hours by computing how many hours are required for the number of week
     * days in the pay period and prorating it based on the minimum hours required per year.
     *
     * @param period PayPeriod
     */
    public void incrementYtdHoursExpected(PayPeriod period) {
        BigDecimal hoursExpectedInPeriod = getExpectedHoursForPeriod(period);
        if (ytdHoursExpected == null) {
            throw new IllegalStateException("YtdHoursExpected needs to be initialized before incrementing it.");
        }
        BigDecimal totalYtdHours = this.getYtdHoursExpected().add(hoursExpectedInPeriod);
        BigDecimal four = new BigDecimal(4);
        BigDecimal roundedYtdHours = totalYtdHours.multiply(four).setScale(0, RoundingMode.HALF_UP).divide(four);
        this.setYtdHoursExpected(roundedYtdHours);
    }

    /**
     * Get the number hours the employee is required to work during the given pay period
     * @param period PayPeriod
     */
    private BigDecimal getExpectedHoursForPeriod(PayPeriod period) {
        return getHoursExpectedForDays(period.getNumWeekDaysInPeriod(Range.atLeast(beginDate)));
    }

    /**
     * Get the number of hours expected of an employee for the given number of workdays
     * @param numWorkDays number of days
     */
    private BigDecimal getHoursExpectedForDays(int numWorkDays) {
        return getProratePercentage()
                .multiply(HOURS_PER_DAY)
                .multiply(new BigDecimal(numWorkDays));
    }

    /**
     * At the start of a new year the following operations must take place on the recorded accruals:
     * - Excess vacation and sick hours are banked, ensuring they are capped to their maximum values.
     * - Year to date Vacation and sick accruals are reset.
     * - Year to date expected hours are reset.
     * - Year to date accrual usages and hours worked are set back to 0.
     * - Personal hours are reset to their initial state (35 hours prorated based on min hours required).
     */
    public void applyYearRollover() {
        this.setVacHoursBanked(
            this.getVacHoursBanked()
                    .add(this.getVacHoursAccrued())
                    .subtract(this.getVacHoursUsed())
                    .min(AccrualRate.VACATION.getMaxHoursBanked()));
        this.setVacHoursAccrued(BigDecimal.ZERO);
        this.setEmpHoursBanked(
                this.getEmpHoursBanked()
                        .add(this.getEmpHoursAccrued())
                        .subtract(this.getEmpHoursUsed())
                        .subtract(this.getFamHoursUsed())
                        .min(AccrualRate.SICK.getMaxHoursBanked()));
        this.setEmpHoursAccrued(BigDecimal.ZERO);
        this.setYtdHoursExpected(BigDecimal.ZERO);
        resetCurrentYearUsage();
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

    /** Return 0 for sick rate if employee has accruals turned off */
    public BigDecimal getSickRate() {
        return empAccruing ? sickRate : BigDecimal.ZERO;
    }

    /** Return 0 for vacation rate if employee has accruals turned off */
    public BigDecimal getVacRate() {
        return empAccruing ? vacRate : BigDecimal.ZERO;
    }

    /** --- Basic Getters/Setters --- */

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isEmpAccruing() {
        return empAccruing;
    }

    public void setEmpAccruing(boolean isActive) {
        this.empAccruing = isActive;
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

    public void setSickRate(BigDecimal sickRate) {
        this.sickRate = sickRate;
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

    public boolean isSubmittedRecords() {
        return submittedRecords;
    }

    public void setSubmittedRecords(boolean submittedRecords) {
        this.submittedRecords = submittedRecords;
    }
}
