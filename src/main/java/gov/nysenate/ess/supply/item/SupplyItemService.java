package gov.nysenate.ess.supply.item;

import gov.nysenate.ess.supply.item.dao.ItemRestrictionDao;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * The SupplyItemService should be always be used to get items from the database.
 * This service ensures complete initialization by querying information from multiple sources.
 */
@Service
public class SupplyItemService {

    private SupplyItemDao supplyItemDao;
    private ItemRestrictionDao itemRestrictionDao;

    @Autowired
    public SupplyItemService(SupplyItemDao supplyItemDao, ItemRestrictionDao itemRestrictionDao) {
        this.supplyItemDao = supplyItemDao;
        this.itemRestrictionDao = itemRestrictionDao;
    }

    public Set<SupplyItem> getSupplyItems() {
        Set<SupplyItem> items = supplyItemDao.getSupplyItems();
        for(SupplyItem item: items) {
            item.setRestriction(itemRestrictionDao.forItem(item.getId()));
        }
        return items;
    }

    public SupplyItem getItemById(Integer id) {
        SupplyItem item = supplyItemDao.getItemById(id);
        item.setRestriction(itemRestrictionDao.forItem(id));
        return item;
    }
}
