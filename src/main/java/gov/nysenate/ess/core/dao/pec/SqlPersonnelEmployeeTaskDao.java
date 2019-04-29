package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelEmployeeTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.ess.core.dao.pec.SqlPersonnelEmployeeTaskQuery.*;

@Repository
public class SqlPersonnelEmployeeTaskDao extends SqlBaseDao implements PersonnelEmployeeTaskDao {

    @Override
    public List<PersonnelEmployeeTask> getTasksForEmp(int empId) {
        return localNamedJdbc.query(
                SELECT_TASKS_FOR_EMP.getSql(schemaMap()),
                getEmpIdParams(empId),
                petRowMapper
        );
    }

    @Override
    public void updatePersonnelEmployeeTask(PersonnelEmployeeTask task) {
        MapSqlParameterSource params = getPETParams(task);
        int updated = localNamedJdbc.update(UPDATE_TASK.getSql(schemaMap()), params);
        if (updated == 0) {
            localNamedJdbc.update(INSERT_TASK.getSql(schemaMap()), params);
        } else if (updated != 1) {
            throw new IllegalStateException("Too many updates (" + updated + ") occurred for " + task);
        }
    }

    private static final RowMapper<PersonnelEmployeeTask> petRowMapper = (rs, rowNum) ->
            new PersonnelEmployeeTask(
                    rs.getInt("emp_id"),
                    new PersonnelTaskId(
                            PersonnelTaskType.valueOf(rs.getString("task_type")),
                            rs.getInt("task_number")
                    ),
                    getLocalDateTime(rs, "timestamp"),
                    getNullableInt(rs, "update_user_id"),
                    rs.getBoolean("completed")

            );

    private MapSqlParameterSource getEmpIdParams(int empId) {
        return new MapSqlParameterSource("empId", empId);
    }

    private MapSqlParameterSource getPETParams(PersonnelEmployeeTask task) {
        return getEmpIdParams(task.getEmpId())
                .addValue("taskType", task.getTaskType().name())
                .addValue("taskNumber", task.getTaskNumber())
                .addValue("timestamp", toDate(task.getTimestamp()))
                .addValue("updateUserId", task.getUpdateUserId())
                .addValue("completed", task.isCompleted());
    }
}
