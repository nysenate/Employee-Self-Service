package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

public class OrderHistoryRowMapper extends BaseHandler {

    private SqlOrderDao orderDao;
    private SortedMap<LocalDateTime, OrderVersion> versionMap;

    public OrderHistoryRowMapper(SqlOrderDao orderDao) {
        this.orderDao = orderDao;
        this.versionMap = new TreeMap<>();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        int versionId = rs.getInt("version_id");
        LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "created_date_time");
        OrderVersion version = orderDao.getOrderVersion(versionId);
        versionMap.put(dateTime, version);
    }

    public OrderHistory getOrderHistory() {
        return OrderHistory.of(versionMap);
    }
}
