package gov.nysenate.ess.supply.requisition.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.LineItemView;

public class SubmitRequisitionView implements ViewObject {

    protected int customerId;
    protected LineItemView[] lineItems;
    protected String destinationId;
    protected String specialInstructions;

    public SubmitRequisitionView() {
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LineItemView[] getLineItems() {
        return lineItems;
    }

    public void setLineItems(LineItemView[] lineItems) {
        this.lineItems = lineItems;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    @Override
    public String getViewType() {
        return "submit-order-view";
    }
}
