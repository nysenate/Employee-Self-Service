package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.pec.acknowledgment.Acknowledgment;
import gov.nysenate.ess.core.model.pec.acknowledgment.ReportAck;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportAckRowMapper extends BaseRowMapper<ReportAck> {

    private String pfx = "";

    public ReportAckRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public ReportAck mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReportAck reportAck = new ReportAck();
        Acknowledgment ack = new Acknowledgment();
//        AckDoc ackDoc = new AckDoc();
//        ack.setEmpId(rs.getInt(pfx + "emp_id"));
//        ack.setTimestamp(getLocalDateTimeFromRs(rs, pfx +"timestamp"));
//        ack.setAckDocId(rs.getInt(pfx + "ack_doc_id"));
//        ackDoc.setId(rs.getInt(pfx + "ack_doc_id"));
//        ackDoc.setTitle(rs.getString(pfx + "title"));
//        ackDoc.setFilename(rs.getString(pfx + "filename"));
//        ackDoc.setActive(rs.getBoolean(pfx + "active"));
//        ackDoc.setEffectiveDateTime(getLocalDateTimeFromRs(rs, pfx +"effective_date_time"));
        reportAck.setAck(ack);
//        reportAck.setAckDoc(ackDoc);
        return reportAck;
    }
}
