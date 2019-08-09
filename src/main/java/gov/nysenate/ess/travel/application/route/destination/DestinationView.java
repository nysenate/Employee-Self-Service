package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class DestinationView implements ViewObject {

    private int id;
    private AddressView address;
    private String arrivalDate;
    private String departureDate;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;

    public DestinationView() {
    }

    public DestinationView(Destination d) {
        this.id = d.id;
        this.address = new AddressView(d.address);
        this.arrivalDate = d.arrivalDate().format(ISO_DATE);
        this.departureDate = d.departureDate().format(ISO_DATE);
        this.mealPerDiems = new MealPerDiemsView(d.mealPerDiems());
        this.lodgingPerDiems = new LodgingPerDiemsView(d.lodgingPerDiems());
    }

    public Destination toDestination() {
        return new Destination(
                id,
                address.toAddress(),
                arrivalDate == null ? null : LocalDate.parse(arrivalDate, ISO_DATE),
                departureDate == null ? null : LocalDate.parse(departureDate, ISO_DATE),
                mealPerDiems == null ? null : mealPerDiems.toMealPerDiems().allMealPerDiems().stream()
                        .collect(Collectors.toMap(MealPerDiem::date, m -> new PerDiem(m.date(), m.rate(), m.isReimbursementRequested()))),
                lodgingPerDiems == null ? null : lodgingPerDiems.toLodgingPerDiems().allLodgingPerDiems().stream()
                        .collect(Collectors.toMap(LodgingPerDiem::date, l -> new PerDiem(l.date(), l.rate(), l.isReimbursementRequested())))
        );
    }

    public int getId() {
        return id;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public MealPerDiemsView getMealPerDiems() {
        return mealPerDiems;
    }

    public LodgingPerDiemsView getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    @Override
    public String getViewType() {
        return "destination";
    }
}
