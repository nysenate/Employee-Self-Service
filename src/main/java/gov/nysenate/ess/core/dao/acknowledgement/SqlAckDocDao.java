package gov.nysenate.ess.core.dao.acknowledgement;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.acknowledgement.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlAckDocDao extends SqlBaseDao implements AckDocDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlAckDocDao.class);

    public AckDoc getAckDoc(int ackDocId) throws AckDocNotFoundEx {
        AckDoc ackDoc;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ackDocId", ackDocId);
        try {
            ackDoc = localNamedJdbc.queryForObject(
                    SqlAckDocQuery.GET_ACK_DOC_BY_ID_SQL.getSql(schemaMap()), params, getAckDocRowMapper());
        }
        catch (EmptyResultDataAccessException ex) {
            throw new AckDocNotFoundEx(ackDocId);
        }
        return ackDoc;
    }

    public void insertAckDoc(AckDoc ackDoc) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title",ackDoc.getTitle());
        params.addValue("filename",ackDoc.getFilename());
        params.addValue("active",ackDoc.getActive());
        params.addValue("effectiveDateTime",ackDoc.getEffectiveDateTime());
        localNamedJdbc.update(SqlAckDocQuery.INSERT_ACK_DOC_SQL.getSql(),params);
    }

    public List<AckDoc> getActiveAckDocs() {
        List<AckDoc> activeAckDocsList;
        activeAckDocsList = localNamedJdbc.query(SqlAckDocQuery.GET_ALL_ACTIVE_ACK_DOCS_SQL.getSql(), getAckDocRowMapper());
        return activeAckDocsList;
    }



    public Acknowledgement getAcknowledgementById(int empId, int ackDocId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ackDocId", ackDocId);
        params.addValue("empId",empId);
            return localNamedJdbc.queryForObject(
                    SqlAckDocQuery.GET_ACK_BY_ID.getSql(schemaMap()), params, getAcknowledgementRowMapper());
    }

    public void insertAcknowledgement(Acknowledgement acknowledgement) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId",acknowledgement.getEmpId());
        params.addValue("ack_doc",acknowledgement.getAckDocId());
        params.addValue("timestamp",acknowledgement.getTimestamp());
        localNamedJdbc.update(SqlAckDocQuery.INSERT_ACK_SQL.getSql(),params);

    }

    public List<Acknowledgement> getAllAcknowledgements() {
        List<Acknowledgement> allAcknowledgements;
        allAcknowledgements = localNamedJdbc.query(SqlAckDocQuery.GET_ALL_ACKNOWLEDGEMENTS.getSql(), getAcknowledgementRowMapper());
        return allAcknowledgements;
    }



    /** Returns an AckDocRowMapper that's configured for use in this dao */
    private static AckDocRowMapper getAckDocRowMapper() {
        return new AckDocRowMapper("");
    }

    /** Returns a EmployeeRowMapper that's configured for use in this dao */
    private static AcknowledgementRowMapper getAcknowledgementRowMapper() {
        return new AcknowledgementRowMapper("");
    }

}
