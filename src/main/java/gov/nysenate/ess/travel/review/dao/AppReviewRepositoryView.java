package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

/**
 * Temporarily holds values from the App review table so the ApplicationReview object
 * can be created all at once without using multiple db connections.
 */
class AppReviewRepositoryView {
    public int appReviewId;
    public int appId;
    public TravelRole travelerRole;
    public boolean isShared;

    public AppReviewRepositoryView() {
    }
}
