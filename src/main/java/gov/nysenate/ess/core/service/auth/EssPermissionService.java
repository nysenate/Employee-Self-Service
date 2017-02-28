package gov.nysenate.ess.core.service.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.permission.SqlRoleDao;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.authz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    private static final Logger logger = LoggerFactory.getLogger(EssPermissionService.class);

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private SqlRoleDao roleDao;
    @Autowired private List<PermissionFactory> permissionFactories;

    /**
     * Get a list of {@link SenatePerson SenatePerson's} permissions across all ESS applications.
     */
    public ImmutableList<Permission> getPermissions(SenatePerson person) {
        Integer empId;
        try {
            empId = person.getEmployeeId();
        } catch (Exception ex) {
            logger.error("Could not retrieve employee id from SenatePerson: " +
                            Objects.toString(person), ex);
            return ImmutableList.of();
        }
        try {
            Employee employee = employeeInfoService.getEmployee(empId);
            final ImmutableSet<EssRole> roles = roleDao.getRoles(employee);
            return permissionFactories.stream()
                    .map(pFactory -> pFactory.getPermissions(employee, roles))
                    .flatMap(Collection::stream)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        } catch (Exception ex) {
            // Catch and log exception as we don't have a good way of handling exceptions in this context
            // This will surely generate an exception when the user attempts to make an api call
            logger.error("Error while resolving permissions for employee #" + empId, ex);
            return ImmutableList.of();
        }
    }
}
