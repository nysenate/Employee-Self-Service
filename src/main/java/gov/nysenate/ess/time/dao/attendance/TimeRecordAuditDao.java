package gov.nysenate.ess.time.dao.attendance;

import java.math.BigInteger;

public interface TimeRecordAuditDao {
    /**
     * Generates an audit record for a time record
     * This saves a snapshot of the current record data in the time record audit table
     * @param timeRecordId BigInteger - time record id
     */
    void auditTimeRecord(BigInteger timeRecordId);
}
