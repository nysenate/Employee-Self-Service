package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationDao;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
public class SqlApplicationReviewDao extends SqlBaseDao implements ApplicationReviewDao {

    @Autowired private TravelApplicationDao travelApplicationDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private SqlActionDao actionDao;

    /**
     * Save an Application Review
     */
    @Override
    @Transactional(value = "localTxManager")
    public void saveApplicationReview(ApplicationReview appReview) {
        if (appReview.getAppReviewId() == 0) {
            insertApplicationReview(appReview);
        } else {
            updateApplicationReview(appReview);
        }

        actionDao.saveAppReviewActions(appReview.actions(), appReview.getAppReviewId());
    }

    private void insertApplicationReview(ApplicationReview appApproval) {
        MapSqlParameterSource params = appReviewParams(appApproval);
        String sql = SqlApplicationReviewQuery.INSERT_APPLICATION_REVIEW.getSql(schemaMap());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        appApproval.setAppReviewId((Integer) keyHolder.getKeys().get("app_review_id"));
    }

    private void updateApplicationReview(ApplicationReview appApproval) {
        MapSqlParameterSource params = appReviewParams(appApproval);
        String sql = SqlApplicationReviewQuery.UPDATE_APPLICATION_REVIEW.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * Get an ApplicationReview by its id.
     */
    @Override
    public ApplicationReview selectAppReviewById(int appReviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appReviewId", appReviewId);
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEW_BY_ID.getSql(schemaMap());
        List<ApplicationReview> applicationReviews =  localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, employeeInfoService, actionDao));
        if (applicationReviews.isEmpty() || applicationReviews == null) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return applicationReviews.get(0);
        }
    }

    /**
     * Get all ApplicationReview's which needed to be reviewed by the provided role.
     */
    @Override
    public List<ApplicationReview> pendingReviewsByRole(TravelRole nextReviewerRole) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nextReviewerRole", nextReviewerRole == null ? null : nextReviewerRole.name())
                .addValue("disapproval", ActionType.DISAPPROVE.name());
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEWS_BY_NEXT_ROLE.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, employeeInfoService, actionDao));
    }

    /**
     * Get a list of ApplicationReviews where the given role has performed an action on it.
     */
    @Override
    public List<ApplicationReview> reviewHistoryForRole(TravelRole role) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", role.name());
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEWS_WITH_ROLE_ACTION.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, employeeInfoService, actionDao));
    }

    private MapSqlParameterSource appReviewParams(ApplicationReview appApproval) {
        return new MapSqlParameterSource()
                .addValue("appReviewId", appApproval.getAppReviewId())
                .addValue("appId", appApproval.application().getAppId())
                .addValue("travelerRole", appApproval.travelerRole().name())
                .addValue("nextReviewerRole", appApproval.nextReviewerRole().name());
    }

    private enum SqlApplicationReviewQuery implements BasicSqlQuery {
        INSERT_APPLICATION_REVIEW(
                "INSERT INTO ${travelSchema}.app_review \n" +
                        " (app_id, traveler_role, next_reviewer_role) \n" +
                        " VALUES (:appId, :travelerRole, :nextReviewerRole)"
        ),
        UPDATE_APPLICATION_REVIEW(
                "UPDATE ${travelSchema}.app_review \n" +
                        " SET next_reviewer_role = :nextReviewerRole\n" +
                        " WHERE app_review_id = :appReviewId"
        ),
        SELECT_APPLICATION_REVIEWS_BY_NEXT_ROLE(
                "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role, app_review.next_reviewer_role\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE app_review.next_reviewer_role = :nextReviewerRole" +
                        " AND :disapproval NOT IN" +
                        "    (SELECT type FROM ${travelSchema}.app_review_action\n" +
                        "     WHERE app_review_action.app_review_id = app_review.app_review_id)"
        ),
        SELECT_APPLICATION_REVIEWS_WITH_ROLE_ACTION(
                "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role, app_review.next_reviewer_role\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE EXISTS \n" +
                        "  (SELECT DISTINCT(action.app_review_id) \n" +
                        "  FROM ${travelSchema}.app_review_action action \n" +
                        "  WHERE action.role = :role \n" +
                        "  AND action.app_review_id = app_review.app_review_id)"
        ),
        SELECT_APPLICATION_REVIEW_BY_ID(
                "SELECT app_review_id, app_id, traveler_role, next_reviewer_role\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE app_review_id = :appReviewId"
        );

        private final String sql;

        SqlApplicationReviewQuery(String sql) {
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

    private static class ApplicationReviewRowMapper extends BaseRowMapper<ApplicationReview> {

        private TravelApplicationDao travelApplicationDao;
        private EmployeeInfoService employeeInfoService;
        private SqlActionDao actionDao;

        private int appReviewId;
        private TravelApplication application;
        private TravelRole travelRole;
        private List<Action> actions = new ArrayList<>();

        ApplicationReviewRowMapper(TravelApplicationDao travelApplicationDao,
                                   EmployeeInfoService employeeInfoService,
                                   SqlActionDao actionDao) {
            this.travelApplicationDao = travelApplicationDao;
            this.employeeInfoService = employeeInfoService;
            this.actionDao = actionDao;
        }

        @Override
        public ApplicationReview mapRow(ResultSet rs, int rowNum) throws SQLException {
            appReviewId = rs.getInt("app_review_id");
            application = travelApplicationDao.selectTravelApplication(rs.getInt("app_id"));
            travelRole = rs.getString("traveler_role") == null
                    ? null
                    : TravelRole.valueOf(rs.getString("traveler_role"));
            actions = actionDao.selectActionsByApprovalId(appReviewId);
            return new ApplicationReview(appReviewId, application, travelRole, actions);
        }
    }
}
