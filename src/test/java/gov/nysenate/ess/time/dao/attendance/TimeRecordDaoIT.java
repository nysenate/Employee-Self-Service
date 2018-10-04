package gov.nysenate.ess.time.dao.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static gov.nysenate.ess.core.config.DatabaseConfig.remoteTxManager;
import static gov.nysenate.ess.time.model.attendance.TimeRecordStatus.APPROVED_PERSONNEL;

@Category(IntegrationTest.class)
@Transactional(value = remoteTxManager)
public class TimeRecordDaoIT extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordDaoIT.class);

    @Autowired private TimeRecordDao timeRecordDao;
    @Autowired private EmployeeDao employeeDao;

    private TimeRecord testRecord;
    private static final LocalDate trStartDate = LocalDate.of(1990, 8, 2);
    private static final LocalDate trEndDate = LocalDate.of(1990, 8, 15);

    private void initTestRecord() {
        testRecord = new TimeRecord();

        testRecord.setEmployeeId(11423);
        testRecord.setLastUser("STOUFFER");
        testRecord.setActive(true);
        testRecord.setRecordStatus(TimeRecordStatus.SUBMITTED);
        testRecord.setRemarks("Hello world");
        testRecord.setSupervisorId(9896);
        testRecord.setBeginDate(trStartDate);
        testRecord.setEndDate(trEndDate);

        testRecord.setRespHeadCode("STSBAC");
        testRecord.setPayPeriod(new PayPeriod(PayPeriodType.AF, trStartDate, trEndDate, "10", true));

        for (LocalDate date = trStartDate; !date.isAfter(trEndDate); date = date.plusDays(1)) {
            testRecord.addTimeEntry(new TimeEntry(testRecord, PayType.RA, date));
        }
    }

    @Before
    public void init() {
        initTestRecord();
    }

    /**
     * Tests scenario where ess tries to insert new record over personnel approved record
     */
    @Test(expected = IllegalRecordModificationEx.class)
    public void overwriteAPRecordTest() {
        // Get an active employee id with a history
        int empId = employeeDao.getActiveEmployees().stream()
                .sorted(Comparator.comparing(emp ->
                        Optional.ofNullable(emp.getSenateContServiceDate()).orElse(LocalDate.now())))
                .findFirst()
                .map(Employee::getEmployeeId)
                .orElseThrow(() -> new AssertionError("Can't find an employee"));
        // Get an approved record for that employee
        List<TimeRecord> approvedRecords =
                timeRecordDao.getRecordsDuring(empId, Range.all(), Collections.singleton(APPROVED_PERSONNEL));
        TimeRecord record = approvedRecords.stream()
                .sorted()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Cant find AP records for longest serving emp: " + empId));
        record.setTimeRecordId(null);
        record.setRemarks("These are ESS test remarks.  You shouldn't see this in the db due to test db rollbacks");
        timeRecordDao.saveRecord(record);
    }

    /* --- Ignored Tests ---

    These are "unit tests" from a simpler time.
    Some of them don't work, all of them are smoke tests at best.
    They are still useful for debugging, however.
     */

    @Test
    @Ignore
    public void insertTimeRecord() {
        boolean existing = false;
        try {
            TimeRecord oldRecord = timeRecordDao.getRecordsDuring(11423, Range.closed(trStartDate, trEndDate)).get(0);
            testRecord.setTimeRecordId(oldRecord.getTimeRecordId());
            existing = true;
        } catch (IndexOutOfBoundsException ignored) {}
        Stopwatch sw = Stopwatch.createStarted();
        timeRecordDao.saveRecord(testRecord);
        logger.info( (existing ? "update" : "insert") + " time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    @Ignore
    public void removeRecordTest() {
        timeRecordDao.getRecordsDuring(11423, Range.closed(trStartDate, trEndDate)).forEach(record -> {
            Stopwatch sw = Stopwatch.createStarted();
            timeRecordDao.deleteRecord(record.getTimeRecordId());
            logger.info("record removal time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        });
    }

    @Test
    @Ignore
    public void updateTimeRecordTest() {
        removeRecordTest();
        insertTimeRecord();
        for (int i = 0; i < 10; i++) {
            testRecord.getTimeEntries().get(i).setWorkHours(BigDecimal.ONE);
        }
        insertTimeRecord();
        for (int i = 0; i < 10; i++) {
            testRecord.getTimeEntries().get(i).setWorkHours(BigDecimal.TEN);
        }
        insertTimeRecord();
    }

    @Test
    @Ignore
    public void getRecordByEmployeeId() throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        ListMultimap<Integer, TimeRecord> timeRecords = timeRecordDao.getRecordsDuring(Collections.singleton(11423),
                Range.closed(LocalDate.of(2015, 1, 1), LocalDate.now()),
                EnumSet.allOf(TimeRecordStatus.class));
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    @Ignore
    public void testGetRecord() throws Exception {
        List<TimeRecord> records = timeRecordDao.getRecordsDuring(11423, Range.closedOpen(LocalDate.of(2015, 8, 12), LocalDate.of(2015, 8, 15)));
        records.forEach(record -> logger.info("{}", record.getBeginDate()));
    }

    @Test
    @Ignore
    public void getTimeRecordTest() {
        Stopwatch sw = Stopwatch.createStarted();
        timeRecordDao.getTimeRecord(new BigInteger("39555288913054012606969560863737970844"));
        logger.info("record retrieval time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    @Ignore
    public void getUpdatedTRecsTest() {
        logger.info("Getting updated time records");
        Stopwatch sw = Stopwatch.createStarted();
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusHours(1);
        Range<LocalDateTime> dateTimeRange = Range.closed(startDateTime, endDateTime);
        List<TimeRecord> tRecs = timeRecordDao.getUpdatedRecords(dateTimeRange);
        logger.info("{}", tRecs.size());
        logger.info("record retrieval time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    @Ignore
    public void getLastUpdateTimeTest() {
        logger.info("Getting latest update timestamp...");
        LocalDateTime lastUpdate = timeRecordDao.getLatestUpdateTime();
        logger.info("last update was {}", lastUpdate);
    }
}
