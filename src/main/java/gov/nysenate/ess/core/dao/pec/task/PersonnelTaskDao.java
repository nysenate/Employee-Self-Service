package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.ethics.DateRangedEthicsCode;

import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DAO for retrieving {@link PersonnelTask}s
 */
public interface PersonnelTaskDao extends BaseDao {

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
     * Update a personnel assigned task active status
     */
    void updatePersonnelAssignedActiveStatus(int empID, int updateEmpID, boolean activeStatus, int taskID);

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

    /**
     * Gets the ethics code id for any ethics live task
     */
    int getEthicsCodeId(int taskId);

    /**
     * Update the ethics live codes table in the database with new codes
     */
    void updateEthicsCode(String code, int ethicsCodeId, int sequenceNo );

    void updateEthicsCode(String code, int ethicsCodeId, int sequenceNo, String startDate, String endDate);

    void insertEthicsCode(int ethicsCodeId, int sequenceNo, String Label, String code, LocalDateTime StartDate, LocalDateTime endDate);

    List<DateRangedEthicsCode> getEthicsCodes();
}
