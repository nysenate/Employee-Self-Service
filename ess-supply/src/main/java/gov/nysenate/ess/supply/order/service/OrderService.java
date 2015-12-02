package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.order.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    int submitOrder(Employee customer, Location location, Map<String, Integer> items);

    Order getOrderById(int id);
}
