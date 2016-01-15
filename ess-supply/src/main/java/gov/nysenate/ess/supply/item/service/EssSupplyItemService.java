package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EssSupplyItemService implements SupplyItemService {

    private SupplyItemDao supplyItemDao;

    @Autowired
    public EssSupplyItemService(SupplyItemDao supplyItemDao) {
        this.supplyItemDao = supplyItemDao;
    }

    @Override
    public List<SupplyItem> getSupplyItems() {
        return supplyItemDao.getSupplyItems();
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return supplyItemDao.getItemById(id);
    }
}
