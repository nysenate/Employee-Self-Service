package gov.nysenate.ess.core.client.view.policy;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDateTime;

public class AcknowledgementView implements ViewObject {

    private Integer empId;
    private Integer policyId;
    private LocalDateTime timestamp;

    public AcknowledgementView() {}

    public AcknowledgementView(Integer empId, Integer policyId, LocalDateTime timestamp) {
        this.empId = empId;
        this.policyId = policyId;
        this.timestamp = timestamp;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getViewType() {
        return "acknowledgement";
    }
}
