package gov.nysenate.ess.travel;

/**
 * Common events senate staff travel to.
 */
public enum EventType {
    PUBLIC_HEARING("Public Hearing"),
    ROUND_TABLE("Round Table"),
    FORUM("Forum"),
    OTHER("Other");

    private String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
