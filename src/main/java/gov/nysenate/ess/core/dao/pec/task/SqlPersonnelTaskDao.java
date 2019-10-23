package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlPersonnelTaskDao extends SqlBaseDao implements PersonnelTaskDao {

    @Override
    public List<PersonnelTask> getAllTasks() {
        return localNamedJdbc.query(SqlPersonnelTaskQuery.SELECT_ALL_TASKS.getSql(schemaMap()), taskRowMapper);
    }

    private static final RowMapper<PersonnelTask> taskRowMapper = (rs, rowNum) ->
            new PersonnelTask(
                    rs.getInt("task_id"),
                    PersonnelTaskType.valueOf(rs.getString("task_type")),
                    rs.getString("title"),
                    getLocalDateTime(rs, "effective_date_time"),
                    getLocalDateTime(rs, "end_date_time"),
                    rs.getBoolean("active")
            );
}
