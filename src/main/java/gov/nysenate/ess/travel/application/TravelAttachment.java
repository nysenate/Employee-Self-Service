package gov.nysenate.ess.travel.application;

import java.util.Objects;

public class TravelAttachment {

    // The unique filename of this attachment.
    private final String id;
    private final String originalName;
    private final String contentType;

    public TravelAttachment(String id, String originalName, String contentType) {
        this.id = id;
        this.originalName = originalName;
        this.contentType = contentType;
    }

    protected String getId() {
        return id;
    }

    protected String getOriginalName() {
        return originalName;
    }

    protected String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "TravelAttachment{" +
                "id='" + id + '\'' +
                ", originalName='" + originalName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelAttachment that = (TravelAttachment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(originalName, that.originalName) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalName, contentType);
    }
}
