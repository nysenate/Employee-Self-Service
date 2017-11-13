package gov.nysenate.ess.travel.addressvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistrictAssignmentService {

    @Autowired
    ObjectMapper jsonObjectMapper;

    public DistrictResponse assignDistrict(Address address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://pubgeo.nysenate.gov/api/v2/district/assign?" +
                "addr1=" + address.getAddr1() + "&" +
                "city="  + address.getCity() + "&" +
                "state=" + address.getState();
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
