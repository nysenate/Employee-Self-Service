package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Collection;

/**
 * A search result containing an employee and tasks assigned to that employee.
 */
public class EmployeeTaskSearchResult {

    private final Employee employee;
    private final ImmutableList<PersonnelAssignedTask> tasks;

    public EmployeeTaskSearchResult(Employee employee, Collection<PersonnelAssignedTask> tasks) {
        this.employee = employee;
        this.tasks = ImmutableList.copyOf(tasks);
    }

    public Employee getEmployee() {
        return employee;
    }

    public ImmutableList<PersonnelAssignedTask> getTasks() {
        return tasks;
    }
}
