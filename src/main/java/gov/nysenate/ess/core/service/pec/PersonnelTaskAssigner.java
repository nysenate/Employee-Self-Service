package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;

/**
 * A service responsible for generating {@link PersonnelAssignedTask}s for active employees.
 */
public interface PersonnelTaskAssigner {

    /**
     * Generates tasks for unassigned active personnel tasks for each active employee.
     */
    void assignTasks();

    /**
     * Generates tasks for unassigned active personnel tasks for a single active employee.
     *
     * @param empId int - employee id of the chosen employee.
     */
    void assignTasks(int empId);
}
