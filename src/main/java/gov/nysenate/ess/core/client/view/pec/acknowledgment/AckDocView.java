package gov.nysenate.ess.core.client.view.pec.acknowledgment;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.file.Paths;

@XmlRootElement
public class AckDocView extends PersonnelTaskView {

    private String path;

    public AckDocView(AckDoc ackDoc, String ackDocResPath) {
        super(ackDoc);
        this.path = Paths.get(ackDocResPath, ackDoc.getFilename()).toString();
    }

    @XmlElement
    public String getPath() {
        return path;
    }
}
