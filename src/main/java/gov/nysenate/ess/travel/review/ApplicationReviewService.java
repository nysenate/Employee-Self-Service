package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
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

    public void approveApplication(ApplicationReview applicationReview, Employee approver, String notes,
                                   TravelRole approverRole, boolean isDiscussionRequested) {
        Action approvalAction = new Action(0, approver, approverRole, ActionType.APPROVE, notes,
                LocalDateTime.now(), isDiscussionRequested);
        applicationReview.addAction(approvalAction);
        saveApplicationReview(applicationReview);

        if (applicationReview.nextReviewerRole() == TravelRole.NONE) {
            applicationReview.application().activeAmendment().approve();
            travelApplicationService.saveTravelApplication(applicationReview.application(), approver);
        }
    }

    public void disapproveApplication(ApplicationReview applicationReview, Employee disapprover,
                                      String notes, TravelRole disapproverRole) {
        Action disapproveAction = new Action(0, disapprover, disapproverRole, ActionType.DISAPPROVE,
                notes, LocalDateTime.now(), false);
        applicationReview.addAction(disapproveAction);
        saveApplicationReview(applicationReview);

        applicationReview.application().activeAmendment().disapprove(notes);
        travelApplicationService.saveTravelApplication(applicationReview.application(), disapprover);
    }

    public ApplicationReview createApplicationReview(TravelApplication app) {
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(app.getTraveler());
        return new ApplicationReview(app, roles.apex());
    }

    public ApplicationReview getApplicationReview(int appReviewId) {
        return appReviewDao.selectAppReviewById(appReviewId);
    }

    public void saveApplicationReview(ApplicationReview appReview) {
        appReviewDao.saveApplicationReview(appReview);
    }

    public List<ApplicationReview> pendingAppReviewsForEmpWithRole(Employee employee, TravelRole role) {
        if (role == TravelRole.NONE) {
            return new ArrayList<>();
        }

        List<ApplicationReview> pendingReviews = appReviewDao.pendingReviewsByRole(role);
        if (role == TravelRole.SUPERVISOR) {
            pendingReviews = pendingReviews.stream()
                    .filter(a -> isSupervisor(employee, a))
                    .collect(Collectors.toList());
        }

        return pendingReviews;
    }

    public List<ApplicationReview> appReviewHistoryForRole(TravelRole role) {
        // TODO if SUPERVISOR need to filter out employees who are not theirs
        // TODO wait on implementing this until Dept Heads are added.
        return appReviewDao.reviewHistoryForRole(role);
    }

    private boolean isSupervisor(Employee employee, ApplicationReview applicationReview) {
        return supervisorInfoService.getSupervisorIdForEmp(applicationReview.application().getTraveler().getEmployeeId(), LocalDate.now()) == employee.getEmployeeId();
    }
}
