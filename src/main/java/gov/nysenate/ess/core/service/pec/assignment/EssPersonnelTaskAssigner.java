package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.pec.notification.EmployeeEmail;
import gov.nysenate.ess.core.service.pec.task.CachedPersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final PersonnelTaskDao personnelTaskDao;
    private final PersonnelTaskAssignmentDao assignmentDao;

    private final CachedPersonnelTaskService cachedPersonnelTaskService;

    @Value("${scheduler.personnel_task.assignment.enabled:true}")
    private boolean scheduledAssignmentEnabled;

    /** Classes which handle assignment for different {@link PersonnelTaskAssignmentGroup} */
    private final List<GroupTaskAssigner> groupTaskAssigners;

    public EssPersonnelTaskAssigner(EmployeeInfoService empInfoService,
                                    EmpTransactionService transactionService,
                                    List<GroupTaskAssigner> groupTaskAssigners,
                                    PersonnelTaskDao personnelTaskDao,
                                    CachedPersonnelTaskService cachedPersonnelTaskService,
                                    PersonnelTaskAssignmentDao assignmentDao,
                                    EventBus eventBus) {
        this.empInfoService = empInfoService;
        this.transactionService = transactionService;
        this.groupTaskAssigners = groupTaskAssigners;
        this.personnelTaskDao = personnelTaskDao;
        this.assignmentDao = assignmentDao;
        this.cachedPersonnelTaskService = cachedPersonnelTaskService;
        eventBus.register(this);
    }

    @Override
    public void assignTasks() {
        logger.info("Determining and assigning personnel tasks for all active employees...");
        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();
        activeEmpIds.stream().sorted().forEach(this::assignTasks);
        logger.info("Personnel task assignment completed for all active employees.");
        cachedPersonnelTaskService.markTasksComplete();
    }

    @Override
    public List<EmployeeEmail> getInviteEmails() {
        var emails = new ArrayList<EmployeeEmail>();
        for (int empId : empInfoService.getActiveEmpIds()) {
            if (!needsTaskAssignment(empId)) {
                continue;
            }
            for (var assigner : groupTaskAssigners) {
                emails.addAll(assigner.getGroupInviteEmails(empId));
            }
        }
        return emails;
    }

    @Override
    public void assignTasks(int empId) {
        if (needsTaskAssignment(empId)) {
            logger.info("Performing task assignment for emp #{} ...", empId);
            groupTaskAssigners.forEach(groupAssigner -> groupAssigner.assignGroupTasks(empId));
            logger.info("Completed task assignment for emp #{}.", empId);
        }
        else {
            logger.info("Skipping task assignment for ineligible emp #{}", empId);
        }
    }

    @Override
    public void updateAssignedTaskCompletion(int empID, int updateEmpID, boolean completed, int taskID) {
        personnelTaskDao.updatePersonnelAssignedTaskCompletion(empID, updateEmpID, completed, taskID);

        logger.info("Task assignment " + taskID + " was updated for Employee " + empID +
        " by employee " + updateEmpID + ". Its completion status is " + completed);
    }

    @Override
    public void updateAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID) {
        personnelTaskDao.updatePersonnelAssignedTaskAssignment(empID,updateEmpID,assigned,taskID);
    }

    @Override
    public void generateDueDatesForExistingTaskAssignments() {
        logger.info("Beginning Date Assignment Processing");
        for (Integer empId : empInfoService.getActiveEmpIds()) {
            List<PersonnelTaskAssignment> empAssignments = assignmentDao.getAssignmentsForEmp(empId);
            for (PersonnelTaskAssignment assignment : empAssignments) {
                if (!assignment.isActive() || assignment.isCompleted() || assignment.getDueDate() != null) {
                    continue;
                }
                PersonnelTaskType type;
                if (assignment.getTaskId() == 5) {
                    type = PersonnelTaskType.MOODLE_COURSE;
                }
                else if (assignment.getTaskId() == 16) {
                    type = PersonnelTaskType.ETHICS_LIVE_COURSE;
                }
                else {
                    continue;
                }
                LocalDate continuousServiceDate = empInfoService.getEmployeesMostRecentContinuousServiceDate(empId);
                assignmentDao.updateAssignmentDates(assignment.withDates(continuousServiceDate, type, false));
                logger.info("Completed update for Emp: " + assignment.getEmpId() +
                        ". Updated Task ID " + assignment.getTaskId());
            }
        }
        logger.info("Completed Date Assignment Processing");
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
                .filter(this::needsTaskAssignment)
                .forEach(this::assignTasks);
        cachedPersonnelTaskService.markTasksComplete();
    }

    /**
     * Handles scheduled task assignments.
     */
    @Scheduled(cron = "${scheduler.personnel_task.assignment.cron:0 0 3 * * *}")
    public void scheduledPersonnelTaskAssignment() {
        if (scheduledAssignmentEnabled) {
            assignTasks();
        }
    }

    /* --- Internal Methods --- */

    /**
     * Determine if the employee is eligible for task assignment.
     */
    private boolean needsTaskAssignment(int empId) {
        TransactionHistory transHistory = transactionService.getTransHistory(empId);
        Range<LocalDate> presentAndFuture = Range.atLeast(LocalDate.now());
        // They are eligible if they are currently active, or will be active in the future.
        return transHistory.getActiveDates().intersects(presentAndFuture);
    }
}
