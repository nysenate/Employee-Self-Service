package gov.nysenate.ess.core.service.security.authorization.role;

import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.stream.Stream;

/**
 * Contains logic to assign roles to employees.
 */
public interface RoleFactory {
    /**
     * Returns the roles for a given employee.
     * Roles should be an Enum. For example, {@link EssRole}.
     * @param employee
     * @return
     */
    Stream<Enum<?>> getRoles(Employee employee);
}
