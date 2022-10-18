package gov.nysenate.ess.core.service.transaction;

import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryMissingEx;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.cache.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class EssCachedEmpTransactionService extends CachingService<Integer, TransactionHistory>
        implements EmpTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmpTransactionService.class);

    private final EmpTransactionDao transactionDao;
    private LocalDateTime lastUpdateDateTime;

    @Autowired
    public EssCachedEmpTransactionService(EmpTransactionDao transactionDao) {
        this.transactionDao = transactionDao;
        this.lastUpdateDateTime = transactionDao.getMaxUpdateDateTime();
    }

    /** --- Transaction Service Methods --- */

    /** {@inheritDoc} */
    @Override
    public TransactionHistory getTransHistory(int empId) {
        TransactionHistory history = cache.get(empId);
        if (history == null) {
            try {
                history = getTransHistoryFromDao(empId);
            } catch (EmptyResultDataAccessException ex) {
                throw new TransactionHistoryMissingEx(empId);
            }
            cache.put(empId, history);
        }
        return history;
    }

    /** --- Caching Service Implemented Methods ---
     * @see CachingService*/

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.TRANSACTION;
    }

    /** --- Internal Methods --- */

    private TransactionHistory getTransHistoryFromDao(int empId) {
        return transactionDao.getTransHistory(empId, EmpTransDaoOption.INITIALIZE_AS_APP);
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.transactions:60000}")
    private void syncTransHistory() {
        logger.info("Checking for transaction updates since {}...", lastUpdateDateTime);
        List<TransactionRecord> transRecs = transactionDao.updatedRecordsSince(lastUpdateDateTime);
        LocalDateTime lastCheckTime = LocalDateTime.now();
        logger.info("{} new transaction records have been found.", transRecs.size());
        if (!transRecs.isEmpty()) {
            // Get the last updated record date/time
            lastUpdateDateTime = transRecs.stream()
                    .flatMap(tRec -> Stream.of(tRec.getAuditUpdateDate(), tRec.getUpdateDate()))
                    .max(LocalDateTime::compareTo).get();
            // Gather a set of affected employee ids and refresh their transaction cache
            transRecs.stream().map(TransactionRecord::getEmployeeId).distinct().forEach(empId -> {
                logger.info("Re-Caching transactions for employee {}", empId);
                cache.put(empId, getTransHistoryFromDao(empId));
            });
            // Post the update event
            eventBus.post(new TransactionHistoryUpdateEvent(transRecs, lastCheckTime));
        }
    }
}