package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeActiveDatesView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.EmployeeSearchView;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.EMPLOYEE_INFO;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/employees")
public class EmployeeRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRestApiCtrl.class);

    @Autowired protected EmployeeDao employeeDao;
    @Autowired private EmployeeInfoService empInfoService;

    /**
     * Get Employee Info API
     * ---------------------
     * Get current personnel and payroll data for the requested employee
     *
     * Usage:       (GET) /api/v1/employees
     *
     * Request Params:
     * @param empId Integer - required - the employee id of the requested employee
     * @param detail boolean - will return more information if true
     * @return {@link EmployeeView} or {@link DetailedEmployeeView} depending on value of <code>detail</code>
     * @throws EmployeeException if something gets messed up
     */
    @RequestMapping(value = "", method = {GET, HEAD}, params = "empId")
    public BaseResponse getEmployeeById(@RequestParam(required = true) Integer empId[],
                                        @RequestParam(defaultValue = "false") boolean detail) throws EmployeeException {
        Arrays.stream(empId)
                .map(eId -> new CorePermission(eId, EMPLOYEE_INFO, GET))
                .forEach(this::checkPermission);

        return getEmployeeResponse(
            Arrays.asList(empId).stream().map(empInfoService::getEmployee).collect(toList()), detail);
    }

    /**
     * Get Active Employee API
     * -----------------------
     *
     * Get a list of all currently active employees
     *
     * Usage:       (GET) /api/v1/employees/active
     *
     * @return {@link ListViewResponse<EmployeeSearchView>}
     */
    @RequestMapping(value = "/active", method = {GET, HEAD})
    public ListViewResponse<EmployeeSearchView> getAllEmployees(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        Set<Employee> employees = empInfoService.getAllEmployees(activeOnly);

        List<EmployeeSearchView> employeeViewList = employees.stream()
                .map(EmployeeSearchView::new)
                .collect(Collectors.toList());

        return ListViewResponse.of(employeeViewList, "employees");
    }

    /**
     * Employee Search API
     * -----------------------
     *
     * Search active employees by their full name.
     *
     * Usage:       (GET) /api/v1/employees/search
     *
     * Request Params:
     * @param term String - The search term. Matched against employee full names.
     * @param empId int - default 0 - an optional param that overrides term and will return an employee
     *              with the given employee id, if one exists
     * @return {@link ListViewResponse<EmployeeSearchView>}
     */
    @RequestMapping(value = "/search", method = {GET, HEAD})
    public ListViewResponse<EmployeeSearchView> searchEmployees(
            @RequestParam(defaultValue = "") String term,
            @RequestParam(defaultValue = "0") int empId,
            @RequestParam(defaultValue = "false") boolean activeOnly,
            WebRequest request) {

        LimitOffset limitOffset = getLimitOffset(request, 10);
        PaginatedList<Employee> empSearchResults;

        // If a > 0 emp id is passed in, override term search and return that employee
        if (empId > 0) {
            List<Employee> employeeList = new ArrayList<>();
            try {
                Employee employee = empInfoService.getEmployee(empId);
                employeeList.add(employee);
            } catch (EmployeeNotFoundEx ignored) {}
            empSearchResults = new PaginatedList<>(employeeList.size(), LimitOffset.ALL, employeeList);
        } else {
            empSearchResults = empInfoService.searchEmployees(term, activeOnly, limitOffset);
        }

        List<EmployeeSearchView> employeeViewList = empSearchResults.getResults().stream()
                .map(EmployeeSearchView::new)
                .collect(Collectors.toList());

        return ListViewResponse.of(employeeViewList, "employees",
                empSearchResults.getTotal(), empSearchResults.getLimOff());
    }

    /**
     * Get Employee Active Years API
     * -----------------------------
     * Get a list of years that the employee was active
     *
     * Usage:       (GET) /api/v1/employees/activeYears
     *
     * Request Params:
     * @param empId Integer - required - the employee id of the requested employee
     * @return {@link ListViewResponse} of integers containing active years
     */
    @RequestMapping(value = "/activeYears")
    public BaseResponse getEmployeeYearsActive(@RequestParam(required = true) Integer empId,
                                               @RequestParam(defaultValue = "false") boolean fiscalYear) {
        checkPermission(new CorePermission(empId, EMPLOYEE_INFO, GET));

        return ListViewResponse.ofIntList(empInfoService.getEmployeeActiveYearsService(empId, fiscalYear), "activeYears");
    }

    /**
     * Get Employee Active Dates API
     * -----------------------------
     * Get a list of dates that the employee was active
     *
     * Usage:       (GET) /api/v1/employees/activeDates
     *
     * Request Params:
     * @param empId Integer - required - the employee id of the requested employee
     * @return {@link ViewObjectResponse} containing an {@link EmployeeActiveDatesView}
     */
    @RequestMapping(value = "/activeDates")
    public BaseResponse employeeActiveDates(@RequestParam Integer empId) {
        checkPermission(new CorePermission(empId, EMPLOYEE_INFO, GET));

        RangeSet<LocalDate> activeDates = empInfoService.getEmployeeActiveDatesService(empId);

        return new ViewObjectResponse<>(new EmployeeActiveDatesView(empId, activeDates), "activeDates");
    }

    /* --- Internal Methods --- */

    private BaseResponse getEmployeeResponse(List<Employee> employeeList, boolean detail) {
        if (employeeList.size() == 1) {
            return new ViewObjectResponse<>((detail) ? new DetailedEmployeeView(employeeList.get(0))
                                                     : new EmployeeView(employeeList.get(0)), "employee");
        }
        else {
            return ListViewResponse.of(
                employeeList.stream().map((detail) ? DetailedEmployeeView::new : EmployeeView::new).collect(toList()),
                "employees");
        }
    }
}
