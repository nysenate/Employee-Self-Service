package gov.nysenate.ess.time.model.attendance;

import com.google.common.collect.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static gov.nysenate.ess.time.model.attendance.TimeRecordAction.*;
import static gov.nysenate.ess.time.model.attendance.TimeRecordScope.*;

/**
 * The TimeRecordStatus enum represents the possible states that a time record can be in.
 */
public enum TimeRecordStatus
{
    SUBMITTED("S", "Submitted", SUPERVISOR),
    NOT_SUBMITTED("W","Not Submitted", EMPLOYEE),
    APPROVED("A","Approved by Supervisor", PERSONNEL),
    DISAPPROVED("D","Disapproved by Supervisor", EMPLOYEE),
    SUBMITTED_PERSONNEL("SP","Submitted to Personnel", PERSONNEL),
    APPROVED_PERSONNEL("AP","Approved by Personnel", PERSONNEL),
    DISAPPROVED_PERSONNEL("DP","Disapproved by Personnel", EMPLOYEE),
    ;

    private final String code;
    private final String name;

    /** The scope indicates who can perform an action on the time record at that given stage.
     *  For example when the status is 'Submitted' the supervisor scope 'S' can only take action (i.e approve/disapprove).
     */
    private final TimeRecordScope scope;

    TimeRecordStatus(String code, String name, TimeRecordScope scope) {
        this.code = code;
        this.name = name;
        this.scope = scope;
    }

    /** --- Functional Getters --- */

    public boolean isUnlockedForEmployee() {
        return TimeRecordScope.EMPLOYEE.equals(scope);
    }

    public boolean isUnlockedForSupervisor() {
        return TimeRecordScope.SUPERVISOR.equals(scope);
    }

    public boolean isUnlockedForPersonnel() {
        return TimeRecordScope.PERSONNEL.equals(scope);
    }

    /**
     * Get the resulting status when the given action is applied to this status
     * @param action TimeRecordAction - an action applied to this status
     * @return TimeRecordStatus - the resulting status
     * @throws InvalidTimeRecordActionEx if the given action cannot apply to this status
     */
    public TimeRecordStatus getResultingStatus(TimeRecordAction action) {
        if (!stateTable.contains(scope, action)) {
            throw new InvalidTimeRecordActionEx(this, action);
        }
        return stateTable.get(scope, action);
    }

    /**
     * @return {@link ImmutableSet<TimeRecordAction>} actions that are valid for this status
     */
    public ImmutableSet<TimeRecordAction> getValidActions() {
        return stateTable.row(this.scope).keySet();
    }

    /** --- Getters --- */

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public TimeRecordScope getScope() {
        return scope;
    }

    /** --- Static Variables --- */

    /**
     * Mapping of unlockedFor values (E,S,P) to a set of corresponding time record statuses.
     */
    private static final ImmutableSetMultimap<TimeRecordScope, TimeRecordStatus> unlockedForMap =
            ImmutableSetMultimap.<TimeRecordScope, TimeRecordStatus>builder()
                    .putAll(Multimaps.index(Arrays.asList(TimeRecordStatus.values()), TimeRecordStatus::getScope))
                    .build();

    /**
     * A table that describes how a time record can change statuses within this app
     * Each row is a time record scope (that encompasses several time record statuses)
     * Each column is an action that can be taken on a time record scope row
     * Each value is the resulting status when the action is applied
     */
    private static final ImmutableTable<TimeRecordScope, TimeRecordAction, TimeRecordStatus> stateTable =
            ImmutableTable.<TimeRecordScope, TimeRecordAction, TimeRecordStatus>builder()
                    .put(EMPLOYEE,      SAVE,   NOT_SUBMITTED)
                    .put(EMPLOYEE,      SUBMIT, SUBMITTED)
                    .put(SUPERVISOR,    SUBMIT, APPROVED)
                    .put(SUPERVISOR,    REJECT, DISAPPROVED)
                    .build();

    /** Set of Statuses that are "in progress" i.e. not finalized*/
    private static final EnumSet<TimeRecordStatus> inProgress =
            EnumSet.of(SUBMITTED, NOT_SUBMITTED, APPROVED, DISAPPROVED, SUBMITTED_PERSONNEL, DISAPPROVED_PERSONNEL);

    /** --- Static Methods --- */

    public static TimeRecordStatus valueOfCode(String code){
        for (TimeRecordStatus status : TimeRecordStatus.values()) {
            if (status.code.equals(code)) return status;
        }
        return null;
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
}