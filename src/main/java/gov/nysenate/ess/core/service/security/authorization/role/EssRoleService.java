package gov.nysenate.ess.core.service.security.authorization.role;

import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * Handles all instances of {@link RoleFactory} to generate a complete list of roles for an employee.
 */
@Service
public class EssRoleService {

    @Autowired private List<RoleFactory> roleFactories;

    /**
     * Get a stream of roles for an employee
     */
    public Stream<Enum<?>> getRoles(Employee employee) {
        return roleFactories.stream()
                .flatMap(rf -> rf.getRoles(employee));
    }
}
