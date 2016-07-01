package gov.nysenate.ess.seta.dao.attendance;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class EssTimeRecordAuditDao extends SqlBaseDao implements TimeRecordAuditDao {

    /** {@inheritDoc} */
    @Override
    public void auditTimeRecord(BigInteger timeRecordId) {
        remoteNamedJdbc.update(SqlTimeRecordAuditQuery.INSERT_TIMESHEET_AUDIT.getSql(schemaMap()),
                new MapSqlParameterSource("timeRecordId", String.valueOf(timeRecordId)));
    }
}
