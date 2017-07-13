package gov.nysenate.ess.time.dao.personnel.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.model.personnel.DockHoursRecord;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Brian Heitner
 *
 * This will map the results from the docked hours sql query.
 *
 */
 public class DockHoursRecordRowMapper extends BaseRowMapper<DockHoursRecord> {
        @Override
        public DockHoursRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            DockHoursRecord record = new DockHoursRecord();
            record.setEmployeeId(rs.getInt("NUXREFEM"));
            record.setBeginDate(DateUtils.getLocalDate(rs.getDate("DTBEGIN")));
            record.setEndDate(DateUtils.getLocalDate(rs.getDate("DTEND")));
            record.setDockHours(rs.getBigDecimal("DOCKED_HOURS"));
            return record;
        }
}
