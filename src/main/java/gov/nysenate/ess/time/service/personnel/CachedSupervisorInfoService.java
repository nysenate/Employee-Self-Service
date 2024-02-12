package gov.nysenate.ess.time.service.personnel;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.service.cache.EmployeeCache;
import gov.nysenate.ess.core.service.personnel.ActiveEmployeeIdService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.dao.personnel.SupervisorDao;
import gov.nysenate.ess.time.model.personnel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;
import static gov.nysenate.ess.time.model.personnel.SupOverrideType.EMPLOYEE;
import static gov.nysenate.ess.time.model.personnel.SupOverrideType.SUPERVISOR;

public class CachedSupervisorInfoService extends EmployeeCache<PrimarySupEmpGroup>
        implements SupervisorInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CachedSupervisorInfoService.class);
    // This is how we represent an employee that isn't a supervisor.
    private static final PrimarySupEmpGroup invalidGroup = new PrimarySupEmpGroup(Integer.MIN_VALUE);
    /** A set of transactions that can affect a supervisor employee group */
    private static final ImmutableSet<TransactionCode> supervisorTransCodes =
            ImmutableSet.of(SUP, APP, RTP, EMP, AGY, TYP);

    private final EmpTransactionService empTransService;
    private final EmployeeInfoService empInfoService;
    private final SupervisorDao supervisorDao;
    private final ActiveEmployeeIdService employeeIdService;
    private final EventBus eventBus;
    private LocalDateTime lastSupOvrUpdate;

    @Autowired
    public CachedSupervisorInfoService(EmpTransactionService empTransService, EmployeeInfoService empInfoService,
                                       SupervisorDao supervisorDao, ActiveEmployeeIdService employeeIdService,
                                       EventBus eventBus) {
        this.empTransService = empTransService;
        this.empInfoService = empInfoService;
        this.supervisorDao = supervisorDao;
        this.employeeIdService = employeeIdService;
        this.eventBus = eventBus;
        this.lastSupOvrUpdate = supervisorDao.getLastSupUpdateDate();
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

        List<Integer> processedSupervisors = new ArrayList<>();
        processedSupervisors.add(supId);
        Queue<EmployeeSupInfo> supInfoQueue = new ArrayDeque<>(extendedEmpGroup.getPrimaryEmpSupInfos());

        while (!supInfoQueue.isEmpty()) {
            EmployeeSupInfo supInfo = supInfoQueue.remove();
            SecondarySupEmpGroup subEmpGroup;
            try {
                subEmpGroup = new SecondarySupEmpGroup(getPrimarySupEmpGroup(supInfo.getEmpId()),
                        supInfo.getSupId());
            } catch (SupervisorException ignored) {
                // Do not add entries for employees that are not supervisors
                continue;
            }
            subEmpGroup.setActiveDates(supInfo.getEffectiveDateRange());
            processedSupervisors.add(supInfo.getEmpId());

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
                    for (var info : subEmpGroup.getPrimaryEmpSupInfos()) {
                        // Prevent an infinite loop when 2 emps are the supervisors for each other.
                        if (!processedSupervisors.contains(info.getEmpId())) {
                            supInfoQueue.add(info);
                        }
                    }
                }
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
            cachePrimarySupEmpGroup(override.getGranteeEmpId());
            eventBus.post(new SupervisorGrantUpdateEvent(override));
        }
        else {
            throw new SupervisorException("Grantee/Granter for override must both have been a supervisor.");
        }
    }

    // --- Caching Service Implemented Methods ---

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.SUPERVISOR_EMP_GROUP;
    }

    @Override
    protected void warmCache() {
        for (var empId : employeeIdService.getActiveEmployeeIds()) {
            try {
                cachePrimarySupEmpGroup(empId);
            }
            catch (SupervisorException ignored) {}
        }
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
            empGroup = new SupervisorEmpGroup(supId);
        }

        for (SupervisorOverride override : overrides) {
            // Only use the override if it is currently effective.
            if (!override.isInEffect()) {
                continue;
            }
            switch (override.getSupOverrideType()) {
                case SUPERVISOR -> getSupOverrideEmps(override)
                        .forEach(empGroup::addSupOverrideEmployee);
                case EMPLOYEE -> getEmpOverrideEmp(override).forEach(empGroup::addOverrideEmployee);
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

        return empInfoRanges.asRanges().stream()
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
    }

    /**
     * Get a {@link PrimarySupEmpGroup} for the given employee
     * The group will be retrieved from the cache if it exists there
     * Or pulled from the database (and into the cache) if not
     * @see #cachePrimarySupEmpGroup(int)
     * @param supId int - supervisor id
     * @return {@link PrimarySupEmpGroup}
     * @throws SupervisorException if the employee has no direct employees
     */
    private PrimarySupEmpGroup getPrimarySupEmpGroup(int supId) throws SupervisorException {
        var result = cache.get(supId);
        if (result == null) {
            result = cachePrimarySupEmpGroup(supId);
        }
        else if (result.getSupervisorId() == invalidGroup.getSupervisorId()) {
            throw new SupervisorMissingEmpsEx(supId);
        }
        // Return a copy to prevent unintentional modification to the cached value
        return new PrimarySupEmpGroup(result);
    }

    /**
     * Get a {@link PrimarySupEmpGroup} and save it to the cache
     * Register the employee in the cache as a non-supervisor if no {@link PrimarySupEmpGroup} can be retrieved
     * @param supId int - supervisor id
     * @return {@link PrimarySupEmpGroup}
     * @throws SupervisorException if the employee is not a supervisor
     */
    private PrimarySupEmpGroup cachePrimarySupEmpGroup(int supId) throws SupervisorException {
        try {
            PrimarySupEmpGroup primarySupEmpGroup = supervisorDao.getPrimarySupEmpGroup(supId);
            cache.put(supId, primarySupEmpGroup);
            return primarySupEmpGroup;
        } catch (SupervisorException ex) {
            cache.put(supId, invalidGroup);
            throw ex;
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
    public void handleSupervisorTransactions(TransactionHistoryUpdateEvent transUpdateEvent) {
        // Get a map of affected employees with the date range possibly impacted by the update.
        Map<Integer, RangeSet<LocalDate>> affectedEmpDates = new HashMap<>();
        transUpdateEvent.getTransRecs().stream()
                .filter(rec -> supervisorTransCodes.contains(rec.getTransCode()))
                .forEach(rec -> {
                    RangeSet<LocalDate> affectedRanges =
                            affectedEmpDates.getOrDefault(rec.getEmployeeId(), TreeRangeSet.create());
                    // Include day before and after record just in case
                    affectedRanges.add(Range.closed(
                            rec.getEffectDate().minusDays(1),
                            rec.getEffectDate().plusDays(1)
                    ));
                    affectedEmpDates.put(rec.getEmployeeId(), affectedRanges);
                });

        Set<Integer> affectedSupervisorIds = new HashSet<>();
        // Get affected cache entries.
        affectedSupervisorIds.addAll(getAffectedSupIdsInCache(affectedEmpDates));
        // Get supervisors that need a cache update according to transaction history
        affectedSupervisorIds.addAll(getAffectedSupIdsFromTrans(affectedEmpDates));

        if (affectedSupervisorIds.size() > 0) {
            affectedSupervisorIds.forEach(this::cachePrimarySupEmpGroup);
            logger.info("Updated {} supervisor employee groups in response to new transactions",
                    affectedSupervisorIds.size());
        }
    }

    /**
     * Get sup ids from cache entries that contain the given employee ids at the given dates.
     * This will detect instances when a supervisor is no longer in an employee's transaction history,
     * but the employee is still in the supervisors cached emp group,
     * which is not doable using transaction history alone.
     * @see #getAffectedSupIdsFromTrans(Map), to get affected supervisors not necessarily covered by this method.
     */
    private Set<Integer> getAffectedSupIdsInCache(Map<Integer, RangeSet<LocalDate>> affectedEmpDates) {
        Set<Integer> affectedSupIds = new HashSet<>();
        for (var entry : cache) {
            PrimarySupEmpGroup group = entry.getValue();
            if (group.getSupervisorId() == invalidGroup.getSupervisorId()) {
                continue;
            }
            // Check if the supervisor had any of the affected emps during the relevant dates.
            for (Map.Entry<Integer, RangeSet<LocalDate>> empDates : affectedEmpDates.entrySet()) {
                for (Range<LocalDate> dates : empDates.getValue().asRanges()) {
                    if (group.hasEmployeeDuringRange(empDates.getKey(), dates)) {
                        affectedSupIds.add(group.getSupervisorId());
                    }
                }
            }
        }
        return affectedSupIds;
    }

    /**
     * Get sup ids of supervisors for the given emps at given dates according to current transaction history.
     * This will pick up new supervisors added to an employee's transaction history,
     * something not doable using the cache.
     * @see #getAffectedSupIdsInCache(Map), to get affected supervisors not necessarily covered by this method.
     */
    private Set<Integer> getAffectedSupIdsFromTrans(Map<Integer, RangeSet<LocalDate>> affectedEmpDates) {
        Set<Integer> affectedSupIds = new HashSet<>();
        for (Map.Entry<Integer, RangeSet<LocalDate>> entry : affectedEmpDates.entrySet()) {
            int empId = entry.getKey();
            RangeSet<LocalDate> dates = entry.getValue();
            TransactionHistory transHistory = empTransService.getTransHistory(empId);
            for (Range<LocalDate> dateRange : dates.asRanges()) {
                affectedSupIds.addAll(transHistory.getEffectiveSupervisorIds(dateRange).values());
            }
        }
        return affectedSupIds;
    }

    /**
     * Check for updates to supervisor overrides and update the cache as necessary
     */
    @WorkInProgress(author = "sam", since = "11/2/2015", desc = "insufficient live testing")
    @Scheduled(fixedDelayString = "${cache.poll.delay.supervisors:60000}")
    private void syncSupervisorCache() {
        logger.info("Checking for supervisor override updates...");
        Set<Integer> modifiedSups = new HashSet<>(getOvrUpdatedSups());
        modifiedSups.forEach(this::cachePrimarySupEmpGroup);
        logger.info("Refreshed {} supervisor emp groups", modifiedSups.size());
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