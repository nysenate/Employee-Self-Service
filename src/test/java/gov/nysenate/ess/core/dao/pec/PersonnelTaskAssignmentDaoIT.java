package gov.nysenate.ess.core.dao.pec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskTestBuilder;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.pec.PersonnelTaskTestDao;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;
import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.VIDEO_CODE_ENTRY;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
public class PersonnelTaskAssignmentDaoIT extends BaseTest {

    @Autowired private PersonnelTaskAssignmentDao assignmentDao;
    @Autowired private PersonnelTaskDao taskDao;

    @Autowired private PersonnelTaskTestDao taskTestDao;

    @Test
    public void insertUpdateTest() {
        final int empId = 123456789;
        final int taskId = taskTestDao.insertDummyTask().getTaskId();
        final PersonnelTaskAssignment initialAssignment = PersonnelTaskAssignment.newTask(empId, taskId);
        List<PersonnelTaskAssignment> assignments;
        // Make sure the test employee doesn't have any assignments
        assignments = assignmentDao.getAssignmentsForEmp(empId);
        assertTrue(assignments.isEmpty());
        // Add an initial assignment
        assignmentDao.updateAssignment(initialAssignment);
        // Verify that initial assignment is present
        assignments = assignmentDao.getAssignmentsForEmp(empId);
        assertEquals(1, assignments.size());
        assertEquals(initialAssignment, assignments.get(0));
        // Update the initial assignment with different data
        final PersonnelTaskAssignment updatedTask =
                new PersonnelTaskAssignment(taskId, empId, empId, LocalDateTime.now(), true, true);
        assertNotEquals(initialAssignment, updatedTask);
        assignmentDao.updateAssignment(updatedTask);
        // Ensure that it was updated
        assignments = assignmentDao.getAssignmentsForEmp(empId);
        assertEquals(1, assignments.size());
        assertEquals(updatedTask, assignments.get(0));
    }

    @Test
    public void queryTest() {
        final ImmutableList<Integer> bogusEmpIds = ImmutableList.of(9999999, 5555555, 1111111);
        boolean completedValue = true;

        // Generate dummy tasks for each type, then assignments for each bogus emp with alternating completion
        ImmutableList.Builder<PersonnelTaskAssignment> assignListBuilder = ImmutableList.builder();
        ImmutableList.Builder<PersonnelTask> dummyTaskListBuilder = ImmutableList.builder();
        for (PersonnelTaskType taskType : PersonnelTaskType.values()) {
            PersonnelTask dummyTask = taskTestDao.insertDummyTask(
                    new PersonnelTaskTestBuilder().setTaskType(taskType));
            dummyTaskListBuilder.add(dummyTask);
            for (int empId : bogusEmpIds) {
                LocalDateTime timestamp = completedValue ? LocalDateTime.now() : null;
                assignListBuilder.add(new PersonnelTaskAssignment(
                        dummyTask.getTaskId(), empId, null, timestamp, completedValue, true));
                completedValue = !completedValue;
            }
        }
        final ImmutableList<PersonnelTaskAssignment> allAssignments = assignListBuilder.build();
        final ImmutableList<PersonnelTask> allDummyTasks = dummyTaskListBuilder.build();
        final ImmutableMap<Integer, PersonnelTask> dummyTaskMap =
                Maps.uniqueIndex(allDummyTasks, PersonnelTask::getTaskId);

        // Insert test tasks
        allAssignments.forEach(assignmentDao::updateAssignment);

        // Run queries and verify the results

        // Empty query should return all tasks
        PTAQueryBuilder emptyQuery = new PTAQueryBuilder();
        queryResultAssertion(emptyQuery, allAssignments, task -> true);

        // Query by task type
        PTAQueryBuilder taskTypeQuery = new PTAQueryBuilder()
                .setTaskType(DOCUMENT_ACKNOWLEDGMENT);
        queryResultAssertion(taskTypeQuery, allAssignments,
                assignment -> getTaskType(assignment) == DOCUMENT_ACKNOWLEDGMENT);

        // Query by completed status
        PTAQueryBuilder completedQuery = new PTAQueryBuilder().setCompleted(true);
        queryResultAssertion(completedQuery, allAssignments, PersonnelTaskAssignment::isCompleted);

        // Query by completion date
        PTAQueryBuilder partialCompDateQuery = new PTAQueryBuilder()
                .setCompletedFrom(LocalDateTime.now())
                .setCompletedTo(LocalDateTime.now().plusMinutes(1));
        queryResultAssertion(partialCompDateQuery, allAssignments, task -> true);

        List<LocalDateTime> firstTimestamps = allAssignments.stream()
                .map(PersonnelTaskAssignment::getUpdateTime)
                .filter(Objects::nonNull)
                .limit(10).collect(toList());
        LocalDateTime compFrom = firstTimestamps.get(0);
        LocalDateTime compTo = firstTimestamps.get(firstTimestamps.size() - 1);
        Range<LocalDateTime> compRange = Range.closed(compFrom, compTo);
        PTAQueryBuilder compDateQuery = new PTAQueryBuilder()
                .setCompleted(true)
                .setCompletedFrom(compFrom)
                .setCompletedTo(compTo);
        queryResultAssertion(compDateQuery, allAssignments,
                assignment -> assignment.isCompleted() &&
                        assignment.getUpdateTime() != null &&
                        compRange.contains(assignment.getUpdateTime()));

        // Query by type and completed status
        PTAQueryBuilder completedCodeQuery = new PTAQueryBuilder()
                .setTaskType(VIDEO_CODE_ENTRY)
                .setCompleted(true);
        queryResultAssertion(completedCodeQuery, allAssignments,
                assignment -> getTaskType(assignment) == VIDEO_CODE_ENTRY && assignment.isCompleted());

        // Query by task id
        int taskId = allDummyTasks.get(0).getTaskId();
        PTAQueryBuilder taskIdQuery = new PTAQueryBuilder()
                .setTaskIds(Collections.singleton(taskId));
        queryResultAssertion(taskIdQuery, allAssignments, assignment -> taskId == assignment.getTaskId());

        Set<Integer> taskIds = allDummyTasks.subList(0, allDummyTasks.size() - 1)
                .stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());
        PTAQueryBuilder multiTaskIdQuery = new PTAQueryBuilder()
                .setTaskIds(taskIds);
        queryResultAssertion(multiTaskIdQuery, allAssignments, task -> taskIds.contains(task.getTaskId()));

        // Query incomplete tasks for specific emp
        int firstEmpId = bogusEmpIds.get(0);
        PTAQueryBuilder incEmpIdQuery = new PTAQueryBuilder()
                .setEmpId(firstEmpId)
                .setCompleted(false);
        queryResultAssertion(incEmpIdQuery, allAssignments, task -> task.getEmpId() == firstEmpId && !task.isCompleted());
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
        List<PersonnelTaskAssignment> results = assignmentDao.getTasks(query);
        for (PersonnelTaskAssignment result : results) {
            unseenExpected.remove(result);
            assertTrue(query + " should not return " + result, predicate.test(result));
        }
        assertEquals("Unseen expected results should be empty for " + query,
                Collections.emptySet(), unseenExpected);
    }

    private PersonnelTaskType getTaskType(PersonnelTaskAssignment assignment) {
        return taskDao.getPersonnelTask(assignment.getTaskId())
                .getTaskType();
    }
}