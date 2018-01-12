package gov.nysenate.ess.core.dao.acknowledgement;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class AckDocDao extends SqlBaseDao implements AcknowlegdementDocumentDao {

    private static final Logger logger = LoggerFactory.getLogger(AckDocDao.class);

    public AckDoc getAckDoc(int ackDocId) {
        AckDoc ackDoc;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ackDocId", ackDocId);
        try {
            ackDoc = localNamedJdbc.queryForObject(
                    SqlAckDocQuery.GET_ACK_DOC_BY_ID_SQL.getSql(schemaMap()), params, getAckDocRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching ack doc record for ackDocId: " + ackDocId);
        }
        return ackDoc;
    }

    public void insertAckDoc(AckDoc ackDoc) {

    }

    public Set<AckDoc> getActiveAckDocs() {
        return new HashSet<>();
    }



    public Acknowledgement getAcknowledgementById(int empId, int ackDocId) {
        return new Acknowledgement();
    }

    public void insertAcknowledgement(Acknowledgement acknowledgement) {

    }

    public Set<Acknowledgement> getAllAcknowledgements() {
        return new HashSet<>();
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
