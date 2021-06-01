package gov.nysenate.ess.travel.review;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.department.Department;
import gov.nysenate.ess.core.department.DepartmentDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.app.TravelApplicationService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.delegate.Delegation;
import gov.nysenate.ess.travel.delegate.DelegationDao;
import gov.nysenate.ess.travel.notifications.email.TravelEmailService;
import gov.nysenate.ess.travel.review.dao.ApplicationReviewDao;
import gov.nysenate.ess.travel.review.view.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired private DepartmentDao departmentDao;

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
            emailService.sendApprovalEmails(applicationReview);
        } else {
            emailService.sendPendingReviewEmail(applicationReview);
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
        emailService.sendDisapprovalEmails(applicationReview);
    }

    public ApplicationReview createApplicationReview(TravelApplication app) {
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(app.getTraveler());
        ApplicationReview appReview = new ApplicationReview(app, roles.apex());
        emailService.sendPendingReviewEmail(appReview);
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
     * @param employee
     * @return
     */
    public Map<TravelRole, List<ApplicationReview>> pendingReviews(Employee employee) {
        Map<TravelRole, List<ApplicationReview>> pendingReviews = new HashMap<>();
        var userRoles = travelRoleFactory.travelRolesForEmp(employee);
        var nonDeptPrimaryRoles = userRoles.primary().stream()
                .filter(r -> !r.equals(TravelRole.DEPARTMENT_HEAD))
                .collect(Collectors.toSet());
        for (TravelRole role : nonDeptPrimaryRoles) {
            pendingReviews.put(role, appReviewDao.pendingReviewsForRole(role));
        }

        var departmentIds = allDeptIdsUnderEmp(employee, userRoles);
        if (!departmentIds.isEmpty()) {
            pendingReviews.put(TravelRole.DEPARTMENT_HEAD, appReviewDao.pendingReviewsForDeptIds(departmentIds));
        }
        return pendingReviews;
    }

    // Returns a Set of department ids for all departments managed or delegated to employee.
    private Set<Integer> allDeptIdsUnderEmp(Employee employee, TravelRoles userRoles) {
        var departmentIds = new HashSet<Integer>();
        var primaryDepartmentIds = departmentIdsForHead(employee);
        departmentIds.addAll(primaryDepartmentIds);

        if (userRoles.delegate().contains(TravelRole.DEPARTMENT_HEAD)) {
            var deptHdDelegations = delegationDao.findByDelegateEmpId(employee.getEmployeeId()).stream()
                    .filter(d -> d.role().equals(TravelRole.DEPARTMENT_HEAD))
                    .collect(Collectors.toSet());
            for (Delegation delegation : deptHdDelegations) {
                departmentIds.addAll(departmentIdsForHead(delegation.principal()));
            }
        }
        return departmentIds;
    }

    // Return a Set of department Ids for all departments managed by employee..
    private Set<Integer> departmentIdsForHead(Employee employee) {
        Set<Department> departments = departmentDao.getDepartmentsByHead(employee.getEmployeeId());
        return departments.stream()
                .map(Department::getId)
                .collect(Collectors.toSet());
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
            } else {
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

    public List<ApplicationReview> appsToReconcile() {
        return appReviewDao.approvedAppReviews();
    }
}
