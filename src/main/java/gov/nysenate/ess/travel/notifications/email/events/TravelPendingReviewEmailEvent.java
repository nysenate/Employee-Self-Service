package gov.nysenate.ess.travel.notifications.email.events;

import gov.nysenate.ess.travel.review.ApplicationReview;

public class TravelPendingReviewEmailEvent {

    private final ApplicationReview appReview;

    public TravelPendingReviewEmailEvent(ApplicationReview appReview) {
        this.appReview = appReview;
    }

    public ApplicationReview getAppReview() {
        return appReview;
    }
}
