package gov.nysenate.ess.travel.request.app;

import java.util.Objects;

public class TravelApplicationStatus {

    protected AppStatus status;
    // A note about this status. Currently, used to explain disapproval reasons.
    protected String note;

    public TravelApplicationStatus(AppStatus status) {
        this(status, "");
    }

    public TravelApplicationStatus(AppStatus status, String note) {
        this.status = status;
        this.note = note == null ? "" : note;
    }

    public TravelApplicationStatus(String status, String note) {
        this(AppStatus.valueOf(status), note);
    }

    public boolean isPending() {
        return status == AppStatus.DEPARTMENT_HEAD || status == AppStatus.TRAVEL_UNIT;
    }

    public boolean isApproved() {
        return status == AppStatus.APPROVED;
    }

    public boolean isDisapproved() {
        return status == AppStatus.DISAPPROVED;
    }

    public boolean isNotApplicable() {
        return status == AppStatus.NOT_APPLICABLE;
    }

    public boolean isDraft() {
        return status == AppStatus.DRAFT;
    }

    public boolean isCanceled() {
        return status == AppStatus.CANCELED;
    }

    public AppStatus status() {
        return status;
    }

    public String note() {
        return note;
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
