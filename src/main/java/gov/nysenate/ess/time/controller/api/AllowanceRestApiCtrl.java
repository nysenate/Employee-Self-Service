package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.allowances.AllowanceUsageView;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.allowance.AllowanceService;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ALLOWANCE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/allowances")
public class AllowanceRestApiCtrl extends BaseRestApiCtrl
{

    private static final Logger logger = LoggerFactory.getLogger(AllowanceRestApiCtrl.class);

    @Autowired
    AllowanceService allowanceService;

    @RequestMapping(method = GET, produces = "application/json")
    public ListViewResponse getAllowances(@RequestParam Integer[] empId,
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
                        .collect(Collectors.toList())
        );
    }
}
