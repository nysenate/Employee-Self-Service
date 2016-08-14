package gov.nysenate.ess.core.model.transaction;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.ProperTest;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.util.RangeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

@Category(ProperTest.class)
@WorkInProgress(author = "sam", since = "11/17/2015", desc = "not nearly done")
public class TransactionHistoryTests {

    private static final Iterator<Integer> empIdAssigner = RangeUtils.getCounter();
    private static final Iterator<Integer> changeIdAssigner = RangeUtils.getCounter();
    private static final Iterator<LocalDate> effectDateAssigner =
            RangeUtils.getDateRangeIterator(Range.atLeast(LocalDate.of(2014, 7, 12)));

    private static final int empId = empIdAssigner.next();

    private final TransactionRecord appRecord = new TransactionRecord();

    private static final int appChangeId = changeIdAssigner.next();
    private static final String appDocId = "APP_DOC_ID";
    private static final LocalDate appDate = effectDateAssigner.next();
    private static final int appSupId = empIdAssigner.next();
    private static final Map<String, String> appValMap = new HashMap<>();

    private final TransactionRecord supRecord = new TransactionRecord();
    private static final int supChangeId = changeIdAssigner.next();
    private static final String supDocId = "SUP_DOC_ID";
    private static final int supSupId = empIdAssigner.next();
    private static final LocalDate supDate = effectDateAssigner.next();
    private static final Map<String, String> supValMap = new HashMap<>();

    private final TransactionRecord empRecord = new TransactionRecord();
    private static final int empChangeId = changeIdAssigner.next();
    private static final String empDocId = "EMP_DOC_ID";
    private static final LocalDate empDate = effectDateAssigner.next();
    private static final Map<String, String> empValMap = new HashMap<>();

    @Before
    public void setUp() {
        initializeAppRecord();
        initializeEmpRecord();
        initializeSupRecord();
    }

    @Test
    public void hasRecordsTest() {
        TransactionHistory emptyHistory = new TransactionHistory(empId, Collections.emptyList());
        TransactionHistory nonEmptyHistory = new TransactionHistory(empId, Collections.singletonList(appRecord));
        assertFalse(emptyHistory.hasRecords());
        assertTrue(nonEmptyHistory.hasRecords());

        assertFalse(nonEmptyHistory.hasRecords(TransactionCode.ACC));
        assertTrue(nonEmptyHistory.hasRecords(appRecord.getTransCode()));
    }

    @Test
    public void getEffectiveSupIdsTest() {
        TransactionHistory transHistory = new TransactionHistory(empId, Arrays.asList(appRecord, supRecord, empRecord));

        TreeMap<LocalDate, Integer> justAppSups = transHistory.getEffectiveSupervisorIds(Range.lessThan(supDate));
        assertEquals("should only get 1 sup", 1, justAppSups.size());
        assertEquals(ImmutablePair.of(appDate, appSupId), justAppSups.firstEntry());

        TreeMap<LocalDate, Integer> justSupSups = transHistory.getEffectiveSupervisorIds(Range.open(appDate, empDate));
        assertEquals("should only get 1 sup", 1, justSupSups.size());
        assertEquals(ImmutablePair.of(supDate, supSupId), justSupSups.firstEntry());

        TreeMap<LocalDate, Integer> allSups = transHistory.getEffectiveSupervisorIds(Range.all());
        assertEquals("should get 2 sups", 2, allSups.size());
        assertEquals(ImmutablePair.of(appDate, appSupId), allSups.firstEntry());
        assertEquals(ImmutablePair.of(supDate, supSupId), allSups.lastEntry());
    }

    /** --- Test Value Initialization --- */

    private void initializeAppRecord() {
        appRecord.setEmployeeId(empId);
        appRecord.setTransCode(TransactionCode.APP);
        appRecord.setActive(true);
        appRecord.setChangeId(appChangeId);
        appRecord.setDocumentId(appDocId);
        appRecord.setEffectDate(appDate);
        appRecord.setOriginalDate(appDate.atStartOfDay().minusDays(2));
        appValMap.put("CDEMPSTATUS", "A");
        appValMap.put("NUXREFSV", String.valueOf(appSupId));
        appRecord.setValueMap(appValMap);
    }

    private void initializeEmpRecord() {
        empRecord.setEmployeeId(empId);
        empRecord.setTransCode(TransactionCode.EMP);
        empRecord.setActive(true);
        empRecord.setChangeId(empChangeId);
        empRecord.setDocumentId(empDocId);
        empRecord.setEffectDate(empDate);
        empRecord.setOriginalDate(empDate.atStartOfDay().minusDays(2));
        empValMap.put("CDEMPSTATUS", "I");
        empRecord.setValueMap(empValMap);
    }

    private void initializeSupRecord() {
        supRecord.setEmployeeId(empId);
        supRecord.setTransCode(TransactionCode.SUP);
        supRecord.setActive(true);
        supRecord.setChangeId(supChangeId);
        supRecord.setDocumentId(supDocId);
        supRecord.setEffectDate(supDate);
        supRecord.setOriginalDate(supDate.atStartOfDay().minusDays(2));
        supValMap.put("NUXREFSV", String.valueOf(supSupId));
        supRecord.setValueMap(supValMap);
    }
}
