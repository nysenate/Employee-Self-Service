package gov.nysenate.ess.travel.maps;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.application.model.Itinerary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Uses the OSRM API to calculate driving distance for a trip.
 * Used as a backup datasource if google does not work.
 * TODO Currently not fully implemented. Need to implement getLegDistance and add to MileageAllowanceService.
 */
@Service
public class OsrmMapService implements MapService {

    /**
     * TODO Implement!
     * @param leg
     * @return
     */
    @Override
    public long getLegDistance(Leg leg) {
        throw new NotImplementedException();
    }

    /**
     * Old method of querying ORSM.
     * Use as reference when implementing getLegDistance.
     */
//    public TripDistance getTripDistance(Itinerary itinerary) {
//        List<Address> tripRoute = itinerary.travelRoute();
//        TripDistance tripDistance = new TripDistance();
//
//        List<String> tripRouteCoordinates = addressesToCoordinates(tripRoute);
//
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://router.project-osrm.org/route/v1/driving/";
//        for (int i = 0; i < tripRouteCoordinates.size(); i++) {
//            url += tripRouteCoordinates.get(i);
//            if (i < tripRouteCoordinates.size()-1) {
//                url += ";";
//            }
//        }
//        url += "?overview=false";
//        try {
//            String resp = restTemplate.getForObject(url, String.class);
//            JsonParser parser = new JsonParser();
//            JsonObject jsonObject = parser.parse(resp).getAsJsonObject();
//
//            // returns distance in meters
//            // divide by 1609.344 to get miles
//            double totalDist = jsonObject.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsDouble() / 1609.344;
//            tripDistance.setTripDistanceTotal(totalDist);
//
//            // get distance between last destination and the trip origin
//            JsonArray legs = jsonObject.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("legs").getAsJsonArray();
//            double distOut = totalDist - legs.get(legs.size()-1).getAsJsonObject().get("distance").getAsDouble() / 1609.344;
//            tripDistance.setTripDistanceOut(distOut);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return tripDistance;
//    }

    // Uses SAGE to geocode addresses
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