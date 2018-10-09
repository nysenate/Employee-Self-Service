package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InventoryRowHandler implements RowCallbackHandler {

    private LocationId locationId;
    private Map<Integer, Integer> inventory = new HashMap<>();

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        if (locationId == null) {
            locationId = new LocationId(rs.getString("CDLOCAT"), rs.getString("CDLOCTYPE").charAt(0));
        }
        inventory.put(rs.getInt("NUXREFCO"), rs.getInt("AMQTYOHSTD"));
    }

    public Inventory results() {
        return new Inventory(locationId, inventory);
    }
}
