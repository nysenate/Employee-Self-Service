package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class SqlUncompletedTravelApplicationDao extends SqlBaseDao implements UncompletedTravelApplicationDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlUncompletedTravelApplicationQuery.class);

    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SqlUncompletedTravelApplicationDao(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    @Override
    @Transactional(value = "localTxManager")
    public void saveUncompletedApplication(TravelApplication app) {
        // Only store 1 uncomplete app at a time per employee.
        deleteUncompletedApplication(app.getTraveler().getEmployeeId());
        insertUncompletedApplication(app);
    }

    private void insertUncompletedApplication(TravelApplication app) {
        MapSqlParameterSource params = uncompletedAppParams(app);
        String sql = SqlUncompletedTravelApplicationQuery.INSERT_UNCOMPLETED_TRAVEL_APP.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource uncompletedAppParams(TravelApplication app) {
        return new MapSqlParameterSource()
                .addValue("travelerId", app.getTraveler().getEmployeeId())
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
    public boolean hasUncompletedApplication(int travelerId) {
        try {
            selectUncompletedApplication(travelerId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteUncompletedApplication(int travelerId) {
        MapSqlParameterSource params = new MapSqlParameterSource("travelerId", travelerId);
        String sql = SqlUncompletedTravelApplicationQuery.DELETE_UNCOMPLETED_TRAVEL_APP.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlUncompletedTravelApplicationQuery implements BasicSqlQuery {
        INSERT_UNCOMPLETED_TRAVEL_APP(
                "INSERT INTO ${travelSchema}.uncompleted_travel_application(traveler_id, app_json, modified_date_time)\n" +
                        " VALUES(:travelerId, :appJson, :modifiedDateTime)"
        ),
        SELECT_APP_BY_TRAVELER_ID(
                "SELECT traveler_id, app_json \n" +
                        "FROM ${travelSchema}.uncompleted_travel_application\n" +
                        "WHERE traveler_id = :travelerId"
        ),
        DELETE_UNCOMPLETED_TRAVEL_APP(
                "DELETE FROM ${travelSchema}.uncompleted_travel_application \n" +
                        "WHERE traveler_id = :travelerId"
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
                TravelApplication app = new TravelApplication(0, 0, traveler);
                app.setPurposeOfTravel(view.getPurposeOfTravel());
                app.setRoute(view.getRoute().toRoute());
                app.setAllowances(view.getAllowances().toAllowances());
                return app;
            } catch (IOException e) {
                logger.error("Unable to deserialize travel application", e);
            }
            return null;
        }
    }
}
