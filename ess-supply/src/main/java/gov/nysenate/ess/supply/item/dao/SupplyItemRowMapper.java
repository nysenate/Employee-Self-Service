package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyItemRowMapper extends BaseRowMapper<SupplyItem> {

    @Override
    public SupplyItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyItem(
                rs.getInt("Nuxrefco"),
                rs.getString("CdCommodity"),
                rs.getString("DeCommodityf"),
                rs.getString("CdIssUnit"),
                new Category(rs.getString("CdCategory")),
                getQuantityOrDefault(rs.getInt("numaxunitord"), 2),
                getQuantityOrDefault(rs.getInt("numaxunitmon"), 4),
                rs.getInt("AmStdUnit")
        );
    }

    // Lots of invalid data in database.
    private int getQuantityOrDefault(int qty, int defaultQty) {
        if (qty != 0 && qty < 100) {
            return qty;
        }
        return defaultQty;
    }
}
