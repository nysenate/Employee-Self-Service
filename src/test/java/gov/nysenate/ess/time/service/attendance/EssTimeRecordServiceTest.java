package gov.nysenate.ess.time.service.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Category(SillyTest.class)
public class EssTimeRecordServiceTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordServiceTest.class);

    @Autowired private TimeRecordService timeRecordService;

    @Test
    public void testGetActiveRecords() throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<TimeRecord> timeRecords = timeRecordService.getTimeRecords(Collections.singleton(1719),
                Range.closed(LocalDate.of(2015, 1, 1), LocalDate.now()),
                EnumSet.allOf(TimeRecordStatus.class));
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetSupervisorRecordsTest() throws Exception {
        LocalDate now = LocalDate.now();
        Stopwatch sw = Stopwatch.createStarted();
        ListMultimap<Integer, TimeRecord> supRecords =
                timeRecordService.getSupervisorRecords(9896, Range.all());
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        supRecords.keySet().forEach(supId -> logger.info("empId {}: {} records", supId, supRecords.get(supId).size()));
    }

    @Test
    public void testGetSupervisorRecordsTest2() throws Exception {
        LocalDate now = LocalDate.now();
        Stopwatch sw = Stopwatch.createStarted();
        Set<TimeRecordStatus> statusSet = Sets.union(TimeRecordStatus.unlockedForEmployee(), TimeRecordStatus.unlockedForSupervisor());
        ListMultimap<Integer, TimeRecord> supRecords =
                timeRecordService.getSupervisorRecords(3117, Range.all(), statusSet);
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        supRecords.keySet().forEach(supId -> logger.info("empId {}: {} records", supId, supRecords.get(supId).size()));
    }

    @Test
    public void tempActiveRecordsTest() {
        int empId = 2868;
        List<TimeRecord> tRecs = timeRecordService.getActiveTimeRecords(empId);
        tRecs.forEach(record -> logger.info("{} - {}", record.getDateRange(), record.getTimeRecordId()));
    }

    @Test
    public void testSaveRecord() throws Exception {

    }

    @Test
    public void testGetActiveRecords1() throws Exception {

    }

    @Test
    public void testCreateEmptyTimeRecords() throws Exception {

    }

    @Test
    public void testSaveRecord1() throws Exception {

    }
}