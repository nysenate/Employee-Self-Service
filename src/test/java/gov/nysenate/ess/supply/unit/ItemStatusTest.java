package gov.nysenate.ess.supply.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.supply.item.model.ItemStatus;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@org.junit.experimental.categories.Category(UnitTest.class)
public class ItemStatusTest {

    @Test
    public void itemNotOrderedBySupply_DoesNotRequireSynchronization(){
        ItemStatus status = new ItemStatus(true, false);
        assertSynchronizationNotRequired(status);

        status = new ItemStatus(false, false);
        assertSynchronizationNotRequired(status);
    }

    @Test
    public void nonExpendableItem_DoesNotRequireSynchronization() {
        ItemStatus status = new ItemStatus(false, true);
        assertSynchronizationNotRequired(status);

        status = new ItemStatus(false, false);
        assertSynchronizationNotRequired(status);
    }

    @Test
    public void expendableItemsOrderedBySupply_RequireSynchronization() {
        ItemStatus status = new ItemStatus(true, true);
        assertSynchronizationRequired(status);
    }

    private void assertSynchronizationNotRequired(ItemStatus status) {
        assertThat(status.requiresSynchronization(), is(false));
    }

    private void assertSynchronizationRequired(ItemStatus status) {
        assertThat(status.requiresSynchronization(), is(true));
    }
}
