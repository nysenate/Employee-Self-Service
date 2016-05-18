package gov.nysenate.ess.supply.unit.requisition;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.SupplyUnitTests;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequisitionVersionTests extends SupplyUnitTests {

    private static Location stubLocation;
    private static Employee stubEmployee;
    private static Set<LineItem> stubLineItems;
    private static String stubNote = "stub note";

    @BeforeClass
    public static void before() {
        stubLocation = new Location(new LocationId("A42FB", 'W'));
        stubEmployee = new Employee();
        stubEmployee.setEmployeeId(1);
        stubLineItems = createStubLineItem();
    }

    private static Set<LineItem> createStubLineItem() {
        SupplyItem stubItem = new SupplyItem(1, "", "", "", new Category(""), 1, 1, 1);
        Set<LineItem> stubLineItems = new HashSet<>();
        stubLineItems.add(new LineItem(stubItem, 1));
        return stubLineItems;
    }

    @Test
    public void defaultValues() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withCustomer(stubEmployee)
                .withDestination(stubLocation)
                .withLineItems(stubLineItems)
                .withStatus(RequisitionStatus.PENDING)
                .withModifiedBy(stubEmployee)
                .build();

        assertThat(version.getId(), is(0));
        assertThat(version.getIssuer().isPresent(), is(false));
        assertThat(version.getNote().isPresent(), is(false));
    }

    @Test
    public void builderWorks() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(stubEmployee)
                .withDestination(stubLocation)
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(stubLineItems)
                .withIssuer(stubEmployee)
                .withModifiedBy(stubEmployee)
                .withNote(stubNote)
                .build();
        assertThat(version.getId(), is(1));
        assertThat(version.getCustomer(), is(stubEmployee));
        assertThat(version.getDestination(), is(stubLocation));
        assertThat(version.getStatus(), is(RequisitionStatus.PENDING));
        assertThat(version.getLineItems(), is(stubLineItems));
        assertThat(version.getIssuer().get(), is(stubEmployee));
        assertThat(version.getModifiedBy(), is(stubEmployee));
        assertThat(version.getNote().get(), is(stubNote));
    }

    @Test(expected = NullPointerException.class)
    public void customerNotNull() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withDestination(stubLocation)
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(stubLineItems)
                .withIssuer(stubEmployee)
                .withModifiedBy(stubEmployee)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void destinationNotNull() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(stubEmployee)
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(stubLineItems)
                .withIssuer(stubEmployee)
                .withModifiedBy(stubEmployee)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void statusNotNull() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(stubEmployee)
                .withDestination(stubLocation)
                .withLineItems(stubLineItems)
                .withIssuer(stubEmployee)
                .withModifiedBy(stubEmployee)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void lineItemsNotNull() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(stubEmployee)
                .withDestination(stubLocation)
                .withStatus(RequisitionStatus.PENDING)
                .withIssuer(stubEmployee)
                .withModifiedBy(stubEmployee)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void lineItemsNotEmpty() {
        Set<LineItem> emptyLineItems = new HashSet<>();
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(stubEmployee)
                .withDestination(stubLocation)
                .withLineItems(emptyLineItems)
                .withStatus(RequisitionStatus.PENDING)
                .withIssuer(stubEmployee)
                .withModifiedBy(stubEmployee)
                .build();
    }
}
