package gov.nysenate.ess.core.dao.pec.notification;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import static gov.nysenate.ess.core.dao.pec.notification.SqlPECNotificationQuery.MARK_NOTIFICAION_SENT;
import static gov.nysenate.ess.core.dao.pec.notification.SqlPECNotificationQuery.WAS_NOTIFICATION_SENT;

@Repository
public class SqlPECNotificationDao extends SqlBaseDao implements PECNotificationDao {

    /** {@inheritDoc} */
    @Override
    public boolean wasNotificationSent(int empId, int taskId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", taskId);
        params.addValue("empId", empId);
        return localNamedJdbc.queryForObject(WAS_NOTIFICATION_SENT.getSql(schemaMap()), params, Boolean.class);
    }

    /** {@inheritDoc} */
    @Override
    public void markNotificationSent(int empId, int taskId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", taskId);
        params.addValue("empId", empId);
        localNamedJdbc.update(MARK_NOTIFICAION_SENT.getSql(schemaMap()), params);
    }
}
