package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.ApplicationReview;

import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    ApplicationReview selectAppReviewById(int approvalId);

    List<ApplicationReview> selectAllReviews();

    /**
     * Gets all reviews that have been shared, have not been disapproved,
     * and have not been approved by both the Travel Admin and SOS.
     */
    List<ApplicationReview> pendingSharedReviews();

    /**
     * Get all ApplicationReview's which have an action from the given role.
     * <p>
     * Note, to get review history for a single department, see
     * {@link #reviewHistoryForDeptHead(Employee)}.
     */
    List<ApplicationReview> reviewHistoryForRole(TravelRole role);

    /**
     * Gets all ApplicationReview's that have a DEPARTMENT_HEAD action
     * and the traveler is in a department managed by {@code departmentHead}.
     * <p>
     * Returns an empty list if {@code departmentHead} is not a Department Head.
     *
     * @param departmentHead A department head.
     */
    List<ApplicationReview> reviewHistoryForDeptHead(Employee departmentHead);

    ApplicationReview selectAppReviewByAppId(int appId);
}
