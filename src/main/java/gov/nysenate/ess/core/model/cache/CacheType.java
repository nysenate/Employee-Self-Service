package gov.nysenate.ess.core.model.cache;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum CacheType {
    // TODO: TIME THESE, vs originals. and pseudo-caches
    EMPLOYEE, ACCRUAL_ANNUAL, TRANSACTION,
    SUPERVISOR_EMP_GROUP, ACTIVE_TIME_RECORDS
}
