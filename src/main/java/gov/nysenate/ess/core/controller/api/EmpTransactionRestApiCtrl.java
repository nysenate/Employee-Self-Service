package gov.nysenate.ess.core.controller.api;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.EmpTransItemView;
import gov.nysenate.ess.core.client.view.EmpTransRecordView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionType;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.TRANSACTION_HISTORY;
import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/empTransactions")
public class EmpTransactionRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmpTransactionRestApiCtrl.class);

    @Autowired private EmpTransactionService transactionService;
    @Autowired private EmployeeDao employeeDao;

    /**
     * Transactions for employee API
     * -----------------------------
     *
     * Returns a list of transactions within the given date range for a specific employee.
     *
     * @param empId int - Employee id
     * @param fromDate String - from date (inclusive)
     * @param toDate String - to date (inclusive)
     * @param codes String - comma separated list of tx codes
     * @param type String - Get only transaction codes of a specific type
     *             (PAY or PER for payroll or personnel) (overrides 'codes' param)
     * @param restrictValues boolean - restrict returned values for each transaction record
     *                       to only the values explicitly set by the transaction
     *                       as determined by the transactions code. (default false)
     *                       @see TransactionCode
     * @return ListViewResponse of EmpTransRecordViews
     */
    @RequestMapping("")
    public BaseResponse getTransactionsByEmpId(@RequestParam Integer empId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate,
                                               @RequestParam(required = false) String codes,
                                               @RequestParam(required = false) String type,
                                               @RequestParam(defaultValue = "false") boolean restrictValues) {
        checkPermission(new CorePermission(empId, TRANSACTION_HISTORY, GET));

        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from-date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to-date") : DateUtils.THE_FUTURE;
        Range<LocalDate> range = Range.closed(fromLocalDate, toLocalDate);
        Set<TransactionCode> codeSet = type != null
                ? TransactionCode.getTransactionsOfType(
                        getEnumParameter("type", type, TransactionType.class))
                : getTransCodesFromString(codes);
        return ListViewResponse.of(
            transactionService.getTransHistory(empId)
                .getTransRecords(range, codeSet, SortOrder.ASC).stream()
                .map(record -> new EmpTransRecordView(record, restrictValues))
                .collect(toList()), "transactions");
    }

    @RequestMapping("/snapshot")
    public BaseResponse getFlatTransactionByEmpId(@RequestParam Integer empId,
                                                  @RequestParam(required = false) String date) {
        checkPermission(new CorePermission(empId, TRANSACTION_HISTORY, GET));

        LocalDate localDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();
        Map<String, EmpTransItemView> itemMap = new HashMap<>();
        transactionService.getTransHistory(empId).getRecordSnapshots()
                .floorEntry(localDate).getValue().forEach(
                        (key, value) -> itemMap.put(key, new EmpTransItemView(key, value)));
        return new ViewObjectResponse<>(MapView.of(itemMap), "snapshot");
    }

    /**
     * Retrieves a snapshot of the current state of an employees data.
     * This method should be used when looking at current data as it is more accurate
     *  than reconstructing data from the transaction audit trail.
     *
     * @param empId Integer - employee id
     */
    @RequestMapping("/snapshot/current")
    public BaseResponse getCurrentTransactionByEmpId(@RequestParam Integer empId) {
        checkPermission(new CorePermission(empId, TRANSACTION_HISTORY, GET));

        return new ViewObjectResponse<>(
                MapView.of(
                        employeeDao.getRawEmployeeColumns(empId).entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        (entry) -> new EmpTransItemView(entry.getKey(), entry.getValue())))
                ),
                "snapshot"
        );
    }

    private static EnumSet<TransactionCode> timelineCodes =
        EnumSet.of(APP, RTP, TYP, PHO, SAL, SUP, MAR, LEG, LOC, EMP);

    /**
     * The timeline API returns a subset of the transaction records that can be used to display
     * the updates that general end users will care about.
     *
     * @param empId int - Employee id
     * @return List of EmpTransRecordViews
     */
    @RequestMapping("/timeline")
    public BaseResponse getEmpTimeline(@RequestParam Integer empId) {
        checkPermission(new CorePermission(empId, TRANSACTION_HISTORY, GET));

        return ListViewResponse.of(
            transactionService.getTransHistory(empId)
                .getTransRecords(DateUtils.ALL_DATES, timelineCodes, SortOrder.ASC)
                .stream()
                .map(EmpTransRecordView::new)
                .collect(toList()), "transactions");
    }

    /** --- Internal --- */

    private Set<TransactionCode> getTransCodesFromString(String codes) {
        if (StringUtils.isNotBlank(codes)) {
            List<String> codeStrList = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(codes.toUpperCase());

            Set<TransactionCode> codeSet = new HashSet<>();
            for (String code : codeStrList) {
                try {
                    codeSet.add(TransactionCode.valueOf(code));
                }
                catch (IllegalArgumentException ex) {
                    throw new InvalidRequestParamEx(codes, "codes", "String",
                            "comma separated transaction codes: " + code + " is not a valid transaction code.");
                }
            }
            return codeSet;
        }
        return TransactionCode.getAll();
    }
}