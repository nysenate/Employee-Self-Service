package gov.nysenate.ess.travel.review;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationReviewService {

    @Autowired private ApplicationReviewDao appReviewDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private TravelRoleFactory travelRoleFactory;

    // TODO include notes
    // TODO Transactional
    public void approveApplication(ApplicationReview applicationReview, Employee approver, TravelRole approverRole) {
        Action approvalAction = new Action(0, approver, approverRole, ActionType.APPROVE, "", LocalDateTime.now());
        applicationReview.addAction(approvalAction);
        saveApplicationReview(applicationReview);

        if (applicationReview.nextReviewerRole() == TravelRole.NONE) {
            applicationReview.application().approve();
            travelApplicationService.saveTravelApplication(applicationReview.application(), approver);
        }
    }

    public void disapproveApplication(ApplicationReview applicationReview, Employee disapprover, TravelRole approverRole) {
        Action disapproveAction = new Action(0, disapprover, approverRole, ActionType.DISAPPROVE, "", LocalDateTime.now());
        applicationReview.addAction(disapproveAction);
        saveApplicationReview(applicationReview);

        applicationReview.application().disapprove();
        travelApplicationService.saveTravelApplication(applicationReview.application(), disapprover);
    }

    public ApplicationReview createApplicationReview(TravelApplication app) {
        TravelRole travelerRole = travelRoleFactory.travelRoleForEmp(app.getTraveler()).orElse(TravelRole.NONE);
        ApplicationReview appReview = new ApplicationReview(app, travelerRole);
        return appReview;
    }

    public ApplicationReview getApplicationReview(int appReviewId) {
        return appReviewDao.selectAppReviewsById(appReviewId);
    }

    public void saveApplicationReview(ApplicationReview appReview) {
        appReviewDao.saveApplicationReview(appReview);
    }

    public List<ApplicationReview> pendingAppReviewsForEmpWithRole(Employee employee, TravelRole role) {
        if (role == TravelRole.NONE) {
            return new ArrayList<>();
        }

        List<ApplicationReview> pendingReviews = appReviewDao.selectAppReviewsByNextRole(role);
        if (role == TravelRole.SUPERVISOR) {
            pendingReviews = pendingReviews.stream()
                    .filter(a -> isSupervisor(employee, a))
                    .collect(Collectors.toList());
        }

        return pendingReviews;
    }

    public List<ApplicationReview> appReviewHistoryForEmp(int employeeId) {
        return appReviewDao.selectReviewHistoryForEmp(employeeId);
    }

    private boolean isSupervisor(Employee employee, ApplicationReview applicationReview) {
        return supervisorInfoService.getSupervisorIdForEmp(applicationReview.application().getTraveler().getEmployeeId(), LocalDate.now()) == employee.getEmployeeId();
    }
}
