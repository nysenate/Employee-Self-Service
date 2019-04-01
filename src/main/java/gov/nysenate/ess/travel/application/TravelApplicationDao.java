package gov.nysenate.ess.travel.application;

import java.util.List;

public interface TravelApplicationDao {

    void insertTravelApplication(TravelApplication app);

    void deleteTravelApplication(int appId);

    TravelApplication selectTravelApplication(int appId);

    List<TravelApplication> selectTravelApplications(int travelerId);
}
