package gov.nysenate.ess.seta.model.attendance;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public enum TimeRecordScope
{
    EMPLOYEE("E"),
    SUPERVISOR("S"),
    PERSONNEL("P")
    ;

    private String code;

    TimeRecordScope(String code) {
        this.code = code;
    }

    public Set<TimeRecordStatus> getStatuses() {
        switch (this) {
            case EMPLOYEE: return TimeRecordStatus.unlockedForEmployee();
            case SUPERVISOR: return TimeRecordStatus.unlockedForSupervisor();
            case PERSONNEL: return TimeRecordStatus.unlockedForPersonnel();
            default: return Sets.newHashSet();
        }
    }

    public String getCode() {
        return code;
    }

    public static TimeRecordScope getScopeFromCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("Code cannot be blank");
        }
        code = code.toUpperCase().trim();
        switch (code) {
            case "E":
                return EMPLOYEE;
            case "S":
                return SUPERVISOR;
            case "P":
                return PERSONNEL;
            default:
                throw new IllegalArgumentException("Code did not match nay time record scopes.");
        }
    }
}