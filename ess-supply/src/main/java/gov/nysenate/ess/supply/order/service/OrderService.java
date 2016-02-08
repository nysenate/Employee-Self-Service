package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;

import java.util.Set;

public interface OrderService {

    Order submitOrder(Set<LineItem> items, int empId, int modifiedEmpId);

    Order processOrder(int orderId, int issuingEmpId, int modifiedEmpId);

    Order completeOrder(int orderId, int modifiedEmpId);

    Order undoCompletion(int orderId, int modifiedEmpId);

    Order rejectOrder(int orderId, int modifiedEmpId);

    /**
     * Replaces an order's line items with newLineItems and saves the changes.
     * @return The updated order.
     */
    Order updateOrderLineItems(int orderId, Set<LineItem> newLineItems, int modifiedEmpId);
}
