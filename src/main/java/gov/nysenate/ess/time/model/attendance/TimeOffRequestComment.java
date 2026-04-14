package gov.nysenate.ess.time.model.attendance;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class TimeOffRequestComment implements Comparable<TimeOffRequestComment> {

    public static final Comparator<TimeOffRequestComment> defaultComparator =
            Comparator.comparing(TimeOffRequestComment::getTimestamp);

    protected int requestId;
    protected int authorId;
    protected LocalDateTime timestamp;
    protected String text;

    public TimeOffRequestComment() {}

    public TimeOffRequestComment(int requestId, int authorId, String text) {
        this.requestId = requestId;
        this.authorId = authorId;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    public TimeOffRequestComment(int requestId, int authorId, LocalDateTime timestamp, String text) {
        this.requestId = requestId;
        this.authorId = authorId;
        this.timestamp = timestamp;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeOffRequestComment that = (TimeOffRequestComment) o;
        return requestId == that.requestId &&
                authorId == that.authorId &&
                timestamp.equals(that.timestamp) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, authorId, timestamp, text);
    }

    /* Basic getters and setters */
    public int getRequestId() {
        return requestId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(TimeOffRequestComment o) {
        return defaultComparator.compare(this, o);
    }
}
