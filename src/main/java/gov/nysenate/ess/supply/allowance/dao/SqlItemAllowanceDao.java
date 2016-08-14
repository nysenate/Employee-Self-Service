package gov.nysenate.ess.supply.allowance.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.allowance.ItemAllowance;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SqlItemAllowanceDao extends SqlBaseDao implements ItemAllowanceDao {

    @Autowired private SupplyItemService itemService;

    @Override
    public Set<ItemAllowance> getItemAllowances(LocationId locationId) {
        List<SupplyItem> items = itemService.getSupplyItems(LimitOffset.ALL).getResults();
        Set<ItemAllowance> itemAllowances = items.stream()
                                                 .map(i -> createAllowanceFromItemAndLoc(i, locationId))
                                                 .collect(Collectors.toSet());
        // TODO: entire functionality needs to be refactored. for now remove items that are hidden from return set.
        return itemAllowances.stream()
                             .filter(a -> a.getVisibility() != ItemVisibility.HIDDEN)
                             .collect(Collectors.toSet());
    }

    private List<Integer> getLocationSpecificItems() {
        return localNamedJdbc.query(SqlItemAllowanceQuery.GET_LOCATION_SPECIFIC_ITEMS.getSql(schemaMap()),
                                    ((rs, i) -> {
                                        return rs.getInt("item_id");
                                    }));
    }

    private ItemAllowance createAllowanceFromItemAndLoc(SupplyItem item, LocationId locationId) {
        // Not fully implemented yet, for now all items are allowed and visible.
        ItemAllowance allowance = new ItemAllowance();
        allowance.setSupplyItem(item);
        if (getLocationSpecificItems().contains(item.getId())) {
            if (canLocationOrderItem(locationId, item)) {
//                // TODO: Item visibility and Special vs not special needs to be separated!
//                // Location specific items are not guaranteed to be special items.
                allowance.setVisibility(ItemVisibility.SPECIAL);
            } else {
                allowance.setVisibility(ItemVisibility.HIDDEN);
            }
        } else {
            allowance.setVisibility(item.getVisibility());
        }
        allowance.setPerOrderAllowance(item.getMaxQtyPerOrder());
        allowance.setPerMonthAllowance(item.getMaxQtyPerMonth());
        allowance.setQtyOrderedMonthToDate(queryQtyOrderedMonthToDate(item, locationId));
        return allowance;
    }

    /**
     * Checks if a location is allowed to order a item.
     *
     * @param item
     * @return
     */
    private boolean canLocationOrderItem(LocationId locationId, SupplyItem item) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locationId", locationId.toString())
                .addValue("itemId", item.getId());
        String sql = SqlItemAllowanceQuery.CAN_LOCATION_ORDER_ITEM.getSql(schemaMap());
        List<Integer> results = localNamedJdbc.query(sql, params, (rs, i) -> {
            return rs.getInt("count");
        });
        return results.get(0) != 0;
    }

    private int queryQtyOrderedMonthToDate(SupplyItem item, LocationId locationId) {
        return 0;
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("itemId", item.getId())
//                .addValue("location", locationId.toString())
//                .addValue("fromDate", toDate(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)))
//                .addValue("toDate", toDate(LocalDateTime.now()));
//        String sql = SqlItemAllowanceQuery.GET_LOCATION_ITEM_ORDER_QTY_FOR_DATE_RANGE.getSql(schemaMap());
//        return localNamedJdbc.queryForObject(sql, params, (rs, i) -> rs.getInt("total"));
    }

    private enum SqlItemAllowanceQuery implements BasicSqlQuery {
        GET_LOCATION_ITEM_ORDER_QTY_FOR_DATE_RANGE(
                "SELECT SUM(i.quantity) as total FROM ${supplySchema}.line_item i\n" +
                "INNER JOIN ${supplySchema}.requisition_version v ON v.version_id = i.version_id\n" +
                "INNER JOIN ${supplySchema}.requisition r ON r.active_version_id = v.version_id\n" +
                "WHERE v.destination = :location\n" +
                "AND i.item_id = :itemId\n" +
                "AND r.ordered_date_time BETWEEN :fromDate AND :toDate"
        ),
        GET_LOCATION_SPECIFIC_ITEMS(
                "SELECT DISTINCT item_id from ${supplySchema}.location_specific_items"
        ),
        CAN_LOCATION_ORDER_ITEM(
                "SELECT count(*) as count from ${supplySchema}.location_specific_items \n" +
                "WHERE item_id = :itemId AND location_id = :locationId"
        );

        SqlItemAllowanceQuery(String sql) {
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

}
