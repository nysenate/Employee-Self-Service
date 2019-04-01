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

    public void saveAllowances(Allowances allowances, int appVersionId, int previousAppVersionId) {
        Allowances previousAllowances = selectAllowances(previousAppVersionId);
        if (previousAllowances.equals(allowances) && previousAppVersionId != 0) {
            // If Allowances did not change, just update the join table.
            for (Allowance allowance : allowances.typeToAllowance.values()) {
                joinAllowanceWithAppVersion(allowance, appVersionId);
            }
        } else {
            for (Allowance allowance : allowances.typeToAllowance.values()) {
                insertAllowance(allowance);
                joinAllowanceWithAppVersion(allowance, appVersionId);
            }
        }
    }

    private void insertAllowance(Allowance allowance) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("allowanceType", allowance.type.name())
                .addValue("allowance", allowance.dollars.toString());
        String sql = SqlAllowancesQuery.INSERT_ALLOWANCE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        allowance.allowanceId = (Integer) keyHolder.getKeys().get("allowance_id");
    }

    private void joinAllowanceWithAppVersion(Allowance allowance, int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appVersionId", appVersionId)
                .addValue("allowanceId", allowance.allowanceId);
        String sql = SqlAllowancesQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    public Allowances selectAllowances(int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appVersionId", appVersionId);
        String sql = SqlAllowancesQuery.SELECT_ALLOWANCES_FOR_VERSION.getSql(schemaMap());
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
                "INSERT INTO ${travelSchema}.app_allowance(allowance_type, allowance)\n" +
                        " VALUES(:allowanceType, :allowance)"
        ),
        INSERT_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.app_version_allowance(app_version_id, allowance_id)\n" +
                        " VALUES(:appVersionId, :allowanceId)"
        ),
        SELECT_ALLOWANCES_FOR_VERSION(
                "SELECT allowance_id\n" +
                        " FROM ${travelSchema}.app_version_allowance\n" +
                        " WHERE app_version_id = :appVersionId"
        ),
        SELECT_ALLOWANCE(
                "SELECT allowance_id, allowance_type, allowance\n" +
                        " FROM ${travelSchema}.app_allowance\n" +
                        " WHERE allowance_id IN (:allowanceIds)"
        );

        private String sql;

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

    private class AllowanceHandler extends BaseHandler {

        private Allowances allowances;

        AllowanceHandler() {
            this.allowances = new Allowances();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int allowanceId = rs.getInt("allowance_id");
            AllowanceType type = AllowanceType.valueOf(rs.getString("allowance_type"));
            Dollars allowance = new Dollars(rs.getString("allowance"));
            allowances.typeToAllowance.put(type, new Allowance(allowanceId, type, allowance));
        }

        Allowances getAllowances() {
            return allowances;
        }
    }
}
