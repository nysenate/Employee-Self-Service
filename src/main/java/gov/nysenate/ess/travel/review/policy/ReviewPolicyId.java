package gov.nysenate.ess.travel.review.policy;

public record ReviewPolicyId(ReviewPolicyType type, int version) {

    public ReviewPolicyId {
        if (version < 1) {
            throw new IllegalArgumentException("Review policy version must be positive.");
        }
    }
}
