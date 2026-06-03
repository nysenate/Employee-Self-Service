package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.BACHelpEmpStatusChangeView;
import gov.nysenate.ess.core.client.view.BACHelpEmployeeView;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.BACHELP_API_ACCESS;
import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Contains API endpoints intended for use by BACHelp Redmine Plugins
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/bachelp")
public class BACHelpRestApiCtrl extends BaseRestApiCtrl {
    /**
     * Set of transaction codes that bachelp is allowed to query for.
     */
    private static final Set<TransactionCode> allowedCodes = ImmutableSet.of(
            APP, LOC, NAM, PHO, RTP, LIN, EMP, RSH);

    private final EmployeeInfoService empInfoService;
    private final EmpTransactionDao empTransactionDao;

    public BACHelpRestApiCtrl(EmployeeInfoService empInfoService,
                              EmpTransactionDao empTransactionDao) {
        this.empInfoService = empInfoService;
        this.empTransactionDao = empTransactionDao;
    }

    /**
     * Perform a simple search over active and inactive employees, matching full name.
     * Get a limited search response containing only BACHelp relevant fields.
     *
     * @param term    - String - search term used to match against full name
     * @param request - WebRequest
     * @return ListViewResponse<BACHelpEmployeeView> - search results
     */
    @RequestMapping(value = "/employee/search", method = {GET, HEAD})
    public ListViewResponse<BACHelpEmployeeView> employeeSearch(@RequestParam(defaultValue = "") String term,
                                                                WebRequest request) {
        checkPermission(BACHELP_API_ACCESS.getPermission());
        LimitOffset limitOffset = getLimitOffset(request, 20);
        PaginatedList<Employee> results = empInfoService.searchEmployees(term, false, limitOffset);
        return ListViewResponse.fromPaginatedList(results, BACHelpEmployeeView::new);
    }

    /**
     * Retrieve info for a specific employee.
     *
     * @param empId - int - id of employee to lookup
     * @return ViewObjectResponse<BACHelpEmployeeView> - employee info
     */
    @RequestMapping(value = "/employee/{empId}", method = {GET, HEAD})
    public ViewObjectResponse<BACHelpEmployeeView> empLookup(@PathVariable int empId) {
        checkPermission(BACHELP_API_ACCESS.getPermission());
        Employee emp = empInfoService.getEmployee(empId);
        return new ViewObjectResponse<>(
                new BACHelpEmployeeView(emp), "employee"
        );
    }

    @ExceptionHandler(EmployeeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ViewObjectErrorResponse handleEmpNotFoundEx(EmployeeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getEmpId());
    }

    /**
     * Get a list of employees that had certain status changes on or after the given "from" date,
     * and before the given "to" date
     *
     * @param from - String - Defaults to one day ago
     * @param to   - String - Defaults to far in the future
     * @return ListViewResponse<BACHelpEmpStatusChangeView>
     */
    @RequestMapping(value = "/statusChanges", method = {GET, HEAD})
    public ListViewResponse<BACHelpEmpStatusChangeView> getStatusChangeEmps(@RequestParam(required = false) String from,
                                                                            @RequestParam(required = false) String to) {
        checkPermission(BACHELP_API_ACCESS.getPermission());
        LocalDate fromDate = Optional.ofNullable(from)
                .map(f -> parseISODate(f, "from"))
                .orElse(LocalDate.now().minusDays(1));
        LocalDate toDate = Optional.ofNullable(to)
                .map(f -> parseISODate(f, "to"))
                .orElse(DateUtils.THE_FUTURE);
        Range<LocalDate> dateRange = getClosedOpenRange(fromDate, toDate, "from", "to");

        List<TransactionRecord> transactionRecords = empTransactionDao.getRecordsByPostDate(dateRange, allowedCodes);

        List<BACHelpEmpStatusChangeView> empStatusChangeViews = transactionRecords.stream()
                .map(tRec -> new BACHelpEmpStatusChangeView(
                        empInfoService.getEmployee(tRec.getEmployeeId()), tRec))
                .toList();

        return ListViewResponse.of(empStatusChangeViews);
    }
}