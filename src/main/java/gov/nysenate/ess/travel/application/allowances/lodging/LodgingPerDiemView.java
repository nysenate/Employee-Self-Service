package gov.nysenate.ess.travel.application.allowances.lodging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class LodgingPerDiemView implements ViewObject {

    private String date;
    private AddressView address;
    private String rate;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public LodgingPerDiemView() {
    }

    public LodgingPerDiemView(LodgingPerDiem lpd) {
        this.date = lpd.date().format(ISO_DATE);
        this.address = new AddressView(lpd.address());
        this.rate = lpd.rate().toString();
        this.isReimbursementRequested = lpd.isReimbursementRequested();
        this.requestedPerDiem = lpd.requestedPerDiem().toString();
        this.maximumPerDiem = lpd.maximumPerDiem().toString();
    }

    public LodgingPerDiem toLodgingPerDiem() {
        return new LodgingPerDiem(
                address.toAddress(),
                new PerDiem(
                        LocalDate.parse(date, ISO_DATE),
                        new Dollars(rate),
                        isReimbursementRequested
                )
        );
    }

    @JsonIgnore
    public LocalDate date() {
        return LocalDate.parse(date, ISO_DATE);
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
        return "lodging-per-diem";
    }
}
