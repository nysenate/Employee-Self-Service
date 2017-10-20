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
@RequestMapping("https://pubgeo.nysenate.gov/api/v2/address/validate")
public class AddressValidationCtrl extends BaseRestApiCtrl {

    private static final Logger log = LoggerFactory.getLogger(AddressValidationCtrl.class);

    @RequestMapping(value="")
    public static BaseResponse validateAddress(@RequestParam String addr1,
                                               @RequestParam String city,
                                               @RequestParam String state) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://pubgeo.nysenate.gov/api/v2/address/validate?" +
                     "addr1=" + addr1 + "&" +
                     "city="  + city + "&" +
                     "state=" + state;
        String resp = restTemplate.getForObject(url, String.class);
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(resp).getAsJsonObject();
        JsonElement status = jsonResponse.get("status");
        System.out.println(status.toString());
        return null;
    }

    public static void main(String[] args) {
        validateAddress("100 South Swan St", "Albany", "NY");
        validateAddress("100000000 east nowhere", "timbuktu", "albania");
        validateAddress("515 Loudon Road", "", "");
    }
}