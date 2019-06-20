package gov.nysenate.ess.core.dao.pec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.*;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
public class PersonnelAssignedTaskDaoIT extends BaseTest {

    @Autowired private PersonnelAssignedTaskDao taskDao;

    @Test
    public void insertUpdateTest() {
        final int empId = 123456789;
        final PersonnelTaskId taskId = new PersonnelTaskId(DOCUMENT_ACKNOWLEDGMENT, 999);
        final PersonnelAssignedTask initialTask =
                new PersonnelAssignedTask(empId, taskId, null, null, false);
        List<PersonnelAssignedTask> tasks;
        // Make sure the test employee doesn't have any tasks
        tasks = taskDao.getTasksForEmp(empId);
        assertTrue(tasks.isEmpty());
        // Add an initial task
        taskDao.updatePersonnelAssignedTask(initialTask);
        // Verify that initial task is present
        tasks = taskDao.getTasksForEmp(empId);
        assertEquals(1, tasks.size());
        assertEquals(initialTask, tasks.get(0));
        // Update the initial task with different data
        final PersonnelAssignedTask updatedTask =
                new PersonnelAssignedTask(empId, taskId, LocalDateTime.now(), empId, true);
        assertNotEquals(initialTask, updatedTask);
        taskDao.updatePersonnelAssignedTask(updatedTask);
        // Ensure that it was updated
        tasks = taskDao.getTasksForEmp(empId);
        assertEquals(1, tasks.size());
        assertEquals(updatedTask, tasks.get(0));
    }

    @Test
    public void queryTest() {
        final ImmutableList<PersonnelTaskType> taskTypes = ImmutableList.copyOf(EnumSet.allOf(PersonnelTaskType.class));
        final ImmutableList<Integer> bogusTaskNums = ImmutableList.of(111111, 222222, 333333);
        final ImmutableList<Integer> bogusEmpIds = ImmutableList.of(9999999, 5555555, 1111111);
        boolean completedValue = true;

        // Generate combination of all possible type/num/empids from the above lists, with alternating complete vals
        ImmutableList.Builder<PersonnelAssignedTask> taskListBuilder = ImmutableList.builder();
        for (PersonnelTaskType taskType : taskTypes) {
            for (int taskNum : bogusTaskNums) {
                PersonnelTaskId taskId = new PersonnelTaskId(taskType, taskNum);
                for (int empId : bogusEmpIds) {
                    taskListBuilder.add(new PersonnelAssignedTask(empId, taskId, null, null, completedValue));
                    completedValue = !completedValue;
                }
            }
        }
        final ImmutableList<PersonnelAssignedTask> allTasks = taskListBuilder.build();

        // Insert test tasks
        allTasks.forEach(taskDao::updatePersonnelAssignedTask);

        // Run queries and verify the results

        // Empty query should return all tasks
        PATQueryBuilder emptyQuery = new PATQueryBuilder();
        queryResultAssertion(emptyQuery, allTasks, task -> true);

        // Query by task type
        PATQueryBuilder taskTypeQuery = new PATQueryBuilder()
                .setTaskType(DOCUMENT_ACKNOWLEDGMENT);
        queryResultAssertion(taskTypeQuery, allTasks, task -> task.getTaskType() == DOCUMENT_ACKNOWLEDGMENT);

        // Query by completed status
        PATQueryBuilder completedQuery = new PATQueryBuilder().setCompleted(true);
        queryResultAssertion(completedQuery, allTasks, PersonnelAssignedTask::isCompleted);

        // Query by type and completed status
        PATQueryBuilder completedCodeQuery = new PATQueryBuilder()
                .setTaskType(VIDEO_CODE_ENTRY)
                .setCompleted(true);
        queryResultAssertion(completedCodeQuery, allTasks,
                task -> task.getTaskType() == VIDEO_CODE_ENTRY && task.isCompleted());

        // Query by task id
        PersonnelTaskId taskId = new PersonnelTaskId(taskTypes.get(0), bogusTaskNums.get(0));
        PATQueryBuilder taskIdQuery = new PATQueryBuilder()
                .setTaskIds(Collections.singleton(taskId));
        queryResultAssertion(taskIdQuery, allTasks, task -> taskId.equals(task.getTaskId()));

        Set<PersonnelTaskId> taskIds = Sets.newHashSet(
                new PersonnelTaskId(taskTypes.get(0), bogusTaskNums.get(0)),
                new PersonnelTaskId(taskTypes.get(0), bogusTaskNums.get(1)),
                new PersonnelTaskId(taskTypes.get(1), bogusTaskNums.get(1))
        );
        PATQueryBuilder multiTaskIdQuery = new PATQueryBuilder()
                .setTaskIds(taskIds);
        queryResultAssertion(multiTaskIdQuery, allTasks, task -> taskIds.contains(task.getTaskId()));

        // Query incomplete tasks for specific emp
        int firstEmpId = bogusEmpIds.get(0);
        PATQueryBuilder incEmpIdQuery = new PATQueryBuilder()
                .setEmpId(firstEmpId)
                .setCompleted(false);
        queryResultAssertion(incEmpIdQuery, allTasks, task -> task.getEmpId() == firstEmpId && !task.isCompleted());
    }

    /**
     * Run the given query, asserting that the results all match the given predicate,
     * and that all of the given sample tasks matching the predicate are included in the results.
     * Also asserts that at least one of the sample tasks match the predicate.
     */
    private void queryResultAssertion(PATQueryBuilder query,
                                      List<PersonnelAssignedTask> sampleTasks,
                                      Predicate<PersonnelAssignedTask> predicate) {
        HashSet<PersonnelAssignedTask> unseenExpected =
                sampleTasks.stream().filter(predicate).collect(Collectors.toCollection(HashSet::new));
        assertFalse("No sample tasks match the test predicate", unseenExpected.isEmpty());
        List<PersonnelAssignedTask> results = taskDao.getTasks(query);
        for (PersonnelAssignedTask result : results) {
            unseenExpected.remove(result);
            assertTrue(query + " should not return " + result, predicate.test(result));
        }
        assertEquals("Unseen expected results should be empty for " + query,
                Collections.emptySet(), unseenExpected);
    }
}