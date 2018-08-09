package gov.nysenate.ess.travel.provider.addressvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistrictAssignmentService {

    private String sageBaseUrl;
    private ObjectMapper jsonObjectMapper;

    @Autowired
    public DistrictAssignmentService(@Value("${sage.api.url}") String sageBaseUrl, ObjectMapper jsonObjectMapper) {
        this.sageBaseUrl = sageBaseUrl;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    public DistrictResponse assignDistrict(Address address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = sageBaseUrl + "/district/assign?" +
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
