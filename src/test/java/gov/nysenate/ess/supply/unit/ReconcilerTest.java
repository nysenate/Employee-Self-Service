package gov.nysenate.ess.supply.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.supply.reconcilation.ReconciliationException;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationError;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResults;
import gov.nysenate.ess.supply.reconcilation.service.Reconciler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class ReconcilerTest {

    private static Inventory EMPTY_INV;
    private static Inventory INV_ONE;
    private static Inventory INV_TWO;
    private Reconciler reconciler = new Reconciler();


    @BeforeClass
    public static void beforeClass() {
        EMPTY_INV = new Inventory(new HashMap<>());
        Map<Integer, Integer> itemQuantities = new HashMap<>();
        itemQuantities.put(1, 1);
        INV_ONE = new Inventory(itemQuantities);
        itemQuantities.put(1, 2);
        INV_TWO = new Inventory(itemQuantities);

    }

    @Test
    public void emptyInventories_reconcileSuccessfully() {
        ReconciliationResults results = reconciler.reconcile(EMPTY_INV, EMPTY_INV);
        assertTrue(results.success());
    }

    @Test (expected = ReconciliationException.class)
    public void actualInventoryMissingItem_throwException() {
        ReconciliationResults results = reconciler.reconcile(INV_ONE, EMPTY_INV);
    }

    @Test
    public void expectedEmpty_reconcileSuccessfully() {
        ReconciliationResults results = reconciler.reconcile(EMPTY_INV, INV_ONE);
        assertTrue(results.success());
    }

    @Test
    public void equalInventories_noError() {
        ReconciliationResults results = reconciler.reconcile(INV_ONE, INV_ONE);
        assertTrue(results.success());
    }

    @Test
    public void unequalInventories_returnError() {
        ReconciliationResults results = reconciler.reconcile(INV_ONE, INV_TWO);
        assertTrue(!results.success());

        ReconciliationError error = results.errors().iterator().next();
        assertEquals(1, error.getItemId());
        assertEquals(1, error.getExpectedQuantity());
        assertEquals(2, error.getActualQuantity());
    }

}
