package gov.nysenate.ess.web.dao.personnel;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.web.model.personnel.SupervisorOverride;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SupervisorOverrideRowMapper implements RowMapper<SupervisorOverride>
{
    @Override
    public SupervisorOverride mapRow(ResultSet rs, int i) throws SQLException {
        SupervisorOverride supOvr = new SupervisorOverride();
        supOvr.setGranteeSupervisorId(rs.getInt("NUXREFEM"));
        supOvr.setGranterSupervisorId(rs.getInt("NUXREFSVSUB"));
        supOvr.setActive(rs.getString("CDSTATUS").equals("A"));
        supOvr.setOriginDate(SqlBaseDao.getLocalDateTime(rs, "DTTXNORIGIN"));
        supOvr.setUpdateDate(SqlBaseDao.getLocalDateTime(rs, "DTTXNUPDATE"));
        supOvr.setStartDate(Optional.ofNullable(SqlBaseDao.getLocalDate(rs, "DTSTART")));
        supOvr.setEndDate(Optional.ofNullable(SqlBaseDao.getLocalDate(rs, "DTEND")));
        return supOvr;
    }
}
