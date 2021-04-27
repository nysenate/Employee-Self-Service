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

        var john = new Employee();
        john.setEmployeeId(7);
        john.setFirstName("John");
        john.setLastName("Smith");

        var johnA = new Employee();
        johnA.setEmployeeId(8);
        johnA.setFirstName("John");
        johnA.setInitial("A");
        johnA.setLastName("Smith");

        employees = Sets.newHashSet(rachel, patrick, toby, joseph, frederick, elizabeth, john, johnA);
    }

    @Test
    public void givenNullDeptName_thenReturnEmpId0() {
        var empId = GetDepartmentHeadId.forSenatorDepartment(null, employees);
        assertEquals(0, empId);
    }

    @Test
    public void givenEmptyDeptName_thenReturnEmpId0() {
        var empId = GetDepartmentHeadId.forSenatorDepartment("", employees);
        assertEquals(0, empId);
    }

    @Test
    public void givenDeptNameNotPrefixedWithSenator_thenReturnEmpId0() {
        int empId = GetDepartmentHeadId.forSenatorDepartment("Rachel May", employees);
        assertEquals(0, empId);
    }

    @Test
    public void givenNoMiddleName_thenMatchCanBeFound() {
        String departmentName = "Senator Rachel May";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(1, empId);
    }

    @Test
    public void givenFullNames_thenMatchCanBeFound() {
        String departmentName = "Senator Patrick M. Gallivan";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(2, empId);

        departmentName = "Senator Toby Ann Stavisky";
        empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(3, empId);
    }

    @Test
    public void givenMiddleNameMissingFromDeptName_thenMatchCanBeFound() {
        String departmentName = "Senator Patrick Gallivan";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(2, empId);
    }

    @Test
    public void givenMiddleNameMissingFromEmpData_thenMatchCanBeFound() {
        String departmentName = "Senator Rachel Z. May";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(1, empId);
    }

    @Test
    public void givenMultipleEmpsWithSameName_thenNoMatchFound() {
        var departmentName = "Senator John Smith";
        int empId = GetDepartmentHeadId.forSenatorDepartment(departmentName, employees);
        assertEquals(0, empId);
    }

    @Test
    public void testNameEdgeCases() {
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
