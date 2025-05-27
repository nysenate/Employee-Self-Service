package gov.nysenate.ess.travel.request.app;

public enum AppStatus {
    /* Not Applicable indicates a travel v1.0 application. */
    NOT_APPLICABLE("Not Applicable"),
    DRAFT("Draft"),
    DEPARTMENT_HEAD("Department Head"),
    TRAVEL_UNIT("Travel Unit"),
    APPROVED("Approved"),
    DISAPPROVED("Disapproved"),
    CANCELED("Canceled")
    ;

    private String label;

    AppStatus(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}
