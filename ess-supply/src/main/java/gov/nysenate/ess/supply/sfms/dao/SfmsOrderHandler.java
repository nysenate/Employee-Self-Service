package gov.nysenate.ess.supply.sfms.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.sfms.SfmsOrder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SfmsOrderHandler extends BaseHandler {

    private List<SfmsOrder> sfmsOrders = new ArrayList<>();

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        SfmsOrder order = new SfmsOrder();
        order.setNuIssue(rs.getInt("NUISSUE"));
        order.setIssueDate(getLocalDateFromRs(rs, "DTISSUE"));
        order.setLocCode(rs.getString("CDLOCATTO"));
        order.setLocType(rs.getString("CDLOCTYPETO"));
        order.setIssuedBy(rs.getString("NAISSUEDBY"));
        if (!sfmsOrders.contains(order)) {
            sfmsOrders.add(order);
        }
        sfmsOrders.get(sfmsOrders.indexOf(order)).addItem(new LineItem(rs.getInt("NUXREFCO"), rs.getInt("AMQTYISSUE")));
    }

    public List<SfmsOrder> getSfmsOrders() {
        return sfmsOrders;
    }
}
