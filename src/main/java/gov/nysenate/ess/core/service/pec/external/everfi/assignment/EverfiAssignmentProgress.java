package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EverfiAssignmentProgress {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DUE_ON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss.SSSz");

    @JsonProperty("id")
    private String uuid;
    private String name;
    @JsonProperty("due_on")
    private String dueOn;
    @JsonProperty("content_id")
    private String contentId;
    @JsonProperty("started_at")
    private String startedAt;
    @JsonProperty("completed_at")
    private String completedAt;
    @JsonProperty("content_status")
    private String contentStatus;
    @JsonProperty("last_progress_at")
    private String lastProgressAt;
    @JsonProperty("percent_completed")
    private String percentCompleted;

    public EverfiAssignmentProgress() {
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDueOn() {
        return LocalDate.parse(dueOn, DUE_ON_DATE_FORMATTER);
    }

    public String getContentId() {
        return contentId;
    }

    public ZonedDateTime getStartedAt() {
        return ZonedDateTime.parse(startedAt, DATE_TIME_FORMATTER);
    }

    public ZonedDateTime getCompletedAt() {
        try {
            return ZonedDateTime.parse(completedAt);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public ZonedDateTime getLastProgressAt() {
        return ZonedDateTime.parse(lastProgressAt, DATE_TIME_FORMATTER);
    }

    public String getPercentCompleted() {
        return percentCompleted;
    }

    @Override
    public String toString() {
        return "EverfiAssignmentProgress{" +
                "id='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", dueOn='" + dueOn + '\'' +
                ", contentId='" + contentId + '\'' +
                ", startedAt='" + startedAt + '\'' +
                ", completedAt='" + completedAt + '\'' +
                ", contentStatus='" + contentStatus + '\'' +
                ", lastProgressAt='" + lastProgressAt + '\'' +
                ", percentCompleted='" + percentCompleted + '\'' +
                '}';
    }
}
