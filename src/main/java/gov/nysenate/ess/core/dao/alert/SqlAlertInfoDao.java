package gov.nysenate.ess.core.dao.alert;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.AlertInfoNotFound;
import gov.nysenate.ess.core.model.alert.ContactOptions;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static gov.nysenate.ess.core.model.alert.AlertInfo.Builder;
import static gov.nysenate.ess.core.dao.alert.SqlAlertInfoQuery.*;

/**
 * Implements {@link AlertInfo} for a local postgres database
 */
@Repository
public class SqlAlertInfoDao extends SqlBaseDao implements AlertInfoDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public AlertInfo getAlertInfo(int empId) throws AlertInfoNotFound {
        final String sql = GET_ALERT_INFO_BY_EMP.getSql(schemaMap());
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId);
        try {
            return localNamedJdbc.queryForObject(sql, params, alertInfoRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new AlertInfoNotFound(empId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AlertInfo> getAllAlertInfo() {
        final String sql = GET_ALERT_INFO.getSql(schemaMap());
        return localNamedJdbc.query(sql, alertInfoRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAlertInfo(AlertInfo alertInfo) {
        MapSqlParameterSource params = getAlertInfoParams(alertInfo);
        final String updateDml = UPDATE_ALERT_INFO.getSql(schemaMap());
        int rowsAffected = localNamedJdbc.update(updateDml, params);
        if (rowsAffected == 0) {
            final String insertDml = INSERT_ALERT_INFO.getSql(schemaMap());
            localNamedJdbc.update(insertDml, params);
        }
    }

    /* --- Internal Methods --- */

    private static final RowMapper<AlertInfo> alertInfoRowMapper = ((rs, rowNum) -> {
        Builder builder = AlertInfo.builder();
        builder.setEmpId(rs.getInt("employee_id"))
                .setHomePhone(rs.getString("phone_home"))
                .setMobilePhone(rs.getString("phone_mobile"))
                .setMobileOptions(Optional.ofNullable(rs.getString("mobile_options"))
                        .map(ContactOptions::valueOf).orElse(ContactOptions.EVERYTHING))
                .setAlternatePhone(rs.getString("phone_alternate"))
                .setAlternateOptions(Optional.ofNullable(rs.getString("alternate_options"))
                        .map(ContactOptions::valueOf).orElse(ContactOptions.EVERYTHING))
                .setPersonalEmail(rs.getString("email_personal"))
                .setAlternateEmail(rs.getString("email_alternate"));
        return builder.build();
    });

    private MapSqlParameterSource getAlertInfoParams(AlertInfo alertInfo) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", alertInfo.getEmpId());
        params.addValue("homePhone", alertInfo.getHomePhone());
        params.addValue("mobilePhone", alertInfo.getMobilePhone());
        params.addValue("mobileOptions", String.valueOf(alertInfo.getMobileOptions()));
        params.addValue("alternatePhone", alertInfo.getAlternatePhone());
        params.addValue("alternateOptions", String.valueOf(alertInfo.getAlternateOptions()));
        params.addValue("personalEmail", alertInfo.getPersonalEmail());
        params.addValue("alternateEmail", alertInfo.getAlternateEmail());
        return params;
    }

}
