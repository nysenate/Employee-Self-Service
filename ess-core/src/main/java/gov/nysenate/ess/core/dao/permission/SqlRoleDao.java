package gov.nysenate.ess.core.dao.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.permission.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.stereotype.Repository;

@Repository
public class SqlRoleDao implements RoleDao {

    /** {@inheritDoc}
     * @param employee*/
    public ImmutableList<EssRole> getRoles(Employee employee) {
        return ImmutableList.of(EssRole.SENATE_EMPLOYEE, EssRole.SUPPLY_EMPLOYEE, EssRole.SUPPLY_MANAGER);
    }
}
