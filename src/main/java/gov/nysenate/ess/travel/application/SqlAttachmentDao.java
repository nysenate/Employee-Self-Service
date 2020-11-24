package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlAttachmentDao extends SqlBaseDao {

    public void saveAttachments(List<Attachment> attachments, int amendmentId) {
        for (Attachment attachment : attachments) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("amendmentId", amendmentId)
                    .addValue("filename", attachment.getFilename())
                    .addValue("originalFilename", attachment.getOriginalName())
                    .addValue("contentType", attachment.getContentType());
            localNamedJdbc.update(SqlAttachmentQuery.INSERT_ATTACHMENTS.getSql(schemaMap()), params);
        }
    }

    public List<Attachment> selectAttachments(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId);
        String sql = SqlAttachmentQuery.SELECT_ATTACHMENTS.getSql(schemaMap());
        AttachmentHandler handler = new AttachmentHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    public Attachment selectAttachment(String filename) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filename", filename);
        String sql = SqlAttachmentQuery.SELECT_ATTACHMENT.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new AttachmentMapper());
    }

    private enum SqlAttachmentQuery implements BasicSqlQuery {
        INSERT_ATTACHMENTS(
                "INSERT INTO ${travelSchema}.amendment_attachment \n" +
                        "(amendment_id, filename, original_filename, content_type) \n" +
                        "VALUES (:amendmentId, :filename, :originalFilename, :contentType)"
        ),
        SELECT(
                "SELECT filename, original_filename, content_type \n" +
                        "FROM ${travelSchema}.amendment_attachment \n"
        ),
        SELECT_ATTACHMENTS(
                SELECT.getSql() +
                        "WHERE amendment_id = :amendmentId"
        ),
        SELECT_ATTACHMENT(
                SELECT.getSql() +
                        "WHERE filename = :filename"
        )
        ;

        private String sql;

        SqlAttachmentQuery(String sql) {
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

    private class AttachmentHandler extends BaseHandler {

        private List<Attachment> attachments = new ArrayList<>();
        private AttachmentMapper attachmentMapper = new AttachmentMapper();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            attachments.add(attachmentMapper.mapRow(rs, rs.getRow()));
        }

        public List<Attachment> getResult() {
            return attachments;
        }
    }

    private class AttachmentMapper extends BaseRowMapper<Attachment> {

        @Override
        public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Attachment(
                    rs.getString("filename"),
                    rs.getString("original_filename"),
                    rs.getString("content_type"));
        }
    }
}
