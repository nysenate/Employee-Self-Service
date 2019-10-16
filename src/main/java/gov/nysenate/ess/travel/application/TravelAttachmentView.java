package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class TravelAttachmentView implements ViewObject {

    String id;
    String originalName;
    String contentType;

    public TravelAttachmentView() {
    }

    public TravelAttachmentView(TravelAttachment attachment) {
        this.id = attachment.getId();
        this.originalName = attachment.getOriginalName();
        this.contentType = attachment.getContentType();
    }

    public TravelAttachment toTravelAttachment() {
        return new TravelAttachment(id, originalName, contentType);
    }

    public String getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String getViewType() {
        return "travel-attachment";
    }
}
