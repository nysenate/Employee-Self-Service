package gov.nysenate.ess.core.service.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.permission.SqlRoleDao;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.authz.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for getting a users permissions.
 * First, it gets the users roles. Then calls an instance of
 * {@link PermissionFactory} to map those roles to permissions for
 * each app in ESS.
 *
 * New Ess apps should create an implementation of {@code PermissionFactory},
 * and inject it into spring to be included in the permission factory list in this class
 */
@Service
public class EssPermissionService {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private SqlRoleDao roleDao;
    @Autowired private List<PermissionFactory> permissionFactories;

    /**
     * Get a list of {@link SenatePerson SenatePerson's} permissions across all ESS applications.
     */
    public ImmutableList<Permission> getPermissions(SenatePerson person) {
        Employee employee = employeeInfoService.getEmployee(person.getEmployeeId());
        final ImmutableSet<EssRole> roles = roleDao.getRoles(employee);
        return permissionFactories.stream()
                .map(pFactory -> pFactory.getPermissions(employee, roles))
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public ImmutableList<Employee> getEmployeesWithRole(EssRole role) {
        return roleDao.getEmployeesWithRole(role);
    }
}
