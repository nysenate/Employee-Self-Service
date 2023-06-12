package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.util.OutputUtils;

import java.time.LocalDateTime;

/**
 * Represents a single row in the travel.draft table
 */
public class DraftRecord {

    protected int id;
    protected int userEmpId;
    protected int travelerEmpId;
    protected String amendmentJson;
    protected LocalDateTime updatedDateTime;

    public DraftRecord() {
    }

    public DraftRecord(Draft draft) {
        this.id = draft.getId();
        this.userEmpId = draft.getUserEmpId();
        this.travelerEmpId = draft.getTraveler().getEmployeeId();
        this.amendmentJson = OutputUtils.toJson(draft.getAmendment());
        this.updatedDateTime = draft.getUpdatedDateTime();
    }
}
