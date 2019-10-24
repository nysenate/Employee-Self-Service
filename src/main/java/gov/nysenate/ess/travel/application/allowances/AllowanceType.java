package gov.nysenate.ess.travel.application.allowances;

/**
 * Allowance Types that the traveler enters a value for.
 */
public enum AllowanceType {
    TOLLS,
    PARKING,
    TRAIN_AND_PLANE,
    ALTERNATE_TRANSPORTATION, // Transportation not included by other types. Such as taxi, bus, subway.
    REGISTRATION
}
