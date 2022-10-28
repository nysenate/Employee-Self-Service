package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;

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
}
