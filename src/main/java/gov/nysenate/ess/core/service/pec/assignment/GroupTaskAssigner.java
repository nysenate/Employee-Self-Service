package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.pec.notification.EmployeeEmail;

import java.util.List;
import java.util.Set;

/**
 * A service that assigns {@link PersonnelTask}s of a specific {@link PersonnelTaskAssignmentGroup} to employees.
 */
public interface GroupTaskAssigner {

    /**
     * @return the {@link PersonnelTaskAssignmentGroup} assigned by this service.
     */
    PersonnelTaskAssignmentGroup getTargetGroup();

    /**
     * Check and assign {@link PersonnelTask}s of the target {@link PersonnelTaskAssignmentGroup} for the given employee.
     * @param empId int - emp id
     * @return int - number of new assignments.
     */
    int assignGroupTasks(int empId);

    /**
     * Returns a Set of task IDs that are in this group for this employee.
     */
    Set<Integer> getRequiredTaskIds(int empId);
}
