package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.travel.request.app.PurposeOfTravel;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;

import java.time.LocalDateTime;

/**
 * Temporarily holds data from the Application table while other
 * data is being queried to fully construct the Application.
 */
class TravelAppRepositoryView {

    public int appId;
    public int travelerEmpId;
    public int travelerDeptHeadEmpId;
    public int createdByEmpId;
    public TravelApplicationStatus status;
    public PurposeOfTravel pot;
    public LocalDateTime submittedDateTime;
    public LocalDateTime modifiedDateTime;
    public int modifiedByEmpId;

    public TravelAppRepositoryView() {
    }
}
