package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

public class GsaMieView implements ViewObject {

    private int id;
    private int fiscalYear;
    private String breakfast;
    private String dinner;
    private String total;
    private String gsaBreakfast;
    private String gsaLunch;
    private String gsaDinner;
    private String gsaIncidental;
    private String gsaFirstLastDay;

    public GsaMieView() {
    }

    public GsaMieView(GsaMie mie) {
        this.id = mie.getId();
        this.fiscalYear = mie.getFiscalYear();
        this.breakfast = mie.breakfast().toString();
        this.dinner = mie.dinner().toString();
        this.total = mie.total().toString();
        this.gsaBreakfast = mie.getGsaBreakfast().toString();
        this.gsaLunch = mie.getGsaLunch().toString();
        this.gsaDinner = mie.getGsaDinner().toString();
        this.gsaIncidental = mie.getGsaIncidental().toString();
        this.gsaFirstLastDay = mie.getGsaFirstLastDay().toString();
    }

    public GsaMie toGsaMie() {
        return new GsaMie(
                id,
                fiscalYear,
                new Dollars(total),
                new Dollars(gsaBreakfast),
                new Dollars(gsaLunch),
                new Dollars(gsaDinner),
                new Dollars(gsaIncidental),
                new Dollars(gsaFirstLastDay)
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

    public String getGsaBreakfast() {
        return gsaBreakfast;
    }

    public String getGsaLunch() {
        return gsaLunch;
    }

    public String getGsaDinner() {
        return gsaDinner;
    }

    public String getGsaIncidental() {
        return gsaIncidental;
    }

    public String getGsaFirstLastDay() {
        return gsaFirstLastDay;
    }

    @Override
    public String getViewType() {
        return "gsa-mie";
    }
}
