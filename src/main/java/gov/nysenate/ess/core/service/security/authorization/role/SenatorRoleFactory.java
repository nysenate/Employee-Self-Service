package gov.nysenate.ess.core.service.security.authorization.role;

import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Adds the SENATOR role if an employee is a senator.
 */
@Service
public class SenatorRoleFactory implements RoleFactory {

    @Override
    public Stream<Enum<?>> getRoles(Employee employee) {
        if (employee.isSenator()) {
            return Stream.of(EssRole.SENATOR);
        }
        return Stream.empty();
    }
}
