package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Utility methods for generating email HTML.
 */
@Service
class PecEmailUtils {
    private final PersonnelTaskAssignmentDao assignmentDao;
    private final EmployeeDao employeeDao;
    private final EmployeeInfoService employeeInfoService;

    @Value("${domain.url}")
    private String domainUrl;

    @Autowired
    public PecEmailUtils(PersonnelTaskAssignmentDao assignmentDao, EmployeeDao employeeDao,
                         EmployeeInfoService employeeInfoService) {
        this.assignmentDao = assignmentDao;
        this.employeeDao = employeeDao;
        this.employeeInfoService = employeeInfoService;
    }

    /**
     * Constructs an email, fetching the Employee data if needed.
     */
    public EmployeeEmail getEmail(PecEmailType type, Optional<Employee> employeeOpt, AssignmentWithTask data) {
        Employee employee = employeeOpt.orElseGet(() -> employeeInfoService.getEmployee(data.assignment().getEmpId()));
        return getEmail(type, employee, List.of(data));
    }

    public EmployeeEmail getEmail(PecEmailType type, Employee employee, List<AssignmentWithTask> dataList) {
        return new EmployeeEmail(employee, type, dataList, domainUrl);
    }

    public List<EmployeeEmail> getEmails(List<String> addresses, PecEmailType type,
                                         Optional<PersonnelTask> taskOpt,
                                         List<String> extraData) {
        var emails = new ArrayList<EmployeeEmail>();
        for (String address : addresses) {
            Employee emp = employeeDao.getEmployeeByEmail(address);
            var dataList = new ArrayList<AssignmentWithTask>();
            taskOpt.ifPresent(task -> dataList.add(new AssignmentWithTask(emp.getEmployeeId(), task)));
            emails.add(new EmployeeEmail(emp, type, dataList, extraData.toArray(new String[0])));
        }
        return emails;
    }

    public Map<Employee, List<AssignmentWithTask>> getNotifiableTaskMap(boolean invitableOnly) {
        var idToTaskMap = new HashMap<Integer, List<AssignmentWithTask>>();
        for (var task : assignmentDao.getNotifiableAssignmentsWithTasks(invitableOnly)) {
            if (!invitableOnly && !shouldSendReminder(task.assignment().getDueDate())) {
                continue;
            }
            int id = task.assignment().getEmpId();
            if (!idToTaskMap.containsKey(id)) {
                idToTaskMap.put(id, new ArrayList<>());
            }
            idToTaskMap.get(id).add(task);
        }
        var empToTaskMap = new HashMap<Employee, List<AssignmentWithTask>>();
        for (var entry : idToTaskMap.entrySet()) {
            Employee emp = employeeInfoService.getEmployee(entry.getKey());
            if (emp.isActive() && !emp.isSenator()) {
                empToTaskMap.put(emp, entry.getValue());
            }
        }
        return empToTaskMap;
    }

    /**
     * Emails should send weekly, unless the due date is within a week.
     * Then, they should be sent daily.
     */
    private static boolean shouldSendReminder(LocalDateTime dueDate) {
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.MONDAY) {
            return true;
        }
        return dueDate != null && ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate) <= 7;
    }
}
