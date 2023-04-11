package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.service.pec.notification.AssignmentWithTask;

import java.util.List;

/**
 * A service responsible for generating {@link PersonnelTaskAssignment}s for active employees.
 */
public interface PersonnelTaskAssigner {

    /**
     * Generates tasks for unassigned active personnel tasks for each active employee.
     */
    List<AssignmentWithTask> assignTasks(boolean updateDb);

    /**
     * Generates tasks for unassigned active personnel tasks for a single active employee.
     * @param empId int - employee id of the chosen employee.
     * @param updateDb boolean - ipf the database should be updated.
     */
    List<AssignmentWithTask> assignTasks(int empId, boolean updateDb);

    /**
     * Updates a tasks completion status assigned to an employee
     */
    void updateAssignedTaskCompletion(int empID, int updateEmpID, boolean completed, int taskID);

    /**
     * Updates a tasks assignment status for a task assigned to an employee
     */
    void updateAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID);

    /**
     * Assigns due dates to active, incomplete tasks in the database.
     */
    void generateDueDatesForExistingTaskAssignments();
}
