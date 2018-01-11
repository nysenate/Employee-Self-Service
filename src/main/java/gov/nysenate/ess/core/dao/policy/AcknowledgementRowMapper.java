package gov.nysenate.ess.core.dao.policy;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.policy.Acknowledgement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AcknowledgementRowMapper extends BaseRowMapper<Acknowledgement> {

    private String pfx = "";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AcknowledgementRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Acknowledgement mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getInt(pfx + "emp_id") != -1) {
            Acknowledgement acknowledgement = new Acknowledgement();
            acknowledgement.setEmpId(rs.getInt(pfx + "emp_id"));
            acknowledgement.setPolicyId(rs.getInt(pfx + "policy_id"));
            acknowledgement.setTimestamp(LocalDateTime.parse(rs.getString(pfx + "timestamp") , formatter));
            return acknowledgement;
        }
        return null;
    }

}
