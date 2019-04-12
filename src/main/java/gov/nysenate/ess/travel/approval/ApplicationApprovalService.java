package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationApprovalService {

    @Autowired private ApplicationApprovalDao approvalDao;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private TravelRoleFactory travelRoleFactory;

    public ApplicationApproval createApplicationApproval(TravelApplication app) {
        TravelRole travelerRole = travelRoleFactory.travelRoleForEmp(app.getTraveler()).orElse(null);
        ApplicationApproval approval = new ApplicationApproval(app, travelerRole);
        return approval;
    }

    public void saveApplicationApproval(ApplicationApproval approval) {
        approvalDao.saveApplicationApproval(approval);
    }

    // FIXME very partial implementation
    public List<ApplicationApproval> pendingApprovalsForRole(Employee employee, TravelRole role) {
        // Get all approvals requiring supervisor action. FIXME does not handle other roles
        // Supervisor object?
        List<ApplicationApproval> reqSupAction = approvalDao.selectApprovalsByNextRole(role).stream()
                .collect(Collectors.toList());

        return reqSupAction.stream()
                .filter(a ->
                        supervisorInfoService.getSupervisorIdForEmp(a.application().getTraveler().getEmployeeId(), LocalDate.now()) == employee.getEmployeeId())
                .collect(Collectors.toList());
    }
}
