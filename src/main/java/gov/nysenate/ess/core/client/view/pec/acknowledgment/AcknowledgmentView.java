package gov.nysenate.ess.core.client.view.pec.acknowledgment;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.acknowledgment.Acknowledgment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
@Deprecated
public class AcknowledgmentView implements ViewObject {

    private Integer empId;
    private Integer ackDocId;
    private LocalDateTime timestamp;
    private boolean personnelAcked;

    protected AcknowledgmentView() {}

    public AcknowledgmentView(Acknowledgment ack) {
        this.empId = ack.getEmpId();
        this.ackDocId = ack.getAckDocId();
        this.timestamp = ack.getTimestamp();
        this.personnelAcked = ack.isPersonnelAcked();
    }

    @XmlElement
    public Integer getEmpId() {
        return empId;
    }

    @XmlElement
    public Integer getAckDocId() {
        return ackDocId;
    }

    @XmlElement
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @XmlElement
    public boolean isPersonnelAcked() {return personnelAcked;}

    @Override
    @XmlElement
    public String getViewType() {
        return "acknowledgment";
    }
}
