package gov.nysenate.ess.travel.review.dao;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.ApplicationReview;

import java.util.Collection;
import java.util.Set;

public class PendingReviews {

    public static Set<ApplicationReview> forRole(TravelRole role, Collection<ApplicationReview> reviews) {
        return reviews.stream()
                .filter(r -> r.nextReviewerRole().equals(role))
                .filter(r -> r.application().status().isPending())
                .collect(ImmutableSet.toImmutableSet());
    }

    public static Set<ApplicationReview> forDepartment(Collection<ApplicationReview> reviews,
                                                       Set<Integer> departmentIds) {
        return forRole(TravelRole.DEPARTMENT_HEAD, reviews).stream()
                .filter(r -> departmentIds.contains(r.application().getTravelerDepartmentId()))
                .collect(ImmutableSet.toImmutableSet());
    }
}
