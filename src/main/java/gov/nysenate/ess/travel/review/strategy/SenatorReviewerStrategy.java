package gov.nysenate.ess.travel.review.strategy;

import com.google.common.collect.Lists;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

/**
 * The reviewer strategy for senator travelers.
 *
 * @deprecated as of travel v2.0, senators are not to use the digital travel workflow.
 */
@Deprecated
public class SenatorReviewerStrategy implements ReviewerStrategy {

    private static final List<TravelRole> order = Lists.newArrayList(
            TravelRole.TRAVEL_ADMIN,
            TravelRole.SECRETARY_OF_THE_SENATE,
            TravelRole.MAJORITY_LEADER,
            TravelRole.NONE);

    @Override
    public List<TravelRole> order() {
        return order;
    }
}
