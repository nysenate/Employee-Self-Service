package gov.nysenate.ess.supply.unit;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.statistics.location.LocationStatistic;
import gov.nysenate.ess.supply.unit.fixtures.RequisitionFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LocationStatisticTests {

    private static final Location LOCATION = new Location(new LocationId("A42FB-W"));
    private Requisition requisition;

    @Before
    public void setup() {
        requisition = RequisitionFixture.baseRequisition();
        requisition = requisition.setDestination(LOCATION);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullRequisitions_throwNPE() {
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, null);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullLocation_throwNPE() {
        LocationStatistic locationStatistic = new LocationStatistic(null, new ArrayList<>());
    }

    @Test
    public void givenNoRequisitions_returnsEmptyMap() {
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, new ArrayList<>());
        assertThat(locationStatistic.calculate().size(), is(0));
    }

    @Test
    public void givenRequisitionWithNoLineItems_returnsEmptyMap() {
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Arrays.asList(requisition));
        assertThat(locationStatistic.calculate().size(), is(0));
    }

    @Test
    public void ignoresLineItemsWithZeroQuantity() {
        requisition = addLineItem(requisition, "ABC", 0);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Arrays.asList(requisition));
        assertThat(locationStatistic.calculate().size(), is(0));
    }

    @Test
    public void giveSingleItem_calculatesStatistics() {
        requisition = addLineItem(requisition, "AA", 1);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Arrays.asList(requisition));
        assertThat(locationStatistic.calculate().size(), is(1));
        assertThat(locationStatistic.calculate().get("AA"), is(1));
    }

    @Test
    public void givenMultipleRequisitionsWithSameItem_SumsTheTotalQuantity() {
        Requisition first = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        first = addLineItem(first, "AA", 1);
        Requisition second = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        second = addLineItem(second, "AA", 3);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Arrays.asList(first, second));
        assertThat(locationStatistic.calculate().size(), is(1));
        assertThat(locationStatistic.calculate().get("AA"), is(4));
    }

    @Test
    public void onlyRequisitionsForGivenLocationAreUsedInCalculation() {
        Requisition first = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        first = addLineItem(first, "AA", 1);
        first = addLineItem(first, "BB", 0);
        first = addLineItem(first, "CC", 2);
        first = addLineItem(first, "DD", 5);
        Requisition second = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        second = addLineItem(second, "BB", 3);
        second = addLineItem(second, "CC", 1);
        second = addLineItem(second, "ZZ", 1);
        Requisition third = RequisitionFixture.baseRequisition().setDestination(new Location(new LocationId("ZZZ-Z")));
        third = addLineItem(third, "AA", 0);
        third = addLineItem(third, "CC", 1);
        third = addLineItem(third, "DD", 3);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Arrays.asList(first, second, third));
        assertThat(locationStatistic.calculate().size(), is(5));
        assertThat(locationStatistic.calculate().get("AA"), is(1));
        assertThat(locationStatistic.calculate().get("BB"), is(3));
        assertThat(locationStatistic.calculate().get("CC"), is(3));
        assertThat(locationStatistic.calculate().get("DD"), is(5));
        assertThat(locationStatistic.calculate().get("ZZ"), is(1));
    }

    private Requisition addLineItem(Requisition simple, String commodityCode, int quantity) {
        Set<LineItem> lineItems = copyLineItems(simple);
        lineItems.add(createLineItem(commodityCode, quantity));
        return simple.setLineItems(lineItems);
    }

    private Set<LineItem> copyLineItems(Requisition simple) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItem lineItem : simple.getLineItems()) {
            lineItems.add(lineItem);
        }
        return lineItems;
    }

    private LineItem createLineItem(String commodityCode, int quantity) {
        SupplyItem stubItem = new SupplyItem(1, commodityCode, "", "", new Category(""), 1, 1, 1, ItemVisibility.VISIBLE);
        return new LineItem(stubItem, quantity);
    }
}
