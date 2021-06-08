package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.moodle.MoodleCourseTask;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * {@link PersonnelTaskDetailDao} for {@link MoodleCourseTask}s
 */
@Repository
public class MoodleTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<MoodleCourseTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.MOODLE_COURSE;
    }

    @Override
    public MoodleCourseTask getTaskDetails(PersonnelTask task) {
        List<MoodleCourseTask> moodleCourseTasks = localNamedJdbc.query(
                Query.SELECT_MOODLE_COURSE.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", task.getTaskId()),
                new MoodleCourseRowMapper(task)
        );
        if (moodleCourseTasks.isEmpty() || moodleCourseTasks == null) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return moodleCourseTasks.get(0);
        }
    }

    private static class MoodleCourseRowMapper implements RowMapper<MoodleCourseTask> {
        private final PersonnelTask personnelTask;

        private MoodleCourseRowMapper(PersonnelTask personnelTask) {
            this.personnelTask = personnelTask;
        }

        @Override
        public MoodleCourseTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MoodleCourseTask(personnelTask, rs.getString("url"));
        }
    }

    private enum Query implements BasicSqlQuery {
        SELECT_MOODLE_COURSE("SELECT * FROM ${essSchema}.moodle_course WHERE task_id = :taskId"),
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
