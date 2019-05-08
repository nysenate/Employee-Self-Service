package gov.nysenate.ess.core.client.view.pec.acknowledgment;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskIdView;
import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class AckDocView implements PersonnelTaskView {

    private String title;
    private String path;
    private boolean active;
    private int id;
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

    @Override
    @XmlElement
    public PersonnelTaskIdView getTaskId() {
        return new PersonnelTaskIdView(PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT, id);
    }

    @Override
    @XmlElement
    public String getTitle() {
        return title;
    }

    @XmlElement
    public String getPath() {
        return path;
    }

    @XmlElement
    public boolean getActive() {
        return active;
    }

    @XmlElement
    public int getId() {
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
