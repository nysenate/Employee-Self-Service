package gov.nysenate.ess.travel.request.route;

import gov.nysenate.ess.core.util.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteViewValidator {

    /**
     * Validates a new application route's travel dates.
     *
     * Dates must be valid and in chronological order.
     *
     * @throws InvalidTravelDatesException if there are any issues with the dates.
     */
    public void validateTravelDates(RouteView routeView) {
        List<LocalDate> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Add all dates to a list in order.
        for (LegView leg : routeView.getOutboundLegs()) {
            dates.add(parseDate(formatter, leg));
        }
        for (LegView leg : routeView.getReturnLegs()) {
            dates.add(parseDate(formatter, leg));
        }

        // Validate chronological order of dates.
        LocalDate prev = DateUtils.LONG_AGO;
        for (LocalDate date : dates) {
            if (date.isBefore(prev)) {
                throw new InvalidTravelDatesException();
            }
            prev = date;
        }
    }

    private LocalDate parseDate(DateTimeFormatter formatter, LegView leg) {
        try {
            return LocalDate.parse(leg.getTravelDate(), formatter);
        } catch (DateTimeParseException ex) {
            throw new InvalidTravelDatesException();
        }
    }
}
