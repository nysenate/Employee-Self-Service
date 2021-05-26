package gov.nysenate.ess.travel.request.attachment;

import java.util.Objects;

public class Attachment {

    // A unique filename that is used to persist this file to disk.
    private final String filename;
    // The original filename from the user.
    private final String originalName;
    private final String contentType;

    public Attachment(String filename, String originalName, String contentType) {
        this.filename = filename;
        this.originalName = originalName;
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
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
                "filename='" + filename + '\'' +
                ", originalName='" + originalName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(filename, that.filename) &&
                Objects.equals(originalName, that.originalName) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, originalName, contentType);
    }
}
