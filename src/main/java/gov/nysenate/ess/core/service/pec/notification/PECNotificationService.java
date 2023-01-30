package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);

    private EmployeeDao employeeDao;

    private PersonnelTaskService taskService;

    private PersonnelTaskAssignmentDao assignmentDao;
    private SendMailService sendMailService;

    private Map<Integer, PersonnelTask> activeTaskMap;

    @Value("${scheduler.pec.notifs.enabled:false}")
    private boolean pecNotifsEnabled;

    @Value("${pec.test.mode:true}")
    private boolean pecTestMode;

    private List<String> reportEmails;

    public PECNotificationService(EmployeeDao employeeDao, PersonnelTaskService taskService,
                                  PersonnelTaskAssignmentDao assignmentDao, SendMailService sendMailService,
                                  @Value("${report.email}") String reportEmailList) {
        this.employeeDao = employeeDao;
        this.taskService = taskService;
        this.assignmentDao = assignmentDao;
        this.sendMailService = sendMailService;
        Map<Integer, PersonnelTask> activeTaskMap = new HashMap<>();
        for (PersonnelTask task : taskService.getPersonnelTasks(true)) {
            activeTaskMap.put(task.getTaskId(), task);
        }
        this.activeTaskMap = activeTaskMap;
        this.reportEmails = Arrays.asList(reportEmailList.replaceAll(" ","").split(","));
    }

    @Scheduled(cron = "${scheduler.pec.notifs.cron}")
    public void runUpdateMethods() throws Exception {
        if (pecNotifsEnabled) {
            runPECNotificationProcess();
        }
    }

    //TODO fix email logic after testing
    public void runPECNotificationProcess() throws Exception {
        logger.info("Starting PEC Notification Process");

        //Document all employees with missing info to report to the admins
        List<Employee> employeesWithMissingEmails = new ArrayList<>();

        //Get All active employees
        Set<Employee> activeEmployees = employeeDao.getActiveEmployees();

        //Cycle thru all employees
        for (Employee employee : activeEmployees) {

            //Get all assignments for the given employee
            List<PersonnelTaskAssignment> empAssignments = assignmentDao.getAssignmentsForEmp(employee.getEmployeeId());

            //Determine if they have any outstanding active tasks
            List<PersonnelTaskAssignment> incompleteEmpAssignments = new ArrayList<>();
            for (PersonnelTaskAssignment assignment : empAssignments) {
                if (assignment.isActive() && !assignment.isCompleted()) {
                    incompleteEmpAssignments.add(assignment);
                }
            }

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

    public void sendInviteEmail(int empID, List<Integer> taskIDs) {

        ArrayList<PersonnelTask> tasks = new ArrayList<>();

        for (Integer taskID : taskIDs) {
            tasks.add(taskService.getPersonnelTask(taskID));
        }

        Employee employee = employeeDao.getEmployeeById(empID);

        String recipientEmail;
        if (pecTestMode) {
            recipientEmail = this.reportEmails.get(0);
        }
        else {
            recipientEmail = employee.getEmail();
        }
        String subject;
        String html;
        if (tasks.size() == 1) {
            subject = "You have completed the task:" + tasks.get(0).getTitle();
            html = "<b>Our records indicate you have an outstanding task to complete for personnel.</b><br>\n" +
                    "You can find instructions to complete them by logging into ESS, " +
                    "clicking the My Info tab and then the To Do List<br><br>\n\n" +
                    "<b>You must complete the following tasks: </b><br><br>\n\n";
        }
        else {
            subject = employee.getFullName() + " ,You have multiple pending Personnel tasks";
            html = "<b>Our records indicate you have an outstanding tasks to complete for personnel.</b><br>\n" +
                    "You can find instructions to complete them by logging into ESS, " +
                    "clicking the My Info tab and then the To Do List<br><br>\n\n" +
                    "<b>You must complete the following tasks: </b><br><br>\n\n";
        }
        sendEmail(recipientEmail, subject, html);
    }

    public void sendCompletionEmail(int empID, int taskID) {
        PersonnelTask task = taskService.getPersonnelTask(taskID);

        Employee employee = employeeDao.getEmployeeById(empID);

        String recipientEmail;
        if (pecTestMode) {
            recipientEmail = this.reportEmails.get(0);
        }
        else {
            recipientEmail = employee.getEmail();
        }
        String subject = "You have completed the task:" + task.getTitle();
        String html = "<b>Our records have been updated to indicated you have completed " + task.getTitle() + "<b>";

        sendEmail(recipientEmail, subject, html);
    }

    // Determine the email address & content for their email, then send the email
    private void contructAndSendEmailToTheEmployee(Employee employee, List<PersonnelTaskAssignment> incompleteEmpAssignments) throws Exception {

        //TODO implement start date data with employee objects for more accurate emails

        //Get basic employee data
        String recipientEmail;
        if (pecTestMode) {
            recipientEmail = this.reportEmails.get(0);
        }
        else {
            recipientEmail = employee.getEmail();
        }

        String subject = employee.getFullName() + " you have outstanding Personnel Tasks";

        //Standard instructions for all tasks
        String html = "<b>Our records indicate you have outstanding tasks to complete for personnel.</b><br>\n" +
                "You can find instructions to complete them by logging into ESS, " +
                "clicking the My Info tab and then the To Do List<br><br>\n\n" +
                "<b>You must complete the following tasks: </b><br><br>\n\n";

        //Add known time limit text to the emails
        for (PersonnelTaskAssignment assignment : incompleteEmpAssignments) {
            PersonnelTask task = this.activeTaskMap.get( assignment.getTaskId() );

            html = html + task.getTitle() + "<br>\n";

            switch (task.getTaskType()) {
                case DOCUMENT_ACKNOWLEDGMENT:
                    html = html + ""; //Time limit is unknown at this time
                    break;
                case MOODLE_COURSE:
                    html = html + " You have 30 days to complete this assignment from your hiring date<br>\n";
                    break;
                case VIDEO_CODE_ENTRY:
                    html = html + ""; //Time limit is unknown at this time
                    break;
                case EVERFI_COURSE:
                    html = html + ""; //Time limit is unknown at this time
                    break;
                case ETHICS_COURSE:
                    html = html + ""; //Time limit is unknown at this time
                    break;
                case ETHICS_LIVE_COURSE:
                    html = html + " You have 90 days to complete this assignment if you were newly hired / are a returning employee. " +
                "Existing employees must finish this course before the end of the calendar year. These live sessions run once a month.<br>\n";
                    break;
                default:
                    throw new Exception("Unrecognized Personnel Task Type");
            }
        }

        //Send email to employee
        sendEmail(recipientEmail, subject, html);
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = sendMailService.newHtmlMessage(to.trim(),
                    subject, html);
            sendMailService.send(message);
        }
        catch (Exception e) {
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
        for (Employee employee: emps) {
            employeeListDetails = employeeListDetails + " NAME: " + employee.getFullName() + " EMPID: " + employee.getEmployeeId() + "<br>\n";
        }
        return employeeListDetails;
    }
}
