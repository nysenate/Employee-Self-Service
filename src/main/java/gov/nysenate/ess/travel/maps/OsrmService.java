package gov.nysenate.ess.travel.maps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OsrmService implements MapInterface {

    @Override
    public TripDistance getTripDistance(List<Address> travelRoute) {
        return null;
    }

    public static TripDistance getTripDistances(List<Address> tripRoute) {
        TripDistance tripDistance = new TripDistance();

        //convert Addresses into lat/lng

        double distance;

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://router.project-osrm.org/route/v1/driving/";
        url += "42.6525655,-73.7632602;";
        url += "42.6848185,-73.7841829";
        url += "?overview=false";
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(resp).getAsJsonObject();

            System.out.println(jsonObject.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("distance"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tripDistance;
    }

    public static void main(String[] args) {
        getTripDistances(new LinkedList<>());
    }
}
