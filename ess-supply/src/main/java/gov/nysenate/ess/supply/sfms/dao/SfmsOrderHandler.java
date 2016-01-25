package gov.nysenate.ess.supply.sfms.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.sfms.SfmsOrder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SfmsOrderHandler extends BaseHandler {

    private SupplyItemService itemService;
    private List<SfmsOrder> sfmsOrders = new ArrayList<>();

    public SfmsOrderHandler(SupplyItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Each order object in the result set is represented in multiple rows.
     * Each row will contain information on a single item and duplicated order information.
     * Need to use the duplicated order information to join all items belonging to that order.
     */
    @Override
    public void processRow(ResultSet rs) throws SQLException {
        SfmsOrder order = extractSfmsOrder(rs);
        if (!sfmsOrders.contains(order)) {
            sfmsOrders.add(order);
        }
        addItemToOrder(rs, order);
    }
    private SfmsOrder extractSfmsOrder(ResultSet rs) throws SQLException {
        SfmsOrder order = new SfmsOrder();
        order.setNuIssue(rs.getInt("NUISSUE"));
        order.setIssueDate(getLocalDateFromRs(rs, "DTISSUE"));
        order.setLocCode(rs.getString("CDLOCATTO"));
        order.setLocType(rs.getString("CDLOCTYPETO"));
        order.setIssuedBy(rs.getString("NAISSUEDBY"));
        return order;
    }

    private void addItemToOrder(ResultSet rs, SfmsOrder order) throws SQLException {
        sfmsOrders.get(sfmsOrders.indexOf(order)).addItem(
                new LineItem(itemService.getItemById(rs.getInt("NUXREFCO")), rs.getInt("AMQTYISSUE")));
    }

    public List<SfmsOrder> getSfmsOrders() {
        return sfmsOrders;
    }
}
