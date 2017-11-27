package gov.nysenate.ess.travel.maps;

import com.google.maps.*;
import com.google.maps.model.*;
import gov.nysenate.ess.core.model.unit.Address;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapsService implements MapInterface {

    @Value("${google.maps.apiKey}") private String apiKey;

    /**
     * Gets the total trip distance for a trip with only one API call
     */
    public TripDistance getTripDistance(List<Address> travelRoute) throws Exception{
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        TripDistance tripDistance = new TripDistance();
        String[] addresses = new String[travelRoute.size()-2];
        for (int i = 1; i < travelRoute.size()-1; i++) {
            addresses[i-1] = travelRoute.get(i).toString();
        }
        String origin = travelRoute.get(0).toString();
        double totalDist = 0;
        DirectionsResult result = null;
        try {
            result = DirectionsApi.getDirections(context, origin, origin)
                    .waypoints(addresses)
                    .departureTime(DateTime.now())
                    .trafficModel(TrafficModel.OPTIMISTIC)
                    .await();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        DirectionsRoute d = result.routes[0];
        for (int i = 0; i < d.legs.length; i++) {
            if (i == d.legs.length - 1) {
                tripDistance.setTripDistanceOut(totalDist);
            }
            totalDist += d.legs[i].distance.inMeters / 1609.344;
        }
        tripDistance.setTripDistanceTotal(totalDist);
        return tripDistance;
    }

    /**
     * Assume that the traveler visits all destinations in order and then returns to the origin
     * @param travelRoute List of addresses, in order, of stops along the trip from origin
     *                    to all destinations and back to origin
     * @return Total distance traveled over the trip in miles
     */
    @Deprecated
    public TripDistance oldGetTripDistance(List<Address> travelRoute) throws Exception{
        TripDistance tripDistance = new TripDistance();
        double totalDist = 0;
        Address from;
        Address to;
        for (int i = 0; i < travelRoute.size()-1; i++) {
            if (i == travelRoute.size()-2) {
                tripDistance.setTripDistanceOut(totalDist);
            }
            from = travelRoute.get(i);
            to = travelRoute.get(i+1);
            totalDist += getDistance(from, to);
        }
        tripDistance.setTripDistanceTotal(totalDist);
        return tripDistance;
    }

    /**
     *
     * @param origin Address where trip originates
     * @param destination Address where trip terminates
     * @return Distance between the origin and destination in miles
     */
    @Deprecated
    private double getDistance(Address origin, Address destination) throws Exception{
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        String[] origins = new String[] {origin.toString()};
        String[] destinations = new String[] {destination.toString()};
        double totalDist = 0;
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
}