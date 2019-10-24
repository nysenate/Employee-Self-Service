package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.EventType;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class PurposeOfTravelView implements ViewObject {

    private EventTypeView eventType;
    private String eventName;
    private String additionalPurpose;
    // A set of all valid event types that a purpose of travel could have.
    private List<EventTypeView> validEventTypes;

    public PurposeOfTravelView() {
    }

    public PurposeOfTravelView(PurposeOfTravel purposeOfTravel) {
        this.eventType = purposeOfTravel == null ? null : new EventTypeView(purposeOfTravel.eventType());
        this.eventName = purposeOfTravel == null ? "" : purposeOfTravel.eventName();
        this.additionalPurpose = purposeOfTravel == null ? "" : purposeOfTravel.additionalPurpose();
        this.validEventTypes = EnumSet.allOf(EventType.class).stream().map(EventTypeView::new).collect(Collectors.toList());
    }

    public PurposeOfTravel toPurposeOfTravel() {
        if (this.eventType == null) {
            return null;
        }
        return new PurposeOfTravel(eventType.toEventType(), eventName, additionalPurpose);
    }

    public EventTypeView getEventType() {
        return eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public String getAdditionalPurpose() {
        return additionalPurpose;
    }

    public List<EventTypeView> getValidEventTypes() {
        return validEventTypes;
    }

    @Override
    public String getViewType() {
        return "purpose-of-travel";
    }
}
