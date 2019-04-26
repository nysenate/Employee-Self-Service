package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationApprovalService {

    @Autowired private ApplicationApprovalDao approvalDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private TravelRoleFactory travelRoleFactory;

    // TODO include notes
    // TODO Transactional
    public void approveApplication(ApplicationApproval applicationApproval, Employee approver, TravelRole approverRole) {
        Action approvalAction = new Action(0, approver, approverRole, ActionType.APPROVE, "", LocalDateTime.now());
        applicationApproval.addAction(approvalAction);
        saveApplicationApproval(applicationApproval);

        if (applicationApproval.nextReviewerRole() == TravelRole.NONE) {
            applicationApproval.application().approve();
            travelApplicationService.saveTravelApplication(applicationApproval.application());
        }
    }

    public void disapproveApplication(ApplicationApproval applicationApproval, Employee approver, TravelRole approverRole) {
        Action approvalAction = new Action(0, approver, approverRole, ActionType.DISAPPROVE, "", LocalDateTime.now());
        applicationApproval.addAction(approvalAction);
        saveApplicationApproval(applicationApproval);

        applicationApproval.application().disapprove();
        travelApplicationService.saveTravelApplication(applicationApproval.application());
    }

    public ApplicationApproval createApplicationApproval(TravelApplication app) {
        TravelRole travelerRole = travelRoleFactory.travelRoleForEmp(app.getTraveler()).orElse(TravelRole.NONE);
        ApplicationApproval approval = new ApplicationApproval(app, travelerRole);
        return approval;
    }

    public ApplicationApproval getApplicationApproval(int approvalId) {
        return approvalDao.selectApprovalById(approvalId);
    }

    public void saveApplicationApproval(ApplicationApproval approval) {
        approvalDao.saveApplicationApproval(approval);
    }

    public List<ApplicationApproval> pendingApprovalsForRole(Employee employee, TravelRole role) {
        List<ApplicationApproval> pending = approvalDao.selectApprovalsByNextRole(role);
        if (role == TravelRole.SUPERVISOR) {
            pending = pending.stream()
                    .filter(a -> isSupervisor(employee, a))
                    .collect(Collectors.toList());
        }

        return pending;
    }
    // Is the given employee a supervisor for the traveling employee.

    private boolean isSupervisor(Employee employee, ApplicationApproval applicationApproval) {
        return supervisorInfoService.getSupervisorIdForEmp(applicationApproval.application().getTraveler().getEmployeeId(), LocalDate.now()) == employee.getEmployeeId();
    }
}
