package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.Maps;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.pec.*;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskNotFoundEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.pec.PersonnelTaskSource;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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

    private final Map<Class, PersonnelTaskViewFactory> viewFactoryMap;

    public PersonnelTaskApiCtrl(PersonnelTaskSource taskSource,
                                PersonnelAssignedTaskDao taskDao,
                                EmployeeInfoService empInfoService,
                                List<PersonnelTaskViewFactory> taskViewFactories) {
        this.taskSource = taskSource;
        this.taskDao = taskDao;
        this.empInfoService = empInfoService;
        this.viewFactoryMap = Maps.uniqueIndex(taskViewFactories, PersonnelTaskViewFactory::getTaskClass);
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
    public ListViewResponse<PersonnelAssignedTaskView> getTasksForEmployee(
            @PathVariable int empId,
            @RequestParam(defaultValue = "false") boolean detail) {

        checkPermission(new CorePermission(empId, PERSONNEL_TASK, GET));

        // Determine method to use to generate view objects.
        Function<PersonnelAssignedTask, PersonnelAssignedTaskView> viewMapper =
                detail ? this::getDetailedTaskView : PersonnelAssignedTaskView::new;

        List<PersonnelAssignedTask> tasks = taskDao.getTasksForEmp(empId);
        List<PersonnelAssignedTaskView> taskViews = tasks.stream()
                .map(viewMapper)
                .collect(Collectors.toList());
        return ListViewResponse.of(taskViews, "tasks");
    }

    /**
     * Get Task for Emp API
     * --------------------
     *
     * Gets a list of all tasks for a specific employee.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/emp/{empId}/{taskType}/{taskNumber}
     *
     * Path params:
     * @param empId int - employee id
     * @param taskType {@link PersonnelTaskType}
     * @param taskNumber int - task number
     *
     * @return {@link ViewObjectResponse<PersonnelAssignedTaskView>}
     */
    @RequestMapping(value = "/emp/{empId}/{taskType}/{taskNumber}", method = {GET, HEAD})
    public ViewObjectResponse<DetailPersonnelAssignedTaskView> getSpecificTaskForEmployee(
            @PathVariable int empId,
            @PathVariable String taskType,
            @PathVariable int taskNumber) {

        checkPermission(new CorePermission(empId, PERSONNEL_TASK, GET));

        PersonnelTaskType parsedTaskType =
                getEnumParameter("taskType", taskType, PersonnelTaskType.class);

        PersonnelTaskId taskId = new PersonnelTaskId(parsedTaskType, taskNumber);

        PersonnelAssignedTask task = taskDao.getTaskForEmp(empId, taskId);

        DetailPersonnelAssignedTaskView detailedTaskView = getDetailedTaskView(task);

        return new ViewObjectResponse<>(detailedTaskView, "task");
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


    @ExceptionHandler(PersonnelAssignedTaskNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ViewObjectErrorResponse handleAssignedTaskNotFoundEx(PersonnelAssignedTaskNotFoundEx ex) {
        return new ViewObjectErrorResponse(
                ErrorCode.PERSONNEL_ASSIGNED_TASK_NOT_FOUND,
                new PersonnelAssignedTaskIdView(ex.getEmpId(), ex.getTaskId())
        );
    }

    /* --- Internal Methods --- */

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

    /**
     * Generate a detailed task view from the given task.
     * This involves loading task details and packinging it with the task.
     */
    @SuppressWarnings("unchecked")
    private DetailPersonnelAssignedTaskView getDetailedTaskView(PersonnelAssignedTask assignedTask) {
        PersonnelTask personnelTask = taskSource.getPersonnelTask(assignedTask.getTaskId());
        Class<? extends PersonnelTask> taskClass = personnelTask.getClass();
        if (!viewFactoryMap.containsKey(taskClass)) {
            throw new IllegalArgumentException("No view factory exists for PersonnelTasks of class: " + taskClass.getName());
        }
        PersonnelTaskView taskView = viewFactoryMap.get(taskClass).getView(personnelTask);
        return new DetailPersonnelAssignedTaskView(assignedTask, taskView);
    }

}
