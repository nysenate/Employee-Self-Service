package gov.nysenate.ess.time.service.attendance;

import com.google.common.base.Stopwatch;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.service.cache.EssCacheManager;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EssCachedEmpTransactionService;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Category(SillyTest.class)
public class EssTimeRecordManagerTest extends BaseTest
{

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordManagerTest.class);

    @Autowired EssTimeRecordManager manager;
    @Autowired PayPeriodService periodService;
    @Autowired TimeRecordService timeRecordService;
    @Autowired EssCachedEmpTransactionService transService;
    @Autowired AttendanceDao attendanceDao;

    @Value("${master.schema}") protected String MASTER_SCHEMA;
    @Value("${ts.schema}") protected String TS_SCHEMA;

    @Resource(name = "remoteNamedJdbcTemplate") NamedParameterJdbcTemplate remoteNamedJdbcTemplate;

    private static void printRecords(Collection<TimeRecord> records) {
        records.stream().sorted().forEach(record -> {
            logger.info("{}", record.getDateRange());
//            record.getTimeEntries().stream()
//                    .filter(entry -> !entry.isEmpty())
//                    .forEach(entry -> logger.info("{}: {}", entry.getDate(), entry.getDailyTotal()));
        });
    }

    @Test
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public void ensureRecordsTest() {
        int empId = 12045;

        RangeSet<LocalDate> openDates = attendanceDao.getOpenDates(empId);
        List<PayPeriod> payPeriods = openDates.asRanges().stream()
                .flatMap(range -> periodService.getPayPeriods(PayPeriodType.AF, range, SortOrder.ASC).stream())
                .collect(Collectors.toList());

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
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public void ensureAllRecordsTest() {
        Stopwatch started = Stopwatch.createStarted();
        manager.ensureAllActiveRecords();
        logger.info("TRM run completed in {}s", started.stop());
    }

    @Test
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public void splitRecordTest() {
        int empId = 11423;

        postSplitTransaction(empId);

        EssCacheManager.removeEntry(transService.cacheType(), String.valueOf(empId));

        manager.ensureRecords(empId);
    }

    private void postSplitTransaction(int empId) {

        logger.info("posting sup transaction for " + empId);

        int changeNo = 999444;
        int docNo = 999445;
        LocalDate effectDate = LocalDate.of(2017, 5, 24);
        TransactionCode transCode = TransactionCode.SUP;
        int newSupId = 6221;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("empId", empId)
                .addValue("changeNo", changeNo)
                .addValue("docNo", docNo)
                .addValue("effectDate", SqlBaseDao.toDate(effectDate))
                .addValue("now", SqlBaseDao.toDate(LocalDateTime.now()))
                .addValue("newSupId", newSupId)
                .addValue("transCode", transCode.name())
                .addValue("transType", transCode.getType().name())
                ;

        final String auditSql =
                "INSERT INTO " + MASTER_SCHEMA + ".PM21PERAUDIT \n" +
                "       (CDSTATUS, NUXREFEM, DTEFFECT, NUDOCUMENT, NUCHANGE, DTTXNORIGIN, DTTXNUPDATE, NUXREFSV, NATXNORGUSER, NATXNUPDUSER)\n" +
                "VALUES ('A', :empId, :effectDate, :docNo, :changeNo, :now, :now, :newSupId, USER, USER)";

        final String ptxSql =
                "INSERT INTO " + MASTER_SCHEMA + ".PD21PTXNCODE \n" +
                "       (CDSTATUS, NUXREFEM, DTEFFECT, NUCHANGE, NUDOCUMENT, DTTXNORIGIN, DTTXNUPDATE, CDTRANS, CDTRANSTYP,\n" +
                "           DTTXNPOST, NATXNORGUSER, NATXNUPDUSER)\n" +
                "VALUES ('A', :empId, :effectDate, :docNo, :changeNo, :now, :now, :transCode, :transType,\n" +
                "           :now, USER, USER)";

        remoteNamedJdbcTemplate.update(auditSql, params);
        remoteNamedJdbcTemplate.update(ptxSql, params);

    }

}
