package gov.nysenate.ess.supply.requisition.dao;


import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class SqlReqLineItemDao extends SqlBaseDao {

    @Autowired private SupplyItemService itemService;

    protected void insertVersionLineItems(RequisitionVersion version, int versionId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LineItem lineItem: version.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("versionId", versionId)
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity());
            paramList.add(params);
        }
        String sql = SqlReqLineItemQuery.INSERT_LINE_ITEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    public Set<LineItem> getLineItems(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId);
        String sql = SqlReqLineItemQuery.GET_LINE_ITEMS.getSql(schemaMap());
        ReqLineItemHandler handler = new ReqLineItemHandler(itemService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getLineItems();
    }

    private enum SqlReqLineItemQuery implements BasicSqlQuery {

        INSERT_LINE_ITEM(
                "INSERT INTO ${supplySchema}.line_item(version_id, item_id, quantity) \n" +
                "VALUES (:versionId, :itemId, :quantity)"
        ),
        GET_LINE_ITEMS(
                "SELECT item_id, quantity \n" +
                "FROM ${supplySchema}.line_item \n" +
                "WHERE version_id = :versionId"
        );

        SqlReqLineItemQuery(String sql) {
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

    /**
     * Return a set of line items sorted alphabetically by their descriptions.
     * Cant sort in original select statement since description information is
     * not contained in local database.
     */
    private class ReqLineItemHandler extends BaseHandler {

        private SupplyItemService itemService;
        private Set<LineItem> lineItems;
        private Comparator alphabeticalItemDesc = new Comparator<LineItem>() {
            @Override
            public int compare(LineItem o1, LineItem o2) {
                return o1.getItem().getDescription().compareTo(o2.getItem().getDescription());
            }
        };

        public ReqLineItemHandler(SupplyItemService itemService) {
            this.itemService = itemService;
            lineItems = new TreeSet<>(alphabeticalItemDesc);
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
