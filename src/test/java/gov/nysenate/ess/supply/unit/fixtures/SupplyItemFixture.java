package gov.nysenate.ess.supply.unit.fixtures;

import gov.nysenate.ess.supply.item.model.*;

public class SupplyItemFixture {

    public static SupplyItem.Builder getDefaultBuilder() {
        return new SupplyItem.Builder().withId(1)
                .withCommodityCode("A")
                .withDescription("desc")
                .withStatus(new ItemStatus(true, false, true, false))
                .withCategory(new Category(""))
                .withAllowance(new ItemAllowance(2, 4))
                .withUnit(new ItemUnit("1", 1));
    }
}
