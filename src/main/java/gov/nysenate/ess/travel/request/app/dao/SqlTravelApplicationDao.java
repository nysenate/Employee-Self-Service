package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.request.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.request.allowances.lodging.SqlLodgingPerDiemsDao;
import gov.nysenate.ess.travel.request.allowances.meal.SqlMealPerDiemsDao;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.amendment.Version;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import gov.nysenate.ess.travel.request.route.RouteDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private RouteDao routeDao;
    @Autowired private SqlAllowancesDao allowancesDao;
    @Autowired private SqlMealPerDiemsDao mealPerDiemsDao;
    @Autowired private SqlLodgingPerDiemsDao lodgingPerDiemsDao;
    @Autowired private SqlAttachmentDao attachmentDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private SqlAmendmentDao amendmentDao;

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
                insertAmendment(amd, app.id());
                routeDao.saveRoute(amd.route(), amd.amendmentId());
                allowancesDao.saveAllowances(amd.allowances(), amd.amendmentId());
                mealPerDiemsDao.saveMealPerDiems(amd.mealPerDiems(), amd.amendmentId());
                lodgingPerDiemsDao.saveLodgingPerDiems(amd.lodgingPerDiems(), amd.amendmentId());
                attachmentDao.saveAttachments(amd.attachments(), amd.amendmentId());
            }
        }
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

        return new TravelApplication(view.appId, traveler,
                view.travelerDepartmentId, view.status, amds);
    }

    private void saveApplication(TravelApplication app) {
        if (updateApplication(app) == 0) {
            insertApplication(app);
        }
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

    private MapSqlParameterSource travelAppParams(TravelApplication app) {
        return new MapSqlParameterSource()
                .addValue("appId", app.getAppId())
                .addValue("travelerId", app.getTraveler().getEmployeeId())
                .addValue("travelerDepartmentId", app.getTravelerDepartmentId())
                .addValue("submittedById", app.getSubmittedBy().getEmployeeId())
                .addValue("status", app.status().status().name())
                .addValue("note", app.status().note());
    }

    private void insertAmendment(Amendment amd, int appId) {
        // If amendment id is set, its already in the database.
        // Amendments should never be modified so we can return.
        if (amd.amendmentId() != 0) {
            return;
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId)
                .addValue("eventType", amd.purposeOfTravel().eventType().name())
                .addValue("eventName", amd.purposeOfTravel().eventName())
                .addValue("additionalPurpose", amd.purposeOfTravel().additionalPurpose())
                .addValue("createdBy", amd.createdBy().getEmployeeId())
                .addValue("version", amd.version().name());
        String sql = SqlTravelApplicationQuery.INSERT_AMENDMENT.getSql(schemaMap());
        KeyHolder kh = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, kh);
        amd.setAmendmentId((Integer) kh.getKeys().get("amendment_id"));
    }

    private enum SqlTravelApplicationQuery implements BasicSqlQuery {
        INSERT_APP(
                "INSERT INTO ${travelSchema}.app(traveler_id, submitted_by_id, status, status_note, traveler_department_id) \n" +
                        "VALUES (:travelerId, :submittedById, :status, :note, :travelerDepartmentId)"
        ),
        UPDATE_APP(
                "UPDATE ${travelSchema}.app \n" +
                        "SET status = :status, status_note = :note \n" +
                        "WHERE app_id = :appId"
        ),
        INSERT_AMENDMENT(
                "INSERT INTO ${travelSchema}.amendment \n" +
                        "(app_id, version, event_type, event_name, additional_purpose, created_by) \n" +
                        "VALUES (:appId, :version, :eventType, :eventName, :additionalPurpose, :createdBy)"
        ),
        TRAVEL_APP_SELECT(
                "SELECT app.app_id, app.traveler_id, app.status, app.status_note, app.traveler_department_id,\n" +
                        " amendment.amendment_id, amendment.app_id, amendment.version,\n" +
                        " amendment.event_type, amendment.event_name, amendment.additional_purpose,\n" +
                        " amendment.created_date_time, amendment.created_by\n" +
                        " FROM ${travelSchema}.app\n" +
                        " INNER JOIN ${travelSchema}.amendment amendment ON amendment.app_id = app.app_id \n"
        ),
        SELECT_APP_BY_ID(
                TRAVEL_APP_SELECT.getSql() + " \n" +
                        "WHERE app.app_id = :appId"
        ),
        SELECT_APP_BY_TRAVELER(
                TRAVEL_APP_SELECT.getSql() + "\n" +
                        "WHERE (app.traveler_id = :userId OR app.submitted_by_id = :userId)"
        );

        private String sql;

        SqlTravelApplicationQuery(String sql) {
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

    private class TravelApplicationHandler extends BaseHandler {

        private TravelAppRepositoryView view;
        private AmendmentRowMapper amdRowMapper;

        public TravelApplicationHandler() {
            view = new TravelAppRepositoryView();
            amdRowMapper = new AmendmentRowMapper();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            view.appId = rs.getInt("app_id");
            view.travelerEmpId = rs.getInt("traveler_id");
            view.travelerDepartmentId = rs.getInt("traveler_department_id");
            view.status  = StringUtils.isBlank(rs.getString("status"))
                    ? new TravelApplicationStatus()
                    : new TravelApplicationStatus(rs.getString("status"), rs.getString("status_note"));
            view.amendmentViews.add(amdRowMapper.mapRow(rs, rs.getRow()));
        }

        public TravelAppRepositoryView results() {
            return view;
        }
    }

    private class TravelApplicationListHandler extends BaseHandler {

        private Map<Integer, TravelAppRepositoryView> idToApp;
        private AmendmentRowMapper amdRowMapper;

        TravelApplicationListHandler() {
            idToApp = new HashMap<>();
            amdRowMapper = new AmendmentRowMapper();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            var appId = rs.getInt("app_id");
            if (idToApp.containsKey(appId)) {
                idToApp.get(appId).amendmentViews.add(amdRowMapper.mapRow(rs, rs.getRow()));
            } else {
                // create new app and add amd.
                TravelAppRepositoryView view = new TravelAppRepositoryView();
                view.appId = appId;
                view.travelerEmpId = rs.getInt("traveler_id");
                view.travelerDepartmentId = rs.getInt("traveler_department_id");
                view.status = StringUtils.isBlank(rs.getString("status"))
                        ? new TravelApplicationStatus()
                        : new TravelApplicationStatus(rs.getString("status"), rs.getString("status_note"));
                view.amendmentViews.add(amdRowMapper.mapRow(rs, rs.getRow()));
                idToApp.put(appId, view);
            }
        }

        List<TravelAppRepositoryView> getApplications() {
            return new ArrayList<>(idToApp.values());
        }
    }

    private class AmendmentRowMapper extends BaseRowMapper<AmendmentRepositoryView> {

        private AmendmentRepositoryView view;

        public AmendmentRowMapper() {
            view = new AmendmentRepositoryView();
        }

        @Override
        public AmendmentRepositoryView mapRow(ResultSet rs, int i) throws SQLException {
            view.amendmentId = rs.getInt("amendment_id");
            view.appId = rs.getInt("app_id");
            view.version = Version.valueOf(rs.getString("version"));
            view.pot =  new PurposeOfTravel(
                    EventType.valueOf(rs.getString("event_type")),
                    rs.getString("event_name"),
                    rs.getString("additional_purpose")
            );
            view.createdDateTime = getLocalDateTimeFromRs(rs, "created_date_time");
            view.createdByEmpId = rs.getInt("created_by");
            return view;
        }
    }
}
