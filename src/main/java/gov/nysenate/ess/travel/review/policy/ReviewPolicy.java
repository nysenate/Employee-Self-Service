package gov.nysenate.ess.travel.review.policy;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public record ReviewPolicy(ReviewPolicyId id, List<TravelRole> reviewerOrder) {

    public ReviewPolicy {
        reviewerOrder = List.copyOf(reviewerOrder);
        if (reviewerOrder.isEmpty() || reviewerOrder.getLast() != TravelRole.NONE) {
            throw new IllegalArgumentException("Review policies must end with the NONE role.");
        }
    }

    public TravelRole firstReviewer() {
        return reviewerOrder.getFirst();
    }

    public TravelRole after(TravelRole reviewerRole) {
        if (reviewerRole == TravelRole.NONE) {
            return TravelRole.NONE;
        }
        int currentIndex = reviewerOrder.indexOf(reviewerRole);
        if (currentIndex < 0 || currentIndex + 1 >= reviewerOrder.size()) {
            throw new IllegalArgumentException("Role is not a review step in policy " + id + ".");
        }
        return reviewerOrder.get(currentIndex + 1);
    }
}
