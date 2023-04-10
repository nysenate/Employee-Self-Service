package gov.nysenate.ess.travel.request.attachment;

import gov.nysenate.ess.core.dao.base.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SqlAttachmentDao extends SqlBaseDao {

    public void saveAttachment(Attachment attachment) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("attachmentId", attachment.getAttachmentId())
                .addValue("originalFilename", attachment.getOriginalName())
                .addValue("contentType", attachment.getContentType());
        localNamedJdbc.update(SqlAttachmentQuery.INSERT_ATTACHMENTS.getSql(schemaMap()), params);
    }

    public void saveAttachments(List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            saveAttachment(attachment);
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
                .addValue("attachmentId", UUID.fromString(filename));
        String sql = SqlAttachmentQuery.SELECT_ATTACHMENT.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new AttachmentMapper());
    }

    /**
     * Saves a list of attachments to an Amendment.
     * @param attachments
     * @param amendmentId
     */
    public void saveAmendmentAttachments(List<Attachment> attachments, int amendmentId) {
        for (Attachment attachment : attachments) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("amendmentId", amendmentId)
                    .addValue("attachmentId", attachment.getAttachmentId());
            localNamedJdbc.update(SqlAttachmentQuery.INSERT_AMENDMENT_ATTACHMENTS.getSql(schemaMap()), params);
        }
    }

    public List<Attachment> selectAmendmentAttachments(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("amendmentId", amendmentId);
        String sql = SqlAttachmentQuery.SELECT_AMENDMENT_ATTACHMENTS.getSql(schemaMap());
        AttachmentHandler handler = new AttachmentHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    private enum SqlAttachmentQuery implements BasicSqlQuery {
        INSERT_ATTACHMENTS(
                "INSERT INTO ${travelSchema}.attachment \n" +
                        "(attachment_id, original_filename, content_type) \n" +
                        "VALUES (:attachmentId, :originalFilename, :contentType) \n" +
                        "ON CONFLICT DO NOTHING"
        ),
        SELECT(
                "SELECT attachment_id, original_filename, content_type \n" +
                        "FROM ${travelSchema}.attachment \n"
        ),
        SELECT_ATTACHMENTS(
                SELECT.getSql() +
                        "WHERE amendment_id = :amendmentId"
        ),
        SELECT_ATTACHMENT(
                SELECT.getSql() +
                        "WHERE attachment_id = :attachmentId"
        ),
        INSERT_AMENDMENT_ATTACHMENTS("""
                INSERT INTO ${travelSchema}.amendment_attachment(amendment_id, attachment_id)
                VALUES (:amendmentId, :attachmentId)
                """
        ),
        SELECT_AMENDMENT_ATTACHMENTS("""
                SELECT attachment.attachment_id, original_filename, content_type
                FROM ${travelSchema}.attachment
                JOIN ${travelSchema}.amendment_attachment
                  ON amendment_attachment.attachment_id = attachment.attachment_id
                WHERE amendment_id = :amendmentId
                """
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
                    (UUID) rs.getObject("attachment_id"),
                    rs.getString("original_filename"),
                    rs.getString("content_type"));
        }
    }
}
