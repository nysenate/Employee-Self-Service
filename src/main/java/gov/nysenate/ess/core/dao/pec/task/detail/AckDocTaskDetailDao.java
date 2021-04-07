package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link PersonnelTaskDetailDao} for {@link AckDoc}s
 */
@Repository
public class AckDocTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<AckDoc> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;
    }

    @Override
    public AckDoc getTaskDetails(PersonnelTask task) {
        return localNamedJdbc.queryForObject(
                Query.SELECT_ACK_DOC.getSql(schemaMap()),
                new MapSqlParameterSource("taskId", task.getTaskId()),
                new AckDocRowMapper(task)
        );
    }

    private static class AckDocRowMapper implements RowMapper<AckDoc> {
        private final PersonnelTask personnelTask;

        private AckDocRowMapper(PersonnelTask personnelTask) {
            this.personnelTask = personnelTask;
        }

        @Override
        public AckDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AckDoc(personnelTask, rs.getString("filename"));
        }
    }

    private enum Query implements BasicSqlQuery {
        SELECT_ACK_DOC("SELECT * FROM ${essSchema}.ack_doc WHERE task_id = :taskId"),
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
