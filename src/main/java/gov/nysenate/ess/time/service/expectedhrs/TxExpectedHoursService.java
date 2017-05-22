package gov.nysenate.ess.time.service.expectedhrs;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.personnel.Agency;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.model.EssTimeConstants;
import gov.nysenate.ess.time.service.allowance.AllowanceService;
import gov.nysenate.ess.time.util.AccrualUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
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
    @Autowired
    PayPeriodService payPeriodService;

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
        logger.debug("getExpectedHours:"+payPeriod.getDateRange().toString());
        TransactionHistory transHistory = empTransactionService.getTransHistory(empId);


        LocalDate startOfYear = LocalDate.ofYearDay(payPeriod.getYear(), 1);

        Range<LocalDate>  upToPayPeriodRange =  Range.openClosed(startOfYear, payPeriod.getStartDate().minusDays(1));

        List<PayPeriod> yearPayPeriods =  payPeriodService.getPayPeriods(PayPeriodType.AF, upToPayPeriodRange, SortOrder.ASC);

        BigDecimal expectedHours = yearPayPeriods.stream()
                .map(entry -> getTotalPayPeriodExpectedHours(entry, transHistory))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add any Temporary Actual Hours to the Expected Hours within the year prior to the given Pay Period.
        // Currently, the Temporary Hours included are only the Submitted Hours. RA/SA Hours include unsubmitted hours.
        // If including only Submitted Temporary Hours becomes an issue, then we may need to include all Temporary
        // Hours.

        expectedHours.add(allowanceService.getAllowanceUsage(empId, payPeriod.getStartDate()).getHoursUsed());

        expectedHours = AccrualUtils.roundExpectedHours(expectedHours);

        return expectedHours;
    }

    /**
     * Returns the Expected Hours for a specified Pay Period.
     *
     * @param payPeriod PayPeriod - Pay Period to compute Expected hours for
     * @param transHistory
     * @return BigDecimal
     */

    private BigDecimal getTotalPayPeriodExpectedHours(PayPeriod payPeriod, TransactionHistory transHistory) {
        ImmutableRangeSet<LocalDate>  expectedPayPeriodHourDates = getExpectedPayPeriodHourDates(transHistory, payPeriod);

        RangeSet<LocalDate> nonSenatorDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(transHistory.getEffectiveAgencyCodes(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(entry -> !Agency.SENATOR_AGENCY_CODE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .forEach(nonSenatorDates::add);

        transHistory.getEffectiveEmpStatus(payPeriod.getDateRange());

        ImmutableRangeSet<LocalDate>  expectedPayPeriodSplitHourDates = ImmutableRangeSet.copyOf(
                RangeUtils.intersection(
                        Arrays.asList(expectedPayPeriodHourDates, nonSenatorDates)
                )
        );

        BigDecimal expectedHours = getPayPeriodExpectedHours(payPeriod, transHistory, expectedPayPeriodSplitHourDates);

        return expectedHours;
    }

    /**
     * Returns the Expected Hours for a specified Pay Period.
     *
     *
     * @param payPeriod PayPeriod - Pay Period to compute Expected hours for
     * @param transHistory
     * @return BigDecimal
     */

    private BigDecimal getPayPeriodExpectedHours(PayPeriod payPeriod,  TransactionHistory transHistory,  ImmutableRangeSet<LocalDate> expectedHourDates) {
        BigDecimal expectedHours;
        Range<LocalDate> payPeriodRange = payPeriod.getDateRange();
        RangeSet<LocalDate>  payPeriodRangeSet =  TreeRangeSet.create();
        payPeriodRangeSet.add(payPeriodRange);

        RangeMap<LocalDate, BigDecimal> expectedPayPeriodHoursMap = getExpectedPayPeriodHoursMap(transHistory, payPeriod);

        expectedHourDates.complement().asRanges().stream()
                .forEach(expectedPayPeriodHoursMap::remove);

        expectedHours = expectedPayPeriodHoursMap.asMapOfRanges().entrySet().stream()
                .map(entry -> getExpectedHours(entry.getKey(), entry.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

    private ImmutableRangeSet<LocalDate> getExpectedPayPeriodHourDates(TransactionHistory empTrans, PayPeriod payPeriod) {
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

        LocalDate yearStart = payPeriod.getStartDate();
        LocalDate endDate = payPeriod.getEndDate().plusDays(1);

        ImmutableRangeSet<LocalDate> payPeriodRange = ImmutableRangeSet.of(Range.closedOpen(yearStart, endDate));

        // Create a range set containing dates where the employee is regular / special annual
        RangeSet<LocalDate> annualEmploymentDates = getAnnualEmploymentDates(empTrans);

        return ImmutableRangeSet.copyOf(
                RangeUtils.intersection(
                        Arrays.asList(personnelStatusDates, annualEmploymentDates, nonSenatorDates, payPeriodRange)
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

    /**
     * Returns Employee Transaction Service
     *
     * @return EmpTransactionService
     */

    public EmpTransactionService getEmpTransactionService() {
        return empTransactionService;
    }

    /**
     * Returns a map of Expected Pay Period Hours within a given Pay Period.
     * @param empTrans TransactionHistory - Employee Transactions
     * @param payPeriod PayPeriod
     *
     * @return EmpTransactionService RangeMap<LocalDate, BigDecimal>
     */

    private RangeMap<LocalDate, BigDecimal> getExpectedPayPeriodHoursMap(TransactionHistory empTrans, PayPeriod payPeriod) {
        RangeMap<LocalDate, BigDecimal> minHoursMap =
                RangeUtils.toRangeMap(empTrans.getEffectiveMinHours(DateUtils.ALL_DATES));

        ImmutableRangeSet<LocalDate> expectedHourDates = getExpectedPayPeriodHourDates(empTrans, payPeriod);

        empTrans.getTransRecords(payPeriod.getDateRange(), TransactionCode.getAll(), SortOrder.ASC);

        expectedHourDates.complement().asRanges().stream()
                .forEach(minHoursMap::remove);

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
}