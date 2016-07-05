package gov.nysenate.ess.core.dao.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.permission.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;

public interface RoleDao {

    /**
     * Gets a list of {@link EssRole EssRole's} for a user.
     * @param employee The {@link Employee} who's roles should be returned.
     * @return Immutable list of the users roles.
     */
    ImmutableList<EssRole> getRoles(Employee employee);
}
