package gov.nysenate.ess.travel.maps;

import com.google.maps.*;
import com.google.maps.model.*;
import gov.nysenate.ess.travel.application.dao.IrsRateDao;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class MapsService {

    @Value("${google.maps.apiKey}") private String apiKey;

    public boolean validateAddress(String address) {

        return false;
    }

    /**
     *
     * @param origin Address where trip originates
     * @param destination Address where trip terminates
     * @return Distance between the origin and destination in miles
     */
    private double getDistance(String origin, String destination) {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        String[] origins = new String[] {origin};
        String[] destinations = new String[] {destination};
        double totalDist = 0;
        try {
            DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations)
                    .departureTime(DateTime.now())
                    .trafficModel(TrafficModel.OPTIMISTIC)
                    .units(Unit.IMPERIAL)
                    .await();
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
    public  double getTripDistance(String origin, String[] destinations) {
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

    /**
     * Does not include the return trip.  Useful for determining how many miles
     * the traveler went in one direction.
     * @param origin
     * @param destinations
     * @return
     */
    public double getDistanceOut(String origin, String[] destinations) {
        double totalDist = 0;
        String from = origin;
        String to = "";
        for (String dest : destinations) {
            to = dest;
            totalDist += getDistance(from, to);
            from = to;
        }
        //add the return from last destination to the origin
        return totalDist;
    }

    /**
     * In theory, I can query the Google Maps API to get a list of String directions
     * i.e, "Turn left onto Loudon Road" or "Merge onto I-90 West"
     * but these won't tell me what exit a driver is getting on.
     * I could potentially geolocate which exit they're getting on based on their
     * lng/lat at the time and the lng/lat of exits.
     * Then I'd have to emulate a web interaction with the NYS DOT toll calculator
     * and input the correct exits, submit the form, and retrieve the toll amount.
     *
     * This is also assuming that the driver takes the path I expect them to.
     * They may take a different path, thus making this inaccurate.
     * @param origin
     * @param destination
     */
    public void directions(String origin, String destination) {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        DirectionsResult result = null;
        try {
            result = DirectionsApi.getDirections(context, origin, destination)
                    .departureTime(DateTime.now())
                    .trafficModel(TrafficModel.OPTIMISTIC)
                    .await();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (DirectionsRoute d : result.routes) {
            System.out.println(d.legs[0].startAddress);
        }
    }
}
