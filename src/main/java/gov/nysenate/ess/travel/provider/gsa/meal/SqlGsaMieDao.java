package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class SqlGsaMieDao extends SqlBaseDao {

    public GsaMie selectGsaMie(int gsaMieId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gsaMieId", gsaMieId);
        String sql = SqlGsaMieQuery.SELECT_GSA_MIE_BY_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new GsaMieRowMapper());
    }

    public GsaMie selectGsaMie(int fiscalYear, Dollars total) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fiscalYear", fiscalYear)
                .addValue("total", total.toString());
        String sql = SqlGsaMieQuery.SELECT_GSA_MIE.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new GsaMieRowMapper());
    }


    /**
     * Saves all given {@link GsaMie}, overwriting the existing data if the fiscal_year + total unique
     * index is violated. We overwrite here just in case these values can be updated mid year.
     * @param mies
     */
    public void saveGsaMies(Collection<GsaMie> mies) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (GsaMie mie : mies) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("fiscalYear", mie.getFiscalYear())
                    .addValue("total", mie.total().toString())
                    .addValue("gsaBreakfast", mie.getGsaBreakfast().toString())
                    .addValue("gsaLunch", mie.getGsaLunch().toString())
                    .addValue("gsaDinner", mie.getGsaDinner().toString())
                    .addValue("gsaIncidental", mie.getGsaIncidental().toString())
                    .addValue("gsaFirstLastDay", mie.getGsaFirstLastDay().toString());
            paramList.add(params);
        }
        String sql = SqlGsaMieQuery.INSERT_GSA_MIE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private enum SqlGsaMieQuery implements BasicSqlQuery {
        INSERT_GSA_MIE(
                "INSERT INTO ${travelSchema}.gsa_mie(fiscal_year, total, breakfast, lunch, dinner, incidental, first_last_day)\n" +
                        " VALUES (:fiscalYear, :total, :gsaBreakfast, :gsaLunch, :gsaDinner, :gsaIncidental, :gsaFirstLastDay)\n" +
                        " ON CONFLICT (fiscal_year, total) DO UPDATE\n" +
                        " SET breakfast = :gsaBreakfast, lunch = :gsaLunch, dinner = :gsaDinner, incidental = :gsaIncidental," +
                        " first_last_day = :gsaFirstLastDay"
        ),
        SELECT_GSA_MIE_BY_ID(
                "SELECT * FROM ${travelSchema}.gsa_mie\n" +
                        "WHERE gsa_mie_id = :gsaMieId"
        ),
        SELECT_GSA_MIE(
                "SELECT * FROM ${travelSchema}.gsa_mie\n" +
                        "WHERE fiscal_year = :fiscalYear AND total = :total"
        )
        ;

        private String sql;

        SqlGsaMieQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class GsaMieRowMapper implements RowMapper<GsaMie> {

        @Override
        public GsaMie mapRow(ResultSet rs, int i) throws SQLException {
            return new GsaMie(
                    rs.getInt("gsa_mie_id"),
                    rs.getInt("fiscal_year"),
                    new Dollars(rs.getString("total")),
                    new Dollars(rs.getString("breakfast")),
                    new Dollars(rs.getString("lunch")),
                    new Dollars(rs.getString("dinner")),
                    new Dollars(rs.getString("incidental")),
                    new Dollars(rs.getString("first_last_day"))
            );
        }
    }
}
