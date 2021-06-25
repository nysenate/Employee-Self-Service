package gov.nysenate.ess.travel.application.unsubmitted;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.application.TravelApplicationView;
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
 *
 * {@code userId} is the employee id of the logged in user.
 */
@Repository
public class UnsubmittedAppDao extends SqlBaseDao {

    /**
     * Find an unsubmitted travel application for userId.
     *
     * @param userId     The employee id of the logged in user.
     * @return An optional containing a {@link TravelApplicationView} if a record was found.
     * Or an empty optional if no record was found.
     * @throws IOException
     */
    public Optional<TravelApplicationView> find(int userId) throws IOException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);
        String sql = SqlUnsubmittedAppQuery.FIND.getSql(schemaMap());
        try {
            String appJson = "";
            List<String> jsons = localNamedJdbc.query(sql, params, new UnsubmittedAppRowMapper());
            if (jsons.isEmpty() || jsons == null) {
                return Optional.empty();
            }
            else {
                appJson = jsons.get(0);
                return Optional.of(deserializeAppView(appJson));
            }
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * Saves a {@link TravelApplicationView} into the unsubmitted_app table.
     * Attempts to update the current record if one exists, otherwise inserts a new record.
     * @param userId
     * @param view
     */
    public void save(int userId, TravelApplicationView view) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("appJson", serializeAppView(view));
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
     * @param userId
     */
    public void delete(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);
        String sql = SqlUnsubmittedAppQuery.DELETE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private String serializeAppView(TravelApplicationView appView) {
        return OutputUtils.toJson(appView);
    }

    private TravelApplicationView deserializeAppView(String json) throws IOException {
        return OutputUtils.jsonToObject(json, TravelApplicationView.class);
    }

    private enum SqlUnsubmittedAppQuery implements BasicSqlQuery {
        FIND(
                "SELECT user_id, app_json\n" +
                        "FROM ${travelSchema}.unsubmitted_app\n" +
                        "WHERE user_id = :userId;"
        ),
        UPDATE(
                "UPDATE ${travelSchema}.unsubmitted_app\n" +
                        "SET app_json = :appJson\n" +
                        "WHERE user_id = :userId"
        ),
        INSERT(
                "INSERT INTO ${travelSchema}.unsubmitted_app(user_id, app_json)\n" +
                        "VALUES(:userId, :appJson)"
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

    private class UnsubmittedAppRowMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getString("app_json");
        }
    }
}
