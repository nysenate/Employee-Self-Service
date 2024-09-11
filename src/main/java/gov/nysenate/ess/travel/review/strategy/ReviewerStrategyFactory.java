package gov.nysenate.ess.travel.review.strategy;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewerStrategyFactory {

    private TravelRoleFactory travelRoleFactory;
    private EmployeeInfoService employeeInfoService;

    @Autowired
    public ReviewerStrategyFactory(TravelRoleFactory travelRoleFactory, EmployeeInfoService employeeInfoService) {
        this.travelRoleFactory = travelRoleFactory;
        this.employeeInfoService = employeeInfoService;
    }

    /**
     * Returns the appropriate {@link ReviewerStrategy} for the given {@link TravelApplication app}.
     */
    @NotNull
    public ReviewerStrategy createStrategy(TravelApplication app) {
        TravelRoles travelerRoles = travelRoleFactory.travelRolesForEmp(app.getTraveler());
        TravelRoles deptHdRoles = travelRoleFactory.travelRolesForEmp(
                employeeInfoService.getEmployee(app.getTravelerDeptHeadEmpId()));

        if (deptHdRoles.hasPrimaryRole(TravelRole.MAJORITY_LEADER)) {
            // Special case where the traveler's department head is the Majority Leader.
            // In this case, skip approval by the Department head (Majority Leader) and go straight to the Travel Admin team.
            return new DepartmentHeadReviewerStrategy();
        }

        return switch (travelerRoles.apex()) {
            case DEPARTMENT_HEAD -> new DepartmentHeadReviewerStrategy();
            case TRAVEL_ADMIN -> new TravelAdminReviewerStrategy();
            case MAJORITY_LEADER -> new MajReviewerStrategy();
            case SECRETARY_OF_THE_SENATE -> new SosReviewerStrategy();
            default -> new DefaultReviewerStrategy();
        };
    }
}
