package gov.nysenate.ess.travel;

/**
 * Common events senate staff travel to.
 */
public enum EventType {
    PUBLIC_HEARING("Public Hearing", true, false),
    ROUND_TABLE("Round Table", true, false),
    FORUM("Forum", true, false),
    OTHER("Other", false, true);

    private String displayName;
    // Is the user required to provide the name of this event.
    private boolean requiresName;
    // Is the user required to provide an additional purpose description of this event.
    private boolean requiresAdditionalPurpose;

    EventType(String displayName, boolean requiresName, boolean requiresAdditionalPurpose) {
        this.displayName = displayName;
        this.requiresName = requiresName;
        this.requiresAdditionalPurpose = requiresAdditionalPurpose;
    }

    public String displayName() {
        return displayName;
    }

    public boolean requiresName() {
        return requiresName;
    }

    public boolean requiresAdditionalPurpose() {
        return requiresAdditionalPurpose;
    }
}
