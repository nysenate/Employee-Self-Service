package gov.nysenate.ess.supply.reconcilation.model;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.LocationId;

import java.util.Set;
import java.util.stream.Collectors;

public class ReconciliationResultsView implements ViewObject {

    protected String locationCode;
    protected String locationType;
    protected Set<ReconciliationErrorView> errors;
    protected boolean success;

    public ReconciliationResultsView() {
    }

    public ReconciliationResultsView(ReconciliationResults results) {
        this.locationCode = results.getLocationId().getCode();
        this.locationType = results.getLocationId().getType().getCode() + "";
        this.errors = results.errors().stream()
                .map(ReconciliationErrorView::new)
                .collect(Collectors.toSet());
        this.success = results.success();
    }

    public ReconciliationResults toReconciliationResults() {
        return new ReconciliationResults(new LocationId(locationCode, locationType.charAt(0)),
                errors.stream().map(ReconciliationErrorView::toReconciliationError).collect(Collectors.toSet()));
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getLocationType() {
        return locationType;
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
