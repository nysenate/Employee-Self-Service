package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.application.view.GsaAllowanceView;
import gov.nysenate.ess.travel.application.view.ItineraryView;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.gsa.controller.GsaApplicationCtrl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Category(IntegrationTest.class)
public class GsaApplicationCtrlIT extends BaseTest {

    @Autowired private GsaApplicationCtrl gsaApplicationCtrl;

    @Test
    public void blah(){
        Address fromAddress = new Address("515 Loudon Rd", "Loudonville", "NY", "12211");
        Address toAddress = new Address("S Mall Arterial", "Albany", "NY", "12210");

        LocalDate arrival = LocalDate.of(2017, Month.SEPTEMBER, 30);
        LocalDate departure = LocalDate.of(2017, Month.OCTOBER, 2);

        TravelDestination travelDestination = new TravelDestination(arrival, departure, toAddress);
        List<TravelDestination> travelDestinations = Arrays.asList(travelDestination);

        Itinerary itinerary = new Itinerary(fromAddress, travelDestinations);
        ItineraryView itineraryView = new ItineraryView(itinerary);

        BaseResponse baseResponse = gsaApplicationCtrl.searchGsa(itineraryView);
        ListViewResponse listView = (ListViewResponse) baseResponse;

        GsaAllowanceView gsaAllowanceView = (GsaAllowanceView) listView.result.items.get(0);
        System.out.println(gsaAllowanceView.getMeals());
    }
}
