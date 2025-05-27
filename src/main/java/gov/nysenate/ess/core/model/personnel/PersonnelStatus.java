package gov.nysenate.ess.core.model.personnel;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Enumerates all possible employee statuses
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PersonnelStatus {

    /**
     * The enum name of the status is also its sfms code as seen in PL21EMPSTAT
     *                                  Employed    Time Entry  Effect Offset   CDENCLV*/
    SLHP("SICK LEAVE WITH HALF PAY",    true,       true,       0,              true),
    TRNO("TRANSFER OUT",                false,      true,       1,              false),
    ACTV("ACTIVE",                      true,       true,       0,              false),
    NONE("NONE",                        false,      true,       0,              true),
    RSGN("RESIGNED",                    false,      true,       1,              false),
    TERM("TERMINATED",                  false,      true,       1,              false),
    CHLD("CHILD CARE LEAVE",            true,       true,       0,              false),
    MILT("MILITARY LEAVE",              true,       true,       0,              false),
    RETD("RETIRED",                     false,      true,       0,              false),
    EXTS("EXTRA SERVICE",               true,       true,       0,              false),
    STUD("STUDENT",                     true,       true,       0,              false),
    DSBL("DISABLED",                    true,       true,       0,              false),
    DECD("DECEASED",                    false,      true,       1,              false),
    LWOP("LEAVE WITHOUT PAY",           true,       false,      0,              true),
    WCMP("WORKERS' COMPENSATION",       true,       true,       0,              true),
    TRNW("TRANSFER WITHIN",             true,       true,       0,              true);

    /** TODO: this field is present in the database but I'm not sure what it means */
    private final boolean CDENCLV;
    /** Brief description of personnel status */
    private final String description;
    /** Indicates whether the status applies to current employees (true) or former employees (false) */
    private final boolean employed;
    /** True iff employees with this status are required to enter timesheets */
    private final boolean timeEntryRequired;
    /**
     * Certain personnel statuses are not effective on their listed effect dates
     * but are effective according to an offset (typically 1 day after)
     */
    private final int effectDateOffset;

    PersonnelStatus(String description, boolean employed, boolean timeEntryRequired,
                    int effectDateOffset, boolean CDENCLV) {
        this.CDENCLV = CDENCLV;
        this.description = description;
        this.employed = employed;
        this.timeEntryRequired = timeEntryRequired;
        this.effectDateOffset = effectDateOffset;
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

    public int getEffectDateOffset() {
        return effectDateOffset;
    }
}
