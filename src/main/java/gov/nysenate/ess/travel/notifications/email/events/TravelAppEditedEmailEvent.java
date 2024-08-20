package gov.nysenate.ess.travel.notifications.email.events;

import gov.nysenate.ess.travel.request.app.TravelApplication;

public class TravelAppEditedEmailEvent {

    private final TravelApplication application;

    public TravelAppEditedEmailEvent(TravelApplication app) {
        this.application = app;
    }

    public TravelApplication getApplication() {
        return application;
    }
}
