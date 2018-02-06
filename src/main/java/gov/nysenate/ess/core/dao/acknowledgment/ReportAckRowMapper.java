package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;
import gov.nysenate.ess.core.model.acknowledgment.ReportAck;

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
        reportAck.setEmpId(rs.getInt(pfx + "emp_id"));
        reportAck.setAckTimestamp(getLocalDateTimeFromRs(rs, pfx +"timestamp"));
        reportAck.setAckDocId(rs.getInt(pfx + "ack_doc_id"));
        reportAck.setAckDocTitle(rs.getString(pfx + "title"));
        reportAck.setAckDocEffectiveTime(getLocalDateTimeFromRs(rs, pfx +"effective_date_time"));
        return reportAck;
    }
}
