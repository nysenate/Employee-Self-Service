package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class PerDiemView implements ViewObject {

    AddressView address;
    String date;
    String dollars;

    public PerDiemView() {
    }

    public PerDiemView(PerDiem perDiem) {
        this.address = new AddressView(perDiem.getAddress());
        this.date = perDiem.getDate().format(DateTimeFormatter.ISO_DATE);
        this.dollars = perDiem.getDollars().toString();
    }

    public AddressView getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getDollars() {
        return dollars;
    }

    @Override
    public String getViewType() {
        return "per-diem";
    }
}
