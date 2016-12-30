package gov.nysenate.ess.core.client.response.error;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.Set;

@Category(UnitTest.class)
public class ErrorCodeTest {

    @Test
    public void errorCodeUniqueTest() {
        Set<Integer> usedCodes = new HashSet<>();
        for (ErrorCode code : ErrorCode.values()) {
            int errorCode = code.getCode();
            Assert.assertFalse("Error code in use: " + errorCode, usedCodes.contains(errorCode));
            usedCodes.add(errorCode);
        }
    }
}
