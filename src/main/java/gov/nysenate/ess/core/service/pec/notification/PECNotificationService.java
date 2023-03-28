package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.notification.PECNotificationDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.service.pec.notification.EmailType.*;
import static java.nio.file.StandardOpenOption.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final SendMailService sendMailService;
    private final EmployeeInfoService employeeInfoService;
    private final PECNotificationDao pecNotificationDao;
    private final Map<Integer, PersonnelTask> activeTaskMap = new HashMap<>();
    private final List<String> reportEmails;
    private final boolean pecTestMode;
    private final double emailLimit;
    private final Path emailLogPath;
    private double emailCount = 0;

    @Value("${scheduler.pec.notifs.enabled:false}")
    private boolean pecNotifsEnabled;
    @Value("${all.pec.notifs.enabled:false}")
    private boolean allPecNotifsEnabled;

    public PECNotificationService(PersonnelTaskService taskService,
                                  PersonnelTaskAssignmentDao assignmentDao, SendMailService sendMailService,
                                  EmployeeInfoService employeeInfoService,
                                  PECNotificationDao pecNotificationDao,
                                  @Value("${report.email}") String reportEmailList,
                                  @Value("${pec.test.mode:true}") boolean pecTestMode,
                                  @Value("${data.dir}") String dataDir) {
        this.assignmentDao = assignmentDao;
        this.sendMailService = sendMailService;
        this.employeeInfoService = employeeInfoService;
        this.pecNotificationDao = pecNotificationDao;
        for (PersonnelTask task : taskService.getPersonnelTasks(true)) {
            activeTaskMap.put(task.getTaskId(), task);
        }
        this.reportEmails = List.of(reportEmailList.replaceAll(" ", "").split(","));
        this.pecTestMode= pecTestMode;
        this.emailLimit = pecTestMode ? 5 : Double.POSITIVE_INFINITY;
        this.emailLogPath = Path.of(dataDir, "emailLog.txt");
    }

    @Scheduled(cron = "${scheduler.pec.notifs.cron}")
    public void runUpdateMethods() {
        if (pecNotifsEnabled) {
            runPECNotificationProcess();
        }
    }

    // TODO: test and remove?
    public void resetTestModeCounter() {
        emailCount = 0;
    }

    /**
     * Fetches data on emails that would be sent right now, if the relevant cron ran.
     * Notably does NOT include invite emails.
     * @param sendAdminEmails whether to send emails about employees with missing emails.
     * @return List of email information.
     */
    public List<EmployeeEmail> getScheduledEmails(boolean sendAdminEmails) {
        // Document all employees with missing info to report to admins
        List<Employee> employeesWithMissingEmails = new ArrayList<>();
        List<EmployeeEmail> emailsToSend = new ArrayList<>();
        List<Employee> employees = employeeInfoService.getAllEmployees(true).stream()
                .filter(emp -> !emp.isSenator()).collect(Collectors.toList());
        for (Employee employee : employees) {
            List<PersonnelTaskAssignment> incompleteTaskAssignments = getIncompleteTaskAssignments(employee);
            if (incompleteTaskAssignments.isEmpty()) {
                continue;
            }
            // Continue processing employee if they have outstanding assignments and a valid email
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                employeesWithMissingEmails.add(employee);
                logger.warn("Employee %s #%d is missing an email! No notification will be sent to them."
                        .formatted(employee.getFullName(), employee.getEmployeeId()));
            } else {
                Map<PersonnelTask, LocalDate> taskMap = new HashMap<>();
                for (var assignment : incompleteTaskAssignments) {
                    LocalDate date = assignment.getDueDate() == null ? null : assignment.getDueDate().toLocalDate();
                    taskMap.put(activeTaskMap.get(assignment.getTaskId()), date);
                }
                emailsToSend.add(new EmployeeEmail(employee, REMINDER, taskMap, pecTestMode));
            }
        }
        if (!employeesWithMissingEmails.isEmpty() && sendAdminEmails) {
            String html = PecEmailUtils.getMissingEmailHtml(employeesWithMissingEmails);
            for (String adminEmail : reportEmails) {
                sendEmail(new ReportEmail(adminEmail, REPORT_MISSING, html));
            }
        }
        return emailsToSend;
    }

    /**
     * The actual scheduled process. Will fetch and send out emails.
     */
    public void runPECNotificationProcess() {
        logger.info("Starting PEC Notification Process");
        resetTestModeCounter();
        getScheduledEmails(true).forEach(this::sendEmail);
        logger.info("Completed PEC Notification Process");
    }

    public Optional<EmployeeEmail> getInviteEmail(int empId, PersonnelTask task, LocalDateTime dueDate) {
        Employee employee = employeeInfoService.getEmployee(empId);
        boolean wasNotifSent = pecNotificationDao.wasNotificationSent(empId, task.getTaskId());

        if (!task.isActive() || wasNotifSent || employee.isSenator() || !task.isNotifiable()) {
            return Optional.empty();
        }

        Map<PersonnelTask, LocalDate> taskMap = new HashMap<>();
        taskMap.put(task, dueDate == null ? null : dueDate.toLocalDate());
        return Optional.of(new EmployeeEmail(employee, INVITE, taskMap, pecTestMode));
    }

    public void sendInviteEmail(EmployeeEmail email) {
        sendEmail(email);
        if (!pecTestMode) {
            pecNotificationDao.markNotificationSent(email.getEmployee().getEmployeeId(),
                    email.tasks().get(0).getTaskId());
        }
    }

    public void sendCompletionEmail(int empId, PersonnelTask task) {
        if (!task.isNotifiable()) {
            return;
        }
        Employee employee = employeeInfoService.getEmployee(empId);
        var emailInfo = new EmployeeEmail(employee, COMPLETION, task);
        sendEmail(emailInfo);
    }

    /**
     * Sends emails. Limited by test mode and PEC notifs enabled.
     */
    public void sendEmail(NotificationEmail emailInfo) {
        if (!allPecNotifsEnabled && emailInfo.type() != ADMIN_CODES) {
            return;
        }
        if (emailInfo.type() == INVITE || emailInfo.type() == REMINDER) {
            if (emailCount >= emailLimit) {
                return;
            }
            emailCount++;
        }
        String address = emailInfo.sendTo().trim();
        // Keeps spacing consistent and positive.
        String spaces = " ".repeat(Math.max(28 - address.length(), 1));
        String logMessage = "Recipient: %sSubject: %s\n".formatted(address + spaces, emailInfo.subject());
        if (pecTestMode) {
            address = reportEmails.get(0);
        }
        MimeMessage message = sendMailService.newHtmlMessage(address, emailInfo.subject(), emailInfo.html);
        try {
            sendMailService.send(message);
            Files.writeString(emailLogPath, logMessage, CREATE, WRITE, APPEND);
        } catch (Exception e) {
            logger.error("There was an error trying to send the PEC notification email ", e);
        }
    }

    // TODO: BaseGroupTaskAssigner should really just have an EmployeeInfoService,
    //  getting this information isn't this class' responsibility.
    public LocalDate getContinuousServiceDate(int empId) {
        return employeeInfoService.getEmployeesMostRecentContinuousServiceDate(empId);
    }

    private List<PersonnelTaskAssignment> getIncompleteTaskAssignments(Employee employee) {
        return assignmentDao.getAssignmentsForEmp(employee.getEmployeeId()).stream()
                .filter(assignment -> assignment.isActive() && !assignment.isCompleted())
                .filter(assignment -> activeTaskMap.containsKey(assignment.getTaskId()))
                .collect(Collectors.toList());
    }

    public LocalDateTime getEndOfTheYear() {
        return LocalDateTime.of(LocalDate.now().getYear(),12,31,0,0);
    }

    public Map<Integer, PersonnelTask> getActiveTaskMap() {
        return activeTaskMap;
    }

    public void generateDueDatesForExistingTaskAssignments () {

        //cycle thru employees
        //get tasks
        //if task 5 and 16 get continuous service / calc due date
        //skip if task already has dates
        //update tasks in database
        logger.info("Beginning Date Assignment Processing");

        Set<Employee> employees = employeeDao.getActiveEmployees();

        for (Employee emp : employees) {
            List<PersonnelTaskAssignment> empAssignments = assignmentDao.getAssignmentsForEmp(emp.getEmployeeId());
            for (PersonnelTaskAssignment assignment : empAssignments) {

                //Only updating these 2 tasks. Skip if they already have a due date, is not active, or is completed
                if ((assignment.getTaskId() == 5 || assignment.getTaskId() == 16)
                        && assignment.isActive() && !assignment.isCompleted()
                        && assignment.getDueDate() == null) {

                    LocalDate contServiceDate = getConitnuousServiceDate(assignment.getEmpId());

                    PersonnelTaskAssignment updatedAssignment;

                    if (assignment.getTaskId() == 5) {
                        updatedAssignment = new PersonnelTaskAssignment(
                                assignment.getTaskId(), assignment.getEmpId(), assignment.getUpdateEmpId(),
                                assignment.getUpdateTime(), assignment.isCompleted(), assignment.isActive(),
                                assignment.wasManuallyOverridden(),
                                LocalDateTime.of(contServiceDate, LocalTime.of(0,0)),
                                LocalDateTime.of(getDueDate(contServiceDate, 30), LocalTime.of(0,0))
                        );
                        assignmentDao.updateAssignmentDates(updatedAssignment);
                        logger.info("Completed update for Emp: " + assignment.getEmpId() + ". Updated Task ID 5");
                    }
                    else if (assignment.getTaskId() == 16) {

                        if (isExistingEmployee(contServiceDate)) {
                            updatedAssignment = new PersonnelTaskAssignment(
                                    assignment.getTaskId(), assignment.getEmpId(), assignment.getUpdateEmpId(),
                                    assignment.getUpdateTime(), assignment.isCompleted(), assignment.isActive(),
                                    assignment.wasManuallyOverridden(),
                                    LocalDateTime.of(contServiceDate, LocalTime.of(0,0)),
                                    getEndOfTheYear());
                        }
                        else {
                            updatedAssignment = new PersonnelTaskAssignment(
                                    assignment.getTaskId(), assignment.getEmpId(), assignment.getUpdateEmpId(),
                                    assignment.getUpdateTime(), assignment.isCompleted(), assignment.isActive(),
                                    assignment.wasManuallyOverridden(),
                                    LocalDateTime.of(contServiceDate, LocalTime.of(0,0)),
                                    LocalDateTime.of(getDueDate(contServiceDate, 90), LocalTime.of(0,0))
                            );
                        }
                        assignmentDao.updateAssignmentDates(updatedAssignment);
                        logger.info("Completed update for Emp: " + assignment.getEmpId() + ". Updated Task ID 16");
                    }

                }

            }
        }

        logger.info("Completed Date Assignment Processing");
    }
}
