package gov.nysenate.ess.core.client.view.policy;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.policy.Acknowledgement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class AcknowledgementView implements ViewObject {

    private Integer empId;
    private Integer policyId;
    private LocalDateTime timestamp;

    protected AcknowledgementView() {}

    public AcknowledgementView(Acknowledgement ack) {
        this.empId = ack.getEmpId();
        this.policyId = ack.getPolicyId();
        this.timestamp = ack.getTimestamp();
    }

    @XmlElement
    public Integer getEmpId() {
        return empId;
    }

    @XmlElement
    public Integer getPolicyId() {
        return policyId;
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
