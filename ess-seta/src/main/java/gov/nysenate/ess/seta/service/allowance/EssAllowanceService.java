package gov.nysenate.ess.seta.service.allowance;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.seta.model.allowances.AllowanceUsage;
import gov.nysenate.ess.seta.model.allowances.HourlyWorkPayment;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.seta.service.attendance.TimeRecordService;
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
        getRecordAllowanceUsage(allowanceUsage, unpaidDates);
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
        List<HourlyWorkPayment> payments = getHourlyPayments(year, transHistory);
        // Initialize unpaid dates as entire year, paid dates will be removed as we iterate through payments
        RangeSet<LocalDate> unpaidDates = TreeRangeSet.create();
        unpaidDates.add(DateUtils.yearDateRange(year));

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
    private void getRecordAllowanceUsage(AllowanceUsage allowanceUsage, RangeSet<LocalDate> unpaidDates) {
        // Get time record statuses for records not editable by employees
        Set<TimeRecordStatus> submittedStatuses =
                Sets.difference(TimeRecordStatus.getAll(), TimeRecordStatus.unlockedForEmployee());
        Set<Integer> employeeIdSet = Collections.singleton(allowanceUsage.getEmpId());

        Collection<TimeEntry> unpaidTimeEntries = unpaidDates.asRanges().stream()
                .map(unpaidDateRange -> tRecS.getTimeRecords(employeeIdSet, unpaidDateRange, submittedStatuses))
                .flatMap(Collection::stream)
                .map(TimeRecord::getTimeEntries)
                .flatMap(Collection::stream)
                .filter(timeEntry -> unpaidDates.contains(timeEntry.getDate()))
                .collect(Collectors.toList());

        BigDecimal recordHours = BigDecimal.ZERO;
        BigDecimal recordMoney = BigDecimal.ZERO;
        // Add up hours and calculated payment for submitted time records that have not been paid out yet
        for (TimeEntry entry : unpaidTimeEntries) {
            recordHours = recordHours.add(entry.getWorkHours().orElse(BigDecimal.ZERO));
            recordMoney = recordMoney.add(allowanceUsage.getEntryCost(entry));
        }
        allowanceUsage.setRecordHoursUsed(recordHours);
        allowanceUsage.setRecordMoneyUsed(recordMoney);
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
        Range<LocalDate> yearRange = Range.closed(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));

        Table<Integer, String, TransactionRecord> effectiveRecords = TreeBasedTable.create();
        transHistory.getRecords(TransactionCode.HWT).stream()
                // Filter out records more than a year before or after the requested year
                .filter(record -> auditDateRange.contains(record.getAuditDate().toLocalDate()))
                        // Filter out records that are not temporary employee transactions
                .forEach(record -> {
                    // Add the record to the set of effective temporary transactions
                    // if two records with the same document number exist for the same year,
                    //   use only the one with the latest audit date
                    TransactionRecord existingRecord =
                            effectiveRecords.get(record.getAuditDate().getYear(), record.getDocumentId());
                    if (existingRecord == null || existingRecord.getAuditDate().isBefore(record.getAuditDate())) {
                        effectiveRecords.put(record.getAuditDate().getYear(), record.getDocumentId(), record);
                    }
                });

        Map<LocalDate, TransactionRecord> priorYearPayments = transHistory.getRecords(TransactionCode.PYA).stream()
                .collect(Collectors.toMap(TransactionRecord::getEffectDate, Function.identity()));

        // Parse the transactions into HourlyWorkPayment records
        // Return the HourlyWorkPayments with work date ranges that overlap with the requested year
        return effectiveRecords.values().stream()
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
