package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseGroupTaskAssigner implements GroupTaskAssigner {

    private static final Logger logger = LoggerFactory.getLogger(BaseGroupTaskAssigner.class);

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final PersonnelTaskService taskService;

    private final PECNotificationService pecNotificationService;

    public BaseGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                   PersonnelTaskService taskService,
                                 PECNotificationService pecNotificationService) {
        this.assignmentDao = assignmentDao;
        this.taskService = taskService;
        this.pecNotificationService = pecNotificationService;
    }

    protected List<PersonnelTask> getActiveGroupTasks() {
        return taskService.getActiveTasksInGroup(getTargetGroup());
    }

    protected int assignTasks(int empId, Set<Integer> assignableTaskIds) {
        Map<Integer, PersonnelTask> personnelTaskMap = buildPersonnelTaskMap(taskService.getPersonnelTasks(false));

        Map<Integer, PersonnelTaskAssignment> assignmentMap = getAssignmentMap(empId);

        Set<Integer> existingTaskIds = assignmentMap.keySet();

        // Get active tasks that are not currently assigned to the employee.
        Set<Integer> activeUnassigned = Sets.difference(assignableTaskIds, existingTaskIds);

        // Get tasks assigned to the employee that are not active.
        Set<Integer> inactiveAssigned = Sets.difference(existingTaskIds, assignableTaskIds);

        // Assign new tasks for active unassigned.
        List<PersonnelTaskAssignment> newAssignments = activeUnassigned.stream()
                .map(taskId -> PersonnelTaskAssignment.newTask(empId, taskId))
                .collect(Collectors.toList());

        for (PersonnelTaskAssignment assignment : newAssignments) {
            boolean taskHasBeenManuallyOverridden = false;
            try {
                taskHasBeenManuallyOverridden = assignmentDao.getManualOverrideStatus(empId,assignment.getTaskId());
            }
            catch (EmptyResultDataAccessException e) {
                //No need to do anything. It means we are going to update this task in the database
            }
            if (!taskHasBeenManuallyOverridden) {
                //Get assignment due date
                LocalDate continuousServiceDate = pecNotificationService.getConitnuousServiceDate(empId);
                PersonnelTaskType taskType = personnelTaskMap.get( assignment.getTaskId() ).getTaskType();

                LocalDateTime dueDate = null;
                if (taskType == PersonnelTaskType.MOODLE_COURSE) {
                    dueDate = pecNotificationService.getDueDate(continuousServiceDate,30).atTime(0,0);
                } else if (taskType == PersonnelTaskType.ETHICS_LIVE_COURSE) {
                    if (pecNotificationService.isExistingEmployee(continuousServiceDate)) {
                        dueDate = LocalDate.of(LocalDate.now().getYear(), 12,31).atTime(0,0);
                    }
                    else {
                        dueDate = pecNotificationService.getDueDate(continuousServiceDate,90).atTime(0,0);
                    }
                }
                PersonnelTaskAssignment assignmentWithDueDate = new PersonnelTaskAssignment(
                        assignment.getTaskId(),assignment.getEmpId(), assignment.getUpdateEmpId(),
                        assignment.getUpdateTime(), assignment.isCompleted(), assignment.isActive(),
                        LocalDateTime.now(), dueDate);
                assignmentDao.updateAssignment(assignmentWithDueDate);
                logger.info("Assigning {} personnel tasks to emp #{} : Task ID #{}",
                        getTargetGroup(), empId, assignment.getTaskId() );
                pecNotificationService.sendInviteEmails(empId, assignmentWithDueDate);
            }
        }

        // Deactivate inactive tasks that have not been completed.
        Set<Integer> idsToDeactivate = inactiveAssigned.stream()
                .filter(taskId -> !assignmentMap.get(taskId).isCompleted())
                .collect(Collectors.toSet());
        for (Integer taskId: idsToDeactivate) {
            boolean taskHasBeenManuallyOverridden = false;
            try {
                taskHasBeenManuallyOverridden = assignmentDao.getManualOverrideStatus(empId,taskId);
            }
            catch (EmptyResultDataAccessException e) {
                //No need to do anything. It means we are going to update this task in the database
            }
            if (!taskHasBeenManuallyOverridden && !personnelTaskMap.get(taskId).isActive()) {
                //only deactivate if it was not manually overridden
                assignmentDao.deactivatePersonnelTaskAssignment(empId, taskId);
                logger.info("Deactivating {} task for emp #{} : Task ID #{}",
                        getTargetGroup(), empId, idsToDeactivate);
            }
        }
        return newAssignments.size();
    }

    protected List<PersonnelTaskAssignment> getGroupAssignments(int empId) {
        return assignmentDao.getAssignmentsForEmp(empId).stream()
                .filter(this::assignmentInGroup)
                .collect(Collectors.toList());
    }

    protected Map<Integer, PersonnelTaskAssignment> getAssignmentMap(int empId) {
        return Maps.uniqueIndex(getGroupAssignments(empId), PersonnelTaskAssignment::getTaskId);
    }

    protected Set<Integer> getAssignedIds(int empId) {
        return getGroupAssignments(empId).stream()
                .map(PersonnelTaskAssignment::getTaskId)
                .collect(Collectors.toSet());
    }

    protected boolean assignmentInGroup(PersonnelTaskAssignment assignment) {
        PersonnelTask task = taskService.getPersonnelTask(assignment.getTaskId());
        return task.getAssignmentGroup() == getTargetGroup();
    }

    private Map<Integer, PersonnelTask> buildPersonnelTaskMap(List<PersonnelTask> allPersonnelTasks) {
        HashMap<Integer, PersonnelTask> personnelTaskMap = new HashMap<>();
        for (PersonnelTask task: allPersonnelTasks) {
            personnelTaskMap.put(task.getTaskId(), task);
        }
        return personnelTaskMap;
    }
}
