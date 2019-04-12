package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.role.RoleFactory;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TravelRoleFactory implements RoleFactory {

    @Autowired private SupervisorInfoService supervisorInfoService;

    @Override
    public Stream<Enum> getRoles(Employee employee) {
        return travelRoleForEmp(employee)
                .map(role -> (Enum) role)
                .map(Stream::of)
                .orElse(Stream.empty());
    }

    /**
     * An employee is only granted a single travel role. If they qualifty for multiple they will be granted
     * the 'highest' in the hierarchy.
     *
     * @return The TravelRole for an employee.
     */
    public Optional<TravelRole> travelRoleForEmp(Employee employee) {
        TravelRole role = null;
        if (supervisorInfoService.isSupervisor(employee.getEmployeeId())) {
            role = TravelRole.SUPERVISOR;
        }
        if (employee.getJobTitle().equals("Deputy Executive Assistant")) {
            role = TravelRole.DEPUTY_EXECUTIVE_ASSISTANT;
        }
        if (employee.getJobTitle().equals("Secretary of the Senate")) {
            role = TravelRole.SECRETARY_OF_THE_SENATE;
        }
        if (employee.getEmployeeId() == 8944) {
            role = TravelRole.MAJORITY_LEADER;
        }
        return Optional.ofNullable(role);
    }
}
