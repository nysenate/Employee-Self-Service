package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.VideoTask;
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

/**
 * {@link PersonnelTaskDetailDao} for {@link VideoTask}s
 */
@Repository
public class VideoTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<VideoTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.VIDEO_CODE_ENTRY;
    }

    @Override
    public VideoTask getTaskDetails(PersonnelTask task) {
        SingleVideoRowHandler rowHandler = new SingleVideoRowHandler(task);
        localNamedJdbc.query(
                Query.SELECT_VIDEO_TASK.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", task.getTaskId()),
                rowHandler
        );
        return rowHandler.getResult();
    }

    private static class SingleVideoRowHandler implements RowCallbackHandler {

        private final PersonnelTask task;
        private Integer videoId = null;
        private List<VideoTaskCode> codes = new ArrayList<>();

        private SingleVideoRowHandler(PersonnelTask task) {
            this.task = task;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int videoId = rs.getInt("task_id");
            if (this.videoId == null) {
                this.videoId = videoId;
            } else if (this.videoId != videoId) {
                throw new IncorrectResultSizeDataAccessException(
                        "Multiple pec video ids returned in one query: " + this.videoId + " and " + videoId,
                        1, 2);
            }
            if (rs.getString("code") != null) {
                codes.add(codeRowMapper.mapRow(rs, rs.getRow()));
            }
        }

        public VideoTask getResult() {
            if (videoId == null) {
                throw new EmptyResultDataAccessException("No video task found for id " + task.getTaskId(), 1);
            }
            return new VideoTask(task, codes);
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
        SELECT_VIDEO_TASK("" +
                "SELECT *\n" +
                "FROM ${essSchema}.pec_video_code v\n" +
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
