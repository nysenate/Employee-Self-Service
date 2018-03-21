package gov.nysenate.ess.travel.application;

import com.google.common.base.Strings;
import com.google.maps.errors.ApiException;
import gov.nysenate.ess.travel.accommodation.AccommodationFactory;
import gov.nysenate.ess.travel.route.RouteFactory;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Component
public class TravelApplicationFactory {

    private AccommodationFactory accommodationFactory;
    private RouteFactory routeFactory;

    @Autowired
    public TravelApplicationFactory(AccommodationFactory accommodationFactory, RouteFactory routeFactory) {
        this.accommodationFactory = accommodationFactory;
        this.routeFactory = routeFactory;
    }

    public TravelApplication createApplication(TravelApplicationView appView) throws IOException, ApiException, InterruptedException {
        TravelApplication app = new TravelApplication(0, appView.getTraveler().toEmployee(),
                appView.getSubmitter().toEmployee());
        app.setAccommodations(accommodationFactory.createAccommodations(appView));
        app.setRoute(routeFactory.createRoute(appView));
        app.setPurposeOfTravel(appView.getPurposeOfTravel());
        app.setTolls(new Dollars(appView.getTollsAllowance()));
        app.setParking(new Dollars(appView.getParkingAllowance()));
        app.setAlternate(new Dollars(appView.getAlternateAllowance()));
        app.setRegistration(new Dollars(appView.getRegistrationAllowance()));
        if (!Strings.isNullOrEmpty(appView.getSubmittedDateTime())) {
            app.setSubmittedDateTime(LocalDateTime.parse(appView.getSubmittedDateTime(), ISO_DATE_TIME));
        }
        return app;
    }
}
