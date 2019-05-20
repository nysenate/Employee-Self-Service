package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

import java.util.List;

/**
 * Supports read and write operations to data store of {@link PersonnelAssignedTask} data.
 */
public interface PersonnelAssignedTaskDao {

    /**
     * Get all tasks assigned to the given employee.
     *
     * @param empId int
     * @return {@link List<PersonnelAssignedTask>}
     */
    List<PersonnelAssignedTask> getTasksForEmp(int empId);

    /**
     * Get a specific task assigned to the given employee.
     *
     * @param empId int
     * @param taskId {@link List<PersonnelAssignedTask>}
     * @return {@link PersonnelAssignedTask}
     * @throws PersonnelAssignedTaskNotFoundEx if no such task exists.
     */
    PersonnelAssignedTask getTaskForEmp(int empId, PersonnelTaskId taskId) throws PersonnelAssignedTaskNotFoundEx;

    /**
     * Get a list of tasks matching the given query
     *
     * @param query {@link PATQueryBuilder}
     * @return {@link List<PersonnelAssignedTask>}
     */
    List<PersonnelAssignedTask> getTasks(PATQueryBuilder query);

    /**
     * Add or update the given {@link PersonnelAssignedTask} to the db.
     *
     * @param task {@link PersonnelAssignedTask}
     */
    void updatePersonnelAssignedTask(PersonnelAssignedTask task);

    /**
     * Deactivates the task assignment.
     *
     * @param empId int
     * @param taskId {@link PersonnelTaskId}
     */
    void deactivatePersonnelAssignedTask(int empId, PersonnelTaskId taskId);
}
