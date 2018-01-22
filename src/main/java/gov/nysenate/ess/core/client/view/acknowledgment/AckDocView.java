package gov.nysenate.ess.core.client.view.acknowledgment;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.acknowledgment.AckDoc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class AckDocView implements ViewObject {

    private String title;
    private String path;
    private Boolean active;
    private Integer id;
    private LocalDateTime effectiveDateTime;

    protected AckDocView() {
    }

    public AckDocView(AckDoc ackDoc, String ackDocResPath) {
        this.title = ackDoc.getTitle();
        this.path = ackDocResPath + ackDoc.getFilename();
        this.active = ackDoc.getActive();
        this.id = ackDoc.getId();
        this.effectiveDateTime = ackDoc.getEffectiveDateTime();
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    @XmlElement
    public String getPath() {
        return path;
    }

    @XmlElement
    public Boolean getActive() {
        return active;
    }

    @XmlElement
    public Integer getId() {
        return id;
    }

    @XmlElement
    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "ack_doc";
    }
}
