package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

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
}
