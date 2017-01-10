package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.core.model.period.PayPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

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
    protected boolean computed = false;

    /** True iff this summary was computed using submitted time records */
    protected boolean submitted = false;

    /** The accrual state used for computing this record if applicable. */
    protected EmpAccrualState empAccrualState = null;

    protected PayPeriod refPayPeriod;
    protected PayPeriod payPeriod;

    protected int year;
    protected BigDecimal prevTotalHoursYtd;
    protected BigDecimal expectedTotalHours;
    protected BigDecimal expectedBiweekHours;

    /** The rates should reflect the current pay period, not the base pay period. */
    protected BigDecimal sickRate;
    protected BigDecimal vacRate;

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

    @Deprecated
    public static int getWorkingDaysBetweenDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        Calendar curCal = Calendar.getInstance();

        curCal.setTime(startCal.getTime());
        int workDays = 0;

        // if start and end are the same and it is a weekday, return 1
        // if start and end are the same and it is a weekend, return 0
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            if (curCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && curCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                return 1;
            }
            else {
                return 0;
            }
        }
        //Return -1 if the start is later than the end date which indicates an error
        else if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            return -1;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }
        /*
        * Subtracting dates leaves one day lower than actual work days. We simply addUsage one day
        * to the end date to get the correct work days.
        * EX:   Subtracting Dates        Subtract Value    Real Work Days
        *        1/1/14 - 1/1/14         0                 1  (0 +1)
        *        6/9/14(Mo)-6/13/14(Fr)  4                 5  (4 +1)
        *        6/9/14(Mo)-6/14/14(Sa)  5                 5  (5 + 0)
        *        6/9/14(Mo)-6/15/14(Su)  5                 5  (5[Mo-Fr] + 0)
        *        6/9/14(Mo)-6/15/14(Mo)  6                 6  (5 + 1)
        *
         */
        //endCal.addUsage(Calendar.DATE, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
        SimpleDateFormat simpleDateFormat0 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

        do {
            if (curCal.getTime().equals(endCal.getTime())) {
                logger.debug("IN DO WHILE Current Date equals End Date");
            }
            //excluding start date
            if (curCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && curCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
            logger.debug(simpleDateFormat0.format(curCal.getTime())+" "+simpleDateFormat.format(curCal.getTime())+" Workdays:"+workDays);
            curCal.add(Calendar.DATE,1);
        } while (curCal.getTime().equals(endCal.getTime())||curCal.getTime().before(endCal.getTime()));

        return workDays;
    }
}