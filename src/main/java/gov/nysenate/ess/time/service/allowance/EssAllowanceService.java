package gov.nysenate.ess.time.service.allowance;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.payroll.SalaryRec;
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
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EssAllowanceService implements AllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(EssAllowanceService.class);

    @Autowired protected EmpTransactionService transService;
    @Autowired protected PayPeriodService periodService;
    @Autowired protected TimeRecordService tRecS;
    @Autowired protected AttendanceDao attendanceDao;

    /** {@inheritDoc} */
    @Override
    public AllowanceUsage getAllowanceUsage(int empId, int year) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        AllowanceUsage allowanceUsage = new AllowanceUsage(empId, year);
        // Set Salary Recs
        Range<LocalDate> yearRange = DateUtils.yearDateRange(allowanceUsage.getYear());
        allowanceUsage.addSalaryRecs(transHistory.getEffectiveSalaryRecs(yearRange).values());
        // Set yearly allowance
        allowanceUsage.setYearlyAllowance(getYearlyAllowance(year, transHistory));

        // Set Allowance usage, getting dates not covered by hourly work payments
        RangeSet<LocalDate> unpaidDates = getBaseAllowanceUsage(allowanceUsage, transHistory);
        getTimesheetAllowanceUsage(allowanceUsage, unpaidDates);
        return allowanceUsage;
    }

    /** --- Internal Methods --- */

    /**
     * Get the latest yearly allowance recorded in the given transaction history for the given year
     */
    private static BigDecimal getYearlyAllowance(int year, TransactionHistory transHistory) {
        Range<LocalDate> yearlyRange = Range.closed(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        TreeMap<LocalDate, BigDecimal> effectiveAllowances = transHistory.getEffectiveAllowances(yearlyRange);
        return effectiveAllowances.isEmpty() ? BigDecimal.ZERO : effectiveAllowances.lastEntry().getValue();
    }

    /**
     *  Calculate the number of hours and amount of money paid out for the given year, adding it to the allowance usage
     *  Returns a set of pay periods in the year for which the employee has not received pay
     */
    private RangeSet<LocalDate> getBaseAllowanceUsage(AllowanceUsage allowanceUsage, TransactionHistory transHistory) {
        int year = allowanceUsage.getYear();
        Range<LocalDate> yearRange = DateUtils.yearDateRange(year);

        List<HourlyWorkPayment> payments = getHourlyPayments(year, transHistory);

        // Generate a range set including all dates where the employee was not a temporary employee
        RangeMap<LocalDate, PayType> payTypeRangeMap =
                RangeUtils.toRangeMap(transHistory.getEffectivePayTypes(yearRange));
        RangeSet<LocalDate> nonTempEmploymentDates = TreeRangeSet.create();
        payTypeRangeMap.asMapOfRanges().entrySet().stream()
                .filter(entry -> entry.getValue() != PayType.TE)
                .map(Map.Entry::getKey)
                .forEach(nonTempEmploymentDates::add);

        // Initialize unpaid dates as entire year, paid dates will be removed as we iterate through payments
        RangeSet<LocalDate> unpaidDates = TreeRangeSet.create();
        unpaidDates.add(yearRange);
        // Remove all dates where the employee was not a temporary employee
        unpaidDates.removeAll(nonTempEmploymentDates);

        BigDecimal hoursPaid = BigDecimal.ZERO;
        BigDecimal moneyPaid = BigDecimal.ZERO;

        // Add up hourly work payments to get the total hours/money paid for the year
        for (HourlyWorkPayment payment : payments) {
            unpaidDates.remove(payment.getWorkingRange());
            hoursPaid = hoursPaid.add(payment.getHoursPaid());
            moneyPaid = moneyPaid.add(payment.getMoneyPaidForYear(year));
        }
        allowanceUsage.setBaseHoursUsed(hoursPaid);
        allowanceUsage.setBaseMoneyUsed(moneyPaid);
        return unpaidDates;
    }

    /**
     * Calculate the number of hours and amount of money used as recorded on timesheets for the given unpaid pay periods
     * These hours / moneys are added to the allowance usage as record hours / money
     */
    private void getTimesheetAllowanceUsage(AllowanceUsage allowanceUsage, RangeSet<LocalDate> unpaidDates) {
        // Get time record statuses for records not editable by employees
        Set<TimeRecordStatus> submittedStatuses =
                Sets.difference(TimeRecordStatus.getAll(), TimeRecordStatus.unlockedForEmployee());
        Set<Integer> employeeIdSet = Collections.singleton(allowanceUsage.getEmpId());

        List<TimeRecord> unpaidTimeRecords = tRecS.getTimeRecords(employeeIdSet, unpaidDates.span(), submittedStatuses);

        // Get non empty time entries that are unpaid
        Collection<TimeEntry> unpaidTimeEntries = unpaidTimeRecords.stream()
                .map(TimeRecord::getTimeEntries)
                .flatMap(Collection::stream)
                .filter(entry -> unpaidDates.contains(entry.getDate()))
                .filter(timeEntry -> timeEntry.getPayType() == PayType.TE)
                .collect(Collectors.toList());

        // Remove the record date ranges from unpaid dates
        unpaidTimeRecords.forEach(record -> unpaidDates.remove(record.getDateRange()));

        BigDecimal recordHours = BigDecimal.ZERO;
        BigDecimal recordMoney = BigDecimal.ZERO;
        // Add up hours and calculated payment for submitted time records that have not been paid out yet
        for (TimeEntry entry : unpaidTimeEntries) {
            recordHours = recordHours.add(entry.getWorkHours().orElse(BigDecimal.ZERO));
            recordMoney = recordMoney.add(allowanceUsage.getEntryCost(entry));
        }
        allowanceUsage.setRecordHoursUsed(recordHours);
        allowanceUsage.setRecordMoneyUsed(recordMoney);

        getAttendRecordAllowanceUsage(allowanceUsage, unpaidDates);
    }

    /**
     * Calculate the number of hours used in attendance records for the given unpaid dates
     * These hours / moneys are added to the allowance usage as record hours / money
     * It is preferred to use electronic timesheet records for this purpose since they are broken down by day
     * @see #getTimesheetAllowanceUsage(AllowanceUsage, RangeSet)
     * This is necessary to handle those pesky paper timesheets that are only recorded as attendance records
     *
     * @param allowanceUsage AllowanceUsage
     * @param unpaidDates RangeSet<LocalDate>
     */
    private void getAttendRecordAllowanceUsage(AllowanceUsage allowanceUsage, RangeSet<LocalDate> unpaidDates) {
        List<AttendanceRecord> attendRecs = attendanceDao.getOpenAttendanceRecords(allowanceUsage.getEmpId());

        attendRecs.stream()
                .filter(record -> unpaidDates.encloses(record.getDateRange()))
                .forEach(record -> applyAttendRecordAllowanceUsage(allowanceUsage, record));
    }

    /**
     * Calculate estimated hours and money used by the given attendance record
     * Add estimated hours/money to the given allowance usage
     * @param allowanceUsage AllowanceUsage
     * @param record AttendanceRecord
     */
    private void applyAttendRecordAllowanceUsage(AllowanceUsage allowanceUsage, AttendanceRecord record) {
        // Get the highest temporary salary rate that was effective during the attendance record
        BigDecimal appliedRate = allowanceUsage.getSalaryRecs().stream()
                .filter(salaryRec -> salaryRec.getPayType() == PayType.TE)
                .filter(salaryRec -> RangeUtils.intersects(salaryRec.getEffectiveRange(), record.getDateRange()))
                .map(SalaryRec::getSalaryRate)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal existingRecordHours = allowanceUsage.getRecordHoursUsed();
        BigDecimal existingRecordMoney = allowanceUsage.getRecordMoneyUsed();

        BigDecimal attendRecordHours = record.getWorkHours().orElse(BigDecimal.ZERO);
        BigDecimal attendRecordMoney = attendRecordHours.multiply(appliedRate);

        allowanceUsage.setRecordHoursUsed(existingRecordHours.add(attendRecordHours));
        allowanceUsage.setRecordMoneyUsed(existingRecordMoney.add(attendRecordMoney));
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
                .collect(Collectors.toList());

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
                                : BigDecimal.ZERO
                ))
                .filter(payment -> yearRange.contains(payment.getEffectDate()) ||
                        yearRange.contains(payment.getEndDate()))
                .sorted((hwpA, hwpB) -> hwpA.getEffectDate().compareTo(hwpB.getEffectDate()))
                .collect(Collectors.toList());
    }
}
