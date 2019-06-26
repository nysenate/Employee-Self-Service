package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.role.RoleFactory;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.delegate.Delegate;
import gov.nysenate.ess.travel.delegate.DelegateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TravelRoleFactory implements RoleFactory {

    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private DelegateDao delegateDao;

    @Override
    public Stream<Enum> getRoles(Employee employee) {
        // Roles specifically assigned to this employee.
        Stream<Enum> userRole = travelRoleForEmp(employee)
                .map(role -> (Enum) role)
                .map(Stream::of)
                .orElse(Stream.empty());

        // Roles this employee has been assigned as a delegate.
        Stream<Enum> delegatedRoles = Stream.of();
        Optional<Delegate> delegate = delegateDao.delegateAssignedToEmp(employee.getEmployeeId(), LocalDate.now());
        if (delegate.isPresent()) {
            delegatedRoles = getRoles(delegate.get().principal());
            delegatedRoles = Stream.concat(delegatedRoles, Stream.of(TravelRole.DELEGATE));
        }

        return Stream.concat(userRole, delegatedRoles);
    }

    /**
     * An employee is only granted a single travel role. If they qualify for multiple they will be granted
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
