package gov.nysenate.ess.core.service.pec.task;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;

import java.util.List;
import java.util.Set;

/**
 * Service providing {@link PersonnelTask}s
 */
public interface PersonnelTaskService {

    /**
     * Get a set of all task ids.
     *
     * @param activeOnly boolean
     * @return int set
     */
    Set<Integer> getAllTaskIds(boolean activeOnly);

    /**
     * Get the {@link PersonnelTask} for the given id
     *
     * @param taskId int
     * @param getDetail boolean
     * @return {@link PersonnelTask}
     * @throws PersonnelTaskNotFoundEx if no task exists for the given id.
     */
    PersonnelTask getPersonnelTask(int taskId, boolean getDetail) throws PersonnelTaskNotFoundEx;

    default PersonnelTask getPersonnelTask(int taskId) throws PersonnelTaskNotFoundEx {
        return getPersonnelTask(taskId, false);
    }

    /**
     * Get a list of all active personnel tasks.
     * @return {@link List <PersonnelTask>}
     * @param activeOnly if only active tasks should be retrieved.
     * @param getDetail if a detailed version should be returned.
     */
    List<PersonnelTask> getPersonnelTasks(boolean activeOnly, boolean getDetail);

    default List<PersonnelTask> getPersonnelTasks(boolean activeOnly) {
        return getPersonnelTasks(activeOnly, false);
    }

    /**
     * Gets a list of active personnel tasks for a particular assignment group.
     * @param assignmentGroup {@link PersonnelTaskAssignmentGroup}
     * @return {@link List<PersonnelTask>}
     */
    List<PersonnelTask> getActiveTasksInGroup(PersonnelTaskAssignmentGroup assignmentGroup);
}
