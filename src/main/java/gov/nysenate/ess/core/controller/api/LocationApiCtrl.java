package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.base.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/locations")
public class LocationApiCtrl extends BaseRestApiCtrl {

    @Autowired private LocationService locationService;

    @RequestMapping(value = "")
    public BaseResponse getLocations() {
        return ListViewResponse.of(locationService.getLocations(true).stream().map(LocationView::new).collect(Collectors.toList()));
    }

    @RequestMapping(value = "", params = {"locCode", "locType"})
    public BaseResponse getLocationByCodeAndType(@RequestParam String locCode, @RequestParam char locType) {
        return new ViewObjectResponse<>(new LocationView(locationService.getLocation(
                new LocationId(locCode, locType))));
    }

    @RequestMapping(value = "/search")
    public BaseResponse searchLocations(@RequestParam String term) {
        return ListViewResponse.of(locationService.searchLocations(term)
                                              .stream()
                                              .map(LocationView::new)
                                              .collect(Collectors.toList()));
    }
}
