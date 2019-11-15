package gov.nysenate.ess.travel.application.allowances.lodging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.GoogleAddressView;
import gov.nysenate.ess.travel.application.allowances.PerDiem;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class LodgingPerDiemView implements ViewObject {

    private int id;
    private String date;
    private String rate;
    private GoogleAddressView address;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public LodgingPerDiemView() {
    }

    public LodgingPerDiemView(LodgingPerDiem lpd) {
        this.id = lpd.id();
        this.date = lpd.date().format(ISO_DATE);
        this.address = new GoogleAddressView(lpd.address());
        this.rate = lpd.rate().toString();
        this.isReimbursementRequested = lpd.isReimbursementRequested();
        this.requestedPerDiem = lpd.requestedPerDiem().toString();
        this.maximumPerDiem = lpd.maximumPerDiem().toString();
    }

    public LodgingPerDiem toLodgingPerDiem() {
        return new LodgingPerDiem(
                id,
                address.toGoogleAddress(),
                new PerDiem(LocalDate.parse(date, ISO_DATE), new BigDecimal(rate)),
                isReimbursementRequested
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

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public GoogleAddressView getAddress() {
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
