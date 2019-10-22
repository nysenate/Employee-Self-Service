package gov.nysenate.ess.travel.provider.dod;

import com.google.common.collect.Range;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;

public class DodMealTier {

    private Dollars incidental;
    private Dollars total; //bfast + lunch + dinner
    private Dollars lodging;
    private String tier; //meals + incidental
    private LocalDate effectiveDate;
    private Range<LocalDate> season;
    private String location;

    public DodMealTier(String location, Range<LocalDate> season, String lodging, String total, String incidental,
                       LocalDate effectiveDate ) {

        this.location = location;
        this.season = season;
        this.lodging = new Dollars(lodging);
        this.total = new Dollars(total);
        this.incidental = new Dollars(incidental);
        this.tier = (this.incidental.add(this.total)).toString();
        this.effectiveDate = effectiveDate;

    }

    /**
     * DOD does not separate out meals into breakfast, lunch, dinner.
     * Need to figure out what to use for this, for now use zero.
     * @return
     */
    public Dollars breakfast() {
        return Dollars.ZERO;
    }

    /**
     * DOD does not separate out meals into breakfast, lunch, dinner.
     * Need to figure out what to use for this, for now use zero.l
     * @return
     */
    public Dollars dinner() {
        return Dollars.ZERO;
    }

    public Dollars total() {
        // TODO difference between total and tier? where does total come from?
        return new Dollars(tier);
    }

    public Dollars getIncidental() {
        return incidental;
    }

    public void setIncidental(Dollars incidental) {
        this.incidental = incidental;
    }

    public Dollars getTotal() {
        return total;
    }

    public void setTotal(Dollars total) {
        this.total = total;
    }

    public Dollars getLodging() {
        return lodging;
    }

    public void setLodging(Dollars lodging) {
        this.lodging = lodging;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Range<LocalDate> getSeason() {
        return season;
    }

    public void setSeason(Range<LocalDate> season) {
        this.season = season;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
