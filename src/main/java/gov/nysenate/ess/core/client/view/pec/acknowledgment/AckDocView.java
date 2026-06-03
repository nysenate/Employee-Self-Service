package gov.nysenate.ess.core.client.view.pec.acknowledgment;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.PersonnelTask;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.nio.file.Paths;

@XmlRootElement
public class AckDocView extends PersonnelTaskView {

    private String path;

    public AckDocView(PersonnelTask task, String ackDocResPath) {
        super(task);
        this.path = Paths.get(ackDocResPath, task.getResource()).toString();
    }

    @XmlElement
    public String getPath() {
        return path;
    }
}
