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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SqlItemAllowanceDao extends SqlBaseDao implements ItemAllowanceDao {

    @Autowired private SupplyItemService itemService;

    @Override
    public Set<ItemAllowance> getItemAllowances(LocationId locationId) {
        List<SupplyItem> items = itemService.getSupplyItems(LimitOffset.ALL).getResults();
        return items.stream()
                    .map(i -> createAllowanceFromItemAndLoc(i, locationId))
                    .collect(Collectors.toSet());
    }

    private ItemAllowance createAllowanceFromItemAndLoc(SupplyItem item, LocationId locationId) {
        // Not fully implemented yet, for now all items are allowed and visible.
        ItemAllowance allowance = new ItemAllowance();
        allowance.setSupplyItem(item);
        allowance.setVisibility(ItemVisibility.SPECIAL);
        allowance.setPerMonthAllowance(item.getMaxQtyPerMonth());
        allowance.setQtyOrderedMonthToDate(queryQtyOrderedMonthToDate(item, locationId));
        return allowance;
    }

    private int queryQtyOrderedMonthToDate(SupplyItem item, LocationId locationId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("itemId", item.getId())
                .addValue("location", locationId.toString())
                .addValue("fromDate", toDate(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)))
                .addValue("toDate", toDate(LocalDateTime.now()));
        String sql = SqlItemAllowanceQuery.GET_LOCATION_ITEM_ORDER_QTY_FOR_DATE_RANGE.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, (rs, i) -> rs.getInt("total"));
    }

    private enum SqlItemAllowanceQuery implements BasicSqlQuery {
        // TODO: Ideally this will use the ordered date time, not the version created date time.
        GET_LOCATION_ITEM_ORDER_QTY_FOR_DATE_RANGE(
                "SELECT SUM(i.quantity) as total FROM ${supplySchema}.line_item i\n" +
                "INNER JOIN ${supplySchema}.order_version v ON v.version_id = i.version_id\n" +
                "INNER JOIN ${supplySchema}.order o ON o.active_version = v.version_id\n" +
                "INNER JOIN ${supplySchema}.order_history h ON (h.order_id, h.version_id) = (o.order_id, o.active_version)\n" +
                "WHERE v.destination = :location\n" +
                "AND i.item_id = :itemId\n" +
                "AND h.created_date_time BETWEEN :fromDate AND :toDate"
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
