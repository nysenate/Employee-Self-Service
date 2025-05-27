package gov.nysenate.ess.core.service.security.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(EssPermissionService.class);

    @Autowired private List<PermissionFactory> permissionFactories;

    /**
     * Get a list of an employee's permissions across all ESS applications
     */
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum<?>> roles) {
        return permissionFactories.stream()
                .map(pFactory -> pFactory.getPermissions(employee, roles))
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }
}
