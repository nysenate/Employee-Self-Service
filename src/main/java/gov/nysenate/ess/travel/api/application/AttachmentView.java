package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.attachment.Attachment;

import java.util.UUID;

public class AttachmentView implements ViewObject {

    String filename;
    String originalName;
    String contentType;

    public AttachmentView() {
    }

    public AttachmentView(Attachment attachment) {
        this.filename = attachment.getFilename();
        this.originalName = attachment.getOriginalName();
        this.contentType = attachment.getContentType();
    }

    public Attachment toAttachment() {
        return new Attachment(UUID.fromString(filename), originalName, contentType);
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
    public String getViewType() {
        return "attachment";
    }
}
