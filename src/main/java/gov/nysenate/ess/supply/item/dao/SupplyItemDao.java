package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class SupplyItemDao extends SqlBaseDao {

    private ItemRestrictionDao itemRestrictionDao;

    @Autowired
    public SupplyItemDao(ItemRestrictionDao itemRestrictionDao) {
        this.itemRestrictionDao = itemRestrictionDao;
    }

    public Set<SupplyItem> getSupplyItems() {
        String sql = OracleSupplyItemQuery.GET_ALL_SUPPLY_ITEMS.getSql(schemaMap(), new OrderBy("CDCOMMODITY", SortOrder.ASC));
        HashSet<SupplyItem> items = new HashSet<>(remoteNamedJdbc.query(sql, new SupplyItemRowMapper()));
        return setItemRestrictions(items);
    }

    public SupplyItem getItemById(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = OracleSupplyItemQuery.GET_SUPPLY_ITEM_BY_ID.getSql(schemaMap());
        SupplyItem item = remoteNamedJdbc.queryForObject(sql, params, new SupplyItemRowMapper());
        return setItemRestrictions(item);
    }

    private SupplyItem setItemRestrictions(SupplyItem item) {
        item.setRestriction(itemRestrictionDao.forItem(item.getId()));
        return item;
    }

    private Set<SupplyItem> setItemRestrictions(Set<SupplyItem> items) {
        for (SupplyItem item : items) {
            item.setRestriction(itemRestrictionDao.forItem(item.getId()));
        }
        return items;
    }
}
