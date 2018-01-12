package gov.nysenate.ess.core.dao.acknowledgement;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AcknowledgementRowMapper extends BaseRowMapper<Acknowledgement> {

    private String pfx = "";

    public AcknowledgementRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Acknowledgement mapRow(ResultSet rs, int rowNum) throws SQLException {
        Acknowledgement acknowledgement = new Acknowledgement();
        acknowledgement.setEmpId(rs.getInt(pfx + "emp_id"));
        acknowledgement.setAckDocId(rs.getInt(pfx + "ack_doc_id"));
        acknowledgement.setTimestamp(getLocalDateTimeFromRs(rs,"timestamp"));
        return acknowledgement;
    }

}
