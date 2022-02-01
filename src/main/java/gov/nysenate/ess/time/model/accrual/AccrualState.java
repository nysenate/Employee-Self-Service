package gov.nysenate.ess.time.model.accrual;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.util.AccrualUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static gov.nysenate.ess.core.model.payroll.PayType.SA;
import static gov.nysenate.ess.time.model.EssTimeConstants.ANNUAL_PER_HOURS;

/**
 * This class is intended for use within the accrual dao layer. It contains the necessary information
 * needed to compute accruals for a given pay period and provides methods to transfer data to/from other
 * accrual related classes.
 */
public class AccrualState extends AccrualSummary
{
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
    /** Tracks usage for each period */
    private Map<PayPeriod, PeriodAccUsage> periodAccUsageMap = new HashMap<>();

    /** Range set of expected dates for the current pay period */
    protected RangeSet<LocalDate> expectedDates = TreeRangeSet.create();

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
        periodAccSummary.setExpectedBiweekHours(this.getExpectedHoursForPeriod());
        periodAccSummary.setPrevTotalHoursYtd(this.getTotalHoursUsed());
        periodAccSummary.setSickRate(this.getSickRate());
        periodAccSummary.setVacRate(this.getVacRate());
        PeriodAccUsage accUsage = periodAccUsageMap.get(currPeriod);
        periodAccSummary.setPeriodAccUsage(Optional.ofNullable(accUsage).orElse(new PeriodAccUsage()));
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

    /**
     * Helper method which increment the pay periods worked by 1.
     */
    public void incrementPayPeriodCount() {
        this.payPeriodCount++;
    }

    /**
     * Increments the accrued vacation and sick time based on the currently set vac/sick accrual rates.
     * Do not increment hours if employee is not accruing
     */
    public void incrementAccrualsEarned() {
        if (!empAccruing) {
            return;
        }
        this.setVacHoursAccrued(this.getVacHoursAccrued().add(this.getVacRate()));
        this.setEmpHoursAccrued(this.getEmpHoursAccrued().add(this.getSickRate()));
    }

    /**
     * Increments the year to date expected hours by computing how many hours are required for the number of week
     * days in the pay period and prorating it based on the minimum hours required per year.
     */
    public void incrementYtdHoursExpected() {
        BigDecimal hoursExpectedInPeriod = getExpectedHoursForPeriod();
        if (ytdHoursExpected == null) {
            throw new IllegalStateException("YtdHoursExpected needs to be initialized before incrementing it.");
        }
        BigDecimal totalYtdHours = this.getYtdHoursExpected().add(hoursExpectedInPeriod);
        BigDecimal roundedYtdHours = AccrualUtils.roundExpectedHours(totalYtdHours);
        this.setYtdHoursExpected(roundedYtdHours);
    }

    /* --- Internal Methods --- */

    private BigDecimal getProratePercentage() {
        return AccrualUtils.getProratePercentage(this.minTotalHours);
    }

    private BigDecimal getSpecialAnnualProratePercentage() {
        return AccrualUtils.getProratePercentage(this.ytdHoursExpected);
    }

    /**
     * Get the number hours the employee is required to work during the given pay period
     */
    private BigDecimal getExpectedHoursForPeriod() {
        BigDecimal hoursPerDay = AccrualUtils.getHoursPerDay(minTotalHours);
        BigDecimal numWorkDays = new BigDecimal(getNumWorkDays());

        return AccrualUtils.roundExpectedHours(hoursPerDay.multiply(numWorkDays));
    }

    /**
     * Get the number of expected work days in this period
     * @return long
     */
    private long getNumWorkDays() {
        return expectedDates.asRanges().stream()
                .map(DateUtils::getNumberOfWeekdays)
                .reduce(0L, Long::sum);
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
        this.setPerHoursAccrued(AccrualUtils.roundPersonalHours(
                ANNUAL_PER_HOURS.multiply(getProratePercentage())));

        if (this.payType == SA) {
            this.setPerHoursAccrued(AccrualUtils.roundPersonalHours(
                    ANNUAL_PER_HOURS.multiply(getSpecialAnnualProratePercentage())));
        }

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

    public void addPeriodAccUsage(PeriodAccUsage periodAccUsage) {
        super.addUsage(periodAccUsage);
        PayPeriod period = periodAccUsage.getPayPeriod();
        PeriodAccUsage currentAccUsage = periodAccUsageMap.get(period);
        if (currentAccUsage == null) {
            currentAccUsage = new PeriodAccUsage();
            currentAccUsage.setEmpId(periodAccUsage.getEmpId());
            currentAccUsage.setYear(period.getYear());
            currentAccUsage.setPayPeriod(period);
            periodAccUsageMap.put(period, currentAccUsage);
        }
        currentAccUsage.addUsage(periodAccUsage);
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

    public RangeSet<LocalDate> getExpectedDates() {
        return expectedDates;
    }

    public void setExpectedDates(RangeSet<LocalDate> expectedDates) {
        this.expectedDates = expectedDates;
    }
}
