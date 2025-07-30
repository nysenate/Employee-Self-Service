package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.BACHelpEmpStatusChangeView;
import gov.nysenate.ess.core.client.view.BACHelpEmployeeView;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;

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
            APP, LOC, NAM, PHO, RTP, LIN, EMP);

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
    @RequestMapping(value = "empSearch", params = "term")
    public ListViewResponse<BACHelpEmployeeView> employeeSearch(@RequestParam String term,
                                                                WebRequest request) {
        LimitOffset limitOffset = getLimitOffset(request, 20);
        PaginatedList<Employee> results = empInfoService.searchEmployees(term, false, limitOffset);
        return ListViewResponse.fromPaginatedList(results, BACHelpEmployeeView::new);
    }

    /**
     * Get a list of employees that had certain status changes after the given "from" datetime.
     *
     * @param from - String - date time string
     * @return ListViewResponse<BACHelpEmpStatusChangeView>
     */
    @RequestMapping(value = "statusChanges", params = "from")
    public ListViewResponse<BACHelpEmpStatusChangeView> getStatusChangeEmps(@RequestParam String from) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        if (fromDateTime.isBefore(weekAgo)) {
            throw new InvalidRequestParamEx(fromDateTime, "from", "datetime",
                    "from datetime must not be earlier than 7 days ago");
        }

        List<TransactionRecord> transactionRecords = empTransactionDao.postedRecordsSince(fromDateTime, allowedCodes);

        List<BACHelpEmpStatusChangeView> empStatusChangeViews = transactionRecords.stream()
                .map(tRec -> new BACHelpEmpStatusChangeView(
                        empInfoService.getEmployee(tRec.getEmployeeId()), tRec))
                .toList();

        return ListViewResponse.of(empStatusChangeViews);
    }
}