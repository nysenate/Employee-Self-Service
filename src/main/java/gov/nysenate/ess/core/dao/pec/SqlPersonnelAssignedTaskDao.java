package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.dao.pec.SqlPersonnelAssignedTaskQuery.*;

@Repository
public class SqlPersonnelAssignedTaskDao extends SqlBaseDao implements PersonnelAssignedTaskDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlPersonnelAssignedTaskDao.class);

    @Override
    public List<PersonnelAssignedTask> getTasksForEmp(int empId) {
        return localNamedJdbc.query(
                SELECT_TASKS_FOR_EMP.getSql(schemaMap()),
                getEmpIdParams(empId),
                patRowMapper
        );
    }

    @Override
    public PersonnelAssignedTask getTaskForEmp(int empId, PersonnelTaskId taskId)
            throws PersonnelAssignedTaskNotFoundEx {
        try {
            return localNamedJdbc.queryForObject(
                    SELECT_SPECIFIC_TASK_FOR_EMP.getSql(schemaMap()),
                    getEmpIdTaskIdParams(empId, taskId),
                    patRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new PersonnelAssignedTaskNotFoundEx(empId, taskId);
        }
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

    @Override
    public void setTaskComplete(int empId, PersonnelTaskId taskId, int updateEmpId) {
        MapSqlParameterSource params = getEmpIdTaskIdParams(empId, taskId);
        params.addValue("updateUserId", updateEmpId);
        int updated = localNamedJdbc.update(UPDATE_COMPLETE_TASK.getSql(schemaMap()), params);
        if (updated == 0) {
            localNamedJdbc.update(INSERT_COMPLETE_TASK.getSql(schemaMap()), params);
        } else if (updated != 1) {
            throw new IllegalStateException(
                    "Too many updates (" + updated + ") occurred for assigned task - " +
                            "empId:" + empId + " taskId:" + taskId );
        }
    }

    @Override
    public void deactivatePersonnelAssignedTask(int empId, PersonnelTaskId taskId) {
        MapSqlParameterSource empIdTaskIdParams = getEmpIdTaskIdParams(empId, taskId);
        int updated = localNamedJdbc.update(DEACTIVATE_TASK.getSql(schemaMap()), empIdTaskIdParams);
        if (updated != 1) {
            logger.warn("Unexpected update count for PAT deactivation!");
            logger.warn("empId={}, taskId={}, update_count={}", empId, taskId, updated);
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

    private MapSqlParameterSource getEmpIdTaskIdParams(int empId, PersonnelTaskId taskId) {
        return getEmpIdParams(empId)
                .addValue("taskType", taskId.getTaskType().name())
                .addValue("taskNumber", taskId.getTaskNumber());
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
                .addValue("active", queryBuilder.getActive())
                .addValue("taskType",
                        Optional.ofNullable(queryBuilder.getTaskType()).map(Enum::name).orElse(null))
                .addValue("completed", queryBuilder.getCompleted())
                .addValue("completedFrom", toDate(queryBuilder.getCompletedFrom()))
                .addValue("completedTo", toDate(queryBuilder.getCompletedTo()))
                .addValue("taskIdsPresent", queryBuilder.getTaskIds() == null)
                .addValue("taskIds", Optional.ofNullable(queryBuilder.getTaskIds())
                        .map(taskIds -> taskIds.stream()
                                .map(tid -> new Object[]{tid.getTaskType().name(), tid.getTaskNumber()})
                                .collect(Collectors.toList())
                        )
                        .orElse(null))
                ;
    }
}
