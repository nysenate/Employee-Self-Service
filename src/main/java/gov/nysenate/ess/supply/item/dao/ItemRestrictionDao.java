package gov.nysenate.ess.supply.item.dao;

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
import java.util.HashSet;
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

    private enum ItemRestrictionQuery implements BasicSqlQuery {
        RESTRICTIONS_FOR_ITEM(
                "SELECT location_id from ${supplySchema}.location_specific_items \n" +
                        "WHERE item_id = :itemId"
        );

        ItemRestrictionQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class ItemRestrictionHandler implements RowCallbackHandler {

        private Set<LocationId> locationSet = new HashSet<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            locationSet.add(new LocationId(rs.getString("location_id")));
        }

        public Set<LocationId> results() {
            return locationSet;
        }
    }
}
