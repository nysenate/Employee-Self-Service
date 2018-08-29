package gov.nysenate.ess.travel.application.route;

import java.util.UUID;

public interface RouteDao {

    void insertRoute(UUID versionId, Route route);

    Route getRoute(UUID versionId);

    Leg getLeg(UUID legId);
}
