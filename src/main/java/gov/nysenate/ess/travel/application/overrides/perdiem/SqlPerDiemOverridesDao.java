package gov.nysenate.ess.travel.application.overrides.perdiem;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class SqlPerDiemOverridesDao extends SqlBaseDao {

    /**
     * Saves the given {@link PerDiemOverrides} linking them to the given appVersionId.
     * // TODO improve efficiency. Maybe only insert if overrides have changes, otherwise just update join table similar to SqlAllowancesDao?
     * @param overrides
     * @param appVersionId
     */
    public void savePerDiemOverrides(PerDiemOverrides overrides, int appVersionId) {
        for (PerDiemOverride override : overrides.typeToOverride.values()) {
            int overrideId = insertPerDiemOverride(override);
            override.perDiemOverrideId = overrideId;
            insertIntoJoinTable(override, appVersionId);
        }
    }

    private int insertPerDiemOverride(PerDiemOverride override) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("perdiemType", override.type.name())
                .addValue("dollars", override.dollars.toString());

        String sql = SqlPerDiemOverridesQuery.INSERT_PERDIEM_OVERRIDE.getSql(schemaMap());
        KeyHolder keyholder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyholder);
        return (Integer) keyholder.getKeys().get("app_perdiem_override_id");
    }

    private void insertIntoJoinTable(PerDiemOverride override, int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appPerDiemOverrideId", override.perDiemOverrideId)
                .addValue("appVersionId", appVersionId);

        String sql = SqlPerDiemOverridesQuery.INSERT_INTO_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * @return The {@link PerDiemOverrides} for a given app version id.
     */
    public PerDiemOverrides selectPerDiemOverrides(int appVersionId) {
        List<Integer> perDiemIds = selectPerDiemOverrideIds(appVersionId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("perDiemIds", perDiemIds);
        String sql = SqlPerDiemOverridesQuery.SELECT_PERDIEM_OVERRIDE.getSql(schemaMap());
        PerDiemOverrideHandler handler = new PerDiemOverrideHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getOverrides();
    }

    private List<Integer> selectPerDiemOverrideIds(int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appVersionId", appVersionId);
        String sql = SqlPerDiemOverridesQuery.SELECT_PERDIEM_OVERRIDE_IDS.getSql(schemaMap());
        return localNamedJdbc.queryForList(sql, params, Integer.class);
    }

    private enum SqlPerDiemOverridesQuery implements BasicSqlQuery {
        INSERT_PERDIEM_OVERRIDE(
                "INSERT INTO ${travelSchema}.app_perdiem_override(perdiem_type, dollars)\n" +
                        "  VALUES(:perdiemType, :dollars)"
        ),
        INSERT_INTO_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.app_version_perdiem_override" +
                        "  (app_version_id, app_perdiem_override_id) \n" +
                        "  VALUES(:appVersionId, :appPerDiemOverrideId)"
        ),
        SELECT_PERDIEM_OVERRIDE_IDS(
                "SELECT app_perdiem_override_id FROM ${travelSchema}.app_version_perdiem_override\n" +
                        "  WHERE app_version_id = :appVersionId"
        ),
        SELECT_PERDIEM_OVERRIDE(
                "SELECT app_perdiem_override_id, perdiem_type, dollars\n" +
                        "  FROM ${travelSchema}.app_perdiem_override\n" +
                        "  WHERE app_perdiem_override_id IN (:perDiemIds)"
        );

        private String sql;

        SqlPerDiemOverridesQuery(String sql) {
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

    private class PerDiemOverrideHandler extends BaseHandler {

        private PerDiemOverrides overrides = new PerDiemOverrides();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int appPerDiemOverrideId = rs.getInt("app_perdiem_override_id");
            PerDiemType perDiemType = PerDiemType.valueOf(rs.getString("perdiem_type"));
            Dollars dollars = new Dollars(rs.getString("dollars"));
            overrides.typeToOverride.put(perDiemType, new PerDiemOverride(appPerDiemOverrideId, perDiemType, dollars));
        }

        PerDiemOverrides getOverrides() {
            return overrides;
        }
    }
}
