package gov.nysenate.ess.core.service.security.authorization;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;

/**
 * A service that can perform queries on a senate departmental whitelist
 * Used primarily to determine if an employee belongs to a department that is in the whitelist
 */
public interface DepartmentalWhitelistService {

    /**
     * Indicates whether or not an employee belongs to a department on the departmental whitelist
     *
     * @param employee {@link Employee} an employee
     * @return {@link Boolean} true iff the employee is permitted via the departmental whitelist
     */
    boolean isAllowed(Employee employee);

    /**
     * @see #isAllowed(Employee)
     * An overload of {@link #isAllowed(Employee)} that will retrieve the employee from the given employee id
     *
     * @param empId {@link Integer} employee id
     * @return {@link Boolean} true iff the employee is permitted via the departmental whitelist
     */
    boolean isAllowed(int empId);

    /**
     * Get the set of departments that are on the whitelist
     * @return {@link ImmutableSet<String>} set of department names
     */
    ImmutableSet<String> getWhitelist();

    /**
     * @return {@link Boolean} if departmental restriction is enabled
     */
    boolean isRestrictionEnabled();
}
