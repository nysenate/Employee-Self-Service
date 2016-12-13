package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.item.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyItemRowMapper extends BaseRowMapper<SupplyItem> {

    @Override
    public SupplyItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyItem.Builder()
                .withId(rs.getInt("Nuxrefco"))
                .withCommodityCode(rs.getString("CdCommodity"))
                .withDescription(getDescription(rs))
                .withStatus(getItemStatus(rs))
                .withCategory(new Category(rs.getString("CdCategory")))
                .withAllowance(new ItemAllowance(rs.getInt("numaxunitord"), rs.getInt("numaxunitmon")))
                .withUnit(new ItemUnit(rs.getString("CdIssUnit"), rs.getInt("AmStdUnit")))
                .build();
    }

    private String getDescription(ResultSet rs) throws SQLException {
        return rs.getString("DeCommdtyEssSupply") == null
                ? rs.getString("DeCommodityf")
                : rs.getString("DeCommdtyEssSupply");
    }

    private ItemStatus getItemStatus(ResultSet rs) throws SQLException {
        String cdStockItem = rs.getString("cdstockitem");
        String cdSenSuppiedItem = rs.getString("cdsensuppieditem");
        String cdSpecPerMVisible = rs.getString("CdSpecPerMVisible");
        String cdSpecPerMReq = rs.getString("CdSpecPerMReq");
        return new ItemStatus(
                // If null, initialize with default value.
                cdStockItem == null ? false : cdStockItem.equals("Y"),
                cdSenSuppiedItem == null ? false : !cdSenSuppiedItem.equals("Y"),
                cdSpecPerMVisible == null ? false : cdSpecPerMVisible.equals("Y"),
                cdSpecPerMReq == null ? false : cdSpecPerMReq.equals("Y"));
    }
}
