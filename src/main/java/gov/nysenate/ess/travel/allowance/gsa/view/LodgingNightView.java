package gov.nysenate.ess.travel.allowance.gsa.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingNight;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class LodgingNightView implements ViewObject {

    private String date;
    private AddressView address;
    private String rate;

    public LodgingNightView(LodgingNight night) {
        this.date = night.getDate().format(ISO_DATE);
        this.address = new AddressView(night.getAddress());
        this.rate = night.getRate().toString();
    }

    public LodgingNight toLodgingNight() {
        return new LodgingNight(LocalDate.parse(date, ISO_DATE), address.toAddress(), new BigDecimal(rate));
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

    @Override
    public String getViewType() {
        return "lodging-night";
    }
}
