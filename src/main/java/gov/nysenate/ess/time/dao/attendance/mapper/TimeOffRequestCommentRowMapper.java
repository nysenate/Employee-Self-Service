package gov.nysenate.ess.time.dao.attendance.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestComment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeOffRequestCommentRowMapper extends BaseRowMapper<TimeOffRequestComment> {

    public TimeOffRequestCommentRowMapper() {}

    @Override
    public TimeOffRequestComment mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeOffRequestComment comment = new TimeOffRequestComment();
        comment.setAuthorId(rs.getInt("author_id"));
        comment.setRequestId(rs.getInt("request_id"));
        comment.setText(rs.getString("comment"));
        comment.setTimestamp(rs.getTimestamp("time_stamp").toLocalDateTime());
        return comment;
    }
}
