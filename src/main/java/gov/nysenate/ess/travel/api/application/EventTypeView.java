package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.EventType;

public class EventTypeView implements ViewObject {

    private String name;
    private String displayName;
    private boolean requiresName;
    private boolean requiresAdditionalPurpose;

    public EventTypeView() {
    }

    public EventTypeView(EventType eventType){
        this.name = eventType.name();
        this.displayName = eventType.displayName();
        this.requiresName = eventType.requiresName();
        this.requiresAdditionalPurpose = eventType.requiresAdditionalPurpose();
    }

    public EventType toEventType() {
        return EventType.valueOf(name);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequiresName() {
        return requiresName;
    }

    public boolean isRequiresAdditionalPurpose() {
        return requiresAdditionalPurpose;
    }

    @Override
    public String getViewType() {
        return "event-type";
    }
}
