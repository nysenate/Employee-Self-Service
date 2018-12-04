package gov.nysenate.ess.supply.reconcilation.model;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class ReconciliationErrorView implements ViewObject {

    protected int itemId;
    protected int expectedQuantity;
    protected int actualQuantity;

    public ReconciliationErrorView() {
    }

    public ReconciliationErrorView(ReconciliationError error) {
        this.itemId = error.getItemId();
        this.expectedQuantity = error.getExpectedQuantity();
        this.actualQuantity = error.getActualQuantity();
    }

    public ReconciliationError toReconciliationError() {
        return new ReconciliationError(itemId, expectedQuantity, actualQuantity);
    }

    public int getItemId() {
        return itemId;
    }

    public int getExpectedQuantity() {
        return expectedQuantity;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    @Override
    public String getViewType() {
        return "reconciliation-error-view";
    }
}
