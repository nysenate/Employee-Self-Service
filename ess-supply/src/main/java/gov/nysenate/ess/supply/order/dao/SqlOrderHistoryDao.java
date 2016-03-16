package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SqlOrderHistoryDao extends SqlBaseDao implements OrderHistoryDao {

    @Override
    public void insertOrderHistory(int orderId, int versionId, LocalDateTime modifyDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderId", orderId)
                .addValue("versionId", versionId)
                .addValue("createdDateTime", toDate(modifyDateTime));
        String sql = SqlOrderHistoryQuery.INSERT_ORDER_HISTORY.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlOrderHistoryQuery implements BasicSqlQuery {

        INSERT_ORDER_HISTORY(
                "INSERT INTO ${supplySchema}.order_history(order_id, version_id, created_date_time) \n" +
                "VALUES (:orderId, :versionId, :createdDateTime)"
        );

        SqlOrderHistoryQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

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
