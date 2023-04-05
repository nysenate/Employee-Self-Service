package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility methods for generating email HTML.
 */
@Service
class PecEmailUtils {
    private final PersonnelTaskAssignmentDao assignmentDao;
    private final PersonnelTaskService taskService;
    private final EmployeeDao employeeDao;
    private final EmployeeInfoService employeeInfoService;

    @Value("${domain.url}")
    private String domainUrl;

    @Autowired
    public PecEmailUtils(PersonnelTaskAssignmentDao assignmentDao, PersonnelTaskService taskService,
                         EmployeeDao employeeDao, EmployeeInfoService employeeInfoService) {
        this.assignmentDao = assignmentDao;
        this.taskService = taskService;
        this.employeeDao = employeeDao;
        this.employeeInfoService = employeeInfoService;
    }

    public EmployeeEmail getEmail(PecEmailType type, AssignmentWithTask data) {
        return getEmail(type, List.of(data));
    }

    public EmployeeEmail getEmail(PecEmailType type, List<AssignmentWithTask> dataList) {
        Employee emp = employeeInfoService.getEmployee(dataList.get(0).assignment().getEmpId());
        if (emp.isSenator()) {
            return null;
        }
        return new EmployeeEmail(emp, type, dataList, domainUrl);
    }

    public List<EmployeeEmail> getEmails(List<String> addresses, PecEmailType type,
                                         Optional<PersonnelTask> taskOpt,
                                         String... extraData) {
        var emails = new ArrayList<EmployeeEmail>();
        for (String address : addresses) {
            Employee emp = employeeDao.getEmployeeByEmail(address);
            if (!emp.isSenator()) {
                var dataList = new ArrayList<AssignmentWithTask>();
                taskOpt.ifPresent(task -> dataList.add(new AssignmentWithTask(emp.getEmployeeId(), task)));
                emails.add(new EmployeeEmail(emp, type, dataList, extraData));
            }
        }
        return emails;
    }

    public List<AssignmentWithTask> getNotifiableAssignments(Employee employee) {
        return assignmentDao.getAssignmentsForEmp(employee.getEmployeeId()).stream()
                .filter(assignment -> assignment.isActive() && !assignment.isCompleted())
                .map(assignment -> new AssignmentWithTask(assignment, taskService.getPersonnelTask(assignment.getTaskId())))
                .filter(data -> data.task().isActive() && data.task().isNotifiable())
                .filter(data -> shouldSend(data.assignment().getDueDate()))
                .collect(Collectors.toList());
    }

    /**
     * Emails should send weekly, unless the due date is within a week.
     * Then, they should be sent daily.
     */
    private static boolean shouldSend(LocalDateTime dueDate) {
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.MONDAY) {
            return true;
        }
        return dueDate != null && ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate) <= 7;
    }

    public List<Employee> getActiveNonSenatorEmployees() {
        return employeeDao.getActiveEmployees().stream()
                .filter(emp -> !emp.isSenator()).collect(Collectors.toList());
    }
}
