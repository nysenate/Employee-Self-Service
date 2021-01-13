package gov.nysenate.ess.travel.review;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.department.Department;
import gov.nysenate.ess.core.department.DepartmentDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.delegate.Delegation;
import gov.nysenate.ess.travel.delegate.DelegationDao;
import gov.nysenate.ess.travel.notifications.email.TravelEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationReviewService {

    @Autowired private ApplicationReviewDao appReviewDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private TravelRoleFactory travelRoleFactory;
    @Autowired private TravelEmailService emailService;
    @Autowired private DelegationDao delegationDao;

    public void approveApplication(ApplicationReview applicationReview, Employee approver, String notes,
                                   TravelRole approverRole) {
        Action approvalAction = new Action(0, approver, approverRole,
                ActionType.APPROVE, notes, LocalDateTime.now());
        applicationReview.addAction(approvalAction);
        saveApplicationReview(applicationReview);

        if (applicationReview.nextReviewerRole() == TravelRole.NONE) {
            // If no one else needs to review, the application is completely approved.
            applicationReview.application().approve();
            travelApplicationService.saveApplication(applicationReview.application());
            emailService.sendApprovalEmails(applicationReview.application());
        }
    }

    public void disapproveApplication(ApplicationReview applicationReview, Employee disapprover,
                                      String reason, TravelRole disapproverRole) {
        Action disapproveAction = new Action(0, disapprover, disapproverRole, ActionType.DISAPPROVE,
                reason, LocalDateTime.now());
        applicationReview.addAction(disapproveAction);
        saveApplicationReview(applicationReview);

        applicationReview.application().disapprove(reason);
        travelApplicationService.saveApplication(applicationReview.application());
        emailService.sendDisapprovalEmails(applicationReview.application(), disapprover, reason);
    }

    public ApplicationReview createApplicationReview(TravelApplication app) {
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(app.getTraveler());
        ApplicationReview appReview = new ApplicationReview(app, roles.apex());
        return appReview;
    }

    public ApplicationReview getApplicationReview(int appReviewId) {
        return appReviewDao.selectAppReviewById(appReviewId);
    }

    public void saveApplicationReview(ApplicationReview appReview) {
        appReviewDao.saveApplicationReview(appReview);
    }

    public ApplicationReview updateIsShared(ApplicationReview review, boolean isShared) {
        review.setShared(isShared);
        saveApplicationReview(review);
        return review;
    }

    /**
     * @return All {@code ApplicationReview}s that require action by the given employee
     * acting with the given {@code role}.
     */
    public List<ApplicationReview> pendingAppReviews(Employee employee, Set<TravelRole> roles) {
        List<ApplicationReview> appReviews = new ArrayList<>();
        for (TravelRole role : roles) {
            if (role == TravelRole.DEPARTMENT_HEAD) {
                appReviews.addAll(pendingReviewsForDeptHead(employee));
            } else {
                appReviews.addAll(appReviewDao.pendingReviewsByRole(role));
            }
        }
        return appReviews;
    }

    private List<ApplicationReview> pendingReviewsForDeptHead(Employee employee) {
        List<ApplicationReview> appReviews = new ArrayList<>();
        // Add AppReviews for this employee's department if any.
        appReviews.addAll(appReviewDao.pendingReviewsForDeptHead(employee));
        // Add AppReviews for delegated departments.
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(employee);
        if (roles.delegate().contains(TravelRole.DEPARTMENT_HEAD)) {
            List<Delegation> delegations = delegationDao.findByDelegateEmpId(employee.getEmployeeId());
            for (Delegation delegation : delegations) {
                // Check all delegations for any department head pending AppReviews.
                appReviews.addAll(appReviewDao.pendingReviewsForDeptHead(delegation.principal()));
            }
        }
        return appReviews;
    }

    /**
     * Returns shared app reviews that have not yet been approved by all reviewers.
     *
     * @return
     */
    public List<ApplicationReview> pendingSharedAppReviews() {
        return appReviewDao.pendingSharedReviews();
    }

    /**
     * @param emp
     * @return All ApplicationReviews that have been modified by any of {@code emp} roles.
     */
    public List<ApplicationReview> appReviewHistory(Employee emp) {
        List<ApplicationReview> appReviews = new ArrayList<>();
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(emp);
        for (TravelRole role : Sets.newHashSet(roles.all())) {
            // Convert roles.all to a set to remove duplicates. The only practical duplicate is DEPARTMENT_HEAD,
            // which can occur from delegation. These will be handled by the delegation handling below.
            if (role.equals(TravelRole.DEPARTMENT_HEAD)) {
                appReviews.addAll(appReviewDao.reviewHistoryForDeptHead(emp));
            }
            else {
                 appReviews.addAll(appReviewDao.reviewHistoryForRole(role));
            }
        }
        // If the DeptHd role is delegated, we need to add the reviewHistory for their delegate principal.
        if (roles.delegate().contains(TravelRole.DEPARTMENT_HEAD)) {
            List<Delegation> delegations = delegationDao.findByDelegateEmpId(emp.getEmployeeId());
            for (Delegation delegation : delegations) {
                appReviews.addAll(appReviewDao.reviewHistoryForDeptHead(delegation.principal()));
            }
        }
        return appReviews;
    }
}
