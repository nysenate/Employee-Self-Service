package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDepartmentHeadId {

    public static final Pattern pattern = Pattern.compile("Senator (\\S+) (\\S+) ?(\\w*).*");

    /**
     * Attempts to return the employeeId for the Department Head for the given senator departmentName.
     *
     * This is only meant to be used for senator departments, they are named after the senator
     * so we are able to match them to an Employee.
     *
     * @param departmentName
     * @param employees
     * @return The employeeId of the department head for the {@code departmentName} department, or 0
     * if no match was found.
     */
    public static int forSenatorDepartment(String departmentName, Collection<Employee> employees) {
        Matcher matcher = pattern.matcher(departmentName);
        if (matcher.matches()) {
            String firstName = matcher.group(1);
            String lastName = matcher.group(3).isEmpty() ? matcher.group(2) : matcher.group(3);

            Employee emp = employees.stream()
                    .filter(e -> e.getFirstName().equals(firstName) && e.getLastName().equals(lastName))
                    .findFirst()
                    .orElse(null);

            return emp == null ? 0 : emp.getEmployeeId();
        } else {
            return 0;
        }
    }
}
