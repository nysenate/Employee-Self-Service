package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.auth.DepartmentalWhitelistService;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.time.model.attendance.*;
import gov.nysenate.ess.time.service.accrual.AccrualInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EssTimeRecordManager implements TimeRecordManager
{
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManager.class);

    /** A set of transactions that, when posted, will require changes in existing time records */
    private static final ImmutableSet recordAlteringTransCodes =
            ImmutableSet.of(
                    TransactionCode.SUP,    // Supervisor change
                    TransactionCode.TYP,    // Pay type change
                    TransactionCode.EMP,    // Termination
                    TransactionCode.RSH,    // Responsibility center head change
                    TransactionCode.SPE     // Personnel status change
            );

    /** --- Daos --- */
    @Autowired protected EmployeeDao employeeDao;
    @Autowired protected TimeRecordDao timeRecordDao;
    @Autowired protected AttendanceDao attendanceDao;

    /** --- Services --- */
    @Lazy
    @Autowired protected TimeRecordService timeRecordService;
    @Autowired protected AccrualInfoService accrualInfoService;
    @Autowired protected PayPeriodService payPeriodService;
    @Autowired protected EmpTransactionService transService;
    @Autowired protected EmployeeInfoService empInfoService;
    @Autowired protected DepartmentalWhitelistService deptWhitelistService;

    @Autowired protected EventBus eventBus;

    /** When set to false, the scheduled run of ensureAllRecords wont run */
    @Value("${scheduler.timerecord.ensureall.enabled:false}")
    private boolean ensureAllEnabled;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public int ensureRecords(int empId, Collection<PayPeriod> payPeriods) {
        List<TimeRecord> existingRecords =
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll());
        List<AttendanceRecord> attendanceRecords = attendanceDao.getOpenAttendanceRecords(empId);
        return ensureRecords(empId, payPeriods, existingRecords, attendanceRecords);
    }

    /** {@inheritDoc} */
    @Override
    public int ensureRecords(int empId) {
        List<PayPeriod> payPeriods = accrualInfoService.getOpenPayPeriods(PayPeriodType.AF, empId, SortOrder.ASC);
        List<TimeRecord> activeTimeRecords = timeRecordService.getActiveTimeRecords(empId);
        List<AttendanceRecord> attendanceRecords = attendanceDao.getOpenAttendanceRecords(empId);
        return ensureRecords(empId, payPeriods, activeTimeRecords, attendanceRecords);
    }

    /** {@inheritDoc} */
    @Override
    public void ensureAllActiveRecords() {
        logger.info("***** CHECKING ACTIVE TIME RECORDS *****");
        // Get all employees currently active, also get all active attendance records
        Set<Integer> activeEmpIds = employeeDao.getActiveEmployeeIds();
        logger.info("getting active attendance records...");
        ListMultimap<Integer, AttendanceRecord> activeAttendanceRecords = attendanceDao.getOpenAttendanceRecords();

        // Examine employees that are active or have open attendance records
        Set<Integer> empIds = Sets.union(activeEmpIds, activeAttendanceRecords.keySet());

        logger.info("processing active employee records...");

        // Create and patch records for each employee
        int totalSaved = empIds.stream()
                // Do not modify records for departments that are not yet in ESS
                .filter(deptWhitelistService::isAllowed)
                .sorted()
                .map(empId -> ensureRecords(empId,
                        accrualInfoService.getOpenPayPeriods(PayPeriodType.AF, empId, SortOrder.ASC),
                        timeRecordService.getActiveTimeRecords(empId),
                        Optional.ofNullable(activeAttendanceRecords.get(empId)).orElse(Collections.emptyList())))
                .reduce(0, Integer::sum);
        logger.info("checked {} employees\tsaved {} records", empIds.size(), totalSaved);
    }

    /**
     * invokes the ensureAllActiveRecords method according to the configured cron value
     * @see #ensureAllActiveRecords()
     */
    @Scheduled(cron = "${scheduler.timerecord.ensureall.cron}")
    public synchronized void scheduledEnsureAll() {
        if (ensureAllEnabled) {
            ensureAllActiveRecords();
        }
    }

    /**
     * Modifies records when new transactions are posted
     * @param event TransactionHistoryUpdateEvent
     */
    @Subscribe
    public synchronized void handleTransactionHistoryUpdateEvent(TransactionHistoryUpdateEvent event) {
        event.getTransRecs().stream()
                .filter(transRec -> recordAlteringTransCodes.contains(transRec.getTransCode()))
                .map(TransactionRecord::getEmployeeId)
                .distinct()
                .peek(empId -> logger.info("Re checking records for employee {} due to detected transaction post", empId))
                .forEach(this::ensureRecords);
    }

    /* --- Internal Methods --- */

    /**
     * Ensure that the employee has up to date records that cover all given pay periods
     * Existing records are split/modified as needed to ensure correctness
     * If createTempRecords is false, then records will only be created for periods with annual pay work days
     */
    private int ensureRecords(int empId, Collection<PayPeriod> payPeriods, Collection<TimeRecord> existingRecords,
                              Collection<AttendanceRecord> attendanceRecords) {
        logger.info("Generating records for {} over {} pay periods with {} existing records",
                empId, payPeriods.size(), existingRecords.size());

        // Get a set of ranges for which there should be time records
        LinkedHashSet<Range<LocalDate>> recordRanges = getRecordRanges(empId, payPeriods, attendanceRecords);
        List<TimeRecord> recordsToSave = new LinkedList<>();
        TransactionHistory transHistory = transService.getTransHistory(empId);

        // Get the latest submitted record.  New records will not be created for dates before this record
        Optional<TimeRecord> latestSubmitted = existingRecords.stream()
                .filter(record -> record.getRecordStatus().getScope() != TimeRecordScope.EMPLOYEE)
                .max(TimeRecord::compareTo);

        // Check that existing records correspond to the record ranges
        // Split any records that span multiple ranges
        //  also ensure that existing records and entries contain up to date information
        // Remove ranges that are covered by existing records
        List<TimeRecord> patchedRecords = patchExistingRecords(existingRecords, recordRanges);
        recordsToSave.addAll(patchedRecords);
        long patchedRecordsSaved = patchedRecords.size();

        // Create new records for all ranges not covered by existing records
        long newRecordsSaved = 0;
        if (transHistory.isFullyAppointed()) {
            newRecordsSaved = recordRanges.stream()
                    .filter(range -> DateUtils.startOfDateRange(range).isAfter(
                            latestSubmitted.map(TimeRecord::getEndDate).orElse(LocalDate.MIN)))
                    .map(range -> createTimeRecord(empId, range))
                    .peek(recordsToSave::add)
                    .count();
        }

        recordsToSave.forEach(timeRecordService::saveRecord);

        if (recordsToSave.isEmpty()) {
            logger.info("empId {}: no changes", empId);
        } else {
            logger.info("empId {}:\t{} periods\t{} existing\t{} saved:\t{} new\t{} patched/split",
                    empId, payPeriods.size(), existingRecords.size(), recordsToSave.size(), newRecordsSaved, patchedRecordsSaved);
        }
        return recordsToSave.size();
    }

    /**
     * Check existing records to make sure that records correspond with the computed record date ranges,
     *  and contain correct information
     * Records that are already approved by personnel will not be patched
     * As existing records are checked, corresponding covered ranges are removed from recordRanges
     * Records that do not check out are modified accordingly
     * @return List<TimeRecord> - a list of existing records that were modified
     */
    private List<TimeRecord> patchExistingRecords(
            Collection<TimeRecord> existingRecords, LinkedHashSet<Range<LocalDate>> recordRanges) {
        return existingRecords.stream()
                .filter(record -> TimeRecordStatus.inProgress().contains(record.getRecordStatus()))
                .map(record -> patchRecord(record, recordRanges))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Checks the given record and patches it if necessary
     * @param record {@link TimeRecord} - Time record to be patched
     * @param recordRanges LinkedHashSet<Range<LocalDate>> - Set of date ranges for which there should be records
     *                     The date ranges corresponding to the passed in time record are removed
     * @return List<TimeRecord> - List of new or existing time records that have been modified
     *                            and need to be saved
     */
    private List<TimeRecord> patchRecord(TimeRecord record, LinkedHashSet<Range<LocalDate>> recordRanges) {
        // Get list of record ranges that cover the given record
        List<Range<LocalDate>> rangesUnderRecord = recordRanges.stream()
                .filter(range -> range.isConnected(record.getDateRange()) &&
                        !range.intersection(record.getDateRange()).isEmpty())
                .collect(Collectors.toList());
        // Remove these ranges from the range set to indicate that they are covered
        recordRanges.removeAll(rangesUnderRecord);
        // If there are no active record dates for the record and the begin date has passed,
        // set it as inactive
        if (rangesUnderRecord.isEmpty()) {
            if (record.getBeginDate().isBefore(LocalDate.now())) {
                logger.info("deactivating record empid: {}  dates: {}", record.getEmployeeId(), record.getDateRange());
                record.setActive(false);
                return Collections.singletonList(record);
            }
        }
        // If there are multiple active record date ranges for the record,
        // or the record range has changed, split it
        else if (rangesUnderRecord.size() > 1 ||
                !rangesUnderRecord.get(0).equals(record.getDateRange())) {
            return splitRecord(record, rangesUnderRecord);
        }
        // otherwise, check the record for inconsistencies and patch it if necessary
        else if (patchRecordData(record)) {
            return Collections.singletonList(record);
        }
        return Collections.emptyList();
    }

    /**
     * Generate a new time record for the given employee id spanning the given range
     */
    private TimeRecord createTimeRecord(int empId, Range<LocalDate> dateRange) {
        return new TimeRecord(
                empInfoService.getEmployee(empId, DateUtils.startOfDateRange(dateRange)),
                dateRange,
                payPeriodService.getPayPeriod(PayPeriodType.AF, DateUtils.startOfDateRange(dateRange))
        );
    }

    /**
     * Splits an existing time record according to the given date ranges
     * @param record TimeRecord
     * @param ranges List<Range<LocalDate>> - ranges corresponding to dates for which there should be distinct time records
     *               These ranges should all intersect with the existing time record
     * @return List<TimeRecord> - the records resulting from the split
     */
    private List<TimeRecord> splitRecord(TimeRecord record, List<Range<LocalDate>> ranges) {
        if (ranges.stream().anyMatch(range -> !RangeUtils.intersects(range, record.getDateRange()))) {
            throw new IllegalArgumentException("split ranges should all intersect with the record to be split");
        }

        Iterator<Range<LocalDate>> rangeIterator = ranges.iterator();
        List<TimeRecord> splitResult = new LinkedList<>();

        if (rangeIterator.hasNext()) {
            // Adjust the begin and end dates of the existing record to match the first range
            // patch the existing record + entries, ensuring correct supervisor and pay types
            record.setDateRange(rangeIterator.next());
            patchRecordData(record);

            // Prune any existing entries with dates outside of the first range,
            // saving them to be added to any appropriate new records that are created
            TreeMap<LocalDate, TimeEntry> existingEntryMap = new TreeMap<>();
            record.getTimeEntries().stream()
                    .map(TimeEntry::getDate)
                    .filter(date -> !record.getDateRange().contains(date))
                    .map(record::removeEntry)
                    .forEach(entry -> existingEntryMap.put(entry.getDate(), entry));

            splitResult.add(record);

            // Generate time records for the remaining ranges, adding the existing time records as appropriate
            rangeIterator.forEachRemaining(range -> {
                TimeRecord newRecord = createTimeRecord(record.getEmployeeId(), range);
                existingEntryMap.subMap(newRecord.getBeginDate(), true, newRecord.getEndDate(), true)
                        .values().forEach(newRecord::addTimeEntry);
                splitResult.add(newRecord);
            });
        }

        return splitResult;
    }

    /**
     * Verify that the given time record contains correct data
     * If not the record will be patched
     * @return true iff the record was patched
     */
    private boolean patchRecordData(TimeRecord record) {
        boolean modifiedRecord = false;
        Employee empInfo = empInfoService.getEmployee(record.getEmployeeId(), record.getBeginDate());
        if (!record.checkEmployeeInfo(empInfo)) {
            modifiedRecord = true;
            record.setEmpInfo(empInfo);
        }

        return patchEntries(record) || modifiedRecord;
    }

    /**
     * Verify the time entries of the given record, patching them if they have incorrect pay types
     * @return true if one or more entries were patched
     */
    private boolean patchEntries(TimeRecord record) {
        boolean modifiedEntries = false;
        // Get effective pay types for the record
        RangeMap<LocalDate, PayType> payTypes = getPayTypeRangeMap(record.getEmployeeId());
        // Get effective Accruing flag for the record
        RangeMap<LocalDate, Boolean> accrualStatuses = getAccrualRangeMap(record.getEmployeeId());

        // Check the pay types for each entry
        for (TimeEntry entry : record.getTimeEntries()) {
            PayType correctPayType = payTypes.get(entry.getDate());
            boolean correctAccruing = accrualStatuses.get(entry.getDate());
            if (!Objects.equals(entry.getPayType(), correctPayType)) {
                modifiedEntries = true;
                entry.setPayType(correctPayType);
            }
        }
        return modifiedEntries;
    }

    /**
     * Get a range map containing the effective pay types for all employed dates of the given employee
     */
    private RangeMap<LocalDate, PayType> getPayTypeRangeMap(int empId) {
        return RangeUtils.toRangeMap(
                transService.getTransHistory(empId).getEffectivePayTypes(Range.all()));
    }

    /**
     * Get a range map containing the effective pay types for all employed dates of the given employee
     */
    private RangeMap<LocalDate, Boolean> getAccrualRangeMap(int empId) {
        return RangeUtils.toRangeMap(
                transService.getTransHistory(empId).getEffectiveAccrualStatus(Range.all()));
    }


    /**
     * Get date ranges corresponding to active record dates over a collection of pay periods
     * Determined by pay periods, supervisor changes, and active dates of service
     */
    private LinkedHashSet<Range<LocalDate>> getRecordRanges(int empId, Collection<PayPeriod> periods,
                                                            Collection<AttendanceRecord> attendanceRecords) {
        TransactionHistory transHistory = transService.getTransHistory(empId);

        // Get dates when there was a change of supervisor
        Set<LocalDate> newSupDates = transHistory.getEffectiveSupervisorIds(DateUtils.ALL_DATES).keySet();

        // Get date ranges where the employee was required to enter time records
        RangeSet<LocalDate> timeEntryRequiredDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(transHistory.getEffectivePersonnelStatus(DateUtils.ALL_DATES))
                .asMapOfRanges().entrySet().stream()
                .filter(entry ->  entry.getValue().isTimeEntryRequired())
                .map(Map.Entry::getKey)
                .forEach(timeEntryRequiredDates::add);

        // Get active dates of service
        RangeSet<LocalDate> activeDates = empInfoService.getEmployeeActiveDatesService(empId);

        RangeSet<LocalDate> attendanceRecDates = TreeRangeSet.create();
        attendanceRecords.stream().map(AttendanceRecord::getDateRange).forEach(attendanceRecDates::add);

        return periods.stream()
                .sorted()
                .map(PayPeriod::getDateRange)
                // split any ranges that contain dates where there was a supervisor change
                .flatMap(periodRange -> RangeUtils.splitRange(periodRange, newSupDates).stream())
                // get the intersection of each range with the active dates of service
                .flatMap(range -> activeDates.subRangeSet(range).asRanges().stream())
                // Filter out ranges that are covered by already entered attendance periods
                .filter(range -> !attendanceRecDates.encloses(range))
                // Filter out ranges where the employee isn't required to enter time records
                .filter(range -> RangeUtils.intersects(timeEntryRequiredDates, range))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
