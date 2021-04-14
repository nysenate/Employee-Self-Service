package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.Action;
import gov.nysenate.ess.travel.review.view.ActionType;
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
     * Save the actions from an application review.
     *
     * Only saves new actions, old actions should never be updated.
     *
     * @param actions
     * @param appReviewId
     */
    public void saveAppReviewActions(Collection<Action> actions, int appReviewId) {
        Collection<Action> newActions = actions.stream()
                .filter(a -> a.actionId() == 0)
                .collect(Collectors.toList());

        for (Action action : newActions) {
            insertReviewAction(action, appReviewId);
        }
    }

    private void insertReviewAction(Action action, int appReviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appReviewId", appReviewId)
                .addValue("employeeId", action.user().getEmployeeId())
                .addValue("role", action.role().name())
                .addValue("type", action.type().name())
                .addValue("notes", action.notes())
                .addValue("dateTime", toDate(action.dateTime()));

        String sql = SqlActionQuery.INSERT_REVIEW_ACTION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
    }

    /**
     * Select all actions for a given application review id.
     * @param appReviewId
     * @return
     */
    public List<Action> selectActionsByReviewId(int appReviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appReviewId", appReviewId);
        String sql = SqlActionQuery.SELECT_ACTIONS_BY_REVIEW_ID.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ActionRowMapper(employeeInfoService));
    }

    private enum SqlActionQuery implements BasicSqlQuery {
        INSERT_REVIEW_ACTION(
                "INSERT INTO ${travelSchema}.app_review_action\n" +
                        " (app_review_id, employee_id, role, type, notes, date_time)\n" +
                        " VALUES (:appReviewId, :employeeId, :role, :type, :notes, :dateTime)"
        ),
        SELECT_ACTIONS_BY_REVIEW_ID(
                "SELECT app_review_action_id, employee_id, role, type, notes, date_time\n" +
                        " FROM ${travelSchema}.app_review_action\n" +
                        " WHERE app_review_id = :appReviewId"
        ),
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
                    rs.getInt("app_review_action_id"),
                    employeeInfoService.getEmployee(rs.getInt("employee_id")),
                    TravelRole.valueOf(rs.getString("role")),
                    ActionType.valueOf(rs.getString("type")),
                    rs.getString("notes"),
                    getLocalDateTimeFromRs(rs, "date_time")
            );
        }
    }
}
