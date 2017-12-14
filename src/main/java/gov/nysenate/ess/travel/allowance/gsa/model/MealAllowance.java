package gov.nysenate.ess.travel.allowance.gsa.model;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MealAllowance {

    private BigDecimal meals;

    public MealAllowance(BigDecimal meals) {
        checkArgument(checkNotNull(meals).signum() >= 0);
        this.meals = meals;
    }
}
