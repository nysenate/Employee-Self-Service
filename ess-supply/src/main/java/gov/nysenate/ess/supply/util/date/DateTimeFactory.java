package gov.nysenate.ess.supply.util.date;

import java.time.LocalDateTime;

/**
 * Wrapper around LocalDateTime.
 * Allows injection of date time instances to make testing easier.
 */
public interface DateTimeFactory {

    LocalDateTime now();

}
