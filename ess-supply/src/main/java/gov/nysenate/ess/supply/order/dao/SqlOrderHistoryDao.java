package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository
public class SqlOrderHistoryDao extends SqlBaseDao implements OrderHistoryDao {

    @Autowired private SqlOrderVersionDao orderVersionDao;

    @Override
    public void insertOrderHistory(int orderId, int versionId, LocalDateTime modifyDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderId", orderId)
                .addValue("versionId", versionId)
                .addValue("createdDateTime", toDate(modifyDateTime));
        String sql = SqlOrderHistoryQuery.INSERT_ORDER_HISTORY.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    @Override
    public OrderHistory getOrderHistory(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource("orderId", orderId);
        String sql = SqlOrderHistoryQuery.GET_ORDER_HISTORY.getSql(schemaMap());
        OrderHistoryHandler handler = new OrderHistoryHandler(orderVersionDao);
        localNamedJdbc.query(sql, params, handler);
        return handler.getOrderHistory();
    }

    private enum SqlOrderHistoryQuery implements BasicSqlQuery {

        INSERT_ORDER_HISTORY(
                "INSERT INTO ${supplySchema}.order_history(order_id, version_id, created_date_time) \n" +
                "VALUES (:orderId, :versionId, :createdDateTime)"
        ),
        GET_ORDER_HISTORY(
                "SELECT version_id, created_date_time \n" +
                "FROM ${supplySchema}.order_history \n" +
                "WHERE order_id = :orderId"
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

    private class OrderHistoryHandler extends BaseHandler {

        private SqlOrderVersionDao versionDao;
        private SortedMap<LocalDateTime, OrderVersion> versionMap;

        public OrderHistoryHandler(SqlOrderVersionDao versionDao) {
            this.versionDao = versionDao;
            this.versionMap = new TreeMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int versionId = rs.getInt("version_id");
            LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "created_date_time");
            OrderVersion version = versionDao.getOrderVersion(versionId);
            versionMap.put(dateTime, version);
        }

        public OrderHistory getOrderHistory() {
            return OrderHistory.of(versionMap);
        }
    }
}
