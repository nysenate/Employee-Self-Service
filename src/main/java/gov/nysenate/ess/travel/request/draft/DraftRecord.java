package gov.nysenate.ess.travel.request.draft;

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
}
