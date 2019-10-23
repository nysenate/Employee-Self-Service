package gov.nysenate.ess.core.dao.pec;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
public class PersonnelTaskAssignmentDaoIT extends BaseTest {

    @Autowired private PersonnelTaskAssignmentDao taskDao;

    /*
     * TODO find a way of creating mock tasks
     *  - Assignments now have a foreign key referencing tasks, so we can't just insert an assignment for a bogus task.
     */

    @Ignore("We don't have a way to generate test tasks yet...")
    @Test
    public void insertUpdateTest() {
        final int empId = 123456789;
        final int taskId = 1234554321;
        final PersonnelTaskAssignment initialTask = PersonnelTaskAssignment.newTask(empId, taskId);
        List<PersonnelTaskAssignment> tasks;
        // Make sure the test employee doesn't have any tasks
        tasks = taskDao.getTasksForEmp(empId);
        assertTrue(tasks.isEmpty());
        // Add an initial task
        taskDao.updateAssignment(initialTask);
        // Verify that initial task is present
        tasks = taskDao.getTasksForEmp(empId);
        assertEquals(1, tasks.size());
        assertEquals(initialTask, tasks.get(0));
        // Update the initial task with different data
        final PersonnelTaskAssignment updatedTask =
                new PersonnelTaskAssignment(empId, taskId, empId, LocalDateTime.now(), true, true);
        assertNotEquals(initialTask, updatedTask);
        taskDao.updateAssignment(updatedTask);
        // Ensure that it was updated
        tasks = taskDao.getTasksForEmp(empId);
        assertEquals(1, tasks.size());
        assertEquals(updatedTask, tasks.get(0));
    }

    @Ignore("We don't have a way to generate test tasks yet...")
    @Test
    public void queryTest() {
        final ImmutableList<Integer> bogusTaskIds = ImmutableList.of(111111, 222222, 333333);
        final ImmutableList<Integer> bogusEmpIds = ImmutableList.of(9999999, 5555555, 1111111);
        boolean completedValue = true;

        // Generate combination of all possible type/num/empids from the above lists, with alternating complete vals
        ImmutableList.Builder<PersonnelTaskAssignment> taskListBuilder = ImmutableList.builder();
        for (int taskId : bogusTaskIds) {
            for (int empId : bogusEmpIds) {
                LocalDateTime timestamp = completedValue ? LocalDateTime.now() : null;
                taskListBuilder.add(new PersonnelTaskAssignment(
                        empId, taskId, null, timestamp, completedValue, true));
                completedValue = !completedValue;
            }
        }
        final ImmutableList<PersonnelTaskAssignment> allTasks = taskListBuilder.build();

        // Insert test tasks
        allTasks.forEach(taskDao::updateAssignment);

        // Run queries and verify the results

        // Empty query should return all tasks
        PTAQueryBuilder emptyQuery = new PTAQueryBuilder();
        queryResultAssertion(emptyQuery, allTasks, task -> true);

        // Query by task type
        PTAQueryBuilder taskTypeQuery = new PTAQueryBuilder()
                .setTaskType(DOCUMENT_ACKNOWLEDGMENT);
//        queryResultAssertion(taskTypeQuery, allTasks, task -> task.getTaskType() == DOCUMENT_ACKNOWLEDGMENT);

        // Query by completed status
        PTAQueryBuilder completedQuery = new PTAQueryBuilder().setCompleted(true);
        queryResultAssertion(completedQuery, allTasks, PersonnelTaskAssignment::isCompleted);

        // Query by completion date
        PTAQueryBuilder partialCompDateQuery = new PTAQueryBuilder()
                .setCompletedFrom(LocalDateTime.now())
                .setCompletedTo(LocalDateTime.now().plusMinutes(1));
        queryResultAssertion(partialCompDateQuery, allTasks, task -> true);

//        List<LocalDateTime> firstTimestamps = allTasks.stream()
//                .map(PersonnelTaskAssignment::getTimestamp)
//                .filter(Objects::nonNull)
//                .limit(10).collect(toList());
//        LocalDateTime compFrom = firstTimestamps.get(0);
//        LocalDateTime compTo = firstTimestamps.get(firstTimestamps.size() - 1);
//        Range<LocalDateTime> compRange = Range.closed(compFrom, compTo);
//        PTAQueryBuilder compDateQuery = new PTAQueryBuilder()
//                .setCompleted(true)
//                .setCompletedFrom(compFrom)
//                .setCompletedTo(compTo);
//        queryResultAssertion(compDateQuery, allTasks,
//                task -> task.isCompleted() && task.getTimestamp() != null && compRange.contains(task.getTimestamp()));

//        // Query by type and completed status
//        PTAQueryBuilder completedCodeQuery = new PTAQueryBuilder()
//                .setTaskType(VIDEO_CODE_ENTRY)
//                .setCompleted(true);
//        queryResultAssertion(completedCodeQuery, allTasks,
//                task -> task.getTaskType() == VIDEO_CODE_ENTRY && task.isCompleted());

//        // Query by task id
//        PersonnelTaskId taskId = new PersonnelTaskId(taskTypes.get(0), bogusTaskIds.get(0));
//        PTAQueryBuilder taskIdQuery = new PTAQueryBuilder()
//                .setTaskIds(Collections.singleton(taskId));
//        queryResultAssertion(taskIdQuery, allTasks, task -> taskId.equals(task.getTaskId()));
//
//        Set<PersonnelTaskId> taskIds = Sets.newHashSet(
//                new PersonnelTaskId(taskTypes.get(0), bogusTaskIds.get(0)),
//                new PersonnelTaskId(taskTypes.get(0), bogusTaskIds.get(1)),
//                new PersonnelTaskId(taskTypes.get(1), bogusTaskIds.get(1))
//        );
//        PTAQueryBuilder multiTaskIdQuery = new PTAQueryBuilder()
//                .setTaskIds(taskIds);
//        queryResultAssertion(multiTaskIdQuery, allTasks, task -> taskIds.contains(task.getTaskId()));
//
//        // Query incomplete tasks for specific emp
//        int firstEmpId = bogusEmpIds.get(0);
//        PTAQueryBuilder incEmpIdQuery = new PTAQueryBuilder()
//                .setEmpId(firstEmpId)
//                .setCompleted(false);
//        queryResultAssertion(incEmpIdQuery, allTasks, task -> task.getEmpId() == firstEmpId && !task.isCompleted());
    }

    /**
     * Run the given query, asserting that the results all match the given predicate,
     * and that all of the given sample tasks matching the predicate are included in the results.
     * Also asserts that at least one of the sample tasks match the predicate.
     */
    private void queryResultAssertion(PTAQueryBuilder query,
                                      List<PersonnelTaskAssignment> sampleTasks,
                                      Predicate<PersonnelTaskAssignment> predicate) {
        HashSet<PersonnelTaskAssignment> unseenExpected =
                sampleTasks.stream().filter(predicate).collect(Collectors.toCollection(HashSet::new));
        assertFalse("No sample tasks match the test predicate", unseenExpected.isEmpty());
        List<PersonnelTaskAssignment> results = taskDao.getTasks(query);
        for (PersonnelTaskAssignment result : results) {
            unseenExpected.remove(result);
            assertTrue(query + " should not return " + result, predicate.test(result));
        }
        assertEquals("Unseen expected results should be empty for " + query,
                Collections.emptySet(), unseenExpected);
    }
}