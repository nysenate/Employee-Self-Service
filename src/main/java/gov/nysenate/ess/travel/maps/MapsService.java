package gov.nysenate.ess.travel.maps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.model.*;

public class MapsService {

    public static void main(String[] args) {
        getDistance();
    }

    public static void getDistance() {
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyD7k-ClexcbzxqMfLjLhYLc13qKL2vufiA");
        String[] origins = {"1600 Amphitheatre Parkway Mountain View, CA 94043"};
        String[] destinations = {"515 Loudon Road Loudonville, NY 12211"};
        //each request has an array of rows
        //each row has an array of elements
        //each element corresponds to an origin/destination pair and has Distance, Duration, and Fare
        try {
            DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations).units(Unit.IMPERIAL).await();
            DistanceMatrixRow[] rows = request.rows;
            for (DistanceMatrixRow d : rows) {
                for (DistanceMatrixElement el : d.elements) {
                    System.out.println(el.distance.humanReadable);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
