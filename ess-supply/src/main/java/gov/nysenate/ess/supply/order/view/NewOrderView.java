package gov.nysenate.ess.supply.order.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.LineItemView;

public class NewOrderView implements ViewObject {

    protected int customerId;
    protected LineItemView[] items;

    public NewOrderView() {
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LineItemView[] getItems() {
        return items;
    }

    public void setItems(LineItemView[] items) {
        this.items = items;
    }

    @Override
    public String getViewType() {
        return "New order view";
    }
}
