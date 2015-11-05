package gov.nysenate.ess.seta.controller.api;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.seta.client.view.AllowanceUsageView;
import gov.nysenate.ess.seta.service.allowance.AllowanceService;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/allowances")
public class AllowanceRestApiCtrl extends BaseRestApiCtrl
{

    private static final Logger logger = LoggerFactory.getLogger(AllowanceRestApiCtrl.class);

    @Autowired
    AllowanceService allowanceService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ListViewResponse getAllowances(@RequestParam Integer[] empId,
                                          @RequestParam Integer[] year) {
        Set<Integer> empIds = Sets.newHashSet(empId);
        Set<Integer> years = Sets.newHashSet(year);
        return ListViewResponse.of(
                empIds.stream()
                        .flatMap(eId -> years.stream()
                                .map(yr -> allowanceService.getAllowanceUsage(eId, yr)))
                        .map(AllowanceUsageView::new)
                        .collect(Collectors.toList())
        );
    }
}
