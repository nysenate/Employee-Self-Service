package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class SqlOrderVersionDao extends SqlBaseDao implements OrderVersionDao {

    @Override
    public int insertOrderVersion(OrderVersion version) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", version.getCustomer().getEmployeeId())
                .addValue("destination", generateLocationId(version.getDestination()))
                .addValue("status", version.getStatus().toString())
                .addValue("note", version.getNote().orElse(null))
                .addValue("modifiedById", version.getModifiedBy().getEmployeeId());
        String sql = SqlOrderVersionQuery.INSERT_ORDER_VERSION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("version_id");
    }

    private enum SqlOrderVersionQuery implements BasicSqlQuery {

        INSERT_ORDER_VERSION(
                "INSERT INTO ${supplySchema}.order_version(customer_id, destination, status, note, modified_by) \n" +
                "VALUES (:customerId, :destination, :status::${supplySchema}.order_status, :note, :modifiedById)"
        );

        SqlOrderVersionQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

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
