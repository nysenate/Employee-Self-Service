package gov.nysenate.ess.seta.service.allowance;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.seta.model.allowances.AllowanceUsage;
import gov.nysenate.ess.seta.model.allowances.HourlyWorkPayment;
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

        allowanceUsage.setYearlyAllowance(getYearlyAllowance(year, transHistory));

        Set<PayPeriod> unpaidPeriods = getBaseAllowanceUsage(allowanceUsage, transHistory);
        getRecordAllowanceUsage(allowanceUsage, unpaidPeriods, transHistory);
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
    private Set<PayPeriod> getBaseAllowanceUsage(AllowanceUsage allowanceUsage, TransactionHistory transHistory) {
        int year = allowanceUsage.getYear();
        List<HourlyWorkPayment> payments = getHourlyPayments(year, transHistory);
        Range<LocalDate> yearDateRange = Range.closed(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        Set<PayPeriod> unpaidPeriods = new HashSet<>(periodService.getPayPeriods(PayPeriodType.AF, yearDateRange, SortOrder.NONE));

        // Add up hourly work payments to get the total hours/money paid for the year
        allowanceUsage.setBaseMoneyUsed(
                payments.stream()
                        .filter(payment -> payment.getMoneyPaidForYear(year).compareTo(BigDecimal.ZERO) > 0)
                        .peek(payment -> unpaidPeriods.removeAll(
                                periodService.getPayPeriods(PayPeriodType.AF, payment.getWorkingRange(), SortOrder.NONE) ))
                        .map(payment -> payment.getMoneyPaidForYear(year))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        return unpaidPeriods;
    }

    /**
     * Calculate the number of hours and amount of money used as recorded on timesheets for the given unpaid pay periods
     * These hours / moneys are added to the allowance usage as record hours / money
     */
    private void getRecordAllowanceUsage(AllowanceUsage allowanceUsage, Set<PayPeriod> unpaidPeriods,
                                         TransactionHistory transHistory) {
        getSalaryRecs(allowanceUsage, transHistory);
        List<TimeRecord> timeRecords =
                tRecS.getTimeRecords(Collections.singleton(allowanceUsage.getEmpId()), unpaidPeriods,
                        Sets.difference(TimeRecordStatus.getAll(), TimeRecordStatus.unlockedForEmployee()));

        allowanceUsage.setRecordMoneyUsed(
                // Add up hours and calculated payment for submitted time records that have not been paid out yet
                timeRecords.stream()
                        .map(allowanceUsage::getRecordCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
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

    /**
     * Get salary recs for an allowance usage year
     */
    private static void getSalaryRecs(AllowanceUsage allowanceUsage, TransactionHistory transHistory) {
        Range<LocalDate> yearRange = Range.closedOpen(
                LocalDate.ofYearDay(allowanceUsage.getYear(), 1), LocalDate.ofYearDay(allowanceUsage.getYear() + 1, 1));

        allowanceUsage.addSalaryRecs(transHistory.getEffectiveSalaryRecs(yearRange).values());
    }
}
