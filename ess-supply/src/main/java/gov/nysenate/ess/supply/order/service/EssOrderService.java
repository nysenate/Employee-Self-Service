package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.sfms.dao.SfmsOrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class EssOrderService implements OrderService {

    @Autowired private OrderDao orderDao;

    @Autowired private SfmsOrderDao sfmsDao;

    @Autowired private OrderSearchService orderSearchService;

    @Autowired
    private EmployeeInfoService employeeInfoService;

    @Override
    public Order submitOrder(Set<LineItem> lineItems, int customerId, int modifiedEmpId) {
        Employee customer = employeeInfoService.getEmployee(customerId);
        Location location = customer.getWorkLocation();
        Order order = new Order.Builder(customer, LocalDateTime.now(), location, OrderStatus.PENDING,
                                        modifiedEmpId, LocalDateTime.now()).lineItems(lineItems).build();
        return orderDao.insertOrder(order);
    }

    @Override
    public Order processOrder(int orderId, int issuingEmpId, int modifiedEmpId) {
        Employee issuingEmployee = employeeInfoService.getEmployee(issuingEmpId);
        Order order = orderSearchService.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new WrongOrderStatusException("Can only process orders with status of " + OrderStatus.PENDING +
                                                ". Tried to process order with status of " + order.getStatus().toString());
        }
        order = order.setIssuingEmployee(issuingEmployee);
        order = order.setStatus(OrderStatus.PROCESSING);
        order = order.setProcessedDateTime(LocalDateTime.now());
        saveOrder(order, modifiedEmpId);
        return order;
    }

    @Override
    public Order completeOrder(int orderId, int modifiedEmpId) {
        Order order = orderSearchService.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new WrongOrderStatusException("Can only complete orders with status of " + OrderStatus.PROCESSING +
                                                ". Tried to complete order with status of " + order.getStatus().toString());
        }
        order = order.setStatus(OrderStatus.COMPLETED);
        order = order.setCompletedDateTime(LocalDateTime.now());
        saveOrder(order, modifiedEmpId);
//        sfmsDao.saveOrder(order); // FIXME: sfms will be updated in separate process.
        return order;
    }

    // TODO: are we keeping this functionality? Update SFMS?
    @Override
    public Order undoCompletion(int orderId, int modifiedEmpId) {
        Order order = orderSearchService.getOrderById(orderId);
        order = order.setStatus(OrderStatus.PROCESSING);
        order = order.setCompletedDateTime(null);
        saveOrder(order, modifiedEmpId);
        return order;
    }

    @Override
    public Order rejectOrder(int orderId, int modifiedEmpId) {
        Order order = orderSearchService.getOrderById(orderId);
        if (!statusIsPendingOrProcessing(order)) {
            throw new WrongOrderStatusException("Can only reject orders with status of " + OrderStatus.PENDING +
                                                ". Tried to reject order with status of " + order.getStatus().toString());
        }
        order = order.setStatus(OrderStatus.REJECTED);
        saveOrder(order, modifiedEmpId);
        return order;
    }

    @Override
    public Order updateOrderLineItems(int orderId, Set<LineItem> newLineItems, int modifiedEmpId) {
        Order original = orderSearchService.getOrderById(orderId);
        Order updated = original.setLineItems(newLineItems);
        saveOrder(updated, modifiedEmpId);
        return updated;
    }

    /**
     * Persist the order to the database.
     */
    private void saveOrder(Order order, int modifiedEmpId) {
        order = order.setModifiedEmpId(modifiedEmpId);
        order = order.setModifiedDateTime(LocalDateTime.now());
        orderDao.saveOrder(order);
    }

    private boolean statusIsPendingOrProcessing(Order order) {
        return order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PROCESSING;
    }
}
