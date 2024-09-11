package gov.nysenate.ess.travel.review.strategy;

import com.google.common.collect.Lists;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public class SosReviewerStrategy implements ReviewerStrategy {

    private static final List<TravelRole> order = Lists.newArrayList(
            TravelRole.TRAVEL_ADMIN,
            TravelRole.NONE);

    @Override
    public List<TravelRole> order() {
        return order;
    }
}
