package gov.nysenate.ess.time.service.personnel;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.AsyncRunner;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.dao.personnel.SupervisorDao;
import gov.nysenate.ess.time.model.personnel.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;
import static gov.nysenate.ess.time.model.personnel.SupOverrideType.EMPLOYEE;
import static gov.nysenate.ess.time.model.personnel.SupOverrideType.SUPERVISOR;

@Service
public class EssCachedSupervisorInfoService implements SupervisorInfoService, CachingService<Integer>
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedSupervisorInfoService.class);

    /** A set of transactions that can affect a supervisor employee group */
    private static final ImmutableSet<TransactionCode> supervisorTransCodes =
            ImmutableSet.of(SUP, APP, RTP, EMP, AGY, TYP);

    /** Employees are assigned this value in the cache if they are not supervisors */
    private static final Object NON_SUPERVISOR_VALUE = Boolean.FALSE;

    @Autowired private EmpTransactionService empTransService;
    @Autowired private EmployeeInfoService empInfoService;
    @Autowired private SupervisorDao supervisorDao;
    @Autowired private EhCacheManageService cacheManageService;
    @Autowired private EventBus eventBus;
    @Autowired private AsyncRunner asyncRunner;

    @Value("${cache.warm.onstartup.supervisors:true}")
    private boolean warmOnStartup;

    private Cache supEmployeeGroupCache;

    private LocalDateTime lastSupOvrUpdate;
    private LocalDateTime lastSupTransUpdate;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        supEmployeeGroupCache = cacheManageService.registerEternalCache(getCacheType().name());
        lastSupOvrUpdate = lastSupTransUpdate = supervisorDao.getLastSupUpdateDate();
        if (cacheManageService.isWarmOnStartup() && warmOnStartup) {
            // Run asynchronously since this will take a while
            asyncRunner.run(this::warmCache);
        }
    }

    /** --- Supervisor Info Service Implemented Methods --- */

    @Override
    public boolean isSupervisorDuring(int empId, Range<LocalDate> dateRange) {
        try {
            return getSupervisorEmpGroup(empId, dateRange).hasEmployees();
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
        SupervisorEmpGroup filteredEmpGroup = getSupEmpGroup(supId);
        filteredEmpGroup.setActiveDates(dateRange);
        return filteredEmpGroup;
    }

    @Override
    public ExtendedSupEmpGroup getExtendedSupEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException {
        SupervisorEmpGroup empGroup = getSupervisorEmpGroup(supId, dateRange);
        ExtendedSupEmpGroup extendedEmpGroup = new ExtendedSupEmpGroup(empGroup);

        Employee supervisor = empInfoService.getEmployee(supId);

        Queue<EmployeeSupInfo> supInfoQueue = new ArrayDeque<>(extendedEmpGroup.getPrimaryEmpSupInfos());

        while (!supInfoQueue.isEmpty()) {
            EmployeeSupInfo supInfo = supInfoQueue.remove();
            try {
                SecondarySupEmpGroup subEmpGroup =
                        new SecondarySupEmpGroup(getPrimarySupEmpGroup(supInfo.getEmpId()), supInfo.getSupId());
                subEmpGroup.setActiveDates(supInfo.getEffectiveDateRange());

                // The employee may not be supervising any employees for their time under this supervisor
                if (subEmpGroup.hasEmployees()) {
                    extendedEmpGroup.addEmployeeSupEmpGroup(subEmpGroup);
                }

                // If the primary supervisor is a senator,
                // and the employee is in the senator's department,
                // Add the employees of this employee to the queue
                if (supervisor.isSenator() && supervisor.getRespCenterHeadCode() != null) {
                    Employee employee = empInfoService.getEmployee(supInfo.getEmpId());
                    if (supervisor.getRespCenterHeadCode().equals(employee.getRespCenterHeadCode())) {
                        supInfoQueue.addAll(subEmpGroup.getPrimaryEmpSupInfos());
                    }
                }
            } catch (SupervisorException ignored) {
                // Do not add entries for employees that are not supervisors
            }
        }

        return extendedEmpGroup;
    }

    @Override
    public SupervisorChain getSupervisorChain(int supId, LocalDate activeDate, int maxChainLength) throws SupervisorException {
        int currEmpId = supId;
        int currDepth = 0;
        SupervisorChain supChain = new SupervisorChain(currEmpId);
        while (true) {
            /* Eliminate possibility of infinite recursion. */
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
        return supervisorDao.getSupervisorOverrides(supId);
    }

    @Override
    public List<SupervisorOverride> getSupervisorGrants(int supId) throws SupervisorException {
        return supervisorDao.getSupervisorGrants(supId);
    }

    @Override
    public void updateSupervisorOverride(SupervisorOverride override) throws SupervisorException {
        if (isSupervisorDuring(override.getGranteeEmpId(), Range.all()) &&
            isSupervisorDuring(override.getGranterEmpId(), Range.all())) {
            // If the override is set to inactive, only apply the override if there is an existing grant
            // with the given granter -> grantee pair. There's no reason to create it if there isn't one.
            if (!override.isActive()) {
                boolean hasExistingGrant = getSupervisorGrants(override.getGranterEmpId()).stream()
                        .anyMatch(ovr -> ovr.getGranteeEmpId() == override.getGranteeEmpId());
                if (!hasExistingGrant) {
                    return;
                }
            }
            supervisorDao.setSupervisorOverride(override.getGranterEmpId(), override.getGranteeEmpId(),
                    override.isActive(), override.getStartDate().orElse(null), override.getEndDate().orElse(null));
            getAndCachePrimarySupEmpGroup(override.getGranteeEmpId());
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
        evictCache();
        logger.info("Warming {} cache...", getCacheType());
        // Cache all extended sup emp groups
        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();
        TreeSet<Integer> sortedActiveEmpIds = new TreeSet<>(activeEmpIds);
        for (Integer empId : sortedActiveEmpIds) {
            try {
                getExtendedSupEmpGroup(empId, DateUtils.ALL_DATES);
            } catch (SupervisorMissingEmpsEx ignored) {}
        }
        logger.info("{} cache warmed.", getCacheType());
    }

    /* --- Internal Methods --- */

    /**
     * Construct a {@link SupervisorEmpGroup} for the given supId
     * Uses {@link PrimarySupEmpGroup} and/or {@link SupervisorOverride}s
     * @param supId int
     * @return {@link SupervisorEmpGroup}
     * @throws SupervisorException if the employee ref'd by supId has no primary employees or overrides
     */
    private SupervisorEmpGroup getSupEmpGroup(int supId) throws SupervisorException {
        SupervisorEmpGroup empGroup;
        List<SupervisorOverride> overrides = getOverrides(supId);

        try {
            empGroup = new SupervisorEmpGroup(getPrimarySupEmpGroup(supId));
        } catch (SupervisorMissingEmpsEx ex) {
            if (overrides.isEmpty()) {
                throw ex;
            }
            empGroup = new SupervisorEmpGroup();
            empGroup.setSupervisorId(supId);
        }

        for (SupervisorOverride override : overrides) {
            // Only use the override if it is currently effective.
            if (!override.isInEffect()) {
                continue;
            }
            switch (override.getSupOverrideType()) {
                case SUPERVISOR:
                    getSupOverrideEmps(override)
                            .forEach(empGroup::addSupOverrideEmployee);
                    break;
                case EMPLOYEE:
                    getEmpOverrideEmp(override).forEach(empGroup::addOverrideEmployee);
                    break;
            }
        }

        return empGroup;
    }

    /**
     * Generates {@link EmployeeSupInfo}s for the given supervisor override.
     * Uses the granter's {@link PrimarySupEmpGroup} to get {@link EmployeeSupInfo}s.
     * Must be called for {@link SupOverrideType#SUPERVISOR} overrides.
     *
     * @param override {@link SupervisorOverride}
     * @return {@link Set<EmployeeSupInfo>}
     * @throws IllegalStateException if non {@link SupOverrideType#SUPERVISOR} is passed in
     */
    private Set<EmployeeSupInfo> getSupOverrideEmps(SupervisorOverride override) {
        if (SUPERVISOR != override.getSupOverrideType()) {
            throw new IllegalStateException("This method is intended for " + SUPERVISOR + " overrides.  " +
                    "Received: " + override);
        }
        try {
            PrimarySupEmpGroup overrideEmpGroup = getPrimarySupEmpGroup(override.getGranterEmpId());
            return overrideEmpGroup.getPrimaryEmpSupInfos();
        } catch (SupervisorMissingEmpsEx ex) {
            return Collections.emptySet();
        }
    }

    /**
     * Generates {@link EmployeeSupInfo}s for the given employee supervisor override.
     * Uses the employee's {@link TransactionHistory} to filter out non-employed dates
     *   and possibly split into multiple {@link EmployeeSupInfo}s if the employee had multiple supervisors
     *   during the override.
     * Must be called for {@link SupOverrideType#EMPLOYEE} overrides
     *
     * @param override {@link SupervisorOverride}
     * @return {@link Set<EmployeeSupInfo>}
     * @throws IllegalStateException if non {@link SupOverrideType#EMPLOYEE} is passed in
     */
    private Set<EmployeeSupInfo> getEmpOverrideEmp(SupervisorOverride override) {
        if (EMPLOYEE != override.getSupOverrideType()) {
            throw new IllegalStateException("This method is intended for " + EMPLOYEE + " overrides.  " +
                    "Received: " + override);
        }
        int empId = override.getGranterEmpId();

        TransactionHistory transHistory = empTransService.getTransHistory(empId);

        RangeSet<LocalDate> activeDates = transHistory.getActiveDates();

        TreeMap<LocalDate, Integer> effectiveSupervisorIds = transHistory.getEffectiveSupervisorIds(Range.all());
        RangeSet<LocalDate> supervisorDates = RangeUtils.getRangeSet(RangeUtils.toRangeMap(effectiveSupervisorIds));

        // Get intersection of active dates, continuous supervisor dates, and the override effective date range
        // this will get ranges for each necessary EmployeeSupInfo
        RangeSet<LocalDate> empInfoRanges = RangeUtils.intersection(activeDates, supervisorDates);

        Employee employee = empInfoService.getEmployee(empId);

        Set<EmployeeSupInfo> empSupInfos = empInfoRanges.asRanges().stream()
                .map(range -> {
                    LocalDate startDate = DateUtils.startOfDateRange(range);
                    LocalDate endDate = DateUtils.endOfDateRange(range);
                    Integer supId = transHistory.getEffectiveSupervisorIds(range).lastEntry().getValue();
                    EmployeeSupInfo empSupInfo = new EmployeeSupInfo(empId, supId);
                    empSupInfo.setEmpFirstName(employee.getFirstName());
                    empSupInfo.setEmpLastName(employee.getLastName());
                    empSupInfo.setSupStartDate(startDate);
                    empSupInfo.setSupEndDate(endDate);
                    return empSupInfo;
                })
                .collect(Collectors.toSet());

        return empSupInfos;
    }

    /**
     * Get a {@link PrimarySupEmpGroup} for the given employee
     * The group will be retrieved from the cache if it exists there
     * Or pulled from the database (and into the cache) if not
     * @see #getCachedPrimarySupEmpGroup(int)
     * @see #getAndCachePrimarySupEmpGroup(int)
     * @param supId int - supervisor id
     * @return {@link PrimarySupEmpGroup}
     * @throws SupervisorException if the employee has no direct employees
     */
    private PrimarySupEmpGroup getPrimarySupEmpGroup(int supId) throws SupervisorException {
        PrimarySupEmpGroup primarySupEmpGroup =
                Optional.ofNullable(getCachedPrimarySupEmpGroup(supId))
                        .orElseGet(() -> this.getAndCachePrimarySupEmpGroup(supId));

        // Return a copy to prevent unintentional modification to the cached value
        return new PrimarySupEmpGroup(primarySupEmpGroup);
    }

    /**
     * Get a {@link PrimarySupEmpGroup} from the cache for the given supervisor
     *
     * @param supId int - supervisor id
     * @return {@link PrimarySupEmpGroup} if one exists, null otherwise
     * @throws SupervisorException if the employee is registered in the cache as a non-supervisor
     */
    private PrimarySupEmpGroup getCachedPrimarySupEmpGroup(int supId) throws SupervisorException {
        supEmployeeGroupCache.acquireReadLockOnKey(supId);
        Element cachedElem = supEmployeeGroupCache.get(supId);
        supEmployeeGroupCache.releaseReadLockOnKey(supId);
        if (cachedElem == null) {
            return null;
        }
        Object cachedValue = cachedElem.getObjectValue();
        if (NON_SUPERVISOR_VALUE.equals(cachedValue)) {
            throw new SupervisorMissingEmpsEx(supId);
        }
        return (PrimarySupEmpGroup) cachedElem.getObjectValue();
    }

    /**
     * Get a {@link PrimarySupEmpGroup} and save it to the cache
     * Register the employee in the cache as a non-supervisor if no {@link PrimarySupEmpGroup} can be retrieved
     * @param supId int - supervisor id
     * @return {@link PrimarySupEmpGroup}
     * @throws SupervisorException if the employee is not a supervisor
     */
    private PrimarySupEmpGroup getAndCachePrimarySupEmpGroup(int supId) throws SupervisorException {
        try {
            PrimarySupEmpGroup primarySupEmpGroup = supervisorDao.getPrimarySupEmpGroup(supId);
            putValueInCache(supId, primarySupEmpGroup);
            return primarySupEmpGroup;
        } catch (SupervisorException ex) {
            putValueInCache(supId, NON_SUPERVISOR_VALUE);
            throw ex;
        }
    }

    private void putValueInCache(int empId, Object value) {
        supEmployeeGroupCache.acquireWriteLockOnKey(empId);
        try {
            supEmployeeGroupCache.put(new Element(empId, value));
        }
        finally {
            supEmployeeGroupCache.releaseWriteLockOnKey(empId);
        }
    }

    /**
     * Get all employee and supervisor overrides for the given employee
     * @param supId int
     * @return {@link List<SupervisorOverride>}
     */
    private List<SupervisorOverride> getOverrides(int supId) {
        return supervisorDao.getAllOverrides(supId);
    }

    /**
     * If any new transactions are posted that affect supervisor employee groups,
     *  recache the affected supervisor's groups
     * @param transUpdateEvent TransactionHistoryUpdateEvent
     */
    @Subscribe
    private void handleSupervisorTransactions(TransactionHistoryUpdateEvent transUpdateEvent) {
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
                .peek(this::getAndCachePrimarySupEmpGroup)
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
    private void syncSupervisorCache() {
        logger.debug("Checking for supervisor override updates...");
        Set<Integer> modifiedSups = new HashSet<>();

        modifiedSups.addAll(getOvrUpdatedSups());

        modifiedSups.forEach(this::getAndCachePrimarySupEmpGroup);

        logger.debug("Refreshed {} supervisor emp groups", modifiedSups.size());
    }

    /**
     * @return Set<Integer> - supervisor ids for override grantees whose overrides have been updated since the last check
     */
    private Set<Integer> getOvrUpdatedSups() {
        return supervisorDao.getSupOverrideChanges(lastSupOvrUpdate).stream()
                .peek(supOvr -> lastSupOvrUpdate = supOvr.getUpdateDate().isAfter(lastSupOvrUpdate)
                        ? supOvr.getUpdateDate() : lastSupOvrUpdate)
                .map(SupervisorOverride::getGranteeEmpId)
                .collect(Collectors.toSet());
    }
}