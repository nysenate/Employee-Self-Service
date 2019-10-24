package gov.nysenate.ess.travel.application;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.EventType;

import java.util.Objects;

public final class PurposeOfTravel {

    private final EventType eventType;
    private final String eventName;
    private final String additionalPurpose;

    public PurposeOfTravel(EventType eventType, String eventName, String additionalPurpose) {
        this.eventType = Preconditions.checkNotNull(eventType);
        // Store no value as an empty string.
        this.eventName = eventName == null ? "" : eventName;
        this.additionalPurpose = additionalPurpose == null ? "" : additionalPurpose;

        if (this.eventType.requiresName() && this.eventName.equals("")) {
            throw new IllegalArgumentException("Event Type of " + this.eventType.displayName() + " requires an event name.");
        }
        if (this.eventType.requiresAdditionalPurpose() && this.additionalPurpose.equals("")) {
            throw new IllegalArgumentException("Event Type of " + this.eventType.displayName() + " requires an additional purpose description.");
        }
    }

    public EventType eventType() {
        return eventType;
    }

    public String eventName() {
        return eventName;
    }

    public String additionalPurpose() {
        return additionalPurpose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurposeOfTravel that = (PurposeOfTravel) o;
        return eventType == that.eventType &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(additionalPurpose, that.additionalPurpose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, eventName, additionalPurpose);
    }
}
