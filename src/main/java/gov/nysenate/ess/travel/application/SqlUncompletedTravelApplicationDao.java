package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class SqlUncompletedTravelApplicationDao extends SqlBaseDao implements UncompletedTravelApplicationDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlUncompletedTravelApplicationQuery.class);

    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SqlUncompletedTravelApplicationDao(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    @Override
    public void saveUncompletedApplication(TravelApplication app) {
        MapSqlParameterSource params = uncompletedAppParams(app);
        if (updateUncompletedApplication(params) < 1) {
            insertUncompletedApplication(params);
        }
    }

    // Attempts to update a current uncompleted application, returning the number of rows updated.
    private int updateUncompletedApplication(MapSqlParameterSource params) {
        String sql = SqlUncompletedTravelApplicationQuery.UPDATE_UNCOMPLETED_TRAVEL_APP.getSql(schemaMap());
        return localNamedJdbc.update(sql, params);
    }

    private void insertUncompletedApplication(MapSqlParameterSource params) {
        String sql = SqlUncompletedTravelApplicationQuery.INSERT_UNCOMPLETED_TRAVEL_APP.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource uncompletedAppParams(TravelApplication app) {
        return new MapSqlParameterSource()
                .addValue("id", app.getId().toString())
                .addValue("versionId", app.getVersionId().toString())
                .addValue("travelerId", app.getTraveler().getEmployeeId())
                .addValue("submitterId", app.getSubmitter().getEmployeeId())
                .addValue("appJson", OutputUtils.toJson(new UncompletedTravelApplicationView(app))) // Create view before serializing.
                .addValue("modifiedDateTime", toDate(LocalDateTime.now()));
    }

    @Override
    public TravelApplication selectUncompletedApplication(int travelerId) {
        MapSqlParameterSource params = new MapSqlParameterSource("travelerId", travelerId);
        String sql = SqlUncompletedTravelApplicationQuery.SELECT_APP_BY_TRAVELER_ID.getSql(schemaMap());
        UncompletedTravelApplicationRowMapper mapper = new UncompletedTravelApplicationRowMapper(employeeInfoService);
        return localNamedJdbc.queryForObject(sql, params, mapper);
    }

    @Override
    public TravelApplication selectUncompletedApplication(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id.toString());
        String sql = SqlUncompletedTravelApplicationQuery.SELECT_APP_BY_ID.getSql(schemaMap());
        UncompletedTravelApplicationRowMapper mapper = new UncompletedTravelApplicationRowMapper(employeeInfoService);
        return localNamedJdbc.queryForObject(sql, params, mapper);
    }

    @Override
    public boolean hasUncompletedApplication(int travelerId) {
        try {
            selectUncompletedApplication(travelerId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteUncompletedApplication(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id.toString());
        String sql = SqlUncompletedTravelApplicationQuery.DELETE_UNCOMPLETED_TRAVEL_APP.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlUncompletedTravelApplicationQuery implements BasicSqlQuery {
        INSERT_UNCOMPLETED_TRAVEL_APP(
                "INSERT INTO ${travelSchema}.uncompleted_travel_application(id, version_id,\n" +
                        "traveler_id, submitter_id, app_json, modified_date_time)\n" +
                        "VALUES(:id::uuid, :versionId::uuid, :travelerId, :submitterId, :appJson, :modifiedDateTime)"
        ),
        UPDATE_UNCOMPLETED_TRAVEL_APP(
                "UPDATE ${travelSchema}.uncompleted_travel_application \n" +
                        "SET app_json = :appJson, modified_date_time = :modifiedDateTime\n" +
                        "WHERE traveler_id = :travelerId"
        ),
        SELECT_APP_BY_TRAVELER_ID(
                "SELECT id, version_id, traveler_id, submitter_id, app_json \n" +
                        "FROM ${travelSchema}.uncompleted_travel_application\n" +
                        "WHERE traveler_id = :travelerId"
        ),
        SELECT_APP_BY_ID(
                 "SELECT id, version_id, traveler_id, submitter_id, app_json \n" +
                        "FROM ${travelSchema}.uncompleted_travel_application\n" +
                        "WHERE id = :id::uuid"
        ),
        DELETE_UNCOMPLETED_TRAVEL_APP(
                "DELETE FROM ${travelSchema}.uncompleted_travel_application \n" +
                        "WHERE id = :id::uuid"
        )
        ;

        private String sql;

        SqlUncompletedTravelApplicationQuery(String sql) {
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

    private class UncompletedTravelApplicationRowMapper extends BaseRowMapper<TravelApplication> {

        private EmployeeInfoService employeeInfoService;

        public UncompletedTravelApplicationRowMapper(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public TravelApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                UncompletedTravelApplicationView view = OutputUtils.jsonToObject(rs.getString("app_json"), UncompletedTravelApplicationView.class);
                Employee traveler = employeeInfoService.getEmployee(view.getTravelerId());
                Employee submitter = employeeInfoService.getEmployee(view.getSubmitterId());
                TravelApplication app = new TravelApplication(UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("version_id")), traveler, submitter);
                app.setPurposeOfTravel(view.getPurposeOfTravel());
                app.setRoute(view.getRoute().toRoute());
                app.setDestinations(view.getAccommodations().toDestinations());
                app.setMileageAllowances(view.getMileageAllowance().toMileageAllowances());
                app.setMealAllowances(view.getMealAllowance().toMealAllowances());
                app.setLodgingAllowances(view.getLodgingAllowance().toLodgingAllowances());
                app.setTolls(new Dollars(view.getTollsAllowance()));
                app.setParking(new Dollars(view.getParkingAllowance()));
                app.setAlternate(new Dollars(view.getAlternateAllowance()));
                app.setTrainAndAirplane(new Dollars(view.getTrainAndAirplaneAllowance()));
                app.setRegistration(new Dollars(view.getRegistrationAllowance()));
                return app;
            } catch (IOException e) {
                logger.error("Unable to deserialize travel application", e);
            }
            return null;
        }
    }
}
