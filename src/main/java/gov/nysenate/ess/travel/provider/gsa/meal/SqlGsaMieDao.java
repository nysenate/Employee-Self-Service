package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class SqlGsaMieDao extends SqlBaseDao {

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
}
