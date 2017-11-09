package gov.nysenate.ess.travel.maps;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OsrmService implements MapInterface {

    @Override
    public TripDistance getTripDistance(List<Address> tripRoute) {
        TripDistance tripDistance = new TripDistance();

        List<String> tripRouteCoordinates = addressesToCoordinates(tripRoute);

        tripDistance.setTripDistanceTotal(getResponse(tripRouteCoordinates));
        tripRouteCoordinates.remove(tripRouteCoordinates.size() - 1);
        tripDistance.setTripDistanceOut(getResponse(tripRouteCoordinates));

        return tripDistance;
    }

    private double getResponse(List<String> tripRouteCoordinates) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://router.project-osrm.org/route/v1/driving/";
        for (int i = 0; i < tripRouteCoordinates.size(); i++) {
            url += tripRouteCoordinates.get(i);
            if (i < tripRouteCoordinates.size()-1) {
                url += ";";
            }
        }
        url += "?overview=false";
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(resp).getAsJsonObject();

            // returns distance in meters
            // divide by 1609.344 to get miles
            return jsonObject.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsDouble() / 1609.344;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<String> addressesToCoordinates(List<Address> addresses) {
        RestTemplate restTemplate = new RestTemplate();
        ArrayList<String> coordinates = new ArrayList<>();
        for (Address address: addresses) {
            String url = "https://pubgeo.nysenate.gov/api/v2/geo/geocode?";
            url += "addr1=" + address.getAddr1();
            url += "&city=" + address.getCity();
            url += "&state=" + address.getState();

            String lngLat = "";

            try {
                String resp = restTemplate.getForObject(url, String.class);
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(resp).getAsJsonObject().get("geocode").getAsJsonObject();
                String lng = jsonObject.get("lon").getAsString();
                String lat = jsonObject.get("lat").getAsString();
                lngLat = lng + "," + lat;
            } catch (Exception e) {
                e.printStackTrace();
            }
            coordinates.add(lngLat);
        }
        return coordinates;
    }
}