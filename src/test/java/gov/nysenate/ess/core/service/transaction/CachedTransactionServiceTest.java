package gov.nysenate.ess.core.service.transaction;

import com.google.common.base.Stopwatch;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.cache.CacheEvictIdEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Category(SillyTest.class)
public class CachedTransactionServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CachedTransactionServiceTest.class);

    @Autowired EmpTransactionDao transDao;
    @Autowired EssCachedEmpTransactionService transService;

    @Autowired EventBus eventBus;

    private void timedGet(int empId) {
        Stopwatch sw = Stopwatch.createStarted();
        TransactionHistory transHistory = transService.getTransHistory(empId);
        logger.info("got trans history for {}:  {}", empId, sw.stop().elapsed(TimeUnit.NANOSECONDS));
    }

    @Test
    public void cacheTest() {
        int empId = 45;
        for (int i = 0; i < 4; i++) {
            timedGet(empId);
        }
    }

    @Test
    public void invalidateTest() {
        int empId = 5803;
        timedGet(empId);
        timedGet(empId);
        logger.info("invalidating {}!", empId);
        eventBus.post(new CacheEvictIdEvent<>(ContentCache.TRANSACTION, empId));
        timedGet(empId);
    }

    @Test
    public void testTransactions() throws Exception {
        TransactionHistory transHistory = transDao.getTransHistory(1719, EmpTransDaoOption.NONE);
        logger.info("{}", transHistory.getEffectiveAccrualStatus(Range.upTo(LocalDate.now(), BoundType.CLOSED)));
    }

    @Test
    public void transHistoryUpdateEventTest() {
        ImmutableCollection<TransactionRecord> transactionRecords =
                transService.getTransHistory(439).getRecordsByCode().values();
        eventBus.post(new TransactionHistoryUpdateEvent(new ArrayList<>(transactionRecords), LocalDateTime.now()));
    }
}
