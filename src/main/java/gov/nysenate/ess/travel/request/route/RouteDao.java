package gov.nysenate.ess.travel.request.route;

public interface RouteDao {

    void saveRoute(Route route, int appId);

    Route selectRoute(int appId);
}
