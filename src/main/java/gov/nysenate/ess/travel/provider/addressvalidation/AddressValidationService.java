package gov.nysenate.ess.travel.provider.addressvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class AddressValidationService {

    private String sageBaseUrl;
    private ObjectMapper jsonObjectMapper;

    @Autowired
    public AddressValidationService(@Value("${sage.api.url}") String sageBaseUrl, ObjectMapper jsonObjectMapper) {
        this.sageBaseUrl = sageBaseUrl;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    public SageResponse validateAddress(String addr, String city, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String url = sageBaseUrl + "/address/validate?" +
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

    public SageResponse validateAddress(Address address) {
       return validateAddress(address.getAddr1(), address.getCity(), address.getState());
    }
}
