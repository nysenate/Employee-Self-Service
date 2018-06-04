package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class AccommodationView implements ViewObject {

    AddressView address;
    List<DayView> days;
    List<NightView> nights;
    String mealAllowance;
    String lodgingAllowance;
    String arrivalDate;
    String departureDate;

    public AccommodationView() {
    }

    public AccommodationView(Accommodation a) {
        address = new AddressView(a.getAddress());
        days = a.getDays().stream()
                .map(DayView::new)
                .collect(Collectors.toList());
        nights = a.getNights().stream()
                .map(NightView::new)
                .collect(Collectors.toList());
        mealAllowance = a.mealAllowance().toString();
        lodgingAllowance = a.lodgingAllowance().toString();
        arrivalDate = a.arrivalDate().format(ISO_DATE);
        departureDate = a.departureDate().format(ISO_DATE);
    }

    public Accommodation toAccommodation() {
        return new Accommodation(address.toAddress(),
                days.stream().map(DayView::toDay).collect(ImmutableSet.toImmutableSet()),
                nights.stream().map(NightView::toNight).collect(ImmutableSet.toImmutableSet()));
    }

    public AddressView getAddress() {
        return address;
    }

    public List<DayView> getDays() {
        return days;
    }

    public List<NightView> getNights() {
        return nights;
    }

    public String getMealAllowance() {
        return mealAllowance;
    }

    public String getLodgingAllowance() {
        return lodgingAllowance;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    @Override
    public String getViewType() {
        return "accommodation";
    }
}
