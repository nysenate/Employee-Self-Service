package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.handler.SqlLineItemHandler;
import gov.nysenate.ess.supply.order.dao.handler.SqlOrderHandler;
import gov.nysenate.ess.supply.order.dao.handler.SqlPaginatedOrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlOrderDao extends SqlBaseDao implements OrderDao {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private LocationService locationService;
    @Autowired private SupplyItemService itemService;

    @Override
    public Order insertOrder(Order order) {
        MapSqlParameterSource params = getOrderParams(order);
        String sql = SqlOrderDaoQuery.INSERT_ORDER.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        order = order.setId((Integer) keyHolder.getKeys().get("order_id"));
        saveLineItems(order);
        return order;
    }

    private MapSqlParameterSource getOrderParams(Order order) {
        return new MapSqlParameterSource()
                .addValue("orderId", order.getId())
                .addValue("status", order.getStatus().toString())
                .addValue("customerId", order.getCustomer().getEmployeeId())
                .addValue("locCode", order.getLocationCode())
                .addValue("locType", order.getLocationType())
                .addValue("issueEmpId", order.getIssuingEmployee().isPresent() ? order.getIssuingEmployee().get().getEmployeeId() : null)
                .addValue("approveEmpId", order.getApprovedEmpId() == 0 ? null : order.getApprovedEmpId()) // don't insert default value of 0
                .addValue("orderDateTime", toDate(order.getOrderDateTime()))
                .addValue("processDateTime", toDate(order.getProcessedDateTime().orElse(null)))
                .addValue("completeDateTime", toDate(order.getCompletedDateTime().orElse(null)))
                .addValue("modifiedDateTime", toDate(order.getModifiedDateTime()))
                .addValue("modifiedEmpId", order.getModifiedEmpId());
    }

    /**
     * Saves an order's line items.
     * Will insert new line items and update existing line items.
     */
    private void saveLineItems(Order order) {
        String updateSql = SqlOrderDaoQuery.UPDATE_LINE_ITEMS.getSql(schemaMap());
        String insertSql = SqlOrderDaoQuery.INSERT_LINE_ITEMS.getSql(schemaMap());
        for (LineItem lineItem: order.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("orderId", order.getId())
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity())
                    .addValue("modifiedDateTime", toDate(order.getModifiedDateTime()))
                    .addValue("modifiedEmpId", order.getModifiedEmpId());
            if (localNamedJdbc.update(updateSql, params) == 0) {
                localNamedJdbc.update(insertSql, params);
            }
        }
    }

    @Override
    public void saveOrder(Order order) {
        MapSqlParameterSource params = getOrderParams(order);
        String sql = SqlOrderDaoQuery.UPDATE_ORDER.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
        saveLineItems(order);
    }

    @Override
    public Order getOrderById(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId);
        String sql = SqlOrderDaoQuery.GET_ORDER_BY_ID.getSql(schemaMap());
        SqlOrderHandler handler = new SqlOrderHandler(employeeInfoService, locationService, itemService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getOrders().get(0);
    }

    @Override
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                          Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locCode", formatSearchString(locCode))
                .addValue("locType", formatSearchString(locType))
                .addValue("issueEmpId", formatSearchString(issuerEmpId))
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)))
                .addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        String sql = SqlOrderDaoQuery.SEARCH_ORDERS.getSql(schemaMap(), limOff);
        SqlPaginatedOrderHandler handler = new SqlPaginatedOrderHandler(employeeInfoService, locationService, limOff);
        localNamedJdbc.query(sql, params, handler);
        return populateOrderLineItems(handler.getOrders());
    }

    /**
     * Perform additional queries to the the line items for each order.
     * Cannot get full order in one query since the one-to-many relationship between order and line items
     * returns incomplete results when querying with a limit offset.
     * @param paginatedOrders A PaginatedList of orders which are missing line item information.
     * @return A PaginatedList of orders fully populated with line item information.
     */
    private PaginatedList<Order> populateOrderLineItems(PaginatedList<Order> paginatedOrders) {
        List<Order> emptyOrders = paginatedOrders.getResults();
        List<Order> fullOrders = emptyOrders.stream().map(order -> order.setLineItems(getOrderItems(order)))
                                                     .collect(Collectors.toList());
        return new PaginatedList<>(paginatedOrders.getTotal(), paginatedOrders.getLimOff(), fullOrders);
    }

    /**
     * Get the set of line items belonging to an order.
     */
    private Set<LineItem> getOrderItems(Order order) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orderId", order.getId());
        String sql = SqlOrderDaoQuery.GET_ORDER_ITEMS.getSql(schemaMap());
        SqlLineItemHandler handler = new SqlLineItemHandler(itemService);
        localNamedJdbc.query(sql, params, handler);
        return new HashSet<>(handler.getLineItems());
    }

    @Override
    public void undoCompletion(Order order) {

    }

    private String formatSearchString(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }

    private Set<String> extractEnumSetParams(EnumSet<OrderStatus> statuses) {
        return statuses.stream().map(Enum::name).collect(Collectors.toSet());
    }

}
