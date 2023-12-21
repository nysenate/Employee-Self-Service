package gov.nysenate.ess.travel.allowedtravelers;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlUserOrderableRchDao extends SqlBaseDao {

    /**
     * @param empId
     * @return A set of all extra RCH's the given employee can place orders for.
     */
    public Set<String> forEmpId(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("empId", empId);
        String sql = SqlUserOrderableRchQuery.SELECT_USER_ORDERABLE_RCHS.getSql(schemaMap());
        List<String> rchs = localNamedJdbc.queryForList(sql, params, String.class);
        return new HashSet<>(rchs);
    }

    private enum SqlUserOrderableRchQuery implements BasicSqlQuery {
        SELECT_USER_ORDERABLE_RCHS(
                "SELECT rch FROM ${travelSchema}.user_orderable_rch\n" +
                        "WHERE emp_id = :empId"
        );

        private final String sql;

        SqlUserOrderableRchQuery(String sql) {
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
}
