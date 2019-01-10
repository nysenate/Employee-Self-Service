package gov.nysenate.ess.travel.application;

import java.util.UUID;

/**
 * This dao saves and queries saved (in progress) travel applications.
 */
public interface UncompletedTravelApplicationDao {

    void saveUncompletedApplication(TravelApplication app);

    TravelApplication selectUncompletedApplication(int travelerId);

    TravelApplication selectUncompletedApplication(UUID id);

    boolean hasUncompletedApplication(int travelerId);

    void deleteUncompletedApplication(UUID id);
}
