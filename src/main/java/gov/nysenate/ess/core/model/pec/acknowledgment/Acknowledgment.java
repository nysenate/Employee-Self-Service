package gov.nysenate.ess.core.model.pec.acknowledgment;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

import java.time.LocalDateTime;

/**
 * @deprecated
 * @see PersonnelTaskAssignment a generic model that covers acknowledgments.
 */
@Deprecated
public class Acknowledgment {

    private Integer empId;
    private Integer ackDocId;
    private LocalDateTime timestamp;
    private boolean personnelAcked;

    public Acknowledgment() {}

    //Creating an Ack this way, means personnel did not override
    public Acknowledgment(Integer empId, Integer ackDocId, LocalDateTime timestamp) {
        this.empId = empId;
        this.ackDocId = ackDocId;
        this.timestamp = timestamp;
        this.personnelAcked = false;
    }

    //Creating an Ack this way, means that personnel overide could be true
    public Acknowledgment(Integer empId, Integer ackDocId, LocalDateTime timestamp, boolean personnelAcked) {
        this.empId = empId;
        this.ackDocId = ackDocId;
        this.timestamp = timestamp;
        this.personnelAcked = personnelAcked;
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

    public boolean isPersonnelAcked() {
        return personnelAcked;
    }

    public void setPersonnelAcked(boolean personnelAcked) {
        this.personnelAcked = personnelAcked;
    }
}