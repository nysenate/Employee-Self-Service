package gov.nysenate.ess.travel.application.allowances.meal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MealPerDiemView implements ViewObject {

    private String date;
    private String rate;
    private AddressView address;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public MealPerDiemView() {
    }

    public MealPerDiemView(MealPerDiem mpd) {
        this.date = mpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new AddressView(mpd.address());
        this.rate = mpd.rate().toString();
        this.isReimbursementRequested = mpd.isReimbursementRequested();
        this.requestedPerDiem = mpd.requestedPerDiem().toString();
        this.maximumPerDiem = mpd.maximumPerDiem().toString();
    }

    public MealPerDiem toMealPerDiem() {
        return new MealPerDiem(
                address.toAddress(),
                new PerDiem(LocalDate.parse(date, DateTimeFormatter.ISO_DATE), new Dollars(rate)),
                isReimbursementRequested,
                new Dollars(rate)
        );
    }

    @JsonIgnore
    public LocalDate date() {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    @JsonIgnore
    public BigDecimal rate() {
        return new BigDecimal(rate);
    }


    public String getDate() {
        return date;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getRate() {
        return rate;
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
