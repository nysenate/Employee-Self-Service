package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

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
    public int submitOrder(OrderVersion orderVersion) {
        return orderDao.insertOrder(orderVersion, dateTimeFactory.now());
    }

    @Override
    public void rejectOrder(Order order, String note, Employee modifiedBy) {
        Order updated = order.rejectOrder(note, modifiedBy, dateTimeFactory.now());
        orderDao.saveOrder(updated);
    }

    @Override
    public void updateLineItems(Order order, Set<LineItem> lineItems, String note, Employee modifiedBy) {
        Order updated = order.updateLineItems(lineItems, note, modifiedBy, dateTimeFactory.now());
        orderDao.saveOrder(updated);
    }

    @Override
    public Order getOrder(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    public Order getOrderById(int orderId) {
        return null;
    }
}
