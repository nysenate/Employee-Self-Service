package gov.nysenate.ess.supply.order.dao;

import java.time.LocalDateTime;

public interface OrderHistoryDao {

    void insertOrderHistory(int orderId, int versionId, LocalDateTime modifyDateTime);
}
