package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.app.PurposeOfTravel;

public class PurposeOfTravelView implements ViewObject {

    private EventTypeView eventType;
    private String eventName;
    private String additionalPurpose;

    public PurposeOfTravelView() {
    }

    public PurposeOfTravelView(PurposeOfTravel purposeOfTravel) {
        this.eventType = purposeOfTravel == null ? null : new EventTypeView(purposeOfTravel.eventType());
        this.eventName = purposeOfTravel == null ? "" : purposeOfTravel.eventName();
        this.additionalPurpose = purposeOfTravel == null ? "" : purposeOfTravel.additionalPurpose();
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

    @Override
    public String getViewType() {
        return "purpose-of-travel";
    }
}
