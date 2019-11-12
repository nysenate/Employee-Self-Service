package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.cache.CacheEvictEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskTestBuilder;
import gov.nysenate.ess.core.service.pec.assignment.PersonnelTaskAssigner;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;
import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.VIDEO_CODE_ENTRY;
import static gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup.DEFAULT;
import static gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup.ETHICS;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(DatabaseConfig.localTxManager)
public class EssPersonnelTaskAssignerIT extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EssPersonnelTaskAssignerIT.class);

    @Autowired private PersonnelTaskAssigner taskAssigner;
    @Autowired private PersonnelTaskService taskService;
    @Autowired private PersonnelTaskAssignmentDao assignmentDao;
    @Autowired private EmployeeInfoService empInfoService;
    @Autowired private EventBus eventBus;

    @Autowired private PersonnelTaskTestDao taskTestDao;

    @Before
    public void setUp() {
        eventBus.post(new CacheEvictEvent(Collections.singleton(ContentCache.PERSONNEL_TASK)));
        setUpEthicsTasks();
    }

    @Test
    public void empTaskAssignTest() {
        final int bogusEmpId = 1122334455;
        assertTrue("Test employee has no initial assignments",
                assignmentDao.getAssignmentsForEmp(bogusEmpId).isEmpty());

        taskAssigner.assignTasks(bogusEmpId);

        Set<Integer> tasksPresent = getAssignedTaskIds(bogusEmpId);

        Set<Integer> expectedTaskIds = getNewEmpTaskIds();

        assertEquals("Test employee is assigned expected tasks for new emp", expectedTaskIds, tasksPresent);
    }

    @Test
    public void deactivateTaskTest() {
        final int bogusEmpId = 1122334455;
        assertTrue("Test employee has no initial assignments",
                assignmentDao.getAssignmentsForEmp(bogusEmpId).isEmpty());

        final Set<Integer> newEmpTaskIds = getNewEmpTaskIds();

        // Assign all tasks
        taskAssigner.assignTasks(bogusEmpId);

        // Manually add a bogus task
        final PersonnelTask bogusTask = taskTestDao.insertDummyTask();
        final int bogusTaskId = bogusTask.getTaskId();
        final PersonnelTaskAssignment bogusTaskAssigment = PersonnelTaskAssignment.newTask(bogusEmpId, bogusTaskId);
        assignmentDao.updateAssignment(bogusTaskAssigment);

        Set<Integer> preDeactivateTasks = getAssignedTaskIds(bogusEmpId);

        assertEquals("Bogus task + new emp tasks should be assigned to employee before deactivation",
                Sets.union(newEmpTaskIds, Collections.singleton(bogusTaskId)), preDeactivateTasks);

        taskTestDao.setTaskActive(bogusTaskId, false);

        // Run task assignment (the bogus task assignment should be deactivated here)
        taskAssigner.assignTasks(bogusEmpId);

        Set<Integer> postDeactivateTasks = getAssignedTaskIds(bogusEmpId);
        assertEquals("All original new emp tasks should be assigned to employee",
                newEmpTaskIds, postDeactivateTasks);
        assertFalse("Employee should no longer be assigned bogus task", postDeactivateTasks.contains(bogusTaskId));
    }

    @Test
    public void ethicsAssignmentTest() {
        final int testEmpId = 9001;

        // Pre conditions

        List<PersonnelTask> moodleEthicsTasks = taskService.getActiveTasksInGroup(ETHICS).stream()
                .filter(task -> task.getTaskType() == MOODLE_COURSE)
                .collect(Collectors.toList());
        assertEquals("There must be exactly one moodle ethics task", 1, moodleEthicsTasks.size());
        PersonnelTask moodleEthicsTask = moodleEthicsTasks.get(0);

        Optional<PersonnelTask> nonMoodleEthicsOpt = taskService.getActiveTasksInGroup(ETHICS).stream()
                .filter(task -> task.getTaskType() != MOODLE_COURSE)
                .findFirst();

        assertTrue("There must be a non-moodle ethics course", nonMoodleEthicsOpt.isPresent());
        PersonnelTask nonMoodleEthicsTask = nonMoodleEthicsOpt.get();

        Set<Integer> nonMoodleEthicsIds = taskService.getActiveTasksInGroup(ETHICS).stream()
                .filter(task -> task.getTaskType() != MOODLE_COURSE)
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());

        assertTrue("Test employee has no initial assignments",
                assignmentDao.getAssignmentsForEmp(testEmpId).isEmpty());

        // First assignment

        taskAssigner.assignTasks(testEmpId);

        Set<Integer> initialAssignments = getAssignedTaskIds(testEmpId);

        assertEquals("Test employee has new employee tasks", getNewEmpTaskIds(), initialAssignments);

        assertTrue("Test emp has moodle ethics course",
                initialAssignments.contains(moodleEthicsTask.getTaskId()));
        assertTrue("Test emp does not have any non-moodle ethics assignments",
                Sets.intersection(initialAssignments, nonMoodleEthicsIds).isEmpty());

        // Moodle completion assignment

        PersonnelTaskAssignment completeMoodleAssignment = new PersonnelTaskAssignment(
                moodleEthicsTask.getTaskId(),
                testEmpId,
                testEmpId,
                // Set completed one day BEFORE non-moodle course is active.
                nonMoodleEthicsTask.getEffectiveDateTime().minusDays(1),
                true,
                true
        );
        assignmentDao.updateAssignment(completeMoodleAssignment);

        taskAssigner.assignTasks(9001);

        Set<Integer> moodleCompleteAssignments = getAssignedTaskIds(testEmpId);

        assertTrue("Test emp assigned non-moodle course",
                moodleCompleteAssignments.contains(nonMoodleEthicsTask.getTaskId()));
    }

    @Ignore("EssPersonnelTaskAssignerIT#assignAllEmpsTest takes too long to run.  Still good for debugging.")
    @Test
    public void assignAllEmpsTest() {
        logger.info("assigning tasks to all employees...");
        taskAssigner.assignTasks();
        logger.info("done assigning tasks");

        // Build a multimap of all task ids assigned to all active employees.
        List<PersonnelTaskAssignment> allTasks = assignmentDao.getTasks(
                new PTAQueryBuilder().setActive(true));
        HashMultimap<Integer, Integer> empTaskMultimap = allTasks.stream().collect(Multimaps.toMultimap(
                PersonnelTaskAssignment::getEmpId,
                PersonnelTaskAssignment::getTaskId,
                HashMultimap::create
        ));

        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();

        Set<Integer> defaultTaskIds = taskService.getActiveTasksInGroup(DEFAULT).stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());
        Set<Integer> ethicsTaskIds = taskService.getActiveTasksInGroup(ETHICS).stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());

        for (int empId : activeEmpIds) {
            assertTrue("Active employee " + empId + " must have active assignments",
                    empTaskMultimap.containsKey(empId));
            Set<Integer> assignedTaskIds = empTaskMultimap.get(empId);
            assertTrue("Active employee " + empId + " must be assigned all active default tasks",
                    assignedTaskIds.containsAll(defaultTaskIds));
            Set<Integer> assignedEthicsTaskIds = assignedTaskIds.stream()
                    .filter(ethicsTaskIds::contains)
                    .collect(Collectors.toSet());
            assertEquals("Active emp #" + empId + " should have only one active ethics task assigned",
                    1, assignedEthicsTaskIds.size());
        }
    }

    /**
     * Ensure that there is one moodle course and one non-moodle course in the ethics task assignment group.
     */
    private void setUpEthicsTasks() {
        List<PersonnelTask> existingEthicsTasks =
                taskService.getActiveTasksInGroup(ETHICS);

        if (existingEthicsTasks.stream().noneMatch(task -> task.getTaskType() == MOODLE_COURSE)) {
            PersonnelTaskTestBuilder moodleEthicsCourseBuilder = new PersonnelTaskTestBuilder()
                    .setTaskType(MOODLE_COURSE)
                    .setAssignmentGroup(ETHICS)
                    .setEffectiveDateTime(LocalDateTime.now());
            taskTestDao.insertDummyTask(moodleEthicsCourseBuilder);
        }
        if (existingEthicsTasks.stream().noneMatch(task -> task.getTaskType() != MOODLE_COURSE)) {
            PersonnelTaskTestBuilder nonMoodleEthicsCourseBuilder = new PersonnelTaskTestBuilder()
                    .setTaskType(VIDEO_CODE_ENTRY)
                    .setAssignmentGroup(ETHICS)
                    .setEffectiveDateTime(LocalDateTime.now());
            taskTestDao.insertDummyTask(nonMoodleEthicsCourseBuilder);
        }
    }

    /**
     * Get a set of task ids expected to be assigned to a brand new employee.
     * Assumes that there is an ethics moodle course.
     * @see #setUpEthicsTasks()
     */
    private Set<Integer> getNewEmpTaskIds() {
        return ImmutableList.<PersonnelTask>builder()
                .addAll(taskService.getActiveTasksInGroup(DEFAULT))
                .addAll(taskService.getActiveTasksInGroup(ETHICS).stream()
                        .filter(task -> task.getTaskType() == MOODLE_COURSE)
                        .collect(Collectors.toList())
                )
                .build()
                .stream()
                .map(PersonnelTask::getTaskId)
                .collect(ImmutableSet.toImmutableSet());
    }

    private Set<Integer> getAssignedTaskIds(int empId) {
        return assignmentDao.getAssignmentsForEmp(empId).stream()
                .map(PersonnelTaskAssignment::getTaskId)
                .collect(Collectors.toSet());
    }
}