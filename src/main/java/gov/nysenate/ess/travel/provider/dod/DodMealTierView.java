package gov.nysenate.ess.travel.provider.dod;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;

public class DodMealTierView implements ViewObject {

    private Dollars incidental;
    private Dollars total; //bfast + lunch + dinner
    private Dollars lodging;
    private String tier; //meals + incidental
    private LocalDate effectiveDate;
    private Range<LocalDate> season;
    private String location;

    public DodMealTierView() {}

    public DodMealTierView(DodMealTier dodMealTier) {
        this.incidental = dodMealTier.getIncidental();
        this.total = dodMealTier.getTotal();
        this.lodging = dodMealTier.getLodging();
        this.tier = dodMealTier.getTier();
        this.effectiveDate = dodMealTier.getEffectiveDate();
        this.season = dodMealTier.getSeason();
        this.location = dodMealTier.getLocation();
    }

    public DodMealTier toDodMealTier() {
        return new DodMealTier(location,season, lodging.toString(),
                total.toString(),incidental.toString(), effectiveDate);
    }

    public String getIncidental() {
        return incidental.toString();
    }

    public String getTotal() {
        return total.toString();
    }

    public String getLodging() {
        return lodging.toString();
    }

    public String getTier() {
        return tier;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public Range<LocalDate> getSeason() {
        return season;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String getViewType() {
        return "dod-meal-tier";
    }
}
