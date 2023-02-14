package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.notification.PECNotificationDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
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
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);

    private EmployeeDao employeeDao;

    private PersonnelTaskService taskService;

    private PersonnelTaskAssignmentDao assignmentDao;
    private SendMailService sendMailService;

    private EmployeeInfoService employeeInfoService;

    private PECNotificationDao pecNotificationDao;

    private Map<Integer, PersonnelTask> activeTaskMap;

    @Value("${scheduler.pec.notifs.enabled:false}")
    private boolean pecNotifsEnabled;

    @Value("${pec.test.mode:true}")
    private boolean pecTestMode;

    @Value("${all.pec.notifs.enabled:false}")
    private boolean allPecNotifsEnabled;

    private List<String> reportEmails;

    private String instructionURL;

    private int pecTestModeLimit = 5;
    private int pecTestModeCount = 0;

    public PECNotificationService(EmployeeDao employeeDao, PersonnelTaskService taskService,
                                  PersonnelTaskAssignmentDao assignmentDao, SendMailService sendMailService,
                                  EmployeeInfoService employeeInfoService,
                                  PECNotificationDao pecNotificationDao,
                                  @Value("${report.email}") String reportEmailList,
                                  @Value("${domain.url}") String domainURL) {
        this.employeeDao = employeeDao;
        this.taskService = taskService;
        this.assignmentDao = assignmentDao;
        this.sendMailService = sendMailService;
        this.employeeInfoService = employeeInfoService;
        this.pecNotificationDao = pecNotificationDao;
        Map<Integer, PersonnelTask> activeTaskMap = new HashMap<>();
        for (PersonnelTask task : taskService.getPersonnelTasks(true)) {
            activeTaskMap.put(task.getTaskId(), task);
        }
        this.activeTaskMap = activeTaskMap;
        this.reportEmails = Arrays.asList(reportEmailList.replaceAll(" ", "").split(","));
        this.instructionURL = domainURL + "/myinfo/personnel/todo";
    }

    @Scheduled(cron = "${scheduler.pec.notifs.cron}")
    public void runUpdateMethods() {
        if (pecNotifsEnabled) {
            runPECNotificationProcess();
        }
    }

    public void runPECNotificationProcess() {
        logger.info("Starting PEC Notification Process");

        this.pecTestModeCount = 0;

        //Document all employees with missing info to report to the admins
        List<Employee> employeesWithMissingEmails = new ArrayList<>();

        //Get All active employees
        Set<Employee> activeEmployees = employeeDao.getActiveEmployees();

        //Cycle thru all employees
        for (Employee employee : activeEmployees) {
            //Determine if they have any outstanding active tasks
            List<PersonnelTaskAssignment> incompleteEmpAssignments = getIncompleteAssignmentsForEmp(employee);

            //Continue processing employee if they have outstanding assignments and a valid email
            if (!incompleteEmpAssignments.isEmpty()) {

                if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                    employeesWithMissingEmails.add(employee);
                    logger.warn("Employee " + employee.getFullName() + " "
                            + employee.getEmployeeId() + " is missing an email! No notification will be sent to them");
                } else {
                    contructAndSendEmailToTheEmployee(employee, incompleteEmpAssignments);
                }
            }

        }

        //Only send a failed report email if there are employees missing emails
        if (!employeesWithMissingEmails.isEmpty()) {
            sendFailedNotifsToReportEmails("Employees With Missing Emails", generateFailedNotifReportString(employeesWithMissingEmails));
        }

        logger.info("Completed PEC Notification Process");
    }

    public void sendInviteEmails(int empID, PersonnelTaskAssignment assignment) {
        Employee employee = employeeDao.getEmployeeById(empID);

        boolean wasNotifSent = pecNotificationDao.wasNotificationSent(empID, assignment.getTaskId());

        if (this.activeTaskMap.containsKey(assignment.getTaskId()) && !wasNotifSent) {
            PersonnelTask task = this.activeTaskMap.get(assignment.getTaskId());

            String recipientEmail = employee.getEmail();
            String subject = employee.getFullName() + " You have to complete the task: " + task.getTitle();
            String html = getStandardHTMLInstructions(employee.getFullName());
            ArrayList<PersonnelTaskAssignment> incompleteTask = new ArrayList<>();
            incompleteTask.add(assignment);
            html = html + addDueDateInformationHtml(employee, incompleteTask);

            if (pecTestMode && (pecTestModeLimit >= pecTestModeCount)) {
                //Send email to employee
                sendEmail(recipientEmail, subject, html);
                pecTestModeCount++;
            } else if (!pecTestMode) {
                sendEmail(recipientEmail, subject, html);
            }
            if (!pecTestMode) {
                pecNotificationDao.markNotificationSent( empID, assignment.getTaskId());
            }
        }
    }

    public void sendCompletionEmail(int empID, int taskID) {
        PersonnelTask task = taskService.getPersonnelTask(taskID);

        Employee employee = employeeDao.getEmployeeById(empID);

        String recipientEmail = employee.getEmail();

        String subject = "You have completed the task: " + task.getTitle();
        String html = "Our records have been updated to indicate you have completed " + task.getTitle();

        sendEmail(recipientEmail, subject, html);
    }

    public void resetTestModeCounter() {
        this.pecTestModeCount = 0;
    }

    // Determine the email address & content for their email, then send the email
    private void contructAndSendEmailToTheEmployee(Employee employee, List<PersonnelTaskAssignment> incompleteEmpAssignments) {

        //Get basic employee data
        String recipientEmail = employee.getEmail();

        String subject = employee.getFullName() + " you have outstanding Personnel Tasks";

        //Standard instructions for all tasks
        String html = getStandardHTMLInstructions(employee.getFullName());

        //Add known time limit text to the emails
        html = html + addDueDateInformationHtml(employee, incompleteEmpAssignments);

        if (pecTestMode && (pecTestModeLimit >= pecTestModeCount)) {
            //Send email to employee
            sendEmail(recipientEmail, subject, html);
            pecTestModeCount++;
        } else if (!pecTestMode) {
            sendEmail(recipientEmail, subject, html);
        }

    }

    private void sendEmail(String to, String subject, String html) {
        try {
            if (pecTestMode) {
                to = this.reportEmails.get(0);
            }
            MimeMessage message = sendMailService.newHtmlMessage(to.trim(),
                    subject, html);
            if (allPecNotifsEnabled) {
                sendMailService.send(message);
            }
        } catch (Exception e) {
            logger.error("There was an error trying to send the PEC notification email ", e);
        }
    }

    private void sendFailedNotifsToReportEmails(String subject, String html) {
        for (String email : this.reportEmails) {
            sendEmail(email, subject, html);
        }
    }

    private String generateFailedNotifReportString(List<Employee> emps) {
        String employeeListDetails = "";
        for (Employee employee : emps) {
            employeeListDetails = employeeListDetails + " NAME: " + employee.getFullName() + " EMPID: " + employee.getEmployeeId() + "<br>\n";
        }
        return employeeListDetails;
    }

    private List<PersonnelTaskAssignment> getIncompleteAssignmentsForEmp(Employee employee) {
        //Get all assignments for the given employee
        List<PersonnelTaskAssignment> empAssignments = assignmentDao.getAssignmentsForEmp(employee.getEmployeeId());

        //Determine if they have any outstanding active tasks
        List<PersonnelTaskAssignment> incompleteEmpAssignments = new ArrayList<>();
        for (PersonnelTaskAssignment assignment : empAssignments) {

            if (assignment.isActive() && !assignment.isCompleted() &&
                    this.activeTaskMap.containsKey(assignment.getTaskId())) {
                incompleteEmpAssignments.add(assignment);
            }
        }
        return incompleteEmpAssignments;
    }

    private LocalDate getDueDate(LocalDate continuousServiceDate, int daysToAdd) {
        Date startDate = Date.from(continuousServiceDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = new GregorianCalendar(/* remember about timezone! */);
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, daysToAdd);
        Date dueDate = calendar.getTime();
        return dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Find out if the employee's continuous serivce date greater than 90 days from today
     *
     * @param continuousServiceDate
     * @return
     */
    private boolean isExistingEmployee(LocalDate continuousServiceDate) {
        LocalDate ninetyDaysAgo = LocalDate.now(ZoneId.systemDefault()).minus(Period.ofDays(90));
        return continuousServiceDate.isBefore(ninetyDaysAgo);
    }

    private String getStandardHTMLInstructions(String employeeName) {
        return "<b>" + employeeName + " Our records indicate you have outstanding tasks to complete for Personnel.</b><br>" +
                "You can find instructions to complete them by logging into ESS, " +
                "then clicking the My Info tab and then clicking on the To Do List. " +
                " Or go to this link <a href=\"" + instructionURL + "\">HERE</a><br><br>" +
                "<b>You must complete the following tasks: </b><br><br>\n\n";
    }

    private String addDueDateInformationHtml(Employee employee, List<PersonnelTaskAssignment> incompleteEmpAssignments) {
        LocalDate activeServiceDate =
                employeeInfoService.getEmployeesMostRecentContinuousServiceDate(employee.getEmployeeId());

        boolean existingEmployee = isExistingEmployee(activeServiceDate);

        LocalDate moodleDueDate = getDueDate(activeServiceDate, 30);
        //The ethics live due date only applies if they are a new hire
        LocalDate ethicsLiveNewEmpDueDate = getDueDate(activeServiceDate, 90);

        String html = "";
        for (PersonnelTaskAssignment assignment : incompleteEmpAssignments) {
            if (this.activeTaskMap.containsKey(assignment.getTaskId())) {
                PersonnelTask task = this.activeTaskMap.get(assignment.getTaskId());
                html = html + "<li>" + task.getTitle() + "</li>\n";

                switch (task.getTaskType()) {
                    case DOCUMENT_ACKNOWLEDGMENT:
                        html = html + "<br>"; //Time limit is unknown at this time
                        break;
                    case MOODLE_COURSE:
                        html = html + "You have 30 days to complete this assignment from your hiring date. It is due by " + moodleDueDate + "<br><br>";
                        break;
                    case VIDEO_CODE_ENTRY:
                        html = html + "<br>"; //Time limit is unknown at this time
                        break;
                    case EVERFI_COURSE:
                        html = html + "<br>"; //Time limit is defined by everfi
                        break;
                    case ETHICS_COURSE:
                        html = html + "<br>"; //Time limit is unknown at this time
                        break;
                    case ETHICS_LIVE_COURSE:
                        if (existingEmployee) {
                            html = html + "You have until the end of the " + LocalDate.now().getYear() +
                                    " calendar year to complete this course. These live sessions run once a month.<br><br>";
                        } else {
                            html = html + "You have 90 days to complete this assignment from your hiring date: "
                                    + activeServiceDate + ". It is due by: " + ethicsLiveNewEmpDueDate +
                                    ". These live sessions run once a month.<br><br>";
                        }
                        break;
                    default:
                        logger.error("" + new Exception("Unrecognized Personnel Task Type"));
                }
            }
        }

        if (pecTestMode) {
            html = html + "<br> Employee ID: " + employee.getEmployeeId() + "<br> Email: " + employee.getEmail();
        }

        return html;
    }
}
