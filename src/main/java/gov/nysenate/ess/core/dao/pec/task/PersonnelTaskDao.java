package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.util.HashMap;
import java.util.List;

/**
 * DAO for retrieving {@link PersonnelTask}s
 */
public interface PersonnelTaskDao {

    /**
     * Get all {@link PersonnelTask}s
     * @return {@link List<PersonnelTask>}
     */
    List<PersonnelTask> getAllTasks();

    /**
     * Get a task by its id.
     * @param taskId int
     * @return {@link PersonnelTask}
     */
    PersonnelTask getPersonnelTask(int taskId);

    /**
     * Update a personnel assigned task completion status
     */
    void updatePersonnelAssignedTaskCompletion(int empID, int updateEmpID, boolean completed, int taskID);

    /**
     * Update a personnel assigned task
     */
    void updatePersonnelAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID);

    /**
     * Get a hash map of everfi content ID's to personnel task ids
     */
    HashMap<Integer, String> getEverfiContentIDs();

    /**
     * Get a hash map of everfi Assignment ID's to personnel task ids
     */
    HashMap<Integer, Integer> getEverfiAssignmentIDs();
}
