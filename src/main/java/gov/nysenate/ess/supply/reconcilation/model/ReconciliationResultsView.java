package gov.nysenate.ess.supply.reconcilation.model;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.Set;
import java.util.stream.Collectors;

public class ReconciliationResultsView implements ViewObject {

    protected Set<ReconciliationErrorView> errors;
    protected boolean success;

    public ReconciliationResultsView() {
    }

    public ReconciliationResultsView(ReconciliationResults results) {
        this.errors = results.errors().stream()
                .map(ReconciliationErrorView::new)
                .collect(Collectors.toSet());
        this.success = results.success();
    }

    public ReconciliationResults toReconciliationResults() {
        return new ReconciliationResults(errors.stream()
                .map(ReconciliationErrorView::toReconciliationError)
                .collect(Collectors.toSet()));
    }

    public Set<ReconciliationErrorView> getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getViewType() {
        return "reconciliation-results-view";
    }
}
