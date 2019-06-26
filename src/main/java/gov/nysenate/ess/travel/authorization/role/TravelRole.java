package gov.nysenate.ess.travel.authorization.role;

public enum TravelRole {
    // The DELEGATE role is assigned to users who are a delegate.
    // This is used to disallow them from modifying delegates themselves.
    DELEGATE,
    NONE,
    SUPERVISOR,
    DEPUTY_EXECUTIVE_ASSISTANT,
    SECRETARY_OF_THE_SENATE,
    MAJORITY_LEADER;
}
