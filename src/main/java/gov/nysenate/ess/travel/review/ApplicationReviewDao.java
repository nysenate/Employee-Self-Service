package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    List<ApplicationReview> selectAppReviewsByNextRole(TravelRole nextReviewerRole);

    ApplicationReview selectAppReviewsById(int approvalId);
}
