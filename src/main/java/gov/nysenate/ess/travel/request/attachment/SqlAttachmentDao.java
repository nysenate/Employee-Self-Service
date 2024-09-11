package gov.nysenate.ess.travel.request.attachment;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public class SqlAttachmentDao extends SqlBaseDao {

    /**
     * Fetch all Attachments linked to a given Travel Application.
     * @param appId
     * @return
     */
    public List<Attachment> selectAttachments(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlAttachmentQuery.SELECT_ATTACHMENTS.getSql(schemaMap());
        AttachmentHandler handler = new AttachmentHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    /**
     * Load an Attachment from the database.
     *
     * @param uuid The UUID of the attachment to load.
     * @return {@link Attachment}
     */
    public Attachment selectAttachment(String uuid) {
        MapSqlParameterSource params = new MapSqlParameterSource("attachmentId", uuid);
        String sql = SqlAttachmentQuery.SELECT_ATTACHMENT_BY_FILENAME.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new AttachmentMapper());
    }

    /**
     * Saves an {@link Attachment Attachment's} metadata to the database.
     * <p>
     * This does not associate the attachment with a {@link TravelApplication},
     * for that, see {@link #saveTravelAppAttachments(Collection, int)}.
     *
     * @param attachment
     */
    public void saveAttachment(Attachment attachment) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("attachmentId", attachment.getAttachmentId())
                .addValue("originalFilename", attachment.getOriginalName())
                .addValue("contentType", attachment.getContentType());
        String sql = SqlAttachmentQuery.INSERT_ATTACHMENT.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * Links an {@link Attachment} to a {@link TravelApplication}.
     * @param attachments The attachment to link to a Travel App. This attachment should already be
     *                    persisted to the database.
     * @param appId The id of the travel app.
     */
    public void saveTravelAppAttachments(Collection<Attachment> attachments, int appId) {
        deleteAttachments(appId);
        insertAttachments(attachments, appId);
    }

    private void deleteAttachments(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlAttachmentQuery.DELETE_ATTACHMENTS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertAttachments(Collection<Attachment> attachments, int appId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (Attachment attachment : attachments) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("appId", appId)
                    .addValue("attachmentId", attachment.getAttachmentId());
            paramList.add(params);
        }
        String sql = SqlAttachmentQuery.INSERT_APP_ATTACHMENT.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }


    private enum SqlAttachmentQuery implements BasicSqlQuery {
        SELECT_ATTACHMENTS("""
                SELECT *
                FROM ${travelSchema}.app_attachment
                JOIN ${travelSchema}.attachment USING (attachment_id)
                WHERE app_id = :appId
                """
        ),
        SELECT_ATTACHMENT_BY_FILENAME("""
                SELECT *
                FROM ${travelSchema}.attachment
                WHERE attachment_id = :attachmentId::uuid
                """
        ),
        DELETE_ATTACHMENTS("""
                DELETE FROM ${travelSchema}.app_attachment
                WHERE app_id = :appId
                """
        ),
        INSERT_ATTACHMENT("""
                INSERT INTO ${travelSchema}.attachment(attachment_id, original_filename, content_type)
                VALUES (:attachmentId, :originalFilename, :contentType)
                """
        ),
        INSERT_APP_ATTACHMENT("""
                INSERT INTO ${travelSchema}.app_attachment(attachment_id, app_id)
                VALUES (:attachmentId, :appId)
                """
        );

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
