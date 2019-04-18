package gov.nysenate.ess.travel.approval.reviewer;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

/**
 * Reviewer strategies define the chain of approval necessary for a particular type of traveler.
 */
public interface ReviewerStrategy {

    List<TravelRole> order();

    default TravelRole after(TravelRole lastReviewerRole) {
        if (lastReviewerRole == null) {
            return order().get(0);
        }
        if (lastReviewerRole == TravelRole.NONE) {
            return TravelRole.NONE;
        }
        if (!order().contains(lastReviewerRole)) {
            throw new IllegalArgumentException("Invalid last reviewer role for strategy.");
        }

        return order().get(order().indexOf(lastReviewerRole) + 1);
    }
}
