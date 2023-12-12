package gov.nysenate.ess.travel.request.attachment;

import gov.nysenate.ess.core.dao.base.*;
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

    public List<Attachment> selectAttachments(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlAttachmentQuery.SELECT_ATTACHMENTS.getSql(schemaMap());
        AttachmentHandler handler = new AttachmentHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    public void updateAttachments(Collection<Attachment> attachments, int appId) {
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
                    .addValue("attachmentId", attachment.getAttachmentId())
                    .addValue("originalFilename", attachment.getOriginalName())
                    .addValue("contentType", attachment.getContentType());
            paramList.add(params);
        }
        String sql = SqlAttachmentQuery.INSERT_ATTACHMENTS.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }


    private enum SqlAttachmentQuery implements BasicSqlQuery {
        SELECT_ATTACHMENTS("""
                SELECT *
                FROM ${travelSchema}.app_attachment
                WHERE app_id = :appId
                """
        ),
        DELETE_ATTACHMENTS("""
                DELETE FROM ${travelSchema}.app_attachment
                WHERE app_id = :appId
                """
        ),
        INSERT_ATTACHMENTS("""
                INSERT INTO ${travelSchema}.app_attachment(app_id, attachment_id, original_filename, content_type)
                VALUES (:appId, :attachmentId, :originalFilename, :contentType)
                """
        ),
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
