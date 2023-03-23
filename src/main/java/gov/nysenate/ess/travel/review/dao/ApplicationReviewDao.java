package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.ApplicationReview;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ApplicationReviewDao {

    void saveApplicationReview(ApplicationReview appReview);

    ApplicationReview selectAppReviewById(int approvalId);

    /**
     * Retrieve AppReviews which are pending review by the given role.
     * role should be a primary non DepartmentHead role.
     * @param role A non department head primary role.
     * @return
     */
    List<ApplicationReview> pendingReviewsForRole(TravelRole role);

    /**
     * Retrieve AppReviews which are pending review by a Department head with one of the given emp ids.
     * @param empIds
     * @return
     */
    List<ApplicationReview> pendingReviewsForDeptHd(Collection<Integer> empIds);

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

    /**
     * Get all ApplicationReview's which are approved and have an amendment with
     * a start date between from and to inclusive.
     * @param from
     * @param to
     * @return
     */
    List<ApplicationReview> approvedAppReviews(LocalDate from, LocalDate to);
}
