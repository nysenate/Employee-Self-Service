package gov.nysenate.ess.travel.application;

import java.util.UUID;

public interface TravelApplicationDao {

    void insertTravelApplication(TravelApplication app);

    TravelApplication getTravelApplication(UUID id);
}
