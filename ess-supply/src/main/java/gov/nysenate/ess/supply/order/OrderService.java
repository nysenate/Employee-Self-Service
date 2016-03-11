package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.item.LineItem;

import java.util.Set;

public interface OrderService {

    int submitOrder(OrderVersion orderVersion);

    void rejectOrder(Order order, String note, Employee modifiedBy);

    void updateLineItems(Order order, Set<LineItem> lineItems, String note, Employee modifiedBy);

    Order getOrder(int orderId);
}
