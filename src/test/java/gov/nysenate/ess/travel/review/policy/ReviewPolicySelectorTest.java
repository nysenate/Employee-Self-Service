package gov.nysenate.ess.travel.review.policy;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ReviewPolicySelectorTest {

    private static final TravelRoles NO_ROLES = roles();

    @Test
    public void ordinaryTravelerUsesStandardPolicy() {
        assertEquals(ReviewPolicyType.STANDARD,
                ReviewPolicySelector.selectPolicyType(NO_ROLES, NO_ROLES));
    }

    @Test
    public void departmentHeadAndMajorityLeaderTravelersSkipDepartmentHead() {
        assertEquals(ReviewPolicyType.SKIP_DEPARTMENT_HEAD,
                ReviewPolicySelector.selectPolicyType(roles(TravelRole.DEPARTMENT_HEAD), NO_ROLES));
        assertEquals(ReviewPolicyType.SKIP_DEPARTMENT_HEAD,
                ReviewPolicySelector.selectPolicyType(roles(TravelRole.MAJORITY_LEADER), NO_ROLES));
    }

    @Test
    public void travelerWithMajorityLeaderDepartmentHeadSkipsDepartmentHead() {
        assertEquals(ReviewPolicyType.SKIP_DEPARTMENT_HEAD,
                ReviewPolicySelector.selectPolicyType(NO_ROLES, roles(TravelRole.MAJORITY_LEADER)));
    }

    @Test
    public void travelAdminAndSecretaryUseTheirSingleRolePolicies() {
        assertEquals(ReviewPolicyType.SECRETARY_ONLY,
                ReviewPolicySelector.selectPolicyType(roles(TravelRole.TRAVEL_ADMIN), NO_ROLES));
        assertEquals(ReviewPolicyType.TRAVEL_ADMIN_ONLY,
                ReviewPolicySelector.selectPolicyType(roles(TravelRole.SECRETARY_OF_THE_SENATE), NO_ROLES));
    }

    private static TravelRoles roles(TravelRole... roles) {
        return new TravelRoles(List.of(roles), List.of());
    }
}
