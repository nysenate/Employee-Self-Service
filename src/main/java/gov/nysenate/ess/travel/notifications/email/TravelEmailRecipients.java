package gov.nysenate.ess.travel.notifications.email;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.department.Department;
import gov.nysenate.ess.core.department.DepartmentDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.delegate.Delegation;
import gov.nysenate.ess.travel.delegate.DelegationDao;
import gov.nysenate.ess.travel.review.ApplicationReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TravelEmailRecipients {

    private EmployeeInfoService employeeInfoService;
    private DepartmentDao departmentDao;
    private DelegationDao delegationDao;
    private RoleDao roleDao;

    @Autowired
    public TravelEmailRecipients(EmployeeInfoService employeeInfoService,
                                 DepartmentDao departmentDao,
                                 DelegationDao delegationDao,
                                 RoleDao roleDao) {
        this.employeeInfoService = employeeInfoService;
        this.departmentDao = departmentDao;
        this.delegationDao = delegationDao;
        this.roleDao = roleDao;
    }

    /**
     * Determines which employees should be notified about a status change to an application.
     * Examples of status change: approved, disapproved, edited.
     * @param appReview
     * @return A set of employees who should be emailed.
     */
    public Set<Employee> forStatusUpdate(ApplicationReview appReview) {
        return Sets.newHashSet(
                appReview.application().getSubmittedBy(),
                appReview.application().getTraveler());
    }

    /**
     * Determines which reviewers should be notified when there is a change in the AppReview next reviewer.
     * @param appReview The ApplicationReview an email notification is being sent for.
     * @return A set of employees who should be emailed.
     */
    public Set<Employee> forPendingReview(ApplicationReview appReview) {
        Set<Employee> recipients = Sets.newHashSet();

        switch (appReview.nextReviewerRole()) {
            case DEPARTMENT_HEAD:
                Department travelerDept = departmentDao.getDepartment(appReview.application().getTravelerDepartmentId());
                Employee deptHead = employeeInfoService.getEmployee(travelerDept.getHeadEmpId());
                recipients.add(deptHead);
                recipients.addAll(employeeDelegates(deptHead));
                break;

            case TRAVEL_ADMIN:
                recipients.addAll(essRoleRecipients(EssRole.TRAVEL_ADMIN));
                break;

            case SECRETARY_OF_THE_SENATE:
                recipients.addAll(essRoleRecipients(EssRole.SECRETARY_OF_SENATE));
                break;

            case MAJORITY_LEADER:
                recipients.addAll(essRoleRecipients(EssRole.MAJORITY_LEADER));
                break;
        }

        return recipients;
    }

    private Set<Employee> essRoleRecipients(EssRole essRole) {
        Set<Employee> recipients = new HashSet<>();
        ImmutableList<Employee> emps = roleDao.getEmployeesWithRole(essRole);
        for (Employee emp : emps) {
            recipients.add(emp);
            recipients.addAll(employeeDelegates(emp));
        }
        return recipients;
    }

    private Set<Employee> employeeDelegates(Employee emp) {
        Set<Employee> delegates = new HashSet<>();
        List<Delegation> delegations = delegationDao.findByPrincipalEmpId(emp.getEmployeeId());
        for (Delegation delegation : delegations) {
            delegates.add(delegation.delegate());
        }
        return delegates;
    }
}
