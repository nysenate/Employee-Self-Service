package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.view.PersonnelAssignedTaskUpdateView;
import gov.nysenate.ess.core.client.view.PersonnelAssignedTaskView;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.pec.PersonnelTaskSource;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.PERSONNEL_TASK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for getting and updating personnel tasks assignments.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task")
public class PersonnelTaskApiCtrl extends BaseRestApiCtrl {

    private final PersonnelTaskSource taskSource;
    private final PersonnelAssignedTaskDao taskDao;
    private final EmployeeInfoService empInfoService;

    public PersonnelTaskApiCtrl(PersonnelTaskSource taskSource,
                                PersonnelAssignedTaskDao taskDao,
                                EmployeeInfoService empInfoService) {
        this.taskSource = taskSource;
        this.taskDao = taskDao;
        this.empInfoService = empInfoService;
    }

    /**
     * Get Tasks for Emp API
     * ---------------------
     *
     * Gets a list of all tasks for a specific employee.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/emp/{empId}
     *
     * Path params:
     * @param empId int - employee id
     *
     * @return {@link ListViewResponse<PersonnelAssignedTaskView>} list of tasks assigned to given emp.
     */
    @RequestMapping(value = "/emp/{empId}", method = {GET, HEAD})
    public ListViewResponse<PersonnelAssignedTaskView> getTasksForEmployee(@PathVariable int empId) {
        checkPermission(new CorePermission(empId, PERSONNEL_TASK, POST));

        List<PersonnelAssignedTask> tasks = taskDao.getTasksForEmp(empId);
        List<PersonnelAssignedTaskView> taskViews =
                tasks.stream().map(PersonnelAssignedTaskView::new).collect(Collectors.toList());
        return ListViewResponse.of(taskViews, "tasks");
    }

    /**
     * Update Personnel Task API
     * -------------------------
     *
     * Updates a personnel assigned task with given data.
     *
     * Usage:
     * (POST)   /api/v1/personnel/task/update
     *
     * Request body:
     * @param update {@link PersonnelAssignedTaskUpdateView} - task data to be saved.
     *
     * @return {@link SimpleResponse} indicating update success.
     */
    @RequestMapping(value = "/update", method = POST)
    public SimpleResponse updatePersonnelAssignedTask(@RequestBody PersonnelAssignedTaskUpdateView update) {
        int authenticatedEmpId = ShiroUtils.getAuthenticatedEmpId();
        LocalDateTime updateTimestamp = LocalDateTime.now();
        PersonnelAssignedTask task =
                update.toPersonnelAssignedTask(authenticatedEmpId, updateTimestamp);

        verifyTask(task);

        checkPermission(new CorePermission(task.getEmpId(), PERSONNEL_TASK, POST));

        taskDao.updatePersonnelAssignedTask(task);
        return new SimpleResponse(true,
                "personnel assigned task updated",
                "personnel-assigned-task-update-success"
        );
    }

    /**
     * Verifies the given task to make sure the contents are valid.
     */
    private void verifyTask(PersonnelAssignedTask task) {
        int empId = task.getEmpId();
        try {
            empInfoService.getEmployee(empId);
        } catch (EmployeeNotFoundEx ex) {
            throw new InvalidRequestParamEx(Integer.toString(empId), "empId", "int",
                    "empId param must belong to a valid employee.");
        }
        Set<PersonnelTaskId> allTaskIds = taskSource.getAllPersonnelTaskIds();
        if (!allTaskIds.contains(task.getTaskId())) {
            throw new InvalidRequestParamEx(Objects.toString(task.getTaskId()), "taskId",
                    "personnel-task-id", "task id must refer to an active task");
        }
    }

}
