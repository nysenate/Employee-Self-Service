package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    int submitOrder(Employee customer, LocalDateTime orderDateTime, Location location, Map<String, Integer> items, OrderStatus status);

    Order getOrderById(int id);
}
