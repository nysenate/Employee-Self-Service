package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A service that can provide a set of active PersonnelTasks required for all employees within a specific domain.
 *
 * The returned tasks do not necessarily include ALL tasks, though all {@link PersonnelTaskDomainSource} implementations
 * should collectively be able to do this.
 */
public interface PersonnelTaskDomainSource<TaskType extends PersonnelTask> {

    /**
     * Get a task type covered by this source.
     *
     * @return {@link PersonnelTaskType}
     */
    PersonnelTaskType getTaskType();

    /**
     * Returns a set of {@link PersonnelTaskId} assigned within the scope of this {@link PersonnelTaskDomainSource}.
     *
     * @return {@link Set<PersonnelTaskId>}
     * @param activeOnly
     */
    Set<PersonnelTaskId> getTaskIds(boolean activeOnly);

    /**
     * Get task details given a task number.
     *
     * (The full task id is not needed since this source covers only a specific task type.)
     *
     * @param taskNumber int
     * @return {@link PersonnelTask}
     * @throws PersonnelTaskNotFoundEx - if no task exists with the given number.
     */
    TaskType getPersonnelTask(int taskNumber) throws PersonnelTaskNotFoundEx;

    /**
     * Get a list of all active {@link PersonnelTask} for this source.
     *
     * @return {@link List<PersonnelTask>}
     * @param activeOnly
     */
    default List<TaskType> getTasks(boolean activeOnly) {
        return getTaskIds(activeOnly).stream()
                .map(taskId -> getPersonnelTask(taskId.getTaskNumber()))
                .collect(Collectors.toList());
    }
}
