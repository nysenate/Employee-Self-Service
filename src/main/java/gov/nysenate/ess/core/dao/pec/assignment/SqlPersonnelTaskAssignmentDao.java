package gov.nysenate.ess.core.dao.pec.assignment;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentQuery.*;

@Repository
public class SqlPersonnelTaskAssignmentDao extends SqlBaseDao implements PersonnelTaskAssignmentDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlPersonnelTaskAssignmentDao.class);

    @Override
    public List<PersonnelTaskAssignment> getAssignmentsForEmp(int empId) {
        return localNamedJdbc.query(
                SELECT_TASKS_FOR_EMP.getSql(schemaMap()),
                getEmpIdParams(empId),
                patRowMapper
        );
    }

    @Override
    public PersonnelTaskAssignment getTaskForEmp(int empId, int taskId)
            throws PersonnelTaskAssignmentNotFoundEx {
        try {
            List<PersonnelTaskAssignment> personnelTaskAssignments = localNamedJdbc.query(
                    SELECT_SPECIFIC_TASK_FOR_EMP.getSql(schemaMap()),
                    getEmpIdTaskIdParams(empId, taskId),
                    patRowMapper
            );
            if (personnelTaskAssignments.isEmpty() || personnelTaskAssignments == null) {
                logger.error("FAILED TO GET PERSONNEL TASK ASSIGNMENT WITH ID: " + taskId + " FOR EMPLOYEE: " + empId);
                throw new PersonnelTaskAssignmentNotFoundEx(empId, taskId);
            }
            else {
                return personnelTaskAssignments.get(0);
            }
        } catch (EmptyResultDataAccessException ex) {
            throw new PersonnelTaskAssignmentNotFoundEx(empId, taskId);
        }
    }

    @Override
    public List<PersonnelTaskAssignment> getTasks(PTAQueryBuilder query) {
        return localNamedJdbc.query(
                SELECT_TASKS_QUERY.getSql(schemaMap()),
                getPATQueryParams(query),
                patRowMapper
        );
    }

    @Override
    public void updateAssignment(PersonnelTaskAssignment task) {
        MapSqlParameterSource params = getPATParams(task);
        int updated = localNamedJdbc.update(UPDATE_TASK.getSql(schemaMap()), params);
        if (updated == 0) {
            localNamedJdbc.update(INSERT_TASK.getSql(schemaMap()), params);
        } else if (updated != 1) {
            throw new IllegalStateException("Too many updates (" + updated + ") occurred for " + task);
        }
    }

    @Override
    public void setTaskComplete(int empId, int taskId, int updateEmpId) {
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
    public void deactivatePersonnelTaskAssignment(int empId, int taskId) {
        MapSqlParameterSource empIdTaskIdParams = getEmpIdTaskIdParams(empId, taskId);
        int updated = localNamedJdbc.update(DEACTIVATE_TASK.getSql(schemaMap()), empIdTaskIdParams);
        if (updated != 1) {
            logger.warn("Unexpected update count for PAT deactivation!");
            logger.warn("empId={}, taskId={}, update_count={}", empId, taskId, updated);
        }
    }

    private static final RowMapper<PersonnelTaskAssignment> patRowMapper = (rs, rowNum) ->
            new PersonnelTaskAssignment(
                    rs.getInt("task_id"),
                    rs.getInt("emp_id"),
                    getNullableInt(rs, "update_user_id"),
                    getLocalDateTime(rs, "timestamp"),
                    rs.getBoolean("completed"),
                    rs.getBoolean("active")
            );


    private MapSqlParameterSource getEmpIdParams(int empId) {
        return new MapSqlParameterSource("empId", empId);
    }

    private MapSqlParameterSource getEmpIdTaskIdParams(int empId, int taskId) {
        return getEmpIdParams(empId)
                .addValue("taskId", taskId);
    }

    private MapSqlParameterSource getPATParams(PersonnelTaskAssignment task) {
        return getEmpIdParams(task.getEmpId())
                .addValue("taskId", task.getTaskId())
                .addValue("timestamp", toDate(task.getUpdateTime()))
                .addValue("updateUserId", task.getUpdateEmpId())
                .addValue("completed", task.isCompleted())
                .addValue("active", task.isActive())
                ;
    }

    private MapSqlParameterSource getPATQueryParams(PTAQueryBuilder queryBuilder) {
        return new MapSqlParameterSource()
                .addValue("empId", queryBuilder.getEmpId())
                .addValue("active", queryBuilder.getActive())
                .addValue("taskType",
                        Optional.ofNullable(queryBuilder.getTaskType()).map(Enum::name).orElse(null))
                .addValue("completed", queryBuilder.getCompleted())
                .addValue("completedFrom", toDate(queryBuilder.getCompletedFrom()))
                .addValue("completedTo", toDate(queryBuilder.getCompletedTo()))
                .addValue("taskIdsPresent", queryBuilder.getTaskIds() == null)
                .addValue("taskIds", queryBuilder.getTaskIds())
                ;
    }
}
