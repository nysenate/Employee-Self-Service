package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/statistics")
public class SupplyStatisticsRestCtrl extends BaseRestApiCtrl {

    @Autowired private SupplyStatisticsService statisticsService;

    @RequestMapping("/{locId}")
    public BaseResponse locationStatistics(@PathVariable String locId, @RequestParam int year, @RequestParam int month) {
        ItemStatistic itemStatistic = statisticsService.getItemStatistics(locId, year, month);
        return new ViewObjectResponse<>(new ItemStatisticView(itemStatistic));
    }
}
