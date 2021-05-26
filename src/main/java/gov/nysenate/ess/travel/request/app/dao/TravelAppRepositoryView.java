package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporarily holds data from the Application table while other
 * data is being queried to fully construct the Application.
 */
class TravelAppRepositoryView {

    public int appId;
    public int travelerEmpId;
    public int travelerDepartmentId;
    public TravelApplicationStatus status;
    public List<AmendmentRepositoryView> amendmentViews;

    public TravelAppRepositoryView() {
        this.amendmentViews = new ArrayList<>();
    }

    public TravelAppRepositoryView(int appId,
                                   int travelerEmpId,
                                   int travelerDepartmentId,
                                   TravelApplicationStatus status,
                                   List<AmendmentRepositoryView> amendmentViews) {
        this.appId = appId;
        this.travelerEmpId = travelerEmpId;
        this.travelerDepartmentId = travelerDepartmentId;
        this.status = status;
        this.amendmentViews = amendmentViews;
    }
}
