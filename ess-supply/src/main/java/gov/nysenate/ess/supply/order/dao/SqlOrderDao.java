package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
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

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

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
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses, Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public Set<Order> getOrderHistory(int orderId) {
        return null;
    }

    public OrderVersion getOrderVersion(int versionId) {
        return null;
    }
}
