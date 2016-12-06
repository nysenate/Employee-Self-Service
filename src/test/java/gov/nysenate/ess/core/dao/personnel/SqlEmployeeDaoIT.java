package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class SqlEmployeeDaoIT extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDaoIT.class);

    @Autowired
    private EmployeeDao employeeDao;

    @Test
    public void testGetEmployeeById_validIdReturnsEmployee() throws Exception {
        int validId = 1719;
        Employee emp = employeeDao.getEmployeeById(validId);
        assertNotNull(emp);
        //assertEquals(validId, emp.getEmployeeId());
        logger.debug(OutputUtils.toJson(emp));
    }

    @Test
    public void testGetEmployeeByEmail_validIdReturnsEmployee() throws Exception {
        String validEmail = "stouffer@nysenate.gov";
        Employee emp = employeeDao.getEmployeeByEmail(validEmail);
        assertNotNull(emp);
        assertEquals(validEmail, emp.getEmail());
        logger.debug(OutputUtils.toJson(emp));
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeById_invalidIdThrowsEmployeeNotFoundEx() throws Exception {
        assertNotNull(employeeDao);
        int invalidEmpId = 999999;
        Employee emp = employeeDao.getEmployeeById(invalidEmpId);
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeByEmail_invalidEmailThrowsEmployeeNotFoundEx() throws Exception {
        assertNotNull(employeeDao);
        String invalidEmail = "moose@kitten.com";
        Employee emp = employeeDao.getEmployeeByEmail(invalidEmail);
    }

    @Test
    public void getLastUpdateTimeTest() {
        logger.info("Last update was {}", employeeDao.getLastUpdateTime());
    }

    @Test
    public void getUpdatedEmployeesTest() {
        LocalDateTime fromDateTime = LocalDate.of(2016, 1, 2).atStartOfDay();
        List<Employee> updatedEmps = employeeDao.getUpdatedEmployees(fromDateTime);
        LocalDateTime latestUpdate = updatedEmps.stream()
                .map(Employee::getUpdateDateTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(DateUtils.LONG_AGO.atStartOfDay());
        logger.info("{} emps updated since {}", updatedEmps.size(), fromDateTime);
        LocalDateTime queriedLatestUpdate = employeeDao.getLastUpdateTime();
        assertEquals(latestUpdate, queriedLatestUpdate);
        logger.info("latest update was {}", latestUpdate);
    }

    @Test
    public void testGetActiveEmployess_returnsUniqueEmployeeList() throws Exception {
        Map<Integer, Employee> dupMapCheck = new HashMap<>();
        Set<Integer> dups = new HashSet<>();
        Set<Employee> employees = employeeDao.getActiveEmployees();
        assertNotNull(employees);
        assertTrue("At least one active employee is returned", employees.size() > 0);
        for (Employee e : employees) {
            if (dupMapCheck.get(e.getEmployeeId()) != null) {
                dups.add(e.getEmployeeId());
            }
            else {
                dupMapCheck.put(e.getEmployeeId(), e);
            }
        }
        logger.info("Duplicates: " + dups);
        assertTrue("Duplicate employees records exist!", dups.size() == 0);
        logger.info("Employee count: " + employees.size());
    }

    @Test
    public void getActiveEmpsTest() {
        logger.info("{}", employeeDao.getActiveEmployeeIds().size());
    }
}
