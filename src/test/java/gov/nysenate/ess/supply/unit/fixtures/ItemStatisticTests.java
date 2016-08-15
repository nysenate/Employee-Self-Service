package gov.nysenate.ess.supply.unit.fixtures;

import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.statistics.ItemStatistic;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ItemStatisticTests {

    @Test(expected = NullPointerException.class)
    public void givenNullRequisitions_throwNPE() {
        ItemStatistic itemStatistic = new ItemStatistic(null);
    }

    @Test
    public void givenNoRequisitions_returnsEmptyMap() {
        ItemStatistic itemStatistic = new ItemStatistic(new ArrayList<>());
        assertThat(itemStatistic.calculate().size(), is(0));
    }

    @Test
    public void givenRequisitionWithNoLineItems_returnsEmptyMap() {
        Requisition requisition = RequisitionFixture.baseRequisition();
        ItemStatistic itemStatistic = new ItemStatistic(Arrays.asList(requisition));
        assertThat(itemStatistic.calculate().size(), is(0));
    }

    @Test
    public void ignoresLineItemsWithZeroQuantity() {
        Requisition requisition = RequisitionFixture.baseRequisition();
        requisition = addLineItem(requisition, "ABC", 0);
        ItemStatistic itemStatistic = new ItemStatistic(Arrays.asList(requisition));
        assertThat(itemStatistic.calculate().size(), is(0));
    }

    @Test
    public void giveSingleItem_calculatesStatistics() {
        Requisition simple = RequisitionFixture.baseRequisition();
        simple = addLineItem(simple, "AA", 1);
        ItemStatistic itemStatistic = new ItemStatistic(Arrays.asList(simple));
        assertThat(itemStatistic.calculate().size(), is(1));
        assertThat(itemStatistic.calculate().get("AA"), is(1));
    }

    @Test
    public void givenMultipleRequisitionsWithSameItem_SumsTheTotalQuantity() {
        Requisition first = RequisitionFixture.baseRequisition();
        first = addLineItem(first, "AA", 1);
        Requisition second = RequisitionFixture.baseRequisition();
        second = addLineItem(second, "AA", 3);
        ItemStatistic itemStatistic = new ItemStatistic(Arrays.asList(first, second));
        assertThat(itemStatistic.calculate().size(), is(1));
        assertThat(itemStatistic.calculate().get("AA"), is(4));
    }

    @Test
    public void complexTest() {
        Requisition first = RequisitionFixture.baseRequisition();
        first = addLineItem(first, "AA", 1);
        first = addLineItem(first, "BB", 0);
        first = addLineItem(first, "CC", 2);
        first = addLineItem(first, "DD", 5);
        Requisition second = RequisitionFixture.baseRequisition();
        second = addLineItem(second, "BB", 3);
        second = addLineItem(second, "CC", 1);
        second = addLineItem(second, "ZZ", 1);
        Requisition third = RequisitionFixture.baseRequisition();
        third = addLineItem(third, "AA", 0);
        third = addLineItem(third, "CC", 1);
        third = addLineItem(third, "DD", 3);
        ItemStatistic itemStatistic = new ItemStatistic(Arrays.asList(first, second, third));
        assertThat(itemStatistic.calculate().size(), is(5));
        assertThat(itemStatistic.calculate().get("AA"), is(1));
        assertThat(itemStatistic.calculate().get("BB"), is(3));
        assertThat(itemStatistic.calculate().get("CC"), is(4));
        assertThat(itemStatistic.calculate().get("DD"), is(8));
        assertThat(itemStatistic.calculate().get("ZZ"), is(1));
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
