package gov.nysenate.ess.travel.notifications.email.events;

import gov.nysenate.ess.travel.review.ApplicationReview;

public class TravelDisapprovalEmailEvent {

    private final ApplicationReview appReview;

    public TravelDisapprovalEmailEvent(ApplicationReview appReview) {
        this.appReview = appReview;
    }

    public ApplicationReview getAppReview() {
        return appReview;
    }
}
