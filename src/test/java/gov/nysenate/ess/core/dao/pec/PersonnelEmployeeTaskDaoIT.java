package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.model.pec.PersonnelEmployeeTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
public class PersonnelEmployeeTaskDaoIT extends BaseTest {

    @Autowired private PersonnelEmployeeTaskDao taskDao;

    @Test
    public void insertUpdateTest() {
        final int empId = 123456789;
        final PersonnelTaskId taskId = new PersonnelTaskId(DOCUMENT_ACKNOWLEDGMENT, 999);
        final PersonnelEmployeeTask initialTask =
                new PersonnelEmployeeTask(empId, taskId, null, null, false);
        List<PersonnelEmployeeTask> tasks;
        // Make sure the test employee doesn't have any tasks
        tasks = taskDao.getTasksForEmp(empId);
        assertTrue(tasks.isEmpty());
        // Add an initial task
        taskDao.updatePersonnelEmployeeTask(initialTask);
        // Verify that initial task is present
        tasks = taskDao.getTasksForEmp(empId);
        assertEquals(1, tasks.size());
        assertEquals(initialTask, tasks.get(0));
        // Update the initial task with different data
        final PersonnelEmployeeTask updatedTask =
                new PersonnelEmployeeTask(empId, taskId, LocalDateTime.now(), empId, true);
        assertNotEquals(initialTask, updatedTask);
        taskDao.updatePersonnelEmployeeTask(updatedTask);
        // Ensure that it was updated
        tasks = taskDao.getTasksForEmp(empId);
        assertEquals(1, tasks.size());
        assertEquals(updatedTask, tasks.get(0));
    }
}