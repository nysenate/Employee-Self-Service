package gov.nysenate.ess.supply.item;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.supply.item.dao.OracleSupplyItemQuery;
import gov.nysenate.ess.supply.item.dao.SupplyItemRowMapper;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

/**
 * Gets SupplyItem information from the Oracle database.
 * This should not be used to query SupplyItem objects, use the SupplyItemService instead.
 */
@Repository
public class SupplyItemDao extends SqlBaseDao {

    protected Set<SupplyItem> getSupplyItems() {
        String sql = OracleSupplyItemQuery.GET_ALL_SUPPLY_ITEMS.getSql(schemaMap(), new OrderBy("CDCOMMODITY", SortOrder.ASC));
        return new HashSet<>(remoteNamedJdbc.query(sql, new SupplyItemRowMapper()));
    }

    protected SupplyItem getItemById(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = OracleSupplyItemQuery.GET_SUPPLY_ITEM_BY_ID.getSql(schemaMap());
        return remoteNamedJdbc.queryForObject(sql, params, new SupplyItemRowMapper());
    }
}
