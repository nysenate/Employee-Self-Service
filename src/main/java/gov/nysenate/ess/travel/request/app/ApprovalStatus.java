package gov.nysenate.ess.travel.request.app;

public enum ApprovalStatus {
    /* Not Applicable indicates a travel v1.0 application. */
    NOT_APPLICABLE("Not Applicable"),
    DRAFT("Draft"),
    DEPARTMENT_HEAD("Department Head"),
    TRAVEL_UNIT("Travel Unit"),
    APPROVED("Approved"),
    DISAPPROVED("Disapproved")
    ;

    private String label;

    ApprovalStatus(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}
