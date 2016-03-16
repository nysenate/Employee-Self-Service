package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.order.OrderHistory;

import java.time.LocalDateTime;

public interface OrderHistoryDao {

    void insertOrderHistory(int orderId, int versionId, LocalDateTime modifyDateTime);

    OrderHistory getOrderHistory(int orderId);
}
