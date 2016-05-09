package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyItemRowMapper extends BaseRowMapper<SupplyItem> {

    @Override
    public SupplyItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        int maxOrderQty = rs.getInt("numaxunitmon");
        return new SupplyItem(
                rs.getInt("Nuxrefco"),
                rs.getString("CdCommodity"),
                rs.getString("DeCommodityf"),
                rs.getString("CdIssUnit"),
                new Category(rs.getString("CdCategory")),
                rs.getInt("numaxunitord"),
                isMaxOrderQtyValid(maxOrderQty)? maxOrderQty : 2, // TODO: Use max monthly order quantity if available, default to 2.
                rs.getInt("AmStdUnit")
        );
    }

    // Lots of invalid data in database.
    private boolean isMaxOrderQtyValid(int maxOrderQty) {
        return maxOrderQty != 0 && maxOrderQty < 100;
    }
}
