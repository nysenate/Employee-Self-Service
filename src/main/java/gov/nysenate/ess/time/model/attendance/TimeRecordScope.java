package gov.nysenate.ess.time.model.attendance;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public enum TimeRecordScope
{
    EMPLOYEE("E"),
    SUPERVISOR("S"),
    PERSONNEL("P");

    private final String code;

    TimeRecordScope(String code) {
        this.code = code;
    }

    public Set<TimeRecordStatus> getStatuses() {
        return switch (this) {
            case EMPLOYEE -> TimeRecordStatus.unlockedForEmployee();
            case SUPERVISOR -> TimeRecordStatus.unlockedForSupervisor();
            case PERSONNEL -> TimeRecordStatus.unlockedForPersonnel();
        };
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