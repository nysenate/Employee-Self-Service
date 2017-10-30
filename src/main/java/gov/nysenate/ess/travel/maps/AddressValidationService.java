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
    }
}
