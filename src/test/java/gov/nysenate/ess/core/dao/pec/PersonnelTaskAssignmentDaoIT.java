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
        assertTrue(updatedTask.isCompleted());
    }

    @Test
    public void queryTest() {
        final ImmutableList<Integer> bogusEmpIds = ImmutableList.of(9999999);
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

        // Query by task type
        PTAQueryBuilder taskTypeQuery = new PTAQueryBuilder()
                .setTaskType(DOCUMENT_ACKNOWLEDGMENT)
                .setEmpId(9999999);
        List<PersonnelTaskAssignment> results = assignmentDao.getTasks(taskTypeQuery);
        for (PersonnelTaskAssignment result: results) {
            assertTrue( getTaskType( result ) == DOCUMENT_ACKNOWLEDGMENT );
        }

        // Query by completed status
        PTAQueryBuilder completedQuery = new PTAQueryBuilder().setCompleted(true).setEmpId(9999999);
        results = assignmentDao.getTasks(completedQuery);
        for (PersonnelTaskAssignment result: results) {
            assertTrue( result.isCompleted() );
        }


        // Query by completion date

        LocalDateTime partialCompFrom = LocalDateTime.now().minusMinutes(2);
        LocalDateTime partialCompTo = LocalDateTime.now().plusMinutes(1);
        Range<LocalDateTime> partialCompRange = Range.closed(partialCompFrom, partialCompTo);

        PTAQueryBuilder partialCompDateQuery = new PTAQueryBuilder()
                .setCompletedFrom(partialCompFrom)
                .setCompletedTo(partialCompTo)
                .setEmpId(9999999);
        results = assignmentDao.getTasks(partialCompDateQuery);
        assertTrue(results != null);

        List<LocalDateTime> firstTimestamps = allAssignments.stream()
                .map(PersonnelTaskAssignment::getUpdateTime)
                .filter(Objects::nonNull)
                .limit(10).collect(toList());
        LocalDateTime compFrom = firstTimestamps.get(0);
        LocalDateTime compTo = firstTimestamps.get(firstTimestamps.size() - 1);
        Range<LocalDateTime> compRange = Range.closed(partialCompFrom, compTo);
        PTAQueryBuilder compDateQuery = new PTAQueryBuilder()
                .setCompleted(true)
                .setCompletedFrom(compFrom)
                .setCompletedTo(compTo)
                .setEmpId(9999999);
        results = assignmentDao.getTasks(compDateQuery);
        for (PersonnelTaskAssignment result: results) {
            assertTrue( result.isCompleted() &&
                    result.getUpdateTime() != null &&
                    compRange.contains(result.getUpdateTime()) );
        }

        // Query by type and completed status
        PTAQueryBuilder completedCodeQuery = new PTAQueryBuilder()
                .setTaskType(VIDEO_CODE_ENTRY)
                .setCompleted(true);
        results = assignmentDao.getTasks(completedCodeQuery);
        for (PersonnelTaskAssignment result: results) {
            assertTrue( getTaskType( result ) == (VIDEO_CODE_ENTRY) && result.isCompleted() );
        }

        // Query by task id
        int taskId = allDummyTasks.get(0).getTaskId();
        PTAQueryBuilder taskIdQuery = new PTAQueryBuilder()
                .setTaskIds(Collections.singleton(taskId));
        results = assignmentDao.getTasks(taskIdQuery);
        for (PersonnelTaskAssignment result: results) {
            assertTrue( taskId == result.getTaskId() );
        }

        Set<Integer> taskIds = allDummyTasks.subList(0, allDummyTasks.size() - 1)
                .stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());
        PTAQueryBuilder multiTaskIdQuery = new PTAQueryBuilder()
                .setTaskIds(taskIds);
        results = assignmentDao.getTasks(multiTaskIdQuery);
        for (PersonnelTaskAssignment result: results) {
            assertTrue( taskIds.contains(result.getTaskId()) );
        }

    }

    private PersonnelTaskType getTaskType(PersonnelTaskAssignment assignment) {
        return taskDao.getPersonnelTask(assignment.getTaskId())
                .getTaskType();
    }
}