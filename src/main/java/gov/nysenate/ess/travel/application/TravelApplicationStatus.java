package gov.nysenate.ess.travel.application;

import java.time.LocalDateTime;
import java.util.Objects;

public class TravelApplicationStatus {

    protected int statusId;
    protected ApplicationStatus status;
    protected LocalDateTime dateTime;
    // A note about this status. Currently used to explain disapproval reasons.
    protected String note;

    public TravelApplicationStatus() {
        status = ApplicationStatus.PENDING;
        dateTime = LocalDateTime.now();
    }

    public TravelApplicationStatus(int statusId, ApplicationStatus status, LocalDateTime dateTime, String note) {
        this.statusId = statusId;
        this.status = status;
        this.dateTime = dateTime;
        this.note = note;
    }

    public TravelApplicationStatus(int statusId, String status, LocalDateTime dateTime, String note) {
        this(statusId, ApplicationStatus.valueOf(status), dateTime, note);
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

    public String note() {
        return note;
    }

    public LocalDateTime dateTime() {
        return dateTime;
    }

    protected void approve() {
        status = ApplicationStatus.APPROVED;
        dateTime = LocalDateTime.now();
    }

    protected void disapprove(String note) {
        status = ApplicationStatus.DISAPPROVED;
        dateTime = LocalDateTime.now();
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
                "statusId=" + statusId +
                ", status=" + status +
                ", dateTime=" + dateTime +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelApplicationStatus that = (TravelApplicationStatus) o;
        return statusId == that.statusId &&
                status == that.status &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusId, status, dateTime, note);
    }
}
