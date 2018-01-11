package gov.nysenate.ess.core.model.policy;

import java.time.LocalDateTime;

public class Acknowledgement {

    private Integer empId;
    private Integer policyId;
    private LocalDateTime timestamp;

    public Acknowledgement() {}

    public Acknowledgement(Integer empId, Integer policyId,LocalDateTime timestamp) {
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
}