package gov.nysenate.ess.supply;

import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.HashSet;
import java.util.Set;

public class TestUtils {

    public static final int CUSTOMER_EMP_ID = 6221;
    public static final int ISSUING_EMP_ID = 11168;
    public static final int MODIFIED_EMP_ID = 10012;
    public static final int ALTERNATE_EMP_ID = 7822;
    public static final Set<LineItem> PENCILS_AND_PENS = initPencilsAndPens();

    /** The location code and type that corresponds to the CUSTOMER_EMP_ID. */
    public static final String CUSTOMER_LOC_CODE = "A42FB";
    public static final String CUSTOMER_LOC_TYPE = "W";

    private static Set<LineItem> initPencilsAndPens() {
        Set<LineItem> pencilsAndPens = new HashSet<>();
        pencilsAndPens.add(new LineItem(new SupplyItem(1, "P2", "Pencils", "Number 2 Yellow Pencils", "24/PKG", new Category("Pencils"), 1, 24), 1));
        pencilsAndPens.add(new LineItem(new SupplyItem(2, "PBL", "Blue Ballpoint Pens", "Blue ink, bold point", "DOZEN", new Category("Pens"), 1, 12), 2));
        return pencilsAndPens;
    }
}
