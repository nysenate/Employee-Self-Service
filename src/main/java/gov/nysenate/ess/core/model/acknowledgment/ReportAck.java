package gov.nysenate.ess.core.model.acknowledgment;

import java.time.LocalDateTime;

public class ReportAck {

    private Integer empId;
    private Integer ackDocId;
    private LocalDateTime ackTimestamp;
    private String ackDocTitle;
    private LocalDateTime ackDocEffectiveTime;

    public ReportAck() {}

    public ReportAck(int empId, Integer ackDocId, LocalDateTime ackTimestamp, String ackDocTitle, LocalDateTime ackDocEffectiveTime) {
        this.empId = empId;
        this.ackDocId = ackDocId;
        this.ackTimestamp = ackTimestamp;
        this.ackDocTitle = ackDocTitle;
        this.ackDocEffectiveTime = ackDocEffectiveTime;
    }

    public String toString() {
        return this.ackDocTitle + " active in " + ackDocEffectiveTime.getYear() + " was acknowledged on "
                + ackTimestamp;
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

    public String getAckDocTitle() {
        return ackDocTitle;
    }

    public void setAckDocTitle(String ackDocTitle) {
        this.ackDocTitle = ackDocTitle;
    }

    public LocalDateTime getAckTimestamp() {
        return ackTimestamp;
    }

    public void setAckTimestamp(LocalDateTime ackTimestamp) {
        this.ackTimestamp = ackTimestamp;
    }

    public LocalDateTime getAckDocEffectiveTime() {
        return ackDocEffectiveTime;
    }

    public void setAckDocEffectiveTime(LocalDateTime ackDocEffectiveTime) {
        this.ackDocEffectiveTime = ackDocEffectiveTime;
    }
}
