package gov.nysenate.ess.travel.review.policy;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewPolicySelector {

    private static final int CURRENT_POLICY_VERSION = 1;

    private final TravelRoleFactory travelRoleFactory;
    private final EmployeeInfoService employeeInfoService;
    private final ReviewPolicyRegistry policyRegistry;

    @Autowired
    public ReviewPolicySelector(TravelRoleFactory travelRoleFactory,
                                EmployeeInfoService employeeInfoService,
                                ReviewPolicyRegistry policyRegistry) {
        this.travelRoleFactory = travelRoleFactory;
        this.employeeInfoService = employeeInfoService;
        this.policyRegistry = policyRegistry;
    }

    /**
     * Selects and snapshots the policy for a newly submitted application.
     * Existing reviews must be loaded through {@link ReviewPolicyRegistry}.
     */
    public ReviewPolicy selectFor(TravelApplication application) {
        var travelerRoles = travelRoleFactory.travelRolesForEmp(application.getTraveler());
        var departmentHeadRoles = travelRoleFactory.travelRolesForEmp(
                employeeInfoService.getEmployee(application.getTravelerDeptHeadEmpId()));

        var type = selectPolicyType(travelerRoles, departmentHeadRoles);
        return policyRegistry.resolve(type, CURRENT_POLICY_VERSION);
    }

    static ReviewPolicyType selectPolicyType(TravelRoles travelerRoles, TravelRoles departmentHeadRoles) {
        if (departmentHeadRoles.hasPrimaryRole(TravelRole.MAJORITY_LEADER)) {
            return ReviewPolicyType.SKIP_DEPARTMENT_HEAD;
        }
        return switch (travelerRoles.apex()) {
            case DEPARTMENT_HEAD, MAJORITY_LEADER -> ReviewPolicyType.SKIP_DEPARTMENT_HEAD;
            case TRAVEL_ADMIN -> ReviewPolicyType.SECRETARY_ONLY;
            case SECRETARY_OF_THE_SENATE -> ReviewPolicyType.TRAVEL_ADMIN_ONLY;
            default -> ReviewPolicyType.STANDARD;
        };
    }
}
