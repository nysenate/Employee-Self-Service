package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests {@link TimeRecordErrorCode} class
 */
public class TimeRecordErrorCodeTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordErrorCodeTest.class);

    /**
     * Tests to make sure that the integer codes for each error code are unique
     * @see TimeRecordErrorCode#getCode()
     */
    @Test
    public void uniqueCodeTest() {
        Set<Integer> usedCodes = new HashSet<>();
        for (TimeRecordErrorCode code : TimeRecordErrorCode.values()) {
            assertFalse("Time record error code is unique: " + code + " with code " + code.getCode(),
                    usedCodes.contains(code.getCode()));
            usedCodes.add(code.getCode());
        }
    }

}
