package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SqlPaginatedOrderHandler extends BaseHandler {

    private final EmployeeInfoService employeeInfoService;
    private final LocationService locationService;
    private final SupplyItemService itemService;
    private final LimitOffset limitOffset;
    private int totalCount = 0;
    /** Stores orders while entire result set is processed. */
    private Map<Integer, Order> orderMap = new HashMap<>();

    public SqlPaginatedOrderHandler(EmployeeInfoService employeeInfoService, LocationService locationService,
                           SupplyItemService itemService, LimitOffset limitOffset) {
        this.employeeInfoService = employeeInfoService;
        this.locationService = locationService;
        this.itemService = itemService;
        this.limitOffset = limitOffset;
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        if (totalCount == 0) {
            totalCount = rs.getInt("total_rows");
        }
        int orderId = rs.getInt("order_id");
        Order order = orderMap.containsKey(orderId) ? orderMap.get(orderId) : getOrderFromResultSet(rs);
        LineItem lineItem = getLineItemFromResultSet(rs);
        order = order.addLineItem(lineItem);
        orderMap.put(order.getId(), order); // Must put back in map since Order is immutable.
    }

    private LineItem getLineItemFromResultSet(ResultSet rs) throws SQLException {
        SupplyItem item = itemService.getItemById(rs.getInt("item_id"));
        return new LineItem(item, rs.getInt("quantity"));
    }

    private Order getOrderFromResultSet(ResultSet rs) throws SQLException {
        Order.Builder builder = new Order.Builder(employeeInfoService.getEmployee(rs.getInt("customer_id")),
                                                  getLocalDateTimeFromRs(rs, "order_date_time"),
                                                  locationService.getLocation(rs.getString("location_code"), LocationType.valueOfCode(
                                                          (rs.getString("location_type").charAt(0)))),
                                                  OrderStatus.valueOf(rs.getString("status")),
                                                  rs.getInt("modified_emp_id"),
                                                  getLocalDateTimeFromRs(rs, "modified_date_time"));
        builder.id(rs.getInt("order_id"));
        if (rs.getInt("issue_emp_id") != 0) {
            builder.issuingEmployee(employeeInfoService.getEmployee(rs.getInt("issue_emp_id")));
        }
        builder.processedDateTime(getLocalDateTimeFromRs(rs, "process_date_time"));
        builder.completedDateTime(getLocalDateTimeFromRs(rs, "complete_date_time"));
        return builder.build();
    }

    public PaginatedList<Order> getOrders() {
        return new PaginatedList<>(totalCount, limitOffset, new ArrayList<>(orderMap.values()));
    }
}
