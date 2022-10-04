package gov.nysenate.ess.time.service.personnel;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionInfo;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.time.model.personnel.ExtendedSupEmpGroup;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Category(SillyTest.class)
public class CachedSupervisorInfoServiceIT extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CachedSupervisorInfoServiceIT.class);

    @Autowired EssCachedSupervisorInfoService supInfoService;

    @Test
    public void getSupEmpGroupTest() throws InterruptedException {
        Thread.sleep(5000);
        int supId = 1024;
        Range<LocalDate> dateRange = Range.all();
        logger.info("Getting emps for {}", supId);
        Stopwatch sw = Stopwatch.createStarted();
        SupervisorEmpGroup supervisorEmpGroup = supInfoService.getSupervisorEmpGroup(supId, dateRange);
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        logger.info("Getting emps for {} again", supId);
        sw.reset().start();
        SupervisorEmpGroup supervisorEmpGroup2 = supInfoService.getSupervisorEmpGroup(supId, dateRange);
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void getExtendedSupEmpGroupTest() throws InterruptedException {
        Thread.sleep(5000);
        int supId = 1024;
        Range<LocalDate> dateRange = Range.all();
        logger.info("Getting emps for {}", supId);
        Stopwatch sw = Stopwatch.createStarted();
        ExtendedSupEmpGroup extendedSupEmpGroup = supInfoService.getExtendedSupEmpGroup(supId, dateRange);
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        sw.reset().start();
        logger.info("Getting sups for {} again", supId);
        ExtendedSupEmpGroup extendedSupEmpGroup2 = supInfoService.getExtendedSupEmpGroup(supId, dateRange);
        logger.info("{}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void warmCacheTest() {
        Stopwatch sw = Stopwatch.createStarted();
        logger.info("Cache warmed in {} sec", sw.stop().elapsed(TimeUnit.SECONDS));
    }

    @Test
    public void updateTest() {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setEmployeeId(11423);
        transactionInfo.setEffectDate(LocalDate.of(2019, 7, 12));
        transactionInfo.setTransCode(TransactionCode.SUP);
        TransactionRecord record = new TransactionRecord(transactionInfo);
        TransactionHistoryUpdateEvent updateEvent =
                new TransactionHistoryUpdateEvent(Collections.singletonList(record), LocalDateTime.now());
        Stopwatch sw = Stopwatch.createStarted();
        logger.info("Starting cache update...");
        supInfoService.handleSupervisorTransactions(updateEvent);
        logger.info("Cache updated in {} sec", sw.stop().elapsed(TimeUnit.SECONDS));
    }
}
