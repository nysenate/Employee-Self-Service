package gov.nysenate.ess.supply.util.date;

import gov.nysenate.ess.supply.util.date.DateTimeFactory;

import java.time.LocalDateTime;

/**
 * Dummy DateTimeFactory implementation used for testing.
 */
public class DummyDateTime implements DateTimeFactory {

    public LocalDateTime dateTime;

    @Override
    public LocalDateTime now() {
        return dateTime;
    }
}
