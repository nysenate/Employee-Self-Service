package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.EventType;

public class EventTypeView implements ViewObject {

    private String name;
    private String displayName;

    public EventTypeView() {
    }

    public EventTypeView(EventType eventType){
        this.name = eventType.name();
        this.displayName = eventType.displayName();
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

    @Override
    public String getViewType() {
        return "event-type";
    }
}
