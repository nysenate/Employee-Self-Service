package gov.nysenate.ess.core.model.transaction;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.payroll.SalaryRec;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The TransactionHistory maintains an ordered collection of TransactionRecords. This class is intended to be
 * used in methods that need to know about the history of a specific TransactionCode for an employee.
 */
public class TransactionHistory
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistory.class);

    /** A string assigned to the transactions that are used to create an initial snapshot for employees
     * appointed before the SFMS system was put in place (employees without APP or RPT transactions.)
     * This is not an official document identifier, it is only used in this context
     */
    private static final String PRE_SFMS_APP_DOC_ID = "PRE_SFMS_APP";

    /** The employee id that this history refers to. */
    protected int employeeId;

    /** Records the code of the original transaction (in case it's overwritten as APP). */
    protected TransactionCode originalFirstCode;

    protected TreeMultimap<String, TransactionRecord> appointDocuments = TreeMultimap.create();

    /** A collection of TransactionRecords grouped via the TransactionCode. */
    protected LinkedListMultimap<TransactionCode, TransactionRecord> recordsByCode;

    /** Creates a full snapshot view from the records containing every column and grouped by effective date. */
    protected TreeMap<LocalDate, Map<String, String>> recordSnapshots;

    /** --- Constructors --- */

    public TransactionHistory(int empId, List<TransactionRecord> recordsList) {
        this(empId, null, recordsList);
    }

    public TransactionHistory(int empId, TransactionCode originalFirstCode, List<TransactionRecord> recordsList) {
        this.employeeId = empId;
        this.originalFirstCode = originalFirstCode;

        // Sort the input record list from earliest first. (just to be safe)
        List<TransactionRecord> sortedRecs = new ArrayList<>(recordsList);
        sortedRecs.sort(new TransDateAscending());

        // Initialize the data structures
        this.recordsByCode = LinkedListMultimap.create();
        this.recordSnapshots = new TreeMap<>();

        // Store the records
        this.addTransactionRecords(recordsList);
    }

    /**
     * Returns true if any records exist in the history.
     *
     * @return boolean
     */
    public boolean hasRecords() {
        return !recordsByCode.isEmpty();
    }

    /**
     * Returns true if records exist for a given transaction code.
     *
     * @param code TransactionCode
     * @return boolean
     */
    public boolean hasRecords(TransactionCode code) {
        return !recordsByCode.get(code).isEmpty();
    }

    /** --- Helper Methods --- */

    public TreeMap<LocalDate, Integer> getEffectiveSupervisorIds(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, Integer> supIds = new TreeMap<>();
        getEffectiveEntriesDuring("NUXREFSV", dateRange, true).forEach((k,v) -> supIds.put(k, Integer.parseInt(v)));
        return supIds;
    }

    public TreeMap<LocalDate, PayType> getEffectivePayTypes(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, PayType> payTypes = new TreeMap<>();
        getEffectiveEntriesDuring("CDPAYTYPE", dateRange, true).forEach((k,v) -> payTypes.put(k, PayType.valueOf(v)));
        return payTypes;
    }

    public TreeMap<LocalDate, Boolean> getEffectiveEmpStatus(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, Boolean> empStatuses = new TreeMap<>();
        getUniqueEntriesDuring(Sets.newHashSet("CDEMPSTATUS", "CDSTATPER"), dateRange, true)
                .rowMap().forEach((date, vals) -> {
            if (StringUtils.equals(vals.get("CDEMPSTATUS"), "A")) {
                empStatuses.put(date, true);
            } else if (StringUtils.equals(vals.get("CDEMPSTATUS"), "I")) {
                if (StringUtils.equals(vals.get("CDSTATPER"), "RETD")) {
                    empStatuses.put(date, false);
                } else {
                    empStatuses.put(date.plusDays(1), false);
                }
            }
        });
        return empStatuses;
    }

    public TreeMap<LocalDate, BigDecimal> getEffectiveMinHours(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, BigDecimal> minHrs = new TreeMap<>();
        getEffectiveEntriesDuring("NUMINTOTHRS", dateRange, true).forEach((k,v) -> minHrs.put(k, new BigDecimal(v)));
        return minHrs;
    }

    public TreeMap<LocalDate, Boolean> getEffectiveAccrualStatus(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, Boolean> accrual = new TreeMap<>();
        getEffectiveEntriesDuring("CDACCRUE", dateRange, true).forEach((k, v) -> accrual.put(k, v.equals("Y")));
        return accrual;
    }

    public TreeMap<LocalDate, BigDecimal> getEffectiveAllowances(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, BigDecimal> allowances = new TreeMap<>();
        getEffectiveEntriesDuring("MOAMTEXCEED", dateRange, true)
                .forEach((date, allowance) -> allowances.put(date, new BigDecimal(allowance)));
        return allowances;
    }

    public TreeMap<LocalDate, SalaryRec> getEffectiveSalaryRecs(Range<LocalDate> dateRange) {
        TreeBasedTable<LocalDate, String, String> effectiveSalEntries =
                getUniqueEntriesDuring(Sets.newHashSet("MOSALBIWKLY", "CDPAYTYPE"), dateRange, true);
        TreeMap<LocalDate, SalaryRec> salaryRecs = new TreeMap<>();
        SalaryRec lastRec = null;
        for (LocalDate effectDate : effectiveSalEntries.rowKeySet()) {
            if (lastRec != null) {
                lastRec.setEndDate(effectDate.minusDays(1));
            }
            lastRec = new SalaryRec(
                    new BigDecimal(effectiveSalEntries.get(effectDate, "MOSALBIWKLY")),
                    PayType.valueOf(effectiveSalEntries.get(effectDate, "CDPAYTYPE")),
                    effectDate);
            salaryRecs.put(lastRec.getEffectDate(), lastRec);
        }
        if (lastRec != null) {
            lastRec.setEndDate(DateUtils.endOfDateRange(dateRange));
        }
        return salaryRecs;
    }

    /**
     * Get the effective personnel status for the employee over the given date range
     * @param dateRange Range<LocalDate>
     * @return TreeMap<LocalDate, PersonnelStatus>
     */
    public TreeMap<LocalDate, PersonnelStatus> getEffectivePersonnelStatus(Range<LocalDate> dateRange) {
        TreeMap<LocalDate, PersonnelStatus> effectivePersonnelStatusMap = new TreeMap<>();
        getEffectiveEntriesDuring("CDSTATPER", dateRange, true)
                .forEach((date, statper) -> effectivePersonnelStatusMap.put(date, PersonnelStatus.valueOf(statper)));
        return effectivePersonnelStatusMap;
    }

    /**
     * @return true if the employee is not in the middle of an appoint transaction
     *      ie. they have received a PER and a PAY transaction for the last appointment
     */
    public boolean isFullyAppointed() {
        Range<LocalDate> testRange = Range.closed(LocalDate.now().minusDays(1), LocalDate.now());
        // Return true if the employee is not currently appointed
        if (getEffectiveEmpStatus(testRange).isEmpty() || !getEffectiveEmpStatus(testRange).lastEntry().getValue()) {
            return true;
        }
        String latestAppointDoc = null;
        LocalDate latestAppDocDate = LocalDate.MIN;
        for (String appointDoc : appointDocuments.keySet()) {
            TransactionRecord firstRecord = appointDocuments.get(appointDoc).first();
            if (firstRecord.getEffectDate().isAfter(latestAppDocDate)) {
                latestAppointDoc = appointDoc;
                latestAppDocDate = firstRecord.getEffectDate();
            }
        }
        return latestAppointDoc != null &&
                // the latest appoint document has records of both transaction types
                appointDocuments.get(latestAppointDoc).stream()
                        .map(TransactionRecord::getTransType)
                        .distinct()
                        .count() > 1;
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Adds a transaction record to the history queue.
     *
     * @param record TransactionRecord
     */
    private void addTransactionRecord(TransactionRecord record) {
        if (record != null) {
            recordsByCode.put(record.getTransCode(), record);
            LocalDate effectDate = record.getEffectDate();
            if (recordSnapshots.isEmpty()) {
                // Initialize the snapshot map
                recordSnapshots.put(effectDate, record.getValueMap());
                // Add the first record as an appoint transaction if no proper appoint transactions were found
                if (appointDocuments.isEmpty()) {
                    appointDocuments.put(PRE_SFMS_APP_DOC_ID, record);
                }
            } else {
                // Update the previous map with the newly updated values
                Map<String, String> valueMap = Maps.newHashMap(recordSnapshots.lastEntry().getValue());
                // If the transaction record belongs to an appoint document, and has a transaction type different from the appoint transaction
                //  extract the values of all columns of the record's transaction type as effective values
                if (appointDocuments.containsKey(PRE_SFMS_APP_DOC_ID) &&
                        appointDocuments.get(PRE_SFMS_APP_DOC_ID).size() == 1 &&
                        appointDocuments.get(PRE_SFMS_APP_DOC_ID).first().getTransType() != record.getTransType()) {
                    valueMap.putAll(record.getValuesForCols(TransactionCode.getTypeDbColumnsList(record.getTransType())));
                    appointDocuments.put(PRE_SFMS_APP_DOC_ID, record);
                } else if (appointDocuments.keySet().contains(record.getDocumentId()) &&
                        appointDocuments.get(record.getDocumentId()).first().getTransType() != record.getTransType()) {
                    valueMap.putAll(record.getValuesForCols(TransactionCode.getTypeDbColumnsList(record.getTransType())));
                    appointDocuments.put(record.getDocumentId(), record);
                } else {
                    valueMap.putAll(record.getValuesForCode());
                }
                recordSnapshots.put(effectDate, valueMap);
            }
        }
        else {
            throw new IllegalArgumentException("Cannot add a null record to the transaction history!");
        }
    }

    /**
     * Adds a collection of transaction records to their respective history queue.
     *
     * @param recordsList List<TransactionRecord>
     */
    private void addTransactionRecords(List<TransactionRecord> recordsList) {
        getInitDocIds(recordsList);
        recordsList.forEach(this::addTransactionRecord);
    }

    /**
     * Get the document numbers of any initializing transactions (APP, RTP) in the given list
     *   and add them to the appoint records map
     * @param recordsList List<TransactionRecord>
     */
    private void getInitDocIds(List<TransactionRecord> recordsList) {
        recordsList.stream()
                .filter(record -> record.getTransCode().isAppointType())
                .forEach(record -> appointDocuments.put(record.getDocumentId(), record));
    }

    /**
     * See overloaded method.
     *
     * @see #getTransRecords(Range, Set, SortOrder)
     * @param code TransactionCode
     * @param dateSort SortOrder
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(TransactionCode code, SortOrder dateSort) {
        return getTransRecords(Range.all(), Sets.newHashSet(code), dateSort);
    }

    /**
     * Returns a single ordered LinkedList containing a set of transaction records. This is useful if
     * you need a subset of the transaction records to be ordered into a single collection.
     *
     * @param transCodes Set<TransactionCode> - The set of transaction codes to return in the list.
     * @param dateSort SortOrder - Sort order based on the effective date
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getTransRecords(Range<LocalDate> effectDateRange,
                                                         Set<TransactionCode> transCodes, SortOrder dateSort) {
        LinkedList<TransactionRecord> sortedRecList = new LinkedList<>();
        getRecordsByCode().values().stream()
            .filter(r -> transCodes.contains(r.getTransCode()) && effectDateRange.contains(r.getEffectDate()))
            .forEach(sortedRecList::add);
        sortedRecList.sort((dateSort.equals(SortOrder.ASC)) ? new TransDateAscending() : new TransDateDescending());
        return sortedRecList;
    }

    /**
     * Shorthand method to retrieve every available transaction record.
     *
     * @see #getTransRecords(Range, Set, SortOrder)
     * @param dateOrder SortOrder - Sort order based on the effective date
     * @return LinkedList<TransactionRecord>
     */
    public LinkedList<TransactionRecord> getAllTransRecords(SortOrder dateOrder) {
        return getTransRecords(Range.all(), recordsByCode.keySet(), dateOrder);
    }

    /**
     * Gets the effective values for a set of columns on each date that one of the columns changed
     * @param keys Set<String> - a set of column names
     * @param dateRange Range<LocalDate> - range of dates in which effective values will be queried
     * @param skipNulls boolean - will only include value sets where all values are non-null if set to true
     * @return TreeBasedTable<LocalDate, String, String> - Effective date -> Column name -> Column value on date
     */
    public TreeBasedTable<LocalDate, String, String> getUniqueEntriesDuring(Set<String> keys,
                                                                            Range<LocalDate> dateRange,
                                                                            boolean skipNulls) {
        // Get the effective entries for each value converted into range maps
        Map<String, RangeMap<LocalDate, String>> entryRangeMaps = keys.stream()
                .map(key -> ImmutablePair.of(key,
                        RangeUtils.toRangeMap(
                                getEffectiveEntriesDuring(key, dateRange, skipNulls), DateUtils.THE_FUTURE)))
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));

        // Get a set of all dates where one of the values changed
        Set<LocalDate> changeDates = entryRangeMaps.values().stream()
                .flatMap(entryRangeMap -> entryRangeMap.asMapOfRanges().keySet().stream())
                .map(DateUtils::startOfDateRange)
                .collect(Collectors.toSet());

        TreeBasedTable<LocalDate, String, String> uniqueEntries = TreeBasedTable.create();
        // Get the effective entry of each key for each change date
        changeDates.stream()
                // if skip nulls is set, filter out entries where one or more keys are null
                .filter(date -> !skipNulls ||
                        entryRangeMaps.values().stream().allMatch(rangeMap -> rangeMap.get(date) != null))
                .flatMap(date -> entryRangeMaps.entrySet().stream()
                        .map(entry -> ImmutableTriple.of(date, entry.getKey(), entry.getValue().get(date))))
                .forEach(triple -> uniqueEntries.put(triple.getLeft(), triple.getMiddle(), triple.getRight()));
        return uniqueEntries;
    }

    /**
     * Obtains a mapping of LocalDate -> String for the values associated with the given key during the specified
     * date range. For example given the key 'NUXREFSV', a map will be returned containing the value of that field
     * before/on the start of the date range, and any modifications of that value up until the end of the date range.
     *
     * @param key String - The audit record column name
     * @param dateRange LocalDate - The date range for the values to be effective during.
     * @param skipNulls boolean - If true, null values will be excluded from the map.
     * @return TreeMap<LocalDate, String>
     */
    public TreeMap<LocalDate, String> getEffectiveEntriesDuring(String key, Range<LocalDate> dateRange, boolean skipNulls) {
        TreeMap<LocalDate, String> values = Maps.newTreeMap();
        String lastValue = null;
        Optional<ImmutablePair<LocalDate, String>> firstEntry = getLatestEntryOf(key, DateUtils.startOfDateRange(dateRange), skipNulls);
        if (firstEntry.isPresent()) {
            values.put(firstEntry.get().getLeft(), firstEntry.get().getRight());
            lastValue = firstEntry.get().getRight();
        }
        NavigableMap<LocalDate, Map<String, String>> subMap =
            recordSnapshots.subMap(DateUtils.startOfDateRange(dateRange), false, DateUtils.endOfDateRange(dateRange), true);
        for (Map.Entry<LocalDate, Map<String, String>> entry : subMap.entrySet()) {
            String currValue = entry.getValue().get(key);
            if ((!skipNulls || currValue != null) && !StringUtils.equals(lastValue, currValue)) {
                values.put(entry.getKey(), currValue);
                lastValue = currValue;
            }
        }
        return values;
    }

    public Optional<ImmutablePair<LocalDate, String>> getLatestEntryOf(String key, LocalDate latestDate, boolean skipNulls) {
        return this.recordSnapshots.headMap(latestDate, true)
            .descendingMap().entrySet().stream()
            .filter(e -> (!skipNulls || e.getValue().get(key) != null))       // Skip null values if requested
            .map(e -> new ImmutablePair<>(e.getKey(), e.getValue().get(key)))
            .findFirst();                                                     // Return most recent one
    }

    /**
     * @see #latestValueOf(String, boolean)
     * 'latestDate' defaults to a date far in the future.
     */
    public Optional<String> latestValueOf(String key, boolean skipNulls) {
        return latestValueOf(key, LocalDate.MAX, skipNulls);
    }

    /**
     * Use this method if you want to know the value of the given 'key' where the effective date is the latest
     * to occur before or on 'latestDate'. In the case where multiple transaction records were in effect for
     * the same latest effective date, the value belonging to the most recent record with that effective date
     * will be used.
     *
     * @param key String - A key from the values map (e.g. 'NALAST')
     * @param latestDate LocalDate - The latest date to search until.
     * @param skipNulls boolean - Set to true if you want to find the latest non-null value.
     * @return Optional<String> - If the value is found, it will be set, otherwise an empty Optional is returned.
     */
    public Optional<String> latestValueOf(String key, LocalDate latestDate, boolean skipNulls) {
        Optional<ImmutablePair<LocalDate, String>> entry = getLatestEntryOf(key, latestDate, skipNulls);
        if (entry.isPresent()) {
            return Optional.ofNullable(entry.get().getValue());
        }
        return Optional.empty();
    }

    /**
     * @see #getEarliestValueOf(String, LocalDate, boolean)
     * 'earliestDate' defaults to a date way in the past.
     */
    public Optional<String> getEarliestValueOf(String key, boolean skipNulls) {
       return getEarliestValueOf(key, LocalDate.ofYearDay(1, 1), skipNulls);
    }

    /**
     * Use this method if you want to know the value of the given 'key' where the effective date is the first
     * to occur after or on 'earliestDate'. In the case where multiple transaction records were in effect for
     * the same earliest effective date, the value belonging to the most recent record with that effective date
     * will be used.
     *
     * @param key String - A key from the value map (e.g. 'NALAST').
     * @param earliestDate LocalDate - The earliest date for which to search from.
     * @param skipNulls boolean - Set to true if you want to find the earliest non-null value.
     * @return Optional<String> - If the value is found, it will be set, otherwise an empty Optional is returned.
     */
    public Optional<String> getEarliestValueOf(String key, LocalDate earliestDate, boolean skipNulls) {
        return this.recordSnapshots.tailMap(earliestDate, true)
            .entrySet().stream()
            .filter(e -> (!skipNulls || e.getValue().get(key) != null)) // Skip null values if requested
            .map(e -> e.getValue().get(key))                            // Extract the value for the given 'key'
            .findFirst();                                               // Return most recent one
    }

    /**
     * Get an immutable copy of the record multimap stored in this transaction history.
     *
     * @return ImmutableMultimap<TransactionCode, TransactionRecord>
     */
    public ImmutableMultimap<TransactionCode, TransactionRecord> getRecordsByCode() {
        return ImmutableMultimap.copyOf(recordsByCode);
    }

    /**
     * Get a chronologically ordered immutable list containing all records with the given code in the transaction history
     * @param code TransactionCode
     * @return ImmutableList<TransactionRecord>
     */
    public ImmutableList<TransactionRecord> getRecords(TransactionCode code) {
        List<TransactionRecord> records = recordsByCode.get(code);
        return records != null ? ImmutableList.copyOf(records) : ImmutableList.of();
    }

    /**
     * Get an immutable copy of the record snapshot map stored in this transaction history.
     *
     * @return ImmutableMap<LocalDate, Map<String, String>>
     */
    public ImmutableSortedMap<LocalDate, Map<String, String>> getRecordSnapshots() {
        return ImmutableSortedMap.copyOf(recordSnapshots);
    }

    /** --- Local classes --- */

    /** Sort by earliest (effective date, origin date) first. */
    protected static class TransDateAscending implements Comparator<TransactionRecord>
    {
        @Override
        public int compare(TransactionRecord o1, TransactionRecord o2) {
            return ComparisonChain.start()
                .compare(o1.getEffectDate(), o2.getEffectDate())
                .compare(o1.getOriginalDate(), o2.getOriginalDate())
                .compare(o1.getAuditDate(), o2.getAuditDate())
                .result();
        }
    }

    /** Sort by most recent (effective date, origin date) first. */
    protected static class TransDateDescending implements Comparator<TransactionRecord>
    {
        @Override
        public int compare(TransactionRecord o1, TransactionRecord o2) {
            return ComparisonChain.start()
                .compare(o2.getEffectDate(), o1.getEffectDate())
                .compare(o2.getOriginalDate(), o1.getOriginalDate())
                .compare(o2.getAuditDate(), o1.getAuditDate())
                .result();
        }
    }

    /** --- Basic Getters/Setters --- */

    public int getEmployeeId() {
        return employeeId;
    }

    public TransactionCode getOriginalFirstCode() {
        return originalFirstCode;
    }
}