package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SupplyOrderService implements OrderService {

    private OrderDao orderDao;
    private DateTimeFactory dateTimeFactory;

    @Autowired
    public SupplyOrderService(OrderDao orderDao, DateTimeFactory dateTimeFactory) {
        this.orderDao = orderDao;
        this.dateTimeFactory = dateTimeFactory;
    }

    @Override
    public Order submitOrder(OrderVersion orderVersion) {
        return orderDao.insertOrder(orderVersion, dateTimeFactory.now());
    }

    @Override
    public void rejectOrder(Order order, String note, Employee modifiedBy) {
        order.rejectOrder(note, modifiedBy, LocalDateTime.now());
    }

    @Override
    public Order getOrder(int orderId) {
        return orderDao.getOrderById(orderId);
    }

//    public Order submitOrder(int customerId, String locCode, String locType, Map<Integer, Integer> lineItemMap,
//                             LocalDateTime modifyDateTime) {
//        Employee customer = employeeInfoService.getEmployee(customerId);
//        Location location = locationService.getLocation(locCode, LocationType.valueOfCode(locType.charAt(0)));
//        Set<LineItem> lineItems = createLineItems(lineItemMap);
//        OrderVersion version = new OrderVersion.Builder().withId(1).withCustomer(customer).withDestination(location)
//                                                         .withStatus(OrderStatus.APPROVED).withLineItems(lineItems).withModifiedBy(customer).build();
//        Order order = orderDao.insertOrder(version, modifyDateTime);
//        return order;
//    }
//
//    public Order rejectOrder(int orderId, int modifiedEmpId, LocalDateTime modifiedDateTime) {
//        return rejectOrder(orderId, null, modifiedEmpId, modifiedDateTime);
//    }
//
//    public Order rejectOrder(int orderId, String note, int modifiedEmpId, LocalDateTime modifiedDateTime) {
//        Order order = getOrderById(orderId);
//        Employee modifiedEmp = employeeInfoService.getEmployee(modifiedEmpId);
//        order.rejectOrder(note, modifiedEmp, modifiedDateTime);
//        orderDao.saveOrder(order);
//        return order;
//    }
//
//    public Order updateLineItems(int orderId, Map<Integer, Integer> lineItems, String note, int modifiedEmpId, LocalDateTime modifiedDateTime) {
//        Order order = getOrderById(orderId);
//        Employee modifiedEmp = employeeInfoService.getEmployee(modifiedEmpId);
//        order.updateLineItems(createLineItems(lineItems), note, modifiedEmp, modifiedDateTime);
//        orderDao.saveOrder(order);
//        return order;
//    }

    public Order getOrderById(int orderId) {
        return null;
    }

}
