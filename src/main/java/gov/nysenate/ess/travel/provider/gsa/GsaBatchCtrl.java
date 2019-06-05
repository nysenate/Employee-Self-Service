package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.client.view.base.StringView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/gsa")
public class GsaBatchCtrl extends BaseRestApiCtrl {

    private GsaBatchResponseService gsaBatchResponseService;
    private GsaApi gsaApi;

    @Autowired
    public GsaBatchCtrl(GsaBatchResponseService gsaBatchResponseService, GsaApi gsaApi) {
        this.gsaBatchResponseService = gsaBatchResponseService;
        this.gsaApi = gsaApi;
    }


    @RequestMapping(value = "/batch")
    public StringView updateGsaInformation() throws IOException {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());
        boolean success = gsaBatchResponseService.cycleThroughGsaInfo();
        String responseText = "";
        if (success) {
            responseText = "Success: The GSA data was parsed and stored successfully";
        }
        else {
            responseText = "Failure: The GSA data was not updated";
        }
        return new StringView(responseText);
    }

    @RequestMapping(value = "/{zip}")
    public StringView updateGsaInformation(@PathVariable String zip) throws IOException {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());

        GsaResponse gsaResponse = gsaApi.queryGsa(LocalDate.now(), zip);
        String responseText = "";
        if (gsaResponse != null) {
            responseText = "Success: " + gsaResponse.toString();
        }
        else {
            responseText = "Failure: The GSA data was not updated";
        }
        return new StringView(responseText);
    }
}
