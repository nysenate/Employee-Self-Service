package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryOrder implements OrderDao {

    private Map<Integer, Order> orders = new HashMap<>();

    @Override
    public int submitOrder(Employee customer, LocalDateTime orderDateTime, Location location, Map<String, Integer> items, OrderStatus status) {
        int id = orders.size() + 1;
        Order order = new Order(id, customer, orderDateTime, location, status);
        order.setItems(items);
        orders.put(id, order);
        return id;
    }

    @Override
    public Order getOrderById(int id) {
        return orders.get(id);
    }

}
