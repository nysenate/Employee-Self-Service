package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
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
import java.util.List;

@Repository
public class SqlApplicationReviewDao extends SqlBaseDao implements ApplicationReviewDao {

    @Autowired private TravelApplicationDao travelApplicationDao;
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

    private void insertApplicationReview(ApplicationReview appReview) {
        MapSqlParameterSource params = appReviewParams(appReview);
        String sql = SqlApplicationReviewQuery.INSERT_APPLICATION_REVIEW.getSql(schemaMap());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        appReview.setAppReviewId((Integer) keyHolder.getKeys().get("app_review_id"));
    }

    private void updateApplicationReview(ApplicationReview appReview) {
        MapSqlParameterSource params = appReviewParams(appReview);
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
        return localNamedJdbc.queryForObject(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApplicationReview> pendingReviewsByRole(TravelRole nextReviewerRole) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nextReviewerRole", nextReviewerRole == null ? null : nextReviewerRole.name())
                .addValue("disapproval", ActionType.DISAPPROVE.name());
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEWS_BY_NEXT_ROLE.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApplicationReview> pendingReviewsForDeptHead(Employee departmentHead) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nextReviewerRole", TravelRole.DEPARTMENT_HEAD.name())
                .addValue("disapproval", ActionType.DISAPPROVE.name())
                .addValue("headEmpId", departmentHead.getEmployeeId());
        String sql = SqlApplicationReviewQuery.SELECT_PENDING_APP_REVIEWS_FOR_DEPT_HD.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    @Override
    public List<ApplicationReview> pendingSharedReviews() {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", TravelRole.NONE.name())
                .addValue("disapproval", ActionType.DISAPPROVE.name());
        String sql = SqlApplicationReviewQuery.SELECT_ACTIVE_SHARED_REVIEWS.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    /**
     * Get a list of ApplicationReviews where the given role has performed an action on it.
     */
    @Override
    public List<ApplicationReview> reviewHistoryForRole(TravelRole role) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", role.name());
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEWS_WITH_ROLE_ACTION.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    private MapSqlParameterSource appReviewParams(ApplicationReview appReview) {
        return new MapSqlParameterSource()
                .addValue("appReviewId", appReview.getAppReviewId())
                .addValue("appId", appReview.application().getAppId())
                .addValue("travelerRole", appReview.travelerRole().name())
                .addValue("nextReviewerRole", appReview.nextReviewerRole().name())
                .addValue("isShared", appReview.isShared());
    }

    private enum SqlApplicationReviewQuery implements BasicSqlQuery {
        INSERT_APPLICATION_REVIEW(
                "INSERT INTO ${travelSchema}.app_review \n" +
                        " (app_id, traveler_role, next_reviewer_role, is_shared) \n" +
                        " VALUES (:appId, :travelerRole, :nextReviewerRole, :isShared)"
        ),
        UPDATE_APPLICATION_REVIEW(
                "UPDATE ${travelSchema}.app_review\n" +
                        " SET next_reviewer_role = :nextReviewerRole, is_shared = :isShared\n" +
                        " WHERE app_review_id = :appReviewId"
        ),
        SELECT_APPLICATION_REVIEWS_BY_NEXT_ROLE(
                "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,\n" +
                        " app_review.next_reviewer_role, is_shared\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE app_review.next_reviewer_role = :nextReviewerRole" +
                        " AND :disapproval NOT IN" +
                        "    (SELECT type FROM ${travelSchema}.app_review_action\n" +
                        "     WHERE app_review_action.app_review_id = app_review.app_review_id)"
        ),
        SELECT_PENDING_APP_REVIEWS_FOR_DEPT_HD(
                "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,\n" +
                        "       app_review.next_reviewer_role, is_shared\n" +
                        "FROM ${travelSchema}.app_review\n" +
                        "JOIN ${travelSchema}.app ON app.app_id = app_review.app_id\n" +
                        "  WHERE app_review.next_reviewer_role = :nextReviewerRole\n" +
                        "  AND :disapproval NOT IN\n" +
                        "    (SELECT type\n" +
                        "     FROM ${travelSchema}.app_review_action\n" +
                        "       WHERE app_review_action.app_review_id = app_review.app_review_id)\n" +
                        "  AND app.traveler_department_id IN\n" +
                        "    (SELECT department_id\n" +
                        "     FROM ${essSchema}.department\n" +
                        "       WHERE head_emp_id = :headEmpId)"
        ),
        SELECT_ACTIVE_SHARED_REVIEWS(
                "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,\n" +
                        " app_review.next_reviewer_role, is_shared\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE app_review.next_reviewer_role != :role" +
                        " AND :disapproval NOT IN" +
                        "    (SELECT type FROM ${travelSchema}.app_review_action\n" +
                        "     WHERE app_review_action.app_review_id = app_review.app_review_id)\n" +
                        " AND is_shared = true"
        ),
        SELECT_APPLICATION_REVIEWS_WITH_ROLE_ACTION(
                "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,\n" +
                        " app_review.next_reviewer_role, app_review.is_shared\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE EXISTS \n" +
                        "  (SELECT DISTINCT(action.app_review_id) \n" +
                        "  FROM ${travelSchema}.app_review_action action \n" +
                        "  WHERE action.role = :role \n" +
                        "  AND action.app_review_id = app_review.app_review_id)"
        ),
        SELECT_APPLICATION_REVIEW_BY_ID(
                "SELECT app_review_id, app_id, traveler_role, next_reviewer_role, is_shared\n" +
                        " FROM ${travelSchema}.app_review\n" +
                        " WHERE app_review_id = :appReviewId"
        );

        private String sql;

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

    private class ApplicationReviewRowMapper extends BaseRowMapper<ApplicationReview> {

        private TravelApplicationDao travelApplicationDao;
        private SqlActionDao actionDao;

        ApplicationReviewRowMapper(TravelApplicationDao travelApplicationDao,
                                   SqlActionDao actionDao) {
            this.travelApplicationDao = travelApplicationDao;
            this.actionDao = actionDao;
        }

        @Override
        public ApplicationReview mapRow(ResultSet rs, int rowNum) throws SQLException {
            int appReviewId = rs.getInt("app_review_id");
            TravelApplication application = travelApplicationDao.selectTravelApplication(rs.getInt("app_id"));
            TravelRole travelRole = rs.getString("traveler_role") == null
                    ? null
                    : TravelRole.valueOf(rs.getString("traveler_role"));
            List<Action> actions = actionDao.selectActionsByApprovalId(appReviewId);
            boolean isShared = rs.getBoolean("is_shared");
            return new ApplicationReview(appReviewId, application, travelRole, actions, isShared);
        }
    }
}
