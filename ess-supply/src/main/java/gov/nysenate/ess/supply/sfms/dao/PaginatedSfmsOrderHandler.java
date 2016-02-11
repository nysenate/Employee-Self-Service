package gov.nysenate.ess.supply.sfms.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.sfms.SfmsLineItem;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.SfmsOrderId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaginatedSfmsOrderHandler extends BaseHandler {

    private LimitOffset limitOffset;
    private int totalCount = 0;
    private Map<SfmsOrderId, SfmsOrder> sfmsOrderMap = new HashMap<>();

    public PaginatedSfmsOrderHandler(LimitOffset limitOffset) {
        this.limitOffset = limitOffset;
    }

    /**
     * Each order object in the result set is represented in multiple rows.
     * Each row will contain information on a single item and a composite key of order values.
     * Need to use the composite key to join all items belonging to that order.
     */
    @Override
    public void processRow(ResultSet rs) throws SQLException {
        if (totalCount == 0) {
            totalCount = rs.getInt("total_rows");
        }
        SfmsOrder order = extractSfmsOrder(rs);
        if (!sfmsOrderMap.containsKey(order.getOrderId())) {
            sfmsOrderMap.put(order.getOrderId(), order);
        }
        addItemToOrder(rs, order);
    }

    /**
     * Extract order composite key and other values from the result set.
     * The resulting SmfsOrder is unique per order.
     */
    private SfmsOrder extractSfmsOrder(ResultSet rs) throws SQLException {
        SfmsOrderId sfmsOrderId = new SfmsOrderId(rs.getInt("NUISSUE"), getLocalDateFromRs(rs, "DTISSUE"),
                                                  rs.getString("CDLOCATTO"), rs.getString("CDLOCTYPETO"));
        SfmsOrder sfmsOrder = new SfmsOrder(sfmsOrderId);
        sfmsOrder.setFromLocationCode(rs.getString("CDLOCATFROM"));
        sfmsOrder.setFromLocationType(rs.getString("CDLOCTYPEFRM"));
        sfmsOrder.setUpdateDateTime(getLocalDateTimeFromRs(rs, "DTTXNUPDATE"));
        sfmsOrder.setOriginDateTime(getLocalDateTimeFromRs(rs, "DTTXNORIGIN"));
        sfmsOrder.setUpdateEmpUid(rs.getString("NATXNUPDUSER"));
        sfmsOrder.setOriginalEmpUid(rs.getString("NATXNORGUSER"));
        sfmsOrder.setIssuedBy(rs.getString("NAISSUEDBY"));
        sfmsOrder.setResponsibilityCenterHead(rs.getString("CDRESPCTRHD"));
        return sfmsOrder;
    }

    /**
     * Extract an individual item from the result set.
     * Each row in the result set contains info on a single item.
     */
    private void addItemToOrder(ResultSet rs, SfmsOrder order) throws SQLException {
        SfmsLineItem item = new SfmsLineItem();
        item.setItemId(rs.getInt("NUXREFCO"));
        item.setQuantity(rs.getInt("AMQTYISSUE"));
        item.setStandardQuantity(rs.getInt("AMQTYISSSTD"));
        item.setUnit(rs.getString("CDISSUNIT"));
        sfmsOrderMap.get(order.getOrderId()).addItem(item);
    }

    public PaginatedList<SfmsOrder> getSfmsOrders() {
        return new PaginatedList<>(totalCount, limitOffset, new ArrayList<>(sfmsOrderMap.values()));
    }
}
