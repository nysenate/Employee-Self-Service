package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.everfi.EverfiUserDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserService;
import gov.nysenate.ess.core.service.pec.notification.AssignmentWithTask;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.APP;
import static gov.nysenate.ess.core.model.transaction.TransactionCode.RTP;

/**
 * Assigns tasks to employees based on the currently active personnel tasks and employees' current tasks.
 */
@Service
public class EssPersonnelTaskAssigner implements PersonnelTaskAssigner {
    private static final Logger logger = LoggerFactory.getLogger(EssPersonnelTaskAssigner.class);
    private static final ImmutableSet<TransactionCode> newEmpCodes = ImmutableSet.of(APP, RTP);

    private final EmployeeInfoService empInfoService;
    private final EmpTransactionService transactionService;
    private final EverfiUserService everfiUserService;
    private final SendMailService sendMailService;
    private final PersonnelTaskDao personnelTaskDao;
    private final EverfiUserDao everfiUserDao;
    private final EmployeeDao employeeDao;
    private final PersonnelTaskAssignmentDao assignmentDao;
    private final List<String> everfiReportEmails;

    /** Classes which handle assignment for different {@link PersonnelTaskAssignmentGroup} */
    private final List<GroupTaskAssigner> groupTaskAssigners;

    public EssPersonnelTaskAssigner(EmployeeInfoService empInfoService,
                                    EmpTransactionService transactionService, SendMailService sendMailService, EverfiUserDao everfiUserDao,
                                    List<GroupTaskAssigner> groupTaskAssigners,
                                    PersonnelTaskDao personnelTaskDao,
                                    PersonnelTaskAssignmentDao assignmentDao,
                                    EventBus eventBus, EverfiUserService everfiUserService, EmployeeDao employeeDao, @Value("${everfi.report.email}") String everfiReportEmailList) {
        this.empInfoService = empInfoService;
        this.transactionService = transactionService;
        this.sendMailService = sendMailService;
        this.everfiUserDao = everfiUserDao;
        this.groupTaskAssigners = groupTaskAssigners;
        this.personnelTaskDao = personnelTaskDao;
        this.assignmentDao = assignmentDao;
        this.everfiUserService = everfiUserService;
        this.employeeDao = employeeDao;
        this.everfiReportEmails = Arrays.asList(everfiReportEmailList.replaceAll(" ","").split(","));
        eventBus.register(this);
    }

    @Override
    public List<AssignmentWithTask> assignTasks(boolean updateDb) {
        if (updateDb) {
            logger.info("Determining and assigning personnel tasks for all active employees...");
        }
        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();
        var result = activeEmpIds.stream().sorted()
                .map(empId -> assignTasks(empId, updateDb))
                .flatMap(List::stream).collect(Collectors.toList());
        if (updateDb) {
            logger.info("Personnel task assignment completed for all active employees.");
        }
        return result;
    }

    @Override
    public List<AssignmentWithTask> assignTasks(int empId, boolean updateDb) {
        if (needsTaskAssignment(empId)) {
            return groupTaskAssigners.stream()
                    .map(groupAssigner -> groupAssigner.assignGroupTasks(empId, updateDb))
                    .flatMap(List::stream).collect(Collectors.toList());
        }
        else {
            if (updateDb) {
                logger.info("Skipping task assignment for ineligible emp #{}", empId);
            }
            return List.of();
        }
    }

    @Override
    public void updateAssignedTaskCompletion(int empID, int updateEmpID, boolean completed, int taskID) {
        personnelTaskDao.updatePersonnelAssignedTaskCompletion(empID, updateEmpID, completed, taskID);

        logger.info("Task assignment " + taskID + " was updated for Employee " + empID +
        " by employee " + updateEmpID + ". Its completion status is " + completed);
    }

    @Override
    public void updateAssignedTaskActiveStatus(int empID, int updateEmpID, boolean activeStatus, int taskID) {
        personnelTaskDao.updatePersonnelAssignedActiveStatus(empID, updateEmpID, activeStatus, taskID);

        logger.info("Task assignment " + taskID + " was updated for Employee " + empID +
                " by employee " + updateEmpID + ". Its active status status is " + activeStatus);
    }

    @Override
    public void updateAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID) {
        personnelTaskDao.updatePersonnelAssignedTaskAssignment(empID,updateEmpID,assigned,taskID);
    }

    @Override
    public void generateDueDatesForExistingTaskAssignments(boolean overrideExistingDueDates) {
        logger.info("Beginning Date Assignment Processing");
        List<PersonnelTask> personnelTasks = personnelTaskDao.getAllTasks();
        for (Integer empId : empInfoService.getActiveEmpIds()) {
            List<PersonnelTaskAssignment> empAssignments = assignmentDao.getAssignmentsForEmp(empId);
            boolean hasCompletedAnEthicsLiveTraining = false;

            for (PersonnelTaskAssignment assignment : empAssignments) {
                for (PersonnelTask task: personnelTasks) {
                    if (assignment.getTaskId() == task.getTaskId() && assignment.isCompleted()
                            && task.getTaskType() == PersonnelTaskType.ETHICS_LIVE_COURSE) {
                        hasCompletedAnEthicsLiveTraining = true;
                    }
                }
            }

            for (PersonnelTaskAssignment assignment : empAssignments) {
                if (!assignment.isActive() || assignment.isCompleted()) {
                    continue;
                }
                if ( !overrideExistingDueDates && assignment.getDueDate() != null) {
                    continue;
                }
                PersonnelTaskType type = getPersonnelTaskType(assignment, personnelTasks);
                if (type == null) {
                    continue;
                }
                LocalDate continuousServiceDate = empInfoService.getEmployeesMostRecentContinuousServiceDate(empId);
                assignmentDao.updateAssignmentDates(assignment.withDates(continuousServiceDate, type, false, hasCompletedAnEthicsLiveTraining));
                logger.info("Completed update for Emp: " + assignment.getEmpId() +
                        ". Updated Task ID " + assignment.getTaskId());
            }
        }
        logger.info("Completed Date Assignment Processing");
    }

    @Override
    public void insertAssignedTask(int empID, int updateEmpID, int taskID) {

        checkEverfiRecords(taskID, empID);
        personnelTaskDao.insertPersonnelAssignedTask(empID, updateEmpID, taskID);

        logger.info("Task assignment " + taskID + " was updated for Employee " + empID +
                " by employee " + updateEmpID);
    }

    private void checkEverfiRecords(int taskID, int empID) {
        PersonnelTask task = personnelTaskDao.getPersonnelTask(taskID);
        List<Employee> emps = new ArrayList<>();
        emps.add(employeeDao.getEmployeeById(empID));
        if (task.getTaskType() == PersonnelTaskType.EVERFI_COURSE) {
            EverfiUserIDs everfiUserIDs = everfiUserDao.getEverfiUserIDsWithEmpID(empID);
            if (everfiUserIDs == null && employeeDao.getEmployeeById(empID).getEmail() == null) {
                sendEmailToEverfiReportEmails("Check this employee: ",
                        generateEmployeeListString(emps) + "is not registered in Everfi and didn't have the email.");
            }
            else if (everfiUserIDs == null) {
                everfiUserService.addEmployeesToEverfi(emps);
                sendEmailToEverfiReportEmails("Task Assigned for this employee:  ",
                        generateEmployeeListString(emps) + "This is registered in Everfi.");
            }
            else {
                sendEmailToEverfiReportEmails("Check this employee: ",
                        generateEmployeeListString(emps) + "is registered in Everfi but the task is not assigned.");
            }
        }
    }

    private void sendEmailToEverfiReportEmails(String subject, String html) {
        for (String email : this.everfiReportEmails) {
            System.out.println(email);
            sendEmail(email, subject, html);
        }
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            sendMailService.sendMessage(StringUtils.trim(to), subject, html);
//            sendMailService.send(message);
        }
        catch (Exception e) {
            logger.error("There was an error trying to send the Everfi report email ", e);
        }
    }

    private String generateEmployeeListString(List<Employee> emps) {
        StringBuilder employeeListDetails = new StringBuilder();
        for (Employee employee: emps) {
            employeeListDetails.append(" NAME: ").append(employee.getFullName())
                    .append(" EMAIL: ").append(employee.getEmail()).append(" EMPID: ")
                    .append(employee.getEmployeeId()).append("<br>\n");
        }

        if (employeeListDetails.isEmpty()) {
            employeeListDetails = new StringBuilder("There are no employees to perform this operation on");
        }
        return employeeListDetails.toString();
    }

    private PersonnelTaskType getPersonnelTaskType(PersonnelTaskAssignment assignment, List<PersonnelTask> personnelTasks) {
        PersonnelTaskType type = null;
        for (PersonnelTask task: personnelTasks) {
            if (assignment.getTaskId() == task.getTaskId() && task.getTaskType() == PersonnelTaskType.MOODLE_COURSE) {
                type = PersonnelTaskType.MOODLE_COURSE;
            }
            else if (assignment.getTaskId() == task.getTaskId() && task.getTaskType() == PersonnelTaskType.ETHICS_LIVE_COURSE) {
                type = PersonnelTaskType.ETHICS_LIVE_COURSE;
            }
        }
        return type;
    }

    /**
     * Detect transaction posts for new or reappointed employees and assign tasks to the employees.
     * @param txUpdateEvent {@link TransactionHistoryUpdateEvent}
     */
    @Subscribe
    public void assignTasksToNewEmps(TransactionHistoryUpdateEvent txUpdateEvent) {
        txUpdateEvent.getTransRecs().stream()
                .filter(rec -> newEmpCodes.contains(rec.getTransCode()))
                .map(TransactionRecord::getEmployeeId)
                .distinct()
                .forEach(empId -> assignTasks(empId, true));
    }

    /* --- Internal Methods --- */

    /**
     * Determine if the employee is eligible for task assignment.
     */
    private boolean needsTaskAssignment(int empId) {
        if (empInfoService.getEmployee(empId).isSenator()) {
            return false;
        }
        TransactionHistory transHistory = transactionService.getTransHistory(empId);
        Range<LocalDate> presentAndFuture = Range.atLeast(LocalDate.now());
        // They are eligible if they are currently active, or will be active in the future.
        return transHistory.getActiveDates().intersects(presentAndFuture);
    }
}
