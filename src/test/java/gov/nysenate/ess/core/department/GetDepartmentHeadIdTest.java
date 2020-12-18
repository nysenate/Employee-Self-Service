package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.junit.experimental.categories.Category(UnitTest.class)
public class GetDepartmentHeadIdTest {

    private static Set<Employee> employees;

    @BeforeClass
    public static void beforeClass() {
        Employee rachel = new Employee();
        rachel.setEmployeeId(1);
        rachel.setFirstName("Rachel");
        rachel.setLastName("May");

        Employee patrick = new Employee();
        patrick.setEmployeeId(2);
        patrick.setFirstName("Patrick");
        patrick.setInitial("M");
        patrick.setLastName("Gallivan");

        Employee toby = new Employee();
        toby.setEmployeeId(3);
        toby.setFirstName("Toby");
        toby.setLastName("Stavisky");

        Employee joseph = new Employee();
        joseph.setEmployeeId(4);
        joseph.setFirstName("Joseph");
        joseph.setLastName("Addabbo");

        Employee frederick = new Employee();
        frederick.setEmployeeId(5);
        frederick.setFirstName("Frederick");
        frederick.setLastName("Akshar");

        Employee elizabeth = new Employee();
        elizabeth.setEmployeeId(6);
        elizabeth.setFirstName("Elizabeth");
        elizabeth.setLastName("Little");

        employees = Sets.newHashSet(rachel, patrick, toby, joseph, frederick, elizabeth);
    }

    @Test
    public void ifDepartmentNameDoesNotStartWithSenator_returnZero() {
        int empId = GetDepartmentHeadId.forSenatorDepartment("Rachel May", Sets.newHashSet());
        assertEquals(0, empId);
    }

    @Test
    public void findsDeptIdWhenMissingMiddleName() {
        String departmentName = "Senator Rachel May";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(1, empId);
    }

    @Test
    public void findsDeptIdWithMiddleName() {
        String departmentName = "Senator Patrick M. Gallivan";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(2, empId);

        departmentName = "Senator Toby Ann Stavisky";
        empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(3, empId);
    }

    @Test
    public void testEdgeCases() {
        String departmentName = "Senator Joseph P. Addabbo, Jr.";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(4, empId);

        departmentName = "Senator Frederick J. Akshar II";
        empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(5, empId);

        departmentName = "Senator Elizabeth O'C. Little";
        empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(6, empId);
    }
}
