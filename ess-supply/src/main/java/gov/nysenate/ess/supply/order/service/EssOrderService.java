package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EssOrderService implements OrderService {

    @Qualifier("inMemoryOrder")
    @Autowired
    private OrderDao orderDao;

    @Qualifier("sfmsInMemoryOrder")
    @Autowired
    private OrderDao sfmsDao;

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
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new WrongOrderStatusException("Can only reject orders with status of " + OrderStatus.PENDING +
                                                ". Tried to reject order with status of " + order.getStatus().toString());
        }
        order.setStatus(OrderStatus.REJECTED);
        orderDao.saveOrder(order);
    }

    @Override
    public void processOrder(int orderId, Employee issuingEmployee) {
        Order order = orderDao.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new WrongOrderStatusException("Can only process orders with status of " + OrderStatus.PENDING +
                                                ". Tried to process order with status of " + order.getStatus().toString());
        }
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

    @Override
    public void completeOrder(int orderId) {
        Order order = orderDao.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new WrongOrderStatusException("Can only complete orders with status of " + OrderStatus.PROCESSING +
                                                ". Tried to complete order with status of " + order.getStatus().toString());
        }
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedDateTime(LocalDateTime.now());
        // TODO: both these saves should be in same transaction.
        orderDao.saveOrder(order);
        sfmsDao.saveOrder(order);
    }

    private List<Order> getOrdersByStatus(OrderStatus status) {
        return this.getOrders().stream().filter(order -> order.getStatus() == status).collect(Collectors.toList());
    }
}
