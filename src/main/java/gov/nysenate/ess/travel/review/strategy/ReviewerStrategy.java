package gov.nysenate.ess.travel.review.strategy;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

import static gov.nysenate.ess.travel.authorization.role.TravelRole.*;

/**
 * Reviewer strategies define the chain of approval necessary for a particular type of traveler.
 */
public interface ReviewerStrategy {

    List<TravelRole> order();

    default TravelRole after(TravelRole lastReviewerRole) {
        if (lastReviewerRole == null) {
            return order().get(0);
        }
        if (lastReviewerRole == NONE) {
            return NONE;
        }
        if (!order().contains(lastReviewerRole)) {
            throw new IllegalArgumentException("Invalid last reviewer role for strategy.");
        }

        return order().get(order().indexOf(lastReviewerRole) + 1);
    }

    public static ReviewerStrategy getStrategy(TravelRole role, boolean isSenator) {
        if (isSenator) {
            throw new IllegalStateException("Senators are not allowed to travel");
            //return new SenatorReviewerStrategy();
        }
        return switch (role) {
            case DEPARTMENT_HEAD -> new DepartmentHeadReviewerStrategy();
            case TRAVEL_ADMIN -> new TravelAdminReviewerStrategy();
            case MAJORITY_LEADER -> new MajReviewerStrategy();
            case SECRETARY_OF_THE_SENATE -> new SosReviewerStrategy();
            default -> new DefaultReviewerStrategy();
        };
    }
}
