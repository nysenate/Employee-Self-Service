package gov.nysenate.ess.core.model.acknowledgement;

import java.time.LocalDateTime;

public class Acknowledgement {

    private Integer empId;
    private Integer ackDocId;
    private LocalDateTime timestamp;

    public Acknowledgement() {}

    public Acknowledgement(Integer empId, Integer ackDocId, LocalDateTime timestamp) {
        this.empId = empId;
        this.ackDocId = ackDocId;
        this.timestamp = timestamp;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public Integer getAckDocId() {
        return ackDocId;
    }

    public void setAckDocId(Integer ackDocId) {
        this.ackDocId = ackDocId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}