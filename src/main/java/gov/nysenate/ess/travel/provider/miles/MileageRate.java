package gov.nysenate.ess.travel.provider.miles;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MileageRate(LocalDate startDate, LocalDate endDate, BigDecimal rate) {
    public MileageRate(LocalDate startDate, LocalDate endDate, String rate) {
        this(startDate, endDate, new BigDecimal(rate));
    }
}
