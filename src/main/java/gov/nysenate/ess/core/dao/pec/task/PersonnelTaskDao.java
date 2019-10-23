package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

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
}
