package gov.nysenate.ess.core.dao.pec.assignment;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

import java.util.List;

/**
 * Supports read and write operations to data store of {@link PersonnelTaskAssignment} data.
 */
public interface PersonnelTaskAssignmentDao {

    /**
     * Get all tasks assigned to the given employee.
     *
     * @param empId int
     * @return {@link List<PersonnelTaskAssignment>}
     */
    List<PersonnelTaskAssignment> getTasksForEmp(int empId);

    /**
     * Get a specific task assigned to the given employee.
     *
     * @param empId int
     * @param taskId int
     * @return {@link PersonnelTaskAssignment}
     * @throws PersonnelTaskAssignmentNotFoundEx if no such task exists.
     */
    PersonnelTaskAssignment getTaskForEmp(int empId, int taskId) throws PersonnelTaskAssignmentNotFoundEx;

    /**
     * Get a list of tasks matching the given query
     *
     * @param query {@link PTAQueryBuilder}
     * @return {@link List<PersonnelTaskAssignment>}
     */
    List<PersonnelTaskAssignment> getTasks(PTAQueryBuilder query);

    /**
     * Add or update the given {@link PersonnelTaskAssignment} to the db.
     *
     * @param task {@link PersonnelTaskAssignment}
     */
    void updateAssignment(PersonnelTaskAssignment task);

    /**
     * Mark a task as completed for the given employee.
     *
     * @param empId int
     * @param taskId int
     * @param updateEmpId int - user performing the update
     */
    void setTaskComplete(int empId, int taskId, int updateEmpId);

    /**
     * Deactivates the task assignment.
     *
     * @param empId int
     * @param taskId int
     */
    void deactivatePersonnelTaskAssignment(int empId, int taskId);
}
