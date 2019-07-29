package gov.nysenate.ess.supply.reconcilation.model;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

public class ReconciliationResults {

    private final ImmutableSet<ReconciliationError> errors;

    public ReconciliationResults(Set<ReconciliationError> errors) {
        this.errors = ImmutableSet.copyOf(errors);
    }

    public boolean success() {
        return getErrors().isEmpty();
    }

    public Set<ReconciliationError> errors() {
        return getErrors();
    }

    private ImmutableSet<ReconciliationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ReconciliationResults{" +
                "errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationResults that = (ReconciliationResults) o;
        return Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }
}
