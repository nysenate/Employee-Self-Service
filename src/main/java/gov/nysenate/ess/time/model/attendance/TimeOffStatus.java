package gov.nysenate.ess.time.model.attendance;

public enum TimeOffStatus {
    SAVED("SAVED"),
    SUBMITTED("SUBMITTED"),
    DISAPPROVED("DISAPPROVED"),
    APPROVED("APPROVED"),
    INVALIDATED("INVALIDATED"),
    ;

    protected String name;

    TimeOffStatus(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
