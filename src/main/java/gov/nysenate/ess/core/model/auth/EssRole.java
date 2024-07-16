package gov.nysenate.ess.core.model.auth;

/**
 * Roles managed in the ess.user_roles table.
 */
public enum EssRole {
    /* General */
    ADMIN,
    SENATE_EMPLOYEE,
    SENATOR,
    TIMEOUT_EXEMPT,
    /* MyInfo */
    PERSONNEL_COMPLIANCE_MANAGER,
    /* Supply */
    SUPPLY_EMPLOYEE,
    SUPPLY_MANAGER,
    SUPPLY_REPORTER,
    /* Time */
    TIME_MANAGER,
    /* Travel */
    MAJORITY_LEADER,
    TRAVEL_ADMIN,
    SECRETARY_OF_SENATE,
    ;
}
