package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationDao;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlApplicationApprovalDao extends SqlBaseDao implements ApplicationApprovalDao {

    @Autowired private TravelApplicationDao travelApplicationDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private SqlActionDao actionDao;

    @Override
    @Transactional(value = "localTxManager")
    public void saveApplicationApproval(ApplicationApproval appApproval) {
        if (appApproval.getApprovalId() == 0) {
            insertApplicationApproval(appApproval);
        } else {
            updateApplicationApproval(appApproval);
        }

        actionDao.saveApprovalActions(appApproval.actions(), appApproval.getApprovalId());
    }

    @Override
    public List<ApplicationApproval> selectApprovalsByNextRole(TravelRole nextReviewerRole) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nextReviewerRole", nextReviewerRole == null ? null : nextReviewerRole.name());
        String sql = SqlApplicationApprovalQuery.SELECT_APPLICATION_APPROVAL_BY_NEXT_ROLE.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationApprovalRowMapper(travelApplicationDao, employeeInfoService, actionDao));
    }

    public void insertApplicationApproval(ApplicationApproval appApproval) {
        MapSqlParameterSource params = applicationApprovalParams(appApproval);
        String sql = SqlApplicationApprovalQuery.INSERT_APPLICATION_APPROVAL.getSql(schemaMap());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        appApproval.setApprovalId((Integer) keyHolder.getKeys().get("app_approval_id"));
    }

    private MapSqlParameterSource applicationApprovalParams(ApplicationApproval appApproval) {
        return new MapSqlParameterSource()
                .addValue("approvalId", appApproval.getApprovalId())
                .addValue("appId", appApproval.application().getAppId())
                .addValue("travelerRole", appApproval.travelerRole())
                .addValue("nextReviewerRole", appApproval.nextReviewerRole().map(Enum::name).orElse(null));
    }

    private void updateApplicationApproval(ApplicationApproval appApproval) {
        MapSqlParameterSource params = applicationApprovalParams(appApproval);
        String sql = SqlApplicationApprovalQuery.UPDATE_APPLICATION_APPROVAL.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlApplicationApprovalQuery implements BasicSqlQuery {
        INSERT_APPLICATION_APPROVAL(
                "INSERT INTO ${travelSchema}.app_approval \n" +
                        " (app_id, traveler_role, next_reviewer_role) \n" +
                        " VALUES (:appId, :travelerRole, :nextReviewerRole)"
        ),
        UPDATE_APPLICATION_APPROVAL(
                "UPDATE ${travelSchema}.app_approval \n" +
                        " SET next_reviewer_role = :nextReviewerRole\n" +
                        " WHERE app_approval_id = :approvalId"
        ),
        SELECT_APPLICATION_APPROVAL_BY_NEXT_ROLE(
                "SELECT app_approval_id, app_id, traveler_role, next_reviewer_role\n" +
                        " FROM ${travelSchema}.app_approval\n" +
                        " WHERE next_reviewer_role = :nextReviewerRole"
        );

        private String sql;

        SqlApplicationApprovalQuery(String sql) {
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

    private class ApplicationApprovalRowMapper extends BaseRowMapper<ApplicationApproval> {

        private TravelApplicationDao travelApplicationDao;
        private EmployeeInfoService employeeInfoService;
        private SqlActionDao actionDao;

        private int approvalId;
        private TravelApplication application;
        private TravelRole travelRole;
        private List<Action> actions = new ArrayList<>();

        ApplicationApprovalRowMapper(TravelApplicationDao travelApplicationDao,
                                     EmployeeInfoService employeeInfoService,
                                     SqlActionDao actionDao) {
            this.travelApplicationDao = travelApplicationDao;
            this.employeeInfoService = employeeInfoService;
            this.actionDao = actionDao;
        }

        @Override
        public ApplicationApproval mapRow(ResultSet rs, int rowNum) throws SQLException {
            approvalId = rs.getInt("app_approval_id");
            application = travelApplicationDao.selectTravelApplication(rs.getInt("app_id"));
            travelRole = rs.getString("traveler_role") == null
                    ? null
                    : TravelRole.valueOf(rs.getString("traveler_role"));
            actions = actionDao.selectActionsByApprovalId(approvalId);
            return new ApplicationApproval(approvalId, application, travelRole, actions);
        }
    }
}
