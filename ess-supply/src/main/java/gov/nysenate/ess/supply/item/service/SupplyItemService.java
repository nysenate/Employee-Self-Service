package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.List;

public interface SupplyItemService {

    List<SupplyItem> getSupplyItems(LimitOffset limOff);

    SupplyItem getItemById(Integer id);
}
