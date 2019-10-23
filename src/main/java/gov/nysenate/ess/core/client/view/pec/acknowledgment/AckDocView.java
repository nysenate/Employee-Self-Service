package gov.nysenate.ess.core.client.view.pec.acknowledgment;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AckDocView extends PersonnelTaskView {

    private String path;

    public AckDocView(AckDoc ackDoc, String ackDocResPath) {
        super(ackDoc);
        this.path = ackDocResPath + ackDoc.getFilename();
    }

    @XmlElement
    public String getPath() {
        return path;
    }
}
