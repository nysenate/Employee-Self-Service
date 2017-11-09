package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

@Category(UnitTest.class)
public class TravelApplicationTest {

    private TravelApplication.Builder builder;

    @Before
    public void before() {
        Employee emp = new Employee();
        emp.setEmployeeId(1);

        TravelAllowances allowances =  new TravelAllowances(
                new GsaAllowance("0", "0", "0"), "0", "0", "0", "0", "0");

        Address address = new Address("101 Washington Ave", "Albany", "NY", "12210");
        Itinerary itinerary = new Itinerary(address, Lists.newArrayList(new TravelDestination(
                LocalDate.now(), LocalDate.now(), address, ModeOfTransportation.PERSONAL_AUTO)));

        builder = TravelApplication.Builder()
                .setApplicant(emp)
                .setAllowances(allowances)
                .setItinerary(itinerary)
                .setModeOfTransportation(ModeOfTransportation.PERSONAL_AUTO)
                .setCreatedBy(emp);
    }

    @Test(expected = NullPointerException.class)
    public void nullApplicant_isInvalid() {
        builder.setApplicant(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyApplicant_isInvalid() {
        builder.setApplicant(new Employee()).build();
    }

    @Test(expected = NullPointerException.class)
    public void nullAllowances_isInvalid() {
        builder.setAllowances(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void nullModeOfTransportation_isInvalid() {
        builder.setModeOfTransportation(null).build();
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
