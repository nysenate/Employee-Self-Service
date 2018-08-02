package gov.nysenate.ess.travel.application.allowances.lodging;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LodgingAllowanceView implements ViewObject {

    AddressView address;
    String date;
    String lodgingRate;
    @JsonProperty(value="isLodgingRequested")
    boolean isLodgingRequested;
    String allowance;

    public LodgingAllowanceView() {
    }

    public LodgingAllowanceView(LodgingAllowance lodgingAllowance) {
        this.address = new AddressView(lodgingAllowance.getAddress());
        this.date = lodgingAllowance.getDate().format(DateTimeFormatter.ISO_DATE);
        this.lodgingRate = lodgingAllowance.getLodgingRate().toString();
        this.isLodgingRequested = lodgingAllowance.isLodgingRequested();
        this.allowance = lodgingAllowance.allowance().toString();
    }

    public LodgingAllowance toLodgingAllowance() {
        return new LodgingAllowance(address.toAddress(), LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
                new Dollars(lodgingRate), isLodgingRequested);
    }

    public AddressView getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getLodgingRate() {
        return lodgingRate;
    }

    @JsonProperty(value="isLodgingRequested")
    public boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    public String getAllowance() {
        return allowance;
    }

    @Override
    public String getViewType() {
        return "lodging-allowance";
    }
}
