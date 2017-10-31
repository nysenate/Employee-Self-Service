package gov.nysenate.ess.travel.addressvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
