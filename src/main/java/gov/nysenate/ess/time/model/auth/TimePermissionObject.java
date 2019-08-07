package gov.nysenate.ess.time.model.auth;

/**
 * An enumeration of data 'objects' that are restricted via the permission system
 * These enums are used to construct permissions in a standardized way
 * @see EssTimePermission
 */
public enum TimePermissionObject {
    ACCRUAL,
    ACCRUAL_ACTIVE_YEARS,
    ALLOWANCE,
    ALLOWANCE_ACTIVE_YEARS,
    ATTENDANCE_RECORDS,
    MISC_LEAVE_GRANT,
    PAYCHECK,
    SUPERVISOR,
    SUPERVISOR_EMPLOYEES,
    SUPERVISOR_OVERRIDES,
    SUPERVISOR_TIME_OFF_REQUESTS,
    SUPERVISOR_TIME_RECORDS,
    TIME_OFF_REQUEST_ACTIVE_YEARS,
    TIME_OFF_REQUESTS,
    TIME_OFF_REQUEST_REVIEW,
    TIME_RECORD_ACTIVE_YEARS,
    TIME_RECORDS,
    TIME_RECORD_NOTIFICATION
}
