package gov.nysenate.ess.time.dao.personnel;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.time.model.personnel.SupOverrideType;
import gov.nysenate.ess.time.model.personnel.SupervisorOverride;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class SupervisorOverrideRowMapper implements RowMapper<SupervisorOverride>
{
    @Override
    public SupervisorOverride mapRow(ResultSet rs, int i) throws SQLException {
        SupervisorOverride supOvr = new SupervisorOverride();

        int ovrSupId = rs.getInt("NUXREFSVSUB");
        int ovrEmpId = rs.getInt("NUXREFEMSUB");

        // True for standard supervisor override
        if (ovrSupId > 0 && ovrEmpId == 0) {
            supOvr.setGranterEmpId(ovrSupId);
            supOvr.setSupOverrideType(SupOverrideType.SUPERVISOR);
        }
        // True for employee override
        else if (ovrSupId == 0 && ovrEmpId > 0) {
            supOvr.setGranterEmpId(ovrEmpId);
            supOvr.setSupOverrideType(SupOverrideType.EMPLOYEE);
        }
        else {
            throw new IllegalStateException("Illegal state of supervisor override record - " +
                    "supId: " + ovrSupId + "\tempId: " + ovrEmpId);
        }

        supOvr.setGranteeEmpId(rs.getInt("NUXREFEM"));
        supOvr.setActive(rs.getString("CDSTATUS").equals("A"));
        supOvr.setOriginDate(SqlBaseDao.getLocalDateTime(rs, "DTTXNORIGIN"));
        supOvr.setUpdateDate(SqlBaseDao.getLocalDateTime(rs, "DTTXNUPDATE"));
        supOvr.setStartDate(SqlBaseDao.getLocalDate(rs, "DTSTART"));
        supOvr.setEndDate(SqlBaseDao.getLocalDate(rs, "DTEND"));
        return supOvr;
    }
}
