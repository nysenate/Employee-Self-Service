package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.TravelApplicationDao;
import gov.nysenate.ess.travel.application.TravelApplicationStatus;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.view.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

        localNamedJdbc.update(sql, params);
    }

    private void updateApplicationReview(ApplicationReview appReview) {
        MapSqlParameterSource params = appReviewParams(appReview);
        String sql = SqlApplicationReviewQuery.UPDATE_APPLICATION_REVIEW.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * Select all app reviews
     * @return
     */
    public List<ApplicationReview> selectAllReviews() {
        String sql = SqlApplicationReviewQuery.ALL_APP_REVIEW.getSql(schemaMap());
        return localNamedJdbc.query(sql, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
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
        String sql = SqlApplicationReviewQuery.SELECT_APP_REVIEW_HISTORY_FOR_ROLE.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    @Override
    public List<ApplicationReview> reviewHistoryForDeptHead(Employee departmentHead) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", TravelRole.DEPARTMENT_HEAD.name())
                .addValue("headEmpId", departmentHead.getEmployeeId());
        String sql = SqlApplicationReviewQuery.SELECT_APP_REVIEW_HISTORY_FOR_DEPT_HD.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    @Override
    public ApplicationReview selectAppReviewByAppId(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEW_BY_APP_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new ApplicationReviewRowMapper(travelApplicationDao, actionDao));
    }

    private MapSqlParameterSource appReviewParams(ApplicationReview appReview) {
        return new MapSqlParameterSource()
                .addValue("appReviewId", appReview.getAppReviewId())
                .addValue("appId", appReview.application().getAppId())
                .addValue("travelerRole", appReview.travelerRole().name())
                .addValue("nextReviewerRole", appReview.nextReviewerRole().name())
                .addValue("isShared", appReview.isShared());
    }
}
