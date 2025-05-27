package gov.nysenate.ess.core.model.cache;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * have functionality to warm up upon request.
 */
public enum CacheType {
    ACCRUAL_ANNUAL, ACTIVE_EMPLOYEE_IDS, ACTIVE_TIME_RECORDS, EMPLOYEE,
    HOLIDAY, LOCATION, PAY_PERIOD, PERSONNEL_TASK, SUPERVISOR_EMP_GROUP, TRANSACTION
}
