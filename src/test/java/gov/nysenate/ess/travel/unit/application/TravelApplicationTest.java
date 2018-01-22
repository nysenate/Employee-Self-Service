package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.application.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

@Category(UnitTest.class)
public class TravelApplicationTest {

    private TravelApplication.Builder builder;

    @Before
    public void before() {
        Employee emp = new Employee();
        emp.setEmployeeId(1);

        TravelAllowances allowances =  new TravelAllowances(new MealAllowance(),
                new LodgingAllowance(), new MileageAllowance(new BigDecimal("0")), "0", "0", "0", "0");

        Address address = new Address("101 Washington Ave", "Albany", "NY", "12210");
        Itinerary itinerary = new Itinerary(address);
        itinerary = itinerary.addDestination(
                new TravelDestination(LocalDate.now(), LocalDate.now(), address),
                new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO));

        builder = TravelApplication.Builder()
                .setTraveler(emp)
                .setAllowances(allowances)
                .setItinerary(itinerary)
                .setCreatedBy(emp);
    }

    @Test(expected = NullPointerException.class)
    public void nullApplicant_isInvalid() {
        builder.setTraveler(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyApplicant_isInvalid() {
        builder.setTraveler(new Employee()).build();
    }

    @Test(expected = NullPointerException.class)
    public void nullAllowances_isInvalid() {
        builder.setAllowances(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void nullItinerary_isInvalid() {
        builder.setItinerary(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void nullCreatedBy_isInvalid() {
        builder.setCreatedBy(null).build();
    }
}
