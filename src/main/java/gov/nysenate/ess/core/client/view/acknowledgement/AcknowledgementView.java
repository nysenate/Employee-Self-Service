package gov.nysenate.ess.core.client.view.acknowledgement;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class AcknowledgementView implements ViewObject {

    private Integer empId;
    private Integer ackDocId;
    private LocalDateTime timestamp;

    protected AcknowledgementView() {}

    public AcknowledgementView(Acknowledgement ack) {
        this.empId = ack.getEmpId();
        this.ackDocId = ack.getAckDocId();
        this.timestamp = ack.getTimestamp();
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

    @Override
    @XmlElement
    public String getViewType() {
        return "acknowledgement";
    }
}
