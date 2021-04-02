package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.utils.UnitUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapsService implements MapService {

    private String apiKey;
    private final GeoApiContext context;

    @Autowired
    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey) {
        this.apiKey = apiKey;
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    /**
     * Calculates the driving distance in miles from one address to another.
     * @param from The starting address.
     * @param to The ending address.
     */
    @Override
    public double drivingDistance(GoogleAddress from, GoogleAddress to) throws InterruptedException, ApiException, IOException {
        String[] origins = new String[] {getGoolgeAddressParam(from)};
        String[] destinations = new String[] {getGoolgeAddressParam(to)};
        DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations)
                .mode(TravelMode.DRIVING)
                .departureTime(java.time.Instant.ofEpochMilli(DateTime.now().toInstant().getMillis()))
                .trafficModel(TrafficModel.OPTIMISTIC)
                .units(Unit.IMPERIAL)
                .await();
        long meters = 0;
        if (request.rows[0].elements[0].distance != null) {
            meters = request.rows[0].elements[0].distance.inMeters;
        }
        return UnitUtils.metersToMiles(meters).doubleValue();
    }

    /**
     * Get the address param to be passed into google distance matrix.
     *
     * Use the place_id if it exists, otherwise use the address string.
     * @param address
     * @return
     */
    private String getGoolgeAddressParam(GoogleAddress address) {
        if (address.getPlaceId().isEmpty()) {
            return address.toString();
        }
        else {
            return "place_id:" + address.getPlaceId();
        }
    }
}
