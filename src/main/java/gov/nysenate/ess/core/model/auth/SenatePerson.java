package gov.nysenate.ess.core.model.auth;

/**
 * Encapsulates the basic information about an employee that will allow for linking to an employee's records
 * in SFMS.
 */
public interface SenatePerson
{
    String getUid();

    Integer getEmployeeId();

    String getFullName();

    String getEmail();
}