package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/statistics")
public class SupplyStatisticsRestCtrl extends BaseRestApiCtrl {

    @RequestMapping("/{locId}")
    public BaseResponse locationStatistics(@PathVariable String locId) {

        return null;
    }
}
