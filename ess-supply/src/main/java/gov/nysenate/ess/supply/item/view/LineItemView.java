package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.LineItem;

public class LineItemView implements ViewObject {

    protected SupplyItemView item;
    protected int quantity;

    public LineItemView() {

    }

    public LineItemView(LineItem lineItem) {
        this.item = new SupplyItemView(lineItem.getItem());
        this.quantity = lineItem.getQuantity();
    }

    public LineItem toLineItem() {
        return new LineItem(item.toSupplyItem(), this.quantity);
    }

    public SupplyItemView getItem() {
        return item;
    }

    public void setItem(SupplyItemView item) {
        this.item = item;
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
