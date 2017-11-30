package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.allowances.AllowanceUsageView;
import gov.nysenate.ess.time.client.view.allowances.PeriodAllowanceUsageView;
import gov.nysenate.ess.time.model.allowances.PeriodAllowanceUsage;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.allowance.AllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ALLOWANCE;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/allowances")
public class AllowanceRestApiCtrl extends BaseRestApiCtrl
{

    private static final Logger logger = LoggerFactory.getLogger(AllowanceRestApiCtrl.class);

    private final AllowanceService allowanceService;

    @Autowired
    public AllowanceRestApiCtrl(AllowanceService allowanceService) {
        this.allowanceService = allowanceService;
    }

    /**
     * Get Allowance Api
     * -----------------
     *
     * Get employees' allowance usage for given years:
     * (GET) /api/v1/allowances[.json]
     *
     * Request Params:
     * @param empId int[] - required - Employee ids for retrieved allowances
     * @param year int[] - required - Years for which allowances will be retrieved
     *
     * @return {@link ListViewResponse<AllowanceUsageView>} allowance usages for each employee for each year
     */
    @RequestMapping(method = {GET, HEAD}, produces = "application/json")
    public ListViewResponse<AllowanceUsageView> getAllowances(@RequestParam Integer[] empId,
                                                              @RequestParam Integer[] year) {
        Set<Integer> empIds = Sets.newHashSet(empId);
        Set<Integer> years = Sets.newHashSet(year);

        // Check permissions
        empIds.forEach(eId ->
                years.stream()
                        .map(yr -> new EssTimePermission(eId, ALLOWANCE, GET, DateUtils.yearDateRange(yr)))
                        .forEach(this::checkPermission)
        );

        return ListViewResponse.of(
                empIds.stream()
                        .flatMap(eId -> years.stream()
                                .map(yr -> allowanceService.getAllowanceUsage(eId, yr)))
                        .map(AllowanceUsageView::new)
                        .collect(toList())
        );
    }

    /**
     * Get Period Allowance Api
     * ------------------------
     *
     * Get a breakdown of an employee's allowance usage by period for a given year:
     * (GET) /api/v1/allowances/period[.json]
     *
     * Request Params:
     * @param empId int - required - Employee id for retrieved allowance
     * @param year int - required - Year for retrieved allowance
     *
     * @return {@link ListViewResponse<PeriodAllowanceUsageView>}
     *  The employee's allowance usage for each active period in the given year.
     */
    @RequestMapping(value = "/period" ,method = {GET, HEAD}, produces = "application/json")
    public ListViewResponse<PeriodAllowanceUsageView> getPeriodAllowances(@RequestParam int empId,
                                                                          @RequestParam int year) {
        checkPermission(new EssTimePermission(empId, ALLOWANCE, GET, DateUtils.yearDateRange(year)));

        List<PeriodAllowanceUsage> periodAllowanceUsage = allowanceService.getPeriodAllowanceUsage(empId, year);
        List<PeriodAllowanceUsageView> perAllowUsageViews = periodAllowanceUsage.stream()
                .map(PeriodAllowanceUsageView::new)
                .collect(toList());

        return ListViewResponse.of(perAllowUsageViews);
    }
}
