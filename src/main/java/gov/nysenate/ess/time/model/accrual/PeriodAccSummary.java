package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Helper class to store accrual summary information per pay period.
 * Also contains the accrual rates and expected hours, which are not
 * included in the AnnualAccSummary.
 *
 * Holds PD23ACCUSAGE data.
 */
public class PeriodAccSummary extends AccrualSummary
{
    private static final Logger logger = LoggerFactory.getLogger(PeriodAccSummary.class);

    /** If true, this summary record was computed. */
    private boolean computed = false;

    /** True iff this summary was computed using submitted time records */
    private boolean submitted = false;

    /** The accrual state used for computing this record if applicable. */
    private EmpAccrualState empAccrualState = null;

    private PayPeriod refPayPeriod;
    private PayPeriod payPeriod;

    /** Summary of usage within this period */
    private PeriodAccUsage periodAccUsage;

    private int year;
    private BigDecimal prevTotalHoursYtd;
    private BigDecimal expectedTotalHours;
    private BigDecimal expectedBiweekHours;

    /** The rates should reflect the current pay period, not the base pay period. */
    private BigDecimal sickRate;
    private BigDecimal vacRate;

    /** --- Constructors --- */

    public PeriodAccSummary() {}

    public PeriodAccSummary(AccrualSummary summary) {
        super(summary);
    }

    /** --- Functional Getters/Setters --- */

    public LocalDate getEndDate() {
        if (refPayPeriod != null) {
            return refPayPeriod.getEndDate();
        }
        throw new IllegalStateException("Base pay period was not set in period accrual summary.");
    }

    public BigDecimal getTotalHoursYtd() {
        return getTotalHoursUsed();
    }

    /**
     * Sets expected hour values for this record.
     * The values in the database are not always correct so we override with calculated values.
     *
     * @param expectedHours {@link ExpectedHours}
     */
    public void setExpectedHours(ExpectedHours expectedHours) {
        setPrevTotalHoursYtd(expectedHours.getYtdHoursExpected());
        setExpectedBiweekHours(expectedHours.getPeriodHoursExpected());
        setExpectedTotalHours(expectedHours.getPeriodEndHoursExpected());

    }

    /** --- Basic Getters/Setters --- */

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isComputed() {
        return computed;
    }

    public void setComputed(boolean computed) {
        this.computed = computed;
    }

    public EmpAccrualState getEmpAccrualState() {
        return empAccrualState;
    }

    public void setEmpAccrualState(EmpAccrualState empAccrualState) {
        this.empAccrualState = empAccrualState;
    }

    public PayPeriod getRefPayPeriod() {
        return refPayPeriod;
    }

    public void setRefPayPeriod(PayPeriod refPayPeriod) {
        this.refPayPeriod = refPayPeriod;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
    }

    public BigDecimal getPrevTotalHoursYtd() {
        return prevTotalHoursYtd;
    }

    public void setPrevTotalHoursYtd(BigDecimal prevTotalHoursYtd) {
        this.prevTotalHoursYtd = prevTotalHoursYtd;
    }

    public BigDecimal getExpectedTotalHours() {
        return expectedTotalHours;
    }

    public void setExpectedTotalHours(BigDecimal expectedTotalHours) {
        this.expectedTotalHours = expectedTotalHours;
    }

    public BigDecimal getExpectedBiweekHours() {
        return expectedBiweekHours;
    }

    public void setExpectedBiweekHours(BigDecimal expectedBiweekHours) {
        this.expectedBiweekHours = expectedBiweekHours;
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

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public PeriodAccUsage getPeriodAccUsage() {
        return periodAccUsage;
    }

    public void setPeriodAccUsage(PeriodAccUsage periodAccUsage) {
        this.periodAccUsage = periodAccUsage;
    }

}