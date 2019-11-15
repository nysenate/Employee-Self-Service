package gov.nysenate.ess.travel.provider.senate;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

public class SenateMieView implements ViewObject {

    private int id;
    private int fiscalYear;
    private String breakfast;
    private String dinner;
    private String total;

    public SenateMieView() {
    }

    public SenateMieView(SenateMie mie) {
        this.id = mie.getId();
        this.fiscalYear = mie.getFiscalYear();
        this.breakfast = mie.breakfast().toString();
        this.dinner = mie.dinner().toString();
        this.total = mie.total().toString();
    }

    public SenateMie toSenateMie() {
        return new SenateMie(
                id,
                fiscalYear,
                new Dollars(total),
                new Dollars(breakfast),
                new Dollars(dinner)
        );
    }

    public int getId() {
        return id;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getDinner() {
        return dinner;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public String getViewType() {
        return "senate-mie";
    }
}
