package gov.nysenate.ess.travel.application;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.lodging.SqlLodgingPerDiemsDao;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.SqlMealPerDiemsDao;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteDao;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private RouteDao routeDao;
    @Autowired private SqlAllowancesDao allowancesDao;
    @Autowired private SqlMealPerDiemsDao mealPerDiemsDao;
    @Autowired private SqlLodgingPerDiemsDao lodgingPerDiemsDao;
    @Autowired private SqlAttachmentDao attachmentDao;
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
        for (Amendment amd : app.amendments) {
            if (amd.amendmentId() == 0) {
                insertAmendment(amd, app.appId);
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
        AmendmentRowMapper amdRowMapper = new AmendmentRowMapper(routeDao, allowancesDao, employeeInfoService,
                mealPerDiemsDao, lodgingPerDiemsDao, attachmentDao);
        TravelApplicationHandler handler = new TravelApplicationHandler(amdRowMapper, employeeInfoService);
        localNamedJdbc.query(sql, params, handler);
        return handler.results();
    }

    @Override
    public List<TravelApplication> selectTravelApplications(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_TRAVELER.getSql(schemaMap());
        AmendmentRowMapper amdHandler = new AmendmentRowMapper(routeDao, allowancesDao, employeeInfoService,
                mealPerDiemsDao, lodgingPerDiemsDao, attachmentDao);
        TravelApplicationListHandler handler = new TravelApplicationListHandler(amdHandler, employeeInfoService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getApplications();
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

        private int appId;
        private Employee traveler;
        private int travelerDepartmentId;
        private TravelApplicationStatus status;
        private List<Amendment> amendments;

        private EmployeeInfoService employeeInfoService;
        private AmendmentRowMapper amendmentRowMapper;

        public TravelApplicationHandler(AmendmentRowMapper amendmentRowMapper,
                                        EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
            this.amendmentRowMapper = amendmentRowMapper;
            this.amendments = new ArrayList<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (appId == 0) {
                appId = rs.getInt("app_id");
                traveler = employeeInfoService.getEmployee(rs.getInt("traveler_id"));
                travelerDepartmentId = rs.getInt("traveler_department_id");
                status = StringUtils.isBlank(rs.getString("status"))
                        ? new TravelApplicationStatus()
                        : new TravelApplicationStatus(rs.getString("status"), rs.getString("status_note"));
            }
            amendments.add(amendmentRowMapper.mapRow(rs, rs.getRow()));
        }

        public TravelApplication results() {
            return new TravelApplication(appId, traveler, travelerDepartmentId, status, amendments);
        }
    }

    private class TravelApplicationListHandler extends BaseHandler {

        private AmendmentRowMapper amdRowMapper;
        private Map<Integer, TravelApplication> idToApp;

        TravelApplicationListHandler(AmendmentRowMapper amdRowMapper, EmployeeInfoService employeeInfoService) {
            this.amdRowMapper = amdRowMapper;
            idToApp = new HashMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int appId = rs.getInt("app_id");
            if (idToApp.containsKey(appId)) {
                idToApp.get(appId).addAmendment(amdRowMapper.mapRow(rs, rs.getRow()));
            } else {
                Employee traveler = employeeInfoService.getEmployee(rs.getInt("traveler_id"));
                int travelerDepartmentId = rs.getInt("traveler_department_id");
                Amendment amd = amdRowMapper.mapRow(rs, rs.getRow());
                TravelApplicationStatus status = StringUtils.isBlank(rs.getString("status"))
                        ? new TravelApplicationStatus()
                        : new TravelApplicationStatus(rs.getString("status"), rs.getString("status_note"));
                TravelApplication app = new TravelApplication(appId, traveler, travelerDepartmentId, status, Lists.newArrayList(amd));
                idToApp.put(appId, app);
            }
        }

        List<TravelApplication> getApplications() {
            return new ArrayList<>(idToApp.values());
        }
    }

    private class AmendmentRowMapper extends BaseRowMapper<Amendment> {
        private RouteDao routeDao;
        private SqlAllowancesDao allowancesDao;
        private EmployeeInfoService employeeInfoService;
        private SqlMealPerDiemsDao mealPerDiemsDao;
        private SqlLodgingPerDiemsDao lodgingPerDiemsDao;
        private SqlAttachmentDao attachmentDao;

        public AmendmentRowMapper(RouteDao routeDao, SqlAllowancesDao allowancesDao,
                                  EmployeeInfoService employeeInfoService, SqlMealPerDiemsDao mealPerDiemsDao,
                                  SqlLodgingPerDiemsDao lodgingPerDiemsDao, SqlAttachmentDao attachmentDao) {
            this.routeDao = routeDao;
            this.allowancesDao = allowancesDao;
            this.employeeInfoService = employeeInfoService;
            this.mealPerDiemsDao = mealPerDiemsDao;
            this.lodgingPerDiemsDao = lodgingPerDiemsDao;
            this.attachmentDao = attachmentDao;
        }

        @Override
        public Amendment mapRow(ResultSet rs, int i) throws SQLException {
            int amdId = rs.getInt("amendment_id");
            int appId = rs.getInt("app_id");
            Version version = Version.valueOf(rs.getString("version"));
            PurposeOfTravel pot = new PurposeOfTravel(
                    EventType.valueOf(rs.getString("event_type")),
                    rs.getString("event_name"),
                    rs.getString("additional_purpose")
            );
            LocalDateTime createdDateTime = getLocalDateTimeFromRs(rs, "created_date_time");
            Employee createdBy = employeeInfoService.getEmployee(rs.getInt("created_by"));
            Route route = routeDao.selectRoute(amdId);
            Allowances allowances = allowancesDao.selectAllowances(amdId);

            MealPerDiems mpds = mealPerDiemsDao.selectMealPerDiems(amdId);
            LodgingPerDiems lpds = lodgingPerDiemsDao.selectLodgingPerDiems(amdId);
            List<Attachment> attachments = attachmentDao.selectAttachments(amdId);

            Amendment amd = new Amendment.Builder()
                    .withAmendmentId(amdId)
                    .withVersion(version)
                    .withPurposeOfTravel(pot)
                    .withRoute(route)
                    .withAllowances(allowances)
                    .withMealPerDiems(mpds)
                    .withLodgingPerDiems(lpds)
                    .withAttachments(attachments)
                    .withCreatedDateTime(createdDateTime)
                    .withCreatedBy(createdBy)
                    .build();
            return amd;
        }
    }
}
