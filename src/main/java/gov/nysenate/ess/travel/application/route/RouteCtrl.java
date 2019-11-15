package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/route")
public class RouteCtrl extends BaseRestApiCtrl {

    @Autowired private RouteService routeService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse calculateRoute(@RequestBody RouteView routeView) throws IOException {
        Route route = routeService.createRoute(routeView.toRoute());
        return new ViewObjectResponse<>(new RouteView(route));
    }
}
