package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.ethics.EthicsLiveCourseTask;
import gov.nysenate.ess.core.model.pec.video.VideoTaskCode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EthicsLiveCourseTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<EthicsLiveCourseTask> {
    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.ETHICS_LIVE_COURSE;
    }

    @Override
    public EthicsLiveCourseTask getTaskDetails(PersonnelTask task) {
        EthicsLiveCourseRowHandler rowHandler = new EthicsLiveCourseRowHandler(task);
        localNamedJdbc.query(
                EthicsLiveCourseTaskDetailDao.Query.SELECT_ETHICS_LIVE_COURSE.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", task.getTaskId()),
                rowHandler
        );
        return rowHandler.getResult();
    }

    private static class EthicsLiveCourseRowHandler implements RowCallbackHandler {
        private final PersonnelTask task;
        private Integer taskID = null;
        private List<VideoTaskCode> codes = new ArrayList<>();

        private EthicsLiveCourseRowHandler(PersonnelTask personnelTask) {
            this.task = personnelTask;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int task_id = rs.getInt("task_id");
            if (this.taskID == null) {
                this.taskID = task_id;
            } else if (this.taskID != task_id) {
                throw new IncorrectResultSizeDataAccessException(
                        "Multiple ethics live course ids returned in one query: " + this.taskID + " and " + task_id,
                        1, 2);
            }
            if (rs.getString("code") != null) {
                codes.add(codeRowMapper.mapRow(rs, rs.getRow()));
            }

        }

        public EthicsLiveCourseTask getResult() {
            if (taskID == null) {
                throw new EmptyResultDataAccessException("No ethics live course code id found for task " + task.getTaskId(), 1);
            }
            return new EthicsLiveCourseTask(task, codes);
        }
    }

    private static final RowMapper<VideoTaskCode> codeRowMapper = (rs, rowNum) ->
            new VideoTaskCode(
                    rs.getInt("task_id"),
                    rs.getInt("sequence_no"),
                    rs.getString("label"),
                    rs.getString("code")
            );

    private enum Query implements BasicSqlQuery {

        SELECT_ETHICS_LIVE_COURSE("" +
                "SELECT *\n" +
                "FROM ${essSchema}.ethics_code\n" +
                "LEFT JOIN ${essSchema}.personnel_task c USING (task_id)\n" +
                "WHERE task_id = :taskId"
        ),
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
