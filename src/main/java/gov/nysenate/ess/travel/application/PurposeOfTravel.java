package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.travel.EventType;

import java.util.Objects;

public class PurposeOfTravel {

    private EventType eventType;
    private String eventName;
    private String additionalPurpose;

    public PurposeOfTravel(EventType eventType, String eventName, String additionalPurpose) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.additionalPurpose = additionalPurpose;
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
