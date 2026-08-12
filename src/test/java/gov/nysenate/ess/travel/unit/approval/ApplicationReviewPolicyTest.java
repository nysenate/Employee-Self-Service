package gov.nysenate.ess.travel.unit.approval;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.Action;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyRegistry;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyType;
import gov.nysenate.ess.travel.review.view.ActionType;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ApplicationReviewPolicyTest {

    private final ReviewPolicyRegistry registry = new ReviewPolicyRegistry();

    @Test
    public void approvalAdvancesPersistedPendingRole() {
        var review = new ApplicationReview(null, registry.resolve(ReviewPolicyType.STANDARD, 1));

        review.addAction(action(TravelRole.DEPARTMENT_HEAD, ActionType.APPROVE));

        assertEquals(TravelRole.TRAVEL_ADMIN, review.pendingReviewerRole());
    }

    @Test
    public void disapprovalEndsWorkflow() {
        var review = new ApplicationReview(null, registry.resolve(ReviewPolicyType.STANDARD, 1));

        review.addAction(action(TravelRole.DEPARTMENT_HEAD, ActionType.DISAPPROVE));

        assertEquals(TravelRole.NONE, review.pendingReviewerRole());
    }

    @Test
    public void resubmissionRestartsSamePolicy() {
        var review = new ApplicationReview(null, registry.resolve(ReviewPolicyType.SECRETARY_ONLY, 1));
        review.addAction(action(TravelRole.SECRETARY_OF_THE_SENATE, ActionType.DISAPPROVE));

        review.restart();

        assertEquals(ReviewPolicyType.SECRETARY_ONLY, review.policyType());
        assertEquals(1, review.policyVersion());
        assertEquals(TravelRole.SECRETARY_OF_THE_SENATE, review.pendingReviewerRole());
    }

    @Test(expected = IllegalArgumentException.class)
    public void actionByNonPendingRoleIsRejected() {
        var review = new ApplicationReview(null, registry.resolve(ReviewPolicyType.STANDARD, 1));

        review.addAction(action(TravelRole.TRAVEL_ADMIN, ActionType.APPROVE));
    }

    @Test
    public void restoredReviewUsesPersistedCursorInsteadOfReplayingActions() {
        var policy = registry.resolve(ReviewPolicyType.STANDARD, 1);
        var review = new ApplicationReview(10, null, policy, TravelRole.SECRETARY_OF_THE_SENATE,
                List.of(action(TravelRole.DEPARTMENT_HEAD, ActionType.APPROVE)), false);

        assertEquals(TravelRole.SECRETARY_OF_THE_SENATE, review.pendingReviewerRole());
    }

    private static Action action(TravelRole role, ActionType type) {
        return new Action(0, null, role, type, "", LocalDateTime.now());
    }
}
