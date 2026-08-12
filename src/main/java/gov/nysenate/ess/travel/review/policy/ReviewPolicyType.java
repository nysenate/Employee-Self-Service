package gov.nysenate.ess.travel.review.policy;

/**
 * Identifies the review workflow selected when a travel application is submitted.
 * Names describe workflow behavior rather than the traveler's organizational role.
 */
public enum ReviewPolicyType {
    STANDARD,
    SKIP_DEPARTMENT_HEAD,
    SECRETARY_ONLY,
    TRAVEL_ADMIN_ONLY
}
