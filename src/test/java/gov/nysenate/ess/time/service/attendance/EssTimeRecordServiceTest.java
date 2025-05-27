package gov.nysenate.ess.time.service.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
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
    @Autowired private SupervisorInfoService supervisorInfoService;

    @Test
    public void testGetActiveRecords() {
        Stopwatch sw = Stopwatch.createStarted();
        List<TimeRecord> timeRecords = timeRecordService.getTimeRecords(Collections.singleton(1719),
                Range.closed(LocalDate.of(2015, 1, 1), LocalDate.now()),
                EnumSet.allOf(TimeRecordStatus.class));
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetSupervisorRecordsTest() {
        int empId = 1024;
        // prime sup emp group cache
        supervisorInfoService.getSupervisorEmpGroup(empId, Range.all());
        Stopwatch sw = Stopwatch.createStarted();
        ListMultimap<Integer, TimeRecord> supRecords =
                timeRecordService.getActiveSupervisorRecords(empId, Range.all());
        logger.info("First retrieval {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        sw.reset().start();
        supRecords = timeRecordService.getActiveSupervisorRecords(empId, Range.all());
        logger.info("Second retrieval {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
//        supRecords.keySet().forEach(supId -> logger.info("empId {}: {} records", supId, supRecords.get(supId).size()));
    }

    @Test
    public void testGetSupervisorRecordsTest2() {
        Stopwatch sw = Stopwatch.createStarted();
        Set<TimeRecordStatus> statusSet = Sets.union(TimeRecordStatus.unlockedForEmployee(), TimeRecordStatus.unlockedForSupervisor());
        ListMultimap<Integer, TimeRecord> supRecords =
                timeRecordService.getActiveSupervisorRecords(3117, Range.all(), statusSet);
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
    public void activeRecordsSpeedTest() {
        final int empId = 11423;
        final int initialRetrievals = 5;
        final int bulkRetrievals = 100;
        logger.info("Active Records Speed Test");
        Stopwatch sw = Stopwatch.createUnstarted();
        //Record first few attempts
        logger.info("Testing {} initial retrievals...", initialRetrievals);
        for (int i = 0; i < initialRetrievals; i++) {
            logger.info("Retrieval #{}...", i);
            sw.reset().start();
            timeRecordService.getActiveTimeRecords(empId);
            logger.info("Retrieval #{}: {}us", i, sw.stop().elapsed(TimeUnit.MICROSECONDS));
        }
        logger.info("Testing {} bulk retrievals", bulkRetrievals);
        sw.reset().start();
        for (int i = 0; i < bulkRetrievals; i++) {
            timeRecordService.getActiveTimeRecords(empId);
        }
        long bulkTime = sw.stop().elapsed(TimeUnit.MICROSECONDS);
        logger.info("{} retrievals in {}us.  {}us/retrieval", bulkRetrievals, bulkTime, bulkTime / bulkRetrievals);
    }
}