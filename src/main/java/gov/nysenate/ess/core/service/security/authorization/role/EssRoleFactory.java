package gov.nysenate.ess.core.service.security.authorization.role;

import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Generates roles which are defined in the domain of ESS.
 * i.e. In the local postgresql database.
 */
@Service
public class EssRoleFactory implements RoleFactory {

    @Autowired private RoleDao essRoleDao;

    @Override
    public Stream<Enum> getRoles(Employee employee) {
        return Stream.concat(
                // Everyone has the role of senate employee by default.
                Stream.of(EssRole.SENATE_EMPLOYEE),
                // Get additional roles defined in the ESS database.
                essRoleDao.getRoles(employee).stream()
        );
    }
}
