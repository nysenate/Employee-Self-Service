package gov.nysenate.ess.travel.review.strategy;


import com.google.common.collect.Lists;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public class RegularReviewerStrategy implements ReviewerStrategy {

    private static final List<TravelRole> order = Lists.newArrayList(
            TravelRole.SUPERVISOR,
            TravelRole.DEPUTY_EXECUTIVE_ASSISTANT,
            TravelRole.SECRETARY_OF_THE_SENATE,
            TravelRole.NONE);

    @Override
    public List<TravelRole> order() {
        return order;
    }
}
