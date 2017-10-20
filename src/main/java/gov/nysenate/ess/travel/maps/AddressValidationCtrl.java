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

    @RequestMapping(value="")
    public boolean returnValidationResult(@RequestParam String addr1,
                                          @RequestParam String city,
                                          @RequestParam String state) {

        if (validateAddress(addr1, city, state).equals("SUCCESS")) return true;
        else return false;
    }

    private String validateAddress(String addr1, String city, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://pubgeo.nysenate.gov/api/v2/address/validate?" +
                "addr1=" + addr1 + "&" +
                "city="  + city + "&" +
                "state=" + state;
        String resp = restTemplate.getForObject(url, String.class);
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(resp).getAsJsonObject();
        JsonElement status = jsonResponse.get("status");
        return status.getAsString();
    }
}