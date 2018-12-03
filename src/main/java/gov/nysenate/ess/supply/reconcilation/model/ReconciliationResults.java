package gov.nysenate.ess.supply.reconcilation.model;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.LocationId;

import java.util.Objects;
import java.util.Set;

public class ReconciliationResults {

    private final LocationId locationId;
    private final ImmutableSet<ReconciliationError> errors;

    public ReconciliationResults(LocationId locationId, Set<ReconciliationError> errors) {
        this.locationId = locationId;
        this.errors = ImmutableSet.copyOf(errors);
    }

    public boolean success() {
        return getErrors().isEmpty();
    }

    public Set<ReconciliationError> errors() {
        return getErrors();
    }

    public LocationId getLocationId() {
        return locationId;
    }

    private ImmutableSet<ReconciliationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ReconciliationResults{" +
                "locationId=" + locationId +
                ", errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationResults that = (ReconciliationResults) o;
        return Objects.equals(locationId, that.locationId) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, errors);
    }
}
