package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import gov.nysenate.ess.supply.statistics.location.LocationStatistic;
import gov.nysenate.ess.supply.statistics.location.LocationStatisticView;
import gov.nysenate.ess.supply.statistics.location.SupplyLocationStatisticService;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/statistics")
public class SupplyStatisticsRestCtrl extends BaseRestApiCtrl {

    @Autowired private SupplyLocationStatisticService locationStatisticService;

    /**
     * Return a Map of location strings to location statistics for easy
     * look up in the front end.
     * @param year The year to generate statistics for.
     * @param month The month to generate statistics for.
     */
    @RequestMapping("/locations")
    public BaseResponse allLocationStatistics(@RequestParam int year, @RequestParam int month) {
        checkPermission(SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
        List<LocationStatistic> locationStatistics = locationStatisticService.getAllLocationStatistics(year, month);
        Map<String, LocationStatisticView> locToStatsMap = new HashMap<>();
        for (LocationStatistic stat : locationStatistics) {
            locToStatsMap.put(stat.getLocation().toString(), new LocationStatisticView(stat));
        }
        return new ViewObjectResponse(MapView.of(locToStatsMap));
    }
}
