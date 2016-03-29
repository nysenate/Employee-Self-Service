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
                "Item Name", // TODO: Item name not in databse yet.
                rs.getString("DeCommodityf"),
                rs.getString("CdIssUnit"),
                new Category(rs.getString("CdCategory")),
                2, // TODO: is suggested quantity in database?
                rs.getInt("AmStdUnit")
        );
    }

}
