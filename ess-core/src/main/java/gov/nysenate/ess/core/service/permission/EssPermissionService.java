package gov.nysenate.ess.core.service.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.permission.SqlRoleDao;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.permission.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.authz.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for getting a users permissions.
 * First, it gets the users roles. Then calls an instance of
 * {@link PermissionFactory} to map those roles to permissions for
 * each app in ESS.
 *
 * New Ess apps should create an implementation of {@code PermissionFactory},
 * lazily inject it, and add its permissions to the current list.
 */
@Service
public class EssPermissionService {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private SqlRoleDao roleDao;
    @Autowired @Lazy private PermissionFactory supplyPermissionFactory;

    /**
     * Get a list of {@link SenatePerson SenatePerson's} permissions across all ESS applications.
     */
    public ImmutableList<Permission> getPermissions(SenatePerson person) {
        List<Permission> permissions = new ArrayList<>();
        Employee employee = employeeInfoService.getEmployee(person.getEmployeeId());
        ImmutableList<EssRole> roles = roleDao.getRoles(employee);
        permissions.addAll(supplyPermissionFactory.getPermissions(employee, roles));
        return ImmutableList.copyOf(permissions);
    }

    public ImmutableList<Employee> getEmployeesWithRole(EssRole role) {
        return roleDao.getEmployeesWithRole(role);
    }
}
