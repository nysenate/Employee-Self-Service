package gov.nysenate.ess.seta.model.attendance;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.EnumSet;
import java.util.Set;

/**
 * The TimeRecordStatus enum represents the possible states that a time record can be in.
 */
public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", TimeRecordScope.SUPERVISOR),
    NOT_SUBMITTED("W","Not Submitted", TimeRecordScope.EMPLOYEE),
    APPROVED("A","Approved by Supervisor", TimeRecordScope.PERSONNEL),
    DISAPPROVED("D","Disapproved by Supervisor", TimeRecordScope.EMPLOYEE),
    SUBMITTED_PERSONNEL("SP","Submitted to Personnel", TimeRecordScope.PERSONNEL),
    APPROVED_PERSONNEL("AP","Approved by Personnel", TimeRecordScope.PERSONNEL),
    DISAPPROVED_PERSONNEL("DP","Disapproved by Personnel", TimeRecordScope.EMPLOYEE),
    ;

    protected String code;
    protected String name;

    /** The scope indicates who can perform an action on the time record at that given stage.
     *  For example when the status is 'Submitted' the supervisor scope 'S' can only take action (i.e approve/disapprove).
     */
    protected TimeRecordScope scope;

    /** Mapping of unlockedFor values (E,S,P) to a set of corresponding time record statuses. */
    private static SetMultimap<TimeRecordScope, TimeRecordStatus> unlockedForMap = TreeMultimap.create();
    static {
        for (TimeRecordStatus trs : TimeRecordStatus.values()) {
            unlockedForMap.put(trs.scope, trs);
        }
    }

    private static final EnumSet<TimeRecordStatus> inProgress =
        EnumSet.of(SUBMITTED, NOT_SUBMITTED, APPROVED, DISAPPROVED, SUBMITTED_PERSONNEL, DISAPPROVED_PERSONNEL);

    TimeRecordStatus(String code, String name, TimeRecordScope scope) {
        this.code = code;
        this.name = name;
        this.scope = scope;
    }

    public static TimeRecordStatus valueOfCode(String code){
        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code.equals(code)) return status;
        }
        return null;
    }

    public boolean isUnlockedForEmployee() {
        return TimeRecordScope.EMPLOYEE.equals(scope);
    }

    public boolean isUnlockedForSupervisor() {
        return TimeRecordScope.SUPERVISOR.equals(scope);
    }

    public boolean isUnlockedForPersonnel() {
        return TimeRecordScope.PERSONNEL.equals(scope);
    }

    public static Set<TimeRecordStatus> getAll() {
        return EnumSet.allOf(TimeRecordStatus.class);
    }

    public static Set<TimeRecordStatus> unlockedForEmployee() {
        return unlockedForMap.get(TimeRecordScope.EMPLOYEE);
    }

    public static Set<TimeRecordStatus> unlockedForSupervisor() {
        return unlockedForMap.get(TimeRecordScope.SUPERVISOR);
    }

    public static Set<TimeRecordStatus> unlockedForPersonnel() {
        return unlockedForMap.get(TimeRecordScope.PERSONNEL);
    }

    public static Set<TimeRecordStatus> inProgress() {
        return inProgress;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public TimeRecordScope getScope() {
        return scope;
    }
}