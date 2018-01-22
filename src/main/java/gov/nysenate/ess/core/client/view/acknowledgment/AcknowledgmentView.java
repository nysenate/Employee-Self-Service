package gov.nysenate.ess.core.client.view.acknowledgment;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.acknowledgment.Acknowledgment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class AcknowledgmentView implements ViewObject {

    private Integer empId;
    private Integer ackDocId;
    private LocalDateTime timestamp;

    protected AcknowledgmentView() {}

    public AcknowledgmentView(Acknowledgment ack) {
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
        return "acknowledgment";
    }
}
