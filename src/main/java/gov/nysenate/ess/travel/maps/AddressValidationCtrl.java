package gov.nysenate.ess.travel.maps;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import jdk.nashorn.api.scripting.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/address/validate")
public class AddressValidationCtrl extends BaseRestApiCtrl {

    private static final Logger log = LoggerFactory.getLogger(AddressValidationCtrl.class);

    @Autowired
    AddressValidationService addressValidationService;

    @RequestMapping(value = "")
    public BaseResponse returnValidationResult(@RequestParam String addr1,
                                               @RequestParam String city,
                                               @RequestParam String state) {

        return new ViewObjectResponse<>(new SageResponseView(addressValidationService.validateAddress(addr1, city, state)));
    }
}