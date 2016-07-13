package gov.nysenate.ess.seta.model.auth;

/**
 * An enumeration of data 'objects' that are restricted via the permission system
 * These enums are used to construct permissions in a standardized way
 * @see EssTimePermission
 */
public enum TimePermissionObject {
    ACCRUAL,
    ALLOWANCE,
    ATTENDANCE_RECORDS,
    MISC_LEAVE_GRANT,
    PAYCHECK,
    SUPERVISOR,
    SUPERVISOR_EMPLOYEES,
    SUPERVISOR_OVERRIDES,
    SUPERVISOR_TIME_RECORDS,
    TIME_RECORD_ACTIVE_YEARS,
    TIME_RECORDS,
}
