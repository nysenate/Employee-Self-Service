package gov.nysenate.ess.seta.dao.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.seta.SetaTests;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeRecordDaoTests extends SetaTests
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordDaoTests.class);

    @Autowired private TimeRecordDao timeRecordDao;

    private static TimeRecord testRecord;
    private static LocalDate trStartDate = LocalDate.of(1990, 8, 2);
    private static LocalDate trEndDate = LocalDate.of(1990, 8, 15);

    public static void initTestRecord() {
        testRecord = new TimeRecord();

        testRecord.setEmployeeId(11423);
        testRecord.setLastUpdater("STOUFFER");
        testRecord.setActive(true);
        testRecord.setRecordStatus(TimeRecordStatus.SUBMITTED);
        testRecord.setRemarks("Hello world");
        testRecord.setSupervisorId(9896);
        testRecord.setBeginDate(trStartDate);
        testRecord.setEndDate(trEndDate);

        testRecord.setRespHeadCode("STSBAC");
        testRecord.setPayPeriod(new PayPeriod(PayPeriodType.AF, trStartDate, trEndDate, 10, true));

        for (LocalDate date = trStartDate; !date.isAfter(trEndDate); date = date.plusDays(1)) {
            testRecord.addTimeEntry(new TimeEntry(testRecord, PayType.RA, date));
        }
    }

    @PostConstruct
    public void init() {
        initTestRecord();
    }

    @Test
    public void insertTimeRecord() {
        boolean existing = false;
        try {
            TimeRecord oldRecord = timeRecordDao.getRecordsDuring(11423, Range.closed(trStartDate, trEndDate)) .get(0);
            testRecord.setTimeRecordId(oldRecord.getTimeRecordId());
            existing = true;
        } catch (IndexOutOfBoundsException ignored) {}
        Stopwatch sw = Stopwatch.createStarted();
        timeRecordDao.saveRecord(testRecord);
        logger.info( (existing ? "update" : "insert") + " time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void removeRecordTest() {
        timeRecordDao.getRecordsDuring(11423, Range.closed(trStartDate, trEndDate)).forEach(record -> {
            Stopwatch sw = Stopwatch.createStarted();
            timeRecordDao.deleteRecord(record.getTimeRecordId());
            logger.info("record removal time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        });
    }

    @Test
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
    public void getRecordByEmployeeId() throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        ListMultimap<Integer, TimeRecord> timeRecords = timeRecordDao.getRecordsDuring(Collections.singleton(11423),
                Range.closed(LocalDate.of(2015, 1, 1), LocalDate.now()),
                EnumSet.allOf(TimeRecordStatus.class));
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetRecord() throws Exception {
        List<TimeRecord> records = timeRecordDao.getRecordsDuring(11423, Range.closedOpen(LocalDate.of(2015, 8, 12), LocalDate.of(2015, 8, 15)));
        records.forEach(record -> logger.info("{}", record.getBeginDate()));
    }

    @Test
    public void getTimeRecordTest() {
        Stopwatch sw = Stopwatch.createStarted();
        timeRecordDao.getTimeRecord(new BigInteger("39555288913054012606969560863737970844"));
        logger.info("record retrieval time: {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }
}
