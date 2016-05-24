package gov.nysenate.ess.supply.unit.requisition;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.SupplyUnitTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void before() {
        stubLocation = RequisitionFixture.createStubLocation();
        stubEmployee = RequisitionFixture.createEmployeeWithId(1);
        stubLineItems = RequisitionFixture.createStubLineItem();
    }

    @Test
    public void defaultValues() {
        RequisitionVersion version = RequisitionFixture.getMinimalPendingVersion();
        assertThat(version.getId(), is(0));
        assertThat(version.getIssuer().isPresent(), is(false));
        assertThat(version.getNote().isPresent(), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void customerNotNull() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withDestination(stubLocation)
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(stubLineItems)
                .withIssuer(stubEmployee)
                .withCreatedBy(stubEmployee)
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
                .withCreatedBy(stubEmployee)
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
                .withCreatedBy(stubEmployee)
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
                .withCreatedBy(stubEmployee)
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
                .withCreatedBy(stubEmployee)
                .build();
    }
}
