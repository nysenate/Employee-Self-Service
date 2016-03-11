package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;

public interface OrderService {

    int submitOrder(OrderVersion orderVersion);

    void rejectOrder(Order order, String note, Employee modifiedBy);

    Order getOrder(int orderId);
}
