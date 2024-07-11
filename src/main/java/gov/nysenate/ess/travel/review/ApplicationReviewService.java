package gov.nysenate.ess.travel.review;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.notifications.email.events.TravelApprovalEmailEvent;
import gov.nysenate.ess.travel.notifications.email.events.TravelDisapprovalEmailEvent;
import gov.nysenate.ess.travel.notifications.email.events.TravelPendingReviewEmailEvent;
import gov.nysenate.ess.travel.request.app.AppStatus;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.app.TravelApplicationService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.delegate.Delegation;
import gov.nysenate.ess.travel.delegate.DelegationDao;
import gov.nysenate.ess.travel.notifications.email.TravelEmailService;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;
import gov.nysenate.ess.travel.review.dao.ApplicationReviewDao;
import gov.nysenate.ess.travel.review.view.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationReviewService {

    @Autowired private ApplicationReviewDao appReviewDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private TravelRoleFactory travelRoleFactory;
    @Autowired private TravelEmailService emailService;
    @Autowired private DelegationDao delegationDao;
    @Autowired private EventBus eventBus;

    public void approveApplication(ApplicationReview applicationReview, Employee approver, String notes,
                                   TravelRole approverRole) {
        Action approvalAction = new Action(0, approver, approverRole,
                ActionType.APPROVE, notes, LocalDateTime.now());
        applicationReview.addAction(approvalAction);

        if (applicationReview.nextReviewerRole() == TravelRole.NONE) {
            // If no one else needs to review, the application is completely approved.
            travelApplicationService.updateApplicationStatus(
                    applicationReview.application().getAppId(),
                    new TravelApplicationStatus(AppStatus.APPROVED, "")
            );
            eventBus.post(new TravelApprovalEmailEvent(applicationReview));
        } else {
            if (applicationReview.nextReviewerRole() == TravelRole.TRAVEL_ADMIN
                    || applicationReview.nextReviewerRole() == TravelRole.SECRETARY_OF_THE_SENATE) {
                travelApplicationService.updateApplicationStatus(
                        applicationReview.application().getAppId(),
                        new TravelApplicationStatus(AppStatus.TRAVEL_UNIT, "")
                );
            }
            eventBus.post(new TravelPendingReviewEmailEvent(applicationReview));
        }
        saveApplicationReview(applicationReview);
    }

    public void disapproveApplication(ApplicationReview applicationReview, Employee disapprover,
                                      String reason, TravelRole disapproverRole) {
        Action disapproveAction = new Action(0, disapprover, disapproverRole, ActionType.DISAPPROVE,
                reason, LocalDateTime.now());
        applicationReview.addAction(disapproveAction);
        applicationReview.application().setStatus(new TravelApplicationStatus(AppStatus.DISAPPROVED, reason));

        travelApplicationService.updateApplicationStatus(
                applicationReview.application().getAppId(),
                applicationReview.application().getStatus()
        );
        eventBus.post(new TravelDisapprovalEmailEvent(applicationReview));
        saveApplicationReview(applicationReview);
    }

    public ApplicationReview createApplicationReview(TravelApplication app) {
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(app.getTraveler());
        ApplicationReview appReview = new ApplicationReview(app, roles.apex());
        eventBus.post(new TravelPendingReviewEmailEvent(appReview));
        return appReview;
    }

    public ApplicationReview getApplicationReview(int appReviewId) {
        return appReviewDao.selectAppReviewById(appReviewId);
    }

    public ApplicationReview getApplicationReviewByAppId(int appId) {
        return appReviewDao.selectAppReviewByAppId(appId);
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
     * Get all ApplicationReviews requiring action by employee.
     *
     * @param employee
     * @return
     */
    public Map<TravelRole, List<ApplicationReview>> pendingReviews(Employee employee) {
        Map<TravelRole, List<ApplicationReview>> pendingReviews = new HashMap<>();
        var userRoles = travelRoleFactory.travelRolesForEmp(employee);
        var nonDeptHdRoles = userRoles.all().stream()
                .filter(r -> !r.equals(TravelRole.DEPARTMENT_HEAD))
                .collect(Collectors.toSet());
        for (TravelRole role : nonDeptHdRoles) {
            pendingReviews.put(role, appReviewDao.pendingReviewsForRole(role));
        }

        var deptHeadEmpIdList = new ArrayList<Integer>();
        if (userRoles.primary().contains(TravelRole.DEPARTMENT_HEAD)) {
            deptHeadEmpIdList.add(employee.getEmployeeId());
        }
        if (userRoles.delegate().contains(TravelRole.DEPARTMENT_HEAD)) {
            // Find all Dept head roles this user has been delegated.
            var deptHeadDelegations = delegationDao.findByDelegateEmpId(employee.getEmployeeId())
                    .stream()
                    .filter(Delegation::isActive)
                    .filter(d -> d.role().equals(TravelRole.DEPARTMENT_HEAD))
                    .collect(Collectors.toSet());
            for (var delegation : deptHeadDelegations) {
                deptHeadEmpIdList.add(delegation.principal().getEmployeeId());
            }
        }
        if (!deptHeadEmpIdList.isEmpty()) {
            pendingReviews.put(
                    TravelRole.DEPARTMENT_HEAD,
                    appReviewDao.pendingReviewsForDeptHd(deptHeadEmpIdList)
            );
        }

        return pendingReviews;
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
    public Set<ApplicationReview> appReviewHistory(Employee emp) {
        final Set<TravelRole> ADMIN_ROLES = Sets.newHashSet(TravelRole.TRAVEL_ADMIN, TravelRole.SECRETARY_OF_THE_SENATE);
        Set<ApplicationReview> appReviews = new HashSet<>();
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(emp);

        Set<TravelRole> adminRoles = roles.all().stream()
                .filter(ADMIN_ROLES::contains)
                .collect(Collectors.toSet());

        if (!adminRoles.isEmpty()) {
            appReviews.addAll(appReviewDao.reviewHistoryForRoles(adminRoles));
        }

        for (TravelRole role : Sets.newHashSet(roles.all())) {
            // Convert roles.all to a set to remove duplicates. The only practical duplicate is DEPARTMENT_HEAD,
            // which can occur from delegation. These will be handled by the delegation handling below.
            if (role.equals(TravelRole.DEPARTMENT_HEAD)) {
                appReviews.addAll(appReviewDao.reviewHistoryForDeptHead(emp));
            }
        }
        // If the DeptHd role is delegated, we need to add the reviewHistory for their delegate principal.
        if (roles.delegate().contains(TravelRole.DEPARTMENT_HEAD)) {
            List<Delegation> delegations = delegationDao.findByDelegateEmpId(emp.getEmployeeId())
                    .stream()
                    .filter(Delegation::isActive)
                    .toList();
            for (Delegation delegation : delegations) {
                appReviews.addAll(appReviewDao.reviewHistoryForDeptHead(delegation.principal()));
            }
        }
        return appReviews;
    }

    /**
     * Get a List of ApplicationReviews to display for reconciliation.
     * These are apps which have been fully approved.
     *
     * @param from
     * @param to
     * @return
     */
    public List<ApplicationReview> appsToReconcile(LocalDate from, LocalDate to) {
        return appReviewDao.approvedAppReviews(from, to);
    }
}
