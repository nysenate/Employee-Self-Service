package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;

import java.util.List;

public interface TravelApplicationDao {

    void saveTravelApplication(TravelApplication app);

    void updateTravelApplicationStatus(int appId, TravelApplicationStatus status);

    TravelApplication selectTravelApplication(int appId);

    List<TravelApplication> selectTravelApplications(int travelerId);
}
