package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.request.allowances.lodging.SqlLodgingPerDiemsDao;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.SqlMealPerDiemsDao;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.allowances.mileage.SqlMileagePerDiemsDao;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.request.route.RouteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RouteDao routeDao;
    @Autowired private SqlAllowancesDao allowancesDao;
    @Autowired private SqlMealPerDiemsDao mealPerDiemsDao;
    @Autowired private SqlLodgingPerDiemsDao sqlLodgingPerDiemsDao;
    @Autowired private SqlMileagePerDiemsDao mileagePerDiemsDao;
    @Autowired private SqlAttachmentDao attachmentDao;

    /**
     * Persists a {@link TravelApplication} to the database.
     * <p>
     * SQL Updates should not be made to the TravelApplication or any of its data.
     * Changes to an application are done by creating and inserting a new amendment only.
     * <p>
     * This method will check the id's of an application and its amendments to see if
     * they exist in the database. Applications and amendments existing in the database
     * will be ignored.
     * If:
     * id == 0:   Will be inserted into the database.
     * id != 0:   Will be ignored.
     *
     * @param app
     */
    @Override
    @Transactional(value = "localTxManager")
    public synchronized void saveTravelApplication(TravelApplication app) {
        saveApplication(app);
        routeDao.saveRoute(app.getRoute(), app.getAppId());
        allowancesDao.saveAllowances(app.getAllowances(), app.getAppId());
        mealPerDiemsDao.updateMealPerDiems(app.getMealPerDiems(), app.getAppId());
        sqlLodgingPerDiemsDao.updateLodgingPerDiems(app.getLodgingPerDiems(), app.getAppId());
        mileagePerDiemsDao.updateMileagePerDiems(app.getMileagePerDiems(), app.getAppId());
        attachmentDao.updateAttachments(app.getAttachments(), app.getAppId());
    }

    private void saveApplication(TravelApplication app) {
        if (updateApplication(app) == 0) {
            insertApplication(app);
        }
    }

    private int updateApplication(TravelApplication app) {
        MapSqlParameterSource params = travelAppParams(app);
        String sql = SqlTravelApplicationQuery.UPDATE_APP.getSql(schemaMap());
        return localNamedJdbc.update(sql, params);
    }

    /*
     * Inserts the app into the database and sets its appId.
     */
    private void insertApplication(TravelApplication app) {
        MapSqlParameterSource params = travelAppParams(app);
        String insertSql = SqlTravelApplicationQuery.INSERT_APP.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(insertSql, params, keyHolder);
        app.setAppId((Integer) keyHolder.getKeys().get("app_id"));
    }

    @Override
    public TravelApplication selectTravelApplication(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_ID.getSql(schemaMap());
        TravelAppRepositoryView appRepView = localNamedJdbc.queryForObject(sql, params, new TravelApplicationRowMapper());
        return populateApplicationDetails(appRepView);
    }

    @Override
    public List<TravelApplication> selectTravelApplications(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_TRAVELER.getSql(schemaMap());
        List<TravelAppRepositoryView> appRepViews = localNamedJdbc.query(sql, params, new TravelApplicationRowMapper());
        return appRepViews.stream()
                .map(this::populateApplicationDetails)
                .collect(Collectors.toList());
    }

    private TravelApplication populateApplicationDetails(TravelAppRepositoryView view) {
        Employee traveler = employeeInfoService.getEmployee(view.travelerEmpId);
        Employee submittedBy = employeeInfoService.getEmployee(view.submittedByEmpId);
        Employee modifiedBy = employeeInfoService.getEmployee(view.modifiedByEmpId);

        TravelApplication app = new TravelApplication(view.appId, traveler,
                view.travelerDeptHeadEmpId, view.status);

        Route route = routeDao.selectRoute(view.appId);
        Allowances allowances = allowancesDao.selectAllowances(view.appId);
        MealPerDiems mpds = mealPerDiemsDao.selectMealPerDiems(view.appId);
        LodgingPerDiems lpds = sqlLodgingPerDiemsDao.selectLodgingPerDiems(view.appId);
        MileagePerDiems mileagePerDiems = mileagePerDiemsDao.selectMileagePerDiems(view.appId);
        List<Attachment> attachments = attachmentDao.selectAttachments(view.appId);

        app.setPurposeOfTravel(view.pot);
        app.setCreatedDateTime(view.submittedDateTime);
        app.setSubmittedBy(submittedBy);
        app.setModifiedBy(modifiedBy);
        app.setModifiedDateTime(view.modifiedDateTime);
        app.setRoute(route);
        app.setAllowances(allowances);
        app.setMealPerDiems(mpds);
        app.setLodgingPerDiems(lpds);
        app.setMileagePerDiems(mileagePerDiems);
        app.setAttachments(attachments);
        return app;
    }

    private MapSqlParameterSource travelAppParams(TravelApplication app) {
        return new MapSqlParameterSource()
                .addValue("appId", app.getAppId())
                .addValue("travelerId", app.getTraveler().getEmployeeId())
                .addValue("travelerDeptHeadEmpId", app.getTravelerDeptHeadEmpId())
                .addValue("submittedById", app.getSubmittedBy().getEmployeeId())
                .addValue("status", app.status().status().name())
                .addValue("note", app.status().note())
                .addValue("eventType", app.getPurposeOfTravel().eventType().name())
                .addValue("eventName", app.getPurposeOfTravel().eventName())
                .addValue("additionalPurpose", app.getPurposeOfTravel().additionalPurpose())
                .addValue("modifiedBy", app.getModifiedBy().getEmployeeId())
                .addValue("modifiedDateTime", toDate(LocalDateTime.now()));
    }
}
