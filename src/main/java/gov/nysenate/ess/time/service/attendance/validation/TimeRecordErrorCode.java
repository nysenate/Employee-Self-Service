package gov.nysenate.ess.time.service.attendance.validation;

import java.util.Arrays;
import java.util.Optional;

public enum TimeRecordErrorCode {

    /** Serious errors */
    // These will indicate either a problem with the server/front end code, or someone up to no good
    NO_EXISTING_RECORD(1, "Attempt to save a record with no existing record."),
    UNAUTHORIZED_MODIFICATION(2, "Attempt to modify time record fields that are not open for modification by users"),
    INVALID_STATUS_CHANGE(3, "Attempt to change time record status in violation of time record life cycle"),
    PREVIOUS_UNSUBMITTED_RECORD(4, "Attempt to submit record with prior unsubmitted salaried record"),
    NON_EMPLOYEE_MODIFICATION(6, "Attempt to modify the record of an employee as a supervisor or personnel"),
    INVALID_TIME_RECORD_SCOPE (6, "Attempt to modify Time Record with an inappropriate user scope."),
    UNAUTHORIZED_ENTRY_MODIFICATION(7, "Attempt to modify time entry fields that are not open for modification by users"),
    DATE_OUT_OF_RANGE(8, "The saved record contains an entry date out of range of the electronic T&A record."),
    NULL_DATE(9, "The saved record contains a blank entry date."),
    TIME_ENTRY_DATE_DISCREPANCY(10, "Time entry dates do not match up"),

    /** Accrual errors */
    RECORD_EXCEEDS_ALLOWANCE(20, "The saved record contains time entries that exceed the employees yearly allowance"),
    RECORD_EXCEEDS_ACCRUAL(21, "The saved record contains time entries that exceed the employees accrual"),

    /** Time Entry errors */
    INVALID_HOURLY_INCREMENT(22, "The saved record contains time entries with invalid increments."),
    MISSING_MISC_TYPE(23, "The saved record contains misc hours without comments."),
    MISSING_MISC_HOURS(24, "The saved record contains misc comments without misc hours."),
    TOTAL_LESS_THAN_ZERO(25, "The saved record contains daily totals less than 0."),
    TOTAL_GREATER_THAN_TWENTYFOUR(26, "The saved record contains daily totals greater than 24."),
    FIELD_LESS_THAN_ZERO(27, "The saved record contains a field with a value less than 0."),
    FIELD_GREATER_THAN_MAX(28, "The saved record contains a field with a value greater than the max value."),
    ;

    private int code;
    private String message;

    TimeRecordErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Optional<TimeRecordErrorCode> getErrorByCode(int code) {
        return Arrays.stream(TimeRecordErrorCode.values())
                .filter(error -> error.getCode() == code)
                .findAny();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
