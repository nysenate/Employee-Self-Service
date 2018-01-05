package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Exception thrown when emails are attempted to be sent to inactive employees.
 * Contains a set of the inactive {@link Employee}s
 */
public class InactiveEmployeeEmailEx extends RuntimeException {

    private final ImmutableSet<Employee> employees;

    public InactiveEmployeeEmailEx(Set<Employee> employees) {
        super("Employees are no longer active and cannot receive emails: " + getEmpIds(employees));
        this.employees = ImmutableSet.copyOf(employees);
    }

    public ImmutableSet<Employee> getEmployees() {
        return employees;
    }

    private static Set<Integer> getEmpIds(Set<Employee> employees) {
        return employees.stream()
                .map(Employee::getEmployeeId)
                .collect(toSet());
    }
}
