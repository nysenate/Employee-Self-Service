package gov.nysenate.ess.travel.application.allowances.meal;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.application.destination.Destination;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.provider.gsa.meal.MealTier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class MealAllowancesFactory {

    private GsaAllowanceService gsaService;

    public MealAllowancesFactory(GsaAllowanceService gsaService) {
        this.gsaService = gsaService;
    }

    public MealAllowances createMealAllowances(Destinations destinations) throws IOException {
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
    private MealAllowance getMostExpensiveMealAllowance(LocalDate date, ImmutableList<Destination> dateDestinations) throws IOException {
        // Place MealTiers and Destinations in an ordered map. The last key will be the most expensive MealTier.
        TreeMap<MealTier, Destination> tierToDests = new TreeMap<>();
        for (Destination d : dateDestinations) {
            MealTier mealTier = gsaService.fetchMealTier(date, d.getAddress());
            tierToDests.put(mealTier, d);
        }

        return createMealAllowance(date, tierToDests.lastEntry().getValue());
    }

    private MealAllowance createMealAllowance(LocalDate date, Destination destination) throws IOException {
        MealTier mealTier = gsaService.fetchMealTier(date, destination.getAddress());
        return new MealAllowance(UUID.randomUUID(), destination.getAddress(), date, mealTier, true);
    }
}
