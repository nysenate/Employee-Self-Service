package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyItemRowMapper extends BaseRowMapper<SupplyItem> {

    @Override
    public SupplyItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyItem(
                rs.getInt("NUXREFCO"),
                rs.getString("CDCOMMODITY"),
                "Item Name", // TODO: Item name not in databse yet.
                rs.getString("DECOMMODITYF"),
                rs.getString("CDISSUNIT"),
                rs.getString("CDCATEGORY"),
                2 // TODO: is suggested quantity in database?
        );
    }

}
