package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.role.EssRoleFactory;
import gov.nysenate.ess.core.service.security.authorization.role.RoleFactory;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.delegate.DelegateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Assigns all travel roles except MAJORITY_LEADER to an employee.
 * MAJORITY_LEADER is defined in ess.user_roles and therefore is granted by the {@link EssRoleFactory}
 */
@Service
public class TravelRoleFactory implements RoleFactory {

    @Autowired private RoleDao essRoleDao;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private DelegateDao delegateDao;

    @Override
    public Stream<Enum> getRoles(Employee employee) {
        // Roles specifically assigned to this employee.
        Stream<Enum> userRoles = travelRolesForEmp(employee).roles().stream()
                .map(role -> (Enum) role);

        return userRoles;

        // TODO Delegate roles will have to be adjusted after this role refactoring.
        // Roles this employee has been assigned as a delegate.
//        Stream<Enum> delegatedRoles = Stream.of();
//        Optional<Delegate> delegate = delegateDao.delegateAssignedToEmp(employee.getEmployeeId(), LocalDate.now());
//        if (delegate.isPresent()) {
//            delegatedRoles = getRoles(delegate.get().principal());
//            delegatedRoles = Stream.concat(delegatedRoles, Stream.of(TravelRole.DELEGATE));
//        }

//        return Stream.concat(userRole, delegatedRoles);
    }

    public TravelRoles travelRolesForEmp(Employee employee) {
        List<TravelRole> roles = new ArrayList<>();

        if (supervisorInfoService.isSupervisor(employee.getEmployeeId())) {
            roles.add(TravelRole.SUPERVISOR);
        }
        if (employee.getJobTitle().equals("Deputy Executive Assistant")) {
            roles.add(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT);
        }
        if (employee.getJobTitle().equals("Secretary of the Senate")) {
            roles.add(TravelRole.SECRETARY_OF_THE_SENATE);
        }
        if (essRoleDao.getRoles(employee).contains(EssRole.MAJORITY_LEADER)) {
            roles.add(TravelRole.MAJORITY_LEADER);
        }
        // TODO Get list of delegate roles

        return new TravelRoles(roles, new ArrayList<>());
    }
}
