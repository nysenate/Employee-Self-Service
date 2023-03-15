package gov.nysenate.ess.travel.request.unsubmitted;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.application.AmendmentView;
import gov.nysenate.ess.travel.api.application.TravelAppEditDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * UnsubmittedAppDao saves the state of an application as a user progresses through the New Travel Application wizard.
 */
@Repository
public class UnsubmittedAppDao extends SqlBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(UnsubmittedAppDao.class);

    /**
     * Find an unsubmitted travel application for userId.
     *
     * @param userId     The employee id of the logged in user.
     * @return An optional containing a {@link TravelAppEditDto} if a record was found.
     * Or an empty optional if no record was found.
     */
    public Optional<TravelAppEditDto> find(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);
        String sql = SqlUnsubmittedAppQuery.FIND.getSql(schemaMap());
        try {
            return Optional.ofNullable(localNamedJdbc.queryForObject(sql, params, new UnsubmittedAppRowMapper()));
//            String appJson = "";
//            List<String> jsons = localNamedJdbc.query(sql, params, new UnsubmittedAppRowMapper());
//            if (jsons.isEmpty() || jsons == null) {
//                return Optional.empty();
//            }
//            else {
//                appJson = jsons.get(0);
//                return Optional.of(deserializeAppView(appJson));
//            }
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * Saves the traveler's {@link EmployeeView} and a {@link AmendmentView} into the unsubmitted_app table.
     * Attempts to update the current record if one exists, otherwise inserts a new record.
     *
     * @param userId The logged in user emp id.
     * @param travelerView The traveler.
     * @param amendmentView The amendment data to save.
     */
    public void save(int userId, EmployeeView travelerView, AmendmentView amendmentView, int travelerDeptHeadEmpId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("travelerJson", serializeTraveler(travelerView))
                .addValue("amendmentJson", serializeAmendment(amendmentView))
                .addValue("travelerDeptHeadEmpId", travelerDeptHeadEmpId == 0 ? null : travelerDeptHeadEmpId);
        boolean isUpdated = update(params);
        if (!isUpdated) {
            insert(params);
        }
    }

    // Attempts to update a unsubmittedApp. Returns true if update was successful
    private boolean update(MapSqlParameterSource params) {
        String sql = SqlUnsubmittedAppQuery.UPDATE.getSql(schemaMap());
        return localNamedJdbc.update(sql, params) == 1;
    }

    private void insert(MapSqlParameterSource params) {
        String sql = SqlUnsubmittedAppQuery.INSERT.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * Delete the uncompleted app record for the given userId.
     *
     * @param userId
     */
    public void delete(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);
        String sql = SqlUnsubmittedAppQuery.DELETE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private String serializeAmendment(AmendmentView amdView) {
        return OutputUtils.toJson(amdView);
    }

    private AmendmentView deserializeAmendment(String json) throws IOException {
        return OutputUtils.jsonToObject(json, AmendmentView.class);
    }

    private String serializeTraveler(EmployeeView traveler) {
        return OutputUtils.toJson(traveler);
    }

    private DetailedEmployeeView deserializeTraveler(String json) throws IOException {
        return OutputUtils.jsonToObject(json, DetailedEmployeeView.class);
    }

    private enum SqlUnsubmittedAppQuery implements BasicSqlQuery {
        FIND(
                "SELECT user_id, traveler_json, amendment_json, traveler_dept_head_emp_id\n" +
                        "FROM ${travelSchema}.unsubmitted_app\n" +
                        "WHERE user_id = :userId;"
        ),
        UPDATE(
                "UPDATE ${travelSchema}.unsubmitted_app\n" +
                        "SET amendment_json = :amendmentJson, traveler_json = :travelerJson, traveler_dept_head_emp_id = :travelerDeptHeadEmpId \n" +
                        "WHERE user_id = :userId"
        ),
        INSERT(
                "INSERT INTO ${travelSchema}.unsubmitted_app(user_id, traveler_json, amendment_json, traveler_dept_head_emp_id)\n" +
                        "VALUES(:userId, :travelerJson, :amendmentJson, :travelerDeptHeadEmpId)"
        ),
        DELETE(
                "DELETE FROM ${travelSchema}.unsubmitted_app\n" +
                        "WHERE user_id = :userId;"
        ),
        ;

        private String sql;

        SqlUnsubmittedAppQuery(String sql) {
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

    private class UnsubmittedAppRowMapper implements RowMapper<TravelAppEditDto> {
        @Override
        public TravelAppEditDto mapRow(ResultSet rs, int i) throws SQLException {
            try {
                AmendmentView amendmentView = deserializeAmendment(rs.getString("amendment_json"));
                DetailedEmployeeView travelerView = deserializeTraveler(rs.getString("traveler_json"));
                return new TravelAppEditDto(travelerView, amendmentView, rs.getInt("traveler_dept_head_emp_id"));
            } catch (IOException e) {
                logger.error("Error retrieving data from the unsubmitted_app table.", e);
                return null;
            }
        }
    }
}
