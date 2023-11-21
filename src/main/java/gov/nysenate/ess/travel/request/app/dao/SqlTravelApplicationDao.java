package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.app.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private SqlAmendmentDao amendmentDao;
    @Autowired private EmployeeInfoService employeeInfoService;

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
        for (Amendment amd : app.amendments()) {
            if (amd.amendmentId() == 0) {
                amendmentDao.saveAmendment(amd, app.id());
            }
        }
    }

    private void saveApplication(TravelApplication app) {
        if (updateApplication(app) == 0) {
            insertApplication(app);
        }
    }

    /*
     * Attempts to update the Travel Application status and status note.
     * All other fields in this table should be considered immutable.
     * Returns 1 if the app was updated, 0 if the app did not exist in the table.
     */
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
        TravelApplicationHandler handler = new TravelApplicationHandler();
        localNamedJdbc.query(sql, params, handler);
        TravelAppRepositoryView appRepView = handler.results();
        return populateApplicationDetails(appRepView);
    }

    @Override
    public List<TravelApplication> selectTravelApplications(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_TRAVELER.getSql(schemaMap());
        TravelApplicationListHandler handler = new TravelApplicationListHandler();
        localNamedJdbc.query(sql, params, handler);
        List<TravelAppRepositoryView> appRepViews = handler.getApplications();
        return appRepViews.stream()
                .map(this::populateApplicationDetails)
                .collect(Collectors.toList());
    }

    private TravelApplication populateApplicationDetails(TravelAppRepositoryView view) {
        Employee traveler = employeeInfoService.getEmployee(view.travelerEmpId);
        List<Amendment> amds = view.amendmentViews.stream()
                .map(v -> amendmentDao.selectAmendment(v))
                .collect(Collectors.toList());

        TravelApplication app = new TravelApplication(view.appId, traveler,
                view.travelerDeptHeadEmpId, view.status, amds);
        Amendment amd = app.activeAmendment();
        app.setAmendmentId(amd.amendmentId());
        app.setPurposeOfTravel(amd.purposeOfTravel());
        app.setRoute(amd.route());
        app.setAllowances(amd.allowances());
        app.setAttachments(amd.attachments());
        app.setCreatedDateTime(amd.createdDateTime());
        app.setCreatedBy(amd.createdBy());
        app.setMealPerDiems(amd.mealPerDiems());
        app.setLodgingPerDiems(amd.lodgingPerDiems());
        app.setMileagePerDiems(amd.mileagePerDiems());
        return app;
    }

    private MapSqlParameterSource travelAppParams(TravelApplication app) {
        return new MapSqlParameterSource()
                .addValue("appId", app.getAppId())
                .addValue("travelerId", app.getTraveler().getEmployeeId())
                .addValue("travelerDeptHeadEmpId", app.getTravelerDeptHeadEmpId())
                .addValue("submittedById", app.getSubmittedBy().getEmployeeId())
                .addValue("status", app.status().status().name())
                .addValue("note", app.status().note());
    }
}
