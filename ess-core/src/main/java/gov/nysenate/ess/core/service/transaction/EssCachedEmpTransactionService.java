package gov.nysenate.ess.core.service.transaction;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryMissingEx;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EssCachedEmpTransactionService implements EmpTransactionService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmpTransactionService.class);

    @Autowired EmpTransactionDao transactionDao;
    @Autowired EventBus eventBus;
    @Autowired EhCacheManageService ehCacheManageService;

    private Cache transCache;
    private LocalDateTime lastCheckTime;
    private LocalDateTime lastUpdateDateTime;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        transCache = ehCacheManageService.registerEternalCache(ContentCache.TRANSACTION.name());
        lastUpdateDateTime = transactionDao.getMaxUpdateDateTime();
        lastCheckTime = LocalDateTime.now();
    }

    /** --- Transaction Service Methods --- */

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId) {
        TransactionHistory transactionHistory;
        transCache.acquireReadLockOnKey(empId);
        Element cachedElem = transCache.get(empId);
        try {
            transactionHistory = (cachedElem == null)
                ? getTransHistoryFromDao(empId)
                : (TransactionHistory) cachedElem.getObjectValue();
        }
        catch (EmptyResultDataAccessException ex) {
            throw new TransactionHistoryMissingEx(empId);
        }
        finally {
            transCache.releaseReadLockOnKey(empId);
        }
        if (cachedElem == null) putTransactionHistoryInCache(empId, transactionHistory);
        return transactionHistory;
    }

    private TransactionHistory getTransHistoryFromDao(int empId) {
        return transactionDao.getTransHistory(empId, EmpTransDaoOption.INITIALIZE_AS_APP);
    }

    private void putTransactionHistoryInCache(int empId, TransactionHistory transactionHistory) {
        transCache.acquireWriteLockOnKey(empId);
        try {
            transCache.put(new Element(empId, transactionHistory));
        }
        finally {
            transCache.releaseWriteLockOnKey(empId);
        }
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.transactions:60000}")
    private void syncTransHistory() {
        logger.debug("Checking for transaction updates since {}...", lastUpdateDateTime);
        List<TransactionRecord> transRecs = transactionDao.updatedRecordsSince(lastUpdateDateTime);
        lastCheckTime = LocalDateTime.now();
        logger.debug("{} new transaction records have been found.", transRecs.size());
        if (!transRecs.isEmpty()) {
            // Get the last updated record date/time
            lastUpdateDateTime = transRecs.stream()
                .map(TransactionRecord::getUpdateDate).max(LocalDateTime::compareTo).get();
            // Gather a set of affected employee ids and refresh their transaction cache
            transRecs.stream().map(TransactionRecord::getEmployeeId).distinct().forEach(empId -> {
                logger.debug("Re-Caching transactions for employee {}", empId);
                TransactionHistory transHistory = getTransHistoryFromDao(empId);
                putTransactionHistoryInCache(empId, transHistory);
            });
            // Post the update event
            eventBus.post(new TransactionHistoryUpdateEvent(transRecs, lastCheckTime));
        }
    }
}