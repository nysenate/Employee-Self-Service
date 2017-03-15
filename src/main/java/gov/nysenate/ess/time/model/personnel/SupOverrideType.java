package gov.nysenate.ess.time.model.personnel;

/**
 * Distinguishes between employee overrides where a single employee is granted to a supervisor,
 * or a supervisor override where all of a supervisor's employees are granted to a supervisor
 */
public enum SupOverrideType {
    EMPLOYEE,
    SUPERVISOR
}
