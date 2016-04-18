package gov.nysenate.ess.core.service.personnel;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.*;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EssCachedEmployeeInfoService implements EmployeeInfoService
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmployeeInfoService.class);

    @Autowired protected Environment env;
    @Autowired protected EmployeeDao employeeDao;
    @Autowired protected EmpTransactionService transService;
    @Autowired private LocationService locationService;
    @Autowired protected EventBus eventBus;
    @Autowired protected EhCacheManageService cacheManageService;

    protected volatile Cache empCache;
    private LocalDateTime lastUpdateDateTime;

    @PostConstruct
    protected void init() {
        this.eventBus.register(this);
        this.empCache = this.cacheManageService.registerEternalCache("employees");
        lastUpdateDateTime = employeeDao.getLastUpdateTime();
    }

    /** {@inheritDoc} */
    @Override
    public Employee getEmployee(int empId) throws EmployeeNotFoundEx {
        empCache.acquireReadLockOnKey(empId);
        Element elem = empCache.get(empId);
        empCache.releaseReadLockOnKey(empId);
        if (elem != null) {
            return (Employee) elem.getObjectValue();
        }
        else {
            return getEmployeeAndPutInCache(empId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Employee getEmployee(int empId, LocalDate effectiveDate) throws EmployeeNotFoundEx {
        Employee employee = getEmployee(empId);
        TransactionHistory transHistory = transService.getTransHistory(empId);
        employee.setActive(transHistory.latestValueOf("CDEMPSTATUS", effectiveDate, true).orElse("I").equals("A"));
        employee.setSupervisorId(
            Integer.parseInt(transHistory.latestValueOf("NUXREFSV", effectiveDate, true).orElse("0")));
        employee.setJobTitle(transHistory.latestValueOf("CDEMPTITLE", effectiveDate, false).orElse(null));
        try {
            employee.setPayType(PayType.valueOf(transHistory.latestValueOf("CDPAYTYPE", effectiveDate, true).orElse(null)));
        } catch (NullPointerException | IllegalArgumentException ignored) {}
        setRespCenterAtDate(employee, transHistory, effectiveDate);
        employee.setWorkLocation(getWorkLocAtDate(employee, transHistory, effectiveDate));
        return employee;
    }

    /** {@inheritDoc} */
    @Override
    public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);
        RangeSet<LocalDate> employedDates = TreeRangeSet.create();
        RangeUtils.toRangeMap(transHistory.getEffectiveEmpStatus(DateUtils.ALL_DATES))
                .asMapOfRanges().forEach((range, employed) -> {
            if (employed) {
                employedDates.add(range);
            }
        });
        return employedDates;
    }

    /** --- Caching Methods --- */

    /**
     * Fetches the employee from the database with the given empId and saves the Employee object
     * into the employee cache.
     * @param empId int - Employee Id
     * @return Employee
     */
    private Employee getEmployeeAndPutInCache(int empId) {
        Employee employee = employeeDao.getEmployeeById(empId);
        cacheEmployee(employee);
        return employee;
    }

    /**
     * Saves the given employee info into the employee cache
     * @param employee Employee
     */
    private void cacheEmployee(Employee employee) {
        int empId = employee.getEmployeeId();
        fixFullNameFormat(employee);
        empCache.acquireWriteLockOnKey(empId);
        empCache.put(new Element(empId, employee));
        empCache.releaseWriteLockOnKey(empId);
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.employees:43200000}")
    @WorkInProgress(author = "sam", since = "10/30/2015", desc = "insufficient live testing")
    public void syncEmployeeCache() {
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
                    .filter(emp -> emp.isActive() || empCache.get(emp.getEmployeeId()) != null)
                    .peek(this::cacheEmployee)
                    .count();
            logger.debug("cached {} of {} updated employees", cached, updatedEmps.size());
        } else {
            logger.debug("found no updated employees");
        }
    }

    public void cacheActiveEmployees() {
        if (env.acceptsProfiles("!test")) {
            logger.debug("Refreshing employee cache..");
            empCache.removeAll();
            Set<Employee> activeEmployees = employeeDao.getActiveEmployees();
            activeEmployees.forEach(this::cacheEmployee);
            logger.debug("Finished refreshing employee cache: {} employees cached", activeEmployees.size());
        }
    }

    /** --- Formatting Methods --- */

    private void fixFullNameFormat(Employee employee) {
        String fullName =
            employee.getFirstName() + " " +
            ((StringUtils.isNotBlank(employee.getInitial())) ? (employee.getInitial() + " ") : "") +
            employee.getLastName() + " " +
            ((StringUtils.isNotBlank(employee.getSuffix())) ? employee.getSuffix() : "");
        employee.setFullName(WordUtils.capitalizeFully(fullName.toLowerCase()).trim().replaceAll("\\s+", " "));
    }

    /** --- Internal Methods --- */

    /**
     *
     * These methods extract the most up to date value of a particular employee field from a transaction history
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
        getRespHeadAtDate(rctr, transHistory, effectiveDate);
    }

    private static void setAgencyAtDate(ResponsibilityCenter respCtr, TransactionHistory transHistory, LocalDate effectiveDate) {
        if (respCtr.getAgency() == null) {
            respCtr.setAgency(new Agency());
        }
        Agency agency = respCtr.getAgency();
        agency.setCode(transHistory.latestValueOf("CDAGENCY", effectiveDate, true).orElse(agency.getCode()));
    }

    private static void getRespHeadAtDate(ResponsibilityCenter respCtr, TransactionHistory transHistory, LocalDate effectiveDate) {
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
    private Location getWorkLocAtDate(Employee emp, TransactionHistory transHistory, LocalDate effectiveDate) {
        boolean hasWorkLocation = transHistory.latestValueOf("CDLOCAT", effectiveDate, true).isPresent();
        if (!hasWorkLocation) {
            return null;
        }
        String locCode = transHistory.latestValueOf("CDLOCAT", effectiveDate, true).get();
        // All employee assigned locations have a work location type.
        return locationService.getLocation(new LocationId(locCode, LocationType.WORK));
    }
}
