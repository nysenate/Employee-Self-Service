package gov.nysenate.ess.travel.application.allowances.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.TravelAddressView;
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
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem; // TODO can be derived from rate and isReimbursementRequested?
    private String maximumPerDiem; // TODO Always the same as rate?

    public MealPerDiemView() {
    }

    public MealPerDiemView(MealPerDiem mpd) {
        this.id = mpd.id();
        this.date = mpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new TravelAddressView(mpd.address());
        this.rate = mpd.rate().toString();
        this.mie = mpd.mie() == null ? null : new SenateMieView(mpd.mie());
        this.isReimbursementRequested = mpd.isReimbursementRequested();
        this.requestedPerDiem = mpd.requestedPerDiem().toString();
        this.maximumPerDiem = mpd.maximumPerDiem().toString();
    }

    public MealPerDiem toMealPerDiem() {
        return new MealPerDiem(
                id,
                address.toTravelAddress(),
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
                new Dollars(rate),
                mie == null ? null : mie.toSenateMie(),
                isReimbursementRequested
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

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    @Override
    public String getViewType() {
        return "meal-per-diem";
    }
}
