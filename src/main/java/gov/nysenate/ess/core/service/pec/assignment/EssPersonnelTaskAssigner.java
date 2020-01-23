package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionHistoryUpdateEvent;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
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
    private final boolean scheduledAssignmentEnabled;
    private final PersonnelTaskDao personnelTaskDao;

    /** Classes which handle assignment for different {@link PersonnelTaskAssignmentGroup} */
    private final List<GroupTaskAssigner> groupTaskAssigners;

    public EssPersonnelTaskAssigner(EmployeeInfoService empInfoService,
                                    EmpTransactionService transactionService,
                                    List<GroupTaskAssigner> groupTaskAssigners,
                                    PersonnelTaskDao personnelTaskDao,
                                    @Value("${scheduler.personnel_task.assignment.enabled:true}")
                                            boolean scheduledAssignmentEnabled,
                                    EventBus eventBus) {
        this.empInfoService = empInfoService;
        this.transactionService = transactionService;
        this.scheduledAssignmentEnabled = scheduledAssignmentEnabled;
        this.groupTaskAssigners = groupTaskAssigners;
        this.personnelTaskDao = personnelTaskDao;
        eventBus.register(this);
    }

    @Override
    public void assignTasks() {
        logger.info("Determining and assigning personnel tasks for all active employees...");
        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();
        activeEmpIds.stream().sorted().forEach(this::assignTasks);
        logger.info("Personnel task assignment completed for all active employees.");
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
    }

    @Override
    public void updateAssignedTaskAssignment(int empID, int updateEmpID, boolean assigned, int taskID) {
        personnelTaskDao.updatePersonnelAssignedTaskAssignment(empID,updateEmpID,assigned,taskID);
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

    /**
     * Determine if the employee is eligible for task assignment.
     */
    private boolean needsTaskAssignment(int empId) {
        TransactionHistory transHistory = transactionService.getTransHistory(empId);
        Range<LocalDate> presentAndFuture = Range.atLeast(LocalDate.now());
        // They are are eligible if they are currently active, or will be active in the future.
        return transHistory.getActiveDates().intersects(presentAndFuture)
                && !empInfoService.getEmployee(empId).isSenator();
    }

}
