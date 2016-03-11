package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDateTime;
import java.util.Map;

public interface OrderService {

    Order submitOrder(OrderVersion orderVersion);

    void rejectOrder(Order order, String note, Employee modifiedBy);

    Order getOrder(int orderId);
}
