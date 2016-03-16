package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlOrderDao extends SqlBaseDao implements OrderDao {

    @Override
    public int insertOrder(OrderVersion version, LocalDateTime modifyDateTime) {
        long start = System.nanoTime();
        int versionId = insertOrderVersion(version);
        insertVersionLineItems(version, versionId);
        int orderId = insertNewOrder(versionId);
        insertOrderHistory(orderId, versionId, modifyDateTime);
        System.out.println(System.nanoTime() - start);
        return orderId;
    }

    private int insertOrderVersion(OrderVersion version) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", version.getCustomer().getEmployeeId())
                .addValue("destination", generateLocationId(version.getDestination()))
                .addValue("status", version.getStatus().toString())
                .addValue("note", version.getNote().orElse(null))
                .addValue("modifiedById", version.getModifiedBy().getEmployeeId());
        String sql = SqlOrderQuery.INSERT_ORDER_VERSION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("version_id");
    }

    private void insertVersionLineItems(OrderVersion version, int versionId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LineItem lineItem: version.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("versionId", versionId)
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity());
            paramList.add(params);
        }
        String sql = SqlOrderQuery.INSERT_LINE_ITEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private int insertNewOrder(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("activeVersion", versionId);
        String sql = SqlOrderQuery.INSERT_ORDER.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("order_id");
    }

    private void insertOrderHistory(int orderId, int versionId, LocalDateTime modifyDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderId", orderId)
                .addValue("versionId", versionId)
                .addValue("createdDateTime", toDate(modifyDateTime));
        String sql = SqlOrderQuery.INSERT_ORDER_HISTORY.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public Order getOrderById(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource("orderId", orderId);
        String sql = SqlOrderQuery.GET_ORDER_HISTORY.getSql(schemaMap());
        OrderHistoryRowMapper handler = new OrderHistoryRowMapper(this);
//        localNamedJdbc.query(sql, params, );

        return null;
    }

    @Override
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses, Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public Set<Order> getOrderHistory(int orderId) {
        return null;
    }

    private String generateLocationId(Location destination) {
        return destination.getCode() + "-" + destination.getType().getCode();
    }

    public OrderVersion getOrderVersion(int versionId) {
        return null;
    }
}
