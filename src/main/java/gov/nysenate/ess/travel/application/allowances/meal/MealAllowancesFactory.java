package gov.nysenate.ess.travel.application.allowances.meal;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.provider.ServiceProviderFactory;
import gov.nysenate.ess.travel.application.destination.Destination;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class MealAllowancesFactory {

    private ServiceProviderFactory serviceProviderFactory;

    @Autowired
    public MealAllowancesFactory(ServiceProviderFactory serviceProviderFactory) {
        this.serviceProviderFactory = serviceProviderFactory;
    }

    /**
     * Calculates {@link MealAllowances}.
     * @param destinations Destinations used in the meal allowance calculation.
     * @return {@link MealAllowances}
     * @throws ProviderException If there is an error communicating with our 3rd party meal rate providers.
     */
    public MealAllowances createMealAllowances(Destinations destinations) {
        List<MealAllowance> mealAllowances = new ArrayList<>();

        LocalDate tripStartDate = destinations.startDate();
        LocalDate tripEndDate = destinations.endDate();
        for (LocalDate date = tripStartDate; date.isBefore(tripEndDate.plusDays(1)); date = date.plusDays(1)) {
            ImmutableList<Destination> dateDestinations = destinations.destinationsForDate(date);
            if (dateDestinations.size() > 1) {
                mealAllowances.add(getMostExpensiveMealAllowance(date, dateDestinations));
            }
            else {
                mealAllowances.add(createMealAllowance(date, dateDestinations.get(0)));
            }
        }
        return new MealAllowances(mealAllowances);
    }

    /**
     * A trip can only have a single meal allowance per day. If the traveler will be at multiple destinations
     * on the same day we should give them the meal allowance for the more expensive destination.
     * @param date
     * @param dateDestinations
     * @return
     * @throws IOException
     */
    private MealAllowance getMostExpensiveMealAllowance(LocalDate date, ImmutableList<Destination> dateDestinations) {
        // Place Dollars and Destinations in an ordered map. The last key will be the most expensive MealTier.
        TreeMap<Dollars, Destination> ratesToDests = new TreeMap<>();
        for (Destination d : dateDestinations) {
            Dollars mealRate = serviceProviderFactory.fetchMealRate(date, d.getAddress());
            ratesToDests.put(mealRate, d);
        }

        return createMealAllowance(date, ratesToDests.lastEntry().getValue());
    }

    private MealAllowance createMealAllowance(LocalDate date, Destination destination) {
        Dollars mealRate = serviceProviderFactory.fetchMealRate(date, destination.getAddress());
        return new MealAllowance(UUID.randomUUID(), destination.getAddress(), date, mealRate, true);
    }
}
