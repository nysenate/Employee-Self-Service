package gov.nysenate.ess.travel.maps;

import com.google.maps.*;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MapsService {

    @Value("${google.maps.apiKey}") private String apiKey;

    /**
     *
     * @param origin Address where trip originates
     * @param destination Address where trip terminates
     * @return Distance between the origin and destination in miles
     */
    private double getDistance(String origin, String destination) {
        GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
        String[] origins = new String[] {origin};
        String[] destinations = new String[] {destination};
        double totalDist = 0;
        try {
            DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations).units(Unit.IMPERIAL).await();
            DistanceMatrixRow[] rows = request.rows;
            for (DistanceMatrixRow d : rows) {
                for (DistanceMatrixElement el : d.elements) {
                    totalDist += Double.parseDouble(el.distance.humanReadable.replaceAll("[^\\d.]", ""));
                }
            }
            return totalDist;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Assume that the traveler visits all destinations in order and then returns to the origin
     * @param origin Address where trip originates
     * @param destinations List of addresses, in order, of stops along the trip
     * @return Total distance traveled over the trip in miles
     */
    public double getTripDistance(String origin, String[] destinations) {
        double totalDist = 0;
        String from = origin;
        String to = "";
        for (String dest : destinations) {
            to = dest;
            totalDist += getDistance(from, to);
            from = to;
        }
        //add the return from last destination to the origin
        totalDist += getDistance(destinations[destinations.length-1], origin);
        return totalDist;
    }
}
