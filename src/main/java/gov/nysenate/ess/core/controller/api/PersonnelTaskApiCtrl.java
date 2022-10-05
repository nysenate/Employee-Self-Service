package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.pec.*;
import gov.nysenate.ess.core.client.view.pec.acknowledgment.AckDocView;
import gov.nysenate.ess.core.client.view.pec.video.PECVideoView;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryCompletionStatus;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentNotFoundEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.EmpPATSearchResultView;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask;
import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;
import gov.nysenate.ess.core.model.pec.moodle.MoodleCourseTask;
import gov.nysenate.ess.core.model.pec.video.VideoTask;
import gov.nysenate.ess.core.service.pec.search.*;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final Pattern sortPattern = Pattern.compile("^(?<orderBy>[a-zA-Z_]+):(?<sortOrder>[a-zA-Z]+)$");

    @Value("${resource.path}")
    private String assets;

    @Value("${data.ackdoc_subdir}")
    private String ackDocPath;

    @Value("${data.pecvid_subdir}")
    private String pecVidPath;

    private final PersonnelTaskService taskService;
    private final PersonnelTaskAssignmentDao taskDao;
    private final EmpTaskSearchService empTaskSearchService;

    public PersonnelTaskApiCtrl(PersonnelTaskService taskService,
                                PersonnelTaskAssignmentDao taskDao,
                                EmpTaskSearchService empTaskSearchService) {
        this.taskService = taskService;
        this.taskDao = taskDao;
        this.empTaskSearchService = empTaskSearchService;
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
                taskService.getPersonnelTasks(activeOnly, true).stream()
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
     * (GET)    /api/v1/personnel/task/assignment/{empId}
     *
     * Path params:
     * @param empId int - employee id
     *
     * @return {@link ListViewResponse<PersonnelTaskAssignmentView>} list of tasks assigned to given emp.
     */
    @RequestMapping(value = "/assignment/{empId:\\d+}", method = {GET, HEAD})
    public ListViewResponse<PersonnelTaskAssignmentView> getAssignmentsForEmployee(
            @PathVariable int empId,
            @RequestParam(defaultValue = "false") boolean detail) {

        checkPermission(new CorePermission(empId, PERSONNEL_TASK, GET));

        // Determine method to use to generate view objects.
        Function<PersonnelTaskAssignment, PersonnelTaskAssignmentView> viewMapper =
                detail ? this::getDetailedTaskView : PersonnelTaskAssignmentView::new;

        List<PersonnelTaskAssignment> tasks = taskDao.getAssignmentsForEmp(empId);
        List<PersonnelTaskAssignmentView> taskViews = tasks.stream()
                .map(viewMapper)
                .collect(Collectors.toList());
        return ListViewResponse.of(taskViews, "assignments");
    }

    /**
     * Get Task for Emp API
     * --------------------
     *
     * Gets a specific task assignment.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/assignment/{empId}/{taskId}
     *
     * Path params:
     * @param empId int - employee id
     * @param taskId int - task id
     *
     * @return {@link ViewObjectResponse<PersonnelTaskAssignmentView>}
     */
    @RequestMapping(value = "/assignment/{empId}/{taskId}", method = {GET, HEAD})
    public ViewObjectResponse<DetailPersonnelTaskAssignmentView> getSpecificTaskForEmployee(
            @PathVariable int empId,
            @PathVariable int taskId) {

        checkPermission(new CorePermission(empId, PERSONNEL_TASK, GET));

        PersonnelTaskAssignment assignment = taskDao.getTaskForEmp(empId, taskId);

        DetailPersonnelTaskAssignmentView detailedTaskView = getDetailedTaskView(assignment);

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
     * @return {@link ViewObjectResponse<PersonnelTaskAssignmentView>}
     */
    @RequestMapping(value = "/emp/search", method = {GET, HEAD})
    public ListViewResponse<EmpPATSearchResultView> empTaskSearch(WebRequest request) {

        checkPermission(SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermission());

        LimitOffset limitOffset = getLimitOffset(request, 10);

        EmpPTAQuery empPTAQuery = extractEmpPATQuery(request);

        PaginatedList<EmployeeTaskSearchResult> results = empTaskSearchService.searchForEmpTasks(empPTAQuery, limitOffset);
        List<EmpPATSearchResultView> resultViews = results.getResults().stream()
                .map(EmpPATSearchResultView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), limitOffset);
    }

    /**
     * Employee Task Search Report
     * ---------------------------
     *
     * Returns a CSV report based on the passed in request params
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/emp/search/report
     *
     * Request params:
     * @see #extractEmpPATQuery(WebRequest) for facet parameters
     *
     */
    @RequestMapping(value = "/emp/search/report", method = {GET, HEAD})
    public void generateSearchReportCSV(WebRequest request, HttpServletResponse response) throws IOException {
        checkPermission(SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermission());

        String csvFileName =  "PEC_Report" + LocalDateTime.now().withNano(0)+".csv";
        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        //Set Response
        response.setHeader(headerKey, headerValue);
        response.setContentType("text/csv");
        response.setStatus(200);
        //Get Search Results
        EmpPTAQuery empPTAQuery = extractEmpPATQuery(request);
        PaginatedList<EmployeeTaskSearchResult> results =
                empTaskSearchService.searchForEmpTasks(empPTAQuery, LimitOffset.ALL);
        List<EmpPATSearchResultView> resultViews = results.getResults().stream()
                .map(EmpPATSearchResultView::new)
                .collect(Collectors.toList());

        //Get max amount of tasks
        //get proper csv printer
        //handle csv printing
        int maxNumOfTasks = getMaxNumOfTasks(resultViews);
        CSVPrinter csvPrinter = createProperCSVPrinter(maxNumOfTasks, response);
        for (EmpPATSearchResultView searchResultView: resultViews) {
            DetailedEmployeeView currentEmployee =  searchResultView.getEmployee();
            handleCSVPrinting(csvPrinter,currentEmployee, getRespCenter(currentEmployee), searchResultView.getTasks(), maxNumOfTasks);
        }
        csvPrinter.close();
    }

    @ExceptionHandler(PersonnelTaskAssignmentNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ViewObjectErrorResponse handleAssignmentNotFoundEx(PersonnelTaskAssignmentNotFoundEx ex) {
        return new ViewObjectErrorResponse(
                ErrorCode.PERSONNEL_ASSIGNED_TASK_NOT_FOUND,
                new PersonnelTaskAssignmentIdView(ex.getEmpId(), ex.getTaskId())
        );
    }

    /**
     * Get a map of all personnel tasks and their ids
     */
    private Map<Integer, PersonnelTaskView> getPersonnelTaskIdMap() {
        return taskService.getPersonnelTasks(false, true).stream()
                .map(this::getPersonnelTaskView)
                .collect(Collectors.toMap(PersonnelTaskView::getTaskId, Function.identity()));
    }

    /**
     * return the maximum number of tasks so we can provide the right amount of headers for the CSV report
     */
    private int getMaxNumOfTasks(List<EmpPATSearchResultView> resultViews) {
        int max = 1;
        for (EmpPATSearchResultView searchResultView: resultViews) {
            int recordCount = searchResultView.getTasks().size();
            if (recordCount > max) {
                max = recordCount;
            }
        }
        return max;
    }

    /**
     * Returns a complete CSV printer with the header set
     */
    private CSVPrinter createProperCSVPrinter(int maxNumOfTasks, HttpServletResponse response) throws IOException {
        StringBuilder testOriginalTaskString = new StringBuilder("EmpId, Name, Email, Work Phone, Resp Center, Continuous Service, ");

        for (int i = 1; i < maxNumOfTasks+1; i++) {
            testOriginalTaskString.append(createTaskStrings(i));
        }

         return new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT
                    .withHeader(testOriginalTaskString.toString().split(",")));
    }

    /**
     * Creates additional sections of the csv header relating to the tasks.
     * The number is required because each value in the header must be unique
     */
    private String createTaskStrings(int taskNumber) {
        return "Task " + taskNumber + " Title," + "Task " + taskNumber + " Type," + "Task " + taskNumber + " Completion Time," + "Task " + taskNumber + " Update EmpId,";
    }

    /**
     * Creates the record that will be printed in the downloadable CSV
     */
    private void handleCSVPrinting(CSVPrinter csvPrinter, DetailedEmployeeView currentEmployee, String respCenter,
                                   List<PersonnelTaskAssignmentView> assignments, int maxNumOfTasks) throws IOException {
        Map<Integer, PersonnelTaskView> personnelTaskMap = getPersonnelTaskIdMap();

        ArrayList<Object> recordToPrint = new ArrayList<>();
        recordToPrint.add(currentEmployee.getEmployeeId());
        recordToPrint.add(currentEmployee.getFullName());
        recordToPrint.add(currentEmployee.getEmail());
        recordToPrint.add(currentEmployee.getWorkPhone());
        recordToPrint.add(getRespCenter(currentEmployee));
        recordToPrint.add(currentEmployee.getContServiceDate());

        for (PersonnelTaskAssignmentView assignment : assignments) {
            PersonnelTaskView applicableTask = personnelTaskMap.get(assignment.getTaskId());

            recordToPrint.add(applicableTask.getTitle());
            recordToPrint.add(applicableTask.getTaskType());
            recordToPrint.add(assignment.getTimestamp());
            recordToPrint.add(assignment.getUpdateUserId());
        }

        int emptyTasksToFill = maxNumOfTasks - assignments.size();
        if (emptyTasksToFill >= 1) {
            for (int i = 0; i < emptyTasksToFill; i++) {
                addEmptyTask(recordToPrint);
            }
        }

        csvPrinter.printRecord(recordToPrint);
    }

    /* Ensures that a record that doesn't have data for all tasks will show up empty in the csv */
    private void addEmptyTask(ArrayList<Object> recordToPrint) {
        for (int i = 0; i < 5; i++) {
            recordToPrint.add("");
        }
    }

    /*
    Returns the responsibility center of a given employee
     */
    private String getRespCenter(DetailedEmployeeView currentEmployee) {
        String respCenter = "";
        try {
            respCenter = currentEmployee.getRespCtr().getRespCenterHead().getShortName();
        }
        catch (Exception e) {
            //No need to do anything. This means that the employee does not have a responsibility center
        }
        return respCenter;
    }

    /* --- Internal Methods --- */

    /**
     * Generate a detailed task view from the given task.
     * This involves loading task details and packinging it with the task.
     */
    private DetailPersonnelTaskAssignmentView getDetailedTaskView(
            PersonnelTaskAssignment taskAssignment) {
        PersonnelTask detailedTask = taskService.getPersonnelTask(taskAssignment.getTaskId(), true);
        PersonnelTaskView taskView = getPersonnelTaskView(detailedTask);
        return new DetailPersonnelTaskAssignmentView(taskAssignment, taskView);
    }

    /** Generate a task view for the given task */
    private PersonnelTaskView getPersonnelTaskView(PersonnelTask detailedTask) {
        return switch (detailedTask.getTaskType()) {
            case DOCUMENT_ACKNOWLEDGMENT ->
                    new AckDocView((AckDoc) detailedTask, assets + ackDocPath);
            case MOODLE_COURSE ->
                    new MoodleTaskView((MoodleCourseTask) detailedTask);
            case VIDEO_CODE_ENTRY ->
                    new PECVideoView((VideoTask) detailedTask, assets + pecVidPath);
            case EVERFI_COURSE ->
                    new EverfiTaskView((EverfiCourseTask) detailedTask);
            case ETHICS_COURSE ->
                    new EthicsCourseTaskView((EthicsCourseTask) detailedTask);
        };
    }

    /**
     * Build an {@link EmpPTAQuery} from request parameters.
     *
     * @see #extractEmpSearchParams(WebRequest)
     * @see #extractEmpPATQuery(WebRequest)
     */
    private EmpPTAQuery extractEmpPATQuery(WebRequest request) {
        return new EmpPTAQuery(
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
     * Build an {@link PTAQueryBuilder} from request parameters
     */
    private PTAQueryBuilder extractPATSearchParams(WebRequest request) {
        PTAQueryBuilder PTAQueryBuilder = new PTAQueryBuilder();

        PTAQueryBuilder.setEmpId(getIntegerParam(request, "empId", null));
        PTAQueryBuilder.setActive(getBooleanParam(request, "taskActive", null));
        PTAQueryBuilder.setTaskType(
                Optional.ofNullable(request.getParameter("taskType"))
                        .map(ts -> getEnumParameter("taskType", ts, PersonnelTaskType.class))
                        .orElse(null)
        );
        PTAQueryBuilder.setCompleted(getBooleanParam(request, "completed", null));
        PTAQueryBuilder.setCompletedFrom(Optional.ofNullable(request.getParameter("completedFrom"))
                .map(val -> parseISODateTime(val, "completedFrom"))
                .orElse(null));
        PTAQueryBuilder.setCompletedTo(Optional.ofNullable(request.getParameter("completedTo"))
                .map(val -> parseISODateTime(val, "completedTo"))
                .orElse(null));
        PTAQueryBuilder.setTaskIds(
                Optional.ofNullable(request.getParameterValues("taskId"))
                        .map(tids -> Arrays.stream(tids)
                                .map(Integer::parseInt)
                                .collect(Collectors.toList())
                        )
                        .orElse(null)
        );
        PTAQueryBuilder.setTotalCompletionStatus(
                Optional.ofNullable(request.getParameter("totalCompletion"))
                        .map(val -> getEnumParameter("totalCompletion", val, PTAQueryCompletionStatus.class))
                        .orElse(null)
        );

        return PTAQueryBuilder;
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
