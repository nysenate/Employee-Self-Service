package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.application.AmendmentView;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;

import java.time.LocalDateTime;

/**
 * Represents a single row in the travel.draft table
 */
public class DraftRecord {

    protected int id;
    protected int userEmpId;
    protected int travelerEmpId;
    protected String travelAppJson;
    protected LocalDateTime updatedDateTime;

    public DraftRecord() {
    }

    public DraftRecord(Draft draft) {
        this.id = draft.getId();
        this.userEmpId = draft.getUserEmpId();
        this.travelerEmpId = draft.getTraveler().getEmployeeId();
        this.travelAppJson = OutputUtils.toJson(new TravelApplicationView(draft.getTravelApplication()));
        this.updatedDateTime = draft.getUpdatedDateTime();
    }
}
