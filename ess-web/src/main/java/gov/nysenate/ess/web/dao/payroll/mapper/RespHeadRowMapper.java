package gov.nysenate.ess.web.dao.payroll.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RespHeadRowMapper extends BaseRowMapper<ResponsibilityHead>
{
    private String pfx;

    public RespHeadRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public ResponsibilityHead mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString(pfx + "CDSTATUS") != null) {
            ResponsibilityHead rspHead = new ResponsibilityHead();
            rspHead.setCode(rs.getString(pfx + "CDRESPCTRHD"));
            rspHead.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
            rspHead.setShortName(rs.getString(pfx + "DERESPCTRHDS"));
            rspHead.setName(rs.getString(pfx + "FFDERESPCTRHDF"));
            rspHead.setAffiliateCode(rs.getString(pfx + "CDAFFILIATE"));
            return rspHead;
        }
        return null;
    }
}
