package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GetDepartmentHeadId {

    public static final Pattern pattern = Pattern.compile("Senator (\\S+) (\\S+) ?(\\w*).*");

    /**
     * Attempts to find the department head for a given department name from a list of employees.
     * <p>
     * This is only meant to be used for senator departments, they are named after the senator
     * so we are able to match them to an Employee.
     *
     * @param departmentName A name of a department.
     * @param employees      A Collection of Employee's.
     * @return An employeeId belonging to the likely department head of {@code departmentName} department,
     * or 0 if no likely matches were found.
     */
    public static int forSenatorDepartment(String departmentName, Collection<Employee> employees) {
        if (departmentName == null) {
            return 0;
        }

        int matchingEmpId = 0;
        Matcher matcher = pattern.matcher(departmentName);
        if (matcher.matches()) {
            String firstName = matcher.group(1);
            String lastName = matcher.group(3).isEmpty() ? matcher.group(2) : matcher.group(3);

            Set<Employee> matches = employees.stream()
                    .filter(e -> e.getFirstName().equals(firstName) && e.getLastName().equals(lastName))
                    .collect(Collectors.toSet());

            if (matches.size() == 1) {
                matchingEmpId = matches.stream()
                        .map(Employee::getEmployeeId)
                        .findFirst()
                        .orElse(0);
            }
        }
        return matchingEmpId;
    }
}
