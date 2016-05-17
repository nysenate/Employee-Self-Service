package gov.nysenate.ess.core.model.personnel;

/**
 * Enumerates all possible employee statuses
 */
public enum PersonnelStatus {

    /** The enum name of the status is also its sfms code as seen in PL21EMPSTAT */
    SLHP("SICK LEAVE WITH HALF PAY", true, true, true),
    TRNO("TRANSFER OUT", false, true, false),
    ACTV("ACTIVE", true, true, false),
    NONE("NONE", false, true, true),
    RSGN("RESIGNED", false, true, false),
    TERM("TERMINATED", false, true, false),
    CHLD("CHILD CARE LEAVE", true, true, false),
    MILT("MILITARY LEAVE", true, true, false),
    RETD("RETIRED", false, true, false),
    EXTS("EXTRA SERVICE", true, true, false),
    STUD("STUDENT", true, true, false),
    DSBL("DISABLED", true, true, false),
    DECD("DECEASED", false, true, false),
    LWOP("LEAVE WITHOUT PAY", true, false, true),
    WCMP("WORKERS' COMPENSATION", true, true, true),
    TRNW("TRANSFER WITHIN", true, true, true),
    ;

    /** TODO: this field is present in the database but I'm not sure what it means */
    private boolean CDENCLV;
    /** Brief description of personnel status */
    private String description;
    /** Indicates whether the status applies to current employees (true) or former employees (false) */
    private boolean employed;
    /** True iff employees with this status are required to enter timesheets */
    private boolean timeEntryRequired;

    PersonnelStatus(String description, boolean employed, boolean timeEntryRequired, boolean CDENCLV) {
        this.CDENCLV = CDENCLV;
        this.description = description;
        this.employed = employed;
        this.timeEntryRequired = timeEntryRequired;
    }

    public boolean isCDENCLV() {
        return CDENCLV;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEmployed() {
        return employed;
    }

    public boolean isTimeEntryRequired() {
        return timeEntryRequired;
    }
}
