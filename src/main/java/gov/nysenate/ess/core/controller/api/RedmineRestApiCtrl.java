package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.RedmineEmpStatusChangeView;
import gov.nysenate.ess.core.client.view.RedmineEmployeeView;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
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

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.REDMINE_API_ACCESS;
import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Contains API endpoints intended for use by Redmine Redmine Plugins
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/redmine")
public class RedmineRestApiCtrl extends BaseRestApiCtrl {
    /**
     * Set of transaction codes that redmine is allowed to query for.
     */
    private static final Set<TransactionCode> allowedCodes = ImmutableSet.of(
            APP, LOC, NAM, PHO, RTP, LIN, EMP, RSH);

    private final EmployeeInfoService empInfoService;
    private final EmpTransactionDao empTransactionDao;

    public RedmineRestApiCtrl(EmployeeInfoService empInfoService,
                              EmpTransactionDao empTransactionDao) {
        this.empInfoService = empInfoService;
        this.empTransactionDao = empTransactionDao;
    }

    /**
     * Search over active and inactive employees. The term is tokenized and every token must match the
     * employee's full name or uid/email, so the search is insensitive to word order and middle
     * initials. Results are ranked by match quality. Get a limited search response containing only
     * Redmine relevant fields.
     *
     * @param term    - String - search term matched against full name and uid/email
     * @param request - WebRequest
     * @return ListViewResponse<RedmineEmployeeView> - search results
     */
    @RequestMapping(value = "/employee/search", method = {GET, HEAD})
    public ListViewResponse<RedmineEmployeeView> employeeSearch(@RequestParam(defaultValue = "") String term,
                                                                WebRequest request) {
        checkPermission(REDMINE_API_ACCESS.getPermission());
        LimitOffset limitOffset = getLimitOffset(request, 20);
        EmployeeSearchBuilder searchBuilder = new EmployeeSearchBuilder()
                .setName(term)
                .setFreeTextNameMatch(true);
        PaginatedList<Employee> results = empInfoService.searchEmployees(searchBuilder, limitOffset);
        // Report the tokens that were matched so the client can highlight them in the results.
        List<String> matchedTerms = EmployeeSearchBuilder.tokenizeSearchTerm(term);
        return ListViewResponse.fromPaginatedList(
                results, emp -> new RedmineEmployeeView(emp, matchedTerms));
    }

    /**
     * Retrieve info for a specific employee.
     *
     * @param empId - int - id of employee to lookup
     * @return ViewObjectResponse<RedmineEmployeeView> - employee info
     */
    @RequestMapping(value = "/employee/{empId}", method = {GET, HEAD})
    public ViewObjectResponse<RedmineEmployeeView> empLookup(@PathVariable int empId) {
        checkPermission(REDMINE_API_ACCESS.getPermission());
        Employee emp = empInfoService.getEmployee(empId);
        return new ViewObjectResponse<>(
                new RedmineEmployeeView(emp), "employee"
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
     * @return ListViewResponse<RedmineEmpStatusChangeView>
     */
    @RequestMapping(value = "/statusChanges", method = {GET, HEAD})
    public ListViewResponse<RedmineEmpStatusChangeView> getStatusChangeEmps(@RequestParam(required = false) String from,
                                                                            @RequestParam(required = false) String to) {
        checkPermission(REDMINE_API_ACCESS.getPermission());
        LocalDate fromDate = Optional.ofNullable(from)
                .map(f -> parseISODate(f, "from"))
                .orElse(LocalDate.now().minusDays(1));
        LocalDate toDate = Optional.ofNullable(to)
                .map(f -> parseISODate(f, "to"))
                .orElse(DateUtils.THE_FUTURE);
        Range<LocalDate> dateRange = getClosedOpenRange(fromDate, toDate, "from", "to");

        List<TransactionRecord> transactionRecords = empTransactionDao.getRecordsByPostDate(dateRange, allowedCodes);

        List<RedmineEmpStatusChangeView> empStatusChangeViews = transactionRecords.stream()
                .map(tRec -> new RedmineEmpStatusChangeView(
                        empInfoService.getEmployee(tRec.getEmployeeId()), tRec))
                .toList();

        return ListViewResponse.of(empStatusChangeViews);
    }
}