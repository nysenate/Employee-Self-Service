package gov.nysenate.ess.travel.application.destination;

import java.util.UUID;

public interface DestinationDao {

    void insertDestinations(UUID versionId, Destinations destinations);

    Destinations getDestinations(UUID versionId);
}
