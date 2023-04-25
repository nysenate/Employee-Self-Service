package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.assignment.CsvTaskAssigner;
import gov.nysenate.ess.core.service.pec.assignment.PersonnelTaskAssigner;
import gov.nysenate.ess.core.service.pec.external.PECVideoCSVService;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import gov.nysenate.ess.core.service.pec.task.CachedPersonnelTaskService;
import gov.nysenate.ess.core.service.pec.view.EmployeeEmailView;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;
import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.RUN_PERSONNEL_TASK_ASSIGNER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/personnel/task")
public class PersonnelTaskAdminApiCtrl extends BaseRestApiCtrl {

    private final PersonnelTaskAssigner taskAssigner;
    private final PECVideoCSVService pecVideoCSVService;
    private final CsvTaskAssigner csvTaskAssigner;
    private final CachedPersonnelTaskService cachedPersonnelTaskService;

    private final PECNotificationService pecNotificationService;

    @Autowired
    public PersonnelTaskAdminApiCtrl(PersonnelTaskAssigner taskAssigner, PECVideoCSVService pecVideoCSVService,
                                     CsvTaskAssigner csvTaskAssigner, CachedPersonnelTaskService cachedPersonnelTaskService,
                                     PECNotificationService pecNotificationService) {
        this.taskAssigner = taskAssigner;
        this.pecVideoCSVService = pecVideoCSVService;
        this.csvTaskAssigner = csvTaskAssigner;
        this.cachedPersonnelTaskService = cachedPersonnelTaskService;
        this.pecNotificationService = pecNotificationService;
    }

    /**
     * PEC Reminder Notification API
     * --------------------------
     * Determine personnel tasks for all employees and send emails to remind employees to complete them
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/notify
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/notify", method = POST)
    public SimpleResponse sendNotifications() {
        checkPermission(ADMIN.getPermission());
        pecNotificationService.sendReminderEmails();
        return new SimpleResponse(true,
                "pec notifications complete",
                "pec-notifications-complete");
    }

    /**
     * PEC Send Invites API
     * --------------------------
     * Send invites to all necessary employees.
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/sendInvites
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/sendInvites", method = POST)
    public SimpleResponse sendInvites() {
        checkPermission(ADMIN.getPermission());
        pecNotificationService.sendInviteEmails();
        return new SimpleResponse(true, "Invite emails sent!", "send-invites");
    }

    /**
     * PEC Assignment & Due Dates For Moodle & Ethics Live
     * ---------------------------------------------------
     *
     * This Api call will generate the assignment date and due dates for moodle and ethics live course for employees
     *
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/generate/taskdates
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/generate/taskdates", method = POST)
    public SimpleResponse generateTaskDates() {
        checkPermission(ADMIN.getPermission());
        taskAssigner.generateDueDatesForExistingTaskAssignments();
        return new SimpleResponse(true,
                "pec task date generation complete",
                "pec-task-date-generation-complete");
    }

    /**
     * Assign Personnel Tasks API
     * --------------------------
     *
     * Determine personnel tasks for all employees and assign those that are missing.
     *
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/assign
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/assign", method = POST)
    public SimpleResponse assignTasks() {
        checkPermission(RUN_PERSONNEL_TASK_ASSIGNER.getPermission());
        taskAssigner.assignTasks(true);
        pecNotificationService.sendInviteEmails();
        return new SimpleResponse(true,
                "task assignment complete",
                "task-assignment-complete");
    }

    /**
     * Get Invite Emails API
     * --------------------------
     * Determine personnel tasks for all employees and assign those that are missing.
     * Usage:
     * (GET)   /api/v1/admin/personnel/task/scheduledInviteEmails
     * @return {@link ListViewResponse<EmployeeEmailView>}
     */
    @RequestMapping(value = "/scheduledInviteEmails", method = GET)
    public ListViewResponse<EmployeeEmailView> getScheduledInviteEmails() {
        checkPermission(RUN_PERSONNEL_TASK_ASSIGNER.getPermission());
        var assignableTasks = taskAssigner.assignTasks(false);
        return ListViewResponse.of(pecNotificationService.getInviteEmails(assignableTasks).stream()
                .map(EmployeeEmailView::new).collect(Collectors.toList()));
    }

    /**
     * Get ReminderEmails API
     * --------------------------
     * Determine personnel tasks for all employees and assign those that are missing.
     * Usage:
     * (GET)   /api/v1/admin/personnel/task/scheduledReminderEmails
     * @return {@link ListViewResponse<EmployeeEmailView>}
     */
    @RequestMapping(value = "/scheduledReminderEmails", method = GET)
    public ListViewResponse<EmployeeEmailView> getScheduledReminderEmails() {
        checkPermission(RUN_PERSONNEL_TASK_ASSIGNER.getPermission());
        return ListViewResponse.of(pecNotificationService.getReminderEmails(false)
                .stream().map(EmployeeEmailView::new).collect(Collectors.toList()));
    }

    /**
     * Assign Personnel Tasks for Emp API
     * ----------------------------------
     *
     * Determine personnel tasks for a single employee and assign those that are missing.
     *
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/assign/{empId}
     *
     * Path params:
     * @param empId int - employee id (must be active)
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/assign/{empId:\\d+}", method = POST)
    public SimpleResponse assignTasksForEmp(@PathVariable int empId) {
        checkPermission(RUN_PERSONNEL_TASK_ASSIGNER.getPermission());
        ensureEmpIdActive(empId, "empId");
        taskAssigner.assignTasks(empId, true);
        pecNotificationService.sendInviteEmails();
        return new SimpleResponse(true,
                "task assignment complete for emp#" + empId,
                "task-assignment-complete");
    }

    /**
     * Parse Out Personnel Tasks for PEC Videos
     * ----------------------------------
     *
     * Process personnel task records from csv files
     *
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/csv
     *
     * Path params:
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/csv", method = POST)
    public SimpleResponse getTasksFromCSV() throws IOException {
        checkPermission(ADMIN.getPermission());
        pecVideoCSVService.processCSVReports();
        return new SimpleResponse(true,
                "Records have been parsed from the CSV files",
                "task-assignment-complete");
    }

    /**
     * Personnel Task Assignment Completion Override API
     * ------------------------------------
     *
     * Updates the Completion status of a task for an employee
     *
     * Usage:
     * (GET)   /api/v1/admin/personnel/task/overrride/{updateEmpID}/{taskID}/{completed}/{empID}
     *
     * Path params:
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/overrride/{updateEmpID}/{taskID}/{completed}/{empID}", method = GET)
    public SimpleResponse overrideTaskCompletion(@PathVariable int updateEmpID,
                                                 @PathVariable int taskID,
                                                 @PathVariable boolean completed,
                                                 @PathVariable int empID) throws AuthorizationException {
        Subject subject = SecurityUtils.getSubject();

        boolean isAdmin = subject.hasRole("ADMIN");
        boolean isPecManager = subject.hasRole("PERSONNEL_COMPLIANCE_MANAGER");
        if ( isPecManager || isAdmin ) {
            taskAssigner.updateAssignedTaskCompletion(empID,updateEmpID,completed,taskID);
            return new SimpleResponse(true,
                    "Task assignment " + taskID + " was updated for Employee " + empID +
                            " by employee " + updateEmpID + ". Its completion status is " + completed,
                    "employee-task-override");
        }
        return new SimpleResponse(false,
                "You do not have permission to execute this api functionality",
                "employee-task-override");
    }

    /**
     * Personnel Task Assignment activation Override API
     * ------------------------------------
     *
     * Updates the activation status of a task for an employee
     *
     * Usage:
     * (GET)   /api/v1/admin/personnel/task/overrride/activation/{updateEmpID}/{taskID}/{activeStatus}/{empID}
     *
     * Path params:
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/overrride/activation/{updateEmpID}/{taskID}/{activeStatus}/{empID}", method = GET)
    public SimpleResponse overrideTaskActivation(@PathVariable int updateEmpID,
                                                 @PathVariable int taskID,
                                                 @PathVariable boolean activeStatus,
                                                 @PathVariable int empID) throws AuthorizationException {
        Subject subject = SecurityUtils.getSubject();

        boolean isAdmin = subject.hasRole("ADMIN");
        boolean isPecManager = subject.hasRole("PERSONNEL_COMPLIANCE_MANAGER");
        if ( isPecManager || isAdmin ) {
            taskAssigner.updateAssignedTaskActiveStatus(empID,updateEmpID,activeStatus,taskID);
            return new SimpleResponse(true,
                    "Task assignment " + taskID + " was updated for Employee " + empID +
                            " by employee " + updateEmpID + ". Its active status is " + activeStatus,
                    "employee-task-override");
        }
        return new SimpleResponse(false,
                "You do not have permission to execute this api functionality",
                "employee-task-override");
    }


    /**
     * Update Personnel Task Assignment API
     * ------------------------------------
     *
     * This api call updates the assigned task of a given employee. It can assign or unassign a task
     *
     * Usage:
     * (GET)   /api/v1/admin/personnel/task/overrride/assign/{updateEmpID}/{taskID}/{assigned}/{empID}
     *
     * Path params:
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/overrride/assign/{updateEmpID}/{taskID}/{completed}/{empID}", method = GET)
    public SimpleResponse overrideTaskAssignment(@PathVariable int updateEmpID,
                                                 @PathVariable int taskID,
                                                 @PathVariable boolean completed,
                                                 @PathVariable int empID) throws AuthorizationException {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("ADMIN") || subject.hasRole("PERSONNEL_COMPLIANCE_MANAGER") ) {
            taskAssigner.updateAssignedTaskAssignment(empID, updateEmpID, completed, taskID);
            return new SimpleResponse(true,
                    "Task assignment " + taskID + " was updated for Employee " + empID +
                            " by employee " + updateEmpID + ". Its assignment status is " + completed,
                    "employee-task-override");
        }
        return new SimpleResponse(false,
                "You do not have permission to execute this api functionality",
                "employee-task-override");
    }

    /**
     * Update Personnel Task Assignment API
     * ------------------------------------
     *
     * This api call updates assigned tasks for multiple employees based off of a csv file
     *
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/override/csv/assign/{sts}
     *
     * Path params:
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/override/csv/assign/{sts}", method = POST)
    public SimpleResponse overrideTaskAssignmentFromCSV(@PathVariable boolean sts) throws AuthorizationException, IOException {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("ADMIN") || subject.hasRole("PERSONNEL_COMPLIANCE_MANAGER") ) {

            if (sts) {
                csvTaskAssigner.processCSVForSTSManualAssignments();
            }
            else {
                csvTaskAssigner.processCSVForPersonnelManualAssignments();
            }

            return new SimpleResponse(true,
                    "The batch assignment csv was processed successfully",
                    "employee-task-override");
        }
        return new SimpleResponse(false,
                "You do not have permission to execute this api functionality",
                "employee-task-override");
    }


    /**
     * Mark Specific Individuals Complete
     * ----------------------------------
     *
     * Mark specific employees Personnel task assignments complete
     *
     * Usage:
     * (POST)   /api/v1/admin/personnel/task/mark/complete
     *
     * Path params:
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/mark/complete", method = POST)
    public SimpleResponse markTasksComplete() {
        checkPermission(ADMIN.getPermission());
        cachedPersonnelTaskService.markTasksComplete();
        return new SimpleResponse(true,
                "The tasks have been marked complete",
                "tasks-marked-complete");
    }
}
