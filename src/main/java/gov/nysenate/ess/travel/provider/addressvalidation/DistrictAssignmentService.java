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
                "addr1=" + address.getAddr1();
        if(!address.getCity().isEmpty() || address.getCity() != null) {
            url = url + "&city="  + address.getCity();
        }
        if(!address.getState().isEmpty() || address.getState() != null) {
            url = url + "&state=" + address.getState();
        }
        if(!address.getZip5().isEmpty() || address.getZip5() != null) {
            url = url + "&zip5=" + address.getZip5();
        }

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
