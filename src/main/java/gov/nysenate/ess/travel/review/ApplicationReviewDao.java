package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    ApplicationReview selectAppReviewById(int approvalId);

    List<ApplicationReview> pendingReviewsByRole(TravelRole nextReviewerRole);

    /**
     * Gets all reviews that have been shared, have not been disapproved,
     * and have not been approved by both the Travel Admin and SOS.
     * @return
     */
    List<ApplicationReview> pendingSharedReviews();

    List<ApplicationReview> reviewHistoryForRole(TravelRole role);
}
