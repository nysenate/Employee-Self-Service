package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.service.pec.assignment.PersonnelTaskAssigner;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(DatabaseConfig.localTxManager)
public class EssPersonnelTaskAssignerIT extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EssPersonnelTaskAssignerIT.class);

    @Autowired private PersonnelTaskAssigner taskAssigner;
    @Autowired private PersonnelTaskService taskService;
    @Autowired private PersonnelTaskAssignmentDao taskDao;
    @Autowired private EmployeeInfoService empInfoService;

    @Autowired private PersonnelTaskTestDao taskTestDao;

    @Test
    public void empTaskAssignTest() {
        final int bogusEmpId = 1122334455;
        assertTrue("Test employee has no initial assignments",
                taskDao.getAssignmentsForEmp(bogusEmpId).isEmpty());

        taskAssigner.assignTasks(bogusEmpId);

        Set<Integer> tasksPresent = taskDao.getAssignmentsForEmp(bogusEmpId).stream()
                .map(PersonnelTaskAssignment::getTaskId)
                .collect(Collectors.toSet());

        Set<Integer> allTaskIds = taskService.getAllTaskIds(true);

        assertEquals("Test employee is assigned all active tasks", allTaskIds, tasksPresent);
    }

    @Test
    public void deactivateTaskTest() {
        final int bogusEmpId = 1122334455;
        assertTrue("Test employee has no initial assignments",
                taskDao.getAssignmentsForEmp(bogusEmpId).isEmpty());

        final Set<Integer> allTaskIds = taskService.getAllTaskIds(true);

        // Assign all tasks
        taskAssigner.assignTasks(bogusEmpId);

        // Manually add a bogus task
        final PersonnelTask bogusTask = taskTestDao.insertDummyTask();
        final int bogusTaskId = bogusTask.getTaskId();
        final PersonnelTaskAssignment bogusTaskAssigment = PersonnelTaskAssignment.newTask(bogusEmpId, bogusTaskId);
        taskDao.updateAssignment(bogusTaskAssigment);

        Set<Integer> preDeactivateTasks = taskDao.getAssignmentsForEmp(bogusEmpId).stream()
                .map(PersonnelTaskAssignment::getTaskId)
                .collect(Collectors.toSet());

        assertEquals("Bogus task + all tasks should be assigned to employee before deactivation",
                Sets.union(allTaskIds, Collections.singleton(bogusTaskId)), preDeactivateTasks);

        // Run task assignment (the bogus task assignment should be deactivated here)
        taskAssigner.assignTasks(bogusEmpId);

        Set<Integer> postDeactivateTasks = taskDao.getAssignmentsForEmp(bogusEmpId).stream()
                .map(PersonnelTaskAssignment::getTaskId)
                .collect(Collectors.toSet());
        assertEquals("All tasks should be assigned to employee", allTaskIds, postDeactivateTasks);
        assertFalse("Employee should no longer be assigned bogus task", postDeactivateTasks.contains(bogusTaskId));
    }

    @Test
    public void assignAllEmpsTest() {
        final Set<Integer> allTaskIds = taskService.getAllTaskIds(true);
        if (allTaskIds.isEmpty()) {
            // If there are no tasks to assign this test won't work properly.
            logger.warn("Skipping \"assignAllEmpsTest\" due to lack of active tasks.");
            return;
        }
        logger.info("assigning tasks to all employees...");
        taskAssigner.assignTasks();
        logger.info("done assigning tasks");

        List<PersonnelTaskAssignment> allTasks = taskDao.getTasks(new PTAQueryBuilder());
        HashMultimap<Integer, Integer> empTaskMultimap = allTasks.stream().collect(Multimaps.toMultimap(
                PersonnelTaskAssignment::getEmpId,
                PersonnelTaskAssignment::getTaskId,
                HashMultimap::create
        ));

        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();

        for (int empId : activeEmpIds) {
            assertTrue("Active employee " + empId + " must have active tasks", empTaskMultimap.containsKey(empId));
            assertTrue("Active employee " + empId + " must be assigned all currently active tasks",
                    empTaskMultimap.get(empId).containsAll(allTaskIds));
        }
    }

}