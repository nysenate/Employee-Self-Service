package gov.nysenate.ess.core.service.personnel;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.*;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.core.service.base.LocationService;
import gov.nysenate.ess.core.service.cache.EmployeeEhCache;
import gov.nysenate.ess.core.service.cache.EssCacheManager;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CachedEmployeeInfoService extends EmployeeEhCache<Employee>
        implements EmployeeInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CachedEmployeeInfoService.class);

    private final EmployeeDao employeeDao;
    private final ActiveEmployeeIdService employeeIdService;
    private final EmpTransactionService transService;
    private final LocationService locationService;
    private LocalDateTime lastUpdateDateTime;

    @Autowired
    public CachedEmployeeInfoService(EmployeeDao employeeDao,
                                     ActiveEmployeeIdService employeeIdService,
                                     EmpTransactionService transService,
                                     LocationService locationService) {
        this.employeeDao = employeeDao;
        this.employeeIdService = employeeIdService;
        this.transService = transService;
        this.locationService = locationService;
        lastUpdateDateTime = employeeDao.getLastUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee getEmployee(int empId) throws EmployeeNotFoundEx {
        Employee employee = cache.get(empId);
        if (employee == null) {
            employee = employeeDao.getEmployeeById(empId);
            cache.put(employee.getEmployeeId(), employee);
        }
        return employee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee getEmployee(int empId, LocalDate effectiveDate) throws EmployeeNotFoundEx {
        Employee employee = new Employee(getEmployee(empId));
        TransactionHistory transHistory = transService.getTransHistory(empId);
        employee.setActive(transHistory.latestValueOf("CDEMPSTATUS", effectiveDate, true).orElse("I").equals("A"));
        employee.setSupervisorId(
                Integer.parseInt(transHistory.latestValueOf("NUXREFSV", effectiveDate, true).orElse("0")));
        employee.setJobTitle(transHistory.latestValueOf("CDEMPTITLE", effectiveDate, false).orElse(null));
        try {
            employee.setPayType(PayType.valueOf(transHistory.latestValueOf("CDPAYTYPE", effectiveDate, true).orElse(null)));
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
        setRespCenterAtDate(employee, transHistory, effectiveDate);
        employee.setWorkLocation(getWorkLocAtDate(transHistory, effectiveDate));
        return employee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        return transHistory.getActiveDates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getEmployeesMostRecentContinuousServiceDate(int empId) {
        RangeSet<LocalDate> activeServiceDates =
                getEmployeeActiveDatesService(empId);
        Set<Range<LocalDate>> descendingSetRange = activeServiceDates.asDescendingSetOfRanges(); //[[2023-01-01..+∞), [2007-02-08..2018-12-15)]
        Optional<Range<LocalDate>> firstString = descendingSetRange.stream().findFirst();
        if (firstString.isPresent()) {
            Range<LocalDate> activeServiceRange = firstString.get();
            return activeServiceRange.lowerEndpoint();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getEmployeeActiveYearsService(int empId, boolean fiscalYears) {
        RangeSet<LocalDate> rangeSet = getEmployeeActiveDatesService(empId);
        Range<LocalDate> pastAndPresent = Range.atMost(LocalDate.now());
        return rangeSet.asRanges().stream()
                // Only use dates from past and present
                .filter(range -> range.isConnected(pastAndPresent))
                .map(range -> range.intersection(pastAndPresent))
                // Ensure that the range is bounded, no employees have been here since the dawn of time
                .filter(range -> range.hasLowerBound() && range.hasUpperBound())
                // Convert to a range of years
                .map(range -> DateUtils.toYearRange(range, fiscalYears))
                .map(range -> range.canonical(DiscreteDomain.integers()))
                .flatMapToInt(range -> IntStream.range(range.lowerEndpoint(), range.upperEndpoint()))
                .boxed()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> getActiveEmpIds() {
        return employeeIdService.getActiveEmployeeIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Employee> getAllEmployees(boolean activeOnly) {
        return activeOnly ? getActiveEmployeesFromCache() : employeeDao.getAllEmployees();
    }

    private Set<Employee> getActiveEmployeesFromCache() {
        return employeeIdService.getActiveEmployeeIds().stream()
                .map(this::getEmployee)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<Employee> searchEmployees(String term, boolean activeOnly, LimitOffset limitOffset) {
        return employeeDao.searchEmployees(term, activeOnly, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<Employee> searchEmployees(EmployeeSearchBuilder employeeSearchBuilder, LimitOffset limitOffset) {
        return employeeDao.searchEmployees(employeeSearchBuilder, limitOffset);
    }

    // --- CachingService Implemented Methods ---

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheType cacheType() {
        return CacheType.EMPLOYEE;
    }

    @Override
    protected void warmCache() {
        var activeEmployees = employeeDao.getActiveEmployees();
        for (var emp : activeEmployees) {
            cache.put(emp.getEmployeeId(), emp);
        }
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.employees:43200000}")
    @WorkInProgress(author = "sam", since = "10/30/2015", desc = "insufficient live testing")
    private void syncEmployeeCache() {
        // Get employees updated since the last check
        logger.debug("syncing employee cache: getting emps updated since {}", lastUpdateDateTime);
        List<Employee> updatedEmps = employeeDao.getUpdatedEmployees(lastUpdateDateTime);

        if (!updatedEmps.isEmpty()) {
            logger.debug("found {} updated employees, updating the cache", updatedEmps.size());
            // Update the last update date time with the most recent update date
            lastUpdateDateTime = updatedEmps.stream()
                    .map(Employee::getUpdateDateTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo).orElse(lastUpdateDateTime);

            logger.debug("new latest employee update date: {}", lastUpdateDateTime);

            // Insert all updated employees that are active or already cached
            long cached = updatedEmps.stream()
                    .filter(emp -> emp.isActive() || cache.get(emp.getEmployeeId()) != null)
                    .peek(employee -> cache.put(employee.getEmployeeId(), employee))
                    .count();
            logger.debug("cached {} of {} updated employees", cached, updatedEmps.size());
        } else {
            logger.debug("found no updated employees");
        }
    }

    /**
     * These methods extract the most up-to-date value of a particular employee field from a transaction history
     * effective after a given date
     * TODO: you can't get every value of these objects from the transaction layer
     */
    private static void setRespCenterAtDate(Employee emp, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (emp.getRespCenter() == null) {
            emp.setRespCenter(new ResponsibilityCenter());
        }
        ResponsibilityCenter rctr = emp.getRespCenter();
        rctr.setCode(Integer.parseInt(transHistory.latestValueOf("CDRESPCTR", effectiveDate, true).orElse(Integer.toString(rctr.getCode()))));
        setAgencyAtDate(rctr, transHistory, effectiveDate);
        setRespHeadAtDate(rctr, transHistory, effectiveDate);
    }

    private static void setAgencyAtDate(ResponsibilityCenter respCtr, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (respCtr.getAgency() == null) {
            respCtr.setAgency(new Agency());
        }
        Agency agency = respCtr.getAgency();
        agency.setCode(transHistory.latestValueOf("CDAGENCY", effectiveDate, true).orElse(agency.getCode()));
    }

    private static void setRespHeadAtDate(ResponsibilityCenter respCtr, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (respCtr.getHead() == null) {
            respCtr.setHead(new ResponsibilityHead());
        }
        ResponsibilityHead rHead = respCtr.getHead();
        rHead.setCode(transHistory.latestValueOf("CDRESPCTRHD", effectiveDate, false).orElse(rHead.getCode()));
    }

    /**
     * Get an employees work location at a particular date.
     * Return the location or null if they were not assigned a location.
     */
    private Location getWorkLocAtDate(TransactionHistory transHistory, LocalDate effectiveDate) {
        boolean hasWorkLocation = transHistory.latestValueOf("CDLOCAT", effectiveDate, true).isPresent();
        if (!hasWorkLocation) {
            return null;
        }
        String locCode = transHistory.latestValueOf("CDLOCAT", effectiveDate, true).get();
        // All employee assigned locations have a work location type.
        return locationService.getLocation(new LocationId(locCode, LocationType.WORK));
    }
}
