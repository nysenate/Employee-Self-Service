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
     * Saves the given {@link PerDiemOverrides} and links them to the given {@code amendmentId}.
     *
     * @param overrides
     * @param amendmentId
     */
    public void savePerDiemOverrides(PerDiemOverrides overrides, int amendmentId) {
        for (PerDiemOverride override : overrides.typeToOverride.values()) {
            override.perDiemOverrideId = insertPerDiemOverride(override);
            insertIntoJoinTable(override, amendmentId);
        }
    }

    private int insertPerDiemOverride(PerDiemOverride override) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("type", override.type.name())
                .addValue("value", override.dollars.toString());

        String sql = SqlPerDiemOverridesQuery.INSERT_PERDIEM_OVERRIDE.getSql(schemaMap());
        KeyHolder keyholder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyholder);
        return (Integer) keyholder.getKeys().get("perdiem_override_id");
    }

    private void insertIntoJoinTable(PerDiemOverride override, int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("perDiemOverrideId", override.perDiemOverrideId)
                .addValue("amendmentId", amendmentId);

        String sql = SqlPerDiemOverridesQuery.INSERT_INTO_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * @return The {@link PerDiemOverrides} for a given amendment id.
     * If {@code amendmentId} does not match any records, an empty {@link PerDiemOverrides} object is returned.
     */
    public PerDiemOverrides selectPerDiemOverrides(int amendmentId) {
        List<Integer> perDiemIds = selectPerDiemOverrideIds(amendmentId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("perDiemIds", perDiemIds);
        String sql = SqlPerDiemOverridesQuery.SELECT_PERDIEM_OVERRIDE.getSql(schemaMap());
        PerDiemOverrideHandler handler = new PerDiemOverrideHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getOverrides();
    }

    private List<Integer> selectPerDiemOverrideIds(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId);
        String sql = SqlPerDiemOverridesQuery.SELECT_PERDIEM_OVERRIDE_IDS.getSql(schemaMap());
        return localNamedJdbc.queryForList(sql, params, Integer.class);
    }

    private enum SqlPerDiemOverridesQuery implements BasicSqlQuery {
        INSERT_PERDIEM_OVERRIDE(
                "INSERT INTO ${travelSchema}.perdiem_override(type, value)\n" +
                        "  VALUES(:type, :value)"
        ),
        INSERT_INTO_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.amendment_perdiem_overrides" +
                        "  (amendment_id, perdiem_override_id) \n" +
                        "  VALUES(:amendmentId, :perDiemOverrideId)"
        ),
        SELECT_PERDIEM_OVERRIDE_IDS(
                "SELECT perdiem_override_id FROM ${travelSchema}.amendment_perdiem_overrides\n" +
                        "  WHERE amendment_id = :amendmentId"
        ),
        SELECT_PERDIEM_OVERRIDE(
                "SELECT perdiem_override_id, type, value\n" +
                        "  FROM ${travelSchema}.perdiem_override\n" +
                        "  WHERE perdiem_override_id IN (:perDiemIds)"
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
            int perDiemOverrideId = rs.getInt("perdiem_override_id");
            PerDiemType perDiemType = PerDiemType.valueOf(rs.getString("type"));
            Dollars dollars = new Dollars(rs.getString("value"));
            overrides.typeToOverride.put(perDiemType, new PerDiemOverride(perDiemOverrideId, perDiemType, dollars));
        }

        PerDiemOverrides getOverrides() {
            return overrides;
        }
    }
}
