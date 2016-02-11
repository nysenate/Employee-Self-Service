package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Order Dao implementation for testing.
 * Pagination functionality is not implemented.
 * Should test pagination directly through real dao implementations.
 */
@Primary
@Profile("test")
@Repository
public class InMemoryOrderDao implements OrderDao {

    private Map<Integer, Order> orderDB = new TreeMap<>();

    public InMemoryOrderDao() {
        reset();
    }

    public void reset() {
        orderDB = new TreeMap<>();
    }

    private int getUniqueId() {
        return orderDB.size() + 1;
    }

    @Override
    public Order insertOrder(Order order) {
        order = order.setId(getUniqueId());
        saveOrder(order);
        return order;
    }

    @Override
    public void saveOrder(Order order) {
        orderDB.put(order.getId(), order);
    }

    /**
     * Not fully functional
     */
    @Override
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                    Range<LocalDate> dateRange, LimitOffset limOff) {
        List<Order> filteredOrders = new ArrayList<>();
        List<Order> allOrders = orderDB.values().stream().filter(order -> statuses.contains(order.getStatus())).collect(Collectors.toList());
        for (Order order : allOrders) {
            if (matchesLocCode(locCode, order) && matchesLocType(locType, order) && matchesIssuer(issuerEmpId, order)) {
                filteredOrders.add(order);
            }
        }
        return new PaginatedList<>(filteredOrders.size(), limOff, filteredOrders);
    }

    private boolean matchesLocCode(String locCode, Order order) {
        return locCode.equals("all") || order.getLocation().getCode().equals(locCode);
    }

    private boolean matchesLocType(String locType, Order order) {
        return locType.equals("all") || String.valueOf(order.getLocation().getType().getCode()).equals(locType);
    }

    private boolean matchesIssuer(String issuerEmpId, Order order) {
        // TODO: only search by issuer if status != pending/rejected
        // TODO: should verify issuerEmpId can be converted to int.
        return issuerEmpId.equals("all") || order.getIssuingEmployee().map(Employee::getEmployeeId).orElse(null) == Integer.valueOf(issuerEmpId);
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderDB.get(orderId);
    }

    @Override
    public void undoCompletion(Order order) {

    }
}
