package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EssOrderService implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public synchronized int submitOrder(Employee customer, Location location, Map<Integer, Integer> items) {
        int id = orderDao.getUniqueId();
        Order order = new Order(id, customer, LocalDateTime.now(), location);
        order.setItems(items);
        orderDao.saveOrder(order);
        return id;
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public void updateOrderItems(int orderId, Map<Integer, Integer> newItems) {
        Order order = orderDao.getOrderById(orderId);
        order.setItems(newItems);
        orderDao.saveOrder(order);
    }

    @Override
    public void rejectOrder(int orderId) {
        Order order = orderDao.getOrderById(orderId);
        order.setStatus(OrderStatus.REJECTED);
        orderDao.saveOrder(order);
    }

    @Override
    public void processOrder(int orderId, Employee issuingEmployee) {
        Order order = orderDao.getOrderById(orderId);
        order.setIssuingEmployee(issuingEmployee);
        order.setStatus(OrderStatus.PROCESSING);
        order.setProcessedDateTime(LocalDateTime.now());
        orderDao.saveOrder(order);
    }

    @Override
    public List<Order> getOrders() {
        return orderDao.getOrders();
    }

    @Override
    public List<Order> getPendingOrders() {
        return getOrdersByStatus(OrderStatus.PENDING);
    }

    @Override
    public List<Order> getProcessingOrders() {
        return getOrdersByStatus(OrderStatus.PROCESSING);
    }

    private List<Order> getOrdersByStatus(OrderStatus status) {
        return this.getOrders().stream().filter(order -> order.getStatus() == status).collect(Collectors.toList());
    }
}
