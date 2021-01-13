package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    ApplicationReview selectAppReviewById(int approvalId);

    /**
     * Get all ApplicationReview's that require action by the given role.
     *
     * Note for TravelRole.DepartmentHead, this returns ApplicationReviews
     * for ALL departments. Use {@link #pendingReviewsForDeptHead(Employee)}
     * to get pending reviews for a single department.
     */
    List<ApplicationReview> pendingReviewsByRole(TravelRole nextReviewerRole);

    /**
     * Get all ApplicationReview's that require action by the department head
     * {@code departmentHead}.
     *
     * Returns an empty list if {@code departmentHead} is not a Department Head.
     */
    List<ApplicationReview> pendingReviewsForDeptHead(Employee departmentHead);

    /**
     * Gets all reviews that have been shared, have not been disapproved,
     * and have not been approved by both the Travel Admin and SOS.
     */
    List<ApplicationReview> pendingSharedReviews();

    /**
     * Get all ApplicationReview's which have an action from the given role.
     *
     * Note, to get review history for a single department, see
     * {@link #reviewHistoryForDeptHead(Employee)}.
     */
    List<ApplicationReview> reviewHistoryForRole(TravelRole role);

    /**
     * Gets all ApplicationReview's that have a DEPARTMENT_HEAD action
     * and the traveler is in a department managed by {@code departmentHead}.
     *
     * Returns an empty list if {@code departmentHead} is not a Department Head.
     * @param departmentHead A department head.
     */
    List<ApplicationReview> reviewHistoryForDeptHead(Employee departmentHead);
}
