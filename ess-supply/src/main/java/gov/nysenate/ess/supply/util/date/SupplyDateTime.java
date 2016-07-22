package gov.nysenate.ess.supply.util.date;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SupplyDateTime implements DateTimeFactory {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
