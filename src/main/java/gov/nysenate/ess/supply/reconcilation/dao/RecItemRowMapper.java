package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecItemRowMapper extends BaseRowMapper<RecOrder> {

    @Override
    public RecOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RecOrder.Builder()
                .withItemId(String.valueOf(rs.getInt("NUXREFCO")))
                .withQuantity(rs.getInt("AMQTYOHSTD"))
                .build();

    }

}
