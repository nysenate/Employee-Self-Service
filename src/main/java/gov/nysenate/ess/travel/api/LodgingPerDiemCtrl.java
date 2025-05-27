package gov.nysenate.ess.travel.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.request.allowances.lodging.CreateLodgingPerDiemRequest;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiemService;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BaseRestApiCtrl.REST_PATH + "/travel/lodging-per-diems",
        produces = "application/json",
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class LodgingPerDiemCtrl {

    private final LodgingPerDiemService lodgingPerDiemService;

    @Autowired
    public LodgingPerDiemCtrl(LodgingPerDiemService lodgingPerDiemService) {
        this.lodgingPerDiemService = lodgingPerDiemService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse createLodgingPerDiem(@RequestBody CreateLodgingPerDiemRequest createLodgingPerDiemRequest) {
        LodgingPerDiem lpd = lodgingPerDiemService.createLodgingPerDiem(
                createLodgingPerDiemRequest.date(),
                createLodgingPerDiemRequest.travelAddress());
        return new ViewObjectResponse<>(new LodgingPerDiemView(lpd));
    }
}
