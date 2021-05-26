package gov.nysenate.ess.travel.request.route;

public interface RouteDao {

    void saveRoute(Route route, int amendmentId);

    Route selectRoute(int amendmentId);
}
