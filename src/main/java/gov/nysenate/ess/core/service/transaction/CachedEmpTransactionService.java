package gov.nysenate.ess.core.service.transaction;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryMissingEx;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.cache.EmployeeEhCache;
import gov.nysenate.ess.core.service.personnel.ActiveEmployeeIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class CachedEmpTransactionService extends EmployeeEhCache<TransactionHistory>
        implements EmpTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(CachedEmpTransactionService.class);

    private final EmpTransactionDao transactionDao;
    private final ActiveEmployeeIdService employeeIdService;
    private final EventBus eventBus;
    private LocalDateTime lastUpdateDateTime;

    @Autowired
    public CachedEmpTransactionService(EmpTransactionDao transactionDao, ActiveEmployeeIdService employeeIdService,
                                       EventBus eventBus) {
        this.transactionDao = transactionDao;
        this.employeeIdService = employeeIdService;
        this.eventBus = eventBus;
        this.lastUpdateDateTime = transactionDao.getMaxUpdateDateTime();
    }

    // --- Transaction Service Methods ---

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionHistory getTransHistory(int empId) {
        if (!cache.containsKey(empId)) {
            try {
                putId(empId);
            } catch (EmptyResultDataAccessException ex) {
                throw new TransactionHistoryMissingEx(empId);
            }
        }
        return cache.get(empId);
    }

    // --- Caching Service Implemented Methods ---

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheType cacheType() {
        return CacheType.TRANSACTION;
    }

    @Override
    protected void warmCache() {
        for (var empId : employeeIdService.getActiveEmployeeIds()) {
            putId(empId);
        }
    }

    private void putId(int empId) {
        cache.put(empId, transactionDao.getTransHistory(empId, EmpTransDaoOption.INITIALIZE_AS_APP));
    }

    /**
     * --- Internal Methods ---
     */

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
                putId(empId);
            });
            // Post the update event
            eventBus.post(new TransactionHistoryUpdateEvent(transRecs, lastCheckTime));
        }
    }
}
