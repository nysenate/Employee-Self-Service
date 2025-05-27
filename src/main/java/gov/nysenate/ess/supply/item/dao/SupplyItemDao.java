package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.supply.item.model.ItemRestriction;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        Set<SupplyItem> items = new HashSet<>(remoteNamedJdbc.query(sql, new SupplyItemRowMapper()));
        applyItemRestrictions(items);
        return items;
    }

    /**
     * Get a set of items from a set of item ids.
     */
    public Set<SupplyItem> getItemsByIds(Set<Integer> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        String sql = OracleSupplyItemQuery.GET_SUPPLY_ITEMS_BY_IDS.getSql(schemaMap());
        Set<SupplyItem> items = new HashSet<>(remoteNamedJdbc.query(sql, params, new SupplyItemRowMapper()));
        applyItemRestrictions(items);
        return items;
    }

    /**
     * Get a single item by its Id.
     */
    public SupplyItem getItemById(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = OracleSupplyItemQuery.GET_SUPPLY_ITEM_BY_ID.getSql(schemaMap());
        List<SupplyItem> itemList = remoteNamedJdbc.query(sql, params, new SupplyItemRowMapper());
        if (itemList.isEmpty() || itemList == null) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return setItemRestriction(itemList.get(0));
        }
    }

    private void applyItemRestrictions(Set<SupplyItem> items) {
        Map<Integer, ItemRestriction> restrictions = itemRestrictionDao.getItemRestrictions();
        for (SupplyItem item : items) {
            ItemRestriction restriction = restrictions.get(item.getId());
            if (restriction != null) {
                item.setRestriction(restriction);
            }
        }
    }

    private SupplyItem setItemRestriction(SupplyItem item) {
        item.setRestriction(itemRestrictionDao.forItem(item.getId()));
        return item;
    }
}
