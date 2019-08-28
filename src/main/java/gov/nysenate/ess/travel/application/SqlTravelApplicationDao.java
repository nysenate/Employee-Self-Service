package gov.nysenate.ess.travel.application;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.overrides.perdiem.SqlPerDiemOverridesDao;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteDao;
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
import java.util.*;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private RouteDao routeDao;
    @Autowired private SqlAllowancesDao allowancesDao;
    @Autowired private SqlPerDiemOverridesDao perDiemOverridesDao;
    @Autowired private SqlAmendmentStatusDao statusDao;
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
        insertApplication(app);
        for (Amendment amd : app.amendments) {
            if (amd.amendmentId() == 0) {
                insertAmendment(amd, app.appId);
                routeDao.saveRoute(app.activeAmendment().route(), amd.amendmentId());
                allowancesDao.saveAllowances(app.activeAmendment().allowances(), amd.amendmentId());
                perDiemOverridesDao.savePerDiemOverrides(app.activeAmendment().perDiemOverrides(), amd.amendmentId());
                statusDao.saveAmendmentStatus(app.activeAmendment().status(), amd.amendmentId());
            }
        }
    }

    @Override
    public TravelApplication selectTravelApplication(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_ID.getSql(schemaMap());
        AmendmentRowMapper amdRowMapper = new AmendmentRowMapper(routeDao, allowancesDao, perDiemOverridesDao, employeeInfoService);
        TravelApplicationHandler handler = new TravelApplicationHandler(amdRowMapper, employeeInfoService);
        localNamedJdbc.query(sql, params, handler);
        return handler.results();
    }

    @Override
    public List<TravelApplication> selectTravelApplications(int travelerId) {
        MapSqlParameterSource params = new MapSqlParameterSource("travelerId", travelerId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_TRAVELER.getSql(schemaMap());
        AmendmentRowMapper amdHandler = new AmendmentRowMapper(routeDao, allowancesDao, perDiemOverridesDao, employeeInfoService);
        TravelApplicationListHandler handler = new TravelApplicationListHandler(amdHandler, employeeInfoService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getApplications();
    }

    private void insertApplication(TravelApplication app) {
        // If the app id is set, its already in the database.
        // Apps should never be modified so we can return.
        if (app.appId != 0) {
            return;
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("travelerId", app.getTraveler().getEmployeeId());
        String insertSql = SqlTravelApplicationQuery.INSERT_APP.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(insertSql, params, keyHolder);
        app.appId = (Integer) keyHolder.getKeys().get("app_id");
    }

    private void insertAmendment(Amendment amd, int appId) {
        // If amendment id is set, its already in the database.
        // Amendments should never be modified so we can return.
        if (amd.amendmentId() != 0) {
            return;
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId)
                .addValue("purposeOfTravel", amd.purposeOfTravel())
                .addValue("createdBy", amd.createdBy().getEmployeeId())
                .addValue("version", amd.version().name());
        String sql = SqlTravelApplicationQuery.INSERT_AMENDMENT.getSql(schemaMap());
        KeyHolder kh = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, kh);
        amd.setAmendmentId((Integer) kh.getKeys().get("amendment_id"));
    }

    private enum SqlTravelApplicationQuery implements BasicSqlQuery {
        INSERT_APP(
                "INSERT INTO ${travelSchema}.app(traveler_id) \n" +
                        "VALUES (:travelerId)"
        ),
        INSERT_AMENDMENT(
                "INSERT INTO ${travelSchema}.amendment \n" +
                        "(app_id, version, purpose_of_travel, created_by) \n" +
                        "VALUES (:appId, :version, :purposeOfTravel, :createdBy)"
        ),
        INSERT_APP_VERSION(
                "INSERT INTO ${travelSchema}.app_version \n" +
                        "(app_version_id, app_id, purpose_of_travel, created_by, submitted_date_time) \n" +
                        "VALUES (:versionId, :appId, :purposeOfTravel, :createdBy, :submittedDateTime)"
        ),
        TRAVEL_APP_SELECT(
                "SELECT app.app_id, app.traveler_id,\n" +
                        " amendment.amendment_id, amendment.app_id, amendment.version,\n" +
                        " amendment.purpose_of_travel, amendment.created_date_time," +
                        " amendment.created_by, status.amendment_status_id, status.created_date_time,\n" +
                        " status.status, status.note\n" +
                        " FROM ${travelSchema}.app\n" +
                        " INNER JOIN ${travelSchema}.amendment amendment ON amendment.app_id = app.app_id \n" +
                        " LEFT JOIN ${travelSchema}.amendment_status status ON amendment.amendment_id = status.amendment_id \n"
        ),
        SELECT_APP_BY_ID(
                TRAVEL_APP_SELECT.getSql() + " \n" +
                        "WHERE app.app_id = :appId"
        ),
        SELECT_APP_BY_TRAVELER(
                TRAVEL_APP_SELECT.getSql() + "\n" +
                        "WHERE app.traveler_id = :travelerId"
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
        private List<Amendment> amendments;

        private EmployeeInfoService employeeInfoService;
        private AmendmentRowMapper amendmentRowMapper;

        public TravelApplicationHandler(AmendmentRowMapper amendmentRowMapper,
                                        EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
            this.amendmentRowMapper = amendmentRowMapper;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (appId == 0) {
                appId = rs.getInt("app_id");
            }
            if (traveler == null) {
                traveler = employeeInfoService.getEmployee(rs.getInt("traveler_id"));
            }
            amendments.add(amendmentRowMapper.mapRow(rs, rs.getRow()));
        }

        public TravelApplication results() {
            return new TravelApplication(appId, traveler, amendments);
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
                Amendment amd = amdRowMapper.mapRow(rs, rs.getRow());
                TravelApplication app = new TravelApplication(appId, traveler, Lists.newArrayList(amd));
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
        private SqlPerDiemOverridesDao perDiemOverridesDao;
        private EmployeeInfoService employeeInfoService;

        public AmendmentRowMapper(RouteDao routeDao, SqlAllowancesDao allowancesDao,
                                  SqlPerDiemOverridesDao perDiemOverridesDao, EmployeeInfoService employeeInfoService) {
            this.routeDao = routeDao;
            this.allowancesDao = allowancesDao;
            this.perDiemOverridesDao = perDiemOverridesDao;
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public Amendment mapRow(ResultSet rs, int i) throws SQLException {
            int amdId = rs.getInt("amendment_id");
            int appId = rs.getInt("app_id");
            Version version = Version.valueOf(rs.getString("version"));
            String pot = rs.getString("purpose_of_travel");
            LocalDateTime createdDateTime = getLocalDateTimeFromRs(rs, "created_date_time");
            Employee createdBy = employeeInfoService.getEmployee(rs.getInt("created_by"));
            Route route = routeDao.selectRoute(amdId);
            Allowances allowances = allowancesDao.selectAllowances(amdId);
            PerDiemOverrides pdOverrides = perDiemOverridesDao.selectPerDiemOverrides(amdId);
            TravelApplicationStatus status = new TravelApplicationStatus(
                    rs.getInt("amendment_status_id"),
                    rs.getString("status"),
                    getLocalDateTimeFromRs(rs, "created_date_time"),
                    rs.getString("note")
            );

            Amendment amd = new Amendment.Builder()
                    .withAmendmentId(amdId)
                    .withVersion(version)
                    .withPurposeOfTravel(pot)
                    .withRoute(route)
                    .withAllowances(allowances)
                    .withPerDiemOverrides(pdOverrides)
                    .withStatus(status)
                    .withCreatedDateTime(createdDateTime)
                    .withCreatedBy(createdBy)
                    .build();
            return amd;
        }
    }
}
