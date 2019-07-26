package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InventoryRowHandler implements RowCallbackHandler {

    private Map<Integer, Integer> inventory = new HashMap<>();

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        // Divide the unit quantity by the unit size to get the normal item quantity used in reconciliation.
        int quantity = rs.getInt("AMQTYOHSTD")/rs.getInt("AmStdUnit");
        inventory.put(rs.getInt("NUXREFCO"), quantity);
    }

    public Inventory results() {
        return new Inventory(inventory);
    }
}
