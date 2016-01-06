package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryOrder implements OrderDao {

    private Map<Integer, Order> orderDB = new TreeMap<>();

    public InMemoryOrder() {
        reset();
    }

    public void reset() {
        orderDB = new TreeMap<>();
    }

    @Override
    public int getUniqueId() {
        return orderDB.size() + 1;
    }

    @Override
    public void saveOrder(Order order) {
        orderDB.put(order.getId(), order);
    }

    /**
     * Not fully functional
     */
    @Override
    public List<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                    Range<LocalDate> dateRange, LimitOffset limOff) {
        List<Order> filteredOrders = orderDB.values().stream().filter(order -> statuses.contains(order.getStatus())).collect(Collectors.toList());
        for (Order order : filteredOrders) {
            if (!locCode.equals("all")) {
                if (!order.getLocation().getCode().equals(locCode)) {
                    filteredOrders.remove(order);
                    break;
                }
            }
            if (!locType.equals("all")) {
                if (!String.valueOf(order.getLocation().getType().getCode()).equals(locType)) {
                    filteredOrders.remove(order);
                    break;
                }
            }
            // TODO: only search by issuer if status != pending/rejected
            if (!issuerEmpId.equals("all")) {
                // TODO: should verify issuerEmpId can be converted to int.
                if (order.getIssuingEmployee().getEmployeeId() != Integer.valueOf(issuerEmpId)) {
                    filteredOrders.remove(order);
                    break;
                }
            }
        }
        return filteredOrders;
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderDB.get(orderId);
    }

    @Override
    public void undoCompletion(Order order) {

    }
}
