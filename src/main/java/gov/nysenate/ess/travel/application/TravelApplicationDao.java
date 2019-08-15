package gov.nysenate.ess.travel.application;

import java.util.List;

public interface TravelApplicationDao {

    void insertTravelApplication(TravelApplication app);

    TravelApplication selectTravelApplication(int appId);

    List<TravelApplication> selectTravelApplications(int travelerId);
}
