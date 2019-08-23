package gov.nysenate.ess.travel.application;

import java.util.List;

public interface TravelApplicationDao {

    void saveTravelApplication(TravelApplication app);

    TravelApplication selectTravelApplication(int appId);

    List<TravelApplication> selectTravelApplications(int travelerId);
}
