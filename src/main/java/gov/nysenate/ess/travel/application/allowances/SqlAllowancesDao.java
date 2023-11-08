package gov.nysenate.ess.travel.application.allowances;

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
import java.util.List;

@Repository
public class SqlAllowancesDao extends SqlBaseDao {

    public void saveAllowances(Allowances allowances, int amendmentId) {
        for (Allowance allowance : allowances.typeToAllowance.values()) {
            insertAllowance(allowance);
            insertIntoJoinTable(allowance, amendmentId);
        }
    }

    // Insert an allowance and set its id to the auto generated id.
    private void insertAllowance(Allowance allowance) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("type", allowance.type.name())
                .addValue("value", allowance.dollars.toString());
        String sql = SqlAllowancesQuery.INSERT_ALLOWANCE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        allowance.allowanceId = (Integer) keyHolder.getKeys().get("allowance_id");
    }

    private void insertIntoJoinTable(Allowance allowance, int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("allowanceId", allowance.allowanceId);
        String sql = SqlAllowancesQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * Get Allowances by an amendment Id.
     *
     * @param amendmentId
     * @return Allowances for the provided {@code amendmentId}. Allowances will be empty
     * if {@code amendmentId} does not exist.
     */
    public Allowances selectAllowances(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("amendmentId", amendmentId);
        String sql = SqlAllowancesQuery.SELECT_ALLOWANCES_FOR_AMENDMENT.getSql(schemaMap());
        List<Integer> allowanceIds = localNamedJdbc.queryForList(sql, params, Integer.class);

        if (allowanceIds.isEmpty()) {
            return new Allowances();
        }

        MapSqlParameterSource allowanceParams = new MapSqlParameterSource("allowanceIds", allowanceIds);
        String allowanceSql = SqlAllowancesQuery.SELECT_ALLOWANCE.getSql(schemaMap());
        AllowanceHandler handler = new AllowanceHandler();
        localNamedJdbc.query(allowanceSql, allowanceParams, handler);

        return handler.getAllowances();
    }

    private enum SqlAllowancesQuery implements BasicSqlQuery {
        INSERT_ALLOWANCE(
                "INSERT INTO ${travelSchema}.allowance(type, value)\n" +
                        " VALUES(:type, :value)"
        ),
        INSERT_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.amendment_allowances(amendment_id, allowance_id)\n" +
                        " VALUES(:amendmentId, :allowanceId)"
        ),
        SELECT_ALLOWANCES_FOR_AMENDMENT(
                "SELECT allowance_id\n" +
                        " FROM ${travelSchema}.amendment_allowances\n" +
                        " WHERE amendment_id = :amendmentId"
        ),
        SELECT_ALLOWANCE(
                "SELECT allowance_id, type, value\n" +
                        " FROM ${travelSchema}.allowance\n" +
                        " WHERE allowance_id IN (:allowanceIds)"
        );

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
