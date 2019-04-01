package gov.nysenate.ess.travel.application.route;

public interface RouteDao {

    void saveRoute(Route route, int appVersionId, int previousAppVersionId);

    Route selectRoute(int appVersionId);
}
