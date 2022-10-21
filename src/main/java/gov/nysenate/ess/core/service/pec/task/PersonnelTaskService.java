package gov.nysenate.ess.core.service.pec.task;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;

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
     * @return {@link PersonnelTask}
     * @throws PersonnelTaskNotFoundEx if no task exists for the given id.
     */
    PersonnelTask getPersonnelTask(int taskId) throws PersonnelTaskNotFoundEx;

    /**
     * Get a list of all active personnel tasks.
     * @return {@link List <PersonnelTask>}
     * @param activeOnly
     */
    List<PersonnelTask> getPersonnelTasks(boolean activeOnly);

    /**
     * Gets a list of active personnel tasks for a particular assignment group.
     * @param assignmentGroup {@link PersonnelTaskAssignmentGroup}
     * @return {@link List<PersonnelTask>}
     */
    List<PersonnelTask> getActiveTasksInGroup(PersonnelTaskAssignmentGroup assignmentGroup);
}
