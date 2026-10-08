package gov.nysenate.ess.travel.request.app;

public class TravelResubmissionConflictException extends RuntimeException {
    public TravelResubmissionConflictException(int appId) {
        super("Application " + appId + " is no longer available for resubmission.");
    }
}
