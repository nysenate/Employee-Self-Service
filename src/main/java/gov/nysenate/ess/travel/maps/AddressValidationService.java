package gov.nysenate.ess.travel.maps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.config.JacksonConfig;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class AddressValidationService {

    @Autowired
    ObjectMapper jsonObjectMapper;

    public SageResponse validateAddress(String addr, String city, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://pubgeo.nysenate.gov/api/v2/address/validate?" +
                "addr1=" + addr + "&" +
                "city="  + city + "&" +
                "state=" + state;
        String resp = restTemplate.getForObject(url, String.class);
        SageResponse sageResponse = null;
        try {
            sageResponse = jsonObjectMapper.readValue(resp, SageResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sageResponse;
//        JsonParser parser = new JsonParser();
//        JsonObject jsonResponse = parser.parse(resp).getAsJsonObject();
//        String status = jsonResponse.get("status").getAsString();
//        String source = jsonResponse.get("source").getAsString();
//
//        JsonObject addressObject = jsonResponse.getAsJsonObject("address");
//
//        String addr1 = jsonResponse.getAsJsonObject("address").get("addr1").getAsString();
//        String addr2 = jsonResponse.getAsJsonObject("address").get("addr2").getAsString();
//        String cityR = jsonResponse.getAsJsonObject("address").get("city").getAsString();
//        String stateR = jsonResponse.getAsJsonObject("address").get("state").getAsString();
//        String zip5 = jsonResponse.getAsJsonObject("address").get("zip5").getAsString();
//        String zip4 = jsonResponse.getAsJsonObject("address").get("zip4").getAsString();
//        Address address = new Address(addr1, addr2, cityR, stateR, zip5, zip4);
//
//        boolean validated = jsonResponse.get("validated").getAsBoolean();
//        int statusCode = jsonResponse.get("statusCode").getAsInt();
//        String description = jsonResponse.get("description").getAsString();
//
//        return new SageResponse(status, source, address, validated, statusCode, description);
    }
}
