package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.accrual.AccrualDao;
import gov.nysenate.ess.time.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.time.model.accrual.*;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import gov.nysenate.ess.time.service.expectedhrs.TxExpectedHoursService;
import gov.nysenate.ess.time.util.AccrualUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.personnel.PersonnelStatus.LWOP;
import static gov.nysenate.ess.core.model.personnel.PersonnelStatus.SLHP;
import static gov.nysenate.ess.time.model.EssTimeConstants.ANNUAL_PER_HOURS;
import static gov.nysenate.ess.time.model.EssTimeConstants.MAX_DAYS_PER_YEAR;
import static gov.nysenate.ess.time.util.AccrualUtils.getProratePercentage;
import static gov.nysenate.ess.time.util.AccrualUtils.roundPersonalHours;

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
public class EssAccrualComputeService implements AccrualComputeService
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeService.class);

    /** Set of statuses that prevent accrual if present at pay period end */
    private static final Set<PersonnelStatus> accrualOverrideStatuses = ImmutableSet.of(SLHP, LWOP);

    @Autowired private AccrualDao accrualDao;
    @Autowired private TimeRecordDao timeRecordDao;

    @Autowired private DonationService donationService;
    @Autowired private PayPeriodService payPeriodService;
    @Autowired private AccrualInfoService accrualInfoService;
    @Autowired private EmpTransactionService empTransService;
    @Autowired private TxExpectedHoursService expHoursService;
    @Autowired private EmployeeInfoService empInfoService;

    /* --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public AccrualsAvailable getAccrualsAvailable(int empId, PayPeriod payPeriod) throws AccrualException {
        verifyValidPayPeriod(payPeriod);

        PayPeriod prevPeriod = payPeriodService.getPayPeriod(
                PayPeriodType.AF, payPeriod.getStartDate().minusDays(1));

        // Get accrual records for the current and previous pay period
        TreeMap<PayPeriod, PeriodAccSummary> periodAccruals =
                getAccruals(empId, Arrays.asList(prevPeriod, payPeriod));

        PeriodAccSummary currentAccruals = periodAccruals.get(payPeriod);
        PeriodAccSummary lastAccruals = periodAccruals.get(prevPeriod);

        if (currentAccruals == null) {
            throw new AccrualException(empId, AccrualExceptionType.PERIOD_RECORD_NOT_FOUND);
        }

        // An accrual summary we will use to calculate available accruals
        AccrualSummary referenceSummary;
        // Hours expected for the current year
        BigDecimal serviceYtdExpected;
        // Hours expected for the current pay period
        BigDecimal biWeekHrsExpected = AccrualUtils.roundExpectedHours(currentAccruals.getExpectedBiweekHours());

        // If the pay period is the first of its year or is the first pay period,
        // we can get the accrual usage record for the pay period
        // but set all usage to 0 to ignore time entered during that period
        if (lastAccruals == null || payPeriod.isStartOfYearSplit()) {
            referenceSummary = new AccrualSummary(currentAccruals);

            // Remove accrued hours as they are not available until next period
            referenceSummary.setEmpHoursAccrued(BigDecimal.ZERO);
            referenceSummary.setVacHoursAccrued(BigDecimal.ZERO);

            referenceSummary.resetAccrualUsage();
            serviceYtdExpected = BigDecimal.ZERO;
        }
        // If the pay period is not the beginning of the year,
        // we can use the accrual summary as of the end of the last pay period.
        // Expected hours need to be computed for the employee instead of relying on
        // the value in the Accruals so it won't vary if a new accrual record is added
        // or removed in SFMS.
        else {
            referenceSummary = lastAccruals;
            serviceYtdExpected = lastAccruals.getExpectedTotalHours();
        }

        return new AccrualsAvailable(referenceSummary, payPeriod, serviceYtdExpected, biWeekHrsExpected,
                donationService.getHoursDonated(empId, payPeriod.getYear()));
    }

    /** {@inheritDoc} */
    @Override
    public PeriodAccSummary getAccruals(int empId, PayPeriod payPeriod) throws AccrualException {
        return getAccruals(empId, Collections.singletonList(payPeriod)).get(payPeriod);
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, Collection<PayPeriod> payPeriods) throws AccrualException {
        // Short circuit when no periods are requested.
        if (payPeriods.isEmpty()) {
            return new TreeMap<>();
        }

        // Create a sorted set of periods and ensure that all given pay periods are valid
        TreeSet<PayPeriod> periodSet = payPeriods.stream()
                .peek(this::verifyValidPayPeriod)
                .collect(Collectors.toCollection(TreeSet::new));

        LocalDate endDate = periodSet.last().getEndDate().plusDays(1);
        TreeMap<PayPeriod, PeriodAccSummary> periodAccruals =
                accrualDao.getPeriodAccruals(empId, endDate, LimitOffset.ALL, SortOrder.ASC);

        // Get existing accrual records
        TreeMap<PayPeriod, PeriodAccSummary> resultMap = getRelevantAccruals(empId, periodAccruals, periodSet);

        // Get a set of pay periods that need to be computed
        TreeSet<PayPeriod> remainingPeriods = getMissingPeriods(resultMap, periodSet);

        // Compute accruals for any remaining periods
        getGapAccruals(empId, remainingPeriods, periodAccruals)
                .forEach(accSummary -> resultMap.put(accSummary.getPayPeriod(), accSummary));

        return resultMap;
    }

    /* --- Internal Methods --- */

    private Collection<PeriodAccSummary> getGapAccruals(int empId,
                                                        SortedSet<PayPeriod> remainingPeriods,
                                                        TreeMap<PayPeriod, PeriodAccSummary> periodAccruals) {

        TransactionHistory empTrans = empTransService.getTransHistory(empId);

        // Do not proceed if there are no eligible gap periods
        if (remainingPeriods.isEmpty()) {
            return Collections.emptyList();
        }

        RangeSet<LocalDate> accrualAllowedDates = getAccrualAllowedDates(empTrans);

        // Fetch the annual accrual records (PM23ATTEND) because it provides the pay period counter which
        // is necessary for determining if the accrual rates should change.
        PayPeriod lastPeriod = remainingPeriods.last();
        TreeMap<Integer, AnnualAccSummary> annualAcc =
                new TreeMap<>(accrualInfoService.getAnnualAccruals(empId, lastPeriod.getYear()));

        // Attempt to compute an initial annual accrual summary if none exists
        if (annualAcc.isEmpty()) {
            remainingPeriods = remainingPeriods.stream()
                    .filter(period -> accrualAllowedDates.intersects(period.getDateRange()))
                    .collect(Collectors.toCollection(TreeSet::new));
            if (remainingPeriods.isEmpty()) {
                throw new AccrualException(empId, AccrualExceptionType.NO_ACTIVE_ANNUAL_RECORD_FOUND);
            }
            AnnualAccSummary annualAccSummary = computeInitialAnnualAccSummary(empTrans);
            annualAcc.put(annualAccSummary.getYear(), annualAccSummary);
        }

        AnnualAccSummary lastAnnualAccSummary = annualAcc.lastEntry().getValue();

        // Get the first day after the last annual accrual summary
        // Or use the day before the employee's cont. service date if they don't have an annual acc. summary
        LocalDate fromDate = Optional.ofNullable(lastAnnualAccSummary)
                .map(AnnualAccSummary::getEndDate)
                .map(date -> date.plusDays(1))
                .orElseGet(() -> empInfoService.getEmployee(empId).getSenateContServiceDate());

        // Throw accrual exception if from date is null
        if (fromDate == null) {
            throw new AccrualException(empId, AccrualExceptionType.NO_FROM_DATE_FOUND);
        }

        if (fromDate.isAfter(lastPeriod.getEndDate())) {
            return Collections.emptyList();
        }
        // Range from last existing accrual entry to the end of the last pay period
        Range<LocalDate> periodRange = Range.closedOpen(fromDate, lastPeriod.getEndDate().plusDays(1));
        // Pay periods which do not have existing accrual records
        List<PayPeriod> unMatchedPeriods = payPeriodService.getPayPeriods(PayPeriodType.AF, periodRange, SortOrder.ASC);

        List<TimeRecord> timeRecords = timeRecordDao.getRecordsDuring(empId, periodRange);

        TreeMap<PayPeriod, PeriodAccUsage> periodUsages = accrualDao.getPeriodAccrualUsages(empId, periodRange);

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
        PayPeriod refPeriod = optPeriodAccRecord
                .map(PeriodAccSummary::getRefPayPeriod)
                .orElseGet(gapPeriods::getFirst); // FIXME?

        ImmutableRangeSet<LocalDate> expectedHourDates = getExpectedHourDates(empTrans);

        List<PeriodAccSummary> gapAccruals = new ArrayList<>();

        // Compute accruals for each gap period
        for (PayPeriod period : gapPeriods) {

            boolean countRemainingPeriod = false;
            if (remainingPeriods.contains(period)) {
                countRemainingPeriod = true;
            }

            computeGapPeriodAccruals(period, accrualState, empTrans,
                    timeRecords, periodUsages, accrualAllowedDates, expectedHourDates, countRemainingPeriod);

            if (countRemainingPeriod) {
                PeriodAccSummary periodAccSummary = accrualState.toPeriodAccrualSummary(refPeriod, period);
                gapAccruals.add(periodAccSummary);
            }
        }

        return gapAccruals;
    }

    /**
     * Return period accruals that intersect with the given set of pay periods
     * @param empId int - Employee id
     * @param periodAccruals TreeMap<PayPeriod, PeriodAccSummary>
     * @param payPeriods desired pay periods
     * @return TreeMap<PayPeriod, PeriodAccSummary>
     */
    private TreeMap<PayPeriod, PeriodAccSummary> getRelevantAccruals(int empId,
            TreeMap<PayPeriod, PeriodAccSummary> periodAccruals, SortedSet<PayPeriod> payPeriods) {
        RangeSet<LocalDate> employedAnnualEmploymentDates =
                getEmployedAnnualEmploymentDates(empTransService.getTransHistory(empId));
        return periodAccruals.values().stream()
                .filter(perAccSumm -> payPeriods.contains(perAccSumm.getPayPeriod()))
                .filter(perAccSumm ->
                        employedAnnualEmploymentDates.intersects(perAccSumm.getPayPeriod().getDateRange()))
                .peek(perAccSumm -> perAccSumm.setExpectedHours(
                        expHoursService.getExpectedHours(perAccSumm.getEmpId(), perAccSumm.getPayPeriod())))
                .collect(Collectors.toMap(PeriodAccSummary::getPayPeriod, Function.identity(),
                        (a, b) -> b, TreeMap::new));
    }

    /**
     * Get pay periods that from the given pay period set that are not present in the given accrual record map
     * @param accrualRecords TreeMap<PayPeriod, PeriodAccSummary>
     * @param payPeriods Collection<PayPeriod>
     * @return TreeSet<PayPeriod>
     */
    private TreeSet<PayPeriod> getMissingPeriods(
            TreeMap<PayPeriod, PeriodAccSummary> accrualRecords, Collection<PayPeriod> payPeriods) {
        return payPeriods.stream()
                .filter(period -> !accrualRecords.keySet().contains(period))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Computes an annual accrual summary that can be used to calculate accruals for a new employee
     * This should only be used if no actual annual accrual summaries exist for an employee yet
     * @param transHistory {@link TransactionHistory}
     * @return {@link AnnualAccSummary}
     */
    private AnnualAccSummary computeInitialAnnualAccSummary(TransactionHistory transHistory) {

        LocalDate latestAppDate = getLatestAppDate(transHistory);
        BigDecimal startingPerHours = getStartingPerHours(transHistory);
        BigDecimal startingSickAccRate = getStartingSickAccRate(transHistory);

        AnnualAccSummary annualAccSummary = new AnnualAccSummary();

        annualAccSummary.setEmpId(transHistory.getEmployeeId());
        annualAccSummary.setContServiceDate(latestAppDate);
        annualAccSummary.setEndDate(latestAppDate.minusDays(1));
        annualAccSummary.setYear(latestAppDate.getYear());
        annualAccSummary.setPayPeriodsYtd(0);
        annualAccSummary.setPayPeriodsBanked(0);
        annualAccSummary.setUpdateDate(LocalDateTime.now());
        annualAccSummary.setPerHoursAccrued(startingPerHours);
        annualAccSummary.setEmpHoursAccrued(startingSickAccRate);

        return annualAccSummary;
    }

    /**
     * Get the latest appoint date for an employee
     * @param transHistory {@link TransactionHistory}
     * @return LocalDate
     */
    private LocalDate getLatestAppDate(TransactionHistory transHistory) {
        TreeMap<LocalDate, Boolean> effectiveEmpStatusMap = transHistory.getEffectiveEmpStatus(Range.all());
        return effectiveEmpStatusMap.keySet().stream()
                .filter(effectiveEmpStatusMap::get)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("Employee has no app date!"));
    }

    /**
     * Calculate the number of personal hours an employee starts starts with
     * @param transHistory
     * @return
     */
    private BigDecimal getStartingPerHours(TransactionHistory transHistory) {
        LocalDate appDate = getLatestAppDate(transHistory);

        PayType payType = transHistory.getEffectivePayTypes(Range.singleton(appDate))
                .lastEntry().getValue();

        if (payType == PayType.TE) {
            return BigDecimal.ZERO;
        }

        BigDecimal minTotalHours = transHistory.getEffectiveMinHours(Range.singleton(appDate))
                .lastEntry().getValue();
        BigDecimal prorateRatio = getProratePercentage(minTotalHours);

        Range<LocalDate> remainderOfYear = Range.closedOpen(appDate, appDate.plusYears(1).withDayOfYear(1));
        BigDecimal daysLeft = new BigDecimal(DateUtils.getNumberOfWeekdays(remainderOfYear))
                .min(MAX_DAYS_PER_YEAR);
        BigDecimal yearRatio = daysLeft.divide(MAX_DAYS_PER_YEAR, new MathContext(4));

        BigDecimal rawPerHours = ANNUAL_PER_HOURS
                .multiply(prorateRatio)
                .multiply(yearRatio);

        return roundPersonalHours(rawPerHours);
    }

    /**
     * Calculate initial sick rate of employee
     * @param transHistory
     * @return
     */
    private BigDecimal getStartingSickAccRate(TransactionHistory transHistory) {
        LocalDate appDate = getLatestAppDate(transHistory);

        BigDecimal minTotalHours = transHistory.getEffectiveMinHours(Range.singleton(appDate))
                .lastEntry().getValue();

        BigDecimal prorateRatio = getProratePercentage(minTotalHours);

        return AccrualRate.SICK.getRate(0, prorateRatio);
    }

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
        // Use the day before the from date as the end date if none exists
        // (this means that there have been no accruals posted yet for the employee)
        if (accrualState.getEndDate() == null) {
            accrualState.setEndDate(fromDate.minusDays(1));
        }

        PayPeriod firstPayPeriod = payPeriodService.getPayPeriod(PayPeriodType.AF, fromDate);

        // Create a date range from the end date to the first day after the end date
        // This is done so that the initial values are used if the employee was not active on the end date
        Range<LocalDate> initialRange = Range.closed(accrualState.getEndDate(), accrualState.getEndDate().plusDays(1));

        if (annualAcc.getEndDate() == null && fromDate.isBefore(LocalDate.of(1992, 1, 1))) {
            // SFMS records only go back to 1992. If using dates from before that, adjust them to the start of this
            // year instead. Fixes an edge case for employees with a continuous service date before 1992.
            LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            firstPayPeriod = payPeriodService.getPayPeriod(PayPeriodType.AF, startOfYear);
            initialRange = Range.closed(startOfYear, startOfYear.plusDays(1));
        }

        // Set the expected YTD hours from the last PD23ACCUSAGE record
        if (periodAccSum.isPresent()) {
            ExpectedHours expectedHours = expHoursService.getExpectedHours(transHistory.getEmployeeId(), firstPayPeriod);
            accrualState.setYtdHoursExpected(expectedHours.getYtdHoursExpected());
        }
        else {
            accrualState.setYtdHoursExpected(BigDecimal.ZERO);
        }
        TreeMap<LocalDate, PayType> payTypesMap = transHistory.getEffectivePayTypes(initialRange);
        if (!payTypesMap.isEmpty()) {
            accrualState.setPayType(transHistory.getEffectivePayTypes(initialRange).firstEntry().getValue());
        }
        accrualState.setNumintotend( accrualDao.getBasisForSAPersonalTime(transHistory.getEmployeeId()) );
        accrualState.setMinTotalHours(transHistory.getEffectiveMinHours(initialRange).firstEntry().getValue());
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
                                          RangeSet<LocalDate> accrualAllowedDates,
                                          RangeSet<LocalDate> expectedHoursDates,
                                          boolean countRemainingPeriod) {
        Range<LocalDate> gapPeriodRange = gapPeriod.getDateRange();

        TreeMap<LocalDate, PersonnelStatus> statusTreeMap = transHistory.getEffectivePersonnelStatus(gapPeriodRange);
        PersonnelStatus lastStatus;
        if (statusTreeMap.isEmpty()) {
            lastStatus = PersonnelStatus.NONE;
        }
        else {
            lastStatus = Optional.ofNullable(
                    transHistory.getEffectivePersonnelStatus(gapPeriodRange).lastEntry().getValue())
                    .orElse(PersonnelStatus.NONE);
        }


        // If the employee was not allowed to accrue during the gap period, don't increment accruals
        // Also, do not allow accrual if the effective personnel status at pay period end prevents it.
        boolean accruing =
                accrualAllowedDates.intersects(gapPeriodRange) &&
                !accrualOverrideStatuses.contains(lastStatus);
        accrualState.setEmpAccruing(accruing);

        // TODO if min total hours changes mid pay period,
        //   the period total hours need to be calculated according to rate of total hours / 260
        TreeMap<LocalDate, BigDecimal> minHours = transHistory.getEffectiveMinHours(gapPeriodRange);
        if (!minHours.isEmpty()) {
            accrualState.setMinTotalHours(minHours.lastEntry().getValue());
        }

        // Get a range set of days in the period where hours are expected
        RangeSet<LocalDate> expectedPeriodDates =
                RangeUtils.intersection(expectedHoursDates, ImmutableRangeSet.of(gapPeriodRange));
        accrualState.setExpectedDates(expectedPeriodDates);


        // If pay period is start of new year perform necessary adjustments to the accruals.
        if (gapPeriod.isStartOfYearSplit()) {
            accrualState.applyYearRollover();
        }

        boolean usesSubmittedRecord = false;

        // Set accrual usage from matching PD23ATTEND record if it exists
        if (periodUsages.containsKey(gapPeriod)) {
            accrualState.addPeriodAccUsage(periodUsages.get(gapPeriod));
            usesSubmittedRecord = true;
        }
        // Otherwise check if there is a time record to apply accrual usage from.
        else {
            while (!timeRecords.isEmpty() && !gapPeriod.getEndDate().isBefore(timeRecords.get(0).getBeginDate())) {
                TimeRecord record = timeRecords.remove(0);
                if (gapPeriod.getDateRange().contains(record.getBeginDate())) {
                    accrualState.addPeriodAccUsage(record.getPeriodAccUsage());
                    if (record.isActive() && record.getScope() != TimeRecordScope.EMPLOYEE) {
                        usesSubmittedRecord = true;
                    }
                }
            }
        }

        accrualState.setSubmittedRecords(usesSubmittedRecord);

        // As long as this is a valid accrual period, increment the accruals.
        if (!gapPeriod.isEndOfYearSplit()) {
            if (countRemainingPeriod) {
                accrualState.incrementPayPeriodCount();
            }
            accrualState.computeRates();
            accrualState.incrementAccrualsEarned();
        }
        // Adjust the year to date hours expected
        accrualState.incrementYtdHoursExpected();
    }

    /**
     * Return a range set containing all dates where:
     * the employee is active, has accruals allowed, and is a regular or special annual employee
     * @param empTrans TransactionHistory
     * @return ImmutableRangeSet<LocalDate>
     */
    private ImmutableRangeSet<LocalDate> getAccrualAllowedDates(TransactionHistory empTrans) {

        // Create a range set containing dates where the employee's accrual flag was set to true
        RangeSet<LocalDate> accrualStatusDates = empTrans.getAccrualDates();

        // Create a range set containing dates where the employee is regular / special annual
        // And employed
        ImmutableRangeSet<LocalDate> employedAnnualEmploymentDates = getEmployedAnnualEmploymentDates(empTrans);

        // Return the intersection of the 2 range sets
        return ImmutableRangeSet.copyOf(
                RangeUtils.intersection(employedAnnualEmploymentDates, accrualStatusDates)
        );
    }

    /**
     * Get a range set of the dates that the employee is active, and is a regular or special annual employee
     * @param empTrans
     * @return
     */
    private ImmutableRangeSet<LocalDate> getEmployedAnnualEmploymentDates(TransactionHistory empTrans) {
        // Create a range set containing dates that the employee was active
        RangeSet<LocalDate> activeDates = empTrans.getActiveDates();

        // Create a range set containing dates where the employee is regular / special annual
        RangeSet<LocalDate> annualEmploymentDates = empTrans.getPayTypeDates(PayType::isBiweekly);

        // Return the intersection of the 2 range sets
        return ImmutableRangeSet.copyOf(
                RangeUtils.intersection(activeDates, annualEmploymentDates)
        );
    }

    private ImmutableRangeSet<LocalDate> getExpectedHourDates(TransactionHistory empTrans) {
        // Get a range set of dates where the employee is employed and required to enter time
        RangeSet<LocalDate> personnelStatusDates = empTrans.getPerStatusDates(
                perStat -> perStat.isEmployed() && perStat.isTimeEntryRequired()
        );

        // Create a range set containing dates where the employee is regular / special annual
        RangeSet<LocalDate> annualEmploymentDates = empTrans.getPayTypeDates(PayType::isBiweekly);

        return ImmutableRangeSet.copyOf(
                RangeUtils.intersection(personnelStatusDates, annualEmploymentDates)
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