package gov.nysenate.ess.travel.provider.gsa.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.travel.utils.Dollars;

/**
 * This view is used for deserializing the json response from the GSA meal and incidental expenses api.
 */
public class GsaMealIncidentalExpensesView {

    private double total;
    private double breakfast;
    private double lunch;
    private double dinner;
    private double incidental;
    @JsonProperty("FirstLastDay ") // Note the space at the end.
    private double FirstLastDay;

    public GsaMealIncidentalExpensesView() {
    }

    /**
     * Convert to a {@link GsaMealIncidentalExpenses}
     * @param fiscalYear The fiscal year of this rate.
     * @return
     */
    public GsaMealIncidentalExpenses toGsaMealRate(int fiscalYear) {
        return new GsaMealIncidentalExpenses(fiscalYear, new Dollars(total), new Dollars(breakfast), new Dollars(lunch),
                new Dollars(dinner), new Dollars(incidental), new Dollars(FirstLastDay));
    }

    public double getTotal() {
        return total;
    }

    public double getBreakfast() {
        return breakfast;
    }

    public double getLunch() {
        return lunch;
    }

    public double getDinner() {
        return dinner;
    }

    public double getIncidental() {
        return incidental;
    }

    public double getFirstLastDay() {
        return FirstLastDay;
    }
}
