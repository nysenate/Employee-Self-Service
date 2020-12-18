package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.junit.experimental.categories.Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
public class DepartmentDaoIT extends BaseTest {

    @Autowired private DepartmentDao departmentDao;
    private static final int deptHdId = 99999999;
    private Department testDepartment;

    @Before
    public void before() {
        LdapDepartment ldapDepartment = new LdapDepartment("Test", Arrays.asList(1, 2, 3));
        testDepartment = new Department(0, ldapDepartment, deptHdId, true);
    }

    @Test
    public void canGetDepartmentById() {
        Department expected = departmentDao.updateDepartment(testDepartment);
        Department actual = departmentDao.getDepartment(expected.getId());
        assertEquals(expected, actual);
    }

    @Test
    public void canGetAllDepartments() {
        departmentDao.updateDepartment(testDepartment);
        Set<Department> departments = departmentDao.getDepartments();
        assertTrue(departments.contains(testDepartment));
    }

    @Test
    public void canGetEmptyDepartment() {
        LdapDepartment empty = new LdapDepartment("empty");
        Department emptyDepartment = new Department(0, empty, deptHdId, true);
        Department expected = departmentDao.updateDepartment(emptyDepartment);
        Department actual = departmentDao.getDepartment(expected.getId());
        assertEquals(expected, actual);
    }

    @Test
    public void departmentShouldInsert() {
        Department expected = testDepartment;
        Department actual = departmentDao.updateDepartment(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void departmentShouldUpdate() {
        Department department = departmentDao.updateDepartment(testDepartment);
        assertEquals(testDepartment, department); // verify nothing was changed.

        Department expected = department.setActive(false);
        Department actual = departmentDao.updateDepartment(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void canCheckIfEmpIsADepartmentHead() {
        departmentDao.updateDepartment(testDepartment);
        assertTrue(departmentDao.isEmployeeADepartmentHead(deptHdId));
    }

    @Test
    public void canGetEmployeeDepartment() {
        departmentDao.updateDepartment(testDepartment);
        Department actual = departmentDao.getEmployeeDepartment(1);
        assertEquals(testDepartment, actual);
    }
}
