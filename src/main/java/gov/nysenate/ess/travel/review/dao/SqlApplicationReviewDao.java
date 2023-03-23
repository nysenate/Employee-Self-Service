package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.request.app.dao.TravelApplicationDao;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.view.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<ApplicationReview> pendingReviewsForRole(TravelRole role) {
        var params = new MapSqlParameterSource("role", role.name());
        var sql = SqlApplicationReviewQuery.PENDING_REVIEWS_FOR_ROLE.getSql(schemaMap());
        var repViews = localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper());
        return populateRepViews(repViews);
    }

    @Override
    public List<ApplicationReview> pendingReviewsForDeptHd(Collection<Integer> empIds) {
        if (empIds.isEmpty()) {
            return new ArrayList<>();
        }
        var params = new MapSqlParameterSource("empIds", empIds);
        var sql = SqlApplicationReviewQuery.PENDING_REVIEWS_FOR_DEPT_HD.getSql(schemaMap());
        var repViews = localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper());
        return populateRepViews(repViews);
    }

    private List<ApplicationReview> populateRepViews(Collection<AppReviewRepositoryView> views) {
        var reviews = new ArrayList<ApplicationReview>();
        for (var view : views) {
            reviews.add(populateRepView(view));
        }
        return reviews;
    }

    private ApplicationReview populateRepView(AppReviewRepositoryView view) {
        var travelApplication = travelApplicationDao.selectTravelApplication(view.appId);
        var actions = actionDao.selectActionsByReviewId(view.appReviewId);
        return new ApplicationReview(view.appReviewId, travelApplication, view.travelerRole, actions, view.isShared);
    }

    /**
     * Get an ApplicationReview by its id.
     */
    @Override
    public ApplicationReview selectAppReviewById(int appReviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appReviewId", appReviewId);
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEW_BY_ID.getSql(schemaMap());
        AppReviewRepositoryView view = localNamedJdbc.queryForObject(sql, params, new ApplicationReviewRowMapper());
        return populateRepView(view);
    }

    @Override
    public List<ApplicationReview> pendingSharedReviews() {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", TravelRole.NONE.name())
                .addValue("disapproval", ActionType.DISAPPROVE.name());
        String sql = SqlApplicationReviewQuery.SELECT_ACTIVE_SHARED_REVIEWS.getSql(schemaMap());
        var views = localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper());
        return populateRepViews(views);
    }

    /**
     * Get a list of ApplicationReviews where the given role has performed an action on it.
     */
    @Override
    public List<ApplicationReview> reviewHistoryForRole(TravelRole role) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", role.name());
        String sql = SqlApplicationReviewQuery.SELECT_APP_REVIEW_HISTORY_FOR_ROLE.getSql(schemaMap());
        var views = localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper());
        return populateRepViews(views);
    }

    @Override
    public List<ApplicationReview> reviewHistoryForDeptHead(Employee departmentHead) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("role", TravelRole.DEPARTMENT_HEAD.name())
                .addValue("empId", departmentHead.getEmployeeId());
        String sql = SqlApplicationReviewQuery.SELECT_APP_REVIEW_HISTORY_FOR_DEPT_HD.getSql(schemaMap());
        var views = localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper());
        return populateRepViews(views);
    }

    @Override
    public ApplicationReview selectAppReviewByAppId(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlApplicationReviewQuery.SELECT_APPLICATION_REVIEW_BY_APP_ID.getSql(schemaMap());
        var view = localNamedJdbc.queryForObject(sql, params, new ApplicationReviewRowMapper());
        return populateRepView(view);
    }

    @Override
    public List<ApplicationReview> approvedAppReviews(LocalDate from, LocalDate to) {
        var params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);
        String sql = SqlApplicationReviewQuery.SELECT_APP_REVIEWS_FOR_RECONCILIATION.getSql(schemaMap());
        var repViews = localNamedJdbc.query(sql, params, new ApplicationReviewRowMapper());
        return populateRepViews(repViews);
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
