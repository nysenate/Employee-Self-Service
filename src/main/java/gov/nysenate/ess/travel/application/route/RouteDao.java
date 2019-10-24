package gov.nysenate.ess.travel.application.route;

public interface RouteDao {

    void saveRoute(Route route, int amendmentId);

    Route selectRoute(int appVersionId);
}
