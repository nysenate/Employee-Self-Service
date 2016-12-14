package gov.nysenate.ess.supply.unit;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.supply.item.OrderableItems;
import gov.nysenate.ess.supply.item.model.ItemStatus;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.unit.fixtures.SupplyItemFixture;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@org.junit.experimental.categories.Category(UnitTest.class)
public class OrderableItemsTest {

    @Test
    public void givenNull_returnsEmptyList() {
        assertTrue(OrderableItems.forItems(null).isEmpty());
    }

    @Test
    public void givenEmptyList_returnEmptyList() {
        assertTrue(OrderableItems.forItems(new ArrayList<>()).isEmpty());
    }

    @Test
    public void nonExpendableItems_notOrderable() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().withStatus(new ItemStatus(false, true, true, false)).build());
        assertTrue(OrderableItems.forItems(items).isEmpty());
    }

    @Test
    public void visibleItems_areOrderable() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().build());
        assertEquals(items, OrderableItems.forItems(items));
    }

    @Test
    public void hiddenItems_notOrderable() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().withStatus(new ItemStatus(true, true, false, false)).build());
        assertTrue(OrderableItems.forItems(items).isEmpty());
    }

    // TODO: for location tests
}
