package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    ApplicationReview selectAppReviewById(int approvalId);

    /**
     * Get all ApplicationReview's that require action by the given role.
     * Note for TravelRole.DepartmentHead, this returns ApplicationReviews
     * for ALL departments. Use {@link #pendingReviewsForDeptHead(Employee)}
     * to get pending reviews for a single department.
     */
    List<ApplicationReview> pendingReviewsByRole(TravelRole nextReviewerRole);

    /**
     * Get all ApplicationReview's that require action by the department head
     * {@code departmentHead}.
     * Returns an empty list if {@code departmentHead} is not really a Department Head.
     */
    List<ApplicationReview> pendingReviewsForDeptHead(Employee departmentHead);

    /**
     * Gets all reviews that have been shared, have not been disapproved,
     * and have not been approved by both the Travel Admin and SOS.
     * @return
     */
    List<ApplicationReview> pendingSharedReviews();

    List<ApplicationReview> reviewHistoryForRole(TravelRole role);
}
