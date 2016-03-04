package gov.nysenate.ess.supply.order.dao.handler;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.supply.order.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class SqlOrderHistoryHandler extends BaseHandler {

    private Set<Order> orderHistory;

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {

    }
}
