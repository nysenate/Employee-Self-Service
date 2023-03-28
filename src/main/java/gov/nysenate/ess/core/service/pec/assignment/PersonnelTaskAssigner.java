package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.service.pec.notification.EmployeeEmail;

import java.util.List;

/**
 * A service responsible for generating {@link PersonnelTaskAssignment}s for active employees.
 */
public interface PersonnelTaskAssigner {

    /**
     * Generates tasks for unassigned active personnel tasks for each active employee.
     */
    void assignTasks();

    /**
     * Returns a list of invite emails that would be sent if assignment is run.
     */
    List<EmployeeEmail> getInviteEmails();

    /**
     * Generates tasks for unassigned active personnel tasks for a single active employee.
     *
     * @param empId int - employee id of the chosen employee.
     */
    void assignTasks(int empId);

    /**
     * Updates a tasks completion status assigned to an employee
     */
    void updateAssignedTaskCompletion(int empID, int updateEmpID, boolean completed, int taskID);

    /**
     * Updates a tasks assignment status for a task assigned to an employee
     */
    void updateAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID);
}
