package gov.nysenate.ess.core.dao.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;

public interface RoleDao {

    /**
     * Gets a list of {@link EssRole EssRole's} for a user.
     * @param employee The {@link Employee} who's roles should be returned.
     * @return Immutable set of the users roles.
     */
    ImmutableSet<EssRole> getRoles(Employee employee);

    /**
     * Get a list of {@link Employee Employee's} with the given role.
     */
    ImmutableList<Employee> getEmployeesWithRole(EssRole role);
}
