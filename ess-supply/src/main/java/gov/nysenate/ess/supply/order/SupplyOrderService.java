package gov.nysenate.ess.supply.order;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.shipment.ShipmentService;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
public class SupplyOrderService implements OrderService {

    @Autowired private OrderDao orderDao;
    @Autowired private DateTimeFactory dateTimeFactory;
    @Autowired private ShipmentService shipmentService;

    public SupplyOrderService() {
    }

    public SupplyOrderService(OrderDao orderDao, DateTimeFactory dateTimeFactory, ShipmentService shipmentService) {
        this.orderDao = orderDao;
        this.dateTimeFactory = dateTimeFactory;
        this.shipmentService = shipmentService;
    }

    @Override
    public int submitOrder(OrderVersion orderVersion) {
        int  orderId = orderDao.insertOrder(orderVersion, dateTimeFactory.now());
        shipmentService.initializeShipment(orderDao.getOrderById(orderId));
        return orderId;
    }

    @Override
    public void rejectOrder(Order order, String note, Employee modifiedBy) {
        Order updated = order.rejectOrder(note, modifiedBy, dateTimeFactory.now());
        orderDao.saveOrder(updated);
    }

    @Override
    public void approveOrder(Order order, Employee modifiedBy) {
        Order updated = order.approveOrder(modifiedBy, dateTimeFactory.now());
        orderDao.saveOrder(updated);
    }

    @Override
    public void updateLineItems(Order order, Set<LineItem> lineItems, String note, Employee modifiedBy) {
        Order updated = order.updateLineItems(lineItems, note, modifiedBy, dateTimeFactory.now());
        orderDao.saveOrder(updated);
    }

    @Override
    public void addNote(Order order, String note, Employee modifiedBy) {
        Order updated = order.addNote(note, modifiedBy, dateTimeFactory.now());
        orderDao.saveOrder(updated);
    }

    @Override
    public Order getOrder(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public PaginatedList<Order> getOrders(String location, String customerId, EnumSet<OrderStatus> statuses, Range<LocalDateTime> updatedDateTimeRange, LimitOffset limOff) {
        return orderDao.getOrders(location, customerId, statuses, updatedDateTimeRange, limOff);
    }

}
