package gov.nysenate.ess.core.dao.personnel;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class SqlEmployeeDaoIT extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDaoIT.class);

    @Autowired
    private EmployeeDao employeeDao;

    @Test
    public void testGetEmployeeById_validIdReturnsEmployee() {
        int validId = 1719;
        Employee emp = employeeDao.getEmployeeById(validId);
        assertNotNull(emp);
        assertEquals(validId, emp.getEmployeeId());
    }

    @Test
    public void testGetEmployeeByEmail_validIdReturnsEmployee() {
        String validEmail = "stouffer@nysenate.gov";
        Employee emp = employeeDao.getEmployeeByEmail(validEmail);
        assertNotNull(emp);
        assertEquals(validEmail, emp.getEmail());
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeById_invalidIdThrowsEmployeeNotFoundEx() {
        assertNotNull(employeeDao);
        int invalidEmpId = 999999;
        employeeDao.getEmployeeById(invalidEmpId);
    }

    @Test(expected = EmployeeNotFoundEx.class)
    public void testGetEmployeeByEmail_invalidEmailThrowsEmployeeNotFoundEx() {
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
    public void testGetActiveEmployess_returnsUniqueEmployeeList() {
        Map<Integer, Employee> dupMapCheck = new HashMap<>();
        Set<Integer> dups = new HashSet<>();
        Set<Employee> employees = employeeDao.getActiveEmployees();
        assertNotNull(employees);
        assertFalse("At least one active employee is returned", employees.isEmpty());
        for (Employee e : employees) {
            if (dupMapCheck.get(e.getEmployeeId()) != null) {
                dups.add(e.getEmployeeId());
            }
            else {
                dupMapCheck.put(e.getEmployeeId(), e);
            }
        }
        assertTrue("Duplicate employee records exist for empIds: " + dups, dups.isEmpty());
        logger.info("Active employee count: " + employees.size());
    }

    @Test
    public void getActiveEmpsTest() {
        logger.info("Testing active employee methods");
        Set<Integer> activeEmployeeIds = employeeDao.getActiveEmployeeIds();
        Set<Employee> employees = employeeDao.getActiveEmployees();
        assertEquals("Get active employees returns same number of emps as get active employee ids",
                employees.size(), activeEmployeeIds.size());
        Set<Integer> activeEmpsEmpIds = employees.stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        assertEquals(activeEmployeeIds, activeEmpsEmpIds);
    }

    @Test
    public void nameSearchTest() {
        // Pick an employee from active employees
        Set<Integer> activeEmployeeIds = employeeDao.getActiveEmployeeIds();
        Optional<Integer> anyOldEmpIdOpt = activeEmployeeIds.stream().findFirst();
        assertTrue("There should be at least one active employee", anyOldEmpIdOpt.isPresent());
        final int expectedEmpId = anyOldEmpIdOpt.get();

        // Generate search text using fragments of employee's name
        Employee employee = employeeDao.getEmployeeById(expectedEmpId);
        String searchText = employee.getLastName() + ", " + employee.getFirstName().charAt(0);
        EmployeeSearchBuilder esb = new EmployeeSearchBuilder()
                .setName(searchText);

        // Search using the search text, and verify that the employee is in the results.
        PaginatedList<Employee> results = employeeDao.searchEmployees(esb, LimitOffset.ALL);
        boolean containsExpectedId = results.getResults().stream()
                .anyMatch(e -> e.getEmployeeId() == expectedEmpId);
        assertTrue("Search results must contain expected emp id", containsExpectedId);
        assertTrue("Search results should be fewer than all active emps",
                results.getTotal() < activeEmployeeIds.size());
    }

    @Test
    public void freeTextSearch_isInsensitiveToWordOrder() {
        Employee employee = anyActiveEmployee();
        final int expectedEmpId = employee.getEmployeeId();

        // "First Last" and the reversed "Last First" should both find the employee.
        String naturalOrder = employee.getFirstName() + " " + employee.getLastName();
        String reversedOrder = employee.getLastName() + " " + employee.getFirstName();

        assertTrue("Free-text search should match natural word order",
                freeTextSearchContains(naturalOrder, expectedEmpId));
        assertTrue("Free-text search should match reversed word order",
                freeTextSearchContains(reversedOrder, expectedEmpId));
    }

    @Test
    public void freeTextSearch_ignoresMiddleInitialBetweenNames() {
        // Pick an employee that actually has a middle initial - the legacy substring search would
        // fail to match "First Last" for these because the initial sits between the two names.
        Employee employee = employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getInitial() != null && !e.getInitial().trim().isEmpty())
                .filter(e -> e.getFirstName() != null && e.getLastName() != null)
                .min(Comparator.comparingInt(Employee::getEmployeeId))
                .orElseThrow(() -> new AssertionError("Expected at least one employee with a middle initial"));

        String firstAndLast = employee.getFirstName() + " " + employee.getLastName();
        assertTrue("Free-text search should match first + last even with a middle initial in between",
                freeTextSearchContains(firstAndLast, employee.getEmployeeId()));
    }

    @Test
    public void freeTextSearch_matchesUid() {
        Employee employee = employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getUid() != null && !e.getUid().trim().isEmpty())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected at least one employee with a uid"));

        assertTrue("Free-text search should match on uid/email",
                freeTextSearchContains(employee.getUid(), employee.getEmployeeId()));
    }

    @Test
    public void freeTextSearch_matchesNameSuffix() {
        // The suffix (e.g. "Jr") is folded onto the end of the name, so including it as a search
        // token must still find the employee. Suffixes are rare, so skip if the DB snapshot has none.
        Optional<Employee> suffixedEmp = employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getSuffix() != null && !e.getSuffix().trim().isEmpty())
                .filter(e -> e.getFirstName() != null && e.getLastName() != null)
                .findFirst();
        if (suffixedEmp.isEmpty()) {
            logger.warn("No active employee with a name suffix found - skipping suffix search test");
            return;
        }
        Employee employee = suffixedEmp.get();
        String nameWithSuffix = employee.getFirstName() + " " + employee.getLastName() + " " + employee.getSuffix();
        assertTrue("Free-text search should match a term that includes the name suffix",
                freeTextSearchContains(nameWithSuffix, employee.getEmployeeId()));
    }

    @Test
    public void freeTextSearch_ranksExactNameMatchFirst() {
        Employee employee = anyActiveEmployee();
        String fullName = employee.getFirstName() + " " + employee.getLastName();

        EmployeeSearchBuilder esb = new EmployeeSearchBuilder()
                .setName(fullName)
                .setFreeTextNameMatch(true);
        List<Employee> results = employeeDao.searchEmployees(esb, LimitOffset.ALL).getResults();
        assertFalse("Exact name search should return results", results.isEmpty());
        // The exact-name match should be ranked at (tied for) the top. Names are compared normalized,
        // since e.g. "O'Brien" and "Obrien" are indistinguishable to the search and tie for first.
        Employee top = results.getFirst();
        assertEquals("An exact full-name match should be ranked first",
                normalizedName(employee), normalizedName(top));
    }

    @Test
    public void freeTextSearch_findsEveryPunctuatedNameByFullName() {
        // Names with hyphens, apostrophes or periods used to be unfindable, because only the search
        // term had its punctuation stripped. Check every such active employee, not just one.
        List<Employee> punctuated = employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getFirstName() != null && e.getLastName() != null)
                .filter(e -> (e.getFirstName() + e.getLastName()).matches(".*[^A-Za-z0-9 ].*"))
                .sorted(Comparator.comparingInt(Employee::getEmployeeId))
                .toList();
        if (punctuated.isEmpty()) {
            logger.warn("No active employee with a punctuated name found - skipping punctuated name test");
            return;
        }
        List<String> notFound = punctuated.stream()
                .filter(e -> !freeTextSearchContains(e.getFirstName() + " " + e.getLastName(), e.getEmployeeId()))
                .map(e -> e.getEmployeeId() + " " + e.getFirstName() + " " + e.getLastName())
                .toList();
        assertEquals("Every punctuated name should be findable by its full name", List.of(), notFound);
    }

    @Test
    public void freeTextSearch_matchesHyphenatedNameWithoutHyphen() {
        Optional<Employee> hyphenatedEmp = employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getFirstName() != null && e.getLastName() != null)
                .filter(e -> e.getLastName().contains("-"))
                .min(Comparator.comparingInt(Employee::getEmployeeId));
        if (hyphenatedEmp.isEmpty()) {
            logger.warn("No active employee with a hyphenated last name found - skipping hyphen test");
            return;
        }
        Employee employee = hyphenatedEmp.get();
        String spaced = employee.getFirstName() + " " + employee.getLastName().replace("-", " ");
        String joined = employee.getFirstName() + " " + employee.getLastName().replace("-", "");
        assertTrue("Free-text search should match a hyphenated name typed with a space",
                freeTextSearchContains(spaced, employee.getEmployeeId()));
        assertTrue("Free-text search should match a hyphenated name typed without the hyphen",
                freeTextSearchContains(joined, employee.getEmployeeId()));
    }

    @Test
    public void freeTextSearch_matchesFullEmail() {
        Employee employee = employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getEmail() != null && e.getEmail().contains("@"))
                .min(Comparator.comparingInt(Employee::getEmployeeId))
                .orElseThrow(() -> new AssertionError("Expected at least one employee with an email"));

        assertTrue("Free-text search should match a full email address",
                freeTextSearchContains(employee.getEmail(), employee.getEmployeeId()));
    }

    /** Deterministic choice of test subject; set iteration order changes whenever employee records do. */
    private Employee anyActiveEmployee() {
        return employeeDao.getActiveEmployees().stream()
                .filter(e -> e.getFirstName() != null && e.getLastName() != null)
                .min(Comparator.comparingInt(Employee::getEmployeeId))
                .orElseThrow(() -> new AssertionError("Expected at least one active employee"));
    }

    private static String normalizedName(Employee employee) {
        return String.join(" ",
                EmployeeSearchBuilder.tokenizeSearchTerm(employee.getFirstName() + " " + employee.getLastName()));
    }

    private boolean freeTextSearchContains(String term, int expectedEmpId) {
        EmployeeSearchBuilder esb = new EmployeeSearchBuilder()
                .setName(term)
                .setFreeTextNameMatch(true);
        return employeeDao.searchEmployees(esb, LimitOffset.ALL).getResults().stream()
                .anyMatch(e -> e.getEmployeeId() == expectedEmpId);
    }

    @Test
    public void rctrHdSearchTest() {
        // Pick an employee from active employees
        final Set<Integer> activeEmployeeIds = employeeDao.getActiveEmployeeIds();
        Optional<Integer> anyOldEmpIdOpt = activeEmployeeIds.stream().findFirst();
        assertTrue("There should be at least one active employee", anyOldEmpIdOpt.isPresent());
        final int firstEmpId = anyOldEmpIdOpt.get();
        final String firstRCHCode = employeeDao.getEmployeeById(firstEmpId).getRespCenterHeadCode();
        assertNotNull(firstRCHCode);

        //Pick another employee with a different resp. ctr. head
        Optional<Employee> secondEmpOpt = activeEmployeeIds.stream()
                .filter(empId -> empId != firstEmpId)
                .map(employeeDao::getEmployeeById)
                .filter(emp -> !firstRCHCode.equals(emp.getRespCenterHeadCode()))
                .findFirst();
        assertTrue("There should be more than one RCH", secondEmpOpt.isPresent());
        final int secondEmpId = secondEmpOpt.get().getEmployeeId();
        final String secondRCHCode = secondEmpOpt.get().getRespCenterHeadCode();
        assertNotNull(secondRCHCode);

        // Perform search with individual codes
        EmployeeSearchBuilder firstRCHQuery = new EmployeeSearchBuilder()
                .setRespCtrHeadCodes(Collections.singleton(firstRCHCode));
        Set<Integer> firstRCHQueryEmpIds = employeeDao.searchEmployees(firstRCHQuery, LimitOffset.ALL)
                .getResults().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        assertTrue("Emp should be in results for own RCH", firstRCHQueryEmpIds.contains(firstEmpId));

        EmployeeSearchBuilder secondRCHQuery = new EmployeeSearchBuilder()
                .setRespCtrHeadCodes(Collections.singleton(secondRCHCode));
        Set<Integer> secondRCHEmpIds = employeeDao.searchEmployees(secondRCHQuery, LimitOffset.ALL)
                .getResults().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        assertTrue("Emp should be in results for own RCH", secondRCHEmpIds.contains(secondEmpId));

        assertTrue("Query results for 2 different rchs should be distinct",
                Sets.intersection(firstRCHQueryEmpIds, secondRCHEmpIds).isEmpty());

        // Perform search with both codes
        EmployeeSearchBuilder combinedRCHQuery = new EmployeeSearchBuilder()
                .setRespCtrHeadCodes(Sets.newHashSet(firstRCHCode, secondRCHCode));
        Set<Integer> combinedRCHQueryEmpIds = employeeDao.searchEmployees(combinedRCHQuery, LimitOffset.ALL)
                .getResults().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        assertEquals("Combined query should return all emps from all RCHs",
                Sets.union(firstRCHQueryEmpIds, secondRCHEmpIds), combinedRCHQueryEmpIds);
    }

    @Test
    public void activeEmpSearchTest() {
        logger.warn("Start: " + LocalDateTime.now());
        logger.warn("Getting active Ids: " + LocalDateTime.now());
        Set<Integer> activeEmployeeIds = employeeDao.getActiveEmployeeIds();
        EmployeeSearchBuilder activeSearchBuilder = new EmployeeSearchBuilder()
                .setActive(true);
        logger.warn("Getting from search: " + LocalDateTime.now());
        Set<Integer> activeSearchEmpIds = employeeDao.searchEmployees(activeSearchBuilder, LimitOffset.ALL).getResults().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        assertEquals("Active empid search  should match active emp query",
                activeEmployeeIds, activeSearchEmpIds);

        logger.warn("Getting from search 2: " + LocalDateTime.now());
        Set<Integer> allEmpIds = employeeDao.getAllEmployees().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());

        EmployeeSearchBuilder inactiveSearchBuilder = new EmployeeSearchBuilder()
                .setActive(false);
        logger.warn("Getting from search 3: " + LocalDateTime.now());
        Set<Integer> inactiveSearchEmpIds = employeeDao.searchEmployees(inactiveSearchBuilder, LimitOffset.ALL)
                .getResults().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        assertTrue("Inactive and active searches must have no overlap",
                Sets.intersection(activeSearchEmpIds, inactiveSearchEmpIds).isEmpty());
        assertEquals("Active and Inactive searches must combine to a set of all emps",
                allEmpIds, Sets.union(activeEmployeeIds, inactiveSearchEmpIds));
        logger.warn("End: " + LocalDateTime.now());
    }

    @Test
    public void emptySearchTest() {
        Set<Integer> allEmpIds = employeeDao.getAllEmployees().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        Set<Integer> emptySearchEmpIds = employeeDao.searchEmployees(new EmployeeSearchBuilder(), LimitOffset.ALL)
                .getResults().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());

        assertEquals("Empty Search should return all emps", allEmpIds, emptySearchEmpIds);
    }
}
