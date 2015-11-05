package gov.nysenate.ess.web.service.transaction;

import com.google.common.base.Stopwatch;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.cache.CacheEvictIdEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.transaction.EssCachedEmpTransactionService;
import gov.nysenate.ess.web.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class CachedTransactionServiceTests extends BaseTests
{

    private static final Logger logger = LoggerFactory.getLogger(CachedTransactionServiceTests.class);

    @Autowired EmpTransactionDao transDao;
    @Autowired
    EssCachedEmpTransactionService transService;

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
}
