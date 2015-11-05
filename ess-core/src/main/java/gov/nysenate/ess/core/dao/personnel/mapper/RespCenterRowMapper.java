package gov.nysenate.ess.core.dao.personnel.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RespCenterRowMapper extends BaseRowMapper<ResponsibilityCenter>
{
    private String pfx;

    private RespHeadRowMapper respHeadMapper;
    private AgencyRowMapper agencyRowMapper;

    public RespCenterRowMapper(String pfx, String rctrHdPfx, String agcyPfx) {
        this.pfx = pfx;
        this.respHeadMapper = new RespHeadRowMapper(rctrHdPfx);
        this.agencyRowMapper = new AgencyRowMapper(agcyPfx);
    }

    @Override
    public ResponsibilityCenter mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString(pfx + "CDSTATUS") != null) {
            ResponsibilityCenter rctr = new ResponsibilityCenter();
            rctr.setCode(rs.getInt(pfx + "CDRESPCTR"));
            rctr.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
            rctr.setName(rs.getString(pfx + "DERESPCTR"));
            rctr.setEffectiveDateBegin(getLocalDateFromRs(rs, pfx + "DTEFFECTBEG"));
            rctr.setEffectiveDateEnd(getLocalDateFromRs(rs, pfx + "DTEFFECTEND"));
            rctr.setHead(respHeadMapper.mapRow(rs, rowNum));
            rctr.setAgency(agencyRowMapper.mapRow(rs, rowNum));
            return rctr;
        }
        return null;
    }
}
