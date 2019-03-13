package gov.nysenate.ess.travel.application;

/**
 * This dao saves and queries saved (in progress) travel applications.
 */
public interface UncompletedTravelApplicationDao {

    void saveUncompletedApplication(TravelApplication app);

    TravelApplication selectUncompletedApplication(int travelerId);

    boolean hasUncompletedApplication(int travelerId);

    void deleteUncompletedApplication(int travelerId);
}
