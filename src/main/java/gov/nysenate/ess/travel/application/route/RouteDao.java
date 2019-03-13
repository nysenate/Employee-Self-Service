package gov.nysenate.ess.travel.application.route;

public interface RouteDao {

    void insertRoute(int appVersionId, Route route);

    Route selectRoute(int appVersionId);
}
