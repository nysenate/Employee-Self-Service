package gov.nysenate.ess.supply.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.supply.item.model.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@org.junit.experimental.categories.Category(UnitTest.class)
public class SupplyItemTest {

    @Test
    public void itemNotOrderedBySupply_DoesNotRequireSynchronization() {
        SupplyItem item = createItemWithStatus(true, false);
        assertSynchronizationNotRequired(item);

        item = createItemWithStatus(false, false);
        assertSynchronizationNotRequired(item);
    }

    @Test
    public void nonExpendableItem_DoesNotRequireSynchronization() {
        SupplyItem item = createItemWithStatus(false, true);
        assertSynchronizationNotRequired(item);

        item = createItemWithStatus(false, false);
        assertSynchronizationNotRequired(item);
    }

    @Test
    public void expendableItemsOrderedBySupply_RequireSynchronization() {
        SupplyItem item = createItemWithStatus(true, true);
        assertSynchronizationRequired(item);
    }

    private void assertSynchronizationNotRequired(SupplyItem item) {
        assertThat(item.requiresSynchronization(), is(false));
    }

    private void assertSynchronizationRequired(SupplyItem item) {
        assertThat(item.requiresSynchronization(), is(true));
    }

    private SupplyItem createItemWithStatus(boolean isExpenable, boolean orderedBySupply) {
        return new SupplyItem.Builder()
                .withId(1)
                .withCommodityCode("A")
                .withDescription("desc")
                .withStatus(new ItemStatus(isExpenable, orderedBySupply, true, false))
                .withCategory(new Category(""))
                .withAllowance(new ItemAllowance(2, 4))
                .withUnit(new ItemUnit("1", 1))
                .build();
    }
}
