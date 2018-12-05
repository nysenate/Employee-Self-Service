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
        return getAllowanceUsage(empId, DateUtils.yearDateRange(year));
    }

    /** {@inheritDoc} */
    @Override
    public AllowanceUsage getAllowanceUsage(int empId, LocalDate date) {
        return getAllowanceUsage(empId, DateUtils.yearToDate(date));
    }

    /** {@inheritDoc} */
    @Override
    public List<PeriodAllowanceUsage> getPeriodAllowanceUsage(int empId, int year) {
        SortedSet<PayPeriod> tempPeriods = getTempPeriods(empId, year);

        // Get relevant time and attendance records
        List<AttendanceRecord> attendRecords = attendanceDao.getAttendanceRecords(empId, year);
        List<TimeRecord> timeRecords = getValidTimeRecords(empId, year, attendRecords);

        List<PeriodAllowanceUsage> periodAllowanceUsages = new ArrayList<>();
        for (PayPeriod payPeriod : tempPeriods) {
            AllowanceUsage priorUsage = getAllowanceUsage(empId,
                    DateUtils.yearToDate(payPeriod.getStartDate()),
                    attendRecords, timeRecords);
            AllowanceUsage periodUsage = getAllowanceUsage(empId,
                    payPeriod.getDateRange(),
                    attendRecords, timeRecords);
            PeriodAllowanceUsage periodAllowanceUsage = new PeriodAllowanceUsage(payPeriod, priorUsage, periodUsage);
            initAllowanceUsage(periodAllowanceUsage);
            periodAllowanceUsages.add(periodAllowanceUsage);
        }
        return periodAllowanceUsages;
    }

    /* --- Internal Methods --- */

    private int getAllowanceYear(Range<LocalDate> dateRange) {
        LocalDate beginDate = DateUtils.startOfDateRange(dateRange);
        LocalDate endDate = DateUtils.endOfDateRange(dateRange);
        if (beginDate.getYear() != endDate.getYear()) {
            throw new IllegalArgumentException("You cannot calculate allowance over multiple years: " + dateRange);
        }
        return beginDate.getYear();
    }

    private AllowanceUsage getAllowanceUsage(int empId, Range<LocalDate> dateRange) {
        int year = getAllowanceYear(dateRange);
        List<AttendanceRecord> attendRecs = attendanceDao.getAttendanceRecords(empId, year);
        List<TimeRecord> tRecs = getValidTimeRecords(empId, year, attendRecs);
        return getAllowanceUsage(empId, dateRange, attendRecs, tRecs);
    }

    private AllowanceUsage getAllowanceUsage(int empId, Range<LocalDate> dateRange,
                                             List<AttendanceRecord> attendRecs, List<TimeRecord> tRecs) {
        int year = getAllowanceYear(dateRange);
        AllowanceUsage allowanceUsage = new AllowanceUsage(empId, year, dateRange);
        initAllowanceUsage(allowanceUsage);
        TransactionHistory transHistory = transService.getTransHistory(empId);

        // Initialize unpaid dates as all TE employed dates in during the given range
        RangeSet<LocalDate> unpaidDates = RangeUtils.intersection(
                ImmutableRangeSet.of(dateRange),
                getAllowanceDates(allowanceUsage.getEmpId())
        );

        // Set Allowance usage, getting dates not covered by hourly work payments
        unpaidDates = calculatePaidAllowanceUsage(allowanceUsage, transHistory, unpaidDates,  attendRecs, tRecs);
        // If some dates are still not accounted for, see if there are any applicable time records
        if (!unpaidDates.isEmpty()) {
            unpaidDates = calculateTimesheetAllowanceUsage(allowanceUsage, unpaidDates, tRecs);
        }
        // If dates are still not accounted for, see if there are any applicable attendance records
        if (!unpaidDates.isEmpty()) {
            calculateAttendRecordAllowanceUsage(allowanceUsage, unpaidDates, attendRecs);
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
        Range<LocalDate> effectiveRange = allowanceUsage.getEffectiveRange();

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
                                                            RangeSet<LocalDate> unpaidDates,
                                                            List<AttendanceRecord> attendRecs,
                                                            List<TimeRecord> timeRecords) {
        int year = allowanceUsage.getYear();
        RangeSet<LocalDate> newUnpaidDates = TreeRangeSet.create(unpaidDates);

        List<HourlyWorkPayment> payments = getHourlyPayments(year, transHistory);

        RangeMap<LocalDate, SalaryRec> salaryRecRangeMap = TreeRangeMap.create();
        allowanceUsage.getSalaryRecs().forEach(salRec -> salaryRecRangeMap.put(salRec.getEffectiveRange(), salRec));

        RangeMap<LocalDate, TimeRecord> timeRecRangeMap = TreeRangeMap.create();
        timeRecords.forEach(trec -> timeRecRangeMap.put(trec.getDateRange(), trec));

        BigDecimal totalHoursPaid = ZERO;
        BigDecimal totalMoneyPaid = ZERO;

        // Add up hourly work payments to get the total hours/money paid for the year
        for (HourlyWorkPayment payment : payments) {
            Range<LocalDate> effectiveRange = payment.getWorkingRangeForYear(year);
            if (!unpaidDates.encloses(effectiveRange)) {
                continue;
            }

            BigDecimal hoursPaid = getHoursPaidInPayment(year, payment, attendRecs);
            BigDecimal moneyPaid = getMoneyPaidInPayment(year, payment, salaryRecRangeMap, timeRecRangeMap, hoursPaid);

            totalHoursPaid = totalHoursPaid.add(hoursPaid);
            totalMoneyPaid = totalMoneyPaid.add(moneyPaid);

            newUnpaidDates.remove(effectiveRange);
        }

        allowanceUsage.setBaseHoursUsed(totalHoursPaid);
        allowanceUsage.setBaseMoneyUsed(totalMoneyPaid);

        return newUnpaidDates;
    }

    /**
     * Get the number of hours paid for in the given {@link HourlyWorkPayment}.
     */
    private BigDecimal getHoursPaidInPayment(int year, HourlyWorkPayment payment,
                                             List<AttendanceRecord> attendRecs) {
        Range<LocalDate> effectiveRange = payment.getWorkingRangeForYear(year);
        // Normally, the hours paid are what the record says they are.
        BigDecimal hoursPaid = payment.getHoursPaid();
        // But things get tricky if the payment straddles multiple years
        if (payment.getEndDate().getYear() != payment.getEffectDate().getYear()) {
            if (payment.getMoneyPaidForYear(year).compareTo(ZERO) == 0) {
                hoursPaid = ZERO;
            } else if (payment.getMoneyPaidForYear(year).compareTo(payment.getMoneyPaid()) < 0 &&
                    RangeUtils.intersects(attendRecs.get(0).getDateRange(), effectiveRange)) {
                // If payment is split over multiple years, use attendance record hours
                hoursPaid = attendRecs.stream()
                        .filter(attRec -> RangeUtils.intersects(attRec.getDateRange(), effectiveRange))
                        .map(attRec -> attRec.getWorkHours().orElse(ZERO))
                        .reduce(ZERO, BigDecimal::add);
            }
        }
        return hoursPaid;
    }

    /**
     * Get the amount of money paid out by the given {@link HourlyWorkPayment}
     */
    private BigDecimal getMoneyPaidInPayment(int year, HourlyWorkPayment payment,
                                             RangeMap<LocalDate, SalaryRec> salaryRecRangeMap,
                                             RangeMap<LocalDate, TimeRecord> timeRecRangeMap,
                                             BigDecimal hoursPaid) {
        Range<LocalDate> effectiveRange = payment.getWorkingRangeForYear(year);
        Collection<SalaryRec> effectiveSalaries =
                salaryRecRangeMap.subRangeMap(effectiveRange).asMapOfRanges().values();
        boolean salaryChanged = effectiveSalaries.stream()
                .anyMatch(salaryRec -> salaryRec.getAuditDate().isAfter(payment.getAuditDate()));

        // Normally, the record is correct about the amount of money paid for the year.
        BigDecimal moneyPaid = payment.getMoneyPaidForYear(year);
        // But if there was a retroactive salary change entered after the payment was made,
        // the difference is not included in the payment, so we need to calculate the money paid.
        if (salaryChanged) {
            moneyPaid = ZERO;
            Collection<TimeRecord> PaymentTRecs =
                    timeRecRangeMap.subRangeMap(effectiveRange).asMapOfRanges().values();
            BigDecimal remainingHours = hoursPaid;
            // Only use time records if there are multiple salaries, which necessitate day by day calculation.
            if (effectiveSalaries.size() > 1) {
                for (TimeRecord tRec : PaymentTRecs) {
                    for (TimeEntry e : tRec.getTimeEntries()) {
                        if (!effectiveRange.contains(e.getDate()) || e.getPayType() != TE) {
                            continue;
                        }
                        BigDecimal workHours = e.getWorkHours().orElse(ZERO);
                        BigDecimal salary = Optional.ofNullable(salaryRecRangeMap.get(e.getDate()))
                                .map(SalaryRec::getSalaryRate)
                                .orElse(ZERO);
                        remainingHours = remainingHours.subtract(workHours);
                        moneyPaid = moneyPaid.add(workHours.multiply(salary));
                    }
                }
            }
            // It is possible that there are paid hours not covered by time records, or that they were not needed.
            // Assume any hours not covered by time records are paid using the max salary for the effective range.
            BigDecimal maxSalary = effectiveSalaries.stream()
                    .map(SalaryRec::getSalaryRate).max(BigDecimal::compareTo).get();
            moneyPaid = moneyPaid.add(remainingHours.multiply(maxSalary));
        }
        return moneyPaid;
    }

    /**
     * Calculate the number of hours and amount of money used as recorded on timesheets for the given unpaid pay periods
     * These hours / moneys are added to the allowance usage as record hours / money
     * Return the range set of unpaid dates not covered by time records.
     */
    private RangeSet<LocalDate> calculateTimesheetAllowanceUsage(AllowanceUsage allowanceUsage,
                                                                 RangeSet<LocalDate> unpaidDates,
                                                                 List<TimeRecord> timeRecords) {
        List<TimeRecord> unpaidTimeRecords = timeRecords.stream()
                .filter(tRec -> unpaidDates.intersects(tRec.getDateRange()))
                .collect(toList());

        Pair<BigDecimal, BigDecimal> trecUsage =
                getTimeRecordAllowanceUsage(allowanceUsage, unpaidTimeRecords, unpaidDates);

        allowanceUsage.setRecordHoursUsed(trecUsage.getLeft());
        allowanceUsage.setRecordMoneyUsed(trecUsage.getRight());

        RangeSet<LocalDate> appliedTimeRecordDates = unpaidTimeRecords.stream()
                .map(TimeRecord::getDateRange)
                .collect(collectingAndThen(toList(), TreeRangeSet::create));

        RangeSet<LocalDate> newUnpaidDates = TreeRangeSet.create(unpaidDates);
        newUnpaidDates.removeAll(appliedTimeRecordDates);

        return newUnpaidDates;
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
                .filter(tRec -> validDates.intersects(tRec.getDateRange()))
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
     * @see #calculateTimesheetAllowanceUsage(AllowanceUsage, RangeSet, List)
     * This is necessary to handle those pesky paper timesheets that are only recorded as attendance records
     *
     * @param allowanceUsage AllowanceUsage
     * @param unpaidDates RangeSet<LocalDate>
     */
    private void calculateAttendRecordAllowanceUsage(AllowanceUsage allowanceUsage,
                                                     RangeSet<LocalDate> unpaidDates,
                                                     List<AttendanceRecord> attendRecs) {
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

    private List<TimeRecord> getValidTimeRecords(int empId, int year, List<AttendanceRecord> attendRecs) {
        Set<TimeRecordStatus> submittedStatuses =
                Sets.difference(TimeRecordStatus.getAll(), TimeRecordStatus.unlockedForEmployee());
        Set<Integer> employeeIdSet = Collections.singleton(empId);

        List<TimeRecord> tRecs = tRecS.getTimeRecords(employeeIdSet, DateUtils.yearDateRange(year), submittedStatuses);

        RangeMap<LocalDate, AttendanceRecord> aRecRangeMap = TreeRangeMap.create();
        attendRecs.forEach(arec -> aRecRangeMap.put(arec.getDateRange(), arec));

        return tRecs.stream()
                .filter(tRec -> {
                    AttendanceRecord aRec = aRecRangeMap.get(tRec.getBeginDate());
                    return aRec == null || aRec.getTimesheetIds().contains(tRec.getTimeRecordId());
                })
                .collect(toList());
    }
}
