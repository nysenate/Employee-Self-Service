package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    ApplicationReview selectAppReviewsById(int approvalId);

    List<ApplicationReview> pendingReviewsByRole(TravelRole nextReviewerRole);

    List<ApplicationReview> selectReviewHistoryForEmp(int employeeId);
}
