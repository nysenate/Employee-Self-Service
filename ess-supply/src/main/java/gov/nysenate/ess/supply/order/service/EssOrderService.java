package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EssOrderService implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public int submitOrder(Employee customer, Location location, Map<String, Integer> items) {
        return orderDao.submitOrder(customer, LocalDateTime.now(), location, items, OrderStatus.PENDING);
    }

    @Override
    public Order getOrderById(int id) {
        return orderDao.getOrderById(id);
    }

}
