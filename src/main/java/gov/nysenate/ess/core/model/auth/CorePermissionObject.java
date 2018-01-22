package gov.nysenate.ess.core.model.auth;

/**
 * An enumeration of data 'objects' that are restricted via the permission system
 * These enums are used to construct permissions in a standardized way
 * @see CorePermission
 */
public enum CorePermissionObject {
    EMPLOYEE_INFO,
    TRANSACTION_HISTORY,
    ACKNOWLEDGMENT,
}
