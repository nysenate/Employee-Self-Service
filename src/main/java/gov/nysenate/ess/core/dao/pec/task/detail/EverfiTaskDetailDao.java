package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * {@link PersonnelTaskDetailDao} for {@link EverfiCourseTask}s
 */
@Repository
public class EverfiTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<EverfiCourseTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.EVERFI_COURSE;
    }

    @Override
    public EverfiCourseTask getTaskDetails(PersonnelTask task) {
        List<EverfiCourseTask> everfiCourseTasks = localNamedJdbc.query(
                Query.SELECT_EVERFI_COURSE.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", task.getTaskId()),
                new EverfiCourseRowMapper(task)
        );
        if (everfiCourseTasks.isEmpty()) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return everfiCourseTasks.get(0);
        }
    }

    private static class EverfiCourseRowMapper implements RowMapper<EverfiCourseTask> {
        private final PersonnelTask personnelTask;

        private EverfiCourseRowMapper(PersonnelTask personnelTask) {
            this.personnelTask = personnelTask;
        }

        @Override
        public EverfiCourseTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new EverfiCourseTask(personnelTask, rs.getString("url"));
        }
    }

    private enum Query implements BasicSqlQuery {
        SELECT_EVERFI_COURSE("SELECT * FROM ${essSchema}.everfi_course WHERE task_id = :taskId"),
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
