package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.LineItem;

public class LineItemView implements ViewObject {

    protected int itemId;
    protected int quantity;

    public LineItemView() {

    }

    public LineItemView(LineItem lineItem) {
        this.itemId = lineItem.getItemId();
        this.quantity = lineItem.getQuantity();
    }

    public LineItem toLineItem() {
        return new LineItem(this.itemId, this.quantity);
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getViewType() {
        return "Supply Line Item";
    }
}
