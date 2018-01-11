package gov.nysenate.ess.travel.maps;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapsService implements MapService {

    @Value("${google.maps.api.key}") private String apiKey;

    /**
     * Gets the distance for a single leg of the trip.
     * @param leg A {@link Leg} of the trip.
     * @return The driving distance in meters.
     * @throws InterruptedException
     * @throws ApiException
     * @throws IOException
     */
    @Override
    public long getLegDistance(Leg leg) throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        String[] origins = new String[] {leg.getFrom().toString()};
        String[] destinations = new String[] {leg.getTo().toString()};
        DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations)
                .mode(TravelMode.DRIVING)
                .departureTime(DateTime.now())
                .trafficModel(TrafficModel.OPTIMISTIC)
                .units(Unit.IMPERIAL)
                .await();
        return request.rows[0].elements[0].distance.inMeters;
    }
}