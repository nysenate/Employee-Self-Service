package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.PaginatedRowHandler;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SqlOrderDao extends SqlBaseDao implements OrderDao {

    @Autowired private SqlOrderHistoryDao orderHistoryDao;
    @Autowired private SqlOrderVersionDao orderVersionDao;
    @Autowired private SqlLineItemDao lineItemDao;

    @Override
    public int insertOrder(OrderVersion version, LocalDateTime modifyDateTime) {
        int versionId = orderVersionDao.insertOrderVersion(version);
        lineItemDao.insertVersionLineItems(version, versionId);
        int orderId = insertNewOrder(versionId);
        orderHistoryDao.insertOrderHistory(orderId, versionId, modifyDateTime);
        return orderId;
    }

    private int insertNewOrder(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("activeVersion", versionId);
        String sql = SqlOrderQuery.INSERT_ORDER.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("order_id");
    }

    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public Order getOrderById(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource("orderId", orderId);
        OrderHistory history = orderHistoryDao.getOrderHistory(orderId);
        return Order.of(orderId, history);
    }

    @Override
    public PaginatedList<Order> getOrders(String location, String customerId, EnumSet<OrderStatus> statuses,
                                          Range<LocalDateTime> updatedDateTime, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("location", formatSearchString(location))
                .addValue("customerId", formatSearchString(customerId))
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("startDate", toDate(DateUtils.startOfDateTimeRange(updatedDateTime)))
                .addValue("endDate", toDate(DateUtils.endOfDateTimeRange(updatedDateTime)));
        String sql = SqlOrderQuery.ORDER_SEARCH.getSql(schemaMap(), limOff);
        PaginatedRowHandler<Order> paginatedHandler = new PaginatedRowHandler<>(limOff, "total_rows", new OrderRowMapper(orderHistoryDao));
        localNamedJdbc.query(sql, params, paginatedHandler);
        return paginatedHandler.getList();
    }

    private String formatSearchString(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }

    /** Convert an EnumSet into a Set containing each enum's name. */
    private Set<String> extractEnumSetParams(EnumSet<OrderStatus> statuses) {
        return statuses.stream().map(Enum::name).collect(Collectors.toSet());
    }

    private class OrderRowMapper extends BaseRowMapper<Order> {

        private SqlOrderHistoryDao orderHistoryDao;

        public OrderRowMapper(SqlOrderHistoryDao orderHistoryDao) {
            this.orderHistoryDao = orderHistoryDao;
        }

        @Override
        public Order mapRow(ResultSet rs, int i) throws SQLException {
            int orderId = rs.getInt("order_id");
            OrderHistory history = orderHistoryDao.getOrderHistory(orderId);
            return Order.of(orderId, history);
        }
    }
}
