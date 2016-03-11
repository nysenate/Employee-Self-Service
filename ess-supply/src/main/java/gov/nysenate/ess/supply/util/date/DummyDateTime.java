package gov.nysenate.ess.supply.util.date;

import java.time.LocalDateTime;

/**
 * Dummy DateTimeFactory implementation used for testing.
 */
public class DummyDateTime implements DateTimeFactory {

    private LocalDateTime dateTime;

    @Override
    public LocalDateTime now() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
