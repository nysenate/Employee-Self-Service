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
    ENTRY_CANNOT_CHANGE_IN_SCOPE (5, "Attempt to modify Time Entry in scope without needed permissions."),
    INVALID_TIME_RECORD_SCOPE (6, "Attempt to modify Time Record with an inappropriate user scope."),


    /** Time Entry errors */
    RECORD_EXCEEDS_ALLOWANCE(20, "The saved record contains time entries that exceed the employees yearly allowance"),
    RECORD_EXCEEDS_ACCRUAL(21, "The saved record contains time entries that exceed the employees accrual"),
    INVALID_HOURLY_INCREMENT(22, "The saved record contains time entries with invalid increments."),
    MISSING_MISC_TYPE(23, "The saved record contains misc hours without comments."),
    MISSING_MISC_HOURS(24, "The saved record contains misc comments without misc hours."),
    TOTAL_LESS_THAN_ZERO(25, "The saved record contains daily totals less than 0."),
    TOTAL_GREATER_THAN_TWENTYFOUR(26, "The saved record contains daily totals greater than 24."),
    FIELD_LESS_THAN_ZERO(27, "The saved record contains a field with a value less than 0."),
    FIELD_GREATER_THAN_MAX(28, "The saved record contains a field with a value greater than the max value."),
    DATE_OUT_OF_RANGE(29, "The saved record contains an entry date out of range of the electronic T&A record."),
    NULL_DATE(30, "The saved record contains a blank entry date."),
    DUPLICATE_DATE(31, "The saved record contains a duplicate entry date."),
    TIME_REC_ID_CHANGED(32, "The Time Record ID cannot be changed."),
    INACTIVE_TIME_RECORD(33, "The Time Record cannot be inactivated."),
    EMP_REC_ID_CHANGED(34, "The Employee ID cannot be changed."),
    ENTRY_ID_CHANGED (35, "The Entry ID cannot be changed."),
    ENTRY_ID_REMOVED (36, "The Entry ID cannot be removed."),
    ENTRY_ID_ADDED (37, "A new Entry ID cannot be added."),
    ENTRY_CHANGED (38, "The Entry cannot be changed."),
    DUPLICATE_ENTRY_ID (40, "A duplicate Entry ID was found."),
    DUPLICATE_ENTRY (41, "A duplicate Entry was found."),
    PAYTYPE_CHANGED (42, "Paytype has changed."),
    ORIGIN_DATE_CHANGED (44, "Origin Date has changed."),
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
