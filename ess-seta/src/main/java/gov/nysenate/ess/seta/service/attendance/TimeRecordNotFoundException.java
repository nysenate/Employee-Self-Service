package gov.nysenate.ess.seta.service.attendance;

import java.io.Serializable;
import java.math.BigInteger;

public class TimeRecordNotFoundException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -6647639428891147275L;

    private BigInteger timeRecordId;

    public TimeRecordNotFoundException(BigInteger timeRecordId) {
        super("No time record could be retrieved with a time record id of " + timeRecordId);
        this.timeRecordId = timeRecordId;
    }

    public BigInteger getTimeRecordId() {
        return timeRecordId;
    }
}
