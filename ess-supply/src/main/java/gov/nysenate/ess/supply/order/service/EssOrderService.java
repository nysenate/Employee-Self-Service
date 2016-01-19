package gov.nysenate.ess.supply.order.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.order.dao.sfms.SfmsOrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class EssOrderService implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SfmsOrderDao sfmsDao;

    @Autowired
    private EmployeeInfoService employeeInfoService;

    @Override
    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff) {
        return getOrders("all", "all", "all", statuses, dateRange, limOff);
    }

    @Override
    public List<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                    Range<LocalDate> dateRange, LimitOffset limOff) {
        return orderDao.getOrders(locCode, locType, issuerEmpId, statuses, dateRange, limOff);
    }

    @Override
    public List<Order> getSfmsOrders(Range<LocalDate> dateRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public List<Order> getSfmsOrders(String locCode, String locType, String issuerEmpId, Range<LocalDate> dateRange, LimitOffset limOff) {
        return null;
    }

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
    public void saveOrder(Order order) {
        orderDao.saveOrder(order);
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

    // TODO: are we keeping this functionality?
    @Override
    public Order undoCompletion(int id) {
        Order order = orderDao.getOrderById(id);
        orderDao.undoCompletion(order);
//        sfmsDao.undoCompletion(order);
        order = order.setStatus(OrderStatus.PROCESSING);
        order = order.setCompletedDateTime(null);
        orderDao.saveOrder(order);
        return order;
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

}
