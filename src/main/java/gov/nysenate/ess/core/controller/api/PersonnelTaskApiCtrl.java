package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.Maps;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.pec.*;
import gov.nysenate.ess.core.dao.pec.PATQueryBuilder;
import gov.nysenate.ess.core.dao.pec.PATQueryCompletionStatus;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskNotFoundEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.*;
import gov.nysenate.ess.core.service.pec.*;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.PERSONNEL_TASK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * API for getting and updating personnel tasks assignments.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task")
public class PersonnelTaskApiCtrl extends BaseRestApiCtrl {

    private static final Pattern taskIdPattern = Pattern.compile("^(?<taskType>[a-zA-Z_]+)-(?<taskNumber>[0-9]+)$");
    private static final Pattern sortPattern = Pattern.compile("^(?<orderBy>[a-zA-Z_]+):(?<sortOrder>[a-zA-Z]+)$");

    private final PersonnelTaskSource taskSource;
    private final PersonnelAssignedTaskDao taskDao;
    private final EmpTaskSearchService empTaskSearchService;

    private final Map<Class, PersonnelTaskViewFactory> viewFactoryMap;

    public PersonnelTaskApiCtrl(PersonnelTaskSource taskSource,
                                PersonnelAssignedTaskDao taskDao,
                                EmpTaskSearchService empTaskSearchService, List<PersonnelTaskViewFactory> taskViewFactories) {
        this.taskSource = taskSource;
        this.taskDao = taskDao;
        this.empTaskSearchService = empTaskSearchService;
        this.viewFactoryMap = Maps.uniqueIndex(taskViewFactories, PersonnelTaskViewFactory::getTaskClass);
    }

    /** Get Tasks API
     * --------------
     *
     * Get a list of all personnel tasks.
     *
     * Request params:
     * @param activeOnly boolean - default false - if true, only active tasks are returned, otherwise all tasks.
     *
     * @return {@link ListViewResponse<PersonnelTaskView>}
     */
    @RequestMapping(value = "", method = {GET, HEAD})
    public ListViewResponse<PersonnelTaskView> getTasks(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return ListViewResponse.of(
                taskSource.getPersonnelTasks(activeOnly).stream()
                        .map(this::getPersonnelTaskView)
                        .collect(Collectors.toList()),
                "tasks"
        );
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
    @RequestMapping(value = "/emp/{empId:\\d+}", method = {GET, HEAD})
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
     * Employee Task Search
     * --------------------
     *
     * Search for employees and tasks.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/emp/search
     *
     * Request params:
     * @see #extractEmpPATQuery(WebRequest) for facet parameters
     * limit - int - default 10 - limit the number of results
     * offset - int - default 1 - start the result list from this result.
     *
     * @return {@link ViewObjectResponse<PersonnelAssignedTaskView>}
     */
    @RequestMapping(value = "/emp/search", method = {GET, HEAD})
    public ListViewResponse<EmpPATSearchResultView> empTaskSearch(WebRequest request) {

        checkPermission(SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermission());

        LimitOffset limitOffset = getLimitOffset(request, 10);

        EmpPATQuery empPatQuery = extractEmpPATQuery(request);

        PaginatedList<EmployeeTaskSearchResult> results = empTaskSearchService.searchForEmpTasks(empPatQuery, limitOffset);
        List<EmpPATSearchResultView> resultViews = results.getResults().stream()
                .map(EmpPATSearchResultView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), limitOffset);
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
     * Generate a detailed task view from the given task.
     * This involves loading task details and packinging it with the task.
     */
    private DetailPersonnelAssignedTaskView getDetailedTaskView(PersonnelAssignedTask assignedTask) {
        PersonnelTask personnelTask = taskSource.getPersonnelTask(assignedTask.getTaskId());
        PersonnelTaskView taskView = getPersonnelTaskView(personnelTask);
        return new DetailPersonnelAssignedTaskView(assignedTask, taskView);
    }


    /** Generate a task view for the given task */
    @SuppressWarnings("unchecked")
    private PersonnelTaskView getPersonnelTaskView(PersonnelTask personnelTask) {
        Class<? extends PersonnelTask> taskClass = personnelTask.getClass();
        if (!viewFactoryMap.containsKey(taskClass)) {
            throw new IllegalArgumentException("No view factory exists for PersonnelTasks of class: " + taskClass.getName());
        }
        return viewFactoryMap.get(taskClass).getView(personnelTask);
    }

    /**
     * Build an {@link EmpPATQuery} from request parameters.
     *
     * @see #extractEmpSearchParams(WebRequest)
     * @see #extractEmpPATQuery(WebRequest)
     */
    private EmpPATQuery extractEmpPATQuery(WebRequest request) {
        return new EmpPATQuery(
                extractEmpSearchParams(request),
                extractPATSearchParams(request),
                extractSortDirectives(request)
        );
    }

    /**
     * Build an {@link EmployeeSearchBuilder} from request parameters
     */
    private EmployeeSearchBuilder extractEmpSearchParams(WebRequest request) {
        EmployeeSearchBuilder searchBuilder = new EmployeeSearchBuilder();

        searchBuilder.setName(request.getParameter("name"));
        searchBuilder.setActive(getBooleanParam(request, "empActive", null));

        LocalDate contSrvFrom = Optional.ofNullable(request.getParameter("contSrvFrom"))
                .map(ds -> parseISODate(ds, "contSrvFrom"))
                .orElse(null);
        LocalDate contSrvTo = Optional.ofNullable(request.getParameter("contSrvTo"))
                .map(ds -> parseISODate(ds, "contSrvTo"))
                .orElse(null);
        searchBuilder.setContinuousServiceFrom(contSrvFrom)
                .setContinuousServiceTo(contSrvTo);

        List<String> respCtrHeadCodes = Optional.ofNullable(request.getParameterValues("respCtrHead"))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
        searchBuilder.setRespCtrHeadCodes(respCtrHeadCodes);

        return searchBuilder;
    }

    /**
     * Build an {@link PATQueryBuilder} from request parameters
     */
    private PATQueryBuilder extractPATSearchParams(WebRequest request) {
        PATQueryBuilder patQueryBuilder = new PATQueryBuilder();

        patQueryBuilder.setEmpId(getIntegerParam(request, "empId", null));
        patQueryBuilder.setActive(getBooleanParam(request, "taskActive", null));
        patQueryBuilder.setTaskType(
                Optional.ofNullable(request.getParameter("taskType"))
                        .map(ts -> getEnumParameter("taskType", ts, PersonnelTaskType.class))
                        .orElse(null)
        );
        patQueryBuilder.setCompleted(getBooleanParam(request, "completed", null));
        patQueryBuilder.setCompletedFrom(Optional.ofNullable(request.getParameter("completedFrom"))
                .map(val -> parseISODateTime(val, "completedFrom"))
                .orElse(null));
        patQueryBuilder.setCompletedTo(Optional.ofNullable(request.getParameter("completedTo"))
                .map(val -> parseISODateTime(val, "completedTo"))
                .orElse(null));
        patQueryBuilder.setTaskIds(
                Optional.ofNullable(request.getParameterValues("taskId"))
                        .map(tids -> Arrays.stream(tids)
                                .map(this::parseTaskId)
                                .collect(Collectors.toList())
                        )
                        .orElse(null)
        );
        patQueryBuilder.setTotalCompletionStatus(
                Optional.ofNullable(request.getParameter("totalCompletion"))
                        .map(val -> getEnumParameter("totalCompletion", val, PATQueryCompletionStatus.class))
                        .orElse(null)
        );

        return patQueryBuilder;
    }

    /**
     * Parse a {@link PersonnelTaskId} from a parameter string.
     */
    private PersonnelTaskId parseTaskId(String idStr) {
        Matcher matcher = taskIdPattern.matcher(idStr);
        if (!matcher.matches()) {
            throw new InvalidRequestParamEx(idStr, "taskId", "personnel-task-id", taskIdPattern.pattern());
        }
        PersonnelTaskType taskType = getEnumParameter("taskId", matcher.group("taskType"), PersonnelTaskType.class);
        int taskNumber = parseIntegerParam("taskId", matcher.group("taskNumber"));
        return new PersonnelTaskId(taskType, taskNumber);
    }

    /**
     * Extract sort directives from request params.
     */
    private List<EmpTaskSort> extractSortDirectives(WebRequest request) {
        String[] sorts = request.getParameterValues("sort");
        if (sorts == null) {
            return null;
        }
        return Arrays.stream(sorts)
                .map(this::parseEmpTaskSort)
                .collect(Collectors.toList());
    }

    /**
     * Parse a sort directive from a sort string
     */
    private EmpTaskSort parseEmpTaskSort(String sortStr) {
        Matcher matcher = sortPattern.matcher(sortStr);
        if (!matcher.matches()) {
            throw new InvalidRequestParamEx(sortStr, "sort", "sort",
                    sortPattern.pattern());
        }
        EmpTaskOrderBy orderBy = getEnumParameter("sort", matcher.group("orderBy"), EmpTaskOrderBy.class);
        SortOrder sortOrder = getEnumParameter("sort", matcher.group("sortOrder"), SortOrder.class);
        return new EmpTaskSort(orderBy, sortOrder);
    }

}
