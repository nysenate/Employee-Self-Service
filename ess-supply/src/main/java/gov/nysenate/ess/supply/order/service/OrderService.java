package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;

import java.util.Set;

public interface OrderService {

    Order submitOrder(Set<LineItem> items, int empId);

    Order processOrder(int orderId, int issuingEmpId);

    Order completeOrder(int orderId);

    Order undoCompletion(int id);

    Order rejectOrder(int orderId);

    /**
     * Replaces an order's line items with newLineItems and saves the changes.
     * @return The updated order.
     */
    Order updateOrderLineItems(int id, Set<LineItem> newLineItems);
}
