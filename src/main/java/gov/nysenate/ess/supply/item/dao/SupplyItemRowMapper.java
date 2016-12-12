package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.model.*;

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

        return new SupplyItem.Builder()
                .withId(rs.getInt("Nuxrefco"))
                .withCommodityCode(rs.getString("CdCommodity"))
                .withDescription(rs.getString("DeCommdtyEssSupply") == null ? rs.getString("DeCommodityf") : rs.getString("DeCommdtyEssSupply"))
                .withStatus(new ItemStatus(rs.getString("cdstockitem").equals("Y"), !rs.getString("cdsensuppieditem").equals("Y")))
                .withCategory(new Category(rs.getString("CdCategory")))
                .withAllowance(new ItemAllowance(rs.getInt("numaxunitord"), rs.getInt("numaxunitmon")))
                .withUnit(new ItemUnit(rs.getString("CdIssUnit"),rs.getInt("AmStdUnit")))
                .withVisibility(visibility)
                .build();
    }
}
