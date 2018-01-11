package gov.nysenate.ess.core.dao.policy;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.policy.Policy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PolicyRowMapper extends BaseRowMapper<Policy> {

    private String pfx = "";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PolicyRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Policy mapRow(ResultSet rs, int rowNum) throws SQLException {
        if ( rs.getInt(pfx + "policy_id") != 0) {
            Policy policy = new Policy();
            policy.setPolicyId(rs.getInt(pfx +"policy_id"));
            policy.setTitle(rs.getString(pfx + "title"));
            policy.setFilename(rs.getString(pfx + "filename"));
            policy.setActive(rs.getBoolean(pfx + "active"));
            policy.setEffectiveDateTime(LocalDateTime.parse(rs.getString(pfx + "effective_date_time") , formatter));
            return policy;
        }
        return null;
    }
}
