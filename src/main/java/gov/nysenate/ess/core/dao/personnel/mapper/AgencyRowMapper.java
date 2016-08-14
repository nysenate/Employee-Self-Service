package gov.nysenate.ess.core.dao.personnel.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.personnel.Agency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AgencyRowMapper extends BaseRowMapper<Agency>
{
    private String pfx;

    public AgencyRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Agency mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString(pfx + "CDSTATUS") != null) {
            Agency agency = new Agency();
            agency.setCode(rs.getString(pfx + "CDAGENCY"));
            agency.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
            agency.setShortName(rs.getString(pfx + "DEAGENCYS"));
            agency.setName(rs.getString(pfx + "DEAGENCYF"));
            return agency;
        }
        return null;
    }
}
