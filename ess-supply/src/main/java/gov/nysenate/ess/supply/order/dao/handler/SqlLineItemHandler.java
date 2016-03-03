package gov.nysenate.ess.supply.order.dao.handler;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle a result set containing Line Item's for a single Order.
 */
public class SqlLineItemHandler extends BaseHandler {
    
    private final SupplyItemService itemService;
    private List<LineItem> lineItems;

    public SqlLineItemHandler(SupplyItemService itemService) {
        this.itemService = itemService;
        lineItems = new ArrayList<>();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        lineItems.add(new LineItem(itemService.getItemById(rs.getInt("item_id")), rs.getInt("quantity")));
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }
}
