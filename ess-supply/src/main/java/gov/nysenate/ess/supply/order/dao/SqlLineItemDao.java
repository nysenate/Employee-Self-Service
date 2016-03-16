package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlLineItemDao extends SqlBaseDao implements LineItemDao {

    @Autowired private SupplyItemService itemService;

    @Override
    public void insertVersionLineItems(OrderVersion version, int versionId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LineItem lineItem: version.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("versionId", versionId)
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity());
            paramList.add(params);
        }
        String sql = SqlLineItemQuery.INSERT_LINE_ITEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    public Set<LineItem> getLineItems(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId);
        String sql = SqlLineItemQuery.GET_LINE_ITEMS.getSql(schemaMap());
        LineItemHandler handler = new LineItemHandler(itemService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getLineItems();
    }

    private enum SqlLineItemQuery implements BasicSqlQuery {

        INSERT_LINE_ITEM(
                "INSERT INTO ${supplySchema}.line_item(version_id, item_id, quantity) \n" +
                "VALUES (:versionId, :itemId, :quantity)"
        ),
        GET_LINE_ITEMS(
                "SELECT item_id, quantity \n" +
                "FROM ${supplySchema}.line_item \n" +
                "WHERE version_id = :versionId"
        );

        SqlLineItemQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class LineItemHandler extends BaseHandler {

        private SupplyItemService itemService;
        private Set<LineItem> lineItems;

        public LineItemHandler(SupplyItemService itemService) {
            this.itemService = itemService;
            lineItems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SupplyItem item = itemService.getItemById(rs.getInt("item_id"));
            int quantity = rs.getInt("quantity");
            lineItems.add(new LineItem(item, quantity));
        }

        public Set<LineItem> getLineItems() {
            return lineItems;
        }
    }
}
