package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyItemRowMapper extends BaseRowMapper<SupplyItem> {

    @Override
    public SupplyItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        // TODO horrible hack, need to refactor item/location allowance/item visibility functionality.
        String specPerReq = rs.getString("CdSpecPerMReq");
        String vis = rs.getString("CdSpecPerMVisible");
        ItemVisibility visibility = ItemVisibility.VISIBLE;
        if (vis != null && specPerReq != null) {
            if (vis.equals("N")) {
                visibility = ItemVisibility.HIDDEN;
            } else if (specPerReq.equals("Y")) {
                visibility = ItemVisibility.SPECIAL;
            }
        }

        return new SupplyItem(
                rs.getInt("Nuxrefco"),
                rs.getString("CdCommodity"),
                // Use the supply app specific description. If not available fall back to the default description.
                rs.getString("DeCommdtyEssSupply") == null ? rs.getString("DeCommodityf") : rs.getString("DeCommdtyEssSupply"),
                rs.getString("CdIssUnit"),
                new Category(rs.getString("CdCategory")),
                rs.getInt("numaxunitord"),
                rs.getInt("numaxunitmon"),
                rs.getInt("AmStdUnit"),
                visibility,
                !rs.getString("cdsensuppieditem").equals("Y")
        );
    }
}
