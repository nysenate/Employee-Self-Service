package gov.nysenate.ess.core.model.auth;

/**
 * Encapsulates the basic information about an employee that will allow for linking to an employee's records
 * in SFMS.
 */
public interface SenatePerson
{
    public String getUid();

    public Integer getEmployeeId();

    public String getFullName();

    public String getEmail();
}