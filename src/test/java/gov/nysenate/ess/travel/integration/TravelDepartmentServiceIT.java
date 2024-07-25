package gov.nysenate.ess.travel.integration;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.department.Department;
import gov.nysenate.ess.travel.department.DepartmentNotFoundEx;
import gov.nysenate.ess.travel.department.TravelDepartmentService;
import gov.nysenate.ess.web.SillyTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

import static org.junit.Assert.fail;

@Category(SillyTest.class)
public class TravelDepartmentServiceIT extends BaseTest {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelDepartmentService departmentService;
    private String deptHdAssignmentFilePath = "/Users/nysenate/depthdcsvvalidate.csv";

    /**
     * Used to help validate the department head algorithm. Accepts a csv file of expected employee to department head
     * mappings and validates each one using the department head algorithm. Any inconsistencies are printed out.
     */
    @Test
    public void validateDeptHdAssignmentFile() throws IOException, DepartmentNotFoundEx {
        boolean failure = false;
        List<String> failureNotices = new ArrayList<>();
        try (Reader in = new FileReader(deptHdAssignmentFilePath)) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL
                    .withHeader("Employee Id", "Employee Full Name", "Department Head Emp Id", "Department Head Full Name")
                    .withSkipHeaderRecord(true)
                    .parse(in);
            for (CSVRecord record : records) {
                Employee employee = employeeInfoService.getEmployee(Integer.parseInt(record.get("Employee Id")));
                Employee expectedDeptHd = employeeInfoService.getEmployee(Integer.parseInt(record.get("Department Head Emp Id")));

                Department actualDepartment = departmentService.departmentForEmployee(employee);
                Employee actualDeptHd = actualDepartment.getHead();

                if (!expectedDeptHd.equals(actualDeptHd)) {
                    String notice = String.format("Failure: %s (%s) expected deptHdId is %s (%s) but was %s (%s)",
                            employee.getFullName(), employee.getEmployeeId(),
                            expectedDeptHd.getFullName(), expectedDeptHd.getEmployeeId(),
                            actualDeptHd.getFullName(), actualDeptHd.getEmployeeId());
                    failureNotices.add(notice);
                    failure = true;
                }
            }

            if (failure) {
                System.out.println("========== FAILURES ==========");
                for (String notice : failureNotices) {
                    System.out.println(notice);
                }
                System.out.println("========== TOTAL: " + failureNotices.size() + " ==========");
                fail();
            }
        }
    }

    /**
     * Exports calculated department head for every employee to a csv file.
     * Can optionally ignore employees who work under senators.
     */
    @Test
    public void dumpDeptHdAssignmentsToCsv() throws IOException, DepartmentNotFoundEx {
        String FILENAME = ""; // path and filename of where to save the csv.
        boolean INCLUDE_SENATOR_DEPARTMENTS = false;

        if (StringUtils.isBlank(FILENAME)) {
            fail("Must set FILENAME.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME));
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord("Employee Id", "Employee Full Name", "Department Head Emp Id", "Department Head Full Name");

            for (Department dept : getAllDepartments()) {
                if (!INCLUDE_SENATOR_DEPARTMENTS && dept.getHead().isSenator()) {
                    continue;
                }

                if (dept != null) {
                    for (Employee sub : dept.getSubordinates()) {
                        printer.printRecord(
                                sub.getEmployeeId(),
                                sub.getFullName(),
                                dept.getHead().getEmployeeId(),
                                dept.getHead().getFullName()
                        );
                    }
                }
            }
        }
    }

    private TreeSet<Department> getAllDepartments() throws DepartmentNotFoundEx {
        TreeSet<Department> departments = new TreeSet<>(Comparator.comparing(o -> o.getHead().getLastName()));
        Set<Employee> allEmployees = employeeInfoService.getAllEmployees(true);
        for (Employee emp : allEmployees) {
            Department dept = departmentService.departmentForEmployee(emp);
            if (dept != null) {
                departments.add(dept);
            }
        }
        return departments;
    }
}
