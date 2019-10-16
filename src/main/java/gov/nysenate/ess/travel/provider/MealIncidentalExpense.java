package gov.nysenate.ess.travel.provider;

import gov.nysenate.ess.travel.utils.Dollars;

public interface MealIncidentalExpense {

    /**
     * The Senate's reimbursement for breakfast.
     * @return
     */
    Dollars breakfast();

    /**
     * The Senate's reimbursement for dinner.
     * @return
     */
    Dollars dinner();

    /**
     * The Senate's total daily meal reimbursement.
     * @return
     */
    Dollars total();

}
