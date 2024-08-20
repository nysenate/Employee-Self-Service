package gov.nysenate.ess.travel.notifications.email.events;

import gov.nysenate.ess.travel.review.ApplicationReview;

public final class TravelApprovalEmailEvent {

    private final ApplicationReview applicationReview;

    public TravelApprovalEmailEvent(ApplicationReview applicationReview) {
        this.applicationReview = applicationReview;
    }

    public ApplicationReview getApplicationReview() {
        return applicationReview;
    }
}
