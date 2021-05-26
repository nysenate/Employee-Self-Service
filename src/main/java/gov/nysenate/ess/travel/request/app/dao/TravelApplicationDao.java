package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.travel.request.app.TravelApplication;

import java.util.List;

public interface TravelApplicationDao {

    void saveTravelApplication(TravelApplication app);

    TravelApplication selectTravelApplication(int appId);

    List<TravelApplication> selectTravelApplications(int travelerId);
}
