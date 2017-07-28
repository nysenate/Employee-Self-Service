package gov.nysenate.ess.core.dao.emergency_notification;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfoNotFound;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo.EmergencyNotificationInfoBuilder;
import static gov.nysenate.ess.core.dao.emergency_notification.SqlEmergencyNotificationInfoQuery.*;

/**
 * Implements {@link EmergencyNotificationInfo} for a local postgres database
 */
@Repository
public class SqlEmergencyNotificationInfoDao extends SqlBaseDao implements EmergencyNotificationInfoDao {

    /** {@inheritDoc} */
    @Override
    public EmergencyNotificationInfo getEmergencyNotificationInfo(int empId) throws EmergencyNotificationInfoNotFound {
        final String sql = GET_EMERGENCY_NOTIFICATION_INFO_BY_EMP.getSql(schemaMap());
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId);
        try {
            return localNamedJdbc.queryForObject(sql, params, emergencyNotifInfoRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new EmergencyNotificationInfoNotFound(empId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<EmergencyNotificationInfo> getAllEmergencyNotificationInfo() {
        final String sql = GET_EMERGENCY_NOTIFICATION_INFO.getSql(schemaMap());
        return localNamedJdbc.query(sql, emergencyNotifInfoRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void updateEmergencyNotificationInfo(EmergencyNotificationInfo emergencyNotificationInfo) {
        MapSqlParameterSource params = getEmergencyNotifInfoParams(emergencyNotificationInfo);
        final String updateDml = UPDATE_EMERGENCY_NOTIFICATION_INFO.getSql(schemaMap());
        int rowsAffected = localNamedJdbc.update(updateDml, params);
        if (rowsAffected == 0) {
            final String insertDml = INSERT_EMERGENCY_NOTIFICATION_INFO.getSql(schemaMap());
            localNamedJdbc.update(insertDml, params);
        }
    }

    /* --- Internal Methods --- */

    private static final RowMapper<EmergencyNotificationInfo> emergencyNotifInfoRowMapper = ((rs, rowNum) -> {
        EmergencyNotificationInfoBuilder builder = EmergencyNotificationInfo.builder();
        builder.setEmpId(rs.getInt("employee_id"));
        builder.setHomePhone(rs.getString("phone_home"));
        builder.setMobilePhone(rs.getString("phone_mobile"));
        builder.setAlternatePhone(rs.getString("phone_alternate"));
        builder.setSmsSubscribed(rs.getBoolean("sms_subscribed"));
        builder.setPersonalEmail(rs.getString("email_personal"));
        builder.setAlternateEmail(rs.getString("email_alternate"));
        return builder.build();
    });

    private MapSqlParameterSource getEmergencyNotifInfoParams(EmergencyNotificationInfo eni) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", eni.getEmpId());
        params.addValue("homePhone", eni.getHomePhone());
        params.addValue("mobilePhone", eni.getMobilePhone());
        params.addValue("alternatePhone", eni.getAlternatePhone());
        params.addValue("smsSubscribed", eni.isSmsSubscribed());
        params.addValue("personalEmail", eni.getPersonalEmail());
        params.addValue("alternateEmail", eni.getAlternateEmail());
        return params;
    }

}
