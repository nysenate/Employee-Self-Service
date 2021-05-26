package gov.nysenate.ess.travel.request.app;

import java.util.Objects;

public class TravelApplicationStatus {

    protected ApprovalStatus status;
    // A note about this status. Currently used to explain disapproval reasons.
    protected String note;

    public TravelApplicationStatus() {
        status = ApprovalStatus.PENDING;
    }

    public TravelApplicationStatus(ApprovalStatus status, String note) {
        this.status = status;
        this.note = note == null ? "" : note;
    }

    public TravelApplicationStatus(String status, String note) {
        this(ApprovalStatus.valueOf(status), note);
    }

    public boolean isPending() {
        return status == ApprovalStatus.PENDING;
    }

    public boolean isApproved() {
        return status == ApprovalStatus.APPROVED;
    }

    public boolean isDisapproved() {
        return status == ApprovalStatus.DISAPPROVED;
    }

    public ApprovalStatus status() {
        return status;
    }

    public String note() {
        return note;
    }

    protected void approve() {
        status = ApprovalStatus.APPROVED;
    }

    protected void disapprove(String note) {
        status = ApprovalStatus.DISAPPROVED;
        this.note = note;
    }

    @Override
    public String toString() {
        return "TravelApplicationStatus{" +
                "status=" + status +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelApplicationStatus that = (TravelApplicationStatus) o;
        return status == that.status &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, note);
    }
}
