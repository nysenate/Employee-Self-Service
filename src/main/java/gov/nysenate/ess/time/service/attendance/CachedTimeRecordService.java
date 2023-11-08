package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.cache.EmployeeIdCache;
import gov.nysenate.ess.core.service.period.HolidayService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.ShiroUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.attendance.TimeRecordAuditDao;
import gov.nysenate.ess.time.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.time.model.attendance.*;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import gov.nysenate.ess.time.model.personnel.SupervisorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidationService;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.attendance.TimeRecordAction.SUBMIT;
import static gov.nysenate.ess.time.model.attendance.TimeRecordStatus.APPROVED;
import static java.util.stream.Collectors.toList;

@WorkInProgress(author = "Ash", since = "2015/09/11", desc = "Reworking methods in the class, adding caching")
public class CachedTimeRecordService
        extends EmployeeIdCache<CachedTimeRecordService.TimeRecordCacheCollection>
        implements TimeRecordService {
    private static final Logger logger = LoggerFactory.getLogger(CachedTimeRecordService.class);

    private final TimeRecordDao timeRecordDao;
    private final TimeRecordAuditDao auditDao;
    private final EmpTransactionService transService;
    private final SupervisorInfoService supervisorInfoService;
    private final HolidayService holidayService;
    private final TimeRecordValidationService trValidationService;
    private final EventBus eventBus;

    public CachedTimeRecordService(TimeRecordDao timeRecordDao, TimeRecordAuditDao auditDao,
                                   EmpTransactionService transService, SupervisorInfoService supervisorInfoService,
                                   HolidayService holidayService, TimeRecordValidationService trValidationService, EventBus eventBus) {
        this.timeRecordDao = timeRecordDao;
        this.auditDao = auditDao;
        this.transService = transService;
        this.supervisorInfoService = supervisorInfoService;
        this.holidayService = holidayService;
        this.trValidationService = trValidationService;
        this.eventBus = eventBus;
    }

    private LocalDateTime lastUpdateTime;

    @PostConstruct
    public void init() {
        lastUpdateTime = timeRecordDao.getLatestUpdateTime();
    }

    /** Helper class to store a collection of time records in a cache. */
    protected static class TimeRecordCacheCollection {
        private final int empId;
        private final Map<BigInteger, TimeRecord> cachedTimeRecords = new LinkedHashMap<>();

        public TimeRecordCacheCollection(int empId, Collection<TimeRecord> cachedTimeRecords) {
            this.empId = empId;
            cachedTimeRecords.forEach(this::update);
        }

        public int getEmpId() {
            return empId;
        }

        public List<TimeRecord> getTimeRecords() {
            // Return a copy of each time record
            return cachedTimeRecords.values().stream()
                    .map(TimeRecord::new)
                    .collect(toList());
        }

        public void update(TimeRecord record) {
            if (record.getTimeRecordId() == null) {
                throw new IllegalArgumentException("Attempt to insert time record with null id into cache");
            }
            cachedTimeRecords.put(record.getTimeRecordId(), record);
        }

        public void remove(BigInteger timeRecId) {
            cachedTimeRecords.remove(timeRecId);
        }
    }

    // --- TimeRecordService Implementation ---

    /** {@inheritDoc} */
    @Override
    public TimeRecord getTimeRecord(BigInteger timeRecordId) throws TimeRecordNotFoundException {
        try {
            TimeRecord timeRecord = timeRecordDao.getTimeRecord(timeRecordId);
            initializeEntries(timeRecord);
            return timeRecord;
        } catch (EmptyResultDataAccessException ex) {
            throw new TimeRecordNotFoundException(timeRecordId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Integer> getTimeRecordYears(Integer empId, SortOrder yearOrder) {
        return timeRecordDao.getTimeRecordYears(empId, yearOrder);
    }

    /** {@inheritDoc}
     *
     * The active time records for an employee will be cached.
     */
    @Override
    public List<TimeRecord> getActiveTimeRecords(Integer empId) {
        TimeRecordCacheCollection cachedRecs = cache.get(empId);
        if (cachedRecs == null) {
            List<TimeRecord> records = timeRecordDao.getActiveRecords(empId);
            cachedRecs = new TimeRecordCacheCollection(empId, records);
            cache.put(empId, cachedRecs);
        }
        // Initialize time entries before returning records
        return cachedRecs.getTimeRecords().stream()
                .peek(this::initializeEntries)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                           Set<TimeRecordStatus> statuses) {
        TreeMultimap<PayPeriod, TimeRecord> records = TreeMultimap.create();
        timeRecordDao.getRecordsDuring(empIds, dateRange, EnumSet.allOf(TimeRecordStatus.class)).values()
                .forEach(rec -> records.put(rec.getPayPeriod(), rec));
        return records.values().stream()
                .filter(record -> statuses.contains(record.getRecordStatus()))
                .peek(this::initializeEntries)
                .collect(toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getTimeRecords(Set<Integer> empIds, Collection<PayPeriod> payPeriods,
                                           Set<TimeRecordStatus> statuses) {
        RangeSet<LocalDate> dateRanges = TreeRangeSet.create();
        payPeriods.forEach(period -> dateRanges.add(period.getDateRange()));
        if (dateRanges.isEmpty()) {
            return Collections.emptyList();
        }
        return getTimeRecords(empIds, dateRanges.span(), statuses).stream()
                .filter(record -> dateRanges.encloses(record.getDateRange()))
                .collect(toList());
    }

    /** {@inheritDoc} */
    @Override
    public Multimap<Integer, TimeRecord> getTimeRecords(Multimap<Integer, LocalDate> empIdBeginDateMap)
            throws TimeRecordNotFoundEidBeginDateEx {

        // Establish a date range that encloses all given dates
        RangeSet<LocalDate> recordDateRangeSet = TreeRangeSet.create();
        empIdBeginDateMap.values().stream()
                .map(Range::singleton)
                .forEach(recordDateRangeSet::add);
        Range<LocalDate> overallRecordRange = recordDateRangeSet.span();

        // Get time records over those dates
        List<TimeRecord> timeRecords = getTimeRecords(
                empIdBeginDateMap.keySet(), overallRecordRange, TimeRecordStatus.getAll());

        Set<Map.Entry<Integer, LocalDate>> foundDates = new HashSet<>();

        // Convert retrieved time records into a multimap
        HashMultimap<Integer, TimeRecord> timeRecordMultimap = HashMultimap.create();
        timeRecords.stream()
                .peek(tRec -> foundDates.add(ImmutablePair.of(tRec.getEmployeeId(), tRec.getBeginDate())))
                .filter(tRec -> empIdBeginDateMap.containsKey(tRec.getEmployeeId()) &&
                        empIdBeginDateMap.get(tRec.getEmployeeId()).contains(tRec.getBeginDate()))
                .forEach(timeRecord -> timeRecordMultimap.put(timeRecord.getEmployeeId(), timeRecord));

        // Check to make sure that all requested records were found
        empIdBeginDateMap.entries().forEach(entry -> {
            if (!foundDates.contains(entry)) {
                throw new TimeRecordNotFoundEidBeginDateEx(entry.getKey(), entry.getValue());
            }
        });

        return timeRecordMultimap;
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getTimeRecordsWithSupervisor(Integer empId, Integer supId, Range<LocalDate> dateRange) {
        List<TimeRecord> timeRecords = getTimeRecords(Collections.singleton(empId), dateRange, TimeRecordStatus.getAll());
        return timeRecords.stream().filter(t -> t.getSupervisorId().equals(supId)).collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public ListMultimap<Integer, TimeRecord> getActiveSupervisorRecords(int supId, Range<LocalDate> dateRange,
                                                                        Set<TimeRecordStatus> statuses)
            throws SupervisorException {

        SupervisorEmpGroup empGroup = supervisorInfoService.getSupervisorEmpGroup(supId, dateRange);
        ListMultimap<Integer, TimeRecord> records = ArrayListMultimap.create();
        empGroup.getDirectEmpIds().stream()
                .map(this::getActiveTimeRecords)
                .flatMap(Collection::stream)
                .filter(tr -> statuses.contains(tr.getRecordStatus()))
                .filter(tr -> empGroup.hasEmployeeDuringRange(tr.getEmployeeId(), tr.getDateRange()))
                .forEach(tr -> records.put(tr.getEmployeeId(), tr));
        return records;
    }

    @Override
    public boolean hasActiveEmployeeRecord(int supId) {
        return timeRecordDao.hasActiveEmployeeRecord(supId);
    }

    @Override
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public synchronized boolean saveRecord(TimeRecord record) {
        boolean updated = timeRecordDao.saveRecord(record);
        if (updated) {
            updateCache(record);
        }
        return updated;
    }

    @Override
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public boolean saveRecord(final TimeRecord record, final TimeRecordAction action) {
        trValidationService.validateTimeRecord(record, action);
        // Set resulting status according to action
        TimeRecordStatus initialStatus = record.getRecordStatus();
        TimeRecordStatus nextStatus = initialStatus.getResultingStatus(action);
        record.setRecordStatus(nextStatus);

        // Set update user fields for time record and entries based on authenticated user
        String updateUser = Optional.ofNullable(ShiroUtils.getAuthenticatedUid())
                .orElse("TS_OWNER")
                .toUpperCase();
        record.setOverallUpdateUser(updateUser);

        // If record is being approved, then we need to set the Approval Xref#
        if (nextStatus == APPROVED && initialStatus != APPROVED) {
            record.setApprovalEmpId(ShiroUtils.getAuthenticatedEmpId());
        }

        int empId = record.getEmployeeId();

        try {
            boolean saved = saveRecord(record);
            if (saved) {
                // If the record was in the employee scope and was submitted,
                // deactivate any active temporary time records that came before it
                if (initialStatus.isUnlockedForEmployee() && action == SUBMIT) {
                    deactivatePreviousTERecs(record);
                }
                // Generate an audit record for the time record if a significant action was made on the time record.
                if (action != TimeRecordAction.SAVE) {
                    auditDao.auditTimeRecord(record.getTimeRecordId());
                }
                eventBus.post(new TimeRecordActionEvent(record, action));
            }
            return saved;
        } catch (Exception ex) {
            // If anything goes wrong, attempt to clear the employee's record cache.
            logger.warn("Clearing time record cache for emp:{} due to time record save error.", empId);
            cache.remove(empId);
            throw ex;
        }
    }

    @Override
    public boolean deleteRecord(BigInteger timeRecordId) {
        return timeRecordDao.deleteRecord(timeRecordId);
    }

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.ACTIVE_TIME_RECORDS;
    }

    @Override
    protected void putId(int id) {
        getActiveTimeRecords(id);
    }

    /**
     * Check for updated time records in the data store
     * If time records are updated, update the cache
     */
    @Scheduled(fixedDelayString = "${cache.poll.delay.timerecords:60000}")
    @Override
    public void syncTimeRecords() {
        logger.info("Checking for time record updates since {}", lastUpdateTime);
        Range<LocalDateTime> updateRange = Range.openClosed(lastUpdateTime, LocalDateTime.now());
        List<TimeRecord> updatedTRecs = timeRecordDao.getUpdatedRecords(updateRange);
        lastUpdateTime = updatedTRecs.stream()
                .peek(this::updateCache)
                .map(TimeRecord::getOverallUpdateDate)
                .max(LocalDateTime::compareTo)
                .orElse(lastUpdateTime);
        logger.info("Refreshed cache with {} updated time records", updatedTRecs.size());
    }

    /* --- Internal Methods --- */

    /**
     * Updates the active time record cache with the given record
     * If the record is active and in progress, it is added/updated, otherwise it is removed
     * @param record TimeRecord
     */
    private void updateCache(TimeRecord record) {
        TimeRecordCacheCollection cachedRecs = cache.get(record.getEmployeeId());
        if (cachedRecs == null) {
            return;
        }
        if (record.isActive() && TimeRecordStatus.inProgress().contains(record.getRecordStatus())) {
            initializeEntries(record);
            cachedRecs.update(record);
        } else {
            cachedRecs.remove(record.getTimeRecordId());
        }
    }

    /**
     * Deactivates all active records that meet the following criteria:
     *  - The record ends before the given record
     *  - The record is in the employee scope
     *  - The record contains ONLY temporary pay entries
     *
     * @param record {@link TimeRecord}
     */
    private void deactivatePreviousTERecs(TimeRecord record) {
        getActiveTimeRecords(record.getEmployeeId()).stream()
                .filter(otherRec -> otherRec.getBeginDate().isBefore(record.getBeginDate()))
                .filter(otherRec -> otherRec.getRecordStatus().getScope() == TimeRecordScope.EMPLOYEE)
                .filter(otherRec -> Objects.equals(otherRec.getPayTypes(), Collections.singleton(PayType.TE)))
                .peek(otherRec -> otherRec.setActive(false))
                .forEach(this::saveRecord);
    }

    /**
     * Ensures that the given time record contains entries for each day covered.
     * @param timeRecord - TimeRecord
     */
    private void initializeEntries(TimeRecord timeRecord) {
        RangeMap<LocalDate, PayType> payTypeMap = null;

        for (LocalDate entryDate = timeRecord.getBeginDate(); !entryDate.isAfter(timeRecord.getEndDate());
             entryDate = entryDate.plusDays(1)) {
            boolean newEntry = false;
            if (!timeRecord.containsEntry(entryDate)) {
                if (payTypeMap == null) {
                    TransactionHistory transHistory = transService.getTransHistory(timeRecord.getEmployeeId());
                    payTypeMap = RangeUtils.toRangeMap(
                            transHistory.getEffectivePayTypes(timeRecord.getDateRange()), timeRecord.getEndDate());
                }
                timeRecord.addTimeEntry(new TimeEntry(timeRecord, payTypeMap.get(entryDate), entryDate));
                newEntry = true;
            }
            TimeEntry entry = timeRecord.getEntry(entryDate);
            if (entry.getPayType() != PayType.TE) {
                // Set holiday hours if applicable
                Optional<Holiday> holiday = holidayService.getActiveHoliday(entryDate);
                if (holiday.isPresent()) {
                    if (entry.getPayType() == PayType.RA && newEntry) {
                        entry.setHolidayHours(holiday.get().getHours());
                    }
                    else if (entry.getHolidayHours().isEmpty() && newEntry) {
                        entry.setHolidayHours(BigDecimal.ZERO);
                    }
                }
            }
        }
    }
}
