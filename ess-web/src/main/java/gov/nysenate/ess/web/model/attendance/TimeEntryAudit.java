package gov.nysenate.ess.web.model.attendance;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by riken on 4/8/14.
 */
public class TimeEntryAudit {

    protected Timestamp auditDate;
    protected String auditName;
    protected BigDecimal auditId;
    protected TimeEntry timeEntry;

    public Timestamp getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Timestamp auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }

    public BigDecimal getAuditId() {
        return auditId;
    }

    public void setAuditId(BigDecimal auditId) {
        this.auditId = auditId;
    }

    public TimeEntry getTimeEntry() {
        return timeEntry;
    }

    public void setTimeEntry(TimeEntry timeEntry) {
        this.timeEntry = timeEntry;
    }
}
