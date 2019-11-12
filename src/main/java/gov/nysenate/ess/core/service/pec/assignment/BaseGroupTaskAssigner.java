package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseGroupTaskAssigner implements GroupTaskAssigner {

    private static final Logger logger = LoggerFactory.getLogger(BaseGroupTaskAssigner.class);

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final PersonnelTaskService taskService;

    public BaseGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                   PersonnelTaskService taskService) {
        this.assignmentDao = assignmentDao;
        this.taskService = taskService;
    }

    protected List<PersonnelTask> getActiveGroupTasks() {
        return taskService.getActiveTasksInGroup(getTargetGroup());
    }

    protected int assignTasks(int empId, Set<Integer> assignableTaskIds) {
        Set<Integer> existingTaskIds = getAssignedIds(empId);

        // Get active tasks that are not currently assigned to the employee.
        Set<Integer> activeUnassigned = Sets.difference(assignableTaskIds, existingTaskIds);
        // Get tasks assigned to the employee that are not active.
        Set<Integer> inactiveAssigned = Sets.difference(existingTaskIds, assignableTaskIds);

        // Assign new tasks for active unassigned.
        List<PersonnelTaskAssignment> newAssignments = activeUnassigned.stream()
                .map(taskId -> PersonnelTaskAssignment.newTask(empId, taskId))
                .collect(Collectors.toList());
        if (!newAssignments.isEmpty()) {
            logger.info("Assigning {} {} personnel tasks to emp #{} : {}",
                    newAssignments.size(),
                    getTargetGroup(),
                    empId,
                    newAssignments.stream()
                            .map(PersonnelTaskAssignment::getTaskId)
                            .collect(Collectors.toList()));
        }
        newAssignments.forEach(assignmentDao::updateAssignment);

        // Deactivate assigned tasks that are inactive.
        if (!inactiveAssigned.isEmpty()) {
            logger.info("Deactivating {} inactive {} tasks for emp #{} : {}",
                    inactiveAssigned.size(), getTargetGroup(), empId, inactiveAssigned);
        }
        inactiveAssigned.forEach(taskId -> assignmentDao.deactivatePersonnelTaskAssignment(empId, taskId));
        return newAssignments.size();
    }

    protected List<PersonnelTaskAssignment> getGroupAssignments(int empId) {
        return assignmentDao.getAssignmentsForEmp(empId).stream()
                .filter(this::assignmentInGroup)
                .collect(Collectors.toList());
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
}
