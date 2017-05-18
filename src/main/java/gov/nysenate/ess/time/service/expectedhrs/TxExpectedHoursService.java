package gov.nysenate.ess.time.service.expectedhrs;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.personnel.Agency;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.model.EssTimeConstants;
import gov.nysenate.ess.time.service.allowance.AllowanceService;
import gov.nysenate.ess.time.util.AccrualUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Brian Heitner
 */
@Service
public class TxExpectedHoursService implements ExpectedHoursService {
    private static final Logger logger = LoggerFactory.getLogger(TxExpectedHoursService.class);

    @Autowired
    EmpTransactionService empTransactionService;
    @Autowired
    AllowanceService allowanceService;

    //yearDateRange


    /**
     * Returns the Year to Date Expected Hours up to a specified Pay Period.
     *
     * @param empId int - Employee id
     * @param payPeriod PayPeriod - Pay Period to compute YTD Expected hours up to
     * @return BigDecimal
     */

    @Override
    public BigDecimal getExpectedHours(PayPeriod payPeriod, int empId) {
        TransactionHistory transHistory = empTransactionService.getTransHistory(empId);
        RangeMap<LocalDate, BigDecimal> expectedHoursMap = getExpectedHoursMap(transHistory, payPeriod);
        BigDecimal expectedHours = expectedHoursMap.asMapOfRanges().entrySet().stream()
                .map(entry -> getExpectedHours(entry.getKey(), entry.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add any Temporary Actual Hours to the Expected Hours within the year prior to the given Pay Period.
        // Currently, the Temporary Hours included are only the Submitted Hours. RA/SA Hours include unsubmitted hours.
        // If including only Submitted Temporary Hours becomes an issue, then we may need to include all Temporary Hours.
        // FIXME Remove .getYear() once LocalDate Parameter is added to Allowance Service.

        expectedHours.add(allowanceService.getAllowanceUsage(empId, payPeriod.getStartDate().getYear()).getHoursUsed());

        expectedHours = AccrualUtils.roundExpectedHours(expectedHours);

        return expectedHours;
    }

    /**
     * Returns the range of dates within the year up to the Pay Period where the employee needed to
     * enter time and was not a Senator.
     *
     * @param empTrans TransactionHistory - Employee Transaction History
     * @param payPeriod PayPeriod - Pay Period to compute YTD Expected hours up to
     * @return ImmutableRangeSet<LocalDate>
     */
    private ImmutableRangeSet<LocalDate> getExpectedHourDates(TransactionHistory empTrans, PayPeriod payPeriod) {
        // Get a range set of dates where the employee is employed and required to enter time
        RangeSet<LocalDate> personnelStatusDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(empTrans.getEffectivePersonnelStatus(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(entry -> entry.getValue().isEmployed() && entry.getValue().isTimeEntryRequired())
                .map(Map.Entry::getKey)
                .forEach(personnelStatusDates::add);


        RangeSet<LocalDate> nonSenatorDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(empTrans.getEffectiveAgencyCodes(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(entry -> !Agency.SENATOR_AGENCY_CODE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .forEach(nonSenatorDates::add);


        LocalDate yearStart = LocalDate.ofYearDay(payPeriod.getYear(),01);
        LocalDate endDate = payPeriod.getStartDate();

        ImmutableRangeSet<LocalDate> yearRange = ImmutableRangeSet.of(Range.closedOpen(yearStart, endDate));

        // Create a range set containing dates where the employee is regular / special annual
        RangeSet<LocalDate> annualEmploymentDates = getAnnualEmploymentDates(empTrans);

        return ImmutableRangeSet.copyOf(
                   RangeUtils.intersection(
                           Arrays.asList(personnelStatusDates, annualEmploymentDates, nonSenatorDates, yearRange)
                   )
        );
    }

    /**
     * Returns the range of dates within the year up to the Pay Period
     *
     * @param empTrans TransactionHistory - Employee Transaction History
     * @return ImmutableRangeSet<LocalDate>
     */
    private RangeSet<LocalDate> getAnnualEmploymentDates(TransactionHistory empTrans) {
        RangeSet<LocalDate> annualEmploymentDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(empTrans.getEffectivePayTypes(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(entry -> entry.getValue() == PayType.RA || entry.getValue() == PayType.SA)
                .map(Map.Entry::getKey)
                .forEach(annualEmploymentDates::add);
        return annualEmploymentDates;
    }

    private RangeMap<LocalDate, BigDecimal> getExpectedHoursMap(TransactionHistory empTrans, PayPeriod payPeriod) {
        RangeMap<LocalDate, BigDecimal> minHoursMap =
                RangeUtils.toRangeMap(empTrans.getEffectiveMinHours(DateUtils.ALL_DATES));

        ImmutableRangeSet<LocalDate> expectedHourDates = getExpectedHourDates(empTrans, payPeriod);

        testRecords("Before dates removal:", expectedHourDates);

        expectedHourDates.complement().asRanges().stream()
                .forEach(minHoursMap::remove);

        testRecords("After dates removal:", expectedHourDates);

        return minHoursMap;
    }

    /**
     * Returns the expected hours for a given date range based on the Minimum Hours for the yeear.
     *
     * @param dateRange Range<LocalDate> - Date Range to get expected hours for
     * @return ImmutableRangeSet<LocalDate>
     */
    private BigDecimal getExpectedHours(Range<LocalDate> dateRange, BigDecimal minHours) {

        BigDecimal numberOfWeekdays = new BigDecimal(DateUtils.getNumberOfWeekdays(dateRange));
        MathContext mc = new MathContext(3);
        BigDecimal hoursPerDay = minHours.divide(EssTimeConstants.MAX_DAYS_PER_YEAR, mc);
        BigDecimal expectedHours =  AccrualUtils.roundExpectedHours(hoursPerDay.multiply(numberOfWeekdays));
        logger.debug(dateRange.lowerEndpoint()+" = "+dateRange.upperEndpoint()+":Hours per day:"+hoursPerDay+" WeekDays:"+numberOfWeekdays+" = (Expected Hours)"+expectedHours);

        return expectedHours;
    }

    private void testRecords (String msg, RangeSet<LocalDate> rangeSet) {
        Iterator iterator = rangeSet.asRanges().iterator();

        while (iterator.hasNext()) {
            logger.debug("Testing Records:"+msg +":"+iterator.next().toString());
        }
    }

}