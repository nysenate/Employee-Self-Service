package gov.nysenate.ess.travel.request.allowances.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.address.TravelAddressView;
import gov.nysenate.ess.travel.provider.senate.SenateMieView;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MealPerDiemView implements ViewObject {

    private int id;
    private String date;
    private String rate;
    private SenateMieView mie;
    private TravelAddressView address;
    @JsonProperty("isBreakfastRequested")
    private boolean isBreakfastRequested = true;
    @JsonProperty("isDinnerRequested")
    private boolean isDinnerRequested = true;
    @JsonProperty("qualifiesForBreakfast")
    private boolean qualifiesForBreakfast;
    @JsonProperty("qualifiesForDinner")
    private boolean qualifiesForDinner;

    private String total;
    private String breakfast;
    private String dinner;

    public MealPerDiemView() {
    }

    public MealPerDiemView(MealPerDiem mpd) {
        this.id = mpd.id();
        this.date = mpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new TravelAddressView(mpd.address());
        this.rate = mpd.rate().toString();
        this.mie = mpd.mie() == null ? null : new SenateMieView(mpd.mie());
        this.isBreakfastRequested = mpd.isBreakfastRequested();
        this.isDinnerRequested = mpd.isDinnerRequested();
        this.qualifiesForBreakfast = mpd.qualifiesForBreakfast();
        this.qualifiesForDinner = mpd.qualifiesForDinner();

        this.total = mpd.total().toString();
        this.breakfast = mpd.breakfast().toString();
        this.dinner = mpd.dinner().toString();
    }

    public MealPerDiem toMealPerDiem() {
        return new MealPerDiem(
                id,
                address.toTravelAddress(),
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
                new Dollars(rate),
                mie == null ? null : mie.toSenateMie(),
                isBreakfastRequested,
                isDinnerRequested,
                qualifiesForBreakfast,
                qualifiesForDinner
        );
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getRate() {
        return rate;
    }

    public SenateMieView getMie() {
        return mie;
    }

    public TravelAddressView getAddress() {
        return address;
    }

    public boolean isBreakfastRequested() {
        return isBreakfastRequested;
    }

    public boolean isDinnerRequested() {
        return isDinnerRequested;
    }

    public boolean isQualifiesForBreakfast() {
        return qualifiesForBreakfast;
    }

    public boolean isQualifiesForDinner() {
        return qualifiesForDinner;
    }

    public String getTotal() {
        return total;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getDinner() {
        return dinner;
    }

    @Override
    public String getViewType() {
        return "meal-per-diem";
    }
}
