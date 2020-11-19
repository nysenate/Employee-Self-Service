package gov.nysenate.ess.travel.application;

import java.util.Objects;

public class TravelApplicationStatus {

    protected ApplicationStatus status;
    // A note about this status. Currently used to explain disapproval reasons.
    protected String note;

    public TravelApplicationStatus() {
        status = ApplicationStatus.PENDING;
    }

    public TravelApplicationStatus(ApplicationStatus status, String note) {
        this.status = status;
        this.note = note == null ? "" : note;
    }

    public TravelApplicationStatus(String status, String note) {
        this(ApplicationStatus.valueOf(status), note);
    }

    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }

    public boolean isApproved() {
        return status == ApplicationStatus.APPROVED;
    }

    public boolean isDisapproved() {
        return status == ApplicationStatus.DISAPPROVED;
    }

    public ApplicationStatus status() {
        return status;
    }

    public String note() {
        return note;
    }

    protected void approve() {
        status = ApplicationStatus.APPROVED;
    }

    protected void disapprove(String note) {
        status = ApplicationStatus.DISAPPROVED;
        this.note = note;
    }

    protected enum ApplicationStatus {
        PENDING,
        APPROVED,
        DISAPPROVED
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
