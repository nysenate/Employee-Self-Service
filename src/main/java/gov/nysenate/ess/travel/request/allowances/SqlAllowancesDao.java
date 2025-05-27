package gov.nysenate.ess.travel.request.allowances;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SqlAllowancesDao extends SqlBaseDao {

    /**
     * Save the Allowances to the database.
     * Attempts to update the row if it exists, otherwise inserts a new row and sets allowance.allowance_id.
     */
    public void saveAllowances(Allowances allowances, int appId) {
        for (Allowance allowance : allowances.typeToAllowance.values()) {
            saveAllowance(allowance, appId);
        }
    }

    private void saveAllowance(Allowance allowance, int appId) {
        if (updateAllowance(allowance, appId) == 0) {
            insertAllowance(allowance, appId);
        }
    }

    private int updateAllowance(Allowance allowance, int appId) {
        MapSqlParameterSource params = allowanceParams(allowance, appId);
        String sql = SqlAllowancesQuery.UPDATE_ALLOWANCE.getSql(schemaMap());
        return localNamedJdbc.update(sql, params);
    }

    private void insertAllowance(Allowance allowance, int appId) {
        MapSqlParameterSource params = allowanceParams(allowance, appId);
        String sql = SqlAllowancesQuery.INSERT_ALLOWANCE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        allowance.allowanceId = (Integer) keyHolder.getKeys().get("allowance_id");
    }

    private MapSqlParameterSource allowanceParams(Allowance allowance, int appId) {
        return new MapSqlParameterSource()
                .addValue("allowance_id", allowance.allowanceId)
                .addValue("type", allowance.type.name())
                .addValue("value", allowance.dollars.toString())
                .addValue("appId", appId);
    }

    /**
     * Fetches the allowances associated with the given appId.
     * The returned Allowances object is empty if none were found.
     */
    public Allowances selectAllowances(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlAllowancesQuery.SELECT_ALLOWANCES.getSql(schemaMap());
        AllowanceHandler handler = new AllowanceHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getAllowances();
    }

    private enum SqlAllowancesQuery implements BasicSqlQuery {
        UPDATE_ALLOWANCE("""
                UPDATE ${travelSchema}.app_allowance
                SET value = :value
                WHERE app_id = :appId
                  AND type = :type
                """
        ),
        INSERT_ALLOWANCE("""
                INSERT INTO ${travelSchema}.app_allowance(type, value, app_id)
                VALUES(:type, :value, :appId)
                """
        ),
        SELECT_ALLOWANCES("""
                SELECT *
                FROM ${travelSchema}.app_allowance
                WHERE app_id = :appId
                """
        ),
        ;

        private final String sql;

        SqlAllowancesQuery(String sql) {
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

    private static class AllowanceHandler extends BaseHandler {

        private Allowances allowances;

        AllowanceHandler() {
            this.allowances = new Allowances();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int allowanceId = rs.getInt("allowance_id");
            AllowanceType type = AllowanceType.valueOf(rs.getString("type"));
            Dollars allowance = new Dollars(rs.getString("value"));
            allowances.typeToAllowance.put(type, new Allowance(allowanceId, type, allowance));
        }

        Allowances getAllowances() {
            return allowances;
        }
    }
}
