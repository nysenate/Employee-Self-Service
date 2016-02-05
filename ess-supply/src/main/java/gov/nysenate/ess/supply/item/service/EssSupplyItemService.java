package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EssSupplyItemService implements SupplyItemService {

    @Autowired private SupplyItemDao supplyItemDao;

    @Override
    public List<SupplyItem> getSupplyItems(LimitOffset limOff) {
        return supplyItemDao.getSupplyItems(limOff);
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return supplyItemDao.getItemById(id);
    }
}
