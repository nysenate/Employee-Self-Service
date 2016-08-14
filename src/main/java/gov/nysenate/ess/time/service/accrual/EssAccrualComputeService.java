package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.accrual.AccrualDao;
import gov.nysenate.ess.time.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.time.model.accrual.*;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.base.SqlDaoBaseService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Objects.firstNonNull;

/**
 * Service layer for computing accrual information for an employee based on processed accrual
 * and employee transaction data in SFMS.
 *
 * Accrual computation is slightly tricky because we have to rely on transaction data to adjust
 * the accrual rates. @see SqlAccrualDao for details on how the accruals are stored in the database.
 *
 * Essentially the high-level approach we take here is to:
 * 1. Pull in all the relevant data from the dao layer (which may be cached periodically)
 * 2. Figure out which pay periods we are missing accrual data for
 * 3. For those pay periods compute the accrual state which indicates what the rates are
 *    based on several factors obtained from the transaction history
 * 4. Apply the accrual state to increment/decrement the accruals for the given pay period
 * 5. Repeat steps 3 and 4 until all the pay periods are filled in.
 */
@Service
public class EssAccrualComputeService extends SqlDaoBaseService implements AccrualComputeService
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeService.class);

    @Autowired private AccrualDao accrualDao;
    @Autowired private TimeRecordDao timeRecordDao;

    @Autowired private PayPeriodService payPeriodService;
    @Autowired private AccrualInfoService accrualInfoService;
    @Autowired private EmpTransactionService empTransService;

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public PeriodAccSummary getAccruals(int empId, PayPeriod payPeriod) throws AccrualException {
        return getAccruals(empId, Collections.singletonList(payPeriod)).get(payPeriod);
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, List<PayPeriod> payPeriods) throws AccrualException {
        // Short circuit when no periods are requested.
        if (payPeriods.isEmpty()) {
            return new TreeMap<>();
        }

        // Sorted set of the supplied pay periods.
        TreeSet<PayPeriod> periodSet = new TreeSet<>(payPeriods);

        TreeMap<PayPeriod, PeriodAccSummary> resultMap = new TreeMap<>();

        // Get period accrual records up to last pay period.
        LocalDate endDate = periodSet.last().getEndDate().plusDays(1);
        TreeMap<PayPeriod, PeriodAccSummary> periodAccruals =
                accrualDao.getPeriodAccruals(empId, endDate, LimitOffset.ALL, SortOrder.ASC);

        // Filter out the pay periods that we already have PD23ACCUSAGE records for.
        Iterator<PayPeriod> periodItr = periodSet.iterator();
        while (periodItr.hasNext()) {
            PayPeriod currPeriod = periodItr.next();
            verifyValidPayPeriod(currPeriod);
            if (periodAccruals.containsKey(currPeriod)) {
                resultMap.put(currPeriod, periodAccruals.get(currPeriod));
                periodItr.remove();
            }
        }

        // Check if we have pay periods that are missing accrual data, these are the periods we need to
        // compute accruals for.
        if (!periodSet.isEmpty()) {

            // Fetch the annual accrual records (PM23ATTEND) because it provides the pay period counter which
            // is necessary for determining if the accrual rates should change.
            PayPeriod lastPeriod = periodSet.last();
            TreeMap<Integer, AnnualAccSummary> annualAcc = accrualInfoService.getAnnualAccruals(empId, lastPeriod.getYear());
            if (annualAcc.isEmpty()) {
                throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
            }
            LocalDate fromDate = firstNonNull(annualAcc.lastEntry().getValue().getEndDate(),
                                              annualAcc.lastEntry().getValue().getContServiceDate());

            if (fromDate.isBefore(lastPeriod.getEndDate())) {
                // Range from last existing accrual entry to the end of the last pay period
                Range<LocalDate> periodRange = Range.openClosed(fromDate, lastPeriod.getEndDate());
                // Pay periods which do not have existing accrual records
                List<PayPeriod> unMatchedPeriods = payPeriodService.getPayPeriods(PayPeriodType.AF, periodRange, SortOrder.ASC);

                TransactionHistory empTrans = empTransService.getTransHistory(empId);
                List<TimeRecord> timeRecords = timeRecordDao.getRecordsDuring(empId, periodRange);

                TreeMap<PayPeriod, PeriodAccUsage> periodUsages = accrualDao.getPeriodAccrualUsages(empId, periodRange);

                RangeSet<LocalDate> accrualAllowedDates = getAccrualAllowedDates(empTrans);

                // Get the latest already existing period accrual summary, if one exists
                Map.Entry<PayPeriod, PeriodAccSummary> periodAccRecord = periodAccruals.lowerEntry(lastPeriod);
                Optional<PeriodAccSummary> optPeriodAccRecord = Optional.ofNullable(periodAccRecord).map(Map.Entry::getValue);

                // Create an accrual state to keep a running tally of accrual data as new records are computed
                // The accrual state is constructed using the latest existing period accrual summary
                AccrualState accrualState = computeInitialAccState(empTrans, optPeriodAccRecord,
                    // Obtain the latest annual accrual record before/on the last pay period year requested
                    annualAcc.floorEntry(lastPeriod.getYear()).getValue(), fromDate);

                // Generate a list of all the pay periods between the period immediately following the DTPERLSPOST and
                // before the pay period we are trying to compute available accruals for. We will call these the accrual
                // gap periods.
                Range<LocalDate> gapDateRange = Range.closedOpen(
                        accrualState.getEndDate().plusDays(1),
                        lastPeriod.getEndDate().plusDays(1));
                LinkedList<PayPeriod> gapPeriods = new LinkedList<>(unMatchedPeriods.stream()
                        .filter(p -> RangeUtils.intersects(gapDateRange, p.getDateRange()))
                        .collect(Collectors.toList()));
                PayPeriod refPeriod = (optPeriodAccRecord.isPresent()) ? optPeriodAccRecord.get().getRefPayPeriod()
                        : gapPeriods.getFirst(); // FIXME?

                // Compute accruals for each gap period
                for (PayPeriod gapPeriod : gapPeriods) {
                    computeGapPeriodAccruals(gapPeriod, accrualState, empTrans, timeRecords, periodUsages, accrualAllowedDates);
                    if (periodSet.contains(gapPeriod)) {
                        resultMap.put(gapPeriod, accrualState.toPeriodAccrualSummary(refPeriod, gapPeriod));
                    }
                }
            }
        }
        return resultMap;
    }

    /** --- Internal Methods --- */

    /**
     * Compute an initial accrual state that is effective up to the DTPERLSPOST date from the annual accrual record.
     *
     * @param transHistory TransactionHistory
     * @param periodAccSum Optional<PeriodAccSummary>
     * @param annualAcc AnnualAccSummary
     * @param fromDate
     * @return AccrualState
     */
    private AccrualState computeInitialAccState(TransactionHistory transHistory, Optional<PeriodAccSummary> periodAccSum,
                                                AnnualAccSummary annualAcc, LocalDate fromDate) {
        AccrualState accrualState = new AccrualState(annualAcc);
        // Use the from date if there is no end date
        // (this means that there have been no accruals posted yet for the employee)
        if (accrualState.getEndDate() == null) {
            accrualState.setEndDate(fromDate);
        }

        Range<LocalDate> initialRange = Range.atMost(accrualState.getEndDate());

        // Set the expected YTD hours from the last PD23ACCUSAGE record
        if (periodAccSum.isPresent()) {
            accrualState.setYtdHoursExpected(periodAccSum.get().getExpectedTotalHours());
        }
        else {
            accrualState.setYtdHoursExpected(BigDecimal.ZERO);
        }
        accrualState.setPayType(transHistory.getEffectivePayTypes(initialRange).lastEntry().getValue());
        accrualState.setMinTotalHours(transHistory.getEffectiveMinHours(initialRange).lastEntry().getValue());
        accrualState.computeRates();
        return accrualState;
    }

    /**
     * @param gapPeriod PayPeriod
     * @param accrualState AccrualState
     * @param transHistory TransactionHistory
     * @param timeRecords List<TimeRecord>
     * @param periodUsages TreeMap<PayPeriod, PeriodAccUsage>
     * @param accrualAllowedDates
     */
    private void computeGapPeriodAccruals(PayPeriod gapPeriod, AccrualState accrualState, TransactionHistory transHistory,
                                          List<TimeRecord> timeRecords, TreeMap<PayPeriod, PeriodAccUsage> periodUsages,
                                          RangeSet<LocalDate> accrualAllowedDates) {
        Range<LocalDate> gapPeriodRange = gapPeriod.getDateRange();

        // If the employee was not allowed to accrue during the gap period, don't increment accruals
        if (!RangeUtils.intersects(accrualAllowedDates, gapPeriodRange)) {
            accrualState.setEmpAccruing(false);
            return;
        } else {
            accrualState.setEmpAccruing(true);
        }

        TreeMap<LocalDate, BigDecimal> minHours = transHistory.getEffectiveMinHours(gapPeriodRange);
        if (!minHours.isEmpty()) {
            accrualState.setMinTotalHours(minHours.lastEntry().getValue());
        }

        // If pay period is start of new year perform necessary adjustments to the accruals.
        if (gapPeriod.isStartOfYearSplit()) {
            accrualState.applyYearRollover();
        }

        // Set accrual usage from matching PD23ATTEND record if it exists
        if (periodUsages.containsKey(gapPeriod)) {
            accrualState.addUsage(periodUsages.get(gapPeriod));
        }
        // Otherwise check if there is a time record to apply accrual usage from.
        else {
            while (!timeRecords.isEmpty() && gapPeriod.getEndDate().isAfter(timeRecords.get(0).getBeginDate())) {
                if (gapPeriod.getDateRange().contains(timeRecords.get(0).getBeginDate())) {
                    accrualState.addUsage(timeRecords.get(0).getPeriodAccUsage());
                }
                timeRecords.remove(0);
            }
        }

        // As long as this is a valid accrual period, increment the accruals.
        if (!gapPeriod.isEndOfYearSplit()) {
            accrualState.incrementPayPeriodCount();
            accrualState.computeRates();
            accrualState.incrementAccrualsEarned();
        }
        // Adjust the year to date hours expected
        accrualState.incrementYtdHoursExpected(gapPeriod);
    }

    /**
     * Return a range set containing all dates where:
     * the employee is active, has accruals allowed, and is a regular or special annual employee
     * @param empTrans TransactionHistory
     * @return ImmutableRangeSet<LocalDate>
     */
    private ImmutableRangeSet<LocalDate> getAccrualAllowedDates(TransactionHistory empTrans) {
        // Create a range set containing dates that the employee was active
        RangeSet<LocalDate> activeDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(empTrans.getEffectiveEmpStatus(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .forEach(activeDates::add);

        // Create a range set containing dates where the employee's accrual flag was set to true
        RangeSet<LocalDate> accrualStatusDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(empTrans.getEffectiveAccrualStatus(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .forEach(accrualStatusDates::add);

        // Create a range set containing daets where the employee is regular / special annual
        RangeSet<LocalDate> annualEmploymentDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(empTrans.getEffectivePayTypes(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(entry -> entry.getValue() == PayType.RA || entry.getValue() == PayType.SA)
                .map(Map.Entry::getKey)
                .forEach(annualEmploymentDates::add);

        // Return the intersection of the 3 range sets
        return ImmutableRangeSet.copyOf(
                RangeUtils.intersection(RangeUtils.intersection(activeDates, accrualStatusDates), annualEmploymentDates)
        );
    }

    private void verifyValidPayPeriod(PayPeriod payPeriod) {
        if (payPeriod == null) {
            throw new IllegalArgumentException("Supplied payPeriod cannot be null.");
        }
        else if (!payPeriod.getType().equals(PayPeriodType.AF)) {
            throw new IllegalArgumentException("Supplied payPeriod must be of type AF (Attendance Fiscal).");
        }
    }
}