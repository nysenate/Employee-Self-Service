package gov.nysenate.ess.web.service.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.web.BaseTests;
import gov.nysenate.ess.web.model.attendance.TimeRecord;
import gov.nysenate.ess.web.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.web.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class EssTimeRecordManagerTests extends BaseTests
{

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManagerTests.class);

    @Autowired EssTimeRecordManager manager;
    @Autowired
    PayPeriodService periodService;
    @Autowired TimeRecordService timeRecordService;
    @Autowired
    EmpTransactionService transService;

    private static void printRecords(Collection<TimeRecord> records) {
        records.stream().sorted().forEach(record -> {
            logger.info("{}", record.getDateRange());
//            record.getTimeEntries().stream()
//                    .filter(entry -> !entry.isEmpty())
//                    .forEach(entry -> logger.info("{}: {}", entry.getDate(), entry.getDailyTotal()));
        });
    }

    @Test
    public void ensureRecordsTest() {
        int empId = 11303;
                List<PayPeriod> payPeriods =
                periodService.getOpenPayPeriods(PayPeriodType.AF, empId, SortOrder.ASC);
        // Print existing records
        Set<TimeRecord> existingRecords =
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll())
                        .stream().map(TimeRecord::new).collect(Collectors.toSet());
        logger.info("-------- EXISTING RECORDS --------");
        printRecords(existingRecords);

        Stopwatch sw = Stopwatch.createStarted();
        // Generate records
        manager.ensureRecords(empId);
        logger.info("generation took {} ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));

        // Print difference
        Set<TimeRecord> newRecords = new TreeSet<>(
                timeRecordService.getTimeRecords(Collections.singleton(empId), payPeriods, TimeRecordStatus.getAll()));
        logger.info("-------- NEW RECORDS --------");
        printRecords(Sets.difference(newRecords, existingRecords));
    }

    @Test
    public void ensureAllRecordsTest() {
        manager.ensureAllActiveRecords();
    }

}
