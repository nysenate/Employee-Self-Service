package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.acknowledgment.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgment.Acknowledgment;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.ess.core.dao.acknowledgment.SqlAckDocQuery.*;

@Repository
public class SqlAckDocDao extends SqlBaseDao implements AckDocDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlAckDocDao.class);

    public AckDoc getAckDoc(int ackDocId) throws AckDocNotFoundEx {
        AckDoc ackDoc;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ackDocId", ackDocId);
        try {
            ackDoc = localNamedJdbc.queryForObject(
                    GET_ACK_DOC_BY_ID_SQL.getSql(schemaMap()), params, getAckDocRowMapper());
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
        localNamedJdbc.update(INSERT_ACK_DOC_SQL.getSql(schemaMap()),params);
    }

    public List<AckDoc> getActiveAckDocs() {
        return localNamedJdbc.query(GET_ALL_ACTIVE_ACK_DOCS_SQL.getSql(schemaMap()), getAckDocRowMapper());
    }



    public Acknowledgment getAcknowledgmentById(int empId, int ackDocId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ackDocId", ackDocId);
        params.addValue("empId", empId);
        return localNamedJdbc.queryForObject(
                GET_ACK_BY_ID.getSql(schemaMap()), params, getAcknowledgmentRowMapper());
    }

    public void insertAcknowledgment(Acknowledgment acknowledgment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", acknowledgment.getEmpId());
        params.addValue("ack_doc", acknowledgment.getAckDocId());
        params.addValue("timestamp", toDate(acknowledgment.getTimestamp()));
        localNamedJdbc.update(INSERT_ACK_SQL.getSql(schemaMap()),params);

    }

    public List<Acknowledgment> getAllAcknowledgments() {
        return localNamedJdbc.query(GET_ALL_ACKNOWLEDGMENTS.getSql(schemaMap()), getAcknowledgmentRowMapper());
    }

    public List<Acknowledgment> getAllAcknowledgmentsForEmp(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId",empId);
        return localNamedJdbc.query(GET_ALL_ACKNOWLEDGMENTS_FOR_EMPLOYEE.getSql(schemaMap()), params ,getAcknowledgmentRowMapper());
    }

    //1st Report
    public List<EmpAckReport> getAllAcksForDocWithNameAndYear(String title, int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title",title);
        params.addValue("year",year);
        return localNamedJdbc.query(GET_ALL_ACKS_FOR_DOC_WITH_NAME_AND_YEAR.getSql(schemaMap()), params ,getEmpAckReportRowMapper());
    }

    //2nd Report
    public List<EmpAckReport> getAllAcksForEmpWithTimestampAndDocRef() {
        return localNamedJdbc.query(  GET_ALL_ACKS_WITH_TIMESTAMP_AND_DOC_REF.getSql(schemaMap()) ,getEmpAckReportRowMapper());
    }


    /** Returns an AckDocRowMapper that's configured for use in this dao */
    private static AckDocRowMapper getAckDocRowMapper() {
        return new AckDocRowMapper("");
    }

    /** Returns a EmployeeRowMapper that's configured for use in this dao */
    private static AcknowledgmentRowMapper getAcknowledgmentRowMapper() {
        return new AcknowledgmentRowMapper("");
    }

    private static EmpAckReportRowMapper getEmpAckReportRowMapper() {
        return new EmpAckReportRowMapper("");
    }

}
