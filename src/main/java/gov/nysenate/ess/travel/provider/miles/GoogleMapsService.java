package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.UnitUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapsService implements MapService {

    @Value("${google.maps.api.key}") private String apiKey;

    /**
     * Calculates the driving distance in miles from one address to another.
     * @param from The starting address.
     * @param to The ending address.
     */
    @Override
    public double drivingDistance(Address from, Address to) throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        String[] origins = new String[] {from.toString()};
        String[] destinations = new String[] {to.toString()};
        DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations)
                .mode(TravelMode.DRIVING)
                .departureTime(DateTime.now())
                .trafficModel(TrafficModel.OPTIMISTIC)
                .units(Unit.IMPERIAL)
                .await();
        long meters = 0;
        if (request.rows[0].elements[0].distance != null) {
            meters = request.rows[0].elements[0].distance.inMeters;
        }
        return UnitUtils.metersToMiles(meters).doubleValue();
    }
}