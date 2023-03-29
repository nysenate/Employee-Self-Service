package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.service.pec.notification.EmployeeEmail;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseGroupTaskAssigner implements GroupTaskAssigner {

    private static final Logger logger = LoggerFactory.getLogger(BaseGroupTaskAssigner.class);

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final PersonnelTaskService taskService;
    private final PECNotificationService pecNotificationService;
    private final EmployeeInfoService employeeInfoService;

    public BaseGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                 PersonnelTaskService taskService,
                                 PECNotificationService pecNotificationService,
                                 EmployeeInfoService employeeInfoService) {
        this.assignmentDao = assignmentDao;
        this.taskService = taskService;
        this.pecNotificationService = pecNotificationService;
        this.employeeInfoService = employeeInfoService;
    }

    @Override
    public int assignGroupTasks(int empId) {
        return assignTasks(empId, getRequiredTaskIds(empId), true).size();
    }

    @Override
    public List<EmployeeEmail> getGroupInviteEmails(int empId) {
        return assignTasks(empId, getRequiredTaskIds(empId), false);
    }

    protected List<PersonnelTask> getActiveGroupTasks() {
        return taskService.getActiveTasksInGroup(getTargetGroup());
    }

    private List<EmployeeEmail> assignTasks(int empId, Set<Integer> assignableTaskIds, boolean sendUpdates) {
        var emails = new LinkedList<EmployeeEmail>();
        Map<Integer, PersonnelTask> personnelTaskMap = buildPersonnelTaskMap(taskService.getPersonnelTasks(false));
        Map<Integer, PersonnelTaskAssignment> assignmentMap = getAssignmentMap(empId);
        Set<Integer> existingTaskIds = assignmentMap.keySet();
        assignableTaskIds = removeManualOverrides(assignableTaskIds, empId);
        existingTaskIds = removeManualOverrides(existingTaskIds, empId);

        // Get active tasks that are not currently assigned to the employee.
        Set<PersonnelTask> activeUnassigned = Sets.difference(assignableTaskIds, existingTaskIds)
                .stream().map(personnelTaskMap::get).collect(Collectors.toSet());

        for (PersonnelTask task : activeUnassigned) {
            LocalDate continuousServiceDate = employeeInfoService.getEmployeesMostRecentContinuousServiceDate(empId);

            PersonnelTaskAssignment assignmentWithDueDate = PersonnelTaskAssignment.newTask(empId, task.getTaskId())
                    .withDates(continuousServiceDate, task.getTaskType(), true);
            var emailOpt = pecNotificationService.getInviteEmail(empId, task, assignmentWithDueDate.getDueDate());
            if (emailOpt.isEmpty()) {
                continue;
            }
            emails.add(emailOpt.get());
            if (sendUpdates) {
                assignmentDao.updateAssignment(assignmentWithDueDate);
                logger.info("Assigning {} personnel tasks to emp #{} : Task ID #{}",
                        getTargetGroup(), empId, task.getTaskId());
                pecNotificationService.sendEmail(emailOpt.get());
            }
        }
        if (!sendUpdates) {
            return emails;
        }

        // Get tasks assigned to the employee that are not active.
        Set<PersonnelTask> inactiveAssigned = Sets.difference(existingTaskIds, assignableTaskIds)
                .stream().filter(taskId -> !assignmentMap.get(taskId).isCompleted())
                .map(personnelTaskMap::get)
                .filter(task -> !task.isActive()).collect(Collectors.toSet());
        // Deactivate inactive tasks that have not been completed.
        for (PersonnelTask task : inactiveAssigned) {
            assignmentDao.deactivatePersonnelTaskAssignment(empId, task.getTaskId());
            logger.info("Deactivating {} task for emp #{} : Task ID #{}",
                    getTargetGroup(), empId, task.getTaskId());
        }
        return emails;
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

    private boolean assignmentInGroup(PersonnelTaskAssignment assignment) {
        PersonnelTask task = taskService.getPersonnelTask(assignment.getTaskId());
        return task.getAssignmentGroup() == getTargetGroup();
    }

    private Set<Integer> removeManualOverrides(Set<Integer> taskIds, int empId) {
        // In case the set is immutable.
        taskIds = new HashSet<>(taskIds);
        taskIds.removeIf(taskId -> {
            try {
                return assignmentDao.getManualOverrideStatus(empId, taskId);
            }
            catch (EmptyResultDataAccessException e) {
                return false;
            }
        });
        return taskIds;
    }

    private static Map<Integer, PersonnelTask> buildPersonnelTaskMap(List<PersonnelTask> allPersonnelTasks) {
        HashMap<Integer, PersonnelTask> personnelTaskMap = new HashMap<>();
        for (PersonnelTask task: allPersonnelTasks) {
            personnelTaskMap.put(task.getTaskId(), task);
        }
        return personnelTaskMap;
    }
}
