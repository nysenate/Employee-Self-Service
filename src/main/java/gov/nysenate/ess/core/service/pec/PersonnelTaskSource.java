package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

import java.util.List;
import java.util.Set;

/**
 * Can provide a set of all active personnel tasks.
 */
public interface PersonnelTaskSource {

    /**
     * Gets a set of all currently active {@link PersonnelTaskId}s
     * @return {@link Set<PersonnelTaskId>}
     * @param activeOnly
     */
    Set<PersonnelTaskId> getAllPersonnelTaskIds(boolean activeOnly);

    /**
     * Get task details given a task id.
     *
     * @param taskId {@link PersonnelTaskId}
     * @return {@link PersonnelTask}
     * @throws PersonnelTaskNotFoundEx - if no task is found with the given id.
     */
    PersonnelTask getPersonnelTask(PersonnelTaskId taskId) throws PersonnelTaskNotFoundEx;

    /**
     * Get a list of all active personnel tasks.
     * @return {@link List<PersonnelTask>}
     * @param activeOnly
     */
    List<PersonnelTask> getPersonnelTasks(boolean activeOnly);
}
