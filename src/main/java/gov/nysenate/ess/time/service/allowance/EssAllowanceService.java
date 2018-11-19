package gov.nysenate.ess.time.service.allowance;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.payroll.SalaryRec;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
import gov.nysenate.ess.time.model.allowances.HourlyWorkPayment;
import gov.nysenate.ess.time.model.allowances.PeriodAllowanceUsage;
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.payroll.PayType.TE;
import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;
import static gov.nysenate.ess.core.util.SortOrder.NONE;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.*;

@Service
public class EssAllowanceService implements AllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(EssAllowanceService.class);

    private final EmpTransactionService transService;
    private final TimeRecordService tRecS;
    private final AttendanceDao attendanceDao;
    private final PayPeriodService payPeriodService;

    @Autowired
    public EssAllowanceService(EmpTransactionService transService,
                               TimeRecordService tRecS,
                               AttendanceDao attendanceDao,
                               PayPeriodService payPeriodService) {
        this.transService = transService;
        this.tRecS = tRecS;
        this.attendanceDao = attendanceDao;
        this.payPeriodService = payPeriodService;
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Integer> getAllowanceYears(int empId) {
        RangeSet<LocalDate> allowanceDates = getAllowanceDates(empId);
        allowanceDates.remove(Range.atLeast(LocalDate.now().plusDays(1)));  // Cut off the future
        return allowanceDates.asRanges().stream()
                .map(dateRange -> DateUtils.toYearRange(dateRange, false))
                .map(yearRange -> ContiguousSet.create(yearRange, DiscreteDomain.integers()))
                .flatMap(Collection::stream)
                .collect(toCollection(TreeSet::new));
    }

    /** {@inheritDoc} */
    @Override
    public AllowanceUsage getAllowanceUsage(int empId, int year) {
        return getAllowanceUsage(empId, LocalDate.ofYearDay(year + 1, 1), false);
    }

    /** {@inheritDoc} */
    @Override
    public AllowanceUsage getAllowanceUsage(int empId, LocalDate date) {
        return getAllowanceUsage(empId, date, true);
    }

    /** {@inheritDoc} */
    @Override
    public List<PeriodAllowanceUsage> getPeriodAllowanceUsage(int empId, int year) {
        SortedSet<PayPeriod> tempPeriods = getTempPeriods(empId, year);
        PayPeriod firstPeriod = payPeriodService.getPayPeriod(AF, LocalDate.ofYearDay(year, 1));

        // Get relevant time and attendance records
        List<AttendanceRecord> attendRecords = attendanceDao.getAttendanceRecords(empId, year);
        List<TimeRecord> timeRecords =
                tRecS.getTimeRecords(Collections.singleton(empId), tempPeriods, TimeRecordStatus.getAll());

        // Map of pay period num -> attendance record
        Map<String, AttendanceRecord> attendRecordMap =
                Maps.uniqueIndex(attendRecords, AttendanceRecord::getPayPeriodNum);
        // Multimap of pay period num -> time records for that pay period
        Multimap<String, TimeRecord> timeRecordMap = Multimaps.index(timeRecords, TimeRecord::getPayPeriodNum);

        // Store running allowance usage to accumulate allowance usage for each period
        PeriodAllowanceUsage runningAllowanceUsage = new PeriodAllowanceUsage(empId, year, firstPeriod);

        List<PeriodAllowanceUsage> periodAllowanceUsages = new ArrayList<>();
        for (PayPeriod payPeriod : tempPeriods) {
            runningAllowanceUsage = runningAllowanceUsage.getNextPerAllowanceUsage(payPeriod);
            initAllowanceUsage(runningAllowanceUsage);
            String periodNo = payPeriod.getPayPeriodNum();
            // Calculate period allowance usage using relevant time and attendance records for period
            calculatePeriodAllowanceUsage(runningAllowanceUsage,
                    attendRecordMap.get(periodNo), timeRecordMap.get(periodNo));
            periodAllowanceUsages.add(runningAllowanceUsage);
        }
        return periodAllowanceUsages;
    }

    /* --- Internal Methods --- */

    private AllowanceUsage getAllowanceUsage(int empId, LocalDate date, boolean yearOfDate) {
        int year = yearOfDate ? date.getYear() : date.minusDays(1).getYear();
        TransactionHistory transHistory = transService.getTransHistory(empId);
        AllowanceUsage allowanceUsage = new AllowanceUsage(empId, year, date);
        initAllowanceUsage(allowanceUsage);

        // Set Allowance usage, getting dates not covered by hourly work payments
        RangeSet<LocalDate> unpaidDates = calculatePaidAllowanceUsage(allowanceUsage, transHistory, date);
        if (!unpaidDates.isEmpty()) {
            calculateTimesheetAllowanceUsage(allowanceUsage, unpaidDates);
        }
        return allowanceUsage;
    }

    /**
     * Initializes the given {@link AllowanceUsage} object, setting the allowance value and salaries.
     *
     * @param allowanceUsage {@link AllowanceUsage}
     */
    private void initAllowanceUsage(AllowanceUsage allowanceUsage) {
        TransactionHistory transHistory = transService.getTransHistory(allowanceUsage.getEmpId());
        Range<LocalDate> effectiveRange = Range.closedOpen(
                Year.of(allowanceUsage.getYear()).atDay(1),
                allowanceUsage.getEndDate().plusDays(1)
        );

        // Set Salary Recs
        allowanceUsage.setSalaryRecs(transHistory.getEffectiveSalaryRecs(effectiveRange).values());

        // Set yearly allowance
        TreeMap<LocalDate, BigDecimal> effectiveAllowances = transHistory.getEffectiveAllowances(effectiveRange);
        if (effectiveAllowances.isEmpty()) {
            allowanceUsage.setYearlyAllowance(ZERO);
        } else {
            allowanceUsage.setYearlyAllowance(effectiveAllowances.lastEntry().getValue());
        }
    }

    /**
     *  Calculate the number of hours and amount of money paid out for the given year, adding it to the allowance usage
     *  Returns a set of pay periods in the year for which the employee has not received pay
     */
    private RangeSet<LocalDate> calculatePaidAllowanceUsage(AllowanceUsage allowanceUsage,
                                                            TransactionHistory transHistory,
                                                            LocalDate beforeDate) {
        int year = allowanceUsage.getYear();

        List<HourlyWorkPayment> payments = getHourlyPayments(year, transHistory);

        // Initialize unpaid dates as all TE employed dates in the year before beforeDate
        RangeSet<LocalDate> unpaidDates = RangeUtils.intersection(Arrays.asList(
                ImmutableRangeSet.of(DateUtils.yearDateRange(year)),
                ImmutableRangeSet.of(Range.lessThan(beforeDate)),
                getAllowanceDates(allowanceUsage.getEmpId())
        ));

        BigDecimal hoursPaid = ZERO;
        BigDecimal moneyPaid = ZERO;

        // Add up hourly work payments to get the total hours/money paid for the year
        for (HourlyWorkPayment payment : payments) {
            if (payment.getEndDate().isBefore(beforeDate)) {
                unpaidDates.remove(payment.getWorkingRange());
                hoursPaid = hoursPaid.add(payment.getHoursPaid());
                moneyPaid = moneyPaid.add(payment.getMoneyPaidForYear(year));
            }
        }
        allowanceUsage.setBaseHoursUsed(hoursPaid);
        allowanceUsage.setBaseMoneyUsed(moneyPaid);
        return unpaidDates;
    }

    /**
     * Calculate the number of hours and amount of money used as recorded on timesheets for the given unpaid pay periods
     * These hours / moneys are added to the allowance usage as record hours / money
     */
    private void calculateTimesheetAllowanceUsage(AllowanceUsage allowanceUsage, RangeSet<LocalDate> unpaidDates) {
        // Get time record statuses for records not editable by employees
        Set<TimeRecordStatus> submittedStatuses =
                Sets.difference(TimeRecordStatus.getAll(), TimeRecordStatus.unlockedForEmployee());
        Set<Integer> employeeIdSet = Collections.singleton(allowanceUsage.getEmpId());

        List<TimeRecord> unpaidTimeRecords = tRecS.getTimeRecords(employeeIdSet, unpaidDates.span(), submittedStatuses);

        Pair<BigDecimal, BigDecimal> trecUsage =
                getTimeRecordAllowanceUsage(allowanceUsage, unpaidTimeRecords, unpaidDates);

        allowanceUsage.setRecordHoursUsed(trecUsage.getLeft());
        allowanceUsage.setRecordMoneyUsed(trecUsage.getRight());

        RangeSet<LocalDate> appliedTimeRecordDates = unpaidTimeRecords.stream()
                .map(TimeRecord::getDateRange)
                .collect(collectingAndThen(toList(), TreeRangeSet::create));

        unpaidDates.removeAll(appliedTimeRecordDates);

        calculateAttendRecordAllowanceUsage(allowanceUsage, unpaidDates);
    }

    /**
     * Get hours and money used by the given time records to the given allowance usage object.
     *
     * @param allowanceUsage {@link AllowanceUsage}
     * @param timeRecords {@link Collection<TimeRecord>}
     * @param validDates {@link RangeSet<LocalDate>}
     *
     * @return Pair<BigDecimal, BigDecimal> tuple containing used hours and money respectively
     */
    private Pair<BigDecimal, BigDecimal> getTimeRecordAllowanceUsage(AllowanceUsage allowanceUsage,
                                                            Collection<TimeRecord> timeRecords,
                                                            RangeSet<LocalDate> validDates) {

        // Get non empty time entries falling within the set of valid dates
        Collection<TimeEntry> timeEntries = timeRecords.stream()
                .map(TimeRecord::getTimeEntries)
                .flatMap(Collection::stream)
                .filter(e -> e.getPayType() == TE &&
                        validDates.contains(e.getDate()) &&
                        !e.isEmpty())
                .collect(toList());

        RangeSet<LocalDate> appliedDates = TreeRangeSet.create();

        BigDecimal recordHours = ZERO;
        BigDecimal recordMoney = ZERO;
        // Add up hours and calculated payment for submitted time records that have not been paid out yet
        for (TimeEntry entry : timeEntries) {
            recordHours = recordHours.add(entry.getWorkHours().orElse(ZERO));
            recordMoney = recordMoney.add(allowanceUsage.getEntryCost(entry));
            appliedDates.add(Range.closedOpen(entry.getDate(), entry.getDate().plusDays(1)));
        }

        return Pair.of(recordHours, recordMoney);
    }

    /**
     * Calculate the number of hours used in attendance records for the given unpaid dates
     * These hours / moneys are added to the allowance usage as record hours / money
     * It is preferred to use electronic timesheet records for this purpose since they are broken down by day
     * @see #calculateTimesheetAllowanceUsage(AllowanceUsage, RangeSet)
     * This is necessary to handle those pesky paper timesheets that are only recorded as attendance records
     *
     * @param allowanceUsage AllowanceUsage
     * @param unpaidDates RangeSet<LocalDate>
     */
    private void calculateAttendRecordAllowanceUsage(AllowanceUsage allowanceUsage, RangeSet<LocalDate> unpaidDates) {
        List<AttendanceRecord> attendRecs = attendanceDao.getOpenAttendanceRecords(allowanceUsage.getEmpId());

        attendRecs.stream()
                .filter(record -> unpaidDates.encloses(record.getDateRange()))
                .forEach(record -> applyAttendRecordAllowanceUsage(record, allowanceUsage));
    }

    /**
     * Applies hours from an attendance record to the given allowance usage
     *
     * @param attendRecord {@link AttendanceRecord}
     * @param allowanceUsage {@link AllowanceUsage}
     */
    private void applyAttendRecordAllowanceUsage(AttendanceRecord attendRecord, AllowanceUsage allowanceUsage) {
        Pair<BigDecimal, BigDecimal> usage = getAttendRecordAllowanceUsage(allowanceUsage, attendRecord);
        allowanceUsage.setRecordHoursUsed(allowanceUsage.getRecordHoursUsed().add(usage.getLeft()));
        allowanceUsage.setRecordMoneyUsed(allowanceUsage.getRecordMoneyUsed().add(usage.getRight()));
    }

    /**
     * Get estimated hours and money used by the given attendance record
     * Add estimated hours/money to the given allowance usage
     * @param allowanceUsage AllowanceUsage
     * @param record AttendanceRecord
     * @return Pair<BigDecimal, BigDecimal> tuple containing used hours and money respectively
     */
    private Pair<BigDecimal, BigDecimal> getAttendRecordAllowanceUsage(AllowanceUsage allowanceUsage,
                                                                       AttendanceRecord record) {
        // Get the highest temporary salary rate that was effective during the attendance record
        BigDecimal appliedRate = allowanceUsage.getSalaryRecs().stream()
                .filter(salaryRec -> salaryRec.getPayType() == TE)
                .filter(salaryRec -> RangeUtils.intersects(salaryRec.getEffectiveRange(), record.getDateRange()))
                .map(SalaryRec::getSalaryRate)
                .max(BigDecimal::compareTo)
                .orElse(ZERO);

        BigDecimal attendRecordHours = record.getWorkHours().orElse(ZERO);
        BigDecimal attendRecordMoney = attendRecordHours.multiply(appliedRate);

        return Pair.of(attendRecordHours, attendRecordMoney);
    }

    /**
     * Get a list of hourly work payments that are applicable to work performed in the given year
     * @param year int
     * @return List<HourlyWorkPayment>
     */
    private static List<HourlyWorkPayment> getHourlyPayments(int year, TransactionHistory transHistory) {
        LocalDate prevYearStart = LocalDate.of(year - 1, 1, 1);
        LocalDate nextYearEnd = LocalDate.of(year + 1, 12, 31);
        Range<LocalDate> auditDateRange = Range.closed(prevYearStart, nextYearEnd);
        Range<LocalDate> yearRange = DateUtils.yearDateRange(year);

        List<TransactionRecord> effectiveRecords = transHistory.getRecords(TransactionCode.HWT).stream()
                // Filter out records more than a year before or after the requested year
                .filter(record -> auditDateRange.contains(record.getAuditDate().toLocalDate()))
                .collect(toList());

        Map<LocalDate, TransactionRecord> priorYearPayments = transHistory.getRecords(TransactionCode.PYA).stream()
                .collect(Collectors.toMap(TransactionRecord::getEffectDate, Function.identity()));

        // Parse the transactions into HourlyWorkPayment records
        // Return the HourlyWorkPayments with work date ranges that overlap with the requested year
        return effectiveRecords.stream()
                .map(record -> new HourlyWorkPayment(
                        record.getAuditDate(),
                        record.getEffectDate(),
                        record.getLocalDateValue("DTENDTE"),
                        record.getBigDecimalValue("NUHRHRSPD"),
                        new BigDecimal(transHistory.latestValueOf("MOTOTHRSPD", record.getEffectDate(), false).orElse("0")),
                        priorYearPayments.containsKey(record.getEffectDate())
                                ? priorYearPayments.get(record.getEffectDate()).getBigDecimalValue("MOPRIORYRTE")
                                : ZERO
                ))
                .filter(payment -> RangeUtils.intersects(yearRange, payment.getWorkingRange()))
                .sorted(Comparator.comparing(HourlyWorkPayment::getEffectDate))
                .collect(toList());
    }

    /**
     * Get a set of dates where the employee can use allowance.
     *
     * @param empId int
     * @return RangeSet<LocalDate>
     */
    private RangeSet<LocalDate> getAllowanceDates(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeSet<LocalDate> tempDates = transHistory.getPayTypeDates(TE::equals);
        RangeSet<LocalDate> activeDates = transHistory.getPerStatusDates(personnelStatus ->
                personnelStatus.isEmployed() && personnelStatus.isTimeEntryRequired());
        return RangeUtils.intersection(tempDates, activeDates);
    }

    /**
     * Gets a set of pay periods in the given year for which the employee is an active temporary employee.
     * Excludes future dates.
     *
     * @param empId int
     * @param year int
     * @return {@link SortedSet<PayPeriod>}
     */
    private SortedSet<PayPeriod> getTempPeriods(int empId, int year) {
        RangeSet<LocalDate> allowanceDates = getAllowanceDates(empId);
        RangeSet<LocalDate> yearDates = ImmutableRangeSet.of(Range.closedOpen(
                LocalDate.ofYearDay(year, 1),
                LocalDate.now().plusDays(1)
        ));

        RangeSet<LocalDate> applicableDates = RangeUtils.intersection(yearDates, allowanceDates);

        return applicableDates.asRanges().stream()
                .map(dateRange -> payPeriodService.getPayPeriods(AF, dateRange, NONE))
                .flatMap(Collection::stream)
                .collect(toCollection(TreeSet::new));
    }

    /**
     * Calculate period hours and money used for the given {@link PeriodAllowanceUsage}.
     * Calculation is performed with given {@link AttendanceRecord} and {@link TimeRecord},
     * which should correspond to the {@link PayPeriod} of the period allowance usage.
     *
     * @param periodUsage {@link PeriodAllowanceUsage}
     * @param attendRec {@link AttendanceRecord}
     * @param timeRecords {@link Collection<TimeRecord>}
     */
    private void calculatePeriodAllowanceUsage(PeriodAllowanceUsage periodUsage,
                                               AttendanceRecord attendRec,
                                               Collection<TimeRecord> timeRecords) {
        RangeSet<LocalDate> allowanceDates = getAllowanceDates(periodUsage.getEmpId());
        Range<LocalDate> periodDates = periodUsage.getPayPeriod().getDateRange();
        RangeSet<LocalDate> periodAllowanceDates =
                RangeUtils.intersection(allowanceDates, ImmutableRangeSet.of(periodDates));

        Collection<TimeRecord> validTimeRecords = timeRecords;

        // Check time records against listed time record ids of attendance record
        if (attendRec != null) {
            validTimeRecords = attendRec.getTimeRecordCoverage(timeRecords);
        }

        Pair<BigDecimal, BigDecimal> usage = Pair.of(ZERO, ZERO);

        if (!validTimeRecords.isEmpty()) {
            // use time records
            usage = getTimeRecordAllowanceUsage(periodUsage, timeRecords, periodAllowanceDates);
        } else if (attendRec != null) {
            // use attend record
            usage = getAttendRecordAllowanceUsage(periodUsage, attendRec);
        }

        periodUsage.setPeriodHoursUsed(usage.getLeft());
        periodUsage.setPeriodMoneyUsed(usage.getRight());
    }
}
