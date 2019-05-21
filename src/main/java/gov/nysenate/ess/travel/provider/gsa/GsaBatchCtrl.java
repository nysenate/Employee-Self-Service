package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.client.view.base.StringView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/batch/gsa")
public class GsaBatchCtrl extends BaseRestApiCtrl {

    private GsaBatchResponseService gsaBatchResponseService;

    @Autowired
    public GsaBatchCtrl(GsaBatchResponseService gsaBatchResponseService) {
        this.gsaBatchResponseService = gsaBatchResponseService;
    }


    @RequestMapping(value = "")
    public StringView updateGsaInformation() throws IOException {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());
        boolean success = gsaBatchResponseService.cycleThroughGsaInfo();
        String repsonseText = "";
        if (success) {
            repsonseText = "Success: The GSA data was parsed and stored successfully";
        }
        else {
            repsonseText = "Failure: The GSA data was not updated";
        }
        return new StringView(repsonseText);
    }
}
