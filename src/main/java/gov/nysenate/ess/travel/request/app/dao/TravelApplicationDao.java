package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.app.TravelApplicationQuery;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;
import gov.nysenate.ess.core.util.PaginatedList;

import java.time.LocalDateTime;
import java.util.List;

public interface TravelApplicationDao {

    void saveTravelApplication(TravelApplication app);

    void updateTravelApplicationStatus(int appId, TravelApplicationStatus status);

    TravelApplication selectTravelApplication(int appId);

    PaginatedList<TravelApplication> selectTravelApplications(int userId, TravelApplicationQuery query);

    List<TravelApplication> selectAllApplications(LocalDateTime fromDate, LocalDateTime toDate);
}
