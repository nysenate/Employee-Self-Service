package gov.nysenate.ess.web.controller.rest;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.view.EmpTransItemView;
import gov.nysenate.ess.core.client.view.EmpTransRecordView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.web.client.response.base.BaseResponse;
import gov.nysenate.ess.web.client.response.base.ListViewResponse;
import gov.nysenate.ess.web.client.response.base.ViewObjectResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/empTransactions")
public class EmpTransactionRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmpTransactionRestCtrl.class);

    @Autowired private EmpTransactionService transactionService;

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
     * @return ListViewResponse of EmpTransRecordViews
     */
    @RequestMapping("")
    public BaseResponse getTransactionsByEmpId(@RequestParam Integer empId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate,
                                               @RequestParam(required = false) String codes) {
        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from-date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to-date") : DateUtils.THE_FUTURE;
        Range<LocalDate> range = Range.closed(fromLocalDate, toLocalDate);
        Set<TransactionCode> codeSet = getTransCodesFromString(codes);
        return ListViewResponse.of(
            transactionService.getTransHistory(empId)
                .getTransRecords(range, codeSet, SortOrder.ASC).stream()
                .map(EmpTransRecordView::new)
                .collect(toList()), "transactions");
    }

    @RequestMapping("/snapshot")
    public BaseResponse getFlatTransactionByEmpId(@RequestParam Integer empId,
                                                  @RequestParam(required = false) String date) {
        LocalDate localDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();
        Map<String, EmpTransItemView> itemMap = new HashMap<>();
        transactionService.getTransHistory(empId).getRecordSnapshots()
            .floorEntry(localDate).getValue().entrySet().stream().forEach(e -> {
            itemMap.put(e.getKey(), new EmpTransItemView(e.getKey(), e.getValue()));
        });
        return new ViewObjectResponse<>(MapView.of(itemMap), "snapshot");
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
                .splitToList(codes);

            Set<TransactionCode> codeSet = new HashSet<>();
            for (String code : codeStrList) {
                try {
                    codeSet.add(TransactionCode.valueOf(code));
                }
                catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException(code + " is not a valid transaction code.");
                }
            }
            return codeSet;
        }
        return TransactionCode.getAll();
    }
}