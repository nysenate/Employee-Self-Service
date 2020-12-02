package gov.nysenate.ess.travel.authorization.role;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.role.RoleFactory;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.delegate.Delegation;
import gov.nysenate.ess.travel.delegate.DelegationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Assigns all travel roles to an employee.
 * <p>
 * The TravelRole.MAJORITY_LEADER is assigned if the employee is assigned the EssRole.MAJORITY_LEADER
 * role from the ess.user_roles table.
 */
@Service
public class TravelRoleFactory implements RoleFactory {

    @Autowired private RoleDao essRoleDao;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private DelegationDao delegateDao;

    @Override
    public Stream<Enum> getRoles(Employee employee) {
        // Add TravelRole.DELEGATE role if user has delegated roles.
        TravelRoles roles = travelRolesForEmp(employee);
        if (!roles.delegate().isEmpty()) {
            ImmutableList<TravelRole> delegatedRoles = new ImmutableList.Builder<TravelRole>()
                    .addAll(roles.delegate())
                    .add(TravelRole.DELEGATE)
                    .build();
            roles = new TravelRoles(roles.primary(), delegatedRoles);
        }

        return roles.all().stream()
                .map(role -> (Enum) role);
    }

    /**
     * Get the travel roles for an employee.
     * TravelRole.NONE and TravelRole.DELEGATE are excluded from these results.
     *
     * @param employee
     * @return
     */
    public TravelRoles travelRolesForEmp(Employee employee) {
        List<TravelRole> primaryRoles = primaryRoles(employee);
        List<TravelRole> delegateRoles = delegateRoles(employee);

        return new TravelRoles(primaryRoles, delegateRoles);
    }

    private List<TravelRole> primaryRoles(Employee employee) {
        List<TravelRole> r = new ArrayList<>();
        Set<EssRole> empRoles = essRoleDao.getRoles(employee);

        if (supervisorInfoService.isSupervisor(employee.getEmployeeId())) {
            r.add(TravelRole.SUPERVISOR);
        }
        if (empRoles.contains(EssRole.TRAVEL_ADMIN)) {
            r.add(TravelRole.TRAVEL_ADMIN);
        }
        if (empRoles.contains(EssRole.SECRETARY_OF_SENATE)) {
            r.add(TravelRole.SECRETARY_OF_THE_SENATE);
        }
        if (empRoles.contains(EssRole.MAJORITY_LEADER)) {
            r.add(TravelRole.MAJORITY_LEADER);
        }

        return r;
    }

    private List<TravelRole> delegateRoles(Employee employee) {
        // Roles this employee has been assigned as a delegate.
        List<TravelRole> delegatedRoles = new ArrayList<>();
        List<Delegation> delegations = delegateDao.findByDelegateEmpId(employee.getEmployeeId());
        List<Delegation> active = delegations.stream().filter(Delegation::isActive).collect(Collectors.toList());

        for (Delegation d : active) {
            delegatedRoles.addAll(primaryRoles(d.principal()));
        }

        return delegatedRoles;
    }

}
