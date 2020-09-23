package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.everfi.EverfiAssignmentID;
import gov.nysenate.ess.core.model.pec.everfi.EverfiContentID;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

import static gov.nysenate.ess.core.dao.pec.task.SqlPersonnelTaskQuery.*;

@Repository
public class SqlPersonnelTaskDao extends SqlBaseDao implements PersonnelTaskDao {

    @Override
    public List<PersonnelTask> getAllTasks() {
        return localNamedJdbc.query(SELECT_ALL_TASKS.getSql(schemaMap()), taskRowMapper);
    }

    @Override
    public PersonnelTask getPersonnelTask(int taskId) {
        return localNamedJdbc.queryForObject(
                SELECT_TASK_BY_ID.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", taskId),
                taskRowMapper
        );
    }


    @Override
    public void updatePersonnelAssignedTaskCompletion(int empID, int updateEmpID, boolean completed, int taskID) {
        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("updateUserId",updateEmpID);
        updateParams.addValue("completed",completed);
        updateParams.addValue("empId",empID);
        updateParams.addValue("taskId",taskID);

        int value = localNamedJdbc.update(UPDATE_TASK_COMPLETION.getSql(schemaMap()), updateParams );
    }

    @Override
    public void updatePersonnelAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID) {
        localJdbc.update(UPDATE_TASK_ASSIGNMENT.getSql(schemaMap()), assigned,updateEmpID,empID,taskID );
    }

    @Override
    public HashMap<String, Integer> getEverfiContentIDs() {
        List<EverfiContentID> everfiContentIDList =
                localJdbc.query(SELECT_EVERFI_CONTENT_IDS.getSql(schemaMap()), everfiContentIDRowMapper);

        HashMap<String, Integer> everfiContentIDMap = new HashMap<>();
        for (EverfiContentID everfiContentID: everfiContentIDList) {
            everfiContentIDMap.put(everfiContentID.getID(), everfiContentID.getTaskID());
        }

        return everfiContentIDMap;
    }

    @Override
    public HashMap<Integer, Integer> getEverfiAssignmentIDs() {
        List<EverfiAssignmentID> everfiAssignmentIDList =
                localJdbc.query(SELECT_EVERFI_ASSIGNMENT_IDS.getSql(schemaMap()), everfiAssignmentIDRowMapper);

        HashMap<Integer, Integer> everfiAssignmentIDMap = new HashMap<>();
        for (EverfiAssignmentID everfiAssignmentID: everfiAssignmentIDList) {
            everfiAssignmentIDMap.put(everfiAssignmentID.getID(), everfiAssignmentID.getTaskID());
        }

        return everfiAssignmentIDMap;
    }

    private static final RowMapper<PersonnelTask> taskRowMapper = (rs, rowNum) ->
            new PersonnelTask(
                    rs.getInt("task_id"),
                    PersonnelTaskType.valueOf(rs.getString("task_type")),
                    PersonnelTaskAssignmentGroup.valueOf(rs.getString("assignment_group")),
                    rs.getString("title"),
                    getLocalDateTime(rs, "effective_date_time"),
                    getLocalDateTime(rs, "end_date_time"),
                    rs.getBoolean("active")
            );

    private static final RowMapper<EverfiContentID> everfiContentIDRowMapper = (rs, rowNum) ->
            new EverfiContentID(
                    rs.getInt("task_id"),
                    rs.getString("everfi_content_id")
            );

    private static final RowMapper<EverfiAssignmentID> everfiAssignmentIDRowMapper = (rs, rowNum) ->
            new EverfiAssignmentID(
                    rs.getInt("task_id"),
                    rs.getInt("everfi_assignment_id")
            );
}
