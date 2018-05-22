package gov.nysenate.ess.time.dao.attendance;

import java.math.BigInteger;

/**
 * Exception thrown when an attempt is made at an illegal time record modification in the dao
 */
public class IllegalRecordModificationEx extends RuntimeException {

    private BigInteger recordId;

    public IllegalRecordModificationEx(BigInteger recordId, String reason) {
        super("Cannot modify time record " + recordId + ": " + reason);
        this.recordId = recordId;
    }

    public BigInteger getRecordId() {
        return recordId;
    }
}
