package gov.nysenate.ess.travel.addressvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistrictAssignmentService {

    @Autowired
    ObjectMapper jsonObjectMapper;

    public DistrictResponse assignDistrict(String addr, String city, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://pubgeo.nysenate.gov/api/v2/district/assign?" +
                "addr1=" + addr + "&" +
                "city="  + city + "&" +
                "state=" + state;
        JsonParser parser = new JsonParser();
        int senateDistNumber = 0;
        String senateDistName = null;
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonObject senateInfo = parser.parse(resp).getAsJsonObject().get("districts")
                    .getAsJsonObject().get("senate").getAsJsonObject();
            senateDistName = senateInfo.get("name").getAsString();
            senateDistNumber = senateInfo.get("district").getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DistrictResponse(senateDistName, senateDistNumber);
    }
}
