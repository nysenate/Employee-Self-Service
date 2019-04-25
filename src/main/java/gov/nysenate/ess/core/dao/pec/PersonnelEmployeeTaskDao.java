package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.model.pec.PersonnelEmployeeTask;

import java.util.List;

/**
 * Supports read and write operations to data store of {@link PersonnelEmployeeTask} data.
 */
public interface PersonnelEmployeeTaskDao {

    /**
     * Get all tasks for the given employee.
     *
     * @param empId int
     * @return {@link List<PersonnelEmployeeTask>}
     */
    List<PersonnelEmployeeTask> getTasksForEmp(int empId);

    /**
     * Add or update the given {@link PersonnelEmployeeTask} to the db.
     *
     * @param task {@link PersonnelEmployeeTask}
     */
    void updatePersonnelEmployeeTask(PersonnelEmployeeTask task);
}
