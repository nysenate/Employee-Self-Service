package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.application.overrides.perdiem.SqlPerDiemOverridesDao;
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
import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private RouteDao routeDao;
    @Autowired private SqlAllowancesDao allowancesDao;
    @Autowired private SqlPerDiemOverridesDao perDiemOverridesDao;
    @Autowired private SqlTravelApplicationStatusDao statusDao;
    @Autowired private EmployeeInfoService employeeInfoService;

    @Override
    @Transactional(value = "localTxManager")
    public synchronized void insertTravelApplication(TravelApplication app) {
        int previousAppVersionId = app.getVersionId();
        app.setVersionId(fetchNextVersionId());
        insertApplication(app);
        insertApplicationVersion(app);

        routeDao.saveRoute(app.getRoute(), app.getVersionId(), previousAppVersionId);
        allowancesDao.saveAllowances(app.getAllowances(), app.getVersionId(), previousAppVersionId);
        perDiemOverridesDao.savePerDiemOverrides(app.getPerDiemOverrides(), app.getVersionId());
        statusDao.saveTravelApplicationStatus(app.status(), app.getVersionId());
    }

    @Override
    public void deleteTravelApplication(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlTravelApplicationQuery.DELETE_APP.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private int fetchNextVersionId() {
        String sql = SqlTravelApplicationQuery.FETCH_NEXT_VERSION_ID.getSql(schemaMap());
        return localNamedJdbc.query(sql, (rs, i) -> rs.getInt("nextval")).get(0);
    }

    @Override
    public TravelApplication selectTravelApplication(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params,
                new TravelApplicationRowMapper(routeDao, allowancesDao, perDiemOverridesDao, employeeInfoService));
    }

    @Override
    public List<TravelApplication> selectTravelApplications(int travelerId) {
        MapSqlParameterSource params = new MapSqlParameterSource("travelerId", travelerId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_TRAVELER.getSql(schemaMap());
        TravelApplicationListHandler handler = new TravelApplicationListHandler(
                routeDao, allowancesDao, perDiemOverridesDao, employeeInfoService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getApplications();
    }

    private void insertApplication(TravelApplication app) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", app.getAppId())
                .addValue("versionId", app.getVersionId())
                .addValue("travelerId", app.getTraveler().getEmployeeId());

        String updateSql = SqlTravelApplicationQuery.UPDATE_APP.getSql(schemaMap());
        boolean wasUpdated = localNamedJdbc.update(updateSql, params) == 1;
        if (!wasUpdated) {
            String insertSql = SqlTravelApplicationQuery.INSERT_APP.getSql(schemaMap());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            localNamedJdbc.update(insertSql, params, keyHolder);
            app.setAppId((Integer) keyHolder.getKeys().get("app_id"));
        }
    }

    private void insertApplicationVersion(TravelApplication app) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", app.getAppId())
                .addValue("versionId", app.getVersionId())
                .addValue("purposeOfTravel", app.getPurposeOfTravel())
                .addValue("createdBy", app.getModifiedBy().getEmployeeId())
                .addValue("submittedDateTime", toDate(app.getSubmittedDateTime()));
        String sql = SqlTravelApplicationQuery.INSERT_APP_VERSION.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlTravelApplicationQuery implements BasicSqlQuery {
        FETCH_NEXT_VERSION_ID(
                "SELECT nextval('${travelSchema}.app_version_app_version_id_seq'::regclass)"
        ),
        UPDATE_APP(
                "UPDATE ${travelSchema}.app SET app_version_id = :versionId \n" +
                        " WHERE app_id = :appId"
        ),
        INSERT_APP(
                "INSERT INTO ${travelSchema}.app(app_version_id, traveler_id) \n" +
                        "VALUES (:versionId, :travelerId)"
        ),
        DELETE_APP(
                "DELETE FROM ${travelSchema}.app" +
                        " WHERE app_id = :appId"
        ),

        INSERT_APP_VERSION(
                "INSERT INTO ${travelSchema}.app_version \n" +
                        "(app_version_id, app_id, purpose_of_travel, created_by, submitted_date_time) \n" +
                        "VALUES (:versionId, :appId, :purposeOfTravel, :createdBy, :submittedDateTime)"
        ),
        TRAVEL_APP_SELECT(
                "SELECT app.app_id, app.app_version_id, app.traveler_id,\n" +
                        " version.purpose_of_travel, version.created_date_time as modified_date_time," +
                        " version.created_by as modified_by, version.submitted_date_time,\n" +
                        " status.app_version_status_id, status.created_date_time,\n" +
                        " status.status, status.note\n" +
                        " FROM ${travelSchema}.app\n" +
                        " INNER JOIN ${travelSchema}.app_version version ON app.app_version_id = version.app_version_id\n" +
                        " INNER JOIN ${travelSchema}.app_version_status status ON version.app_version_id = status.app_version_id \n"
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

    private class TravelApplicationRowMapper extends BaseRowMapper<TravelApplication> {

        private RouteDao routeDao;
        private SqlAllowancesDao allowancesDao;
        private SqlPerDiemOverridesDao perDiemOverridesDao;
        private EmployeeInfoService employeeInfoService;

        public TravelApplicationRowMapper(RouteDao routeDao,
                                          SqlAllowancesDao allowancesDao,
                                          SqlPerDiemOverridesDao perDiemOverridesDao,
                                          EmployeeInfoService employeeInfoService) {
            this.routeDao = routeDao;
            this.allowancesDao = allowancesDao;
            this.perDiemOverridesDao = perDiemOverridesDao;
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public TravelApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
            int appId = rs.getInt("app_id");
            int versionId = rs.getInt("app_version_id");
            Employee traveler = employeeInfoService.getEmployee(rs.getInt("traveler_id"));
            TravelApplication app = new TravelApplication(appId, versionId, traveler);
            app.setPurposeOfTravel(rs.getString("purpose_of_travel"));
            app.setRoute(routeDao.selectRoute(versionId));
            app.setAllowances(allowancesDao.selectAllowances(versionId));
            app.setPerDiemOverrides(perDiemOverridesDao.selectPerDiemOverrides(versionId));
            app.setSubmittedDateTime(getLocalDateTimeFromRs(rs, "submitted_date_time"));
            app.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
            app.setModifiedBy(employeeInfoService.getEmployee(rs.getInt("modified_by")));

            TravelApplicationStatus status = new TravelApplicationStatus(
                    rs.getInt("app_version_status_id"),
                    rs.getString("status"),
                    getLocalDateTimeFromRs(rs, "created_date_time"),
                    rs.getString("note")
            );

            app.setStatus(status);
            return app;
        }
    }

    private class TravelApplicationListHandler extends BaseHandler {

        private TravelApplicationRowMapper mapper;
        private List<TravelApplication> applications;

        TravelApplicationListHandler(RouteDao routeDao,
                                     SqlAllowancesDao allowancesDao,
                                     SqlPerDiemOverridesDao perDiemOverridesDao,
                                     EmployeeInfoService employeeInfoService) {
            mapper = new TravelApplicationRowMapper(routeDao, allowancesDao, perDiemOverridesDao, employeeInfoService);
            this.applications = new ArrayList<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            applications.add(mapper.mapRow(rs, rs.getRow()));
        }

        List<TravelApplication> getApplications() {
            return this.applications;
        }
    }
}
