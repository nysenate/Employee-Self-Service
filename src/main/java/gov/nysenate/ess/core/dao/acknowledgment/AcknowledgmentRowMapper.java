package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.pec.acknowledgment.Acknowledgment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AcknowledgmentRowMapper extends BaseRowMapper<Acknowledgment> {

    private String pfx = "";

    public AcknowledgmentRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Acknowledgment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Acknowledgment acknowledgment = new Acknowledgment();
        acknowledgment.setEmpId(rs.getInt(pfx + "emp_id"));
        acknowledgment.setAckDocId(rs.getInt(pfx + "ack_doc_id"));
        acknowledgment.setTimestamp(getLocalDateTimeFromRs(rs,"timestamp"));
        acknowledgment.setPersonnelAcked(rs.getBoolean("personnel_acked"));
        return acknowledgment;
    }

}
