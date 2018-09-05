package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.address.TravelAddressDao;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowanceDao;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowanceDao;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowanceDao;
import gov.nysenate.ess.travel.application.destination.Destination;
import gov.nysenate.ess.travel.application.destination.DestinationDao;
import gov.nysenate.ess.travel.application.route.RouteDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private RouteDao routeDao;
    @Autowired private DestinationDao destinationDao;
    @Autowired private MileageAllowanceDao mileageAllowanceDao;
    @Autowired private MealAllowanceDao mealAllowanceDao;
    @Autowired private LodgingAllowanceDao lodgingAllowanceDao;
    @Autowired private EmployeeInfoService employeeInfoService;

    @Override
    @Transactional(value = "localTxManager")
    public synchronized void insertTravelApplication(TravelApplication app) {
        insertApplication(app);
        insertApplicationVersion(app);
        routeDao.insertRoute(app.getVersionId(), app.getRoute());
        destinationDao.insertDestinations(app.getVersionId(), app.getDestinations());
        mileageAllowanceDao.insertMileageAllowances(app.getVersionId(), app.getMileageAllowances());
        mealAllowanceDao.insertMealAllowances(app.getVersionId(), app.getMealAllowances());
        lodgingAllowanceDao.insertLodgingAllowances(app.getVersionId(), app.getLodgingAllowances());
    }

    @Override
    public TravelApplication getTravelApplication(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id.toString());
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new TravelApplicationRowMapper(routeDao, destinationDao,
                mileageAllowanceDao, mealAllowanceDao, lodgingAllowanceDao, employeeInfoService));
    }

    @Override
    public List<TravelApplication> getActiveTravelApplications(int travelerId) {
        MapSqlParameterSource params = new MapSqlParameterSource("travelerId", travelerId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_TRAVELER.getSql(schemaMap());
        TravelApplicationRowMapper mapper = new TravelApplicationRowMapper(routeDao, destinationDao,
                mileageAllowanceDao, mealAllowanceDao, lodgingAllowanceDao, employeeInfoService);
        return localNamedJdbc.query(sql, params, mapper);
    }

    private void insertApplication(TravelApplication app) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", app.getId().toString())
                .addValue("currentVersionId", app.getVersionId().toString())
                .addValue("travelerId", app.getTraveler().getEmployeeId())
                .addValue("submitterId", app.getSubmitter().getEmployeeId())
                .addValue("createdDateTime", toDate(app.getSubmittedDateTime()));
        String sql = SqlTravelApplicationQuery.INSERT_APP.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertApplicationVersion(TravelApplication app) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", app.getId().toString())
                .addValue("currentVersionId", app.getVersionId().toString())
                .addValue("createdDateTime", toDate(app.getModifiedDateTime()))
                .addValue("createdBy", app.getModifiedBy().getEmployeeId())
                .addValue("isDeleted", app.isDeleted())
                .addValue("purposeOfTravel", app.getPurposeOfTravel())
                .addValue("tollsAllowance", app.getTolls().toString())
                .addValue("parkingAllowance", app.getParking().toString())
                .addValue("alternateAllowance", app.getAlternate().toString())
                .addValue("trainAndPlaneAllowance", app.getTrainAndAirplane().toString())
                .addValue("registrationAllowance", app.getRegistration().toString());
        String sql = SqlTravelApplicationQuery.INSERT_APP_VERSION.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlTravelApplicationQuery implements BasicSqlQuery {
        INSERT_APP(
                "INSERT INTO ${travelSchema}.app(id, current_version_id, traveler_id, " +
                        "submitter_id, created_date_time) \n" +
                        "VALUES (:appId::uuid, :currentVersionId::uuid, :travelerId, :submitterId, :createdDateTime)"
        ),

        INSERT_APP_VERSION(
                "INSERT INTO ${travelSchema}.app_version \n" +
                        "(id, app_id, purpose_of_travel, tolls_allowance, parking_allowance, \n" +
                        "alternate_allowance, train_and_plane_allowance, registration_allowance, \n" +
                        "created_date_time, created_by, is_deleted) \n" +
                        "VALUES (:currentVersionId::uuid, :appId::uuid, :purposeOfTravel, :tollsAllowance, :parkingAllowance, \n" +
                        ":alternateAllowance, :trainAndPlaneAllowance, :registrationAllowance, :createdDateTime, \n" +
                        ":createdBy, :isDeleted)"
        ),
        TRAVEL_APP_COLUMNS(
                "SELECT app.id, app.current_version_id, app.traveler_id, app.submitter_id, app.created_date_time as submitted_date_time,\n" +
                        "  app_version.id, app_version.created_date_time as modified_date_time, app_version.created_by as modified_by,\n" +
                        "  app_version.is_deleted, app_version.purpose_of_travel, app_version.tolls_allowance, app_version.parking_allowance,\n" +
                        "  app_version.alternate_allowance, app_version.train_and_plane_allowance, app_version.registration_allowance,\n" +
                        "  app_version.registration_allowance"
        ),
        TRAVEL_APP_TABLES(
                "FROM ${travelSchema}.app \n" +
                        "INNER JOIN ${travelSchema}.app_version ON app.current_version_id = app_version.id"
        ),
        SELECT_APP_BY_ID(
                TRAVEL_APP_COLUMNS.getSql() + " \n" +
                        TRAVEL_APP_TABLES.getSql() + "\n" +
                        "WHERE app.id = :id::uuid"
        ),
        SELECT_APP_BY_TRAVELER(
                TRAVEL_APP_COLUMNS.getSql() + "\n" +
                        TRAVEL_APP_TABLES.getSql() + "\n" +
                        "WHERE app.traveler_id = :travelerId AND is_deleted = false"
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
        private DestinationDao destinationDao;
        private MileageAllowanceDao mileageAllowanceDao;
        private MealAllowanceDao mealAllowanceDao;
        private LodgingAllowanceDao lodgingAllowanceDao;
        private EmployeeInfoService employeeInfoService;

        public TravelApplicationRowMapper(RouteDao routeDao, DestinationDao destinationDao, MileageAllowanceDao mileageAllowanceDao,
                                          MealAllowanceDao mealAllowanceDao, LodgingAllowanceDao lodgingAllowanceDao, EmployeeInfoService employeeInfoService) {
            this.routeDao = routeDao;
            this.destinationDao = destinationDao;
            this.mileageAllowanceDao = mileageAllowanceDao;
            this.mealAllowanceDao = mealAllowanceDao;
            this.lodgingAllowanceDao = lodgingAllowanceDao;
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public TravelApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
            UUID id = UUID.fromString(rs.getString("id"));
            UUID versionId = UUID.fromString(rs.getString("current_version_id"));
            Employee traveler = employeeInfoService.getEmployee(rs.getInt("traveler_id"));
            Employee submitter = employeeInfoService.getEmployee(rs.getInt("submitter_id"));
            TravelApplication app = new TravelApplication(id, versionId, traveler, submitter);
            app.setPurposeOfTravel(rs.getString("purpose_of_travel"));
            app.setRoute(routeDao.getRoute(versionId));
            app.setDestinations(destinationDao.getDestinations(versionId));
            app.setMileageAllowances(mileageAllowanceDao.getMileageAllowance(versionId));
            app.setMealAllowances(mealAllowanceDao.getMealAllowances(versionId));
            app.setLodgingAllowances(lodgingAllowanceDao.getLodgingAllowances(versionId));
            app.setTolls(new Dollars(rs.getString("tolls_allowance")));
            app.setParking(new Dollars(rs.getString("parking_allowance")));
            app.setAlternate(new Dollars(rs.getString("alternate_allowance")));
            app.setTrainAndAirplane(new Dollars(rs.getString("train_and_plane_allowance")));
            app.setRegistration(new Dollars(rs.getString("registration_allowance")));
            app.setDeleted(rs.getBoolean("is_deleted"));
            app.setSubmittedDateTime(getLocalDateTimeFromRs(rs, "submitted_date_time"));
            app.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
            app.setModifiedBy(employeeInfoService.getEmployee(rs.getInt("modified_by")));
            return app;
        }
    }
}
