package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class UpdateLineItemView implements ViewObject {

    protected LineItemView[] lineItems;
    protected String note;

    public LineItemView[] getLineItems() {
        return lineItems;
    }

    public void setLineItems(LineItemView[] lineItems) {
        this.lineItems = lineItems;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getViewType() {
        return "update-line-item";
    }
}
