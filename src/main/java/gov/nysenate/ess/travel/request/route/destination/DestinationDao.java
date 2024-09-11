package gov.nysenate.ess.travel.request.route.destination;

import java.util.Collection;

public interface DestinationDao {

    void insertDestinations(Collection<Destination> destinations);

    void insertDestination(Destination destination);

    Destination selectDestination(int destinationId);
}
