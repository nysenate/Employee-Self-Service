package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static gov.nysenate.ess.core.dao.pec.SqlPersonnelAssignedTaskQuery.*;

@Repository
public class SqlPersonnelAssignedTaskDao extends SqlBaseDao implements PersonnelAssignedTaskDao {

    @Override
    public List<PersonnelAssignedTask> getTasksForEmp(int empId) {
        return localNamedJdbc.query(
                SELECT_TASKS_FOR_EMP.getSql(schemaMap()),
                getEmpIdParams(empId),
                patRowMapper
        );
    }

    @Override
    public List<PersonnelAssignedTask> getTasks(PATQueryBuilder query) {
        return localNamedJdbc.query(
                SELECT_TASKS_QUERY.getSql(schemaMap()),
                getPATQueryParams(query),
                patRowMapper
        );
    }

    @Override
    public void updatePersonnelAssignedTask(PersonnelAssignedTask task) {
        MapSqlParameterSource params = getPATParams(task);
        int updated = localNamedJdbc.update(UPDATE_TASK.getSql(schemaMap()), params);
        if (updated == 0) {
            localNamedJdbc.update(INSERT_TASK.getSql(schemaMap()), params);
        } else if (updated != 1) {
            throw new IllegalStateException("Too many updates (" + updated + ") occurred for " + task);
        }
    }

    private static final RowMapper<PersonnelAssignedTask> patRowMapper = (rs, rowNum) ->
            new PersonnelAssignedTask(
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

    private MapSqlParameterSource getPATParams(PersonnelAssignedTask task) {
        return getEmpIdParams(task.getEmpId())
                .addValue("taskType", task.getTaskType().name())
                .addValue("taskNumber", task.getTaskNumber())
                .addValue("timestamp", toDate(task.getTimestamp()))
                .addValue("updateUserId", task.getUpdateUserId())
                .addValue("completed", task.isCompleted());
    }

    private MapSqlParameterSource getPATQueryParams(PATQueryBuilder queryBuilder) {
        return new MapSqlParameterSource()
                .addValue("empId", queryBuilder.getEmpId())
                .addValue("taskType",
                        Optional.ofNullable(queryBuilder.getTaskType()).map(Enum::name).orElse(null))
                .addValue("taskNumber", queryBuilder.getTaskNumber())
                .addValue("completed", queryBuilder.getCompleted());
    }
}
