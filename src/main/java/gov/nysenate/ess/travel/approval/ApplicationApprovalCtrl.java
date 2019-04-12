package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/approval")
public class ApplicationApprovalCtrl extends BaseRestApiCtrl {

    @Autowired private ApplicationApprovalService approvalService;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Get approvals which need approval by the logged in user.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getPendingApprovals() throws AuthenticationException {
        TravelRole role = subjectRole();
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        List<ApplicationApproval> pendingApprovals = approvalService.pendingApprovalsForRole(employee, role);
        return ListViewResponse.of(pendingApprovals.stream()
                .map(ApplicationApprovalView::new)
                .collect(Collectors.toList()));
    }

    private TravelRole subjectRole() throws AuthenticationException {
        TravelRole role = null;
        if (getSubject().hasRole(TravelRole.SUPERVISOR.name())) {
            role = TravelRole.SUPERVISOR;
        }
        if (getSubject().hasRole(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT.name())) {
            role = TravelRole.DEPUTY_EXECUTIVE_ASSISTANT;
        }
        if (getSubject().hasRole(TravelRole.SECRETARY_OF_THE_SENATE.name())) {
            role = TravelRole.SECRETARY_OF_THE_SENATE;
        }
        if (getSubject().hasRole(TravelRole.MAJORITY_LEADER.name())) {
            role = TravelRole.MAJORITY_LEADER;
        }

        if (role == null) {
            throw new UnauthorizedException("Missing a required role.");
        }

        return role;
    }
}
