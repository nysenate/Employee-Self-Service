package gov.nysenate.ess.supply.item.dao;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.model.ItemRestriction;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class ItemRestrictionDao extends SqlBaseDao {

    public ItemRestriction forItem(int itemId) {
        MapSqlParameterSource params = new MapSqlParameterSource("itemId", itemId);
        String sql = ItemRestrictionQuery.RESTRICTIONS_FOR_ITEM.getSql(schemaMap());
        ItemRestrictionHandler handler = new ItemRestrictionHandler();
        localNamedJdbc.query(sql, params, handler);
        return new ItemRestriction(handler.results());
    }

    /**
     * Get all item restrictions.
     * @return A map of item id's to its ItemRestriction.
     */
    public Map<Integer, ItemRestriction> getItemRestrictions() {
        String sql = ItemRestrictionQuery.GET_ALL_ITEM_RESTRICTIONS.getSql(schemaMap());
        ItemRestrictionsHandler handler = new ItemRestrictionsHandler();
        localNamedJdbc.query(sql, handler);
        return handler.getResults();
    }

    private enum ItemRestrictionQuery implements BasicSqlQuery {
        RESTRICTIONS_FOR_ITEM(
                "SELECT location_id from ${supplySchema}.location_specific_items \n" +
                        "WHERE item_id = :itemId"
        ),

        GET_ALL_ITEM_RESTRICTIONS(
                "SELECT item_id, location_id \n" +
                "FROM ${supplySchema}.location_specific_items \n"
        );

        ItemRestrictionQuery(String sql) {
            this.sql = sql;
        }

        private final String sql;

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private static class ItemRestrictionHandler implements RowCallbackHandler {

        private final Set<LocationId> locationSet = new HashSet<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            locationSet.add(LocationId.ofString(rs.getString("location_id")));
        }

        public Set<LocationId> results() {
            return locationSet;
        }
    }

    private static class ItemRestrictionsHandler implements RowCallbackHandler {

        private final Multimap<Integer, LocationId> itemRestrictions = HashMultimap.create();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            itemRestrictions.put(rs.getInt("item_id"), LocationId.ofString(rs.getString("location_id")));
        }

        public Map<Integer, ItemRestriction> getResults() {
            Map<Integer, ItemRestriction> allRestrictions = new HashMap<>();
            for (Integer itemId : itemRestrictions.keys()) {
                allRestrictions.put(itemId, new ItemRestriction(itemRestrictions.get(itemId)));
            }
            return allRestrictions;
        }
    }
}
