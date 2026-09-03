package gov.nysenate.ess.travel.request.allowances.meal;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Thrown when no Senate meal rate is on file for a travel date.
 * Rates are loaded per federal fiscal year, so this is expected for dates in a fiscal year whose
 * rates have not been published and loaded yet.
 */
@Getter
public class MealRatesUnavailableException extends RuntimeException {
    private final LocalDate date;

    public MealRatesUnavailableException(LocalDate date) {
        super("No Senate meal rates are available for " + date);
        this.date = date;
    }
}
