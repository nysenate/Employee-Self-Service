package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link PersonnelTaskDetailDao} for {@link gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask}s
 */
@Repository
public class EthicsCourseTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<EthicsCourseTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.ETHICS_COURSE;
    }

    @Override
    public EthicsCourseTask getTaskDetails(PersonnelTask task) {
        return localNamedJdbc.queryForObject(
                Query.SELECT_ETHICS_COURSE.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", task.getTaskId()),
                new EthicsCourseRowMapper(task)
        );
    }

    private static class EthicsCourseRowMapper implements RowMapper<EthicsCourseTask> {
        private final PersonnelTask personnelTask;

        private EthicsCourseRowMapper(PersonnelTask personnelTask) {
            this.personnelTask = personnelTask;
        }

        @Override
        public EthicsCourseTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new EthicsCourseTask(personnelTask, rs.getString("url"));
        }
    }

    private enum Query implements BasicSqlQuery {
        SELECT_ETHICS_COURSE("SELECT * FROM ${essSchema}.ethics_course WHERE task_id = :taskId"),
        ;

        private final String sql;

        Query(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }
}
