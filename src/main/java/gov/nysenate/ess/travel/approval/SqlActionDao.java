package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SqlActionDao extends SqlBaseDao {

    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Save the actions from an application approval.
     *
     * Only saves new actions, old actions should never be updated.
     *
     * @param actions
     * @param approvalId
     */
    public void saveApprovalActions(Collection<Action> actions, int approvalId) {
        Collection<Action> newActions = actions.stream()
                .filter(a -> a.getActionId() == 0)
                .collect(Collectors.toList());

        for (Action action : newActions) {
            insertApprovalAction(action, approvalId);
        }
    }

    /**
     * Select all actions for a given application approval id.
     * @param approvalId
     * @return
     */
    public List<Action> selectActionsByApprovalId(int approvalId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("approvalId", approvalId);
        String sql = SqlActionQuery.SELECT_ACTIONS_BY_APPROVAL_ID.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ActionRowMapper(employeeInfoService));
    }

    private void insertApprovalAction(Action action, int approvalId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("approvalId", approvalId)
                .addValue("employeeId", action.user().getEmployeeId())
                .addValue("role", action.role().name())
                .addValue("type", action.type().name())
                .addValue("notes", action.notes())
                .addValue("dateTime", toDate(action.dateTime()));

        String sql = SqlActionQuery.INSERT_APPROVAL_ACTION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        action.setActionId((Integer) keyHolder.getKeys().get("app_approval_action_id"));
    }

    private enum SqlActionQuery implements BasicSqlQuery {
        INSERT_APPROVAL_ACTION(
                "INSERT INTO ${travelSchema}.app_approval_action\n" +
                        " (app_approval_id, employee_id, role, type, notes, date_time)\n" +
                        " VALUES (:approvalId, :employeeId, :role, :type, :notes, :dateTime)"
        ),
        SELECT_ACTIONS_BY_APPROVAL_ID(
                "SELECT app_approval_action_id, employee_id, role, type, notes, date_time\n" +
                        " FROM ${travelSchema}.app_approval_action\n" +
                        " WHERE app_approval_id = :approvalId"
        )
        ;

        private String sql;

        SqlActionQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class ActionRowMapper extends BaseRowMapper<Action> {

        private EmployeeInfoService employeeInfoService;

        ActionRowMapper(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public Action mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Action(
                    rs.getInt("app_approval_action_id"),
                    employeeInfoService.getEmployee(rs.getInt("employee_id")),
                    TravelRole.valueOf(rs.getString("role")),
                    ActionType.valueOf(rs.getString("type")),
                    rs.getString("notes"),
                    getLocalDateTimeFromRs(rs, "date_time")
            );
        }
    }
}
