package gov.nysenate.ess.travel.application.uncompleted;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;

/**
 * A Data Transfer Object, used to accept front end model data from the
 * travel application expenses page.
 *
 * This data is used to update {@link gov.nysenate.ess.travel.accommodation.Accommodation} and allowances.
 */
public class ExpensesDtoView implements ViewObject {

    List<DestinationDtoView> destinations;
    AllowancesDtoView allowances;

    public ExpensesDtoView() {
    }

    public List<DestinationDtoView> getDestinations() {
        return destinations;
    }

    public AllowancesDtoView getAllowances() {
        return allowances;
    }

    @Override
    public String getViewType() {
        return "expenses-dto";
    }
}
