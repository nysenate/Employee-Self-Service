package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

/**
 * A dao that loads detailed information for a specific task.
 * @param <DT> class representing the task details.
 */
public interface PersonnelTaskDetailDao<DT extends PersonnelTask> {

    /**
     * The task type served by this detail dao.
     *
     * @return {@link PersonnelTaskType}
     */
    PersonnelTaskType taskType();

    /**
     * Get details for a personnel task.
     *
     * @param task {@link PersonnelTask}
     * @return {@link DT}
     */
    DT getTaskDetails(PersonnelTask task);
}
