package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class PurposeOfTravelView implements ViewObject {

    private EventTypeView eventType;
    private String eventName;
    private String additionalPurpose;

    public PurposeOfTravelView() {
    }

    public PurposeOfTravelView(PurposeOfTravel purposeOfTravel) {
        this.eventType = new EventTypeView(purposeOfTravel.eventType());
        this.eventName = purposeOfTravel.eventName();
        this.additionalPurpose = purposeOfTravel.additionalPurpose();
    }

    public PurposeOfTravel toPurposeOfTravel() {
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

    @Override
    public String getViewType() {
        return "purpose-of-travel";
    }
}
