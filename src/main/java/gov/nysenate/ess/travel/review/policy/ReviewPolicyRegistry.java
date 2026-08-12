package gov.nysenate.ess.travel.review.policy;

import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReviewPolicyRegistry {

    private static final int VERSION_ONE = 1;

    private final Map<ReviewPolicyId, ReviewPolicy> policies = Map.of(
            id(ReviewPolicyType.STANDARD), policy(ReviewPolicyType.STANDARD,
                    TravelRole.DEPARTMENT_HEAD,
                    TravelRole.TRAVEL_ADMIN,
                    TravelRole.SECRETARY_OF_THE_SENATE,
                    TravelRole.NONE),
            id(ReviewPolicyType.SKIP_DEPARTMENT_HEAD), policy(ReviewPolicyType.SKIP_DEPARTMENT_HEAD,
                    TravelRole.TRAVEL_ADMIN,
                    TravelRole.SECRETARY_OF_THE_SENATE,
                    TravelRole.NONE),
            id(ReviewPolicyType.SECRETARY_ONLY), policy(ReviewPolicyType.SECRETARY_ONLY,
                    TravelRole.SECRETARY_OF_THE_SENATE,
                    TravelRole.NONE),
            id(ReviewPolicyType.TRAVEL_ADMIN_ONLY), policy(ReviewPolicyType.TRAVEL_ADMIN_ONLY,
                    TravelRole.TRAVEL_ADMIN,
                    TravelRole.NONE)
    );

    public ReviewPolicy resolve(ReviewPolicyType type, int version) {
        var id = new ReviewPolicyId(type, version);
        var policy = policies.get(id);
        if (policy == null) {
            throw new IllegalArgumentException("Unsupported review policy: " + id);
        }
        return policy;
    }

    private static ReviewPolicyId id(ReviewPolicyType type) {
        return new ReviewPolicyId(type, VERSION_ONE);
    }

    private static ReviewPolicy policy(ReviewPolicyType type, TravelRole... order) {
        return new ReviewPolicy(id(type), List.of(order));
    }
}
