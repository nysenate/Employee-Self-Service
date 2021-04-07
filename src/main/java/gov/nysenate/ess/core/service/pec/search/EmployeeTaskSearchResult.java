package gov.nysenate.ess.core.service.pec.search;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Collection;

/**
 * A search result containing an employee and tasks assigned to that employee.
 */
public class EmployeeTaskSearchResult {

    private final Employee employee;
    private final ImmutableList<PersonnelTaskAssignment> tasks;

    public EmployeeTaskSearchResult(Employee employee, Collection<PersonnelTaskAssignment> tasks) {
        this.employee = employee;
        this.tasks = ImmutableList.copyOf(tasks);
    }

    public Employee getEmployee() {
        return employee;
    }

    public ImmutableList<PersonnelTaskAssignment> getTasks() {
        return tasks;
    }
}
