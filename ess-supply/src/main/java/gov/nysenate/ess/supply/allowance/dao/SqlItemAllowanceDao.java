package gov.nysenate.ess.supply.allowance.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.allowance.ItemAllowance;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlItemAllowanceDao extends SqlBaseDao implements ItemAllowanceDao {

    @Autowired private SupplyItemService itemService;

    @Override
    public Set<ItemAllowance> getItemAllowances(LocationId locationId) {
        Set<ItemAllowance> itemAllowances = new HashSet<>();
        // Not fully implemented yet, for now all items should be allowed and visible.
        List<SupplyItem> items = itemService.getSupplyItems(LimitOffset.ALL).getResults();
        for (SupplyItem i: items) {
            ItemAllowance allowance = new ItemAllowance();
            allowance.setSupplyItem(i);
            allowance.setVisibility(ItemVisibility.VISIBLE);
            allowance.setMaxQtyPerMonth(i.getSuggestedMaxQty());
            itemAllowances.add(allowance);
        }
        return itemAllowances;
    }

}
