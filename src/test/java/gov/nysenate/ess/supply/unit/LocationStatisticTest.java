package gov.nysenate.ess.supply.unit;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.model.*;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.statistics.location.LocationStatistic;
import gov.nysenate.ess.supply.unit.fixtures.RequisitionFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@org.junit.experimental.categories.Category(UnitTest.class)
public class LocationStatisticTest {

    private static final Location LOCATION = new Location(new LocationId("A42FB-W"));
    private static final Location ALTERNATE_LOCATION = new Location(new LocationId("ZZZ-Z"));
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
        LocationStatistic locationStatistic = new LocationStatistic(null, new HashSet<>());
    }

    @Test
    public void givenNoRequisitions_returnsEmptyMap() {
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, new HashSet<>());
        assertThat(locationStatistic.calculate().size(), is(0));
    }

    @Test
    public void givenRequisitionWithNoLineItems_returnsEmptyMap() {
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Sets.newHashSet(requisition));
        assertThat(locationStatistic.calculate().size(), is(0));
    }

    @Test
    public void ignoresLineItemsWithZeroQuantity() {
        requisition = addLineItem(1, requisition, "ABC", 0);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Sets.newHashSet(requisition));
        assertThat(locationStatistic.calculate().size(), is(0));
    }

    @Test
    public void giveSingleItem_calculatesStatistics() {
        requisition = addLineItem(1, requisition, "AA", 1);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Sets.newHashSet(requisition));
        assertThat(locationStatistic.calculate().size(), is(1));
        assertThat(locationStatistic.calculate().get("AA"), is(1));
    }

    @Test
    public void givenMultipleRequisitionsWithSameItem_SumsTheTotalQuantity() {
        Requisition first = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        first = addLineItem(1, first, "AA", 1);
        Requisition second = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        second = addLineItem(1, second, "AA", 3);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Sets.newHashSet(first, second));
        assertThat(locationStatistic.calculate().size(), is(1));
        assertThat(locationStatistic.calculate().get("AA"), is(4));
    }

    @Test
    public void onlyRequisitionsForGivenLocationAreUsedInCalculation() {
        Requisition first = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        first = addLineItem(1, first, "AA", 1);
        first = addLineItem(2, first, "BB", 0);
        first = addLineItem(3, first, "CC", 2);
        first = addLineItem(4, first, "DD", 5);
        Requisition second = RequisitionFixture.baseRequisition().setDestination(LOCATION);
        second = addLineItem(2, second, "BB", 3);
        second = addLineItem(3, second, "CC", 1);
        second = addLineItem(5, second, "ZZ", 1);
        Requisition third = RequisitionFixture.baseRequisition().setDestination(ALTERNATE_LOCATION);
        third = addLineItem(1, third, "AA", 0);
        third = addLineItem(3, third, "CC", 1);
        third = addLineItem(4, third, "DD", 3);
        LocationStatistic locationStatistic = new LocationStatistic(LOCATION, Sets.newHashSet(first, second, third));
        assertThat(locationStatistic.calculate().size(), is(5));
        assertThat(locationStatistic.calculate().get("AA"), is(1));
        assertThat(locationStatistic.calculate().get("BB"), is(3));
        assertThat(locationStatistic.calculate().get("CC"), is(3));
        assertThat(locationStatistic.calculate().get("DD"), is(5));
        assertThat(locationStatistic.calculate().get("ZZ"), is(1));
    }

    private Requisition addLineItem(int itemId, Requisition req, String commodityCode, int quantity) {
        Set<LineItem> lineItems = copyLineItems(req);
        lineItems.add(createLineItem(itemId, commodityCode, quantity));
        return req.setLineItems(lineItems);
    }

    private Set<LineItem> copyLineItems(Requisition simple) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItem lineItem : simple.getLineItems()) {
            lineItems.add(lineItem);
        }
        return lineItems;
    }

    private LineItem createLineItem(int itemId, String commodityCode, int quantity) {
        SupplyItem stubItem = new SupplyItem.Builder()
                .withId(itemId)
                .withCommodityCode(commodityCode)
                .withDescription("")
                .withStatus(new ItemStatus(true, true, true, false))
                .withCategory(new Category(""))
                .withAllowance(new ItemAllowance(1, 1))
                .withUnit(new ItemUnit("1", 1))
                .build();
        return new LineItem(stubItem, quantity);
    }
}
