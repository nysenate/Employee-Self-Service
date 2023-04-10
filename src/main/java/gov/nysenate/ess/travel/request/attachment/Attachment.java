package gov.nysenate.ess.travel.request.attachment;

import java.util.Objects;
import java.util.UUID;

public class Attachment {

    private final UUID attachmentId;
    // The original filename from the user.
    private final String originalName;
    private final String contentType;

    public Attachment(UUID id, String originalName, String contentType) {
        this.attachmentId = id;
        this.originalName = originalName;
        this.contentType = contentType;
    }

    public UUID getAttachmentId() {
        return attachmentId;
    }

    public String getFilename() {
        return attachmentId.toString();
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "attachmentId=" + attachmentId +
                ", originalName='" + originalName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(attachmentId, that.attachmentId)
                && Objects.equals(originalName, that.originalName)
                && Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachmentId, originalName, contentType);
    }
}
