package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assigns tasks to employees based on the currently active personnel tasks and employees' current tasks.
 */
@Service
public class EssPersonnelTaskAssigner implements PersonnelTaskAssigner {

    private static final Logger logger = LoggerFactory.getLogger(EssPersonnelTaskAssigner.class);

    private final PersonnelTaskSource taskSource;
    private final PersonnelAssignedTaskDao taskDao;
    private final EmployeeInfoService empInfoService;

    public EssPersonnelTaskAssigner(PersonnelTaskSource taskSource,
                                    PersonnelAssignedTaskDao taskDao,
                                    EmployeeInfoService empInfoService) {
        this.taskSource = taskSource;
        this.taskDao = taskDao;
        this.empInfoService = empInfoService;
    }

    @Override
    public void assignTasks() {
        logger.info("Determining and assigning personnel tasks for all active employees...");
        Set<PersonnelTaskId> activeTaskIds = getAllActiveTaskIds();
        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();
        activeEmpIds.stream()
                .sorted()
                .forEach(empId -> assignTasks(empId, activeTaskIds));
        logger.info("Personnel task assignment completed...");
    }

    @Override
    public void assignTasks(int empId) {
        logger.info("Determining personnel tasks for emp #{} ...", empId);
        Set<PersonnelTaskId> activeTaskIds = getAllActiveTaskIds();
        assignTasks(empId, activeTaskIds);
        logger.info("Completed task assignment for emp #{} ...", empId);
    }

    /* --- Internal Methods --- */

    private Set<PersonnelTaskId> getAllActiveTaskIds() {
        return taskSource.getAllPersonnelTaskIds();
    }

    /**
     * Create and save new tasks for the employee based on active tasks that are currently unassigned.
     */
    private void assignTasks(int empId, Set<PersonnelTaskId> activeTaskIds) {
        Set<PersonnelTaskId> existingTaskIds = taskDao.getTasksForEmp(empId).stream()
                .map(PersonnelAssignedTask::getTaskId)
                .collect(Collectors.toSet());

        Set<PersonnelTaskId> difference = Sets.difference(activeTaskIds, existingTaskIds);

        List<PersonnelAssignedTask> newTasks = difference.stream()
                .map(taskId -> PersonnelAssignedTask.newTask(empId, taskId))
                .collect(Collectors.toList());

        if (!newTasks.isEmpty()) {
            logger.info("Assigning {} personnel tasks to emp #{} :", newTasks.size(), empId);
            newTasks.forEach(task -> logger.info("\t{}", task.getTaskId()));
        }

        newTasks.forEach(taskDao::updatePersonnelAssignedTask);
    }

}
