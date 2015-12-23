package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EssOrderService implements OrderService {

    @Qualifier("inMemoryOrder")
    @Autowired
    private OrderDao orderDao;

    @Qualifier("sfmsInMemoryOrder")
    @Autowired
    private OrderDao sfmsDao;

    @Autowired
    private EmployeeInfoService employeeInfoService;

    @Override
    public synchronized Order submitOrder(int empId, Set<LineItem> items) {
        Employee customer = employeeInfoService.getEmployee(empId);
        Location location = customer.getWorkLocation();
        Order order = new Order.Builder(orderDao.getUniqueId(), customer, LocalDateTime.now(), location, OrderStatus.PENDING).build();
        order = order.setItems(items);
        orderDao.saveOrder(order);
        return order;
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public void saveOrder(Order order) {
        orderDao.saveOrder(order);
    }

    @Override
    public Order rejectOrder(int orderId) {
        Order order = orderDao.getOrderById(orderId);
        if (!statusIsPendingOrProcessing(order)) {
            throw new WrongOrderStatusException("Can only reject orders with status of " + OrderStatus.PENDING +
                                                ". Tried to reject order with status of " + order.getStatus().toString());
        }
        order = order.setStatus(OrderStatus.REJECTED);
        orderDao.saveOrder(order);
        return order;
    }

    private boolean statusIsPendingOrProcessing(Order order) {
        return order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PROCESSING;
    }

    @Override
    public Order processOrder(int orderId, int issuingEmpId) {
        Employee issuingEmployee = employeeInfoService.getEmployee(issuingEmpId);
        Order order = orderDao.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new WrongOrderStatusException("Can only process orders with status of " + OrderStatus.PENDING +
                                                ". Tried to process order with status of " + order.getStatus().toString());
        }
        order = order.setIssuingEmployee(issuingEmployee);
        order = order.setStatus(OrderStatus.PROCESSING);
        order = order.setProcessedDateTime(LocalDateTime.now());
        orderDao.saveOrder(order);
        return order;
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
    public Order completeOrder(int orderId) {
        Order order = orderDao.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new WrongOrderStatusException("Can only complete orders with status of " + OrderStatus.PROCESSING +
                                                ". Tried to complete order with status of " + order.getStatus().toString());
        }
        order = order.setStatus(OrderStatus.COMPLETED);
        order = order.setCompletedDateTime(LocalDateTime.now());
        // TODO: both these saves should be in same transaction.
        orderDao.saveOrder(order);
        sfmsDao.saveOrder(order);
        return order;
    }

    @Override
    public List<Order> getCompletedOrders() {
        return getOrdersByStatus(OrderStatus.COMPLETED);
    }

    @Override
    public List<Order> getCompletedOrdersBetween(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = new ArrayList<>();
        for (Order order : getOrdersByStatus(OrderStatus.COMPLETED)) {
            if (betweenStartAndEndTime(order.getCompletedDateTime(), start, end))
                orders.add(order);
        }
        return orders;
    }

    @Override
    public Order undoCompletion(int id) {
        Order order = orderDao.getOrderById(id);
        orderDao.undoCompletion(order);
        sfmsDao.undoCompletion(order);
        order = order.setStatus(OrderStatus.PROCESSING);
        order = order.setCompletedDateTime(null);
        orderDao.saveOrder(order);
        return order;
    }

    /** Return true if dateTime is between start and end date time's, inclusive.*/
    private boolean betweenStartAndEndTime(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        if (dateTime.isEqual(start) || dateTime.isAfter(start)) {
            if (dateTime.isEqual(end) || dateTime.isBefore(end)) {
                return true;
            }
        }
        return false;
    }

    private List<Order> getOrdersByStatus(OrderStatus status) {
        return this.getOrders().stream().filter(order -> order.getStatus() == status).collect(Collectors.toList());
    }
}
