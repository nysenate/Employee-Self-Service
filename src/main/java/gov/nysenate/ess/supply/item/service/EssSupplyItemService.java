package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EssSupplyItemService implements SupplyItemService {

    @Autowired private SupplyItemDao supplyItemDao;

    @Override
    public PaginatedList<SupplyItem> getSupplyItems(LimitOffset limOff) {
        return supplyItemDao.getSupplyItems(limOff);
    }

    @Override
    public PaginatedList<SupplyItem> getSupplyItemsByCategorys(List<Category> categories, LimitOffset limOff) {
        return supplyItemDao.getSupplyItemsByCategories(categories, limOff);
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return supplyItemDao.getItemById(id);
    }
}
