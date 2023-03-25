package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.pec.notification.EmployeeEmail;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
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

    private void removeManualOverrides(Set<Integer> taskIds, int empId) {
        taskIds.removeIf(taskId -> {
            try {
                return assignmentDao.getManualOverrideStatus(empId, taskId);
            }
            catch (EmptyResultDataAccessException e) {
                return false;
            }
        });
    }

    protected int assignTasks(int empId, Set<Integer> assignableTaskIds) {
        return assignTasks(empId, assignableTaskIds, true).size();
    }

    protected List<EmployeeEmail> assignTasks(int empId, Set<Integer> assignableTaskIds, boolean sendUpdates) {
        var emails = new LinkedList<EmployeeEmail>();
        Map<Integer, PersonnelTask> personnelTaskMap = buildPersonnelTaskMap(taskService.getPersonnelTasks(false));
        Map<Integer, PersonnelTaskAssignment> assignmentMap = getAssignmentMap(empId);
        Set<Integer> existingTaskIds = assignmentMap.keySet();
        removeManualOverrides(assignableTaskIds, empId);
        removeManualOverrides(existingTaskIds, empId);

        // Get active tasks that are not currently assigned to the employee.
        Set<PersonnelTask> activeUnassigned = Sets.difference(assignableTaskIds, existingTaskIds)
                .stream().map(personnelTaskMap::get).collect(Collectors.toSet());

        for (PersonnelTask task : activeUnassigned) {
            LocalDate continuousServiceDate = pecNotificationService.getContinuousServiceDate(empId);
            var assignmentWithDueDate = getAssignment(empId, task, continuousServiceDate);
            var emailOpt = pecNotificationService.getInviteEmail(empId, task, assignmentWithDueDate.getDueDate());
            if (emailOpt.isEmpty()) {
                continue;
            }
            emails.add(emailOpt.get());
            if (sendUpdates) {
                assignmentDao.updateAssignment(assignmentWithDueDate);
                logger.info("Assigning {} personnel tasks to emp #{} : Task ID #{}",
                        getTargetGroup(), empId, task.getTaskId());
                pecNotificationService.sendInviteEmail(emails.getLast());
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

    protected boolean assignmentInGroup(PersonnelTaskAssignment assignment) {
        PersonnelTask task = taskService.getPersonnelTask(assignment.getTaskId());
        return task.getAssignmentGroup() == getTargetGroup();
    }

    private static Map<Integer, PersonnelTask> buildPersonnelTaskMap(List<PersonnelTask> allPersonnelTasks) {
        HashMap<Integer, PersonnelTask> personnelTaskMap = new HashMap<>();
        for (PersonnelTask task: allPersonnelTasks) {
            personnelTaskMap.put(task.getTaskId(), task);
        }
        return personnelTaskMap;
    }

    private static PersonnelTaskAssignment getAssignment(Integer empId,
                                                         PersonnelTask task,
                                                         LocalDate continuousServiceDate) {
        var assignment = PersonnelTaskAssignment.newTask(empId, task.getTaskId());
        LocalDate dueDate = getDueDate(continuousServiceDate, task.getTaskType());
        return new PersonnelTaskAssignment(
                assignment.getTaskId(), assignment.getEmpId(), assignment.getUpdateEmpId(),
                assignment.getUpdateTime(), assignment.isCompleted(), assignment.isActive(),
                LocalDateTime.now(), dueDate == null ? null : dueDate.atStartOfDay());
    }

    private static LocalDate getDueDate(LocalDate continuousServiceDate, PersonnelTaskType type) {
        LocalDate dueDate = null;
        if (type == PersonnelTaskType.MOODLE_COURSE) {
            dueDate = addDays(continuousServiceDate,30);
        } else if (type == PersonnelTaskType.ETHICS_LIVE_COURSE) {
            LocalDate ninetyDaysAgo = LocalDate.now(ZoneId.systemDefault()).minus(Period.ofDays(90));
            // Checks whether this is an old employee.
            if (continuousServiceDate.isBefore(ninetyDaysAgo)) {
                dueDate = LocalDate.of(LocalDate.now().getYear(), 12,31);
            }
            else {
                dueDate = addDays(continuousServiceDate,90);
            }
        }
        return dueDate;
    }

    private static LocalDate addDays(LocalDate baseDate, int daysToAdd) {
        Date startDate = Date.from(baseDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = new GregorianCalendar(/* remember about timezone! */);
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, daysToAdd);
        return calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
