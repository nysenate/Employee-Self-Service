package gov.nysenate.ess.travel.accommodation;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NightView implements ViewObject {

    public String date;
    public String lodgingAllowance;
    public String lodgingRate;
    public boolean isLodgingRequested;

    public NightView() {
    }

    public NightView(Night night) {
        this.date = night.getDate().format(DateTimeFormatter.ISO_DATE);
        this.lodgingAllowance = night.lodgingAllowance().toString();
        this.lodgingRate = night.getLodgingRate().toString();
        this.isLodgingRequested = night.isLodgingRequested();
    }

    public Night toNight() {
        return new Night(LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
                new Dollars(lodgingRate), isLodgingRequested);
    }

    public String getDate() {
        return date;
    }

    public String getLodgingAllowance() {
        return lodgingAllowance;
    }

    @JsonProperty(value="isLodgingRequested")
    public boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    public String getLodgingRate() {
        return lodgingRate;
    }

    @Override
    public String getViewType() {
        return "night";
    }
}
