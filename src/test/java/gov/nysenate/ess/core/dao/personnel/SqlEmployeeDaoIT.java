package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.OutputUtils;
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
public class SqlEmployeeDaoIT extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDaoIT.class);

    @Autowired
    private EmployeeDao employeeDao;

    @Test
    public void testGetEmployeeById_validIdReturnsEmployee() throws Exception {
        int validId = 1719;
        Employee emp = employeeDao.getEmployeeById(validId);
        assertNotNull(emp);
        assertEquals(validId, emp.getEmployeeId());
//        logger.debug(OutputUtils.toJson(emp));
    }

    @Test
    public void testGetEmployeeByEmail_validIdReturnsEmployee() throws Exception {
        String validEmail = "stouffer@nysenate.gov";
        Employee emp = employeeDao.getEmployeeByEmail(validEmail);
        assertNotNull(emp);
        assertEquals(validEmail, emp.getEmail());
//        logger.debug(OutputUtils.toJson(emp));
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeById_invalidIdThrowsEmployeeNotFoundEx() throws Exception {
        assertNotNull(employeeDao);
        int invalidEmpId = 999999;
        employeeDao.getEmployeeById(invalidEmpId);
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeByEmail_invalidEmailThrowsEmployeeNotFoundEx() throws Exception {
        assertNotNull(employeeDao);
        String invalidEmail = "moose@kitten.com";
        employeeDao.getEmployeeByEmail(invalidEmail);
    }

    @Test
    public void getLastUpdateTimeTest() {
        logger.info("Last update was {}", employeeDao.getLastUpdateTime());
    }

    @Test
    public void getUpdatedEmployeesTest() {
        final LocalDateTime lastUpdateTime = employeeDao.getLastUpdateTime();
        // Get updated employees back to 1 day before the last update time
        List<Employee> updatedEmps = employeeDao.getUpdatedEmployees(lastUpdateTime.minusDays(1));
        Optional<LocalDateTime> updatedEmpsLatestUpdateTime = updatedEmps.stream()
                .map(Employee::getUpdateDateTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);
        assertTrue("getUpdatedEmployees() should return at least 1 result from 1 day before getLastUpdateTime",
                updatedEmpsLatestUpdateTime.isPresent());
        assertEquals("latest update time from getUpdatedEmployees() should equal getLastUpdateTime()",
                lastUpdateTime, updatedEmpsLatestUpdateTime.get());

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
        assertTrue("Duplicate employee records exist for empIds: " + dups, dups.size() == 0);
        logger.info("Active employee count: " + employees.size());
    }

    @Test
    public void getActiveEmpsTest() {
        logger.info("{}", employeeDao.getActiveEmployeeIds().size());
    }
}
