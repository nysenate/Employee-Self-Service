package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class AccommodationView implements ViewObject {

    private boolean isMealsRequested;
    private boolean isLodingRequested;
    private AddressView address;
    private List<StayView> stays;
    private String mealAllowance;
    private String lodgingAllowance;
    private String arrivalDate;
    private String departureDate;

    public AccommodationView() {
    }

    public AccommodationView(Accommodation a) {
        isMealsRequested = a.isMealsRequested();
        isLodingRequested = a.isLodgingRequested();
        address = new AddressView(a.getAddress());
        stays = a.getStays().stream()
                .map(StayView::new)
                .collect(Collectors.toList());
        mealAllowance = a.mealAllowance().toString();
        lodgingAllowance = a.lodgingAllowance().toString();
        arrivalDate = a.arrivalDate().format(ISO_DATE);
        departureDate = a.departureDate().format(ISO_DATE);
    }

    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    public boolean isLodingRequested() {
        return isLodingRequested;
    }

    public AddressView getAddress() {
        return address;
    }

    public List<StayView> getStays() {
        return stays;
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
