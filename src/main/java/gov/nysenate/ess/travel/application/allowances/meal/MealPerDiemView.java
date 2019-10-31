package gov.nysenate.ess.travel.application.allowances.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.GoogleAddressView;
import gov.nysenate.ess.travel.provider.senate.SenateMieView;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MealPerDiemView implements ViewObject {

    private int id;
    private String date;
    private String rate;
    private SenateMieView mie;
    private GoogleAddressView address;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public MealPerDiemView() {
    }

    public MealPerDiemView(MealPerDiem mpd) {
        this.id = mpd.id();
        this.date = mpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new GoogleAddressView(mpd.address());
        this.rate = mpd.rate().toString();
        this.mie = mpd.mie() == null ? null : new SenateMieView(mpd.mie());
        this.isReimbursementRequested = mpd.isReimbursementRequested();
        this.requestedPerDiem = mpd.requestedPerDiem().toString();
        this.maximumPerDiem = mpd.maximumPerDiem().toString();
    }

    public MealPerDiem toMealPerDiem() {
        return new MealPerDiem(
                id,
                address.toGoogleAddress(),
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

    public GoogleAddressView getAddress() {
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
