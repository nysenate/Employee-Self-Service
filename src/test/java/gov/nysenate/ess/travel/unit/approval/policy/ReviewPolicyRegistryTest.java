package gov.nysenate.ess.travel.unit.approval.policy;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyRegistry;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyType;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ReviewPolicyRegistryTest {

    private final ReviewPolicyRegistry registry = new ReviewPolicyRegistry();

    @Test
    public void standardVersionOneUsesCompleteReviewSequence() {
        var policy = registry.resolve(ReviewPolicyType.STANDARD, 1);

        assertEquals(TravelRole.DEPARTMENT_HEAD, policy.firstReviewer());
        assertEquals(TravelRole.TRAVEL_ADMIN, policy.after(TravelRole.DEPARTMENT_HEAD));
        assertEquals(TravelRole.SECRETARY_OF_THE_SENATE, policy.after(TravelRole.TRAVEL_ADMIN));
        assertEquals(TravelRole.NONE, policy.after(TravelRole.SECRETARY_OF_THE_SENATE));
    }

    @Test
    public void eachPolicyResolvesItsFirstReviewer() {
        assertEquals(TravelRole.TRAVEL_ADMIN,
                registry.resolve(ReviewPolicyType.SKIP_DEPARTMENT_HEAD, 1).firstReviewer());
        assertEquals(TravelRole.SECRETARY_OF_THE_SENATE,
                registry.resolve(ReviewPolicyType.SECRETARY_ONLY, 1).firstReviewer());
        assertEquals(TravelRole.TRAVEL_ADMIN,
                registry.resolve(ReviewPolicyType.TRAVEL_ADMIN_ONLY, 1).firstReviewer());
    }

    @Test
    public void skipDepartmentHeadVersionOneUsesTravelUnitSequence() {
        var policy = registry.resolve(ReviewPolicyType.SKIP_DEPARTMENT_HEAD, 1);

        assertEquals(TravelRole.SECRETARY_OF_THE_SENATE, policy.after(TravelRole.TRAVEL_ADMIN));
        assertEquals(TravelRole.NONE, policy.after(TravelRole.SECRETARY_OF_THE_SENATE));
    }

    @Test
    public void singleRolePoliciesEndAfterTheirOnlyReviewer() {
        assertEquals(TravelRole.NONE,
                registry.resolve(ReviewPolicyType.SECRETARY_ONLY, 1)
                        .after(TravelRole.SECRETARY_OF_THE_SENATE));
        assertEquals(TravelRole.NONE,
                registry.resolve(ReviewPolicyType.TRAVEL_ADMIN_ONLY, 1)
                        .after(TravelRole.TRAVEL_ADMIN));
    }

    @Test
    public void terminalRoleRemainsTerminal() {
        assertEquals(TravelRole.NONE,
                registry.resolve(ReviewPolicyType.STANDARD, 1).after(TravelRole.NONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void roleOutsidePolicyIsRejected() {
        registry.resolve(ReviewPolicyType.SECRETARY_ONLY, 1).after(TravelRole.TRAVEL_ADMIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownPolicyVersionIsRejected() {
        registry.resolve(ReviewPolicyType.STANDARD, 2);
    }
}
