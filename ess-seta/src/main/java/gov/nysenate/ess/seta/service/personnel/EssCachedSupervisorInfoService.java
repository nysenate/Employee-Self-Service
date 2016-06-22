package gov.nysenate.ess.seta.service.personnel;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.seta.dao.personnel.SupervisorDao;
import gov.nysenate.ess.seta.model.personnel.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;

@Service
public class EssCachedSupervisorInfoService implements SupervisorInfoService, CachingService<Integer>
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedSupervisorInfoService.class);

    /** A set of transactions that can affect a supervisor employee group */
    private static final ImmutableSet<TransactionCode> supervisorTransCodes = ImmutableSet.of(SUP, APP, RTP, EMP);

    @Autowired private EmpTransactionService empTransService;
    @Autowired private SupervisorDao supervisorDao;
    @Autowired private EhCacheManageService cacheManageService;
    @Autowired private EventBus eventBus;

    private Cache supEmployeeGroupCache;

    private LocalDateTime lastSupOvrUpdate;
    private LocalDateTime lastSupTransUpdate;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        supEmployeeGroupCache = cacheManageService.registerTimeBasedCache(getCacheType().name(), 3600L);
        lastSupOvrUpdate = lastSupTransUpdate = supervisorDao.getLastSupUpdateDate();
    }

    /** --- Supervisor Info Service Implemented Methods --- */

    @Override
    public boolean isSupervisorDuring(int supId, Range<LocalDate> dateRange) {
        try {
            return getSupervisorEmpGroup(supId, dateRange).hasEmployees();
        }
        catch (SupervisorException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * The latest employee transactions before the given 'date' are checked to determine
     * the supervisor id.
     */
    @Override
    public int getSupervisorIdForEmp(int empId, LocalDate date) throws SupervisorException {
        TransactionHistory transHistory = empTransService.getTransHistory(empId);
        TreeMap<LocalDate, Integer> effectiveSupervisorIds =
            transHistory.getEffectiveSupervisorIds(Range.upTo(date, BoundType.CLOSED));
        if (!effectiveSupervisorIds.isEmpty()) {
            return effectiveSupervisorIds.lastEntry().getValue();
        }
        throw new SupervisorNotFoundEx("Supervisor id not found for empId: " + empId + " for date: " + date);
    }

    @Override
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException {
        SupervisorEmpGroup empGroup;
        supEmployeeGroupCache.acquireReadLockOnKey(supId);
        Element cachedElem = supEmployeeGroupCache.get(supId);
        supEmployeeGroupCache.releaseReadLockOnKey(supId);
        if (cachedElem == null) {
            empGroup = getAndCacheSupEmpGroup(supId);
        }
        else {
            empGroup = (SupervisorEmpGroup) cachedElem.getObjectValue();
        }
        SupervisorEmpGroup filteredEmpGroup = new SupervisorEmpGroup(empGroup);
        filteredEmpGroup.setStartDate(DateUtils.startOfDateRange(dateRange));
        filteredEmpGroup.setEndDate(DateUtils.endOfDateRange(dateRange));
        filteredEmpGroup.filterActiveEmployeesByDate(dateRange);
        return filteredEmpGroup;
    }

    @Override
    public SupervisorChain getSupervisorChain(int supId, LocalDate activeDate, int maxChainLength) throws SupervisorException {
        int currEmpId = supId;
        int currDepth = 0;
        SupervisorChain supChain = new SupervisorChain(currEmpId);
        while (true) {
            /** Eliminate possibility of infinite recursion. */
            if (currDepth >= maxChainLength) {
                break;
            }
            int currSupId = getSupervisorIdForEmp(currEmpId, activeDate);
            if (supId != currSupId && !supChain.containsSupervisor(currSupId)) {
                supChain.addSupervisorToChain(currSupId);
                currEmpId = currSupId;
                currDepth++;
            }
            else {
                break;
            }
        }
        SupervisorChainAlteration alterations = supervisorDao.getSupervisorChainAlterations(supId);
        supChain.addAlterations(alterations);
        return supChain;
    }

    @Override
    public List<SupervisorOverride> getSupervisorOverrides(int supId) throws SupervisorException {
        return supervisorDao.getSupervisorOverrides(supId, SupGrantType.GRANTEE);
    }

    @Override
    public List<SupervisorOverride> getSupervisorGrants(int supId) throws SupervisorException {
        return supervisorDao.getSupervisorOverrides(supId, SupGrantType.GRANTER);
    }

    @Override
    public void updateSupervisorOverride(SupervisorOverride override) throws SupervisorException {
        if (isSupervisorDuring(override.getGranteeSupervisorId(), Range.all()) &&
            isSupervisorDuring(override.getGranterSupervisorId(), Range.all())) {
            // If the override is set to inactive, only apply the override if there is an existing grant
            // with the given granter -> grantee pair. There's no reason to create it if there isn't one.
            if (!override.isActive()) {
                boolean hasExistingGrant = getSupervisorGrants(override.getGranterSupervisorId()).stream()
                        .filter(ovr -> ovr.getGranteeSupervisorId() == override.getGranteeSupervisorId())
                        .findAny().isPresent();
                if (!hasExistingGrant) {
                    return;
                }
            }
            supervisorDao.setSupervisorOverride(override.getGranterSupervisorId(), override.getGranteeSupervisorId(),
                    override.isActive(), override.getStartDate().orElse(null), override.getEndDate().orElse(null));
            getAndCacheSupEmpGroup(override.getGranteeSupervisorId());
            eventBus.post(new SupervisorGrantUpdateEvent(override));
        }
        else {
            throw new SupervisorException("Grantee/Granter for override must both have been a supervisor.");
        }
    }

    /** --- Caching Service Implemented Methods ---
     * @see CachingService */

    /** {@inheritDoc} */
    @Override
    public ContentCache getCacheType() {
        return ContentCache.SUPERVISOR_EMP_GROUP;
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(Integer supId) {
        supEmployeeGroupCache.remove(supId);
    }

    /** {@inheritDoc} */
    @Override
    public void evictCache() {
        logger.info("Clearing {} cache..", getCacheType());
        supEmployeeGroupCache.removeAll();
    }

    /** {@inheritDoc} */
    @Override
    public void warmCache() {
        // No warming for this cache
    }

    /** --- Internal Methods --- */

    private SupervisorEmpGroup getAndCacheSupEmpGroup(int supId) throws SupervisorException {
        SupervisorEmpGroup supervisorEmpGroup = supervisorDao.getSupervisorEmpGroup(supId, DateUtils.ALL_DATES);
        putSupEmpGroupInCache(supervisorEmpGroup);
        return supervisorEmpGroup;
    }

    private void putSupEmpGroupInCache(SupervisorEmpGroup empGroup) {
        supEmployeeGroupCache.acquireWriteLockOnKey(empGroup.getSupervisorId());
        try {
            supEmployeeGroupCache.put(new Element(empGroup.getSupervisorId(), empGroup));
        }
        finally {
            supEmployeeGroupCache.releaseWriteLockOnKey(empGroup.getSupervisorId());
        }
    }

    /**
     * If any new transactions are posted that affect supervisor employee groups,
     *  recache the affected supervisor's groups
     * @param transUpdateEvent TransactionHistoryUpdateEvent
     */
    @Subscribe
    public void handleSupervisorTransactions(TransactionHistoryUpdateEvent transUpdateEvent) {
        long updatedSups = transUpdateEvent.getTransRecs().stream()
                .filter(rec -> supervisorTransCodes.contains(rec.getTransCode()))
                .flatMap(rec -> {
                    // Add any sups that were active immediately before or after the transaction effect date
                    TransactionHistory transHistory = empTransService.getTransHistory(rec.getEmployeeId());
                    Range<LocalDate> relevantDates = Range.closedOpen(
                            rec.getEffectDate().minusDays(1), rec.getEffectDate().plusDays(1));
                    return transHistory.getEffectiveSupervisorIds(relevantDates).values().stream();
                })
                .distinct()
                .peek(this::getAndCacheSupEmpGroup)
                .count();
        if (updatedSups > 0) {
            logger.info("Updated {} supervisor employee groups in response to new transactions", updatedSups);
        }
    }

    /**
     * Check for updates to supervisor overrides and update the cache as necessary
     */
    @WorkInProgress(author = "sam", since = "11/2/2015", desc = "insufficient live testing")
    @Scheduled(fixedDelayString = "${cache.poll.delay.supervisors:60000}")
    public void syncSupervisorCache() {
        logger.debug("Checking for supervisor override updates...");
        Set<Integer> modifiedSups = new HashSet<>();

        modifiedSups.addAll(getOvrUpdatedSups());

        modifiedSups.forEach(this::getAndCacheSupEmpGroup);

        logger.debug("Refreshed {} supervisor emp groups", modifiedSups.size());
    }

    /**
     * @return Set<Integer> - supervisor ids for override grantees whose overrides have been updated since the last check
     */
    private Set<Integer> getOvrUpdatedSups() {
        return supervisorDao.getSupOverrideChanges(lastSupOvrUpdate).stream()
                .peek(supOvr -> lastSupOvrUpdate = supOvr.getUpdateDate().isAfter(lastSupOvrUpdate)
                        ? supOvr.getUpdateDate() : lastSupOvrUpdate)
                .map(SupervisorOverride::getGranteeSupervisorId)
                .collect(Collectors.toSet());
    }
}