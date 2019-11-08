package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    private final PersonnelTaskService taskService;
    private final PersonnelTaskAssignmentDao taskDao;
    private final EmployeeInfoService empInfoService;
    private final EmpTransactionService transactionService;
    private final boolean scheduledAssignmentEnabled;

    public EssPersonnelTaskAssigner(PersonnelTaskService taskService,
                                    PersonnelTaskAssignmentDao taskDao,
                                    EmployeeInfoService empInfoService,
                                    EmpTransactionService transactionService,
                                    @Value("${scheduler.personnel_task.assignment.enabled:true}")
                                            boolean scheduledAssignmentEnabled,
                                    EventBus eventBus) {
        this.taskService = taskService;
        this.taskDao = taskDao;
        this.empInfoService = empInfoService;
        this.transactionService = transactionService;
        this.scheduledAssignmentEnabled = scheduledAssignmentEnabled;
        eventBus.register(this);
    }

    @Override
    public void assignTasks() {
        logger.info("Determining and assigning personnel tasks for all active employees...");
        Set<Integer> activeTaskIds = getAllActiveTaskIds();
        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();
        activeEmpIds.stream()
                .sorted()
                .forEach(empId -> assignTasks(empId, activeTaskIds));
        logger.info("Personnel task assignment completed...");
    }

    @Override
    public void assignTasks(int empId) {
        logger.info("Determining personnel tasks for emp #{} ...", empId);
        Set<Integer> activeTaskIds = getAllActiveTaskIds();
        assignTasks(empId, activeTaskIds);
        logger.info("Completed task assignment for emp #{} ...", empId);
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
    }

    /**
     * Handles scheduled task assignments.
     */
    @Scheduled(cron = "${scheduler.personnel_task.assignment.cron:0 0 1 * * *}")
    public void scheduledPersonnelTaskAssignment() {
        if (scheduledAssignmentEnabled) {
            assignTasks();
        }
    }

    /* --- Internal Methods --- */

    private Set<Integer> getAllActiveTaskIds() {
        return taskService.getAllTaskIds(true);
    }

    /**
     * Create and save new tasks for the employee based on active tasks that are currently unassigned.
     */
    private void assignTasks(int empId, Set<Integer> activeTaskIds) {
        Set<Integer> existingTaskIds = taskDao.getAssignmentsForEmp(empId).stream()
                .map(PersonnelTaskAssignment::getTaskId)
                .collect(Collectors.toSet());

        // Get active tasks that are not currently assigned to the employee.
        Set<Integer> activeUnassigned = Sets.difference(activeTaskIds, existingTaskIds);
        // Get tasks assigned to the employee that are not active.
        Set<Integer> inactiveAssigned = Sets.difference(existingTaskIds, activeTaskIds);

        // Assign new tasks for active unassigned.
        List<PersonnelTaskAssignment> newTasks = activeUnassigned.stream()
                .map(taskId -> PersonnelTaskAssignment.newTask(empId, taskId))
                .collect(Collectors.toList());
        if (!newTasks.isEmpty()) {
            logger.info("Assigning {} personnel tasks to emp #{} : {}",
                    newTasks.size(),
                    empId,
                    newTasks.stream()
                            .map(PersonnelTaskAssignment::getTaskId)
                            .collect(Collectors.toList()));
        }
        newTasks.forEach(taskDao::updateAssignment);

        // Deactivate assigned tasks that are inactive.
        if (!inactiveAssigned.isEmpty()) {
            logger.info("Deactivating {} inactive tasks for emp #{} : {}",
                    inactiveAssigned.size(), empId, inactiveAssigned);
        }
        inactiveAssigned.forEach(taskId -> taskDao.deactivatePersonnelTaskAssignment(empId, taskId));
    }

    /**
     * Determine if the employee is eligible for task assignment.
     */
    private boolean needsTaskAssignment(int empId) {
        TransactionHistory transHistory = transactionService.getTransHistory(empId);
        Range<LocalDate> presentAndFuture = Range.atLeast(LocalDate.now());
        // They are are eligible if they are currently active, or will be active in the future.
        return transHistory.getActiveDates().intersects(presentAndFuture);
    }

}
